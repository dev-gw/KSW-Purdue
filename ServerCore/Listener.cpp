#include "pch.h"
#include "Listener.h"
#include "SocketUtils.h"
#include "EpollEvent.h"
#include "Session.h"
#include "Service.h"

/*--------------
	Listener
---------------*/

Listener::~Listener()
{
	SocketUtils::Close(_socket);

	for (AcceptEvent* acceptEvent : _acceptEvents)
	{
		// TODO

		xdelete(acceptEvent);
	}
}

bool Listener::StartAccept(ServerServiceRef service)
{
	_service = service;
	if (_service == nullptr)
		return false;

	_socket = SocketUtils::CreateSocket();
	if (_socket == INVALID_SOCKET)
		return false;

	if (SocketUtils::SetReuseAddress(_socket, true) == false)
		return false;

	if (SocketUtils::SetLinger(_socket, 0, 0) == false)
		return false;

	if (SocketUtils::Bind(_socket, _service->GetNetAddress()) == false)
		return false;

	if (IS_VALID_SOCKET(SocketUtils::MakeSocketNonBlocking(_socket)) == false)
		return false;

	if (SocketUtils::Listen(_socket) == false)
		return false;

	if (_service->GetEpollCore()->Register(shared_from_this()) == false)
		return false;

	const int32 acceptCount = _service->GetMaxSessionCount();
	for (int32 i = 0; i < acceptCount; i++)
	{
		AcceptEvent* acceptEvent = new AcceptEvent();
		acceptEvent->owner = shared_from_this();

		//epoll_event* acceptEvent = new epoll_event();
		_acceptEvents.push_back(acceptEvent);
		RegisterAccept(acceptEvent);
	}

	return true;
}

void Listener::CloseSocket()
{
	SocketUtils::Close(_socket);
}

int Listener::GetHandle()
{
	return _socket;
}

void Listener::Dispatch(EpollEvent* epollEvent)
{
	ASSERT_CRASH(epollEvent->eventType == EventType::Accept);
	
	ProcessAccept(static_cast<AcceptEvent*>(epollEvent));
}

void Listener::RegisterAccept(AcceptEvent* acceptEvent)
{
	

	SessionRef session = _service->CreateSession();

	acceptEvent->Init();
	acceptEvent->session = session;

	SOCKET newSocket;

	if (_registeredOnEpoll == false)
	{
		_registeredOnEpoll.store(true);
		newSocket = _socket;
	}
	else
	{
		newSocket = fcntl(_socket, F_DUPFD); // Duplicate the socket fd
	}

	if (_SocketMap.find(acceptEvent) == _SocketMap.end())
	{
		_SocketMap[acceptEvent] = newSocket;
		ASSERT_CRASH(epoll_ctl(_service->GetEpollCore()->GetHandle(), EPOLL_CTL_ADD, newSocket, acceptEvent) < 0);
	}
	
}

void Listener::ProcessAccept(AcceptEvent* acceptEvent)
{
	SessionRef session = acceptEvent->session;

	sockaddr_in inSockAddress;
	socklen_t sizeOfSockAddr;
	sizeOfSockAddr = sizeof(inSockAddress);

	SOCKET acceptSocket = accept(_SocketMap[acceptEvent], (sockaddr*) &inSockAddress, &sizeOfSockAddr);
	ASSERT_CRASH(IS_VALID_SOCKET(SocketUtils::MakeSocketNonBlocking(acceptSocket)));

	//_SocketMap.erase(acceptEvent);

	session->SetSocket(acceptSocket);
	session->SetNetAddress(NetAddress(inSockAddress));
	session->ProcessConnect();


	// Create new session
	//SessionRef session = _service->CreateSession();
	//SessionRef* newSession = &session;
	//epoll_event* newEvent = new epoll_event();

	//newEvent->events = EPOLLIN | EPOLLET;
	//newEvent->data.ptr = newSession;

	//ASSERT_CRASH(IS_VALID_SOCKET(epoll_ctl(GET_EPOLL_HANDLE /*epoll_fd*/ , EPOLL_CTL_ADD, acceptSocket, connectEvent)));
	
	//SocketUtils::Bind(acceptSocket, inSockAddress);

	
	RegisterAccept(acceptEvent);
}
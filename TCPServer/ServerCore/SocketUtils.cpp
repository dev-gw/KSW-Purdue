#include "pch.h"
#include "SocketUtils.h"

/*----------------
	SocketUtils
-----------------*/



void SocketUtils::Init()
{
	SOCKET dummySocket = CreateSocket();
	if (dummySocket < 0)
	{
		cout << dummySocket + " : " << errno;
	}
	ASSERT_CRASH(dummySocket >= 0);
	Close(dummySocket);
}

void SocketUtils::Clear()
{
	//TODO
}


SOCKET SocketUtils::CreateSocket()
{
	return socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
}

bool SocketUtils::SetLinger(SOCKET socket, uint16 onoff, uint16 inLinger)
{
	linger option;
	option.l_onoff = onoff;
	option.l_linger = inLinger;
	return SetSockOpt(socket, SOL_SOCKET, SO_LINGER, option);
}

bool SocketUtils::SetReuseAddress(SOCKET socket, int32 flag)
{
	return SetSockOpt(socket, SOL_SOCKET, SO_REUSEADDR, flag);
}

bool SocketUtils::SetRecvBufferSize(SOCKET socket, int32 size)
{
	return SetSockOpt(socket, SOL_SOCKET, SO_RCVBUF, size);
}

bool SocketUtils::SetSendBufferSize(SOCKET socket, int32 size)
{
	return SetSockOpt(socket, SOL_SOCKET, SO_SNDBUF, size);
}

bool SocketUtils::SetTcpNoDelay(SOCKET socket, int32 flag)
{
	return SetSockOpt(socket, SOL_SOCKET, TCP_NODELAY, flag);
}



bool SocketUtils::Bind(SOCKET socket, NetAddress netAddr)
{
	if (::bind(socket, reinterpret_cast<sockaddr*>(&netAddr.GetSockAddr()), sizeof(sockaddr_in)) < 0)
	{
		cout << strerror(errno) << endl;
		return false;
	}
	return true;
}

bool SocketUtils::BindAnyAddress(SOCKET socket, uint16 port)
{
	sockaddr_in myAddress;
	myAddress.sin_family = AF_INET;
	myAddress.sin_addr.s_addr = ::htonl(INADDR_ANY);
	myAddress.sin_port = ::htons(port);

	return IS_VALID_SOCKET(::bind(socket, reinterpret_cast<sockaddr*>(&myAddress), sizeof(myAddress)));
}

bool SocketUtils::Listen(SOCKET socket, int32 backlog)
{
	IF_FALSE_PRINT_AND_RETURN_FALSE(::listen(socket, backlog) >= 0);
	return true;
}

void SocketUtils::Close(SOCKET& socket)
{
	if (socket >= 0)
		close(socket);
	socket = -1;
}

int SocketUtils::MakeSocketNonBlocking(SOCKET& sfd)
{
	int flags, s;

	flags = fcntl(sfd, F_GETFL, 0);
	if (flags == -1)
	{
		perror("fcntl");
		return -1;
	}

	flags |= O_NONBLOCK;
	s = fcntl(sfd, F_SETFL, flags);
	if (s == -1)
	{
		perror("fcntl");
		return -1;
	}

	return 0;
}
#pragma once

#include "EpollCore.h"
#include "NetAddress.h"

class ServerService;

/*--------------
	Listener
---------------*/

class Listener : public EpollObject
{
public:
	Listener() = default;
	~Listener();

public:
	bool StartAccept(ServerServiceRef service);
	void CloseSocket();

public:
	virtual int GetHandle() override;
	virtual void Dispatch(EpollEvent* acceptEvent) override;

private:
	void RegisterAccept(AcceptEvent* acceptEvent);
	void ProcessAccept(AcceptEvent* acceptEvent);

protected:
	Atomic<bool> _registeredOnEpoll = false;
	SOCKET _socket;
	Vector<AcceptEvent*> _acceptEvents;
	ServerServiceRef _service;
	unordered_map<EpollEvent*, SOCKET> _SocketMap;
};


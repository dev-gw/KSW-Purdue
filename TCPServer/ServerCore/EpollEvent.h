#pragma once
#include "sys/epoll.h"

typedef unsigned int		SOCKET;

enum class EventType : uint8
{
	Connect,
	Disconnect,
	Accept,
	//PreRecv,
	Recv,
	Send
};

class EpollEvent : public epoll_event
{
public:
	EpollEvent(EventType type);

	void			Init();

public:
	EventType eventType;
	EpollObjectRef owner;
};

class ConnectEvent : public EpollEvent
{
public:
	ConnectEvent() : EpollEvent(EventType::Connect) { }
};

/*--------------------
	DisconnectEvent
----------------------*/

class DisconnectEvent : public EpollEvent
{
public:
	DisconnectEvent() : EpollEvent(EventType::Disconnect) { }
};

/*----------------
	AcceptEvent
-----------------*/

class AcceptEvent : public EpollEvent
{
public:
	AcceptEvent() : EpollEvent(EventType::Accept) { }

public:
	SessionRef	session = nullptr;
};

/*----------------
	RecvEvent
-----------------*/

class RecvEvent : public EpollEvent
{
public:
	RecvEvent() : EpollEvent(EventType::Recv) { }
};

/*----------------
	SendEvent
-----------------*/

class SendEvent : public EpollEvent
{
public:
	SendEvent() : EpollEvent(EventType::Send) { }

	Vector<SendBufferRef> sendBuffers;
};
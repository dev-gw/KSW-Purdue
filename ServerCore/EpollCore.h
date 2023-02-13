#pragma once

#include "EpollEvent.h"

/*----------------
	EpollObject
-----------------*/

#define MAXEVENTS 128

class EpollObject : public enable_shared_from_this<EpollObject>
{

public:
	virtual int GetHandle() abstract;
	virtual void Dispatch(EpollEvent* epollEvent) abstract;

public:
	epoll_event* _event;
};

class EpollCore
{
public:
	EpollCore();
	~EpollCore();

	int32	GetHandle() { return _epollHandle; }

	bool	Register(EpollObjectRef epollObject);
	bool	Dispatch(uint32 timeoutMs = INFINITE);

public:
	unordered_map<epoll_event*, EpollObject> _eventMap;

private:
	epoll_event _events[MAXEVENTS];
	SOCKET _epollHandle;
};


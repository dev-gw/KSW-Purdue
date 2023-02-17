#pragma once

#include "EpollEvent.h"

/*----------------
	EpollObject
-----------------*/

#define MAXEVENTS	128
#define INFINITE	0xFFFFFFFF

class EpollObject : public enable_shared_from_this<EpollObject>
{

public:
	virtual int GetHandle() = 0;
	virtual void Dispatch(EpollEvent* epollEvent) = 0;

public:
	epoll_event* _event;
};

class EpollCore
{
public:
	EpollCore();
	~EpollCore();

	int32	GetHandle() { return _epollHandle; }

	bool	Register(EpollObjectRef epollObject, EpollEvent* epollEvent);
	bool	Dispatch(uint32 timeoutMs = INFINITE);


private:
	epoll_event _events[MAXEVENTS];
	SOCKET _epollHandle;
};


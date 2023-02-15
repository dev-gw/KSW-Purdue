#include "pch.h"
#include "EpollCore.h"

EpollCore::EpollCore()
{
	_epollHandle = ::epoll_create(1);
	ASSERT_CRASH(_epollHandle == -1);
}

EpollCore::~EpollCore()
{
	close(_epollHandle);
}

bool EpollCore::Register(EpollObjectRef epollObject)
{
	return IS_VALID_SOCKET(epoll_ctl(_epollHandle, EPOLL_CTL_ADD, epollObject->GetHandle(), epollObject->_event));
}

bool EpollCore::Dispatch(uint32 timeoutMs)
{
	DWORD numOfBytes = 0;
	ULONG_PTR key = 0;

	int n = epoll_wait(_epollHandle, _events, MAXEVENTS, -1);

	for (int i = 0; i < n; i++)
	{
		EpollEvent* epollEvent = static_cast<EpollEvent*>(&_events[i]);
		EpollObjectRef epollObject = epollEvent->owner;
		epollObject->Dispatch(epollEvent);
	}
}
#pragma once

#include "Types.h"
#include "CoreMacro.h"
#include "CoreTLS.h"
#include "CoreGlobal.h"
#include "Container.h"

#include <unistd.h>
#include <arpa/inet.h>
//#include <sys/queue.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <fcntl.h>
#include <sys/epoll.h>
#include <netdb.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <iostream>
#include <time.h>
using namespace std;

//#include <winsock2.h>
//#include <mswsock.h>
//#include <ws2tcpip.h>
//#pragma comment(lib, "ws2_32.lib")

#include "Lock.h"
#include "ObjectPool.h"
#include "TypeCast.h"
#include "Memory.h"
#include "SendBuffer.h"
#include "Session.h"
#include "JobQueue.h"
#include "ConsoleLog.h"

typedef unsigned long       DWORD;
typedef int                 BOOL;
typedef unsigned char       BYTE;
typedef unsigned short      WORD;
typedef unsigned long		ULONG_PTR;

typedef unsigned int		SOCKET;

typedef unsigned long long int Tick64_t;
typedef unsigned long int Tick32_t;

#define MEMFD "/dev/mem"
#define INVALID_SOCKET (SOCKET)(~0)

Tick64_t GetTickCount64()
{
    Tick64_t tick = 0ull;

#if defined(WIN32) || defined(WIN64)
    tick = GetTickCount64();
#else
    timespec tp;

    ::clock_gettime(CLOCK_MONOTONIC, &tp);

    tick = (tp.tv_sec * 1000ull) + (tp.tv_nsec / 1000ull / 1000ull);
#endif

    return tick;
}
#include "pch.h"

Tick64_t GetTickCount_64()
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

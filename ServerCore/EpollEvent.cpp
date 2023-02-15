#include "pch.h"
#include "EpollEvent.h"

EpollEvent::EpollEvent(EventType type) : eventType(type)
{
	Init();
}

void EpollEvent::Init()
{
	
}
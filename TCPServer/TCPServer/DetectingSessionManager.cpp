#include "pch.h"
#include "DetectingSessionManager.h"
#include "DetectingSession.h"

DetectingSessionManager GSessionManager;

void DetectingSessionManager::Add(DetectingSessionRef session)
{
	WRITE_LOCK;
	_sessions.insert(session);
}

void DetectingSessionManager::Remove(DetectingSessionRef session)
{
	WRITE_LOCK;
	_sessions.erase(session);
}

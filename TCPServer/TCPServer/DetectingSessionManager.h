#pragma once

class DetectingSession;

using DetectingSessionRef = shared_ptr<DetectingSession>;

class DetectingSessionManager
{
public:
	void Add(DetectingSessionRef session);
	void Remove(DetectingSessionRef session);

private:
	USE_LOCK;
	Set<DetectingSessionRef> _sessions;
};

extern DetectingSessionManager GSessionManager;

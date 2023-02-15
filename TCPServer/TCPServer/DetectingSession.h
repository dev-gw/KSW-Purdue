#pragma once
#include "Session.h"

class DetectingSession : public PacketSession
{
public:
	~DetectingSession()
	{
		cout << "~DetectingSession" << endl;
	}

	virtual void OnConnected() override;
	virtual void OnDisconnected() override;
	virtual void OnRecvPacket(BYTE* buffer, int32 len) override;
	virtual void OnSend(int32 len) override;

public:
	UserRef _currentUser;
};


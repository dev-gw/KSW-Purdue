#include "pch.h"
#include "DetectingSession.h"
#include "DetectingSessionManager.h"
#include "ClientPacketHandler.h"

void DetectingSession::OnConnected()
{
	GSessionManager.Add(static_pointer_cast<DetectingSession>(shared_from_this()));
	cout << "OnConnected()" << endl;
}

void DetectingSession::OnDisconnected()
{
	GSessionManager.Remove(static_pointer_cast<DetectingSession>(shared_from_this()));

	_currentUser = nullptr;
}

void DetectingSession::OnRecvPacket(BYTE* buffer, int32 len)
{
	PacketSessionRef session = GetPacketSessionRef();
	PacketHeader* header = reinterpret_cast<PacketHeader*>(buffer);

	cout << "DetectingSession::OnRecvPacket()" << endl;
	
	ClientPacketHandler::HandlePacket(session, buffer, len);
}

void DetectingSession::OnSend(int32 len)
{
	cout << "DetectingSession::OnRecvPacket(), To " << GetAddress().GetIpAddress() << endl;
}
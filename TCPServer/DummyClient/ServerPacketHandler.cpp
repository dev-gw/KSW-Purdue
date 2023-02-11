#include "pch.h"
#include "ServerPacketHandler.h"
#include "BufferReader.h"
#include "BufferWriter.h"

PacketHandlerFunc GPacketHandler[UINT16_MAX];


struct PKT_S_DETECTION_RESULT
{
	uint16 packetSize; // 공용 헤더
	uint16 packetId; // 공용 헤더
	bool result;

	bool Validate()
	{
		uint32 size = 0;
		size += sizeof(PKT_S_DETECTION_RESULT);
		if (packetSize < size)
			return false;

		return true;
	}
};

void ServerPacketHandler::HandlePacket(PacketSessionRef& session, BYTE* buffer, int32 len)
{
	BufferReader br(buffer, len);

	PacketHeader header;
	br >> header;

	switch (header.id)
	{
	case S_DETECTION_RESULT:
		Handle_S_DETECTION_RESULT(session, buffer, len);
		break;
	}
}

bool Handle_INVALID(PacketSessionRef& session, BYTE* buffer, int32 len)
{
	PacketHeader* header = reinterpret_cast<PacketHeader*>(buffer);
	// TODO : Log
	return false;
}



bool ServerPacketHandler::Handle_S_LOGIN(PacketSessionRef& session, BYTE* buffer, int32 len)
{
	return true;
}

bool ServerPacketHandler::Handle_S_DETECTION_RESULT(PacketSessionRef& session, BYTE* buffer, int32 len)
{
	BufferReader br(buffer, len);
	PKT_S_DETECTION_RESULT* pkt = reinterpret_cast<PKT_S_DETECTION_RESULT*>(buffer);

	if (pkt->Validate() == false)
		return false;

	cout << pkt->result << endl;

	return true;
}
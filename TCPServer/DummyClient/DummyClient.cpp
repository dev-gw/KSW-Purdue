#include "pch.h"
#include "ThreadManager.h"
#include "Service.h"
#include "Session.h"
#include "BufferReader.h"
#include "ServerPacketHandler.h"

char sendData[] = "Hello World";

class ServerSession : public PacketSession
{
public:
	~ServerSession()
	{
		cout << "~ServerSession" << endl;
	}

	virtual void OnConnected() override
	{
		cout << "Connected To Server" << endl;
	}

	virtual void OnRecvPacket(BYTE* buffer, int32 len) override
	{
		PacketSessionRef session = GetPacketSessionRef();
		PacketHeader* header = reinterpret_cast<PacketHeader*>(buffer);
		ServerPacketHandler::HandlePacket(session, buffer, len);
	}

	virtual void OnSend(int32 len) override
	{
		cout << "OnSend Len = " << len << endl;
	}

	virtual void OnDisconnected() override
	{
		//cout << "Disconnected" << endl;
	}
};

int main()
{
	float data[40] = { -6.26099670e+02,  6.08171043e+01,  1.21538630e+01,  1.10330324e+01,
 -3.38118315e+00,  7.72425175e+00, -8.84613228e+00, -5.71947002e+00,
 -9.74624252e+00, -1.90162218e+00, -1.41135235e+01, -9.04522324e+00,
 -2.92101121e+00, -6.41781187e+00, -4.54213810e+00, -8.88110352e+00,
 -4.29623079e+00, -3.78915739e+00, -6.61831951e+00, -8.36560535e+00,
 -5.85096216e+00, -7.87125444e+00, -6.12021399e+00, -5.98344231e+00,
 -5.71011066e+00, -5.42288208e+00, -4.55634403e+00, -4.21312284e+00,
 -2.76323390e+00, -1.03867352e+00,  2.73384601e-01,  4.97162676e+00,
  4.76197100e+00,  4.28834200e+00,  1.30950105e+00, -2.93097210e+00,
 -2.33082223e+00, -1.85949576e+00, -1.12502873e+00,  3.47768217e-01 };
	
	//this_thread::sleep_for(chrono::seconds(1));

	ClientServiceRef service = MakeShared<ClientService>(
		NetAddress("192.168.122.1", 632),
		MakeShared<EpollCore>(),
		MakeShared<ServerSession>, // TODO : SessionManager µî
		1);

	ASSERT_CRASH(service->Start());

	for (int32 i = 0; i < 1; i++)
	{
		GThreadManager->Launch([=]()
			{
				while (true)
				{
					service->GetEpollCore()->Dispatch();
				}
			});
	}

	PKT_C_AUDIO_DATA_WRITE pktWriter(C_AUDIO_DATA, data);

	// PKT_C_AUDIO_DATA_WRITE::FeatureLists featureList = pktWriter.ReserveFeatureList(40);

	SendBufferRef sendBuffer = pktWriter.CloseAndReturn();

	for (int num = 0; num < 10; ++num)
	{
		ServerPacketHandler::_startTime = GetTickCount_64();
		service->Broadcast(sendBuffer);
		this_thread::sleep_for(chrono::seconds(10));
	}

	GThreadManager->Join();
}
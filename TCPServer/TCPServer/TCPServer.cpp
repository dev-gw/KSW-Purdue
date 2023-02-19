#include "pch.h"
#include "ThreadManager.h"
#include "Service.h"
#include "Session.h"
#include "DetectingSession.h"
#include "DetectingSessionManager.h"
#include "BufferWriter.h"
#include "ClientPacketHandler.h"
#include "PacketProtocol.h"
#include "Job.h"
#include "User.h"

enum
{
	WORKER_TICK = 64
};

void DoWorkerJob(ServerServiceRef& service)
{
	while (true)
	{
		LEndTickCount = ::GetTickCount_64() + WORKER_TICK;

		// 네트워크 입출력 처리 -> 인게임 로직까지 (패킷 핸들러에 의해)
		service->GetEpollCore()->Dispatch(10);

		// 예약된 일감 처리
		ThreadManager::DistributeReservedJobs();

		// 글로벌 큐
		ThreadManager::DoGlobalQueueWork();
	}
}

int main()
{
	//AcceptEvent* acceptEvent = xnew<AcceptEvent>();

	ServerServiceRef service = MakeShared<ServerService>(
		NetAddress("127.0.0.1", 631),
		MakeShared<EpollCore>(),
		MakeShared<DetectingSession>,
		10);

	ASSERT_CRASH(service->Start());

	/*for (int32 i = 0; i < 5; i++)
	{
		GThreadManager->Launch([&service]()
			{
				DoWorkerJob(service);
			});
	}*/

	// Main Thread
	DoWorkerJob(service);

	GThreadManager->Join();
}
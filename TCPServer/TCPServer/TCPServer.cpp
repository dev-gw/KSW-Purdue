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

#include "MLManager.h"


enum
{
	WORKER_TICK = 64
};

void DoWorkerJob(ServerServiceRef& service)
{
	while (true)
	{
		LEndTickCount = ::GetTickCount_64() + WORKER_TICK;

		service->GetEpollCore()->Dispatch(10);

		ThreadManager::DistributeReservedJobs();

		ThreadManager::DoGlobalQueueWork();
	}
}

int main()
{
	ServerServiceRef service = MakeShared<ServerService>(
		NetAddress("127.0.0.1", 7367),
		MakeShared<EpollCore>(),
		MakeShared<DetectingSession>,
		10);

	ASSERT_CRASH(service->Start());

	// Can be used when every single thread has their own epoll core
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
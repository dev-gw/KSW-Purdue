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

#define PY_SSIZE_T_CLEAN
#include <python3.9/Python.h>

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
    PyObject* pName, * pModule, * pFunc;
    PyObject* pArgs, * pValue;
    int i;


    Py_Initialize();
    pName = PyUnicode_DecodeFSDefault("Test");
    /* Error checking of pName left out */

    pModule = PyImport_Import(pName);
    Py_DECREF(pName);

    if (pModule != NULL) {
        pFunc = PyObject_GetAttrString(pModule, "Test");
        /* pFunc is a new reference */

        if (pFunc && PyCallable_Check(pFunc)) {
            pArgs = PyTuple_New(0);

            pValue = PyObject_CallObject(pFunc, pArgs);
            Py_DECREF(pArgs);
            if (pValue != NULL) {
                printf("Result of call: %s\n", PyUnicode_AS_DATA(pValue));
                Py_DECREF(pValue);
            }
            else {
                Py_DECREF(pFunc);
                Py_DECREF(pModule);
                PyErr_Print();
                fprintf(stderr, "Call failed\n");
                return 1;
            }
        }
        else {
            if (PyErr_Occurred())
                PyErr_Print();
            fprintf(stderr, "Cannot find function \"%s\"\n", "Test");
        }
        Py_XDECREF(pFunc);
        Py_DECREF(pModule);
    }
    else {
        PyErr_Print();
        fprintf(stderr, "Failed to load \"%s\"\n", "Test");
        return 1;
    }
    if (Py_FinalizeEx() < 0) {
        return 120;
    }
    return 0;

	ServerServiceRef service = MakeShared<ServerService>(
		NetAddress("127.0.0.1", 631),
		MakeShared<EpollCore>(),
		MakeShared<DetectingSession>,
		100);

	ASSERT_CRASH(service->Start());

	for (int32 i = 0; i < 5; i++)
	{
		GThreadManager->Launch([&service]()
			{
				DoWorkerJob(service);
			});
	}

    


	// Main Thread
	DoWorkerJob(service);

	GThreadManager->Join();
}
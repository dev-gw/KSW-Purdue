#include "NetAddress.h"

/*----------------
	SocketUtils
-----------------*/

class SocketUtils
{
public:


public:
	static void Init();
	static void Clear();


	static SOCKET CreateSocket();

	static bool SetLinger(SOCKET socket, uint16 onoff, uint16 inLinger);
	static bool SetReuseAddress(SOCKET socket, bool flag);
	static bool SetRecvBufferSize(SOCKET socket, int32 size);
	static bool SetSendBufferSize(SOCKET socket, int32 size);
	static bool SetTcpNoDelay(SOCKET socket, bool flag);
	static bool SetUpdateAcceptSocket(SOCKET socket, SOCKET listenSocket);


	static bool Bind(SOCKET socket, NetAddress netAddr);
	static bool BindAnyAddress(SOCKET socket, uint16 port);
	static bool Listen(SOCKET socket, int32 backlog = SOMAXCONN);
	static void Close(SOCKET& socket);

	static int MakeSocketNonBlocking(SOCKET& sfd);
};

template<typename T>
static inline bool SetSockOpt(SOCKET inSocket, int32 level, int32 optName, T optVal)
{
	return IS_VALID_SOCKET(setsockopt(inSocket, level, optName, reinterpret_cast<char*>(&optVal), sizeof(T)));
}
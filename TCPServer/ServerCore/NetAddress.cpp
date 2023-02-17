#include "pch.h"
#include "NetAddress.h"

/*--------------
	NetAddress
---------------*/

NetAddress::NetAddress(sockaddr_in sockAddr) : _sockAddr(sockAddr)
{
}

NetAddress::NetAddress(string ip, uint16 port)
{
	::memset(&_sockAddr, 0, sizeof(_sockAddr));
	_sockAddr.sin_family = AF_INET;
	_sockAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	_sockAddr.sin_port = ::htons(port);
}

string NetAddress::GetIpAddress()
{
	char buffer[100];
	inet_ntop(AF_INET, &_sockAddr.sin_addr, buffer, len32(buffer));
	return string(buffer);
}

in_addr NetAddress::Ip2Address(const char* ip)
{
	in_addr address;
	inet_pton(AF_INET, ip, &address);
	return address;
}
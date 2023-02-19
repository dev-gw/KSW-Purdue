#pragma once

/*--------------
	NetAddress
---------------*/

class NetAddress
{
public:
	NetAddress() = default;
	NetAddress(sockaddr_in sockAddr);
	NetAddress(string ip, uint16 port);

	sockaddr_in& GetSockAddr() { return _sockAddr; }
	string			GetIpAddress();
	uint16			GetPort() { return ::ntohs(_sockAddr.sin_port); }

public:
	static in_addr	Ip2Address(const char* ip);

private:
	sockaddr_in		_sockAddr = {};
};
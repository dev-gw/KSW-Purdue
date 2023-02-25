package com.example.acousticuavdetection.network
import java.net.InetSocketAddress

class NetAddress
{
    private var _sockAddr: InetSocketAddress
    constructor(ip: String, port: Int)
    {
        _sockAddr = InetSocketAddress(ip, port)
    }
    constructor(sockAddr: InetSocketAddress)
    {
        _sockAddr = sockAddr;
    }

    fun GetSockAddr(): InetSocketAddress { return _sockAddr; }
    fun GetIPAddress(): String? { return _sockAddr.hostString; }

    fun GetPort(): Int { return _sockAddr.port; }


}
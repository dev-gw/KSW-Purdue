package Network
import java.net.InetSocketAddress

class NetAddress
{
    private var _sockAddr: InetSocketAddress;
    constructor(sockAddr: InetSocketAddress)
    {
        _sockAddr = sockAddr;
    }
    constructor(ip: String, port: Int)
    {
        _sockAddr = InetSocketAddress(ip, port);
    }

    fun GetSockAddr(): InetSocketAddress { return _sockAddr; }
    fun GetIPAddress(): String? { return _sockAddr.hostString; }

    fun GetPort(): Int { return _sockAddr.port; }


}
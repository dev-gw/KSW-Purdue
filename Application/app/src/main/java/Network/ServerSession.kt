package Network

import java.nio.ByteBuffer
import kotlin.properties.Delegates

class ServerSession: PacketSession()
{

    private var _result by Delegates.observable(-1) {
            property, oldValue, newValue ->  GetService().GetMainActivity().changeResult(newValue) };


    override fun OnConnected()
    {
        println("On Connected");
        TODO("Not yet implemented")
    }

    override fun OnRecvPacket(buffer: ByteBuffer, len: Int)
    {
        //ServerPacketHandler.HandlePacket(this, buffer);
        TODO("Not yet implemented")
    }

    override fun OnSend(len: Int)
    {
        println("On Send");
        TODO("Not yet implemented")
    }

    override fun OnDisconnected()
    {
        println("On Disconnected");
        TODO("Not yet implemented")
    }

    fun SetDetectionResult(result: Int)
    {
        _result = result;
        // TODO
    }

}
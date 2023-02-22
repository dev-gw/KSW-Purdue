package Network

import java.nio.ByteBuffer

enum class PacketID(val id: UInt)
{
    C_LOGIN(1000U),
    S_LOGIN(1001U),
    C_AUDIO_DATA(1002U),
    S_DETECTION_RESULT(1003U),
}

class PacketHeader
{

}

public interface IPacket
{
    var pktSize: UInt;
    var pktID: UInt;
    fun Read(byteBuffer: ByteBuffer);
    fun Write(): ByteBuffer;
}

class PKT_C_AUDIO_DATA : IPacket
{
    override var pktSize = 4U + 4U + 4U * 40U;
    override var pktID = PacketID.C_AUDIO_DATA.id;
    var featureOffset: UInt = 4U + 4U;
    var featureCount: UInt = 40U;
    var featureData: FloatArray = FloatArray(featureCount as Int);


    override fun Read(byteBuffer: ByteBuffer)
    {

    }
    override fun Write(): ByteBuffer
    {
        var byteBuffer: ByteBuffer = ByteBuffer.allocate(pktSize as Int);

        byteBuffer.put(pktSize.toByte());
        byteBuffer.put(pktID.toByte());
        byteBuffer.put(featureOffset.toByte());
        byteBuffer.put(featureCount.toByte());

        for (i in featureData)
            byteBuffer.put(i.toInt().toByte());

        return byteBuffer;
    }
}


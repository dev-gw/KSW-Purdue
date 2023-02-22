package Network

import java.nio.ByteBuffer

enum class PacketID(val id: Int)
{
    C_LOGIN(1000),
    S_LOGIN(1001),
    C_AUDIO_DATA(1002),
    S_DETECTION_RESULT(1003),
}

class PacketHeader
{

}

public interface IPacket
{
    var pktSize: Int;
    var pktID: Int;
    fun Read(byteBuffer: ByteBuffer);
    fun Write(): ByteBuffer;
}

class PKT_C_AUDIO_DATA : IPacket
{
    override var pktSize = 4 + 4 + 4 * 40;
    override var pktID = PacketID.C_AUDIO_DATA.id;
    var featureOffset: Int = 4 + 4 + 4;
    var featureCount: Int = 40;
    var featureData: FloatArray = FloatArray(featureCount);


    override fun Read(byteBuffer: ByteBuffer)
    {

    }
    override fun Write(): ByteBuffer
    {
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(pktSize);

        byteBuffer.put(pktSize.toByte());
        byteBuffer.put(pktID.toByte());
        byteBuffer.put(featureOffset.toByte());
        byteBuffer.put(featureCount.toByte());

        for (i in featureData)
            byteBuffer.put(i.toInt().toByte());

        return byteBuffer;
    }
}


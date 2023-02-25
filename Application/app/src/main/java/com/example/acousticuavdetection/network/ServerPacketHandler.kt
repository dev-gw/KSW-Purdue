package com.example.acousticuavdetection.network

import java.nio.ByteBuffer



enum class DetectionResult(val id: UInt)
{
    autel_evo_2(0U),
    DJI_Phantom_4(1U),
    Noise(2U),
}

//

class ServerPacketHandler
{
    companion object
    {
        fun HandlePacket(session: Session, pktId: Int, buffer: ByteBuffer)
        {
            when (pktId)
            {
                PacketID.S_DETECTION_RESULT.id -> Handle_S_DETECTION_RESULT(session, buffer);
                else -> buffer.clear();
            }
        }

        fun Handle_S_DETECTION_RESULT(session: Session, buffer: ByteBuffer): Boolean
        {
            try
            {
                val serverSession = session as ServerSession;
                val result = buffer.getInt();
                buffer.clear();
                serverSession.SetDetectionResult(result);
                return true;
            }
            catch (e: Exception)
            {
                println(e);
                buffer.clear();
            }
            return false;
        }
    }
}
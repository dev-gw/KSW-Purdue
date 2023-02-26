package com.example.acousticuavdetection.network

import com.example.acousticuavdetection.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer


enum class ServiceType
{
    Server,
    Client
}


class ClientService constructor(private val _type: ServiceType, private val _netAddress: NetAddress, private var _sessionFactory: Session, private val _maxSessionCount: Int, private val _mainActivity: MainActivity)
{
    private lateinit var _selectorCore: SelectorCore;
    private lateinit var _session: Session
    private var _sessionCount : Int = 0;
    private var _sessions: MutableSet<Session> = mutableSetOf();

    fun Start() : Boolean
    {
        if (CanStart() == false)
            return false;

        _session = CreateSession();
        _session.SetService(this);

        assert(_session.Connect());

        _sessions.add(_session);

        return true;
    }
    fun CanStart() : Boolean { return _sessionFactory != null; }

    fun SendAudioData(floatArray: FloatArray)
    {
        var pkt: PKT_C_AUDIO_DATA = PKT_C_AUDIO_DATA();
        pkt.featureData = floatArray; // Put the array to packet

        var byteBuffer: ByteBuffer = pkt.Write(); // Convert contents of the packet to byte array
        byteBuffer.flip();
        CoroutineScope(Dispatchers.IO).launch()
        {
            _session.GetSocketChannel().write(byteBuffer); // Send through socket channel
        }

    }

    fun RecvData()
    {
         _session.RegisterRecv();
    }

    fun GetMainActivity(): MainActivity
    {
        return _mainActivity;
    }

    fun CloseService()
    {
        for (session in _sessions)
        {
            session.Disconnect();
        }
    }
    fun SetSessionFactory(func: Session) { _sessionFactory = func; }

    fun CreateSession() : Session
    {
        var session: Session = _sessionFactory;
        return session;
    }

    fun GetOwnSession(): Session
    {
        return _session;
    }

    fun AddSession(session: Session)
    {
        _sessionCount++;
        _sessions.add(session);
    }

    fun ReleaseSession(session : Session)
    {
        try
        {
            _sessions.remove(session);
            _sessionCount;
        }
        catch (e: java.lang.Exception)
        {
            // TODO
        }

    }
    fun GetCurrentSessionCount(): Int { return _sessionCount; }
    fun GetMaxSessionCount(): Int { return _maxSessionCount; }

    fun GetServiceType(): ServiceType { return _type; }
    fun GetNetAddress(): NetAddress { return _netAddress; }
    fun GetSelectorCore(): SelectorCore? { return _selectorCore; }


}
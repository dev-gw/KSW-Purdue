package com.example.acousticuavdetection.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.util.*


open class Session
{

    enum class BufferSize(val size: Int)
    {
        KB_64(0x10000),
    }

    open lateinit var _service: ClientService;
    open lateinit var _socketChannel: AsynchronousSocketChannel;
    open lateinit var _netAddress: NetAddress;
    open var _connected: Boolean = false;
    open lateinit var _recvBuffer: ByteBuffer;
    open var _sendQueue: Queue<ByteBuffer> = LinkedList<ByteBuffer>();

    fun Send(sendBuffer: ByteBuffer)
    {
        // If multithread used in application, code for race condition should be placed
        _sendQueue.add(sendBuffer);
        RegisterSend();
    }

    suspend fun Connect(): Boolean
    {
        var netAddress: NetAddress = GetService().GetNetAddress();
        try
        {
            withContext(Dispatchers.Default) { _socketChannel = AsynchronousSocketChannel.open(); }
            withContext(Dispatchers.Default){
                _socketChannel.connect(GetService().GetNetAddress().GetSockAddr())
            }

            _recvBuffer = ByteBuffer.allocate(BufferSize.KB_64.size);
            _connected = true;
            return true;
        }
        catch (e: Exception)
        {
            println(e);
            return false;
        }
    }

    fun Disconnect()
    {
        _socketChannel.close();
    }

    fun GetService(): ClientService
    {
        return _service;
    }

    fun SetService(service: ClientService)
    {
        _service = service;
    }

    fun SetNetAddress(netAddress: NetAddress) { _netAddress = netAddress; }

    fun GetAddress(): NetAddress { return _netAddress; }

    fun GetSocketChannel(): AsynchronousSocketChannel { return _socketChannel; }

    fun IsConnected(): Boolean { return _connected; }

    fun GetSession(): Session { return this }

    suspend fun RegisterRecv()
    {
        _recvBuffer.clear();
        withContext(Dispatchers.Default) {
            var readResult = _socketChannel.read(_recvBuffer)
            readResult.get();
        }

        ProcessRecv(_recvBuffer);
    }

    fun RegisterSend()
    {
        if (IsConnected() == false)
            return;

        var sendBuffer = ByteBuffer.allocate(_sendQueue.size * BufferSize.KB_64.size);

        while (_sendQueue.any() == true)
        {
            sendBuffer.put(_sendQueue.first());
            _sendQueue.remove();
        }

        var writeResult = _socketChannel.write(sendBuffer);
    }

    fun ProcessRecv(buffer: ByteBuffer)
    {
        var pktSize = buffer.getInt();
        assert(pktSize == buffer.limit());
        var pktId = buffer.getInt();

        ServerPacketHandler.HandlePacket(this, pktId, buffer);
    }

    open fun OnConnected() { }

    open fun OnSend(len: Int) { }

    open fun OnDisconnected() { }
}



open class PacketSession: Session()
{
    fun GetPacketSession(): PacketSession { return this;}

    open fun OnRecvPacket(buffer: ByteBuffer, len: Int) { }
}
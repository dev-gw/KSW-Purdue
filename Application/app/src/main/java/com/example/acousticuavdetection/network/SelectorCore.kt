package com.example.acousticuavdetection.network

import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class SelectorCore
{
    private val _selector: Selector = Selector.open();


    fun GetSelector(): Selector { return _selector; }
//    fun Dispatch()
//    {
//        while (_selector.select() > 0)
//        {
//            var keys = _selector.selectedKeys().iterator()
//            while (keys.hasNext())
//            {
//                var key = keys.next();
//
//                keys.remove();
//
//                if (key.isValid() == false)
//                    continue;
//
//                if (key.isReadable())
//                {
//                    var session = key.attachment() as Session;
//                    session.ProcessRecv(key);
//                }
//                else if (key.isWritable())
//                {
//                    this.Send(key);
//                }
//            }
//        }
//    }

    fun Send(key: SelectionKey)
    {
        try
        {
            var channel = key.channel() as SocketChannel;
            channel.configureBlocking(false);

            channel.write(key.attachment() as ByteBuffer);
        }
        catch (e: java.lang.Exception)
        {

        }
    }
}
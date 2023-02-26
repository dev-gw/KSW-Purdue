package com.example.acousticuavdetection.network

import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

class SelectorCore
{
    private val _selector: Selector = Selector.open();


    fun GetSelector(): Selector { return _selector; }

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
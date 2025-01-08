package org.server;

import org.springframework.stereotype.Service;

import java.util.Arrays;





@Service
public class UDPPacketForwarder {


    public void DecodeAndForwardAsVideo(Byte[] byteBuffer)
    {
        if(byteBuffer.length < 64)
        {
            // malformed packet
            return;
        }
    }

    public Byte[] DecodeAndForwardAsFrame(Byte[] byteBuffer)
    {
        if(byteBuffer.length < 64)
        {
            // malformed packet
            return null;
        }

        return Arrays.stream(byteBuffer).skip(64).toArray(Byte[]::new);
    }
}
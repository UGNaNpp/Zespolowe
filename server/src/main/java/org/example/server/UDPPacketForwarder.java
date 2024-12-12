package org.example.server;

import org.springframework.stereotype.Service;

import java.util.Arrays;

//
//  This class is responsible for decoding the UDP packets and forwarding them to the appropriate service
//
//  Depending on first order of bytes in the packet, the packet is identified in the sequence, if it is buffered
//  as a video.
//
//
//
//  Bytes sequence:
//
//  0-64 bytes
//  1st byte - Packet sequence number from 0 to 255 (no camera sufficient to provide more than that in time)
//
//  rest of the bytes - payload



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
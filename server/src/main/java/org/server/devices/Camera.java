package org.server.devices;


import org.apache.commons.lang3.ArrayUtils;
import org.server.StreamProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.util.Arrays.stream;
import static org.server.UDPConfig.UDP_PREHEADER_INFO_SIZE;

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
//  0-24 bytes - universal packet info / header
//  25-63 bytes - specific?
//  0-3 bytes - transmission id number
//  4-5 bytes - packet sequence number
//  6-7 bytes - total number of packets in transmission
//  8-11 bytes - total size of transmission
//  12-13 bytes - size of this packet payload
//  rest of the bytes - payload


public class Camera extends Device {
    @Autowired
    private StreamProvider streamProvider;
    final protected PacketAccumulator packetAccumulator = new PacketAccumulator();
    protected int heightResolution = -1;
    protected int widthResolution = -1;
    public boolean recordingMode = false;
    public boolean recordingVideo = false;

    public void newTransmission(byte[] transmission) {
    }
    public void newPacket(byte[] packet)
    {
        Byte[] packetObject = ArrayUtils.toObject(packet);

        ByteBuffer headerBuffer = ByteBuffer.wrap(packet, 0, UDP_PREHEADER_INFO_SIZE);
        int transmissionID = headerBuffer.getInt(0);
        short packetSequenceNumber = headerBuffer.getShort(4);
        short totalNumberOfPackets = headerBuffer.getShort(6);
        int totalSizeOfTransmission = headerBuffer.getInt(8);
        short sizeOfThisPayload = headerBuffer.getShort(12);

        ByteBuffer payloadBuffer = ByteBuffer.wrap(packet, UDP_PREHEADER_INFO_SIZE, sizeOfThisPayload);

        // begin new packet stream as the previous transmission has ended
        if(packetSequenceNumber == 0)
        {
            packetAccumulator.PacketSequenceNumber = 0;
            packetAccumulator.expectedPacketNumber = 0;
            packetAccumulator.transmissionID = transmissionID;
        }

        // packet out of expected sequence
        if(packetSequenceNumber != packetAccumulator.PacketSequenceNumber)
        {


            return;
        }

        // extract the valuable data from the packet

        Byte[] valuable_data = Arrays.copyOfRange(packetObject, UDP_PREHEADER_INFO_SIZE, UDP_PREHEADER_INFO_SIZE + sizeOfThisPayload);



        packetAccumulator.addPacket(valuable_data); // add the packet to the accumulator
        packetAccumulator.PacketSequenceNumber++;

        if(
                packetSequenceNumber == totalNumberOfPackets - 1
        )
        {
            Byte[] entire_data = packetAccumulator.getAccumulatedBytes();        // do something with this
            if(entire_data.length != totalSizeOfTransmission)
            {


                return;
            }
            newTransmission(packetAccumulator.getAccumulatedBytesPrimitive());

            packetAccumulator.clear();
            return;
        }

    }

    public Camera()
    {

    }
    public Camera(int heightResolution, int widthResolution)
    {
        this.heightResolution = heightResolution;
        this.widthResolution = widthResolution;
    }
}
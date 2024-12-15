package org.server.devices;


import org.apache.commons.lang3.ArrayUtils;
import org.server.StreamProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static java.util.Arrays.stream;
import static org.server.UDPConfig.UDP_PREHEADER_INFO_SIZE;


public class Camera {
    @Autowired
    private StreamProvider streamProvider;

    final protected PacketAccumulator packetAccumulator = new PacketAccumulator();

    protected int heightResolution = -1;
    protected int widthResolution = -1;

    public boolean recordingMode = false;

    public boolean recordingVideo = false;

    public void newPacket(byte[] packet)
    {
        Byte[] packetObject = ArrayUtils.toObject(packet);

        if(packetObject[1] == 0) // begin new packet stream as the previous packet stream has ended
        {
            Byte[] entire_data = packetAccumulator.getAccumulatedBytes();        // do something with this
            packetAccumulator.clear();
        }

        Byte[] valuable_data = (Byte[]) Arrays.stream(packetObject).skip(UDP_PREHEADER_INFO_SIZE).toArray();

        packetAccumulator.addPacket(valuable_data); // add the packet to the accumulator
    }
}
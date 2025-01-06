package org.server;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.server.devices.Camera;
import org.server.devices.Device;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class PacketingTests {
    @Mock
    private StreamProvider streamProvider;

    @InjectMocks
    Camera cam = new Camera(1280,720,0);

    @BeforeEach
    public void setup()
    {
        //streamProvider = Mockito.mock(StreamProvider.class);
    }

    @Test
    public void test3SimplePackets(){

        //header
        ByteBuffer header = ByteBuffer.allocate(64);
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 0);      // packet sequence number = 0
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet0 = new Byte[66];
        packet0 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{2,3});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 1);      // packet sequence number = 1
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet1 = new Byte[66];
        packet1 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{5,2});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 2);      // packet sequence number = 2
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet2 = new Byte[66];
        packet2 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{1,1});

        Byte[] packetsPayloadCombined = new Byte[]{2,3,5,2,1,1};

        doAnswer( invocation -> {
            Byte[] frame = (Byte[]) invocation.getArguments()[1];

            assertEquals(frame.length, packetsPayloadCombined.length);
            assertArrayEquals(frame, packetsPayloadCombined);

            return null;
        }).when(streamProvider).newFrame(anyLong(),any(Byte[].class));

        cam.newPacket(ArrayUtils.toPrimitive(packet0));
        cam.newPacket(ArrayUtils.toPrimitive(packet1));
        cam.newPacket(ArrayUtils.toPrimitive(packet2));
    }

    @Test
    void test3SimplePacketsOutOfOrder()
    {
        //header
        ByteBuffer header = ByteBuffer.allocate(64);
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 0);      // packet sequence number = 0
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2


        // set payload with header
        Byte[] packet0 = new Byte[66];
        packet0 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{2,3});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 1);      // packet sequence number = 1
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet1 = new Byte[66];
        packet1 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{5,2});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 2);      // packet sequence number = 2
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet2 = new Byte[66];
        packet2 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{1,1});

        Byte[] packetsPayloadCombined = new Byte[]{2,3,5,2,1,1};

        doAnswer( invocation -> {
            Byte[] frame = (Byte[]) invocation.getArguments()[1];

            assertEquals(frame.length, packetsPayloadCombined.length);
            assertArrayEquals(frame, packetsPayloadCombined);

            return null;
        }).when(streamProvider).newFrame(anyLong(),any(Byte[].class));

        cam.newPacket(ArrayUtils.toPrimitive(packet0));
        cam.newPacket(ArrayUtils.toPrimitive(packet2));
        cam.newPacket(ArrayUtils.toPrimitive(packet1));
    }

    @Test
    void test3SimplePacketsOutOfOrderOneMissing()
    {
        //header
        ByteBuffer header = ByteBuffer.allocate(64);
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 0);      // packet sequence number = 0
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2


        // set payload with header
        Byte[] packet0 = new Byte[66];
        packet0 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{2,3});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 1);      // packet sequence number = 1
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet1 = new Byte[66];
        packet1 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{5,2});

        //header
        header.putInt(0,0);           // transmission id = 0
        header.putShort(4, (short) 2);      // packet sequence number = 2
        header.putShort(6, (short) 3);      // total number of packets in transmission = 3
        header.putInt(8, 6);          // total size of transmission = 1280*720
        header.putShort(12, (short) 2);     // size of this packet payload = 2

        // set payload with header
        Byte[] packet2 = new Byte[66];
        packet2 = ArrayUtils.addAll(ArrayUtils.toObject(header.array()), new Byte[]{1,1});

        Byte[] packetsPayloadCombined = new Byte[]{2,3,5,2,1,1};

        doAnswer( invocation -> {
            Byte[] frame = (Byte[]) invocation.getArguments()[1];

            assertEquals(frame.length, packetsPayloadCombined.length);
            //assertArrayEquals(frame, packetsPayloadCombined);

            return null;
        }).when(streamProvider).newFrame(anyLong(),any(Byte[].class));

        cam.newPacket(ArrayUtils.toPrimitive(packet0));
        cam.newPacket(ArrayUtils.toPrimitive(packet2));
        //cam.newPacket(ArrayUtils.toPrimitive(packet1));
    }
}

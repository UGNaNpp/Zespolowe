package org.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.bytebuddy.implementation.EqualsMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.server.devices.Camera;
import org.server.devices.DeviceMapper;

import java.util.concurrent.ExecutionException;

public class StreamTest{
    private StreamProvider streamProvider;
    private DeviceMapper deviceMapper;
    private Camera mockCam;

    @BeforeEach
    public void setup()
    {
        mockCam = mock(Camera.class);
        deviceMapper = mock(DeviceMapper.class);
    }

    @Test
    public void streamBasicTest() throws ExecutionException, InterruptedException {
        streamProvider = new StreamProvider();

        Byte[] frame = new Byte[]{1,2};

        when(deviceMapper.getDeviceByID(0L)).thenReturn(mockCam);

        streamProvider.getLastFrame(0L).thenApply(f -> {
            return f;
        });

        streamProvider.newFrame(0L,frame);

        assertEquals(streamProvider.getLastFrame(0L).get(), frame);
    }

    @Test
    public void streamTimeoutTest() throws ExecutionException, InterruptedException {
        streamProvider = new StreamProvider();

        Byte[] frame = new Byte[]{1,2};

        when(deviceMapper.getDeviceByID(0L)).thenReturn(mockCam);

        streamProvider.getLastFrameTimeout(0L, 1000).thenApply(f -> {
            return f;
        });

        streamProvider.newFrame(0L,frame);

        assertEquals(streamProvider.getLastFrameTimeout(0L, 1000).get(), frame);
    }
}

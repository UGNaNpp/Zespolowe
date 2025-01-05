package org.server;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.bytebuddy.implementation.EqualsMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.server.devices.Camera;
import org.server.devices.DeviceMapper;

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
    public void continousStreamMock()
    {
        streamProvider = new StreamProvider();

        Byte[] frame = new Byte[]{1,2};

        when(deviceMapper.getDeviceByID(0L)).thenReturn(mockCam);


        //when(streamProvider.getLastFrame(0L, 300)).thenReturn(CompletableFuture.completedFuture(frame));
        streamProvider.getLastFrameTimeout(0L,3000).thenApply(f -> {
            assertEquals(f, frame);
            return f;
        });

        streamProvider.newFrame(0L,frame);




    }
}

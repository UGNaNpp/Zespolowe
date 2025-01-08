package org.server;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Component("StreamProvider")
public class StreamProvider {

    @Autowired
    private DeviceMapper deviceMapper;

    private ConcurrentHashMap<Long, Pair<Long, CompletableFuture<Byte[]>>> lastFrames = new ConcurrentHashMap
            <Long,Pair<Long, CompletableFuture<Byte[]>>>();

    public synchronized void newFrame(Long deviceID, Byte[] frame) {
        long newFrameId = System.currentTimeMillis(); // Assuming frame ID is a timestamp or unique value.

        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.getOrDefault(deviceID,
                new ImmutablePair<>(deviceID, new CompletableFuture<>())
        );

        CompletableFuture<Byte[]> future = currentPair.getRight();
        if(!future.isDone()) {
            future.complete(frame);

            String FILEPATH = "test.jpeg";
            File file = new File(FILEPATH);

            try {

                // Initialize a pointer in file
                // using OutputStream
                OutputStream os = new FileOutputStream(file);

                // Starting writing the bytes in it
                os.write(ArrayUtils.toPrimitive(frame));

                // Display message onconsole for successful
                // execution
                System.out.println("Successfully"
                        + " byte inserted");

                // Close the file connections
                os.close();
            }

            // Catch block to handle the exceptions
            catch (Exception e) {

                // Display exception on console
                System.out.println("Exception: " + e);
            }

        }
        else {
            future = new CompletableFuture<>();
            future.completeOnTimeout(frame,10,TimeUnit.MILLISECONDS);
        }
        //lastFrames.put(deviceID, new ImmutablePair<>(newFrameId, new CompletableFuture<>()));
    }

    public CompletableFuture<Byte[]> getLastFrameTimeout(Long deviceID, long timeoutMillis) {
        // Get or initialize the frame pair with a CompletableFuture.
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.computeIfAbsent(deviceID,
                id -> new ImmutablePair<>(0L, new CompletableFuture<>())
        );

        // Return the CompletableFuture with a timeout applied.
        return currentPair.getRight().orTimeout(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public CompletableFuture<Byte[]> getLastFrame(Long deviceID) {
        // Get or initialize the frame pair with a CompletableFuture.
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.computeIfAbsent(deviceID,
                id -> new ImmutablePair<>(0L, new CompletableFuture<>())
        );

        // Return the CompletableFuture with a timeout applied.
        return currentPair.getRight();
    }

    public void cancelWaiting(Long deviceID) {
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.get(deviceID);
        if (currentPair != null) {
            currentPair.getRight().cancel(true);
        }
    }

}

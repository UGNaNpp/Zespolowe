package org.server;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Component
public class StreamProvider {

    @Autowired
    private DeviceMapper deviceMapper;

    private ConcurrentHashMap<Long, Pair<Long, CompletableFuture<Byte[]>>> lastFrames = new ConcurrentHashMap
            <Long,
                    Pair<Long, CompletableFuture<Byte[]>>>();

    public synchronized void newFrame(Long deviceID, Byte[] frame) {
        long newFrameId = System.currentTimeMillis(); // Assuming frame ID is a timestamp or unique value.

        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.getOrDefault(deviceID,
                new ImmutablePair<>(0L, new CompletableFuture<>())
        );

        CompletableFuture<Byte[]> future = currentPair.getRight();
        future.complete(frame);

        lastFrames.put(deviceID, new ImmutablePair<>(newFrameId, new CompletableFuture<>()));
    }

    public CompletableFuture<Byte[]> getLastFrame(Long deviceID, long timeoutMillis) {
        // Get or initialize the frame pair with a CompletableFuture.
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.computeIfAbsent(deviceID,
                id -> new ImmutablePair<>(0L, new CompletableFuture<>())
        );

        // Return the CompletableFuture with a timeout applied.
        return currentPair.getRight().orTimeout(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public void cancelWaiting(Long deviceID) {
        // Cancel the CompletableFuture if it exists.
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.get(deviceID);
        if (currentPair != null) {
            currentPair.getRight().cancel(true);
        }
    }

}

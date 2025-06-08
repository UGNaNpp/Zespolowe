package org.server;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@EnableAsync
@Component("StreamProvider")
public class StreamProvider {

    @Value("${filepath.media}")
    private String mediaFilepath;

    private ConcurrentHashMap<Long, Pair<Long, CompletableFuture<Byte[]>>> lastFrames = new ConcurrentHashMap
            <Long,Pair<Long, CompletableFuture<Byte[]>>>();

    public synchronized void newFrame(Long deviceID, Byte[] frame) {
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.get(deviceID);

        if(currentPair == null) {       // nikt nie czeka na to
            return;
        }
        CompletableFuture<Byte[]> future = currentPair.getRight();

        if(!future.isDone()) {
            future.complete(frame);
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String FILEPATH = mediaFilepath + "/frames/" + formattedDate + "/" + System.currentTimeMillis() + ".jpeg";

            File file = new File(FILEPATH);
            File parentDir = file.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs(); // kosztowna tylko przy pierwszym wywołaniu dla danej daty
            }

            try (OutputStream os = new FileOutputStream(file)) {
                os.write(ArrayUtils.toPrimitive(frame));
            }
            // Catch block to handle the exceptions
            catch (Exception e) {
                // Display exception on console
                System.out.println("Exception: " + e);
            }
        }
        else {
            future.cancel(true);
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
        Pair<Long, CompletableFuture<Byte[]>> currentPair;
        if(lastFrames.containsKey(deviceID))
        {
            currentPair = lastFrames.replace(deviceID,
                    new ImmutablePair<>(0L, new CompletableFuture<>())
            );
        }
        else{
            currentPair = lastFrames.put(deviceID,
                    new ImmutablePair<>(0L, new CompletableFuture<>())
            );
        }

        // Return the CompletableFuture with a timeout applied.
        return lastFrames.get(deviceID).getRight();
    }

    public void cancelWaiting(Long deviceID) {
        Pair<Long, CompletableFuture<Byte[]>> currentPair = lastFrames.get(deviceID);
        if (currentPair != null) {
            currentPair.getRight().cancel(true);
        }
    }

}

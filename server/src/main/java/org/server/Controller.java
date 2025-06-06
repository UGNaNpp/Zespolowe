package org.server;

import jakarta.servlet.http.HttpServletResponse;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@RestController
public class Controller {
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private StreamProvider streamProvider;

    @GetMapping(
            value = "/{id}/stream",
            produces = "multipart/x-mixed-replace;boundary=frame12344321")
    public ResponseEntity<StreamingResponseBody> cameraStream(
            @PathVariable("id") Long id,
            HttpServletResponse response
               ) {
        response.setContentType("multipart/x-mixed-replace;boundary=frame12344321");
        response.setHeader("Connection", "keep-alive");

        int k = deviceMapper.getDeviceByID(id).whatAmI();

        if (k != 0) {
            ResponseEntity.badRequest().body("Device is not camera");
        }

        StreamingResponseBody responseBody = outputStream -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // Get the latest frame asynchronously
                        Byte[] frame = streamProvider.getLastFrame(id).get();

                        // Convert Byte[] to byte[] and write to the output stream
                        byte[] frameData = new byte[frame.length];
                        for (int i = 0; i < frame.length; i++) {
                            frameData[i] = frame[i];
                        }


                        outputStream.write(("Content-Type: image/jpeg Content-Length: " + frame.length + "\r\n\r\n").getBytes());
                        outputStream.write(frameData);
                        outputStream.write(("\r\n--frame12344321\r\n").getBytes());
                        //outputStream.write("\r\n".getBytes());

                        outputStream.flush();
                    } catch (IOException ioException) {
                        System.err.println("Connection closed: " + ioException.getMessage());
                        break;
                    } catch (Exception e) {
                        System.err.println("Error streaming frame: " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception ex) {
                // Log the general exception for debugging
                System.err.println("Streaming failed: " + ex.getMessage());
            }
        };
        return ResponseEntity.ok().body(responseBody);
    }


    @GetMapping("/active-cameras")
    ResponseEntity<Map<Long, Boolean>> getActiveCameras() {
        Long[] available = this.deviceMapper.getAllCamerasIDs();
        long timeoutMillis = 1000;

        if (available.length == 0) { return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK); }

        ExecutorService executor = Executors.newFixedThreadPool(available.length);
        Map<Long, Boolean> resultMap = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(available.length);

        for (Long id : available) {
            executor.submit(() -> {
                try {
                    Future<Byte[]> futureFrame = streamProvider.getLastFrame(id);
                    Byte[] frame = futureFrame.get(timeoutMillis, TimeUnit.MILLISECONDS);

                    boolean isValid = frame != null && frame.length > 0;
                    resultMap.put(id, isValid);
                } catch (TimeoutException e) {
//                    System.out.println("Timeout for device " + id);
                    resultMap.put(id, false);
                } catch (Exception e) {
//                    System.out.println("Error for device " + id + ": " + e.getMessage());
                    resultMap.put(id, false);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(timeoutMillis, TimeUnit.MILLISECONDS); // +buffer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
        }

        return ResponseEntity.ok(resultMap);
    }
}
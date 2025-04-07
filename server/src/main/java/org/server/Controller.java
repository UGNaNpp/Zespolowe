package org.server;

import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import jakarta.servlet.http.HttpServletRequest;


import java.io.IOException;

@RestController
public class Controller {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StreamProvider streamProvider;

    @GetMapping(
            value = "/{id}/stream",
            produces = "multipart/x-mixed-replace;boundary=frame")
    public ResponseEntity<StreamingResponseBody> cameraStream(
            @PathVariable("id") Long id
    ) {
        if (deviceMapper.getDeviceByID(id).whatAmI() == 0) {
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

                        outputStream.write(("--frame\r\n").getBytes());
                        outputStream.write(("Content-Type: image/jpeg Content-Length: " + frame.length + "\r\n").getBytes());
                        outputStream.write(frameData);
                        outputStream.write("\r\n".getBytes());

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

    @Deprecated
    @GetMapping("/public/test-button")
    public void testButton(HttpServletRequest request) {
        System.out.println("Otrzymaono żądanie");
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                System.out.println("Cookie: " + cookie.getName() + "=" + cookie.getValue());
            }
        }
    }
}
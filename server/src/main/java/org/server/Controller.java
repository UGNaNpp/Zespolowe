package org.server;

import jakarta.servlet.http.HttpServletResponse;
import org.server.devices.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
public class Controller {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StreamProvider streamProvider;

    @GetMapping(
            value = "/{id}/stream",
            produces = "multipart/x-mixed-replace;boundary=frame1234")
    public ResponseEntity<StreamingResponseBody> cameraStream(
            @PathVariable("id") Long id,
            HttpServletResponse response
    ) {
        response.setContentType("multipart/x-mixed-replace;boundary=frame1234");

        if(deviceMapper.getDeviceByID(id).whatAmI() == 0)
        {
            ResponseEntity.badRequest().body("Device is not camera");
        }
        
        StreamingResponseBody responseBody = outputStream -> {
            try {

                Byte[] prevFrame = null;

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // Get the latest frame asynchronously

                        CompletableFuture<Byte[]> frameFuture = streamProvider.getLastFrame(id);

                        Byte[] frame = frameFuture.get();

                        if(prevFrame == frame)  // czy to samo, NIE CZY MAJA TO SAMO
                        {
                            continue;
                        }



                        prevFrame = frame;

                        // Convert Byte[] to byte[] and write to the output stream
                        byte[] frameData = new byte[frame.length];
                        for (int i = 0; i < frame.length; i++) {
                            frameData[i] = frame[i];
                        }

                        outputStream.write(("Content-Type: image/jpeg\r\nContent-Length: " + frame.length + "\r\n\r\n").getBytes());
                        outputStream.write(frameData);
                        outputStream.write(("\r\n--frame1234\r\n").getBytes());


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
}
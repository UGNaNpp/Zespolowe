package org.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;


@RestController
public class Controller {
    @Autowired
    private StreamProvider streamProvider;

    @GetMapping(
            value = "/{id}/stream",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<StreamingResponseBody> cameraStream(
            @PathVariable("id") Long id
    ) {
        StreamingResponseBody responseBody = outputStream -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // Get the latest frame asynchronously with a timeout of 300ms
                        Byte[] frame = streamProvider.getLastFrame(id, 300).join();

                        // Convert Byte[] to byte[] and write to the output stream
                        byte[] frameData = new byte[frame.length];
                        for (int i = 0; i < frame.length; i++) {
                            frameData[i] = frame[i];
                        }
                        outputStream.write(frameData);
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
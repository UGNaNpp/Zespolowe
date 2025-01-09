package org.server.sendFiles;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.sarxos.webcam.Webcam;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

@RestController
public class WebcamStreamController {
    private final Webcam webcam;

    public WebcamStreamController() {
        this.webcam = Webcam.getDefault();
        if (this.webcam == null) {
            throw new IllegalStateException("No webcam detected!");
        }
    }

    @GetMapping(value = "/public/webcam", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void stream(HttpServletResponse response) {
        response.setContentType("multipart/x-mixed-replace; boundary=frame");

        if (!this.webcam.isOpen()) this.webcam.open();

        try (OutputStream out = response.getOutputStream()) {
            while (true) {
                if (webcam.isOpen()) {
                    BufferedImage image = webcam.getImage();
                    if (image != null) {
                        out.write(("--frame\r\n").getBytes());
                        out.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());

                        ImageIO.write(image, "JPEG", out);

                        out.write("\r\n".getBytes());
                        out.flush();
                    }
                }
                Thread.sleep(100);
            }
        } catch (Exception e) {
            this.webcam.close();
            if (!e.getMessage().contains("Nawiązane połączenie zostało przerwane przez oprogramowanie zainstalowane w komputerze-hoście")) {
                e.printStackTrace();
            }
        }
    }
}

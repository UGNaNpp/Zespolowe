//package org.server.sendFiles;
//
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.github.sarxos.webcam.Webcam;
//import java.io.IOException;
//
//import javax.imageio.ImageIO;
//import jakarta.servlet.http.HttpServletResponse;
//import java.awt.image.BufferedImage;
//import java.io.OutputStream;
//
//@RestController
//public class WebcamStreamController {
//    private final Webcam webcam;
//
//    public WebcamStreamController() {
//        this.webcam = Webcam.getDefault();
//        if (this.webcam == null) {
//            throw new IllegalStateException("No webcam detected!");
//        }
//    }
//
//    @GetMapping(value = "/public/webcam", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public void stream(HttpServletResponse response) throws IOException {
//        response.setContentType("multipart/x-mixed-replace; boundary=frame");
//
//        if (!this.webcam.isOpen()) this.webcam.open();
//        OutputStream out = null;
//
//        try {
//            out = response.getOutputStream();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//
//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                if (webcam.isOpen()) {
//                    BufferedImage image = webcam.getImage();
//                    if (image != null) {
//                        out.write(("--frame\r\n").getBytes());
//                        out.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
//
//                        ImageIO.write(image, "JPEG", out);
//
//                        out.write("\r\n".getBytes());
//                            out.flush();
//                    }
//                }
//
//            } catch (IOException ioException) {
//                out.flush();
//                this.webcam.close();
//                System.err.println("Connection closed: " + ioException.getMessage());
//                break;
//            } catch (Exception e) {
//                out.flush();
//                this.webcam.close();
//                if (!e.getMessage().contains("Nawiązane połączenie zostało przerwane przez oprogramowanie zainstalowane w komputerze-hoście")) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//    }
//}

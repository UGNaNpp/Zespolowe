package org.server.devices;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.server.StreamProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MockCamera extends Camera {

    private final String videoPath;

    public MockCamera(long id, String videoPath) {
        this.id = id;
        this.videoPath = videoPath;
        this.name = "mock";
    }

    public void startMockStreaming(int fps) {
    Executors.newSingleThreadExecutor().submit(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
                grabber.start();
                Java2DFrameConverter converter = new Java2DFrameConverter();

                long frameDelay = 1000L / fps;

                while (!Thread.currentThread().isInterrupted()) {
                    var frame = grabber.grabImage();
                    if (frame == null) break; // koniec filmu

                    BufferedImage image = converter.convert(frame);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpeg", baos);
                    baos.flush();
                    byte[] byteArray = baos.toByteArray();
                    baos.close();

                    Byte[] boxed = new Byte[byteArray.length];
                    for (int i = 0; i < byteArray.length; i++) {
                        boxed[i] = byteArray[i];
                    }

                    newTransmission(boxed);

                    TimeUnit.MILLISECONDS.sleep(frameDelay);
                }

                grabber.stop(); // niepotrzebne z try-with-resources, ale nie zaszkodzi
            } catch (Exception e) {
                e.printStackTrace();
                break; // przerywamy zapętlanie, jeśli coś poszło źle
            }
        }
    });

    }

    @Override
    public void newPacket(byte[] packet) {
        // zablokuj odbieranie pakietów UDP – to mock, nie kamera fizyczna
    }

    @Override
    public void newTransmission(Byte[] transmission) {
        // możesz np. zalogować info albo po prostu wywołać oryginalne zachowanie
        super.newTransmission(transmission);
    }
}

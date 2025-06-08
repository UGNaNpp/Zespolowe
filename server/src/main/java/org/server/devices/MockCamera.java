package org.server.devices;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import org.server.StreamProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.File;


public class MockCamera extends Camera {
    private final String videoPath;

    public MockCamera(long id, String videoPath, String AssociatedIP, String AssociatedMAC) {
        this.id = id;
        this.videoPath = videoPath;
        this.name = "mock";
        this.AssociatedIP = AssociatedIP;
        this.AssociatedMAC = AssociatedMAC;
        this.recordingMode = true;
    }

    public void startMockStreaming(int fps) {
    Executors.newSingleThreadExecutor().submit(() -> {
        while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Mocked camera video exists? " + new File(videoPath).exists());
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

                grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    });
    }

    @Override
    public void newPacket(byte[] packet) {
        // zablokuj odbieranie pakietów UDP – to mock, nie kamera fizyczna
    }

    @Override
    public final byte whatAmI() {
        return 0;
    }

    @Override
    public String toString() {
        return "MockCamera{" +
                "videoPath='" + videoPath + '\'' +
                ", packetAccumulator=" + packetAccumulator +
                ", heightResolution=" + heightResolution +
                ", widthResolution=" + widthResolution +
                ", recordingMode=" + recordingMode +
                ", recordingVideo=" + recordingVideo +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", AssociatedIP='" + AssociatedIP + '\'' +
                ", AssociatedMAC='" + AssociatedMAC + '\'' +
                '}';
    }
}

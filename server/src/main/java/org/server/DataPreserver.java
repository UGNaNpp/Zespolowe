//package org.server;
//
//import org.apache.commons.lang3.ArrayUtils;
//import org.bridj.jawt.JAWTUtils;
//import org.jcodec.api.SequenceEncoder;
//import org.jcodec.common.io.NIOUtils;
//import org.jcodec.common.model.ColorSpace;
//import org.jcodec.common.model.Picture;
//import org.jcodec.common.model.Rational;
//import org.jcodec.scale.*;
//import org.server.devices.Device;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class DataPreserver {
//
//    Map<Long,List<Byte[]>> frameBuffer = new HashMap<>();
//    Map<Long,Instant> last_frame_times = new HashMap<>();
//
//
//    public void VideoFileSave(Device dev)
//    {
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
//
//        String formattedTime = now.format(formatter);
//
//
//        String fileName = dev.id + "/" + formattedTime + ".mp4";
//
//        File outputFile = new File("fileName");
//
//        try {
//            SequenceEncoder encoder = SequenceEncoder.createSequenceEncoder(
//                    outputFile,
//                    25
//            );
//
//
//            List<Byte[]> frameBuffer = this.frameBuffer.get(dev.id);
//
//            for (Byte[] frame : frameBuffer) {
//
//                byte[] byte_frame = ArrayUtils.toPrimitive(frame);
//
//                InputStream is = new ByteArrayInputStream(byte_frame);
//                BufferedImage bufferedFrame = ImageIO.read(is);
//                if (frame != null) {
//                    Picture pic = AWTUtil.fromBufferedImage(bufferedFrame, ColorSpace.RGB);
//
//                    encoder.encodeNativeFrame(pic);
//                }
//            }
//            encoder.finish();
//
//        }
//        catch
//        (Exception e)
//        {
//            System.out.println("Error while creating video file");
//            e.printStackTrace();
//            return;
//        }
//    }
//
//    public void FrameSave(Byte[] buffer, Device dev)
//    {
//        frameBuffer.putIfAbsent(dev.id, new ArrayList<>());
//
//        try {
////            frameBuffer.get(dev.id).add(ImageIO.read(new ByteArrayInputStream(ArrayUtils.toPrimitive(buffer)));
//        }
//        catch (Exception e)
//        {
//            System.out.println("Error while saving frame");
//            e.printStackTrace();
//        }
//
//
//
//        Instant now = Instant.now();
//
//        if(
//            last_frame_times.containsKey(dev.id) &&
//            now.minusSeconds(300).isAfter(last_frame_times.get(dev.id))
//        )
//        {
//            VideoFileSave(dev);
//        }
//
//
//
//
//    }
//}
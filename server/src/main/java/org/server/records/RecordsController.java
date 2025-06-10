package org.server.records;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/records")
public class RecordsController {
    @Value("${filepath.media}")
    private String mediaFilePath;

    private RecordsService recordsService;

    public RecordsController(RecordsService recordsService) {
        this.recordsService = recordsService;
    }

    @GetMapping("/size")
    public ResponseEntity<Long> getRecordsSize() {
        try (Stream<Path> paths = Files.walk(Path.of(mediaFilePath))) {
            long size = paths
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
            return new ResponseEntity<>(size, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/available-days")
    public List<LocalDate> getRecordsAvailableDays() {
        try (Stream<Path> paths = Files.list(Path.of(mediaFilePath + "/frames/"))) {
            return paths
                    .filter(Files::isDirectory)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .map(LocalDate::parse)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/available-cameras-by-date/{date}")
    public ResponseEntity<List<String>> getRecordsAvailableCamerasByDate(@PathVariable LocalDate date) {
        try (Stream<Path> paths = Files.list(Path.of(mediaFilePath + "/frames/" + date))) {
            List<String> result = paths
                .filter(Files::isDirectory)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(Collections.emptyList() ,HttpStatus.OK);
        }
    }

    @GetMapping("/last-frame/{cameraId:\\d+}/{datetime}")
    public ResponseEntity<byte[]> getLastFrameBefore(
            @PathVariable String cameraId,
            @PathVariable String datetime) {     // w formacie ISO 8601 yyyy-MM-ddTHH:mm:ss

        try {
            LocalDateTime target = LocalDateTime.parse(datetime);
            String targetDate = target.toLocalDate().toString();
            Path cameraFolder = Path.of(mediaFilePath, "frames", targetDate, cameraId);

            if (!Files.exists(cameraFolder) || !Files.isDirectory(cameraFolder)) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Path> lastImage = Files.list(cameraFolder)
                    .filter(p -> p.toString().endsWith(".jpeg"))
                    .filter(p -> {
                        String name = p.getFileName().toString().replace(".jpeg", "");
                        try {
                            long timestamp = Long.parseLong(name);
                            LocalDateTime fileTime = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            return !fileTime.isAfter(target); // przed LUB równa target
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .max(Comparator.comparingLong(p -> Long.parseLong(
                            p.getFileName().toString().replace(".jpeg", ""))));

            if (lastImage.isPresent()) {
                byte[] imageBytes = Files.readAllBytes(lastImage.get());
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/stream/{cameraId:\\d+}/{datetime}/{fps}", produces = "multipart/x-mixed-replace; boundary=frame")
    public void streamFramesBefore(
        @PathVariable String cameraId,
        @PathVariable String datetime,
        @PathVariable int fps,
        HttpServletResponse response) {

        try {
            LocalDateTime target = LocalDateTime.parse(datetime);
            String targetDate = target.toLocalDate().toString();
            Path cameraFolder = Path.of(mediaFilePath, "frames", targetDate, cameraId);

            if (!Files.exists(cameraFolder) || !Files.isDirectory(cameraFolder)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            List<Path> images = Files.list(cameraFolder)
                    .filter(p -> p.toString().endsWith(".jpeg"))
                    .filter(p -> {
                        try {
                            long timestamp = Long.parseLong(p.getFileName().toString().replace(".jpeg", ""));
                            LocalDateTime fileTime = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            return !fileTime.isAfter(target);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    })
                    .sorted(Comparator.comparingLong(p ->
                            Long.parseLong(p.getFileName().toString().replace(".jpeg", ""))))
                    .toList();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("multipart/x-mixed-replace; boundary=frame");

            long delayMillis = 1000L / Math.max(fps, 1);

            try (ServletOutputStream out = response.getOutputStream()) {
                for (Path image : images) {
                    byte[] content = Files.readAllBytes(image);

                    out.println("--frame");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: " + content.length);
                    out.println();
                    out.write(content);
                    out.println();
                    out.flush();

                    Thread.sleep(delayMillis);
                }
            }

        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException | InterruptedException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/video")
    public void getVideo(
            @RequestParam long cameraId,
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime stopDateTime,
            HttpServletResponse response
    ) {
        int frameRate = 10;

        try {
            List<Path> images = imagesPathsListForVideo(cameraId, startDateTime, stopDateTime);
            if (images.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

            BufferedImage firstImage = ImageIO.read(images.get(0).toFile());
            int width = firstImage.getWidth();
            int height = firstImage.getHeight();

            response.setContentType("video/webm");
            response.setHeader("Content-Disposition", "inline; filename=\"video.webm\"");

            try (ServletOutputStream out = response.getOutputStream()) {
                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(out, width, height);
                recorder.setFrameRate(frameRate);
                recorder.setVideoCodec(avcodec.AV_CODEC_ID_VP8); // VP8 zamiast H264
                recorder.setFormat("webm");
                recorder.setPixelFormat(0); // yuv420p

                recorder.start();
                Java2DFrameConverter converter = new Java2DFrameConverter();

                for (Path path : images) {
                    BufferedImage img = ImageIO.read(path.toFile());
                    if (img.getWidth() != width || img.getHeight() != height) {
                        System.err.println("Pomijam obraz o innym rozmiarze: " + path);
                        continue;
                    }
                    Frame frame = converter.convert(img);
                    recorder.record(frame);
                }

                recorder.stop();
                recorder.release();
            }

        } catch (Exception e) {
//            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/video/save")
    public ResponseEntity<String> saveVideoToDisk(
            @RequestParam long cameraId,
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime stopDateTime
    ) {
        int frameRate = 10;

        try {
            List<Path> images = imagesPathsListForVideo(cameraId, startDateTime, stopDateTime);
            if (images.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            BufferedImage firstImage = ImageIO.read(images.get(0).toFile());
            int width = firstImage.getWidth();
            int height = firstImage.getHeight();

            // Ustal ścieżkę docelową (np. videos/video_<id>.mp4)
            String filename = "video_" + cameraId + "_" + System.currentTimeMillis() + ".mp4";
            Path outputPath = Paths.get("videos").resolve(filename);
            Files.createDirectories(outputPath.getParent());

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath.toString(), width, height);
            recorder.setFrameRate(frameRate);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setPixelFormat(0); // yuv420p

            recorder.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();

            for (Path path : images) {
                BufferedImage img = ImageIO.read(path.toFile());
                if (img.getWidth() != width || img.getHeight() != height) {
                    System.err.println("Pomijam obraz o innym rozmiarze: " + path);
                    continue;
                }
                Frame frame = converter.convert(img);
                recorder.record(frame);
            }

            recorder.stop();
            recorder.release();

            return ResponseEntity.ok("/videos/" + filename);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Błąd przy generowaniu wideo: " + e.getMessage());
        }
    }





    private List<Path> imagesPathsListForVideo(Long cameraId, LocalDateTime startDateTime,
                                     LocalDateTime stopDateTime) throws IOException {

    Path directory = Path.of(mediaFilePath, "frames",startDateTime.toLocalDate().toString(), cameraId.toString());
    String randomFilename = UUID.randomUUID().toString().replace("-", "").substring(0,10) + "-ffmpeg-list.txt";
    Path outputListFile = Path.of(directory.toString(), randomFilename);
    List<Path> images = new ArrayList<>();


    try (Stream<Path> stream = Files.list(directory)) {
        images = stream
            .filter(p -> p.toString().endsWith(".jpeg"))
            .filter(p -> {
                String name = p.getFileName().toString().replace(".jpeg", "");
                try {
                    long timestamp = Long.parseLong(name);
                    LocalDateTime fileTime = Instant.ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    return !fileTime.isBefore(startDateTime) && !fileTime.isAfter(stopDateTime);
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .toList();
    }
    return images;


}


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleInvalidDateFormat(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == LocalDate.class) {
            return new ResponseEntity<>("Invalid date format. Expected format: yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        } else if (ex.getRequiredType() == LocalDateTime.class) {
        return new ResponseEntity<>("Invalid datetime format. Expected format: yyyy-MM-dd'T'HH:mm:ss", HttpStatus.BAD_REQUEST);
    }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

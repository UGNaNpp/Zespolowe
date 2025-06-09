package org.server;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
                System.out.println(cameraFolder);
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



    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleInvalidDateFormat(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == LocalDate.class) {
            return new ResponseEntity<>("Invalid date format. Expected format: yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

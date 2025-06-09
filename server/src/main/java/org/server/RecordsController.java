package org.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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



    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleInvalidDateFormat(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == LocalDate.class) {
            return new ResponseEntity<>("Invalid date format. Expected format: yyyy-MM-dd", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

package org.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/avaible-days")
    public List<LocalDate> getRecordsAvailableDays() {
        try (Stream<Path> paths = Files.list(Path.of(mediaFilePath + "/frames/"))) {
            return paths
                    .filter(Files::isDirectory)
                        .map(Path::getFileName)                     // -> "2025-06-08"
                        .map(Path::toString)
                        .map(LocalDate::parse)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}

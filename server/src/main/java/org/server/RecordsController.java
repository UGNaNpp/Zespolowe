package org.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController("/record")
public class RecordsController {
    @Value("${filepath.media}")
    private String mediaFilePath;

    @GetMapping("/size")
    public ResponseEntity<Long> getRecordsSize() {
        Path folder = Paths.get(mediaFilePath);
        try {
            long size = Files.walk(folder)
                    .filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
            return new ResponseEntity<>(size, HttpStatus.OK);
        } catch (Exception e){
           return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

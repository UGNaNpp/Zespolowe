package org.server.records;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
@Component
public class RecordsService { // Just simple FFmpeg util service
    @Value("${filepath.media}")
    private String mediaFilePath;

    List<Path> imagesPathsListForVideo(Long cameraId, LocalDateTime startDateTime,
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


}

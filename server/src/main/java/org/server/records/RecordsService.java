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

@Component
public class RecordsService { // Just simple FFmpeg util service
    @Value("${filepath.media}")
    private String mediaFilePath;

    Path generateFFmpegConcatFile(Long cameraId, LocalDateTime startDateTime,
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

    if (images.size() < 2) throw new IllegalArgumentException("Need at least 2 images to compute durations");

    Path parentDir = outputListFile.getParent();

    try (BufferedWriter writer = Files.newBufferedWriter(outputListFile)) {
        for (int i = 0; i < images.size() - 1; i++) {
            Path current = images.get(i);
            Path next = images.get(i + 1);

            long t1 = Long.parseLong(current.getFileName().toString().replace(".jpeg", ""));
            long t2 = Long.parseLong(next.getFileName().toString().replace(".jpeg", ""));
            double duration = (t2 - t1) / 1000.0;

            writer.write("file '" + current.toAbsolutePath() + "'\n");
            writer.write("duration " + duration + "\n");
        }
        // Ostatni plik należy zapisać bez duration.
        writer.write("file '" + images.get(images.size() - 1).toAbsolutePath() + "'\n");
    }
    return outputListFile;
}

    Path generateVideo(Path configFile) throws IOException, InterruptedException {
        String outputPath = configFile.getFileName().toString().replace("-ffmpeg-list.txt", "-video.mp4");

         ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-f", "concat",
                "-safe", "0",
                "-i", configFile.toAbsolutePath().toString(),
                "-vsync", "vfr",
//                "-pix_fmt", "yuv420p",
                outputPath
        );
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with code " + exitCode);
        }

    return Path.of(outputPath);
    }

}

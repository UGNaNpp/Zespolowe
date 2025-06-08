package org.server.sendFiles;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;

@Deprecated
@Controller
public class VideoStreamController {

    @GetMapping("/public/video")
    public ResponseEntity<Resource> streamVideo(
            @RequestParam String fileName,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws Exception {

        Resource videoResource = new ClassPathResource("video/" + fileName);

        if (!videoResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        File videoFile = videoResource.getFile();
        long fileSize = videoFile.length();

        if (rangeHeader == null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
                    .body(videoResource);
        }

        HttpRange range = HttpRange.parseRanges(rangeHeader).get(0);
        long start = range.getRangeStart(fileSize);
        long end = range.getRangeEnd(fileSize);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize)
                .body(new ClassPathResource("video/" + fileName) {
                    @Override
                    public long contentLength() {
                        return end - start + 1;
                    }
                });
    }
}

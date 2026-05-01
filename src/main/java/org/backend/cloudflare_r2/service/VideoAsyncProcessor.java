package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
import org.backend.cloudflare_r2.entity.Video;
import org.backend.cloudflare_r2.repo.VideoRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VideoAsyncProcessor {

    private final VideoProcessingService processingService;
    private final R2UploadService r2UploadService;
    private final VideoRepository repository;

    @Async
    public void processVideo(Long videoId, File inputFile, String title, String publicUrl) {

        try {
            long timestamp = System.currentTimeMillis();
            File tempDir = new File("/tmp/videos/" + timestamp);
            tempDir.mkdirs();

            // 1. HLS
            File hlsDir = processingService.convertToHls(inputFile, tempDir + "/hls");

            // 2. Thumbnail
            File thumbnailFile = new File(tempDir, "thumbnail.jpg");
            processingService.generateThumbnail(inputFile, thumbnailFile);

            // 3. Upload to R2
            String keyPrefix = "videos/" + timestamp;

            r2UploadService.uploadFolder(hlsDir, keyPrefix);
            r2UploadService.uploadFile(thumbnailFile, keyPrefix + "/thumbnail.jpg");

            // 4. URLs
            String streamUrl = publicUrl + "/" + keyPrefix + "/index.m3u8";
            String thumbnailUrl = publicUrl + "/" + keyPrefix + "/thumbnail.jpg";

            // 5. Update DB
            Video video = repository.findById(videoId)
                    .orElseThrow();

            video.setStreamUrl(streamUrl);
            video.setThumbnailUrl(thumbnailUrl);
            video.setStatus("READY");

            repository.save(video);

        } catch (Exception e) {

            Video video = repository.findById(videoId).orElse(null);
            if (video != null) {
                video.setStatus("FAILED");
                repository.save(video);
            }

            throw new RuntimeException(e);
        }
    }
}
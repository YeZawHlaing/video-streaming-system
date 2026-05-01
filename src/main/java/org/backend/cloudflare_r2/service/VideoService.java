package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
import org.backend.cloudflare_r2.dto.VideoResponse;
import org.backend.cloudflare_r2.entity.Video;
import org.backend.cloudflare_r2.repo.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository repository;
    private final VideoProcessingService processingService;
    private final R2UploadService r2UploadService;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    public Video uploadAndProcess(MultipartFile file, String title) throws Exception {

        long timestamp = System.currentTimeMillis();

        // 1. Save temp file
        File tempDir = new File("/tmp/videos/" + timestamp);
        tempDir.mkdirs();

        File inputFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(inputFile);

        // 2. Convert to HLS
        File hlsDir = processingService.convertToHls(inputFile, tempDir + "/hls");

        // 3. Generate thumbnail
        File thumbnailFile = new File(tempDir, "thumbnail.jpg");
        processingService.generateThumbnail(inputFile, thumbnailFile);

        // 4. Upload HLS to R2
        String keyPrefix = "videos/" + timestamp;
        r2UploadService.uploadFolder(hlsDir, keyPrefix);

        // 5. Upload thumbnail to R2
        String thumbKey = keyPrefix + "/thumbnail.jpg";
        r2UploadService.uploadFile(thumbnailFile, thumbKey);

        // 6. Build URLs
        String streamUrl = publicUrl + "/" + keyPrefix + "/index.m3u8";
        String thumbnailUrl = publicUrl + "/" + thumbKey;

        // 7. Save DB
        Video video = new Video();
        video.setTitle(title);
        video.setStreamUrl(streamUrl);
        video.setThumbnailUrl(thumbnailUrl);
        video.setStatus("READY");
        video.setCreatedAt(LocalDateTime.now());

        return repository.save(video);
    }

    public List<Video> getAll() {
        return repository.findAll();
    }
}
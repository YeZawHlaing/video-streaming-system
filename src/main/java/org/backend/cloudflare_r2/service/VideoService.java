package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
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

        // 1. Save temp file
        File tempDir = new File("/tmp/videos/" + System.currentTimeMillis());
        tempDir.mkdirs();

        File inputFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(inputFile);

        // 2. Convert to HLS
        File hlsDir = processingService.convertToHls(inputFile, tempDir + "/hls");

        // 3. Upload to R2
        String keyPrefix = "videos/" + System.currentTimeMillis();
        r2UploadService.uploadFolder(hlsDir, keyPrefix);

        // 4. Build stream URL
        String streamUrl = publicUrl + "/" + keyPrefix + "/index.m3u8";

        // 5. Save DB
        Video video = new Video();
        video.setTitle(title);
        video.setStreamUrl(streamUrl);
        video.setStatus("READY");
        video.setCreatedAt(LocalDateTime.now());

        return repository.save(video);
    }

    public List<Video> getAll() {
        return repository.findAll();
    }
}

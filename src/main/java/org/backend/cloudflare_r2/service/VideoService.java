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
    private final VideoAsyncProcessor videoAsyncProcessor;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    public VideoResponse uploadAndProcess(MultipartFile file, String title) throws Exception {

        long timestamp = System.currentTimeMillis();

        File tempDir = new File("/tmp/videos/" + timestamp);
        tempDir.mkdirs();

        File inputFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(inputFile);

        // 1. Save DB FIRST
        Video video = new Video();
        video.setTitle(title);
        video.setStatus("PROCESSING");
        video.setCreatedAt(LocalDateTime.now());

        Video saved = repository.save(video);

        // 2. Trigger async processing (DO NOT WAIT)
        videoAsyncProcessor.processVideo(
                saved.getId(),
                inputFile,
                title,
                publicUrl
        );

        // 3. Return immediately
        VideoResponse response = new VideoResponse();
        response.setId(saved.getId());
        response.setTitle(title);
//        response.setStatus("PROCESSING");

        return response;
    }

    public List<Video> getAll() {
        return repository.findAll();
    }
}
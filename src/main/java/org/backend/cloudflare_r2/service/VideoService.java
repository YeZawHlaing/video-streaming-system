package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
import org.backend.cloudflare_r2.dto.VideoResponse;
import org.backend.cloudflare_r2.entity.Video;
import org.backend.cloudflare_r2.repo.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository repository;
    private final VideoAsyncProcessor videoAsyncProcessor;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    // ===============================
    // UPLOAD VIDEO (ASYNC + CLEAR CACHE)
    // ===============================
    @CacheEvict(value = "videos", allEntries = true)
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

        // 2. Trigger async processing
        videoAsyncProcessor.processVideo(
                saved.getId(),
                inputFile,
                title,
                publicUrl
        );

        // 3. Return response immediately
        return mapToResponse(saved);
    }

    // ===============================
    // GET VIDEOS (CACHED + PAGINATION)
    // ===============================
    @Cacheable(value = "videos", key = "#page + '-' + #size")
    public Page<VideoResponse> getVideos(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Video> videoPage = repository.findAll(pageable);

        return videoPage.map(this::mapToResponse);
    }

    // ===============================
    // MAPPER (ENTITY → DTO)
    // ===============================
    private VideoResponse mapToResponse(Video video) {

        VideoResponse res = new VideoResponse();
        res.setId(video.getId());
        res.setTitle(video.getTitle());
        res.setThumbnailUrl(video.getThumbnailUrl());
//        res.setStreamUrl(video.getStreamUrl());
//        res.setStatus(video.getStatus());
//        res.setCreatedAt(video.getCreatedAt());

        return res;
    }
}
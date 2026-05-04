package org.backend.cloudflare_r2.controller;

import lombok.RequiredArgsConstructor;
import org.backend.cloudflare_r2.dto.UploadUrlRequest;
import org.backend.cloudflare_r2.dto.UploadUrlResponse;
import org.backend.cloudflare_r2.dto.VideoResponse;
import org.backend.cloudflare_r2.entity.Video;
import org.backend.cloudflare_r2.repo.VideoRepository;
import org.backend.cloudflare_r2.service.R2Service;
import org.backend.cloudflare_r2.service.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final R2Service r2Service;
    private final VideoService videoService;
    private final VideoRepository repository;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    private String buildVideoUrl(String key) {
        return publicUrl + "/" + key.replace(" ", "%20");
    }

    @PostMapping("/upload-url")
    public UploadUrlResponse uploadUrl(
            @RequestBody UploadUrlRequest request
    ) {
        return r2Service.generateUploadUrl(request.getFileName());
    }

    // ===============================
    // 2. Direct Upload (your main flow)
    // ===============================
    @PostMapping("/upload")
    public VideoResponse upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title
    ) throws Exception {
        return videoService.uploadAndProcess(file, title);
    }
//
//    @GetMapping("/getAll")
//    public List<Video> getAll() {
//        return videoService.getAll();
//    }

    @GetMapping
    public Page<VideoResponse> getVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return videoService.getVideos(page, size);
    }

    @DeleteMapping("/{id}")
    public String deleteVideo(@PathVariable Long id) {

        videoService.deleteVideoById(id);

        return "Video deleted successfully with id: " + id;
    }
}
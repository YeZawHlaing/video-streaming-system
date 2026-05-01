package org.backend.cloudflare_r2.dto;

import lombok.Data;

@Data
public class VideoRequest {
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
}
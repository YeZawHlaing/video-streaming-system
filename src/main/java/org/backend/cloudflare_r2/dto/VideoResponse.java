package org.backend.cloudflare_r2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoResponse {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
}
package org.backend.cloudflare_r2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String originalUrl;   // MP4 (optional)
    private String streamUrl;     // HLS (.m3u8)

    private String status; // UPLOADED, PROCESSING, READY

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    private LocalDateTime createdAt;
}
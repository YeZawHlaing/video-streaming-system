package org.backend.cloudflare_r2.repo;

import org.backend.cloudflare_r2.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
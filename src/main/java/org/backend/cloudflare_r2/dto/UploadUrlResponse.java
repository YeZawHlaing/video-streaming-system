package org.backend.cloudflare_r2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadUrlResponse {
    private String uploadUrl;
    private String key;
}
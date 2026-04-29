package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.File;


@Service
@RequiredArgsConstructor
public class R2UploadService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public void uploadFolder(File folder, String keyPrefix) {

        File[] files = folder.listFiles();

        for (File file : files) {

            String key = keyPrefix + "/" + file.getName();

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(getContentType(file.getName()))
                            .build(),
                    file.toPath()
            );
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".m3u8")) return "application/vnd.apple.mpegurl";
        if (fileName.endsWith(".ts")) return "video/mp2t";
        return "application/octet-stream";
    }
}

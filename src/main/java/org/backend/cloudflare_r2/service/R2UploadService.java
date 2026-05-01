package org.backend.cloudflare_r2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class R2UploadService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public void uploadFolder(File folder, String keyPrefix) {

        if (folder == null || !folder.exists()) {
            throw new RuntimeException("Folder does not exist: " + folder);
        }

        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            throw new RuntimeException("No files found in folder: " + folder.getAbsolutePath());
        }

        for (File file : files) {
            if (!file.isFile()) continue;

            String key = keyPrefix + "/" + file.getName();

            try {
                log.info("Uploading file to R2: {}", key);

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .contentType(getContentType(file.getName()))
                                .build(),
                        file.toPath()
                );

                log.info("Upload success: {}", key);

            } catch (Exception e) {
                log.error("Failed to upload file: {}", key, e);
                throw new RuntimeException("R2 upload failed for " + key, e);
            }
        }
    }

    public void uploadFile(File file, String key) {

        if (file == null || !file.exists()) {
            throw new RuntimeException("File not found: " + file);
        }

        try {
            log.info("Uploading single file: {}", key);

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(getContentType(file.getName()))
                            .build(),
                    file.toPath()
            );

        } catch (Exception e) {
            log.error("Failed to upload file: {}", key, e);
            throw new RuntimeException("R2 upload failed for " + key, e);
        }
    }

    private String getContentType(String fileName) {

        if (fileName.endsWith(".m3u8"))
            return "application/vnd.apple.mpegurl";

        if (fileName.endsWith(".ts"))
            return "video/mp2t";

        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            return "image/jpeg";

        if (fileName.endsWith(".png"))
            return "image/png";

        return "application/octet-stream";
    }
}
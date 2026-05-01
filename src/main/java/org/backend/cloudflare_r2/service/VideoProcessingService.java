package org.backend.cloudflare_r2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VideoProcessingService {

    @Value("${app.video.temp-path:/tmp/videos}")
    private String tempPath;

    public File convertToHls(File inputFile, String outputDir) throws Exception {

        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-codec:v", "libx264",
                "-codec:a", "aac",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls",
                outputDir + "/index.m3u8"
        );

        pb.inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg HLS conversion failed");
        }

        return outDir;
    }

    // ✅ ADD THIS (for thumbnail feature)
    public File generateThumbnail(File inputFile, File outputFile) throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-ss", "00:00:02",
                "-vframes", "1",
                outputFile.getAbsolutePath()
        );

        pb.inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Thumbnail generation failed");
        }

        return outputFile;
    }
}
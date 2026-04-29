package org.backend.cloudflare_r2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;


@Service
public class VideoProcessingService {

    @Value("${app.video.temp-path}")
    private String tempPath;

    public File convertToHls(File inputFile, String outputDir) throws Exception {

        new File(outputDir).mkdirs();

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-codec:", "copy",
                "-start_number", "0",
                "-hls_time", "10",
                "-hls_list_size", "0",
                "-f", "hls",
                outputDir + "/index.m3u8"
        );

        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();

        return new File(outputDir);
    }
}
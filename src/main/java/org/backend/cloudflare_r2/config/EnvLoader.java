//package org.backend.cloudflare_r2.config;
//
//import io.github.cdimascio.dotenv.Dotenv;
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EnvLoader {
//
//    @PostConstruct
//    public void init() {
//
//        Dotenv dotenv = Dotenv.configure()
//                .ignoreIfMissing()
//                .load();
//
//        System.setProperty("CLOUDFLARE_R2_ACCESS_KEY",
//                dotenv.get("CLOUDFLARE_R2_ACCESS_KEY"));
//
//        System.setProperty("CLOUDFLARE_R2_SECRET_KEY",
//                dotenv.get("CLOUDFLARE_R2_SECRET_KEY"));
//
//        System.setProperty("CLOUDFLARE_R2_ENDPOINT",
//                dotenv.get("CLOUDFLARE_R2_ENDPOINT"));
//
//        System.setProperty("CLOUDFLARE_R2_BUCKET",
//                dotenv.get("CLOUDFLARE_R2_BUCKET"));
//
//        System.setProperty("CLOUDFLARE_R2_PUBLIC_URL",
//                dotenv.get("CLOUDFLARE_R2_PUBLIC_URL"));
//    }
//}
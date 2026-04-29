# 📺 Video Streaming Backend API (Mini YouTube System)

A scalable **video streaming backend system** built with **Spring Boot 4 + Java 21**, supporting **HLS streaming, FFmpeg processing, MySQL, and Cloudflare R2 storage**.  
This project is designed as a **YouTube-like video backend architecture**.

---

# 🚀 Tech Stack

- ☕ Java 21
- 🌱 Spring Boot 4
- 🐬 MySQL 8
- ☁️ :contentReference[oaicite:0]{index=0}
- 🎬 FFmpeg (Video Processing)
- 📡 HLS Streaming (.m3u8)
- 🐳 Docker
- ☁️ AWS EC2 (Deployment)

---

# 🏗️ System Architecture

```text
Frontend (React)
      ↓
Spring Boot API
      ↓
Upload MP4 Video
      ↓
FFmpeg converts to HLS format
      ↓
Upload .m3u8 + .ts files to Cloudflare R2
      ↓
Store metadata in MySQL
      ↓
Return streaming URL (.m3u8)
```
---

# 📁 Project Structure


src/main/java/org/backend/cloudflare_r2/
│
├── controller/     → REST APIs
├── service/        → Business logic (FFmpeg, R2 upload)
├── repository/     → MySQL access
├── entity/         → Database models
├── dto/            → Request/Response models
├── config/         → R2 + environment config
└── CloudflareR2Application.java

---

#  ⚙️️ Enviroment variables

CLOUDFLARE_R2_ACCESS_KEY=your_access_key
CLOUDFLARE_R2_SECRET_KEY=your_secret_key
CLOUDFLARE_R2_ENDPOINT=https://<account-id>.r2.cloudflarestorage.com
CLOUDFLARE_R2_BUCKET=video_storage
CLOUDFLARE_R2_PUBLIC_URL=https://pub-xxxx.r2.dev

DB_URL=jdbc:mysql://localhost:3306/video_db
DB_USERNAME=root
DB_PASSWORD=root

---

# 🧾 application.properties
spring:
datasource:
url: ${DB_URL}
username: ${DB_USERNAME}
password: ${DB_PASSWORD}

cloudflare:
r2:
access-key: ${CLOUDFLARE_R2_ACCESS_KEY}
secret-key: ${CLOUDFLARE_R2_SECRET_KEY}
endpoint: ${CLOUDFLARE_R2_ENDPOINT}
bucket: ${CLOUDFLARE_R2_BUCKET}
public-url: ${CLOUDFLARE_R2_PUBLIC_URL}

---

# 📡 API Endpoints
POST /videos/upload
---

# 📥 Get All Videos

GET /videos

---

# 📦 Response Example
```text
[
{
"id": 1,
"title": "My Video",
"originalUrl": null,
"streamUrl": "https://pub-xxx.r2.dev/videos/123/index.m3u8",
"status": "READY",
"createdAt": "2026-04-29T23:50:15"
}
]
```
---


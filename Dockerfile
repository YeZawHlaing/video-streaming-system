FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests



#  STAGE 2: Runtime stage
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Install FFmpeg (VERY IMPORTANT for your video system)
RUN apk add --no-cache ffmpeg

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8000

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
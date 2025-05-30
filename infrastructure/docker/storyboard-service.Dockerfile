# Multi-stage build for Storyboard Service
# Stage 1: Build stage
FROM gradle:8.14.0-jdk21-alpine AS build
# Set working directory
WORKDIR /app

# Copy gradle files first (for better caching)
COPY backend/storyboard-service/build.gradle .
COPY backend/storyboard-service/settings.gradle .
COPY backend/commons/ /app/commons/

# Copy source code
COPY backend/storyboard-service/src ./src

# Build the application
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port (Storyboard Service runs on 8087)
EXPOSE 8087

# JVM optimizations for containerized environment
# Extra memory for MongoDB operations and canvas processing
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms512m"

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

# Multi-stage build for API Gateway Service
# Stage 1: Build stage
FROM gradle:8.6-jdk21-alpine AS build
# Set working directory
WORKDIR /app

# Copy gradle files first (for better caching)
COPY backend/api-gateway/build.gradle .
COPY backend/api-gateway/settings.gradle .
COPY backend/commons/ /app/commons/

# Copy source code
COPY backend/api-gateway/src ./src

# Build the application (skip tests for faster builds in CI)
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port (API Gateway runs on 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

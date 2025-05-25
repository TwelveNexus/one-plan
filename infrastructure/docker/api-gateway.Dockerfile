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

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create application user for security
RUN addgroup --system appgroup && adduser --system --group appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to app user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the port (API Gateway runs on 8080)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD SERVICE_NAME=api-gateway SERVER_PORT=8080 /usr/local/bin/healthcheck.sh

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

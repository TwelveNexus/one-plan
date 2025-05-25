# Multi-stage build for Notification Service
# Stage 1: Build stage
FROM gradle:8.6-jdk21-alpine AS build

# Set working directory
WORKDIR /app

# Copy gradle files first (for better caching)
COPY backend/notification-service/build.gradle .
COPY backend/notification-service/settings.gradle .
COPY backend/commons/ /app/commons/

# Copy source code
COPY backend/notification-service/src ./src

# Build the application
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime stage
FROM openjdk:21-jre-slim

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

# Expose the port (Notification Service runs on 8089)
EXPOSE 8089

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8089/actuator/health || exit 1

# JVM optimizations for containerized environment
# Optimized for message processing and email delivery
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms512m"

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

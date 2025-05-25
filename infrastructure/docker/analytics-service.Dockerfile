# Multi-stage build for Analytics Service
# Stage 1: Build stage
FROM gradle:8.14.0-jdk21-alpine AS build

# Set working directory
WORKDIR /app

# Copy gradle files first (for better caching)
COPY backend/analytics-service/build.gradle .
COPY backend/analytics-service/settings.gradle .
COPY backend/commons/ /app/commons/

# Copy source code
COPY backend/analytics-service/src ./src

# Build the application
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

# Expose the port (Analytics Service runs on 8090)
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8090/actuator/health || exit 1

# JVM optimizations for containerized environment
# Higher memory allocation for analytics processing and caching
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0 -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -Xms1g -XX:+UseLargePages"

# Run the application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

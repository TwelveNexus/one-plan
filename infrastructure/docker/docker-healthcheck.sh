#!/bin/bash

# docker-healthcheck.sh
# Comprehensive health check script for One Plan microservices
# This script can be used across all services with different parameters

set -e

# Default values
SERVICE_NAME=${SERVICE_NAME:-"unknown"}
HEALTH_ENDPOINT=${HEALTH_ENDPOINT:-"/actuator/health"}
PORT=${SERVER_PORT:-8080}
TIMEOUT=${HEALTH_TIMEOUT:-10}
MAX_RETRIES=${HEALTH_MAX_RETRIES:-3}
RETRY_INTERVAL=${HEALTH_RETRY_INTERVAL:-2}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH-CHECK] [${SERVICE_NAME}]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH-CHECK] [${SERVICE_NAME}] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH-CHECK] [${SERVICE_NAME}] ERROR:${NC} $1"
}

# Function to check if service is responding
check_http_health() {
    local url="http://localhost:${PORT}${HEALTH_ENDPOINT}"
    local response
    local status_code

    log "Checking health at: ${url}"

    # Use curl to check the health endpoint
    response=$(curl -s -w "%{http_code}" --max-time ${TIMEOUT} "${url}" || echo "000")
    status_code="${response: -3}"

    if [ "$status_code" = "200" ]; then
        log "Health check passed (HTTP ${status_code})"
        return 0
    else
        error "Health check failed (HTTP ${status_code})"
        return 1
    fi
}

# Function to check database connectivity (for services that use databases)
check_database_connectivity() {
    if [ -n "$SPRING_DATASOURCE_URL" ]; then
        log "Checking database connectivity..."

        # Extract database info from JDBC URL
        if [[ $SPRING_DATASOURCE_URL =~ mariadb://([^:]+):([0-9]+)/(.+) ]]; then
            local db_host="${BASH_REMATCH[1]}"
            local db_port="${BASH_REMATCH[2]}"
            local db_name="${BASH_REMATCH[3]}"

            # Check if database port is accessible
            if timeout 5 bash -c "</dev/tcp/${db_host}/${db_port}"; then
                log "Database connectivity check passed (${db_host}:${db_port})"
                return 0
            else
                warn "Database connectivity check failed (${db_host}:${db_port})"
                return 1
            fi
        fi
    fi
    return 0
}

# Function to check MongoDB connectivity (for services that use MongoDB)
check_mongodb_connectivity() {
    if [ -n "$SPRING_DATA_MONGODB_URI" ]; then
        log "Checking MongoDB connectivity..."

        # Extract MongoDB info from URI
        if [[ $SPRING_DATA_MONGODB_URI =~ mongodb://([^:]+):([0-9]+)/(.+) ]]; then
            local mongo_host="${BASH_REMATCH[1]}"
            local mongo_port="${BASH_REMATCH[2]}"

            # Check if MongoDB port is accessible
            if timeout 5 bash -c "</dev/tcp/${mongo_host}/${mongo_port}"; then
                log "MongoDB connectivity check passed (${mongo_host}:${mongo_port})"
                return 0
            else
                warn "MongoDB connectivity check failed (${mongo_host}:${mongo_port})"
                return 1
            fi
        fi
    fi
    return 0
}

# Function to check Redis connectivity (for services that use Redis)
check_redis_connectivity() {
    if [ -n "$SPRING_REDIS_HOST" ]; then
        log "Checking Redis connectivity..."

        local redis_host="$SPRING_REDIS_HOST"
        local redis_port="${SPRING_REDIS_PORT:-6379}"

        # Check if Redis port is accessible
        if timeout 5 bash -c "</dev/tcp/${redis_host}/${redis_port}"; then
            log "Redis connectivity check passed (${redis_host}:${redis_port})"
            return 0
        else
            warn "Redis connectivity check failed (${redis_host}:${redis_port})"
            return 1
        fi
    fi
    return 0
}

# Function to check memory usage
check_memory_usage() {
    local memory_threshold=${MEMORY_THRESHOLD:-85}

    if command -v free >/dev/null 2>&1; then
        local memory_usage=$(free | grep Mem | awk '{printf("%.0f", $3/$2 * 100.0)}')

        if [ "$memory_usage" -gt "$memory_threshold" ]; then
            warn "High memory usage detected: ${memory_usage}% (threshold: ${memory_threshold}%)"
            return 1
        else
            log "Memory usage check passed: ${memory_usage}%"
            return 0
        fi
    fi
    return 0
}

# Function to check disk space
check_disk_space() {
    local disk_threshold=${DISK_THRESHOLD:-90}

    if command -v df >/dev/null 2>&1; then
        local disk_usage=$(df / | tail -1 | awk '{print $5}' | sed 's/%//g')

        if [ "$disk_usage" -gt "$disk_threshold" ]; then
            warn "High disk usage detected: ${disk_usage}% (threshold: ${disk_threshold}%)"
            return 1
        else
            log "Disk usage check passed: ${disk_usage}%"
            return 0
        fi
    fi
    return 0
}

# Main health check function
perform_health_check() {
    local retry_count=0
    local all_checks_passed=false

    log "Starting comprehensive health check for ${SERVICE_NAME}"

    while [ $retry_count -lt $MAX_RETRIES ]; do
        local checks_failed=0

        # HTTP Health Check (Primary)
        if ! check_http_health; then
            ((checks_failed++))
        fi

        # Database connectivity checks (Secondary)
        if ! check_database_connectivity; then
            ((checks_failed++))
        fi

        if ! check_mongodb_connectivity; then
            ((checks_failed++))
        fi

        if ! check_redis_connectivity; then
            ((checks_failed++))
        fi

        # System resource checks (Advisory)
        check_memory_usage || warn "Memory usage is high but not failing health check"
        check_disk_space || warn "Disk usage is high but not failing health check"

        # If primary checks pass, we're healthy
        if [ $checks_failed -eq 0 ]; then
            log "All health checks passed successfully"
            all_checks_passed=true
            break
        fi

        ((retry_count++))
        if [ $retry_count -lt $MAX_RETRIES ]; then
            warn "Health check attempt ${retry_count}/${MAX_RETRIES} failed, retrying in ${RETRY_INTERVAL}s..."
            sleep $RETRY_INTERVAL
        fi
    done

    if [ "$all_checks_passed" = true ]; then
        log "Service ${SERVICE_NAME} is healthy"
        exit 0
    else
        error "Service ${SERVICE_NAME} failed health check after ${MAX_RETRIES} attempts"
        exit 1
    fi
}

# Help function
show_help() {
    cat << EOF
Docker Health Check Script for One Plan Microservices

Usage: $0 [OPTIONS]

Environment Variables:
    SERVICE_NAME              Name of the service (for logging)
    SERVER_PORT               Port the service is running on (default: 8080)
    HEALTH_ENDPOINT           Health check endpoint (default: /actuator/health)
    HEALTH_TIMEOUT            HTTP timeout in seconds (default: 10)
    HEALTH_MAX_RETRIES        Maximum retry attempts (default: 3)
    HEALTH_RETRY_INTERVAL     Interval between retries in seconds (default: 2)
    MEMORY_THRESHOLD          Memory usage threshold % (default: 85)
    DISK_THRESHOLD            Disk usage threshold % (default: 90)

Database Environment Variables (optional):
    SPRING_DATASOURCE_URL     MariaDB JDBC URL
    SPRING_DATA_MONGODB_URI   MongoDB connection URI
    SPRING_REDIS_HOST         Redis host
    SPRING_REDIS_PORT         Redis port

Examples:
    # Basic usage
    ./docker-healthcheck.sh

    # With custom service name and port
    SERVICE_NAME=api-gateway SERVER_PORT=8080 ./docker-healthcheck.sh

    # With all environment variables
    SERVICE_NAME=identity-service SERVER_PORT=8081 \\
    SPRING_DATASOURCE_URL=jdbc:mariadb://mariadb:3306/oneplan_identity \\
    SPRING_REDIS_HOST=redis ./docker-healthcheck.sh

EOF
}

# Parse command line arguments
case "${1:-}" in
    -h|--help)
        show_help
        exit 0
        ;;
    "")
        perform_health_check
        ;;
    *)
        error "Unknown argument: $1"
        show_help
        exit 1
        ;;
esac

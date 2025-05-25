#!/bin/bash

# build.sh - Build script for One Plan microservices
# This script builds Docker images for all services or specific services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../../.." && pwd)"
DOCKER_DIR="$ROOT_DIR/infrastructure/docker"

# Default values
BUILD_ALL=true
SERVICES=""
PUSH_IMAGES=false
DOCKER_REGISTRY=""
IMAGE_TAG="latest"
PARALLEL_BUILDS=false
CACHE_FROM=""

# Service list
ALL_SERVICES=(
    "api-gateway"
    "identity-service"
    "tenant-service"
    "organization-service"
    "project-service"
    "task-service"
    "requirement-service"
    "storyboard-service"
    "integration-service"
    "notification-service"
    "analytics-service"
    "subscription-service"
)

# Logging functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [BUILD]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [BUILD] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [BUILD] ERROR:${NC} $1"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [BUILD] INFO:${NC} $1"
}

# Help function
show_help() {
    cat << EOF
One Plan Build Script

Usage: $0 [OPTIONS]

Options:
    -s, --services SERVICES     Comma-separated list of services to build
    -a, --all                   Build all services (default)
    -p, --push                  Push images to registry after building
    -r, --registry REGISTRY     Docker registry to push to
    -t, --tag TAG              Image tag (default: latest)
    -j, --parallel             Build services in parallel
    -c, --cache-from IMAGE     Use cache from specific image
    --no-cache                 Build without cache
    -h, --help                 Show this help message

Examples:
    # Build all services
    $0 --all

    # Build specific services
    $0 --services api-gateway,identity-service

    # Build and push to registry
    $0 --all --push --registry myregistry.com --tag v1.0.0

    # Build in parallel (faster but uses more resources)
    $0 --all --parallel

Environment Variables:
    DOCKER_REGISTRY            Default registry for pushing images
    DOCKER_TAG                 Default tag for images
    BUILD_PARALLEL             Build in parallel if set to 'true'
    NO_CACHE                   Skip cache if set to 'true'

EOF
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -s|--services)
                SERVICES="$2"
                BUILD_ALL=false
                shift 2
                ;;
            -a|--all)
                BUILD_ALL=true
                shift
                ;;
            -p|--push)
                PUSH_IMAGES=true
                shift
                ;;
            -r|--registry)
                DOCKER_REGISTRY="$2"
                shift 2
                ;;
            -t|--tag)
                IMAGE_TAG="$2"
                shift 2
                ;;
            -j|--parallel)
                PARALLEL_BUILDS=true
                shift
                ;;
            -c|--cache-from)
                CACHE_FROM="$2"
                shift 2
                ;;
            --no-cache)
                export NO_CACHE=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                error "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done

    # Use environment variables as defaults
    DOCKER_REGISTRY=${DOCKER_REGISTRY:-$DOCKER_REGISTRY}
    IMAGE_TAG=${IMAGE_TAG:-${DOCKER_TAG:-latest}}
    PARALLEL_BUILDS=${PARALLEL_BUILDS:-${BUILD_PARALLEL:-false}}
}

# Validate service name
validate_service() {
    local service=$1
    for valid_service in "${ALL_SERVICES[@]}"; do
        if [[ "$service" == "$valid_service" ]]; then
            return 0
        fi
    done
    return 1
}

# Build a single service
build_service() {
    local service=$1
    local dockerfile="$DOCKER_DIR/${service}.Dockerfile"
    local image_name

    if [[ -n "$DOCKER_REGISTRY" ]]; then
        image_name="$DOCKER_REGISTRY/oneplan-$service:$IMAGE_TAG"
    else
        image_name="oneplan-$service:$IMAGE_TAG"
    fi

    log "Building $service..."

    # Check if Dockerfile exists
    if [[ ! -f "$dockerfile" ]]; then
        error "Dockerfile not found: $dockerfile"
        return 1
    fi

    # Build Docker image
    local build_args=(
        "build"
        "--file" "$dockerfile"
        "--tag" "$image_name"
    )

    # Add cache options
    if [[ "$NO_CACHE" == "true" ]]; then
        build_args+=("--no-cache")
    elif [[ -n "$CACHE_FROM" ]]; then
        build_args+=("--cache-from" "$CACHE_FROM")
    fi

    # Add build context (root directory)
    build_args+=("$ROOT_DIR")

    # Execute docker build
    if docker "${build_args[@]}"; then
        log "✅ Successfully built $service"

        # Push image if requested
        if [[ "$PUSH_IMAGES" == "true" ]]; then
            push_service "$service" "$image_name"
        fi

        return 0
    else
        error "❌ Failed to build $service"
        return 1
    fi
}

# Push a service image
push_service() {
    local service=$1
    local image_name=$2

    if [[ -z "$DOCKER_REGISTRY" ]]; then
        warn "No registry specified, skipping push for $service"
        return 0
    fi

    log "Pushing $service to registry..."

    if docker push "$image_name"; then
        log "✅ Successfully pushed $service"
        return 0
    else
        error "❌ Failed to push $service"
        return 1
    fi
}

# Build services in parallel
build_parallel() {
    local services=("$@")
    local pids=()
    local failed_services=()

    info "Building ${#services[@]} services in parallel..."

    # Start builds in background
    for service in "${services[@]}"; do
        build_service "$service" &
        pids+=($!)
    done

    # Wait for all builds to complete
    for i in "${!pids[@]}"; do
        local pid=${pids[i]}
        local service=${services[i]}

        if wait $pid; then
            log "✅ $service completed successfully"
        else
            error "❌ $service failed"
            failed_services+=("$service")
        fi
    done

    # Report results
    if [[ ${#failed_services[@]} -eq 0 ]]; then
        log "🎉 All services built successfully!"
        return 0
    else
        error "❌ Failed services: ${failed_services[*]}"
        return 1
    fi
}

# Build services sequentially
build_sequential() {
    local services=("$@")
    local failed_services=()

    info "Building ${#services[@]} services sequentially..."

    for service in "${services[@]}"; do
        if build_service "$service"; then
            log "✅ $service completed successfully"
        else
            error "❌ $service failed"
            failed_services+=("$service")
        fi
    done

    # Report results
    if [[ ${#failed_services[@]} -eq 0 ]]; then
        log "🎉 All services built successfully!"
        return 0
    else
        error "❌ Failed services: ${failed_services[*]}"
        return 1
    fi
}

# Pre-build checks
pre_build_checks() {
    log "Running pre-build checks..."

    # Check if Docker is available
    if ! command -v docker &> /dev/null; then
        error "Docker is not installed or not in PATH"
        exit 1
    fi

    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        error "Docker daemon is not running"
        exit 1
    fi

    # Check if we're in the right directory
    if [[ ! -f "$ROOT_DIR/backend/api-gateway/build.gradle" ]]; then
        error "Not in the correct project directory"
        exit 1
    fi

    # Check if Dockerfiles exist
    local missing_dockerfiles=()
    for service in "${ALL_SERVICES[@]}"; do
        if [[ ! -f "$DOCKER_DIR/${service}.Dockerfile" ]]; then
            missing_dockerfiles+=("${service}.Dockerfile")
        fi
    done

    if [[ ${#missing_dockerfiles[@]} -gt 0 ]]; then
        error "Missing Dockerfiles: ${missing_dockerfiles[*]}"
        exit 1
    fi

    log "✅ Pre-build checks passed"
}

# Main function
main() {
    log "Starting One Plan build process..."

    # Parse arguments
    parse_args "$@"

    # Pre-build checks
    pre_build_checks

    # Determine which services to build
    local services_to_build=()

    if [[ "$BUILD_ALL" == "true" ]]; then
        services_to_build=("${ALL_SERVICES[@]}")
        log "Building all ${#ALL_SERVICES[@]} services"
    else
        IFS=',' read -ra service_array <<< "$SERVICES"
        for service in "${service_array[@]}"; do
            service=$(echo "$service" | xargs)  # Trim whitespace
            if validate_service "$service"; then
                services_to_build+=("$service")
            else
                error "Invalid service: $service"
                exit 1
            fi
        done
        log "Building ${#services_to_build[@]} services: ${services_to_build[*]}"
    fi

    # Display build configuration
    info "Build Configuration:"
    info "  - Services: ${services_to_build[*]}"
    info "  - Registry: ${DOCKER_REGISTRY:-'local'}"
    info "  - Tag: $IMAGE_TAG"
    info "  - Push: $PUSH_IMAGES"
    info "  - Parallel: $PARALLEL_BUILDS"
    info "  - No Cache: ${NO_CACHE:-false}"

    # Start build process
    local start_time=$(date +%s)

    if [[ "$PARALLEL_BUILDS" == "true" ]]; then
        build_parallel "${services_to_build[@]}"
    else
        build_sequential "${services_to_build[@]}"
    fi

    local exit_code=$?
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))

    # Summary
    log "Build completed in ${duration} seconds"

    if [[ $exit_code -eq 0 ]]; then
        log "🎉 Build successful!"
    else
        error "❌ Build failed!"
    fi

    exit $exit_code
}

# Run main function with all arguments
main "$@"

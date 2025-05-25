#!/bin/bash

# deploy.sh - Deployment script for One Plan to Coolify
# This script handles deployment of services to Coolify platform

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

# Default values
ENVIRONMENT="production"
DEPLOY_ALL=true
SERVICES=""
COOLIFY_URL=""
COOLIFY_TOKEN=""
HEALTH_CHECK_TIMEOUT=300
SKIP_HEALTH_CHECK=false
DRY_RUN=false

# Service list with their Coolify UUIDs (to be configured)
declare -A SERVICE_UUIDS=(
    ["api-gateway"]=""
    ["identity-service"]=""
    ["tenant-service"]=""
    ["organization-service"]=""
    ["project-service"]=""
    ["task-service"]=""
    ["requirement-service"]=""
    ["storyboard-service"]=""
    ["integration-service"]=""
    ["notification-service"]=""
    ["analytics-service"]=""
    ["subscription-service"]=""
)

# Service URLs for health checks (to be configured)
declare -A SERVICE_URLS=(
    ["api-gateway"]=""
    ["identity-service"]=""
    ["tenant-service"]=""
    ["organization-service"]=""
    ["project-service"]=""
    ["task-service"]=""
    ["requirement-service"]=""
    ["storyboard-service"]=""
    ["integration-service"]=""
    ["notification-service"]=""
    ["analytics-service"]=""
    ["subscription-service"]=""
)

# Logging functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [DEPLOY]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [DEPLOY] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [DEPLOY] ERROR:${NC} $1"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [DEPLOY] INFO:${NC} $1"
}

# Help function
show_help() {
    cat << EOF
One Plan Deployment Script

Usage: $0 [OPTIONS]

Options:
    -e, --environment ENV       Deployment environment (production, staging)
    -s, --services SERVICES     Comma-separated list of services to deploy
    -a, --all                   Deploy all services (default)
    -u, --url URL              Coolify URL
    -t, --token TOKEN          Coolify API token
    --timeout SECONDS          Health check timeout (default: 300)
    --skip-health-check        Skip health checks after deployment
    --dry-run                  Show what would be deployed without actually deploying
    -h, --help                 Show this help message

Examples:
    # Deploy all services to production
    $0 --all --environment production

    # Deploy specific services
    $0 --services api-gateway,identity-service --environment staging

    # Dry run to see what would be deployed
    $0 --all --dry-run

    # Deploy with custom Coolify settings
    $0 --all --url https://coolify.yourdomain.com --token your-token

Environment Variables:
    COOLIFY_URL                Coolify instance URL
    COOLIFY_TOKEN              Coolify API token
    COOLIFY_ENVIRONMENT        Deployment environment
    DEPLOY_TIMEOUT             Health check timeout in seconds

Required Secrets (in GitHub or environment):
    COOLIFY_*_SERVICE_UUID     UUID for each service in Coolify
    COOLIFY_*_SERVICE_URL      URL for each service health check

EOF
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -e|--environment)
                ENVIRONMENT="$2"
                shift 2
                ;;
            -s|--services)
                SERVICES="$2"
                DEPLOY_ALL=false
                shift 2
                ;;
            -a|--all)
                DEPLOY_ALL=true
                shift
                ;;
            -u|--url)
                COOLIFY_URL="$2"
                shift 2
                ;;
            -t|--token)
                COOLIFY_TOKEN="$2"
                shift 2
                ;;
            --timeout)
                HEALTH_CHECK_TIMEOUT="$2"
                shift 2
                ;;
            --skip-health-check)
                SKIP_HEALTH_CHECK=true
                shift
                ;;
            --dry-run)
                DRY_RUN=true
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
    COOLIFY_URL=${COOLIFY_URL:-$COOLIFY_URL}
    COOLIFY_TOKEN=${COOLIFY_TOKEN:-$COOLIFY_TOKEN}
    ENVIRONMENT=${ENVIRONMENT:-${COOLIFY_ENVIRONMENT:-production}}
    HEALTH_CHECK_TIMEOUT=${HEALTH_CHECK_TIMEOUT:-${DEPLOY_TIMEOUT:-300}}
}

# Load service UUIDs and URLs from environment
load_service_config() {
    log "Loading service configuration for $ENVIRONMENT environment..."

    local env_prefix=""
    case $ENVIRONMENT in
        production)
            env_prefix="COOLIFY_PRODUCTION"
            ;;
        staging)
            env_prefix="COOLIFY_STAGING"
            ;;
        *)
            env_prefix="COOLIFY"
            ;;
    esac

    # Load service UUIDs
    for service in "${!SERVICE_UUIDS[@]}"; do
        local uuid_var="${env_prefix}_$(echo $service | tr '[:lower:]' '[:upper:]' | tr '-' '_')_UUID"
        local url_var="${env_prefix}_$(echo $service | tr '[:lower:]' '[:upper:]' | tr '-' '_')_URL"

        SERVICE_UUIDS[$service]=${!uuid_var}
        SERVICE_URLS[$service]=${!url_var}

        if [[ -z "${SERVICE_UUIDS[$service]}" ]]; then
            warn "No UUID found for $service (${uuid_var})"
        fi

        if [[ -z "${SERVICE_URLS[$service]}" ]]; then
            warn "No URL found for $service (${url_var})"
        fi
    done
}

# Validate configuration
validate_config() {
    log "Validating deployment configuration..."

    # Check required parameters
    if [[ -z "$COOLIFY_URL" ]]; then
        error "Coolify URL is required (set COOLIFY_URL or use --url)"
        exit 1
    fi

    if [[ -z "$COOLIFY_TOKEN" ]]; then
        error "Coolify token is required (set COOLIFY_TOKEN or use --token)"
        exit 1
    fi

    # Check if curl is available
    if ! command -v curl &> /dev/null; then
        error "curl is required but not installed"
        exit 1
    fi

    # Test Coolify API connectivity
    local api_test_url="$COOLIFY_URL/api/v1/security"
    if ! curl -f -s --max-time 10 -H "Authorization: Bearer $COOLIFY_TOKEN" "$api_test_url" > /dev/null; then
        error "Cannot connect to Coolify API at $COOLIFY_URL"
        error "Please check URL and token"
        exit 1
    fi

    log "✅ Configuration validation passed"
}

# Deploy a single service to Coolify
deploy_service() {
    local service=$1
    local uuid="${SERVICE_UUIDS[$service]}"

    if [[ -z "$uuid" ]]; then
        error "No UUID configured for $service"
        return 1
    fi

    log "Deploying $service (UUID: $uuid)..."

    if [[ "$DRY_RUN" == "true" ]]; then
        info "DRY RUN: Would deploy $service to Coolify"
        return 0
    fi

    # Trigger deployment via Coolify API
    local deploy_url="$COOLIFY_URL/api/v1/deploy"
    local deploy_data="{\"uuid\":\"$uuid\",\"force\":true}"

    local response
    local http_code

    response=$(curl -s -w "%{http_code}" \
        -X POST \
        -H "Authorization: Bearer $COOLIFY_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$deploy_data" \
        "$deploy_url" || echo "000")

    http_code="${response: -3}"
    response_body="${response%???}"

    if [[ "$http_code" =~ ^2[0-9][0-9]$ ]]; then
        log "✅ Successfully triggered deployment for $service"
        return 0
    else
        error "❌ Failed to deploy $service (HTTP $http_code)"
        error "Response: $response_body"
        return 1
    fi
}

# Wait for service to be healthy
wait_for_service_health() {
    local service=$1
    local service_url="${SERVICE_URLS[$service]}"

    if [[ -z "$service_url" ]]; then
        warn "No health check URL configured for $service, skipping health check"
        return 0
    fi

    log "Checking health of $service at $service_url..."

    local health_url="$service_url/actuator/health"
    local max_attempts=$((HEALTH_CHECK_TIMEOUT / 10))
    local attempt=1

    while [[ $attempt -le $max_attempts ]]; do
        if curl -f -s --max-time 10 "$health_url" > /dev/null 2>&1; then
            log "✅ $service is healthy"
            return 0
        else
            info "⏳ $service not ready yet (attempt $attempt/$max_attempts)"
            sleep 10
            ((attempt++))
        fi
    done

    error "❌ $service failed health check after $HEALTH_CHECK_TIMEOUT seconds"
    return 1
}

# Deploy multiple services
deploy_services() {
    local services=("$@")
    local failed_deployments=()
    local failed_health_checks=()

    info "Starting deployment of ${#services[@]} services..."

    # Phase 1: Trigger all deployments
    log "Phase 1: Triggering deployments..."
    for service in "${services[@]}"; do
        if deploy_service "$service"; then
            log "✅ $service deployment triggered"
        else
            error "❌ $service deployment failed"
            failed_deployments+=("$service")
        fi
    done

    # Check if any deployments failed
    if [[ ${#failed_deployments[@]} -gt 0 ]]; then
        error "❌ Failed to trigger deployments for: ${failed_deployments[*]}"
        return 1
    fi

    # Phase 2: Wait for deployments to complete and services to be healthy
    if [[ "$SKIP_HEALTH_CHECK" == "false" && "$DRY_RUN" == "false" ]]; then
        log "Phase 2: Waiting for services to be healthy..."

        # Wait a bit for deployments to start
        sleep 30

        for service in "${services[@]}"; do
            if wait_for_service_health "$service"; then
                log "✅ $service is healthy"
            else
                error "❌ $service health check failed"
                failed_health_checks+=("$service")
            fi
        done
    fi

    # Summary
    local total_services=${#services[@]}
    local successful_deployments=$((total_services - ${#failed_deployments[@]}))
    local successful_health_checks=$((total_services - ${#failed_health_checks[@]}))

    log "Deployment Summary:"
    log "  - Total services: $total_services"
    log "  - Successful deployments: $successful_deployments"

    if [[ "$SKIP_HEALTH_CHECK" == "false" && "$DRY_RUN" == "false" ]]; then
        log "  - Healthy services: $successful_health_checks"
    fi

    if [[ ${#failed_deployments[@]} -eq 0 && ${#failed_health_checks[@]} -eq 0 ]]; then
        log "🎉 All services deployed successfully!"
        return 0
    else
        error "❌ Some services failed to deploy or become healthy"
        [[ ${#failed_deployments[@]} -gt 0 ]] && error "Failed deployments: ${failed_deployments[*]}"
        [[ ${#failed_health_checks[@]} -gt 0 ]] && error "Failed health checks: ${failed_health_checks[*]}"
        return 1
    fi
}

# Rollback deployment
rollback_deployment() {
    local service=$1
    warn "Rollback functionality not yet implemented for $service"
    # TODO: Implement rollback logic using Coolify API
}

# Get deployment status
get_deployment_status() {
    local service=$1
    local uuid="${SERVICE_UUIDS[$service]}"

    if [[ -z "$uuid" ]]; then
        error "No UUID configured for $service"
        return 1
    fi

    local status_url="$COOLIFY_URL/api/v1/applications/$uuid"
    local response

    response=$(curl -s \
        -H "Authorization: Bearer $COOLIFY_TOKEN" \
        "$status_url" || echo "")

    if [[ -n "$response" ]]; then
        echo "$response" | jq -r '.status // "unknown"' 2>/dev/null || echo "unknown"
    else
        echo "error"
    fi
}

# Pre-deployment checks
pre_deployment_checks() {
    log "Running pre-deployment checks..."

    # Check if jq is available (optional but helpful)
    if ! command -v jq &> /dev/null; then
        warn "jq not found - JSON responses will not be formatted"
    fi

    # Validate environment
    if [[ "$ENVIRONMENT" != "production" && "$ENVIRONMENT" != "staging" ]]; then
        warn "Unknown environment: $ENVIRONMENT"
    fi

    log "✅ Pre-deployment checks completed"
}

# Main function
main() {
    log "Starting One Plan deployment to Coolify..."

    # Parse arguments
    parse_args "$@"

    # Load configuration
    load_service_config

    # Validate configuration
    validate_config

    # Pre-deployment checks
    pre_deployment_checks

    # Determine which services to deploy
    local services_to_deploy=()

    if [[ "$DEPLOY_ALL" == "true" ]]; then
        services_to_deploy=($(printf '%s\n' "${!SERVICE_UUIDS[@]}" | sort))
        log "Deploying all ${#services_to_deploy[@]} services"
    else
        IFS=',' read -ra service_array <<< "$SERVICES"
        for service in "${service_array[@]}"; do
            service=$(echo "$service" | xargs)  # Trim whitespace
            if [[ -n "${SERVICE_UUIDS[$service]}" ]]; then
                services_to_deploy+=("$service")
            else
                error "Invalid or unconfigured service: $service"
                exit 1
            fi
        done
        log "Deploying ${#services_to_deploy[@]} services: ${services_to_deploy[*]}"
    fi

    # Display deployment configuration
    info "Deployment Configuration:"
    info "  - Environment: $ENVIRONMENT"
    info "  - Services: ${services_to_deploy[*]}"
    info "  - Coolify URL: $COOLIFY_URL"
    info "  - Health Check: $([ "$SKIP_HEALTH_CHECK" == "true" ] && echo "Disabled" || echo "Enabled ($HEALTH_CHECK_TIMEOUT s)")"
    info "  - Dry Run: $DRY_RUN"

    # Confirm deployment in production
    if [[ "$ENVIRONMENT" == "production" && "$DRY_RUN" == "false" && -t 0 ]]; then
        read -p "Deploy to PRODUCTION environment? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log "Deployment cancelled by user"
            exit 0
        fi
    fi

    # Start deployment process
    local start_time=$(date +%s)

    deploy_services "${services_to_deploy[@]}"
    local exit_code=$?

    local end_time=$(date +%s)
    local duration=$((end_time - start_time))

    # Final summary
    log "Deployment completed in ${duration} seconds"

    if [[ $exit_code -eq 0 ]]; then
        log "🎉 Deployment successful!"
        info "All services are deployed and healthy in $ENVIRONMENT environment"
    else
        error "❌ Deployment failed!"
        info "Check the logs above for details"
    fi

    exit $exit_code
}

# Run main function with all arguments
main "$@"

#!/bin/bash

# health-check.sh - Health check script for One Plan deployment
# This script checks the health of deployed services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Default values
CHECK_ALL=true
SERVICES=""
BASE_URL=""
TIMEOUT=30
MAX_RETRIES=3
RETRY_INTERVAL=5
OUTPUT_FORMAT="table"
ENVIRONMENT="production"

# Service list with default ports
declare -A SERVICE_PORTS=(
    ["api-gateway"]="8080"
    ["identity-service"]="8081"
    ["tenant-service"]="8082"
    ["organization-service"]="8083"
    ["project-service"]="8084"
    ["task-service"]="8085"
    ["requirement-service"]="8086"
    ["storyboard-service"]="8087"
    ["integration-service"]="8088"
    ["notification-service"]="8089"
    ["analytics-service"]="8090"
    ["subscription-service"]="8091"
)

# Service URLs for different environments
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

# Health check results
declare -A HEALTH_RESULTS=()
declare -A RESPONSE_TIMES=()

# Logging functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH] ERROR:${NC} $1"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [HEALTH] INFO:${NC} $1"
}

# Help function
show_help() {
    cat << EOF
One Plan Health Check Script

Usage: $0 [OPTIONS]

Options:
    -s, --services SERVICES     Comma-separated list of services to check
    -a, --all                   Check all services (default)
    -u, --url URL              Base URL for services
    -e, --environment ENV       Environment (local, staging, production)
    -t, --timeout SECONDS      HTTP timeout per request (default: 30)
    -r, --retries COUNT        Max retries per service (default: 3)
    -i, --interval SECONDS     Retry interval (default: 5)
    -f, --format FORMAT        Output format: table, json, simple (default: table)
    --continuous               Run health checks continuously
    --alert-webhook URL        Webhook URL for alerts
    -h, --help                 Show this help message

Examples:
    # Check all services locally
    $0 --all --environment local

    # Check specific services in production
    $0 --services api-gateway,identity-service --environment production

    # Check with custom base URL
    $0 --all --url https://api.yourdomain.com

    # Continuous monitoring
    $0 --all --continuous --environment production

    # JSON output for scripting
    $0 --all --format json

Environment Variables:
    ONEPLAN_BASE_URL           Base URL for services
    HEALTH_CHECK_TIMEOUT       HTTP timeout
    HEALTH_CHECK_RETRIES       Max retries
    ALERT_WEBHOOK_URL          Webhook for alerts

EOF
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -s|--services)
                SERVICES="$2"
                CHECK_ALL=false
                shift 2
                ;;
            -a|--all)
                CHECK_ALL=true
                shift
                ;;
            -u|--url)
                BASE_URL="$2"
                shift 2
                ;;
            -e|--environment)
                ENVIRONMENT="$2"
                shift 2
                ;;
            -t|--timeout)
                TIMEOUT="$2"
                shift 2
                ;;
            -r|--retries)
                MAX_RETRIES="$2"
                shift 2
                ;;
            -i|--interval)
                RETRY_INTERVAL="$2"
                shift 2
                ;;
            -f|--format)
                OUTPUT_FORMAT="$2"
                shift 2
                ;;
            --continuous)
                CONTINUOUS=true
                shift
                ;;
            --alert-webhook)
                ALERT_WEBHOOK_URL="$2"
                shift 2
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
    BASE_URL=${BASE_URL:-$ONEPLAN_BASE_URL}
    TIMEOUT=${TIMEOUT:-${HEALTH_CHECK_TIMEOUT:-30}}
    MAX_RETRIES=${MAX_RETRIES:-${HEALTH_CHECK_RETRIES:-3}}
    ALERT_WEBHOOK_URL=${ALERT_WEBHOOK_URL:-$ALERT_WEBHOOK_URL}
}

# Load service URLs based on environment
load_service_urls() {
    log "Loading service URLs for $ENVIRONMENT environment..."

    case $ENVIRONMENT in
        local)
            BASE_URL=${BASE_URL:-"http://localhost"}
            for service in "${!SERVICE_PORTS[@]}"; do
                SERVICE_URLS[$service]="$BASE_URL:${SERVICE_PORTS[$service]}"
            done
            ;;
        staging)
            # Load staging URLs from environment variables
            for service in "${!SERVICE_PORTS[@]}"; do
                local url_var="STAGING_$(echo $service | tr '[:lower:]' '[:upper:]' | tr '-' '_')_URL"
                SERVICE_URLS[$service]=${!url_var:-""}
            done
            ;;
        production)
            # Load production URLs from environment variables
            for service in "${!SERVICE_PORTS[@]}"; do
                local url_var="PRODUCTION_$(echo $service | tr '[:lower:]' '[:upper:]' | tr '-' '_')_URL"
                SERVICE_URLS[$service]=${!url_var:-""}
            done
            ;;
        *)
            warn "Unknown environment: $ENVIRONMENT"
            ;;
    esac

    # Override with base URL if provided
    if [[ -n "$BASE_URL" && "$ENVIRONMENT" != "local" ]]; then
        for service in "${!SERVICE_PORTS[@]}"; do
            if [[ -z "${SERVICE_URLS[$service]}" ]]; then
                SERVICE_URLS[$service]="$BASE_URL"
            fi
        done
    fi
}

# Check health of a single service
check_service_health() {
    local service=$1
    local service_url="${SERVICE_URLS[$service]}"

    if [[ -z "$service_url" ]]; then
        HEALTH_RESULTS[$service]="NO_URL"
        RESPONSE_TIMES[$service]="N/A"
        return 1
    fi

    local health_url="$service_url/actuator/health"
    local attempt=1

    while [[ $attempt -le $MAX_RETRIES ]]; do
        local start_time=$(date +%s%3N)
        local response
        local http_code

        # Make the health check request
        response=$(curl -s -w "%{http_code}" --max-time $TIMEOUT "$health_url" 2>/dev/null || echo "000")
        http_code="${response: -3}"
        local end_time=$(date +%s%3N)
        local response_time=$((end_time - start_time))

        if [[ "$http_code" == "200" ]]; then
            HEALTH_RESULTS[$service]="HEALTHY"
            RESPONSE_TIMES[$service]="${response_time}ms"
            return 0
        else
            if [[ $attempt -eq $MAX_RETRIES ]]; then
                case $http_code in
                    000)
                        HEALTH_RESULTS[$service]="UNREACHABLE"
                        ;;
                    404)
                        HEALTH_RESULTS[$service]="NO_HEALTH_ENDPOINT"
                        ;;
                    5*)
                        HEALTH_RESULTS[$service]="SERVER_ERROR"
                        ;;
                    *)
                        HEALTH_RESULTS[$service]="UNHEALTHY"
                        ;;
                esac
                RESPONSE_TIMES[$service]="N/A"
                return 1
            else
                sleep $RETRY_INTERVAL
                ((attempt++))
            fi
        fi
    done
}

# Check health of multiple services
check_services_health() {
    local services=("$@")
    local total=${#services[@]}
    local current=0

    info "Checking health of ${total} services..."

    for service in "${services[@]}"; do
        ((current++))
        info "[$current/$total] Checking $service..."
        check_service_health "$service"
    done
}

# Output results in table format
output_table() {
    local services=("$@")

    echo
    printf "%-20s %-15s %-15s %-30s\n" "SERVICE" "STATUS" "RESPONSE TIME" "URL"
    printf "%-20s %-15s %-15s %-30s\n" "────────────────────" "──────────────" "──────────────" "──────────────────────────────"

    for service in "${services[@]}"; do
        local status="${HEALTH_RESULTS[$service]}"
        local response_time="${RESPONSE_TIMES[$service]}"
        local url="${SERVICE_URLS[$service]}"

        # Color code the status
        case $status in
            HEALTHY)
                status="${GREEN}✅ HEALTHY${NC}"
                ;;
            UNHEALTHY|SERVER_ERROR)
                status="${RED}❌ UNHEALTHY${NC}"
                ;;
            UNREACHABLE)
                status="${RED}🔌 UNREACHABLE${NC}"
                ;;
            NO_HEALTH_ENDPOINT)
                status="${YELLOW}⚠️ NO ENDPOINT${NC}"
                ;;
            NO_URL)
                status="${YELLOW}🔗 NO URL${NC}"
                ;;
            *)
                status="${RED}❓ UNKNOWN${NC}"
                ;;
        esac

        printf "%-35s %-25s %-15s %-30s\n" "$service" "$status" "$response_time" "$url"
    done
    echo
}

# Output results in JSON format
output_json() {
    local services=("$@")

    echo "{"
    echo "  \"timestamp\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\","
    echo "  \"environment\": \"$ENVIRONMENT\","
    echo "  \"services\": {"

    local first=true
    for service in "${services[@]}"; do
        if [[ "$first" == "false" ]]; then
            echo ","
        fi
        first=false

        local status="${HEALTH_RESULTS[$service]}"
        local response_time="${RESPONSE_TIMES[$service]}"
        local url="${SERVICE_URLS[$service]}"

        echo -n "    \"$service\": {"
        echo -n "\"status\": \"$status\", "
        echo -n "\"response_time\": \"$response_time\", "
        echo -n "\"url\": \"$url\""
        echo -n "}"
    done

    echo
    echo "  }"
    echo "}"
}

# Output results in simple format
output_simple() {
    local services=("$@")
    local healthy=0
    local total=${#services[@]}

    for service in "${services[@]}"; do
        local status="${HEALTH_RESULTS[$service]}"
        echo "$service: $status"
        if [[ "$status" == "HEALTHY" ]]; then
            ((healthy++))
        fi
    done

    echo "Summary: $healthy/$total services healthy"
}

# Send alert if services are unhealthy
send_alert() {
    local unhealthy_services=()

    for service in "${!HEALTH_RESULTS[@]}"; do
        if [[ "${HEALTH_RESULTS[$service]}" != "HEALTHY" ]]; then
            unhealthy_services+=("$service")
        fi
    done

    if [[ ${#unhealthy_services[@]} -gt 0 && -n "$ALERT_WEBHOOK_URL" ]]; then
        local alert_message="🚨 Health Check Alert for $ENVIRONMENT environment:\\n\\nUnhealthy services: ${unhealthy_services[*]}\\n\\nTimestamp: $(date)"

        curl -s -X POST "$ALERT_WEBHOOK_URL" \
            -H "Content-Type: application/json" \
            -d "{\"text\": \"$alert_message\"}" || warn "Failed to send alert"
    fi
}

# Generate health summary
generate_summary() {
    local services=("$@")
    local healthy=0
    local total=${#services[@]}

    for service in "${services[@]}"; do
        if [[ "${HEALTH_RESULTS[$service]}" == "HEALTHY" ]]; then
            ((healthy++))
        fi
    done

    local percentage=$((healthy * 100 / total))

    echo
    if [[ $healthy -eq $total ]]; then
        log "🎉 All services are healthy! ($healthy/$total)"
    elif [[ $healthy -gt $((total * 3 / 4)) ]]; then
        warn "⚠️ Most services are healthy ($healthy/$total - ${percentage}%)"
    else
        error "❌ Many services are unhealthy ($healthy/$total - ${percentage}%)"
    fi

    return $((total - healthy))
}

# Continuous health monitoring
continuous_monitoring() {
    local services=("$@")

    log "Starting continuous health monitoring (Ctrl+C to stop)..."

    while true; do
        clear
        echo "One Plan Health Monitor - $ENVIRONMENT Environment"
        echo "Last check: $(date)"
        echo "Press Ctrl+C to stop"

        # Reset results
        HEALTH_RESULTS=()
        RESPONSE_TIMES=()

        check_services_health "${services[@]}"

        case $OUTPUT_FORMAT in
            json)
                output_json "${services[@]}"
                ;;
            simple)
                output_simple "${services[@]}"
                ;;
            *)
                output_table "${services[@]}"
                ;;
        esac

        generate_summary "${services[@]}"
        send_alert

        sleep 30
    done
}

# Main function
main() {
    log "Starting One Plan health check..."

    # Parse arguments
    parse_args "$@"

    # Load service URLs
    load_service_urls

    # Determine which services to check
    local services_to_check=()

    if [[ "$CHECK_ALL" == "true" ]]; then
        services_to_check=($(printf '%s\n' "${!SERVICE_PORTS[@]}" | sort))
        log "Checking all ${#services_to_check[@]} services"
    else
        IFS=',' read -ra service_array <<< "$SERVICES"
        for service in "${service_array[@]}"; do
            service=$(echo "$service" | xargs)  # Trim whitespace
            if [[ -n "${SERVICE_PORTS[$service]}" ]]; then
                services_to_check+=("$service")
            else
                error "Invalid service: $service"
                exit 1
            fi
        done
        log "Checking ${#services_to_check[@]} services: ${services_to_check[*]}"
    fi

    # Display configuration
    info "Health Check Configuration:"
    info "  - Environment: $ENVIRONMENT"
    info "  - Services: ${services_to_check[*]}"
    info "  - Timeout: ${TIMEOUT}s"
    info "  - Max Retries: $MAX_RETRIES"
    info "  - Output Format: $OUTPUT_FORMAT"
    info "  - Continuous: ${CONTINUOUS:-false}"

    # Run health checks
    if [[ "$CONTINUOUS" == "true" ]]; then
        continuous_monitoring "${services_to_check[@]}"
    else
        check_services_health "${services_to_check[@]}"

        # Output results
        case $OUTPUT_FORMAT in
            json)
                output_json "${services_to_check[@]}"
                ;;
            simple)
                output_simple "${services_to_check[@]}"
                ;;
            *)
                output_table "${services_to_check[@]}"
                ;;
        esac

        generate_summary "${services_to_check[@]}"
        local unhealthy_count=$?

        send_alert

        exit $unhealthy_count
    fi
}

# Handle Ctrl+C gracefully in continuous mode
trap 'log "Health monitoring stopped"; exit 0' INT

# Run main function with all arguments
main "$@"

#!/bin/bash

# test.sh - Test script for One Plan microservices
# This script runs tests for all services or specific services

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
TEST_ALL=true
SERVICES=""
TEST_TYPE="unit"
PARALLEL_TESTS=false
COVERAGE_REPORT=false
INTEGRATION_TESTS=false
GENERATE_REPORTS=false

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
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] [TEST]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] [TEST] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] [TEST] ERROR:${NC} $1"
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] [TEST] INFO:${NC} $1"
}

# Help function
show_help() {
    cat << EOF
One Plan Test Script

Usage: $0 [OPTIONS]

Options:
    -s, --services SERVICES     Comma-separated list of services to test
    -a, --all                   Test all services (default)
    -t, --type TYPE            Test type: unit, integration, all (default: unit)
    -j, --parallel             Run tests in parallel
    -c, --coverage             Generate coverage reports
    -i, --integration          Run integration tests
    -r, --reports              Generate test reports
    --clean                    Clean test artifacts before running
    -h, --help                 Show this help message

Examples:
    # Run unit tests for all services
    $0 --all --type unit

    # Run tests for specific services
    $0 --services api-gateway,identity-service

    # Run all tests with coverage
    $0 --all --type all --coverage

    # Run tests in parallel (faster)
    $0 --all --parallel

Environment Variables:
    TEST_PARALLEL              Run tests in parallel if set to 'true'
    TEST_COVERAGE              Generate coverage if set to 'true'
    SPRING_PROFILES_ACTIVE     Spring profile for tests (default: test)

EOF
}

# Parse command line arguments
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -s|--services)
                SERVICES="$2"
                TEST_ALL=false
                shift 2
                ;;
            -a|--all)
                TEST_ALL=true
                shift
                ;;
            -t|--type)
                TEST_TYPE="$2"
                shift 2
                ;;
            -j|--parallel)
                PARALLEL_TESTS=true
                shift
                ;;
            -c|--coverage)
                COVERAGE_REPORT=true
                shift
                ;;
            -i|--integration)
                INTEGRATION_TESTS=true
                shift
                ;;
            -r|--reports)
                GENERATE_REPORTS=true
                shift
                ;;
            --clean)
                CLEAN_FIRST=true
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
    PARALLEL_TESTS=${PARALLEL_TESTS:-${TEST_PARALLEL:-false}}
    COVERAGE_REPORT=${COVERAGE_REPORT:-${TEST_COVERAGE:-false}}
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

# Setup test databases
setup_test_databases() {
    log "Setting up test databases..."

    # Check if we're in CI/GitHub Actions
    if [[ -n "$GITHUB_ACTIONS" ]]; then
        log "Running in GitHub Actions - databases should already be set up"
        return 0
    fi

    # For local testing, we can use Docker Compose
    if command -v docker-compose &> /dev/null; then
        log "Starting test databases with Docker Compose..."
        cd "$ROOT_DIR/infrastructure/docker"
        docker-compose up -d mariadb mongodb redis

        # Wait for databases to be ready
        log "Waiting for databases to be ready..."
        sleep 20

        # Create test databases
        docker-compose exec -T mariadb mysql -u root -prootpassword -e "
            CREATE DATABASE IF NOT EXISTS oneplan_identity_test;
            CREATE DATABASE IF NOT EXISTS oneplan_tenant_test;
            CREATE DATABASE IF NOT EXISTS oneplan_organization_test;
            CREATE DATABASE IF NOT EXISTS oneplan_project_test;
            CREATE DATABASE IF NOT EXISTS oneplan_task_test;
            CREATE DATABASE IF NOT EXISTS oneplan_requirement_test;
            CREATE DATABASE IF NOT EXISTS oneplan_storyboard_test;
            CREATE DATABASE IF NOT EXISTS oneplan_integration_test;
            CREATE DATABASE IF NOT EXISTS oneplan_notification_test;
            CREATE DATABASE IF NOT EXISTS oneplan_analytics_test;
            CREATE DATABASE IF NOT EXISTS oneplan_subscription_test;
            GRANT ALL PRIVILEGES ON *.* TO 'oneplan'@'%';
            FLUSH PRIVILEGES;
        " || warn "Could not create test databases"

        cd "$ROOT_DIR"
    else
        warn "Docker Compose not available - assuming databases are already running"
    fi
}

# Clean test artifacts
clean_test_artifacts() {
    local service=$1
    local service_dir="$ROOT_DIR/backend/$service"

    if [[ -d "$service_dir" ]]; then
        log "Cleaning test artifacts for $service..."
        cd "$service_dir"
        if [[ -f "./gradlew" ]]; then
            chmod +x ./gradlew
            ./gradlew clean || warn "Failed to clean $service"
        fi
    fi
}

# Run tests for a single service
test_service() {
    local service=$1
    local service_dir="$ROOT_DIR/backend/$service"

    if [[ ! -d "$service_dir" ]]; then
        error "Service directory not found: $service_dir"
        return 1
    fi

    log "Testing $service..."

    cd "$service_dir"

    # Make gradlew executable
    if [[ -f "./gradlew" ]]; then
        chmod +x ./gradlew
    else
        error "Gradle wrapper not found for $service"
        return 1
    fi

    # Clean if requested
    if [[ "$CLEAN_FIRST" == "true" ]]; then
        clean_test_artifacts "$service"
    fi

    # Prepare test command
    local test_commands=()

    case $TEST_TYPE in
        unit)
            test_commands+=("test")
            ;;
        integration)
            test_commands+=("integrationTest")
            ;;
        all)
            test_commands+=("test" "integrationTest")
            ;;
        *)
            error "Unknown test type: $TEST_TYPE"
            return 1
            ;;
    esac

    # Add coverage if requested
    if [[ "$COVERAGE_REPORT" == "true" ]]; then
        test_commands+=("jacocoTestReport")
    fi

    # Set environment variables for tests
    export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-test}
    export SPRING_DATASOURCE_URL="jdbc:mariadb://localhost:3306/oneplan_${service//-/_}_test"
    export SPRING_DATASOURCE_USERNAME=oneplan
    export SPRING_DATASOURCE_PASSWORD=oneplan123
    export SPRING_DATA_MONGODB_URI="mongodb://localhost:27017/oneplan_${service//-/_}_test"
    export SPRING_REDIS_HOST=localhost
    export SPRING_REDIS_PORT=6379

    # Run tests
    local gradle_args=(
        "${test_commands[@]}"
        "--info"
        "--stacktrace"
    )

    if ./gradlew "${gradle_args[@]}"; then
        log "✅ Tests passed for $service"
        return 0
    else
        error "❌ Tests failed for $service"
        return 1
    fi
}

# Run tests in parallel
test_parallel() {
    local services=("$@")
    local pids=()
    local failed_services=()

    info "Running tests for ${#services[@]} services in parallel..."

    # Start tests in background
    for service in "${services[@]}"; do
        test_service "$service" &
        pids+=($!)
    done

    # Wait for all tests to complete
    for i in "${!pids[@]}"; do
        local pid=${pids[i]}
        local service=${services[i]}

        if wait $pid; then
            log "✅ $service tests completed successfully"
        else
            error "❌ $service tests failed"
            failed_services+=("$service")
        fi
    done

    # Report results
    if [[ ${#failed_services[@]} -eq 0 ]]; then
        log "🎉 All tests passed!"
        return 0
    else
        error "❌ Failed services: ${failed_services[*]}"
        return 1
    fi
}

# Run tests sequentially
test_sequential() {
    local services=("$@")
    local failed_services=()

    info "Running tests for ${#services[@]} services sequentially..."

    for service in "${services[@]}"; do
        if test_service "$service"; then
            log "✅ $service tests completed successfully"
        else
            error "❌ $service tests failed"
            failed_services+=("$service")
        fi
    done

    # Report results
    if [[ ${#failed_services[@]} -eq 0 ]]; then
        log "🎉 All tests passed!"
        return 0
    else
        error "❌ Failed services: ${failed_services[*]}"
        return 1
    fi
}

# Generate test reports
generate_test_reports() {
    local services=("$@")

    if [[ "$GENERATE_REPORTS" != "true" ]]; then
        return 0
    fi

    log "Generating test reports..."

    local reports_dir="$ROOT_DIR/test-reports"
    mkdir -p "$reports_dir"

    for service in "${services[@]}"; do
        local service_dir="$ROOT_DIR/backend/$service"

        # Copy test results
        if [[ -d "$service_dir/build/test-results" ]]; then
            cp -r "$service_dir/build/test-results" "$reports_dir/$service-test-results" || true
        fi

        # Copy coverage reports
        if [[ -d "$service_dir/build/reports/jacoco" ]]; then
            cp -r "$service_dir/build/reports/jacoco" "$reports_dir/$service-coverage" || true
        fi

        # Copy test reports
        if [[ -d "$service_dir/build/reports/tests" ]]; then
            cp -r "$service_dir/build/reports/tests" "$reports_dir/$service-reports" || true
        fi
    done

    log "Test reports generated in $reports_dir"
}

# Pre-test checks
pre_test_checks() {
    log "Running pre-test checks..."

    # Check Java version
    if ! java -version &> /dev/null; then
        error "Java is not installed or not in PATH"
        exit 1
    fi

    # Check if we're in the right directory
    if [[ ! -f "$ROOT_DIR/backend/api-gateway/build.gradle" ]]; then
        error "Not in the correct project directory"
        exit 1
    fi

    log "✅ Pre-test checks passed"
}

# Main function
main() {
    log "Starting One Plan test execution..."

    # Parse arguments
    parse_args "$@"

    # Pre-test checks
    pre_test_checks

    # Setup test databases
    setup_test_databases

    # Determine which services to test
    local services_to_test=()

    if [[ "$TEST_ALL" == "true" ]]; then
        services_to_test=("${ALL_SERVICES[@]}")
        log "Testing all ${#ALL_SERVICES[@]} services"
    else
        IFS=',' read -ra service_array <<< "$SERVICES"
        for service in "${service_array[@]}"; do
            service=$(echo "$service" | xargs)  # Trim whitespace
            if validate_service "$service"; then
                services_to_test+=("$service")
            else
                error "Invalid service: $service"
                exit 1
            fi
        done
        log "Testing ${#services_to_test[@]} services: ${services_to_test[*]}"
    fi

    # Display test configuration
    info "Test Configuration:"
    info "  - Services: ${services_to_test[*]}"
    info "  - Test Type: $TEST_TYPE"
    info "  - Coverage: $COVERAGE_REPORT"
    info "  - Parallel: $PARALLEL_TESTS"
    info "  - Reports: $GENERATE_REPORTS"
    info "  - Clean First: ${CLEAN_FIRST:-false}"

    # Start test execution
    local start_time=$(date +%s)

    if [[ "$PARALLEL_TESTS" == "true" ]]; then
        test_parallel "${services_to_test[@]}"
    else
        test_sequential "${services_to_test[@]}"
    fi

    local exit_code=$?
    local end_time=$(date +%s)
    local duration=$((end_time - start_time))

    # Generate reports if requested
    generate_test_reports "${services_to_test[@]}"

    # Summary
    log "Tests completed in ${duration} seconds"

    if [[ $exit_code -eq 0 ]]; then
        log "🎉 All tests passed!"
    else
        error "❌ Some tests failed!"
    fi

    exit $exit_code
}

# Run main function with all arguments
main "$@"

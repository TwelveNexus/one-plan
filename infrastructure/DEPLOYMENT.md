# One Plan Deployment Guide

<div align="center">

![Deployment](https://via.placeholder.com/400x200?text=One+Plan+Deployment)

**Complete deployment guide for One Plan microservices**

</div>

## 📋 Table of Contents

- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Local Deployment](#local-deployment)
- [Coolify Deployment](#coolify-deployment)
- [Environment Configuration](#environment-configuration)
- [Database Setup](#database-setup)
- [SSL/TLS Configuration](#ssltls-configuration)
- [Monitoring Setup](#monitoring-setup)
- [Backup and Recovery](#backup-and-recovery)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

One Plan supports multiple deployment strategies:

- **🏠 Local Development**: Docker Compose with local databases
- **☁️ Coolify Production**: Container-based deployment with managed databases
- **📦 Kubernetes**: Enterprise-scale deployment (future)
- **🚀 CI/CD Automation**: GitHub Actions integration

## ✅ Prerequisites

### System Requirements

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| **CPU** | 4 cores | 8+ cores |
| **RAM** | 8GB | 16GB+ |
| **Storage** | 50GB | 100GB+ SSD |
| **Network** | 100Mbps | 1Gbps+ |

### Software Dependencies

```bash
# Required
Docker 20.10+
Docker Compose 2.0+
Git 2.30+

# For development
Java 21
Node.js 20+
Gradle 8.6+

# For production
Coolify instance or Kubernetes cluster
```

### External Services

- **AI Service**: OpenAI, Anthropic, or similar
- **Email Service**: SMTP server or service
- **Payment Gateway**: Razorpay, PhonePe (India)
- **Git Providers**: GitHub, GitLab, Bitbucket
- **Monitoring**: Optional Prometheus/Grafana

## 🏠 Local Deployment

### 1. Quick Start

```bash
# Clone repository
git clone https://github.com/yourusername/one-plan.git
cd one-plan

# Setup environment
cp infrastructure/docker/.env.example infrastructure/docker/.env.local
chmod +x infrastructure/ci-cd/scripts/*.sh

# Start all services
cd infrastructure/docker
docker-compose up -d

# Verify deployment
curl http://localhost:8080/actuator/health
```

### 2. Development Mode

```bash
# Start only databases
docker-compose up -d mariadb mongodb redis

# Run services individually for development
cd backend/api-gateway
./gradlew bootRun

# In separate terminals, start other services
cd backend/identity-service && ./gradlew bootRun
cd backend/tenant-service && ./gradlew bootRun
# ... etc
```

### 3. Build and Test

```bash
# Build all Docker images
./infrastructure/ci-cd/scripts/build.sh --all

# Run comprehensive tests
./infrastructure/ci-cd/scripts/test.sh --all --coverage

# Health check all services
./infrastructure/ci-cd/scripts/health-check.sh --all --environment local
```

## ☁️ Coolify Deployment

### 1. Coolify Setup

1. **Install Coolify**
```bash
# On your server
curl -fsSL https://cdn.coollabs.io/coolify/install.sh | bash
```

2. **Access Coolify Dashboard**
   - Open `https://your-server-ip:8000`
   - Complete initial setup
   - Create project for One Plan

### 2. Database Configuration

Create managed databases in Coolify:

```yaml
# MariaDB Configuration
Service: MariaDB 11.2
Databases:
  - oneplan_identity
  - oneplan_tenant
  - oneplan_organization
  - oneplan_project
  - oneplan_task
  - oneplan_requirement
  - oneplan_storyboard
  - oneplan_integration
  - oneplan_notification
  - oneplan_analytics
  - oneplan_subscription

# MongoDB Configuration
Service: MongoDB 6.0
Databases:
  - oneplan_storyboard
  - oneplan_integration
  - oneplan_analytics

# Redis Configuration
Service: Redis 7.0
Database: 0
```

### 3. Service Deployment

For each of the 12 microservices:

1. **Create New Service**
   - Type: Docker Compose
   - Source: GitHub repository
   - Dockerfile: `infrastructure/docker/{service}.Dockerfile`

2. **Environment Variables**
```bash
# Common variables for all services
SPRING_PROFILES_ACTIVE=production
SPRING_DATASOURCE_USERNAME=${MARIADB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${MARIADB_PASSWORD}

# Service-specific database URLs
SPRING_DATASOURCE_URL=jdbc:mariadb://${MARIADB_HOST}:3306/oneplan_{service}

# For services using MongoDB
SPRING_DATA_MONGODB_URI=mongodb://${MONGODB_HOST}:27017/oneplan_{collection}

# For services using Redis
SPRING_REDIS_HOST=${REDIS_HOST}
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=${REDIS_PASSWORD}
```

3. **Health Check Configuration**
```yaml
Health Check:
  Endpoint: /actuator/health
  Interval: 30s
  Timeout: 10s
  Retries: 3
```

### 4. GitHub Actions Integration

Set up these secrets in your GitHub repository:

```bash
# Coolify Configuration
COOLIFY_URL=https://your-coolify-instance.com
COOLIFY_TOKEN=your-api-token

# Service UUIDs (get from Coolify dashboard)
COOLIFY_API_GATEWAY_UUID=uuid-from-coolify
COOLIFY_IDENTITY_SERVICE_UUID=uuid-from-coolify
COOLIFY_TENANT_SERVICE_UUID=uuid-from-coolify
COOLIFY_ORGANIZATION_SERVICE_UUID=uuid-from-coolify
COOLIFY_PROJECT_SERVICE_UUID=uuid-from-coolify
COOLIFY_TASK_SERVICE_UUID=uuid-from-coolify
COOLIFY_REQUIREMENT_SERVICE_UUID=uuid-from-coolify
COOLIFY_STORYBOARD_SERVICE_UUID=uuid-from-coolify
COOLIFY_INTEGRATION_SERVICE_UUID=uuid-from-coolify
COOLIFY_NOTIFICATION_SERVICE_UUID=uuid-from-coolify
COOLIFY_ANALYTICS_SERVICE_UUID=uuid-from-coolify
COOLIFY_SUBSCRIPTION_SERVICE_UUID=uuid-from-coolify

# Service URLs for health checks
COOLIFY_API_GATEWAY_URL=https://api.yourdomain.com
COOLIFY_IDENTITY_SERVICE_URL=https://auth.yourdomain.com
# ... (for each service)
```

### 5. Automated Deployment

```bash
# Manual deployment
./infrastructure/ci-cd/scripts/deploy.sh --all --environment production

# GitHub Actions will automatically deploy on push to main
git push origin main

# Monitor deployment
./infrastructure/ci-cd/scripts/health-check.sh --all --environment production
```

## 🔧 Environment Configuration

### Development Environment (.env.local)

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=oneplan
DB_PASSWORD=oneplan123

# MongoDB
MONGODB_HOST=localhost
MONGODB_PORT=27017

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration (development only)
JWT_SECRET=dev-secret-change-in-production
JWT_EXPIRATION=86400

# AI Services (optional for development)
OPENAI_API_KEY=your-dev-key
```

### Production Environment (.env.prod)

```bash
# Database Configuration (Coolify managed)
DB_HOST=${MARIADB_HOST}
DB_PORT=${MARIADB_PORT}
DB_USERNAME=${MARIADB_USERNAME}
DB_PASSWORD=${MARIADB_PASSWORD}

# MongoDB (Coolify managed)
MONGODB_HOST=${MONGODB_HOST}
MONGODB_PORT=${MONGODB_PORT}

# Redis (Coolify managed)
REDIS_HOST=${REDIS_HOST}
REDIS_PORT=${REDIS_PORT}
REDIS_PASSWORD=${REDIS_PASSWORD}

# Security (use strong values)
JWT_SECRET=${JWT_SECRET_PRODUCTION}
JWT_EXPIRATION=86400

# AI Services
OPENAI_API_KEY=${OPENAI_API_KEY}

# OAuth Configuration
GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID}
GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET}

# Payment Gateways
RAZORPAY_KEY_ID=${RAZORPAY_KEY_ID}
RAZORPAY_KEY_SECRET=${RAZORPAY_KEY_SECRET}

# Email Configuration
SMTP_HOST=${SMTP_HOST}
SMTP_PORT=${SMTP_PORT}
SMTP_USERNAME=${SMTP_USERNAME}
SMTP_PASSWORD=${SMTP_PASSWORD}

# Frontend URLs
FRONTEND_URL=https://app.yourdomain.com
API_GATEWAY_URL=https://api.yourdomain.com
```

### Environment Variable Reference

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Yes | Spring profile | `production` |
| `JWT_SECRET` | Yes | JWT signing key | `256-bit-secure-key` |
| `OPENAI_API_KEY` | No | AI service key | `sk-...` |
| `GITHUB_CLIENT_ID` | No | OAuth GitHub | `your-client-id` |
| `RAZORPAY_KEY_ID` | No | Payment gateway | `rzp_...` |
| `SMTP_HOST` | No | Email server | `smtp.gmail.com` |

## 🗄️ Database Setup

### MariaDB Schema Initialization

```sql
-- Create databases for all services
CREATE DATABASE IF NOT EXISTS oneplan_identity;
CREATE DATABASE IF NOT EXISTS oneplan_tenant;
CREATE DATABASE IF NOT EXISTS oneplan_organization;
CREATE DATABASE IF NOT EXISTS oneplan_project;
CREATE DATABASE IF NOT EXISTS oneplan_task;
CREATE DATABASE IF NOT EXISTS oneplan_requirement;
CREATE DATABASE IF NOT EXISTS oneplan_storyboard;
CREATE DATABASE IF NOT EXISTS oneplan_integration;
CREATE DATABASE IF NOT EXISTS oneplan_notification;
CREATE DATABASE IF NOT EXISTS oneplan_analytics;
CREATE DATABASE IF NOT EXISTS oneplan_subscription;

-- Create user and grant permissions
CREATE USER 'oneplan'@'%' IDENTIFIED BY 'secure-password';
GRANT ALL PRIVILEGES ON oneplan_*.* TO 'oneplan'@'%';
FLUSH PRIVILEGES;
```

### MongoDB Collections

```javascript
// Collections will be created automatically by services
// Storyboard Service
use oneplan_storyboard;
db.storyboard_canvas.createIndex({"storyboardId": 1});

// Integration Service
use oneplan_integration;
db.webhook_events.createIndex({"timestamp": 1});

// Analytics Service
use oneplan_analytics;
db.analytics_events.createIndex({"timestamp": 1, "eventType": 1});
```

### Database Migration

```bash
# Migrations are handled by Flyway in each service
# They run automatically on service startup

# Manual migration (if needed)
cd backend/identity-service
./gradlew flywayMigrate

# Check migration status
./gradlew flywayInfo
```

## 🔐 SSL/TLS Configuration

### Nginx SSL Setup

```nginx
# /etc/nginx/sites-available/oneplan
server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;
    
    # SSL Configuration
    ssl_certificate /etc/ssl/certs/yourdomain.crt;
    ssl_certificate_key /etc/ssl/private/yourdomain.key;
    
    # Security Headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Frame-Options DENY always;
    add_header X-Content-Type-Options nosniff always;
    
    # Proxy to API Gateway
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name api.yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

### Let's Encrypt with Coolify

Coolify automatically handles SSL certificates through Let's Encrypt when you:

1. Set up custom domain for each service
2. Enable SSL in service settings
3. Coolify handles certificate provisioning and renewal

## 📊 Monitoring Setup

### Health Check Monitoring

```bash
# Set up continuous monitoring
./infrastructure/ci-cd/scripts/health-check.sh \
  --all \
  --continuous \
  --environment production \
  --alert-webhook https://hooks.slack.com/your-webhook
```

### Application Metrics

Each service exposes metrics at `/actuator/metrics`:

```bash
# Check metrics for a service
curl https://api.yourdomain.com/actuator/metrics
curl https://api.yourdomain.com/actuator/metrics/jvm.memory.used
curl https://api.yourdomain.com/actuator/metrics/http.server.requests
```

### Log Aggregation

```bash
# Centralized logging with Docker
docker-compose logs -f --tail=100

# Filter by service
docker-compose logs -f api-gateway

# Export logs
docker-compose logs --no-color > oneplan.log
```

### Prometheus Integration (Optional)

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'oneplan-services'
    static_configs:
      - targets:
        - 'api-gateway:8080'
        - 'identity-service:8081'
        - 'tenant-service:8082'
        # ... all services
    metrics_path: '/actuator/prometheus'
```

## 💾 Backup and Recovery

### Database Backup

```bash
# MariaDB backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/mariadb"

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup all One Plan databases
databases=("oneplan_identity" "oneplan_tenant" "oneplan_organization" 
           "oneplan_project" "oneplan_task" "oneplan_requirement" 
           "oneplan_storyboard" "oneplan_integration" "oneplan_notification" 
           "oneplan_analytics" "oneplan_subscription")

for db in "${databases[@]}"; do
    mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $db > "$BACKUP_DIR/${db}_${DATE}.sql"
    gzip "$BACKUP_DIR/${db}_${DATE}.sql"
done

# Cleanup old backups (keep 30 days)
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
```

### MongoDB Backup

```bash
# MongoDB backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups/mongodb"

mkdir -p $BACKUP_DIR

# Backup MongoDB databases
mongodump --host $MONGODB_HOST:$MONGODB_PORT \
          --db oneplan_storyboard \
          --out "$BACKUP_DIR/oneplan_storyboard_$DATE"

mongodump --host $MONGODB_HOST:$MONGODB_PORT \
          --db oneplan_integration \
          --out "$BACKUP_DIR/oneplan_integration_$DATE"

mongodump --host $MONGODB_HOST:$MONGODB_PORT \
          --db oneplan_analytics \
          --out "$BACKUP_DIR/oneplan_analytics_$DATE"

# Compress backups
tar -czf "$BACKUP_DIR/mongodb_backup_$DATE.tar.gz" -C "$BACKUP_DIR" .

# Cleanup old backups
find $BACKUP_DIR -name "*.tar.gz" -mtime +30 -delete
```

### Automated Backup with Cron

```bash
# Add to crontab
crontab -e

# Daily backup at 2 AM
0 2 * * * /scripts/backup-mariadb.sh
0 3 * * * /scripts/backup-mongodb.sh

# Weekly full backup
0 4 * * 0 /scripts/backup-full.sh
```

### Disaster Recovery

```bash
# MariaDB restore
#!/bin/bash
BACKUP_FILE=$1
DATABASE=$2

if [ -z "$BACKUP_FILE" ] || [ -z "$DATABASE" ]; then
    echo "Usage: $0 <backup_file.sql.gz> <database_name>"
    exit 1
fi

# Restore database
gunzip -c $BACKUP_FILE | mysql -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DATABASE

echo "Database $DATABASE restored from $BACKUP_FILE"
```

```bash
# MongoDB restore
#!/bin/bash
BACKUP_DIR=$1
DATABASE=$2

if [ -z "$BACKUP_DIR" ] || [ -z "$DATABASE" ]; then
    echo "Usage: $0 <backup_directory> <database_name>"
    exit 1
fi

# Restore MongoDB
mongorestore --host $MONGODB_HOST:$MONGODB_PORT \
             --db $DATABASE \
             --drop \
             $BACKUP_DIR/$DATABASE
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Service Won't Start

**Symptoms**: Service fails to start or crashes immediately

**Diagnosis**:
```bash
# Check service logs
docker-compose logs service-name

# Check health endpoint
curl http://localhost:8081/actuator/health

# Check resource usage
docker stats
```

**Solutions**:
```bash
# Restart service
docker-compose restart service-name

# Rebuild image
./infrastructure/ci-cd/scripts/build.sh --services service-name --no-cache

# Check configuration
docker-compose config
```

#### 2. Database Connection Issues

**Symptoms**: Services can't connect to database

**Diagnosis**:
```bash
# Test database connectivity
telnet database-host 3306

# Check database logs
docker-compose logs mariadb

# Verify credentials
mysql -h DB_HOST -u DB_USER -p
```

**Solutions**:
```bash
# Restart database
docker-compose restart mariadb

# Check network connectivity
docker network ls
docker network inspect oneplan-network

# Verify environment variables
docker-compose exec api-gateway env | grep DB
```

#### 3. High Memory Usage

**Symptoms**: Services consuming excessive memory

**Diagnosis**:
```bash
# Check memory usage
docker stats

# Check JVM memory
curl http://localhost:8081/actuator/metrics/jvm.memory.used

# Check heap dump
curl http://localhost:8081/actuator/heapdump
```

**Solutions**:
```bash
# Tune JVM settings in Dockerfile
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Scale down non-essential services
docker-compose scale analytics-service=0

# Add memory limits
services:
  api-gateway:
    deploy:
      resources:
        limits:
          memory: 1g
```

#### 4. Slow API Response

**Symptoms**: API calls taking too long

**Diagnosis**:
```bash
# Check response times
./infrastructure/ci-cd/scripts/health-check.sh --all --format table

# Monitor database performance
SHOW PROCESSLIST;

# Check Redis performance
redis-cli info stats
```

**Solutions**:
```bash
# Add database indexes
CREATE INDEX idx_user_email ON users(email);

# Optimize queries
EXPLAIN SELECT * FROM projects WHERE organization_id = ?;

# Add caching
@Cacheable("projects")
public List<Project> findByOrganization(Long orgId)
```

#### 5. CI/CD Pipeline Failures

**Symptoms**: GitHub Actions workflows failing

**Diagnosis**:
```bash
# Check workflow logs in GitHub Actions
# Check secrets configuration
# Verify Coolify connectivity
```

**Solutions**:
```bash
# Test deployment script locally
./infrastructure/ci-cd/scripts/deploy.sh --dry-run --all

# Verify Coolify API access
curl -H "Authorization: Bearer $COOLIFY_TOKEN" $COOLIFY_URL/api/v1/security

# Update service UUIDs
# Check environment variables in GitHub secrets
```

### Performance Optimization

#### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_projects_org_id ON projects(organization_id);
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assignee ON tasks(assignee_id);
CREATE INDEX idx_comments_resource ON comments(resource_type, resource_id);

-- Optimize queries
EXPLAIN ANALYZE SELECT * FROM tasks WHERE project_id = ? AND status = ?;

-- Monitor slow queries
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

#### JVM Tuning

```dockerfile
# Optimized JVM settings for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseCGroupMemoryLimitForHeap \
               -Djava.security.egd=file:/dev/./urandom"
```

#### Redis Optimization

```bash
# Redis configuration
maxmemory 512mb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

### Security Considerations

#### Secure Deployment Checklist

- [ ] Strong JWT secrets (256-bit minimum)
- [ ] Database credentials rotated regularly
- [ ] SSL/TLS certificates configured
- [ ] CORS origins restricted
- [ ] Rate limiting enabled
- [ ] Security headers configured
- [ ] Dependency scanning enabled
- [ ] Container security scanning
- [ ] Network segmentation
- [ ] Firewall rules configured

#### Secret Management

```bash
# Use environment-specific secrets
# Never commit secrets to Git
# Rotate secrets regularly
# Use secret management services

# Example: Rotating JWT secret
kubectl create secret generic jwt-secret --from-literal=key=$(openssl rand -base64 32)
```

## 📈 Scaling Strategies

### Horizontal Scaling

```yaml
# Docker Compose scaling
docker-compose up --scale api-gateway=3 --scale task-service=2

# Coolify scaling
# Use Coolify dashboard to scale individual services
# Configure load balancers for scaled services
```

### Database Scaling

```bash
# Read replicas for MariaDB
# Sharding for MongoDB
# Redis clustering for high availability

# Example: MariaDB read replica
services:
  mariadb-master:
    image: mariadb:11.2
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      
  mariadb-slave:
    image: mariadb:11.2
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_MASTER_HOST: mariadb-master
```

### Caching Strategy

```java
// Application-level caching
@Cacheable("projects")
public List<Project> findByOrganization(Long orgId) {
    return projectRepository.findByOrganizationId(orgId);
}

// Redis distributed caching
@Cacheable(value = "user-sessions", key = "#userId")
public UserSession getUserSession(Long userId) {
    return sessionService.getSession(userId);
}
```

## 🚀 Deployment Checklist

### Pre-deployment

- [ ] All tests pass locally
- [ ] Environment variables configured
- [ ] Database migrations ready
- [ ] Secrets properly set
- [ ] SSL certificates configured
- [ ] Monitoring setup complete
- [ ] Backup strategy implemented

### Deployment

- [ ] Deploy to staging first
- [ ] Run integration tests
- [ ] Check all health endpoints
- [ ] Verify database connections
- [ ] Test external integrations
- [ ] Monitor resource usage
- [ ] Check logs for errors

### Post-deployment

- [ ] Verify all services healthy
- [ ] Test critical user flows
- [ ] Check monitoring dashboards
- [ ] Verify backup jobs running
- [ ] Update documentation
- [ ] Notify stakeholders

## 🔗 Additional Resources

- **Infrastructure Repository**: [GitHub](https://github.com/yourusername/one-plan)
- **API Documentation**: [Swagger UI](https://api.yourdomain.com/swagger-ui)
- **Monitoring Dashboard**: [Grafana](https://monitoring.yourdomain.com)
- **Status Page**: [Status](https://status.yourdomain.com)

## 📞 Support

For deployment issues:

1. **Check troubleshooting section** above
2. **Review logs** for error messages
3. **Test connectivity** between services
4. **Create GitHub issue** with detailed information
5. **Contact support**: [info@apavayan.com](mailto:info@apavayan.com)

---

<div align="center">

**One Plan Deployment Guide**

Built with ❤️ for scalable, reliable deployments

[Documentation](../docs/) • [Issues](https://github.com/yourusername/one-plan/issues) • [Support](mailto:info@apavayan.com)

</div>

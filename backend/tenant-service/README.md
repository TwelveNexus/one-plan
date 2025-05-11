# One Plan - Tenant Service

This service is responsible for tenant management in the One Plan platform.

## Prerequisites

- Java 21
- MariaDB 11.2+
- Gradle 8.5+

## Setup

1. Create a database:
```sql
CREATE DATABASE IF NOT EXISTS oneplan_tenant;

Configure application properties in src/main/resources/application.yml

Running the Service
bash./gradlew bootRun
API Documentation
Once the service is running, access the Swagger UI at:
http://localhost:8081/api/v1/swagger-ui.html
Available Endpoints

POST /api/v1/tenants - Create a new tenant
GET /api/v1/tenants - Get all tenants
GET /api/v1/tenants/{id} - Get tenant by ID
GET /api/v1/tenants/domain/{domain} - Get tenant by domain
PUT /api/v1/tenants/{id} - Update a tenant
DELETE /api/v1/tenants/{id} - Delete a tenant


## Service Readiness Checklist:

1. ✅ Core service structure with Spring Boot
2. ✅ Model, repository, service, and controller layers implemented
3. ✅ Database schema with Flyway migration
4. ✅ OpenAPI/Swagger documentation
5. ✅ Exception handling
6. ✅ Basic security setup (will be improved later)
7. ✅ README documentation

The Tenant Service is now ready to run. To run it:

1. Make sure MariaDB is running
2. Create the database: `CREATE DATABASE IF NOT EXISTS oneplan_tenant;`
3. Run the service with `./gradlew bootRun`
4. Access the Swagger UI at http://localhost:8082/api/v1/swagger-ui.html to test the APIs
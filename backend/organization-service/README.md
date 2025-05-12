# One Plan - Organization Service

This service is responsible for organization, team, and team member management in the One Plan platform.

## Prerequisites

- Java 21
- MariaDB 11.2+
- Gradle 8.5+

## Setup

1. Create a database:
```sql
CREATE DATABASE IF NOT EXISTS oneplan_organization;
```

Configure application properties in src/main/resources/application.yml

Running the Service
bash./gradlew bootRun
API Documentation
Once the service is running, access the Swagger UI at:
http://localhost:8082/api/v1/swagger-ui.html
Available Endpoints
Organizations

POST /api/v1/organizations - Create a new organization
GET /api/v1/organizations - Get all organizations
GET /api/v1/organizations/{id} - Get organization by ID
GET /api/v1/organizations/tenant/{tenantId} - Get organizations by tenant ID
PUT /api/v1/organizations/{id} - Update an organization
DELETE /api/v1/organizations/{id} - Delete an organization

Teams

POST /api/v1/teams - Create a new team
GET /api/v1/teams/{id} - Get team by ID
GET /api/v1/teams/organization/{organizationId} - Get teams by organization ID
PUT /api/v1/teams/{id} - Update a team
DELETE /api/v1/teams/{id} - Delete a team

Team Members

POST /api/v1/team-members - Add a member to a team
GET /api/v1/team-members/{id} - Get team member by ID
GET /api/v1/team-members/team/{teamId} - Get members by team ID
GET /api/v1/team-members/user/{userId} - Get teams by user ID
PATCH /api/v1/team-members/{id}/role - Update a member's role
PATCH /api/v1/team-members/{id}/status - Update a member's status
DELETE /api/v1/team-members/{id} - Remove a member from a team


## Service Readiness Checklist:

1. ✅ Core service structure with Spring Boot
2. ✅ Entity models for Organization, Team, and TeamMember
3. ✅ Repository, service, and controller layers implemented
4. ✅ Database schema with Flyway migration
5. ✅ OpenAPI/Swagger documentation
6. ✅ Exception handling
7. ✅ Basic security setup (will be improved later)
8. ✅ README documentation

The Organization Service is now ready to run. To run it:

1. Make sure MariaDB is running
2. Create the database: `CREATE DATABASE IF NOT EXISTS oneplan_organization;`
3. Run the service with `./gradlew bootRun`
4. Access the Swagger UI at http://localhost:8082/api/v1/swagger-ui.html to test the APIs

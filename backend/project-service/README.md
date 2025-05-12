# Project Service

## Description
This service manages projects within the One Plan platform.

## Running the Service

1. Ensure MariaDB is running
2. Create the database: `CREATE DATABASE oneplan_project;`
3. Set environment variables or update .env file
4. Run: `./gradlew bootRun`

## API Documentation
Once running, visit: http://localhost:8083/api/v1/swagger-ui.html

## Endpoints
- POST /api/v1/projects - Create a new project
- GET /api/v1/projects/{id} - Get project by ID
- GET /api/v1/projects/organization/{organizationId} - Get projects by organization
- PUT /api/v1/projects/{id} - Update project
- DELETE /api/v1/projects/{id} - Delete project

## Next Steps:

Run the database creation command
Start the service with ./gradlew bootRun
Test the API using Swagger UI at http://localhost:8083/api/v1/swagger-ui.html

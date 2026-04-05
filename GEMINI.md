# Sonata Playlists - Instructional Context

This document provides a comprehensive overview of the Sonata Playlists microservice. Use this as a guide for development, testing, and understanding the project's architecture.

## Project Overview

**Sonata Playlists** is a reactive microservice built with **Spring Boot 3.1.2** and **Spring WebFlux**. It serves as a container for tracks and other playable items, providing a REST API to create, manage, and retrieve user playlists.

### Core Technologies
- **Language:** Java 17
- **Framework:** Spring Boot 3.1.2 (Reactive Stack)
- **Database:** PostgreSQL (R2DBC for app, JDBC for Flyway)
- **Migrations:** Flyway
- **Messaging:** Apache Kafka (Reactor Kafka)
- **Service Discovery:** Spring Cloud Netflix Eureka
- **Contract Testing:** Spring Cloud Contract
- **Mapping:** MapStruct
- **Build Tool:** Gradle 8.x

## Architecture and Structure

The project follows a standard Spring Boot layered architecture:
- `controller/`: REST endpoints (Reactive WebFlux).
- `service/`: Core business logic, abstracted through interfaces (e.g., `PlaylistOperationsFacade`).
- `repository/`: R2DBC repositories for reactive database interaction.
- `model/` & `entity/`: Domain models and database entities.
- `dto/`: Data Transfer Objects for API requests and responses.
- `support/`: Converters, utility classes, and web support.

### Key Directories
- `src/main/resources/db/migration`: Flyway SQL migration scripts.
- `docs/`: Detailed API documentation for each endpoint.
- `src/test/java/testing/`: Extensive testing support, fakers, and asserts.

## Building and Running

### Prerequisites
- Java 17
- Docker & Docker Compose
- GitHub Packages credentials (`GH_USERNAME`, `GH_ACCESS_TOKEN`) for private dependencies.

### Local Development
To run the service locally without Docker (requires a running Postgres):
```bash
./gradlew bootRun
```

### Docker Build
```bash
# Build the production image
docker build --secret id=github.username,env=GH_USERNAME --secret id=github.token,env=GH_ACCESS_TOKEN -t "playlists" .
```

## Testing

The project uses a containerized testing approach to ensure environment consistency.

### Running Tests
You can run tests locally using Gradle:
```bash
./gradlew test
```

Or use the provided scripts to run tests in a Docker environment:
```bash
./run-tests.sh
```
This script builds a test image and runs it using `docker-compose.test.yml`, which includes a dedicated PostgreSQL instance.

### Test Components
- **JUnit 5**: Standard testing framework.
- **Testcontainers**: Used for some integration tests.
- **RestAssured**: For API verification.
- **Spring Cloud Contract**: For consumer-driven contract tests.
- **Fakers**: Located in `src/test/java/testing/faker/` for generating domain objects.

## Available Scripts
- `run-tests.sh`: Main entry point for running the full test suite in Docker.
- `sonata-playlists-test-build.sh`: Builds the `Dockerfile.test` image.
- `sonata-playlists-test-run.sh`: Launches the test environment using Docker Compose.

## Development Conventions

1.  **Reactive First:** All I/O and service methods must return `Mono` or `Flux`.
2.  **Clean Separation:** Keep business logic out of controllers. Use `Facade` patterns where appropriate.
3.  **Entity/DTO Mapping:** Use MapStruct for all conversions.
4.  **Database Evolution:** Never modify existing Flyway scripts. Always create a new versioned script.
5.  **Testing:** Every new feature or bug fix must include corresponding tests (Unit, Integration, or Contract).
6.  **Style:** Use Lombok to reduce boilerplate and follow existing naming conventions.

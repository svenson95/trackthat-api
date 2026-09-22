# TrackThat API

REST API for the [TrackThat mobile app](https://github.com/svenson95/trackthat-mobile-app).

The API provides Google authentication and manages application data such as users, workouts and workout logs.

## Tech Stack

* Java
* Spring Boot
* Spring Data MongoDB
* MongoDB
* Maven
* MapStruct
* JWT authentication
* Google authentication
* Testcontainers
* JUnit

## Requirements

* Java 21
* Docker
* Maven Wrapper

Docker is required for integration tests using Testcontainers.

## Development

Start the application locally:

```bash
./mvnw spring-boot:run
```

The application uses the local Spring profile configuration where required.

## Build

Build the application:

```bash
./mvnw clean package
```

The executable Spring Boot JAR is written to:

```text
target/
```

## Testing

Run the complete test suite:

```bash
./mvnw clean verify
```

Integration tests use Testcontainers to start the required MongoDB instance automatically.

Tests are also executed automatically through GitHub Actions for pushes and pull requests targeting the main branch.

## Environment Variables

The application requires environment-specific configuration.

```text
GOOGLE_CLIENT_ID=
JWT_SECRET=
MONGODB_URI=
PORT=
```

Sensitive values should not be committed to the repository.

## API

The application exposes REST endpoints for:

* Google authentication
* User management
* Workout management
* Workout log management

## Hosting

The production API is hosted on Fly.io.

Deployments are handled through GitHub Actions after the test suite has completed successfully.

## Versions

as of September 22, 2026:

| Technology          | Version  |
| ------------------- | -------- |
| Java                | `21`     |
| Spring Boot         | `3.5.16` |
| JJWT                | `0.13.0` |
| MapStruct           | `1.6.3`  |
| Google API Client   | `2.9.1`  |
| Google Auth Library | `1.52.0` |

Check the local Java and Maven versions:

```bash
java --version
./mvnw --version
```

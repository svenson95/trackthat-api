# TrackThat api

Backend API for the [TrackThat mobile app](https://github.com/svenson95/trackthat-mobile-app).

## Tech Stack

- Java
- Spring Boot
- Maven
- MongoDB (Cloud)
- REST API

## Hosting

Hosted on Fly.io.

## Requirements

- Java JDK
- Maven

## Development

To run this project locally:

```bash
mvnw.cmd spring-boot:run
```

## Environment Variables

```
GOOGLE_CLIENT_ID=your-google-client-id
JWT_SECRET=your-secret
MONGODB_URI=mongodb-secret
PORT=0000
```

## Versions

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.5 |

Check versions locally:

```bash
java --version
./mvnw -v

# Video Game DB Application

The core Spring Boot application providing a REST API for managing video games. Built with Jersey (JAX-RS), Spring JDBC,
H2 in-memory database, and integrated Swagger UI for API documentation.

## Overview

This module contains the main application logic and REST API endpoints for the VideoGameDB project. It exposes a
complete CRUD interface for video game management with support for both JSON and XML representations.

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/ai/tester/
│   │   │   ├── app/                 # Application entry point and configuration
│   │   │   │   ├── App.java         # Spring Boot application class
│   │   │   │   ├── AppResourceConfig.java  # JAX-RS + Swagger configuration
│   │   │   │   └── WebConfig.java   # Spring MVC web configuration
│   │   │   ├── config/              # Security and infrastructure config
│   │   │   │   └── SecurityConfig.java     # HTTP Basic Auth configuration
│   │   │   ├── model/               # Data models
│   │   │   │   ├── VideoGame.java   # Video game entity
│   │   │   │   ├── VideoGameList.java     # Wrapper for list responses
│   │   │   │   └── LocalDateAdapter.java  # XML date serialization
│   │   │   └── resource/            # JAX-RS endpoints
│   │   │       └── VideoGameResource.java # CRUD endpoints for video games
│   │   └── resources/
│   │       ├── application.properties    # Spring Boot configuration
│   │       ├── schema.sql               # H2 database schema
│   │       └── static/                  # Swagger UI (webjar)
│   └── test/
│       └── java/                    # Unit tests
└── pom.xml
```

## Tech Stack

| Component           | Version |
|---------------------|---------|
| Java                | 21      |
| Spring Boot         | 3.5.3   |
| Jersey (JAX-RS)     | 3.1.x   |
| Spring JDBC         | 6.2.x   |
| H2 Database         | 2.3.232 |
| Swagger UI (webjar) | 5.18.2  |
| Spring Security     | 6.x     |
| Jackson             | 2.18.x  |

## Running the Application

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+**

### Start the Server

```bash
cd app
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Build Executable JAR

```bash
mvn clean package
java -jar target/videogamedb-app-1.0-SNAPSHOT-exec.jar
```

## API Documentation

Once running, access the interactive API documentation:

| Resource                                   | Description                           |
|--------------------------------------------|---------------------------------------|
| **http://localhost:8080/swagger-ui.html**  | Swagger UI (interactive API explorer) |
| **http://localhost:8080/app/openapi.json** | OpenAPI specification (JSON)          |
| **http://localhost:8080/h2-console**       | H2 database console                   |

## REST API Endpoints

All endpoints are prefixed with `/app` and support both **JSON** and **XML** media types.

### Video Games

| Method | Endpoint                        | Description                   |
|--------|---------------------------------|-------------------------------|
| GET    | `/app/videogames`               | Get all video games           |
| GET    | `/app/videogames/{videoGameId}` | Get a video game by ID        |
| POST   | `/app/videogames`               | Create a new video game       |
| PUT    | `/app/videogames/{videoGameId}` | Update an existing video game |
| DELETE | `/app/videogames/{videoGameId}` | Delete a video game           |

### Request/Response Example

**GET** `/app/videogames`

Response (JSON):

```json
{
  "videoGames": [
    {
      "id": 1,
      "name": "The Legend of Zelda",
      "releaseDate": "1986-02-21",
      "reviewScore": 95,
      "category": "Adventure",
      "rating": "E"
    }
  ]
}
```

## Authentication

The API is protected with **HTTP Basic Authentication**:

```
Username: test
Password: test
```

Include credentials in API requests:

```bash
curl -u test:test http://localhost:8080/app/videogames
```

## Database

The application uses an **H2 in-memory database** configured in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.h2.console.enabled=true
```

### Database Schema

The schema is initialized from `src/main/resources/schema.sql` on startup:

```sql
CREATE TABLE VIDEOGAME
(
    ID           INT          PRIMARY KEY,
    NAME         VARCHAR(100) DEFAULT '',
    RELEASED_ON  DATE,
    REVIEW_SCORE INT,
    CATEGORY     VARCHAR(100),
    RATING       VARCHAR(100)
);
```

Access the H2 console at: **http://localhost:8080/h2-console**

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)

## Configuration

Key properties in `application.properties`:

```properties
# Server
server.port=8080
logging.level.org.springframework=INFO
# Jersey - JAX-RS endpoints mounted at /app
spring.jersey.type=filter
spring.jersey.application-path=/app
spring.jersey.init.jersey.config.servlet.filter.forwardOn404=true
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
spring.h2.console.enabled=true
```

## Key Classes

### `App.java`

Spring Boot application class with `@SpringBootApplication` annotation. Scans `com.ai.tester` packages for components.

### `AppResourceConfig.java`

Configures Jersey (JAX-RS):

- Registers `VideoGameResource` endpoint
- Configures Jackson for JSON serialization with `LocalDate` ISO format
- Sets up JAXB for XML serialization
- Configures Swagger/OpenAPI documentation

### `VideoGameResource.java`

JAX-RS endpoint (`@Path("/videogames")`) providing CRUD operations for video games. Uses `NamedParameterJdbcTemplate`
for database access.

### `SecurityConfig.java`

Spring Security configuration enabling HTTP Basic Authentication.

### Models

- `VideoGame.java` — Entity representing a video game
- `VideoGameList.java` — Wrapper for list responses
- `LocalDateAdapter.java` — XML serialization adapter for `LocalDate`

## Testing

Unit tests are located in `tests/` module and follow the pattern `*UnitTest.class`.

Run tests:

```bash
mvn test
```

## Related Documentation

- Parent project: [README.md](../README.md)
- Test automation framework: [tests/README.md](../tests/README.md)

## Features

✅ RESTful API with CRUD operations  
✅ JSON and XML support  
✅ HTTP Basic Authentication  
✅ Interactive Swagger UI  
✅ H2 in-memory database  
✅ Spring JDBC for database access  
✅ Comprehensive OpenAPI documentation  
✅ Component testable with Spring Boot Test

## Troubleshooting

### Port Already in Use

If port 8080 is already occupied, change it in `application.properties`:

```properties
server.port=8081
```

### H2 Console Not Accessible

Ensure the H2 console is enabled in `application.properties`:

```properties
spring.h2.console.enabled=true
```

### Authentication Errors

Verify you're using the correct credentials:

- **Username**: `test`
- **Password**: `test`

Make sure your HTTP client includes Basic Auth headers (most tools handle this automatically with `-u` flag).

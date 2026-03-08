# VideoGameDB

## Tech Stack

| Component              | Version       |
|------------------------|---------------|
| Java                   | 21            |
| Spring Boot            | 3.5.3         |
| Jersey (JAX-RS)        | Jakarta EE 10 |
| H2 Database            | 2.3.232       |
| swagger-jaxrs2-jakarta | 2.2.28        |
| Swagger UI (webjar)    | 5.18.2        |

## Requirements

- **Java 21** or higher
- **Maven 3.9+**

## Running the Application

```bash
mvn spring-boot:run
```

## Endpoints

Once running, the application is available at:

| URL                                   | Description                       |
|---------------------------------------|-----------------------------------|
| http://localhost:8080/swagger-ui.html | Swagger UI (interactive API docs) |
| http://localhost:8080/h2-console      | H2 in-memory database console     |

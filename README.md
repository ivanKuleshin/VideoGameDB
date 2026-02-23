# VideoGameDB

Video Game Database application with API endpoints that support **JSON** and **XML**

This application was developed to support my Udemy courses:

- [REST Assured Fundamentals](https://www.udemy.com/course/rest-assured-fundamentals/)
- [Gatling Fundamentals for Stress, Load & Performance Testing](https://www.udemy.com/course/gatling-fundamentals/)

## Tech Stack

| Component              | Version       |
|------------------------|---------------|
| Java                   | 21            |
| Spring Boot            | 3.4.3         |
| Jersey (JAX-RS)        | Jakarta EE 10 |
| H2 Database            | 2.3.232       |
| swagger-jaxrs2-jakarta | 2.2.28        |
| Swagger UI (webjar)    | 5.18.2        |

## Requirements

- **Java 21** or higher
- **Maven 3.9+** or **Gradle 8+**

## Running the Application

```bash
# Maven
mvn spring-boot:run

# Gradle
./gradlew bootRun
```

## Endpoints

Once running, the application is available at:

| URL                                   | Description                       |
|---------------------------------------|-----------------------------------|
| http://localhost:8080/swagger-ui.html | Swagger UI (interactive API docs) |
| http://localhost:8080/h2-console      | H2 in-memory database console     |

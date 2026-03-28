# WireMock Dependency Reference

## Spring Boot 3.x — Recommended Artifact

Zero-boilerplate `@EnableWireMock` / `@InjectWireMock` integration for Spring Boot 3.

```xml
<dependency>
    <groupId>org.wiremock.integrations</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>3.2.0</version>
    <scope>test</scope>
</dependency>
```

`wiremock-standalone` is pulled in transitively — no extra artifact needed.

---

## Spring Boot 3.x — Standalone (manual lifecycle)

Use when you want explicit server lifecycle control or a non-Spring unit test.

```xml
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>3.9.2</version>
    <scope>test</scope>
</dependency>
```

---

## Spring Boot 2.x (legacy, avoid for new projects)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-wiremock</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Gradle (Spring Boot 3 / standalone)

```groovy
testImplementation 'org.wiremock:wiremock-standalone:3.9.2'
```

---

## Version Compatibility Matrix

| Spring Boot | WireMock artifact               | Min version |
|-------------|---------------------------------|-------------|
| 3.2+        | wiremock-spring-boot            | 3.2.0       |
| 3.0–3.1     | wiremock-standalone             | 3.5.0       |
| 2.7.x       | spring-cloud-contract-wiremock  | 3.1.x       |
| 2.5–2.6     | spring-cloud-contract-wiremock  | 3.0.x       |

> Keep `wiremock-standalone` on the latest 3.x patch — it bundles all transitive deps (Jetty, Jackson)
> and avoids classpath conflicts with your application's own versions.


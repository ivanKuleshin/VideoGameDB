---
name: wiremock-testing
description: >-
  Expert guide for WireMock HTTP stubbing and verification in Spring Boot 3 component tests.
  Use this skill whenever a Spring Boot service makes outbound HTTP calls to an external system
  (REST API, third-party service, downstream microservice) and the user needs to control what that
  service returns in tests — even if they don't mention WireMock by name. Trigger on: stubbing or
  faking external/downstream REST APIs in tests, setting up WireMock servers, verifying outbound
  HTTP interactions, simulating 5xx errors or network faults, testing retry or circuit-breaker
  behaviour, injecting fault responses for third-party APIs, investigating third-party HTTP calls in
  an application and implementing test stubs for them, or any request to avoid hitting real external
  APIs during component tests. Also trigger on: @EnableWireMock, WireMockExtension, WireMockServer,
  "stub the response", "fake the API", "intercept outbound calls", "service virtualisation".
  Do NOT trigger for: Mockito mocks of JPA repositories or in-process beans (@MockBean, @Mock,
  @ExtendWith(MockitoExtension.class)), testing your own endpoints with REST Assured, H2 database
  setup, Kafka consumer tests, or anything that does not involve outbound HTTP network calls.
---

# WireMock Testing in Spring Boot

Comprehensive guide for implementing WireMock stubs and verifications in Spring Boot 3 component tests,
aligned with this project's conventions (`BaseTest` / `ComponentTest` split, `AllureSteps` wrapping,
`@ActiveProfiles("test")` setup, and Lombok-first style).

---

## When to Use WireMock

Use WireMock when your service makes outbound HTTP calls (RestTemplate, WebClient, Feign, RestClient, etc.)
and you need:

- **Deterministic responses** without spinning up real downstream services
- **Fault injection** — timeouts, 5xx errors, network resets, malformed responses
- **Call verification** — assert that specific requests were (or weren't) made with the right shape
- **Stateful flows** — simulate polling, retries, or multi-step protocols

For purely in-process logic, plain Mockito is simpler. WireMock shines at the HTTP boundary.

---

## Quick-Start Checklist

1. Add the Maven dependency (see [`references/dependencies.md`](references/dependencies.md)).
2. Externalise the downstream base URL to a Spring property in `application-test.properties`.
3. Annotate the `*BaseTest` class with `@EnableWireMock` and inject `WireMockServer` via `@InjectWireMock`.
4. Call `wireMockServer.resetAll()` in `@BeforeEach`.
5. Place stub response bodies in `src/test/resources/wiremock/__files/*.json`.
6. Register stubs in `prepare*()` helpers in `*BaseTest`, referencing bodies with `.withBodyFile(...)`. Wrap each in `AllureSteps.logStep()`.
7. Define a `verifyStub(int count, String url, Map<String,String> headers, String jsonPath)` helper in `*BaseTest` that wraps `verify(...)` in `AllureSteps.logStep()`.
8. In `*ComponentTest`, call `prepare*()` and `verifyStub()` — no WireMock API imports needed.

---

## Dependency

Add to `tests/pom.xml` (Spring Boot 3 zero-boilerplate integration):

```xml
<dependency>
    <groupId>org.wiremock.integrations</groupId>
    <artifactId>wiremock-spring-boot</artifactId>
    <version>3.2.0</version>
    <scope>test</scope>
</dependency>
```

Full coordinates and version matrix: [`references/dependencies.md`](references/dependencies.md).

---

## Core Patterns

### Pattern 1 — `@EnableWireMock` (recommended, Spring Boot 3)

Annotate the `*BaseTest` class. The integration auto-starts the server, injects its URL into the Spring
property, and tears it down cleanly after the test class.

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@EnableWireMock({
    @ConfigureWireMock(name = "payments-api", property = "payments.base-url")
})
@Log4j2
public abstract class PaymentBaseTest extends ApiBaseTest {

    @InjectWireMock("payments-api")
    protected WireMockServer paymentsMock;

    @BeforeEach
    void resetWireMock() {
        paymentsMock.resetAll();
    }

    protected void prepareChargeStub(String responseBody) {
        AllureSteps.logStep(log, "Stub POST /charge → 200",
            () -> paymentsMock.stubFor(post(urlEqualTo("/charge"))
                .willReturn(okJson(responseBody))));
    }
}
```

Key points:
- `property` must match the Spring property your HTTP client reads for its base URL.
- Multiple `@ConfigureWireMock` entries support multiple downstream services.
- Keep all `stubFor(...)` calls in `prepare*()` helpers in `*BaseTest`; keep `@Test` in `*ComponentTest`.

### Pattern 2 — `WireMockExtension` (JUnit 5, non-Spring)

Useful for lightweight unit tests that don't need the full Spring context.

```java
class ExternalClientTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    @Test
    void returnsDataFromRemoteApi() {
        wireMock.stubFor(get("/items").willReturn(okJson("[{\"id\":1}]")));
        String baseUrl = wireMock.baseUrl();
        // pass baseUrl into the client under test
    }
}
```

### Pattern 3 — Standalone (shared across a suite)

```java
private static WireMockServer server;

@BeforeAll
static void startServer() {
    server = new WireMockServer(wireMockConfig().dynamicPort());
    server.start();
    WireMock.configureFor("localhost", server.port());
}

@BeforeEach
void resetStubs() { server.resetAll(); }

@AfterAll
static void stopServer() { server.stop(); }
```

---

## Spring Boot Property Injection

Externalise the downstream base URL so WireMock can override it during tests.

`application-test.properties`:
```properties
payments.base-url=https://real-payments.example.com
```

`PaymentsClient.java`:
```java
@Value("${payments.base-url}")
private String baseUrl;
```

With `@ConfigureWireMock(property = "payments.base-url")` the server URL `http://localhost:<port>` is
injected automatically — no manual `@TestPropertySource` needed.

---

## Stub Building

Full DSL reference: [`references/stubs.md`](references/stubs.md).

**Always put stub response bodies in `__files/` JSON files** — never in Java string variables or constants.
Place them under `src/test/resources/wiremock/__files/` and reference them with `.withBodyFile(...)`.

```
src/test/resources/
  wiremock/
    __files/
      charge-approved-response.json   ← {"transactionId":"tx-999","status":"APPROVED"}
```

```java
// POST stub loading body from __files/
paymentsMock.stubFor(post(urlEqualTo("/charge"))
    .withRequestBody(matchingJsonPath("$.amount"))
    .withHeader("Content-Type", containing("application/json"))
    .willReturn(aResponse()
        .withStatus(201)
        .withHeader("Content-Type", "application/json")
        .withBodyFile("charge-approved-response.json")));

// Simulate timeout — exceed your client's read timeout
paymentsMock.stubFor(get(urlEqualTo("/slow"))
    .willReturn(aResponse().withFixedDelay(5000).withStatus(200)));

// Network-level fault
paymentsMock.stubFor(get(urlEqualTo("/broken"))
    .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

// 503 shortcut
paymentsMock.stubFor(get(urlEqualTo("/inventory/SKU-001"))
    .willReturn(serviceUnavailable()));
```

---

## Verifying Outbound Calls

Define a **`verifyStub()` helper** in `*BaseTest` — never call `wireMock.verify(...)` inline in
`*ComponentTest` methods. This keeps the component test readable and wraps Allure logging once:

```java
// In *BaseTest:
protected void verifyStub(int count, String url, Map<String, String> headers, String requestBodyJsonPath) {
    AllureSteps.logStep(log, "Verify " + count + " request(s) to " + url, () -> {
        RequestPatternBuilder pattern = RequestPatternBuilder.newRequestPattern()
            .withUrl(url);
        headers.forEach((key, value) ->
            pattern.withHeader(key, matching(value)));
        if (requestBodyJsonPath != null) {
            pattern.withRequestBody(matchingJsonPath(requestBodyJsonPath));
        }
        wireMockServer.verify(exactly(count), pattern);
    });
}

// In *ComponentTest — clean, no WireMock API visible:
verifyStub(1, "/v1/charge",
    Map.of("Authorization", "Bearer .+"),
    "$.amount");
```

Full verification DSL: [`references/stubs.md`](references/stubs.md#verification-dsl).

---

## Fault & Resilience Testing

```java
// 503 to test circuit-breaker / fallback
paymentsMock.stubFor(get(urlEqualTo("/inventory"))
    .willReturn(serviceUnavailable()));

// Retry scenario — fail twice, then succeed
paymentsMock.stubFor(get(urlEqualTo("/inventory"))
    .inScenario("retry-then-succeed").whenScenarioStateIs(STARTED)
    .willReturn(serviceUnavailable()).willSetStateTo("first-retry"));

paymentsMock.stubFor(get(urlEqualTo("/inventory"))
    .inScenario("retry-then-succeed").whenScenarioStateIs("first-retry")
    .willReturn(serviceUnavailable()).willSetStateTo("ready"));

paymentsMock.stubFor(get(urlEqualTo("/inventory"))
    .inScenario("retry-then-succeed").whenScenarioStateIs("ready")
    .willReturn(okJson("{\"stock\":5}")));
```

---

## JSON Mapping Files

For long/complex stubs, place them in `src/test/resources/wiremock/mappings/` — WireMock loads them
automatically, keeping test code free of large inline strings.

```
src/test/resources/
  wiremock/
    mappings/
      get-product-200.json
    __files/
      product-response.json    ← referenced by bodyFileName
```

`get-product-200.json`:
```json
{
  "request": { "method": "GET", "url": "/products/42" },
  "response": {
    "status": 200,
    "bodyFileName": "product-response.json",
    "headers": { "Content-Type": "application/json" }
  }
}
```

---

## Project Conventions

When adding WireMock to this project, follow these rules:

1. **Class split** — WireMock setup and `prepare*()` / `verifyStub()` helpers go in `*BaseTest`; all
   `@Test` methods go in the paired `*ComponentTest`. Surefire only picks up `**/*ComponentTest`.
2. **AllureSteps wrapping** — every `stubFor(...)` call in a `prepare*()` helper must be wrapped in
   `AllureSteps.logStep()`. Verification is always done through the `verifyStub()` helper (rule 4), which
   handles Allure wrapping internally — `*ComponentTest` never calls `wireMock.verify(...)` directly.
3. **Reset in `@BeforeEach`** — call `wireMockServer.resetAll()` in `@BeforeEach` to prevent stub state
   from leaking between tests.
4. **`verifyStub()` helper** — define `verifyStub(int count, String url, Map<String, String> headers,
   String requestBodyJsonPath)` in `*BaseTest`. Wrap the `verify(...)` call inside
   `AllureSteps.logStep()` once in this method. `*ComponentTest` calls `verifyStub(...)` — it never
   imports or calls WireMock verify APIs directly.
5. **Response bodies in `__files/`** — stub response bodies go in
   `src/test/resources/wiremock/__files/*.json` and are referenced with `.withBodyFile("name.json")`.
   Never put response bodies in Java string variables, constants, fixture enums, or any in-code form.
   This keeps large payloads readable, diffable, and editable outside Java source.
6. **Property convention** — externalise all downstream base URLs in `application-test.properties`; use the
   `property` attribute on `@ConfigureWireMock` to override them during tests.
7. **`@Log4j2`** — add `@Log4j2` to every `*BaseTest` and `*ComponentTest` class; pass `log` to every
   `AllureSteps` call.
8. **`@TmsLink`** — add `@TmsLink("XSP-NNN")` on every `@Test` method to link to the Jira/Xray test case.

---

## Reference Files

| File | When to read |
|---|---|
| [`references/dependencies.md`](references/dependencies.md) | Adding WireMock to `pom.xml` / Gradle, version matrix |
| [`references/stubs.md`](references/stubs.md) | Full stub DSL, all matchers, response builders, verification DSL |


# WireMock Stub DSL Reference

Full reference for building stubs and verifying requests with the WireMock 3.x Java DSL.

All examples assume:
```java
import static com.github.tomakehurst.wiremock.client.WireMock.*;
```

---

## URL Matchers

```java
urlEqualTo("/exact/path")                  // exact path + query string
urlPathEqualTo("/path/only")               // ignores query string
urlPathMatching("/orders/[0-9]+")          // regex on path
urlMatching("/items\\?category=.*")        // regex on full URL
anyUrl()                                   // matches everything
```

---

## Request Matchers

### HTTP Method

```java
get(...)      post(...)     put(...)
delete(...)   patch(...)    head(...)
options(...)  any(...)      request("CUSTOM", ...)
```

### Headers

```java
.withHeader("Accept", equalTo("application/json"))
.withHeader("X-Api-Key", matching("[A-Za-z0-9]{32}"))
.withHeader("Authorization", containing("Bearer"))
.withoutHeader("X-Sensitive")
```

### Query Parameters

```java
.withQueryParam("page", equalTo("2"))
.withQueryParam("sort", matching("asc|desc"))
```

### Request Body

```java
// Plain text / XML
.withRequestBody(equalTo("exact body"))
.withRequestBody(containing("partial match"))
.withRequestBody(matching("regex.*"))
.withRequestBody(equalToXml("<order><id>1</id></order>"))

// JSON — structural equality (ignores key order and whitespace)
.withRequestBody(equalToJson("{\"id\":1,\"name\":\"test\"}"))

// JSONPath expression
.withRequestBody(matchingJsonPath("$.orderId"))
.withRequestBody(matchingJsonPath("$.amount", equalTo("9.99")))
.withRequestBody(matchingJsonPath("$.items[*].sku", containing("SKU-42")))

// Multipart
.withMultipartRequestBody(aMultipart("file")
    .withHeader("Content-Type", containing("text/plain")))
```

### Cookies

```java
.withCookie("session", matching("[a-f0-9]{32}"))
```

---

## Response Builders

### Status Shortcuts

```java
ok()                  // 200, empty body
okJson("...")         // 200, Content-Type: application/json
okXml("...")          // 200, Content-Type: application/xml
created()             // 201
noContent()           // 204
notFound()            // 404
serverError()         // 500
serviceUnavailable()  // 503
unauthorized()        // 401
forbidden()           // 403
badRequest()          // 400
```

### `aResponse()` Builder (full control)

```java
aResponse()
    .withStatus(202)
    .withHeader("Content-Type", "application/json")
    .withHeader("X-Request-Id", "{{randomValue type='UUID'}}")  // templating
    .withBody("{\"queued\":true}")
    .withFixedDelay(200)                         // fixed delay in ms
    .withLogNormalRandomDelay(100, 0.1)          // realistic jitter
```

### Body from File (`__files/` directory)

```java
aResponse()
    .withStatus(200)
    .withBodyFile("responses/product-42.json")
    .withHeader("Content-Type", "application/json")
```

Place files under `src/test/resources/wiremock/__files/`.

### Faults (Network-Level Errors)

```java
aResponse().withFault(Fault.EMPTY_RESPONSE)
aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)
aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)
aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE)
```

---

## Priorities and Stub Override

Lower number = higher priority. Default priority is 5.

```java
// Catch-all fallback
stubFor(get(urlPathMatching("/products/.*"))
    .atPriority(10)
    .willReturn(notFound()));

// Specific override wins
stubFor(get(urlEqualTo("/products/42"))
    .atPriority(1)
    .willReturn(okJson("{\"id\":42,\"name\":\"Widget\"}")));
```

---

## Stateful Scenarios

Model multi-step interactions such as polling, retries, or wizard-style flows:

```java
stubFor(get(urlEqualTo("/status"))
    .inScenario("polling")
    .whenScenarioStateIs(STARTED)
    .willReturn(okJson("{\"status\":\"PENDING\"}"))
    .willSetStateTo("processing"));

stubFor(get(urlEqualTo("/status"))
    .inScenario("polling")
    .whenScenarioStateIs("processing")
    .willReturn(okJson("{\"status\":\"DONE\"}")));

// Reset a single scenario mid-test
wireMock.resetScenario("polling");
```

---

## Response Templating

Echo request data back in the response body using Handlebars syntax.

```java
// Enable globally on WireMockServer
new WireMockServer(options().dynamicPort()
    .extensions(new ResponseTemplateTransformer(true)));

// Enable per stub
aResponse()
    .withBody("Hello, {{jsonPath request.body '$.name'}}!")
    .withTransformers("response-template")
```

Common helpers:
- `{{request.url}}`, `{{request.method}}`
- `{{request.headers.X-Foo}}`
- `{{request.body}}`, `{{jsonPath request.body '$.field'}}`
- `{{randomValue type='UUID'}}`, `{{randomValue type='ALPHANUMERIC' length=10}}`
- `{{now format='yyyy-MM-dd'}}`

---

## Verification DSL

```java
// Exactly once
verify(exactly(1), postRequestedFor(urlEqualTo("/payments")));

// At least N times
verify(moreThan(2), getRequestedFor(urlPathMatching("/retry/.*")));

// Never called
verify(never(), deleteRequestedFor(anyUrl()));

// With header and body checks (same matchers as stubFor)
verify(postRequestedFor(urlEqualTo("/orders"))
    .withHeader("Authorization", matching("Bearer .+"))
    .withRequestBody(matchingJsonPath("$.items[0].sku")));

// Retrieve recorded requests for manual assertion
List<LoggedRequest> requests = wireMock.findAll(postRequestedFor(urlEqualTo("/charge")));
assertThat(requests).hasSize(1);
assertThat(requests.get(0).getBodyAsString()).contains("\"amount\":\"9.99\"");
```

---

## Global Reset

```java
wireMock.resetAll();        // clears stubs, recorded requests, and scenario states
wireMock.resetRequests();   // clears recorded requests only
wireMock.resetMappings();   // clears stubs only
wireMock.resetScenarios();  // resets all scenario states to STARTED
```


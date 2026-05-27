# Code Review Checklist Reference

## Quick Scan Checklist (5 minutes)

- [ ] Clear class and method naming (PascalCase, camelCase with verb prefixes)
- [ ] No wildcard imports
- [ ] Proper access modifiers (private by default)
- [ ] Methods are concise (<20 lines typical)
- [ ] No obvious duplicated code
- [ ] Proper exception handling (try-catch, throw RuntimeException)
- [ ] Constructor injection used (not field @Autowired)

---

## Comprehensive Checklist - Code Quality

### Naming Conventions

- [ ] **Classes**: PascalCase, descriptive, no abbreviations
    - ✅ `GameService`, `ApiBaseTest`, `VideoGameResponse`
    - ❌ `GS`, `GT`, `Resp`

- [ ] **Methods**: camelCase with verb prefixes
    - ✅ `prepareGameRequest()`, `createGame()`, `validateInput()`, `getGameById()`
    - ❌ `game()`, `request()`, `test()`

- [ ] **Variables**: camelCase, descriptive, no single letters (except i,j,k in loops)
    - ✅ `gameRepository`, `testData`, `expectedResponse`
    - ❌ `g`, `r`, `x`

- [ ] **Constants**: UPPER_SNAKE_CASE, static final
    - ✅ `DEFAULT_GAME_PRICE`, `MAX_GAME_YEAR`
    - ❌ `defaultPrice`, `MaxYear`

### Code Organization

- [ ] Single import statements (no wildcard imports)
- [ ] Imports organized by package hierarchy
- [ ] No commented-out code
- [ ] No TODOs without context
- [ ] No dead code or unused imports
- [ ] Proper class structure (fields, constructor, methods)

### SOLID Principles

- [ ] **Single Responsibility**: Each class has one reason to change
- [ ] **Open/Closed**: Open for extension, closed for modification
- [ ] **Liskov Substitution**: Subtypes can replace parent types
- [ ] **Interface Segregation**: No fat interfaces
- [ ] **Dependency Inversion**: Depend on abstractions, not concretions

### Code Complexity

- [ ] Methods are concise (>50 lines is a red flag)
- [ ] Cyclomatic complexity is low (<10 per method)
- [ ] No deeply nested conditionals (max 3 levels)
- [ ] Proper use of helper methods
- [ ] Clear, logical flow

### Exception Handling

- [ ] No `throws` declarations (use try-catch)
- [ ] All checked exceptions handled
- [ ] RuntimeException thrown from catch blocks
- [ ] Meaningful error messages
- [ ] Logging includes context

### Immutability & Final

- [ ] Final used on references that don't change
- [ ] Constructor injection uses final fields
- [ ] Immutable objects where appropriate
- [ ] No unnecessary field mutation

---

## Comprehensive Checklist - Spring Boot & Framework

### Dependency Injection

- [ ] Constructor injection used (not field @Autowired)
- [ ] All dependencies final and passed to constructor
- [ ] No circular dependencies
- [ ] Proper @Configuration classes
- [ ] @Bean methods are appropriately scoped

### Component Stereotypes

- [ ] @Service for business logic
- [ ] @Repository for data access
- [ ] @Component for generic components
- [ ] @Controller/@RestController for web layer
- [ ] Not overused (don't use @Component for service)

### Test Configuration

- [ ] @SpringBootTest with webEnvironment = RANDOM_PORT
- [ ] @ActiveProfiles("test") for test profile
- [ ] Test configuration uses in-memory H2
- [ ] No hardcoded ports or URLs
- [ ] Proper test context cleanup

### REST API Design

- [ ] Correct HTTP methods (GET, POST, PUT, DELETE, PATCH)
- [ ] Appropriate status codes (200, 201, 204, 400, 404, 500)
- [ ] Request validation with @Valid
- [ ] Proper DTOs for requests/responses
- [ ] Error responses follow standard format
- [ ] No entity classes exposed directly

### Data Access

- [ ] Spring Data repositories used properly
- [ ] @Query annotations for complex queries
- [ ] Named parameters (not positional)
- [ ] Transaction boundaries correct
- [ ] No N+1 query problems
- [ ] JdbcTemplate used for H2 in tests

---

## Comprehensive Checklist - Test Quality

### Test Structure

- [ ] Test extends ApiBaseTest or appropriate base
- [ ] @Log4j2 annotation present
- [ ] @SpringBootTest configured properly
- [ ] @ActiveProfiles("test") applied
- [ ] Given/When/Then structure evident
- [ ] Clear test isolation
- [ ] No test interdependencies

### Test Content

- [ ] No hardcoded test values
- [ ] AllureSteps used for step reporting
- [ ] CommonSteps used for verification logic
- [ ] Fixtures used for test data (VideoGameTestDataFixtures)
- [ ] Database verification included
- [ ] Response content validated
- [ ] Both positive and negative cases covered

### Naming & Documentation

- [ ] Descriptive test method names (e.g., `getAllGamesPositiveTest`)
- [ ] @DisplayName with human-readable description
- [ ] @TmsLink annotations link to Jira tickets
- [ ] No hardcoded endpoint names in Allure steps
- [ ] No expected results in @DisplayName

### Assertions

- [ ] AssertJ used for assertions
- [ ] Soft assertions for multiple checks
- [ ] Meaningful assertion messages
- [ ] No null checks when Optional available
- [ ] No `assertTrue(condition == true)`
- [ ] Proper use of `contains`, `containsExactly`, etc.

### AllureSteps Usage

- [ ] `AllureSteps.logStep()` for void operations
- [ ] `AllureSteps.logStepAndReturn()` for operations with return value
- [ ] Step descriptions are clear and high-level
- [ ] No hardcoded data in step descriptions
- [ ] Descriptive runnable/supplier blocks

### Test Data

- [ ] Fixtures used when inserting data
- [ ] Test data fetched from DB as needed
- [ ] No magic numbers or strings
- [ ] Builder pattern used for complex objects
- [ ] Reusable data builders (e.g., `prepareGameRequest()`)

### Database Testing

- [ ] H2DbClient used for database operations
- [ ] JdbcTemplate queries properly parameterized
- [ ] Database state verified after operations
- [ ] Test data cleaned up (or fixtures handle it)
- [ ] Transaction rollback verified for failures

---

## Comprehensive Checklist - Security

### Input Validation

- [ ] All API endpoints validate input with @Valid
- [ ] Custom validators for complex rules
- [ ] Meaningful validation error messages
- [ ] No trusting of client-side validation

### Data Protection

- [ ] No sensitive data in logs
- [ ] Passwords/secrets not in code
- [ ] No credentials in configuration files
- [ ] Proper use of Spring Security
- [ ] CORS configured appropriately

### SQL Safety

- [ ] Parameterized queries (no string concatenation)
- [ ] No dynamic SQL injection vulnerabilities
- [ ] Named parameters in @Query
- [ ] Spring Data repositories prevent SQL injection

### Authentication & Authorization

- [ ] Spring Security properly configured
- [ ] Method-level security where needed
- [ ] Role-based access control
- [ ] Authorization headers validated

---

## Comprehensive Checklist - Performance

### Database

- [ ] No N+1 query problems
- [ ] Fetch strategies appropriate (LAZY vs EAGER)
- [ ] Indexes used for large datasets
- [ ] Pagination implemented for large results
- [ ] Connection pooling configured

### Memory

- [ ] No unnecessary object creation in loops
- [ ] Stream operations don't cause memory issues
- [ ] Large collections don't accumulate
- [ ] Resources properly closed (@Transactional, try-with-resources)

### Concurrency

- [ ] Immutable objects used where appropriate
- [ ] No race conditions
- [ ] Proper synchronization if needed
- [ ] Thread pools configured properly

---

## Severity Classification

| Severity     | Examples                                                       | Action                      |
|--------------|----------------------------------------------------------------|-----------------------------|
| **Critical** | SQL injection, hardcoded credentials, data loss, test failures | Must fix before merge       |
| **High**     | N+1 queries, infinite loops, missing validation, broken tests  | Should fix before merge     |
| **Medium**   | Naming issues, code duplication, complexity, style violations  | Should fix, but can discuss |
| **Low**      | Minor improvements, preferred patterns, documentation          | Nice to have                |
| **Info**     | Suggestions, learning opportunities, best practice notes       | FYI only                    |

---

## Review Template

Use this template for providing code review feedback:

```
## Code Review: [FileName]

### Summary
[1-2 sentence overall assessment]

### Statistics
- Lines of code: X
- Methods/classes: X
- Cyclomatic complexity: X
- Test coverage: X%

### Issues Found
- [ ] Critical: X
- [ ] High: X
- [ ] Medium: X
- [ ] Low: X
- [ ] Info: X

### Detailed Findings

#### 1. [Category] - [Issue Title]
- **Severity**: [Level]
- **Location**: Line X
- **Current**: [Code snippet]
- **Issue**: [Explanation]
- **Recommendation**: [Solution with example]
- **Reference**: [Link to docs/standards if applicable]

### Strengths
- ✅ [Positive finding 1]
- ✅ [Positive finding 2]

### Next Steps
- [ ] Action 1
- [ ] Action 2

### Questions for Author
- Question 1?
- Question 2?
```

---

## Review Efficiency Tips

1. **Start with high-level structure** - Is the overall design sound?
2. **Check naming** - Are names clear and consistent?
3. **Look for duplicates** - Extract common code?
4. **Verify test structure** - Are tests well-organized?
5. **Check for anti-patterns** - Are common mistakes present?
6. **Validate standards** - Does it match project conventions?
7. **Assess SOLID** - Could design be improved?
8. **Review security** - Are there security issues?

---

## Common Questions to Ask

- What is this method's responsibility? (Single Responsibility)
- Could this be a separate method? (Extract Method)
- Is this duplicated elsewhere? (DRY - Don't Repeat Yourself)
- Is the test name descriptive? (Naming)
- Could this fail in production? (Edge cases)
- Is this testable? (Testability)
- Would a junior developer understand this? (Clarity)
- Is this efficient? (Performance)
- Is this secure? (Security)
- Is this maintainable? (Maintainability)


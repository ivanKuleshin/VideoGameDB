---
name: code-review
description: >-
  Code review expert for Java Spring Boot applications with JUnit5 tests. 
  Use when asked to review, analyze, audit, or evaluate code quality in Java/Spring Boot projects, 
  including component tests, integration tests, REST APIs, service layers, data access layers, 
  configuration, or any Java source code. Performs detailed code reviews focusing on adherence 
  to project standards, coding principles, best practices, test quality, and maintainability.
---

# Code Review Expert

Specialized code review agent for Java Spring Boot applications with comprehensive test coverage expertise.

## When to Use This Skill

- Review Java/Spring Boot source code
- Audit component tests and integration tests
- Evaluate test structure and quality
- Assess code against project standards and SOLID principles
- Review REST API design and implementation
- Evaluate test coverage and testing strategies
- Audit security, performance, or maintainability issues
- Provide detailed feedback with actionable improvements
- Check adherence to coding conventions and naming standards

## Review Methodology

### Phase 1: Context Gathering
1. Request or read the file(s) to be reviewed
2. Understand the project structure and coding standards
3. Identify applicable skills (component-testing, spring-boot-engineer, etc.)
4. Load relevant reference documentation as needed

### Phase 2: Multi-Aspect Analysis
Perform comprehensive review across these dimensions:

**Code Structure & Organization**
- Class hierarchy and inheritance patterns
- Separation of concerns and layering
- Method organization and cohesion
- Proper use of access modifiers

**Naming & Conventions**
- Class names (PascalCase with descriptive intent)
- Method names (camelCase with verb prefixes: prepare*, create*, validate*, get*, check*)
- Variable names (descriptive, avoid abbreviations)
- Constants (UPPER_SNAKE_CASE)
- No wildcard imports, organized by package hierarchy

**SOLID Principles & Design Patterns**
- Single Responsibility Principle
- Open/Closed Principle
- Liskov Substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle
- Appropriate design patterns

**Spring Boot & Framework Usage**
- Correct use of @SpringBootTest, @ActiveProfiles
- Proper dependency injection patterns (constructor injection preferred)
- Service layer implementation
- Repository and data access patterns
- Configuration management

**Test Quality (for test code reviews)**
- Given/When/Then structure adherence
- Test isolation and independence
- Proper use of fixtures and test data
- Meaningful assertions
- AllureSteps usage for reporting
- Avoidance of hardcoded values
- Database verification patterns

**Code Quality Metrics**
- Method length and complexity
- Cyclomatic complexity
- Code duplication
- Dead code or unused imports
- Exception handling patterns
- Resource management

**Security Review**
- Input validation
- SQL injection prevention
- Sensitive data exposure
- Authentication/Authorization
- Secret management

**Performance Considerations**
- N+1 query problems
- Unnecessary object creation
- Collection inefficiencies
- Resource utilization

### Phase 3: Detailed Report Generation
Provide structured feedback:

1. **Summary** - Overall assessment and key findings
2. **Issues Found** - Categorized by severity (Critical, High, Medium, Low, Info)
3. **Detailed Findings** - Specific locations with context and explanation
4. **Recommendations** - Actionable improvements with examples
5. **Strengths** - Positive patterns observed
6. **Suggested Changes** - Code snippets showing improvements

## Review Checklist

### General Code Quality
- [ ] Single Responsibility Principle - each class has one reason to change
- [ ] Methods are concise (<20 lines typically, <50 lines maximum)
- [ ] No commented-out code or TODOs without context
- [ ] No magic numbers or strings
- [ ] Clear, descriptive variable and method names
- [ ] Proper exception handling (try-catch, not throws)
- [ ] No unnecessary nesting or complexity
- [ ] Consistent code style and formatting

### Java/Spring Boot Standards
- [ ] No wildcard imports
- [ ] Constructor injection used (not field @Autowired)
- [ ] Proper access modifiers (private by default)
- [ ] Immutability where appropriate
- [ ] Proper use of Lombok annotations
- [ ] No unnecessary null checks with Optional
- [ ] Proper stream and lambda usage

### Test Quality (Component & Integration Tests)
- [ ] Test extends appropriate base class (ApiBaseTest, etc.)
- [ ] @Log4j2 annotation present
- [ ] @SpringBootTest(webEnvironment = RANDOM_PORT) configured
- [ ] @ActiveProfiles("test") applied
- [ ] Given/When/Then structure evident
- [ ] No hardcoded values in tests
- [ ] AllureSteps used for step reporting
- [ ] @TmsLink annotations present and correct
- [ ] CommonSteps used for verification logic
- [ ] Fixtures used for test data (VideoGameTestDataFixtures)
- [ ] Meaningful @DisplayName annotations
- [ ] Proper assertion patterns (soft assertions, AssertJ)

### Database & Persistence
- [ ] H2DbClient used for database operations
- [ ] JdbcTemplate queries properly parameterized
- [ ] Transactions properly managed
- [ ] N+1 query problems avoided
- [ ] Connection pooling configured

### API & REST
- [ ] Proper HTTP methods used
- [ ] Correct status codes returned
- [ ] Request/Response DTOs validated
- [ ] Error responses handled consistently
- [ ] REST Assured HttpClient used correctly

### Security
- [ ] Input validation on API endpoints
- [ ] SQL injection prevention
- [ ] No credentials in code or configuration
- [ ] Proper authentication/authorization checks
- [ ] Sensitive data not logged

## Output Format

When providing code review feedback:

```
## Code Review: [FileName]

### Summary
[Brief overall assessment]

### Key Findings
- **Issue 1**: [Description]
- **Issue 2**: [Description]

### Detailed Review

#### 1. [Category]: [Finding Title]
- **Severity**: [Critical/High/Medium/Low/Info]
- **Location**: Line X-Y
- **Current Code**: [Code snippet]
- **Issue**: [Explanation]
- **Recommendation**: [Solution with example]

### Strengths
- [Positive pattern 1]
- [Positive pattern 2]

### Action Items
- [ ] [Action 1]
- [ ] [Action 2]
```

## Project-Specific Standards

Review code against your project standards including:

- Component test structure per `component-testing` skill
- Spring Boot patterns per `spring-boot-engineer` skill
- Database testing per `db-testing` skill
- Coding principles from copilot-instructions.md:
  - Clean code and SOLID principles
  - Prioritize readability and maintainability
  - Use Lombok to reduce boilerplate
  - Always handle exceptions (no throws), throw RuntimeException in catch
  - Use AssertJ for assertions
  - Use Spring JDBC with JdbcTemplate for H2 database
  - Use Jackson for JSON/XML serialization

## Best Practices for Reviewers

1. **Be Constructive** - Frame feedback as suggestions for improvement, not criticism
2. **Be Specific** - Always reference line numbers and provide code examples
3. **Explain the Why** - Justify recommendations with principles and benefits
4. **Acknowledge Strengths** - Recognize good patterns and practices
5. **Prioritize Issues** - Focus on critical issues first, info-level items last
6. **Ask Questions** - When unclear, ask for clarification rather than assume
7. **Provide Examples** - Show how to improve with concrete code snippets
8. **Consider Context** - Understand constraints and trade-offs before criticizing


---
name: code-review
description: >-
  Code review expert for Java Spring Boot applications with JUnit5 tests.
  Use when asked to review, analyze, audit, or evaluate code quality in Java/Spring Boot projects,
  including component tests, integration tests, REST APIs, service layers, data access layers,
  configuration, or any Java source code. Trigger even when the user doesn't say "review" explicitly —
  casual phrases like "take a look at this", "does this look right", "any issues here?", "can you check
  this test", or "something feels off" are all valid triggers. Performs detailed code reviews focusing
  on adherence to project standards, coding principles, best practices, test quality, and maintainability.
---

# Code Review Expert

Specialized code review agent for Java Spring Boot applications with comprehensive test coverage expertise.

## Review Methodology

### Phase 1: Context Gathering

1. Request or read the file(s) to be reviewed
2. Understand the project structure and coding standards
3. Identify applicable skills (component-testing, spring-boot-engineer, db-testing, etc.)
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
- Proper dependency injection patterns (constructor injection preferred) ONLY for application development
- Service layer implementation
- Repository and data access patterns
- Configuration management

**Test Quality (for test code reviews)**

- Given/When/Then structure adherence
- Test isolation and independence
- Proper use of fixtures and test data is fetched from DB
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

For the full per-category checklist, see `references/checklist.md`.
For common anti-patterns with good/bad examples, see `references/common-issues.md`.

## Output Format

Provide feedback as a Markdown (.md) document using this structure:

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

Review code against these project standards:

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

## Reference Files

| File                          | Load When                                                                                                 |
|-------------------------------|-----------------------------------------------------------------------------------------------------------|
| `references/checklist.md`     | Running through the review — use the quick scan for a fast pass, comprehensive checklist for a full audit |
| `references/common-issues.md` | Identifying anti-patterns — has good/bad code examples for the most frequent violations                   |

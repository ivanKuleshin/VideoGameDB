# GitHub Copilot Instructions

For full project context, module structure, technology stack, and patterns refer to:
- [`AGENTS.md`](../AGENTS.md) — project overview, shared conventions, build commands
- [`app/AGENTS.md`](../app/AGENTS.md) — app module layout, endpoints, design decisions
- [`tests/AGENTS.md`](../tests/AGENTS.md) — test infrastructure, mandatory patterns, test data rules

# Coding Principles

- Follow clean code and SOLID principles
- Prioritize readability and maintainability
- Use Lombok as much as possible to reduce boilerplate code
- Never use `throws`, always handle exceptions with try-catch blocks and throw `RuntimeException` in catch block
- Do not create code if it's not going to be used, avoid Boat Anchor anti-pattern

# Naming Conventions

- **Classes**: PascalCase with descriptive names (e.g., `ApiBaseTest`, `HttpClient`)
- **Methods**: camelCase with verb prefixes (`prepare*`, `create*`, `validate*`, `get*`, `check*`)
- **Variables**: camelCase, descriptive names avoiding abbreviations
- **Constants**: UPPER_SNAKE_CASE for static final fields
- **Imports**: no wildcard imports, organized by package hierarchy

# Comments and Javadoc

- Do not add custom comments and Javadoc if not asked
- Code should be self-explanatory through clear naming and structure

# Final MD files

- Do not create multiple MD files when you need to summarize changes, your steps, etc., unless I ask you to do so.

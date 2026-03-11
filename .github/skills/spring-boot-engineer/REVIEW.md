# Skill Review: `spring-boot-engineer`

## Overview

A generic senior Spring Boot engineer persona. At **95 lines** it is compact, has a `references/` directory with
five topic-specific reference files, and uses a reference-guide table for progressive disclosure. This is an
**externally authored** skill (by `jeffallan`) with a version tag and MIT licence — it predates this project and
was not written for it.

---

## Pros

- **Progressive disclosure via reference table** — the "Reference Guide" section maps topics to files with an
  explicit *"Load When"* column. This is the clearest implementation of the three-level loading model in the
  entire skills repo.
- **Compact body** — 95 lines means the full skill loads quickly with minimal context cost.
- **MUST DO / MUST NOT DO lists** — exhaustive constraints on constructor injection, `@Transactional`, input
  validation, secret management, and deprecated patterns.
- **Role definition with seniority framing** — *"10+ years of enterprise Java"* primes the model to produce
  production-grade, not tutorial-grade, output.
- **Output templates section** — describes exactly what artefacts to produce (entity, repository, service,
  controller, DTO, config, tests), preventing incomplete implementations.

---

## Cons

### 1. Description is the weakest in the repo
> *"Use when building Spring Boot 3.x applications, microservices, or reactive Java applications. Invoke for
> Spring Data JPA, Spring Security 6, WebFlux, Spring Cloud integration."*

It only lists technology names, not user contexts. It is not pushy, gives no phrasing examples, and will
undertrigger for common requests like *"add a new endpoint"*, *"secure this route"*, or *"write a service layer"*.

### 2. Non-standard YAML frontmatter fields
The frontmatter contains `license`, `metadata.author`, `metadata.version`, `metadata.domain`, `metadata.triggers`,
etc. None of these fields are part of the skill spec. They add noise and could confuse tooling that reads the
frontmatter.

### 3. "When to Use This Skill" section duplicates description
Per skill-creator rules, *"When to use"* belongs in the description, not the body. The body section is a direct
duplication of the description's technology list.

### 4. MUST / MUST NOT format is exactly what skill-creator discourages
Skill-creator guidance: *"If you find yourself writing ALWAYS or NEVER in all caps … that's a yellow flag — if
possible, reframe and explain the reasoning."* The constraint sections are pure lists with no rationale. The model
follows them by rote without understanding *why*, leading to brittle application.

### 5. Completely unaware of this project's stack
The skill is generic Spring Boot but the project uses:
- **Jersey (JAX-RS)** instead of Spring MVC `@RestController`
- **H2 + NamedParameterJdbcTemplate** (no JPA)
- **Lombok** everywhere
- **HTTP Basic Auth** (not OAuth2/JWT)
- **`VideoGame` entity with JAXB annotations** (not standard JPA)

Following this skill's output templates verbatim would produce code that doesn't compile in the `app` module.

### 6. "Knowledge Reference" section is a keyword dump
> *"Spring Boot 3.x, Spring Framework 6, Spring Data JPA, Spring Security 6, … JUnit 5, Mockito, Testcontainers,
> Docker, Kubernetes"*

This adds nothing beyond restating the description. It should either be removed or replaced with a link to a
relevant reference file.

### 7. `references/` directory contents are unknown
The reference table promises five files (`web.md`, `data.md`, `security.md`, `cloud.md`, `testing.md`) but their
actual content is not reviewed here (they may be well-written). However, none of them are project-specific, so
the risk of conflicts with project conventions applies to all five.

---

## Steps to Improve

1. **Rewrite the description to be pushy and project-aware:**
   > *"Use when building or modifying Spring Boot 3.x application code in the `app` module — Jersey endpoints,
   > Spring Security configuration, H2 schema, Jackson serialization, or JAXB models. Also trigger for general
   > Java/Spring questions like 'add an endpoint', 'how do I secure this', 'write a service', or 'extend the
   > schema', even when Spring isn't mentioned by name."*

2. **Remove all non-standard YAML frontmatter fields** (`license`, `metadata.*`). Keep only `name` and
   `description`.

3. **Remove `## When to Use This Skill`** from the body — merge any unique context into the description instead.

4. **Replace MUST/MUST NOT lists with reasoned guidelines** — e.g.:
   > *"Use constructor injection (not `@Autowired` on fields) so dependencies are explicit, the class is
   > testable without a Spring context, and the compiler enforces required deps."*

5. **Add a `## Project Overrides` section** at the top of the body that explicitly states how this project
   deviates from the generic Spring Boot patterns:
   - Jersey/JAX-RS for HTTP endpoints (not `@RestController`)
   - `NamedParameterJdbcTemplate` for DB access (no JPA/repositories)
   - HTTP Basic Auth (no OAuth2)
   - Lombok everywhere
   - JAXB + `@XmlRootElement` for XML serialization

6. **Remove the "Knowledge Reference" keyword dump** — it adds no value over the description.

7. **Audit `references/*.md` files** for conflicts with project conventions and add project-specific overrides
   or notes where the generic Spring Boot patterns don't apply.


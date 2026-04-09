---
name: Avoid spaghetti code anti-pattern
description: Do not produce spaghetti code — keep logic structured, modular, and readable
type: feedback
---

Avoid spaghetti code anti-patterns in all generated code.

**Why:** User explicitly called this out as an undesirable pattern.

**How to apply:** Keep logic structured and modular — extract well-named methods, avoid deep nesting, maintain clear separation of concerns. Do not write long, tangled methods or classes where control flow jumps around unpredictably.
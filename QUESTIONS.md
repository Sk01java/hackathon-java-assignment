# Questions

Here are 2 questions related to the codebase. There's no right or wrong answer - we want to understand your reasoning.

## Question 1: API Specification Approaches

When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded everything directly. 

What are your thoughts on the pros and cons of each approach? Which would you choose and why?

**Answer:**
```txt
Both approaches are valid, but they optimize for different risks.

OpenAPI-first (Warehouse)
Pros:
- Strong contract governance: schema and endpoints are explicit and versionable.
- Better consumer alignment: client/server generation reduces drift.
- Easier documentation and onboarding via a single source of truth.

Cons:
- Generation toolchain adds build complexity and occasional friction.
- Rapid iteration can feel slower when every change starts in YAML.
- Generated artifacts can be noisy in code review.

Code-first (Product/Store)
Pros:
- Fast iteration for internal APIs and experiments.
- Lower tooling overhead; straightforward debugging and refactoring.
- Business logic is close to endpoint code.

Cons:
- Higher chance of contract drift from docs.
- Requires stronger discipline to keep request/response formats stable.
- Client generation and API governance are weaker by default.

My choice:
For externally consumed or cross-team APIs, I would choose OpenAPI-first to make contracts explicit and enforceable.
For internal, rapidly evolving endpoints, code-first is acceptable initially, but I would still publish and validate an OpenAPI spec in CI.

Practical compromise for this project:
- Keep Warehouse OpenAPI-first.
- Add contract tests and OpenAPI snapshots for Product/Store.
- Enforce backward-compatibility checks in CI before release.

```

---

## Question 2: Testing Strategy

Given the need to balance thorough testing with time and resource constraints, how would you prioritize tests for this project? 

Which types of tests (unit, integration, parameterized, etc.) would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
Given time constraints, I would prioritize tests by business risk and change frequency:

1) Fast unit tests on domain use cases (highest ROI)
- Validate business rules and edge cases for create/replace/archive flows.
- Include negative paths and parameterized input validation.

2) API integration tests for endpoint contracts
- Verify status codes, payload shape, sorting/filtering, and error mapping.
- Include positive + negative + malformed input scenarios.

3) Persistence-focused tests
- Cover uniqueness constraints, optimistic locking behavior, and transaction boundaries.
- Add deterministic checks for flush-time constraint failures.

4) Concurrency and critical-path integration tests
- Keep a smaller, stable set for optimistic lock and race-condition scenarios.
- Run on CI, but separate from ultra-fast PR checks if needed.

5) Regression strategy over time
- Use JaCoCo thresholds (>=80%) as a guardrail, not as the only quality metric.
- Track coverage trend and mutation-prone modules (stores/warehouses).
- Add tests for every production bug fix to prevent recurrence.
- Keep test data builders/helpers to reduce flakiness and improve readability.

Pipeline suggestion:
- PR: unit + selected integration tests + contract checks.
- Main/nightly: full integration + concurrency + containerized tests.
- Release: full suite plus compatibility and performance smoke checks.

```

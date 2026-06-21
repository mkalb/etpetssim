---
applyTo: "**/src/test/java/**/*.java"
description: "Test-source rules for etpetssim. Use when creating or modifying JUnit tests, fixtures, support classes, or manual test utilities."
---

# Test Source Instructions

Rules that are specific to test sources; do not repeat broader repository or Java rules.

## Test Types

- Use JUnit 5 (Jupiter) on the JUnit Platform for automated tests.
- Do not add TestNG, Spock, AssertJ, Mockito, or other test frameworks without prior agreement.
- Name JUnit test classes `*Test`; keep reusable package-local support classes as `*TestSupport`.
- Non-JUnit test-source utilities are allowed only for manual analysis/tuning/debugging; use suffix `Analyzer`,
  `Runner`,
  `Tuner`, or `Debugger`.

## JUnit Class Shape

- Prefer package-private `final` test classes; use `public` only when required by a tool or shared support API.
- Keep reused constants near the top and helpers before tests when they make test cases easier to scan.
- Use standard lifecycle method names: `setUpBeforeAll()`, `setUpBeforeEach()`, `tearDownAfterEach()`.
- Initialize `AppLogger` in `@BeforeAll` with `AppLogger.initializeForTesting()` when production code under test logs.
- Reset `AppLogger` in `@BeforeEach` with `AppLogger.resetForTesting()` only when a test mutates logger state.
- Add `@Execution(ExecutionMode.SAME_THREAD)` for `AppLogger.initializeForTesting()`, JavaFX state, mutable static
  state, or
  other required sequential execution.

## Test Method Style

- Test method names start with `test` and describe the behavior or contract, e.g.,
  `testConstructorRejectsNegativeWidth`.
- Do not add Javadoc to test methods; the method name should carry the intent.
- Prefer one behavior per test; group related checks with `assertAll(...)`.
- Use `assertThrows(...)` for exception contracts and `assertDoesNotThrow(...)` only for meaningful valid-operation
  checks.
- Prefer specific assertions; add assertion messages only when the failure would otherwise be ambiguous.
- Use a delta for floating-point comparisons.
- Use `@Disabled("reason")` with a concrete reason.

## Test Data and Helpers

- Create focused helpers for verbose setup, e.g., `createConfig(...)` or `coordinate(...)`.
- Keep helper names aligned with production naming (`create...`, `build...`, `compute...`, `is...`).
- Prefer immutable test data; verify defensive copies and unmodifiable views where they are part of the contract.
- Put test-only nested enums, records, and small fixture types at the bottom of the class.
- Use method-level `@SuppressWarnings("DataFlowIssue")` for intentional null-passing tests.
- Use class-level `@SuppressWarnings("MagicNumber")` only when many domain numbers are central to the cases.

## Common Contract Patterns

- Enum tests should cover, in order: `testEnumValues`, `testEnumCount`, `testDeclarationOrder`, invalid/null
  `valueOf(...)`, resource-key methods, then enum-specific behavior such as `testVertexCount()`.
- Configuration and factory tests should cover valid defaults, each rejected unsupported option, and boundary values
  just
  inside/outside configured ranges.
- Collection/model tests should cover mutation rejection, defensive copies, empty/sparse cases, and representative
  domain
  entities via small test fixtures.
- CLI/localization/resource tests should check accepted variants, ignored invalid input, stable output fragments, and
  missing/invalid key behavior without relying on unrelated full text.

## JavaFX Tests

- Use `de.mkalb.FxTestSupport`.
- Call `FxTestSupport.ensureStarted()` once in `@BeforeAll`.
- Create and mutate JavaFX components via `FxTestSupport.runAndWait(...)`; use the timeout overload only when needed.
- Use `FxTestSupport.supplyAndWait(...)` to create a JavaFX object on the FX thread and return it for assertions on the
  calling thread.
- Throwables (including `AssertionError`) thrown inside `runAndWait`/`supplyAndWait` propagate to the calling thread, so
  assertions may be placed inside the runnable or made on returned values.
- Use `@Execution(ExecutionMode.SAME_THREAD)` when JavaFX state is shared or platform ordering matters.

## Manual Test Utilities

- Make non-JUnit utility classes `public final` only when they are intended for IDE/manual launch; otherwise
  package-private
  is enough.
- Use a private constructor and `static void main()` without `String[] args`.
- Do not add JUnit annotations.
- Set `Locale.setDefault(Locale.ROOT)` at the start of `main()`.
- Prefer `System.out.printf(Locale.ROOT, ...)` for formatted output and stable columns.
- Document purpose and manual usage in class Javadoc; keep computation helpers private and deterministic.

## Organization

- Preserve nearby test ordering and naming conventions when editing existing tests.
- For larger classes, use simple section comments such as `// --- Construction tests ---`; common sections are
  construction, validation, behavior, edge cases, and exception tests.

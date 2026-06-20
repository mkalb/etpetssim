---
applyTo: "**/src/test/java/**/*.java"
description: "Test-source rules for etpetssim. Use when creating or modifying JUnit tests, fixtures, support classes, or manual test utilities."
---

# Test Source Instructions

Test-source rules for this repository. Complements project-wide and Java coding instructions.

## Test Framework

- Use JUnit 5 (JUnit Jupiter) executed via the JUnit Platform.
- Do not add additional test frameworks (TestNG, Spock, AssertJ, Mockito) without prior agreement.

## Test Class Structure

- Prefer package-private test classes.
- Prefer `final` for test classes unless extension is required for test inheritance.
- Place reused test constants near the top of the class before lifecycle and test methods.
- Use descriptive constant names in UPPER_SNAKE_CASE (e.g., `GRID_WIDTH_SAMPLE`, `CELL_EDGE_LENGTH`).

## Test Lifecycle

- Use `@BeforeAll` with method name `setUpBeforeAll()` for one-time setup.
- Use `@BeforeEach` with method name `setUpBeforeEach()` for per-test setup.
- Use `@AfterEach` with method name `tearDownAfterEach()` for per-test cleanup.
- Initialize `AppLogger` in `@BeforeAll` via `AppLogger.initializeForTesting()` when tests use logging.
- Reset `AppLogger` in `@BeforeEach` via `AppLogger.resetForTesting()` when tests modify logger state.
- Initialize JavaFX in `@BeforeAll` via `FxTestSupport.ensureStarted()` for JavaFX tests.
- Use `@Execution(ExecutionMode.SAME_THREAD)` when tests share mutable static state or require sequential execution.

## Test Naming

- Start all test methods with `test` followed by the behavior or scenario under test.
- Use descriptive names that document the test intent: `testConstructorRejectsNegativeWidth`.
- Do not add Javadoc to test methods; the method name should be self-documenting.
- Avoid generic names like `testMethod1` or `testCase`.

## Assertions

- Use `assertAll(...)` to group related assertions for a single behavior.
- Use `assertThrows(ExceptionType.class, () -> ...)` for exception contract checks.
- Use `assertDoesNotThrow(() -> ...)` to verify valid operations.
- Prefer specific assertions (`assertEquals`, `assertTrue`) over generic ones when possible.
- Add assertion messages when the failure reason might be ambiguous.
- Use a delta parameter for floating-point comparisons.
- Use `@Disabled("reason")` with a clear reason when temporarily disabling tests.

## Test Data and Helpers

- Create helper methods for complex object construction (e.g., `createConfig(int width, int height)`).
- Use nested enums or records for test-specific data types (place at bottom of test class).
- Prefer immutable test data; use defensive copying when needed.
- Keep helper method names consistent with production code naming conventions.

## Common Test Patterns

### Enum Testing

Enum test classes follow a standardized structure. Order test methods as follows:

1. Core contract tests (`testEnumValues`, `testEnumCount`, `testDeclarationOrder`)
2. Exception tests (`testValueOfInvalidThrows`, `testValueOfNullThrows`)
3. Resource key tests (if applicable)
4. Domain-specific behavior tests

- Test `values()`, `valueOf(...)`, declaration order, expected count, invalid names, and null names when relevant.
- For localized enums, test each resource key method separately.
- Test enum-specific methods with descriptive names such as `testVertexCount()` or `testOpposite()`.

### Boundary, Immutability, and Copying Tests

- Cover minimum, maximum, and invalid boundary values for constructors and factories.
- Verify unmodifiable collection views reject mutation.
- Verify constructors and accessors make defensive copies for mutable inputs or outputs.

## Suppressions in Tests

Follow general suppressions policy from java.instructions.md.

Common test-specific suppressions:

- Use `@SuppressWarnings("MagicNumber")` at class level when tests use many numeric literals.
- Use `@SuppressWarnings("DataFlowIssue")` at method level for intentional null-passing tests.
- Use `@SuppressWarnings("SpellCheckingInspection")` at class level for domain-specific terminology.

## JavaFX Testing

- Use `de.mkalb.FxTestSupport` from the test source tree for JavaFX platform initialization and thread synchronization.
- Call `FxTestSupport.ensureStarted()` once in `@BeforeAll` for tests using JavaFX components.
- Use `FxTestSupport.runAndWait(runnable)` to execute JavaFX operations synchronously from test threads.
- `runAndWait()` uses a 10-second default timeout; use the two-argument overload `runAndWait(runnable, timeoutSeconds)`
  for custom timeouts.
- `runAndWait()` automatically handles execution context (runs immediately if already on FX Application Thread).
- Do not create JavaFX components outside the JavaFX Application Thread.
- Use `@Execution(ExecutionMode.SAME_THREAD)` when JavaFX state is shared across tests.

## Manual Testing and Analysis Utilities

The test source tree may contain non-JUnit classes for manual testing, analysis, and tuning:

- Use descriptive suffixes: `Analyzer`, `Runner`, `Tuner`, `Debugger`.
- Make the class `final` with a private constructor.
- Provide a `static void main()` method (not `public static void main(String[] args)`).
- Do not add JUnit annotations (`@Test`, `@BeforeEach`, etc.).
- Use `@SuppressWarnings("MagicNumber")` at class level for analysis constants.
- Set `Locale.setDefault(Locale.ROOT)` at the start of `main()` for consistent output.
- Generate formatted console output using `System.out.printf(Locale.ROOT, ...)`.
- Document the purpose and usage in the class Javadoc.

## Test Organization

Group larger test classes with comment separators such as `// --- Construction tests ---`.
Common groupings: construction, validation, behavior, edge case, and exception tests.

---
applyTo: "**/*Test.java"
description: "JUnit 5 test writing rules for etpetssim. Use when creating or modifying JUnit tests, writing test assertions, organizing test methods, or setting up test fixtures."
---

# JUnit Test Instructions

JUnit 5 test writing rules for this repository. Complements project-wide and Java coding instructions.

## Test Class Structure

- Prefer package-private test classes.
- Prefer `final` for test classes unless extension is required for test inheritance.
- Organize tests by functionality using section comments (e.g., `// --- Construction tests ---`).
- Place test-specific constants at the top of the class after fields.
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
- Use delta parameter for floating-point comparisons: `assertEquals(expected, actual, 0.00001d)`.

## Test Data and Helpers

- Define test-specific constants at class level when values are reused across tests.
- Create helper methods for complex object construction (e.g., `createConfig(int width, int height)`).
- Use nested enums or records for test-specific data types (place at bottom of test class).
- Prefer immutable test data; use defensive copying when needed.
- Keep helper method names consistent with production code naming conventions.

## Common Test Patterns

### Enum Testing

```java
@Test
void testValueOfInvalidThrows() {
    assertThrows(IllegalArgumentException.class, () -> MyEnum.valueOf("INVALID"));
}

@Test
void testValueOfNullThrows() {
    assertThrows(NullPointerException.class, () -> MyEnum.valueOf(null));
}
```

### Boundary Value Testing

```java
@Test
void testMinAndMaxSizeAreValid() {
    assertDoesNotThrow(() -> new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
    assertDoesNotThrow(() -> new GridSize(GridSize.MAX_SIZE, GridSize.MAX_SIZE));
}
```

### Immutability Testing

```java
@Test
void testGetValidValuesReturnsUnmodifiableList() {
    assertThrows(UnsupportedOperationException.class,
            () -> property.getValidValues().add(newValue));
}
```

### Defensive Copying Testing

```java
@Test
void testConstructorMakesDefensiveCopyOfValidValues() {
    List<Mode> validValues = new ArrayList<>(List.of(Mode.A, Mode.B));
    Property property = new Property(validValues);
    validValues.clear();
    assertEquals(List.of(Mode.A, Mode.B), property.getValidValues());
}
```

## Suppressions in Tests

- Use `@SuppressWarnings("MagicNumber")` at class level when tests use many numeric literals.
- Use `@SuppressWarnings("DataFlowIssue")` at method level for intentional null-passing tests.
- Use `@SuppressWarnings("SpellCheckingInspection")` at class level for domain-specific terminology.
- Keep each suppression as narrow as possible (prefer method over class scope).
- Do not add explanatory comments to `@SuppressWarnings`.

## JavaFX Testing

- Use `de.mkalb.FxTestSupport` for JavaFX platform initialization and thread synchronization.
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

Example:

```java
/**
 * Manual analyzer for scoring formula tuning.
 * Generates tabular output for iterative refinement.
 */
@SuppressWarnings("MagicNumber")
public final class ScoringAnalyzer {
    private ScoringAnalyzer() {}
    
    static void main() {
        Locale.setDefault(Locale.ROOT);
        runAnalysis();
    }
    
    private static void runAnalysis() { ... }
}
```

## Test Organization

Group related tests using comment separators:

```java
// --- Construction tests ---

@Test
void testConstructorWithValidArguments() { ... }

// --- Validation tests ---

@Test
void testIsValidReturnsTrueForValidConfig() { ... }
```

Common groupings:

- Construction tests (constructors, factory methods)
- Validation tests (invariants, preconditions)
- Behavior tests (methods, state transitions)
- Edge case tests (boundary values, null handling)
- Exception tests (error conditions, contract violations)

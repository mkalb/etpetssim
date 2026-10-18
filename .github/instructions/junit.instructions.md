---
applyTo: "**/src/test/java/**/*.java"
description: "Rules for JUnit tests, fixtures, test support classes, and manual test utilities."
---

# Test Source Instructions

Rules for test sources only; repository-wide and Java instructions still apply.

## Test Types

- Use JUnit 5 (Jupiter) on the JUnit Platform for automated tests.
- Do not add TestNG, Spock, AssertJ, Mockito, or other test frameworks without prior agreement.
- Name a JUnit test class `<ProductionType>Test`: it tests one top-level production type and lies in the package of
  that type. Do not split a type into several test classes and do not use `@Nested`.
- As an exception, name a test class `<Topic>Test` when it checks a contract across several production types (e.g., the
  statistics of all simulations); it lies in the package of the common abstraction. Tests that essentially check one
  type belong in `<Type>Test`. Skill tests follow "Repository Skill Tests" instead.
- Keep reusable package-local support classes as `*TestSupport`.
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

- Test method names follow `test<Subject>[<Behavior>[<Condition>]]` in UpperCamelCase; no underscores and no
  `@DisplayName`.
  - `<Subject>` is the exact name of the tested production method, or `Constructor`, so that renames stay searchable.
  - When a production method has a single test method, `test<Subject>` is enough, even if it checks several aspects.
  - When a production method has several test methods, the test of the main case may stay `test<Subject>`; each other
    test adds a `<Behavior>`, a verb phrase such as `Returns...`, `Rejects...`, `Throws...`, `Records...`, or
    `Ignores...`, and, if needed, a `<Condition>` such as `For...`, `When...`, or `With...` (e.g.,
    `testConstructorRejectsNegativeWidth`).
  - `testRecord` is the fixed name for the basic test of a record: canonical constructor, accessors, and `toString()`.
  - The fixed enum test name `testDeclarationOrder` remains; it names the declaration-order test of one enum. An
    `<Interface>Test` names order checks across the constants of its implementing enums freely (e.g.,
    `testConstantsAreSortedByDisplayName`).
  - Tests of a nested type put its simple name before a fixed name (e.g., `testFixedActionDeclarationOrder`).
  - A constant has one test named `test<ConstantName>` in UpperCamelCase (e.g., `ORIGIN` -> `testOrigin`); a check
    across all constants of a type describes the contract freely (e.g., `testConstantsUseCssTokenFormat`).
  - When the tested behavior is triggered indirectly (e.g., through a listener), `<Subject>` is the private or
    package-private method that implements the reaction (e.g., `testHandleStartActionRetainsCapturedTimedSettings`).
- Name overload tests `test<Subject>[With<Parameter>][<Behavior>[<Condition>]]`. `With<Parameter>` follows `<Subject>`
  directly and names only the parameter that distinguishes the overload, by role or, if clearer, by type
  (`WithSupplier`, `WithThrowable`); no count such as `OneParameter`. The base overload (the simplest or most common
  signature) may stay unqualified, e.g., `testDebug` and `testDebugWithSupplier`.
- In a topic test, `<Subject>` does not apply: the method name describes the checked contract freely, with the prefix
  `test`.
- Aim for at most about 70 characters per test method name; do not repeat the test class name in the method name and do
  not use historical terms such as `Legacy`.
- Test sources contain no block comments and no line comments. Exception: inside a test method body, a line comment may
  precede one or more assertions when it explains something important that is not immediately evident; `//noinspection`
  directives are allowed. Do not comment helpers, fixtures, constants, or fields; a better name carries the intent.
- Add Javadoc to test sources very sparingly and only where it adds clear value; test classes and test methods
  normally have none. Shared support classes such as `FXTestSupport` and manual test utilities keep Javadoc for the
  class and its public API (a manual utility's `main()`). Package-local `*TestSupport` classes and all private
  helpers have no Javadoc.
- Prefer one behavior per test; group related checks with `assertAll(...)`.
- Do not write tests that only check what the compiler guarantees (e.g., `assertInstanceOf` on a statically typed
  value).
- Use `assertThrows(...)` for exception contracts and `assertDoesNotThrow(...)` only for meaningful valid-operation
  checks.
- Prefer specific assertions; add assertion messages only when the failure would otherwise be ambiguous.
- Use a delta for floating-point comparisons.
- Use `@Disabled("reason")` with a concrete reason.

## Test Data and Helpers

- Create focused helpers for verbose setup, e.g., `createConfig(...)` or `coordinate(...)`.
- Keep helper names aligned with production naming (`create...`, `build...`, `compute...`, `is...`); never start a
  helper name with `test`.
- Name fixture types `Test<Type>` for a minimal implementation of a production type; `Stub<Type>`, `Counting<Type>`,
  or `Recording<Type>` when the behavior of the test double matters; otherwise a meaningful domain name. Fixture names
  that contain a production type name follow its renames.
- Prefer immutable test data; verify defensive copies and unmodifiable views where they are part of the contract.
- Put test-only nested enums, records, and small fixture types at the bottom of the class.
- Use method-level `@SuppressWarnings("DataFlowIssue")` for intentional null-passing tests.
- Use class-level `@SuppressWarnings("MagicNumber")` when test cases use literal domain numbers; in test sources, the
  class is the intended scope.
- Prefix test resources with `test_` (e.g., `css/test_style.css`, `i18n/test_messages_en_US.properties`) so they
  never shadow main resources inside the patched module; prefer main resources when a test does not need special
  content.

## Common Contract Patterns

- Enum tests cover the enum's own contract, in this order: `testDeclarationOrder` only when the type Javadoc
  documents declaration-order semantics, resource-key methods, then enum-specific behavior such as
  `testVertexCount()`. Do not test what the JDK generates for every enum (`values()`, `valueOf(...)`, the number
  of constants).
- Resource-key tests check that each returned key exists in all main resource bundles, not the literal key value;
  use `de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles(...)`, or
  `assertDescriptorKeysInAllBundles(...)` for the keys of entity descriptor providers.
- Configuration and factory tests should cover valid defaults, each rejected unsupported option, and boundary values
  just
  inside/outside configured ranges.
- Collection/model tests should cover mutation rejection, defensive copies, empty/sparse cases, and representative
  domain
  entities via small test fixtures.
- Test an interface contract shared by several implementations in `<Interface>Test` with `@ParameterizedTest` and
  `@EnumSource` over a package-private fixture enum that creates the implementations; keep implementation-specific
  behavior in `<Implementation>Test`.
- Test the default and static methods of an interface in `<Interface>Test`, using a minimal `Test<Type>` fixture or a
  real implementer, or in the contract test of a sub-interface that covers the whole hierarchy (e.g.,
  `WritableGridModelTest`); interfaces without default or static methods (markers, pure contracts) need no test of
  their own.
- CLI/localization/resource tests should check accepted variants, ignored invalid input, stable output fragments, and
  missing/invalid key behavior without relying on unrelated full text.

## JavaFX Tests

- Use `de.mkalb.etpetssim.testsupport.FXTestSupport`.
- Call `FXTestSupport.ensureStarted()` once in `@BeforeAll`.
- Create and mutate JavaFX components via `FXTestSupport.runAndWait(...)`; use the timeout overload only when needed.
- Use `FXTestSupport.supplyAndWait(...)` to create a JavaFX object on the FX thread and return it for assertions on the
  calling thread.
- Throwables (including `AssertionError`) thrown inside `runAndWait`/`supplyAndWait` propagate to the calling thread, so
  assertions may be placed inside the runnable or made on returned values.
- Use `@Execution(ExecutionMode.SAME_THREAD)` when JavaFX state is shared or platform ordering matters.

## Repository Skill Tests

- Put all automated tests for repository skills under
  `app/src/test/java/de/mkalb/etpetssim/skills` in the `de.mkalb.etpetssim.skills` package and annotate each test class
  with `@Tag("skill")`.
- Run skill tests with `gradlew.bat app:skillTest`; the regular `test` task intentionally excludes the `skill` tag.
- The existing `skillTest` task automatically discovers every test class tagged `skill`; adding tests for another
  skill must not require changes to the Gradle configuration.
- Run skills that read or modify repository-shaped files against fixtures under `@TempDir`; never use production files
  as mutable test input.
- For command-style skills, assert the exit code and stable output fragments or exact output. For modifying modes,
  also assert exact resulting bytes, unchanged input on failure, and idempotence where applicable.
- Give spawned processes a bounded timeout and ensure they and their descendants are terminated during cleanup.

## Manual Test Utilities

- Make non-JUnit utility classes `public final` only when they are intended for IDE/manual launch; otherwise
  package-private
  is enough.
- Use a private constructor and `static void main()` without `String[] args`.
- Do not add JUnit annotations.
- Set `Locale.setDefault(Locale.ROOT)` at the start of `main()`.
- Prefer `System.out.printf(Locale.ROOT, ...)` for formatted output and stable columns.
- Document purpose and manual usage in class Javadoc and document `main()`; keep computation helpers private and
  deterministic.

## Organization

- Preserve nearby test ordering and naming conventions when editing existing tests.
- Recommended order of test methods: constructor and factories first, then the production methods in their declaration
  order, with all tests of a method and its overloads next to each other. Enum tests keep the order given in "Common
  Contract Patterns"; topic tests are free in their order.

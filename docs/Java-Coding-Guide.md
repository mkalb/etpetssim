# Java Coding Guide

This document is the coding reference for this repository.
It is written in simple English for people and for GitHub Copilot prompts.

## Project Baseline

- Language level: **Java 25**
- Main UI stack: **JavaFX**
- Build tool: **Gradle** (wrapper from repository root)
- Project type: **Open-source** on GitHub
- Primary development and test platform: **Windows 11** (UI behavior can differ by OS)
- File format: GitHub Markdown (`.md`), UTF-8

## Goals of This Guide

- Keep naming and code style consistent.
- Make code easier to read, review, and maintain.
- Provide clear conventions for GitHub Copilot code generation.
- Provide a quick lookup for daily development.

## General Engineering Principles

### DRY

- Do not repeat logic in many places.
- Extract shared logic into focused methods or classes.
- Avoid copy/paste implementations.

### KISS

- Prefer simple solutions first.
- Keep methods small and single-purpose.
- Avoid complex abstractions without clear value.

### Clean Code

- Use meaningful names.
- Keep classes cohesive.
- Prefer readability over clever shortcuts.
- Remove dead code and outdated comments.

## Code Organization and Style

### General Formatting

- Use lowerCamelCase for methods and parameters.
- Prefer clear domain terms over abbreviations.
- Keep comments useful; do not restate obvious code.

### IntelliJ IDEA Usage

- Run and fix relevant inspections before commit.
- Use `@SuppressWarnings` only when necessary.
- Keep suppression narrow (smallest scope possible).
- Add a short reason when suppression is not obvious.
- Use IntelliJ formatter with project settings before commit.

## Architecture Pattern: MVVM

- Use **Model** for domain state and business rules.
- Use **View** for JavaFX UI components only.
- Use **ViewModel** for UI state, formatting, and interaction logic.
- Keep View free from domain/business logic.
- Keep Model independent of JavaFX UI code.

## Nullability and Code Documentation

### Nullability and JSpecify

- Use JSpecify as the nullness contract for all Java code in this repository.
- Set package defaults in `package-info.java` with `@org.jspecify.annotations.NullMarked`.
- Under that default, treat all types as non-null unless explicitly annotated.
- Use `org.jspecify.annotations.Nullable` only for real nullable contracts (for example, optional return values,
  optional parameters, or nullable fields required by an API).
- Do not add `@Nullable` "just in case"; add it only when null is an intentional and supported value.

### JavaDoc Rules

- Write JavaDoc for public types and public methods.
- Document intent, inputs, outputs, and side effects.
- In normal cases, explain what the code does, not how it is implemented.
- Use `@param`, `@return`, and `@throws` when applicable.
- Follow the nullability rules from `Nullability and JSpecify` above.
- Because non-null is the default, do not add routine `NullPointerException` notes in JavaDoc.
- If a name is not self-explanatory, or behavior is surprising, document it explicitly.
- Always document important side effects that are not obvious from the API name/signature.
- Use modern JavaDoc style for Java 25.
- Add JavaDoc to `private` classes or methods when it provides important extra context.
- Keep JavaDoc short, factual, and up to date.

## Java Naming Conventions

These method naming patterns are used across the codebase.

- **build**: Assemble multiple UI parts into one composed region/container.
    - Example: `buildMainRegion()`, `buildConfigRegion()`
- **create**: Instantiate and configure one object/control/view.
    - Example: `createLabel()`, `createVBox()`, `createSimulationRegion()`
- **compute**: Deterministic calculation from input or current state.
    - Example: `computeCellDimension()`, `computeCellFontSize()`
- **draw**: Rendering logic on canvas/graphics context.
    - Example: `drawCell()`, `drawCenteredTextInCell()`
- **is**: Boolean state/validity/mode query.
    - Example: `isIllegal()`, `isWithinBounds()`, `isModeTimed()`
- **has**: Boolean presence/availability query.
    - Example: `hasKey()`, `hasEqualEdgeBehaviors()`
- **get / set**: Standard mutable property accessors.
    - Example: `getValue()`, `setValue()`, `setDirection()`
- **Property** suffix: JavaFX property accessor naming.
    - Example: `actionButtonRequestedProperty()`, `stepDurationProperty()`
- **as**: Conversion or alternative view accessor.
    - Example: `asStringBinding()`, `asObjectProperty()`
- **to / from**: Deterministic value/coordinate conversion.
    - Example: `toCanvasPosition()`, `fromCanvasPosition()`
- **request**: User-intent action trigger or flag.
    - Example: `requestActionButton()`, `requestCancelButton()`
- **of**: Validating static factory method.
    - Example: `InputDoubleProperty.of(...)`, `InputEnumProperty.of(...)`
- **with**: Static factory-style configured variant.
    - Example: `withMinStepDuration(...)`
- **initialize / reset / shutdown**: Lifecycle operations.
    - Example: `initialize(...)`, `resetForTesting()`, `shutdown()`

### Accepted Compact Names in Specific Cases

- Utility/value APIs may use concise noun-style accessors.
    - Example: `locale()`, `bundle()`, `keys()`, `supportedLocales()`
- Immutable domain types may use concise derived-operation names.
    - Example: `area()`, `perimeter()`, `aspectRatio()`, `opposite()`, `nextClockwise()`
- Functional combinators may use short composition names.
    - Example: `and(...)`, `or(...)`, `negate()`, `compose(...)`, `identity()`
- Java records keep generated accessor names.
    - Example: `x()`, `y()`

## Testing

### Test Organization and Structure

- Prefer package-private test classes and test methods.
- Prefer `final` for test classes unless extension is required.
- Use consistent lifecycle method names such as `setUpBeforeAll`, `setUpBeforeEach`, and `tearDownAfterEach`.
- If tests use shared static/global state, reset state in `@BeforeEach` and use same-thread execution when needed.
- For JavaFX tests, initialize JavaFX once in `@BeforeAll` via `FxTestSupport.ensureStarted()`.
- Test packages are `@NullMarked`; test null contracts explicitly with assertions instead of JavaDoc notes.

### Test Assertions and Semantics

- Prefer `assertAll(...)` for grouped checks of one behavior.
- Use `assertThrows(...)` for contract and error-path checks.
- Keep `@SuppressWarnings` as narrow as possible; add a short reason if not obvious.
- Do not keep empty TODO test methods in committed code; use `@Disabled("reason")` with a clear note if temporary.

### Test Naming

- Start test method names with `test`.
- Continue with behavior or scenario under test.
- Keep names specific and readable.
    - Example: `testEnumValues()`, `testNullArgumentsThrowsException()`
- Normally, do not use JavaDoc in test classes and test methods.

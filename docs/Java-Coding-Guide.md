# Java Coding Guide

This document is the authoritative coding guide for this repository.
It is optimized for both humans and GitHub Copilot context prompts.

## How to Use This Guide

- Read this file as a ruleset, not as prose.
- Treat each bullet as one atomic rule.
- Follow rule priority when rules appear to conflict.

## Normative Keywords

- **MUST**: mandatory requirement.
- **MUST NOT**: prohibited behavior.
- **SHOULD**: default recommendation; deviate only with a clear reason.
- **SHOULD NOT**: discouraged behavior; deviate only with a clear reason.
- **MAY**: optional behavior.

## Rule Priority

Apply rules in this order (highest first):

1. Correctness and API contracts
2. Nullability contracts (JSpecify)
3. Architecture boundaries (MVVM)
4. Repository style and naming rules
5. Local readability preferences

## Project Baseline

### Platform and Build

- [MUST] Use **Java 25** language level.
- [MUST] Use **JavaFX** for the UI layer.
- [MUST] Use **Gradle wrapper** from the repository root.
- [SHOULD] Assume Windows 11 as primary dev/test platform; verify UI behavior on other OSes when relevant.

### Encoding and File Conventions

- [MUST] Use UTF-8 for `.java`, `.md`, and `.properties` files.
- [MUST] Keep Java properties files sorted alphabetically by key.
- [MUST] Write repository Markdown in standard GitHub Markdown.

### Language and Localization

- [MUST] Write Java comments, Javadoc, and repository Markdown in English (`Locale.en_US`).
- [MUST] Use consistent US English spelling in developer-facing text.
- [MUST NOT] Mix German or other languages in code comments, Javadoc, or Markdown, except direct external quotes.

## Core Engineering Rules

### DRY

- [MUST NOT] Duplicate business logic across multiple locations.
- [SHOULD] Extract shared logic into focused methods or classes.
- [MUST NOT] Keep copy-paste implementations when a shared abstraction is practical.

### KISS

- [SHOULD] Prefer the simplest solution that fully satisfies requirements.
- [SHOULD] Keep methods small and single-purpose.
- [MUST NOT] Introduce complex abstractions without clear ongoing value.

### Clean Code

- [MUST] Use meaningful domain names.
- [MUST] Keep classes cohesive.
- [SHOULD] Prefer readability over cleverness.
- [MUST] Remove dead code and outdated comments.

## Architecture: MVVM

- [MUST] Keep **Model** responsible for domain state and business rules.
- [MUST] Keep **View** responsible for JavaFX UI components only.
- [MUST] Keep **ViewModel** responsible for UI state, formatting, and interaction logic.
- [MUST NOT] Place domain/business logic in View classes.
- [MUST] Keep Model code independent from JavaFX UI classes.

## Code Style

### General Java Style

- [MUST] Use lowerCamelCase for method and parameter names.
- [SHOULD] Prefer clear domain terms over abbreviations.
- [MUST NOT] Add comments that only restate obvious code.
- [MUST] Annotate lambda-oriented single-abstract-method interfaces with `@FunctionalInterface`.
- [SHOULD] Prefer modern Java APIs/features when they improve clarity and are compatible with Java 25.
- [MUST] Use locale-stable normalization for technical text (for example, `toLowerCase(Locale.ROOT)`).

### Java Records

- [SHOULD] Use records for small immutable value carriers.
- [MUST] Keep record components limited to essential stored state.
- [SHOULD] Move derived/convenience behavior to small methods.
- [MUST] Use canonical/compact constructors to enforce invariants when needed.
- [MUST] Defensively copy mutable inputs (collections, maps, arrays) before storing.
- [SHOULD] Add static factories only when they provide clear value (validation, parsing, defaults).
- [SHOULD] Keep generated members (`equals`, `hashCode`, `toString`, accessors) unless semantics require override.

### Enums

- [MUST] Use enums only for fixed, closed domain sets.
- [MUST] Keep enum constants uppercase and domain-specific.
- [MUST NOT] Use unclear abbreviations.
- [SHOULD] Place enum behavior/metadata on the enum type itself.
- [SHOULD] Use exhaustive `switch` expressions for enum branching.
- [SHOULD NOT] Use a `default` branch when all constants are known.
- [MUST NOT] Persist or exchange `ordinal()`.
- [MUST] Use explicit stable codes/keys for external representations.
- [MUST] Document declaration-order semantics in type Javadoc when order matters.

### Naming Conventions

Use these method naming patterns across the codebase.

#### Construction and Factories

- [SHOULD] `create...`: instantiate and configure one object/control/view.
  Example: `createLabel()`, `createVBox()`, `createSimulationRegion()`
- [SHOULD] `of...`: validating static factory.
  Example: `InputDoubleProperty.of(...)`, `InputEnumProperty.of(...)`
- [SHOULD] `with...`: static factory-style configured variant.
  Example: `withMinStepDuration(...)`

#### UI Composition and Rendering

- [SHOULD] `build...`: assemble multiple UI parts into one region/container.
  Example: `buildMainRegion()`, `buildConfigRegion()`
- [SHOULD] `draw...`: rendering on a canvas/graphics context.
  Example: `drawCell()`, `drawCenteredTextInCell()`

#### Computation and Conversion

- [SHOULD] `compute...`: deterministic calculation from input/state.
  Example: `computeCellDimension()`, `computeCellFontSize()`
- [SHOULD] `to...`/`from...`: deterministic value/coordinate conversion.
  Example: `toCanvasPosition()`, `fromCanvasPosition()`
- [SHOULD] `as...`: conversion or alternative view accessor.
  Example: `asStringBinding()`, `asObjectProperty()`

#### Queries

- [SHOULD] `is...`: boolean state/validity/mode query.
  Example: `isIllegal()`, `isWithinBounds()`, `isModeTimed()`
- [SHOULD] `has...`: presence/availability query.
  Example: `hasKey()`, `hasEqualEdgeBehaviors()`

#### Accessors

- [SHOULD] `get...`/`set...`: standard mutable property accessors.
  Example: `getValue()`, `setValue()`, `setDirection()`
- [SHOULD] `...Property`: JavaFX property accessor naming.
  Example: `actionButtonRequestedProperty()`, `stepDurationProperty()`

#### Lifecycle and Actions

- [SHOULD] `initialize...`/`reset...`/`shutdown...`: lifecycle operations.
  Example: `initialize(...)`, `resetForTesting()`, `shutdown()`
- [SHOULD] `request...`: user-intent action trigger/flag.
  Example: `requestActionButton()`, `requestCancelButton()`

#### Accepted Compact Names

- [MAY] Utility/value APIs use concise noun-style accessors.
  Example: `locale()`, `bundle()`, `keys()`, `supportedLocales()`
- [MAY] Immutable domain types use concise derived-operation names.
  Example: `area()`, `perimeter()`, `aspectRatio()`, `opposite()`, `nextClockwise()`
- [MAY] Functional combinators use short composition names.
  Example: `and(...)`, `or(...)`, `negate()`, `compose(...)`, `identity()`
- [MUST] Java records keep generated accessor names.
  Example: `x()`, `y()`

## Nullability (JSpecify)

- [MUST] Use JSpecify as the repository nullness contract.
- [MUST] Set package defaults in `package-info.java` with `@org.jspecify.annotations.NullMarked`.
- [MUST] Treat unannotated types as non-null under `@NullMarked`.
- [MUST] Use `org.jspecify.annotations.Nullable` only for intentional nullable contracts.
- [MUST NOT] Add `@Nullable` defensively or "just in case".

## Javadoc

- [MUST] Document public types at API/module boundaries and public types with non-obvious responsibilities.
- [MUST] Document public methods at API boundaries and non-obvious behavior.
- [MAY] Use concise type-level Javadoc for small internal contracts when method naming is already clear.
- [MUST] Document intent, inputs, outputs, and significant side effects.
- [SHOULD] Describe behavior; avoid implementation detail unless it is contract-relevant.
- [MUST] Use `@param`, `@return`, and `@throws` when applicable.
- [SHOULD NOT] Add Javadoc to pure `@Override` methods when inherited docs already fully define the contract.
- [MUST] Add override Javadoc when the override adds constraints, side effects, or surprising semantics.
- [MUST NOT] Add routine `NullPointerException` notes for non-null-by-default contracts.
- [MAY] Add Javadoc to `private` members when it adds important context.
- [MUST] Keep Javadoc short, factual, and current.

## Testing

### Test Structure

- [SHOULD] Prefer package-private test classes and methods.
- [SHOULD] Prefer `final` for test classes unless extension is required.
- [MUST] Use consistent lifecycle names such as `setUpBeforeAll`, `setUpBeforeEach`, `tearDownAfterEach`.
- [MUST] Reset shared static/global state in `@BeforeEach` when used.
- [SHOULD] Use same-thread execution when shared state requires deterministic ordering.
- [MUST] Initialize JavaFX once in `@BeforeAll` via `FxTestSupport.ensureStarted()` for JavaFX tests.
- [MUST] Keep test packages `@NullMarked`; test null contracts with assertions.

### Assertions and Semantics

- [SHOULD] Use `assertAll(...)` for grouped checks of a single behavior.
- [MUST] Use `assertThrows(...)` for contract/error-path checks.
- [MUST] Follow `@SuppressWarnings` rules from the Tooling section.
- [MUST NOT] Commit empty TODO test methods.
- [MUST] Use `@Disabled("reason")` with a clear reason for temporary test disabling.

### Test Naming

- [MUST] Start test method names with `test`.
- [MUST] Continue with the behavior or scenario under test.
- [MUST] Keep names specific and readable.
- [SHOULD NOT] Add Javadoc to test classes/methods unless genuinely needed.

## Tooling

### IntelliJ IDEA and Warnings

- [MUST] Run and address relevant inspections before commit.
- [MUST] Format with project IntelliJ settings before commit.
- [MUST] Keep each `@SuppressWarnings` scope as narrow as possible.
- [MUST NOT] Add explanatory comments to `@SuppressWarnings`.
- [MUST NOT] Add or remove `@SuppressWarnings` automatically via AI/Copilot.
- [MUST] Add `@SuppressWarnings` only after human review and trade-off evaluation.
- [SHOULD] Prefer improving inspections/rules over adding suppressions when practical.

## Copilot Prompting Notes (Non-Normative)

These notes improve consistency when this file is used as prompt context.

- Prefer requests that mention target layer (`Model`, `View`, or `ViewModel`).
- Mention whether the change affects API contracts, nullability, or tests.
- Ask for deterministic outputs: exact file paths, minimal diffs, and short rationale.
- When rules conflict, cite `Rule Priority` and choose the highest-priority compliant solution.

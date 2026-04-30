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
- [MUST] Use **JavaFX 25** for the UI layer.
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
- [MUST NOT] Let the Model depend on the ViewModel or View.
- [MUST NOT] Import JavaFX scene-graph, control, property, or binding types (`javafx.scene.*`, `javafx.beans.*`,
  `javafx.stage.*`, `javafx.animation.*`) from Model packages.
- [MAY] Use neutral JavaFX value types (for example `javafx.scene.paint.Color`, `javafx.scene.image.Image`,
  `javafx.util.Duration`) inside the Model when they act as plain value carriers without UI behavior.
- [SHOULD] Expose ViewModel state as JavaFX `Property`/`ObservableValue`, or as repository wrapper types such as
  `InputDoubleProperty`/`InputIntegerProperty`/`InputEnumProperty` that encapsulate one underlying property.
- [SHOULD] Drive View updates through JavaFX bindings/property listeners; for fire-and-forget action triggers (for
  example "draw requested", "config changed"), `Runnable`/callback setters on the ViewModel are also acceptable.
- [SHOULD] Marshal Model/background updates back to the JavaFX Application Thread inside the ViewModel
  (see [Threading](#threading)), so Views can rely on FX-thread callbacks.
- [MAY] Omit a dedicated ViewModel for trivial screens with no observable state (for example a static start screen); the
  View class may then implement the relevant View interfaces directly.

## JavaFX UI

### UI Construction

- [MUST] Build the scene graph programmatically in Java; do not use FXML.
- [MUST NOT] Add `.fxml` files or `FXMLLoader` usage to the project.
- [SHOULD] Compose UIs from small, focused factory/builder methods (`createX`, `buildX`).
- [SHOULD] Use `FXComponentFactory` for standard styled controls instead of instantiating and styling them ad hoc;
  direct instantiation is acceptable for one-off layouts that the factory does not cover.
- [SHOULD] Keep `Scene`, `Stage`, and root container wiring in dedicated entry-point/View classes.
- [SHOULD] Prefer standard controls from `javafx.controls`; introduce custom controls only when standard controls are
  insufficient.

### Threading

- [MUST] Mutate the JavaFX scene graph and JavaFX `Property` values only on the JavaFX Application Thread.
- [MUST] Use `Platform.runLater(...)` to marshal updates from background threads to the FX thread.
- [SHOULD] Re-check relevant state (for example simulation state) inside the `Platform.runLater(...)` body before
  applying updates, because state may have changed in the meantime.
- [SHOULD] Use `javafx.concurrent.Task`/`Service` or a dedicated executor for long-running background work that updates
  the UI.
- [MUST NOT] Block the JavaFX Application Thread with long-running computation or I/O.
- [SHOULD] Use `Timeline`/`AnimationTimer` for periodic UI ticks and frame-driven rendering; encapsulate periodic
  simulation drivers (for example `SimulationTimer`) instead of using `Timeline` directly in Views.

### Properties and Bindings

- [SHOULD] Prefer JavaFX `Property`/`Binding` APIs over manual listener wiring for derived UI state.
- [SHOULD] Expose ViewModel state via accessor methods named `xProperty()`; the return type may be a JavaFX `Property`/
  `ObservableValue` or a repository wrapper such as `InputDoubleProperty`.
- [SHOULD] When exposing a plain JavaFX property, also provide `getX()`/`setX(...)` accessors consistent with the
  property type.
- [MAY] Expose ordered groups of related properties via a `getXProperties()` accessor returning
  `List<? extends Property<?>>` or similar; document the index meaning.
- [MUST] Keep property field declared types compatible with their public accessor return types (for example, do not
  expose a `SimpleStringProperty` field as `ObjectProperty<String>`).
- [SHOULD] Unbind explicit bindings and remove explicit listeners when their owner is shut down (for example in
  `shutdownSimulation()`); listeners attached for the lifetime of the scene graph are exempt.
- [MUST NOT] Mutate a bound property directly; update its source instead.

### CSS and Styling

- [MUST] Define visual styling in CSS files under `app/src/main/resources/css/` (for example `scene.css`, `conway.css`,
  `etpets.css`, `lab.css`).
- [MUST] Attach stylesheets via `Scene.getStylesheets()` or `Parent.getStylesheets()` using classpath URLs.
- [MUST] Resolve CSS resources through `AppResources.getCssUrl(...)` rather than calling `getClass().getResource(...)`
  ad hoc.
- [MUST] Use a shared `scene.css` for global rules.
- [SHOULD] Add a dedicated CSS file per simulation/feature when it needs visual rules beyond `scene.css`.
- [MUST NOT] Hardcode colors, fonts, or sizing in Java when an equivalent CSS rule is practical; values used only for
  `Canvas` rendering or other non-CSS-styleable APIs are exempt.
- [MUST NOT] Use inline `setStyle(...)` for styling; use style classes instead.
- [MUST] Reference CSS style classes via constants (no string literals at call sites).
- [MUST] Use `FXStyleClasses` for cross-cutting style classes; use a simulation-local `XStyleClasses` class (for example
  `ConwayStyleClasses`) for style classes that exist only inside one simulation package.
- [SHOULD] Apply style classes via `getStyleClass().add(...)` (or via factory methods that do so).
- [SHOULD] Use lower-kebab-case for CSS style class names (for example `simulation-canvas`, `config-vbox`,
  `conway-transitionrules-gridpane`).
- [SHOULD] Use JavaFX CSS variables (`-fx-*` looked-up colors) for shared theme values.

### Resources and Internationalization

- [MUST] Load images, CSS, and other UI resources from the classpath via `AppResources` (which wraps
  `getClass().getResource(...)`).
- [MUST] Centralize user-facing text in `ResourceBundle` properties files using base name `i18n.messages` and access
  them via `AppLocalization`.
- [MUST] Reference resource bundle keys via constants; do not pass string literals to `AppLocalization`.
- [MUST] Define cross-cutting keys (used from multiple simulations or from shared infrastructure) in
  `AppLocalizationKeys`.
- [MAY] Define simulation-local keys (used only inside one simulation package) as `private static final String`
  constants in the consuming View class.
- [MUST] Keep resource bundle keys sorted alphabetically (see Encoding and File Conventions).

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

## Logging

The repository uses the `AppLogger` facade (built on `java.util.logging`) as the single logging entry point.

- [MUST] Use `de.mkalb.etpetssim.core.AppLogger` for all application logging; do not call `java.util.logging`,
  `System.out`, or `System.err` directly for diagnostic output.
- [MUST NOT] Add other logging frameworks (SLF4J, Log4j, Logback, …) to the project.
- [MUST] Initialize logging exactly once at application startup via `AppLogger.initialize(...)`; use
  `AppLogger.initializeForTesting()` in tests that require logging.
- [MUST] Shut down logging via `AppLogger.shutdown()` during orderly application termination.
- [MUST] Choose log levels by intent:
    - `debug` for developer diagnostics and verbose traces.
    - `info` for lifecycle and high-level state transitions.
    - `warn` for recoverable anomalies and unexpected but non-fatal conditions.
    - `error` for failures and exceptions that affect functionality.
- [MUST] Log exceptions via `AppLogger.error(message, throwable)`; do not concatenate stack traces into the message
  manually.
- [SHOULD] Use the `Supplier<String>` overloads (`debug(() -> ...)`, `info(() -> ...)`, …) when message construction is
  non-trivial, to avoid unnecessary string building when the level is disabled.
- [SHOULD] Use the `*f` formatting overloads (`debugf`, `infof`, `warnf`, `errorf`) for parameterized messages instead
  of manual `String.format(...)` at the call site.
- [MUST] Use `Locale.ROOT` semantics for log message formatting; the `*f` overloads already do so. Do not introduce
  locale-dependent formatting in log messages.
- [MUST NOT] Log secrets, credentials, tokens, or personally identifying information.
- [SHOULD] Prefix log messages with a short, stable component tag when helpful (for example
  `"AppLogger: ..."`, `"SimulationTimer: ..."`) to aid log filtering.
- [SHOULD NOT] Log inside tight rendering or simulation step loops at `info` or higher; use `debug` and guard expensive
  message construction with the `Supplier` overloads.
- [MUST NOT] Use logging as a control-flow mechanism or as a substitute for thrown exceptions.

## Testing

### Test Framework

- [MUST] Use **JUnit 5** (JUnit Jupiter) executed via the **JUnit Platform**.
- [MUST] Declare test dependencies through the `libs.versions.toml` version catalog (for example
  `libs.junit.jupiter`, `libs.junit.platform.launcher`).
- [MUST] Run tests with `useJUnitPlatform()` in Gradle.
- [MUST] Use only `org.junit.jupiter.api.*` / `org.junit.jupiter.params.*` APIs; do not add JUnit 4 (`org.junit.*`)
  tests.
- [SHOULD] Run JavaFX-touching tests with the project's headless JVM args (`-Djavafx.headless=true`, `-Dprism.order=sw`,
  `--enable-native-access=ALL-UNNAMED`) configured in `app/build.gradle.kts`.
- [MUST NOT] Add additional test runners or frameworks (TestNG, Spock, AssertJ, Mockito, …) without prior agreement.

### Test Structure

- [SHOULD] Prefer package-private test classes and methods.
- [SHOULD] Prefer `final` for test classes unless extension is required.
- [MUST] Use consistent lifecycle names such as `setUpBeforeAll`, `setUpBeforeEach`, `tearDownAfterEach`.
- [MUST] Reset shared static or global state in `@BeforeEach` whenever such state exists.
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
- When rules conflict, cite [Rule Priority](#rule-priority) and choose the highest-priority compliant solution.
- Prefer minimal diffs; do not reformat or restructure unrelated code.
- Do not invent file paths, class names, package names, or APIs; verify them via the workspace before referencing them.
- If a rule conflict or ambiguity cannot be resolved from the available context, ask before guessing.
- Preserve existing public API signatures unless the request explicitly asks to change them.
- When adding new code, follow the conventions of the closest existing peer file (naming, layering, style class usage,
  logging) rather than introducing new patterns.
- For UI changes, state explicitly which layer is touched and which CSS file (if any) is affected.
- For logging changes, reference the `AppLogger` facade and the level semantics from the [Logging](#logging) section.

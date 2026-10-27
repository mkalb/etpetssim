# Classes with Private Constructor Overview

**Status:** done

**Related issues:** #23

Temporary working document: an inventory of all classes in the `main` source set whose only constructor is `private`
(utility classes, constant holders, static factories, and one singleton), grouped into categories, and the workflow
for aligning them. The maintainer deletes it after all categories are done and pushed.

## Workflow

### Goals

- Every class in this inventory complies with `.claude/rules`, in particular its Javadoc and its unit tests. Where a
  rule is missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- The classes are as similar as sensible and possible in style, layout, format, Javadoc, naming (class, constants, and
  methods), member order, constructor, annotations, suppressions, and test coverage: strictly within a category, and
  also across category boundaries.
- Similar or identical cases are decided together and the same way, regardless of their category; record such
  decisions under "Cross-category decisions".

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames of classes, constants, and methods, signatures, moves to
  another package) are allowed in this refactoring, but each one needs the maintainer's approval before it is
  implemented.
- Unit tests are always part of the work: check and improve the existing tests of the classes, and add missing ones as
  `junit.md` requires. Decide per class which tests it needs based on `junit.md`.
- Findings that need larger changes or mainly affect other Java types (interfaces, records, enums, or instantiable
  classes) may be moved to "Deferred findings" at the end of this plan instead of being implemented; they are handled
  one by one after the final commit.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not commit, review, or delete this plan; the maintainer requests the review and the commit explicitly after all
  categories are done (after formatting, inspecting, and testing in IntelliJ).
- The working tree contains the uncommitted changes of earlier categories. Do not revert them; if `app:test` or
  `app:javadoc` fails because of them, fix the cause.
- Topics of the sibling plans `code-interface-types.md` and `code-record-types.md` are out of scope; record
  overlapping findings under "Deferred findings" instead of implementing them.
- Start this plan only after `code-interface-types.md` and `code-record-types.md` are completely done; several classes
  belong to interfaces of the first plan (`GridInitializers`, `GridPatterns`, `SnakeMoveStrategies`) or contain
  private nested records of the second, and their decisions apply here. `code-other-classes.md` follows as the next
  plan.

### Class-specific guidelines

- `java.md` has a section "Utility Classes" (added in category 4): `final`, exactly one empty `private` constructor
  without Javadoc, and no `throw new UnsupportedOperationException` in it (affects `LangtonDirectionOptions`); refine
  it while processing the categories.
- Not every class in this inventory is a utility class: `AppLogger` is a singleton with state, and
  `AppLogger.AppLogFormatter` is a regular private `Formatter` with an implicit constructor. Respect this difference
  and record it in the rules where helpful. `AppLogFormatter` stays in the inventory only and is not aligned;
  `AppLogger` is aligned only in style and Javadoc. Aligning the state models of `AppLogger` and `AppLocalization`
  and the visibility of their `*ForTesting` methods goes to "Deferred findings".
- Constant holders (categories 2 and 3): align constant names and order across the nine `*Constraints` classes; moving
  shared config constants to `simulations.core` goes to "Deferred findings".
- Tests for constants are written only where they add clear value, e.g., relations between constants such as
  `min < max` or a default within its range; no test per constant. Refine `junit.md` accordingly.
- Class renames are allowed in this plan. Define the naming rule for these classes (e.g., `*Factory`, plural names,
  `*Logic`, `*Constraints`, `*Balance`) in `java.md`; the maintainer decides the rule and each rename.
- Static step logic (category 6): converting it to instance-based `*StepLogic` or splitting `EtpetsAgentLogic` goes
  to "Deferred findings"; align only signatures (e.g., the parameter order of `apply(..)`), Javadoc, and tests.

### Steps per category

Work on exactly one category at a time. The agent may choose the order; suggested order (the uniform factories first
to settle the basic class pattern, `core` with its stricter rules last): 4, 2, 3, 5, 7, 6, 1.

1. Set the category to `in progress` in the summary table. Read the category section, the relevant rules (`java.md`,
   `junit.md`, and the path-scoped rules for the affected packages), the CSV entries, every class source file of the
   category, and its existing tests. Also read "Cross-category decisions" and "Deferred findings" of this plan and
   the decisions recorded in `code-interface-types.md` and `code-record-types.md`.
2. Analyze: rule violations, inconsistencies within the category and with already processed categories, test gaps,
   and possible rule changes.
3. Present the results and recommendations in the chat and ask the open decision questions; mark every recommended
   API change explicitly. Do not change code before the maintainer has decided.
4. Implement the decisions, including required changes to callers, tests, and rules.
5. Run `app:test` and `app:javadoc`; both must pass before the category counts as done.
6. Update this plan: set the category to `done` in the summary table, record the decisions under the category, add
   cross-category decisions and deferred findings, and correct outdated inventory facts, including the cross-cutting
   facts in the summary (constructors, modifiers, visibility, constant-only classes, and naming suffixes).

### Documentation format

- Category decisions: a `### Decisions` subsection at the end of the category section, one bullet per decision; prefix
  a bullet with "API change:" or "Rule change:" (naming the rule file) when it applies.
- Cross-category decisions: one bullet per decision with the affected categories in parentheses, e.g.,
  "(1, 2) ...".
- Deferred findings: one `###` subsection per finding with a short title, followed by the bullets "Origin:" (category),
  "Types:" (affected types), "Problem:", and "Proposal:".

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv`, `docs/generated/JavaMethodInventory.csv` (regenerated on 2026-10-25;
the method inventory lists no fields), checked against the sources (50 classes). Each class appears in exactly one
category. Names are fully qualified; the source file is `app/src/main/java/` plus the package path. Nested classes
are marked with "nested" and named with their enclosing type. Constant counts are `static final` fields with an
initializer.

| # | Category                                                 | Count | Status |
|---|----------------------------------------------------------|------:|--------|
| 1 | Core application services                                |     6 | done   |
| 2 | Simulation constraints and balance constants             |    12 | done   |
| 3 | Shared key and style class constants                     |     4 | done   |
| 4 | Simulation factories                                     |    11 | done   |
| 5 | Engine and UI static utilities                           |     7 | done   |
| 6 | Static simulation step logic                             |     7 | done   |
| 7 | Simulation-specific catalogs and options                 |     3 | done   |
|   | **Total**                                                |    50 |        |

Cross-cutting facts:

- Every class has exactly one constructor, and it has no parameters. 49 declare it explicitly; the nested
  `AppLogger.AppLogFormatter` has an implicit default constructor that is private because the class is private.
- 49 classes are `final`; `AppLogger.AppLogFormatter` is `private static` without `final`.
- 48 classes are `public`; `EtpetsScoreMath` is package-private and `AppLogger.AppLogFormatter` is private.
- Constructor bodies: 48 are empty; only `AppLogger` initializes its singleton state. No utility class guards its
  constructor (`LangtonDirectionOptions` no longer throws, see category 7).
- 15 classes contain only constants and no methods (categories 2 and 3; `WorkCheckpoints` has a method since
  the deferred finding "Cancellation checkpoint method").
- Nested types (other than `AppLogFormatter`): the public enums `AppLocalization.CountryLocale`, `AppLogger.LogLevel`,
  and `AppStorage.OperatingSystem`, the public record `FXComponentFactory.LabeledControl`, the package-private record
  `CellNeighborhoods.CellNeighborConnection`, and the private records in `EtpetsAgentLogic`, `SugarAgentLogic`, and
  `SnakeMoveStrategies` (`MoveWeights`, `ScoredMove`), and `AppLocalization` (`LocalizationState`). They are not
  part of this inventory; enums and records follow their own plans.
- Naming suffixes: `*Factory` (13), `*Constraints` (9), `*Logic` (5), `*Balance` (3), `*StyleClasses` (2), and
  plural names (`GridInitializers`, `GridPatterns`, `AgentOrderingStrategies`, `CellNeighborhoods`, `ConwayPatterns`,
  `SnakeMoveStrategies`, `LangtonDirectionOptions`, `WorkCheckpoints`).

## 1. Core application services

Application startup and cross-cutting services in the root package and `core`; mostly static API.

| Class                                               | Nested | Constants | Members | State |
|-----------------------------------------------------|:------:|----------:|---------|-------|
| `de.mkalb.etpetssim.AppLauncher`                    |        | 0 | package-private `main(String[])`; private `parseArgumentsAndHandleHelp(..)`, `initializeAppLogger(..)`, `initializeAppLocalization(..)` | - |
| `de.mkalb.etpetssim.core.AppLocalization`           |        | 2 | nested public enum `CountryLocale`; `initialize(..)` (2), `isInitialized()`, `supportedLocales()`, `locale()`, `bundle()`, `getText(..)`, `getOptionalText(..)`, `getFormattedText(..)`; package-private `resolveLocaleFrom*(..)`, `initializeForTesting(..)`, `resetForTesting()` | static mutable `locale`, `bundle` |
| `de.mkalb.etpetssim.core.AppLogger`                 |        | 2 public (+ 4 private) | nested public enum `LogLevel`; `initialize(..)`, `isInitialized()`, `initializeForTesting()`, `shutdown()`, `debug/info/warn/error(..)` with `String`, `Supplier`, `*f(..)`, and `Throwable` variants; package-private `resetForTesting()`, `handlerCountForTesting()` | singleton: private static `APP_LOGGER` with instance fields `logger`, `initialized` |
| `de.mkalb.etpetssim.core.AppLogger.AppLogFormatter` | nested | 3 (private) | extends `Formatter`; `format(LogRecord)`; private static `displayThreadName()` | - |
| `de.mkalb.etpetssim.core.AppResources`              |        | 3 | `getBundle(..)` (2), `getCssUrl(..)`, `getImage(..)`, `getImages(..)`, `getResourceAsStream(..)`, `getResourceAsString(..)`, `getResourceAsUrl(..)` | - |
| `de.mkalb.etpetssim.core.AppStorage`                |        | 1 | nested public enum `OperatingSystem`; `getAppDataFile(..)`, `createAppDataFile(..)`, `deleteAppDataFile(..)`, `getLogFile(..)`, `createTempCacheFile(..)`; 5 private helpers | - |

Notes:

- Two state models: `AppLocalization` keeps static mutable fields, `AppLogger` wraps its state in a private singleton
  instance. Both offer `initialize(..)`, `isInitialized()`, and `*ForTesting` methods, with different visibility
  (`AppLogger.initializeForTesting()` is `public`, `AppLocalization.initializeForTesting(..)` package-private).
- `AppLogger.AppLogFormatter` is not a utility class; it is listed because its implicit constructor is private. It is
  instantiated twice in `AppLogger`.
- `AppLauncher` is a `public` class with a package-private `main(String[])`.

### Decisions

- Javadoc: paragraphs follow `java.md` in all six classes; new type Javadoc for `AppResources` and `AppStorage` that
  states what they provide; Javadoc for `AppLogger.LOG_FILE_NAME` and `DEFAULT_LOG_LEVEL`; the `*ForTesting` methods
  state their testing purpose (`core.md`); long Javadoc lines are wrapped.
- Member order: public methods, then package-private methods (including `*ForTesting`), then private helpers, then
  nested types; the `error`/`errorf` overloads of `AppLogger` stand together; `AppLauncher.main(..)` precedes its
  helpers.
- DRY and simplifications without API change: the text methods of `AppLocalization` use `locale()` and `bundle()`, and
  `initialize(String)` no longer repeats the initialization check; `AppLogger` has the helper `removeHandlers(boolean)`
  for `shutdown()`, `resetForTesting()`, and `initializeLogger(..)` (the warning about missing handlers is still logged
  before the old handlers are removed) and logs the initialization with `infof`; `AppResources.getCssUrl(..)` and
  `getImage(..)` use `getResourceAsUrl(..)` (only the text of the error message changes), `getResourceAsString(..)`
  catches `IOException` instead of `Exception`, and all messages have the prefix `AppResources: `; the private helpers
  of `AppStorage` no longer repeat the null checks, `Path.of` replaces `Paths.get`, and `deleteAppDataFile(..)` checks
  only `Files.isRegularFile(..)`; `AppLocalization.resetForTesting()` is `synchronized` like `initializeForTesting(..)`.
- The null-check messages of `AppResources` use words in sentence case ("Locale must not be null", "Relative path must
  not be null") like the other core classes.
- Comments that restated the code are removed (`AppLauncher`, `AppLogger`); `AppLogger.AppLogFormatter` is unchanged.
- Tests: `AppLoggerTest` checks level, message, and throwable of every log method with the fixture `RecordingHandler`
  instead of `assertDoesNotThrow`, and adds the level filter, `shutdown()`, the log file (`@TempDir`), the missing log
  directory (no handler, level `OFF`), and the null log level; its tests follow the production order.
  `AppLocalizationTest` uses `assertAll`, drops `assertNotNull` checks, and adds the fallback chain of
  `initialize(..)`, the formatting error of `getFormattedText(..)` (new key `formatted` in both test bundles),
  `initializeForTesting(..)`, and `resetForTesting()`. `AppResourcesTest` and `AppStorageTest` drop redundant checks;
  `AppStorageTest` adds an existing file for `createAppDataFile(..)`, the path traversal for `getLogFile(..)`, and a
  temporary file without suffix. The NPE tests stay because `requireNonNull` is a documented core contract.
- `AppLauncher` gets no test (`main(..)` launches JavaFX, the helpers are private); the Javadoc of its private
  helpers stays.
- No rule change.

## 2. Simulation constraints and balance constants

Constant-only classes in the `model` package of each simulation: `*Constraints` holds the limits and defaults of the
config values, `*Balance` holds tuning values of the simulation logic. All constants are `public`.

| Class                                                             | Constants |
|-------------------------------------------------------------------|----------:|
| `de.mkalb.etpetssim.simulations.conway.model.ConwayConstraints`   | 24 |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsConstraints`   | 30 |
| `de.mkalb.etpetssim.simulations.forest.model.ForestConstraints`   | 28 |
| `de.mkalb.etpetssim.simulations.lab.model.LabConstraints`         | 21 |
| `de.mkalb.etpetssim.simulations.langton.model.LangtonConstraints` | 20 |
| `de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConstraints` | 26 |
| `de.mkalb.etpetssim.simulations.snake.model.SnakeConstraints`     | 48 |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarConstraints`     | 55 |
| `de.mkalb.etpetssim.simulations.wator.model.WatorConstraints`     | 66 |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance`       | 95 |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarBalance`         |  5 |
| `de.mkalb.etpetssim.simulations.wator.model.WatorBalance`         |  1 |

Notes:

- Every simulation except Start has a `*Constraints` class; only Etpets, Sugar, and Wa-Tor have a `*Balance` class.
- `EtpetsBalance` (95 constants) is the largest constant holder; `WatorBalance` holds a single constant.
- The common config fields (cell shape, grid size, edge length, ...) are defined per simulation; several constants
  repeat each other (see "Deferred findings").
- The constant counts are those after the decisions below.

### Decisions

- `*Constraints` group their constants with exactly the four line comments of the configs (`// Structure`,
  `// Layout`, `// Initialization`, `// Rules`) and in the order of the `*Config` components: `SEED_INITIAL` first in
  `// Initialization`, `NEIGHBORHOOD_MODE_DEFAULT` first in `// Rules`. The sub-comments `// Initialization - Seed`,
  `// Rules - NeighborhoodMode`, and `// Rules - Movement rules string` are removed.
- API change: constant names follow the config components: `LangtonConstraints.RULE_DEFAULT` ->
  `MOVEMENT_RULES_DEFAULT` (now after `NEIGHBORHOOD_MODE_DEFAULT`), `SnakeConstraints.SNAKE_DEATH_MODE_DEFAULT` and
  `SNAKE_DEATH_MODE_VALUES` -> `DEATH_MODE_DEFAULT` and `DEATH_MODE_VALUES`.
- API change: every integer spinner value has its own `<COMPONENT>_STEP = 1`; `RULE_STEP` is removed from Snake,
  Sugar, and Wa-Tor. Wa-Tor has ten new `*_STEP` constants, which `WatorConfigViewModel` uses.
- API change: `EtpetsConstraints.TOTAL_PERCENT_MAX` and `WatorConstraints.POPULATION_SHARE_SUM_MAX_INCLUSIVE` ->
  `COMBINED_PERCENT_MAX`. The shared `EtpetsConstraints.PERCENT_MIN` and `PERCENT_MAX` of the four Etpets shares stay.
- API change: the 32 `EtpetsBalance` constants with `_RANGE_MIN`/`_RANGE_MAX` end in `_MIN`/`_MAX` (checked: no
  collisions; names such as `PET_TRAITS_REPRODUCTION_MIN_ENERGY_MIN` follow the same pattern as
  `WatorConstraints.FISH_MIN_REPRODUCTION_AGE_MIN`). The Trail constants are ordered `DEFAULT`, `MIN`, `MAX`; the
  PetEgg constants keep `MIN`, `MAX`, `DEFAULT`, because the default refers to the maximum (no forward reference).
  The Javadoc of `PetTraits` names `PET_TRAITS_*_MIN` and `PET_TRAITS_*_MAX`.
- Uniform type Javadoc: "Defaults, limits, and allowed values of the <title> configuration." for `*Constraints` and
  "Fixed balance values of the <title> simulation that are not part of its configuration." followed by
  "<p>The configurable values and their limits are in {@link <Simulation>Constraints}." for `*Balance` (new for
  `EtpetsBalance`; replaces the texts of `SugarBalance` and `WatorBalance` with their separate `<p>` lines). The
  constant Javadoc of `SugarBalance` and `WatorBalance` stays.
- The trailing comments "// Must not be 0.0d" of the two `*_REGENERATION_PER_STEP_DELTA` become Javadoc ("Maximum
  random deviation from ...; must be positive."), because `random.nextDouble(-delta, delta)` requires a positive
  bound.
- New `EtpetsBalanceTest` with four relation tests: `testRangesAreOrdered`, `testDefaultsAreWithinRanges`,
  `testRegenerationDeltasArePositiveAndBelowBase`, `testReproductionQualityWeightsSumToOne`. The helpers take the
  values as parameters to avoid constant-condition warnings. No `*ConstraintsTest`: the relations of the constraints
  are covered by `testIsValidAcceptsDefaultConstraints` of each config test. `SugarBalance` and `WatorBalance` have no
  relations to test.
- Rule change: `junit.md` ("Common Contract Patterns"): constants are tested only for relations between them that the
  code relies on, in a test across the constants of the type; no test per constant, and no own test for relations
  that other tests already cover.
- Rule change: `simulations.md` ("Configuration and Input"): conventions of `<Simulation>Constraints` (suffixes
  `_DEFAULT`, `_MIN`, `_MAX`, `_STEP`, `_VALUES`; names, groups, and order of the config components;
  `COMBINED_PERCENT_MAX`) and the role of `<Simulation>Balance` (fixed tuning values, same range suffixes).
- Style: double literals without trailing zeros (`0.0d`, `0.2d`, `0.3d`, `0.4d`) and with `.0d` for whole numbers
  (`750.0d`); a stray blank line in `EtpetsBalance` and the non-ASCII dash (U+2013) in a `SugarBalance` comment are
  removed.
- Unchanged: the line comments of the config ViewModels (e.g., `// Rules - NeighborhoodMode` in
  `WatorConfigViewModel`), which belong to `code-other-classes.md`.

## 3. Shared key and style class constants

Constant-only classes used across packages. All constants are `public`.

| Class                                                         | Annotations | Constants |
|---------------------------------------------------------------|-------------|----------:|
| `de.mkalb.etpetssim.core.AppLocalizationKeys`                 | `@SuppressWarnings("SpellCheckingInspection")` | 81 |
| `de.mkalb.etpetssim.ui.FXStyleClasses`                        | `@SuppressWarnings("SpellCheckingInspection")` | 56 |
| `de.mkalb.etpetssim.simulations.conway.view.ConwayStyleClasses` | `@SuppressWarnings("SpellCheckingInspection")` | 4 |
| `de.mkalb.etpetssim.engine.support.WorkCheckpoints`           | - | 2 (`CANCELLATION_CHECK_INTERVAL`, private `CANCELLATION_CHECK_MASK`); method `isCancellationCheckpoint(int)` |

Notes:

- `ConwayStyleClasses` is the only simulation-specific style class holder.
- `WorkCheckpoints` was the only constant-only class in `engine`; it has a method since the deferred finding
  "Cancellation checkpoint method".
- All style class constants are used in code and defined in a CSS file; all localization keys are used and exist in
  both bundles.

### Decisions

- Type Javadoc: new for `ConwayStyleClasses`; the texts of `FXStyleClasses` (no `</p>`, "sorted alphabetically"
  instead of "grouped by usage context") and `AppLocalizationKeys` (`<p>` before the first word) are rewritten. Both
  style class holders state the value format (lower-kebab-case, name in upper snake case).
- `ConwayStyleClasses` is sorted alphabetically by constant name, like `FXStyleClasses` and `AppLocalizationKeys`.
- API change: `AppLocalizationKeys.ALERT_HEADER_UNEXPECTED_ERROR` and `ALERT_TITLE_UNEXPECTED_ERROR` ->
  `ALERT_UNEXPECTED_ERROR_HEADER` and `ALERT_UNEXPECTED_ERROR_TITLE` (word order of the key). The `ENUM_LABEL_*`
  constants and the names whose keys only join words (`CELL_EDGE_LENGTH` -> `celledgelength`) stay.
- New shared test support `testsupport.ConstantTestSupport` (`stringConstants(..)`,
  `assertConstantNamesSorted(..)`, `assertConstantValuesUniqueAndNonBlank(..)`, `assertConstantValuesMatch(..)`) with
  `ConstantTestSupportTest`. `FXStyleClassesTest` uses it; new `ConwayStyleClassesTest` (same three tests, values
  with the prefix `conway-`) and `AppLocalizationKeysTest` (`testConstantsExistInAllBundles`,
  `testConstantsAreUniqueAndNonBlank`, `testConstantNamesAreAlphabeticallySorted`). `WorkCheckpointsTest` stays.
- Rule change: `java.md` ("Utility Classes"): constant holders without domain grouping sort their constants
  alphabetically by name, checked with `ConstantTestSupport.assertConstantNamesSorted(..)`.
- Deferred: a checkpoint method in `WorkCheckpoints` (see "Deferred findings").

## 4. Simulation factories

One factory per simulation package that creates the main view, plus the central `SimulationFactory`.

| Class                                                     | Members |
|-----------------------------------------------------------|---------|
| `de.mkalb.etpetssim.simulations.core.SimulationFactory`   | `createInstance(SimulationType, Stage, BiConsumer<Stage, SimulationType>)` |
| `de.mkalb.etpetssim.simulations.conway.ConwayFactory`     | `createMainView()` |
| `de.mkalb.etpetssim.simulations.etpets.EtpetsFactory`     | `createMainView()` |
| `de.mkalb.etpetssim.simulations.forest.ForestFactory`     | `createMainView()` |
| `de.mkalb.etpetssim.simulations.lab.LabFactory`           | `createMainView()` |
| `de.mkalb.etpetssim.simulations.langton.LangtonFactory`   | `createMainView()` |
| `de.mkalb.etpetssim.simulations.rebounding.ReboundingFactory` | `createMainView()` |
| `de.mkalb.etpetssim.simulations.snake.SnakeFactory`       | `createMainView()` |
| `de.mkalb.etpetssim.simulations.sugar.SugarFactory`       | `createMainView()` |
| `de.mkalb.etpetssim.simulations.wator.WatorFactory`       | `createMainView()` |
| `de.mkalb.etpetssim.simulations.start.StartFactory`       | `createMainView(Stage, BiConsumer<Stage, SimulationType>)` |

Notes:

- Uniform pattern: each simulation factory has exactly one public static method and no constants.
- `StartFactory.createMainView(..)` takes the same parameters as `SimulationFactory.createInstance(..)` minus the
  simulation type; all other factories take none.

### Decisions

- Every factory has a type Javadoc in the form of `StartFactory`: "Factory for the <simulation title> main view."
  (the English titles from `messages_en_US`, "forest-fire model" in lowercase). `createMainView()` has no Javadoc;
  the Javadoc of `SimulationFactory.createInstance(..)` stays.
- The ten `createMainView(..)` methods return the main view directly (`return new XMainView(..)`); the comment
  "// Return the main view" and `@SuppressWarnings("UnnecessaryLocalVariable")` are removed. The section comments
  `// Common`, `// ViewModel`, and `// View` stay.
- Uniform order: the `editToolBarViewModel` is created directly after the `configViewModel` (changed in Etpets);
  `SugarFactory` passes `new SugarUserAction()` on the same line as the other arguments.
- API change: the constructor of `LabMainView` takes `entityDescriptorRegistry` directly after the ViewModel, like
  the other main views (`viewModel, entityDescriptorRegistry, configView, controlView, observationView`).
- No own tests for the simulation factories; `SimulationFactoryTest` covers them through `createInstance(..)`. Its
  first two tests are merged into `testCreateInstance` (simulation type and main view class per `SimulationType`,
  without `assertNotNull` checks that `@NullMarked` already guarantees), and the start screen test is renamed to
  `testCreateInstanceInvokesStageUpdaterForStartscreen`.
- Rule change: `java.md` (new section "Utility Classes"): a utility class is `final` with exactly one empty
  `private` constructor without Javadoc, which throws no exception. All eleven factories already comply. Naming
  rules follow in later categories.
- Unchanged: the explicit types of `simulationState` and `readOnlySimulationState`, the different imports (single
  type vs. wildcard), and the line length of the main view constructor calls.

## 5. Engine and UI static utilities

Stateless helper and factory methods for the grid engine and the JavaFX UI.

| Class                                                     | Annotations | Constants | Members |
|-----------------------------------------------------------|-------------|----------:|---------|
| `de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods` | `@SuppressWarnings("MagicNumber")` | 6 (4 public) | 12 public methods: `maxNeighborCount(..)` (2), `isCellNeighbor(..)`, `isValidWithEdgeBehavior(..)`, `applyEdgeBehaviorToCoordinate(..)`, `neighborEdgeBehaviorResults(..)`, `cellNeighborsIgnoringEdgeBehavior(..)`, `cellNeighborWithEdgeBehavior(..)`, `cellNeighborsWithEdgeBehavior(..)`, `cellsByRadiusRings(..)`, `neighborCoordinatesWithinRadius(..)`, `cellNeighborDirections(..)`; package-private `edgeBehaviorActionForCoordinate(..)`, `compute*(..)`, and `getOrComputeCellNeighborConnections(..)`; nested package-private record `CellNeighborConnection` |
| `de.mkalb.etpetssim.engine.support.AgentOrderingStrategies` | - | 0 | `byPosition()`, `byDescriptorId()` |
| `de.mkalb.etpetssim.engine.support.GridInitializers`      | - | 0 | 14 public factories for `GridInitializer`: `identity()`, `fromList(..)`, `fill*(..)` (5), `place*(..)` (7), without overloads (since the deferred finding "Public methods without production callers"); 6 private helpers |
| `de.mkalb.etpetssim.engine.support.GridPatterns`          | - | 0 | `empty()`, `of(..)` (2), `singleton(..)`, `combine(..)`, `horizontalLine(..)`, `verticalLine(..)`, `rectangleOutline(..)`, `circleOutline(..)`; private `checkPositive(..)` |
| `de.mkalb.etpetssim.ui.FXComponentFactory`                | `@SuppressWarnings("MagicNumber")` | 2 (private) | `createLabel(..)`, `createHBox(..)`, `createVBox(..)`, `createLabeled*(..)` (10); 6 private helpers; nested record `LabeledControl` |
| `de.mkalb.etpetssim.ui.FXPaintFactory`                    | `@SuppressWarnings("MagicNumber")` | 2 | `computeBrightnessVariantsMap(..)`, `adjustBrightness(..)`, `create*Gradient(..)` (3), `adjustColorAlpha(..)` |
| `de.mkalb.etpetssim.ui.GridGeometry`                      | - | 12 (2 public) | public `compute*(..)` (8), `toCanvasPosition(..)`, `fromCanvasPosition(..)`, `estimateGridCoordinate(..)`; package-private `compute<Shape>Polygon(..)` and `compute<Shape>FrameSegmentPolyline(..)` (6), `isPointInTriangle(..)`, `isPointInTriangleCell(..)` |

Notes:

- Naming varies for the same role: `*Factory` in `ui` vs. plural names in `engine`; now covered by the naming rule in
  `java.md` (see the decisions).
- `GridInitializers` contained several `Runnable` overloads (cancellation checks) next to the variants without one;
  the variants without a check are removed since the deferred finding "Public methods without production callers".
- The facts in the table are those after the decisions below.

### Decisions

- Javadoc style in all seven classes: `<p>` directly before the first word after an empty `*` line, no `</p>`
  (about 80 places). New type Javadoc for `CellNeighborhoods`, `AgentOrderingStrategies`, `GridInitializers`,
  `GridPatterns`, `FXComponentFactory`, `FXPaintFactory`, and `GridGeometry` that states what the class provides
  instead of "Utility class ...".
- Unchanged: the public methods without a production caller (`GridInitializers`, `GridPatterns`,
  `AgentOrderingStrategies`, `FXPaintFactory`, `CellNeighborhoods`) stay; see "Deferred findings". They get no new
  tests; existing tests stay.
- API change: methods used only inside their class and tested directly are package-private instead of public, like
  the existing `compute*` helpers of `CellNeighborhoods` (and `ui.md` for validation helpers):
  `CellNeighborhoods.edgeBehaviorActionForCoordinate(..)`; in `GridGeometry` the six `compute<Shape>Polygon(..)` and
  `compute<Shape>FrameSegmentPolyline(..)` helpers, `isPointInTriangle(..)`, and `isPointInTriangleCell(..)`. They
  follow the public methods. The eight computation constants of `GridGeometry` (`SQRT_TWO`, ..., `THREE_HALVES`) are
  private; `MIN_EDGE_LENGTH` and `MAX_EDGE_LENGTH` stay public. `GridInitializers.randomCoordinate(..)` (no test) is
  private.
- `GridGeometry`: all `switch` over `CellShape` and `CellShapeSide` are exhaustive switch expressions without a
  `default` branch (`java.md`); the `@throws IllegalArgumentException` for unsupported values is removed. The polygon
  and frame segment helpers return `new double[][]{{x...}, {y...}}` with named local coordinates;
  `@SuppressWarnings("DuplicateExpressions")` on `computeHexagonFrameSegmentPolyline(..)` is removed because the
  duplicated expressions are gone.
- Duplicates removed: `GridPatterns` factories return `of(map)`; `FXComponentFactory` has the private helpers
  `createBoundLabel(..)` (bound label, `setLabelFor`, shared tooltip), `createBoundTextField(..)`, and
  `createTextFieldButton(..)`, used by all labeled controls (also by the two enum methods of issue #85);
  `GridInitializers` has one fill helper `fillWithSupplier(..)` (the constant fill passes `() -> fallback`) and starts
  `placeShuffledCount(..)` with `if (count > 0)`. Comments that only restate the code are removed in
  `FXComponentFactory` and `GridGeometry`.
- `GridInitializers` member order: public factories with their overloads next to each other, then the private
  helpers.
- Tests: new `AgentOrderingStrategiesTest` (`testByPosition`) and `GridPatternsTest` (`testOfWithMap`,
  `testOfWithOffsets`, both with defensive copies), only for methods with production callers. `GridInitializersTest`
  covers `identity`, `fromList` (with cancellation), `placeAllAtRandomPositions` (with cancellation, incl. the
  capacity check), `placeRandomCount`, `fillRandomPercent` (with cancellation, counts for 0.0, 0.25, 0.9, 1.0), and
  `placeShuffledCount` (with cancellation, incl. too few cells). `CellNeighborhoodsTest` covers
  `neighborEdgeBehaviorResults`, `cellNeighborsIgnoringEdgeBehavior`, `cellNeighborWithEdgeBehavior`,
  `cellNeighborsWithEdgeBehavior`, `neighborCoordinatesWithinRadius`, `cellNeighborDirections`, and
  `computeTriangleCellNeighborConnections`, and checks the relation `testDirectionRingsMatchNeighborOrder`; the test
  order follows the production order. `GridGeometryTest` loses the ten constant tests (`testSqrtTwo`, ...,
  `testMaxEdgeLength`); the edge length limits stay covered by the `computeCellDimension` tests.
- Rule change: `java.md` ("Utility Classes"): a utility class with static factories for an interface is named after
  the plural of the interface (`GridPatterns`, `GridInitializers`); classes that create JavaFX objects are named
  `FX*Factory`; other utility classes are named after their role. No class is renamed.
- `FXPaintFactory` writes `@SuppressWarnings("MagicNumber")` without braces. The class-level `MagicNumber`
  suppressions of `CellNeighborhoods`, `FXComponentFactory`, and `FXPaintFactory` stay.
- Deferred: the parameter order of `CellNeighborhoods` (see "Deferred findings").

## 6. Static simulation step logic

Stateless step logic of Etpets and Sugar as static methods, called by the simulation's step runner.

| Class                                                         | Visibility | Constants | Members |
|---------------------------------------------------------------|------------|----------:|---------|
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsAgentLogic`    | public  | 3 (private) | `apply(EtpetsGridModel, Random, EtpetsIdSequence, int, EtpetsStatistics)`; 28 private helpers; nested private records `ActionCandidate`, `ReproductionOption`, `ActionEffect` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsResourceLogic` | public  | 0 | `apply(EtpetsGridModel)` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsTerrainLogic`  | public  | 0 | `apply(EtpetsGridModel)` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsScoreMath`     | package-private | 0 | package-private `computeRawMoveScore(..)`, `computeRawReproduceScore(..)`, `computeRawEatScore(..)`, `clampToUnitRange(..)`; private `compute*(..)` helpers |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsDeterminism`   | public  | 0 | `compareCoordinates(GridCoordinate, GridCoordinate)` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarAgentLogic`      | public  | 4 (private) | `apply(SugarGridModel, SugarConfig, Random, int, SugarStatistics)`; private helpers; nested private record `AgentMoveCandidate` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarResourceLogic`   | public  | 0 | `apply(SugarGridModel, SugarConfig)` |

Notes:

- Uniform entry point `apply(..)` with the parameter order of the decisions below; Etpets passes no config.
- The other simulations implement their step logic as instances (`*StepLogic` classes implementing `AgentStepLogic` or
  `SynchronousStepLogic`, see `code-interface-types.md`, category 4).
- `EtpetsAgentLogic` is the largest utility class (28 private methods).
- `EtpetsDeterminism.compareCoordinates(..)` currently matches the natural order of `GridCoordinate`; the class is a
  placeholder for further deterministic orderings of Etpets.
- The facts in the table are those after the decisions below.

### Decisions

- Type Javadoc for the six public classes (each `*Logic` names its phase of the step) and for `EtpetsScoreMath`;
  every `apply(..)` has Javadoc (order of processing, effects, update of the statistics), as has
  `EtpetsDeterminism.compareCoordinates(..)`.
- API change: the parameters of `apply(..)` are ordered grid model, config, further inputs (`Random`,
  `EtpetsIdSequence`), step index, statistics, as in `AgentStepLogic.performAgentStep(..)`:
  `EtpetsAgentLogic.apply(gridModel, random, idSequence, stepIndex, statistics)`,
  `SugarAgentLogic.apply(gridModel, config, random, stepIndex, statistics)`, and
  `SugarResourceLogic.apply(gridModel, config)`; the two step runners are adapted.
- `EtpetsDeterminism` stays unchanged as a placeholder for further orderings (maintainer decision), including its
  use and the two `TODO` comments in `EtpetsAgentLogic.findEggPlacementCoordinate(..)`.
- The commented-out `AppLogger` trace in `EtpetsAgentLogic` and its formatting helpers stay for the further development
  (maintainer decision); the trace now calls the existing `formatActionCandidate(..)`.
- Simplifications without behavior change (the random values of a seed are consumed in the same order):
  `EtpetsAgentLogic` collects the EAT targets as `ResourceBase` per coordinate, without the second `canConsume()`
  check and the redundant `isEmpty()`/`canSelfReproduce` checks; `SugarAgentLogic` uses the helpers
  `findReachableNeighbor(..)` (the duplicated `VALID`/`WRAPPED` check) and `addMoveCandidateIfFreeWithSugar(..)`,
  stops looking in a direction at the first unreachable cell (the old loop repeated the same failing lookup), breaks
  ties by the natural order of `CompassDirection` instead of `ordinal()`, and takes `ReadableGridModel` for read-only
  helpers; the two `if` of `EtpetsTerrainLogic` are merged.
- Order and comments: the package-private methods of `EtpetsScoreMath` precede its private helpers; the helpers of
  `SugarAgentLogic` follow the call order; comments that restated the code and the step numbers of `SugarAgentLogic`
  are removed; the early return of `EtpetsAgentLogic.pickBestCandidate(..)` explains why it consumes no random value;
  trailing blank lines are removed.
- Tests: new `EtpetsAgentLogicTest` (egg hatches or keeps incubating, pet without energy is marked dead, dead pet is
  removed in the next step, pet eats an adjacent resource, pet moves to the only free neighbor),
  `EtpetsResourceLogicTest`, `EtpetsTerrainLogicTest`, `EtpetsScoreMathTest` (bonuses, penalties, and their exceptions
  of the move score; bounds and clamping of the reproduce score; hunger and overfill of the eat score;
  `clampToUnitRange`), `EtpetsDeterminismTest`, `SugarAgentLogicTest` (move toward the most sugar, harvest,
  replacement after death by energy or age), and `SugarResourceLogicTest`. The step runners only delegate and get no
  tests.
- No rule change.

## 7. Simulation-specific catalogs and options

Static catalogs of predefined values for one simulation.

| Class                                                             | Annotations | Constants | Members |
|-------------------------------------------------------------------|-------------|----------:|---------|
| `de.mkalb.etpetssim.simulations.conway.model.ConwayPatterns`      | `@SuppressWarnings("SpellCheckingInspection")` | 33 (private) | `choices()`, `availableChoices(ConwayConfig)`; private `isAvailableFor*Rules(..)`, `createPattern(..)`, and 15 pattern factories (`beehive()`, `block()`, `glider()`, ...) |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonDirectionOptions` | - | 0 | `selectableInitialDirections(..)`, `resolveInitialDirection(..)`, `validInitialDirections(..)`; private `automaticTriangleInitialDirection(..)` |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies` | - | 16 (private) | `strategies()`; private `createStrategy(..)` and scoring helpers; nested private records `MoveWeights`, `ScoredMove` |

Notes:

- Both catalogs expose their entries only through a constant list (`ConwayPatterns.choices()`,
  `SnakeMoveStrategies.strategies()`); all constants and pattern factories are private.
- `ConwayPatterns` is in `model`, `LangtonDirectionOptions` in `shared`, and `SnakeMoveStrategies` in `model.strategy`.
- The facts in the table are those after the decisions below.

### Decisions

- Type Javadoc: new for `LangtonDirectionOptions` and `SnakeMoveStrategies` (with a legend of the abbreviations in the
  display names, e.g., `F V C+`); the text of `ConwayPatterns` states what the catalog provides instead of
  "Utility class ...", including the availability rules. Javadoc paragraphs follow `java.md`. The three public methods
  of `LangtonDirectionOptions` have Javadoc (empty list for triangles, meaning of an empty `Optional`, `@Nullable`
  parameters).
- `LangtonDirectionOptions`: the constructor no longer throws `UnsupportedOperationException` (`java.md`).
- API change: the 15 pattern factories of `ConwayPatterns` are private; their Javadoc, which repeated the pattern
  strings, is removed. The private helpers follow the public methods; `createPattern(..)` returns `GridPatterns.of(map)`
  (an unmodifiable copy) instead of a lambda over a mutable `HashMap`.
- API change: `SnakeMoveStrategies.strategiesForConfig()` -> `strategies()`, which returns a constant unmodifiable list
  (noun-style catalog accessor like `ConwayPatterns.choices()`); `SnakeEditToolBarViewModel` no longer copies it.
- API change: the 15 strategy constants of `SnakeMoveStrategies` are private; the tests use `strategies()`.
- `SnakeMoveStrategies`: the six positional weights of `scoreAndPick(..)` are the private record `MoveWeights` (with
  Javadoc of its components); `createStrategy(..)` creates the `NamedMoveStrategy`; `scoreNeighbors(..)` takes the
  weights and reads the direction ring from the context. Helpers in call order; comments that restated the code are
  removed. The early returns of `pickRandomTopScoredMove(..)` stay, with a comment, because they consume no random
  value and keep the sequence of a seed stable. The scoring results are unchanged.
- Tests: new `SnakeMoveStrategiesTest` (`testStrategies` and the scoring through `scoreAndPick` and
  `pickRandomTopScoredMove`: no neighbor, only neighbor, food, ground, momentum, vertical, horizontal, clustered,
  spread, choice among ties) and `LangtonDirectionOptionsTest` (all three methods per cell shape).
  `ConwayPatternsTest`: the 15 single pattern tests and `PATTERN_COUNT` are removed; one ordered list of
  `PatternExpectation` is the source for the ids, the order, and the shapes; label keys are checked with
  `LocalizationTestSupport.assertKeysInAllBundles(..)`; the unsupported cell shapes are a `@ParameterizedTest`; the
  pattern maps are checked to be unmodifiable; test names follow `junit.md`.
- No rule change: the naming rule for utility classes and the noun-style rule for catalog values in `java.md` cover
  these classes; `LangtonDirectionOptions` keeps its name.

## Cross-category decisions

- (4, all) Rule change: `java.md` ("Utility Classes"): `final`, exactly one empty `private` constructor without
  Javadoc, and no exception thrown from it; `LangtonDirectionOptions` (category 7) must drop its
  `throw new UnsupportedOperationException(..)`.
- (4, all) Every public utility class has a type Javadoc; a method has Javadoc only when it explains more than its
  name and signature.
- (2, all) Range constants end in `_MIN` and `_MAX` (no `_RANGE_` or `_INCLUSIVE` infix); a range is ordered
  `_DEFAULT`, `_MIN`, `_MAX` (and `_STEP`) unless the default refers to a bound. Rule change: `simulations.md`
  ("Configuration and Input").
- (2, all) Constants are tested only for relations the code relies on (`junit.md`).
- (3, all) Constant holders without domain grouping are sorted alphabetically by name and tested with
  `ConstantTestSupport` (`java.md`).
- (5, all) Helper methods that are tested directly are package-private, other helpers private; helpers follow the
  public methods. Rule change: `java.md` ("Utility Classes").
- (5, all) Javadoc paragraphs follow `java.md` (`<p>` before the first word, no `</p>`); a type Javadoc states what
  the class provides, not "Utility class ...". Rule change: `java.md` ("Utility Classes").
- (4, all) Code returns a created value directly instead of through a local variable with
  `@SuppressWarnings("UnnecessaryLocalVariable")` and a comment that restates the code.
- (7, all) Catalog classes expose their entries through a noun-style accessor that returns a constant unmodifiable
  list (`choices()`, `strategies()`); constants and factories that only build the catalog are private, also when
  tests used them, and the tests go through the accessor. Rule change: `simulations.md` ("Model"), because all
  catalogs are simulation-specific.
- (6, 7) Early returns that avoid a random draw when there is no tie stay and say so in a comment, because they keep
  the random sequence of a seed stable. Rule change: `simulations.md` ("Model").
- (6, all) Static step logic orders the parameters of `apply(..)` as grid model, config, further inputs, step index,
  statistics. Rule change: `simulations.md` ("Naming").

## Deferred findings

### Shared config constants

- Origin: category 2
- Types: the nine `*Constraints` classes, `simulations.core` (e.g., `CommonConfigSettings`)
- Problem: several constants repeat each other in all or most `*Constraints` classes: `SEED_INITIAL = ""` (all nine),
  `CELL_EDGE_LENGTH_MIN = 1` (eight), `GRID_WIDTH_MIN` and `GRID_HEIGHT_MIN = GridSize.MIN_SIZE` (six), and the
  `GRID_*_STEP` values from `GridTopology`.
- Proposal: move the shared values to `simulations.core` (e.g., one shared constants class or defaults in
  `CommonConfigSettings`) and keep only simulation-specific values in the `*Constraints` classes; an API change.
- Status: done (2026-10-26). Decided by the maintainer: the shared values are not moved, because each
  `*Constraints` class shows all limits of its config in one place and the duplicates already refer to shared
  sources (`GridSize.MIN_SIZE`, `GridTopology`). API change: `SEED_INITIAL` -> `SEED_DEFAULT` in all nine
  `*Constraints` classes and the config ViewModels, as `simulations.md` requires `_DEFAULT` for every component.

### Cancellation checkpoint method

- Origin: category 3
- Types: `WorkCheckpoints`, `GridInitializers` (9 places), and the simulation managers of Etpets (8), Rebounding,
  Snake, Sugar, and Wa-Tor (2 each); `WorkCheckpointsTest`, `GridInitializersTest`
- Problem: the expression `(index & CANCELLATION_CHECK_MASK) == 0` is repeated in 24 places; the same logic exists as
  the private helper `isCancellationCheckpoint(..)` in `WorkCheckpointsTest`.
- Proposal: add `public static boolean isCancellationCheckpoint(int index)` to `WorkCheckpoints`, use it in all
  places, and make `CANCELLATION_CHECK_MASK` private; an API change. `WorkCheckpoints` would then no longer be a
  constant-only class. Decided by the maintainer (2026-10-25): not in category 3, but as a deferred finding.
- Status: done (2026-10-26). API change: `WorkCheckpoints.isCancellationCheckpoint(int)` replaces the expression
  in all 24 places (`GridInitializers` 8, `EtpetsSimulationManager` 8, the other four managers 2 each, with a
  static import instead of the mask); `CANCELLATION_CHECK_MASK` is private, `CANCELLATION_CHECK_INTERVAL` stays
  public (documented as a power of two). New type Javadoc "Cooperative cancellation checkpoints for long-running
  loops." `WorkCheckpointsTest` tests `isCancellationCheckpoint` instead of its private helper.

### Public methods without production callers

- Origin: category 5
- Types: `GridInitializers` (`border`, `checkerboard`, `constant`, `supplier`, `fillRandomly`, `fillWithProbability`,
  `placeWithProbability`, `placeRandomPercent`, and the variants without a cancellation check of `fromList`,
  `placeAllAtRandomPositions`, `placeShuffledCount`, and `fillRandomPercent`), `GridPatterns` (`empty`, `singleton`,
  `combine`, `horizontalLine`, `verticalLine`, `rectangle`, `circle`), `AgentOrderingStrategies` (`byEntityClass`,
  `byDescriptorId`), `FXPaintFactory` (`createVerticalGradient`, `createRadialGradient`), `CellNeighborhoods`
  (`isCellNeighbor`, `isValidWithEdgeBehavior`)
- Problem: these public methods have no caller in the production code; some are used only by tests (e.g.,
  `GridPatterns.empty()` and `singleton(..)` in `GridInitializersTest`, `fillRandomPercent(..)` without a check as the
  reference in two tests). The variants without a cancellation check of `GridInitializers` duplicate the used
  variants almost completely and use a different shuffle (`Collections.shuffle` instead of `shuffleCoordinates`).
- Proposal: decide per class whether the methods are a deliberate toolkit (keep, add tests) or dead code (remove,
  adapt the tests); an API change. Decided by the maintainer (2026-10-25): not in category 5, no new tests for them.
- Status `GridInitializers`: done (2026-10-26). `GridInitializers` and `GridPatterns` are a deliberate toolkit
  for future simulations (maintainer); the unused methods of `GridInitializers` were grouped and decided one by
  one:
    - API change: the variants without a cancellation check of `fromList`, `placeAllAtRandomPositions`,
      `placeShuffledCount`, and `fillRandomPercent` are removed; the variants with a check stay (correction: both
      shuffles draw the same random sequence, only the code was duplicated).
    - API change: `constant` -> `fillConstant`, `supplier` -> `fillFromSupplier`, `border` -> `placeBorder`
      (`fill...` and `place...` in `java.md`); `checkerboard` is removed (`(x + y) % 2` is a checkerboard only
      for squares).
    - Kept: `fillRandomly`, `fillWithProbability`, `placeWithProbability` (expected instead of exact count), and
      `placeRandomPercent`.
    - API change: one parameter order for all factories: amount (`count`, `percent`, `probability`), entities,
      replacement rule or fallback, `random`, `cancellationCheck`; this changes `fillRandomPercent` (callers in
      Conway, Forest, and Sugar), `fillWithProbability`, `placeWithProbability`, and `placeRandomPercent`.
      `fillRandomly(..)` names its parameter `entityGenerator`; the private fill helper is `fillCells(..)`.
    - Order: `identity`, `fromList`, `fill*`, `place*`, then the private helpers. Javadoc of all factories
      revised (row-major order of supplier and random calls, behavior at the bounds, state after an
      `IllegalStateException`); the type Javadoc explains `fill...`, `place...`, the cancellation check, and
      the parameter order.
    - Tests: `GridInitializersTest` has one test per factory (new: `fillConstant`, `fillFromSupplier`,
      `fillRandomly`, `fillWithProbability`, `placeBorder`, `placeWithProbability`, `placeRandomPercent` with
      the range check, the rounding of the fallback count above 0.8, and the check before the first cell of
      `fillRandomPercent`); the comparisons with the removed variants are gone, and the test names drop
      `WithCancellation`.
- Status `GridPatterns`: done (2026-10-26). All methods stay (maintainer):
    - API change: `rectangle` -> `rectangleOutline` and `circle` -> `circleOutline`, because they return only
      the outline; the parameter `borderEntity` is `entity` as in the other factories.
    - API change: the shape factories reject a length, width, or height below 1 and a negative radius with an
      `IllegalArgumentException` instead of returning an empty pattern; radius 0 is a single entity.
    - The lines and the rectangle outline pass their offsets to `of(entity, offsets)`; the normalization step
      of the circle outline is removed (the midpoint offsets already start at 0). The Javadoc of the circle
      outline notes that it assumes square cells.
    - The type Javadoc no longer repeats the normalization paragraph of `GridPattern`; it states which
      factories return normalized patterns. The method Javadoc is shortened.
    - Tests: `GridPatternsTest` covers every factory, including the argument checks (13 tests).
- Status of the other classes: done (2026-10-26).
    - API change: `AgentOrderingStrategies.byEntityClass()` is removed; it sorted by the simple class name, an
      implementation detail, for the same purpose as `byDescriptorId()`. `byDescriptorId()` stays; its Javadoc
      recommends `.thenComparing(byPosition())` for a deterministic order, and `AgentOrderingStrategiesTest`
      has the new `testByDescriptorId`.
    - Kept: `FXPaintFactory.createVerticalGradient(..)` and `createRadialGradient(..)` (tested counterparts of
      the used `createHorizontalGradient(..)`), `CellNeighborhoods.isCellNeighbor(..)` and
      `isValidWithEdgeBehavior(..)` (tested basic queries of the engine).
- Status: done (2026-10-26).

### Parameter order of CellNeighborhoods

- Origin: category 5
- Types: `CellNeighborhoods` and its callers in the engine and several simulations
- Problem: `maxNeighborCount(..)` takes `(cellShape, neighborhoodMode)`, while `isCellNeighbor(..)`,
  `cellNeighborsIgnoringEdgeBehavior(..)`, `neighborCoordinatesWithinRadius(..)`, and `cellNeighborDirections(..)`
  take `(..., neighborhoodMode, cellShape)`.
- Proposal: use one order (e.g., coordinate, cell shape or structure, neighborhood mode, further parameters) in all
  methods; an API change with many callers.
- Status: done (2026-10-26). The existing majority (9 of 11 public methods and all package-private helpers) becomes
  the rule: start coordinate (or coordinates), neighborhood mode, cell shape or grid structure, further parameters;
  the type Javadoc states it. API change: `maxNeighborCount(neighborhoodMode, cellShape[, radius])` (callers in
  `ConwayConfig`, `ConwayConfigViewModel`, and the tests) and `cellNeighborWithEdgeBehavior(startCoordinate,
  neighborhoodMode, structure, direction)` (callers in `LangtonStepRunner`, `ReboundingStepLogic`, and
  `SugarAgentLogic`); the `@param` tags follow the new order.

### State models of AppLogger and AppLocalization

- Origin: category 1 (planned in "Class-specific guidelines")
- Types: `AppLogger`, `AppLocalization`, and the tests that call their `*ForTesting` methods
- Problem: `AppLocalization` keeps static mutable fields, `AppLogger` a private singleton instance. The fields of
  `AppLocalization` are not `volatile`, and `locale()`, `bundle()`, and the text methods read them without
  synchronization, while `initialize(..)` is `synchronized`. `AppLogger.initializeForTesting()` is `public` (used by
  tests in many packages), `AppLocalization.initializeForTesting(..)` is package-private.
- Proposal: decide on one state model and its thread safety (e.g., `volatile` fields or an immutable state object)
  and on the visibility of the `*ForTesting` methods; an API change for the tests.
- Status: done (2026-10-26). The two state models stay: `AppLogger` wraps a thread-safe `final` JUL `Logger` and
  reads its `initialized` flag only in `synchronized` methods. `AppLocalization` keeps its state in one
  `private static volatile` field with the private record `LocalizationState(locale, bundle)`, replaced as a whole;
  reads need no lock and always see a matching pair (also in `getFormattedText(..)`), writes stay `synchronized`,
  and `isInitialized()` is no longer `synchronized`. No public API change. The visibility of the `*ForTesting`
  methods stays; rule change: `core.md` ("Documentation and Tests"): `*ForTesting` methods are package-private
  unless tests in other packages need them.

### Isolated directories for AppStorageTest

- Origin: category 1
- Types: `AppStorage`, `AppStorageTest`
- Problem: `AppStorageTest` creates and deletes files in the real per-user directories (application data, logs, and
  cache) of the machine that runs the tests.
- Proposal: let `AppStorage` resolve its directories against an injectable base directory (e.g., a package-private
  overload or a system property for tests) and use `@TempDir` in the tests; an API change.
- Status: decided, no change (2026-10-26). The injectable base directory is too much effort for the benefit: the
  tests write only into the application's own directories, which `AppLauncher` creates anyway for the log file; each
  test file has a unique name (`AppStorageTest_<UUID>`) and is deleted in `@AfterEach`, so only a hard JVM abort
  leaves files behind. Deleting the tests would drop the required core coverage, including the path traversal
  checks; every `AppStorage` method creates its directory, so no test is read-only. The unused public methods
  (`getAppDataFile`, `createAppDataFile`, `deleteAppDataFile`, `createTempCacheFile`) stay for later features
  (maintainer).

### Constructor parameters of the step runners

- Origin: category 6
- Types: `EtpetsStepRunner`, `SugarStepRunner`, `EtpetsSimulationManager`, `SugarSimulationManager`
- Problem: the constructors take `(Random, EtpetsGridModel, EtpetsIdSequence)` and `(SugarConfig, Random,
  SugarGridModel)`, while `apply(..)` of the static step logic now takes the grid model first, then the config and the
  further inputs.
- Proposal: order the constructor parameters like `apply(..)` (grid model, config, further inputs) in
  `code-other-classes.md`; an API change with one caller each.
- Status: moved (2026-10-26) to `code-other-classes.md` ("Constructor parameters of the step runners"), extended by
  `LangtonStepRunner(config, model)`, which deviates the same way.

# Other Classes and Foundation Packages

**Status:** in progress

**Related issues:** #23

Temporary working document for the code outside the `simulations` package: the alignment of its instantiable classes
that no sibling plan covers (category 1), the member order of classes in the rules and in IntelliJ (category 2), the
Javadoc rules of all rule files (category 3), and a review of each package as a whole (category 4). Everything must be
settled before `code-simulation-classes.md` starts, which took over the classes of the `simulations` package
(2026-10-29) and applies the decisions of this plan. The maintainer deletes this plan together with
`code-simulation-classes.md` and the sibling plans.

## Workflow

### Goals

- Every class in this plan complies with `.claude/rules`, in particular its Javadoc and its unit tests. Where a
  rule is missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- The classes are as similar as sensible and possible in style, layout, format, Javadoc, naming (class, fields, and
  methods), member order, constructors, annotations, suppressions, and test coverage.
- The rules on member order and Javadoc fit each other, the formatter, and the build checks, and are practical for
  agents and the maintainer.
- Each package outside `simulations` is consistent and cohesive as a whole, across all its types (classes,
  interfaces, records, and enums), not only per kind of type as in the sibling plans.
- Similar or identical cases are decided together and the same way; record such decisions under "Cross-category
  decisions", which also apply to `code-simulation-classes.md`.

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames of classes, fields, and methods, signatures, moves to another
  package) are allowed in this refactoring, but each one needs the maintainer's approval before it is implemented.
- Unit tests are part of the work for the non-JavaFX classes: check and improve the existing tests of the classes, and
  add missing ones as `junit.md` requires. The JavaFX classes keep their tests, improved where cheap.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not commit, review, or delete this plan; the maintainer requests the review and the commit explicitly (after
  formatting, inspecting, and testing in IntelliJ).
- Rule changes need the maintainer's approval like API changes. The IntelliJ settings belong to the maintainer: the
  agent proposes changes to them and does not assume settings it has not been shown.

### Order

- The sibling plans `code-interface-types.md`, `code-record-types.md`, `code-private-constructor-classes.md`, and
  `code-enum-types.md` are done; their decisions apply here. They stay in `docs/planning` as reference until
  `code-simulation-classes.md` is done.
- Categories 1 to 3 are done and committed (category 1 in `fc3cff53`, category 3 in `af48c441`); category 4 is
  done and committed group by group. The deferred findings are decided and done (2026-10-30).
- Categories 2 and 3 settle the rules before category 4 applies them; category 2 comes first, because it also
  changes code. Category 4 reviews the packages one group at a time.
- `code-simulation-classes.md` starts only after all categories of this plan are done.

### Steps per category

Categories 1 and 4 follow these steps; categories 2 and 3 adapt them as described in their sections.

1. Set the category to `in progress` in the summary table. Read the category section, the relevant rules (`java.md`,
   `junit.md`, and the path-scoped rules for the affected packages), the CSV entries, every class source file of the
   category, and its existing tests. Also read "Cross-category decisions" and "Deferred findings" of this plan and the
   decisions recorded in the sibling plans.
2. Analyze: the findings listed under the category, rule violations, inconsistencies, test gaps, and possible rule
   changes.
3. Present the results and recommendations in the chat and ask the open decision questions; mark every recommended
   API change explicitly. Do not change code before the maintainer has decided.
4. Implement the decisions, including required changes to callers, tests, and rules.
5. Run `app:test` and `app:javadoc`; both must pass before the category counts as done.
6. Update this plan: set the category to `done` in the summary table, record the decisions under the category, mark
   its findings as done, add cross-category decisions and deferred findings, and correct outdated inventory facts.

### Documentation format

- Findings: a `### Findings` subsection per category, one `####` subsection per finding with a short title, followed
  by the bullets "Origin:", "Types:", "Problem:", and "Proposal:"; after the decision a bullet "Status:" with the date.
- Category decisions: a `### Decisions` subsection at the end of the category section, one bullet per decision; prefix
  a bullet with "API change:" or "Rule change:" (naming the rule file) when it applies.
- Cross-category decisions: one bullet per decision with the affected categories in parentheses, e.g., "(1, all)";
  "all" includes the categories of `code-simulation-classes.md`.
- Deferred findings: one `###` subsection per finding in the same format as the findings.

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv` and `docs/generated/JavaMethodInventory.csv` (regenerated on
2026-10-28): 12 classes outside `simulations` in category 1. The table of category 1 is generated from the CSV files
and updated after the category; the counts of category 4 are taken from the source tree (2026-10-29).

Table format:

- Class: relative to `de.mkalb.etpetssim`; the source file is `app/src/main/java/` plus the package path.
- Modifiers: the class is `public` unless stated otherwise; `-` means neither `final` nor `abstract`.
- Supertypes: without type arguments. Constructors: parameter types without type arguments; `public` unless stated
  otherwise.
- Methods: the number of declared methods per visibility, without inherited ones; nested types follow.
- Tests: a test class named after the class; indirect coverage is listed in the notes.

| # | Category                                  | Count        | Status      |
|---|-------------------------------------------|-------------:|-------------|
| 1 | Engine, core, UI, and application classes |   12 classes | done        |
| 2 | Member order: rules and IntelliJ          |            - | done        |
| 3 | Javadoc rules of all rule files           | 5 rule files | done        |
| 4 | Package reviews outside `simulations`     |   9 packages | done        |

## 1. Engine, core, UI, and application classes

Instantiable classes outside `simulations`.

### Quality bar

These packages are the foundation of the application; this category needs the highest quality (maintainer,
2026-10-28). In addition to the steps per category:

- Check every class completely, not only the listed findings: class, method, parameter, field, and constant names
  (`java.md`, "Naming Conventions"), Javadoc of every public and protected type and of every member whose contract is
  not obvious (`java.md`, "Javadoc"; `core.md` for `AppArgs`; `ui.md` for the `ui` classes), style, member order,
  nullability, logging, suppressions, and error messages.
- Tests: every public method of the non-JavaFX classes is tested, including its boundary cases and the exceptions of
  its contract (`junit.md`); existing tests are checked for missing cases, redundant checks, names, and order. The
  JavaFX classes `AboutDialog`, `FXGridCanvasPainter`, and `SimulationTimer` keep their tests, improved where cheap.
- Work in three groups, each presented and decided on its own in the chat before it is implemented:
  `engine.executor` (4), `engine.model` and `engine.model.entity` (3), and `core`, `ui`, and the root package (5).
  `app:test` and `app:javadoc` run after each group.
- The review after this category also checks the decisions against the goals of this plan, because they set the
  standard for `code-simulation-classes.md`.

### Inventory

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `ExtraterrestrialPetsSimulation` | final | Application | implicit | 2/0/0/9 | `ExtraterrestrialPetsSimulationTest` |
| `core.AppArgs` | final | - | `(String[])` | 8/0/1/1 (1 static); nested public enum `Key` | `AppArgsTest` |
| `engine.executor.AsynchronousStepRunner` | final | SimulationStepRunner | `(WritableGridModel, Predicate, Comparator, AgentStepLogic)` | 2/0/0/0 | `AsynchronousStepRunnerTest` |
| `engine.executor.SynchronousStepRunner` | final | SimulationStepRunner | `(WritableGridModel, SynchronousStepLogic)` | 2/0/0/0 | `SynchronousStepRunnerTest` |
| `engine.executor.DefaultSimulationExecutor` | final | SimulationExecutor | `(SimulationStepRunner, Supplier, SimulationTerminationCondition, C)` | 4/0/0/0 | `DefaultSimulationExecutorTest` |
| `engine.executor.TimedSimulationExecutor` | final | SimulationExecutor | `(SimulationExecutor)` | 6/0/0/0 | `TimedSimulationExecutorTest` |
| `engine.model.ArrayGridModel` | final | WritableGridModel | `(GridStructure, ENT)` | 23/0/0/1 | `ArrayGridModelTest` |
| `engine.model.SparseGridModel` | final | WritableGridModel | `(GridStructure, ENT)` | 23/0/0/1 | `SparseGridModelTest` |
| `engine.model.entity.GridEntityDescriptorRegistry` | final | - | private `(int)` | 5/0/0/1 (2 static) | `GridEntityDescriptorRegistryTest` |
| `ui.AboutDialog` | final | - | `(List, HostServices)` | 1/0/1/8 (1 static); nested private record `ManifestLine` | `AboutDialogTest` |
| `ui.FXGridCanvasPainter` | final | - | `(Canvas, GridStructure, double)` | 31/0/2/1 (1 static) | `FXGridCanvasPainterTest` |
| `ui.SimulationTimer` | final | - | `(Runnable)` | 3/0/0/0 | `SimulationTimerTest` |

Notes:

- The four `engine.executor` classes had no tests before this plan; `SimulationExecutorTest` tests only the default
  methods of the interface.
- `ExtraterrestrialPetsSimulation` is the JavaFX `Application` with an implicit constructor.
- `FXGridCanvasPainter` has the most public methods (31); its `@SuppressWarnings("MagicNumber")` is on three
  methods instead of the class since this plan.

### Findings

#### Tests for the executor classes and step runners

- Origin: `code-interface-types.md`, category 5 (executors); inventory of this plan (step runners)
- Types: `DefaultSimulationExecutor`, `TimedSimulationExecutor`, `AsynchronousStepRunner`, `SynchronousStepRunner`
- Problem: The executor classes and the engine step runners have no tests of their own; the default methods of
  `SimulationExecutor` are tested in `SimulationExecutorTest`. The step runners contain engine logic (agent order and
  filter, swapping of the synchronous grid models) that is only covered through the golden values of the simulations.
- Proposal: Add `DefaultSimulationExecutorTest`, `TimedSimulationExecutorTest`, `AsynchronousStepRunnerTest`, and
  `SynchronousStepRunnerTest` with small grids and stub step logic. The tests of `AbstractTimedSimulationManager`
  follow in `code-simulation-classes.md`, category 1.
- Status: done (2026-10-29)

#### Average of the timed executor

- Origin: analysis of category 1
- Types: `TimedSimulationExecutor`
- Problem: `stepTimingStatistics()` divides the timed durations by the step count of the delegate. If the delegate
  executed steps before it was wrapped, the average falls below the minimum (or the current duration is still
  `Long.MIN_VALUE`), and the constructor of `StepTimingStatistics` throws; the Javadoc only called the average
  inexact. All managers wrap a new executor, so the case does not occur today.
- Proposal: Count the timed steps in the decorator, or document the precondition.
- Status: done (2026-10-29); the precondition is documented on the constructor and as `@throws` on
  `stepTimingStatistics()`, the behavior is unchanged.

#### Unused accessors and constructor of the executor classes

- Origin: analysis of category 1
- Types: `TimedSimulationExecutor`, `SynchronousStepRunner`, `AsynchronousStepRunner`
- Problem: `TimedSimulationExecutor.currentStepNanos()` has no caller and returns `Long.MIN_VALUE` before the first
  step, while `stepTimingStatistics().currentNanos()` returns `0`. `SynchronousStepRunner.nextGridModel()` has no
  caller and exposes the buffer that is cleared after each step. The `AsynchronousStepRunner` constructor with a
  context-dependent `Function<C, Comparator>` is only used by the constructor with a fixed `Comparator`.
- Proposal: Remove the two accessors and the `Function` constructor. An API change.
- Status: done (2026-10-29)

#### Duplicated coordinate checks of the grid models

- Origin: analysis of category 1
- Types: `ArrayGridModel`, `SparseGridModel`
- Problem: The bounds check with the same `IndexOutOfBoundsException` message is repeated five times in each class.
- Proposal: A private `checkCoordinate(GridCoordinate)` per class (or a shared `GridStructure.checkCoordinate(..)`).
- Status: done (2026-10-29); private method per class, no API change.

#### Mutability of the filtered coordinates

- Origin: analysis of category 1
- Types: `SparseGridModel`, `ReadableGridModel`
- Problem: `SparseGridModel.filteredCoordinates(..)` returned the immutable `List.of()` for an empty map and a mutable
  list otherwise; `ArrayGridModel` always returns a mutable list, and the Javadoc did not say "mutable" as for the
  other list methods.
- Proposal: Always return a mutable list and document it in `ReadableGridModel`.
- Status: done (2026-10-29)

#### Factories and mutators of the descriptor registry

- Origin: analysis of category 1
- Types: `GridEntityDescriptorRegistry`, the nine `*Factory` classes of the simulations
- Problem: `of(GridEntityDescriptorProvider...)` and `ofArray(GridEntityDescriptorProvider[])` did the same; the
  public constructor and the public `register(..)` with eight parameters were only used by the registry itself and
  its test.
- Proposal: Keep `of(..)` and `ofCollection(..)`, make the constructor and `register(..)` private. An API change.
- Status: done (2026-10-29)

#### Unused API of the argument parser and the canvas painter

- Origin: analysis of category 1
- Types: `AppArgs`, `FXGridCanvasPainter`
- Problem: `AppArgs.getInt(..)`, `containsKey(..)`, and `keys()` have no caller in main sources. Seven public
  methods of `FXGridCanvasPainter` (`canvas()`, `graphicsContext2D()`, `isOutsideGrid(..)`, `clearGridBackground()`,
  `drawScaledCell(..)`, `drawTriangleMatchingCellWidth(..)`, `drawHexagonMatchingCellWidth(..)`) have no caller, and
  four (`drawRectangle(..)`, `drawPolygon(..)`, `drawPolyline(..)`, `drawCenteredTextAt(..)`) are only used by the
  painter itself.
- Proposal: Remove `getInt(..)`; remove the unused painter methods and make the internal ones private.
- Status: rejected (2026-10-29); both classes keep their complete API as reusable infrastructure.

#### Lookup name of the command-line keys

- Origin: analysis of category 1
- Types: `AppArgs.Key`
- Problem: `fromString(String)` does not follow the `from<Code>(String)` pattern of `java.md`.
- Proposal: Rename it to `fromKeyName(String)`. An API change.
- Status: done (2026-10-29)

### Decisions

Group `engine.executor` (2026-10-29):

- API change: `TimedSimulationExecutor.currentStepNanos()` and `SynchronousStepRunner.nextGridModel()` are removed.
- API change: `AsynchronousStepRunner` has only the constructor with a fixed `Comparator`, stored directly.
- `TimedSimulationExecutor` documents that the delegate must not have executed a step before it is wrapped; its
  type Javadoc names all tracked values.
- Redundant field initializations (`stepCount = 0`, `sumStepNanos = 0`) and the comment in
  `DefaultSimulationExecutor.executeStep()` that restated the field Javadoc are removed; `SynchronousStepRunner`
  imports `engine.model.*` instead of linking `ReadableGridModel` fully qualified.
- New tests: `DefaultSimulationExecutorTest`, `TimedSimulationExecutorTest`, `AsynchronousStepRunnerTest`, and
  `SynchronousStepRunnerTest`, each with its own private `TestEntity` fixture like `SimulationExecutorTest`.

Group `engine.model` and `engine.model.entity` (2026-10-29):

- `ArrayGridModel` and `SparseGridModel` check coordinates through a private `checkCoordinate(..)`;
  `ArrayGridModel.clear()` delegates to `fill(defaultEntity)`; the wrong comment in `ArrayGridModel.copy()` (entities
  as immutable value types) and comments that only restated the snapshot contract of `ReadableGridModel` are removed;
  the two branches with the same result in `SparseGridModel.filteredCoordinates(..)` are merged.
- `SparseGridModel.filteredCoordinates(..)` and `filteredCells(..)` no longer special-case an empty map; both always
  return a mutable list, and `ReadableGridModel.filteredCoordinates(..)` documents it.
- API change: `GridEntityDescriptorRegistry.ofArray(..)` is removed (the factories call `of(X.values())`); the
  constructor and `register(..)` are private, so the registry cannot be changed after its creation, and
  `@SuppressWarnings("UnusedReturnValue")` is gone with the public method. The type Javadoc states the
  `AppLocalization` precondition and that the last provider of a descriptor ID wins.
- Tests: `WritableGridModelTest` groups its checks with `assertAll(..)` and adds the mutable empty results of
  `filteredCoordinates(..)` and `filteredCells(..)`, the empty sorted result, `fill(..)` with the default entity, and
  the structure, default entity, and unchanged original of `copyWithDefaultEntity()`; `SparseGridModelTest` renames
  `testFilteredCoordinatesNonDefaultFastPath`; `GridEntityDescriptorRegistryTest` goes through the factories, tests
  the replacement through `of(..)`, adds the positive `findByDescriptorId(..)` case, and follows the declaration
  order.

Group `core`, `ui`, and the root package (2026-10-29):

- `AppArgs`, `FXGridCanvasPainter`, `AboutDialog`, and `SimulationTimer` keep their public API, also the methods
  without a caller in main sources.
- API change: `AppArgs.Key.fromString(..)` is renamed to `fromKeyName(..)`.
- `AppArgs`: the constructor delegates each argument to a private `parseArgument(..)` with early returns; a non-flag
  key without a value is now reported through `AppLogger` like the other ignored arguments; `printHelp(..)` formats
  with `Locale.ROOT` and catches `IOException` instead of `Exception`; the type Javadoc states that a flag with an
  unrecognized value is active; the static `parseBooleanValue(..)` precedes the instance methods (category 2).
- `FXGridCanvasPainter`: the type Javadoc describes the stateful painter instead of a "utility class"; the
  class-level `@SuppressWarnings("MagicNumber")` is narrowed to `drawRectangle(..)`, `drawCircle(..)`, and
  `computeCenteredTextOffset(..)`, as confirmed by the IntelliJ inspection; `toString()` follows the other methods,
  and the private static helper precedes the instance methods (category 2); comments that restated the constructor
  are removed.
- `SimulationTimer`: Javadoc without `</p>` and `<p>` before `<pre>`, no constructor Javadoc, `{@code true}`.
- `AboutDialog`: the static `formatReadmeText(..)` precedes the instance methods (category 2); `Map.of()` instead of
  `Collections.emptyMap()`; `value` instead of `v`.
- `ExtraterrestrialPetsSimulation`: `start(..)` and `stop()` precede the private methods; the routine
  `Objects.requireNonNull(..)` checks of private methods and the fully qualified `Priority` are removed;
  `resolveSimulationType(..)` drops the always-`true` parameter together with `@SuppressWarnings("SameParameterValue")`.
- Tests: `AppArgsTest` follows the declaration order, groups its checks with `assertAll(..)`, and adds tests for
  `getValue(..)`, `getBoolean(..)`, `getInt(..)` (including invalid and overflowing values), `containsKey(..)`,
  `keys()`, the exception of `isFlagActive(..)`, key case, unknown key names, and `isFlag()`; the main tests of
  `FXGridCanvasPainterTest` and `AboutDialogTest` are renamed to `testConstructor` and `testFormatReadmeText`.

Review of category 1 (2026-10-29):

- The Javadoc of `AsynchronousStepRunner` states that the agent cells are a snapshot of the step start and can be
  outdated when their turn comes, as `WatorStepLogic` and `ReboundingStepLogic` already handle.
- Rule change: `java.md` ("Code Style"): fields are not initialized explicitly to their default value.
- Rule change: `java.md` ("Javadoc", "Style"): a constructor summary starts with "Creates a ..."; the literals
  `true`, `false`, and `null` are written with `{@code ...}`.
- Rule change: `core.md` ("Class Structure"): core classes keep their complete public API, also without a current
  caller.
- Rule change: `ui.md` ("Grid Geometry and Rendering"): `FXGridCanvasPainter` keeps its public accessors and drawing
  methods, also without a current caller.
- Moved to category 2 and decided there: the member order of classes. The IntelliJ formatting had moved helpers in
  `AppArgs` and `FXGridCanvasPainter`, which contradicted the then proposed member order and the declaration order of
  their tests.

## 2. Member order: rules and IntelliJ

The order of the members in classes and test classes: what the rules require, what the IntelliJ arrangement does, and
which of the two is the source of truth. Main and test sources of the packages outside `simulations` are in scope;
`code-simulation-classes.md` applies the result to `simulations`.

### Current state

- Rules:
    - `java.md` ("Code Style"): interface members as constants, static methods (including private static helpers),
      abstract methods, then default methods.
    - `java.md` ("Utility Classes"): methods as public, package-private, then private, followed by nested types.
    - `junit.md` ("JUnit Class Shape", "Test Data and Helpers", "Organization"): reused constants near the top,
      helpers before tests, test methods in the declaration order of the production methods, test-only nested types
      at the bottom.
    - No rule for the methods of other classes; the open cross-category decision below proposes one.
- IntelliJ arrangement of the scheme "Default IDE" (the IntelliJ default, shown by the maintainer on 2026-10-29, not
  part of the repository):
    - Matching rules: static fields (`static final` before `static`, each by visibility public, protected,
      package-private, private), static initializer, instance fields (`final` before the others, each by visibility),
      initializer, constructor, static methods (rule 21), methods, then enums, interfaces, static classes, and classes;
      records have no rule of their own. Methods within a rule keep their order.
    - Grouping rules: "Keep getters and setters together" on; "Keep overridden methods together" and "Keep dependent
      methods together" off.
- Observations:
    - The formatting of category 1 moved methods in `AppArgs` (e.g., `parseBooleanValue(..)` between `getValue(..)`
      and `getBoolean(..)`, `getInt(..)` after `keys()`) and in `FXGridCanvasPainter` (e.g., the private static
      `scalePolygonFromCenter(..)` among the `draw*` methods). The arrangement rules do not explain it: rule 21 would
      put all static methods directly after the constructor.
    - Other classes keep static methods after instance methods (e.g., `AboutDialog.formatReadmeText(..)` after
      `show()`), so the arrangement is apparently not applied on every formatting.
    - `AppArgsTest` and `FXGridCanvasPainterTest` no longer follow the declaration order of their production classes.
    - The maintainer switched to the IntelliJ scheme "Default IDE" with the new `.editorconfig` (2026-10-29). The
      reformat of all files with "Rearrange code" moved `AppArgs.parseBooleanValue(..)`,
      `AboutDialog.formatReadmeText(..)`, and `FXGridCanvasPainter.scalePolygonFromCenter(..)` directly after the
      constructor again; the arrangement rules are part of the IDE scheme, not of `.editorconfig`.

### Procedure

1. Analysis (agent): find the cause of the moves in `AppArgs` and `FXGridCanvasPainter` with the maintainer (e.g.,
   "Rearrange code" only on changed lines); list the conflicts between the rules and the arrangement (e.g., private
   static helpers and package-private static methods that rule 21 moves before the public instance methods); scan the
   main and test sources outside `simulations` for classes whose member order contradicts the candidate rule, with a
   script in the scratchpad.
2. Decisions (maintainer): the source of truth (a rule in `java.md` or the IntelliJ arrangement); the rule text for
   classes, including constructors, overridden lifecycle methods, `toString()`, and helpers; changes to the
   arrangement (e.g., removing rule 21) and whether the settings are documented in the repository (e.g., in
   `docs/development`); whether the test order in `junit.md` stays tied to the declaration order; whether an
   automated check is worth it (no new build dependency).
3. Implementation: rule changes; reorder the deviating classes and their tests, at least `AppArgs`,
   `FXGridCanvasPainter`, `AppArgsTest`, and `FXGridCanvasPainterTest`; run `app:test`.
4. Update the cross-category decision on member order and record the decisions here.

### Decisions

Formatter settings (2026-10-29):

- `.editorconfig` in the repository root (`root = true`) holds the formatter settings, together with the IntelliJ
  scheme "Default IDE". It contains only the basic EditorConfig properties (UTF-8, 4 spaces, final newline, no
  trailing whitespace, 120 characters) and the IntelliJ settings that differ from the defaults; the 1159-line export of
  the former scheme, which had been taken over from another project, is reduced to these.
- Java uses single-type imports: `ij_java_class_count_to_use_import_on_demand` and
  `ij_java_names_count_to_use_import_on_demand` are `999`, and no package is imported on demand. The reformat replaced
  all wildcard imports; a static import of the nested type `GridModelTestSupport.TestEntity` in four tests is a
  single-type import. Rule change: `java.md` ("Code Style") requires single-type imports, a regular import for
  nested types, and the import groups and order of IntelliJ, instead of keeping existing wildcard imports.
- Chained method calls are no longer aligned and use the continuation indent of 8 spaces, which `.editorconfig` sets
  explicitly for Java (IntelliJ used 4 without it).
- Kept from the former scheme: one blank line after the class header and before the class end, at most one blank line
  in code and declarations, line comments as `// text` in the code column, no control statements in one line, simple
  lambdas and classes in one line, `extends`, `implements`, and `permits` wrapped when too long, Javadoc without empty
  tags, and simple class names in Javadoc where imported. Invalid Javadoc tags are kept (IntelliJ default), so that
  `app:javadoc` reports them instead of the reformat deleting them with their text.
- `.editorconfig` sets no `end_of_line`; Git normalizes line endings through `.gitattributes` and `core.autocrlf`.
  The Java test files with LF in the working copy are converted to CRLF.
- CSS uses 4 spaces like the other files; JSON keeps 2 spaces, because Claude Code writes `.claude/settings.json` with
  2 spaces. `.properties` files keep aligned delimiters and their blank lines. Kotlin follows the IntelliJ default
  "Kotlin official" (continuation indent 4).
- Markdown is not formatted by IntelliJ: its formatter joined lines starting with `(` or `**` with the previous line,
  independent of `ij_markdown_wrap_text_if_long` and `ij_markdown_force_one_space_between_words`. The maintainer
  excludes `*.md` with "Do not format" in the IDE; `AGENTS.md` ("Encoding and File Conventions") describes how to
  format Markdown by hand, and the Markdown files outside `docs/planning` follow it (`41ea4605`).
- `app:test` and `app:skillTest` pass after the reformat.
- `CellConnectionType.java` got the final newline that the reformat had not added; the empty `etpets.css` stays
  empty.
- The member order of the three methods moved by "Rearrange code", the arrangement rules of the IDE scheme, and the
  reference to `.editorconfig` in `java.md` are decided below.

Member order (2026-10-29):

- Analysis (scan of the main sources with a script in the scratchpad): all 50 classes, enums, and records with both
  static and instance methods (23 outside `simulations`, 27 inside) declare the static methods first, as rule 21 of
  the arrangement does. The member order proposed in category 1 (by visibility regardless of `static`) contradicted
  11 types outside `simulations`; the order decided here contradicted 4 (`AppArgs`, `FXGridCanvasPainter`,
  `GridCoordinate`, `FXComponentFactory`). In `simulations`, 49 of 232 types deviate, almost only in the visibility
  order of the methods (private helpers next to their callers, protected before public).
- Rule change: `java.md` ("Code Style") is the source of truth for the member order of classes, enums, and records and
  follows the IntelliJ default arrangement: static fields, static initializers, instance fields, instance
  initializers, constructors, static methods, instance methods, then nested types; within the static and within the
  instance methods public, protected, package-private, then private; overrides count as public, `toString()` directly
  precedes the private instance methods, and overloads and a getter with its setter stay together. Test classes order
  their methods as `junit.md` describes. The arrangement of "Default IDE" stays unchanged and is not documented in the
  repository; "Rearrange code" keeps the order but does not sort the methods by visibility.
- Rule change: `java.md` ("Utility Classes") no longer repeats the method order, which the general rule covers.
- Rule change: `java.md` ("Code Style") refers to `.editorconfig` and the IntelliJ defaults for formatting.
- Rule change: `junit.md` ("Organization") keeps the test order tied to the declaration order: constructor tests
  (including `testRecord`) first, then the tests of the production methods in their declaration order, with all tests
  of a method next to each other; "factories first" is dropped, because static factories follow the constructor
  anyway. Static and instance overloads (e.g., `GridTopology.requiredWidthMultiple(..)`) are declared apart, so their
  tests are too.
- `AppArgs.parseBooleanValue(..)`, `AboutDialog.formatReadmeText(..)`, and
  `FXGridCanvasPainter.scalePolygonFromCenter(..)` stay directly after the constructor as static methods.
- Reordered main sources: `AppArgs` (`getInt(..)` after `getBoolean(..)` again, `toArgumentString()` before
  `toString()`), `FXGridCanvasPainter` (`drawCenteredTextWithBackgroundAt(..)` before the package-private methods),
  `GridCoordinate` (private helpers at the end), and `FXComponentFactory` (private static helpers after the public
  methods).
- Reordered tests: `AppArgsTest`, `AppResourcesTest`, `CellDimensionTest` (helpers before the tests),
  `CompassDirectionTest`, `FXGridCanvasPainterTest`, `GridGeometryTest`, and `SimulationTimerTest`.
- No automated check of the member order: it would need a new build dependency or a hand-written Java parser in a
  test; "Rearrange code" and reviews are enough.
- `app:test` and `app:javadoc` pass.

## 3. Javadoc rules of all rule files

A review of all Javadoc rules: they fit each other, the build checks, and the code, and they are sensible and
practical. The result is a set of rule changes; the code outside `simulations` follows them in category 4, the
`simulations` package in `code-simulation-classes.md`.

### Sources

- `java.md`: "Javadoc" ("Coverage", "Tags", "Style") and the Javadoc parts of "Java Records" (record components,
  compact constructors, the "The record wraps/groups/holds ..." sentence), "Enums" (declaration-order semantics),
  and "Utility Classes" (type Javadoc, private constructor).
- `core.md`: "Null Validation" (`NullPointerException` in Javadoc) and "Documentation and Tests" (public classes and
  methods always have Javadoc, `*ForTesting()` methods document their purpose).
- `junit.md`: Javadoc in test sources ("Test Method Style"), manual test utilities, and the enum tests that depend on
  the type Javadoc ("Common Contract Patterns").
- `ui.md` and `simulations.md`: no Javadoc rules so far; check whether they need any (e.g., for the many `protected`
  members of the base views and ViewModels).
- `AGENTS.md` (English text, `app:javadoc`) and the build: `app:javadoc` runs doclint with `all,-missing` and
  `-Werror`, so missing comments are not reported.

### Checks

- Consistency: e.g., "Remove existing Javadoc only when it clearly adds no value; keep it when in doubt"
  (`java.md`) against the cross-category decision to remove constructor Javadoc that only restates; "only when it
  explains something" (`java.md`) against "public methods require Javadoc" (`core.md`); no routine
  `NullPointerException` notes (`java.md`) against the documented `NullPointerException` of the public core
  contract (`core.md`).
- Completeness: `protected` members meant for subclasses, type parameters, `@see`, `{@inheritDoc}`, package-private
  types and members, nested types, overridden methods, and the summary sentences of fields and constants.
- Practicality: each rule can be applied without guessing and checked in a review, by doclint, or by an IntelliJ
  inspection; examples are short and correct; no rule only restates what doclint enforces.
- Structure: each topic is ruled in one place, and the other files refer to it instead of repeating it.

### Procedure

1. Analysis (agent): read the sources, compare them with samples from the code (e.g., the classes of category 1 and
   some types of `engine` and `simulations.core`), and list the conflicts, gaps, and impractical rules.
2. Decisions (maintainer): one decision per finding; mark each rule change with its file.
3. Implementation: the rule changes, and the GitHub Copilot reference files in `.github/instructions` if a rule file
   is added or renamed; run `app:javadoc` only if code is changed.
4. Record the decisions here and add cross-category decisions where they apply to `code-simulation-classes.md`.

### Findings

Analysis with scripts in the scratchpad over `app/src/main/java` (2026-10-29): all Javadoc comments with `@param`,
`@return`, or `@throws` are complete, including the type parameters and the checked exceptions (12 `@throws` tags;
the only other `throws` clause is on an override without Javadoc); `@throws NullPointerException` occurs only in
`core` (41 times), exactly for the parameters checked with `requireNonNull(..)` (except `AppLogger.initialize(..)`).
Public members with Javadoc, without overrides: over 90 % in `core`, `engine`, and `ui`, about 24 % in
`simulations`.

#### Removal of restating Javadoc

- Origin: "Checks" of this category
- Types: `java.md` ("Coverage")
- Problem: "Remove existing Javadoc only when it clearly adds no value; keep it when in doubt" seemed to contradict the
  cross-category decision to remove constructor Javadoc that only restates; it left open what "clearly" means.
- Proposal: State that Javadoc whose summary and tags only restate the name and signature clearly adds no value.
- Status: done (2026-10-29)

#### Javadoc coverage in `core`

- Origin: "Checks" of this category
- Types: `core.md` ("Documentation and Tests"), `AppArgs.toString()`
- Problem: "Public methods require Javadoc" left constructors and pure overrides open (`AppArgs.toString()` has
  Javadoc that only restates); the class-level rule repeated `java.md`.
- Proposal: Public constructors and methods require Javadoc, pure overrides follow `java.md`; the type Javadoc rule
  only adds "purpose and responsibilities".
- Status: done (2026-10-29)

#### Null checks and `NullPointerException` in `core`

- Origin: "Checks" of this category
- Types: `core.md` ("Null Validation"), `java.md` ("Nullability", "Tags"), the five `App*` classes of `core`
- Problem: `core` allowed `requireNonNull(..)` for parameters and documented the `NullPointerException` "only when it
  is part of a public core contract", against the general rules; the package-specific exception makes the rules
  harder to apply for agents.
- Proposal: Make the documented practice the rule (`@throws NullPointerException` exactly for checked parameters).
- Status: rejected (2026-10-29); instead the exception for `core` is removed: no package checks non-null parameters
  with `requireNonNull(..)` or documents a `NullPointerException` for them. Category 4 (group 1) removes the checks,
  the tags, and the tests of these exceptions in `core`.

#### Checked exceptions

- Origin: analysis of this category
- Types: `java.md` ("Tags")
- Problem: The rule named only unchecked exceptions; all checked exceptions of methods with Javadoc are documented
  anyway.
- Proposal: Every checked exception of the `throws` clause is documented with `@throws`.
- Status: done (2026-10-29)

#### Use of `@see`

- Origin: analysis of this category
- Types: `java.md` ("Tags"), e.g., `GridStructure`, `GridCoordinate`
- Problem: No rule; 107 `@see` tags in 41 files, many for the types of parameters or components (five in the type
  Javadoc of `GridStructure`) or repeated (`@see #ORIGIN` five times in `GridCoordinate`).
- Proposal: `@see` only for a closely related element that the text does not already name; not for the types of
  parameters, components, fields, or results.
- Status: done (2026-10-29); the maintainer asked to restrict `@see` clearly ("rarely").

#### Fully qualified references and package boundaries

- Origin: analysis of this category
- Types: `java.md` ("Style"), `SimulationUserActionContext`, `EdgeBehaviorAction`, `NeighborhoodMode`, `GridEntity`,
  `AgentOrderingStrategies`, `ConwayRuleProperty`, `ExtraterrestrialPetsSimulation`
- Problem: Eleven references use fully qualified names of types that the code does not import; the rule did not say
  whether to import a type only for Javadoc. `simulations.core.shared.SimulationUserActionContext` links
  `simulations.core.model.SimulationUserAction`, although `shared` must not depend on `model`.
- Proposal: No imports only for Javadoc; fully qualified names for types that the code does not import; no links to
  types of a package that the documented package must not depend on, `{@code SimpleName}` instead.
- Status: done (2026-10-29)

#### Summary sentence of types, fields, and constants

- Origin: analysis of this category
- Types: `java.md` ("Style")
- Problem: "Third person" fits only methods; types, fields, and constants start with noun phrases ("Factory for ...",
  "Contract for ...", "Maximum ..."). Constructors in `simulations.core` start with "Constructs", "Initializes", or
  "Convenience constructor".
- Proposal: Method summaries start with a verb in the third person; types, fields, constants, and enum constants may
  start with a noun phrase, but not with "This class ...". The constructors in `simulations.core` follow in
  `code-simulation-classes.md`.
- Status: done (2026-10-29)

#### Protected members of base classes

- Origin: "Sources" of this category (`ui.md`, `simulations.md`)
- Types: `java.md` ("Coverage"), the base views and ViewModels in `simulations.core`
- Problem: 37 protected members of `simulations` have Javadoc and 36 have none; neither `ui.md` nor `simulations.md`
  rules it.
- Proposal: No rule in `ui.md` or `simulations.md`; the coverage examples of `java.md` name when abstract or protected
  methods meant for subclasses are called and what implementations must do.
- Status: done (2026-10-29)

#### Block tag `@return`

- Origin: analysis of this category
- Types: `java.md` ("Tags", "Style")
- Problem: `{@return ...}` was "welcome", but the code uses only `@return` (545 times).
- Proposal: Use `@return`, not `{@return ...}`; `{@snippet}` stays welcome.
- Status: done (2026-10-29)

#### Missing tags and doclint

- Origin: "Sources" of this category (build)
- Types: `AGENTS.md`, `java.md` ("Tags"), `app/build.gradle.kts`
- Problem: `-Xdoclint:all,-missing` suppresses missing comments and also missing `@param`, `@return`, and `@throws`
  tags; enabling `missing` would report every member without Javadoc.
- Proposal: No build change; `AGENTS.md` says "missing comments and tags", and `java.md` says that reviews check the
  tags.
- Status: done (2026-10-29)

#### Tags of record components

- Origin: "Checks" of this category
- Types: `java.md` ("Tags")
- Problem: The record rule stood apart from the general tag rule and left records without Javadoc open.
- Proposal: One rule: when a type, constructor, or method has Javadoc, every parameter, record component, and type
  parameter has `@param`.
- Status: done (2026-10-29)

#### Javadoc in test sources

- Origin: "Checks" of this category (structure)
- Types: `junit.md` ("Test Method Style", "Manual Test Utilities")
- Problem: The Javadoc rule for test sources stood under "Test Method Style" and repeated "Manual Test Utilities".
- Proposal: A section "Javadoc" in `junit.md` that refers to "Manual Test Utilities".
- Status: done (2026-10-29)

### Decisions

- Rule change: `java.md` ("Coverage"): Javadoc whose summary and tags only restate the name and signature clearly
  adds no value; the coverage examples name when abstract or protected methods meant for subclasses are called and
  what implementations must do. `ui.md` and `simulations.md` get no Javadoc rules.
- Rule change: `core.md` ("Documentation and Tests"): public constructors and methods require Javadoc, pure overrides
  follow `java.md`; the type Javadoc of a public class describes its purpose and responsibilities.
- Rule change: `core.md`: the section "Null Validation" is removed. `java.md` ("Nullability"): no package, including
  `core`, checks non-null-by-default parameters with `requireNonNull(..)`; it only asserts that a `@Nullable` value is
  set. `java.md` ("Tags"): never document a `NullPointerException` for non-null-by-default parameters.
- Rule change: `java.md` ("Tags"): one rule for `@param` (parameters, record components, type parameters), `@return`
  (not `{@return ...}`), and `@throws` for every checked exception of the `throws` clause; reviews check the tags,
  because `app:javadoc` does not report missing ones. `@see` is used rarely, only for a closely related element that
  the text does not already name.
- Rule change: `java.md` ("Style"): method summaries start with a verb in the third person, types, fields, and
  constants may start with a noun phrase; imported types are referenced by simple name, other types by fully
  qualified name, without imports only for Javadoc and without links across forbidden package dependencies;
  `{@snippet}` stays welcome.
- Rule change: `junit.md`: the Javadoc rules of test sources form the section "Javadoc", which refers to "Manual Test
  Utilities" and to the tag and style rules of `java.md`.
- Rule change: `AGENTS.md` ("Platform Baseline"): `app:javadoc` fails on any warning except missing comments and tags.
- No build change; `app:javadoc` was not run, because no code changed.

## 4. Package reviews outside `simulations`

A review of each package outside `simulations` as a whole: all its types together (classes, interfaces, records,
enums, and utility classes), not only per kind of type as in the sibling plans and category 1. It applies the results
of categories 2 and 3.

### Inventory

| Group | Package                 | Main types | Test files | Path-scoped rule |
|------:|-------------------------|-----------:|-----------:|------------------|
| 1     | `core`                  |          6 |          6 | `core.md`        |
| 2     | `engine`                |          8 |          8 | `engine.md`      |
| 3     | `engine.model`          |          8 |          5 | `engine.md`      |
| 3     | `engine.model.entity`   |          8 |          5 | `engine.md`      |
| 4     | `engine.neighborhood`   |          9 |          9 | `engine.md`      |
| 5     | `engine.support`        |          6 |          6 | -                |
| 6     | `engine.executor`       |         11 |          8 | -                |
| 7     | `ui`                    |         14 |         13 | `ui.md`          |
| 8     | root package            |          2 |          1 | -                |
|       | **Total**               |     **72** |     **61** |                  |

Notes:

- Test files are the `.java` files in the same test package, including `*TestSupport` classes.
- The groups follow the package dependencies: `core` first, then `engine` from its base types to the executors, then
  `ui` and the root package (`AppLauncher`, `ExtraterrestrialPetsSimulation`).
- `engine.md` applies to `engine`, `engine.model`, `engine.model.entity`, and `engine.neighborhood` (groups 2 to 4),
  since group 5 to `engine.support`, and since group 6 to all of `engine` (`engine/**/*.java`).

### Checks per package

- Responsibility and cohesion: each type belongs in the package; the package boundaries of `AGENTS.md` and the
  path-scoped rules hold (e.g., the JavaFX `Color` in `engine.model.entity.GridEntityDescriptor`).
- API surface: visibility (`public` or package-private), `final` and `sealed`, unused members (cross-category
  decision), and factories and accessors that follow the naming patterns of `java.md`.
- Consistency across the types of the package: names of the same concept, parameter order, nullability, exceptions
  and their messages, logging, suppressions, member order (category 2), and Javadoc (category 3).
- Duplication within the package and with other packages (DRY).
- Tests: every public method of the non-JavaFX types is tested as `junit.md` requires; test classes and support classes
  of the package are consistent with each other.

### Findings

#### Notes on `engine` from #23

- Origin: #23, "Results of #33", notes for the area follow-ups
- Types: `engine.neighborhood.CellNeighborhoods`, `engine.support.GridInitializers`,
  `simulations.etpets.model.EtpetsSimulationManager`
- Problem: `CellNeighborhoods.isValidWithEdgeBehavior(..)` and `isCellNeighbor(..)` are used in main code only inside
  `CellNeighborhoods` or only in tests; `neighborEdgeBehaviorResults(..)`, `cellNeighborsIgnoringEdgeBehavior(..)`,
  `cellNeighborsWithEdgeBehavior(..)`, `neighborCoordinatesWithinRadius(..)`, and `cellNeighborDirections(..)` have no
  direct tests; `shuffleCoordinates(..)` is duplicated in `GridInitializers` and `EtpetsSimulationManager`.
- Proposal: Verify the notes against the current sources in groups 4 and 5 and decide them there; the duplicate in
  `EtpetsSimulationManager` is removed in `code-simulation-classes.md` (category 5) if `GridInitializers` offers a
  shared method.
- Status: `CellNeighborhoods` done (2026-10-29): `isCellNeighbor(..)` and `isValidWithEdgeBehavior(..)` were kept in
  `code-private-constructor-classes.md` (2026-10-26) and stay under `engine.md`; all five methods have direct tests
  in `CellNeighborhoodsTest`. `GridInitializers` deferred (2026-10-29): see the deferred finding "Shuffle with
  cancellation checks".

#### Null checks of `core`

- Origin: category 3, "Null checks and `NullPointerException` in `core`"
- Types: `AppArgs`, `AppLocalization`, `AppLogger`, `AppResources`, `AppStorage`
- Problem: The five classes check non-null parameters with `requireNonNull(..)` (49 calls) and document a
  `NullPointerException` for them (41 tags); the rules no longer allow either.
- Proposal: Remove the checks of non-null parameters and their `@throws NullPointerException` tags in group 1, and
  the tests of these exceptions; keep `requireNonNull(..)` only where it asserts a `@Nullable` value.
- Status: done (2026-10-29); no `requireNonNull(..)` remains in `core`.

#### Javadoc rules of category 3

- Origin: category 3
- Types: all types outside `simulations`
- Problem: Existing Javadoc does not yet follow the rules of category 3, e.g., `@see` for parameter or component types
  (`GridStructure`, `GridCoordinate`), the link from `SimulationUserActionContext` to `simulations.core.model`, the
  Javadoc of `AppArgs.toString()`, and Javadoc that only restates accessors.
- Proposal: Check and adapt the Javadoc of each package in its group.
- Status: groups 1 to 6 done (2026-10-29), groups 7 and 8 done (2026-10-30)

#### Lookup name of the log levels

- Origin: analysis of group 1
- Types: `AppLogger.LogLevel`, `AppLauncher`
- Problem: `fromString(String)` does not follow the `from<Code>(String)` pattern of `java.md`, unlike
  `AppArgs.Key.fromKeyName(..)`.
- Proposal: Rename it to `fromLevelName(String)`. An API change.
- Status: done (2026-10-29)

#### Duplication and formatting in `core`

- Origin: analysis of group 1
- Types: `AppLocalization`, `AppResources`, `AppStorage`, `AppLogger`, all `core` sources
- Problem: `AppLocalization` repeats four lookup loops over `CountryLocale`; `AppResources.getResourceAsString(..)`
  repeats the lookup and log of `getResourceAsStream(..)`; `AppStorage` repeats `System.getProperty("user.home")`
  seven times; the formatter of `AppLogger` is created through reflection in its test and formats without a locale;
  18 lines in main and test sources exceed 120 characters.
- Proposal: Private helpers, a package-private formatter, and manual line wraps.
- Status: done (2026-10-29)

#### Unused API of the grid base types

- Origin: analysis of group 2
- Types: `GridCoordinate`, `GridOffset`, `GridSize`, `GridStructure`, `GridTopology`
- Problem: Many public members have no caller in main sources, e.g., the arithmetic of `GridOffset`, the orientation
  checks of `GridSize`, `GridCoordinate.clampToBounds(..)`, and `GridTopology.cellVertexCount()`.
- Proposal: Remove them (cross-category decision), or keep them as the vocabulary of the base types.
- Status: rejected (2026-10-29); the base types keep their complete API, ruled in the new `engine.md`.

#### Enum values in display strings

- Origin: analysis of group 2
- Types: `GridTopology`, `GridStructure`, `CellNeighbor`, `CellNeighborWithEdgeBehavior`, `EdgeBehaviorResult`, `Pet`
- Problem: `java.md` required lowercase enum values in `toDisplayString()`, except for enums with their own display
  text, including entities. `GridTopology` showed its enum values in uppercase (`[SQUARE BLOCK/WRAP]`); all entities
  show their type in uppercase (`[WALL]`, `[PET ...]`), and only four types used lowercase. The exception for entities
  and the helper method that lowercase values need make the rule harder to apply.
- Proposal: Show enum values with `name()` everywhere, unless the enum provides its own display text.
- Status: done (2026-10-29); a first change to lowercase in `GridTopology` was reverted.

#### Base types of the grid models and entities

- Origin: analysis of group 3
- Types: `ReadableGridModel`, `ArrayGridModel`, `SparseGridModel`, `GridEntity`, `GridEntityDescriptor`, and the
  descriptor providers
- Problem: `isSparse()`, `colorAsOptional()`, and `GridEntity.isConstant(..)` have no caller in main sources;
  `engine.model.entity` uses the JavaFX `Color`, which `engine.md` did not cover; `filteredCellsSortedBy(..)` is
  identical in both grid models, which compare with the default entity in two different ways.
- Proposal: Extend `engine.md` to both packages and allow `Color` for rendering hints; move
  `filteredCellsSortedBy(..)` into `ReadableGridModel`; compare with `equals(..)` in both models.
- Status: done (2026-10-29)

#### API scope of `engine.neighborhood`

- Origin: analysis of group 4
- Types: `CompassDirection`, `CellNeighborhoods`, `CellNeighbor`, `CellNeighborWithEdgeBehavior`, `NeighborhoodMode`
- Problem: Many public members have no caller in main sources (e.g., `CompassDirection.listClockwise()` and
  `streamCounterClockwise()`, `isEdgeConnection()`, `NeighborhoodMode.includesVertexNeighbors()`); `engine.md` did
  not cover the package, although it depends only on the JDK, `core`, and `engine`.
- Proposal: Extend `engine.md` to `engine.neighborhood` and keep the complete API.
- Status: done (2026-10-29)

#### Duplicated edge behavior and direction ring checks

- Origin: analysis of group 4
- Types: `EdgeBehaviorAction`, `CellNeighborhoods`, and their callers in the simulations
- Problem: `action == VALID || action == WRAPPED` is repeated twice in `CellNeighborhoods` and six times in the
  simulations (Conway, Forest, Snake, Wa-Tor, and twice in Sugar); the selection of a direction ring by cell shape and
  neighborhood mode is repeated in `ReboundingEditToolBarViewModel`, `ReboundingSimulationManager`, and
  `SnakeStepLogic`.
- Proposal: `EdgeBehaviorAction.isWithinGrid()` and `CellNeighborhoods.directionRing(NeighborhoodMode, CellShape)`.
  An API extension.
- Status: done (2026-10-29) for `engine.neighborhood`; the simulations follow in `code-simulation-classes.md`.

#### Radius checks and dead branches of `CellNeighborhoods`

- Origin: analysis of group 4
- Types: `CellNeighborhoods`
- Problem: `cellsByRadiusRings(..)` rejects a negative radius, while `neighborCoordinatesWithinRadius(..)` returned an
  empty set for it, although its Javadoc called the radius non-negative; `maxNeighborCount(.., radius)` repeated one
  formula three times; `edgeBehaviorActionForCoordinate(..)` had an unreachable fallback and an unreachable `throw`;
  `cellNeighborsWithEdgeBehavior(..)` required a valid start coordinate in its Javadoc but returned an empty map.
- Proposal: Reject a negative radius in both methods through one private check; one formula; remove the dead
  branches; correct the Javadoc. A behavior change of `neighborCoordinatesWithinRadius(..)`.
- Status: done (2026-10-29)

#### API scope and dependencies of `engine.support`

- Origin: analysis of group 5
- Types: all types of `engine.support`
- Problem: The maintainer kept the unused methods of `GridInitializers`, `GridPatterns`, `GridPattern`, and
  `AgentOrderingStrategies` as a toolkit (2026-10-26), but no rule covered the package; `AgentOrderingStrategies`
  linked to `engine.executor.AsynchronousStepRunner` by its fully qualified name and had three `@see` tags.
- Proposal: Extend `engine.md` to `engine.support`; write `{@code AsynchronousStepRunner}` and remove the `@see` tags.
- Status: done (2026-10-29)

#### Transformations of `GridPattern`

- Origin: analysis of group 5
- Types: `GridPattern`, `GridPatterns`
- Problem: `shifted(..)`, `flipX()`, `flipY()`, and `mapValues(..)` returned lazy patterns that built a new modifiable
  map on every `offsetMap()` call; the rotations returned their modifiable map; `GridPatterns` promises unmodifiable
  patterns that are independent of the arguments.
- Proposal: Compute all transformations immediately and return `GridPatterns.of(map)`. A behavior change.
- Status: done (2026-10-29)

#### Range checks and row-major loops of `GridInitializers`

- Origin: analysis of group 5
- Types: `GridInitializers`
- Problem: Only `placeRandomPercent(..)` rejected a fraction outside [0.0, 1.0]; `fillRandomPercent(..)`,
  `fillWithProbability(..)`, and `placeWithProbability(..)` treated such values as 0.0 or 1.0. The row-major loop with
  cancellation checkpoints was repeated three times, and `fillRandomPercent(..)` ran the first check in three lambdas.
- Proposal: All four factories reject a value outside [0.0, 1.0] (an API change); one private loop helper; one lambda.
- Status: done (2026-10-29)

#### API scope of `engine.executor`

- Origin: analysis of group 6
- Types: all types of `engine.executor`
- Problem: `engine.executor` depends only on `engine.model` and `engine.model.entity`; its only public members without
  a caller in main sources, `SimulationTerminationCondition.and(..)`, `or(..)`, and `negate()`, were kept as a toolkit
  in `code-interface-types.md`, but no rule covered the package.
- Proposal: Extend `engine.md` to all of `engine`.
- Status: done (2026-10-29)

#### Duplicated results of `executeSteps(..)`

- Origin: analysis of group 6
- Types: `SimulationExecutor`
- Problem: `executeSteps(..)` repeated the interrupted return with its comment twice and built the
  `StepExecutionResult` from `stepCount()` in four places.
- Proposal: Private interface methods `createInterruptedResult(..)` and `createResult(..)`; no behavior change.
- Status: done (2026-10-29)

#### API scope of the drawing toolkit

- Origin: analysis of group 7
- Types: `FXGridCanvasPainter`, `FXPaintFactory`, `GridGeometry`, `CellDimension`
- Problem: `CellDimension.boundingBoxDimension()`, `FXPaintFactory.createVerticalGradient(..)`, and
  `createRadialGradient(..)` have no caller in main sources; `ui.md` kept the complete API only for
  `FXGridCanvasPainter`. The package-private shape helpers of `GridGeometry` are used only inside the class.
- Proposal: Extend the rule of `ui.md` to the whole drawing toolkit; make the shape helpers private.
- Status: done (2026-10-30); the rule is extended, the shape helpers stay package-private so that tests can call
  them directly.

#### Duplicated checks and slider setup in `ui`

- Origin: analysis of group 7
- Types: `FXGridCanvasPainter`, `FXComponentFactory`
- Problem: The painter constructor repeated the edge length check of `GridGeometry.computeCellDimension(..)` with
  only the lower bound; `drawPixelDirect(..)` and `drawPixelRect(..)` repeated the canvas bounds check;
  `createLabeledPercentSlider(..)` and `createLabeledDoubleSlider(..)` repeated the slider setup and the rounding
  listener.
- Proposal: Remove the constructor check and document the range; private helpers `isOnCanvas(..)` and
  `createRoundingSlider(..)`.
- Status: done (2026-10-30)

#### Test gaps of `GridGeometry`

- Origin: analysis of group 7
- Types: `GridGeometry`, `GridGeometryTest`
- Problem: `fromCanvasPosition(..)` was tested only for squares, so the triangle and hexagon search and
  `isPointInTriangleCell(..)` had no test; the polygon and frame segment tests checked only the number of points.
- Proposal: Round trip of points inside every cell for all shapes; exact coordinates for polygons and frame
  segments.
- Status: done (2026-10-30)

#### Reflection in the application test

- Origin: analysis of group 8
- Types: `ExtraterrestrialPetsSimulation`, `ExtraterrestrialPetsSimulationTest`
- Problem: The test wrote the private field `currentSimulationInstance` and called the private
  `shutdownCurrentSimulation()` through reflection (`setAccessible(true)`).
- Proposal: Make `shutdownCurrentSimulation()` package-private and set the field through a package-private
  `setCurrentSimulationInstance(..)`, which `switchToSimulation(..)` uses as well; an extracted lifecycle class was
  rejected as an abstraction for about 50 lines.
- Status: done (2026-10-30)

### Decisions

Group 1, `core` (2026-10-29):

- All 49 `requireNonNull(..)` checks of non-null parameters, their `@throws NullPointerException` tags, and the tests
  of these exceptions (with their `@SuppressWarnings("DataFlowIssue")`) are removed.
- API change: `AppLogger.LogLevel.fromString(..)` is renamed to `fromLevelName(..)`.
- Rule change: `java.md` ("Logging with AppLogger") names `AppLogger.error(throwable, message)`, the actual parameter
  order.
- `AppLocalization.CountryLocale`: the constructor no longer validates the two hard-coded constants and has no
  Javadoc; the type Javadoc is rewrapped. `DEFAULT_LOCALE` drops the sentence that restated its value.
- `AppLocalization.resolveLocaleFromArgument(..)` and `resolveLocaleFromDefault(..)` use a private
  `findSupportedLocale(Predicate)`; `AppResources.getResourceAsString(..)` delegates to `getResourceAsStream(..)`;
  `AppStorage` reads the home directory through a private `userHome()`.
- `AppLogger`: the redundant `initialized = false` and the restating Javadoc of `initializeLogger(..)` are removed;
  `AppLogFormatter` is a package-private `static final` class with a fixed header format and `Locale.ROOT`, without
  restating comments and without `@SuppressWarnings("StringConcatenationMissingWhitespace")`.
- `AppArgs`: no Javadoc on `toString()`; `<p>` directly before the first word.
- All lines in `core` and its tests fit 120 characters.
- Tests: `AppResourcesTest` and `AppStorageTest` name their main tests `test<Subject>` (`testGetBundleWithBaseName`
  for the overload), group checks with `assertAll(..)`, check concrete values instead of presence, and drop the
  assertion messages; `AppStorageTest` drops its `setUpBeforeEach()` and cleans up through one field;
  `AppLoggerTest` creates the formatter directly and adds `testFormatAppendsStackTrace`.
- `OperatingSystem.detect()` stays without a test, because it would need to change the global property `os.name`.
- `app:test` and `app:javadoc` pass.

Group 2, `engine` (2026-10-29):

- Rule change: the new `engine.md` (with `.github/instructions/engine.instructions.md`) applies only to the package
  `engine`: its base types keep their complete public API, also members without a current caller, and depend only on
  the JDK, JSpecify, and `core`. The description of `core.instructions.md` no longer names the removed null
  validation.
- Rule change: `java.md` ("Naming Conventions", `toDisplayString()`) shows enum values with `name()` unless the enum
  provides its own display text, instead of in lowercase. `CellNeighbor`, `CellNeighborWithEdgeBehavior`,
  `EdgeBehaviorResult`, and `Pet` drop `toLowerCase(..)` (e.g., `(EDGE, WRAPPED)`, `A=EAT@12`); `GridTopology` keeps
  its uppercase output.
- `GridTopology.requiredWidthMultiple(..)` and `requiredHeightMultiple(..)` test `!= EdgeBehavior.WRAP`.
- Javadoc: the type Javadoc of `GridCoordinate`, `GridSize`, `GridStructure`, `GridEdgeBehavior`, and `GridOffset`
  drops the `@see` tags of own members and component types; `@see #ORIGIN` (five times), `@see GridSize#area()`,
  `@see GridCoordinate#isWithinBounds(..)`, and `@see CellShape#TRIANGLE` (now a link in the text) are removed; the
  counterparts `GridOffset` → `GridCoordinate#offset(..)`, `EdgeBehavior` → `GridEdgeBehavior`, and the overloads
  keep theirs.
- The exception messages of the `GridSize` constructor are wrapped at 120 characters.
- Tests: all tests group several checks with `assertAll(..)`; `CellShapeTest` drops its assertion messages;
  `GridCoordinateTest` drops `testOrigin` and `testIllegal`; `GridStructureTest` creates structures through
  `createStructure(..)` and checks both coordinate methods through `assertRowMajorCoordinates(..)`; `GridSizeTest`
  checks the exact range limits; `GridTopologyTest` adds `testRequiredMultiplesAreWithinMaximum`, which checks that
  `MAX_REQUIRED_*` is a multiple of every required multiple.
- `app:test` and `app:javadoc` pass.

Group 3, `engine.model` and `engine.model.entity` (2026-10-29):

- Rule change: `engine.md` (and its Copilot reference file) also applies to `engine.model` and
  `engine.model.entity`: their base types keep their complete public API (e.g., `isSparse()`, `colorAsOptional()`,
  and `GridEntity.isConstant(..)`), and `engine` uses no JavaFX except `javafx.scene.paint.Color` for the rendering
  hints of entity descriptors.
- `filteredCellsSortedBy(..)` is a default method of `ReadableGridModel` instead of two identical overrides;
  `ArrayGridModel` compares with the default entity through `equals(..)` like `SparseGridModel`, without `Objects`.
- Javadoc: the `@see` tags of `GridEntity` (including two fully qualified links to `engine.model`),
  `GridEntityDescriptor`, `GridEntityDescriptorProvider`, `GridEntityDescriptorSpec`,
  `SpecBackedGridEntityDescriptorProvider`, `ConstantGridEntityDescriptorProvider`, and `GridEntity.descriptorId()`
  are removed; `ConstantGridEntity` keeps its link to `GridEntity.isConstant(..)`; `isConstant(..)` writes
  `{@code true}` and `{@code false}`.
- Lines in main and test sources fit 120 characters.
- Tests: `WritableGridModelTest` names the main tests `test<Subject>` (`testNonDefaultCoordinates`, `testAllCells`,
  `testNonDefaultCells`, `testGetGridCell`, `testSetEntity`, `testSetEntityWithGridCell`, `testSetSwappedEntities`)
  and checks in `testFilteredCellsSortedBy` that non-matching entities are filtered and the result is mutable;
  `ArrayGridModelTest` and `SparseGridModelTest` drop their line comments before actions, name the checked order or
  content (`testFilteredCoordinatesIsInRowMajorOrder`, `testFilteredCellsReturnsOnlyMatchingNonDefaultCells`), and
  use `assertAll(..)`; `GridEntityTest` merges its two `isConstant(..)` tests; three test files lose a blank line at
  their end.
- `app:test` and `app:javadoc` pass.

Group 4, `engine.neighborhood` (2026-10-29):

- Rule change: `engine.md` (and its Copilot reference file) also applies to `engine.neighborhood`: its types keep
  their complete public API (e.g., `CompassDirection.listClockwise()`); the description names compass directions and
  neighborhoods with edge behavior.
- API extension: `EdgeBehaviorAction.isWithinGrid()` (`VALID` or `WRAPPED`), used by `isValidWithEdgeBehavior(..)`
  and `cellsByRadiusRings(..)`; `CellNeighborhoods.directionRing(NeighborhoodMode, CellShape)` returns one of the three
  ring constants and rejects `TRIANGLE` with an `IllegalArgumentException`. The simulations adopt both in
  `code-simulation-classes.md`.
- API change: `neighborCoordinatesWithinRadius(..)` rejects a negative radius like `cellsByRadiusRings(..)`, through
  the private `checkRadius(..)` with one message; radius `0` returns an empty modifiable set.
- `CellNeighborhoods`: `maxNeighborCount(.., radius)` computes `1 + ((degree * radius * (radius + 1)) / 2)` with the
  direct neighbor count as degree for all shapes; `edgeBehaviorActionForCoordinate(..)` returns `WRAPPED` as the last
  case without the unreachable fallback and `throw` (and without `@throws`); the merge in
  `neighborEdgeBehaviorResults(..)` prefers a `VALID` candidate in one expression; `cellNeighborsWithEdgeBehavior(..)`
  returns an unmodifiable map and documents the empty map for an invalid start coordinate.
- `CellNeighborhoods.CellNeighborConnection` has the package-private factories `ofEdge(..)` and `ofVertex(..)`, used by
  the neighbor tables and `CellNeighborhoodsTest`; the triangle comments name only the example neighbor, and the
  `case` labels are unqualified. Restating comments and the Javadoc of `CACHE` are removed; the ring constants
  describe only their directions, and `directionRing(..)` states that they follow the neighbor order.
- `CompassDirection.listClockwise()` and `listCounterClockwise()` delegate to the stream methods and return
  unmodifiable lists.
- Javadoc: the `@see` tags of `CellNeighborhoods` (five), `CellNeighbor` (three), `CellNeighborWithEdgeBehavior`
  (three), `CompassDirection` (two), `NeighborhoodMode`, and `@see EdgeBehavior` of `EdgeBehaviorAction` are removed;
  `EdgeBehaviorAction` describes the outcome for a coordinate instead of an entity movement; `distanceOnRing(..)` no
  longer mentions `null`.
- Lines in main and test sources fit 120 characters; `RadiusRingCell` and `RadiusRingCellTest` lose a blank line at
  their end.
- Tests: new `EdgeBehaviorActionTest` (`testIsWithinGrid`); `CellNeighborhoodsTest` adds `testDirectionRing`,
  `testDirectionRingRejectsTriangle`, the unmodifiable map of `cellNeighborsWithEdgeBehavior(..)`, and
  `testNeighborCoordinatesWithinRadiusRejectsInvalidRadius` (instead of `...RejectsTooLargeRadius`), uses the helpers
  `coordinate(..)`, `createStructure(..)`, `assertValidWithEdgeBehavior(..)`, `assertEdgeBehaviorResult(..)`, and
  `assertEdgeBehaviorAction(..)`, groups its checks with `assertAll(..)`, and compares the square and hexagon tables
  as ordered lists; `CompassDirectionTest` shares the expected orders as constants and checks the unmodifiable lists;
  `CellNeighborTest` and `CellNeighborWithEdgeBehaviorTest` use `assertAll(..)`.
- Suppressions after the IntelliJ inspection (maintainer): `CellNeighborhoodsTest` drops `DuplicateExpressions`;
  `CompassDirectionTest` gets the class-level `MagicNumber`. Instead of `DataFlowIssue` on the `add(..)` to the
  unmodifiable lists, `CompassDirectionTest` uses the helper `tryAddDirection(..)`, like the `tryPut*` helpers of
  `CellNeighborhoodsTest`; it checks `distance(..)` and `distanceOnRing(..)` in both directions through
  `assertDistance(..)` and `assertDistanceOnRing(..)` and keeps the rings as constants.
- The simulations adopt `isWithinGrid()` and `directionRing(..)` in `code-simulation-classes.md` (category 5, finding
  "Edge behavior checks and direction rings").
- `app:test` and `app:javadoc` pass; the maintainer formatted and inspected the changes in IntelliJ.

Group 5, `engine.support` (2026-10-29):

- Rule change: `engine.md` (and its Copilot reference file) also applies to `engine.support`, the toolkit on top of
  the base types: its types keep their complete public API (e.g., `GridPatterns.circleOutline(..)`), as decided in
  `code-private-constructor-classes.md` and `code-interface-types.md`, and depend only on the base types of `engine`.
- `AgentOrderingStrategies`: the type Javadoc names `{@code AsynchronousStepRunner}` and `filteredCellsSortedBy(..)`
  instead of linking to `engine.executor`; its three `@see` tags are removed.
- Behavior change: `GridPattern.shifted(..)` (now through `GridOffset.add(..)`), `flipX()`, `flipY()`,
  `rotate90()`, `rotate180()`, `rotate270()`, and `mapValues(..)` compute their result immediately and return
  `GridPatterns.of(map)`, an unmodifiable pattern independent of the source; the type Javadoc says so, and
  `offsetMap()` no longer repeats the normalization recommendation. `°` stays in the Javadoc of the rotations.
- API change: `GridInitializers.fillRandomPercent(..)`, `fillWithProbability(..)`, and `placeWithProbability(..)`
  reject a value outside [0.0, 1.0] with an `IllegalArgumentException` like `placeRandomPercent(..)`, through the
  private `checkFraction(value, name)`; the callers in Conway, Forest, and Sugar pass only values in this range.
  `fillRandomPercent(..)` has one lambda without the special cases for 0.0 and 1.0, which the general path covers
  with the same result and random sequence.
- `GridInitializers`: `fillCells(..)`, `createCoordinates(..)`, and `placeAllAtRandomPositions(..)` use the private
  `forEachCoordinate(size, cancellationCheck, action)`; lines fit 120 characters.
- Deferred: the duplicate `shuffleCoordinates(..)` in `EtpetsSimulationManager` (see "Deferred findings").
- Tests: `GridInitializerTest.testAndThen` (renamed); `GridInitializersTest` renames `testPlacePattern`, uses
  `count(..)` instead of stream filters, drops `assertDoesNotThrow(..)`, and adds
  `testFromListRejectsCoordinateOutsideGrid` and the `Rejects...OutOfRange` tests of the three factories;
  `GridPatternTest` checks the transformations through `assertUnmodifiableOffsetMap(..)`; `GridPatternsTest` checks
  that both `of(..)` results are unmodifiable.
- `app:test` and `app:javadoc` pass.

Group 6, `engine.executor` (2026-10-29):

- Rule change: `engine.md` (and its Copilot reference file) applies to all of `engine` through the path
  `engine/**/*.java` and describes `engine.executor`; all engine packages keep their complete public API (e.g.,
  `SimulationTerminationCondition.negate()`).
- `SimulationExecutor.executeSteps(..)` builds its results through the private `createInterruptedResult(..)` and
  `createResult(..)`; the behavior is unchanged.
- Javadoc: `TimedSimulationExecutor`, `AsynchronousStepRunner`, `SynchronousStepRunner`, and
  `DefaultSimulationExecutor` write `<p>` directly before the first word after an empty `*` line; the restating
  Javadoc of both `performStep(..)` overrides and of `AsynchronousStepRunner.model()` and `@see SynchronousStepLogic`
  of `SynchronousStepRunner` (a parameter type) are removed; `AgentStepLogic` and `SynchronousStepLogic` keep their
  `@see` of the counterpart runner.
- Lines in main and test sources fit 120 characters; `StepExecutionResultTest` and `StepTimingStatisticsTest` lose a
  blank line at their end.
- Tests: `StepTimingStatisticsTest` uses literals with the class-level `@SuppressWarnings("MagicNumber")` instead of
  the constants `ZERO` to `HUNDRED_TWENTY` and compares `empty()` with a record; `testRecord` of
  `StepTimingStatisticsTest` and `StepExecutionResultTest` checks the accepted boundary values.
- `app:test` and `app:javadoc` pass.

Group 7, `ui` (2026-10-30):

- Rule change: `ui.md` ("Scope") keeps the complete public API of the drawing toolkit (`FXGridCanvasPainter`,
  `FXPaintFactory`, `GridGeometry`, and `CellDimension`), also members without a current caller; the sentence on
  `FXGridCanvasPainter` in "Grid Geometry and Rendering" is merged into it.
- The package-private shape helpers of `GridGeometry` stay package-private, so that tests call them without
  reflection.
- `FXGridCanvasPainter`: the constructor no longer checks the edge length itself; `@throws` names the range
  [`MIN_EDGE_LENGTH`, `MAX_EDGE_LENGTH`] of `GridGeometry.computeCellDimension(..)`, only the message changes. The
  pixel methods share the private `isOnCanvas(..)`; `drawScaledCell(..)` names its result `scaled`.
- `FXComponentFactory`: the percent and the double slider share the private `createRoundingSlider(..)`; the range of
  the percent slider is computed with `Math.round(..)` instead of a truncating cast (e.g., `0.29` gave `28`); the
  combo box map drops the merge function, because the valid values have no duplicates.
- Suppressions: the class-level `@SuppressWarnings("MagicNumber")` of `FXComponentFactory` and `FXPaintFactory` is
  narrowed to `createLabeledPercentSlider(..)`, `createLabeledDoubleSlider(..)`,
  `computeMinorTickCountForPercentSlider(..)`, `computeBrightnessVariantsMap(..)`, and `createRadialGradient(..)`;
  the maintainer checks it with the IntelliJ inspection.
- The `Input*Property` factories log invalid values through `AppLogger.errorf(..)`; `InputChoiceProperty` checks
  duplicates through the size of a `HashSet`.
- `GridGeometry`: the neighbor candidates are `GridOffset` lists used with `GridCoordinate.offset(..)`; the outer
  radius of the triangle uses `Math.hypot(..)`, and the constant `TWO` is removed. After the IntelliJ inspection,
  `computeGridDimension(..)` computes the width of all columns and the height of all rows once instead of repeating
  them per cell shape; `createLabeledIntSlider(..)` keeps its slider setup, because an extracted method would have
  one caller, and the setup differs from `createRoundingSlider(..)`.
- Javadoc: `<p>` directly before the first word in `AboutDialog`, `SimulationTimer`, and `FXGridCanvasPainter`;
  `{@code null}` in the painter; the `@see` tags of parameter types and of `GridStructure#isCoordinateValid(..)` and
  `@see #gridDimension2D` are removed (the grid area links `gridDimension2D()` in the text); the compact constructor
  of `CellDimension` starts with "Creates a ..."; the restating Javadoc of `createLabel(..)`, `createHBox(..)`,
  `createVBox(..)`, `AboutDialog.createHeader(..)`, and `createTextAreaTab(..)` and restating line comments are
  removed.
- Lines in main and test sources fit 120 characters.
- Tests: `GridGeometryTest` is rebuilt with helpers: exact values of all cell dimensions, polygons, and frame
  segments, `testFromCanvasPositionReturnsCoordinateForPointsInsideCell` for all shapes (the center and points near
  every vertex of every cell), `testIsPointInTriangleCell`, and no `@Execution(SAME_THREAD)`. `CellDimensionTest`
  gets `@Execution(SAME_THREAD)` for `Locale.setDefault(..)` and drops its forwarding helper; new
  `FXDialogStylerTest`; `FXGridCanvasPainterTest` adds the two centered text methods and the upper edge length
  bound; `FXComponentFactoryTest` and `FXPaintFactoryTest` name single tests `test<Subject>`, add the base overload
  of `createLabeledChoiceComboBox(..)`, the invalid checkbox values, the minor ticks of the percent slider, and
  uneven brightness groups; the `Input*PropertyTest` classes check rounding ties.
- `app:test` and `app:javadoc` pass.

Group 8, root package (2026-10-30):

- `ExtraterrestrialPetsSimulation`: `shutdownCurrentSimulation()` and the new `setCurrentSimulationInstance(..)` are
  package-private, so that the test needs no reflection; `resolveSimulationType(..)` and
  `resolveScreenContainingStage(..)` are `private static` and precede the instance methods (category 2).
- Logging: parameterized messages use `AppLogger.infof(..)` and `errorf(..)`; the uncaught exception message gets the
  prefix `Application:`.
- `adjustStageLayoutToScreen(..)` keeps its structure, because no test covers it and its behavior depends on the real
  window and screen; only comments that restated the code and the local variable `margin` are removed.
- `pendingTerminationHandles` keeps one handle per simulation switch until `stop()`; pruning terminated handles would
  need a query in `SimulationTerminationHandle`, which the small list does not justify.
- Javadoc: `<p>` directly before the first word; `stop()` and `awaitPendingTerminations()` describe the shared
  termination timeout, the forced shutdown, and the restored interrupted status; `switchToSimulation(..)` names the
  callback for the `SimulationFactory` in the text instead of two `@see` tags (one fully qualified to
  `StartMainView`). `AppLauncher` drops `@see AppArgs.Key#printHelp(..)` and documents the fallback of
  `initializeAppLogger(..)` instead of a line comment.
- Lines in main and test sources fit 120 characters.
- Tests: `ExtraterrestrialPetsSimulationTest` uses the package-private methods and the helper `createInstance(..)`,
  checks the disabled region inside the FX block, and drops its assertion messages; `AppLogger.initializeForTesting()`
  stays in `@BeforeEach`, because `stop()` shuts the logger down. `AppLauncher` stays without a test: `main(..)` calls
  `System.exit(..)` and `Application.launch(..)`, and its initializations change global state.
- `app:test` and `app:javadoc` pass.

Deferred findings (2026-10-30):

- `GridInitializers.placeShuffledCount(..)` and `placeAllAtRandomPositions(..)` shuffle with
  `Collections.shuffle(list, random)` instead of the private `shuffleCoordinates(..)`, which made the same swaps with
  the same `nextInt(..)` calls; the random sequence of a seed is unchanged, but the shuffle no longer runs the
  cancellation check. `placeShuffledCount(..)` takes the coordinates from `GridStructure.coordinatesList()` instead
  of the private `createCoordinates(..)`. `GridInitializersTest` drops `testPlaceShuffledCountChecksDuringShuffle`
  and its `TrackingRandom`.
- Rule change: `java.md` ("Code Style"): exception messages have no final period and one of three forms:
  `<name> must <condition>, but was: <value>` for rejected values (`in [min, max]` for inclusive ranges, `name=value`
  pairs for relations), `Unable to <action>: <reason or name=value pairs>` for failed operations, and
  `No <thing> found for <name>: <value>` for failed lookups; with the exact code name, `toString()`, and string
  concatenation.
- All exception messages in `core`, `ui`, and `engine` follow these forms (the root package throws none), e.g.,
  `width must be in [8, 16384], but was: 5`,
  `executedSteps must be <= stepCount, but was: executedSteps=5, stepCount=3`, and
  `Unable to initialize AppLogger: already initialized`. `GridSize` and `GridStructure` use concatenation instead of
  `String.format(..)`; the record constructors of `StepTimingStatistics` and `CellDimension` list all their values
  through a private `formatValues(..)`. No test compares these messages.
- `app:test` and `app:javadoc` pass.

### Procedure

- One group at a time, in the order of the inventory; each group is presented and decided on its own in the chat
  before it is implemented, and `app:test` and `app:javadoc` run after each group.
- The steps per category apply to each group; the classes of category 1 are checked again only for the package-wide
  checks and the results of categories 2 and 3.

## Cross-category decisions

- (1, all) Constructors: no redundant initializations of fields to their default values (rule change: `java.md`,
  "Code Style"); no null checks of non-null parameters (`java.md`, "Nullability"); a constructor has Javadoc only
  when it explains something that the name, signature, and type Javadoc do not show (e.g., a precondition or how a
  parameter is used), and then its summary sentence starts with "Creates a ..." (rule change: `java.md`, "Javadoc");
  Javadoc that only restates the class name and parameters is removed.
- (1, all) Javadoc writes the literals `true`, `false`, and `null` as `{@code ...}` (rule change: `java.md`,
  "Javadoc").
- (1, all) Public methods and constructors without a caller in main sources are removed (API change with approval)
  unless the maintainer keeps them for a planned use; core classes, all engine packages, and the drawing toolkit of
  `ui` keep their complete API (rule change: `core.md`, `engine.md`, and `ui.md`).
- (1, 2, all) Member order (rule change: `java.md`, "Code Style"): static fields, instance fields, constructors,
  static methods, instance methods, then nested types; within the static and within the instance methods public,
  protected, package-private, then private, with overrides (e.g., lifecycle methods such as `start(..)`) as public
  and `toString()` directly before the private instance methods. Tests follow the declaration order (`junit.md`).
- (3, all) No package, including `core`, checks non-null-by-default parameters with `requireNonNull(..)` or
  documents a `NullPointerException` for them; `requireNonNull(..)` only asserts that a `@Nullable` value is set
  (rule change: `core.md` and `java.md`, "Nullability" and "Tags").
- (3, all) Javadoc follows the rules of category 3 (`java.md`, "Javadoc"): `@see` rarely, `@return` instead of
  `{@return ...}`, no imports only for Javadoc and no links across forbidden package dependencies, noun phrases
  allowed for types, fields, and constants, and no Javadoc that only restates the name and signature.
- (4, all) `toDisplayString()` shows enum values with `name()` unless the enum provides its own display text (e.g.,
  `arrow()`); no `toLowerCase(..)` and no helper methods for it (rule change: `java.md`, "Naming Conventions").
- (4, all) Code outside `engine.neighborhood` checks edge behavior actions with `EdgeBehaviorAction.isWithinGrid()`
  instead of comparing with `VALID` and `WRAPPED`, and selects a direction ring with
  `CellNeighborhoods.directionRing(..)`.
- (1, all) Class-level `@SuppressWarnings` are narrowed to the members that need them, with the maintainer's
  approval per case (`java.md`, "Suppressions Policy").
- (4, all) Exception messages follow the three forms of `java.md` ("Code Style"); `code-simulation-classes.md` adapts
  the messages of `simulations`.
- (4, all) Lists are shuffled with `Collections.shuffle(list, random)`, without cancellation checks during the
  shuffle; all grid coordinates in row-major order come from `GridStructure.coordinatesList()`.

## Deferred findings

### Shuffle with cancellation checks

- Origin: #23 (notes for the area follow-ups), group 5 of category 4
- Types: `GridInitializers`, `simulations.etpets.model.EtpetsSimulationManager`
- Problem: The Fisher-Yates shuffle with cancellation checkpoints `shuffleCoordinates(..)` is duplicated in
  `GridInitializers` (private, with a `Runnable`) and `EtpetsSimulationManager` (with a
  `SimulationInitializationCancellation`); `EtpetsSimulationManager` also duplicates `createCoordinates(..)`.
- Proposal: Options were a generic `shuffle(List, Random, Runnable)` in `WorkCheckpoints`, a public method in
  `GridInitializers`, `Collections.shuffle(..)`, or keeping the duplicate.
- Status: done (2026-10-30) for `GridInitializers`: `Collections.shuffle(..)`, which makes the same swaps with the
  same random calls, without cancellation checks during the shuffle; the coordinates come from
  `GridStructure.coordinatesList()`. `code-simulation-classes.md` (category 5) removes `shuffleCoordinates(..)` and
  `createCoordinates(..)` of `EtpetsSimulationManager` the same way.

### Exception messages in `engine`

- Origin: analysis of group 6 of category 4
- Types: the types of all engine packages that throw `IllegalArgumentException`, e.g., `GridSize`, `GridStructure`,
  `CellNeighborhoods`, `RadiusRingCell`, `CompassDirection`, `GridPatterns`, `GridInitializers`,
  `StepExecutionResult`, and `StepTimingStatistics`
- Problem: The exception messages use different styles: capitalized or lowercase start, with or without a final
  period, `but was: X`, `but was X.`, or no actual value at all (e.g., `"stepCount must be >= 0"`), `String.format`
  with `Locale.ROOT` or concatenation, and symbols (`>=`, `<=`) or words (`must not be negative`, `at least 1`).
- Proposal: Agree on one message style (and possibly a rule in `java.md`), then adapt the messages in one change;
  check the tests that compare messages.
- Status: done (2026-10-30); three message forms ruled in `java.md` ("Code Style"), applied to `core`, `ui`, and
  `engine` (see the decisions of category 4).

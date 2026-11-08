# Simulations Package Review

**Status:** in progress

**Related issues:** #23

Temporary working document for the review of all Java types of the package `simulations` and its sub-packages in
main and test sources: classes, records, interfaces, and enums, also those that the sibling plans already aligned,
because the rules changed since then. It continues `code-other-classes.md`, which reviewed the code outside
`simulations`. The plan was split off from `code-other-classes.md` (2026-10-29) as `code-simulation-classes.md`,
restructured into script fixes, cross-cutting blocks, and group reviews (2026-11-08), and renamed. The maintainer
deletes it after all phases are done and pushed.

## Workflow

### Goals

- Every type complies with the current `.claude/rules`, in particular its Javadoc and its unit tests. Where a rule is
  missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- Every type is checked against the review checklist of #23 (Javadoc, names, design, dead code and comments,
  contracts, modern Java 27, and tests).
- Types of the same family (e.g., statistics, simulation managers, config views) are as similar as sensible and
  possible in style, layout, Javadoc, naming, member order, constructors, annotations, suppressions, and test
  coverage, across all simulations and with the code aligned in `code-other-classes.md`.
- Similar or identical cases are decided together and the same way; record such decisions under "Cross-group
  decisions".
- The plan aligns and reviews the code; simulation-specific improvements (new behavior, balancing, and domain changes)
  stay in #24, also for ET Pets and the Lab.

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames of types, fields, and methods, signatures, moves to another
  package) are allowed in this refactoring, but each one needs the maintainer's approval before it is implemented.
- Rule changes need the maintainer's approval like API changes. New rules settle a recurring decision; prefer
  extending an existing section over a new one, and do not add a rule for a single case.
- Unit tests: in the groups of `simulations.core` (C1 to C9), check and improve the existing tests and add missing
  ones for the non-JavaFX types as `junit.md` requires. No new tests for JavaFX types (views, ViewModels, and the
  property wrappers `SeedProperty`, `ConwayRuleProperty`, and `LangtonRuleProperty`) and for the concrete simulations
  (groups D1 to D9); their test coverage is optimized separately later (maintainer, 2026-10-28). Their existing tests
  are kept, adapted to the changes, and improved where cheap; test gaps found there are listed in the group notes.
- The Lab is reviewed like the simulations, although it is hidden from the start screen.
- Findings that need larger changes outside the current block or group may be moved to "Deferred findings" and are
  handled after the last group.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not review or delete this plan. Commit only on the maintainer's request: one commit per script fix, block, and
  group, after the maintainer has formatted, inspected, and tested it in IntelliJ.

### Phases

Work on exactly one script fix, block, or group at a time, in the order of the summary table.

- **Phase A, script fixes:** purely mechanical corrections by a script in the scratchpad, without decisions. Run
  `app:test`; one commit each, which needs no detailed review.
- **Phase B, cross-cutting blocks:** one topic across all of `simulations`. A script lists all occurrences; present
  the list with a proposal per occurrence or per pattern in the chat, implement the decisions, run `app:test` and
  `app:javadoc`, and commit the block on its own. Blocks change only their topic, so that the later group reviews
  see clean diffs.
- **Phase C, groups of `simulations.core`:** the shared infrastructure, bottom-up from `shared` to the root package,
  so that each group builds on reviewed types. This is the most important package; it needs the highest quality and
  a manual test of the application by the maintainer after each group that changes behavior or JavaFX code.
- **Phase D, simulation groups:** one group per simulation with all its sub-packages; Wa-Tor is the reference
  (maintainer, 2026-11-08). Each later simulation is compared with Wa-Tor family by family and adopts its decisions
  without asking again, unless it deviates.
- **Final check:** run the script check again, fix the remaining occurrences after the maintainer has decided, and
  handle the deferred findings.

### Steps per group

1. Set the group to `in progress` in the summary table. Generate the inventory of the group from the CSV files
   (types, kinds, modifiers, constructors, method counts, tests) and verify it against the sources. Read the relevant
   rules (`java.md`, `junit.md`, `simulations.md`, `i18n.md`), every source file of the group, and its tests. Also
   read "Cross-group decisions", the findings assigned to the group, and "Deferred findings".
2. Analyze: the assigned findings, rule violations, inconsistencies within the group and with already reviewed
   groups (in phase D with Wa-Tor), test gaps, and possible rule changes.
3. Present the results and recommendations in the chat and ask the open decision questions; mark every recommended
   API change explicitly. Do not change code before the maintainer has decided.
4. Implement the decisions, including required changes to callers, tests, and rules.
5. Run `app:test` and `app:javadoc`; both must pass before the group counts as done.
6. Update this plan: set the group to `done`, record the decisions and the test gaps under the group, mark its
   findings as done, and add cross-group decisions and deferred findings.

### Review guidelines

Decide these points in the first group they apply to, record them under "Cross-group decisions", and apply them in all
later groups:

- Constructors: the parameter order of step runners and step logic (block B5); the general points of
  `code-other-classes.md` (no redundant field initializations such as `sequence = 0` in `CreatureFactory`, no null
  checks of non-null parameters, constructor Javadoc only when it explains something).
- Base classes: the visibility of the members meant for subclasses (`protected` or package-private) and `sealed`
  where the subclasses are known. The names follow `simulations.md` (`Abstract*` only for classes that must be
  abstract, `Base*` for complete base implementations such as `BaseTimedSimulationStatistics`, `*Base` for abstract
  entity base classes, and domain-specific entity factories such as `CreatureFactory`). Concrete classes stay `final`.
- Public API without a caller in main sources: remove it (API change with approval) unless the maintainer keeps it for
  a planned use.
- Section line comments in views and ViewModels (e.g., `// Rules - NeighborhoodMode` in `WatorConfigViewModel`): align
  them with the four groups of the configs (`// Structure`, `// Layout`, `// Initialization`, `// Rules`) where the
  class follows the config components.
- Log messages (block B2): align them with `core`, `ui`, and the root package where possible and sensible (aligned on
  2026-11-07): the class name as prefix, no final period, values after a colon as `name: value` pairs, `Ignoring ...`
  for ignored input, and `Failed to ...` for failures. Some messages are deliberately different and may keep their
  form, e.g., the shared views and ViewModels prefix them with the simulation type (`"%s: ..."`) and append diagnostic
  `name=value` pairs, and the commented-out diagnostic traces of the step logic (e.g., `WatorStepLogic`) stay as
  `java.md` ("Logging with AppLogger") allows.

### Documentation format

- Findings: one `####` subsection per finding under the block or group, with a short title, followed by the bullets
  "Origin:", "Types:", "Problem:", and "Proposal:"; after the decision a bullet "Status:" with the date.
- Decisions: a `### Decisions` subsection at the end of the block or group, one bullet per decision; prefix a bullet
  with "API change:" or "Rule change:" (naming the rule file) when it applies.
- Cross-group decisions: one bullet per decision with the affected blocks or groups in parentheses, e.g., "(C2, D)".
- Deferred findings: one `###` subsection per finding in the same format as the findings.

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv` and `docs/generated/JavaMethodInventory.csv` (regenerated on
2026-11-08): 278 top-level types in main sources (163 classes, 43 enums, 41 interfaces, and 31 records) and 122 in test
sources. "Types" counts the top-level types in main sources of a group.

| #  | Script fix, block, or group                                   | Types | Status      |
|----|---------------------------------------------------------------|------:|-------------|
| A1 | File endings                                                  |     - | done        |
| A2 | Javadoc paragraphs                                            |     - | open        |
| B1 | Exception messages                                            |     - | open        |
| B2 | Log messages                                                  |     - | open        |
| B3 | Member order                                                  |     - | open        |
| B4 | Edge behavior checks and direction rings                      |     - | open        |
| B5 | Constructor parameters of step runners and step logic         |     - | open        |
| B6 | Metrics catalogs of the statistics                            |     - | open        |
| B7 | Line length                                                   |     - | open        |
| C1 | `core.shared`                                                 |    10 | open        |
| C2 | `core.model`: managers, statistics base, and user actions     |    10 | open        |
| C3 | `core.model`: statistics history and extrema                  |     8 | open        |
| C4 | `core.viewmodel`: config, control, and observation            |     7 | open        |
| C5 | `core.viewmodel`: main ViewModels and user action descriptors |     5 | open        |
| C6 | `core.view`: config and control views                         |     6 | open        |
| C7 | `core.view`: observation views and statistics chart           |     3 | open        |
| C8 | `core.view`: main views, edit tool bar, and canvas drawing    |     8 | open        |
| C9 | `core` root package and `start`                               |     6 | open        |
| D1 | Wa-Tor (reference)                                            |    21 | open        |
| D2 | Rebounding                                                    |    18 | open        |
| D3 | Snake                                                         |    25 | open        |
| D4 | Sugarscape                                                    |    28 | open        |
| D5 | ET Pets                                                       |    42 | open        |
| D6 | Conway                                                        |    24 | open        |
| D7 | Forest                                                        |    13 | open        |
| D8 | Langton                                                       |    29 | open        |
| D9 | Lab                                                           |    15 | open        |
|    | **Total**                                                     |   278 |             |

Cross-cutting facts:

- All concrete classes are `final`. The abstract classes are the seven `Abstract*` classes in `simulations.core` and
  the `sealed` entity bases `ResourceBase` and `CreatureBase`; `BaseTimedSimulationStatistics` is neither `abstract`
  nor `final` and is extended by eight statistics classes.
- Most types come in families of one type per simulation; the Lab deviates in several families (own control view and
  ViewModel, own main ViewModel, no timed manager, no timed statistics).
- Of the 128 instantiable classes without a private constructor, 31 have a test class named after them.

## Initial script check (2026-11-08)

A line-based script in the scratchpad checked all Java files of `simulations` in main and test sources (one-line
patterns, no regexes over whole files):

- 385 lines over 120 characters in almost all packages (B7).
- 27 files with a blank line after the final `}`, and `PetActionType` without a blank line before the class end (A1).
- 16 Javadoc lines with `<p>` alone and one `</p>` in `StartMainView` (A2).
- 7 exception messages with a final period, e.g., in `DrawCallThrottler`, `AbstractObservationView`,
  `SnakeStepLogic`, `SnakeEditToolBarViewModel`, and `EtpetsAgentLogic`, and the messages of the
  `requireNonNull(..)` checks in `DefaultMainViewModel` and `LabMainViewModel` (B1).
- 17 `requireNonNull(..)` calls in main sources; check in the groups that each asserts a `@Nullable` value.
- `setAccessible(..)` in `AbstractMainViewTest` and `DefaultMainViewModelTest` (C5, C8).
- 6 line comments in tests that do not precede an assertion, in `StatisticsMetricLayoutTest` and
  `StatisticsHistoryChartViewTest` (C3, C7).
- No trailing whitespace, tabs, wildcard imports, `@throws NullPointerException`, `{@return`, `String.format(..)`
  without a `Locale` (also on the next line), `System.out` or `System.err` in main sources, `@DisplayName`, `@Nested`,
  `@Nullable` in tests, or underscores in test names. `toUpperCase(..)` and `toLowerCase(..)` use `Locale.ROOT`.
  Javadoc in tests only in the manual utilities `ExtremaGoldenValueAnalyzer` and `EtpetsScoreTuningRunner`.

The script stays in the scratchpad of its session; the final check writes a new one with the same checks.

## Phase A: script fixes

### A1. File endings

- Remove the blank line after the final `}` in the 27 files; add the blank line before the class end of
  `etpets.model.entity.PetActionType` (`java.md`, "Code Style"). `lab.shared.LabColorMode` already has it.
- Empty type bodies stay without a blank line.

### A2. Javadoc paragraphs

- Join a `<p>` alone on a line with the first word of the next line (`<p>Text`), and remove the `</p>` of
  `StartMainView` (`java.md`, "Javadoc", "Style"). Do not put `<p>` before block elements; check such cases by hand.

## Phase B: cross-cutting blocks

### B1. Exception messages

All `throw` statements in `simulations` follow the three forms of `java.md` ("Exceptions"), without a final period,
as `core`, `ui`, and `engine` do since `code-other-classes.md`; adapt the tests that compare messages.

#### Exception messages of StatisticsExtremum

- Origin: `code-warning-suppressions.md` (#68), `NumericCastThatLosesPrecision` in main sources (2026-11-07)
- Types: `core.model.StatisticsExtremum`
- Problem: The compact constructor throws `"value must be finite: " + value` and `"stepCount must be >= 0"`; the second
  message lacks the rejected value.
- Proposal: `value must be finite, but was: ...` and `stepCount must be >= 0, but was: ...`.

#### Exception message of ReproductionOption

- Origin: refinement of #111 (2026-11-07)
- Types: `etpets.model.EtpetsAgentLogic` (nested private record `ReproductionOption`)
- Problem: The compact constructor throws
  `"EtpetsAgentLogic: ReproductionOption partnerCell must contain a Pet agent."` with a component prefix and a final
  period, and without the rejected value. No test asserts the text.
- Proposal: `partnerCell must contain a Pet agent, but was: ` plus the agent entity of `partnerCell`; the same for the
  similar message of `ActionCandidate`.

### B2. Log messages

All `AppLogger` calls in `simulations` follow "Logging with AppLogger" in `java.md` and the log message guideline under
"Review guidelines". Most calls are in `DefaultMainViewModel` (24), `AbstractDefaultMainView` (11), `AbstractMainView`
(6), and `WatorStepLogic` (6, mostly commented-out traces).

### B3. Member order

The members of all types follow `java.md` ("Code Style"), and the tests follow the declaration order (`junit.md`,
"Organization"). The scan of `code-other-classes.md` (category 2) found 49 of 232 types in `simulations` that deviate,
almost only in the visibility order of the methods (private helpers next to their callers, protected before public).
Scan again with a script, reorder the deviating types and their tests, and do not change anything else in this
commit.

### B4. Edge behavior checks and direction rings

#### Edge behavior checks and direction rings

- Origin: `code-other-classes.md`, category 4, group 4 (`engine.neighborhood`)
- Types: `ConwayStepLogic`, `ForestStepLogic`, `SnakeStepLogic`, `WatorStepLogic`, `SugarSimulationManager`,
  `SugarAgentLogic`, `ReboundingSimulationManager`, `ReboundingEditToolBarViewModel`
- Problem: Six checks compare an edge behavior action with `EdgeBehaviorAction.VALID` and `WRAPPED`
  (`ConwayStepLogic`, `ForestStepLogic`, `SnakeStepLogic`, `WatorStepLogic`, `SugarSimulationManager`, and
  `SugarAgentLogic`), although `EdgeBehaviorAction.isWithinGrid()` exists.
  `ReboundingEditToolBarViewModel.updateAvailableDirections(..)`,
  `ReboundingSimulationManager.resolveDirectionRing(..)`, and the constructor of `SnakeStepLogic` select the
  direction ring by cell shape and neighborhood mode themselves instead of calling
  `CellNeighborhoods.directionRing(neighborhoodMode, cellShape)`.
- Proposal: Use `action.isWithinGrid()` and `directionRing(..)`; `ReboundingSimulationManager` drops
  `resolveDirectionRing(..)`. `directionRing(..)` rejects `TRIANGLE` with an `IllegalArgumentException`, which
  replaces the `IllegalStateException` of the Rebounding classes. `directionRing(..)` returns a ring for square cells
  with `EDGES_AND_VERTICES`, which `SnakeStepLogic` rejects today; decide whether it keeps this check or relies on
  `SnakeConfig.isValid()`, which allows only `NEIGHBORHOOD_MODE_DEFAULT`. `EtpetsAgentLogic` checks only `VALID`
  and stays unchanged. Existing tests (e.g., `SnakeMoveStrategiesTest`, `LabNeighborhoodHighlightsTest`) keep the
  ring constants and actions as test data.

### B5. Constructor parameters of step runners and step logic

#### Constructor parameters of the step runners

- Origin: `code-private-constructor-classes.md`, category 6
- Types: `EtpetsStepRunner`, `SugarStepRunner`, `LangtonStepRunner`, and their callers `EtpetsSimulationManager`,
  `SugarSimulationManager`, and `LangtonSimulationManager`; the step logic classes `ConwayStepLogic`,
  `ForestStepLogic`, `ReboundingStepLogic`, `SnakeStepLogic`, and `WatorStepLogic`
- Problem: The step runner constructors take `(random, model, idSequence)`, `(config, random, model)`, and
  `(config, model)`, while the engine runners `AsynchronousStepRunner` and `SynchronousStepRunner` take the grid model
  first, and the static step logic orders `apply(..)` as grid model, config, further inputs (`simulations.md`,
  "Naming"). The step logic constructors take the `GridStructure` first, then the config and the further inputs;
  `WatorStepLogic(config, random, creatureFactory)` needs no structure.
- Proposal: Order the constructor parameters of all step runners as grid model, config, further inputs; decide
  whether `simulations.md` states this order for step runners and for the step logic constructors (structure, config,
  further inputs) too. An API change with one caller each.

### B6. Metrics catalogs of the statistics

#### Metrics catalogs of the statistics classes

- Origin: `code-private-constructor-classes.md`, cross-category decisions (catalog rule in `simulations.md`,
  "Model")
- Types: the eight statistics classes that extend `BaseTimedSimulationStatistics` (not `LabStatistics`)
- Problem: Each class exposes its metrics catalog through the static noun-style accessor `metrics()`, but builds a new
  `List.of(..)` on every call instead of returning a constant unmodifiable list, as the catalog rule requires
  (`ConwayPatterns.choices()`, `SnakeMoveStrategies.strategies()`). Callers are the simulation managers, the
  observation views, and several tests in `simulations.core`.
- Proposal: Store the list in a private static final constant and return it from `metrics()`; decide whether the
  catalog rule applies to static accessors of instantiable classes and, if so, clarify it in `simulations.md`.

### B7. Line length

Wrap all lines over 120 characters in main and test sources (`java.md`, "Code Style"); 385 lines in the initial
check, most in `core.model` tests (57), `lab.view` (30), `core.viewmodel` (29), `etpets.model` (28), `wator.view` (22),
and `core.view` (22). Runs after B1 to B6, because they change lines; wrap only, without other changes.

## Phase C: groups of `simulations.core`

### C1. `core.shared`

The layer-neutral enums, records, and interfaces: `CellDisplayMode`, `NoUserActionContext`, `SimulationMode`,
`SimulationNotificationType`, `SimulationStartMode`, `SimulationState`, `SimulationStepEvent`,
`SimulationTerminationCheckMode`, `SimulationUserActionContext`, and `SimulationUserActionScope`.

### C2. `core.model`: managers, statistics base, and user actions

`SimulationConfig`, `SimulationManager`, `SimulationStatistics`, `TimedSimulationStatistics`, `SimulationUserAction`,
`NoUserAction`, `SimulationInitializationCancellation`, `SimulationInitializationCanceledException`,
`AbstractTimedSimulationManager`, and `BaseTimedSimulationStatistics`.

Notes:

- `AbstractTimedSimulationManager` is tested through all simulation managers in `SimulationManagerTest`;
  `BaseTimedSimulationStatistics` only through its subclasses.
- First analysis (2026-11-08): the protected abstract methods of `AbstractTimedSimulationManager` precede the public
  methods (B3); its one-parameter constructor has no caller in main sources (only a fixture in
  `DefaultMainViewModelTest`). `BaseTimedSimulationStatistics` checks `cellCount() < 0`, which cannot occur, and
  initializes `stepCount = 0`.

#### Tests for the timed managers

- Origin: `code-interface-types.md`, category 5
- Types: `AbstractTimedSimulationManager`, `SimulationManagerTest`
- Problem: `SimulationManagerTest` mainly tests `AbstractTimedSimulationManager` through the simulation managers; its
  name does not match the tested class.
- Proposal: Rename or split `SimulationManagerTest` (e.g., into `AbstractTimedSimulationManagerTest`). Note
  (2026-11-08): the test checks the cancellation, samples, and extrema of all managers, including the non-timed Lab
  manager; as such it is a `<Topic>Test` in the package of the common abstraction `SimulationManager` (`junit.md`,
  "Test Types") and may keep its name.

### C3. `core.model`: statistics history and extrema

`StatisticsChartGroup`, `StatisticsExtrema`, `StatisticsExtremaMode`, `StatisticsExtremaTracker`, `StatisticsExtremum`,
`StatisticsHistory`, `StatisticsMetric`, and `StatisticsSample`.

Notes:

- First analysis (2026-11-08): `StatisticsHistory` is used only by `AbstractTimedSimulationManager` in the same
  package; `size()`, `clear()`, `capacity()`, `DEFAULT_CAPACITY`, and the constructor with a capacity have no caller in
  main sources.

### C4. `core.viewmodel`: config, control, and observation

`SimulationConfigViewModel`, `SimulationControlViewModel`, `SimulationObservationViewModel`, `AbstractConfigViewModel`,
`DefaultControlViewModel`, `DefaultObservationViewModel`, and `SeedProperty`.

#### Component names of CommonConfigSettings

- Origin: `code-private-constructor-classes.md`, deferred finding "Shared config constants" (`SEED_INITIAL` ->
  `SEED_DEFAULT`)
- Types: `AbstractConfigViewModel` (nested record `CommonConfigSettings` and the constructor that reads it), the nine
  config ViewModels, `AbstractConfigViewModelTest`
- Problem: The record names its seven default components `cellShapeInitial`, `gridEdgeBehaviorInitial`,
  `gridWidthInitial`, `gridHeightInitial`, `cellEdgeLengthInitial`, `cellDisplayModeInitial`, and `seedInitial`, and
  its Javadoc says "initial ...", while the config ViewModels fill them from the `*_DEFAULT` constants of the
  `*Constraints` classes. The other components already follow the constraint suffixes (`*Values`, `*Min`, `*Max`,
  `*Step`).
- Proposal: Rename the `*Initial` components to `*Default` (e.g., `seedDefault`) and adapt the Javadoc and
  `AbstractConfigViewModelTest`; the callers pass the arguments by position and need no change. An API change.

### C5. `core.viewmodel`: main ViewModels and user action descriptors

`SimulationMainViewModel`, `AbstractMainViewModel`, `DefaultMainViewModel`, `EditToolBarViewModel`, and
`SimulationUserActionDescriptor`.

Notes:

- `DefaultMainViewModel` is the largest class of the package (about 925 lines, 26 private methods); its package-private
  constructor takes an `ExecutorService` for the tests.

#### Reflection in DefaultMainViewModelTest

- Origin: analysis of the tests for reflection (2026-10-30)
- Types: `DefaultMainViewModel`, `DefaultMainViewModelTest`
- Problem: The test reads private fields of `DefaultMainViewModel` through reflection (`getDeclaredField`,
  `setAccessible(true)`, and `Field.get`) in its helpers `getField(Object, String)` (instance fields) and
  `getStaticField(String)` (static fields).
- Proposal: Look at every call of the two helpers and decide per field whether the test can check the behavior through
  the public API or an observable property instead. Otherwise expose the state as a package-private accessor
  (`java.md`, "Test Access"). Remove the helpers.

### C6. `core.view`: config and control views

`SimulationConfigView`, `SimulationControlView`, `AbstractConfigView`, `AbstractControlView`, `DefaultControlView`,
and `SimulationLocalizationKeys`.

#### Range tooltip overload for integer bounds

- Origin: `code-warning-suppressions.md` (#68), `SameParameterValue` in main sources (2026-11-07)
- Types: `AbstractConfigView`
- Problem: The protected overload `formatDoubleRangeTooltip(String, InputDoubleIntRangeProperty)` has only one
  caller, `createLayoutPane(..)` of the same class, always with `CONFIG_CELL_EDGE_LENGTH_TOOLTIP`, which IntelliJ
  reports as `SameParameterValue` (suppressed). No subclass calls it; the overload for `InputDoubleProperty` is used
  by the config views of Forest and Snake.
- Proposal: Decide whether the overload stays `protected` as the counterpart for subclasses or becomes `private`;
  then check whether the suppression is still needed. An API change if it becomes `private`.

### C7. `core.view`: observation views and statistics chart

`SimulationObservationView`, `AbstractObservationView`, and `StatisticsHistoryChartView`.

#### Optional parameters of AbstractObservationView

- Origin: #41 (rule "Use `Optional` only as a return type" in `java.md`, "Nullability (JSpecify)")
- Types: `AbstractObservationView`, the nine observation views that call the two methods
- Problem: The protected methods `updateStatusSectionLabel(Optional<STA>)` and
  `updateGenericMetricSection(Optional<STA>, StatisticsExtrema)` take `Optional` parameters and suppress
  `OptionalUsedAsFieldOrParameterType`; every caller passes `viewModel.getStatistics()`.
- Proposal: Remove the `Optional` parameters, e.g., by reading the statistics from the ViewModel inside the methods
  or by taking a `@Nullable STA`, and remove the suppressions; adapt the callers. An API change.

#### Test accessor of StatisticsHistoryChartView

- Origin: refinement of #69 (2026-10-30)
- Types: `StatisticsHistoryChartView`, `StatisticsHistoryChartViewTest`
- Problem: The package-private `chartsForTesting()` only reads the charts for the test; `java.md` ("Test Access")
  reserves `*ForTesting` for the test infrastructure of `core`.
- Proposal: Rename it to the noun-style `charts()` and adapt the test.

### C8. `core.view`: main views, edit tool bar, and canvas drawing

`SimulationMainView`, `AbstractMainView`, `AbstractDefaultMainView`, `EditToolBarView`, `DrawCallThrottler`,
`CanvasPainters`, `CellDrawer`, and `CoordinateDrawer`.

#### Reflection in AbstractMainViewTest

- Origin: analysis of the tests for reflection (2026-10-30)
- Types: `AbstractMainView`, `AbstractMainViewTest`
- Problem: The test reads private fields of `AbstractMainView` (or of a class passed as `declaringClass`) through
  reflection (`getDeclaredField`, `setAccessible(true)`, and `Field.get`) in its helpers `getNullableField(..)` and
  `getNonNullField(..)`.
- Proposal: Look at every call of the helpers and decide per field whether the test can check the behavior through the
  public or protected API (e.g., the node tree of the view) instead. Otherwise expose the state as a package-private
  accessor (`java.md`, "Test Access"). Remove the helpers.

#### Tooltips of the edit tool bar buttons

- Origin: #36 (review of the split of the localization keys, 2026-11-04)
- Types: `EditToolBarView`; `simulations.md` ("UI Construction")
- Problem: `EditToolBarView` creates a `Tooltip` directly for all its buttons: for the edit and select buttons with
  the fixed keys `SIMULATION_TOOLBAR_EDIT_TOOLTIP` and `SIMULATION_TOOLBAR_SELECT_TOOLTIP`, and for the action
  buttons with `descriptor.tooltipKey()`. `simulations.md` allows direct `Tooltip` instances only for computed texts
  without a fixed key and says that edit tool bar buttons get their tooltip key from `SimulationUserActionDescriptor`;
  the edit and select buttons have no descriptor, and the rule does not say how a button without a label sets its
  tooltip. `FXComponentFactory` has no public method for a button with a tooltip (only the private
  `createTextFieldButton(..)`).
- Proposal: Decide whether the tool bar buttons keep `new Tooltip(..)` and clarify the rule in `simulations.md`
  (buttons without a label get their own tooltip; the edit and select buttons use the fixed keys of
  `SimulationLocalizationKeys`), or create the four buttons through one helper that sets text and tooltip (e.g., in
  `EditToolBarView`, or a new method in `FXComponentFactory`, which would be an API change).

### C9. `core` root package and `start`

`SimulationFactory`, `SimulationInstance`, `SimulationTerminationHandle`, `SimulationType`, `StartFactory`, and
`StartMainView`. `StartMainView` implements `SimulationMainView` and `SimulationControlView`.

## Phase D: simulation groups

Each group covers all sub-packages of one simulation (package root, `model`, `model.entity`, `model.strategy`,
`shared`, `view`, and `viewmodel`). Wa-Tor (D1) sets the standard for the families; the later groups adopt its
decisions.

Families and their known deviations:

- Statistics: eight extend `BaseTimedSimulationStatistics`, take the `GridStructure`, and expose the static `metrics()`
  (B6); `LabStatistics` implements `SimulationStatistics` directly. No own tests; covered through
  `SimulationManagerTest`, `StatisticsMetricLabelKeyTest`, `StatisticsMetricLayoutTest`, and some ViewModel and view
  tests.
- Simulation managers: eight extend `AbstractTimedSimulationManager` and have the constructors `(config)` and
  `(config, SimulationInitializationCancellation)`; `LabSimulationManager` implements `SimulationManager` directly and
  has only `(config)`.
- Step runners and step logic: see B5. Test gaps for the later separate optimization: the five `*StepLogic` classes and
  `LangtonStepRunner`, which contains the Langton rules itself (six package-private methods).
- Termination conditions and user actions: an implicit constructor and one public method each; all eight user actions
  have a test, no termination condition has one.
- Entities: the `sealed` bases `ResourceBase` and `CreatureBase` are tested through their subclasses; `Pet` takes two
  `Integer` parameters (the parent IDs) next to `int` parameters; `CreatureFactory` and `EtpetsIdSequence` create
  running sequence numbers and have no test of their own.
- Config ViewModels take only the simulation state property; the edit tool bar ViewModels of Conway, Langton, and
  Rebounding have an implicit constructor, those of ET Pets, Snake, and Sugar an explicit one that selects the first
  choice. Only `LangtonEditToolBarViewModel` has a test.
- Main views take `(viewModel, [editToolBarViewModel], entityDescriptorRegistry, configView, controlView,
  observationView)`; Forest and Wa-Tor have no edit tool bar. `LabMainView` extends `AbstractMainView` directly and
  has 23 private methods; the Lab has its own control view and ViewModel and its own main ViewModel.

### D1. Wa-Tor (reference)

### D2. Rebounding

### D3. Snake

### D4. Sugarscape

### D5. ET Pets

#### Shuffle and coordinates of EtpetsSimulationManager

- Origin: `code-other-classes.md`, deferred finding "Shuffle with cancellation checks" (2026-10-30)
- Types: `EtpetsSimulationManager`
- Problem: The class still has the private `shuffleCoordinates(..)` (a Fisher-Yates shuffle with cancellation checks)
  and `createCoordinates(..)`, which `GridInitializers` dropped in `code-other-classes.md`.
- Proposal: Use `Collections.shuffle(list, random)` and `structure.coordinatesList()` as decided there; the random
  sequence of a seed stays the same (same swaps with the same `nextInt(..)` calls), and a manual test with the maximum
  grid of ET Pets was fast (2026-10-30). Check that the remaining checkpoints still follow `simulations.md` ("Model
  Initialization").

### D6. Conway

### D7. Forest

### D8. Langton

### D9. Lab

## Cross-group decisions

Decisions of `code-other-classes.md` (2026-10-29 to 2026-10-30), `code-warning-suppressions.md` (#68), and the sibling
plans `code-interface-types.md`, `code-record-types.md`, `code-private-constructor-classes.md`, and
`code-enum-types.md` apply to all phases. Most are rules now; check the code against the current rules, not against
this summary:

- Rules from `code-other-classes.md`, all in `java.md`: no explicit initialization of fields to their default value
  ("Code Style"); member order ("Code Style"); one final newline and one blank line before the closing brace of a
  non-empty type body ("Code Style"); no `requireNonNull(..)` of non-null parameters and no documented
  `NullPointerException` ("Nullability", "Tags"); the Javadoc rules ("Javadoc": constructor Javadoc only when it
  explains something, "Creates a ..." summaries, `{@code true}`, `@return` instead of `{@return ...}`, `@see` rarely,
  `<p>` directly before the first word); `toDisplayString()` with `name()` for enum values ("Naming Conventions");
  the three forms of exception messages ("Exceptions").
- Public methods and constructors without a caller in main sources are removed (API change with approval) unless the
  maintainer keeps them for a planned use; `core`, all `engine` packages, and the drawing toolkit of `ui` keep their
  complete API (`core.md`, `engine.md`, and `ui.md`).
- Code in `simulations` checks edge behavior actions with `EdgeBehaviorAction.isWithinGrid()` instead of comparing
  with `VALID` and `WRAPPED`, and selects a direction ring with `CellNeighborhoods.directionRing(..)` (B4).
- Lists are shuffled with `Collections.shuffle(list, random)`, without cancellation checks during the shuffle; all
  grid coordinates in row-major order come from `GridStructure.coordinatesList()` (D5).
- `@SuppressWarnings` stay as narrow as possible; #68 reviewed all suppressions, so add or remove one only with the
  maintainer's approval (`java.md`, "Suppressions Policy").
- Tests do not use reflection on private members; state is checked through the API, an observable property, or a
  package-private accessor without the `*ForTesting` suffix (`java.md`, "Test Access", #69).

Decisions of this plan:

None yet.

## Deferred findings

None yet.

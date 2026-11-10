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
  form, e.g., the shared views and ViewModels prefix them with a fixed component name through `LOG_COMPONENT`
  (`"%s: ..."`, e.g., `MainView`) and append diagnostic `name=value` pairs, and the commented-out diagnostic traces of the step logic (e.g., `WatorStepLogic`) stay as
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
| A2 | Javadoc paragraphs                                            |     - | done        |
| B1 | Exception messages                                            |     - | done        |
| B2 | Log messages                                                  |     - | done        |
| B3 | Member order                                                  |     - | done        |
| B4 | Edge behavior checks and direction rings                      |     - | done        |
| B5 | Constructor parameters of step runners and step logic         |     - | done        |
| B6 | Metrics catalogs of the statistics                            |     - | done        |
| B7 | Line length                                                   |     - | done        |
| C1 | `core.shared`                                                 |    10 | done        |
| C2 | `core.model`: managers, statistics base, and user actions     |    10 | done        |
| C3 | `core.model`: statistics history and extrema                  |     8 | done        |
| C4 | `core.viewmodel`: config, control, and observation            |     7 | done        |
| C5 | `core.viewmodel`: main ViewModels and user action descriptors |     5 | done        |
| C6 | `core.view`: config and control views                         |     6 | done        |
| C7 | `core.view`: observation views and statistics chart           |     3 | done        |
| C8 | `core.view`: main views, edit tool bar, and canvas drawing    |     8 | done        |
| C9 | `core` root package and `start`                               |     6 | done        |
| D1 | Wa-Tor (reference)                                            |    21 | done        |
| D2 | Rebounding                                                    |    18 | done        |
| D3 | Snake                                                         |    25 | done        |
| D4 | Sugarscape                                                    |    28 | done        |
| D5 | ET Pets                                                       |    42 | done        |
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
- Status: done (2026-11-08).

#### Exception message of ReproductionOption

- Origin: refinement of #111 (2026-11-07)
- Types: `etpets.model.EtpetsAgentLogic` (nested private record `ReproductionOption`)
- Problem: The compact constructor throws
  `"EtpetsAgentLogic: ReproductionOption partnerCell must contain a Pet agent."` with a component prefix and a final
  period, and without the rejected value. No test asserts the text.
- Proposal: `partnerCell must contain a Pet agent, but was: ` plus the agent entity of `partnerCell`; the same for the
  similar message of `ActionCandidate`.
- Status: done (2026-11-08); the entity is appended with its `toString()`.

### Decisions

- All deviating messages of `throw` statements and `requireNonNull(..)` calls in main sources follow the three forms
  of `java.md` now; already conforming messages and the test fixtures stay unchanged, except for the final period of
  the fixture message in `DefaultObservationViewModelTest`.
- Blank checks append the value in quotation marks, as `AppStorage` does: `toolId must not be blank, but was: "..."`.
- Checks of related values name both values as `name=value` pairs after `but was:` (e.g., `StatisticsMetric`,
  `AbstractObservationView`, and the grid model records).
- Precondition failures of the ET Pets actions, the placement checks of `EtpetsSimulationManager`, the parsers of
  `ConwayTransitionRules` and `LangtonMovementRules`, and the `requireNonNull(..)` checks of the simulation manager use
  `Unable to ...`; the messages lose the `EtpetsAgentLogic:` prefix.
- `SimulationInitializationCanceledException` keeps its own message without the final period; its test is adapted.
- `LabMainView` names the switch value as the local variable `checkerGroup`; `ConwayTransitionRules.parse(..)` checks
  the slash count with one check instead of two, with the same behavior.
- The three checks of unsupported cell shapes in `ReboundingSimulationManager`, `ReboundingEditToolBarViewModel`, and
  `SnakeStepLogic` stay unchanged until B4 replaces them.

### B2. Log messages

All `AppLogger` calls in `simulations` follow "Logging with AppLogger" in `java.md` and the log message guideline under
"Review guidelines". Most calls are in `DefaultMainViewModel` (24), `AbstractDefaultMainView` (11), `AbstractMainView`
(6), and `WatorStepLogic` (6, mostly commented-out traces).

### Decisions

- Messages without a component prefix get the class name: the main views of the simulations and the Lab log
  `<Class>: Skipping canvas draw: <field> is not initialized`, `SimulationType` logs
  `Ignoring invalid URI for simulation: ...`.
- `LangtonRuleProperty` logs the ignored rule string with `warnf(..)` instead of `errorf(e, ..)`, with the default as
  `fallback` and the exception itself as `reason`, because `getMessage()` may return `null`.
- The shared views and ViewModels keep their fixed `LOG_COMPONENT` (`MainView` for `AbstractMainView` and
  `AbstractDefaultMainView`) and their `name=value` pairs; their messages lose the final period, put the pairs after a
  colon instead of a period, join two sentences with `; `, and use `Ignoring ...` for ignored user actions and
  `Failed to ...` for failures. Batch state changes are logged as `transition=A -> B`. `logSimulationInfo(..)` of
  `DefaultMainViewModel` appends the context as `; config=..., statistics=...`.
- The pet and egg events of `EtpetsAgentLogic` stay at `info`, because the further development of ET Pets needs them
  (maintainer); only their wording follows the guideline.
- The commented-out traces in `WatorStepLogic` and `EtpetsAgentLogic` and the `DEBUG_MODE` traces of
  `AbstractDefaultMainView` stay; the latter only lose their final period, and the two step messages say
  `Handling ...` instead of `Handle ...`, like their neighbors.
- Lines that were longer than 120 characters before stay for B7.

### B3. Member order

The members of all types follow `java.md` ("Code Style"), and the tests follow the declaration order (`junit.md`,
"Organization"). The scan of `code-other-classes.md` (category 2) found 49 of 232 types in `simulations` that deviate,
almost only in the visibility order of the methods (private helpers next to their callers, protected before public).
Scan again with a script, reorder the deviating types and their tests, and do not change anything else in this
commit.

### Decisions

- The new scan (2026-11-08) found 39 of 305 types in main sources, including nested types, all with deviations in the
  order of their instance methods: the `*Config` records, simulation managers, and statistics classes of all eight
  timed simulations, the main views of ET Pets, Sugar, and Wa-Tor, the config ViewModels of Conway and the Lab,
  `LabMainViewModel`, `StartMainView`, `ResourceBase`, and seven classes of `simulations.core`. A script reordered
  their methods as `java.md` requires, also the protected methods of the abstract classes after the public ones; it
  kept overloads and a getter with its setter together and moved each method with its Javadoc and annotations.
- Seven test classes of `simulations.core` follow the declaration order of their production classes now
  (`junit.md`, "Organization"); only test methods were reordered, helpers and fields keep their place. Enum tests and
  the topic test `SimulationManagerTest` were not checked.
- The commit changes only the order of lines; no line was added or removed.

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
- Note (B1, 2026-11-08): B1 left the messages of the three cell shape checks that B4 replaces unchanged; a check that
  stays adopts the forms of `java.md`.
- Status: done (2026-11-08).

### Decisions

- The six checks use `isWithinGrid()`; the imports of `EdgeBehaviorAction` that became unused are removed.
  `EtpetsAgentLogic` (checks only `VALID`) and the `VALID` argument in `SugarAgentLogic` stay unchanged.
- `ReboundingSimulationManager`, `ReboundingEditToolBarViewModel`, and `SnakeStepLogic` select their ring with
  `CellNeighborhoods.directionRing(..)`; `resolveDirectionRing(..)` and the three own cell shape checks are removed.
  Triangle cells now fail with the `IllegalArgumentException` of the engine; `ReboundingConstraints` and
  `SnakeConstraints` allow only square and hexagon cells.
- `SnakeStepLogic` relies on `SnakeConfig.isValid()`, which allows only `EDGES_ONLY`, instead of rejecting square
  cells with `EDGES_AND_VERTICES` itself (`simulations.md`, "Configuration and Input").
- `LangtonDirectionOptions.selectableInitialDirections(..)` returns `SQUARE_EDGES_DIRECTION_RING` and
  `HEXAGON_DIRECTION_RING` instead of its own identical lists (found in B4).

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
- Status: done (2026-11-08).

### Decisions

- API change: `EtpetsStepRunner(model, random, idSequence)`, `SugarStepRunner(model, config, random)`, and
  `LangtonStepRunner(model, config)`; their callers in the simulation managers are adapted. Their private fields and
  assignments follow the parameter order; the derived `structure` of `LangtonStepRunner` comes last.
- The five step logic classes already take the structure, the config, and further inputs and stay unchanged;
  `WatorStepLogic` needs no structure.
- Rule change (`simulations.md`, "Naming"): constructors of step runners take the grid model, the config, and further
  inputs; constructors of step logic take the `GridStructure`, the config, and further inputs.

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
- Status: done (2026-11-08).

### Decisions

- The eight statistics classes hold their metrics in `private static final List<StatisticsMetric<...>> METRICS`,
  named like `SnakeMoveStrategies.STRATEGIES`, after the other private constants and separated by a blank line;
  `metrics()` returns it. The signature of `metrics()` stays unchanged.
- Rule change (`simulations.md`, "Model"): the catalog rule names the metrics of a statistics class and `metrics()`
  as an example, so it also covers static catalog accessors of instantiable classes.

### B7. Line length

Wrap all lines over 120 characters in main and test sources (`java.md`, "Code Style"); 385 lines in the initial
check, most in `core.model` tests (57), `lab.view` (30), `core.viewmodel` (29), `etpets.model` (28), `wator.view` (22),
and `core.view` (22). Runs after B1 to B6, because they change lines; wrap only, without other changes.

### Decisions

- A script wrapped 353 of the 368 lines that were over 120 characters after B6 (2026-11-08), 15 lines were wrapped by
  hand; no line of `simulations` in main and test sources is longer than 120 characters now.
- Method signatures put one parameter per line, aligned with the first parameter; if the aligned lines are still too
  long, the first parameter starts on a new line with continuation indent.
- Other code breaks at the lowest nesting level and as far right as possible: after a comma, before an operator,
  before a method call in a chain, and only last after `=`; type arguments are broken only where a single type is too
  long (the constructors of the main views).
- Long string literals are split at a space into two concatenated literals; Javadoc and line comments are rewrapped
  at word boundaries, and commented-out code is wrapped like code.
- The trailing `TODO` comment in `EtpetsAgentLogic` moved to its own line above the code; apart from this, only
  whitespace and the concatenations of split literals changed.
- After the IntelliJ reformat of the maintainer, the type arguments of five main view constructors were wrapped once
  more, because the formatter aligns their continuation lines further right.

## Phase C: groups of `simulations.core`

### C1. `core.shared`

The layer-neutral enums, records, and interfaces: `CellDisplayMode`, `NoUserActionContext`, `SimulationMode`,
`SimulationNotificationType`, `SimulationStartMode`, `SimulationState`, `SimulationStepEvent`,
`SimulationTerminationCheckMode`, `SimulationUserActionContext`, and `SimulationUserActionScope`.

Notes:

- Inventory (2026-11-08, verified against the sources): eight enums, the record `SimulationStepEvent`, and the marker
  interface `SimulationUserActionContext`, all public. Tests exist for `CellDisplayMode`, `SimulationMode`,
  `SimulationNotificationType`, `SimulationState`, and `SimulationStepEvent`; the other five types have no methods
  of their own and need none (`junit.md`, "Common Contract Patterns").
- Every constant and method has a caller in main sources; member order, exception message, and tests follow the
  rules. Boolean queries are documented with "Checks if" and "Indicates whether", as elsewhere in `engine` and `ui`;
  no change.

#### Javadoc of the states that does not match the controls

- Origin: C1 (2026-11-08)
- Types: `SimulationState`
- Problem: The constant Javadoc describes the controls inaccurately, compared with `AbstractConfigView`,
  `DefaultControlView`, and `DefaultControlViewModel.isControlConfigDisabled()`:
    - `PAUSED` says "Configuration controls are enabled", but the simulation config and the start mode are disabled,
      because `PAUSED` is not startable; only the control settings (mode, step duration and count, termination
      check) are enabled. `PAUSED` is also reached when the termination condition is met, not only by the user.
    - `PAUSING_BATCH` says "Cancel action is available", but the cancel button is disabled.
    - `RUNNING_BATCH` says "without intermediate UI updates", but the continuous batch mode draws after each batch,
      and the step count is updated during the batch.
- Proposal: Correct the three constants and distinguish the simulation config from the control settings, e.g., for
  `PAUSED`: "The simulation config is disabled; the control settings are enabled. Resume and cancel actions are
  available.", for `PAUSING_BATCH`: "No actions are available until the batch pauses.", and for `RUNNING_BATCH`:
  "Simulation is running in batch mode in a background thread; the canvas is drawn only after a batch."
- Status: done (2026-11-08).

#### Javadoc of the notification types

- Origin: C1 (2026-11-08)
- Types: `SimulationNotificationType`
- Problem: `NONE` says "simulation is running normally", but it is also set before a start and on cancel. `TIMEOUT`
  says "stopped due to a timeout", but `DefaultMainViewModel` pauses the timed simulation when a step or the view
  takes too long. `CANVAS_SIZE_LIMIT` says "The canvas size exceeds the allowed limit", but `AbstractMainView` sets
  it when the grid is larger than the maximum canvas size and the canvas shows only part of it.
- Proposal: `NONE`: "No notification is shown."; `TIMEOUT`: "The timed simulation was paused because a step or its
  drawing took too long."; `CANVAS_SIZE_LIMIT`: "The grid is larger than the maximum canvas size; the canvas shows
  only part of it."
- Status: done (2026-11-08).

#### Javadoc of the termination check modes

- Origin: C1 (2026-11-08)
- Types: `SimulationTerminationCheckMode`
- Problem: The type Javadoc says `UNCHECKED` is for "runs where termination is managed externally or not required",
  which the code does not show; with `CHECKED`, `DefaultMainViewModel` pauses the simulation when the termination
  condition is met, and with `UNCHECKED` the simulation continues.
- Proposal: "Use `CHECKED` to pause the simulation when its termination condition is met after a step. Use
  `UNCHECKED` to let it continue beyond that point."
- Status: done (2026-11-08).

#### Javadoc of the start modes

- Origin: C1 (2026-11-08)
- Types: `SimulationStartMode`
- Problem: The constant summaries are imperative ("Start running ...", "Initialize in a paused state ..."), and the
  type Javadoc has `@see SimulationState`, a link to the type of a related property, which `java.md` ("Tags") does
  not want.
- Proposal: "Starts running immediately after initialization." and "Starts in the state `PAUSED` after
  initialization, so that the user can edit the grid before running."; remove the `@see`.
- Status: done (2026-11-08).

#### Link from shared to model

- Origin: C1 (2026-11-08)
- Types: `SimulationUserActionContext`
- Problem: The type Javadoc links to `simulations.core.model.SimulationUserAction`, although `shared` must not depend
  on `model` (`java.md`, "Javadoc", "Style").
- Proposal: Write `{@code SimulationUserAction}` instead.
- Status: done (2026-11-08).

### Decisions

- The five findings are implemented as proposed; only Javadoc changed. The summary of `PAUSED` also says that the
  state is reached by the user or because the termination condition is met.
- No API change, rule change, or new test; the group has no test gaps.

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
- Inventory (2026-11-08, verified against the sources): six interfaces (`SimulationConfig`, `SimulationManager`,
  `SimulationStatistics`, `TimedSimulationStatistics`, `SimulationUserAction`, and
  `SimulationInitializationCancellation`) and four classes (`NoUserAction`,
  `SimulationInitializationCanceledException`, `AbstractTimedSimulationManager`, and `BaseTimedSimulationStatistics`). Tests exist for `SimulationConfig`,
  `SimulationStatistics`, `NoUserAction`, `SimulationInitializationCancellation`,
  `SimulationInitializationCanceledException`, and the topic `SimulationManagerTest`.
- The member order of `AbstractTimedSimulationManager` follows `java.md` since B3. The default methods of
  `SimulationConfig` all have callers; `createGridTopology()` and `createGridSize()` only inside the interface.

#### Unused NoUserAction

- Origin: C2 (2026-11-08)
- Types: `NoUserAction`, `NoUserActionTest`
- Problem: `NoUserAction` has no caller in main sources; Forest has its own `ForestUserAction` with
  `NoUserActionContext`, and the Lab has no user action. Its bound `SM extends AbstractTimedSimulationManager` is
  also narrower than the `SimulationManager` of the interface.
- Proposal: Remove `NoUserAction` and `NoUserActionTest`; `NoUserActionContext` stays. An API change.
- Status: done (2026-11-08).

#### One-parameter constructor of AbstractTimedSimulationManager

- Origin: first analysis of C2 (2026-11-08)
- Types: `AbstractTimedSimulationManager`, `DefaultMainViewModelTest` (fixture `StubSimulationManager`)
- Problem: All eight managers call `(config, metrics)`; `(config)` is only used by the test fixture. Both
  constructor summaries start with "Initializes ..." instead of "Creates a ...".
- Proposal: Remove `(config)`; the fixture passes `List.of()` (or `ConwayStatistics.metrics()`). The remaining
  constructor gets the summary "Creates a manager that samples the given metrics after each executed step." An API
  change.
- Status: done (2026-11-08).

#### Constructor of BaseTimedSimulationStatistics

- Origin: first analysis of C2 (2026-11-08)
- Types: `BaseTimedSimulationStatistics`
- Problem: The constructor checks `cellCount() < 0`, which `GridSize` excludes, and sets `stepCount = 0`
  explicitly; its Javadoc starts with "Initializes ..." and only says that the values start at their initial values.
- Proposal: Remove the check, the assignment, and the constructor Javadoc.
- Status: done (2026-11-08).

#### Wrapped class declarations

- Origin: C2 (2026-11-08)
- Types: `BaseTimedSimulationStatistics`, `SimulationInitializationCanceledException`
- Problem: Both declarations put `implements TimedSimulationStatistics` and `extends RuntimeException` on a second
  line, although the declaration fits in one line.
- Proposal: Join each declaration into one line.
- Status: done (2026-11-08).

#### Tests of the timed manager and statistics base

- Origin: `code-interface-types.md`, category 5 (see "Tests for the timed managers"), and C2 (2026-11-08)
- Types: `AbstractTimedSimulationManager`, `BaseTimedSimulationStatistics`, `SimulationManagerTest`
- Problem: Three tests of `SimulationManagerTest` (`testExecuteStepRecordsOneSample`,
  `testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample`,
  `testExecuteStepsDoesNotRecordSampleForZeroSteps`) check only `AbstractTimedSimulationManager` through Conway. The
  replacement of non-finite metric values with `NaN` is untested. `BaseTimedSimulationStatistics` has no test of its
  own (initial values, `setStepData(..)` and its rejection, `baseToString()`).
- Proposal: Keep `SimulationManagerTest` as the topic test for the cross-manager contracts (cancellation, step-zero
  sample, extrema, defaults of the non-timed Lab manager). Add `AbstractTimedSimulationManagerTest` with a small
  Conway-based fixture like `StubSimulationManager`, move the three tests there, and add a test for the `NaN`
  replacement. Add `BaseTimedSimulationStatisticsTest`.
- Status: done (2026-11-08).

#### Tests for the timed managers

- Origin: `code-interface-types.md`, category 5
- Types: `AbstractTimedSimulationManager`, `SimulationManagerTest`
- Problem: `SimulationManagerTest` mainly tests `AbstractTimedSimulationManager` through the simulation managers; its
  name does not match the tested class.
- Proposal: Rename or split `SimulationManagerTest` (e.g., into `AbstractTimedSimulationManagerTest`). Note
  (2026-11-08): the test checks the cancellation, samples, and extrema of all managers, including the non-timed Lab
  manager; as such it is a `<Topic>Test` in the package of the common abstraction `SimulationManager` (`junit.md`,
  "Test Types") and may keep its name.
- Status: done (2026-11-08); see "Tests of the timed manager and statistics base".

### Decisions

- API change: `NoUserAction` and `NoUserActionTest` are removed; `NoUserActionContext` stays for Forest and the Lab.
- API change: the constructor `AbstractTimedSimulationManager(config)` is removed; the fixture `StubSimulationManager`
  of `DefaultMainViewModelTest` passes `List.of()`. The remaining constructor is documented with "Creates a manager
  that ...".
- `BaseTimedSimulationStatistics` has a constructor without Javadoc, without the impossible cell count check, and
  without `stepCount = 0`; it and `SimulationInitializationCanceledException` declare their supertype on one line.
- `SimulationManagerTest` stays the topic test for the contracts across all managers. The new
  `AbstractTimedSimulationManagerTest` holds the three tests of the sample history, now on the Conway-based fixture
  `TestSimulationManager` with an empty grid, and tests the replacement of non-finite metric values with `NaN`. The
  new `BaseTimedSimulationStatisticsTest` covers the initial values, `setStepData(..)`, its rejection, and
  `baseToString()`.
- No rule change; the group has no remaining test gaps.

### C3. `core.model`: statistics history and extrema

`StatisticsChartGroup`, `StatisticsExtrema`, `StatisticsExtremaMode`, `StatisticsExtremaTracker`, `StatisticsExtremum`,
`StatisticsHistory`, `StatisticsMetric`, and `StatisticsSample`.

Notes:

- First analysis (2026-11-08): `StatisticsHistory` is used only by `AbstractTimedSimulationManager` in the same
  package; `size()`, `clear()`, `capacity()`, `DEFAULT_CAPACITY`, and the constructor with a capacity have no caller in
  main sources.
- Inventory (2026-11-08, verified against the sources): the enums `StatisticsChartGroup` and `StatisticsExtremaMode`,
  the records `StatisticsExtrema`, `StatisticsExtremum`, `StatisticsMetric`, and `StatisticsSample`, the public class
  `StatisticsHistory`, and the package-private class `StatisticsExtremaTracker`. Each has a test class; the topic
  tests `StatisticsMetricLayoutTest` and `StatisticsMetricLabelKeyTest` check the metrics catalogs of all eight timed
  simulations, and `ExtremaGoldenValueAnalyzer` is a manual utility for `SimulationManagerTest`.
- `StatisticsExtremaMode.MIN` has no caller in main sources; it stays as part of the closed set of modes. The records,
  enums, and `StatisticsExtremaTracker` follow the rules otherwise.

#### Public API of StatisticsHistory

- Origin: first analysis of C3 (2026-11-08)
- Types: `StatisticsHistory`, `StatisticsHistoryTest`
- Problem: `StatisticsHistory` is public, but only `AbstractTimedSimulationManager` in the same package uses it, with
  the default constructor, `add(..)`, and `toList()`. `size()`, `clear()`, `capacity()`, and the public constant
  `DEFAULT_CAPACITY` serve only the tests, and the constructor with a capacity lets the tests use a small history.
- Proposal: Make the class package-private like `StatisticsExtremaTracker`, remove `size()`, `clear()`, and
  `capacity()`, make `DEFAULT_CAPACITY` private, and keep the constructor with a capacity package-private for the
  tests (`java.md`, "Test Access"). Adapt the thread-safety paragraph and the tests, which check the size through
  `toList()`. An API change.
- Status: done (2026-11-08).

#### Constructor Javadoc of StatisticsMetric

- Origin: C3 (2026-11-08)
- Types: `StatisticsMetric`
- Problem: The two convenience constructors start their Javadoc with "Convenience constructor ..." instead of
  "Creates a ...". The `@param chartWindowSize` of the record repeats the invariants of the compact constructor
  (`0` for `NONE`, positive otherwise), which `java.md` ("Tags") wants only in its `@throws`. The second convenience
  constructor puts three parameters on its first line, unlike the other signatures (B7).
- Proposal: "Creates a metric that is not charted." and "Creates a metric with `DEFAULT_CHART_WINDOW_SIZE` for a
  charted group and `0` for `NONE`."; reduce `@param chartWindowSize` to the meaning and the rule that metrics of the
  same chart group use the same value, which the constructor cannot check; one parameter per line.
- Status: done (2026-11-08).

#### Bundle check of the metric label keys

- Origin: C3 (2026-11-08)
- Types: `StatisticsMetricLabelKeyTest`
- Problem: The test loads the two bundles itself with one test per locale, while `junit.md` ("Common Contract
  Patterns") requires `LocalizationTestSupport.assertKeysInAllBundles(..)` for resource-key tests.
- Proposal: Replace the two tests and the loader with one test `testLabelKeysExistInAllBundles` that passes all label
  keys to `assertKeysInAllBundles(..)`.
- Status: done (2026-11-08).

#### Comment and assertions in StatisticsMetricLayoutTest

- Origin: initial script check (C3)
- Types: `StatisticsMetricLayoutTest`
- Problem: In `testChartedMetricsShareWindowSizeWithinGroup`, the two-line comment precedes the stream setup, not an
  assertion (`junit.md`, "Test Method Style"). `testChartWindowSizes` runs eight checks without `assertAll(..)`, so
  the first failure hides the others.
- Proposal: Move the comment directly above the assertion inside the lambda, and group the eight checks with
  `assertAll(..)`.
- Status: done (2026-11-08).

### Decisions

- API change: `StatisticsHistory` is package-private; `size()`, `clear()`, and `capacity()` are removed,
  `DEFAULT_CAPACITY` is private, and both constructors, `add(..)`, and `toList()` are package-private. The default
  constructor lost its Javadoc, which linked the now private constant. The field `samples` is declared as `Deque`
  instead of `ArrayDeque` (IntelliJ warning, maintainer). `StatisticsHistoryTest` checks the empty history and the
  size through `toList()`; the tests of `capacity()` and `clear()` are removed.
- `StatisticsMetric` documents its convenience constructors with "Creates a metric ...", keeps only the
  cross-metric rule in `@param chartWindowSize`, and puts one parameter per line in the second convenience
  constructor.
- `StatisticsMetricLabelKeyTest` has one test `testLabelKeysExistInAllBundles` with
  `LocalizationTestSupport.assertKeysInAllBundles(..)` instead of its own bundle loader.
- `StatisticsMetricLayoutTest` groups the window size checks with `assertAll(..)` and places the comment directly
  above the assertion.
- No rule change; the group has no test gaps.

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
- Status: done (2026-11-08).

Notes:

- Inventory (2026-11-08, verified against the sources): the interfaces `SimulationConfigViewModel`,
  `SimulationControlViewModel`, and `SimulationObservationViewModel`, the abstract class `AbstractConfigViewModel`
  with the nested records `ConfigValidationRule` (private) and `CommonConfigSettings`, and the final classes
  `DefaultControlViewModel`, `DefaultObservationViewModel`, and `SeedProperty`. Tests exist for
  `AbstractConfigViewModel` (only `CommonConfigSettings`), `DefaultControlViewModel`, and
  `DefaultObservationViewModel`. All are JavaFX types; no new tests in this plan.
- `DefaultObservationViewModel` is the only implementation of `SimulationObservationViewModel`; the observation
  views of the simulations use the concrete class.
- Test gaps for the later separate optimization: `SeedProperty` (no test) and
  `AbstractConfigViewModel.registerConfigValidationRule(..)`.

#### Unused observation accessors

- Origin: C4 (2026-11-08)
- Types: `SimulationObservationViewModel`, `DefaultObservationViewModel`, `DefaultObservationViewModelTest`
- Problem: `statisticsProperty()` and `statisticsExtremaProperty()` of the interface and `getStatisticsHistory()` of
  the class have no caller in main sources; the views read `getStatistics()`, `getStatisticsExtrema()`, and
  `statisticsHistoryProperty()`.
- Proposal: Remove the three methods and adapt the tests, which then check the getters only. An API change.
- Status: done (2026-11-08).

#### Seed computation of SeedProperty

- Origin: C4 (2026-11-08)
- Types: `SeedProperty`
- Problem: The five package-private static helpers (`isRandomSeed`, `isNumericSeed`, `parseNumericSeed`, `hashSeed`,
  `fallbackHashSeed`) are package-private for tests that do not exist (`java.md`, "Test Access"). `computeSeed(..)`
  parses a numeric seed twice. `fallbackHashSeed(..)` is dead code, because every Java platform must support
  SHA-256 (`MessageDigest`). The Javadoc lacks the empty lines between its paragraphs, starts the constructor with
  "Constructs a SeedProperty", writes `null` without `{@code}`, and repeats non-null contracts ("must be non-null");
  three line comments restate the code.
- Proposal: Reduce the logic to a private `computeSeed(..)` (blank -> random, `Long.parseLong` of the trimmed text,
  `NumberFormatException` -> hash) and a private `hashSeed(..)` that wraps the impossible
  `NoSuchAlgorithmException` into an `IllegalStateException` (`Unable to ...`); remove `fallbackHashSeed(..)` and
  its constants. Fix the Javadoc and remove the three comments. The seeds of numeric and text input stay the same.
- Status: done (2026-11-08).

#### Names in DefaultControlViewModel and DefaultObservationViewModel

- Origin: C4 (2026-11-08)
- Types: `DefaultControlViewModel`, `DefaultObservationViewModel`, `DefaultControlView`, `DefaultMainViewModel`,
  `DefaultControlViewModelTest`
- Problem: `terminationCheckProperty()` exposes the field `terminationCheckMode`, while `simulationModeProperty()` and
  `startModeProperty()` follow their fields. The private constants `STEP_DURATION_INITIAL` and `STEP_COUNT_INITIAL`
  use `_INITIAL` instead of `_DEFAULT` (`simulations.md`, "Configuration and Input"). In
  `DefaultObservationViewModel`, the wrappers are named `statistics`, `statisticsExtremaWrapper`, and
  `statisticsHistoryWrapper`; only `statistics` is created in the constructor, whose Javadoc explains nothing.
- Proposal: Rename `terminationCheckProperty()` to `terminationCheckModeProperty()` (an API change with two callers),
  the constants to `STEP_DURATION_DEFAULT` and `STEP_COUNT_DEFAULT`, and the wrappers to `statisticsExtrema` and
  `statisticsHistory`; initialize `statistics` at its declaration and remove the constructor Javadoc.
- Status: done (2026-11-08).

#### Javadoc and declarations of the ViewModel contracts

- Origin: C4 (2026-11-08)
- Types: `SimulationObservationViewModel`, `AbstractConfigViewModel`, `DefaultControlViewModel`
- Problem: `setStatistics(..)` documents "never {@code null}", which `@NullMarked` already states.
  `AbstractConfigViewModel` and `DefaultControlViewModel` put `implements ...` on a second line, although the
  declaration fits in one line (decided in C2 for the same case).
- Proposal: Remove "never {@code null}"; join both declarations into one line.
- Status: done (2026-11-08).

### Decisions

- API change: the components of `CommonConfigSettings` are named `cellShapeDefault`, `gridEdgeBehaviorDefault`,
  `gridWidthDefault`, `gridHeightDefault`, `cellEdgeLengthDefault`, `cellDisplayModeDefault`, and `seedDefault`; their
  `@param` tags say "default ...".
- API change: `SimulationObservationViewModel.statisticsProperty()` and `statisticsExtremaProperty()` and
  `DefaultObservationViewModel.getStatisticsHistory()` are removed. `DefaultObservationViewModel` holds the
  statistics and the extrema in plain `SimpleObjectProperty` fields, keeps the `ReadOnlyObjectWrapper` only for the
  history, and has a constructor without Javadoc. `DefaultObservationViewModelTest` checks the getters and
  `statisticsHistoryProperty()`; the listener test of the extrema property is removed. `DefaultMainViewModelTest`
  reads the history through `statisticsHistoryProperty()`.
- `SeedProperty` has only the private helpers `computeSeed(..)` and `hashSeed(..)`; `hashSeed(..)` throws an
  `IllegalStateException` (`Unable to hash seed: algorithm=SHA-256`) instead of the removed fallback hash. The
  seeds of all inputs stay the same.
- API change: `DefaultControlViewModel.terminationCheckProperty()` is renamed to `terminationCheckModeProperty()`;
  `DefaultControlView` and the tests are adapted. The private constants are `STEP_DURATION_DEFAULT` and
  `STEP_COUNT_DEFAULT`.
- The fields of `DefaultObservationViewModel` are `statisticsExtrema` and `statisticsHistory`;
  `AbstractConfigViewModel` and `DefaultControlViewModel` declare their interface on one line, and
  `setStatistics(..)` no longer documents "never {@code null}".
- No rule change. Test gaps: `SeedProperty` and `AbstractConfigViewModel.registerConfigValidationRule(..)`.

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
- Status: done (2026-11-08).

#### Null check of the simulation manager factory

- Origin: B1 (2026-11-08)
- Types: `DefaultMainViewModel`, `DefaultMainViewModelTest`
- Problem: `initializeSimulation(..)` checks the non-null result of `simulationManagerFactory.apply(..)` with
  `requireNonNull(..)`, which `java.md` ("Nullability") allows only for `@Nullable` values. The test wraps checked
  exceptions three times in a plain `RuntimeException` (`java.md`, "Exceptions").
- Proposal: Remove the check unless the factory contract becomes `@Nullable`; throw a specific exception in the test
  helpers. Note (2026-11-08): the three test factories repeat the same wait on a latch; one private helper
  `awaitRelease(CountDownLatch)` that throws an `IllegalStateException` replaces the three `RuntimeException`s.
- Status: done (2026-11-08).

#### Unused click and selection state

- Origin: C5 (2026-11-08)
- Types: `AbstractMainViewModel`, `DefaultMainViewModel`
- Problem: `AbstractMainViewModel` keeps a `previousClickedCoordinate` with `previousClickedCoordinateProperty()` and
  `getPreviousClickedCoordinate()`, which nothing in main or test sources reads. `DefaultMainViewModel` keeps a
  `lastSelectedCoordinate` with `lastSelectedCoordinateProperty()`, which nothing reads either (only
  `lastSelectedEntityProperty()` is used, by `SnakeMainView`), and `resetSelectedProperties()` is public but called
  only by the class itself.
- Proposal: Remove `previousClickedCoordinate` and its two accessors (`updateClickedCoordinateProperties(..)` and
  `resetClickedCoordinateProperties()` then only set the last click), remove `lastSelectedCoordinate` and its
  accessor, and make `resetSelectedProperties()` private. An API change.
- Status: done (2026-11-08).

#### Details of AbstractMainViewModel and DefaultMainViewModel

- Origin: C5 (2026-11-08)
- Types: `AbstractMainViewModel`, `DefaultMainViewModel`
- Problem: The type Javadoc of `AbstractMainViewModel` lacks the empty line before its `<p>` paragraph; the field
  `notificationTypeProperty` carries the accessor suffix; `getLastClickedCoordinate()` is the only public method of
  the class that is not `final`. In `DefaultMainViewModel`, two `// reset` comments restate the code, the listener
  lambda of `lastClickedCoordinateListener` has redundant parentheses, and the generation counter is incremented once
  with `lifecycleGeneration++` and twice with `++lifecycleGeneration`.
- Proposal: Add the empty line, rename the field to `notificationType`, make `getLastClickedCoordinate()` `final`,
  remove the two comments and the parentheses, and use `++lifecycleGeneration` everywhere.
- Status: done (2026-11-08).

#### Sealed base classes in simulations.core

- Origin: review guidelines ("Base classes"), C5 (2026-11-08)
- Types: `AbstractMainViewModel` and all other `Abstract*` and `Base*` classes of `simulations.core`
- Problem: The guideline asks for `sealed` where the subclasses are known. All base classes of `simulations.core`
  have subclasses in the simulation packages (e.g., `AbstractMainViewModel`: `DefaultMainViewModel` and
  `LabMainViewModel`); a `permits` clause would make `simulations.core` name types of the simulations, which
  `simulations.md` ("Package Isolation") forbids.
- Proposal: Record as cross-group decision: the base classes of `simulations.core` stay non-sealed; `sealed` applies
  only to closed families inside one simulation (e.g., `CreatureBase`, `ResourceBase`).
- Status: done (2026-11-08).

### Decisions

- The only reflection test checked that `shutdownSimulation()` releases the three view callbacks; it uses the new
  package-private query `DefaultMainViewModel.hasViewListeners()` now, and the helpers `getField(..)` and
  `getStaticField(..)` are removed. The query compares the listeners with the no-op constants through `equals(..)`,
  which is the identity comparison for lambdas, instead of `!=` with a suppression (maintainer).
- `initializeSimulation(..)` no longer checks the non-null result of the factory. The three test factories wait with
  the helper `awaitRelease(CountDownLatch)`, which throws an `IllegalStateException`; the two factories that ignore
  interruptions on purpose stay unchanged.
- API change: `previousClickedCoordinateProperty()`, `getPreviousClickedCoordinate()`, and
  `lastSelectedCoordinateProperty()` with their fields are removed, and `resetSelectedProperties()` is private.
  `updateClickedCoordinateProperties(..)` and `resetClickedCoordinateProperties()` set only the last click.
- `AbstractMainViewModel` has the field `notificationType`, a `final` `getLastClickedCoordinate()`, and a type Javadoc
  with the empty line before its second paragraph; `DefaultMainViewModel` lost two restating comments and redundant
  parentheses and uses `++lifecycleGeneration` everywhere.
- The `@SuppressWarnings("OptionalIsPresent")` of `applyUserAction(SimulationUserActionDescriptor)` stays, because
  `map(..)` would put the side effect of the action into an `Optional` chain.
- The base classes of `simulations.core` stay non-sealed (cross-group decision).
- No rule change; `SimulationUserActionDescriptor` and `EditToolBarViewModel` need no change.

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
  then check whether the suppression is still needed. An API change if it becomes `private`. Note (C6,
  2026-11-08): inlining the call into `createLayoutPane(..)` removes the overload and the suppression; the tooltip
  text stays the same.
- Status: done (2026-11-08); the overload stays `protected` for future simulations (maintainer) and is renamed to
  `formatIntRangeTooltip(..)`, because the property has `int` bounds and the key uses `%d` placeholders.

Notes:

- Inventory (2026-11-08, verified against the sources): the interfaces `SimulationConfigView` and
  `SimulationControlView`, the abstract classes `AbstractConfigView` (nine subclasses) and `AbstractControlView`
  (`DefaultControlView` and `LabControlView`), the final class `DefaultControlView`, and the constant holder
  `SimulationLocalizationKeys` with its test; all 52 keys have callers in main sources. The views are JavaFX types;
  no new tests in this plan.

#### Members of AbstractConfigView used only by the class

- Origin: C6 (2026-11-08)
- Types: `AbstractConfigView`
- Problem: `PERCENT_SCALE` and `toLabeledControlArray(..)` are `protected`, but only the class itself uses them.
  `createLayoutPane(..)` builds a raw `LabeledControl[]` with `System.arraycopy(..)` and suppresses `unchecked`,
  while `createStructurePane(..)` collects its controls in a list and converts it with `toLabeledControlArray(..)`.
- Proposal: Make `PERCENT_SCALE` and `toLabeledControlArray(..)` private (an API change without callers outside the
  class), and let `createLayoutPane(..)` collect its controls in a list like `createStructurePane(..)`; its
  `@SuppressWarnings("unchecked")` is removed (the one of `toLabeledControlArray(..)` stays).
- Status: done (2026-11-08).

#### Comments, Javadoc, and declarations of the config and control views

- Origin: C6 (2026-11-08)
- Types: `AbstractConfigView`, `AbstractControlView`, `DefaultControlView`
- Problem: The Javadoc of `createConfigTitledPane(..)` lacks the empty line before its `<p>` paragraph. The four
  comments `// cellShapeProperty` to `// gridHeightProperty`, `// Only add cellDisplayModeControl if there are
  multiple valid values`, and `// Place both boxes in a StackPane` restate the code. `AbstractControlView` and
  `DefaultControlView` put `implements` and `extends` on a second line, although the declaration fits in one line
  (C2).
- Proposal: Add the empty line, remove the six comments, and join both declarations into one line.
- Status: done (2026-11-08).

### Decisions

- API change: `AbstractConfigView.formatDoubleRangeTooltip(String, InputDoubleIntRangeProperty)` is renamed to
  `formatIntRangeTooltip(String, InputDoubleIntRangeProperty)` and placed next to the other overload; it stays
  `protected` with its `@SuppressWarnings("SameParameterValue")`. `formatDoubleRangeTooltip(..)` is left only for
  `InputDoubleProperty`.
- API change: `PERCENT_SCALE` and `toLabeledControlArray(..)` are private. `createLayoutPane(..)` collects its
  controls in a list and converts them with `toLabeledControlArray(..)`; its `@SuppressWarnings("unchecked")` and
  the `System.arraycopy(..)` are removed (maintainer).
- The Javadoc of `createConfigTitledPane(..)` has the empty line before its second paragraph, six restating comments
  are removed, and `AbstractControlView` and `DefaultControlView` declare their supertype on one line;
  `AbstractConfigView` keeps two lines, because one would exceed 120 characters.
- No rule change; the views get no new tests in this plan.

### C7. `core.view`: observation views and statistics chart

`SimulationObservationView`, `AbstractObservationView`, and `StatisticsHistoryChartView`.

Notes:

- Inventory (2026-11-09, verified against the sources): the interface `SimulationObservationView`, the abstract class
  `AbstractObservationView` with the private nested record `MetricLabels` and nine subclasses (one per simulation and
  the Lab, all with `DefaultObservationViewModel` as ViewModel), and the package-private final class
  `StatisticsHistoryChartView` with the private nested record `GroupChart`. Only `StatisticsHistoryChartView` has a
  test (18 tests). All are JavaFX types; no new tests in this plan.
- `AbstractDefaultMainView` builds the observation region once with the main view, before a simulation exists, calls
  `initializeObservationLabels()` when a simulation is created or reset, and `updateObservationLabels()` after a step
  or a user action.
- Test gap for the later separate optimization: `AbstractObservationView` (sections, metric formatting, extrema
  tooltips, and the rejection in `createObservationSection(..)`).

#### Optional parameters of AbstractObservationView

- Origin: #41 (rule "Use `Optional` only as a return type" in `java.md`, "Nullability (JSpecify)")
- Types: `AbstractObservationView`, the nine observation views that call the two methods
- Problem: The protected methods `updateStatusSectionLabel(Optional<STA>)` and
  `updateGenericMetricSection(Optional<STA>, StatisticsExtrema)` take `Optional` parameters and suppress
  `OptionalUsedAsFieldOrParameterType`; every caller passes `viewModel.getStatistics()`.
- Proposal: Remove the `Optional` parameters, e.g., by reading the statistics from the ViewModel inside the methods
  or by taking a `@Nullable STA`, and remove the suppressions; adapt the callers. An API change.
- Note (C7, 2026-11-09): `getStatisticsExtrema()` is declared only by `DefaultObservationViewModel`, not by
  `SimulationObservationViewModel`, so the base class cannot read the extrema itself today; see "Common update of the
  observation labels".
- Status: done (2026-11-09); see "Common update of the observation labels".

#### Common update of the observation labels

- Origin: C7 (2026-11-09)
- Types: `AbstractObservationView`, `SimulationObservationViewModel`, the nine observation views
- Problem: All nine views implement the abstract `updateObservationLabels()` with the same calls
  `updateStatusSectionLabel(statistics)` and `updateGenericMetricSection(statistics,
  viewModel.getStatisticsExtrema())`; only the Lab omits the second, which returns early without a metrics section.
- Proposal: Declare `getStatisticsExtrema()` in `SimulationObservationViewModel` (implemented by
  `DefaultObservationViewModel`), implement `updateObservationLabels()` as `final` in `AbstractObservationView`, and
  make `updateStatusSectionLabel()` and `updateGenericMetricSection()` private without parameters, reading the
  statistics and extrema from the ViewModel like `updateGridSectionLabel()`. This solves "Optional parameters of
  AbstractObservationView" and removes its suppression `OptionalUsedAsFieldOrParameterType`; the nine overrides are
  removed. An API change.
- Status: done (2026-11-09).

#### Redundant calls in buildObservationRegion

- Origin: C7 (2026-11-09)
- Types: the nine observation views
- Problem: All nine `buildObservationRegion()` call `updateObservationLabels()` first, before the sections exist: the
  metrics section is not created yet, and `createStatusSection()` resets the step label right after. They also call
  `handleSelectedGridCellChanged(viewModel.selectedGridCellProperty().get())` after `createSelectedCellSection()` or
  `createExtendedSelectedCellSection(..)`, which already call it with the same cell, because the listener registered
  in the constructor keeps `selectedGridCell` current.
- Proposal: Remove both calls in the nine views; the behavior stays the same.
- Status: done (2026-11-09).

#### Members and duplication of AbstractObservationView

- Origin: C7 (2026-11-09)
- Types: `AbstractObservationView`
- Problem: `createObservationSection(..)` is `protected`, but only the class itself calls it.
  `createSelectedCellSection()` repeats `createExtendedSelectedCellSection(..)` without extra rows.
  `createGenericMetricSection(..)` creates the min and max header and value labels with copied code, and
  `updateGenericMetricSection(..)` updates them with copied code; `MetricLabels` holds a label and a tooltip per
  extremum that are only both `null` or both set, which needs two `requireNonNull(..)` calls. Both section factories
  build the titled `VBox` with the same lines.
- Proposal: Make `createObservationSection(..)` private (an API change without callers outside the class); let
  `createSelectedCellSection()` delegate to `createExtendedSelectedCellSection(..)` with empty arrays; extract private
  helpers for an extremum label (creation and update) and for the titled section; replace the label and tooltip
  pairs of `MetricLabels` by a private record `ExtremumLabel(Label label, Tooltip tooltip)` as `@Nullable` components,
  so that the `requireNonNull(..)` calls disappear. The UI stays the same.
- Status: done (2026-11-09).

#### Javadoc of AbstractObservationView

- Origin: C7 (2026-11-09)
- Types: `AbstractObservationView`
- Problem: The constructor Javadoc starts with "Constructs a new observation view ..." and explains nothing (C4
  removed such Javadoc). The Javadoc of `handleSelectedGridCellChanged(..)` and `createGenericMetricSection(..)` lacks
  the empty line before its `<p>` paragraph (C5, C6).
- Proposal: Remove the constructor Javadoc and add the two empty lines.
- Status: done (2026-11-09).

#### Test accessor of StatisticsHistoryChartView

- Origin: refinement of #69 (2026-10-30)
- Types: `StatisticsHistoryChartView`, `StatisticsHistoryChartViewTest`
- Problem: The package-private `chartsForTesting()` only reads the charts for the test; `java.md` ("Test Access")
  reserves `*ForTesting` for the test infrastructure of `core`.
- Proposal: Rename it to the noun-style `charts()` and adapt the test.
- Status: done (2026-11-09).

#### Group state and ceiling logic of StatisticsHistoryChartView

- Origin: C7 (2026-11-09)
- Types: `StatisticsHistoryChartView`
- Problem: The state of a chart group is spread over the list `distinctGroups`, the parallel list `groupCharts`
  (matched by index), and the maps `keysByGroup`, `windowSizeByGroup`, and `ceilingByGroup`; the local variable `gc`
  abbreviates its type. Three streams collect with `Collectors.toList()` instead of `toList()` (`java.md`, "Code
  Style"). In `updateCharts(..)`, the first two branches of the ceiling decision compute the same value, and the three
  trailing comments restate the branches. The comment on the window size names `StatisticsMetricLayoutTest` instead
  of the rule of `StatisticsMetric` that metrics of one chart group share their window size.
- Proposal: Extend `GroupChart` to `(group, keys, windowSize, chart, xAxis, yAxis)` and remove `distinctGroups`,
  `keysByGroup`, and `windowSizeByGroup`; name the local variable `groupChart`; use `toList()`; join the first two
  branches into one condition with one comment ("grow at once; shrink only when the latest value falls below the
  threshold"); refer to the rule of `StatisticsMetric` in the window size comment. The charts stay the same.
- Status: done (2026-11-09).

#### Comments and assertions in StatisticsHistoryChartViewTest

- Origin: initial script check (C7) and C7 (2026-11-09)
- Types: `StatisticsHistoryChartViewTest`
- Problem: Three line comments precede setup code, not an assertion (`junit.md`, "Test Method Style"); their content
  is repeated by the assertion messages. Seven `assertNotNull(..)` calls check results of
  `FXTestSupport.supplyAndWaitNonNull(..)`, which never returns `null`. Two assertion messages contain the non-ASCII
  arrow `U+2192`. Seven tests repeat the cast of an axis to `ValueAxis<Number>`.
- Proposal: Remove the three comments and the seven `assertNotNull(..)` calls, write `->` instead of the arrow, and
  read the axis bounds through two private helpers (e.g., `xAxis(view)` and `yAxis(view)`).
- Status: done (2026-11-09).

### Decisions

- API change: `SimulationObservationViewModel` declares `getStatisticsExtrema()`, which `DefaultObservationViewModel`
  implements with `@Override` (its Javadoc moved to the interface). `AbstractObservationView.updateObservationLabels()`
  is no longer abstract but package-private and `final`, because only `AbstractDefaultMainView` in the same package
  calls it; the private `updateStatusSectionLabel()` and `updateGenericMetricSection()` read the statistics and the
  extrema from the ViewModel. The suppression `OptionalUsedAsFieldOrParameterType` is removed;
  `NumericCastThatLosesPrecision` stays on `updateGenericMetricSection()`.
- The nine observation views lost their `updateObservationLabels()` override, the first call of
  `updateObservationLabels()`, and the second call of `handleSelectedGridCellChanged(..)` in
  `buildObservationRegion()`, and the import of `Optional` where it became unused.
- API change: `createObservationSection(..)` is private. `createSelectedCellSection()` delegates to
  `createExtendedSelectedCellSection(..)`; the private helpers `createTitledSection(..)`, `addExtremumHeader(..)`,
  `createValueLabel()`, `createExtremumLabel(..)`, and `updateExtremumLabel(..)` replace the copied code, and
  `MetricLabels(currentLabel, minimum, maximum)` holds `@Nullable ExtremumLabel(label, tooltip)` components.
- `AbstractObservationView` has no constructor Javadoc, and its two Javadoc comments with a second paragraph have the
  empty line before `<p>`.
- `StatisticsHistoryChartView` builds one `GroupChart(group, keys, windowSize, chart, xAxis, yAxis)` per charted group
  in one loop over the groups, with the series of a group created in the same loop; `seriesByKey` is a `HashMap`, and
  `updateCharts(..)` delegates each group to the private `updateGroupChart(..)`, which asserts the series of a key with
  `requireNonNull(..)` (`Map.get(..)`). The package-private `charts()` replaces `chartsForTesting()`.
- `StatisticsHistoryChartViewTest` reads the axes through the helpers `xAxis(..)` and `yAxis(..)`, without the three
  comments, the seven `assertNotNull(..)` calls, and the arrows.
- No rule change. Test gap: `AbstractObservationView`.

### C8. `core.view`: main views, edit tool bar, and canvas drawing

`SimulationMainView`, `AbstractMainView`, `AbstractDefaultMainView`, `EditToolBarView`, `DrawCallThrottler`,
`CanvasPainters`, `CellDrawer`, and `CoordinateDrawer`.

Notes:

- Inventory (2026-11-09, verified against the sources): the interfaces `SimulationMainView` and the functional
  interfaces `CellDrawer` and `CoordinateDrawer`, the abstract classes `AbstractMainView` (subclasses
  `AbstractDefaultMainView` and `LabMainView`) and `AbstractDefaultMainView` (eight main views), the public record
  `CanvasPainters`, and the package-private final classes `EditToolBarView` and `DrawCallThrottler`. Only
  `AbstractMainView` has a test (six tests through `ConwayFactory`). `DrawCallThrottler` is the only type of the group
  without JavaFX; the others get no new tests in this plan.
- `SimulationMainView`, `CellDrawer`, and `CoordinateDrawer` follow the rules; all members of the group have callers in
  main sources. `SKIP_OVERLAY_SYMBOL` of `AbstractDefaultMainView` keeps its emoji literal (domain need).

#### Reflection in AbstractMainViewTest

- Origin: analysis of the tests for reflection (2026-10-30)
- Types: `AbstractMainView`, `AbstractMainViewTest`
- Problem: The test reads private fields of `AbstractMainView` (or of a class passed as `declaringClass`) through
  reflection (`getDeclaredField`, `setAccessible(true)`, and `Field.get`) in its helpers `getNullableField(..)` and
  `getNonNullField(..)`.
- Proposal: Look at every call of the helpers and decide per field whether the test can check the behavior through the
  public or protected API (e.g., the node tree of the view) instead. Otherwise expose the state as a package-private
  accessor (`java.md`, "Test Access"). Remove the helpers.
- Note (C8, 2026-11-09): The helpers read four fields. `viewModel` and `observationView` are `protected` and thus
  accessible from the test in the same package after a cast to `AbstractMainView<?, ?, ?, ?, ?>`.
  `AbstractMainView.notificationListener` and `AbstractObservationView.selectedGridCellListener` are private; as in
  C5 (`DefaultMainViewModel.hasViewListeners()`), package-private `final` queries `hasNotificationListener()` and
  `hasSelectedGridCellListener()` can replace them.
- Status: done (2026-11-09).

#### Visibility of the members of AbstractMainView

- Origin: C8 (2026-11-09)
- Types: `AbstractMainView`, `AbstractDefaultMainView`; `simulations.md` ("Main View Composition")
- Problem: Several `protected` members have no caller outside the class: `computeCellFontSize(..)`,
  `computeCellEmojiFontSize(..)`, `createSimulationRegion()`, `baseCanvas`, and `dynamicCanvas`.
  `computeVisibleCanvasCenter(..)` and `resolvePreferredFont(..)` are used only by `AbstractDefaultMainView` in the
  same package. `simulations.md` names `createSimulationRegion()` as the way the canvas region is supplied.
- Proposal: Make the first five members private and the two others package-private `final`; adapt the sentence in
  `simulations.md` to "`AbstractMainView` supplies the simulation canvas region." An API change and a rule change.
- Note (C8, 2026-11-09): After the change, `overlayCanvas` was the only `protected` canvas field, used by
  `LabMainView` for its mouse handlers and by `AbstractDefaultMainView` only as the argument of
  `computeVisibleCanvasCenter(..)`. The maintainer chose to make all three canvas fields private, to drop the
  parameter of `computeVisibleCanvasCenter()`, which uses the overlay canvas (all three canvases have the same size),
  and to give `LabMainView` the accessor `overlayCanvas()`.
- Status: done (2026-11-09).

#### Stroke width helpers for all main views

- Origin: C8 (2026-11-09)
- Types: `AbstractDefaultMainView`, `AbstractMainView`, `LabMainView`
- Problem: `NO_STROKE_LINE_WIDTH` and `computeStrokeLineWidth(..)` are declared in `AbstractDefaultMainView`, so
  `LabMainView`, which extends `AbstractMainView`, declares its own private `NO_STROKE_LINE_WIDTH`.
  `computeStrokeLineWidth(..)` uses no instance state.
- Proposal: Move both to `AbstractMainView` next to the font helpers, make `computeStrokeLineWidth(..)` `static`, and
  remove the constant of `LabMainView`. An API change.
- Status: done (2026-11-09).

#### Clearing of the canvas layers

- Origin: C8 (2026-11-09)
- Types: `CanvasPainters`, `AbstractMainView`, `AbstractDefaultMainView`
- Problem: `AbstractMainView.clearPainters()` and `AbstractDefaultMainView.clearCanvasLayers()` repeat the three
  `clearCanvasBackground()` calls of the layers.
- Proposal: Add `CanvasPainters.clearCanvasBackgrounds()` and call it from both methods.
- Status: done (2026-11-09).

#### Details of the main views and DrawCallThrottler

- Origin: C8 (2026-11-09)
- Types: `AbstractMainView`, `AbstractDefaultMainView`, `CanvasPainters`, `DrawCallThrottler`
- Problem: Five Javadoc comments lack the empty line before their `<p>` paragraph (the type Javadoc of
  `AbstractMainView`, `AbstractDefaultMainView`, and `CanvasPainters`, `recreatePaintersAndResizeCanvases(..)`, and
  `DrawCallThrottler.shouldSkip(..)`). `skipOverlayActive = false` and the three `= 0` of `DrawCallThrottler`
  initialize fields to their default value (`java.md`, "Code Style"). The comment `// Font` restates the code, and
  the instance fields of `AbstractDefaultMainView` are separated by blank lines, unlike the other classes.
- Proposal: Add the empty lines, remove the default initializations, the comment, and the blank lines.
- Status: done (2026-11-09).

#### Structure of EditToolBarView

- Origin: C8 (2026-11-09)
- Types: `EditToolBarView`
- Problem: The four buttons repeat the style class, the tooltip, and the bindings of `visible` and `managed` (also
  for the option panel). The listener on the selected toggle checks `selectedToolId == null`, although it is only
  needed when a tool id exists. `clampToUnitRange(..)` only wraps `Math.clamp(..)`. The names of the private
  methods `clearEditAffordanceToolBar()` (removes the nodes) and `clearEditToolBar()` (removes the listener and runs
  the cleanup actions) do not show the difference.
- Proposal: Extend the private `configureEditToolBarButton(..)` by the tooltip key and extract a private
  `bindVisibility(Node, ObservableBooleanValue)`; register the toggle listener inside the existing
  `if (selectedToolId != null)` block; inline `clampToUnitRange(..)`; rename the two methods to `clearNodes()` and
  `releaseResources()`. The UI stays the same.
- Status: done (2026-11-09).

#### Tests of DrawCallThrottler

- Origin: C8 (2026-11-09)
- Types: `DrawCallThrottler`
- Problem: The only non-JavaFX class of the group has no test, although it holds the skip rule of the draw throttling
  (`junit.md`; plan rule "Unit tests").
- Proposal: Add `DrawCallThrottlerTest`: constructor rejections, `shouldSkip(..)` (history not full, average below and
  above the threshold, at most `maxSkips` skips in a row, reset after a draw), and the moving average of
  `recordDurationMillis(..)` and `getAverageDurationMillis()`.
- Status: done (2026-11-09).

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
- Note (C8, 2026-11-09): A private helper in `EditToolBarView` (see "Structure of EditToolBarView") keeps the
  tooltips local to the tool bar; the rule then only needs the addition that the edit and select buttons use the
  fixed keys of `SimulationLocalizationKeys`.
- Status: done (2026-11-09).

### Decisions

- Tests: `AbstractMainViewTest` reads the protected fields `viewModel` and `observationView` directly through the
  helper `createMainView()` and checks the listeners with the new package-private `final` queries
  `AbstractMainView.hasNotificationListener()` and `AbstractObservationView.hasSelectedGridCellListener()`, also
  before the shutdown; the reflection helpers are removed.
- API change: `computeCellFontSize(..)`, `computeCellEmojiFontSize(..)`, `createSimulationRegion()`, and the three
  canvas fields of `AbstractMainView` are private; `LabMainView` reaches the overlay canvas through the new
  `protected final` accessor `overlayCanvas()` and no longer resets the unused `onMouseClicked` handler.
  `computeVisibleCanvasCenter()` has no parameter and is package-private `final` like `resolvePreferredFont(..)`.
  Rule change (`simulations.md`, "Main View Composition"):
  the rule no longer names `createSimulationRegion()`.
- API change: `NO_STROKE_LINE_WIDTH` and the now `static` `computeStrokeLineWidth(..)` moved from
  `AbstractDefaultMainView` to `AbstractMainView`; `LabMainView` uses the inherited constant instead of its own.
- `CanvasPainters.clearCanvasBackgrounds()` clears the three layers for `clearPainters()` and `clearCanvasLayers()`.
- `EditToolBarView` sets the style class and the tooltip of all four buttons in `configureEditToolBarButton(..)` and
  binds `visible` and `managed` with `bindVisibility(..)`; the toggle listener is registered only with a tool id,
  `clampToUnitRange(..)` is inlined, and the private clear methods are named `clearNodes()` and
  `releaseResources()`. Rule change (`simulations.md`, "UI Construction"): edit toolbar buttons get their own
  `Tooltip` in `EditToolBarView`, action buttons with the descriptor key, the edit and select buttons with the fixed
  keys of `SimulationLocalizationKeys`.
- Five Javadoc comments have the empty line before `<p>`; `AbstractDefaultMainView` and `DrawCallThrottler` lost
  their default field initializations, `AbstractMainView` the comment `// Font`, and `AbstractDefaultMainView` the
  blank lines between its instance fields.
- The new `DrawCallThrottlerTest` covers the constructor rejections, the skip rule of `shouldSkip(..)`, and the moving
  average. No remaining test gaps for non-JavaFX types; the JavaFX types of the group get no new tests in this plan.

### C9. `core` root package and `start`

`SimulationFactory`, `SimulationInstance`, `SimulationTerminationHandle`, `SimulationType`, `StartFactory`, and
`StartMainView`.

Notes:

- Inventory (2026-11-09, verified against the sources): the utility classes `SimulationFactory` and `StartFactory`,
  the record `SimulationInstance` with the package-private factory `of(..)`, the interface
  `SimulationTerminationHandle` with the nested enum `CompletedSimulationTerminationHandle`, the enum
  `SimulationType`, and the final class `StartMainView`. Tests exist for `SimulationFactory`, `SimulationInstance`,
  `SimulationTerminationHandle`, and `SimulationType`; `StartMainView` is a JavaFX type and gets no new test in this
  plan, and `StartFactory` is covered through `SimulationFactoryTest`.
- `SimulationFactory`, `StartFactory`, and `SimulationTerminationHandle` follow the rules.

#### Constant implemented flag of SimulationType

- Origin: C9 (2026-11-09)
- Types: `SimulationType`, `StartMainView`, `EtpetssimApplication`, `SimulationTypeTest`
- Problem: All ten constants pass `implemented = true`; the constructor suppresses `SameParameterValue` for it.
  `isImplemented()` is read only by `StartMainView`, whose branch for disabled buttons is dead, and by
  `fromCliArgument(arg, onlyImplemented)`, whose only caller `EtpetssimApplication` passes `true`, which filters
  nothing. `testIsImplemented` only repeats the constant.
- Proposal: Remove the component `implemented`, `isImplemented()`, the parameter `onlyImplemented`, the disabled
  branch of `StartMainView`, the suppression, and `testIsImplemented`; the type Javadoc of `SimulationType` and
  `StartMainView` loses "availability" and "unimplemented ones are disabled". An API change.
- Status: done (2026-11-09).

#### SimulationControlView and region of StartMainView

- Origin: C9 (2026-11-09)
- Types: `StartMainView`, `SimulationMainView`
- Problem: `StartMainView` implements `SimulationControlView` only to build its region in `buildControlRegion()`;
  nothing uses it as a control view, so it needs the no-op `setStepCount(..)` with the comment `// Do nothing`.
  `buildMainRegion()` builds a new region on every call, although `SimulationMainView` requires that repeated calls
  return the same root region. The type Javadoc lacks the empty line before its `<p>` paragraph.
- Proposal: Drop `SimulationControlView` and `setStepCount(..)`; `buildMainRegion()` builds the region once in a
  private `createStartRegion()` and caches it like `AbstractMainView`; adapt the type Javadoc and the C9 intro of
  this plan. An API change.
- Status: done (2026-11-09).

#### Tooltips of the start screen buttons

- Origin: C9 (2026-11-09)
- Types: `StartMainView`; `simulations.md` ("UI Construction")
- Problem: The buttons of the start screen create a `Tooltip` with the subtitle of their `SimulationType` directly;
  the rule allows this only for the edit toolbar buttons (C8) and computed texts without a fixed key.
- Proposal: Generalize the C8 sentence of the rule: buttons without a label control get their own `Tooltip`, e.g.,
  the edit toolbar buttons (with the keys named there) and the start screen buttons (with the subtitle of their
  `SimulationType`). A rule change.
- Status: done (2026-11-09).

#### Javadoc of SimulationInstance

- Origin: C9 (2026-11-09)
- Types: `SimulationInstance`
- Problem: The Javadoc of `of(..)` says "This method is package-private", which the modifier shows.
- Proposal: Keep only "Callers must ensure that the provided type and view are compatible; used by
  `SimulationFactory`."
- Status: done (2026-11-09).

#### Tests of SimulationInstance and SimulationType

- Origin: C9 (2026-11-09)
- Types: `SimulationInstanceTest`, `SimulationTypeTest`
- Problem: `SimulationInstanceTest.testOf` passes the view and the region out of the FX thread through three
  `AtomicReference`s and a helper, although only the stub must be created there. In `SimulationTypeTest`, the main
  test of `cliArguments()` is named `testCliArgumentsReturnsExpectedValues`, and the tests after the resource keys do
  not follow the declaration order (`fromCliArgument(..)` comes after `cliArguments()`; `junit.md`,
  "Organization").
- Proposal: Create the stub with `FXTestSupport.supplyAndWaitNonNull(..)` and remove the references and the helper;
  rename the test to `testCliArguments` and move `testFromCliArgument` directly after the resource key tests.
- Status: done (2026-11-09).

### Decisions

- API change: `SimulationType` has no component `implemented` and no `isImplemented()` any more, and
  `fromCliArgument(String)` lost its parameter `onlyImplemented`; `EtpetssimApplication` passes the method reference,
  `StartMainView` lost its branch for disabled buttons, and the constructor its suppression `SameParameterValue`.
  `testIsImplemented` is removed, and `SimulationFactoryTest` selects the first type shown on the start screen.
- API change: `StartMainView` implements only `SimulationMainView`; `buildControlRegion()` and `setStepCount(..)` are
  removed. `buildMainRegion()` builds the region once in the private `createStartRegion()` and returns the cached
  region afterwards, as the contract of `SimulationMainView` requires. A language change still rebuilds the start
  screen, because the stage updater creates a new instance.
- Rule change (`simulations.md`, "UI Construction"): buttons without a label control get their own `Tooltip`, with
  the edit toolbar buttons and the start screen buttons as examples.
- The Javadoc of `SimulationInstance.of(..)` no longer states its visibility and links `SimulationFactory`.
- `SimulationInstanceTest.testOf` creates only the stub on the FX thread; `SimulationTypeTest` names the main test
  `testCliArguments` and places `testFromCliArgument` after the resource key tests.
- No test gaps; `StartMainView` is covered through `SimulationFactoryTest`.

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
  choice (Snake, Sugar, and ET Pets initialize it at the field declaration since D3 to D5). Only
  `LangtonEditToolBarViewModel` has a test.
- Main views take `(viewModel, [editToolBarViewModel], entityDescriptorRegistry, configView, controlView,
  observationView)`; Forest and Wa-Tor have no edit tool bar. `LabMainView` extends `AbstractMainView` directly and
  has 23 private methods; the Lab has its own control view and ViewModel and its own main ViewModel.

### D1. Wa-Tor (reference)

Notes:

- Inventory (2026-11-09, verified against the sources): 21 types in the package root, `model`, `model.entity`,
  `shared`, `view`, and `viewmodel`. Tests exist for `WatorConfig`, `WatorUserAction`, `Fish`, `Shark`,
  `TerrainConstant`, and `EntityDescriptors`.
- `WatorConfig`, `WatorEntity`, `TerrainConstant`, `EntityDescriptors`, and `WatorFactory` follow the rules.
- Test gaps for the later separate optimization: `WatorStepLogic`, `WatorTerminationCondition`, `WatorStatistics`,
  `CreatureBase` (age, reproduction, and equality), `CreatureFactory`, and the initial cell counts of
  `WatorSimulationManager`.

#### Missing type Javadoc

- Origin: D1 (2026-11-09)
- Types: 12 public types of Wa-Tor; 94 public top-level types in all simulations (none in `core` and `start`)
- Problem: `java.md` ("Javadoc", "Coverage") requires Javadoc for public types; the managers, step logic, termination
  conditions, user actions, creatures, views, and ViewModels of the simulations mostly have none.
- Proposal: Add a short type Javadoc per family that says what the type does in its simulation; the later groups
  adopt the forms of Wa-Tor.
- Status: done (2026-11-09).

#### Redundant counter initializations in the statistics constructors

- Origin: B6 (2026-11-08)
- Types: `WatorStatistics` and the other statistics classes that extend `BaseTimedSimulationStatistics`;
  `CreatureFactory`
- Problem: The constructors set their counters explicitly to `0` (e.g., `fishCells = 0;`), which the review
  guidelines ("Constructors") list as redundant field initializations; the constructor of `CreatureFactory` only
  sets `sequence = 0`.
- Proposal: Remove the assignments in `WatorStatistics` and the constructor of `CreatureFactory`, and adopt the
  decision in the later groups.
- Status: done (2026-11-09).

#### Visibility of the cell count methods and creature factories

- Origin: D1 (2026-11-09)
- Types: `WatorStatistics`, `WatorSimulationManager`, `WatorUserAction`
- Problem: `WatorStatistics.adjustCellCounts(..)` is public, but only `WatorUserAction` in the same package calls it,
  always with one delta of `1` or `-1`. `WatorSimulationManager.createFish(..)` and `createShark(..)` are public, but
  only the manager and `WatorUserAction` call them.
- Proposal: Use the package-private increment and decrement methods in `WatorUserAction` and remove
  `adjustCellCounts(..)`; make `createFish(..)` and `createShark(..)` package-private. An API change.
- Status: done (2026-11-09); `adjustCellCounts(..)` stays public and in use, because it was introduced on purpose
  and the other simulations use it in the same way (maintainer).

#### Reproduction conditions and traces of WatorStepLogic

- Origin: D1 (2026-11-09)
- Types: `WatorStepLogic`
- Problem: The reproduction interval check is copied for fish and sharks, badly wrapped, and nested three levels deep
  for sharks. A commented-out trace is commented out twice and names variables that do not exist.
- Proposal: Extract a private static `hasReproductionIntervalPassed(..)` and join the conditions with `&&`; the random
  sequence of a seed stays the same. Correct the trace.
- Status: done (2026-11-09); the maintainer chose to remove all commented-out traces instead.

#### Natural order and reproduction history of CreatureBase

- Origin: D1 (2026-11-09)
- Types: `CreatureBase`, `Shark`
- Problem: `CreatureBase` implements `Comparable`, but nothing calls `compareTo(..)` (the agents are ordered by
  position). `hashCode()` boxes the sequence ID with `Objects.hashCode(..)`. The list `reproductionStepIndices` keeps
  every reproduction step, although only the count and the last step are read. The constructor parameter
  `initialEnergy` of `Shark` is named `birthEnergy` in `CreatureFactory` and `WatorConfig`.
- Proposal: Remove `Comparable` and `compareTo(..)` (an API change), use `Long.hashCode(..)`, replace the list by the
  fields `reproductionCount` and `stepIndexOfLastReproduction`, and rename the parameter to `birthEnergy`.
- Status: done (2026-11-09).

#### Color variants of WatorMainView

- Origin: D1 (2026-11-09)
- Types: `WatorMainView`
- Problem: The map `entityColors` is filled with `null` placeholders in the constructor; `FALLBACK_COLOR_AGENT` and
  the branch for `TerrainConstant` are not reached, because only fish and shark cells are drawn; the descriptor IDs
  are read from `EntityDescriptors` and from `WatorEntity`.
- Proposal: Hold the variants in the fields `fishColors` and `sharkColors`, resolve the color with a `switch` over the
  entity, use the `DESCRIPTOR_ID_*` constants, and remove the fallback constant and the blank line between the
  instance fields (C8). `SugarMainView` has the same pattern (D4).
- Status: done (2026-11-09).

#### Details of the Wa-Tor types

- Origin: D1 (2026-11-09)
- Types: `WatorConfigViewModel`, `WatorConfigView`, `WatorStatistics`, `WatorUserActionContext`, `WatorUserAction`,
  `WatorBalance`, `WatorConstraints`, `WatorSimulationManager`, `WatorUserActionTest`
- Problem: `WatorConfigViewModel` has the section comments `// Rules - NeighborhoodMode` and `// Rules`; three class
  declarations put their supertype on a second line, although it fits on one (C2). The constant summaries of
  `WatorUserActionContext` are imperative (C1). The comment on the missing selection in `WatorUserAction` restates the
  code. The Javadoc of `TERMINATION_FISH_MAX_SHARE` says the same in two sentences. `CELL_SHAPE_VALUES` wraps
  `List.of(CellShape.values())`. The trailing comment `// negative age for birth time` is unclear. The test helper
  `selectedCell(..)` lacks the `create` prefix (`junit.md`, "Test Data and Helpers").
- Proposal: One `// Rules` comment, declarations on one line, "Adds ..." summaries, remove the comment, one sentence,
  one line, a comment above the line that explains the random age, and `createSelectedCell(..)`.
- Status: done (2026-11-09).

### Decisions

- All public types of Wa-Tor have a short type Javadoc of one sentence that names the role of the type in its
  simulation, without describing its content in detail (maintainer), e.g., "Simulation manager of Wa-Tor.", "Agent
  step logic of Wa-Tor for fish and sharks.", "User action of Wa-Tor that adds or removes fish and sharks.", and
  "Main view of Wa-Tor."; the later groups write theirs in the same forms.
- `WatorStatistics` lost the counter initializations and declares its supertype on one line; `CreatureFactory` has no
  explicit constructor.
- `WatorStatistics.adjustCellCounts(..)` stays public and is used by `WatorUserAction` (maintainer). API change:
  `WatorSimulationManager.createFish(..)` and `createShark(..)` are package-private.
- `WatorStepLogic` checks the reproduction interval with the private static `hasReproductionIntervalPassed(..)` and
  joins the reproduction conditions with `&&`; its commented-out `AppLogger` traces are removed, because Wa-Tor no
  longer needs them (maintainer).
- API change: `CreatureBase` no longer implements `Comparable`; it keeps `reproductionCount` and
  `stepIndexOfLastReproduction` in two `int` fields, uses `Long.hashCode(..)`, and documents
  `stepIndexOfLastReproduction()` and `reproduce(..)`. `Shark` takes `birthEnergy` and adds the delta with `+=`.
- `WatorMainView` holds the `@Nullable` color variants in `fishColors` and `sharkColors` and resolves them with the
  private static `resolveVariantColor(..)`; `FALLBACK_COLOR_AGENT`, the map `entityColors`, and the use of
  `EntityDescriptors` are removed.
- The details are implemented as proposed. No rule change.

### D2. Rebounding

Notes:

- Inventory (2026-11-09, verified against the sources): 18 types in the package root, `model`, `model.entity`,
  `shared`, `view`, and `viewmodel`. Tests exist for `ReboundingConfig`, `ReboundingUserAction`, `Rebounder`,
  `TerrainConstant`, and `EntityDescriptors`.
- `ReboundingConstraints`, `ReboundingEntity`, `TerrainConstant`, `EntityDescriptors`, `ReboundingUserActionContext`,
  and `ReboundingFactory` follow the rules.
- Test gaps for the later separate optimization: `ReboundingStepLogic` (bounce directions),
  `ReboundingTerminationCondition`, `ReboundingStatistics`, and the wall positions of `ReboundingSimulationManager`
  (`computeWallXPositions(..)`).

#### Two terms for the rebounders

- Origin: D2 (2026-11-09)
- Types: `ReboundingConfig`, `ReboundingConstraints`, `ReboundingStatistics`, `ReboundingSimulationManager`,
  `ReboundingStepLogic`, `ReboundingConfigViewModel`, `ReboundingConfigView`, the bundles, and their tests
- Problem: The entity type is `Rebounder`, but the config, statistics, and step logic call it "moving entity"
  (`movingEntityPercent`, `movingEntityCells`, `KEY_MOVING_ENTITY_CELLS`, `MOVING_ENTITY_PERCENT_*`, and the keys
  `rebounding.config.movingentitypercent*` and `rebounding.observation.cells.movingentity`), against the rule of one
  term per concept (`simulations.md`, "Entity Design").
- Proposal: Use "rebounder" in code and keys; the displayed texts stay unchanged. An API change.
- Status: done (2026-11-09).

#### Details of the Rebounding types

- Origin: D2 (2026-11-09)
- Types: all public types without type Javadoc, `ReboundingStatistics`, `ReboundingTerminationCondition`,
  `ReboundingUserAction`, `ReboundingStepLogic`, `ReboundingSimulationManager`, `ReboundingMainView`,
  `ReboundingObservationView`, `ReboundingConfigViewModel`, `ReboundingConfigView`, `ReboundingUserActionTest`
- Problem: Ten public types have no type Javadoc (D1). `ReboundingStatistics` initializes its counters to `0`, uses
  `+= 1`, and has public increment and decrement methods with callers only in its package.
  `ReboundingTerminationCondition` documents its override instead of the type. `ReboundingUserAction` repeats the
  check of the selected cell four times and checks an empty list before a loop. `ReboundingStepLogic` repeats the
  bounce and compares with `TerrainConstant` constants. The private methods of `ReboundingSimulationManager` take the
  config as parameter instead of calling `config()`. `ReboundingMainView` removes the same listeners in both branches,
  and its combo box listener compares old and new value. `ReboundingObservationView` nests two `if` statements. The
  config ViewModel and view have the deviations of D1. The test helper `selectedCell(..)` lacks the `create` prefix.
- Proposal: Adopt the decisions of D1, make the increment and decrement methods package-private (an API change),
  handle `FILL_WALLS` before one check of the selected cell, extract `bounce(..)`, use `isGround()` and `isWall()`,
  use `config()`, register the listener cleanup once, and join the conditions.
- Status: done (2026-11-09).

### Decisions

- API change: "rebounder" replaces "moving entity" in code and keys: `ReboundingConfig.rebounderPercent`,
  `REBOUNDER_PERCENT_*`, `ReboundingStatistics.rebounderCells()`, `KEY_REBOUNDER_CELLS` (`"rebounderCells"`),
  `rebounderPercentProperty()`, and the keys `rebounding.config.rebounderpercent*` and
  `rebounding.observation.cells.rebounder`; the displayed texts and `docs/simulations` keep "Moving Entity".
- API change: the increment and decrement methods of `ReboundingStatistics` are package-private and use `++` and `--`;
  the counters lost their explicit initialization, and the class has a type Javadoc and a one-line declaration.
- All public types have a short type Javadoc; `ReboundingTerminationCondition` states its condition there instead of
  in the Javadoc of `isFinished(..)`.
- `ReboundingUserAction` fills the walls before it checks the selected cell once; the inner `switch` statement has no
  `FILL_WALLS` branch, which IntelliJ reported as unreachable. `ReboundingStepLogic` uses the private `bounce(..)`, `isGround()`, and `isWall()`.
  `ReboundingSimulationManager` reads the config through `config()`.
- `ReboundingMainView` registers the listener cleanup once and the binding cleanup only with a tool id; the combo box
  listener forwards every change. `ReboundingObservationView` joins its conditions.
- The config ViewModel and view, and the test helper `createSelectedCell(..)`, follow D1. No rule change.

### D3. Snake

Notes:

- Inventory (2026-11-10, verified against the sources): 25 types in the package root, `model`, `model.entity`,
  `model.strategy`, `shared`, `view`, and `viewmodel`. Tests exist for `SnakeConfig`, `SnakeUserAction`, `SnakeHead`
  (only `toDisplayString()`), `TerrainConstant`, `EntityDescriptors`, `SnakeDeathMode`, `AnonymousMoveStrategy`,
  `NamedMoveStrategy`, and `SnakeMoveStrategies`.
- `SnakeConfig`, `SnakeEntity`, `TerrainConstant`, `EntityDescriptors`, `MoveDecision`, `SnakeMoveStrategy`,
  `AnonymousMoveStrategy`, `NamedMoveStrategy`, `SnakeDeathMode`, `SnakeUserActionContext`, and `SnakeFactory` follow
  the rules.
- Observation for #24: with `RESPAWN`, the simulation ends when all living snakes die in the same step, because
  `livingSnakeHeadCells()` is `0` before the dead snakes respawn in the next step.
- Test gaps for the later separate optimization: `SnakeStepLogic`, `SnakeTerminationCondition`, `SnakeStatistics`,
  the initialization of `SnakeSimulationManager` (wall positions, snakes, food), the state changes of `SnakeHead`
  (`move(..)`, `die()`, `respawn(..)`, and the age), and `SnakeEditToolBarViewModel`.

#### Empty check of the move strategies

- Origin: B1 (2026-11-08)
- Types: `SnakeEditToolBarViewModel`
- Problem: The constructor throws if `SnakeMoveStrategies.strategies()` is empty, which cannot occur for the constant
  catalog.
- Proposal: Remove the check as dead code.
- Status: done (2026-11-10); see "Unused members of SnakeEditToolBarViewModel and SnakeHead".

#### Unused members of SnakeEditToolBarViewModel and SnakeHead

- Origin: D3 (2026-11-10)
- Types: `SnakeEditToolBarViewModel`, `SnakeHead`
- Problem: `SnakeEditToolBarViewModel.availableStrategies()` and `setSelectedStrategy(..)` and
  `SnakeHead.pendingGrowth()` and `stepIndexOfSpawn()` have no caller. `move(..)` resets `pendingGrowth` to `0` with
  the comment `// Ensure non-negative`, which only matters because `respawn(..)` does not reject a negative
  `initialPendingGrowth` like the constructor.
- Proposal: Remove the four methods (an API change). Initialize the selected strategy of
  `SnakeEditToolBarViewModel` at its field declaration without an explicit constructor. Let `respawn(..)` reject
  negative values like the constructor and remove the reset in `move(..)`.
- Status: done (2026-11-10).

#### Snake colors of SnakeMainView

- Origin: D3 (2026-11-10)
- Types: `SnakeMainView`
- Problem: `drawSimulation(..)` computes twelve local colors (alive, selected, and dead for the head and the segments,
  each with fill and border) on every draw and chooses them in a lambda with several statements (`java.md`, "Code
  Style"); the descriptor colors do not depend on the config.
- Proposal: A private record `SnakeColors(head, headBorder, segment, segmentBorder)`, the final fields
  `aliveColors`, `selectedColors`, and `deadColors` computed once in the constructor, and the private methods
  `drawSnake(..)` and `resolveSnakeColors(..)`.
- Status: done (2026-11-10).

#### Redundant termination check

- Origin: D3 (2026-11-10)
- Types: `SnakeTerminationCondition`
- Problem: `snakeHeadCells() <= 0` is implied by `livingSnakeHeadCells() <= 0`, because every update keeps the living
  snake heads at most as many as all snake heads.
- Proposal: Remove the check; the overflow guard of `cumulativeSnakeDeathCount()` stays.
- Status: done (2026-11-10).

#### Details of the Snake types

- Origin: D3 (2026-11-10)
- Types: all public types without type Javadoc, `SnakeStatistics`, `SnakeSimulationManager`, `SnakeStepLogic`,
  `SnakeUserAction`, `SnakeHead`, `MoveContext`, `SnakeMoveStrategies`, `SnakeConstraints`, `SnakeConfigViewModel`,
  `SnakeConfigView`, `SnakeMainView`, `SnakeObservationView`, `SnakeUserActionTest`
- Problem: Eleven public types have no type Javadoc or, like `SnakeStatistics`, a generic one (D1).
  `SnakeStatistics` initializes its counters to `0` and has public increment and decrement methods with callers only
  in its package. The private methods of `SnakeSimulationManager` take the config as parameter and are named
  `wallInitializer(..)` instead of `create...`. `SnakeStepLogic` names a variable `newCoordinate` that does not exist
  and nests the neighbor check in an `if` with the exception in the `else` branch. `SnakeUserAction` finds the head of
  a segment as an `Optional` coordinate and checks the entity twice. `SnakeHead` initializes fields to their default
  values and uses `formatted(..)` without a `Locale` in `toString()`. `@param random` of `MoveContext` repeats its
  record Javadoc; `SnakeMoveStrategies` has the abbreviation `nc` and a restating comment. `DEATH_MODE_VALUES` wraps
  `List.of(SnakeDeathMode.values())`. `createConfig()` of `SnakeConfigViewModel` has section comments with the seed
  under `// Structure`. `SnakeMainView` registers the binding cleanup also without a tool id and has a restating
  comment and a misplaced constant. `SnakeObservationView` nests two `if` statements. The test helper
  `selectedCell(..)` lacks the `create` prefix.
- Proposal: Adopt the decisions of D1 and D2, make the increment and decrement methods package-private (an API
  change), and fix the other details.
- Status: done (2026-11-10).

### Decisions

- All public types have a short type Javadoc in the forms of D1; `SnakeTerminationCondition` states its condition
  there. The config ViewModel, the config view, and `SnakeStatistics` declare their supertype on one line.
- API change: the increment and decrement methods of `SnakeStatistics` are package-private and follow the getters;
  `adjustWallCells(..)` and `adjustFoodCells(..)` stay public (D1). The counters lost their explicit initialization,
  and the getters follow the order of the fields.
- API change: `SnakeEditToolBarViewModel.availableStrategies()` and `setSelectedStrategy(..)` are removed; the class
  initializes its selection at the field declaration, without an explicit constructor and without the empty check.
- API change: `SnakeHead.pendingGrowth()` and `stepIndexOfSpawn()` are removed; the constructor and `respawn(..)`
  check `initialPendingGrowth` with the private static `checkInitialPendingGrowth(..)`, and `move(..)` no longer
  resets `pendingGrowth`. `SnakeHead` lost its default field initializations, and `toString()` uses
  `String.format(Locale.ROOT, ..)`. The age methods stay, like those of `CreatureBase` in Wa-Tor.
- `SnakeSimulationManager` reads the config through `config()` in `createWallInitializer(..)`,
  `createSnakeInitializer(..)`, and `createFoodInitializer(..)` and counts the snake heads with
  `SnakeEntity::isAgent`.
- `SnakeStepLogic` rejects multiple edge behaviors first and names the target coordinate in its comment;
  `SnakeUserAction` removes a snake by a segment with the private `removeSnakeBySegment(..)`;
  `SnakeTerminationCondition` checks only the living snake heads and the overflow guard.
- `SnakeMainView` holds `aliveColors`, `selectedColors`, and `deadColors` as final fields of the private record
  `SnakeColors`, draws each snake with `drawSnake(..)`, and registers the binding cleanup only with a tool id.
  `SnakeObservationView` joins its conditions.
- `MoveContext`, `SnakeMoveStrategies`, `SnakeConstraints`, `SnakeConfigViewModel`, and the test helper
  `createSelectedCell(..)` follow the details as proposed. No rule change.

### D4. Sugarscape

Notes:

- Inventory (2026-11-10, verified against the sources): 28 types in the package root, `model`, `model.entity`,
  `shared`, `view`, and `viewmodel`. Tests exist for `SugarConfig`, `SugarCell`, `SugarGridModel`, `SugarUserAction`,
  `SugarAgentLogic`, `SugarResourceLogic`, `Agent`, `Sugar`, `SugarEntity`, `EntityDescriptors`, and
  `SugarAddSugarLevel`, with the package-local `SugarLogicTestSupport`.
- `SugarConfig`, `SugarConstraints`, `SugarBalance`, `SugarGridModel`, `SugarAgentLogic`, `SugarResourceLogic`,
  `NoAgent`, `NoResource`, `EntityDescriptors`, and `SugarFactory` follow the rules apart from the details below.
- Observation for D5 or #24: the observation view shows `-` as the type of an empty cell, because `NoResource` and
  `NoAgent` have the descriptor IDs `no_resource` and `no_agent` without an entity descriptor; the background uses the
  descriptor `terrain`, which no entity has. ET Pets uses the same IDs.
- Test gaps for the later separate optimization: the initialization of `SugarSimulationManager` (peaks, radius map,
  and agents), `SugarStatistics`, `SugarTerminationCondition`, `SugarStepRunner`, and `SugarEditToolBarViewModel`.

#### Edit tool bar ViewModel of Sugarscape

- Origin: D4 (2026-11-10)
- Types: `SugarEditToolBarViewModel`, `SugarMainView`
- Problem: The ViewModel copies the dynamic pattern of `ReboundingEditToolBarViewModel` (an `ObservableList`, a
  `@Nullable` selection, a setter that checks the membership, and `resolveSelectedAddSugarContext()`), although the
  levels are the constants of an enum; `SugarMainView` synchronizes its combo box with two listeners and its own list
  cells.
- Proposal: Use an `InputChoiceProperty` initialized at the field declaration and
  `FXComponentFactory.createLabeledChoiceComboBox(..)` like Snake. An API change.
- Status: done (2026-11-10).

#### Unused entity API of Sugarscape

- Origin: D4 (2026-11-10)
- Types: `SugarEntity`, `AgentEntity`, `ResourceEntity`, `Agent`, `Sugar`, `NoAgent`, `NoResource`,
  `AgentEntityTest`, `ResourceEntityTest`
- Problem: `isResource()` and `isAgent()` with their defaults and overrides have no caller in main sources, only the
  two tests; the sealed layer types already state the layer. `Sugar.maxAmount()` has no caller.
- Proposal: Remove the methods and the two tests. An API change.
- Status: done (2026-11-10).

#### Color variants of SugarMainView

- Origin: D1 (2026-11-09)
- Types: `SugarMainView`
- Problem: The map `entityColors` is filled with `null` placeholders, the fallback colors are not reached, and the
  fill color methods switch over the entity with the dummy value `-1` for the empty entities, which are not drawn.
- Proposal: Adopt D1: the `@Nullable` fields `sugarColors` and `agentColors`, the private static
  `resolveVariantColor(..)`, and the base color for `NoResource` and `NoAgent`; remove the fallback constants.
- Status: done (2026-11-10).

#### Details of the Sugarscape types

- Origin: D4 (2026-11-10)
- Types: all public types without type Javadoc, `SugarStatistics`, `SugarSimulationManager`, `SugarStepRunner`,
  `SugarTerminationCondition`, `Agent`, `SugarObservationView`, and the types whose Javadoc says "Sugar simulation"
- Problem: Eleven public types have no type Javadoc or a generic one (D1); several Javadoc comments call the
  simulation "Sugar" instead of "Sugarscape". `SugarStatistics` initializes its counters to `0`.
  `SugarSimulationManager` mixes the parameter `config` with `config()`, limits the maximum sugar amount with
  `Math.max(..)` although `isValid()` ensures it, and has instance helpers without instance state and the parameter
  `percent` for a fraction. `SugarTerminationCondition` lacks the empty line before `<p>`. `Agent` names its
  constructor parameter `currentEnergy` and adds without `+=`. `SugarObservationView` names a `Sugar` `resource`.
  Six class declarations wrap although they fit on fewer lines.
- Proposal: Adopt the decisions of D1 to D3 and fix the details.
- Status: done (2026-11-10).

### Decisions

- All public types have a short type Javadoc in the forms of D1; `SugarTerminationCondition` states its condition
  there and keeps its paragraph about the safety net. The Javadoc calls the simulation "Sugarscape".
- API change: `SugarEditToolBarViewModel` holds an `InputChoiceProperty<SugarAddSugarLevel>` with the first level and
  localized labels, initialized at the field declaration; `availableAddSugarLevels()`, `setSelectedAddSugarLevel(..)`,
  and `resolveSelectedAddSugarContext()` are removed, and `getSelectedAddSugarLevel()` is non-null. `SugarMainView`
  builds the combo box with `FXComponentFactory.createLabeledChoiceComboBox(..)` and binds its disable state like
  Snake.
- API change: `isResource()`, `isAgent()`, and `Sugar.maxAmount()` are removed; `AgentEntity` and `ResourceEntity`
  have no members, and `AgentEntityTest` and `ResourceEntityTest` are deleted.
- `SugarMainView` holds `sugarColors` and `agentColors` and resolves the fill colors with `resolveVariantColor(..)`
  like Wa-Tor; `entityColors` and the fallback colors are removed.
- `SugarStatistics` lost its counter initializations; `adjustResourceCells(..)` stays public and
  `adjustAgentCells(..)` package-private. `SugarSimulationManager` reads the config through `config()`, uses
  `config().maxSugarAmount()` directly, and has the private static `computeClampedIndex(..)` (parameter `fraction`)
  and `computeSugarAmount(..)` after its constructors. `Agent` takes `initialEnergy` and adds with `+=`.
- The details are implemented as proposed. No rule change.

### D5. ET Pets

Notes:

- Inventory (2026-11-10, verified against the sources): 42 types in the package root, `model`, `model.entity`,
  `shared`, `view`, and `viewmodel`. Tests exist for most model types, the entities, the choices, and the score math
  and balance; `EtpetsScoreTuningRunner` is a manual utility.
- `EtpetsConfig`, `EtpetsBalance`, `EtpetsGridModel`, `EtpetsCell`, `EtpetsDeterminism`, `EtpetsResourceLogic`,
  `EtpetsScoreMath`, `TerrainConstant`, `NoAgent`, `NoResource`, `PetActionType`, `EntityDescriptors`, the shared types,
  and `EtpetsFactory` follow the rules apart from the details below.
- `Pet.stepIndexOfBirth()`, `Pet.reproductionCooldownRemaining()`, `Pet.lastAction()`, and
  `PetEgg.stepIndexOfLaying()` have no caller; they stay for the planned labels of the selected cell (TODO in
  `EtpetsObservationView`, maintainer). The commented-out debug blocks of `EtpetsSimulationManager` with their
  `@SuppressWarnings("CommentedOutCode")` and the override of `EtpetsObservationView` with its TODO stay, because the
  further development of ET Pets needs them (maintainer).
- Observations for #24: `PetTraits.genomeQualityScore()` rates a higher `movementCostModifier` as a better quality,
  although higher movement costs are worse for a pet; the two TODOs of `findEggPlacementCoordinate(..)` stay; empty
  cells show `-` as type (D4).
- Test gaps for the later separate optimization: the initialization of `EtpetsSimulationManager`, `EtpetsStatistics`,
  `EtpetsTerminationCondition`, `EtpetsStepRunner`, `EtpetsIdSequence`, the life cycle methods of `Pet`, and
  `EtpetsEditToolBarViewModel`.

#### Shuffle and coordinates of EtpetsSimulationManager

- Origin: `code-other-classes.md`, deferred finding "Shuffle with cancellation checks" (2026-10-30)
- Types: `EtpetsSimulationManager`
- Problem: The class still has the private `shuffleCoordinates(..)` (a Fisher-Yates shuffle with cancellation checks)
  and `createCoordinates(..)`, which `GridInitializers` dropped in `code-other-classes.md`.
- Proposal: Use `Collections.shuffle(list, random)` and `structure.coordinatesList()` as decided there; the random
  sequence of a seed stays the same (same swaps with the same `nextInt(..)` calls), and a manual test with the maximum
  grid of ET Pets was fast (2026-10-30). Check that the remaining checkpoints still follow `simulations.md` ("Model
  Initialization").
- Status: done (2026-11-10); the loops over all cells and over the rock, water, plant, and insect counts keep their
  checkpoints, and the cancellation test of `SimulationManagerTest` still cancels during the terrain population.

#### Edit tool bar ViewModel of ET Pets

- Origin: D3 and D4 (cross-group decision on edit tool bar ViewModels)
- Types: `EtpetsEditToolBarViewModel`, `EtpetsMainView`
- Problem: The ViewModel has the dynamic pattern of Rebounding for two fixed enums (terrain and resource choices), and
  `EtpetsMainView` synchronizes two combo boxes with two copied listener blocks.
- Proposal: Two `InputChoiceProperty` fields and one private generic `createOptionBox(..)` in the view. An API change.
- Status: done (2026-11-10).

#### Unused entity API and shared limits of ET Pets

- Origin: D4 (2026-11-10) and D5 (2026-11-10)
- Types: `EtpetsEntity`, `TerrainEntity`, `ResourceEntity`, `AgentEntity`, the entities, `ResourceBase`,
  `EtpetsConstraints`, their callers and tests
- Problem: `isTerrain()`, `isResource()`, and `isAgent()` have no caller in main sources, as in Sugarscape.
  `ResourceBase.minAmount()` is public, but only the class calls it. `EtpetsConstraints` shares `PERCENT_MIN` and
  `PERCENT_MAX` for four components, unlike `simulations.md` and the other simulations.
- Proposal: Remove the three queries and their tests, make `minAmount()` protected, and name the limits per component.
  API changes.
- Status: done (2026-11-10).

#### Color variants of EtpetsMainView

- Origin: D1 (2026-11-09) and D5 (2026-11-10)
- Types: `EtpetsMainView`
- Problem: The map `entityColors` holds five `null` placeholders; the ranges are looked up by descriptor ID in two
  switches with a default exception, and two overloads of `clampToMapRange(..)` suppress
  `OverloadedMethodsWithSameNumberOfParameters`. Rock and water are filtered by comparing descriptor ID strings.
- Proposal: Five `@Nullable` color fields, the `EtpetsBalance` ranges passed directly, one static
  `clampToMapRange(double, int, int)` without suppression, `resolveVariantColor(..)` as in Wa-Tor, and comparisons with
  `TerrainConstant`.
- Status: done (2026-11-10).

#### Details of the ET Pets types

- Origin: D5 (2026-11-10)
- Types: the public types without type Javadoc, `EtpetsStatistics`, `EtpetsSimulationManager`, `EtpetsAgentLogic`,
  `EtpetsTerrainLogic`, `Pet`, `Trail`, `PetGenome`, `PetTraits`, `EtpetsConfigView`
- Problem: Sixteen public types have no type Javadoc and `EtpetsStatistics` a generic one (D1); `EtpetsStatistics`
  initializes its counters to `0`. `EtpetsSimulationManager` repeats a comment, misses a space, and names a
  `PetTraits` `trait`. `EtpetsAgentLogic` has the non-ASCII arrow `U+2192` in comments, a variable `r`, and the
  counter `cumulativeDeadPetCountChange` for the statistic `cumulativePetDeathCount`. `Pet` uses a `LinkedList` and
  clamps by hand; `Trail` adds without `+=` and has a trailing comment. `PetGenome` and `PetTraits` abbreviate local
  names (`ta`, `movCost`, `normRepE`), and `fromParents(..)` puts two parameters per line. Three class declarations
  wrap although they fit on one line.
- Proposal: Adopt the decisions of D1 to D4 and fix the details.
- Status: done (2026-11-10).

### Decisions

- All public types have a short type Javadoc in the forms of D1; the config view, `EtpetsStatistics`, and
  `EtpetsStepRunner` declare their supertype on one line.
- `EtpetsSimulationManager` shuffles with `Collections.shuffle(..)` and reads the coordinates from
  `structure.coordinatesList()`; `shuffleCoordinates(..)` and `createCoordinates(..)` are removed.
- API change: `EtpetsEditToolBarViewModel` holds an `InputChoiceProperty` for the terrain (`GROUND`) and one for the
  resource (`NONE`); `available...Choices()`, `setSelected...Choice(..)`, and `resolveSelected...Context()` are
  removed, and the getters are non-null. `EtpetsMainView` builds both option boxes with the private generic
  `createOptionBox(..)`.
- API change: `isTerrain()`, `isResource()`, and `isAgent()` are removed with their tests (`ResourceEntityTest` is
  deleted); `ResourceBase.minAmount()` is protected. `PERCENT_MIN` and `PERCENT_MAX` are replaced by
  `ROCK_PERCENT_*`, `WATER_PERCENT_*`, `PLANT_PERCENT_*`, and `INSECT_PERCENT_*` with the same values; the config, the
  ViewModel, `EtpetsConfigTest`, and `SimulationManagerTest` are adapted.
- `EtpetsMainView` holds `trailColors`, `plantColors`, `insectColors`, `petColors`, and `petEggColors`; the min and
  max switches and the suppressed overload are removed (maintainer).
- The four unused accessors, the debug blocks, and the override with its TODO stay (maintainer).
- The details are implemented as proposed; `Pet` takes `initialEnergy`. No rule change.

### D6. Conway

### D7. Forest

### D8. Langton

### D9. Lab

#### Wrapper initializeForDraw of LabObservationView

- Origin: C7 (2026-11-09)
- Types: `LabObservationView`, `LabMainView`
- Problem: The public `initializeForDraw()` only calls the public `initializeObservationLabels()` of the interface.
- Proposal: Let `LabMainView` call `initializeObservationLabels()` and remove the wrapper. An API change.

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

- Step runners take the grid model, the config, and further inputs; step logic takes the `GridStructure`, the config,
  and further inputs (`simulations.md`, "Naming"; B5, D).
- The `Abstract*` and `Base*` classes of `simulations.core` are not `sealed`, because their subclasses lie in the
  simulation packages and a `permits` clause would break the package isolation of `simulations.md`; `sealed` is for
  closed families inside one simulation, such as `CreatureBase` and `ResourceBase` (C5, C6 to C8, D).
- Every public type of a simulation has a short type Javadoc of one sentence in the forms of Wa-Tor, without a
  detailed description of its content (maintainer; D1, D).
- `adjust...Cells(..)` and `adjustCellCounts(..)` of the statistics classes stay public, also with callers only in
  the own `model` package (maintainer; D1, D); `increment...` and `decrement...` methods with callers only in that
  package are package-private (D1, D2, D).
- The commented-out `AppLogger` traces of `EtpetsAgentLogic` stay, because the further development of ET Pets needs
  them; only Wa-Tor removed its traces (maintainer; D1, D5).
- Section comments of the config ViewModels follow the four config groups; `// Rules - NeighborhoodMode` becomes part
  of `// Rules` (D1, D).
- The comment "This user action works only if a cell is selected ..." is removed from the user actions (D1, D).
- Color variants of a main view are held in one `@Nullable` field per entity, without a fallback color for agents
  (D1, D4); colors that depend only on the entity descriptors are computed once in the constructor (D3).
- Edit tool bar ViewModels that select the first choice of a constant catalog initialize the selection at the field
  declaration, without an explicit constructor and without an empty check of the catalog; a fixed catalog uses an
  `InputChoiceProperty` with `FXComponentFactory.createLabeledChoiceComboBox(..)` instead of an observable list and a
  `@Nullable` selection (D3, D4, D5).

## Deferred findings

None yet.

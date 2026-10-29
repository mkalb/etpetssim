# Simulation Classes Overview

**Status:** planned

**Related issues:** #23

**Draft:** This plan was split off from `code-other-classes.md` (2026-10-29) and is not refined yet. Its detailed
refinement (workflow, commit points, categories, findings, and the copied decisions) follows only after
`code-other-classes.md` is done, because its categories 2 to 4 still change the rules and decisions that apply here.
Do not start a category before that refinement.

Temporary working document: an inventory of the instantiable classes of the `simulations` package and its
sub-packages that no sibling plan covers (classes, including abstract base classes, that are neither records, enums,
nor classes with a private constructor), grouped into categories, and the workflow for aligning them. It continues
`code-other-classes.md`, which aligns the code outside `simulations`. The maintainer deletes it after all categories
are done and pushed.

## Workflow

### Goals

- Every class in this inventory complies with `.claude/rules`, in particular its Javadoc and its unit tests. Where a
  rule is missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- The classes are as similar as sensible and possible in style, layout, format, Javadoc, naming (class, fields, and
  methods), member order, constructors, annotations, suppressions, and test coverage: strictly within a category, and
  also across category boundaries and with the classes aligned in `code-other-classes.md`.
- Similar or identical cases are decided together and the same way, regardless of their category; record such
  decisions under "Cross-category decisions".
- Families with one class per simulation (e.g., config views, simulation managers, statistics) are aligned in two
  steps: first the shared base class or base pattern and one reference simulation, then the same decisions for the
  other simulations without asking again, unless a simulation deviates.

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames of classes, fields, and methods, signatures, moves to another
  package) are allowed in this refactoring, but each one needs the maintainer's approval before it is implemented.
- Unit tests are part of the work for the non-JavaFX classes of category 1: check and improve the existing tests of
  the classes, and add missing ones as `junit.md` requires. Decide per class which tests it needs based on
  `junit.md`.
- No new tests in this plan for:
    - JavaFX classes (views, ViewModels, and the JavaFX property wrappers `SeedProperty`, `ConwayRuleProperty`, and
      `LangtonRuleProperty`),
    - the classes of the concrete simulations (categories 4 to 9); their test coverage is optimized and checked
      separately later (maintainer, 2026-10-28).

  Their existing tests are kept, adapted to the changes, and improved where cheap. Test gaps found there are listed
  in the category notes only, not as findings.
- The classes of the Lab simulation are aligned and improved like the others, where sensible and possible, although
  the simulation is hidden from the start screen.
- New rules may be added to `simulations.md` (and the other rule files) where they settle a recurring decision, but
  only a few: prefer extending an existing section over a new one, and do not add a rule for a single case.
- Findings that need larger changes or mainly affect other Java types (interfaces, records, enums, or classes with a
  private constructor) may be moved to "Deferred findings" at the end of this plan instead of being implemented; they
  are handled one by one after the last category.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not commit, review, or delete this plan; the maintainer requests the review and the commit explicitly (after
  formatting, inspecting, and testing in IntelliJ), once after all categories.
- The working tree contains the uncommitted changes of earlier categories. Do not revert them; if `app:test` or
  `app:javadoc` fails because of them, fix the cause.

### Order

- `code-other-classes.md` comes first; its decisions apply here (see "Cross-category decisions"). All its categories,
  including the member order (category 2) and the Javadoc rules (category 3), are done before this plan is refined
  and category 1 starts.
- The sibling plans `code-interface-types.md`, `code-record-types.md`, `code-private-constructor-classes.md`, and
  `code-enum-types.md` are done; their decisions apply here. They stay in `docs/planning` as reference until this
  plan is done and are then deleted together with it and `code-other-classes.md`.
- Categories in their numeric order: the shared base classes (1 to 3) first, because their changes reach all
  subclasses, then the families per simulation (4 to 9).

### Class-specific guidelines

Decide these points in the first category they apply to, record them under "Cross-category decisions", and apply them
in all later categories:

- Constructors: the general points (null checks, redundant field initializations, Javadoc) are decided in
  `code-other-classes.md`; decide the parameter order here (see the finding on the step runners in category 5), and
  apply the general points, e.g., to `sequence = 0` in `CreatureFactory`.
- Base classes: the visibility of the members meant for subclasses (`protected` or package-private) and `sealed`
  where the subclasses are known. The names already follow `simulations.md` (`Abstract*` only for classes that must
  be abstract, `Base*` for complete base implementations such as `BaseTimedSimulationStatistics`, `*Base` for
  abstract entity base classes, and domain-specific entity factories such as `CreatureFactory`). Concrete classes
  are already `final`; keep them `final`.
- Member order of views and ViewModels: constants, fields, constructor, public methods, protected methods, private
  helpers (e.g., `build*` and listener methods); decide in categories 2 and 3, based on the general member order
  decision of `code-other-classes.md` (category 2).
- Section line comments in views and ViewModels (e.g., `// Rules - NeighborhoodMode` in `WatorConfigViewModel`):
  align them with the four groups of the configs (`// Structure`, `// Layout`, `// Initialization`, `// Rules`) where
  the class follows the config components.

### Steps per category

Work on exactly one category at a time.

1. Set the category to `in progress` in the summary table. Read the category section, the relevant rules (`java.md`,
   `junit.md`, and the path-scoped rules for the affected packages), the CSV entries, every class source file of the
   category, and its existing tests. Also read "Cross-category decisions" and "Deferred findings" of this plan and the
   decisions recorded in `code-other-classes.md` and the sibling plans.
2. Analyze: the findings listed under the category, rule violations, inconsistencies within the category and with
   already processed categories, test gaps, and possible rule changes.
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
- Cross-category decisions: one bullet per decision with the affected categories in parentheses, e.g., "(1, 2) ...".
- Deferred findings: one `###` subsection per finding in the same format as the findings.

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv` and `docs/generated/JavaMethodInventory.csv` (regenerated on
2026-10-28): 128 classes. Each class appears in exactly one category. The tables are generated from the CSV files;
verify them against the sources when a category starts.

Table format:

- Class: in categories 1 to 3 relative to `de.mkalb.etpetssim`, in categories 4 to 9 relative to
  `de.mkalb.etpetssim.simulations`; the source file is `app/src/main/java/` plus the package path.
- Modifiers: the class is `public` unless stated otherwise; `-` means neither `final` nor `abstract`.
- Supertypes: without type arguments. Constructors: parameter types without type arguments; `public` unless stated
  otherwise.
- Methods: the number of declared methods per visibility, without inherited ones; nested types follow.
- Tests: a test class named after the class; indirect coverage is listed in the notes.

| # | Category                                                 | Count | Status |
|---|----------------------------------------------------------|------:|--------|
| 1 | Shared simulation model                                  |     6 | open   |
| 2 | Shared ViewModels                                        |     7 | open   |
| 3 | Shared views                                             |     9 | open   |
| 4 | Simulation statistics                                    |     9 | open   |
| 5 | Simulation managers, step runners, and step logic        |    17 | open   |
| 6 | Termination conditions, user actions, and ID sequence    |    17 | open   |
| 7 | Simulation entities                                      |    15 | open   |
| 8 | Simulation ViewModels                                    |    19 | open   |
| 9 | Simulation views                                         |    29 | open   |
|   | **Total**                                                |   128 |        |

Cross-cutting facts:

- All concrete classes are `final`. The abstract classes are the seven `Abstract*` classes in `simulations.core` and
  the `sealed` entity bases `ResourceBase` and `CreatureBase`.
- `BaseTimedSimulationStatistics` is neither `abstract` nor `final` and is extended by eight statistics classes, as
  `simulations.md` intends for `Base*` classes.
- Five classes are package-private: `StatisticsExtremaTracker`, `EditToolBarViewModel`, `DrawCallThrottler`,
  `EditToolBarView`, and `StatisticsHistoryChartView`.
- Most classes come in families of one class per simulation (Conway, Etpets, Forest, Lab, Langton, Rebounding, Snake,
  Sugar, Wa-Tor); Lab deviates in several families (own control view and ViewModel, own main ViewModel, no timed
  manager, no timed statistics).
- Of the 128 classes, 31 have a test class named after them.

## 1. Shared simulation model

Base classes and helpers of the simulation models in `simulations.core.model`.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `simulations.core.model.AbstractTimedSimulationManager` | abstract | SimulationManager | protected `(CON)`; protected `(CON, List)` | 10/3/0/1 | - |
| `simulations.core.model.BaseTimedSimulationStatistics` | - | TimedSimulationStatistics | protected `(GridStructure)` | 4/1/0/0 | - |
| `simulations.core.model.NoUserAction` | final | SimulationUserAction | implicit | 1/0/0/0 | `NoUserActionTest` |
| `simulations.core.model.SimulationInitializationCanceledException` | final | RuntimeException | package-private `()` | 0/0/0/0 | `SimulationInitializationCanceledExceptionTest` |
| `simulations.core.model.StatisticsExtremaTracker` | package-private final | - | package-private `(List)` | 0/0/2/0 | `StatisticsExtremaTrackerTest` |
| `simulations.core.model.StatisticsHistory` | final | - | `()`; `(int)` | 5/0/0/0 | `StatisticsHistoryTest` |

Notes:

- `AbstractTimedSimulationManager` is tested through the simulation managers in `SimulationManagerTest`.
- `BaseTimedSimulationStatistics` is tested only through its subclasses (see category 4).

### Findings

#### Tests for the timed managers

- Origin: `code-interface-types.md`, category 5
- Types: `AbstractTimedSimulationManager`, `SimulationManagerTest`
- Problem: `SimulationManagerTest` mainly tests `AbstractTimedSimulationManager` through the simulation managers; its
  name does not match the tested class.
- Proposal: Rename or split `SimulationManagerTest` (e.g., into `AbstractTimedSimulationManagerTest`).

## 2. Shared ViewModels

Base classes and shared ViewModels in `simulations.core.viewmodel`.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `simulations.core.viewmodel.AbstractConfigViewModel` | abstract | SimulationConfigViewModel | protected `(ReadOnlyObjectProperty, CommonConfigSettings)` | 11/1/0/1; nested private record `ConfigValidationRule`; public record `CommonConfigSettings` | `AbstractConfigViewModelTest` |
| `simulations.core.viewmodel.AbstractMainViewModel` | abstract | SimulationMainViewModel | protected `(ObjectProperty, SimulationConfigViewModel, SimulationObservationViewModel)` | 12/3/0/1 | - |
| `simulations.core.viewmodel.DefaultControlViewModel` | final | SimulationControlViewModel | `(ReadOnlyObjectProperty)` | 19/0/0/0 (1 static) | `DefaultControlViewModelTest` |
| `simulations.core.viewmodel.DefaultMainViewModel` | final | AbstractMainViewModel | `(ObjectProperty, SimulationConfigViewModel, DefaultControlViewModel, DefaultObservationViewModel, BiFunction, BiFunction, SimulationUserAction)`; package-private `(ObjectProperty, SimulationConfigViewModel, DefaultControlViewModel, DefaultObservationViewModel, BiFunction, BiFunction, SimulationUserAction, ExecutorService)` | 20/0/0/27 (2 static); nested private record `ExecutorSimulationTerminationHandle`; private record `SimulationStartRequest` | `DefaultMainViewModelTest` |
| `simulations.core.viewmodel.DefaultObservationViewModel` | final | SimulationObservationViewModel | `(ReadOnlyObjectProperty)` | 15/0/0/0 | `DefaultObservationViewModelTest` |
| `simulations.core.viewmodel.EditToolBarViewModel` | package-private final | - | implicit | 0/0/6/0 | - |
| `simulations.core.viewmodel.SeedProperty` | final | - | `(String)` | 3/0/6/0 (6 static) | - |

Notes:

- `DefaultMainViewModel` is the largest class of this plan (about 900 lines, 27 private methods); its package-private
  constructor takes an `ExecutorService` for the tests.
- JavaFX classes: no new tests (see "Rules for this refactoring"), also not for `AbstractMainViewModel`,
  `EditToolBarViewModel`, and `SeedProperty`.

### Findings

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

## 3. Shared views

Base classes and shared views in `simulations.core.view`.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `simulations.core.view.AbstractConfigView` | abstract | SimulationConfigView | protected `(VM)` | 0/12/0/0 | - |
| `simulations.core.view.AbstractControlView` | abstract | SimulationControlView | protected `(VM)` | 1/4/0/0 | - |
| `simulations.core.view.AbstractDefaultMainView` | abstract | AbstractMainView | protected `(DefaultMainViewModel, CFV, SimulationControlView, OV, GridEntityDescriptorRegistry)` | 1/10/0/10 | - |
| `simulations.core.view.AbstractMainView` | abstract | SimulationMainView | protected `(VM, CFV, CLV, OV, GridEntityDescriptorRegistry)` | 2/16/0/5 (2 static) | `AbstractMainViewTest` |
| `simulations.core.view.AbstractObservationView` | abstract | SimulationObservationView | protected `(VM, GridEntityDescriptorRegistry)` | 3/15/0/2; nested private record `MetricLabels` | - |
| `simulations.core.view.DefaultControlView` | final | AbstractControlView | `(DefaultControlViewModel)` | 1/3/0/4 | - |
| `simulations.core.view.DrawCallThrottler` | package-private final | - | package-private `(int, int)` | 0/0/3/0 | - |
| `simulations.core.view.EditToolBarView` | package-private final | - | package-private `()` | 0/0/4/6 (1 static) | - |
| `simulations.core.view.StatisticsHistoryChartView` | package-private final | - | package-private `(List, ReadOnlyObjectProperty)` | 0/0/3/4 (3 static); nested private record `GroupChart` | `StatisticsHistoryChartViewTest` |

Notes:

- `AbstractObservationView` (about 550 lines) and `AbstractMainView` (about 420 lines) are the largest views.
- Three package-private helpers: `DrawCallThrottler`, `EditToolBarView`, and `StatisticsHistoryChartView`.

## 4. Simulation statistics

One statistics class per simulation in its `model` package.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `conway.model.ConwayStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 6/0/2/0 (1 static) | - |
| `etpets.model.EtpetsStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 5/0/2/0 (1 static) | - |
| `forest.model.ForestStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 5/0/2/0 (1 static) | - |
| `lab.model.LabStatistics` | final | SimulationStatistics | `(GridStructure)` | 3/0/0/0 | - |
| `langton.model.LangtonStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 4/0/2/0 (1 static) | - |
| `rebounding.model.ReboundingStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 8/0/1/0 (1 static) | - |
| `snake.model.SnakeStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 14/0/2/0 (1 static) | - |
| `sugar.model.SugarStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 5/0/2/0 (1 static) | - |
| `wator.model.WatorStatistics` | final | BaseTimedSimulationStatistics | `(GridStructure)` | 5/0/5/0 (1 static) | - |

Notes:

- Eight classes extend `BaseTimedSimulationStatistics`, take the `GridStructure`, and expose the static `metrics()`;
  `LabStatistics` implements `SimulationStatistics` directly and has no metrics catalog.
- The classes have no own tests; they are covered through `SimulationManagerTest`, `StatisticsMetricLabelKeyTest`,
  `StatisticsMetricLayoutTest`, and some ViewModel and view tests.

### Findings

#### Metrics catalogs of the statistics classes

- Origin: `code-private-constructor-classes.md`, cross-category decisions (catalog rule in `simulations.md`,
  "Model")
- Types: the statistics classes except `LabStatistics`
- Problem: Each class exposes its metrics catalog through the static noun-style accessor `metrics()`, but builds a new
  `List.of(..)` on every call instead of returning a constant unmodifiable list, as the catalog rule requires
  (`ConwayPatterns.choices()`, `SnakeMoveStrategies.strategies()`). Callers are the simulation managers, the
  observation views, and several tests in `simulations.core`.
- Proposal: Store the list in a private static final constant and return it from `metrics()`; decide whether the
  catalog rule applies to static accessors of instantiable classes and, if so, clarify it in `simulations.md`.

## 5. Simulation managers, step runners, and step logic

The simulation manager of each simulation and the classes that perform its steps.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `conway.model.ConwaySimulationManager` | final | AbstractTimedSimulationManager | `(ConwayConfig)`; `(ConwayConfig, SimulationInitializationCancellation)` | 2/2/0/2 | - |
| `etpets.model.EtpetsSimulationManager` | final | AbstractTimedSimulationManager | `(EtpetsConfig)`; `(EtpetsConfig, SimulationInitializationCancellation)` | 2/2/0/8 (2 static) | - |
| `forest.model.ForestSimulationManager` | final | AbstractTimedSimulationManager | `(ForestConfig)`; `(ForestConfig, SimulationInitializationCancellation)` | 2/2/0/2 | - |
| `lab.model.LabSimulationManager` | final | SimulationManager | `(LabConfig)` | 9/0/0/0 | - |
| `langton.model.LangtonSimulationManager` | final | AbstractTimedSimulationManager | `(LangtonConfig)`; `(LangtonConfig, SimulationInitializationCancellation)` | 2/2/1/2 | - |
| `rebounding.model.ReboundingSimulationManager` | final | AbstractTimedSimulationManager | `(ReboundingConfig)`; `(ReboundingConfig, SimulationInitializationCancellation)` | 2/2/0/7 | - |
| `snake.model.SnakeSimulationManager` | final | AbstractTimedSimulationManager | `(SnakeConfig)`; `(SnakeConfig, SimulationInitializationCancellation)` | 2/2/2/5 | - |
| `sugar.model.SugarSimulationManager` | final | AbstractTimedSimulationManager | `(SugarConfig)`; `(SugarConfig, SimulationInitializationCancellation)` | 2/2/0/8 | - |
| `wator.model.WatorSimulationManager` | final | AbstractTimedSimulationManager | `(WatorConfig)`; `(WatorConfig, SimulationInitializationCancellation)` | 4/2/0/4 | - |
| `etpets.model.EtpetsStepRunner` | final | SimulationStepRunner | `(Random, EtpetsGridModel, EtpetsIdSequence)` | 2/0/0/0 | - |
| `langton.model.LangtonStepRunner` | final | SimulationStepRunner | `(LangtonConfig, LangtonGridModel)` | 2/0/6/0 | - |
| `sugar.model.SugarStepRunner` | final | SimulationStepRunner | `(SugarConfig, Random, SugarGridModel)` | 2/0/0/0 | - |
| `conway.model.ConwayStepLogic` | final | SynchronousStepLogic | `(GridStructure, ConwayConfig)` | 1/0/0/0 | - |
| `forest.model.ForestStepLogic` | final | SynchronousStepLogic | `(GridStructure, ForestConfig, Random)` | 1/0/0/2 | - |
| `rebounding.model.ReboundingStepLogic` | final | AgentStepLogic | `(GridStructure, ReboundingConfig)` | 1/0/0/1 | - |
| `snake.model.SnakeStepLogic` | final | AgentStepLogic | `(GridStructure, SnakeConfig, Random)` | 1/0/0/4 | - |
| `wator.model.WatorStepLogic` | final | AgentStepLogic | `(WatorConfig, Random, CreatureFactory)` | 1/0/0/3 | - |

Notes:

- The eight timed managers have two constructors, `(config)` and `(config, SimulationInitializationCancellation)`;
  `LabSimulationManager` implements `SimulationManager` directly and has only `(config)`.
- The step logic constructors take the `GridStructure` first, then the config and the further inputs;
  `WatorStepLogic(config, random, creatureFactory)` needs no structure.
- No class of this category has a test of its own; the managers are tested in `SimulationManagerTest` (cancellation,
  samples, extrema, and golden values for Conway, Forest, and Wa-Tor) and through the user action tests. Test gaps
  for the later separate optimization: the five `*StepLogic` classes and `LangtonStepRunner`, which contains the
  Langton rules itself (six package-private methods).
- `EtpetsSimulationManager` duplicates `shuffleCoordinates(..)` of `GridInitializers`; see the finding "Notes on
  `engine` from #23" in `code-other-classes.md`, category 4.

### Findings

#### Constructor parameters of the step runners

- Origin: `code-private-constructor-classes.md`, category 6
- Types: `EtpetsStepRunner`, `SugarStepRunner`, `LangtonStepRunner`, and their callers `EtpetsSimulationManager`,
  `SugarSimulationManager`, and `LangtonSimulationManager`
- Problem: The constructors take `(random, model, idSequence)`, `(config, random, model)`, and `(config, model)`,
  while the engine runners `AsynchronousStepRunner` and `SynchronousStepRunner` take the grid model first, and the
  static step logic orders `apply(..)` as grid model, config, further inputs (`simulations.md`, "Naming").
- Proposal: Order the constructor parameters of all step runners as grid model, config, further inputs; decide
  whether `simulations.md` states this order for step runners and for the step logic constructors (structure,
  config, further inputs) too. An API change with one caller each.

#### Edge behavior checks and direction rings

- Origin: `code-other-classes.md`, category 4, group 4 (`engine.neighborhood`)
- Types: `ConwayStepLogic`, `ForestStepLogic`, `SnakeStepLogic`, `WatorStepLogic`, `SugarSimulationManager`,
  `ReboundingSimulationManager`; also `ReboundingEditToolBarViewModel` (category 8) and `SugarAgentLogic` (a class
  with a private constructor, not in this plan), which are changed together with this finding
- Problem: Six checks compare an edge behavior action with `EdgeBehaviorAction.VALID` and `WRAPPED`
  (`ConwayStepLogic`, `ForestStepLogic`, `SnakeStepLogic`, `WatorStepLogic`, `SugarSimulationManager`, and
  `SugarAgentLogic`), although `EdgeBehaviorAction.isWithinGrid()` exists. `ReboundingEditToolBarViewModel.updateAvailableDirections(..)`,
  `ReboundingSimulationManager.resolveDirectionRing(..)`, and the constructor of `SnakeStepLogic` select the direction
  ring by cell shape and neighborhood mode themselves instead of calling
  `CellNeighborhoods.directionRing(neighborhoodMode, cellShape)`.
- Proposal: Use `action.isWithinGrid()` and `directionRing(..)`; `ReboundingSimulationManager` drops
  `resolveDirectionRing(..)`. `directionRing(..)` rejects `TRIANGLE` with an `IllegalArgumentException`, which
  replaces the `IllegalStateException` of the Rebounding classes. `directionRing(..)` returns a ring for square cells
  with `EDGES_AND_VERTICES`, which `SnakeStepLogic` rejects today; decide whether it keeps this check or relies on
  `SnakeConfig.isValid()`, which allows only `NEIGHBORHOOD_MODE_DEFAULT`. `EtpetsAgentLogic` checks only `VALID`
  and stays unchanged. No API change in `simulations`; existing tests (e.g., `SnakeMoveStrategiesTest`,
  `LabNeighborhoodHighlightsTest`) keep the ring constants and actions as test data.

## 6. Termination conditions, user actions, and ID sequence

Small per-simulation model classes.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `conway.model.ConwayTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `etpets.model.EtpetsTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `forest.model.ForestTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `langton.model.LangtonTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `rebounding.model.ReboundingTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `snake.model.SnakeTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `sugar.model.SugarTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `wator.model.WatorTerminationCondition` | final | SimulationTerminationCondition | implicit | 1/0/0/0 | - |
| `conway.model.ConwayUserAction` | final | SimulationUserAction | implicit | 1/0/0/4 | `ConwayUserActionTest` |
| `etpets.model.EtpetsUserAction` | final | SimulationUserAction | implicit | 1/0/0/4 | `EtpetsUserActionTest` |
| `forest.model.ForestUserAction` | final | SimulationUserAction | implicit | 1/0/0/0 | `ForestUserActionTest` |
| `langton.model.LangtonUserAction` | final | SimulationUserAction | implicit | 1/0/0/1 | `LangtonUserActionTest` |
| `rebounding.model.ReboundingUserAction` | final | SimulationUserAction | implicit | 1/0/0/0 | `ReboundingUserActionTest` |
| `snake.model.SnakeUserAction` | final | SimulationUserAction | implicit | 1/0/0/3 | `SnakeUserActionTest` |
| `sugar.model.SugarUserAction` | final | SimulationUserAction | implicit | 1/0/0/2 | `SugarUserActionTest` |
| `wator.model.WatorUserAction` | final | SimulationUserAction | implicit | 1/0/0/0 | `WatorUserActionTest` |
| `etpets.model.EtpetsIdSequence` | final | - | `(int)` | 1/0/0/0 | - |

Notes:

- The termination conditions and user actions have an implicit constructor and one public method each.
- All eight user actions have a test; no termination condition has one.
- `EtpetsIdSequence` is used in `EtpetsAgentLogicTest` but has no test of its own.

## 7. Simulation entities

Instantiable entities in the `model.entity` packages.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `etpets.model.entity.ResourceBase` | abstract sealed | ResourceEntity | protected `(double, double, double)` | 10/1/0/0 | - |
| `etpets.model.entity.Insect` | final | ResourceBase | `(double, double, double)` | 5/1/0/0 | `InsectTest` |
| `etpets.model.entity.Plant` | final | ResourceBase | `(double, double, double)` | 5/1/0/0 | `PlantTest` |
| `etpets.model.entity.Pet` | final | AgentEntity | `(int, Integer, Integer, int, int, int, PetTraits)` | 28/0/0/0; nested public record `PetLastAction` | `PetTest` |
| `etpets.model.entity.PetEgg` | final | AgentEntity | `(int, int, int, PetGenome, int, int)` | 12/0/0/0 | `PetEggTest` |
| `etpets.model.entity.Trail` | final | TerrainEntity | `(int)` | 6/0/0/0 | `TrailTest` |
| `langton.model.entity.Ant` | final | AntEntity | `(CompassDirection)` | 6/0/0/0 | `AntTest` |
| `rebounding.model.entity.Rebounder` | final | ReboundingEntity | `(CompassDirection)` | 8/0/0/0 | `RebounderTest` |
| `snake.model.entity.SnakeHead` | final | SnakeEntity | `(int, SnakeMoveStrategy, int, int)` | 23/0/0/0 | `SnakeHeadTest` |
| `sugar.model.entity.Agent` | final | AgentEntity | `(int, int)` | 10/0/0/0 | `AgentTest` |
| `sugar.model.entity.Sugar` | final | ResourceEntity | `(int, int)` | 8/0/0/0 | `SugarTest` |
| `wator.model.entity.CreatureBase` | abstract sealed | WatorEntity, Comparable | protected `(String, long, int)` | 13/0/0/0 | - |
| `wator.model.entity.Fish` | final | CreatureBase | `(long, int)` | 4/0/0/0 | `FishTest` |
| `wator.model.entity.Shark` | final | CreatureBase | `(long, int, int)` | 6/0/0/0 | `SharkTest` |
| `wator.model.entity.CreatureFactory` | final | - | `()` | 2/0/0/0 | - |

Notes:

- The `sealed` bases `ResourceBase` and `CreatureBase` are tested through their subclasses.
- `Pet` takes two `Integer` parameters (the parent IDs) next to `int` parameters.
- `CreatureFactory` creates `Fish` and `Shark` with a running sequence number, like `EtpetsIdSequence` in category 6;
  it has no test.

## 8. Simulation ViewModels

ViewModels and input properties in the `viewmodel` packages of the simulations.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `conway.viewmodel.ConwayConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 7/0/0/4 | - |
| `etpets.viewmodel.EtpetsConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 6/0/0/0 | - |
| `forest.viewmodel.ForestConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 5/0/0/0 | - |
| `lab.viewmodel.LabConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 4/0/0/1 | - |
| `langton.viewmodel.LangtonConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 2/0/0/0 | - |
| `rebounding.viewmodel.ReboundingConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 4/0/0/0 | - |
| `snake.viewmodel.SnakeConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 9/0/0/0 | - |
| `sugar.viewmodel.SugarConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 10/0/0/0 | - |
| `wator.viewmodel.WatorConfigViewModel` | final | AbstractConfigViewModel | `(ReadOnlyObjectProperty)` | 14/0/0/0 | - |
| `conway.viewmodel.ConwayEditToolBarViewModel` | final | - | implicit | 6/0/0/1 | - |
| `etpets.viewmodel.EtpetsEditToolBarViewModel` | final | - | `()` | 10/0/0/0 | - |
| `langton.viewmodel.LangtonEditToolBarViewModel` | final | - | implicit | 6/0/0/1 | `LangtonEditToolBarViewModelTest` |
| `rebounding.viewmodel.ReboundingEditToolBarViewModel` | final | - | implicit | 6/0/0/0 | - |
| `snake.viewmodel.SnakeEditToolBarViewModel` | final | - | `()` | 4/0/0/0 | - |
| `sugar.viewmodel.SugarEditToolBarViewModel` | final | - | `()` | 5/0/0/0 | - |
| `conway.viewmodel.ConwayRuleProperty` | final | - | `()` | 6/0/0/0 | - |
| `langton.viewmodel.LangtonRuleProperty` | final | - | `()` | 6/0/0/0 | - |
| `lab.viewmodel.LabControlViewModel` | final | SimulationControlViewModel | `(ReadOnlyObjectProperty)` | 8/0/0/0 | - |
| `lab.viewmodel.LabMainViewModel` | final | AbstractMainViewModel | `(ObjectProperty, LabConfigViewModel, LabControlViewModel, DefaultObservationViewModel)` | 13/0/0/5 | - |

Notes:

- The nine config ViewModels take only the simulation state property; the edit tool bar ViewModels of Conway,
  Langton, and Rebounding have an implicit constructor, those of Etpets, Snake, and Sugar an explicit one that
  selects the first choice.
- Only `LangtonEditToolBarViewModel` has a test; JavaFX classes get no new tests in this plan.
- `ReboundingEditToolBarViewModel.updateAvailableDirections(..)` calls `CellNeighborhoods.directionRing(..)` since
  category 5 (finding "Edge behavior checks and direction rings").

## 9. Simulation views

Views in the `view` packages of the simulations and the start screen.

| Class | Modifiers | Supertypes | Constructors | Methods (pub/prot/pkg/priv) | Tests |
|-------|-----------|------------|--------------|-----------------------------|-------|
| `conway.view.ConwayConfigView` | final | AbstractConfigView | `(ConwayConfigViewModel)` | 1/0/0/2 | - |
| `etpets.view.EtpetsConfigView` | final | AbstractConfigView | `(EtpetsConfigViewModel)` | 1/0/0/0 | - |
| `forest.view.ForestConfigView` | final | AbstractConfigView | `(ForestConfigViewModel)` | 1/0/0/0 | - |
| `lab.view.LabConfigView` | final | AbstractConfigView | `(LabConfigViewModel)` | 1/0/0/0 | - |
| `langton.view.LangtonConfigView` | final | AbstractConfigView | `(LangtonConfigViewModel)` | 1/0/0/0 | - |
| `rebounding.view.ReboundingConfigView` | final | AbstractConfigView | `(ReboundingConfigViewModel)` | 1/0/0/0 | - |
| `snake.view.SnakeConfigView` | final | AbstractConfigView | `(SnakeConfigViewModel)` | 1/0/0/0 | - |
| `sugar.view.SugarConfigView` | final | AbstractConfigView | `(SugarConfigViewModel)` | 1/0/0/0 | - |
| `wator.view.WatorConfigView` | final | AbstractConfigView | `(WatorConfigViewModel)` | 1/0/0/2 | - |
| `conway.view.ConwayMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, ConwayEditToolBarViewModel, GridEntityDescriptorRegistry, ConwayConfigView, DefaultControlView, ConwayObservationView)` | 0/5/0/1 | - |
| `etpets.view.EtpetsMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, EtpetsEditToolBarViewModel, GridEntityDescriptorRegistry, EtpetsConfigView, DefaultControlView, EtpetsObservationView)` | 0/5/0/11 | - |
| `forest.view.ForestMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, GridEntityDescriptorRegistry, ForestConfigView, DefaultControlView, ForestObservationView)` | 0/4/0/0 | - |
| `lab.view.LabMainView` | final | AbstractMainView | `(LabMainViewModel, GridEntityDescriptorRegistry, LabConfigView, LabControlView, LabObservationView)` | 0/3/0/23 | - |
| `langton.view.LangtonMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, LangtonEditToolBarViewModel, GridEntityDescriptorRegistry, LangtonConfigView, DefaultControlView, LangtonObservationView)` | 0/5/0/3 | - |
| `rebounding.view.ReboundingMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, ReboundingEditToolBarViewModel, GridEntityDescriptorRegistry, ReboundingConfigView, DefaultControlView, ReboundingObservationView)` | 0/5/0/1 | - |
| `snake.view.SnakeMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, SnakeEditToolBarViewModel, GridEntityDescriptorRegistry, SnakeConfigView, DefaultControlView, SnakeObservationView)` | 0/5/0/5 (4 static) | - |
| `sugar.view.SugarMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, SugarEditToolBarViewModel, GridEntityDescriptorRegistry, SugarConfigView, DefaultControlView, SugarObservationView)` | 0/5/0/4 | - |
| `wator.view.WatorMainView` | final | AbstractDefaultMainView | `(DefaultMainViewModel, GridEntityDescriptorRegistry, WatorConfigView, DefaultControlView, WatorObservationView)` | 0/4/0/2 | - |
| `start.StartMainView` | final | SimulationMainView, SimulationControlView | `(Stage, BiConsumer)` | 4/0/0/0 | - |
| `conway.view.ConwayObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/1/0/0 | - |
| `etpets.view.EtpetsObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/2/0/0 | - |
| `forest.view.ForestObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/1/0/0 | - |
| `lab.view.LabObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 2/1/0/0 | - |
| `langton.view.LangtonObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/1/0/0 | - |
| `rebounding.view.ReboundingObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/2/0/0 | - |
| `snake.view.SnakeObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/2/0/0 | - |
| `sugar.view.SugarObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/2/0/0 | - |
| `wator.view.WatorObservationView` | final | AbstractObservationView | `(DefaultObservationViewModel, GridEntityDescriptorRegistry)` | 1/2/0/0 | - |
| `lab.view.LabControlView` | final | AbstractControlView | `(LabControlViewModel)` | 1/3/0/0 | - |

Notes:

- The main views take `(viewModel, [editToolBarViewModel], entityDescriptorRegistry, configView, controlView,
  observationView)`; Forest and Wa-Tor have no edit tool bar. `LabMainView` extends `AbstractMainView` directly and
  has 23 private methods; `StartMainView` implements `SimulationMainView` and `SimulationControlView`.
- `WatorConfigView` has a class-level `@SuppressWarnings("SpellCheckingInspection")`; check its scope
  (`java.md`, "Suppressions Policy").
- No view of this category has a test; JavaFX classes get no new tests in this plan.

## Cross-category decisions

Decisions of `code-other-classes.md` that apply to all categories of this plan (copied on 2026-10-29; see there for
the details and the rule changes, and update this copy in the refinement after `code-other-classes.md` is done):

- Constructors: no redundant initializations of fields to their default values (`java.md`, "Code Style"); no null
  checks of non-null parameters (`java.md`, "Nullability"); a constructor has Javadoc only when it explains something
  that the name, signature, and type Javadoc do not show (e.g., a precondition or how a parameter is used), and then
  its summary sentence starts with "Creates a ..." (`java.md`, "Javadoc"); Javadoc that only restates the class name
  and parameters is removed.
- Javadoc writes the literals `true`, `false`, and `null` as `{@code ...}` (`java.md`, "Javadoc").
- Public methods and constructors without a caller in main sources are removed (API change with approval) unless the
  maintainer keeps them for a planned use; core classes, the base types of `engine`, `engine.model`,
  `engine.model.entity`, and `engine.neighborhood`, and `FXGridCanvasPainter` keep their complete API (`core.md`,
  `engine.md`, and `ui.md`).
- Code in `simulations` checks edge behavior actions with `EdgeBehaviorAction.isWithinGrid()` instead of comparing
  with `VALID` and `WRAPPED`, and selects a direction ring with `CellNeighborhoods.directionRing(..)` (category 5,
  finding "Edge behavior checks and direction rings").
- Open (decided in category 2 of `code-other-classes.md`): members are ordered as constants, fields,
  constructors, public methods (including overridden lifecycle methods such as `start(..)`), package-private methods,
  `toString()`, then private helpers, followed by nested types.
- Class-level `@SuppressWarnings` are narrowed to the members that need them, with the maintainer's approval per case
  (`java.md`, "Suppressions Policy").

Decisions of this plan:

None yet.

## Deferred findings

None yet.

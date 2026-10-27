# Interface Types Overview

**Status:** done

**Related issues:** #23

Temporary working document: an inventory of all interface types in the `main` source set, grouped into categories, and
the workflow for aligning them. The maintainer deletes it after all categories are done and pushed.

## Workflow

### Goals

- Every interface in `app/src/main/java` complies with `.claude/rules`, in particular its Javadoc and its unit tests.
  Where a rule is missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- Interfaces are as similar as sensible and possible in style, layout, format, Javadoc, naming, member order,
  annotations, suppressions, and test coverage: strictly within a category, and also across category boundaries.
- Similar or identical cases are decided together and the same way, regardless of their category; record such
  decisions under "Cross-category decisions".

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames, signatures, type hierarchy) are allowed in this refactoring,
  but each one needs the maintainer's approval before it is implemented.
- Unit tests are always part of the work: check and improve the existing tests of the interfaces and their default and
  static methods, and add missing ones as `junit.md` requires. Many interfaces may need no tests of their own (e.g.,
  markers or interfaces without default or static methods); decide this per interface based on `junit.md`.
- Findings that need larger changes or mainly affect other Java types (classes, records, enums) may be moved to
  "Deferred findings" at the end of this plan instead of being implemented; they are handled one by one after the
  final commit.
- When changing a rule in `.claude/rules`, update its copy in `.github/instructions` in the same step.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not commit, review, or delete this plan; the maintainer requests the review and the commit explicitly after all
  categories are done (after formatting, inspecting, and testing in IntelliJ).
- The working tree contains the uncommitted changes of earlier categories. Do not revert them; if `app:test` or
  `app:javadoc` fails because of them, fix the cause.
- Topics of the sibling plans `code-record-types.md` and `code-private-constructor-classes.md` are out of scope; record
  overlapping findings under "Deferred findings" instead of implementing them.
- Complete this plan before `code-record-types.md`; that plan builds on the decisions recorded here, because 27
  records implement interfaces of this plan. `code-private-constructor-classes.md` follows as the third plan.

### Steps per category

Work on exactly one category at a time. The agent may choose the order; suggested order (base contracts first, then
the types that depend on them, MVVM last): 2, 3, 1, 4, 5, 7, 8, 6.

1. Set the category to `in progress` in the summary table. Read the category section, the relevant rules (`java.md`,
   `junit.md`, and the path-scoped rules for the affected packages), the CSV entries, every interface source file of
   the category, and its existing tests. Also read "Cross-category decisions" and "Deferred findings".
2. Analyze: rule violations, inconsistencies within the category and with already processed categories, test gaps,
   and possible rule changes.
3. Present the results and recommendations in the chat and ask the open decision questions; mark every recommended
   API change explicitly. Do not change code before the maintainer has decided.
4. Implement the decisions, including required changes to implementers, callers, tests, and rules.
5. Run `app:test` and `app:javadoc`; both must pass before the category counts as done.
6. Update this plan: set the category to `done` in the summary table, record the decisions under the category, add
   cross-category decisions and deferred findings, and correct outdated inventory facts, including the cross-cutting
   facts in the summary (annotations, suppressions, static methods, type parameters).

### Documentation format

- Category decisions: a `### Decisions` subsection at the end of the category section, one bullet per decision; prefix
  a bullet with "API change:" or "Rule change:" (naming the rule file) when it applies.
- Cross-category decisions: one bullet per decision with the affected categories in parentheses, e.g.,
  "(1, 2) ...".
- Deferred findings: one `###` subsection per finding with a short title, followed by the bullets "Origin:" (category),
  "Types:" (affected types), "Problem:", and "Proposal:".

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv`, `docs/generated/JavaMethodInventory.csv`, checked against the sources
(58 interface types). Each interface appears in exactly one category. Names are fully qualified; the source file is
`app/src/main/java/` plus the package path. Implementers are given by simple name; "(I)" marks an extending interface,
"(E)" an enum, "(R)" a record, and "(C)" a class. Lambda implementations are not listed.

| # | Category                                                 | Count | Status |
|---|----------------------------------------------------------|------:|--------|
| 1 | Grid model hierarchy                                     |     5 | done   |
| 2 | Grid entity and descriptor contracts                     |     5 | done   |
| 3 | Simulation-specific sealed entity hierarchies            |    12 | done   |
| 4 | Functional engine step and grid logic                    |     6 | done   |
| 5 | Simulation model contracts                               |     8 | done   |
| 6 | MVVM view and view model contracts                       |    10 | done   |
| 7 | User action contexts                                     |     7 | done   |
| 8 | Rule presets and move strategies                         |     5 | done   |
|   | **Total**                                                |    58 |        |

Cross-cutting facts:

- All interfaces are top-level; there are no nested interfaces. All are `public` except the package-private
  `AnonymousMoveStrategy`.
- 24 interfaces are `sealed`, two are `non-sealed` (`CompositeGridModel`, `AnonymousMoveStrategy`); all other
  interfaces are open.
- 11 interfaces are annotated `@FunctionalInterface`: `AgentStepLogic`, `SimulationStepRunner`,
  `SimulationTerminationCondition`, `SynchronousStepLogic`, `GridInitializer`, `GridPattern`,
  `SimulationInitializationCancellation`, `SimulationUserAction`, `CellDrawer`, `CoordinateDrawer`, and
  `AnonymousMoveStrategy`.
- `@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")` is used on `GridEntity`, `ConstantGridEntity`,
  `SpecBackedGridEntityDescriptorProvider`, `ConstantGridEntityDescriptorProvider`, and `SimulationConfigView`.
- Nine interfaces declare no members of their own: `ConstantGridEntity`, `AntEntity`, `SimulationUserActionContext`,
  and the six simulation-specific user action contexts. All except `AntEntity` carry
  `@SuppressWarnings("MarkerInterface")`.
- Public static methods exist in only four interfaces: `GridEntity.isConstant(GridEntity)`,
  `SimulationTerminationHandle.completed()`, `SimulationInitializationCancellation.none()` and `interruptionAware()`,
  and `LangtonEntity.allEntityDescriptorProviders()`.
- Type parameter names: `ENT` (entity), `GM` (grid model), `CON` (config), `STA` (statistics), `SM` (simulation
  manager), `CTX` (user action context), and `C` (step context, only in `engine.executor`).
- Duplicate simple names: `AgentEntity` and `ResourceEntity` exist in both `etpets.model.entity` and
  `sugar.model.entity`.

## 1. Grid model hierarchy

Sealed read/write hierarchy of the grid model in `engine.model`, plus the cell view.

| Interface                                         | Modifiers    | Extends / permits | Members (own) | Implementers |
|---------------------------------------------------|--------------|-------------------|---------------|--------------|
| `de.mkalb.etpetssim.engine.model.GridModel`          | `sealed`     | permits `ReadableGridModel`, `CompositeGridModel` | `structure()`, default `isCoordinateValid(GridCoordinate)` | `ReadableGridModel` (I), `CompositeGridModel` (I) |
| `de.mkalb.etpetssim.engine.model.ReadableGridModel`  | `sealed`     | extends `GridModel`; permits `WritableGridModel` | `defaultEntity()`, `getEntity(..)`, `isDefaultEntity(..)`, `isSparse()`, `countEntities(..)`, `nonDefaultCoordinates()`, `filteredCoordinates(..)`, `findRandomDefaultCoordinate(..)`, `allCells()`, `nonDefaultCells()`, `filteredCells(..)`, `filteredCellsSortedBy(..)`; default `getGridCell(..)` | `WritableGridModel` (I) |
| `de.mkalb.etpetssim.engine.model.WritableGridModel`  | `sealed`     | extends `ReadableGridModel`; permits `ArrayGridModel`, `SparseGridModel` | `copy()`, `copyWithDefaultEntity()`, `setEntity(..)`, `setEntityToDefault(..)`, `fill(..)` (3 overloads), `clear()`, `setSwappedEntities(..)`; default `setEntity(GridCell)` | `ArrayGridModel` (C), `SparseGridModel` (C) |
| `de.mkalb.etpetssim.engine.model.CompositeGridModel` | `non-sealed` | extends `GridModel` | `getEntities(GridCoordinate)`, `layerCount()` | `EtpetsGridModel` (R), `LangtonGridModel` (R), `SugarGridModel` (R) |
| `de.mkalb.etpetssim.engine.model.GridCellView`       |              | -                 | `coordinate()`, `entity()`; defaults `descriptorId()`, `toDisplayString()` | `GridCell` (R), `EtpetsCell` (R), `LangtonCell` (R), `SugarCell` (R) |

Notes:

- `CompositeGridModel` is the only `non-sealed` interface; its three implementations are the simulation-specific
  multi-layer models.
- `GridCellView.descriptorId()` and `toDisplayString()` delegate to the entity and duplicate the method names of
  `GridEntity` (category 2).

### Decisions

- API change: `GridModel.isComposite()` and its overrides in `ReadableGridModel` and `CompositeGridModel` are removed;
  they had no production caller (use `instanceof` on the sealed hierarchy).
- The filter, count, and sort parameters keep `Predicate<ENT>` and `Comparator<GridCell<ENT>>` (no `? super`), as
  `countEntities` was aligned by the maintainer.
- Javadoc of all five interfaces follows the `<p>` rules of `java.md`; `ReadableGridModel` is described as read-only
  access instead of a "view of a `WritableGridModel`".
- `WritableGridModelTest` stays the contract test of `GridModel`, `ReadableGridModel`, and `WritableGridModel`
  (over `ArrayGridModel` and `SparseGridModel`); added `testStructure`, `testDefaultEntity`, `testIsSparse`,
  `testFilteredCoordinates`, and `testFilteredCells`, removed `testIsComposite`.
- `GridCellViewTest` (fixture `TestCellView`) tests the defaults of `GridCellView`; the two tests moved there from
  `GridCellTest`, which is removed. `GridCell` is a pure data carrier and gets no `testRecord` (superseded by
  `code-record-types.md`, category 3).
- `CompositeGridModel` has no default or static methods and needs no test of its own; the three composite records
  are covered by the record plan.

## 2. Grid entity and descriptor contracts

Base contracts for grid entities and their descriptors in `engine.model.entity`.

| Interface                                                                     | Extends | Members (own) | Implementers |
|-------------------------------------------------------------------------------|---------|---------------|--------------|
| `de.mkalb.etpetssim.engine.model.entity.GridEntity`                           | -       | `descriptorId()`; default `toDisplayString()`; static `isConstant(GridEntity)` | `ConstantGridEntity` (I) and the seven simulation entity roots (category 3) |
| `de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity`                   | `GridEntity` | - (marker) | `ConstantGridEntityDescriptorProvider` (I), `NoAgent` (E, 3x), `NoResource` (E, 2x), `TerrainConstant` (E, 4x) |
| `de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider`         | -       | `descriptorId()`, `visible()`, `shortNameKey()`, `longNameKey()`, `descriptionKey()`, `emojiKey()`, `color()`, `borderColor()` | `SpecBackedGridEntityDescriptorProvider` (I) |
| `de.mkalb.etpetssim.engine.model.entity.SpecBackedGridEntityDescriptorProvider` | `GridEntityDescriptorProvider` | `descriptorSpec()`; defaults for all eight inherited methods | `ConstantGridEntityDescriptorProvider` (I), `EntityDescriptors` (E, 6x) |
| `de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider` | `ConstantGridEntity`, `SpecBackedGridEntityDescriptorProvider` | default `descriptorId()` | `ConwayEntity` (E), `ForestEntity` (E), `LabEntity` (E), Langton `TerrainConstant` (E) |

Notes:

- `descriptorId()` is declared five times along these lines: abstract in `GridEntity` and
  `GridEntityDescriptorProvider`, default in `SpecBackedGridEntityDescriptorProvider`,
  `ConstantGridEntityDescriptorProvider`, and `GridCellView` (category 1).
- `GridEntityDescriptorProvider` has exactly one direct subtype; all implementations go through
  `SpecBackedGridEntityDescriptorProvider`.
- `emojiKey()`, `color()`, and `borderColor()` are `@Nullable`.

### Decisions

- Javadoc of `GridEntity`, `GridEntityDescriptorProvider`, and `SpecBackedGridEntityDescriptorProvider` follows the
  `<p>` rules of `java.md` (no `</p>`, `<p>` directly before the first word, no `<br>`); `GridEntity` and
  `ConstantGridEntity` link to each other.
- Rule change: `java.md` (Nullability): `@Nullable` is placed inline (`@Nullable String emojiKey()`), as in
  `GridEntityDescriptorProvider` and the rest of the code base.
- `SpecBackedGridEntityDescriptorProviderTest` covers unset optional values (`null` emoji key and colors).
- `GridEntityDescriptorProvider` and `SpecBackedGridEntityDescriptorProvider` stay separate: the registry and tests
  use the narrow contract directly.
- `ConstantGridEntity` and `GridEntityDescriptorProvider` need no tests of their own (no default or static methods).

## 3. Simulation-specific sealed entity hierarchies

Each simulation with typed entities has a sealed root interface extending `GridEntity`; Etpets, Langton, and Sugar add
a second sealed layer.

| Interface                                                                | Extends | Permits | Members (own) |
|--------------------------------------------------------------------------|---------|---------|---------------|
| `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity`        | `GridEntity` | `TerrainEntity`, `ResourceEntity`, `AgentEntity` | `isTerrain()`, `isResource()`, `isAgent()`, `isEmpty()`; default `isNotEmpty()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity`       | `EtpetsEntity` | `TerrainConstant`, `Trail` | `isWalkable()`; defaults `isTerrain()`, `isResource()`, `isAgent()`, `isEmpty()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceEntity`      | `EtpetsEntity` | `NoResource`, `ResourceBase` | defaults `isTerrain()`, `isAgent()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.AgentEntity`         | `EtpetsEntity` | `NoAgent`, `Pet`, `PetEgg` | defaults `isTerrain()`, `isResource()`, `id()` |
| `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity`      | `GridEntity` | `AntEntity`, `TerrainConstant` | `DESCRIPTOR_ID_ANT`, `DESCRIPTOR_ID_NO_AGENT`; `isAgent()`; static `allEntityDescriptorProviders()` |
| `de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity`          | `LangtonEntity` | `Ant`, `NoAgent` | - |
| `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity` | `GridEntity` | `TerrainConstant`, `Rebounder` | `isGround()`, `isWall()`, `isRebounder()` |
| `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity`          | `GridEntity` | `TerrainConstant`, `SnakeHead` | `isAgent()`, `isGround()`, `isWall()`, `isFood()` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity`          | `GridEntity` | `ResourceEntity`, `AgentEntity` | `isResource()`, `isAgent()`, `isEmpty()`; default `isNotEmpty()` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.ResourceEntity`       | `SugarEntity` | `Sugar`, `NoResource` | default `isAgent()` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.AgentEntity`          | `SugarEntity` | `Agent`, `NoAgent` | default `isResource()` |
| `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity`          | `GridEntity` | `CreatureBase`, `TerrainConstant` | `isAgent()`, `isFish()`, `isShark()`, `isWater()` |

Notes:

- Etpets and Sugar use the same two-layer pattern (`isEmpty()`, default `isNotEmpty()`, layer interfaces that
  default the other layers' `is*()` methods to `false`); Etpets adds a terrain layer.
- `isAgent()` is declared in five roots (Etpets, Langton, Snake, Sugar, Wa-Tor) but not in `ReboundingEntity`, which
  uses `isRebounder()`.
- `isGround()` and `isWall()` were abstract in `ReboundingEntity` but default methods in `SnakeEntity` (see the
  decisions).
- `LangtonEntity` is the only entity root with a static method; the other simulations list their descriptors in their
  `EntityDescriptors` enum.
- Conway, Forest, and Lab have no entity interface; their entity enums implement `ConstantGridEntityDescriptorProvider`
  directly (category 2).

### Decisions

- API change: `SnakeEntity.isGround()`, `isWall()`, and `isFood()` are abstract like in `ReboundingEntity` and
  `WatorEntity`; `TerrainConstant` and `SnakeHead` implement them (identity comparison instead of `descriptorId()`
  string comparison).
- API change: `DESCRIPTOR_ID_ANT` and `DESCRIPTOR_ID_NO_AGENT` moved from `AntEntity` to `LangtonEntity`, so every
  root holds its descriptor ID constants; `AntEntity` is now a pure marker.
- Rule change: `simulations.md` (Entity Design): the `DESCRIPTOR_ID_*` constants of a simulation are declared in its
  root entity contract.
- `ReboundingEntity.isRebounder()` stays; it is the only agent type of that simulation.
- Javadoc of the layer interfaces of Etpets and Sugar says "Contract for ..." instead of "Marker contract for ...";
  `AgentEntity.id()` (Etpets) documents the default `0`.
- Member order in these interfaces: constants, static methods, own abstract methods, then default overrides
  (`TerrainEntity.isWalkable()` moved to the top).
- Added tests for the default methods: `EtpetsEntityTest`, `TerrainEntityTest`, `ResourceEntityTest`,
  `AgentEntityTest` (Etpets), `SugarEntityTest`, `ResourceEntityTest`, `AgentEntityTest` (Sugar), and the Snake
  predicates in `TerrainConstantTest`.
- `isEmpty()`/`isNotEmpty()` stay duplicated in `EtpetsEntity` and `SugarEntity`; the hierarchies share no ancestor
  besides `GridEntity`.

## 4. Functional engine step and grid logic

`@FunctionalInterface` types in `engine.executor` and `engine.support` that plug simulation logic into the engine.
`C` is the step context type.

| Interface                                                         | Abstract method | Default methods | Implementers |
|-------------------------------------------------------------------|-----------------|-----------------|--------------|
| `de.mkalb.etpetssim.engine.executor.AgentStepLogic`                 | `performAgentStep(GridCell, WritableGridModel, int, C)` | - | `ReboundingStepLogic` (C), `SnakeStepLogic` (C), `WatorStepLogic` (C) |
| `de.mkalb.etpetssim.engine.executor.SynchronousStepLogic`           | `performSynchronousStep(ReadableGridModel, WritableGridModel, int, C)` | - | `ConwayStepLogic` (C), `ForestStepLogic` (C) |
| `de.mkalb.etpetssim.engine.executor.SimulationStepRunner`           | `performStep(int, C)` | - | `AsynchronousStepRunner` (C), `SynchronousStepRunner` (C), `EtpetsStepRunner` (C), `LangtonStepRunner` (C), `SugarStepRunner` (C) |
| `de.mkalb.etpetssim.engine.executor.SimulationTerminationCondition` | `isFinished(GM, int, C)` | `and(..)`, `or(..)`, `negate()` | one `*TerminationCondition` class per simulation except Lab (8) |
| `de.mkalb.etpetssim.engine.support.GridInitializer`                 | `initialize(WritableGridModel)` | `andThen(..)` | lambdas only |
| `de.mkalb.etpetssim.engine.support.GridPattern`                     | `offsetMap()` | `size()`, `isEmpty()`, `width()`, `height()`, `shifted(..)`, `isTopLeftAtOrigin()`, `normalized()`, `flipX()`, `flipY()`, `rotate90()`, `rotate180()`, `rotate270()`, `mapValues(..)` | lambdas only (e.g., in `GridPatterns`) |

Notes:

- Two ways to provide a step: a `*StepLogic` wrapped by the engine's `AsynchronousStepRunner` or
  `SynchronousStepRunner`, or a simulation-specific `SimulationStepRunner` (Etpets, Langton, Sugar), which are the
  three simulations with a `CompositeGridModel`.
- `GridPattern` is a functional interface with 13 default methods; it works as a value type built from a lambda.

### Decisions

- Javadoc of all six interfaces follows the `<p>` rules of `java.md`; `GridInitializer.andThen` has a short `@return`.
- `SimulationTerminationCondition.and/or/negate` and the unused `GridPattern` defaults (`size`, `isEmpty`, `width`,
  `height`, `flipX`, `flipY`, `rotate90/180/270`, `mapValues`) stay in the API (general engine toolkit) and are now
  covered by tests: `SimulationTerminationConditionTest`, `GridInitializerTest`, `GridPatternTest`.
- `GridPattern` computes bounding-box limits through private static helpers (`minDx`, `maxDx`, `minDy`, `maxDy`)
  instead of repeating the stream expressions; behavior is unchanged.
- `AgentStepLogic`, `SynchronousStepLogic`, and `SimulationStepRunner` have a single abstract method and need no tests
  of their own.

## 5. Simulation model contracts

Contracts for config, manager, statistics, and execution lifecycle; mostly in `simulations.core.model`.

| Interface                                                                       | Annotations / type params | Members (own) | Implementers |
|---------------------------------------------------------------------------------|---------------------------|---------------|--------------|
| `de.mkalb.etpetssim.engine.executor.SimulationExecutor`                           | `ENT`, `GM` | `currentGridModel()`, `stepCount()`, `isFinished()`, `executeStep()`; defaults `isExecutorFinished()`, `executeSteps(int, boolean, Runnable)` | `DefaultSimulationExecutor` (C), `TimedSimulationExecutor` (C) |
| `de.mkalb.etpetssim.simulations.core.model.SimulationManager`                     | `ENT`, `GM`, `CON`, `STA` | `config()`, `structure()`, `statistics()`, `executeStep()`, `executeSteps(..)`, `isFinished()`, `isExecutorFinished()`, `stepCount()`, `currentGridModel()`; defaults `statisticsHistory()`, `statisticsExtrema()` | `AbstractTimedSimulationManager` (C), `LabSimulationManager` (C) |
| `de.mkalb.etpetssim.simulations.core.model.SimulationConfig`                      | - | `cellShape()`, `gridEdgeBehavior()`, `gridWidth()`, `gridHeight()`, `cellEdgeLength()`, `cellDisplayMode()`, `seed()`, `neighborhoodMode()`; 11 defaults (`create*()`, `cellCount()`, `isInRange*()`, `isAllowedSelection(..)`, `hasAllowedCoreSelections(..)`, `hasExpectedSelection(..)`, `isBaseValid()`, `isValid()`) | one `*Config` record per simulation (9) |
| `de.mkalb.etpetssim.simulations.core.model.SimulationStatistics`                  | - | `stepCount()`, `gridStructure()`; default `totalCells()` | `TimedSimulationStatistics` (I), `LabStatistics` (C) |
| `de.mkalb.etpetssim.simulations.core.model.TimedSimulationStatistics`             | - | `stepTimingStatistics()` | `BaseTimedSimulationStatistics` (C) |
| `de.mkalb.etpetssim.simulations.core.model.SimulationUserAction`                  | `@FunctionalInterface`; `ENT`, `GM`, `CON`, `STA`, `SM`, `CTX` | `apply(SM, CTX, GridCellView)` | `NoUserAction` (C) and one `*UserAction` class per simulation except Lab (8) |
| `de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation`  | `@FunctionalInterface` | `checkCanceled()`; static `none()`, `interruptionAware()` | lambdas only |
| `de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle`                 | - | `awaitTermination(long, TimeUnit)`, `shutdownNow()`; static `completed()` | `SimulationTerminationHandle.CompletedSimulationTerminationHandle` (E), `DefaultMainViewModel.ExecutorSimulationTerminationHandle` (R) |

Notes:

- `SimulationManager` repeats six methods of `SimulationExecutor` (`currentGridModel()`, `stepCount()`,
  `isFinished()`, `isExecutorFinished()`, `executeStep()`, `executeSteps(..)`) without extending it;
  `isExecutorFinished()` is abstract in the manager but a default method in the executor.
- `SimulationExecutor` lives in `engine.executor`, the other contracts in `simulations.core`; it is listed here because
  it shares the lifecycle with `SimulationManager`.
- Lab is the exception in this category: `LabSimulationManager` implements `SimulationManager` directly, and
  `LabStatistics` implements `SimulationStatistics` instead of `TimedSimulationStatistics`.
- `SimulationTerminationHandle` is in `simulations.core`, not in `simulations.core.model` like the other contracts.

### Decisions

- Javadoc of `SimulationExecutor` and `SimulationConfig` follows the `<p>` rules of `java.md`; `SimulationManager` and
  `TimedSimulationStatistics` end with a newline.
- `SimulationManager` does not extend `SimulationExecutor`: a manager is not an executor, it only delegates to one.
  The six methods with the same signature (`currentGridModel()`, `stepCount()`, `isFinished()`,
  `isExecutorFinished()`, `executeStep()`, `executeSteps(..)`) stay declared in both interfaces.
- The stateless helpers of `SimulationConfig` (`isInRange*`, `isAllowedSelection`, `hasExpectedSelection`,
  `hasAllowedCoreSelections`) stay default methods, because implementing records call them unqualified.
- `SimulationTerminationHandle` stays in `simulations.core` next to the other lifecycle types (`SimulationInstance`,
  `SimulationFactory`, `SimulationType`).
- Added `SimulationExecutorTest` (default `isExecutorFinished()` and `executeSteps(..)`: count, zero count, early
  termination, ignored termination, technical limit, interruption before a step and from the callback) and
  `SimulationStatisticsTest` (`totalCells()`).
- `SimulationUserAction`, `TimedSimulationStatistics`, and the other contracts without default logic need no tests of
  their own.

## 6. MVVM view and view model contracts

The four view parts (config, control, main, observation) as view and view model interfaces, plus the drawing callbacks
of the main views.

| Interface                                                                      | Members | Implementers |
|--------------------------------------------------------------------------------|---------|--------------|
| `de.mkalb.etpetssim.simulations.core.view.SimulationConfigView`                  | `buildConfigRegion()` | `AbstractConfigView` (C) |
| `de.mkalb.etpetssim.simulations.core.view.SimulationControlView`                 | `buildControlRegion()`, `setStepCount(int)` | `AbstractControlView` (C), `StartMainView` (C) |
| `de.mkalb.etpetssim.simulations.core.view.SimulationMainView`                    | `buildMainRegion()`, `shutdownSimulation()` | `AbstractMainView` (C), `StartMainView` (C) |
| `de.mkalb.etpetssim.simulations.core.view.SimulationObservationView`             | `buildObservationRegion()`, `initializeObservationLabels()`, `shutdownObservation()` | `AbstractObservationView` (C) |
| `de.mkalb.etpetssim.simulations.core.viewmodel.SimulationConfigViewModel`        | `simulationStateProperty()`, `getSimulationState()`, `createConfig()` | `AbstractConfigViewModel` (C) |
| `de.mkalb.etpetssim.simulations.core.viewmodel.SimulationControlViewModel`       | `simulationStateProperty()`, `getSimulationState()` | `DefaultControlViewModel` (C), `LabControlViewModel` (C) |
| `de.mkalb.etpetssim.simulations.core.viewmodel.SimulationMainViewModel`          | `simulationStateProperty()`, `getSimulationState()`, `notificationTypeProperty()`, `setNotificationType(..)`, `getStructure()`, `getCellEdgeLength()`, `shutdownSimulation()` | `AbstractMainViewModel` (C) |
| `de.mkalb.etpetssim.simulations.core.viewmodel.SimulationObservationViewModel`   | `simulationStateProperty()`, `getSimulationState()`, `statisticsProperty()`, `getStatistics()`, `setStatistics(..)`, `lastClickedCoordinateProperty()`, `statisticsExtremaProperty()`, `statisticsHistoryProperty()` | `DefaultObservationViewModel` (C) |
| `de.mkalb.etpetssim.simulations.core.view.CellDrawer`                            | `@FunctionalInterface`: `draw(GridEntityDescriptor, FXGridCanvasPainter, GridCell, int)` | lambdas only (fields in the main views of Etpets, Forest, Langton, Rebounding, Sugar, Wa-Tor) |
| `de.mkalb.etpetssim.simulations.core.view.CoordinateDrawer`                      | `@FunctionalInterface`: `draw(FXGridCanvasPainter, GridCoordinate, int)` | lambdas only (field in `ConwayMainView`) |

Notes:

- All four view model interfaces declare `simulationStateProperty()` and `getSimulationState()` without a common super
  interface.
- `shutdownSimulation()` returning `SimulationTerminationHandle` is declared in both `SimulationMainView` and
  `SimulationMainViewModel`.
- Getter naming is mixed: view models use `get*()` (`getStructure()`, `getCellEdgeLength()`), model contracts use
  record-style accessors (`structure()` in `SimulationManager`, category 5).
- `SimulationConfigView` has a single abstract method and suppresses `InterfaceMayBeAnnotatedFunctional`;
  `CellDrawer` and `CoordinateDrawer` are annotated `@FunctionalInterface`.
- `StartMainView` implements both `SimulationMainView` and `SimulationControlView`; every other implementer covers one
  part.

### Decisions

- Javadoc of the view and view model contracts follows the `<p>` rules of `java.md`;
  `SimulationMainView.shutdownSimulation()` documents idempotence and no waiting for background work like
  `SimulationMainViewModel`, and `setStatistics(..)` is documented.
- The state accessors `simulationStateProperty()` and `getSimulationState()` stay undocumented (the names are enough).
- The four view model interfaces get no common super interface for the state accessors; no caller treats them
  uniformly.
- None of the interfaces has default or static methods; they need no tests of their own.
- The `@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")` of `SimulationConfigView` stays (see
  "Cross-category decisions").

## 7. User action contexts

`SimulationUserActionContext` is the marker root in `simulations.core.shared`; six simulations define a sealed marker
sub-interface in their `shared` package.

| Interface                                                                       | Modifiers | Permits |
|---------------------------------------------------------------------------------|-----------|---------|
| `de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext`          |           | open; implemented by the six interfaces below, `NoUserActionContext` (E), and `WatorUserActionContext` (E) |
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext`            | `sealed`  | `FixedAction` (E), `PlacePattern` (R) |
| `de.mkalb.etpetssim.simulations.etpets.shared.EtpetsUserActionContext`            | `sealed`  | `SetTerrain` (R), `SetResource` (R) |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext`          | `sealed`  | `FixedAction` (E), `AddAnt` (R) |
| `de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext`    | `sealed`  | `FixedAction` (E), `AddRebounder` (R) |
| `de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext`              | `sealed`  | `FixedAction` (E), `AddSnake` (R) |
| `de.mkalb.etpetssim.simulations.sugar.shared.SugarUserActionContext`              | `sealed`  | `FixedAction` (E), `AddSugar` (R) |

Notes:

- All seven interfaces declare no members and carry `@SuppressWarnings("MarkerInterface")`. The permitted types are
  nested in the interface.
- Pattern "nested `FixedAction` enum + one parameterized record" in five simulations; Etpets has two records and no
  `FixedAction`; Wa-Tor uses a plain enum (`WatorUserActionContext`) instead of a sealed interface, as `simulations.md`
  prescribes for contexts without parameterized actions.
- Forest and Lab have no user action context of their own; Forest uses `NoUserActionContext`.

### Decisions

- Javadoc of the context types reads "Identifies which <Simulation> edit action should be applied." (also for
  `WatorUserActionContext`); every nested `FixedAction` says "Fixed actions that do not require additional
  parameters."
- Rule change: `simulations.md` (and its copy in `.github/instructions`) requires a nested `FixedAction` enum only if
  actions without parameters exist.
- `EtpetsUserActionContextTest.testPermittedSubclasses` is removed (it only restated the `permits` clause); the
  record tests keep their `test<Record>Record` names. Superseded by `code-record-types.md`, category 6: the records
  are pure data carriers, so their record tests and the six `*UserActionContextTest` classes are removed.
- The marker interfaces declare no methods and need no tests of their own.

## 8. Rule presets and move strategies

Simulation-specific strategy and preset contracts.

| Interface                                                                   | Modifiers | Members | Implementers |
|-----------------------------------------------------------------------------|-----------|---------|--------------|
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePreset`               | `sealed`  | `ruleString()`, `displayName()`, `recommendedDensityPercent()` | `ConwayRulePresetHexagon`, `ConwayRulePresetSquare`, `ConwayRulePresetTriangle` (E) |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePreset`             | `sealed`  | `ruleString()`, `displayName()` | `LangtonRulePresetHexagon`, `LangtonRulePresetSquare`, `LangtonRulePresetTriangle` (E) |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy`       | `sealed`  | `decideMove(MoveContext)`, `displayName()` | `AnonymousMoveStrategy` (I), `NamedMoveStrategy` (R) |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.AnonymousMoveStrategy`   | package-private, `non-sealed`, `@FunctionalInterface` | default `displayName()` | lambdas only (wrapped by `NamedMoveStrategy`) |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.MoveDecision`            |           | `targetCoordinate()`, `direction()`, `isFoodTarget()` | `SnakeMoveStrategies.ScoredMove` (private R) |

Notes:

- Both rule preset interfaces expose the rule string through `ruleString()`; `toString()` is no longer redeclared.
  Only Conway has `recommendedDensityPercent()`.
- `MoveDecision` has a single implementation, a private record; `NamedMoveStrategy` is a package-private record.

### Decisions

- API change: `SnakeMoveStrategy.name()` is renamed to `displayName()` (like the rule presets); the `NamedMoveStrategy`
  component is `displayName`, and `SnakeObservationView` and `SnakeEditToolBarViewModel` use `displayName()` instead of
  `name()` and `toString()`.
- API change: `SnakeMoveStrategy` is `sealed` and permits `AnonymousMoveStrategy` (now `non-sealed`, so lambdas stay
  possible) and `NamedMoveStrategy`.
- `MoveDecision` stays an interface to allow other strategies with their own decision types later.
- `AnonymousMoveStrategy` documents that its `displayName()` default only keeps the interface functional; added
  `AnonymousMoveStrategyTest`.
- `ConwayRulePresetTest.testRuleStringRespectsMaxNeighborCount` checks the neighbor limit per cell shape (hexagon 6,
  square 8, triangle 9 because of the single-digit notation).
- Javadoc and style: `ConwayRulePreset.displayName()` documents the `EMPTY` case like `LangtonRulePreset`; en dashes
  replaced by ASCII hyphens in the Conway presets; trailing blank lines removed from the Langton preset enums.
- `ConwayRulePreset`, `LangtonRulePreset`, and `MoveDecision` have no default or static methods; the preset tests are
  the contract tests over `PresetFamily`.

## Cross-category decisions

- (5, 8) `displayName()` is the name for the human-readable label of presets and strategies; entities use
  `toDisplayString()`.
- (3, 4, all) Rule change: `java.md` (Code Style): member order in interfaces is constants, static methods (including
  private static helpers), abstract methods, then default methods. The rule applies strictly, also to overloads and
  related methods: the defaults of `ReadableGridModel`, `WritableGridModel`, `SimulationExecutor`, and
  `SimulationManager` moved to the end; test methods keep the tests of overloads together, as `junit.md` requires.
- (all) Rule change: `junit.md` (Common Contract Patterns): the default and static methods of an interface are tested
  in `<Interface>Test` or in the contract test of a sub-interface that covers the whole hierarchy (e.g.,
  `WritableGridModelTest` for `GridModel`, `ReadableGridModel`, and `WritableGridModel`); interfaces without default
  or static methods need no test of their own.
- (2, all) The `@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")` and `"MarkerInterface"` suppressions stay on
  the individual interfaces; the inspections are not changed project-wide.

## Deferred findings

None open. The findings moved to `code-record-types.md`, `code-private-constructor-classes.md`, and
`code-other-classes.md`; the Javadoc of `GridEntityDescriptorRegistry` was fixed directly.

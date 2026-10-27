# Other Classes Overview

**Status:** idea

**Related issues:** #23

Temporary working document for the classes that no sibling plan covers: instantiable classes, including abstract base
classes, that are neither records, enums, nor classes with a private constructor. So far it only collects findings
from the sibling plans; the inventory, the categories, and the workflow follow when the work on this plan starts. The
maintainer deletes it after all findings are done and pushed.

## Order

- The sibling plans `code-interface-types.md`, `code-record-types.md`, `code-private-constructor-classes.md`, and
  `code-enum-types.md` are done; their decisions apply here. They stay in `docs/planning` as reference until this
  plan is done and are then deleted together with it.

## Deferred findings

### Tests for the executor classes and timed managers

- Origin: `code-interface-types.md`, category 5
- Types: `DefaultSimulationExecutor`, `TimedSimulationExecutor`, `AbstractTimedSimulationManager`
- Problem: The executor classes have no tests of their own; `SimulationManagerTest` mainly tests
  `AbstractTimedSimulationManager` through the simulation managers. The default methods of `SimulationExecutor` are
  tested in `SimulationExecutorTest`.
- Proposal: Add `DefaultSimulationExecutorTest` and `TimedSimulationExecutorTest`, and rename or split
  `SimulationManagerTest` (e.g., into `AbstractTimedSimulationManagerTest`).

### Constructor parameters of the step runners

- Origin: `code-private-constructor-classes.md`, category 6
- Types: `EtpetsStepRunner`, `SugarStepRunner`, `LangtonStepRunner`, and their callers `EtpetsSimulationManager`,
  `SugarSimulationManager`, and `LangtonSimulationManager`
- Problem: The constructors take `(random, model, idSequence)`, `(config, random, model)`, and `(config, model)`,
  while the engine runners `AsynchronousStepRunner` and `SynchronousStepRunner` take the grid model first, and the
  static step logic orders `apply(..)` as grid model, config, further inputs (`simulations.md`, "Naming").
- Proposal: Order the constructor parameters of all step runners as grid model, config, further inputs; decide
  whether `simulations.md` states this order for step runners too. An API change with one caller each.

### Metrics catalogs of the statistics classes

- Origin: `code-private-constructor-classes.md`, cross-category decisions (catalog rule in `simulations.md`,
  "Model")
- Types: `ConwayStatistics`, `EtpetsStatistics`, `ForestStatistics`, `LangtonStatistics`, `ReboundingStatistics`,
  `SnakeStatistics`, `SugarStatistics`, `WatorStatistics`
- Problem: Each class exposes its metrics catalog through the static noun-style accessor `metrics()`, but builds a new
  `List.of(..)` on every call instead of returning a constant unmodifiable list, as the catalog rule requires
  (`ConwayPatterns.choices()`, `SnakeMoveStrategies.strategies()`). Callers are the simulation managers, the
  observation views, and several tests in `simulations.core`.
- Proposal: Store the list in a private static final constant and return it from `metrics()`; decide whether the
  catalog rule applies to static accessors of instantiable classes and, if so, clarify it in `simulations.md`.

### Component names of CommonConfigSettings

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

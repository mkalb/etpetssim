# User Action Edit Mode Design

This document is the compact closeout for the completed Version 1 and Version 2 edit-mode work.
Both versions are fully implemented. Keep this file short so future agent sessions spend tokens on current work, not
historical migration detail.

## Completed Behavior

- Version 1 added explicit edit mode above the canvas. Canvas clicks still inspect/select first; model mutations require
  edit mode plus a selected action tool. Leaving `PAUSED` resets edit mode to `Select`. Simulations without descriptors
  show no edit row.
- Version 2 added parameterized edit tools without changing `SimulationUserAction.apply(...)`: Snake has `Add snake`
  with a strategy option, and Conway has `Place pattern` with a config-aware pattern option.
- Fixed-tool simulations remain on fixed descriptors unless they need parameterized tools later.

## Architecture Facts

- `DefaultMainViewModel` owns simulation lifecycle, selected cell state, manager access, statistics refresh, and final
  model-side action application.
- Shared edit-toolbar state lives outside `DefaultMainViewModel`: edit-mode active state, selected stable tool id, and
  reset-to-`Select` behavior are toolbar state.
- `SimulationUserActionDescriptor` is metadata plus context resolution only: stable tool id, scope, label key, tooltip
  key, and resolver. It must not own JavaFX nodes or generic form-control metadata.
- Toolbar selection and enablement use stable tool ids, never descriptor equality or localized text.
- Descriptor resolvers return `Optional<CTX>`. `Optional.empty()` means no valid context, so the action is not applied
  and the view does not redraw for that click/action.
- A `true` return from the shared apply path means a context resolved and `SimulationUserAction.apply(...)` was invoked;
  it does not mean the model changed.
- Simulation-specific option controls live in simulation Views and bind to simulation-specific edit ViewModels.
- Action validity remains model-side. Do not add toolbar-side per-cell applicability checks.

## Simulation Notes

- Snake `Add snake` carries a selected `SnakeMoveStrategy`, applies only to ground cells, uses a monotonic
  `nextSnakeId`, and updates Snake statistics when a snake is actually added.
- Conway `Place pattern` carries a stable `ConwayPatternChoice`. Pattern selection uses stable choice ids, while display
  uses localized label keys.
- Conway pattern availability is config-aware. Classic patterns require square `23/3`; HighLife replicator requires
  square `23/36`.
- Conway pattern placement is model-side and all-or-nothing: validate the full normalized footprint first, do not use
  grid-edge wrapping, overwrite all footprint cells including `DEAD`, and update statistics from the pre-write
  footprint.

## Verification

- Build acceptance command: `.\gradlew.bat compileJava`.
- Focused unit tests exist for behavior with meaningful regression risk, especially Conway pattern catalog,
  availability, and placement invariants.
- Manual smoke checks should cover paused edit tools for Conway, Forest, Langton, Rebounding, Snake, and Wa-Tor after
  shared toolbar changes.

## Version 3 Boundary

Keep these out of Version 2 follow-up work unless a future Version 3 explicitly introduces them:

- action-result reporting or changed/unchanged action results
- per-cell applicability checks in the toolbar
- undo or edit history
- drag painting
- preview behavior
- keyboard shortcuts
- icon-system work

## Completed Follow-up Decisions: Using User Actions in Existing Simulations

These notes capture the completed and explicitly closed Version 2 adoption decisions discussed after the descriptor,
scope, and parameter-context work was completed. There are no open user-action additions left in this plan. They
intentionally avoid Version 3 topics from the boundary above.

General direction:

- Prefer user actions that support runtime experimentation on the current simulation state.
- Do not add actions that merely duplicate starting a new simulation with the same or adjusted configuration.
- Use at least one concrete `GLOBAL` action to exercise the infrastructure in a real simulation. Conway's `Clear grid`
  is the best first candidate because it is simple, selection-independent, and complements pattern placement.
- Most other useful additions are still `CELL_SELECTED` actions, often with simulation-specific parameters.

| Simulation            | Keep / current stance                                                                     | Completed / decided additions                                                                                                                  | Not now / rationale                                                                                                                                                                                 | Erledigt                                                                                                           |
|-----------------------|-------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| Conway's Game of Life | Keep `Toggle cell` and parameterized `Place pattern`.                                     | Add `Clear grid` as a `GLOBAL` action. It provides a quick empty workspace for manual pattern experiments without selecting a cell.            | Do not add `Set cell state` or `Randomize grid` yet. Random initialization is covered by configuration plus restart.                                                                                | ✅ `Clear grid`                                                                                                     |
| Forest-fire model     | Keep `Cycle state`.                                                                       | None for now.                                                                                                                                  | Do not add parameterized `Set state` or global forest reinitialization yet. The current cycle tool is sufficient, and initial tree density is already configurable.                                 | —                                                                                                                  |
| Wa-Tor                | Keep `Add fish` and `Add shark`.                                                          | Add `Remove creature` for the selected fish or shark cell.                                                                                     | Do not add global population reset or random seeding actions; fish and shark percentages are configuration concerns.                                                                                | ✅ `Remove creature`                                                                                                |
| Langton's Ant         | Keep `Add ant`. For triangle grids, derive the initial direction from the selected cell.  | Add `Remove ant` for the selected ant cell.                                                                                                    | Do not add `Reset visited cells` or global reinitialization yet. Restarting already covers clean-state experiments.                                                                                 | ✅ `Add ant` (direction option for square/hexagon, automatic `N`/`S` for triangle), ✅ `Remove ant`                  |
| Snake                 | Keep `Add wall`, `Remove wall`, `Add food`, `Remove food`, and parameterized `Add snake`. | Add `Remove snake` for a selected snake head or segment.                                                                                       | Do not replace the existing terrain tools with a generic `Set cell content` tool yet, and do not add global clear/reinitialize tools.                                                               | ✅ `Remove snake`                                                                                                   |
| Rebounding Entities   | Keep `Add wall`.                                                                          | Add `Remove wall`, parameterized `Add rebounder` with direction, `Remove rebounder`, and `Fill walls` as a `GLOBAL` action.                    | Do not add moving-entity reinitialization; initial moving entity percentage is a configuration concern.                                                                                             | ✅ `Add rebounder` (parameterized direction), ✅ `Remove wall`, ✅ `Remove rebounder`, ✅ `Fill walls`                 |
| Sugarscape            | Keep parameterized `Add sugar` and `Remove sugar`.                                        | Add `Add sugar` with practical levels (`empty`, `low`, `medium`, `high`) and `Remove sugar`.                                                   | `Add agent`, `Remove agent`, and `Set sugar` are explicitly closed as not implemented. Avoid free numeric sugar editing; staged values are easier to expose in the toolbar and keep the UI compact. | ✅ `Add sugar` (parameterized), ✅ `Remove sugar`, ❌ `Add agent`, `Remove agent`, `Set sugar` (wird nicht umgesetzt) |
| ET Pets               | Keep parameterized `Set terrain` and `Set resource`.                                      | Add layer-focused tools: parameterized `Set terrain` (`Ground`, `Rock`, `Water`) and parameterized `Set resource` (`None`, `Plant`, `Insect`). | Do not add `Add pet` or `Add pet egg` yet. Pets and eggs require IDs, traits/genomes, energy, age, cooldown, and parent metadata, so they need a more deliberate design.                            | ✅ `Set terrain`, ✅ `Set resource`                                                                                  |
| Simulation Lab        | Treat as a special development tool.                                                      | None.                                                                                                                                          | Do not add simulation user actions. Existing click behavior already drives inspection and neighborhood highlighting.                                                                                | —                                                                                                                  |

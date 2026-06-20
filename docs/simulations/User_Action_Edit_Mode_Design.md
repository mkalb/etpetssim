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

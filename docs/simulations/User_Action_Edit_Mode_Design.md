# User Action Edit Mode Design

This document summarizes the implemented Version 1 edit-mode design and records the planned Version 2 follow-up for
simulation user actions in etpetssim.

Scope of this document: architecture and UX guidance only. No Java code, tests, resources, or build files are changed by
this document.

## Version 1 Summary (Implemented)

Version 1 has already been implemented. It replaced direct select-then-button editing with an explicit edit mode above
the canvas while preserving normal cell selection as the default inspection behavior.

Implemented intent:

- Canvas clicks select cells by default and update the observation view.
- Grid mutations require edit mode plus a selected cell action tool.
- Entering edit mode shows a compact toolbar with `Select` and simulation-provided cell tools.
- Choosing a tool does not mutate immediately; the next canvas click selects the cell first and then applies the tool.
- Leaving `PAUSED` exits edit mode and resets tool selection to `Select`.
- Simulations without descriptors show no edit row and keep their canvas space.

Main introduced contracts and hooks:

- `SimulationUserActionScope` in `de.mkalb.etpetssim.simulations.core.shared` with `CELL_SELECTED` and `GLOBAL`.
- `SimulationUserActionDescriptor` in `de.mkalb.etpetssim.simulations.core.viewmodel` with fixed `context`, `scope`,
  `labelKey`, and `tooltipKey`.
- `AbstractMainView.createUserActionDescriptors()` for simulation-specific edit tool descriptors.
- Descriptor-based edit-toolbar creation in `AbstractMainView`.
- Edit-mode and selected-descriptor state in `DefaultMainViewModel`.
- Click-to-apply flow in `AbstractDefaultMainView`, delegated to `DefaultMainViewModel.applySelectedCellUserAction()`.

Important Version 1 constraints that still apply unless a later version explicitly changes them:

- `SimulationUserAction.apply(...)` still returns `void`.
- Redraw after an attempted action is acceptable, even if the action is a no-op.
- Action validity remains model-side; there are no per-cell applicability checks in the toolbar.
- There is no undo, drag painting, icon system, or action-result reporting.
- User-facing labels and tooltips remain localized through resource-bundle keys.

## Version 2 Plan

Version 2 is a planned follow-up and must stay separate from the implemented Version 1 changes. Version 1 keeps fixed
cell-action tools. Version 2 extends the same interaction model with parameterized edit tools.

### Problem

The Version 1 descriptor model assumes that selecting a tool is enough to produce a complete
`SimulationUserActionContext`. This is too limited for actions whose behavior depends on an additional user choice.

Known examples:

- Snake needs an `Add snake` tool that also needs a selected `SnakeMoveStrategy`.
- Conway needs a `Place pattern` tool that also needs a selected `GridPattern` whose availability depends on the active
  Conway configuration.

These should not be represented as many separate toggle buttons. `Add snake` should be one tool with a strategy option,
and `Place pattern` should be one tool with a pattern option.

### Architecture

Introduce a dedicated edit-toolbar ViewModel and a simulation-specific option-panel hook. The goal is to keep
`DefaultMainViewModel` focused on simulation lifecycle, cell selection, and action application while moving
tool-specific UI state into edit-focused components.

Recommended ownership:

- `DefaultMainViewModel` keeps ownership of the active simulation manager, selection, and the final call to
  `SimulationUserAction.apply(...)`.
- A shared edit-toolbar ViewModel owns edit-mode state, selected tool state, and context resolution for the currently
  selected tool.
- Simulation-specific edit ViewModels own option state such as the selected Snake strategy or selected Conway pattern.
- Simulation-specific edit Views create option controls such as ComboBoxes and bind them to the simulation-specific edit
  ViewModel.
- `SimulationUserActionDescriptor` remains metadata and context-resolution information. It must not own JavaFX nodes or
  become a generic UI control schema.

The shared toolbar should provide the common edit affordance, the `Select` tool, selected-tool handling, enablement
rules, and click-to-apply flow. Simulations can contribute a small option panel for controls that are specific to their
edit tools.

### Descriptor And Context Resolution

Keep `SimulationUserAction.apply(...)` unchanged for Version 2. Parameter values should be folded into the resolved
action context before the action is applied.

Recommended descriptor direction:

- Replace or supplement the fixed `context` value with a resolver-style concept that can produce the current `CTX` at
  apply time.
- Fixed tools can use a resolver that always returns the same enum or context value.
- Parameterized tools can use a resolver that combines the selected tool with current option state from the edit-toolbar
  ViewModel.
- Add a stable tool id if selected tools must be matched to option controls. Do not use localized labels for logic.

Recommended action-context direction:

- Simple actions may remain enum constants.
- Parameterized actions should use richer context values, such as records implementing the simulation-specific
  `SimulationUserActionContext` contract.
- Snake can use an `AddSnake` context carrying the selected `SnakeMoveStrategy`.
- Conway can use a `PlacePattern` context carrying the selected pattern value.

This keeps model actions independent of JavaFX controls while allowing the existing `apply(manager, context,
selectedCell)` contract to remain stable.

### Toolbar Behavior

Keep the Version 1 inspect-first behavior:

1. Canvas clicks still update selection first.
2. Observation views still show the selected cell.
3. An edit action is applied only when edit mode is active and a non-`Select` tool is selected.

For parameter controls:

- Option controls are visible whenever edit mode is expanded, so the toolbar layout does not jump when switching tools.
- Option controls are disabled unless their associated tool is selected.
- The default selected tool after entering edit mode remains `Select`.
- Tool option values may stay selected while edit mode remains open, enabling repeated edits with the same parameter.
- Leaving `PAUSED` still exits edit mode and returns to inspect/select behavior.

Example behavior:

- Snake shows the strategy ComboBox while edit mode is expanded, but it is enabled only when `Add snake` is selected.
- Conway shows the pattern ComboBox while edit mode is expanded, but it is enabled only when `Place pattern` is selected
  and at least one pattern is available for the active simulation config.

### Snake Behavior

Add a new Snake cell action for creating a snake at the selected cell.

Expected behavior:

- Expanded edit toolbar contains the existing Snake tools plus `Add snake`.
- A strategy ComboBox is visible in the expanded edit toolbar.
- The strategy ComboBox is disabled unless `Add snake` is the selected tool.
- The selected strategy is used for every canvas click while `Add snake` remains selected.
- `Add snake` applies only to ground cells. Walls, food, snake heads, and snake segments remain no-ops.
- The action context carries the selected strategy; it does not carry a snake id.
- Snake ids are assigned by the model or simulation manager at apply time using the next unique id.
- Adding a snake must update Snake statistics consistently, including total snake-head cells and living snake-head cells.

### Conway Behavior

Add a Conway cell action for placing a predefined pattern at the selected cell.

Expected behavior:

- Expanded edit toolbar contains the existing `Toggle cell` tool plus `Place pattern`.
- A pattern ComboBox is visible in the expanded edit toolbar.
- The pattern ComboBox is disabled unless `Place pattern` is selected and at least one pattern is available.
- Available pattern choices are derived from the active `ConwayConfig`, not from editable config controls.
- Pattern choices are recomputed when a Conway simulation is initialized or restarted with a new config.
- The selected cell is the top-left origin of the normalized `GridPattern`.
- Pattern placement does not use grid-edge wrap behavior.
- Pattern placement is all-or-nothing: if any pattern coordinate would fall outside the grid, the action is a no-op.
- Placing a pattern overwrites the full pattern footprint, including `DEAD` cells contained in the pattern.

### Migration Notes

Implement Version 2 only after the implemented Version 1 behavior is stable.

Recommended order:

1. Extract shared edit-toolbar state from `DefaultMainViewModel` into a dedicated edit-toolbar ViewModel without
   changing behavior.
2. Add descriptor context resolution while preserving fixed-context behavior for existing tools.
3. Add the simulation option-panel extension point.
4. Implement Snake `Add snake` with a strategy ComboBox.
5. Implement Conway `Place pattern` with a config-derived pattern ComboBox.
6. Keep other simulations on fixed tools until they need parameterized edit behavior.

Do not add drag painting, undo, per-cell applicability checks, or changed/unchanged action results as part of Version 2.

## Other Future Improvements

- Drag painting for repeated cell edits.
- Confirmation for destructive global actions.
- Keyboard shortcuts for edit tools.
- Icons for common tools after an icon strategy exists.
- Undo or edit history only if future workflows require it.

## Version 3 Note

Version 3 can revisit action results and applicability. A future action API may return whether the model actually
changed, expose optional per-cell applicability checks, and use those results for disabled tools, no-op feedback,
statistics refresh decisions, and more precise redraw behavior.

# User Action Edit Mode Design

This document records the planned design for improving simulation user actions in etpetssim.

Scope of this document: architecture and UX guidance only. No Java code, tests, resources, or build files are changed by
this document.

## Problem

Simulation user actions currently follow this interaction pattern:

1. The user pauses the simulation.
2. The user selects a grid cell on the canvas.
3. The selected cell is exposed through `viewModel.selectedGridCellProperty()`.
4. The user presses an action button in the simulation toolbar.
5. `SimulationUserAction.apply(...)` receives the active manager, action context, and selected cell.

This works for occasional edits, but it is awkward when the user wants to perform the same action repeatedly, for
example adding several walls or toggling several cells. Each change requires selecting a cell and then pressing the
toolbar button again.

At the same time, selection is not only an edit target. It is also the normal inspection mechanism used by the
ObservationView. Selection must therefore remain inspect-first by default and must not automatically mutate simulation
state.

## Goals

- Preserve cell selection as the default inspection behavior.
- Require explicit user intent before clicks mutate the grid.
- Make repeated cell edits faster than the current select-then-button flow.
- Keep `SimulationUserAction` implementations usable without a large model-layer redesign.
- Keep run controls and edit controls conceptually separate.
- Preserve canvas space for simulations without user actions.
- Allow gradual migration without breaking all simulations in one change.

## Non-Goals

- No undo system.
- No drag painting in the first version.
- No per-cell action validity checks in the first version.
- No icon system in the first version.
- No automatic switch into edit mode.
- No change to `SimulationUserAction.apply(...)` return type in the first version.

## Current Code Shape

Relevant current contracts:

| Type                                                    | Current role                                                                                                |
|---------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `SimulationUserAction`                                  | Model-side action contract. Applies a context to the current simulation manager and optional selected cell. |
| `SimulationUserActionContext`                           | Layer-neutral marker for simulation-specific action context values.                                         |
| `GridCell` / `GridCellView`                             | Selected cell value passed to actions.                                                                      |
| `DefaultMainViewModel.applyUserAction(...)`             | Applies configured user action only while simulation state is `PAUSED`.                                     |
| `AbstractDefaultMainView.applyUserActionAndRedraw(...)` | Delegates to ViewModel action application and redraws after attempted action.                               |
| `AbstractMainView.createActionToolBarNodes()`           | Current hook used by simulations to create toolbar buttons.                                                 |
| `DefaultControlView`                                    | Bottom run-control area for start, pause, resume, cancel, mode, speed/count, and step display.              |

Current examples:

- Conway: one selected-cell action, toggle selected cell.
- Snake: several selected-cell actions, add/remove wall and add/remove food.
- Wator: several selected-cell actions, add fish or add shark.
- Some simulations have no meaningful user actions and should not show edit controls.

## UX Decision

Use an explicit edit mode above the canvas in the simulation main view.

Default behavior:

- Selection remains inspect-first.
- Canvas clicks select cells and update the ObservationView.
- No action is applied unless edit mode is active and a cell action tool is selected.

Edit behavior:

- A compact `Edit` affordance is shown above the canvas for simulations that provide user action descriptors.
- The `Edit` affordance is always visible for those simulations, but disabled while the simulation is running.
- The full edit toolbar is shown only while edit mode is active.
- Leaving `PAUSED` automatically exits edit mode.
- Returning to `PAUSED` starts in inspect/select mode again.
- Choosing an action tool never mutates immediately.
- A canvas click first updates selection, then applies the selected cell action to that selected cell.

Recommended labels:

| UI element         | Label    | Tooltip                              |
|--------------------|----------|--------------------------------------|
| Compact affordance | `Edit`   | `Show editing tools`                 |
| Default tool       | `Select` | `Select cells without changing them` |

Controls are text-only in the first version.

## Toolbar Placement

Place edit controls in `SimulationMainView`, above the canvas, where the current simulation toolbar is displayed.

Do not place edit controls in `SimulationControlView`.

Rationale:

- Edit tools affect canvas/grid interaction.
- The bottom `SimulationControlView` is for simulation execution and timing controls.
- Keeping edit controls near the canvas keeps the mental model clean.
- Existing simulation `MainView` classes already own action toolbar definitions.
- Existing action application flow already lives in the main view and main ViewModel path.

Trade-off:

- Expanding edit tools can reduce visible canvas height.
- This layout shift is acceptable because it happens only after explicit user action.
- Simulations without action descriptors should show no edit row at all, preserving canvas space.

## Action Scopes

User actions need explicit scope metadata.

Recommended descriptor scopes:

| Scope           | Meaning                                                                       | Example                            |
|-----------------|-------------------------------------------------------------------------------|------------------------------------|
| `CELL_SELECTED` | Requires a selected grid cell. Applies to the selected cell.                  | Toggle cell, add wall, remove food |
| `GLOBAL`        | Does not require a selected grid cell. Applies to the whole simulation state. | Clear all, fill all                |

`CELL_SELECTED` actions can be represented as toggle tools inside edit mode.

`GLOBAL` actions should be normal buttons inside the expanded edit toolbar. They should not appear when edit mode is
collapsed.

`GLOBAL` is a future extension point. No current simulation needs global user actions in the first migration. The first
implementation should migrate existing `CELL_SELECTED` actions and keep `GLOBAL` support minimal until a real global
action is added.

Future global destructive actions should use confirmation before applying.

## Descriptor Model

Keep `SimulationUserActionContext` as the layer-neutral action value. Add a descriptor to carry UI and behavior
metadata.

Recommended core types:

```java
public enum SimulationUserActionScope {
    CELL_SELECTED,
    GLOBAL
}
```

Package: `de.mkalb.etpetssim.simulations.core.shared`

```java
public record SimulationUserActionDescriptor<CTX extends SimulationUserActionContext>(
        CTX context,
        SimulationUserActionScope scope,
        String labelKey,
        String tooltipKey) {
}
```

Package: `de.mkalb.etpetssim.simulations.core.viewmodel`

Rationale:

- Scope is layer-neutral.
- Descriptor is UI-facing because it includes localization keys.
- Model actions continue to depend only on `SimulationUserActionContext` and selected cells.
- ViewModels can store selected descriptor/context as UI state without creating JavaFX nodes.

## State Ownership

Edit state belongs in `DefaultMainViewModel`.

The main ViewModel should own:

- whether edit mode is active
- selected cell action descriptor/context
- automatic edit-mode reset when simulation state leaves `PAUSED`

`AbstractMainView` should own:

- JavaFX controls
- toggle groups
- compact edit affordance
- expanded edit toolbar layout
- binding controls to ViewModel state

Do not put JavaFX `Node` creation in ViewModel.

## Click Behavior

First version uses click-only behavior.

Expected click flow in edit mode:

1. User selects a `CELL_SELECTED` action tool.
2. User clicks a canvas cell.
3. Main view resolves the clicked coordinate.
4. Main ViewModel updates selected cell state.
5. ObservationView updates to the selected cell.
6. Active action is applied to the selected cell.
7. Selected cell is refreshed after mutation.
8. Simulation redraws.

Do not bypass selection. Action application must still use the selected `GridCellView`, matching the current
`SimulationUserAction.apply(...)` contract.

No drag painting in the first version. Drag painting would need duplicate-cell suppression, pointer tracking, scroll
interaction decisions, and likely undo batching. Keep it as future work.

## No Undo

Do not introduce undo for the first version.

Rationale:

- Most planned edits are easy to reverse manually, such as toggling a cell or removing a wall.
- The simulation can be restarted with the same configuration.
- Undo would require action snapshots, inverse actions, or model copy strategy.
- That would make the first edit-mode change much larger than needed.

## No Per-Cell Validity Checks Initially

Do not add per-cell validity checks in the first version.

Example: Snake `ADD_WALL` on a food cell currently does nothing. Keep this behavior initially.

Rationale:

- Current `SimulationUserAction.apply(...)` owns action rules.
- The method returns `void`, so core cannot distinguish no-op from mutation.
- Per-cell validation would require another API, such as `canApply(manager, cell)`.

Future improvement:

- Change or supplement action application with a boolean changed result.
- Add optional per-action applicability checks.
- Use applicability for disabled tools, tooltip hints, or notifications.

## Compatibility And Migration

Use a transitional two-hook migration to avoid one large internal breaking change.

Phase 1: Add descriptor support.

- Add `SimulationUserActionScope`.
- Add `SimulationUserActionDescriptor`.
- Add a new descriptor hook in `AbstractMainView`, for example `createUserActionDescriptors()`.
- Default descriptor hook returns an empty list.
- Keep existing `createActionToolBarNodes()` temporarily.
- Core uses descriptor-based edit UI when descriptors are present.
- Old toolbar nodes remain available for not-yet-migrated simulations.

Phase 2: Migrate simulations one at a time.

Recommended order:

1. Conway: validates simplest single-action cell edit flow.
2. Snake: validates multiple action contexts and selected tool behavior.
3. Wator: validates simpler multi-action add-only flow.
4. Forest, Langton, Rebounding: migrate matching simple actions.
5. ET Pets, Sugar: keep no edit row unless meaningful descriptors are added.
6. Lab: keep outside this migration. Lab has special status and uses a different core-superclass path, so it is not
   compatible with this user-action edit-mode pipeline.

Rule during migration:

- A migrated simulation uses the new edit behavior only.
- A not-yet-migrated simulation may keep the old direct action buttons temporarily.
- Do not show old direct buttons and new edit tools in the same simulation.

Phase 3: Remove old hook.

- After all simulations are migrated, remove `createActionToolBarNodes()`.
- Remove old direct-button creation code.
- Keep descriptor-based edit UI as the only user-action toolbar mechanism.

## Expected Simulation Behavior

### Conway

- Compact `Edit` control appears above canvas.
- Expanded edit toolbar contains `Select` and `Toggle cell`.
- Selecting `Toggle cell` does not mutate immediately.
- Clicking cells toggles them while the tool is active.
- Leaving pause exits edit mode.

### Snake

- Compact `Edit` control appears above canvas.
- Expanded edit toolbar contains `Select`, `Add wall`, `Remove wall`, `Add food`, `Remove food`.
- Cell actions are mutually exclusive tools.
- Clicking a cell first selects it, then applies the active tool to that selected cell.
- Invalid targets are no-ops.

### Wator

- Compact `Edit` control appears above canvas.
- Expanded edit toolbar contains `Select`, `Add fish`, `Add shark`.
- Adding to non-water cells remains a no-op.

### Simulations Without Descriptors

- No edit row.
- No disabled empty edit button.
- Canvas space is preserved.

### Simulation Lab

- No edit row.
- No user-action descriptor migration.
- Lab keeps its current interaction model because its classes use a different core-superclass structure and are not
  compatible with the regular simulation user-action pipeline.

## Implementation Notes

- Keep `SimulationUserAction.apply(...)` returning `void` for the first version.
- Redraw after attempted action is acceptable, even if the action was a no-op.
- Keep localization keys in resource bundles.
- Keep user-facing labels and tooltips localizable.
- Keep toolbar CSS in shared simulation styling unless a simulation needs special local styling.
- Disable edit affordance while simulation state is not `PAUSED`.
- Auto-exit edit mode when state leaves `PAUSED`.
- Preserve selection when edit mode changes; do not use edit mode as deselect.

## Future Improvements

- Drag painting for repeated cell edits.
- Boolean action result or action event result.
- Optional per-cell applicability checks.
- Confirmation for destructive global actions.
- Keyboard shortcuts for edit tools.
- Icons for common tools after an icon strategy exists.
- Undo or edit history only if future workflows require it.

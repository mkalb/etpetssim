# User Action Edit Mode Design

This document summarizes the completed Version 1 edit-mode design and prepares the planned Version 2 follow-up for
simulation user actions in etpetssim.

Version 1 is implemented, tested, and committed. Treat it as the stable baseline for Version 2. Version 2 work should
preserve the Version 1 user experience after every migration step, then add parameterized edit tools in small,
reviewable slices.

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

Implementation intent for the next agent:

- Keep `SimulationUserAction.apply(...)` unchanged.
- Keep normal canvas selection as the default interaction outside edit mode.
- Keep existing fixed tools working after every step.
- Move shared edit-toolbar state out of `DefaultMainViewModel` before adding Snake or Conway-specific options.
- Do not add unit tests in Version 2. This project has none yet and Version 2 keeps it that way.
- Verify each step with `.\gradlew.bat compileJava` plus a manual run; every step must deliver compilable, runnable
  code.
- Implement the steps strictly one at a time, with a human review and a human test between steps. Treat each numbered
  migration step as a hard stop: after verification, summarize the result and wait for an explicit human instruction
  before starting the next step.
- Keep each step's diff focused on that step. Do not combine baseline verification, shared refactoring, Snake, and
  Conway
  changes in one change set.

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
- Before stable tool ids exist, the selected tool may still be represented by the selected descriptor with `null`
  meaning
  `Select`. After stable ids are introduced, the selected-tool source of truth should become the stable tool id, not a
  descriptor object.
- Simulation-specific edit ViewModels own option state such as the selected Snake strategy or selected Conway pattern.
- Simulation-specific edit Views create option controls such as ComboBoxes and bind them to the simulation-specific edit
  ViewModel.
- `SimulationUserActionDescriptor` remains metadata and context-resolution information. It must not own JavaFX nodes or
  become a generic UI control schema.

The shared toolbar should provide the common edit affordance, the `Select` tool, selected-tool handling, enablement
rules, and click-to-apply flow. Simulations can contribute a small option panel for controls that are specific to their
edit tools.

Current Version 1 implementation anchors:

- `DefaultMainViewModel` owns edit-mode state, selected-descriptor state, selected-cell state, simulation lifecycle, and
  final model-side action application.
- `AbstractMainView` builds the edit affordance, `Select` toggle, descriptor-backed action controls, and global action
  buttons.
- `AbstractDefaultMainView` updates selection first on canvas clicks, then applies the selected cell action and redraws
  when an action was attempted.
- Simulation views override `createUserActionDescriptors()` to contribute fixed tools.
- Localization keys for labels and tooltips live in resource bundles; descriptor logic must not depend on localized
  text.
- `AppLocalizationKeys` contains constants for resource-bundle keys used from Java. New Java code should use constants
  instead of raw localization key strings.

### Descriptor And Context Resolution

Keep `SimulationUserAction.apply(...)` unchanged for Version 2. Parameter values should be folded into the resolved
action context before the action is applied.

Recommended descriptor direction:

- Add a stable tool id to `SimulationUserActionDescriptor` before adding simulation-specific option controls. Use this
  id
  for selected-tool matching and enablement logic. Do not use localized labels for logic. Prefer simulation-prefixed
  constants such as `snake.addSnake` or `conway.placePattern`; ids must be unique within the built toolbar.
- Match the selected tool by its stable id, not by record equality. Version 1 toolbar matching in `AbstractMainView`
  uses
  `descriptor.equals(...)`; adding a resolver function as a record component would break the generated `equals` and the
  toggle selection. Switch selection, enablement, and option-panel wiring to compare stable ids.
- Do not keep descriptor objects as the selected-tool source of truth after this migration. Descriptors may be recreated
  during toolbar rebuilds, and resolver lambdas are not meaningful identity values. Store the selected stable id in the
  shared edit-toolbar ViewModel and look up the matching descriptor by id when needed.
- Replace or supplement the fixed `context` value with a resolver-style concept that resolves the current context at
  apply time.
- Use `Optional<CTX>` as the resolver result shape so a single empty result represents "no context available" when an
  option-dependent tool has no valid option. Skip the call to `SimulationUserAction.apply(...)` when the result is
  empty.
  Do not mix nullable, sentinel, and wrapper styles.
- If the resolver returns `Optional.empty()`, the ViewModel apply method should return `false` so the view does not
  redraw
  merely because a tool was selected without a valid context. If a context resolves and
  `SimulationUserAction.apply(...)`
  is invoked, returning `true` and redrawing remains acceptable even if the model action itself is a no-op.
- Fixed tools can use a resolver that always returns the same enum or context value.
- Parameterized tools can use a resolver that combines the selected tool with current option state from a
  simulation-specific edit ViewModel.
- Keep descriptor fields limited to metadata and context resolution: stable id, scope, label key, tooltip key, and
  resolver. Do not add JavaFX nodes or generic form-control metadata to descriptors.

Recommended action-context direction:

- Simple actions may remain enum constants when a simulation only has fixed actions.
- Simulations that mix fixed and parameterized actions should migrate their simulation-specific context from a single
  enum
  to a small context hierarchy, such as an interface or sealed interface extending `SimulationUserActionContext`.
- Follow the existing simulation package convention for action contexts. For example, Snake currently keeps
  `SnakeUserActionContext` in `de.mkalb.etpetssim.simulations.snake.shared`.
- Fixed actions in such simulations can become enum constants or singleton records implementing that context hierarchy.
- Parameterized actions should use richer context values, such as records implementing the simulation-specific context
  contract.
- Snake can use an `AddSnake` context carrying the selected `SnakeMoveStrategy`.
- Conway should introduce a Conway-specific context instead of continuing to use `NoUserActionContext`; `PlacePattern`
  can carry a selected `GridPattern` or a stable pattern choice that resolves to one.

This keeps model actions independent of JavaFX controls while allowing the existing `apply(manager, context,
selectedCell)` contract to remain stable.

### Toolbar Behavior

Keep the Version 1 inspect-first behavior:

1. Canvas clicks still update selection first.
2. Observation views still show the selected cell.
3. An edit action is applied only when edit mode is active and a non-`Select` tool is selected.

`Select` should remain a toolbar tool with no model action. It may be represented internally by `null` before the
stable-id
migration or by a reserved select id afterward, but it must never resolve to a model action context.

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
- Snake ids come from a monotonic `nextSnakeId` counter owned by `SnakeSimulationManager`, not from grid contents.
  Version 1 assigns ids by index, and dead snakes are removed, so deriving ids from the grid (for example `max id + 1`)
  would reuse ids.
- Initial generated snakes use ids `0..config.snakes() - 1`, and `GridInitializers.placeAllAtRandomPositions(...)` fails
  fast if it cannot place all of them. Initialize `nextSnakeId` from `config.snakes()` and increment it only after a new
  snake is actually placed.
- Construct the new `SnakeHead` with the full Version 1 constructor: the new id, the selected strategy,
  `config.initialPendingGrowth()` as the initial pending growth, and `manager.stepCount() - 1` as
  `stepIndexOfSpawn`, matching the existing step-index convention used by `SnakeHead.ageAtStepCount(...)` and Wa-Tor
  creature creation.
- Adding a snake must update Snake statistics consistently, including total snake-head cells and living snake-head
  cells.
  `SnakeStatistics` has no total-increase path today (only `increaseLivingSnakeHeadCells()` and decrease methods), so a
  method to increase the total snake-head count must be added.
- Strategy choice identity must be the selected `SnakeMoveStrategy` value, not its localized or displayed text.

### Conway Behavior

Add a Conway cell action for placing a predefined pattern at the selected cell.

Expected behavior:

- Expanded edit toolbar contains the existing `Toggle cell` tool plus `Place pattern`.
- A pattern ComboBox is visible in the expanded edit toolbar.
- The pattern ComboBox is disabled unless `Place pattern` is selected and at least one pattern is available.
- Define the pattern catalog and its availability rule explicitly. Version 1 only exposes the static classic factories
  in
  `ConwayPatterns` (`glider`, `block`, `beehive`, `blinker`), and `ConwayConfig` has no pattern field, so "derived from
  the active config" must be made concrete. Add an availability rule such as `availableFor(ConwayConfig)` and decide
  whether classic patterns are always available, square-only, or rule-dependent (`ConwayConstraints` allows all cell
  shapes today).
- Pattern choices need stable ids and label keys separate from localized display names. A choice can carry a stable id,
  a label key, a pattern factory or pattern value, and an `availableFor(ConwayConfig)` rule; UI selection must compare
  the
  stable choice id or the choice value, never localized text.
- `InputChoiceProperty` rejects an empty value list, so a config with zero available patterns needs an empty-safe
  binding
  rather than an `InputChoiceProperty`.
- Pattern choices are recomputed when a Conway simulation is initialized or restarted with a new config.
- The selected cell is the top-left origin of the normalized `GridPattern`.
- Pattern placement does not use grid-edge wrap behavior.
- Pattern placement is all-or-nothing: if any pattern coordinate would fall outside the grid, the action is a no-op.
- Do not call `GridEntityUtils.placePatternAt` directly for placement. It silently skips out-of-bounds coordinates and
  writes the rest, which violates the all-or-nothing rule. Validate the full footprint first (for example a
  `canPlacePatternAt` check), then write, and compute statistics from the pre-write footprint.
- Placing a pattern overwrites the full pattern footprint, including `DEAD` cells contained in the pattern.
- Compute alive/dead deltas and changed-cell count from the pre-write footprint before mutating the model, then update
  statistics once. `changedCells` should increase only by cells whose entity actually changed.

### Migration Notes

Implement Version 2 as independent migration slices, strictly one step at a time. Each step must deliver code that
compiles and runs, and must keep the application usable before the next step starts.

This project has no unit tests yet, and Version 2 must not add any. Per-step verification is limited to a successful
`.\gradlew.bat compileJava` followed by a manual run of the application. After each step, stop for a human review and a
human test before starting the next step.

General guardrails for every step:

- Start from the committed Version 1 baseline.
- Check the working tree before making changes. If unrelated user changes exist, leave them alone and keep the step diff
  isolated.
- Keep public API changes as narrow as possible, but allow intentional descriptor and context API changes where the step
  calls them out.
- Keep `SimulationUserAction.apply(manager, context, selectedCell)` returning `void`.
- Keep action validity model-side. Do not add per-cell applicability checks to the toolbar.
- Keep toolbar labels and tooltips localized through resource-bundle keys.
- Add `AppLocalizationKeys` constants for new localization keys used from Java. Keep properties files sorted
  alphabetically when new localization keys are added.
- Do not add or modify unit tests; this project has none yet and Version 2 keeps it that way.
- Verify each implementation slice with `.\gradlew.bat compileJava`, then run the application and test it manually.
- Implement only one step per change set, then hand off for human review and human testing before the next step starts.
- Manually smoke-check paused simulations with edit tools after shared toolbar changes: Conway, Forest, Langton,
  Rebounding, Snake, and Wa-Tor.

Recommended migration order:

1. Baseline verification.

   Scope:

    - Build the committed Version 1 baseline with `.\gradlew.bat compileJava` and run the application.
    - Manually confirm the current Version 1 behavior: edit-mode reset when leaving `PAUSED`, `Select` as the default
      tool, click selection before action application, and no edit row for simulations without descriptors.
    - Do not change production behavior in this step. Do not add unit tests.

   Acceptance checks:

    - The baseline compiles with `.\gradlew.bat compileJava`.
    - Existing fixed tools still behave exactly as Version 1 describes during a manual run.

2. Extract shared edit-toolbar state.

   Scope:

    - Create a dedicated shared edit-toolbar ViewModel in the core viewmodel area.
    - Move edit-mode active state, selected tool state, reset-to-`Select` behavior, and selected-tool helper methods out
      of
      `DefaultMainViewModel` into that ViewModel.
    - Preserve the Version 1 representation in this step: `null` selected descriptor means `Select` and no cell action.
    - Keep the `simulationStateListener` reset (exit edit mode when leaving `PAUSED`) and the `shutdownSimulation()`
      reset
      consistent with the moved state. Edit-mode and selected-tool reset must not be left split between
      `DefaultMainViewModel` and the new ViewModel.
    - Keep `DefaultMainViewModel` responsible for simulation lifecycle, selected cell state, manager access, statistics
      refresh, and the final call to `SimulationUserAction.apply(...)`.
    - Keep existing public accessors on `DefaultMainViewModel` as delegating methods if that minimizes changes to
      `AbstractMainView` and `AbstractDefaultMainView`.
    - Do not change `SimulationUserActionDescriptor` in this step.

   Acceptance checks:

    - The change compiles with `.\gradlew.bat compileJava`.
    - Entering edit mode still shows `Select` and fixed simulation tools.
    - Leaving `PAUSED` still exits edit mode and clears the selected action tool.
    - Canvas clicks still select first, then apply the selected tool only in edit mode.
    - Simulations without descriptors still keep their canvas space.

3. Add stable tool ids and context resolution.

   Scope:

    - Extend `SimulationUserActionDescriptor` with a stable id and an apply-time context resolver that returns
      `Optional<CTX>`.
    - Preserve an easy fixed-context factory or constructor so existing tools remain concise.
    - Move selected-tool state from selected descriptor object to selected stable tool id. Keep temporary delegating
      accessors only if needed to avoid a broad view rewrite, but do not leave descriptor equality as the active
      matching
      mechanism.
    - Switch toolbar selection and enablement from record equality to stable-id matching. Version 1 `AbstractMainView`
      uses
      `descriptor.equals(...)`; keep the resolver out of `equals` (or match by id regardless) so toggle selection stays
      correct.
    - Update `AbstractMainView`, `AbstractDefaultMainView`, and `DefaultMainViewModel` to resolve context at action time
      instead of reading a fixed descriptor context directly.
    - If the resolver returns an empty `Optional`, do not call `SimulationUserAction.apply(...)` and do not redraw from
      that
      attempted click or global action.
    - Migrate all existing descriptors to stable ids without changing their labels, tooltips, scopes, or behavior.

   Acceptance checks:

    - Existing fixed tools still apply the same contexts as before.
    - Global and cell-scoped tools both use the resolver path.
    - No logic depends on localized labels.
    - Toolbar selection and enablement match by stable tool id, and toggle selection remains correct after adding the
      resolver.

4. Add the simulation option-panel extension point.

   Scope:

    - Add a protected hook in the shared main-view layer for simulation-specific edit option controls.
    - Keep the hook separate from descriptors; descriptors must not create or own JavaFX nodes.
    - Let the hook observe the shared edit-toolbar ViewModel or selected stable tool id, so option controls can bind
      enablement without reading localized text.
    - Bind option-panel visibility and management to expanded edit mode so the layout is stable while switching tools.
    - Let option controls enable or disable themselves based on the selected stable tool id and their own option state.
    - Ensure listener cleanup follows the existing toolbar cleanup pattern. If option controls register listeners on
      long-lived ViewModels or properties, remove those listeners when the toolbar is rebuilt or the simulation shuts
      down.
    - Keep every existing simulation returning no option panel until it needs one.

   Acceptance checks:

    - Adding the hook does not visually change existing fixed-tool simulations.
    - The edit toolbar still collapses to the compact edit affordance outside edit mode.
    - Option-panel controls can observe selected-tool state without reaching into localized text or descriptor labels.

5. Implement Snake `Add snake`.

   Split this step into `5a` and `5b`. Treat them as separate migration slices. After `5a`, verify, summarize, and stop
   for human review and manual testing before starting `5b`.

   **5a. Model and context groundwork.**

   Scope:

    - Migrate Snake action context from the current fixed enum-only shape to a context hierarchy that can represent both
      existing fixed actions and `AddSnake(SnakeMoveStrategy strategy)`.
    - Apply `Add snake` only to ground cells; walls, food, snake heads, and snake segments remain no-ops.
    - Assign snake ids from a monotonic `nextSnakeId` counter owned by `SnakeSimulationManager`, not from grid contents,
      because dead snakes are removed.
    - Initialize `nextSnakeId` from the initial configured snake count after startup initialization, then increment it
      only after the model writes a new snake head.
    - Construct the new `SnakeHead` with id, selected strategy, `config.initialPendingGrowth()`, and the current step
      index `manager.stepCount() - 1` as `stepIndexOfSpawn`.
    - Add a total-increment method to `SnakeStatistics` (it has no total-increase path today) and keep total and living
      snake-head counts consistent.
    - Keep the existing Snake toolbar behavior unchanged in this step. Do not add the visible `Add snake` tool or the
      strategy option panel yet.

   Acceptance checks:

    - Existing Snake tools still work unchanged.
    - The Snake model can apply an `AddSnake(SnakeMoveStrategy strategy)` context when called through the existing user
      action path.
    - Adding a snake through the model action writes a snake head only to ground cells.
    - Added snakes receive monotonic ids from `nextSnakeId`, not ids derived from grid contents.
    - Adding a snake updates Snake statistics for total snake-head cells and living snake-head cells.
    - Invalid target cells remain no-ops without UI-side per-cell checks.

   **5b. Toolbar and strategy option UI.**

   Start this slice only after `5a` has been reviewed, manually tested, and explicitly approved.

   Scope:

    - Add a stable `Add snake` descriptor id, label key, and tooltip key.
    - Add a Snake edit ViewModel for selected strategy state, and construct it in `SnakeFactory` so the resolver and the
      option panel share the same instance.
    - Populate strategies from `SnakeMoveStrategies.strategiesForConfig()`, not from localized display text. Note that
      this method currently takes no `SnakeConfig` despite its name; pass config only if it starts depending on one.
    - Use the first strategy returned by `SnakeMoveStrategies.strategiesForConfig()` as the default selected strategy.
      With the current catalog this is `SnakeMoveStrategies.MOMENTUM`.
    - Store the selected option as the actual `SnakeMoveStrategy` value. Do not store or compare localized text, display
      names, or ComboBox strings for strategy identity.
    - Display strategies in the ComboBox using their existing short strategy names, such as `M`, `F M`, or `F V C+`.
      Do not introduce longer localized strategy display names in this step.
    - Add a strategy ComboBox to the Snake option panel. It should be visible while edit mode is expanded and enabled
      only when `Add snake` is selected.
    - Add localized ComboBox label and tooltip keys for the strategy control in both `messages_en_US.properties` and
      `messages_de_DE.properties`, plus `AppLocalizationKeys` constants, separate from the descriptor label and tooltip
      keys.
    - If selected strategy state is ever nullable, the `Add snake` descriptor resolver must return `Optional.empty()`
      until a strategy is selected. With the default strategy described above, normal startup should have a non-null
      selected strategy.

   Acceptance checks:

    - Existing Snake tools still work unchanged.
    - Expanded edit toolbar contains the existing Snake tools plus `Add snake`.
    - The strategy ComboBox is visible while edit mode is expanded and disabled unless `Add snake` is selected.
    - Repeated clicks with `Add snake` selected reuse the selected strategy.
    - Changing the strategy affects later added snakes only.
    - Adding a snake updates the rendered grid, observation selection, and Snake statistics.
    - Invalid target cells remain no-ops without UI-side per-cell checks.

6. Implement Conway `Place pattern`.

   Scope:

    - Introduce a Conway-specific action context so Conway no longer relies on `NoUserActionContext` for edit tools.
    - Keep `Toggle cell` as a fixed Conway action.
    - Add a stable `Place pattern` descriptor id, label key, and tooltip key.
    - Define the pattern catalog and an availability rule such as `availableFor(ConwayConfig)`. Decide whether the
      classic
      `ConwayPatterns` factories are always available, square-only, or rule-dependent, and add a config-aware accessor
      because `ConwayConfig` has no pattern field today.
    - Represent catalog entries as stable pattern choices, not localized strings. Each choice should provide a stable
      id,
      localized display key, pattern value or factory, and availability rule.
    - Add a Conway edit ViewModel for available pattern choices and selected pattern state, and construct it in
      `ConwayFactory` so the resolver and the option panel share the same instance.
    - Add a pattern ComboBox to the Conway option panel. It should be visible while edit mode is expanded and enabled
      only
      when `Place pattern` is selected and at least one pattern is available. Use an empty-safe binding for the
      zero-pattern case, because `InputChoiceProperty` rejects an empty value list. A plain nullable property plus
      ComboBox
      items is acceptable here.
    - Add localized ComboBox label and tooltip keys plus pattern display names in both `messages_en_US.properties` and
      `messages_de_DE.properties`, plus `AppLocalizationKeys` constants, separate from the descriptor label and tooltip
      keys.
    - Derive available pattern choices from the active `ConwayConfig` after simulation initialization or restart, not
      from
      editable config controls.
    - If no pattern is selected or no patterns are available, the `Place pattern` descriptor resolver must return
      `Optional.empty()`.
    - Use the selected cell as the top-left origin of the normalized `GridPattern`.
    - Place patterns without grid-edge wrapping.
    - Make placement all-or-nothing: validate the full footprint first (for example a `canPlacePatternAt` check) and do
      nothing if any coordinate falls outside the grid. Do not call `GridEntityUtils.placePatternAt` directly, because
      it
      skips out-of-bounds cells and writes the rest.
    - Overwrite the full pattern footprint, including `DEAD` cells contained in the pattern.
    - Define how `Place pattern` updates `ConwayStatistics.changedCells`: count only cells whose value actually changes,
      matching the existing `Toggle cell` semantics, not the full footprint size.

   Acceptance checks:

    - Existing Conway `Toggle cell` behavior remains unchanged.
    - Pattern choices recompute when a new Conway simulation starts with a different config.
    - Out-of-bounds pattern placement leaves the grid and statistics unchanged.
    - Successful placement updates grid cells, alive/dead statistics, observation labels, and redraw state.

7. Stabilize and document follow-up boundaries.

   Scope:

    - Keep Forest, Langton, Rebounding, Wa-Tor, and other fixed-tool simulations on fixed descriptors unless they need
      parameterized tools later.
    - Remove obsolete helper methods or temporary compatibility paths introduced during migration.
    - Keep Version 3 topics out of the implementation: action-result reporting, per-cell applicability, undo, drag
      painting, and edit history.
    - Update this document if the final Version 2 implementation intentionally differs from the plan.

   Acceptance checks:

    - The final code compiles with `.\gradlew.bat compileJava`.
    - All simulations with edit descriptors still show the correct toolbar controls.
    - Simulations without descriptors still show no edit row.
    - The final code has no localized-label logic, no descriptor-owned JavaFX nodes, and no Version 3 behavior.

Do not add drag painting, undo, per-cell applicability checks, or changed/unchanged action results as part of Version 2.

## Version 3 Note

Version 3 can revisit action results and applicability. A future action API may return whether the model actually
changed, expose optional per-cell applicability checks, and use those results for disabled tools, no-op feedback,
statistics refresh decisions, and more precise redraw behavior.

Other possible Version 3 or later improvements:

- Drag painting for repeated cell edits.
- Confirmation for destructive global actions.
- Keyboard shortcuts for edit tools.
- Icons for common tools after an icon strategy exists.
- Undo or edit history only if future workflows require it.

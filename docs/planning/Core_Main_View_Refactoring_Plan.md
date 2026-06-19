# Core Main View Refactoring Plan

This document is a planning brief for a future code change in `de.mkalb.etpetssim.simulations.core`. It is intended as
input for an agent that will investigate and possibly refactor the interaction between the core main view and main
view-model classes.

Scope of this document: architecture and refactoring guidance only. No Java code, tests, resources, or build files are
changed by this document.

## Context

The current core design has two paired layers:

- `AbstractMainView` and `AbstractMainViewModel` provide the thin common shell used by all main views and main
  view-models.
- `AbstractDefaultMainView` and `DefaultMainViewModel` provide the standard timed-simulation behavior used by the
  regular simulations.

This split exists because Lab is special. `LabMainView` extends `AbstractMainView` directly, and `LabMainViewModel`
extends `AbstractMainViewModel` directly. Lab is a diagnostic/showcase environment with explicit draw buttons, model
highlighting, geometry tests, hover rendering, and neighborhood visualization. It does not use the default timed-step
execution flow.

The regular simulations use the default pair:

- `ConwayMainView`, `EtpetsMainView`, `ForestMainView`, `LangtonMainView`, `ReboundingMainView`, `SnakeMainView`,
  `SugarMainView`, and `WatorMainView` extend `AbstractDefaultMainView`.
- Their factories create `DefaultMainViewModel` with simulation-specific manager factories, cell providers, and user
  actions.

The future refactoring should preserve this distinction unless the investigation proves that Lab can use more shared
default behavior without losing its lab-specific interaction model.

## Refactoring Goal

Review whether responsibilities are placed in the best class or helper, with special attention to code that is shared
by Lab and the regular simulations but currently lives only in one side of the split.

The desired result is not necessarily fewer classes. The desired result is clearer ownership, less duplicate lifecycle
or selection logic, fewer fragile bindings/listeners, and a smaller conceptual gap between the abstract pair and the
default pair.

## Current Responsibility Map

### `AbstractMainView`

Current role:

- Builds the common main region with config, control, simulation canvas area, observation, notification label, and edit
  affordance toolbar.
- Owns the three canvas layers and painter fields.
- Converts primary mouse releases on the overlay canvas into grid coordinates.
- Creates and clears canvas painters, computes fonts, updates edge border panes, and computes visible canvas center.
- Registers notification handling.
- Builds descriptor-based edit toolbar UI and delegates actual action application to subclasses.

Potential pressure points:

- It mixes generic shell composition, canvas/painter lifecycle, click routing, notification rendering, and edit-toolbar
  UI mechanics.
- The edit-toolbar code is generic enough for default simulations, but Lab currently gets this machinery even though it
  has no edit descriptors.
- Listener cleanup for the edit toolbar is partly internal to this view class, while related edit state lives in
  `DefaultMainViewModel`.

### `AbstractDefaultMainView`

Current role:

- Connects default view-model events to the view.
- Handles default simulation initialization, step notifications, draw throttling, and skipped-draw overlay.
- Applies selected-cell and global user actions and redraws after changes.
- Owns default click behavior: clicking a cell updates the clicked coordinate and then tries the selected edit action.

Potential pressure points:

- It is strongly tied to `DefaultMainViewModel`, which is appropriate for the default flow but makes common selection
  behavior unavailable to Lab.
- It registers a selected-cell listener without storing the listener for explicit removal; verify whether this is
  acceptable because the view and view-model share scene lifetime, or whether shutdown should make this explicit.
- It contains draw-throttling and skipped-overlay behavior that may be easier to test or reason about as a small helper.

### `AbstractMainViewModel`

Current role:

- Owns shared simulation state and notification type.
- Owns current and previous clicked-coordinate properties.
- Binds the observation view-model's last-clicked-coordinate property to its own clicked-coordinate property.
- Defines abstract access to current config, current model, and manager availability.

Potential pressure points:

- It handles clicked-coordinate history but not selected-cell state, even though selected-cell state is also shared by
  regular simulations and Lab.
- It performs the observation binding in the constructor, which requires every subclass to remember to unbind in
  shutdown.
- `getCurrentConfig()` is protected abstract, while `getCurrentModel()` and `hasSimulationManager()` are public
  abstract. Confirm whether this visibility split is intentional API design or just historical growth.

### `DefaultMainViewModel`

Current role:

- Owns the default timed and batch simulation lifecycle.
- Creates simulation managers, executes timed and batch steps, handles pause/resume/cancel, and updates statistics.
- Owns selected-cell properties, last selected coordinate/entity, edit-mode state, and selected action descriptor.
- Listens to clicked-coordinate changes and refreshes selected-cell state when the simulation state allows selection.
- Applies simulation user actions.
- Manages timer and executor resources.

Potential pressure points:

- It combines several responsibilities: lifecycle execution, manager ownership, statistics updates, selection,
  edit-mode state, action application, timeout configuration, timer control, batch execution, and logging.
- Selected-cell behavior overlaps conceptually with Lab's manual `updateSelectedGridCell(...)` and
  `resetSelectedGridCell()` methods.
- The concrete reference to `DefaultObservationViewModel` is needed for selected-cell binding and unbinding, while the
  abstract superclass only knows `SimulationObservationViewModel`.
- The optional `lastClickedCoordinateListener` field is currently nullable even though it is always assigned in the
  constructor. Verify whether this is needed for generic/nullability reasons.

### Lab Classes

Current Lab-specific behavior:

- `LabMainViewModel` owns custom draw request listeners for config, base draw, model draw, and test draw.
- It creates a `LabSimulationManager` only for explicit draw requests, not for timed execution.
- It computes neighborhood highlight data for the view.
- It manually updates selected-cell state on the concrete `DefaultObservationViewModel`.
- `LabMainView` handles hover overlays, diagnostic drawing, neighborhood highlights, and test geometry rendering.

Potential pressure points:

- Lab duplicates selected-cell reset/update concepts that also exist in the default view-model path.
- Lab calls `viewModel.updateClickedCoordinateProperties(...)` and then separately updates selected-cell state. Default
  simulations use a clicked-coordinate listener to derive selected-cell state.
- Lab's click handling clears and redraws canvas overlays directly, which is probably Lab-specific and should not be
  forced into the default classes.

## Investigation Questions

Before editing code, answer these questions with concrete references from the current implementation:

1. Which behaviors are truly common to every main view, including Lab?
2. Which behaviors are common only to regular timed simulations?
3. Which behaviors are Lab-only diagnostics and should stay outside default abstractions?
4. Can selected-cell handling be shared without making `AbstractMainViewModel` generics significantly harder to read?
5. Should observation binding/unbinding be encapsulated so subclasses cannot forget cleanup?
6. Should edit-toolbar construction remain in `AbstractMainView`, or should it become a dedicated core view/helper
   class with clearer lifecycle cleanup?
7. Would extracting timer/batch execution from `DefaultMainViewModel` reduce complexity without scattering lifecycle
   rules across too many collaborators?
8. Are any listener registrations missing matching shutdown cleanup, or are they intentionally scene-lifetime listeners?
9. Are current public/protected method visibilities intentional and covered by actual call sites?
10. Can any proposed move be made without changing simulation-specific subclasses or factories?

## Candidate Refactorings

These are candidates, not instructions to implement all of them. The future agent should choose the smallest set that
improves ownership without adding unnecessary abstraction.

### Candidate 1: Extract Shared Selection Support

Problem:

Default simulations and Lab both expose selected cells in observation views, but the default path owns a rich selection
model while Lab manually writes selected-cell state into its observation view-model.

Analysis:

Selection is the strongest overlap between the default path and Lab. Both paths need to publish a selected cell to the
observation view and clear that selection during reset/shutdown. The important difference is how selection is triggered:
regular simulations derive selected cells from clicked-coordinate changes and simulation state, while Lab explicitly
updates selection during custom click handling after it has redrawn diagnostic overlays.

Moving all selection state into `AbstractMainViewModel` would make the base class more powerful, but it would also add a
cell-view generic type to a class that currently only knows entity, model, config, and statistics. That would ripple
through both direct subclasses and may make the thin abstract layer harder to read. A focused helper can share the
selected-cell properties and reset/update mechanics without forcing the abstract base class to know every selection
detail.

The future agent should also distinguish between selected-cell storage and selected-cell policy. Storage can be shared;
policy should remain with the caller. `DefaultMainViewModel` should keep deciding when selection is allowed based on
`SimulationState`. `LabMainViewModel` should keep deciding when a click selects, clears, or redraws neighborhood
highlights.

Possible direction:

- Introduce a small core selection helper that owns `selectedGridCell`, last selected coordinate, and last selected
  entity.
- Let `DefaultMainViewModel` use the helper with its existing selected-cell provider.
- Let `LabMainViewModel` use the helper or a smaller variant to set/clear selected cells explicitly.
- Avoid moving all selected-cell behavior into `AbstractMainViewModel` unless the added generic parameter for the cell
  view type remains readable across all subclasses.

Decision criteria:

- Prefer helper extraction if it reduces duplication without changing the abstract generic signature.
- Prefer superclass extraction only if it meaningfully simplifies both `DefaultMainViewModel` and `LabMainViewModel`.
- Do not make Lab depend on default timed-simulation lifecycle types just to share selection state.

Recommendation:

Implement this candidate first, but as a small helper owned by the view-models, not as new state on
`AbstractMainViewModel`. Keep selection policy in `DefaultMainViewModel` and `LabMainViewModel`; share only property
storage, selected-cell binding support, and reset/update operations.

### Candidate 2: Encapsulate Observation Binding Lifecycle

Problem:

`AbstractMainViewModel` binds the observation view-model's last-clicked-coordinate property in its constructor, while
subclasses explicitly unbind during shutdown.

Analysis:

This is a small but worthwhile lifecycle cleanup. The current constructor binding creates a hidden obligation: every
subclass must remember to unbind during shutdown. Both current subclasses do this, but the contract is implicit. That is
exactly the kind of responsibility that belongs in the base class because the base class created the binding.

A full template-method shutdown may be too broad if it requires rearranging timer/executor cleanup, Lab draw-listener
cleanup, or subclass-specific state reset. A narrower protected cleanup method is likely enough. For example, the base
class could expose a final or protected method that unbinds common observation state and clears common clicked-coordinate
state. Subclasses would call it during their existing shutdown sequence at the point where they currently unbind
manually.

The future agent should inspect shutdown ordering before changing this. In the default path, timer and batch shutdown
interacts with state changes and callbacks. In Lab, view callbacks are replaced with no-op runnables after reset.
Common observation cleanup should not accidentally fire selection or notification updates after subclass resources have
already been nulled.

Possible direction:

- Add protected lifecycle methods in `AbstractMainViewModel` for binding and unbinding observation state.
- Let subclasses call a superclass shutdown helper instead of directly unbinding the inherited observation view-model.
- Consider whether `SimulationMainViewModel.shutdownSimulation()` should have a template-method shape, where the base
  class handles common cleanup and subclasses implement a protected hook.

Decision criteria:

- Common cleanup should be impossible to forget.
- Avoid introducing a complex lifecycle framework for two bindings.
- Preserve existing shutdown order where it matters, especially timer/executor cancellation in `DefaultMainViewModel`.

Recommendation:

Implement this candidate as a narrow base-class cleanup method, not as a full shutdown template yet. Move the common
last-clicked-coordinate unbind responsibility into `AbstractMainViewModel`, and have each subclass call the common
cleanup from its current shutdown flow.

### Candidate 3: Split Default Lifecycle Execution From UI State

Problem:

`DefaultMainViewModel` currently owns manager lifecycle, timer execution, batch execution, statistics updates,
selection, edit state, and action application.

Analysis:

`DefaultMainViewModel` is the largest and most overloaded class in the group. It combines simulation orchestration,
JavaFX-thread statistics updates, timer scheduling, batch execution, cancellation, selected-cell state, edit-mode state,
and action application. That makes the class harder to scan, but it also centralizes the default simulation state
machine in one place. Splitting too aggressively could make the lifecycle harder to audit.

The timer and batch logic is the most tempting extraction target, but it is also tightly coupled to simulation state,
notifications, timeout thresholds, statistics updates, and final-step view callbacks. A collaborator that merely hides
`SimulationTimer`, `ExecutorService`, `Future`, and `Thread` handling may help. A collaborator that owns state
transitions may make the code worse unless it has a very clear boundary.

This candidate has higher risk than the selection and observation-binding candidates. It should not be the first move
unless there are known bugs in pause/resume/cancel/shutdown behavior. If pursued, it needs focused tests or at least a
careful manual validation matrix because timing and cancellation failures can be subtle.

Possible direction:

- Extract timer and batch execution into a focused collaborator used only by `DefaultMainViewModel`.
- Keep public API stable on `DefaultMainViewModel`; the view should not need to know about the collaborator.
- Keep manager creation and state transitions in `DefaultMainViewModel` unless the collaborator can own them without
  making state changes harder to audit.

Decision criteria:

- Extract only if it makes start/pause/resume/cancel and shutdown easier to reason about.
- Do not split state transitions across multiple classes unless there is a very clear contract.
- Tests should cover cancellation, shutdown, and state changes if this is implemented.

Recommendation:

Defer this candidate until after smaller ownership cleanups are complete. If it is later implemented, extract only
mechanical execution resources first, such as timer/batch runner management, while keeping state transitions and manager
ownership in `DefaultMainViewModel`.

### Candidate 4: Extract Edit Toolbar View Mechanics

Problem:

`AbstractMainView` builds and manages descriptor-based edit toolbar controls, while edit state and selected descriptor
live only in the default view-model path.

Analysis:

The edit toolbar is generic UI machinery, but it is not core to every main view. `AbstractMainView` currently owns it
because it owns the common simulation layout and because the toolbar appears above the canvas. That placement is
reasonable, yet the implementation adds a lot of JavaFX control construction, toggle mapping, listener cleanup, and
visibility binding to a class that already owns canvas and notification concerns.

An extraction could make `AbstractMainView` easier to read without changing the external behavior. The helper should be
a view-level component, not a view-model. It can create controls from `SimulationUserActionDescriptor`, bind them to
edit-mode state, synchronize the selected descriptor property, and return a configured toolbar node. `AbstractMainView`
would still decide where that node is placed.

This candidate becomes more valuable if Version 2 edit tools add parameter controls. Without extraction, parameterized
tools would likely make `AbstractMainView` even larger. With extraction, the future edit UI can evolve in one dedicated
place while keeping Lab unaffected when it supplies no descriptors.

Possible direction:

- Extract the edit toolbar into a small core view component or helper owned by `AbstractMainView`.
- Keep `AbstractMainView` responsible for placement of the toolbar in the page layout.
- Let the helper own toggle/button creation, listener cleanup, and visible/managed bindings.
- Keep descriptor context and action application outside JavaFX nodes.

Decision criteria:

- Extract only if it reduces `AbstractMainView` complexity and improves cleanup clarity.
- Preserve simulations with no descriptors: no toolbar space should be consumed.
- Preserve Lab behavior: Lab should remain unaffected when it contributes no descriptors.

Recommendation:

Implement this candidate after the selection and observation-binding cleanup, especially before adding parameterized
edit tools. Extract a view helper that owns toolbar node creation and cleanup, while leaving toolbar placement and action
application hooks in `AbstractMainView`.

### Candidate 5: Clarify Canvas/Painter Ownership

Problem:

`AbstractMainView` owns canvas creation, painter creation, font calculation, border-pane updates, and visible-center
calculation. `AbstractDefaultMainView` and `LabMainView` both directly use painter fields.

Analysis:

Canvas and painter ownership is mostly in the right place. All main views need the same three canvas layers, and both
default simulations and Lab need direct drawing access. The current protected painter fields are simple and practical
for a rendering-heavy JavaFX canvas design.

The main pain is not ownership but readiness checks. Several methods guard against null painters before drawing. This
is defensive and understandable because painters are created after a valid simulation/configuration exists and cleared
during resets. However, repeated null checks can obscure the actual drawing logic and create inconsistent warning
messages.

A large abstraction around canvas state would likely be premature. An immutable canvas state object may help later if
more initialized values need to travel together, but it would not by itself solve a clear current problem. A smaller
helper such as `hasPainters()` or `withPainters(...)` could reduce repetition, but it should not hide painter access so
much that simulation-specific drawing becomes awkward.

Possible direction:

- Keep painter fields in `AbstractMainView`, but consider a small immutable result object for initialized canvas state
  if more state is passed around later.
- Avoid moving Lab-specific hover, diagnostic, and neighborhood drawing into default classes.
- Consider whether repeated null checks in subclasses indicate a missing helper method for "all painters are ready".

Decision criteria:

- Do not hide rendering details so much that simulation-specific drawing becomes awkward.
- Keep Canvas rendering performance straightforward and allocation-light.
- Avoid changing painter APIs unless a separate rendering refactoring requires it.

Recommendation:

Do not prioritize this candidate as a standalone refactoring. Keep canvas and painter ownership in `AbstractMainView`;
only add a tiny readiness helper if it naturally falls out of another edit and clearly removes repeated null-check
noise.

### Candidate 6: Normalize Listener Registration And Cleanup

Problem:

The classes use several listener patterns: stored listeners removed during shutdown, lambda listeners that are not
stored, and callbacks stored as `Runnable`/`Consumer` fields.

Analysis:

Listener lifecycle is a cross-cutting risk in these classes. There are listeners attached from view-models to control
properties, from views to view-model properties, from toolbar controls to descriptor state, and from canvases to mouse
events. Some are stored and removed explicitly. Some are local lambdas attached to controls or properties and rely on
scene lifetime. This mix is common in JavaFX, but it makes it harder to verify shutdown behavior.

Not every listener needs explicit removal. Canvas event handlers and child-control listeners that are owned and discarded
with the view are usually acceptable. Listeners that cross between long-lived view-models and views, or that retain
substantial state, deserve more explicit cleanup. The selected-cell listener registered by `AbstractDefaultMainView` is
a good example to inspect because it attaches the view to a view-model property and is not currently stored for removal.

This candidate pairs naturally with the edit-toolbar extraction and observation-binding cleanup. Those changes can make
listener ownership more explicit without introducing a broad listener registry. A generic listener manager should be
avoided unless repeated patterns remain after narrower cleanup.

Possible direction:

- Inventory every listener registered by the four core classes and by Lab's direct subclasses.
- Classify each listener as scene-lifetime, simulation-lifetime, or temporary.
- Store and remove listeners when they can outlive the view/model or retain substantial state.
- Leave scene-lifetime listeners in place only when the ownership is clear and documented by structure.

Decision criteria:

- Prefer explicit cleanup for listeners attached across view/view-model boundaries.
- Avoid noisy cleanup for controls that are created and discarded with their owning view.
- Verify shutdown remains idempotent enough for current application lifecycle expectations.

Recommendation:

Implement this candidate opportunistically alongside Candidates 2 and 4, not as a broad rewrite. Store and remove
cross-boundary listeners where ownership is unclear, document scene-lifetime listeners by structure, and avoid adding a
generic listener registry unless a concrete duplication pattern remains.

## Suggested Refactoring Sequence

1. Inventory call sites and subclass relationships for the four core classes plus Lab direct subclasses.
2. Write a responsibility table before changing code; confirm which methods are common, default-only, and Lab-only.
3. Pick one narrow candidate, preferably selection support or observation binding cleanup.
4. Add or update focused tests for behavior that changes ownership.
5. Refactor with minimal public API changes.
6. Run the relevant test suite with the Gradle Wrapper from the repository root.
7. If a second candidate is still valuable, repeat the same narrow cycle rather than bundling unrelated moves.

## Guardrails For The Future Agent

- Keep Lab as the compatibility test for the thin abstract pair.
- Do not force Lab into `DefaultMainViewModel` or `AbstractDefaultMainView` unless its interaction model genuinely
  becomes default-compatible.
- Preserve public APIs unless a change is explicitly justified and all call sites are updated.
- Keep simulation packages isolated; shared infrastructure belongs in `simulations.core`.
- Do not move model/business logic into view classes or JavaFX UI state into model classes.
- Do not introduce FXML.
- Keep user-facing text localized through resource bundles if new UI text is introduced.
- Keep changes small enough that failures can be traced to one ownership change at a time.

## Validation Checklist

For any implementation based on this plan, verify at least:

- All existing regular simulations still start, pause, resume, cancel, and render through the default path.
- Lab still responds to config changes, draw, draw model, draw test, hover, click selection, and neighborhood highlight
  interactions.
- Observation views still receive last-clicked-coordinate and selected-cell updates.
- Edit mode still appears only when descriptors exist and remains disabled outside `PAUSED`.
- Shutdown removes or neutralizes listeners and releases timer/executor resources as before.
- The Gradle test task passes, or any unrelated pre-existing failures are documented.

## Expected Outcome

The best outcome is a clearer separation like this:

- `AbstractMainView`: common shell, canvas hosting, notification placement, and click routing.
- `AbstractMainViewModel`: common state, notification, click history, and common observation cleanup.
- `AbstractDefaultMainView`: regular timed-simulation rendering flow and redraw policy.
- `DefaultMainViewModel`: regular timed-simulation orchestration, with selected helper extractions where they reduce
  complexity.
- Lab classes: diagnostic rendering and Lab-only interaction behavior, using shared core helpers only where they fit
  naturally.

The future agent should treat this as an optimization and clarity pass, not as a mandate to collapse the two-layer
design.
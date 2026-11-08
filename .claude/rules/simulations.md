---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/simulations/**/*.java"
---

# Simulations Instructions

Rules for production code in `de.mkalb.etpetssim.simulations`; repository-wide, Java, and UI instructions still apply.

## Package Structure

Regular 2D simulations use:

- `model`: domain state, business rules, grid entities
- `view`: JavaFX scene graph, rendering, UI components
- `viewmodel`: JavaFX properties, bindings, UI state
- `shared`: optional layer-neutral types used by the model and by `view` or `viewmodel`
- Package-root `<SimulationName>Factory` exposing `createMainView()`

Both `<simulation>.model` and `simulations.core.model` belong to the MVVM model layer and hold the
simulation-specific part; `engine.model` is the generic grid model (`GridModel` and its implementations) they use.

Special cases:

- `start`: start screen only; no model/view/viewmodel structure
- `lab`: development/testing showcase, not a simulation

## Package Isolation

- Keep all simulation-specific code in `de.mkalb.etpetssim.simulations.<name>`.
- Production code outside a simulation must not access its types; only `simulations.core.SimulationFactory` may
  import the package-root `<SimulationName>Factory` classes.
- Simulations must not depend on each other; shared infrastructure belongs in `simulations.core`.

## Consistency and Registration

Before creating or changing a simulation, review relevant existing implementations, for example:

- Agent-based examples: `wator`, `etpets`, `snake`, `rebounding`
- Cellular automata examples: `conway`, `forest`, `langton`

Keep the cross-cutting files in sync, for example:

| Change                                | Update                                                                  |
|---------------------------------------|-------------------------------------------------------------------------|
| Enum metadata (title, CSS, CLI alias) | `SimulationType.java`, `SimulationTypeTest.java`, localization          |
| Entity/display changes                | `docs/simulations/Simulation_Entity_Catalog.md`                         |
| Localization keys                     | All `messages_*.properties` files (see `i18n.md`)                       |
| Factory wiring                        | `simulations/core/SimulationFactory.java`, `SimulationFactoryTest.java` |

Lists of simulations (e.g., the README table and gallery, and the entity catalog) follow the declaration order of
`SimulationType`.

## MVVM Dependencies

### Model

- Contains domain state and business rules only.
- Must not import JavaFX scene graph, controls, properties, bindings, stages, or animation types.
- May use neutral JavaFX value types (`Color`, `Image`, `Duration`) as plain value carriers.
- May depend on same-simulation `shared`; must not depend on `view` or `viewmodel`.
- Expose a catalog of predefined values (e.g., `ConwayPatterns`, `SnakeMoveStrategies`, or the metrics of a statistics
  class) through a noun-style accessor that returns a constant unmodifiable list (`choices()`, `strategies()`,
  `metrics()`); keep the constants and factories that only build the catalog private, also when tests used them, and
  test them through the accessor.
- Keep an early return that avoids a random draw (e.g., when there is no tie among the candidates) and explain it in
  a comment; removing it changes the random sequence of a seed.

### View

- Contains JavaFX scene graph components and rendering only.
- Must not contain domain/business logic or mutate model rules directly.
- May read `model.entity` types directly for rendering colors, labels, or display strings.
- Must access non-rendering domain data through ViewModel accessors.
- May depend on same-simulation `shared`.

### ViewModel

- Contains UI state, formatting, and interaction logic only; delegate domain computation to `model`.
- Expose state as JavaFX `Property`/`ObservableValue` or repository input wrappers.
- Expose plain values through `getX()` accessors; model and engine types use noun-style accessors.
- May depend on same-simulation `shared`.

### Shared

- Exists because the model must not depend on `view` or `viewmodel`; `simulations.core` uses the same sub-packages.
- Put a `<Simulation>UserActionContext` and its parameter types in `shared`.
- Put types used only by `view` and `viewmodel` in `viewmodel`; rule preset families (`*RulePreset*`) stay in
  `shared` next to the rule type they describe.
- May depend on the same simulation's `model.entity` and `model.strategy`; must not depend on the `model` package
  itself, `view`, or `viewmodel`.
- Must not import JavaFX UI/property/binding types; neutral value types such as `Color` are allowed.

## Entity Design

### Naming

- Entity names are local to a simulation; see `docs/simulations/Simulation_Entity_Catalog.md`.
- Use one simulation-specific root entity contract (e.g., `WatorEntity`, `SugarEntity`).
- For single-entity-enum simulations, use that enum as the primary entity type (e.g., `ConwayEntity`).
- Prefer role-focused names instead of repeating the simulation prefix.
- Prefer established names where applicable, including `NoAgent`, `NoResource`, `TerrainConstant`, and
  `EntityDescriptors`.
- Use one term per concept within a simulation for a grid layer and for the components, accessors, and variables
  that refer to it, following the established type names (e.g., `terrainModel` and `terrainEntity` for
  `TerrainConstant` layers); descriptor IDs and resource keys that refer to a layer use the same term.
- Use suffix `Base` for abstract entity base classes (e.g., `CreatureBase`, `ResourceBase`).
- Use concise domain names for value records (e.g., `PetGenome`, `PetTraits`).
- Prefer domain-specific factories (e.g., `CreatureFactory`) over generic `EntityFactory`.

### Structure

- Keep entity types in `model.entity`, even when views read them for rendering.
- Use sealed interfaces/classes for closed entity sets.
- Implement `GridEntity` or `ConstantGridEntity` as appropriate.
- Declare the `DESCRIPTOR_ID_*` constants of a simulation in its root entity contract (e.g., `SnakeEntity`), not in
  sub-interfaces or implementations.
- Use records for immutable value carriers; use classes for stateful agents/resources.

## Reusable Infrastructure

Check `simulations.core` before adding simulation-local infrastructure. Relevant examples include:

- Views: `AbstractMainView`, `AbstractDefaultMainView`, `AbstractObservationView`, `AbstractControlView`,
  `AbstractConfigView`
- ViewModels: `AbstractMainViewModel`, `AbstractConfigViewModel`, `DefaultMainViewModel`,
  `DefaultControlViewModel`, `DefaultObservationViewModel`
- View helpers: `CellDrawer`, `CoordinateDrawer`, `DrawCallThrottler`, `DefaultControlView`
- ViewModel/model helpers: `SeedProperty`, `SimulationConfig`

Use prefix `Abstract` only for classes that must be abstract; use prefix `Base` for complete base implementations that
are meant to be extended (e.g., `BaseTimedSimulationStatistics`).

## JavaFX UI Patterns

### UI Construction

- Build scene graphs programmatically; do not use FXML.
- Use `FXComponentFactory` for standard styled controls; direct instantiation is fine for one-off layouts.
- Set the tooltip of a label and its control through `FXComponentFactory` with one shared `Tooltip`
  (`createLabeled*(..)`, or `createLabeledControl(..)` for a custom control); edit toolbar buttons get their tooltip
  key from `SimulationUserActionDescriptor`. Create `Tooltip` instances directly only for computed texts without a
  fixed key (e.g., the min and max values in `AbstractObservationView`).
- Keep `Scene`, `Stage`, and root wiring in entry-point/View classes.

### Properties and Bindings

- Prefer JavaFX Property/Binding APIs over manual listeners for derived UI state.
- Expose ViewModel state with `xProperty()` accessors; also provide `getX()`/`setX(...)` for plain JavaFX properties.
- Keep property field types compatible with public accessor return types.
- Do not mutate a bound property directly; update its source.

### Threading

- Mutate scene graph and `Property` values only on the JavaFX Application Thread; pass model and background updates
  through `Platform.runLater(...)` and re-check relevant state inside the runnable.
- Do not block the JavaFX Application Thread with long computation or I/O.
- Drive periodic simulation steps through an encapsulated driver (e.g., `SimulationTimer`), not through a `Timeline`
  in a View; use `Timeline`/`AnimationTimer` only for UI ticks and frame rendering.

### CSS and Styling

- Put CSS under `app/src/main/resources/css/`: global rules in the shared `scene.css`, simulation-specific CSS only
  when needed beyond it.
- Style with CSS classes, not in Java code or with inline `setStyle(...)`; Canvas rendering and other APIs that CSS
  cannot style are exempt.
- Reference style classes via constants: `FXStyleClasses` for shared names, `<Simulation>StyleClasses` (e.g.,
  `ConwayStyleClasses`) for simulation-local names.
- Use lower-kebab-case style class names and JavaFX looked-up colors (`-fx-*`) for shared theme values.

### Resources and Internationalization

- Load images, CSS (`AppResources.getCssUrl(..)`), and other UI resources through `AppResources`.
- Access bundles through `AppLocalization`; `i18n.md` describes the bundles and the localization key constants.

## Factory Pattern

Each simulation package root must provide a factory:

- Name it `<SimulationName>Factory` and make it a `public` utility class (`java.md`).
- Regular simulations provide `public static SimulationMainView createMainView()`; `StartFactory` may accept its
  `Stage` navigation dependencies.
- Let `simulations.core.SimulationFactory` wrap the returned view in `SimulationInstance`.
- Wire model, ViewModel, and view components; delegate complex construction to layer-specific `create...` methods.

## Canvas Rendering

Use `CellDrawer` from `simulations.core.view`:

- In `CellDrawer.draw(...)`, call `painter.drawCell(...)` for supported shapes and
  `painter.drawCenteredTextInCell(...)` for text or emoji overlays.
- Keep custom rendering in simulation-specific View classes.
- Use `DrawCallThrottler` to avoid excessive redraws; prefer batch updates for many cell changes.

## Configuration and Input

- Use `AbstractConfigViewModel` or a simulation-specific config ViewModel.
- Expose config as JavaFX properties or repository input wrappers (`InputDoubleProperty`, `InputIntegerProperty`,
  `InputChoiceProperty`).
- Provide sensible defaults and validate before passing config to the Model layer.
- Keep the defaults, limits, and allowed values of a config in `<Simulation>Constraints` in the `model` package:
  per component `<COMPONENT>_DEFAULT` and either `_MIN`, `_MAX`, and, for integer spinners, `_STEP`, or `_VALUES`
  for selections; a fixed value has only `_DEFAULT`. Name the constants after the config component, group them
  with the line comments and in the order of the `*Config` components, and name the inclusive upper limit of
  combined percentages `COMBINED_PERCENT_MAX`. Order the constants of a component `_DEFAULT`, `_MIN`, `_MAX`, `_STEP`
  or `_DEFAULT`, `_VALUES`, unless the default refers to a bound.
- Keep fixed tuning values of the simulation logic that are not part of the config in `<Simulation>Balance` in the
  `model` package; use the suffixes `_DEFAULT`, `_MIN`, and `_MAX` and their order for value ranges as in
  `*Constraints`.
- `*Config` records are created from unvalidated input and have no validating constructor; check them with `isValid()`
  (which calls `isBaseValid()` first) before passing them to the Model layer.
- Order `*Config` components in four groups, each introduced by a line comment: `// Structure` (cell shape, edge
  behavior, width, height), `// Layout` (cell edge length, display mode, other rendering options),
  `// Initialization` (`seed` first, then the initial values), and `// Rules` (`neighborhoodMode` first, then the
  rule parameters).
- In `isValid()`, check in this order: `isBaseValid()`, `hasAllowedCoreSelections(..)`, fixed values
  (`hasExpectedSelection(..)`), selection lists (`isAllowedSelection(..)`), the private `hasValidRanges()` and
  `hasValidCombinedRanges()`, then rule objects (e.g., `hasValidTransitionRules()`).

## Model Initialization

- A simulation manager constructor calls `SimulationInitializationCancellation.checkCanceled()` directly before and
  after the grid initialization; loops do not call it directly.
- A loop gets a cancellation checkpoint (`WorkCheckpoints.isCancellationCheckpoint(..)`) when its total iteration
  count can exceed `CANCELLATION_CHECK_INTERVAL` within the limits of the `*Constraints` of its simulation (e.g.,
  loops over all cells or a share of them). Nested loops count their iterations with one running index across all
  levels. Loops with a lower maximum (e.g., over walls, peaks, or a fixed number of agents) get no checkpoint.
- Use the `GridInitializers` methods with a `cancellationCheck` parameter for loops that reach this criterion. When a
  simulation needs such an initializer without one, add an overload with a `cancellationCheck` instead of calling
  the variant without it.

## Lifecycle

- Implement `initializeSimulation(...)` for view-specific setup in `AbstractDefaultMainView` subclasses.
- In `shutdownSimulation()`, unbind explicit bindings, remove explicit listeners (scene-graph-lifetime listeners are
  exempt), stop timers and animations, and clear references to large data structures; do not shut down global
  resources such as `AppLogger`.
- Keep reset behavior in the existing main ViewModel and control workflow; there is no shared `reset()` contract.

## Common Patterns

### Observation and Statistics

- Implement `SimulationObservationViewModel` for custom statistics; use `DefaultObservationViewModel` otherwise.
- Format statistics in the ViewModel and localize labels with `AppLocalization`.

### Control Panel

- Use `DefaultControlView` for standard play/pause/step/reset controls; wire its actions to ViewModel methods.

### Main View Composition

- Extend `AbstractDefaultMainView` for the standard observation/canvas/config layout.
- Implement `buildObservationRegion()` and `buildConfigRegion()` in the corresponding Observation and Config Views.
- `AbstractMainView` supplies the simulation canvas region through `createSimulationRegion()`.
- Use `buildMainRegion()` as the UI composition entry point.

### User Action Context

- Use a plain enum implementing `SimulationUserActionContext` when no action has parameters (e.g.,
  `WatorUserActionContext`).
- Otherwise, use a sealed interface with records for parameterized actions and, only if actions without parameters
  exist, a nested `FixedAction` enum for them (e.g., `ConwayUserActionContext`; `EtpetsUserActionContext` has no
  `FixedAction`).
- Keep the validity check of a user action in the model-side `SimulationUserAction`; the toolbar and views do not
  check per-cell applicability.
- Add user actions that support experiments on the current simulation state; do not add actions that only repeat a
  restart with the same or an adjusted configuration.

## Naming

- Name user-triggered redraw requests `requestDraw...` (for example, `requestDraw()`); their handlers follow
  `handle...`.
- Name implementations of `SynchronousStepLogic` and `AgentStepLogic` with the suffix `StepLogic` (for example,
  `ConwayStepLogic`, `WatorStepLogic`); name implementations of `SimulationStepRunner` with the suffix `StepRunner`
  (for example, `SugarStepRunner`).
- Static `<SimulationName><Layer>Logic` helper classes called from a step runner expose `apply(...)` with the
  parameters grid model, config, further inputs (e.g., `Random`), step index, and statistics, in this order;
  `perform...` is reserved for implementations of the step contracts.
- Constructors of step runners take the grid model, the config, and further inputs (e.g., `Random`), in this order;
  constructors of step logic take the `GridStructure`, the config, and further inputs.
- Model, entity, and strategy types may use domain verbs of their simulation (e.g., `move`, `die`, `reproduce`,
  `consume`, `decideMove`) for state changes and decisions.

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

- Each simulation lives in `de.mkalb.etpetssim.simulations.<name>`.
- Production code outside a simulation must not directly access its simulation-specific types.
- `simulations.core.SimulationFactory` may import package-root `<SimulationName>Factory` classes.
- Simulations must not depend on each other; shared infrastructure belongs in `simulations.core`.
- Keep simulation-specific logic, entities, and UI components inside their simulation package.

## Consistency and Registration

Before creating or changing a simulation, review relevant existing implementations. The following are representative
examples, not an exhaustive list:

- Agent-based examples: `wator`, `etpets`, `snake`, `rebounding`
- Cellular automata examples: `conway`, `forest`, `langton`

Reuse `simulations.core` infrastructure and keep relevant cross-cutting files in sync. Common synchronization points
include:

| Change                                | Update                                                                  |
|---------------------------------------|-------------------------------------------------------------------------|
| Enum metadata (title, CSS, CLI alias) | `SimulationType.java`, `SimulationTypeTest.java`, localization          |
| Entity/display changes                | `docs/simulations/Simulation_Entity_Catalog.md`                         |
| Localization keys                     | All `messages_*.properties` files                                       |
| Factory wiring                        | `simulations/core/SimulationFactory.java`, `SimulationFactoryTest.java` |

Lists of simulations (e.g., the README table and gallery, and the entity catalog) follow the declaration order of
`SimulationType`.

## MVVM Dependencies

### Model

- Contains domain state and business rules only.
- Must not import JavaFX scene graph, controls, properties, bindings, stages, or animation types.
- May use neutral JavaFX value types (`Color`, `Image`, `Duration`) as plain value carriers.
- May depend on same-simulation `shared`; must not depend on `view` or `viewmodel`.
- Expose a catalog of predefined values (e.g., `ConwayPatterns`, `SnakeMoveStrategies`) through a noun-style accessor
  that returns a constant unmodifiable list (`choices()`, `strategies()`); keep the constants and factories that only
  build the catalog private, also when tests used them, and test them through the accessor.
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
- Marshal model/background updates to the JavaFX Application Thread via `Platform.runLater(...)`.
- Expose plain values through `getX()` accessors; model and engine types use noun-style accessors.
- May depend on same-simulation `shared`.

### Shared

- Contains layer-neutral enums, records, or value objects that the model uses together with `view` or `viewmodel`.
- Exists because the model must not depend on `view` or `viewmodel`; `simulations.core` uses the same sub-packages.
- Put a `<Simulation>UserActionContext` and its parameter types in `shared`.
- Put types used only by `view` and `viewmodel` in `viewmodel`; rule preset families (`*RulePreset*`) stay in
  `shared` next to the rule type they describe.
- May depend on the same simulation's `model.entity` and `model.strategy`; must not depend on the `model` package
  itself, `view`, or `viewmodel`.
- Must not import JavaFX UI/property/binding types; neutral value types such as `Color` are allowed.
- Entity types stay in `model.entity`, even when views read them for rendering.

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

- Keep entity types in `model.entity`.
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
- Keep `Scene`, `Stage`, and root wiring in entry-point/View classes.

### Properties and Bindings

- Prefer JavaFX Property/Binding APIs over manual listeners for derived UI state.
- Expose ViewModel state with `xProperty()` accessors; also provide `getX()`/`setX(...)` for plain JavaFX properties.
- Keep property field types compatible with public accessor return types.
- Unbind explicit bindings and remove explicit listeners in shutdown methods; scene-graph-lifetime listeners are exempt.
- Do not mutate a bound property directly; update its source.

### Threading

- Mutate scene graph and `Property` values only on the JavaFX Application Thread.
- Use `Platform.runLater(...)` for background-to-FX updates and re-check relevant state inside the runnable.
- Do not block the JavaFX Application Thread with long computation or I/O.
- Use `Timeline`/`AnimationTimer` for periodic UI ticks and frame rendering.
- Encapsulate periodic simulation drivers (e.g., `SimulationTimer`) instead of using `Timeline` directly in Views.

### CSS and Styling

- Put CSS under `app/src/main/resources/css/`; use shared `scene.css` for global rules.
- Add simulation-specific CSS only when needed beyond `scene.css`.
- Resolve CSS through `AppResources.getCssUrl(...)`.
- Prefer CSS over hardcoded Java styling; Canvas rendering and other non-CSS-styleable APIs are exempt.
- Do not use inline `setStyle(...)`; use style classes.
- Reference style classes via constants: `FXStyleClasses` for shared names, `XStyleClasses` for simulation-local names.
- Use lower-kebab-case style class names and JavaFX looked-up colors (`-fx-*`) for shared theme values.

### Resources and Internationalization

- Load images, CSS, and UI resources through `AppResources`.
- Access bundles through `AppLocalization`.
- Put cross-cutting keys in `AppLocalizationKeys`; simulation-local keys may be private constants in the consuming View.

## Factory Pattern

Each simulation package root must provide a factory:

- Name `<SimulationName>Factory`, `public final`, private constructor.
- Regular simulations provide `public static SimulationMainView createMainView()`; `StartFactory` may accept its
  `Stage` navigation dependencies.
- Let `simulations.core.SimulationFactory` wrap the returned view in `SimulationInstance`.
- Wire model, ViewModel, and view components; delegate complex construction to layer-specific `create...` methods.

## Canvas Rendering

Use `CellDrawer` from `simulations.core.view`:

- In `CellDrawer.draw(...)`, call `painter.drawCell(...)` for supported shapes and
  `painter.drawCenteredTextInCell(...)` for text or emoji overlays.
- Keep custom rendering in simulation-specific View classes.
- Cache drawing parameters when possible.
- Use `DrawCallThrottler` to avoid excessive redraws.
- Avoid `info` or higher logging in tight rendering loops; prefer batch updates for many cell changes.
- Use `Canvas.setCache(true)` for static overlays when appropriate.

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

## Lifecycle

- Implement `initializeSimulation(...)` for view-specific setup in `AbstractDefaultMainView` subclasses.
- Use `shutdownSimulation()` to clean up, unbind listeners, and release view or ViewModel resources.
- Keep reset behavior in the existing main ViewModel and control workflow; there is no shared `reset()` contract.

Shutdown checklist:

- Unbind explicit bindings and remove explicit listeners.
- Stop timers and animations.
- Clear references to large data structures.
- Do not shut down global/singleton resources such as `AppLogger`.

## Common Patterns

### Observation and Statistics

- Implement `SimulationObservationViewModel` for custom statistics; use `DefaultObservationViewModel` otherwise.
- Update observation properties from Model via ViewModel.
- Format statistics in ViewModel and localize labels with `AppLocalization`.

### Control Panel

- Use `DefaultControlView` for standard play/pause/step/reset controls.
- Wire control actions to ViewModel methods, not directly to Model.
- Update control state from simulation state.

### Main View Composition

- Extend `AbstractDefaultMainView` for the standard observation/canvas/config layout.
- Implement `buildObservationRegion()` and `buildConfigRegion()` in the corresponding Observation and Config Views.
- `AbstractMainView` supplies the simulation canvas region through `createSimulationRegion()`.
- Use `buildMainRegion()` as the UI composition entry point.
- Keep layout in View classes, not ViewModel.

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

- Use `draw...` for canvas rendering methods.
- Name user-triggered redraw requests `requestDraw...` (for example, `requestDraw()`); their handlers follow
  `handle...`.
- Name implementations of `SynchronousStepLogic` and `AgentStepLogic` with the suffix `StepLogic` (for example,
  `ConwayStepLogic`, `WatorStepLogic`); name implementations of `SimulationStepRunner` with the suffix `StepRunner`
  (for example, `SugarStepRunner`).
- Static `<SimulationName><Layer>Logic` helper classes called from a step runner expose `apply(...)` with the
  parameters grid model, config, further inputs (e.g., `Random`), step index, and statistics, in this order;
  `perform...` is reserved for implementations of the step contracts.
- Model, entity, and strategy types may use domain verbs of their simulation (e.g., `move`, `die`, `reproduce`,
  `consume`, `decideMove`) for state changes and decisions.

## Anti-Patterns

Do not:

- Mix domain logic into Views or UI state into Models.
- Import JavaFX UI types from Model packages.
- Mutate JavaFX properties from background threads.
- Block the JavaFX Application Thread.
- Use FXML or `FXMLLoader`.
- Hardcode style values that belong in CSS.
- Create simulations without a factory.
- Duplicate infrastructure already in `simulations.core`.
- Log at `info` or higher in tight rendering loops.

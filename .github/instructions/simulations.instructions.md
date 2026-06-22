---
applyTo: "**/src/main/java/de/mkalb/etpetssim/simulations/**/*.java"
description: "MVVM and simulation rules for de.mkalb.etpetssim.simulations."
---

# Simulations Instructions

Rules for production code in `de.mkalb.etpetssim.simulations`; repository-wide, Java, and UI instructions still apply.

## Package Structure

Regular 2D simulations use:

- `model`: domain state, business rules, grid entities
- `view`: JavaFX scene graph, rendering, UI components
- `viewmodel`: JavaFX properties, bindings, UI state
- `shared`: optional layer-neutral types used by two or more layers
- Package-root `<SimulationName>Factory` exposing `createMainView()`

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

Before creating or changing a simulation, review similar implementations:

- Agent-based: `wator`, `etpets`, `snake`, `rebounding`
- Cellular automata: `conway`, `forest`, `langton`

Reuse `simulations.core` infrastructure and keep cross-cutting files in sync:

| Change                                | Update                                                                  |
|---------------------------------------|-------------------------------------------------------------------------|
| Enum metadata (title, CSS, CLI alias) | `SimulationType.java`, `SimulationTypeTest.java`, localization          |
| Entity/display changes                | `docs/simulations/Simulation_Entity_Catalog.md`                         |
| Localization keys                     | All `messages_*.properties` files                                       |
| Factory wiring                        | `simulations/core/SimulationFactory.java`, `SimulationFactoryTest.java` |

## MVVM Dependencies

### Model

- Contains domain state and business rules only.
- Must not import JavaFX scene graph, controls, properties, bindings, stages, or animation types.
- May use neutral JavaFX value types (`Color`, `Image`, `Duration`) as plain value carriers.
- May depend on same-simulation `shared`; must not depend on `view` or `viewmodel`.

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
- May depend on same-simulation `shared`.

### Shared

- Contains only layer-neutral enums, records, or value objects used by multiple layers.
- Must not depend on `model`, `view`, or `viewmodel`.
- Must not import JavaFX UI/property/binding types; neutral value types such as `Color` are allowed.
- Entity types stay in `model.entity`, even when views read them for rendering.

## Javadoc

- `simulations.core` and subpackages are shared infrastructure: document public/protected types, members, and constants.
- Simulation-specific packages are internal: add Javadoc only for complex domain logic or non-obvious behavior.

## Entity Design

### Naming

- Entity names are local to a simulation; see `docs/simulations/Simulation_Entity_Catalog.md`.
- Use one simulation-specific root entity contract (e.g., `WatorEntity`, `SugarEntity`).
- For single-entity-enum simulations, use that enum as the primary entity type (e.g., `ConwayEntity`).
- Prefer role-focused names instead of repeating the simulation prefix.
- Standard names: `NoAgent`, `NoResource`, `TerrainConstant`, `EntityDescriptors`.
- Use suffix `Base` for abstract bases.
- Use concise domain names for value records (e.g., `PetGenome`, `PetTraits`).
- Prefer domain-specific factories (e.g., `CreatureFactory`) over generic `EntityFactory`.

### Structure

- Keep entity types in `model.entity`.
- Use sealed interfaces/classes for closed entity sets.
- Implement `GridEntity` or `ConstantGridEntity` as appropriate.
- Use records for immutable value carriers; use classes for stateful agents/resources.

## Reusable Infrastructure

Check `simulations.core` before adding simulation-local infrastructure:

- Views: `AbstractMainView`, `AbstractDefaultMainView`, `AbstractObservationView`, `AbstractControlView`,
  `AbstractConfigView`
- ViewModels: `AbstractMainViewModel`, `AbstractConfigViewModel`, `DefaultMainViewModel`,
  `DefaultControlViewModel`, `DefaultObservationViewModel`
- View helpers: `CellDrawer`, `CoordinateDrawer`, `DrawCallThrottler`, `DefaultControlView`
- ViewModel/model helpers: `SeedProperty`, `SimulationConfig`

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
- Provide `public static SimulationMainView createMainView()`.
- Let `simulations.core.SimulationFactory` wrap the returned view in `SimulationInstance`.
- Wire model, ViewModel, and view components; delegate complex construction to layer-specific builder methods.

## Canvas Rendering

Use `CellDrawer` from `simulations.core.view`:

- Call `drawCell(...)` for supported shapes and `drawCenteredTextInCell(...)` for text or emoji overlays.
- Keep custom rendering in simulation-specific View classes.
- Cache drawing parameters when possible.
- Use `DrawCallThrottler` to avoid excessive redraws.
- Avoid `info` or higher logging in tight rendering loops; prefer batch updates for many cell changes.
- Use `Canvas.setCache(true)` for static overlays when appropriate.

## Configuration and Input

- Use `AbstractConfigViewModel` or a simulation-specific config ViewModel.
- Expose config as JavaFX properties or repository input wrappers (`InputDoubleProperty`, `InputIntegerProperty`,
  `InputEnumProperty`).
- Provide sensible defaults and validate before passing config to the Model layer.

## Lifecycle

Implement:

- `initialize(...)`: one-time setup
- `shutdown()`: cleanup, unbind listeners, release resources
- `reset()`: return to initial state without full reconstruction

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
- Override `buildObservationRegion()`, `buildCanvasRegion()`, and `buildConfigRegion()` as needed.
- Use `buildMainRegion()` as the UI composition entry point.
- Keep layout in View classes, not ViewModel.

## Naming

- Use `draw...` for canvas rendering methods.
- Use `requestDraw()` as the standard redraw callback name.

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

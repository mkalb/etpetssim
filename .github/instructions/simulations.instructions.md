---
applyTo: "**/simulations/**/*.java"
description: "MVVM and simulation-specific rules for de.mkalb.etpetssim.simulations."
---

# Simulations Instructions

MVVM architecture and simulation-specific rules for `de.mkalb.etpetssim.simulations`.
These rules complement the project-wide, Java, UI, and test instructions.

## Package Structure

Each regular 2D simulation package uses three required sub-packages, optional `shared`, and a package-root factory:

- `model`: domain state, business rules, and grid entities
- `view`: JavaFX scene-graph, rendering, and UI components
- `viewmodel`: JavaFX properties, bindings, and UI state
- `shared`: optional layer-neutral types used by two or more layers
- `<SimulationName>Factory`: package-root factory exposing `createMainView()`

### Special-Status Simulations

- **start**: Start screen without model/view/viewmodel structure; contains only `StartFactory` and `StartMainView`
- **lab**: Testing and showcase environment, not a simulation; used for testing infrastructure and demonstrating
  features

## Package Isolation

Each simulation must be in its own sub-package under `de.mkalb.etpetssim.simulations.<name>` (e.g., `wator`,
`langton`, `etpets`). Simulation packages are isolated:

- No production code outside that simulation package may directly access simulation-specific types (classes in `model`,
  `view`, `viewmodel`, or `shared` of a simulation package).
- The central `simulations.core.SimulationFactory` may import package-root `<SimulationName>Factory` classes to create
  simulation instances.
- Simulations must not depend on each other; shared infrastructure belongs in `simulations.core`.
- Keep simulation-specific logic, entities, and UI components within the simulation's own package.

## Consistency Across Simulations

When creating or changing a simulation, first review similar simulations and reuse established patterns:

- Agent-based examples: `wator`, `etpets`, `snake`, `rebounding`
- Cellular automata examples: `conway`, `forest`, `langton`
- Follow comparable package structure, factory wiring, ViewModel APIs, View construction, and entity design.
- Reuse `simulations.core` infrastructure instead of duplicating shared behavior.

## Registering and Maintaining Simulations

When modifying an existing simulation, keep the following cross-cutting files in sync with any changes:

| What changed                          | Files to update                                                         |
|---------------------------------------|-------------------------------------------------------------------------|
| Enum metadata (title, CSS, CLI alias) | `SimulationType.java`, `SimulationTypeTest.java`, localization files    |
| Entity types added, removed, renamed  | `docs/simulations/Simulation_Entity_Catalog.md`                         |
| Display properties (colors, emoji)    | `docs/simulations/Simulation_Entity_Catalog.md`                         |
| Localization keys changed             | All `messages_*.properties` files                                       |
| Factory wiring changed                | `simulations/core/SimulationFactory.java`, `SimulationFactoryTest.java` |

## MVVM Layer Dependencies

### Model Layer

- Must contain only domain state and business rules.
- Must not import JavaFX scene-graph, control, property, or binding types (`javafx.scene.*`, `javafx.beans.*`,
  `javafx.stage.*`, `javafx.animation.*`).
- May use neutral JavaFX value types (`javafx.scene.paint.Color`, `javafx.scene.image.Image`,
  `javafx.util.Duration`) when they act as plain value carriers without UI behavior.
- May depend on `shared` within the same simulation package.
- Must not depend on `view` or `viewmodel`.

### View Layer

- Must contain only JavaFX scene-graph components and rendering logic.
- Must not contain domain/business logic (simulation rules, model mutation).
- May read entity state from `model.entity` types directly for rendering purposes (pattern-matching on entity subtypes
  to resolve colors, labels, or display strings).
- Must access non-rendering domain data only through ViewModel accessors.
- May depend on `shared` within the same simulation package.

### ViewModel Layer

- Must contain only UI state, formatting, and interaction logic.
- Must not contain domain/business logic; delegate computation to `model`.
- Must expose state as JavaFX `Property`/`ObservableValue`, or as repository wrapper types
  (`InputDoubleProperty`, `InputIntegerProperty`, `InputEnumProperty`).
- Must marshal Model/background updates to JavaFX Application Thread via `Platform.runLater(...)`.
- May depend on `shared` within the same simulation package.

### Shared Layer

- Must contain only layer-neutral types (enums, records, value objects).
- Must be referenced by more than one of `model`, `view`, or `viewmodel`.
- Must not depend on `model`, `view`, or `viewmodel`.
- Must not import JavaFX scene-graph, control, property, or binding types; neutral value types
  (`javafx.scene.paint.Color`) are permitted when they act as plain value carriers.
- Entity types defined in `model.entity` remain in `model.entity` even when the view reads them for rendering; they do
  not move to `shared`.

## Javadoc Requirements

- `simulations.core` and subpackages are shared infrastructure: document public/protected types, members, and constants.
- Simulation-specific packages are internal implementation details: add Javadoc only for complex domain logic or
  non-obvious behavior.

## Entity Design

### Entity Naming

Entity names in `.../model/entity` packages are local to each simulation and need not be globally unique.
See `docs/simulations/Simulation_Entity_Catalog.md` for a reference of all existing entity types per simulation.

- Use one simulation-specific root entity contract per simulation (e.g., `EtpetsEntity`, `WatorEntity`,
  `LangtonEntity`, `SugarEntity`, `SnakeEntity`, `ReboundingEntity`).
- For simple simulations with a single entity enum, use that enum itself as the primary entity type
  (e.g., `ConwayEntity`, `ForestEntity`, `LabEntity`).
- Prefer role-focused names inside `.../model/entity` packages instead of repeating the simulation prefix.
- Use standard names where applicable: `NoAgent`, `NoResource`, `TerrainConstant`, `EntityDescriptors`.
- Use suffix `Base` for abstract base classes.
- For value object records, use concise domain-specific names (e.g., `PetGenome`, `PetTraits`).
- For factories, prefer domain-specific names (e.g., `CreatureFactory`) over generic names like `EntityFactory`.

### Entity Structure

- Keep entity types in `model.entity` package.
- Use sealed interfaces or classes when entity type is closed.
- Implement `GridEntity` or `ConstantGridEntity` as appropriate.
- Use records for immutable value carriers (genome, traits, coordinates).
- Use classes for stateful entities with behavior (agents, resources with mutable state).

## Reusable Infrastructure (simulations.core)

Use reusable components from `simulations.core` before adding simulation-local infrastructure:

- View base classes: `AbstractMainView`, `AbstractDefaultMainView`, `AbstractObservationView`, `AbstractControlView`,
  `AbstractConfigView`
- ViewModel base classes: `AbstractMainViewModel`, `AbstractConfigViewModel`, `DefaultMainViewModel`,
  `DefaultControlViewModel`, `DefaultObservationViewModel`
- View helpers: `CellDrawer`, `CoordinateDrawer`, `DrawCallThrottler`, `DefaultControlView`
- ViewModel helpers: `SeedProperty`
- Model helpers: `SimulationConfig`

## JavaFX UI Patterns

### UI Construction

- Build scene graph programmatically in Java; do not use FXML.
- Use `FXComponentFactory` for standard styled controls (labels, buttons, text fields, sliders, spinners).
- Direct instantiation is acceptable for one-off layouts that `FXComponentFactory` does not cover.
- Keep `Scene`, `Stage`, and root container wiring in dedicated entry-point/View classes.

### Properties and Bindings

- Prefer JavaFX Property/Binding APIs over manual listener wiring for derived UI state.
- Expose ViewModel state via accessor methods named `xProperty()`.
- When exposing a plain JavaFX property, also provide `getX()`/`setX(...)` accessors consistent with the property type.
- Keep property field declared types compatible with their public accessor return types (do not expose a
  `SimpleStringProperty` field as `ObjectProperty<String>`).
- Unbind explicit bindings and remove explicit listeners in shutdown methods (e.g., `shutdownSimulation()`); listeners
  attached for the lifetime of the scene graph are exempt.
- Do not mutate a bound property directly; update its source instead.

### Threading

- Mutate JavaFX scene graph and `Property` values only on the JavaFX Application Thread.
- Use `Platform.runLater(...)` to marshal updates from background threads to the FX thread.
- Re-check relevant state (e.g., simulation state) inside the `Platform.runLater(...)` body before applying updates.
- Do not block the JavaFX Application Thread with long-running computation or I/O.
- Use `Timeline`/`AnimationTimer` for periodic UI ticks and frame-driven rendering.
- Encapsulate periodic simulation drivers (e.g., `SimulationTimer`) instead of using `Timeline` directly in Views.

### CSS and Styling

- Define visual styling in CSS files under `app/src/main/resources/css/`.
- Use shared `scene.css` for global rules.
- Add a dedicated CSS file per simulation (e.g., `conway.css`, `etpets.css`, `lab.css`) when it needs visual rules
  beyond `scene.css`.
- Resolve CSS resources through `AppResources.getCssUrl(...)` rather than calling `getClass().getResource(...)`
  ad hoc.
- Do not hardcode colors, fonts, or sizing in Java when an equivalent CSS rule is practical; values used only for
  `Canvas` rendering or other non-CSS-styleable APIs are exempt.
- Do not use inline `setStyle(...)` for styling; use style classes instead.
- Reference CSS style classes via constants (no string literals at call sites).
- Use `FXStyleClasses` for cross-cutting style classes.
- Use simulation-local `XStyleClasses` class (e.g., `ConwayStyleClasses`) for style classes that exist only inside one
  simulation package.
- Use lower-kebab-case for CSS style class names (e.g., `simulation-canvas`, `config-vbox`,
  `conway-transitionrules-gridpane`).
- Use JavaFX CSS variables (`-fx-*` looked-up colors) for shared theme values.

### Resources and Internationalization

- Load images, CSS, and other UI resources from the classpath via `AppResources`.
- Access resource bundles via `AppLocalization`.
- Define cross-cutting keys (used from multiple simulations or shared infrastructure) in `AppLocalizationKeys`.
- Simulation-local keys may live as `private static final String` constants in the consuming View class.

## Simulation Factory Pattern

Each simulation package must provide a package-root factory class used by the central
`simulations.core.SimulationFactory`:

- Name pattern: `<SimulationName>Factory` (e.g., `ConwayFactory`, `WatorFactory`, `EtpetsFactory`)
- Place at simulation package root (e.g., `de.mkalb.etpetssim.simulations.conway.ConwayFactory`)
- Make it a `public final class` with a private constructor
- Provide `public static SimulationMainView createMainView()` to instantiate the main view
- Let `simulations.core.SimulationFactory` wrap the returned view in a `SimulationInstance`
- Use factory to wire together model, viewmodel, and view components
- Keep factory simple; delegate complex construction to builder methods in the respective layer classes

## Canvas Rendering

Use `CellDrawer` from `simulations.core.view` for cell rendering:

- Call `drawCell(...)` methods for hexagons, triangles, or squares
- Use `drawCenteredTextInCell(...)` for emoji or text overlays
- Implement custom rendering logic in simulation-specific View classes when needed
- Cache computed drawing parameters (cell size, positions) when possible
- Use `DrawCallThrottler` to avoid excessive canvas redraws

Rendering performance:

- Do not log at `info` or higher inside tight rendering loops
- Prefer batch updates over incremental redraws when many cells change
- Use `Canvas.setCache(true)` for static overlays when appropriate

## Configuration and Input

Use configuration ViewModels to expose simulation parameters:

- Extend `AbstractConfigViewModel` or use simulation-specific config ViewModel
- Expose configuration as JavaFX properties (doubles, integers, booleans, enums)
- Use repository wrapper types (`InputDoubleProperty`, `InputIntegerProperty`, `InputEnumProperty`) for validated input
- Provide sensible default values
- Validate configuration before passing to Model layer

## Lifecycle Management

Implement proper lifecycle methods in ViewModels and Views:

- `initialize(...)`: one-time setup after construction
- `shutdown()`: cleanup, unbind listeners, release resources
- `reset()`: return to initial state without full reconstruction

Shutdown checklist:

- Unbind all explicit bindings
- Remove all explicit listeners
- Stop timers and animations
- Clear references to large data structures
- Do not shutdown global/singleton resources (e.g., `AppLogger`)

## Common Patterns

### Observation and Statistics

- Implement `SimulationObservationViewModel` for custom statistics
- Use `DefaultObservationViewModel` when no custom statistics are needed
- Update observation properties from Model via ViewModel
- Format statistics as user-friendly strings in ViewModel
- Use `AppLocalization` for statistic labels

### Control Panel

- Use `DefaultControlView` for standard play/pause/step/reset controls
- Extend or customize control view for simulation-specific actions
- Wire control actions to ViewModel methods, not directly to Model
- Update control state (enabled/disabled) based on simulation state

### Main View Composition

- Extend `AbstractDefaultMainView` for standard three-panel layout (observation, canvas, config)
- Override `buildObservationRegion()`, `buildCanvasRegion()`, `buildConfigRegion()` as needed
- Use `buildMainRegion()` as the entry point for UI composition
- Keep layout code in View classes; do not construct UI in ViewModel

## Simulation-Specific Naming

- Use `draw...` for canvas rendering methods.
- Use `requestDraw()` as the standard callback name for triggering canvas redraw.

## Anti-Patterns to Avoid

Do not:

- Mix domain logic into View classes (keep it in Model)
- Mix UI state into Model classes (keep it in ViewModel)
- Import JavaFX UI types from Model packages
- Mutate JavaFX properties from background threads
- Block the JavaFX Application Thread
- Use FXML or `FXMLLoader`
- Hardcode colors/fonts/sizing that belong in CSS
- Create simulations without a factory class
- Duplicate infrastructure code already in `simulations.core`
- Log at `info` or higher in tight rendering loops

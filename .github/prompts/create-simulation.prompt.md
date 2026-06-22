---
description: "Create a new simulation in etpetssim. Use when adding a new simulation type to the project."
agent: "agent"
argument-hint: "Display name, Wikipedia URL, reference simulation (any existing simulation; forest/wator/sugar preferred)"
---

# Create New Simulation

Create a new simulation in this project by collecting input in four phases and then completing all seven registration
steps in order.

- Use [simulations.instructions.md](../instructions/simulations.instructions.md) for coding conventions, MVVM
  architecture, entity design, and factory patterns.
- Before changing files, inspect the chosen reference simulation and the current registration targets.
- The user may choose any existing simulation as the structural reference. Prefer `forest`, `wator`, or `sugar` when the
  user has no strong preference; together they cover the main implementation shapes without many special cases.
- When the repository's existing conventions differ from the examples in this prompt, follow the repository.

## Required Input

Collect input from the user in four phases. After each agent proposal, let the user accept or override before
continuing.

### Phase 1 — User provides

Ask for these three items first:

| Input                    | Example value                                                 | Purpose                                              |
|--------------------------|---------------------------------------------------------------|------------------------------------------------------|
| **Display name (EN)**    | `Wa-Tor`                                                      | Title in `messages_en_US.properties` and `README.md` |
| **Wikipedia URL**        | `https://en.wikipedia.org/wiki/Wa-Tor` or none                | Javadoc link and `simulation.<name>.url` key         |
| **Reference simulation** | Any existing simulation; prefer `forest`, `wator`, or `sugar` | Structural template                                  |

### Phase 2 — Agent proposes category and description

Derive these from the **display name (EN)** and **Wikipedia URL** (fetch the article summary if a URL was provided):

| Derived input           | Rule                                                      | Examples                                                      |
|-------------------------|-----------------------------------------------------------|---------------------------------------------------------------|
| **Simulation category** | Infer from the Wikipedia article or display name          | `Agent-Based Model`, `Cellular Automaton`, `Development Tool` |
| **Short description**   | One-sentence English summary of the simulation's behavior | `Predator-prey ecosystem with sharks and fish`                |

### Phase 3 — Agent proposes technical names

Derive these from the confirmed inputs:

| Derived input         | Rule                                                                                                                                                          | Examples                                                                        |
|-----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| **Package name**      | Short, recognizable lowercase abbreviation — not the full name mechanically stripped                                                                          | `wator`, `forest` (not `forestfiremodel`), `sugar` (not `sugarscape`)           |
| **Class name prefix** | Capitalized package name; prefix for infrastructure classes (`*Factory`, `*Config`, `*ConfigViewModel`, `*MainView`) and the root entity contract (`*Entity`) | `Wator` → `WatorFactory`, `WatorConfig`, `WatorMainView`, `WatorEntity`         |
| **Enum constant**     | Uppercase of the display name's natural word boundaries; single proper nouns stay unseparated, multi-word names use underscores                               | `WATOR`, `SUGARSCAPE`, `FOREST_FIRE`                                            |
| **CLI aliases**       | Package name, full name joined, hyphenated variant, short abbreviations, common synonyms or plurals                                                           | `wator`, `wa-tor`; `sugarscape`, `sugar`; `forestfire`, `forest-fire`, `forest` |
| **CSS file needed**   | Default `no`; most simulations do not need a dedicated CSS file                                                                                               | `no`                                                                            |

### Phase 4 — Agent proposes entity and grid design

Propose an entity model and GridModel layer design based on the category, description, and reference simulation. Do not
create entity or model classes until the user confirms or overrides this design.

Compare the candidate with these reference archetypes and choose the closest starting pattern:

- **Forest-style single enum** — for cellular automata or other simulations with a fixed set of passive cell states.
  One `<class name prefix>Entity` enum implementing `ConstantGridEntityDescriptorProvider`, with descriptor metadata on
  the enum constants. One effective entity layer, typically an `ArrayGridModel` exposed through
  `GridCell<...>`/`ReadableGridModel::getGridCell`.
- **Wa-Tor-style single-layer agents** — for agent simulations where each cell holds one effective entity, such as
  terrain plus moving agents. One `<class name prefix>Entity` sealed interface, a `TerrainConstant` enum, role-focused
  agent classes such as `Fish`/`Shark`, an optional abstract base such as `CreatureBase`, and an `EntityDescriptors`
  enum. One effective entity layer, typically an `ArrayGridModel` initialized with terrain.
- **Sugarscape-style layered agents/resources** — when a cell has multiple logical layers, such as resources and agents.
  One `<class name prefix>Entity` sealed interface, layer marker interfaces such as `AgentEntity`/`ResourceEntity`,
  placeholder constants such as `NoAgent`/`NoResource`, stateful role classes such as `Agent`/`Sugar`, a custom
  GridModel
  wrapper such as `SugarGridModel`, a custom cell view record such as `SugarCell`, and an `EntityDescriptors` enum.

Present the proposal for approval:

| Entity/grid design item  | Example value                                                                   |
|--------------------------|---------------------------------------------------------------------------------|
| **Reference archetype**  | `forest`, `wator`, `sugar`, or a named hybrid                                   |
| **Entity pattern**       | `single enum`, `single-layer agents`, or `layered agents/resources`             |
| **GridModel layers**     | Layer count and names, e.g., one entity layer or `resourceModel` + `agentModel` |
| **Cell view type**       | Standard `GridCell<...>` or a custom cell view record such as `SugarCell`       |
| **Entity classes**       | `ForestEntity`; `WatorEntity`, `TerrainConstant`, `Fish`, `Shark`; etc.         |
| **Initial entity roles** | `cell state`, `terrain`, `agent`, `resource`, `empty placeholder`               |
| **Descriptor metadata**  | Descriptor IDs, display keys, colors, emoji, visibility, and default layering   |

The user may approve, remove, rename, or add layers, entity classes, and descriptor metadata. If the domain is unclear,
choose the closest of `forest`, `wator`, or `sugar`, propose the smallest useful scaffold, and mark domain-specific
layers and entities as optional.

### Final Review — Confirm all inputs before proceeding

After Phase 4 is confirmed, present a consolidated summary of **all** collected and derived values:

| #  | Input                | Value |
|----|----------------------|-------|
| 1  | Display name (EN)    |       |
| 2  | Wikipedia URL        |       |
| 3  | Reference simulation |       |
| 4  | Simulation category  |       |
| 5  | Short description    |       |
| 6  | Package name         |       |
| 7  | Class name prefix    |       |
| 8  | Enum constant        |       |
| 9  | CLI aliases          |       |
| 10 | CSS file needed      |       |
| 11 | Reference archetype  |       |
| 12 | Entity pattern       |       |
| 13 | GridModel layers     |       |
| 14 | Cell view type       |       |
| 15 | Entity classes       |       |
| 16 | Initial entity roles |       |
| 17 | Descriptor metadata  |       |

Review the values for consistency and issues (typos, naming-rule violations, mismatched conventions). List any problems
with suggested corrections, then ask for explicit approval. Do not proceed until the user confirms.

## Registration Steps

Complete all seven steps in order. Do not skip any. Run an incremental compile check (`gradlew.bat app:compileJava`)
after the code-producing steps (Steps 1–4) to catch errors early.

### Step 1 — Register `SimulationType` enum constant and tests

Add a constant using the **enum constant** name in
`app/src/main/java/de/mkalb/etpetssim/SimulationType.java` with all constructor arguments:

- `implemented` flag — use `true`
- `showOnStartScreen` flag — use `true`
- title key — `simulation.<package name>.title`
- subtitle key — `simulation.<package name>.subtitle`
- URL key argument — `simulation.<package name>.url` (the resource key itself is optional; see Step 5)
- CSS path — `<package name>.css` if CSS is needed, otherwise empty string `""`
- CLI aliases — the **CLI aliases** list

If a **Wikipedia URL** was provided, add a Javadoc `@see` or `<a href="...">` link matching existing constants. Follow
the declaration order and Javadoc style of existing constants.

Then update `app/src/test/java/de/mkalb/etpetssim/SimulationTypeTest.java`: add the new **enum constant** to every test
method that references individual `SimulationType` constants, following the existing pattern in each method.

### Step 2 — Create simulation packages

Create the package structure under `de.mkalb.etpetssim.simulations.<package name>` with a `package-info.java` in each
package. Each `package-info.java` must use `@NullMarked` as specified in
[java.instructions.md](../instructions/java.instructions.md):

- `de.mkalb.etpetssim.simulations.<package name>` — simulation root
- `de.mkalb.etpetssim.simulations.<package name>.model` — domain state and business rules
- `de.mkalb.etpetssim.simulations.<package name>.model.entity` — entity types
- `de.mkalb.etpetssim.simulations.<package name>.view` — JavaFX scene-graph and rendering
- `de.mkalb.etpetssim.simulations.<package name>.viewmodel` — JavaFX properties, bindings, UI state

Create `de.mkalb.etpetssim.simulations.<package name>.shared` only when the confirmed design needs layer-neutral types
used by multiple layers, or when the reference simulation has a comparable `shared` package. Match the **reference
simulation** for `package-info.java` content and style.

### Step 3 — Create simulation classes

The goal is a **compilable scaffold** — a working skeleton that follows the reference simulation's architecture with
placeholder logic. The developer refines domain-specific behavior afterward.

First, read the **reference simulation's** complete package structure and study every class in its `model`,
`model.entity`, `view`, and `viewmodel` packages, plus `shared` when present. Compare it against `forest`, `wator`, and
`sugar` to avoid forcing the wrong archetype. Understand the class hierarchy, method signatures, constructor wiring, and
how the factory assembles the components.

Then create the corresponding classes, applying the **Phase 4 archetype** chosen above:

- **Factory class** (package root) — a `public final` class with a private constructor and a static `createMainView()`
  method returning `SimulationMainView`. Mirror the reference factory's wiring of model, viewmodel, and view.
- **Model classes** — config record (implementing `SimulationConfig`), constraints class, simulation manager, GridModel
  classes, step logic, and statistics. Build the confirmed Phase 4 GridModel layers: a single standard grid model for
  forest- and wator-style simulations, or a custom wrapper plus custom cell view for sugar-style layered simulations.
  Use
  simple defaults and minimal placeholder logic.
- **Entity classes** (`model.entity`) — create the confirmed Phase 4 entity design. Only the root entity contract uses
  the **class name prefix** (e.g., `WatorEntity`, `SugarEntity`); other entities use role-focused domain names.
- **View classes** — main view, config view, and observation view extending the appropriate abstract base classes.
  Render a basic grid with terrain only; domain-specific rendering comes later.
- **ViewModel classes** — config viewmodel exposing the config record's parameters as properties.
- **Shared classes** — only if the confirmed design needs layer-neutral types, or the reference simulation has
  comparable
  shared types.

Use the confirmed **class name prefix** for all infrastructure classes. Keep everything compilable — no `TODO` method
bodies, no unresolved references. Use the simplest working implementation for each method (return defaults, empty
collections, or delegate to the reference simulation's pattern).

### Step 4 — Wire `SimulationFactory` and tests

Add a case for the new **enum constant** in the switch expression in
`app/src/main/java/de/mkalb/etpetssim/simulations/core/SimulationFactory.java` that calls
`<class name prefix>Factory.createMainView()`.

Then update `app/src/test/java/de/mkalb/etpetssim/simulations/core/SimulationFactoryTest.java` following the existing
pattern: add an individual type test method for the new simulation and update any explicit per-constant references.
Loop-based guard tests should continue to cover all `SimulationType` values automatically.

### Step 5 — Add localization keys

Add the title and subtitle keys to all message properties files, keeping keys sorted alphabetically within each file:

- `app/src/main/resources/i18n/messages_en_US.properties` — **display name (EN)** and English **short description**
- `app/src/main/resources/i18n/messages_de_DE.properties` — German translations of both

If a **Wikipedia URL** was provided, also add `simulation.<package name>.url` with the same URL value in all locales. If
no URL was provided, leave the URL key absent; `SimulationType.url()` treats missing or blank optional text as no URL.

### Step 6 — Update README.md

Add the simulation to the "Simulations" table in `README.md`:

- **Display name (EN)** (linked to the **Wikipedia URL** if provided)
- **Simulation category**
- **Short description**

Also add a subsection in the "Simulation Gallery" following the existing pattern.

### Step 7 — Document entities in Simulation_Entity_Catalog.md

Add a new section to `docs/simulations/Simulation_Entity_Catalog.md` with:

- Package path and supported cell shapes
- GridModel layer count and names from the confirmed Phase 4 design
- Entity table (Java file, kind, extends, implements, entity role) from the confirmed Phase 4 entity design — only the
  root entity contract uses the **class name prefix**; other entities use role-focused domain names
- Display Catalog table (descriptor ID, long name, long name key, emoji, fill color, border color, default layer) from
  the confirmed descriptor metadata

## Verification

After completing all steps, compile and run the tests:

```shell
.\gradlew.bat app:compileJava
.\gradlew.bat app:test
```

---
description: "Create a new simulation in etpetssim. Use when adding a new simulation type to the project."
agent: "agent"
argument-hint: "Display name, Wikipedia URL, reference simulation (e.g., 'Boids, https://en.wikipedia.org/wiki/Boids, sugar')"
---

# Create New Simulation

Create a new simulation in this project. Follow all seven registration steps below in order.
Use [simulations.instructions.md](../instructions/simulations.instructions.md) for coding conventions, MVVM
architecture, entity design, and factory patterns.

## Required Input

Collect input from the user in three phases.

### Phase 1 — User provides

Ask the user for these three items first:

| Input                    | Example (Wa-Tor)                               | Purpose                                              |
|--------------------------|------------------------------------------------|------------------------------------------------------|
| **Display name (EN)**    | `Wa-Tor`                                       | Title in `messages_en_US.properties` and `README.md` |
| **Wikipedia URL**        | `https://en.wikipedia.org/wiki/Wa-Tor` or none | For Javadoc link and `simulation.<name>.url` key     |
| **Reference simulation** | `sugar`                                        | Existing simulation to use as structural template    |

### Phase 2 — Agent proposes category and description

Derive the following from **display name (EN)** and **Wikipedia URL** (fetch the article summary if a URL was
provided) and present them to the user for confirmation:

| Derived input           | Rule                                                      | Examples                                                      |
|-------------------------|-----------------------------------------------------------|---------------------------------------------------------------|
| **Simulation category** | Infer from Wikipedia article or display name              | `Agent-Based Model`, `Cellular Automaton`, `Development Tool` |
| **Short description**   | One-sentence English summary of the simulation's behavior | `Predator-prey ecosystem with sharks and fish`                |

### Phase 3 — Agent proposes technical names

Derive the following from the confirmed inputs and present them to the user for confirmation:

| Derived input         | Rule                                                                                                                                                                 | Examples                                                                                                  |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| **Package name**      | Short, recognizable lowercase abbreviation — not the full name mechanically stripped                                                                                 | `wator`, `conway` (not `conwaysgameoflife`), `forest` (not `forestfiremodel`), `sugar` (not `sugarscape`) |
| **Class name prefix** | Capitalize package name; used as prefix for infrastructure classes (`*Factory`, `*Config`, `*ConfigViewModel`, `*MainView`) and the root entity contract (`*Entity`) | `Wator` → `WatorFactory`, `WatorConfig`, `WatorMainView`, `WatorEntity`                                   |
| **Enum constant**     | Uppercase of the display name's natural word boundaries; single proper nouns stay unseparated, multi-word names use underscores                                      | `WATOR`, `SUGARSCAPE`, `ET_PETS`, `FOREST_FIRE`, `CONWAYS_LIFE`                                           |
| **CLI aliases**       | Package name, full name joined, hyphenated variant if applicable, short abbreviations, common synonyms or plurals                                                    | `wator`, `wa-tor`; Snake: `snake`, `snakes`; Conway's: `conway`, `life`, `cgol`                           |
| **CSS file needed**   | Suggest `no` by default; most simulations do not need a dedicated CSS file                                                                                           | `no`                                                                                                      |

The user may accept or override any of these.

### Final Review — Confirm all inputs before proceeding

After Phase 3 is confirmed, present a consolidated summary of **all** collected and derived values:

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

Review the values for consistency and potential issues (typos, naming-rule violations, mismatched conventions).
If you spot problems, list them and suggest corrections.

Then ask the user for explicit approval to start the registration steps. Do not proceed until the user confirms.

## Registration Steps

Complete all seven steps. Do not skip any.

### Step 1 — Register `SimulationType` enum constant and tests

Add a new constant using the provided **enum constant** name in
`app/src/main/java/de/mkalb/etpetssim/SimulationType.java` with all constructor arguments:

- `implemented` flag (use `true`)
- `showOnStartScreen` flag (use `true`)
- title key: `simulation.<package name>.title`
- subtitle key: `simulation.<package name>.subtitle`
- URL key: `simulation.<package name>.url`
- CSS path: `<package name>.css` if CSS file needed, otherwise empty string `""`
- CLI aliases: the provided **CLI aliases** list

If a **Wikipedia URL** was provided, add a Javadoc `@see` or `<a href="...">` link matching the style of existing
constants. Follow the declaration order and Javadoc style of existing constants.

Then update `app/src/test/java/de/mkalb/etpetssim/SimulationTypeTest.java` — add the new **enum constant** to every
test method that references individual `SimulationType` constants. Follow the existing pattern in each method.

### Step 2 — Create simulation packages

Create the package structure under `de.mkalb.etpetssim.simulations.<package name>` with a `package-info.java` in
each package. Each `package-info.java` must use `@NullMarked` as specified in
[java.instructions.md](../instructions/java.instructions.md):

- `de.mkalb.etpetssim.simulations.<package name>` — simulation root
- `de.mkalb.etpetssim.simulations.<package name>.model` — domain state and business rules
- `de.mkalb.etpetssim.simulations.<package name>.model.entity` — entity types
- `de.mkalb.etpetssim.simulations.<package name>.shared` — layer-neutral types used by multiple layers
- `de.mkalb.etpetssim.simulations.<package name>.view` — JavaFX scene-graph and rendering
- `de.mkalb.etpetssim.simulations.<package name>.viewmodel` — JavaFX properties, bindings, UI state

Use the provided **reference simulation** to match the `package-info.java` content and style.

### Step 3 — Create simulation classes

The goal of this step is a **compilable scaffold** — a working skeleton that follows the reference simulation's
architecture with placeholder logic. The developer will refine domain-specific behavior afterward.

Before creating any files, read the **reference simulation's** complete package structure and study every class in
its `model`, `model.entity`, `view`, `viewmodel`, and `shared` packages. Understand the class hierarchy, method
signatures, constructor wiring, and how the factory assembles the components.

Then create the corresponding classes for the new simulation:

- **Factory class** at the package root — wire model, viewmodel, and view together. Mirror the reference factory's
  structure.
- **Model classes** — config record (implementing `SimulationConfig`), constraints class, simulation manager,
  step logic, and statistics. Use simple default values and minimal placeholder logic.
- **Entity classes** in `model.entity` — root entity contract, terrain constants, entity descriptors, and at least
  one domain entity. Only the root entity contract uses the **class name prefix** (e.g., `BoidsEntity`); other
  entities use role-focused domain names.
- **View classes** — main view, config view, and observation view extending the appropriate abstract base classes.
  Render a basic grid with terrain only; domain-specific rendering can be added later.
- **ViewModel classes** — config viewmodel exposing the config record's parameters as properties.
- **Shared classes** — only if the reference simulation has them.

Use the confirmed **class name prefix** for all infrastructure classes. Keep all classes compilable — no `TODO`
comments as method bodies, no unresolved references. Use the simplest working implementation for each method
(return defaults, empty collections, or delegate to the reference simulation's pattern).

### Step 4 — Wire `SimulationFactory` and tests

Add a case for the new **enum constant** in the switch expression in
`app/src/main/java/de/mkalb/etpetssim/simulations/core/SimulationFactory.java` that calls
`<class name prefix>Factory.createMainView()`.

Then update `app/src/test/java/de/mkalb/etpetssim/simulations/core/SimulationFactoryTest.java` — add the new
simulation following the existing pattern (individual type test method and any per-constant references).

### Step 5 — Add localization keys

Add the simulation's title, subtitle, and URL keys to all message properties files:

- `app/src/main/resources/i18n/messages_en_US.properties` — use **display name (EN)** and English **short description**
- `app/src/main/resources/i18n/messages_de_DE.properties` — translate display name and short description to German

If a **Wikipedia URL** was provided, add it as the URL key value (same URL for all locales).
Keep keys sorted alphabetically within each file.

### Step 6 — Update README.md

Add the new simulation to the "Simulations" table in `README.md` with:

- **Display name (EN)** (linked to **Wikipedia URL** if provided)
- **Simulation category**
- **Short description**

Also add a subsection in the "Simulation Gallery" following the existing pattern.

### Step 7 — Document entities in Simulation_Entity_Catalog.md

Add a new section to `docs/simulations/Simulation_Entity_Catalog.md` with:

- Simulation's package path and supported cell shapes
- Entity table (Java file, kind, extends, implements, entity role) — only the root entity contract uses the **class name
  prefix**; other entities use role-focused domain names
- Display Catalog table (descriptor ID, long name, long name key, emoji, fill color, border color, default layer)

## Verification

After completing all steps, run the tests to verify:

```shell
.\gradlew.bat test
```

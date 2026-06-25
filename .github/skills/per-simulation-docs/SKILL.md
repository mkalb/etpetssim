---
name: per-simulation-docs
description: 'Creates or updates the user-facing documentation for one simulation in docs/simulations/<package>.md (e.g. wator.md), derived from that simulation''s Java package and the en_US localization bundle. Use when someone wants to write, generate, refresh, or update the documentation / docs page for a specific simulation (Wa-Tor, Conway, Forest-fire, Langton, Sugarscape, Snake, ET Pets, Rebounding, etc.). Requires the simulation name up front.'
argument-hint: "<simulation-name>"
---

# Per-simulation documentation

Produce a single user-facing Markdown document for **one** simulation, written
for end users of the application (not developers). The document is derived from
the simulation's Java package and the English localization bundle.

All output (the document, headings, prose) must be written in **English
(en_US)**, regardless of the language of the request.

## 1. Require and resolve the simulation name

The simulation name is **mandatory**. If the user did not name a simulation,
stop and ask for one — do **not** guess or document all simulations.

Resolve the name to a simulation by matching it (case-insensitively) against the
CLI aliases in
[
`app/src/main/java/de/mkalb/etpetssim/SimulationType.java`](../../../app/src/main/java/de/mkalb/etpetssim/SimulationType.java)
(the `cliArguments` list of each constant) or the simulation package name under
`app/src/main/java/de/mkalb/etpetssim/simulations/`.

From the matched `SimulationType` constant, record:

- the **package name** (lowercase, e.g. `wator`, `sugar`, `conway`) — this is the
  output file stem. **Always use the short package name**, never a CLI alias or
  hyphenated variant (e.g. `etpets`, not `et-pets`),
- the `titleKey`, `subtitleKey`, and `urlKey`.

Exclusions: `STARTSCREEN` (start screen) and `SIMULATION_LAB` (`lab`, a
development showcase) are **not** documentable. If asked for those, say so and
stop.

## 2. Gather the facts

Read these sources for the matched simulation; do not invent values.

- **Localization (en_US):** in
  `app/src/main/resources/i18n/messages_en_US.properties`, read the values for
  `simulation.<name>.title`, `simulation.<name>.subtitle`, and
  `simulation.<name>.url`. Use these exact strings for the document title,
  one-line summary, and the reference link.
- **Package:** `app/src/main/java/de/mkalb/etpetssim/simulations/<package>/`.
  The key source files are listed below. `<Name>` is the simulation's class prefix (e.g. `Wator`).
- Names are conventional, not guaranteed: confirm the actual files in the package, and skip any that do not exist
  for a given simulation.

  | Source file                          | What to extract                                                                                                                                                                                                  | Feeds doc section                |
    |--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------|
  | `model/<Name>Config.java`            | Configurable parameters (record components + Javadoc), grouped by the pane comments (Structure, Layout, Initialization, Rules)                                                                                   | Configuration                    |
  | `model/<Name>Constraints.java`       | Defaults, valid ranges, and allowed choices (e.g. `CELL_SHAPE_VALUES`, `*_DEFAULT`); used for default-first `(default)` marking                                                                                  | Category and grid, Configuration |
  | `model/<Name>GridModel.java`         | Whether the simulation uses a single grid or **multiple layered grids** (e.g. resource layer + agent layer in Sugarscape / ET Pets). Name each layer conceptually.                                               | Entities, Rules and mechanics    |
  | `model/<Name>StepLogic.java`         | Per-agent step logic (implements `AgentStepLogic`): what happens to a single agent each tick — movement, neighbor interactions, reproduction, death (e.g. Wa-Tor, Snake)                                         | Rules and mechanics              |
  | `model/<Name>UpdateStrategy.java`    | Synchronous, double-buffered cellular-automaton step (implements `SynchronousStepLogic`): rules applied to every cell from the current grid into a next grid in one pass (e.g. Conway survival/birth thresholds) | Rules and mechanics              |
  | `model/<Name>StepRunner.java`        | Whole-step orchestration (implements `SimulationStepRunner`): the ordered sub-phases executed per tick (e.g. agent logic, then resource regrowth, then terrain updates in ET Pets / Sugarscape)                  | Rules and mechanics              |
  | `model/<Name>AgentLogic.java`, `<Name>ResourceLogic.java`, and any other `*Logic.java` in the package | Each sub-phase that `StepRunner` invokes. List **every** logic class present, not only those matching the suffixes above.                                                       | Rules and mechanics              |
  | `model/<Name>SimulationManager.java` | **Starting conditions** (peak placement, resource seeding, agent placement strategy, RNG use) **and** overall run flow. For Sugarscape this is where peak coordinates, radial sugar distribution, and percentage-based agent seeding live — these are user-visible facts that exist nowhere else. | Rules and mechanics, Configuration (Initialization) |
  | `model/entity/`                      | The entities the user can see; describe each conceptually                                                                                                                                                        | Entities                         |
  | `view/<Name>ConfigView.java`         | **Authoritative source** for which configuration parameters are actually exposed in the UI and in which titled pane they live (`createConfigTitledPane(CONFIG_TITLE_STRUCTURE/LAYOUT/INITIALIZATION/RULES, ...)`). Also indicates whether cell shape / edge / neighborhood are user-configurable (`createStructurePane(true|false)`). Trust this over the grouping comments in `Config.java` when they disagree. | Configuration                    |
  | `view/<Name>MainView.java`           | (a) Interactive edit tools available while the simulation runs, declared in `createUserActionDescriptors()` together with their localization keys (e.g. `sugar.toolbar.addsugar`). (b) Dynamic visual coding the user perceives but the entity catalog does not cover (brightness by value, markers for state transitions such as newly spawned agents). | Interactive editing, Entities    |
  | `shared/<Name>UserActionContext.java`, `shared/<Name>*Level.java` | Option values that accompany edit tools (e.g. selectable add-sugar levels).                                                                                                                       | Interactive editing              |

  Do not list every parameter exhaustively; condense rules into 3-7
  plain-language bullets, and keep entities conceptual.

  Files that exist in some packages but are **not** sources for user-facing
  documentation, and should be ignored:

    - `model/<Name>Balance.java` — internal tuning constants (e.g. noise ranges,
      fixed peak positions); never quote these magic numbers in the doc.
    - `model/<Name>TerminationCondition.java` — usually a one-line "stop when
      empty" check; mention only if it implies something genuinely surprising to
      the user.
    - `model/<Name>Statistics.java`, `view/<Name>ObservationView.java`,
      `viewmodel/`, `<Name>Factory.java`, `package-info.java` — infrastructure.
- **Category:** classify as **Agent-based** (`wator`, `etpets`, `snake`,
  `rebounding`, `sugar`) or **Cellular automaton** (`conway`, `forest`,
  `langton`); confirm against the actual model if unsure.
- **Screenshot:** look for the file `assets/screenshots/screenshot_<package>_01.png`
  (always the `_01` variant, e.g. `screenshot_wator_01.png`). If it does not
  exist, omit the Screenshot section rather than linking a missing file. Do not
  pick a different number.

Translate everything into **plain, user-facing language**. Do not expose class
names, MVVM layers, or implementation details in the document.

## 3. Write or update the document

Output path: `docs/simulations/<package>.md` (lowercase package name, e.g.
`docs/simulations/wator.md`).

Use the structure in [`template.md`](./template.md). Keep its section headings
and their order. Fill every placeholder from the gathered facts.

### If the file does not exist

Create it from the template, filling all sections.

### If the file already exists

Update it **in place** and **preserve hand-written prose**:

- Refresh only the factual parts (title, summary, category, cell shapes,
  configuration summary, references, screenshot) from the sources.
- Keep any human-authored overview, rule explanations, or notes; improve wording
  only where it is clearly outdated or contradicts the code.
- Where the existing text contradicts the current code (e.g. a parameter range
  changed, an entity was added/removed), **flag the drift** to the user in your
  reply and apply the corrected fact in the document.
- Do not delete sections the author added; do not reorder existing content
  unnecessarily.

## 4. Conventions

- UTF-8, no BOM; standard GitHub Markdown; prefer ASCII unless a name needs
  otherwise.
- In "Category and grid", keep **Edge behavior** and **Neighborhood** on
  separate lines. For any field backed by a configuration choice (cell shapes,
  edge behavior, neighborhood), list the default value first and mark it
  `(default)`, then the other options, e.g. `Square (default), Triangle,
  Hexagon`.
- Do not reproduce the color/emoji rendering tables from
  `docs/simulations/Simulation_Entity_Catalog.md`; the entity section here is
  conceptual and the catalog is a separate, independent document.
- Use the screenshot path relative to `docs/simulations/`, i.e.
  `../../assets/screenshots/screenshot_<package>_01.png`.

## 5. After writing

- State the output path and whether the file was created or updated.
- List any drift you flagged and corrected.
- If a screenshot was missing, note that the Screenshot section was omitted.
- If the `simulation.<name>.url` value was missing or empty, omit the
  **References** section entirely (do not leave an empty heading) and note this
  in your reply.

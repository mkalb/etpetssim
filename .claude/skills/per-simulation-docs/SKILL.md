---
name: per-simulation-docs
description: 'Generate or refresh the user-facing Markdown page for one named simulation in docs/simulations/<package>.md from its Java implementation and en_US localization bundle. Invoke explicitly to document a specific simulation, such as Wa-Tor, Conway, Forest-fire, Langton, Sugarscape, Snake, ET Pets, or Rebounding.'
argument-hint: "<simulation-name>"
user-invocable: true
disable-model-invocation: true
---

# Per-simulation documentation

Produce a single user-facing Markdown document for **one** simulation, written
for end users of the application (not developers). The document is derived from
the simulation's Java package and the English localization bundle.

The generated or updated Markdown document, including all headings and prose,
must be written in **English (en_US)**, regardless of the language of the
request. This language requirement applies only to the document. Write the
completion report in the user's language unless the user requests another
language.

## 1. Require and resolve the simulation name

The simulation name is **mandatory**. If the user did not name a simulation,
stop and ask for one — do **not** guess or document all simulations.

Resolve the name to a simulation by matching it (case-insensitively) against the
CLI aliases in
[`SimulationType.java`](../../../app/src/main/java/de/mkalb/etpetssim/SimulationType.java)
(the `cliArguments` list of each constant) or the simulation package name under
`app/src/main/java/de/mkalb/etpetssim/simulations/`.

From the matched `SimulationType` constant, record:

- the `titleKey`, `subtitleKey`, and `urlKey`.

Derive the **package name** deterministically from the matched constant's
`titleKey`: use the segment between `simulation.` and `.title` (for example,
`simulation.conway.title` becomes `conway`). Confirm that a directory with that
name exists under `app/src/main/java/de/mkalb/etpetssim/simulations/`; stop and
report the mismatch if it does not. This package name is the output file stem.
Always use this short package name, never a CLI alias or hyphenated variant
(e.g. `etpets`, not `et-pets`). Do not infer the package name from the enum
constant name or display title.

Exclusions: `STARTSCREEN` (start screen) and `SIMULATION_LAB` (`lab`, a
development showcase) are **not** documentable. If asked for those, say so and
stop.

## 2. Gather the facts

Read these sources for the matched simulation; do not invent values.

- **Localization (en_US):** in
  `app/src/main/resources/i18n/messages_en_US.properties`, read the values for
  `simulation.<name>.title`, `simulation.<name>.subtitle`, and
  `simulation.<name>.url`. Use these exact strings for the document title,
  one-line summary, and the reference link. The title and subtitle values are
  required and must be non-empty; stop and report the missing value if either
  cannot be resolved. The URL key is optional and may be absent or have an
  empty value; in either case, omit the **References** section and do not invent
  a URL. Also resolve every localization key referenced by exposed
  configuration controls, interactive-edit descriptors, and their option
  controls, including labels, tooltips, and localized option values. Use those
  values as the authoritative user-facing names and as supporting evidence for
  behavior; do not derive UI text from Java identifiers or key names.
- **Package:** `app/src/main/java/de/mkalb/etpetssim/simulations/<package>/`.
  `<Name>` is the class prefix found in the simulation's actual file names. Its
  capitalization may differ from the display title (for example, `Etpets`, not
  `EtPets`); inspect the package instead of constructing the prefix from the
  title. File names are conventional, not guaranteed: confirm the actual files
  in the package, and skip any that do not exist for a given simulation.

  | Source file | What to extract | Feeds doc section |
  |---|---|---|
  | `model/<Name>Config.java` | Configurable parameters (record components + Javadoc), grouped by the pane comments (Structure, Layout, Initialization, Rules) | Configuration |
  | `model/<Name>Constraints.java` | Defaults, valid ranges, and allowed choices (e.g. `CELL_SHAPE_VALUES`, `*_DEFAULT`); used for default-first `(default)` marking | Category and grid, Configuration |
  | `model/<Name>GridModel.java` | Whether the simulation uses one grid or multiple grids for distinct spatial components (e.g. resource and agent layers in Sugarscape / ET Pets). If there are multiple grids, name each conceptually. | Entities, Rules and mechanics |
  | `model/<Name>StepLogic.java` | Per-agent step logic (implements `AgentStepLogic`): what happens to a single agent each tick — movement, neighbor interactions, reproduction, death (e.g. Wa-Tor, Snake) | Rules and mechanics |
  | `model/<Name>UpdateStrategy.java` | Synchronous, double-buffered cellular-automaton step (implements `SynchronousStepLogic`): rules applied to every cell from the current grid into a next grid in one pass (e.g. Conway survival/birth thresholds) | Rules and mechanics |
  | `model/<Name>StepRunner.java` | Whole-step orchestration (implements `SimulationStepRunner`): the ordered sub-phases executed per tick (e.g. agent logic, then resource regrowth, then terrain updates in ET Pets / Sugarscape) | Rules and mechanics |
  | `model/<Name>AgentLogic.java`, `<Name>ResourceLogic.java`, and any other `*Logic.java` used by the simulation | Starting from `StepRunner`, trace each logic class it actually invokes, directly or through another invoked phase. Document only these used classes; do not include a `*Logic.java` class solely because it exists in the package. | Rules and mechanics |
  | `model/<Name>SimulationManager.java` | **Starting conditions** and overall run flow. Trace the methods, helper types, and constants actually used by the manager to determine placement, seeding, resource generation, randomization, and phase orchestration. Describe the resulting user-visible behavior. Do not assume that fixed implementation values are configurable or that all relevant facts are declared directly in the manager. | Rules and mechanics, Configuration (Initialization) |
  | Helper types and constant holders reached from an authoritative code path | Use them as supporting evidence to understand behavior performed by the authoritative source. Do not enumerate internal tuning values or present fixed values as user-configurable. Mention an exact fixed value only when it is directly user-visible and necessary for an accurate explanation. | The section fed by the authoritative source |
  | `model/entity/` | The entities the user can see; describe each conceptually | Entities |
  | `model/<Name>UserAction.java` (implements `SimulationUserAction`) | What actually happens when an edit tool is applied to the selected cell (or globally): e.g. spawn/remove an agent, cycle a cell's state, set terrain or a resource. This is the precise, per-tool behavior behind the toolbar entries in `MainView`. | Interactive editing |
  | `view/<Name>ConfigView.java` | **Authoritative source** for which configuration parameters are actually exposed in the UI and how its panes group them conceptually. Structure and Layout are usually built by the base class (`createStructurePane(...)`, `createLayoutPane(...)`); Initialization and Rules are simulation-specific. Inspect which controls those methods actually create; their boolean arguments govern start-state disabling, not whether a setting is user-configurable. Trust `ConfigView` over the grouping comments in `Config.java` when they disagree. Treat the template subsections as documentation categories, not a one-to-one copy of UI panes: merge related simulation-specific panes into the closest category, including all rule-oriented panes under one **Rules** subsection, while preserving their conceptual groups in prose. Add another subsection only for a genuinely distinct concern that would be misleading under every standard category. | Configuration |
  | Configuration choice and rule types used by controls confirmed in `ConfigView` (for example, `shared/<Name>*Preset*.java`, choice enums, and rule value objects) | Trace only exposed controls to their actual option source. Extract the available choices and their user-facing meaning, plus any user-visible rule notation, limits, or shape-specific validity. Take defaults and allowed selections from `Constraints` when defined there. Do not treat a similarly named type as a documentation source unless its connection to an exposed control is confirmed. | Configuration |
  | `view/<Name>MainView.java` | (a) Interactive edit tools available while the simulation runs, declared in `createUserActionDescriptors()` together with their localization keys (e.g. `sugar.toolbar.addsugar`) and their scope (`SimulationUserActionScope.CELL_SELECTED` vs. `GLOBAL`). (b) Option controls exposed by `createEditToolBarOptionPanel(...)`; trace each visible option from here to its actual data source. (c) Dynamic visual coding the user perceives but the entity catalog does not cover (brightness by value, markers for state transitions such as newly spawned agents). | Interactive editing, Entities |
  | Interactive-edit option sources reached from a visible `MainView` control (for example, catalogs, enums, choice records, or direction/strategy providers in `shared/`, `model/`, or another package) | Extract the selectable values, localized label keys, and configuration-dependent availability. If `MainView` delegates option population through `<Name>EditToolBarViewModel`, read that ViewModel only to follow the data flow to the actual option source; treat it as routing evidence, not as the authority for option meaning or behavior. | Interactive editing |

  Resolve conflicting evidence by subject ownership:

    1. For run-time behavior and step order, the code actually invoked by
       `SimulationManager`, `StepRunner`, `StepLogic`, `UpdateStrategy`, and
       `UserAction` is authoritative.
    2. For which settings and tools users can access, their conceptual UI
       grouping, and action scope, `ConfigView` and `MainView` are authoritative.
    3. For defaults, ranges, and allowed values, use `Constraints` and `Config`
       when they define the subject; otherwise use the actual option source
       traced from the exposed control. Limit all such facts to controls
       confirmed by `ConfigView`.
    4. For visible names, labels, summaries, tooltips, and URLs, resolved values
       from `messages_en_US.properties` are authoritative.
    5. Javadoc and the existing `docs/simulations/<package>.md` document are
       supporting context only; they must not override the current authoritative
       source for the subject.

  If authoritative sources for the same subject still conflict, stop and
  report the conflict instead of guessing or silently choosing one.

  Do not list every parameter exhaustively; condense rules into 3-7
  plain-language bullets, and keep entities conceptual.

  Sources that are normally outside this document's scope:

    - Unreferenced tuning and constant-holder classes are not documentation
      sources. When an authoritative code path uses one, it may be read as
      supporting evidence under the helper-source rule above.
    - `model/<Name>TerminationCondition.java` — usually a one-line "stop when
      empty" check; mention only if it implies something genuinely surprising to
      the user.
    - `model/<Name>Statistics.java` and `view/<Name>ObservationView.java` are
      intentionally outside this document's scope. Do not add an **Observation**
      or **Statistics** section or enumerate status metrics and selected-cell
      readouts unless the user explicitly requests that coverage.
    - `<Name>Factory.java` and `package-info.java` — infrastructure.
    - `viewmodel/` — infrastructure, but a ViewModel or property class may be read
      narrowly as routing evidence when tracing an exposed configuration or
      interactive-edit option to its actual source. Do not treat it as the
      authority for option meaning or run-time behavior.
- **Category:** classify from the simulation's actual model semantics. Common
  categories include **Agent-based** (e.g. `wator`, `etpets`, `snake`) and
  **Cellular automaton** (e.g. `conway`, `forest`, `langton`). These examples
  are illustrative, not exhaustive; use another accurate category when the
  model does not fit either one. Cross-check an existing classification in the
  `README.md` simulation table and report a clear conflict instead of silently
  changing categories.
- **Screenshot:** look for the file `assets/screenshots/screenshot_<package>_01.png`
  (always the `_01` variant, e.g. `screenshot_wator_01.png`). If it does not
  exist, omit the Screenshot section rather than linking a missing file. Do not
  pick a different number.

Translate everything into **plain, user-facing language**. Do not expose class
names, MVVM layers, or implementation details in the document.

## 3. Write or update the document

Output path: `docs/simulations/<package>.md` (lowercase package name, e.g.
`docs/simulations/wator.md`).

Use the structure in [`template.md`](./template.md). Keep its top-level section
headings and their order, subject to the template's conditional-section rules.
Configuration subsections follow the semantic grouping rules above. Fill every
placeholder from the gathered facts.

Determine whether the output file exists from the **current filesystem state
only**. Do not use Git history, the Git index, diffs, deleted-file status, or any
other repository metadata to find or restore an earlier version. A file that is
currently absent is a new document, even if Git records a previous version.

### If the file does not exist

Create it from the template, filling all sections.

### If the file already exists

Update it **in place** and **preserve hand-written prose**:

- Revalidate every factual section against the current sources: title, summary,
  overview, category and grid, rules and mechanics, entities, interactive
  editing, configuration, references, and screenshot.
- Keep human-authored wording, explanations, and notes wherever their factual
  claims remain current. Change only the smallest passage needed when the code
  adds, removes, or changes a documented fact.
- Treat content as drift only when it contradicts the current sources,
  describes behavior or options that no longer exist, or omits information
  required by the template. Missing optional detail is not drift and must not
  trigger a change or be reported as drift.
- Where the existing text contradicts the current code (e.g. a parameter range
  changed, an entity was added/removed), **flag the drift** to the user in your
  reply and apply the corrected fact in the document.
- Do not delete sections the author added; do not reorder existing content
  unnecessarily. A conditional template section may be added or removed when
  its current inclusion rule requires it; this is not considered deletion of a
  human-authored section.

## 4. Conventions

- UTF-8, no BOM; standard GitHub Markdown; prefer ASCII unless a name needs
  otherwise.
- Wrap newly written or changed prose at approximately 80 characters where
  practical. Do not reflow unchanged prose during an update solely to enforce
  this limit. Long URLs, code spans, and Markdown tables may exceed it.
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

## 5. Validate

Before reporting completion, validate the finished document:

- no template placeholders (`<...>`) or authoring HTML comments remain,
- title, summary, interactive-tool names, and the reference URL when present
  match the resolved `en_US` values,
- default-backed choices list the actual default first and mark it `(default)`,
- conditional sections match the current sources, and every local image link
  points to an existing file,
- the `README.md` simulation table contains a row for the simulation whose Docs
  link targets `docs/simulations/<package>.md`; if the row or link is missing
  or incorrect, report it without modifying the README unless the user
  requested that change,
- all factual sections inspected during an update either remain supported by
  the code or were corrected and included in the drift report.

## 6. Report

After validation, report completion:

- State the output path and whether the file was created or updated.
- List any drift you flagged and corrected.
- Report a missing README simulation-table row or an incorrect or missing Docs
  link.
- If a screenshot was missing, note that the Screenshot section was omitted.
- If the `simulation.<name>.url` value was missing or empty, omit the
  **References** section entirely (do not leave an empty heading) and note this
  in your reply.

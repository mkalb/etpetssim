---
name: per-simulation-docs
description: 'Generate or refresh the user-facing Markdown page for one named simulation in docs/simulations from its Java implementation and en_US localization bundle. By default it updates an existing page in place; the optional second argument "regenerate" rewrites the page from scratch without reading the existing simulation docs. Invoke explicitly to document a specific simulation, such as Wa-Tor, Conway, Forest-fire, Langton, Sugarscape, Snake, ET Pets, or Rebounding.'
argument-hint: "<simulation-name> [regenerate]"
disable-model-invocation: true
context: fork
effort: high
background: false
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

**Regenerate mode: before reading anything else, note that no file in
`docs/simulations` and no earlier version of one may be read** (see "Modes").

## Modes

The skill has two modes, selected by the optional second argument:

- **Update** (default, `/per-simulation-docs <simulation-name>`): create the
  document if it does not exist; otherwise update it in place, preserve
  hand-written prose whose facts are current, and report drift (step 3).
- **Regenerate** (`/per-simulation-docs <simulation-name> regenerate`): write
  the document from scratch from the current sources and overwrite any existing
  file completely. Select this mode only when the second argument is
  `regenerate`, ignoring case; stop and ask if the second argument is anything
  else.

In regenerate mode, do **not** read any file in `docs/simulations`, including
the existing `docs/simulations/<package>.md` and the documents of other
simulations, and do not read their earlier versions through Git history (e.g.,
`git show`, `git log -p`, `git diff`, or `git blame`). Searches must exclude
`docs/simulations` as well. Use only [`template.md`](./template.md) of this
skill as the structure. All other steps (name resolution, fact gathering,
conventions, validation, and the README check) apply unchanged unless a step
says otherwise.

## 1. Require and resolve the simulation name

The simulation name is **mandatory**. If the user did not name a simulation,
stop and ask for one — do **not** guess or document all simulations.

Resolve the name to a simulation by matching it (case-insensitively) against the
CLI aliases in
[`SimulationType.java`](../../../app/src/main/java/de/mkalb/etpetssim/simulations/core/SimulationType.java)
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

Exclusion: `SIMULATION_LAB` (`lab`, a development showcase) is **not**
documentable. If asked for it, say so and stop.

## 2. Gather the facts

Read these sources for the matched simulation; do not invent values.

Read source files individually and in parallel where possible; use targeted
searches for constants, localization keys, and signatures instead of dumping
many files in one shell command.

- **Localization (en_US):** in
  `app/src/main/resources/i18n/messages_en_US.properties`, read the values for
  `simulation.<name>.title`, `simulation.<name>.subtitle`, and
  `simulation.<name>.url`. Use these exact strings for the document title,
  one-line summary, and the reference link. The title and subtitle values are
  required and must be non-empty; stop and report the missing value if either
  cannot be resolved. The URL key exists in all bundles, but its value may be
  empty; in that case, omit the **References** section and do not invent a URL.
  Also resolve every localization key referenced by exposed configuration
  controls, interactive-edit descriptors, and their option controls, including
  labels, tooltips, and localized option values. Use those values as the
  authoritative user-facing names and as supporting evidence for behavior; do
  not derive UI text from Java identifiers or key names. The shared controls
  use keys outside the simulation prefix, such as `cellshape.*`,
  `gridedgebehavior.*`, `neighborhoodmode.*`, `celldisplaymode.*`, and
  `config.*`.
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
  | `model/<Name>Balance.java` | Fixed tuning values of the simulation logic that are **not** part of the configuration (e.g. sugar noise and peak positions in Sugarscape). Read it only as supporting evidence when an authoritative code path uses a constant; never present these values as user-configurable, and mention one only under the helper-source rule below | The section fed by the using source |
  | `model/<Name>GridModel.java` | Whether the simulation uses one grid or multiple grids for distinct spatial components (e.g. resource and agent layers in Sugarscape / ET Pets). If there are multiple grids, name each conceptually. | Entities, Rules and mechanics |
  | `model/<Name>Cell.java` (only for multi-grid simulations, e.g. Sugarscape, ET Pets, Langton's Ant) | Which layer entities one grid cell combines and which of them counts as the top-most entity of the cell (e.g. agent before resource). Use it for the layer paragraph of the Entities section (which entities can share a cell and which one is drawn on top); take their appearance from `MainView`, which may draw every layer | Entities |
  | `model/<Name>StepLogic.java` | Per-agent step logic (implements `AgentStepLogic`): what happens to a single agent each tick — movement, neighbor interactions, reproduction, death (e.g. Wa-Tor, Snake). Or synchronous, double-buffered cellular-automaton step logic (implements `SynchronousStepLogic`): rules applied to every cell from the current grid into a next grid in one pass (e.g. Conway survival/birth thresholds) | Rules and mechanics |
  | `model/<Name>StepRunner.java` | Whole-step orchestration (implements `SimulationStepRunner`): the ordered sub-phases executed per tick (e.g. agent logic, then resource regrowth, then terrain updates in ET Pets / Sugarscape) | Rules and mechanics |
  | `model/<Name>TerminationCondition.java` | When the run ends on its own (e.g. when no agent is left). Always state it as the last rule bullet in plain language | Rules and mechanics |
  | `model/<Name>AgentLogic.java`, `<Name>ResourceLogic.java`, and any other `*Logic.java` used by the simulation | Starting from `StepRunner`, trace each logic class it actually invokes, directly or through another invoked phase. Document only these used classes; do not include a `*Logic.java` class solely because it exists in the package. | Rules and mechanics |
  | `model/<Name>SimulationManager.java` | **Starting conditions** and overall run flow. Trace the methods, helper types, and constants actually used by the manager to determine placement, seeding, resource generation, randomization, and phase orchestration. Describe the resulting user-visible behavior. Do not assume that fixed implementation values are configurable or that all relevant facts are declared directly in the manager. | Rules and mechanics, Configuration (Initialization) |
  | Helper types and constant holders reached from an authoritative code path | Use them as supporting evidence to understand behavior performed by the authoritative source. Do not enumerate internal tuning values or present fixed values as user-configurable. Mention an exact fixed value only when it is directly user-visible and necessary for an accurate explanation. | The section fed by the authoritative source |
  | `model/entity/` | The entities the user can see and their appearance. The `EntityDescriptors` class, or the entity enum that provides the descriptors, defines each entity's color, emoji, name, and description; resolve the name (the `.name` key) in `messages_en_US.properties` and use it as the entity name, and translate the color into a plain color name. The `.description` value (shown in the app as the tooltip of the cell type) is a hint to what an entity is and does, and its terms may be used; derive the behavior from the code, and report a description that contradicts the code instead of adopting it | Entities |
  | `model/<Name>UserAction.java` (implements `SimulationUserAction`) | What actually happens when an edit tool is applied to the selected cell (or globally): e.g. spawn/remove an agent, cycle a cell's state, set terrain or a resource. This is the precise, per-tool behavior behind the toolbar entries in `MainView`. | Interactive editing |
  | `view/<Name>ConfigView.java` | **Authoritative source** for which configuration parameters are actually exposed in the UI and how its panes group them conceptually. Structure and Layout are usually built by the base class `simulations/core/view/AbstractConfigView.java` (`createStructurePane(...)`, `createLayoutPane(...)`); Initialization and Rules are simulation-specific. Inspect which controls those methods actually create: a choice whose `_VALUES` in `Constraints` has only one allowed value gets no control. Trust `ConfigView` over the grouping comments in `Config.java` when they disagree. Treat the template subsections as documentation categories, not a one-to-one copy of UI panes: merge related simulation-specific panes into the closest category, including all rule-oriented panes under one **Rules** subsection, while preserving their conceptual groups in prose. Add another subsection only for a genuinely distinct concern that would be misleading under every standard category. | Configuration |
  | Configuration choice and rule types used by controls confirmed in `ConfigView` (for example, `shared/<Name>*Preset*.java`, choice enums, and rule value objects) | Trace only exposed controls to their actual option source. Extract the available choices and their user-facing meaning, plus any user-visible rule notation, limits, or shape-specific validity. Take defaults and allowed selections from `Constraints` when defined there. Do not treat a similarly named type as a documentation source unless its connection to an exposed control is confirmed. | Configuration |
  | `view/<Name>MainView.java` | (a) Interactive edit tools available while the simulation runs, declared in `createUserActionDescriptors()` together with their localization keys (e.g. `sugar.toolbar.addsugar`) and their scope (`SimulationUserActionScope.SELECTED_CELL` vs. `GLOBAL`). (b) Option controls exposed by `createEditToolBarOptionPanel(...)`; trace each visible option from here to its actual data source. (c) How each entity is drawn (e.g. full cell or inner circle, emoji overlay, border) and dynamic visual cues the user perceives (e.g. brightness or color scaled by a value, markers for state transitions such as newly spawned agents). | Interactive editing, Entities |
  | Interactive-edit option sources reached from a visible `MainView` control (for example, catalogs such as `ConwayPatterns` and `SnakeMoveStrategies`, enums such as `SugarAddSugarLevel`, choice records, or direction providers such as `LangtonDirectionOptions`, in `shared/`, `model/`, or another package) | Extract the selectable values, localized label keys, and configuration-dependent availability. If `MainView` delegates option population through `<Name>EditToolBarViewModel`, read that ViewModel only to follow the data flow to the actual option source; treat it as routing evidence, not as the authority for option meaning or behavior. | Interactive editing |

  Resolve conflicting evidence by subject ownership:

    1. For run-time behavior and step order, the code actually invoked by
       `SimulationManager`, `StepRunner`, `StepLogic`, and
       `UserAction` is authoritative.
    2. For which settings and tools users can access, their conceptual UI
       grouping, and action scope, `ConfigView` and `MainView` are authoritative.
    3. For defaults, ranges, and allowed values, use `Constraints` and `Config`
       when they define the subject; otherwise use the actual option source
       traced from the exposed control. Limit all such facts to controls
       confirmed by `ConfigView`.
    4. For visible names, labels, summaries, tooltips, and URLs, resolved values
       from `messages_en_US.properties` are authoritative. A tooltip that
       describes behavior more loosely than the code is not a conflict;
       describe the behavior from the code.
    5. Javadoc and, in update mode, the existing `docs/simulations/<package>.md`
       document are supporting context only; they must not override the current
       authoritative source for the subject. In regenerate mode, the existing
       document is not read at all (see "Modes").

  If authoritative sources for the same subject still conflict, stop and
  report the conflict instead of guessing or silently choosing one.

  Condense the rules, including the starting conditions and the termination
  condition, into 5-10 plain-language bullets that do not repeat the
  configuration values, and describe entities briefly in plain language.
  Start the termination bullet with "The run ends on its own when".
  Summarize decision logic (e.g. how an agent rates its possible actions) as
  the factors that favor or discourage a choice, without weights or
  thresholds.

  Sources that are normally outside this document's scope:

    - Unreferenced tuning and constant-holder classes are not documentation
      sources. When an authoritative code path uses one, it may be read as
      supporting evidence under the helper-source rule above.
    - `model/<Name>Statistics.java` and `view/<Name>ObservationView.java` are
      intentionally outside this document's scope. Do not add an **Observation**
      or **Statistics** section or enumerate status metrics and selected-cell
      readouts unless the user explicitly requests that coverage.
    - `<Name>Factory.java` — infrastructure.
    - `viewmodel/` — infrastructure, but a ViewModel or property class may be read
      narrowly as routing evidence when tracing an exposed configuration or
      interactive-edit option to its actual source, or to find how one control
      changes another (e.g. a preset that fills a rule field). Do not treat it
      as the authority for option meaning or run-time behavior.
- **Category:** use exactly one of the two fixed categories of
  `SimulationCategory`, written in sentence case and plural:
  **Agent- and movement-based models** (moving agents or entities are in the
  foreground: `etpets`, `wator`, `sugar`, `langton`, `snake`, `rebounding`) or
  **Cellular automata** (cell states are updated by local rules: `conway`,
  `forest`). Take the category from `SimulationType` and map its constant to
  this wording (`AGENT_AND_MOVEMENT_BASED_MODEL` and `CELLULAR_AUTOMATON`); do
  not introduce other categories. Cross-check the `README.md` simulation
  table, ignoring capitalization, and report a clear conflict instead of
  silently changing categories.
- **Category note:** when the reference model of a simulation is commonly
  classified in the literature under the other category, add a short paragraph
  after the "Category and grid" list that names the usual classification and
  why the simulation is listed in its category here. Currently this applies
  only to `langton`; keep its note in this wording:
  "Langton's ant is a borderline case: it is often described as a cellular
  automaton (a Turing machine on a grid), but it is listed here with the
  movement-based models because its ants move across the grid." Omit the
  paragraph for all other simulations.
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

### Regenerate mode

Write the complete document from the template and the gathered facts, and
overwrite the output file if it exists. Do not read, preserve, or compare with
the existing content, and do not produce a drift report; the maintainer reviews
the result as a Git diff. If the file exists, delete it first with a shell
command that does not print its content (e.g.,
`rm -f docs/simulations/<package>.md`), then create it. After writing, you may
read the new file for validation; the read ban covers only the previous
content and the other simulation documents. The rest of this step describes the
update mode.

### Update mode

Determine whether the output file exists from the
**current filesystem state only**. Do not use Git history, the Git index, diffs,
deleted-file status, or any other repository metadata to find or restore an
earlier version. A file that is currently absent is a new document, even if Git
records a previous version.

If the file does not exist, create it from the template, filling all sections.

If the file already exists, update it **in place** and **preserve hand-written
prose**:

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

- Follow the Markdown and encoding conventions in `AGENTS.md`, including its
  line width. In regenerate mode, no existing file defines a narrower width, so
  the whole document uses the full width of `AGENTS.md`.
- The block-syntax rule of `AGENTS.md` applies to all of the following at the
  start of a wrapped prose line: `-`, `+`, or `*` followed by a space, `#`,
  `>`, `|`, `=`, a code fence, or a number followed by `.` or `)` (e.g. `1.`
  or `200)`). Such text would start a list, heading, quote, or table, or turn
  the previous line into a heading. Move the line break so that the text
  follows a word on the same line.
- In "Category and grid", keep **Edge behavior** and **Neighborhood** on
  separate lines. For any field backed by a configuration choice (cell shapes,
  edge behavior, neighborhood), list the default value first and mark it
  `(default)`, then the other options, e.g.
  `Square (default), Triangle, Hexagon`. A choice fixed to one value has no
  control; write its value followed by `(fixed)`, without `(default)`. If a
  value of a list contains a comma, separate all values of that list with
  semicolons, e.g. `Wrap X, block Y (default); Block X and Y`; this applies
  to the lists of choices in "Configuration" as well. Always include the
  **Neighborhood** line; omit only the line of a field that the simulation
  does not use at all.
- In "Configuration", name every exposed control in bold by its en_US label
  without format placeholders and trailing colon (e.g. `Agents: %1$s` becomes
  **Agents**), directly followed by all its values in parentheses, taken from
  `Constraints` or, where `Constraints` does not define them, from the option
  source traced from the control:
    - numbers as `(min to max, default d)`, with a step other than 1, e.g.
      **Cell edge length** `(1 to 50 pixels, default 8)` or **Grid width**
      `(8 to 1,000 cells in steps of 2, default 200)`; add a unit when the
      value counts something the user can recognize (e.g. pixels, cells,
      steps, segments), and omit it for abstract quantities (e.g. energy or
      points),
    - percentages as `(0% to 20%, default 0.2%)`, formatted as the
      configuration displays them: the bounds as in the range tooltip and the
      default as in the label, even where `Constraints` stores fractions,
    - choices with the default first and the other values in the order of
      `_VALUES` or of the option source, e.g. `(Square (default), Hexagon)`,
      and an empty default entry as `none (default)`; name the Structure
      controls without their values and refer to "Category and grid" for them,
    - a choice list with more than eight values without its values: say in
      one sentence what the list offers, and give its default; do not name
      individual values, examples, or counts,
    - a composite control (e.g. a checkbox grid for a rule) with its default
      in the displayed notation, e.g. `(default 23/3)`, and its selectable
      range in one sentence; write the row and column labels inside it as
      plain text, not in bold,
    - a text field without a range with what an empty value means and, if
      visible to the user, what an invalid value does,
    - a rule text field (e.g. a turn rule) as `(default RL)`, followed by one
      sentence on its notation and allowed length; for invalid input, name
      only the visible reaction (e.g. a validation message),
    - the seed always in this sentence: "**Seed** (empty by default) sets the
      start value of the random numbers: leave it empty for a random seed, or
      enter a number or any text to repeat a run.",
    - the Structure subsection always in these sentences: "**Cell shape** and
      **Grid edge behavior** offer the choices listed under "Category and
      grid". **Grid width** (<values>) and **Grid height** (<values>) set the
      size of the grid." Leave out a choice that is fixed to one value,
    - the Layout subsection always in these sentences: "**Cell edge length**
      (<values>) sets the size of each cell on the screen. **Cell display**
      (<values>) sets how the cells are drawn." Leave out the second sentence
      if the cell display is fixed.

  Fill each `<values>` with the values of the simulation in the form given
  above for numbers and choices; the fixed sentences prescribe only the
  wording, not the values.

  Write every control as part of a complete sentence, also where no sentence
  on its effect is added. The seed sentence is always a paragraph of its own.
  In a subsection with more than three controls, write one paragraph per
  group of related controls. Describe variants of one control that are shown
  under different conditions (e.g. one preset list per cell shape) together
  in one or two neutral sentences: what they offer, which variant is shown
  when, and their common default; write an empty default in words (e.g. "no
  preset is selected by default"), not as `none (default)`; do not enumerate
  their values or name individual entries. Name the condition of any other
  control that is visible only under a condition (e.g. "for square cells").
  If a label is ambiguous on its own (e.g. a label equal
  to an option value), add a clarifying noun, e.g. "the **Square** preset
  list". Add at most one short sentence on the effect of a control, and only
  when its label does not explain it; this may include how it changes another
  control (e.g. "Selecting a preset fills in the turn rule."). Do not describe
  informational hints that are not controls (e.g. a recommended value shown
  below a slider). State a limit across several controls (e.g. a maximum for
  the sum of several shares) in one sentence after those controls, naming only
  the visible reaction. Do not mention choices fixed to one value in
  "Configuration"; a fixed cell display mode is not mentioned at all. Omit a
  standard subsection without controls (e.g. Initialization without a seed)
  without a note. Format numbers as the en_US UI does (e.g. `1,000`).
- In "Interactive editing", write one bullet per tool in the form
  `- **<Tool name>:** <description>`, directly below the heading and without
  an introductory sentence.
- In "Interactive editing", state the preconditions of each tool from
  `UserAction` (e.g. "only on ground cells without a trail"), even where a
  tooltip is less precise, and describe the initial state of placed entities
  qualitatively (e.g. "starts full"). Do not mention that an option is
  disabled while its tool is not selected, and do not state in the document
  that the tools follow the toolbar order.
- In "Interactive editing", name the values of a tool option by their en_US
  labels, in the order of the option source, as plain text; only the name of
  the option is bold. If the values depend on a condition (e.g. the cell
  shape), name the values of each condition with that condition, and say when
  the option is hidden. Do not name the value that an option preselects. Do
  not enumerate an option list with more than eight values (e.g. patterns);
  say in one sentence what it offers and when values are available, without
  naming individual values, examples, or counts.
- In "Entities", describe the appearance of each visible entity in plain
  language: its color by a common color name of your choice (e.g. "dark
  blue"), its emoji if any, its shape (e.g. inner circle or full cell), and its
  dynamic visual cues (e.g. "brighter with more energy", a border marking a
  state). Describe a dynamic cue in the direction the user sees it over time
  (e.g. "becomes darker as hatching approaches"), and describe a temporary
  state (e.g. a dead agent shown for one more step) in the entry of its
  entity. If the shape depends on the **Cell display** setting, name the shape
  of each display mode briefly: in the entry of the entity if this applies to
  one entity, or in one shared sentence after the list (the display paragraph
  of the template) if it applies to several; do not describe variants for
  small cells (e.g. a dropped border or a circle instead of an emoji). An
  entity that is only drawn as the grid background (e.g. terrain) is listed as
  an entity and described as the background. Combine a family of numbered
  entities that differ only in their number and color (e.g. terrain states)
  into one entry named by its first and last `.name` value (e.g.
  "**Terrain 0** to **Terrain 15**"), and list their colors in order. Do not
  describe UI markers that are not entities, such as the selected-cell marker.
  Do not use hex color codes, descriptor IDs, localization keys, or Java type
  names.
- Use the screenshot path relative to `docs/simulations/`, i.e.
  `../../assets/screenshots/screenshot_<package>_01.png`.

## 5. Validate

Before reporting completion, validate the finished document:

- no template placeholders (`<...>`) or authoring HTML comments remain,
- no wrapped prose line starts with block syntax (see "Conventions"); check
  every line start of the written file,
- no line break falls inside bold text, a code span, or a link that fits on
  one line (e.g. a control label such as **Growth per food**),
- title, summary, interactive-tool names, and the reference URL when present
  match the resolved `en_US` values,
- default-backed choices list the actual default first and mark it `(default)`,
- "Rules and mechanics" has 5-10 bullets, starting with the starting
  conditions and ending with the termination condition,
- "Configuration" names every exposed control with its values in the form
  given under "Conventions", and the values match `Constraints`,
- the interactive tools appear in toolbar order,
- the Entities section names every visible entity (or entity family) by its
  `.name` value, describes its appearance, and contains no hex color codes,
  descriptor IDs, localization keys, or Java type names,
- the described behavior of the entities does not contradict their
  `.description` values,
- conditional sections and paragraphs (category note, display paragraph,
  layer paragraph) match
  the current sources, and every local image link points to an existing file,
- the `README.md` simulation table contains a row for the simulation whose Docs
  link targets `docs/simulations/<package>.md`; if the row or link is missing
  or incorrect, report it without modifying the README unless the user
  requested that change,
- in update mode, all factual sections inspected during the update either
  remain supported by the code or were corrected and included in the drift
  report,
- in regenerate mode, no file in `docs/simulations` and no earlier version of
  one was read, and every statement in the document is supported by the
  gathered sources.

## 6. Report

After validation, report completion:

- State the output path and whether the file was created, updated, or
  regenerated.
- In update mode, list any drift you flagged and corrected. In regenerate mode,
  do not report drift; point the maintainer to the Git diff instead.
- Report a missing README simulation-table row or an incorrect or missing Docs
  link.
- If a screenshot was missing, note that the Screenshot section was omitted.
- If the `simulation.<name>.url` value was empty, omit the **References**
  section entirely (do not leave an empty heading) and note this in your reply.

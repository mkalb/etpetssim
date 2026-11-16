<!--
  Per-simulation documentation generated/maintained by the `per-simulation-docs` skill.
  Audience: end users of the application (not developers).
  Replace every <...> placeholder. Remove all HTML comments, including this
  one, from the final file — they are authoring guidance only.
  Keep the top-level section headings and their order. Do not add a "Controls",
  "Tips", "Observation", or "Statistics" section unless requested; this
  template intentionally omits them.
  Conditionally included sections (delete the whole section if not applicable):
    - "Interactive editing" — only if the MainView declares run-time edit tools.
    - "Screenshot"          — only if screenshot_<package>_01.png exists.
    - "References"          — only if simulation.<name>.url is set.
  Conditionally included paragraphs:
    - Category note         — only for a borderline category case (see SKILL.md).
    - Layer paragraph       — only for simulations with several grid layers.
-->

# <Display title, e.g. Wa-Tor>

<One-line summary: the en_US subtitle verbatim, without an added period,
e.g. "Simulate the Wa-Tor world">

## Overview

<Two to four sentences in plain language: what this simulation models, the core
idea, and what a user sees happening on the grid. Avoid implementation detail.>

## Category and grid

- **Category:** <One of the two fixed categories, see "Category" in SKILL.md>
- **Cell shapes:** <e.g. Square (default), Triangle, Hexagon>
- **Edge behavior:** <e.g. Wrap X and Y (default), Block X and Y>
- **Neighborhood:** <e.g. Edges only (fixed)>

<Optional category note: include it ONLY for a borderline case as described
under "Category note" in SKILL.md (currently only Langton's Ant); otherwise
delete this paragraph.>

<!--
  For any field that maps to a user-selectable configuration choice (cell shapes,
  edge behavior, neighborhood), list the default value first and mark it with
  "(default)", followed by the remaining options, e.g.
  "Square (default), Triangle, Hexagon". Write a value that is fixed for this
  simulation followed by "(fixed)", or omit its line if it does not affect
  visible behavior.
-->

## Rules and mechanics

<Summarize the simulation in plain language as a concise list of 5-10 bullet
points: the starting conditions first, then the per-step rules in their order,
and the termination condition last. Derive this from the SimulationManager
(starting conditions such as seeding and initial placement), the step logic or
step runner with its per-phase `*Logic` classes, and the termination
condition, but phrase it for a user, not a developer.>

- <Rule 1>
- <Rule 2>
- <Rule 3>

## Entities

<One entry per entity the user can see, described in plain language: what it
represents, how it behaves, and how it looks on the grid.>

- **<Short entity name>:** <what it is and how it behaves; how it looks>
- **<Short entity name>:** <what it is and how it behaves; how it looks>

<Layer paragraph: include it ONLY for a simulation with several grid layers
(e.g. resources and agents). One or two sentences on which entities can share a
cell and which one is drawn on top; otherwise delete this paragraph.>

<!--
  Describe the appearance from the entity descriptors in model/entity and the
  drawing code of the MainView: a color name (e.g. "dark blue"), the emoji if
  any, the shape (e.g. inner circle or full cell), and dynamic visual cues
  (e.g. "brighter with more energy", a border marking a state such as newly
  spawned agents), one short sentence per cue. Do not use hex color codes,
  descriptor IDs, localization keys, or Java type names.
-->

## Interactive editing

<!--
  Include this section ONLY if the simulation's MainView declares user actions
  in createUserActionDescriptors() (i.e. the run-time edit toolbar is not
  empty). Otherwise delete the whole section including its heading. Derive the
  precise per-tool behavior from the simulation's `*UserAction` class. Trace
  each visible option control to its actual catalog, enum, choice type, or
  provider regardless of package.
-->

<One short paragraph or a bullet list naming each available tool in plain
language (e.g. "Add sugar (with level selection)", "Remove sugar") and, if
relevant, when it applies (e.g. "applied to the currently selected cell", or
"applies globally" for tools with no cell selection). List the tools in the
toolbar order, i.e. the declaration order in createUserActionDescriptors().>

## Configuration

<A brief prose summary of what the user can adjust. Cover every control exposed
by the simulation's ConfigView, but group related UI panes into the semantic
documentation categories below rather than mirroring every pane as a separate
subsection. In particular, combine all rule-oriented panes under Rules while
preserving their conceptual groups in prose. Omit empty categories. Add another
subsection only for a genuinely distinct concern that would be misleading under
every standard category. In each subsection, name every control with all its
values as described under "Conventions" in SKILL.md, e.g. "**Sugar peaks**
(1 to 5, default 4) sets the number of sugar hills."; write prose, not a
table.>

### Structure

<Grid-related settings, e.g. cell shape, grid size, edge behavior.>

### Layout

<Rendering/display settings, e.g. cell edge length, cell display mode.>

### Initialization

<Starting conditions, e.g. random seed and initial populations.>

### Rules

<Parameters that govern behavior over time, e.g. life-cycle and interaction
settings.>

## Screenshot

<!--
  Verify that assets/screenshots/screenshot_<package>_01.png exists relative to
  the repository root (always the _01 variant). Use the ../../assets/... path
  below from docs/simulations/. If the file does not exist, delete this entire
  section including its heading.
-->

![<Display title> screenshot](../../assets/screenshots/screenshot_<package>_01.png)

## References

<!--
  Include this section ONLY if simulation.<name>.url in messages_en_US.properties
  is set to a non-empty value. Otherwise delete the whole section including its
  heading.
-->

- <Bare URL from the en_US `simulation.<name>.url` key, e.g.
  https://en.wikipedia.org/wiki/Wa-Tor>

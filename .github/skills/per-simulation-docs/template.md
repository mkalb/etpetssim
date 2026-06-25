<!--
  Per-simulation documentation generated/maintained by the `per-simulation-docs` skill.
  Audience: end users of the application (not developers).
  Replace every <...> placeholder. Remove this comment in the final file.
  Keep section headings and their order. Do not add a "Controls" or "Tips" section
  unless requested; this template intentionally omits them.
  Conditionally included sections (delete the whole section if not applicable):
    - "Interactive editing" — only if the MainView declares run-time edit tools.
    - "Screenshot"          — only if screenshot_<package>_01.png exists.
    - "References"          — only if simulation.<name>.url is set.
-->

# <Display title, e.g. Wa-Tor>

<One-line summary, derived from the en_US subtitle, e.g. "Simulate the Wa-Tor world.">

## Overview

<Two to four sentences in plain language: what this simulation models, the core
idea, and what a user sees happening on the grid. Avoid implementation detail.>

## Category and grid

- **Category:** <Agent-based simulation | Cellular automaton>
- **Cell shapes:** <e.g. Square (default), Triangle, Hexagon>
- **Edge behavior:** <plain-language note if relevant, otherwise omit this line>
- **Neighborhood:** <plain-language note if relevant, otherwise omit this line>

<!--
  For any field that maps to a user-selectable configuration choice (cell shapes,
  edge behavior, neighborhood), list the default value first and mark it with
  "(default)", followed by the remaining options, e.g.
  "Square (default), Triangle, Hexagon".
-->

## Rules and mechanics

<Summarize the core per-step loop in plain language as a concise list of 3-7
bullet points. Derive this from whichever step class the simulation provides
(step-logic, update-strategy, or step-runner) together with the model classes,
but phrase it for a user, not a developer.>

- <Rule 1>
- <Rule 2>
- <Rule 3>

## Entities

<One entry per entity the user can see, described in plain language: what it
represents and how it behaves. Do not reproduce colors/emoji tables from the
entity catalog; keep this conceptual.>

- **<Entity name>:** <what it is and how it behaves>
- **<Entity name>:** <what it is and how it behaves>

<!--
  If the simulation renders dynamic visual cues that the entity catalog does
  not cover (e.g. brightness scaled by a value, an extra border to mark a
  state change such as newly spawned agents), mention them here in one short
  sentence per cue.
-->

## Interactive editing

<!--
  Include this section ONLY if the simulation's MainView declares user actions
  in createUserActionDescriptors() (i.e. the run-time edit toolbar is not
  empty). Otherwise delete the whole section including its heading.
-->

<One short paragraph or a bullet list naming each available tool in plain
language (e.g. "Add sugar (with level selection)", "Remove sugar") and, if
relevant, when it applies (e.g. "applied to the currently selected cell").>

## Configuration

<A brief prose summary of what the user can adjust, split into the configuration
panes below. Use only the panes that exist for this simulation (derive them from
the grouping comments in the configuration class, e.g. Structure, Layout,
Initialization, Rules) and omit any pane that does not apply. Keep each pane to a
short sentence or two; mention notable limits or defaults inline only where they
help the user, and do not use a table.>

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
  Use the file assets/screenshots/screenshot_<package>_01.png (always the _01
  variant). If that file does not exist, delete this entire section including
  its heading.
-->

![<Display title> screenshot](../../assets/screenshots/screenshot_<package>_01.png)

## References

<!--
  Include this section ONLY if simulation.<name>.url in messages_en_US.properties
  is set to a non-empty value. Otherwise delete the whole section including its
  heading.
-->

- <External reference from the en_US `simulation.<name>.url` key>

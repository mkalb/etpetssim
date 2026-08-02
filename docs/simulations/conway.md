# Conway's Game of Life

Simulate Conway's Game of Life

## Overview

Conway's Game of Life is a classic cellular automaton: every cell on the grid
is either alive or dead, and the whole grid updates in lockstep, generation
by generation, based only on how many living neighbors each cell has. Simple
local rules produce a wide variety of emergent behavior, from stable shapes
and repeating oscillators to gliders that travel across the grid. The grid
starts with a random scattering of living cells and evolves on its own once
the simulation runs.

## Category and grid

- **Category:** Cellular automaton
- **Cell shapes:** Square (default), Triangle, Hexagon
- **Edge behavior:** Wrap-around on both axes (default), Blocked on both axes
- **Neighborhood:** Edges and vertices (fixed, not user-configurable)

## Rules and mechanics

- Each generation, every cell is updated at the same time, based on the state of the previous generation (a synchronous step).
- A living cell counts its living neighbors; if that count matches one of the configured "survive" counts, it stays alive, otherwise it dies.
- A dead cell counts its living neighbors; if that count matches one of the configured "birth" counts, it comes to life.
- The default rule set is the classic Game of Life: a living cell survives with 2 or 3 living neighbors, and a dead cell is born with exactly 3.
- The grid starts with a random percentage of cells alive, using a seeded random generator so the same seed reproduces the same start.
- The simulation automatically stops once no cells are alive anymore, or once a generation produces no changes at all (a stable or perfectly repeating state).

## Entities

- **Alive:** A living cell, shown in a distinct color; it may survive, die, or trigger the birth of a neighboring cell depending on the active rules.
- **Dead:** An empty cell, shown in the background color; it may come to life if enough living neighbors surround it.

## Interactive editing

- **Clear grid:** Removes all living cells from the grid; applies globally, without needing a selected cell.
- **Toggle cell:** Flips the currently selected cell between alive and dead.
- **Place pattern:** Stamps a chosen well-known pattern (e.g. Blinker, Glider, Pulsar) onto the grid, anchored at the currently selected cell. The available patterns depend on the active cell shape and rule set: most patterns require square cells with the classic Game of Life rules, while the HighLife replicator pattern requires square cells with the HighLife rule set.

## Configuration

### Structure

Choose the cell shape, grid size, and edge behavior (wrap-around or blocked).

### Layout

Adjust the rendered cell edge length and the cell display mode (solid shape, bordered shape, circle, or bordered circle).

### Initialization

Set the random seed and the initial percentage of living cells. A hint suggests a recommended starting density based on the currently selected rule preset.

### Rules

Pick a preset rule set (different presets are offered per cell shape), enter a rule string directly in S/B notation (e.g. `23/3`), or fine-tune individual survive and birth neighbor counts with checkboxes.

## Screenshot

![Conway's Game of Life screenshot](../../assets/screenshots/screenshot_conway_01.png)

## References

- https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life


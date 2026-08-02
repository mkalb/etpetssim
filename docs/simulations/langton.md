# Langton's Ant

Simulate Langton's Ant

## Overview

Langton's Ant is a cellular automaton in which one or more simple "ants" crawl
across a grid of colored ground cells. Each ant looks at the color of the cell
it stands on, turns according to a fixed rule for that color, flips the
cell's color to the next one in a cycle, and moves forward one step. Despite
these trivial per-step rules, the ants trace out intricate, evolving patterns
that a user can watch unfold on the grid.

## Category and grid

- **Category:** Cellular automaton
- **Cell shapes:** Square (default), Triangle, Hexagon
- **Edge behavior:** Wrap (default) or Absorb; an ant that reaches an absorbing
  edge is removed from the simulation.
- **Neighborhood:** Fixed to edge-adjacent neighbors (an ant always moves to a
  cell sharing an edge with its current cell).

## Rules and mechanics

- A single ant starts at the center of the grid, facing north, on a ground
  cell set to its first color; all other cells start unvisited (blank).
- Each step, every ant looks at the color of the cell ahead of it in its
  current facing direction.
- The ant turns according to the turn assigned to that color by the
  configured turn rule (left, right, a double turn, no turn, or a U-turn,
  depending on cell shape and rule), then moves onto that cell.
- The cell the ant leaves keeps its ground color; the cell the ant moves onto
  advances to the next color in the configured cycle (an unvisited cell
  becomes the first color).
- An ant that would move off the grid is removed if the edge behavior absorbs
  it, or wraps around to the opposite side if the edge behavior wraps.
- If two ants would move onto the same cell in one step, the ant that tried to
  move there is removed instead of colliding.
- The simulation ends once every ant has been removed, or once every cell on
  the grid has been visited at least once.

## Entities

- **Ant:** A single mobile agent that moves across the grid, one cell per
  step, turning based on the color of the ground it steps onto. Its current
  facing direction is shown as an arrow drawn on the cell.
- **Ground cell:** A stationary cell colored according to how many times an
  ant has passed over it, cycling through up to 16 colors as defined by the
  active turn rule. Cells that have never been visited are shown as blank
  (unvisited).

## Interactive editing

- **Add Ant:** Places a new ant on the selected cell, using a direction chosen
  from a dropdown of directions valid for the current cell shape.
- **Remove Ant:** Removes the ant from the selected cell.

## Configuration

### Structure

Choose the cell shape (square, triangle, or hexagon), the grid width and
height (100-2000 cells, default 200x200), and the edge behavior (wrap around
or absorb ants at the border).

### Layout

Set the rendered cell edge length (1-50 pixels, default 4) and choose between
plain shapes or shapes with a border outline.

### Rules

Pick a preset turn rule for the current cell shape from a dropdown (options
vary by shape and grow in complexity), or enter a custom turn rule directly as
a compact string using `L` (left), `R` (right), `L2`/`R2` (double turn), `N`
(no turn), and `U` (U-turn). Triangle cells only support `L`, `R`, and `U`.
The rule string must contain at least 2 and at most 16 turns; the default is
`RL`, alternating right and left turns each time a cell is visited.

## Screenshot

![Langton's Ant screenshot](../../assets/screenshots/screenshot_langton_01.png)

## References

- https://en.wikipedia.org/wiki/Langton%27s_ant


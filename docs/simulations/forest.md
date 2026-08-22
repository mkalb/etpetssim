# Forest-fire model

Simulate the Forest-fire model

## Overview

The Forest-fire model shows how a forest changes as trees grow, fires spread,
and burning areas become empty again. Each cell is empty, contains a healthy
tree, or contains a burning tree, producing shifting clusters of vegetation
and fire across the grid.

## Category and grid

- **Category:** Cellular automaton
- **Cell shapes:** Hexagon (default), Triangle, Square
- **Edge behavior:** Block X and Y (default), Wrap X and Y
- **Neighborhood:** Edges only (default), Edges and vertices

## Rules and mechanics

- The grid starts with trees placed randomly according to the configured tree
  density; all remaining cells are empty.
- Each empty cell has a configurable chance to grow a tree on every step.
- A healthy tree starts burning when a neighboring cell is burning or when the
  tree is struck by lightning according to the configured chance.
- A burning tree becomes an empty cell on the next step.
- All cells are updated together from the same current state, so changes made
  during a step affect their neighbors only on the following step.
- Fire spreads through the selected neighborhood and follows the selected edge
  behavior at the grid boundaries.

## Entities

- **Empty Cell:** A cell without a tree where a new tree may grow.
- **Tree Cell:** A healthy tree that can catch fire from a neighbor or a
  lightning strike.
- **Burning Tree:** A tree that can ignite neighboring trees and becomes an
  empty cell on the next step.

## Interactive editing

- **Cycle State:** Applied to the selected cell, this tool cycles its state from
  empty to tree, tree to burning, and burning to empty.

## Configuration

You can adjust the grid, its visual presentation, the initial forest, and the
probabilities that control growth and fire.

### Structure

Choose Hexagon (default), Triangle, or Square cells; set the grid width and
height from 4 to 1,000 cells, with defaults of 100 by 50; and choose between
blocked edges (default) and edges that wrap in both directions.

### Layout

Set the cell edge length from 1 to 50 pixels (4 by default) and choose Shape,
Bordered shape, Inner circle, Bordered inner circle (default), or Emoji as the
cell display mode.

### Initialization

Set the initial Tree Density from 0% to 100% (20% by default). The Seed can be
left blank for a random value or set to a number or text for repeatable random
placement and events.

### Rules

Choose whether the Neighborhood Mode includes only shared edges (default) or
both edges and vertices. Tree Growth ranges from 0.000 to 0.200 (0.002 by
default), and Lightning Chance ranges from 0.0000 to 0.0200 (0.0010 by
default); each is the per-step probability for the corresponding event.

## Screenshot

![Forest-fire model screenshot](../../assets/screenshots/screenshot_forest_01.png)

## References

- https://en.wikipedia.org/wiki/Forest-fire_model

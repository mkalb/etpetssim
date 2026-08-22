# Sugarscape

Simulate the Sugarscape world

## Overview

Sugarscape models agents searching a landscape for sugar that they need to
survive. Sugar is concentrated around several peaks, while agents move across
the grid, harvest resources, spend energy, and eventually die and are replaced.

## Category and grid

- **Category:** Agent-based model
- **Cell shapes:** Square (default), Hexagon
- **Edge behavior:** Wrap X and Y (default), Block X and Y, Block X and Wrap Y,
  Wrap X and Block Y
- **Neighborhood:** Edges only

## Rules and mechanics

- Sugar is initially distributed around one to five peaks, with richer cells
  near each peak and some random variation across the landscape.
- Agents start on random cells according to the configured initial percentage
  and act in a new random order on every step.
- Each agent looks along edge-connected directions within its vision range,
  favors the richest unoccupied location, and prefers nearer locations when
  sugar amounts are equal. It moves one cell toward the chosen location.
- After moving, an agent harvests all sugar on its cell, adds it to its energy,
  and then spends energy according to its metabolism.
- An agent dies when it runs out of energy or reaches its maximum age. Each
  death is followed by an attempt to place a new agent on a random free cell.
- After all agents have acted, every sugar resource regenerates by the
  configured amount, up to that cell's maximum.

## Entities

- **Sugar resource:** A cell containing harvestable sugar. Its brightness
  reflects the current amount of sugar.
- **Agent:** An individual that searches for and consumes sugar. Its brightness
  reflects its current energy, and a white border marks a newly spawned agent.
- **Terrain:** The underlying landscape on which sugar and agents appear.

## Interactive editing

- **Remove Sugar:** Removes the sugar resource from the selected cell.
- **Add Sugar:** Adds or replaces sugar on the selected cell. The **Sugar
  Level** option offers Low (default, 25% of the configured maximum), Medium
  (50%), and High (100%).

## Configuration

You can adjust the world structure, appearance, initial sugar landscape and
agent population, and the rules governing resource recovery and agent survival.

### Structure

Choose Square or Hexagon cells, set a grid width and height from 10 to 500
cells, and select whether each axis blocks movement or wraps to the opposite
edge. The defaults are a 50 by 50 square grid that wraps in both directions.

### Layout

Set the Cell Edge Length from 1 to 50 pixels; the default is 8 pixels. Cells
are displayed as shapes.

### Initialization

Set an optional Seed, the initial Agents percentage from 0% to 100% (20% by
default), and one to five Sugar Peaks (four by default). You can also set the
Sugar Radius Limit from 0 to 100 (14 by default), Max Sugar Amount from 1 to 20
(8 by default), and Agent Initial Energy from 1 to 20 (12 by default).

### Rules

Set Sugar Regeneration and Agent Metabolism from 1 to 10 per step (defaults 1
and 2), Agent Vision Range from 1 to 10 cells (default 8), and Agent Max Age
from 1 to 1,000 steps (default 100).

## Screenshot

![Sugarscape screenshot](../../assets/screenshots/screenshot_sugar_01.png)

## References

- https://en.wikipedia.org/wiki/Sugarscape

# Rebounding Entities

Simulate rebounding entities

## Overview

Rebounding Entities is a toy physics simulation on a grid: a set of moving
entities travel in straight lines and bounce off the grid boundary, off
walls, or off each other. There is no growth or reproduction here, only
motion and collision, so the entity count only ever shrinks until at most one
mover remains. The user sees a field of small moving markers gliding across
the grid, occasionally colliding with fixed wall segments or with each other.

## Category and grid

- **Category:** Agent-based simulation
- **Cell shapes:** Hexagon (default), Square
- **Edge behavior:** The grid boundary always blocks movement; entities bounce
  off it instead of wrapping around or being removed.
- **Neighborhood:** Edges and vertices (default), Edges only

## Rules and mechanics

- At startup, a configurable number of vertical wall segments is generated,
  evenly spaced and centered across the grid width.
- A configurable percentage of the remaining ground cells is filled with
  moving entities, each starting with a random movement direction drawn from
  the directions available for the chosen cell shape and neighborhood mode.
- On each step, every moving entity attempts to advance by one cell in its
  current direction.
- If the next cell lies outside the grid, the entity bounces: its direction
  is reflected so it continues on a new course instead of leaving the grid.
- If the next cell is empty ground, the entity simply moves there.
- If the next cell is a wall, the wall is destroyed (it becomes ground again)
  and the entity bounces off it, continuing in a new direction.
- If the next cell holds another moving entity, the two collide: the other
  entity is destroyed and this entity moves into its cell.
- The simulation ends once at most one moving entity remains on the grid.

## Entities

- **Ground:** An empty, passable cell that moving entities and walls can
  occupy.
- **Wall:** A fixed obstacle. Entities bounce off it and destroy it on
  contact, turning it back into ground.
- **Rebounder (Moving Entity):** An entity that travels across the grid in a
  straight line, changing direction whenever it hits the grid boundary, a
  wall, or another rebounder.

## Interactive editing

While the simulation is running, the following tools are available on the
edit toolbar:

- **Add Wall:** Places a wall on the currently selected ground cell.
- **Remove Wall:** Removes the wall on the currently selected cell, turning
  it back into ground.
- **Add Rebounder:** Places a new moving entity on the currently selected
  ground cell, using a direction chosen from an accompanying direction
  selector.
- **Remove Rebounder:** Removes the moving entity on the currently selected
  cell, turning it back into ground.
- **Fill Walls:** Applies globally, turning every remaining ground cell on
  the grid into a wall.

## Configuration

### Structure

Choose the cell shape (Hexagon or Square), the grid width and height. The
grid edge behavior is fixed to blocking, so entities always bounce off the
boundary.

### Layout

Set the rendered cell edge length in pixels and the cell display mode (plain
shapes or shapes with a border).

### Initialization

Set the random seed, the number of vertical wall segments to generate at
startup, and the initial percentage of ground cells populated with moving
entities.

### Rules

Choose the neighborhood mode, which determines both the set of directions a
moving entity can travel in and which neighboring cells count as adjacent
for movement and collisions: edges and vertices (default) or edges only.

## Screenshot

![Rebounding Entities screenshot](../../assets/screenshots/screenshot_rebounding_01.png)


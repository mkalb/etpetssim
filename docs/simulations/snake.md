# Snake

Simulate the classic Snake game

## Overview

This simulation runs many independent snakes at once on a shared grid, each
one following its own movement strategy instead of being controlled by a
player. Snakes crawl across the grid, growing longer and scoring points each
time they eat food, while avoiding walls, the grid edges, and other snakes'
bodies. When a snake can no longer move, it dies and either respawns
elsewhere or is removed for good, depending on the configured death mode.

## Category and grid

- **Category:** Agent-based simulation
- **Cell shapes:** Hexagon (default), Square
- **Edge behavior:** Wrap X / Block Y (default), Block X/Y, Wrap X/Y, Block X / Wrap Y
- **Neighborhood:** Fixed to edge-adjacent neighbors only; not user-configurable.

## Rules and mechanics

- Each step, every living snake head looks at its ground and food neighbor
  cells (only cells reachable across a valid or wrapped grid edge count) and
  picks a move using its assigned movement strategy; strategies favor moves
  such as continuing straight, heading toward food, staying near the snake's
  own body, or preferring vertical/horizontal directions.
- If no ground or food neighbor is available, the snake dies instead of
  moving.
- Moving onto a food cell grows the snake, awards points that scale with its
  current length, and immediately respawns a new food cell at a random free
  ground cell elsewhere on the grid.
- Moving onto an empty ground cell advances the snake normally; its tail
  segment is removed unless the snake still has pending growth left over from
  a recent meal.
- A dead snake is shown for one step in a distinct "dead" color before being
  cleared from the grid. Depending on the configured death mode, it then
  either respawns at a random free cell with a fresh, short body, or is
  permanently removed from the simulation.
- At start-up, a configurable number of vertical wall segments is generated at
  spaced-out horizontal positions with randomized length, snakes are placed at
  random free cells and assigned strategies in a round-robin fashion, and food
  is scattered across the remaining free ground cells.
- The simulation ends once no snake heads remain at all, or once none of the
  remaining snake heads are still alive.

## Entities

- **Ground:** Plain, empty terrain that snakes and food can occupy.
- **Wall:** A fixed obstacle; snakes cannot move onto wall cells.
- **Food:** A pickup that a snake head consumes to grow, score points, and
  trigger a new food cell elsewhere.
- **Snake Head:** The leading cell of a snake; it decides the snake's
  direction each step based on its assigned movement strategy.
- **Snake Segment:** A body cell trailing behind a snake head.

Dead snake heads and segments are rendered in a different, muted color from
living ones, and the currently selected snake's head and segments are
highlighted with a lighter blended color and an outline.

## Interactive editing

While the simulation runs, the following tools can be applied to the
currently selected cell:

- Add Wall / Remove Wall
- Add Food / Remove Food
- Add Snake (with a movement-strategy option to choose the new snake's
  behavior)
- Remove Snake (removes the entire snake, head and body, when a head or any
  body segment is selected)

## Configuration

### Structure

Choose the cell shape (Hexagon or Square), the grid edge behavior, and the
grid width and height.

### Layout

Adjust the rendered cell edge length and the cell display mode.

### Initialization

Set the random seed, the number of vertical wall segments, the number of food
cells, the number of snakes to start with, and the pending growth each snake
starts (and respawns) with.

### Rules

Choose the death mode (respawn or permanent death), how many segments a snake
grows per food eaten, the base points awarded per food eaten, and the
multiplier that scales bonus points by the snake's current length.

## Screenshot

![Snake screenshot](../../assets/screenshots/screenshot_snake_01.png)

## References

- https://en.wikipedia.org/wiki/Snake_(video_game_genre)


# ET Pets

Simulate extraterrestrial pets

## Overview

ET Pets is a custom agent-based simulation set on a hexagonal grid inhabited
by small alien creatures ("pets"). Pets wander across varied terrain, eat
plants and insects, lay eggs that hatch into new pets, age, and eventually
die. Watching the grid, you see pets roam and cluster around food, leave
fading scent trails behind them, and their population rise and fall as
generations are born and die off.

## Category and grid

- **Category:** Agent-based simulation
- **Cell shapes:** Hexagon (fixed; no other shape is currently offered)
- **Edge behavior:** Block in both axes (fixed; pets cannot move past the
  grid border)
- **Neighborhood:** Fixed to edge-adjacent neighbors for movement and
  interaction

## Rules and mechanics

- Each step, every pet and egg acts once in a fixed order, followed by
  resource regrowth and scent-trail fading.
- An egg incubates for a number of steps, then hatches into a new pet at the
  same position.
- Each pet loses energy every step and ages; if its energy runs out, or an
  age-related mortality chance triggers, it dies and is removed from the grid
  after staying visible for one more step.
- A living pet weighs its options - waiting, moving to a nearby cell, eating
  an adjacent plant or insect, or reproducing with a nearby eligible partner -
  based on its energy level, nearby food and potential partners, scent-trail
  freshness, and local crowding, then performs the best-scoring option.
- Reproduction lays an egg on a free neighboring ground cell; the egg's traits
  are inherited from both parents with a small chance of mutation, and only
  unrelated, cooled-down, sufficiently old and energetic pets can reproduce.
- Moving onto or resting on a ground cell leaves a scent trail that
  intensifies with repeated visits and gradually fades away once pets stop
  passing over it.
- At startup, rock and water terrain and plant and insect resources are
  scattered randomly by configured percentage, then pets are placed on the
  remaining free cells with randomized starting age and energy; the
  simulation ends once no active pets and no eggs remain.

## Entities

- **ET Pet:** The main mobile creature. It moves, eats, ages, reproduces, and
  eventually dies of energy depletion or old age. Its display brightness
  scales with its current energy, and a dead pet is shown dimmed for one
  final step before disappearing.
- **ET Pet Egg:** Laid by two reproducing pets on a nearby free ground cell.
  It incubates silently for a number of steps before hatching into a new pet;
  its display brightness scales with the remaining incubation time.
- **Plant Resource:** A stationary food source on ground cells that pets can
  eat for energy. It slowly regenerates over time, and its brightness scales
  with its remaining amount.
- **Insect Resource:** A second stationary food source, similar to a plant
  but with different nutritional value and regeneration behavior. Its
  brightness also scales with its remaining amount.
- **Ground Terrain:** The default, walkable terrain that pets can move
  across, eat on, and lay eggs on.
- **Rock Terrain:** Impassable terrain that blocks movement and cannot host
  resources or agents.
- **Water Terrain:** Impassable terrain, distinct from rock, that also blocks
  movement.
- **Trail Terrain:** A temporary scent mark left on ground cells by passing
  pets. Its brightness scales with intensity, which grows with repeated
  visits and decays back to plain ground once no pet reinforces it.

## Interactive editing

While the simulation is running, you can apply the following tools to the
currently selected cell:

- **Set terrain:** changes the selected cell to ground, rock, or water,
  chosen from a dropdown; only applies to a cell that has no resource or
  agent on it.
- **Set resource:** places a plant, an insect, or clears any resource on the
  selected cell, chosen from a dropdown; only applies to a ground cell that
  has no agent on it.

## Configuration

You can adjust the grid structure and appearance, and the starting
percentages for terrain, resources, and pets.

### Structure

Choose the grid width and height; cell shape (hexagon) and edge behavior
(blocked on both axes) are fixed and not user-selectable.

### Layout

Set the rendered cell edge length; cells are always displayed as filled
shapes.

### Initialization

Set the random seed and the initial percentages of rock, water, plant, and
insect cells (each individually capped, with their combined total also
capped so plenty of ground remains free), and the initial number of pets to
spawn on the remaining free cells.


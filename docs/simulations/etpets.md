# ET Pets

Simulate extraterrestrial pets

## Overview

ET Pets models a population of extraterrestrial pets searching for food,
avoiding obstacles, leaving trails, and reproducing. The hexagonal world keeps
terrain, renewable resources, and pets on separate layers, so a ground cell can
hold a resource and a pet at the same time.

## Category and grid

- **Category:** Agent-based model
- **Cell shapes:** Hexagon (default)
- **Edge behavior:** Blocked on all sides (default)
- **Neighborhood:** Edge-sharing cells only (default)

## Rules and mechanics

- At the start, rocks and water are placed randomly, plants and insects are
  added to traversable cells, and pets are placed on empty traversable cells.
  Using the same seed and configuration reproduces the same starting world.
- Each step processes eggs and pets first. Eggs count down until they hatch;
  pets lose energy and may wait, move, eat a nearby resource, reproduce with a
  nearby eligible pet, or die from depleted energy or old age.
- Pets evaluate their surroundings when choosing an action. Resources, possible
  partners, trails, obstacles, crowding, and recently visited cells can all
  influence where they go.
- Moving or waiting creates or strengthens a trail. Trails can attract pets,
  fade while unoccupied, and eventually return to ordinary ground.
- Reproduction places an egg in available nearby space. The new pet inherits
  traits from both parents, with a chance of mutation.
- After all agents act, plants and insects regenerate up to their local
  capacity, then trails decay. A pet that dies remains visible in a darkened
  state for one step before disappearing.

## Entities

- **Ground:** Traversable terrain on which resources, pets, and eggs can exist.
- **Rock:** An impassable terrain obstacle.
- **Water:** Impassable terrain that blocks pet movement.
- **Trail:** A temporary trace left by pets; its brightness reflects its
  strength as it is reinforced and fades.
- **Plant:** A renewable food resource. Its brightness reflects the amount
  currently available.
- **Insect:** A renewable food resource. Its brightness reflects the amount
  currently available.
- **ET Pet:** A living agent that searches, eats, moves, and reproduces. Its
  brightness reflects its energy, and a dead pet is shown darkened briefly.
- **ET Pet Egg:** An inherited offspring waiting to hatch. Its appearance
  changes as incubation progresses.

## Interactive editing

Editing applies to the selected cell:

- **Set Terrain:** Choose **Ground**, **Rock**, or **Water**. The terrain changes
  only when the cell contains neither a resource nor an agent.
- **Set Resource:** Choose **None**, **Plant**, or **Insect** to remove, add, or
  replace a resource. The selected cell must be ground terrain without an
  agent.

## Configuration

You can adjust the grid dimensions and display size, choose a random seed, and
set the starting amounts of terrain, resources, and pets.

### Structure

The grid is fixed to hexagonal cells with blocked edges. Width and height can
each range from 20 to 200 cells; the defaults are 50 by 20 cells, and width is
adjusted in steps of four.

### Layout

Cell edge length ranges from 5 to 50 pixels and defaults to 10 pixels. Cells
are displayed as shapes; no alternative display mode is available.

### Initialization

The seed controls repeatable random placement and is empty by default. Rocks,
water, plants, and insects each allow 0% to 50%, with defaults of 1%, 2%, 5%,
and 1% respectively; their combined percentage cannot exceed 50%. The initial
pet count ranges from 0 to 100 and defaults to 10.

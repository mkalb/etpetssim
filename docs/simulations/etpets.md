# Extraterrestrial pets

Watch extraterrestrial pets hatch, roam, and eat

## Overview

This simulation follows a population of extraterrestrial pets on a landscape of
ground, rock, and water. The pets roam in search of plants and insects, leave
trails behind, and lay eggs when they meet a suitable partner. Each pet weighs
its options every step and picks the most promising one, so the user sees pets
wandering, gathering around food, wearing paths into the ground, and new
generations hatching while old pets die.

## Category and grid

- **Category:** Agent- and movement-based models
- **Cell shapes:** Hexagon (fixed)
- **Edge behavior:** Block X and Y (fixed)
- **Neighborhood:** Edges only (fixed)

## Rules and mechanics

- At the start, rock and water cells are scattered at random. Plants and
  insects are then placed on random free ground cells, each fully grown and
  with its own capacity and regrowth speed. Finally, the pets are placed on
  random free ground cells with individual maximum energy, nearly full energy,
  and different ages.
- In every step, the eggs and pets act one after another in the order in which
  they were created. An egg counts down its incubation time and then hatches
  into a young pet that starts with only part of its maximum energy.
- Every living pet loses a little energy per step. A pet dies when its energy
  is used up, and an old pet may also die by chance, which becomes more likely
  the older it gets. A dead pet stays visible for one more step.
- A surviving pet rates its possible actions and performs the best one: wait,
  move to a neighboring cell, eat from a neighboring plant or insect, or
  reproduce with a neighboring pet. If several actions are rated equally, one
  of them is chosen at random.
- Eating is favored by hunger, by very low energy, by a richer food source, and
  by a young age; it is discouraged when the food would exceed the maximum
  energy of the pet, and a pet with full energy does not eat. Moving is favored
  toward cells next to food (more so the hungrier the pet is), toward cells
  next to a possible partner, onto well-used trails, and, for energetic pets
  that move cheaply, onto untouched ground; it is discouraged for recently
  visited cells, for cells with few free neighbors, and for crowded cells.
  Waiting is mostly chosen by old pets or when nothing else is possible.
- Pets move only onto ground or trail cells that hold no resource, pet, or egg;
  rock, water, and the grid edges block them. Moving costs additional energy.
  A pet eats from a neighboring cell without entering it: the plant or insect
  loses part of its amount and the pet gains energy, more from an insect than
  from a plant.
- Two neighboring pets can reproduce when both are old enough, have enough
  energy, are not resting after an earlier reproduction, and are not parent and
  child or siblings. They lay an egg on a free ground cell next to both of
  them. The egg inherits the average of the traits of its parents, sometimes
  with a small mutation, and the choice is rated higher the better these traits
  are.
- A pet that waits, moves, eats, or hatches leaves a trail on its cell or
  strengthens the existing one. After the pets have acted, every plant and
  insect regrows a little up to its capacity, and every trail without a pet or
  egg on it fades until it turns back into ground.
- The simulation ends on its own when neither a living pet nor an egg is left.

## Entities

- **Ground:** open terrain that pets can walk on and where resources and eggs
  can be placed; it is the dark gray background of the grid.
- **Rock:** an obstacle that pets cannot enter; a gray full cell.
- **Water:** an obstacle that pets cannot enter; a blue full cell.
- **Trail:** ground marked by pets that passed or stayed there, which attracts
  other pets when it is strong; a dark brown full cell that becomes brighter
  the more often pets use it and darker as it fades.
- **Plant:** a food source that regrows quickly and gives little energy per
  bite; a green inner circle with a faint border of the same color that becomes
  darker as it is eaten and brighter as it regrows.
- **Insect:** a food source that regrows slowly and gives much energy per bite;
  a dark yellow inner circle with a faint border of the same color that becomes
  darker as it is eaten and brighter as it regrows.
- **Pet:** the creature that roams, eats, and reproduces; a purple inner circle
  that becomes darker as it loses energy and brighter as it eats. A pet that
  has just died is shown as a very dark circle for one more step.
- **Egg:** a pet egg that stays in place until it hatches; a pale pink inner
  circle with a dark purple border that becomes darker as hatching approaches.

The grid has a terrain layer, a resource layer, and a layer for pets and eggs.
A plant, an insect, a pet, or an egg is drawn on top of the terrain of its
cell; a resource never shares a cell with a pet or an egg.

## Interactive editing

Both tools are applied to the currently selected cell:

- **Set terrain** replaces the terrain of the cell, including a trail, with the
  value of the **Terrain** option (Ground, Rock, Water). It works only on cells
  that hold no resource, pet, or egg.
- **Set resource** sets the resource of the cell to the value of the
  **Resource** option (None, Plant, Insect); None removes the resource. It
  works only on ground cells without a trail that hold no pet or egg. A placed
  plant or insect starts fully grown with the largest possible capacity.

## Configuration

The user can adjust the size of the grid, the size of the cells, and the
starting landscape and population.

### Structure

**Grid width** (20 to 200 cells in steps of 2, default 50) and **Grid height**
(20 to 200 cells, default 20) set the size of the grid.

### Layout

**Cell edge length** (5 to 50 pixels, default 10) sets the size of the drawn
cells.

### Initialization

**Seed** (empty by default) sets the start value of the random numbers: leave it
empty for a random seed, or enter a number or any text to repeat a run.

**Rock** (0% to 50%, default 1%) and **Water** (0% to 50%, default 2%) set the
share of all cells that start as rock and as water. **Plants** (0% to 50%,
default 5%) and **Insects** (0% to 50%, default 1%) set the share of all cells
that start with a plant and with an insect. The four shares may add up to at
most 50%; otherwise a validation message is shown.

**Pets** (0 to 100, default 10) sets the number of pets at the start.

# Extraterrestrial pets

Watch extraterrestrial pets hatch, roam, and eat

## Overview

Extraterrestrial pets live on a hexagonal landscape of ground, rock, and water, where plants and insects grow. Each
pet weighs its options in every step: it eats when it is hungry, explores fresh ground, follows well-used trails, and
lays eggs with a suitable partner. The offspring inherit the traits of their parents with small random changes, so
the population changes over generations. On the grid, you see pets roam, trails form and fade, food shrink and regrow,
and eggs hatch into new pets.

## Category and grid

- **Category:** Agent- and movement-based models
- **Cell shapes:** Hexagon (fixed)
- **Edge behavior:** Block X and Y (fixed)
- **Neighborhood:** Edges only (fixed)

## Rules and mechanics

- At the start, rock and water cover randomly chosen cells, and plants, insects, and pets are placed on random free
  ground cells. Plants and insects start full, each with its own capacity and regrowth speed, and the pets start with
  nearly full energy, different energy capacities, and different ages.
- In each step, the eggs and pets act one after another in the order in which they were created. An egg comes closer
  to hatching and then hatches into a young pet with little energy.
- Every living pet loses a little energy in each step. A pet dies when its energy runs out; an old pet also dies with
  a chance that grows with its age. A dead pet stays visible for one step and then disappears.
- Every living pet then chooses one action: wait, move to a neighboring cell, eat from a neighboring plant or insect,
  or lay an egg with a neighboring partner. It takes the best-rated action and picks at random among equally rated
  ones. Pets can only move to free ground or trail cells; rock, water, food, other pets, eggs, and the grid edges
  block the way.
- Moving costs energy. A pet prefers cells that lead toward food or a partner, strong trails, and, when it has plenty
  of energy and moves cheaply, fresh ground. It avoids cells it has just visited, cells with few free neighbors, and
  crowded cells. Eating is preferred by hungry pets, especially when their energy is low, by young pets, and for food
  that gives more energy per bite; it is avoided when the food would overfill the pet, and a pet with full energy
  does not eat. Old pets wait more and more often.
- Eating takes a bite from the food and gives the pet energy; insects give more energy per bite but hold less food
  and regrow more slowly than plants. Pets only eat food that has enough left for a bite.
- Two pets can lay an egg when both are old enough, have enough energy, have rested since their last egg, and are not
  closely related (parent and child or siblings). The egg appears on a free ground cell next to both partners. The
  child inherits the average traits of its parents (energy capacity, movement cost, energy needed for an egg, and rest
  time), each with a small chance of mutation; pairs with better traits are more eager to lay eggs.
- Pets leave a trail on the ground when they wait, move, or eat, and newly hatched pets do so too; an existing trail
  becomes stronger. After all pets have acted, all plants and insects regrow up to their individual maximum, and every
  trail without a pet or egg on it fades a little until it turns back into ground.
- The run ends on its own when neither a living pet nor an egg is left.

## Entities

- **Ground:** walkable land that can hold food, pets, and eggs; drawn as the very dark gray background of the grid.
- **Rock:** an obstacle that pets cannot cross; a gray full cell.
- **Water:** an obstacle that pets cannot cross; a dark blue full cell.
- **Trail:** walkable ground that pets have used recently; a dark brown full cell that becomes brighter as it grows
  stronger and darker as it fades.
- **Plant:** food with a large supply but little energy per bite that regrows quickly; a green inner circle with a
  faint border, brighter when it holds more food.
- **Insect:** food with a small supply but much energy per bite that regrows slowly; a mustard yellow inner circle
  with a faint border, brighter when it holds more food.
- **Pet:** the extraterrestrial pet that roams, eats, and lays eggs; a purple inner circle that is brighter with more
  energy. A dead pet is shown as a very dark purple circle for one step.
- **Egg:** an egg laid by two pets that hatches into a new pet; a pale pink inner circle with a dark purple border
  that becomes darker as hatching approaches.

A pet or egg can stand on ground or a trail and is drawn on top of it; food lies only on ground and is drawn on top of
it. Pets and eggs never share a cell with food.

## Interactive editing

- **Set terrain:** places the terrain selected under **Terrain** (Ground, Rock, Water) on the selected cell, but only
  if the cell holds no plant, insect, pet, or egg. Placing ground on a trail removes the trail.
- **Set resource:** places the resource selected under **Resource** (None, Plant, Insect) on the selected cell, but
  only on ground cells without a trail, pet, or egg. A placed plant or insect starts full with the largest possible
  supply and replaces any existing food; None removes the food from the cell.

## Configuration

You can adjust the size of the grid and its cells, and the initial landscape and population.

### Structure

**Grid width** (20 to 200 cells in steps of 2, default 50) and **Grid height** (20 to 200 cells, default 20) set the
size of the grid.

### Layout

**Cell edge length** (5 to 50 pixels, default 10) sets the size of each cell on the screen.

### Initialization

**Seed** (empty by default) sets the start value of the random numbers: leave it empty for a random seed, or enter a
number or any text to repeat a run.

**Rock** (0% to 50%, default 1%) and **Water** (0% to 50%, default 2%) set the initial share of cells covered by
obstacles. **Plants** (0% to 50%, default 5%) and **Insects** (0% to 50%, default 1%) set the initial share of cells
with food. The shares of rock, water, plants, and insects may add up to at most 50%; otherwise a validation message is
shown.

**Pets** (0 to 100 pets, default 10) sets the initial number of pets.

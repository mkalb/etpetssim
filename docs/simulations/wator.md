# Wa-Tor

Simulate the Wa-Tor world

## Overview

Wa-Tor models a predator-prey ecosystem on a world of water cells. Fish move,
reproduce, and age, while sharks hunt fish for energy and must keep feeding to
survive. The changing fish and shark populations form visible cycles across
the grid.

## Category and grid

- **Category:** Agent-based model
- **Cell shapes:** Square (default), Triangle, Hexagon
- **Edge behavior:** Wrap X and Y (default), Block X and Y
- **Neighborhood:** Edges only (default), Edges and vertices

## Rules and mechanics

- The grid begins with configured shares of fish and sharks placed randomly in
  water. Their starting ages are randomized, and the seed can make the same
  initial world reproducible.
- Fish move to a randomly chosen neighboring water cell. Once old enough, and
  after the configured reproduction interval, a moving fish leaves offspring
  in its previous cell.
- Sharks lose energy each step. They move to and eat a randomly chosen
  neighboring fish when possible; otherwise, they move to neighboring water.
- A shark that moves can leave offspring behind when it meets the configured
  age, energy, and reproduction-interval requirements.
- Fish die at their maximum age. Sharks die at their maximum age or when their
  energy reaches zero.
- Creatures are processed by their positions during each step, so changes made
  by an earlier creature can affect a later creature in the same step.

## Entities

- **Water:** An empty cell where fish or sharks can move.
- **Fish:** Prey that move through water, reproduce, and eventually die of old
  age. Their brightness changes with age.
- **Sharks:** Predators that eat fish to restore energy, reproduce when they
  meet the required conditions, and die from age or depleted energy. Their
  brightness changes with current energy.

## Interactive editing

The edit toolbar provides three tools for the currently selected cell:

- **Add Fish:** Adds a new fish if the selected cell is water.
- **Add Shark:** Adds a new shark if the selected cell is water.
- **Remove Creature:** Removes a fish or shark from the selected cell, leaving
  water behind.

## Configuration

You can adjust the world geometry and appearance, initial populations, and the
life-cycle rules for both species.

### Structure

Choose square, triangle, or hexagon cells; set the grid width and height from 8
to 1,000 cells; and choose whether both axes are blocked or wrap around. The
defaults are square cells, a 200-by-100 grid, and wrapping edges.

### Layout

Set the cell edge length from 1 to 50 pixels and display cells as shapes,
bordered shapes, inner circles, bordered inner circles, or emoji. Shape display
with a 4-pixel edge length is the default.

### Initialization

Set a seed or leave it blank for a random world, then choose the initial fish
and shark percentages. The defaults are 20% fish and 5% sharks, and their
combined share cannot exceed 100%.

### Rules

Choose whether movement and interaction use edge-sharing neighbors only or
also include vertex-sharing neighbors. Fish rules control maximum age,
minimum reproduction age, and the interval between reproductions. Shark rules
add birth energy, energy lost each step, energy gained per fish, and the
minimum energy required for reproduction.

## Screenshot

![Wa-Tor screenshot](../../assets/screenshots/screenshot_wator_01.png)

## References

- https://en.wikipedia.org/wiki/Wa-Tor

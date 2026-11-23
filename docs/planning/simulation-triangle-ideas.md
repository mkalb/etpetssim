# Simulation ideas for triangle cells

**Status:** idea (not planned; after the 1.0 release)

**Related issues:** #3 (candidate simulations), #158 (Conway's Game of Life rules for triangle cells)

Most simulations support triangle cells poorly, and triangle cells are usually their weakest cell shape. This plan
collects simulation ideas in which triangle cells are the only or the best cell shape. Rule ideas for Conway's Game of
Life with triangle cells are tracked in #158.

## Geometry of triangle cells

- `GridCoordinate.isTriangleCellPointingDown()` follows a checkerboard pattern; with `WRAP`, width and height are
  multiples of 2, so the pattern stays consistent on a torus.
- The 3 edge neighbors of a triangle always point in the other direction. Of the 9 vertex neighbors, 6 point in the
  same direction and 3 in the other direction (across the tips).
- The 6 same-orientation vertex neighbors form a triangular lattice inside each orientation.
- A synchronous rule that counts only edge neighbors decouples into two independent automata: the state of upward
  cells at step t depends only on downward cells at step t-1.

## Edge model

Ideas "Side view with gravity" and "Maze" need edges that are present or absent.

- Each edge belongs to exactly one cell. For triangle cells, every inner edge separates exactly one upward and one
  downward triangle, so upward triangles own all three of their edges (3 bits per cell); border edges of downward
  triangles need a rule (e.g., the border is always a wall). For square cells, each cell owns its north and west
  edge; for hexagon cells, each cell owns 3 of its 6 edges.
- Edges are drawn as thick lines; a large `cellEdgeLength` is required. Related: #121, #123.
- Candidate for an engine issue once an idea becomes concrete.

## Two worlds (symbiosis)

- Species A lives only on upward triangles, species B only on downward triangles.
- Within its world, a species spreads over the 6 same-orientation vertex neighbors and competes for space.
- Each species survives only with enough partners across its 3 edges.
- Triangle cells only. Square cells have the same checkerboard property for edge neighbors, but the two worlds are
  not visible geometrically, and each world has only 4 diagonal neighbors.
- Open: concrete rules, parameters, and initialization.

## Sandpile model

- Abelian sandpile (Bak-Tang-Wiesenfeld, a candidate in #3) for all three cell shapes; the threshold is the number of
  edge neighbors (triangle 3, square 4, hexagon 6); default cell shape triangle; neighborhood edges only.
- Edge behavior `ABSORB_XY`: grains falling over the edge are lost.
- State per cell is a grain count (an integer), not an enum.
- Display: colors for 0 to threshold - 1 and a color for toppling cells; statistics: last avalanche size, grains on
  the grid, lost grains.
- Open start modes: a large pile in the center; single grains at random cells; single grains in the center.
- Open: run time for large piles (#54, #155), e.g., an option to compute until stable.

## Side view with gravity

- The grid is viewed from the side (a deliberate extension of the top view described in `README.md`); `BLOCK_X` is
  fixed. Related candidates in #3: "Falling Sand Simulation", "Lemmings".
- Terrain uses the edge model; particles and agents live in a second layer (Sugarscape-style layered model).
- Triangle edges form straight horizontal lines and two 60-degree diagonals, so floors, ramps, and funnels can be
  built. A grain in an upward triangle falls through its base; a grain in a downward triangle slides down-left or
  down-right across a slanted edge. Sand piles form 60-degree slopes without special rules.
- Scope in stages: 1. terrain and sand; 2. water; 3. animals.
- Open: initialization (random, generators, or drawing in the edit mode, #59); top and bottom edge behavior.

## Maze

- A maze from cell edges in the top view; agents explore, search paths, and catch each other. Related candidate in
  #3: "Autonomous Maze Exploration".
- Supports all three cell shapes and meets the triangle goal only partly: triangle cells have at most 3 passages, so
  every junction is a binary choice.
- Open: maze generation (e.g., recursive backtracker, Kruskal, Prim, Wilson), possibly as a visible part of the run.
- Agent ideas:
    - path finding to a goal,
    - exploration that marks visited cells,
    - catching: with simultaneous moves, the parity of the distance between two agents stays the same on triangle
      and square cells, so a pursuer must wait once to catch an evader.

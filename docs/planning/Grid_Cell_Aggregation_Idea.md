# Grid Cell Aggregation Idea

**Status:** idea only, not planned. Priority: later or never. No issue exists; create one only when a concrete
simulation needs this feature.

## Idea

Several small grid cells can be grouped into one larger cell shape. Simulations could then use two cell sizes at the
same time, for example coarse regions or terrain patches on top of a fine grid.

## Geometric Feasibility

The engine renders hexagons flat-top and triangles in a checkerboard-style up/down pattern with horizontal edges (see
`GridTopology`). With these orientations, the following groupings tile exactly:

| Large shape | Small shape | Cells per large cell | Notes |
|---|---|---|---|
| Square, edge `2a` | Square, edge `a` | 4 (2 x 2) | generalizes to `n x n` |
| Triangle, edge `2a` | Triangle, edge `a` | 4 (3 with the same orientation, 1 inverted in the center) | generalizes to `n^2` |
| Hexagon, edge `a` | Triangle, edge `a` | 6 (two triangle rows with 3 triangles each) | generalizes to `6 n^2` with triangle edge `a / n` |
| Hexagon | Hexagon | - | not possible: a hexagon cannot be divided exactly into smaller hexagons |

## Concept: Small Cells in the Model, Large Cells as a Layer

- The `GridModel` stays on the small cells (`GridCell`). All `GridEntity` instances are placed only on small cells.
- The large shape is not a second grid model, but only a kind of layer: a mapping from each small cell coordinate to
  the coordinate of the large cell that contains it, and back to all small cells of a large cell.
- The layer is used in one or both of the following places:
    - **Creation of the `GridModel`:** initialization works on large cells, e.g. all small cells of a large cell get the
      same entity (terrain patches, regions, structured start patterns).
    - **Visualization:** the painter draws the outlines of the large cells or aggregated values per large cell as an
      overlay on top of the small cells (e.g. in `FXGridCanvasPainter`).
- The simulation logic (neighborhoods, step rules) keeps working on the small cells only.
- `CompositeGridModel` is not a fit, because all its layers must share the same grid structure.

## Open Points

- Coordinate mapping per shape pair, including the triangle parity (`x + y` even/odd) and the hexagon column offset
  (odd columns are shifted down by half a hexagon height). The small-cell grid must be aligned so that every large
  cell starts at a matching triangle or square position.
- Grid size constraints: the small-cell grid size must be a multiple of the grouping factor; check the interaction
  with the existing width multiples (`GridTopology.MAX_REQUIRED_WIDTH_MULTIPLE`).
- Grid edges: partial large cells at the edges, and wrapping only when the sizes align.
- Placement of the code: mapping in `engine`, overlay rendering in `ui`, initialization in `simulations`.
- A concrete simulation that benefits from the feature.

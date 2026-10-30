---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/engine/**/*.java"
---

# Engine Package Instructions

Rules for production code in `de.mkalb.etpetssim.engine` and all its sub-packages. `engine`, `engine.model`,
`engine.model.entity`, and `engine.neighborhood` hold the base types of the grid: coordinates, offsets, sizes,
structures, topologies, grid models, cells, entities with their descriptors, compass directions, and neighborhoods with
edge behavior. `engine.support` is the toolkit on top of the base types (grid initializers, grid patterns, agent
orderings, and cancellation checkpoints), and `engine.executor` runs simulation steps (executors, step runners, step
logic contracts, termination conditions, and step results).

## API

- Keep the complete public API of all engine packages, also members without a current caller (e.g.,
  `GridOffset.manhattanLength()`, `ReadableGridModel.isSparse()`, `GridEntity.isConstant(..)`,
  `CompassDirection.listClockwise()`, `GridPatterns.circleOutline(..)`, or `SimulationTerminationCondition.negate()`);
  they form the vocabulary of all grid code.
- Depend only on the JDK, JSpecify, `core`, and the base types of `engine`; never on `ui` or `simulations`.
- Do not use JavaFX, except `javafx.scene.paint.Color` for the rendering hints of entity descriptors in
  `engine.model.entity`.

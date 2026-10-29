---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/engine/*.java"
  - "**/src/main/java/de/mkalb/etpetssim/engine/model/*.java"
  - "**/src/main/java/de/mkalb/etpetssim/engine/model/entity/*.java"
  - "**/src/main/java/de/mkalb/etpetssim/engine/neighborhood/*.java"
---

# Engine Package Instructions

Rules for production code in `de.mkalb.etpetssim.engine`, `engine.model`, `engine.model.entity`, and
`engine.neighborhood`, the base types of the grid: coordinates, offsets, sizes, structures, topologies, grid models,
cells, entities with their descriptors, compass directions, and neighborhoods with edge behavior. The other
sub-packages of `engine` follow the general rules only.

## API

- Keep the complete public API of the base types, also members without a current caller (e.g.,
  `GridOffset.manhattanLength()`, `ReadableGridModel.isSparse()`, `GridEntity.isConstant(..)`, or
  `CompassDirection.listClockwise()`); they form the vocabulary of all grid code.
- Depend only on the JDK, JSpecify, `core`, and the base types of `engine`; never on `ui` or `simulations`.
- Do not use JavaFX, except `javafx.scene.paint.Color` for the rendering hints of entity descriptors in
  `engine.model.entity`.

---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/engine/*.java"
  - "**/src/main/java/de/mkalb/etpetssim/engine/model/*.java"
  - "**/src/main/java/de/mkalb/etpetssim/engine/model/entity/*.java"
---

# Engine Package Instructions

Rules for production code in `de.mkalb.etpetssim.engine`, `engine.model`, and `engine.model.entity`, the base types of
the grid: coordinates, offsets, sizes, structures, topologies, grid models, cells, and entities with their descriptors.
The other sub-packages of `engine` follow the general rules only.

## API

- Keep the complete public API of the base types, also members without a current caller (e.g.,
  `GridOffset.manhattanLength()`, `ReadableGridModel.isSparse()`, or `GridEntity.isConstant(..)`); they form the
  vocabulary of all grid code.
- Depend only on the JDK, JSpecify, `core`, and the base types of `engine`; never on `ui` or `simulations`.
- Do not use JavaFX, except `javafx.scene.paint.Color` for the rendering hints of entity descriptors in
  `engine.model.entity`.

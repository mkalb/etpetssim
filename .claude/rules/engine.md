---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/engine/*.java"
---

# Engine Package Instructions

Rules for production code in `de.mkalb.etpetssim.engine`, the base types of the grid: coordinates, offsets, sizes,
structures, topologies, and their enums. The sub-packages of `engine` follow the general rules only.

## API

- Keep the complete public API of the base types, also members without a current caller (e.g.,
  `GridOffset.manhattanLength()` or `GridSize.aspectRatio()`); they form the vocabulary of all grid code.
- Depend only on the JDK, JSpecify, and `core`; never on `ui`, `simulations`, or JavaFX.

---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/engine/**/*.java"
---

# Engine Package Instructions

Rules for production code in `de.mkalb.etpetssim.engine` and its sub-packages. The base types of the grid lie in
`engine`, `engine.model`, `engine.model.entity`, and `engine.neighborhood`; `engine.support` (grid initializers,
patterns, agent orderings, cancellation checkpoints) and `engine.executor` (executors, step runners, step logic,
termination conditions, step results) build on them.

## API

- Keep the complete public API of all engine packages, also members without a current caller (e.g.,
  `GridOffset.manhattanLength()`, `CompassDirection.listClockwise()`, `SimulationTerminationCondition.negate()`); they
  form the vocabulary of all grid code.
- Depend only on the JDK, JSpecify, `core`, and the base types of `engine`.
- Do not use JavaFX, except `javafx.scene.paint.Color` for the rendering hints of entity descriptors in
  `engine.model.entity`.

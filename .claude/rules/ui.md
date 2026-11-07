---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/ui/*.java"
---

# UI Package Instructions

Rules for production code in `de.mkalb.etpetssim.ui`: reusable UI infrastructure and application-level dialogs.

## Scope

- Add code here only when several UI or simulation components can reuse it; no simulation logic.
- Depend only on the JDK, JSpecify, JavaFX, `core`, and `engine`.
- Keep the complete public API of the drawing toolkit (`FXGridCanvasPainter`, `FXPaintFactory`, `GridGeometry`, and
  `CellDimension`), also members without a current caller (e.g., `CellDimension.boundingBoxDimension()` or
  `FXPaintFactory.createRadialGradient(..)`); all simulations draw with it.

## UI Factories and Styles

- Use the prefix `FX` (uppercase) for helper classes whose main purpose is to create, style, or paint JavaFX scene
  graph objects; value types and property wrappers carry no prefix.
- `FXComponentFactory` creates styled controls and returns `LabeledControl<R>` when a label/control pair is needed.
- Keep labels connected with `setLabelFor(...)`; share tooltips between labels and controls when the factory pattern
  does so.
- Use and extend the `FXStyleClasses` constants for CSS class names instead of literals.
- Provide or preserve cleanup registration for factory-created listeners and bindings that can outlive the control.
- `FXPaintFactory` is for reusable paint/color calculations; validate numeric ranges before creating colors or maps.

## Input Property Records

- `Input*Property` records wrap JavaFX properties plus constraints, valid choices, or display-name providers.
- Compact constructors validate structural constraints and the initial property value; copy valid-value lists
  defensively.
- The properties created by `of(..)` log invalid later values with `AppLogger.errorf(..)` but still set them, so
  controls stay responsive.
- Numeric wrappers expose `asObjectProperty()`, `asStringBinding(..)`, and `adjustValue(..)`; choice wrappers expose
  `asStringBinding(..)`, `isValue(..)`, and `hasMultipleValidValues()`.

## Grid Geometry and Rendering

- Treat `GridGeometry` and `CellDimension` as the source of truth for cell sizes, canvas positions, polygons, bounds,
  and point-to-cell conversion.
- Do not duplicate triangle, square, or hexagon geometry in painters or component code.
- Drawing methods may accept a `@Nullable Paint` or `Color` only when `null` means "do not draw this layer".
- Preserve bounds checks for direct pixel drawing and positive-size checks for shapes.
- `CellShapeSide` represents UI frame sides only; extend it only when all shape-specific frame methods are updated.

## Timer

- `SimulationTimer.start(..)` rejects intervals that are not finite and positive, and stops the previous timeline
  first; `stop()` stops and clears the timeline, so `isRunning()` reflects the current state.

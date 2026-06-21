---
applyTo: "**/de/mkalb/etpetssim/ui/*.java"
description: "Compact JavaFX UI-package rules for reusable controls, input wrappers, canvas rendering, geometry, and timers."
---

# UI Package Instructions

Rules for `de.mkalb.etpetssim.ui`. Keep this package reusable infrastructure for simulations, not simulation logic.

## Scope

- Allowed dependencies: JavaFX, `de.mkalb.etpetssim.core`, and `de.mkalb.etpetssim.engine`.
- Do not depend on `de.mkalb.etpetssim.simulations`.
- Add code here only when it is reusable by multiple UI or simulation components.

## UI Factories and Styles

- `FXComponentFactory` creates styled controls and returns `LabeledControl<R>` when a label/control pair is needed.
- Keep labels connected with `setLabelFor(...)`; share tooltips between labels and controls when the factory pattern
  does so.
- Use `FXStyleClasses` constants for CSS class names; add new constants there instead of scattering literal style names.
- For factory-created listeners, bindings, or bidirectional bindings that can outlive the control, provide or preserve
  cleanup registration.
- `FXPaintFactory` is for reusable paint/color calculations; validate numeric ranges before creating colors or maps.

## Input Property Records

- `Input*Property` records wrap JavaFX properties plus constraints, valid choices, or display-name providers.
- Compact constructors validate structural constraints and the initial property value; copy valid-value lists
  defensively.
- Factory-created setters log invalid later values via `AppLogger.error(...)` but still set them so controls stay
  responsive.
- Numeric wrappers expose `asObjectProperty()`, `asStringBinding(...)`, and `adjustValue(...)` for control integration.
- Choice wrappers expose `asStringBinding(...)`, `isValue(...)`, and `hasMultipleValidValues()`.
- Keep validation helpers package-private when they support tests or shared non-trivial checks.

## Grid Geometry and Rendering

- Treat `GridGeometry` and `CellDimension` as the source of truth for cell sizes, canvas positions, polygons, bounds,
  and point-to-cell conversion.
- Do not duplicate triangle, square, or hexagon geometry in painters or component code.
- `FXGridCanvasPainter` is stateful canvas rendering around one `Canvas`, `GridStructure`, and computed dimensions.
- Drawing methods may accept `@Nullable Paint` or `@Nullable Color` only when null explicitly means "do not draw this
  layer".
- Preserve bounds checks for direct pixel drawing and positive-size checks for shapes.
- `CellShapeSide` represents UI frame sides only; extend it only when all shape-specific frame methods are updated.

## Timer

- `SimulationTimer` owns a JavaFX `Timeline`; `start(...)` validates a finite positive interval and stops the previous
  timeline first.
- `stop()` must stop and clear the timeline so `isRunning()` reflects current state.

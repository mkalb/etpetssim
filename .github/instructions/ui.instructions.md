---
applyTo: "**/de/mkalb/etpetssim/ui/*.java"
description: "JavaFX UI helper classes for de.mkalb.etpetssim.ui package. Use when creating or modifying FX* factory/painter classes or Input* property wrapper classes."
---

# UI Package Instructions

Rules for classes in `de.mkalb.etpetssim.ui` only.

## Package Scope and Dependencies

This package contains reusable JavaFX UI helper classes invoked primarily from the `simulations` package.

- Classes may depend on `de.mkalb.etpetssim.core` and `de.mkalb.etpetssim.engine`.
- Classes must NOT depend on `de.mkalb.etpetssim.simulations`.

## FX* Classes (JavaFX Factories and Painters)

FX-prefixed classes provide JavaFX component factories, paint utilities, and rendering helpers.

- `FXComponentFactory`: creates and configures JavaFX UI controls and containers
- `FXPaintFactory`: creates JavaFX `Paint` objects (gradients, colors, variants)
- `FXStyleClasses`: CSS style class name constants
- `FXGridCanvasPainter`: stateful painter for drawing grids, cells, and shapes on JavaFX `Canvas`

Utility classes with only static methods use `final` class and `private` constructor.
Stateful classes like `FXGridCanvasPainter` use instance methods and standard constructors.

Use `@Nullable Paint` parameters for optional fill and stroke colors in drawing methods.
`@SuppressWarnings("MagicNumber")` is acceptable for UI layout and rendering numeric constants.

Use `GridGeometry` for geometric calculations (cell dimensions, coordinates, polygons).
Do not duplicate geometry logic in other classes.

## Input* Classes (Property Wrappers)

Input-prefixed records wrap JavaFX properties with validation and range constraints.

### Validation Behavior

Input* records set invalid values even when out-of-range, but log an error via `AppLogger.error(...)`.
This allows UI controls to remain responsive while signaling constraint violations.
Preserve this behavior in all Input* classes.

Validate constraints in the compact constructor before storing values.

### Common API

All Input* records provide:

- `of(...)` static factory methods for creating instances
- `getValue()` and `setValue(...)` for value access
- `isValid()` to check current value against constraints
- `property()` or direct property accessor for binding

Additional API methods (e.g., `asStringBinding(...)`, `isMin()`, `isMax()`) are class-specific.

### Helper Methods

Consider adding package-private static helper methods like `isValidValue(...)` and `adjustValue(...)` when validation or
clamping logic is non-trivial or reused.

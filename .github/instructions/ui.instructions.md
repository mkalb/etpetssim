---
applyTo: "**/de/mkalb/etpetssim/ui/*.java"
description: "JavaFX UI helper classes for de.mkalb.etpetssim.ui package. Use when creating or modifying FX* factory/painter classes, Input* property wrapper records, geometry helpers, or SimulationTimer."
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

## Geometry and Timer Helpers

Geometry and timing helpers are reusable UI infrastructure, not simulation-specific domain logic.

- `GridGeometry`, `CellDimension`, and `CellShapeSide` contain shared grid geometry and cell-shape calculations.
- `SimulationTimer` encapsulates JavaFX `Timeline` usage for periodic simulation steps.
- Keep these helpers independent from `de.mkalb.etpetssim.simulations` packages.
- Prefer adding reusable UI/math behavior here only when more than one simulation or UI component needs it.

## Input* Classes (Property Wrappers)

Input-prefixed records wrap JavaFX properties with validation, range constraints, or constrained choices.

### Validation Behavior

Compact constructors validate structural constraints and the initial property value before storing record components.
Throw `IllegalArgumentException` for invalid ranges, empty or duplicate choice lists, or invalid initial values.

Factory methods create JavaFX properties whose setters log invalid later values via `AppLogger.error(...)`, but still
set
the value. This allows UI controls to remain responsive while signaling constraint violations.
Preserve this post-construction setter behavior in all Input* classes.

### Common API

All Input* records provide the relevant subset of:

- static factory methods for creating instances, e.g. `of(...)`, `ofList(...)`, or `ofEnum(...)`
- `getValue()` and `setValue(...)` for value access
- `property()` or direct property accessor for binding

Additional API methods are class-specific:

- Numeric Input* records expose `asObjectProperty()`, `asStringBinding(...)`, and `adjustValue(...)`.
- Choice-based Input* records expose `asStringBinding(...)`, `isValue(...)`, and `hasMultipleValidValues()`.

### Helper Methods

Use package-private static validation helpers like `isInvalidValue(...)` when validation logic is non-trivial or reused.
Use instance methods like `adjustValue(...)` for clamping or snapping values with the record's configured constraints.

---
applyTo: "**/de/mkalb/etpetssim/ui/*.java"
description: "JavaFX UI helper rules for de.mkalb.etpetssim.ui."
---

# UI Package Instructions

Rules for JavaFX helper classes in `de.mkalb.etpetssim.ui` only.

## Package Scope and Dependencies

This package contains reusable JavaFX UI helpers invoked primarily from the `simulations` package.

- Classes may depend on `de.mkalb.etpetssim.core` and `de.mkalb.etpetssim.engine`.
- Classes must NOT depend on `de.mkalb.etpetssim.simulations`.

## FX* Classes (JavaFX Factories and Painters)

FX-prefixed classes provide JavaFX component factories, paint utilities, and rendering helpers.

- Static factory/helper classes use `final` class and private constructor.
- Stateful painter classes such as `FXGridCanvasPainter` use instance methods and standard constructors.
- Use `@Nullable Paint` parameters for optional fill and stroke colors in drawing methods.
- `@SuppressWarnings("MagicNumber")` is acceptable for UI layout and rendering numeric constants.
- Use `GridGeometry` for cell dimensions, coordinates, and polygons; do not duplicate geometry logic.

## Geometry and Timer Helpers

Geometry and timing helpers are reusable UI infrastructure, not simulation-specific domain logic.

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

Input* records provide the relevant subset of:

- static factory methods, e.g. `of(...)`, `ofList(...)`, or `ofEnum(...)`
- `getValue()` and `setValue(...)` for value access
- `property()` or direct property accessor for binding

Additional API methods are class-specific:

- Numeric Input* records expose `asObjectProperty()`, `asStringBinding(...)`, and `adjustValue(...)`.
- Choice-based Input* records expose `asStringBinding(...)`, `isValue(...)`, and `hasMultipleValidValues()`.

### Helper Methods

Use package-private static validation helpers like `isInvalidValue(...)` when validation logic is non-trivial or reused.
Use instance methods like `adjustValue(...)` for clamping or snapping values with the record's configured constraints.

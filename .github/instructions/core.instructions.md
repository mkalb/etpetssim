---
description: "Core infrastructure rules for etpetssim. Use when creating or modifying foundational App-prefixed utilities in de.mkalb.etpetssim.core that serve as cross-cutting services for all other packages."
applyTo: "**/de/mkalb/etpetssim/core/*.java"
---

# Core Package Instructions

Foundation infrastructure for `de.mkalb.etpetssim.core` package only (no subpackages).

## Class Structure Requirements

- All classes must be `public final class`.
- Class names must start with `App` prefix (e.g., `AppLogger`, `AppStorage`, `AppLocalization`).
- Purpose: General-purpose utility classes usable across all other packages.
- May define internal `public enum` and `public record` types.

## Zero-Dependency Rule

Core package must remain dependency-free from all application packages.

**Prohibited imports:**

- `de.mkalb.etpetssim.simulations.*`
- `de.mkalb.etpetssim.engine.*`
- `de.mkalb.etpetssim.ui.*`
- Main application classes (e.g., `ExtraterrestrialPetsSimulation`)

**Allowed dependencies:**

- Java Platform (JDK standard library)
- JSpecify annotations (`@Nullable`, `@NullMarked`)
- JavaFX only in exceptional cases (e.g., `javafx.scene.image.Image` in `AppResources`)

Core provides infrastructure; it does not depend on domain logic or UI.

## Objects.requireNonNull Privilege

Core package is the **only package allowed** to use `Objects.requireNonNull()` for parameter validation.

All other packages (`engine`, `ui`, `simulations`) rely on JSpecify non-null-by-default contracts without explicit
runtime checks.

**Reason:** Core classes are critical infrastructure; fail-fast validation prevents cascading failures.

## Documentation Requirements

- All public classes require class-level Javadoc describing purpose and responsibilities.
- All public methods require Javadoc covering intent, parameters, return values, and exceptions.

## Testing Requirements

- All core classes must have comprehensive JUnit 5 test coverage.
- Methods with `*ForTesting()` naming pattern are allowed for test infrastructure needs (e.g., `resetForTesting()`,
  `initializeForTesting()`).
- Test-specific methods must be documented in Javadoc with their testing purpose.

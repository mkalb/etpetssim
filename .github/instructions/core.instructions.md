---
applyTo: "**/de/mkalb/etpetssim/core/*.java"
description: "Core infrastructure rules for App-prefixed utilities in de.mkalb.etpetssim.core."
---

# Core Package Instructions

Foundation infrastructure rules for `de.mkalb.etpetssim.core` package only (no subpackages).

## Class Structure Requirements

- Concrete utility classes must be `public final class`.
- Class names must start with `App` prefix (e.g., `AppLogger`, `AppStorage`, `AppLocalization`).
- Purpose: cross-cutting utilities usable by all other packages.
- May define internal `public enum` and `public record` types.

## Dependency Boundaries

Core must not depend on application packages or domain/UI logic.

**Prohibited imports:**

- `de.mkalb.etpetssim.simulations.*`
- `de.mkalb.etpetssim.engine.*`
- `de.mkalb.etpetssim.ui.*`
- Main application classes (e.g., `ExtraterrestrialPetsSimulation`)

**Allowed dependencies:**

- Java Platform (JDK standard library)
- JSpecify annotations (`@Nullable`, `@NullMarked`)
- JavaFX only for infrastructure-level resource integration (e.g., `javafx.scene.image.Image` in `AppResources`)

## Objects.requireNonNull Privilege

Core overrides the general Java instruction for `Objects.requireNonNull(...)`.

- Core constructors and methods may validate required parameters with `Objects.requireNonNull(...)`.
- Prefer fail-fast validation for critical infrastructure entry points.
- Other packages (`engine`, `ui`, `simulations`) rely on JSpecify non-null-by-default contracts by default.
- If fail-fast null validation is part of a public core contract, Javadoc may document the resulting
  `NullPointerException`.

## Documentation Requirements

- All public classes require class-level Javadoc describing purpose and responsibilities.
- Public methods require Javadoc covering intent, parameters, return values, and contract-relevant exceptions.

## Testing Requirements

- All core classes must have comprehensive JUnit 5 test coverage.
- Methods with `*ForTesting()` naming pattern are allowed for test infrastructure needs (e.g., `resetForTesting()`,
  `initializeForTesting()`).
- Test-specific methods must be documented in Javadoc with their testing purpose.

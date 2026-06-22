---
applyTo: "**/src/main/java/de/mkalb/etpetssim/core/*.java"
description: "Rules for App-prefixed core infrastructure utilities in de.mkalb.etpetssim.core."
---

# Core Package Instructions

Rules for production code in `de.mkalb.etpetssim.core`.

## Class Structure

- Concrete utility classes must be `public final class` with an `App` prefix (e.g., `AppLogger`, `AppStorage`).
- Core classes provide cross-cutting infrastructure usable by all other packages.
- Internal `public enum` and `public record` types are allowed.

## Dependency Boundaries

Core must not depend on application, domain, simulation, or UI logic.

Prohibited imports:

- `de.mkalb.etpetssim.simulations.*`
- `de.mkalb.etpetssim.engine.*`
- `de.mkalb.etpetssim.ui.*`
- Main application classes (e.g., `ExtraterrestrialPetsSimulation`)

Allowed dependencies:

- JDK standard library
- JSpecify annotations
- JavaFX only for infrastructure-level resource integration (e.g., `Image` in `AppResources`)

## Null Validation

Core overrides the general Java rule for `Objects.requireNonNull(...)`.

- Core constructors and methods may validate required parameters with `Objects.requireNonNull(...)`.
- Prefer fail-fast validation for critical infrastructure entry points.
- Other packages rely on JSpecify non-null-by-default contracts unless a specific instruction says otherwise.
- Document `NullPointerException` in Javadoc only when it is part of a public core contract.

## Documentation and Tests

- Public classes require class-level Javadoc describing purpose and responsibilities.
- Public methods require Javadoc covering intent, parameters, return values, and contract-relevant exceptions.
- Core classes require comprehensive JUnit 5 coverage.
- `*ForTesting()` methods are allowed for test infrastructure and must document their testing purpose.

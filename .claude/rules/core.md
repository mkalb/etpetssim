---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/core/*.java"
---

# Core Package Instructions

Rules for production code in `de.mkalb.etpetssim.core`.

## Class Structure

- Concrete utility classes must be `public final class` with an `App` prefix (e.g., `AppLogger`, `AppStorage`).
- Core classes provide cross-cutting infrastructure usable by all other packages.
- Internal `public enum` and `public record` types are allowed.
- Keep the complete public API of a core class, also methods without a current caller (e.g., `AppArgs.getInt(..)`);
  it is infrastructure for all packages.

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

## Documentation and Tests

Core overrides the general Java rule that constructors and methods have Javadoc only when it adds value.

- The type Javadoc of a public class describes its purpose and responsibilities.
- Public constructors and methods require Javadoc covering intent, parameters, return values, and contract-relevant
  exceptions; pure `@Override` methods follow `java.md`.
- Core classes require comprehensive JUnit 5 coverage.
- `*ForTesting()` methods are allowed for test infrastructure and must document their testing purpose. Make them
  package-private unless tests in other packages need them (e.g., `AppLogger.initializeForTesting()`).

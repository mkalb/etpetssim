---
paths:
  - "**/src/main/java/de/mkalb/etpetssim/core/*.java"
---

# Core Package Instructions

Rules for production code in `de.mkalb.etpetssim.core`, the cross-cutting infrastructure of all other packages.

## Class Structure

- Top-level core classes are `public final` and start with `App` (e.g., `AppLogger`, `AppArgs`); nested public enums
  and records are allowed.
- Keep the complete public API of a core class, also methods without a current caller (e.g., `AppArgs.getInt(..)`).
- Depend only on the JDK, JSpecify, and JavaFX for resource integration (e.g., `Image` in `AppResources`), not on
  other packages, including the application classes of the root package.

## Documentation and Tests

- The type Javadoc of a public class describes its purpose and responsibilities.
- Unlike the "Coverage" rule of `java.md`, public constructors and methods require Javadoc that states their intent;
  pure `@Override` methods follow `java.md`.
- Core classes require comprehensive tests.
- `*ForTesting()` methods are allowed for test infrastructure and must document their testing purpose. Make them
  package-private unless tests in other packages need them (e.g., `AppLogger.initializeForTesting()`).

---
applyTo: "**/*.java"
description: "Java-specific coding rules for etpetssim. Use when writing Java code, defining records, using enums, working with JSpecify nullability, or using AppLogger."
---

# Java Coding Instructions

Java-specific coding rules for all `.java` files in this repository.
These rules complement the project-wide instructions.

## Code Style

- Do not add comments that only restate obvious code.
- Do not spend effort on formatting-only changes, import sorting, or import cleanup.
- Do not change existing imports without a concrete reason; keep existing wildcard imports unless a specific change
  requires otherwise.
- Prefer modern Java 25 APIs and language features when they improve clarity; avoid novelty for its own sake.
- Use locale-stable normalization for technical text (e.g., `toLowerCase(Locale.ROOT)`).

## Naming Conventions

Use these method naming patterns when they match the behavior:

- `create...`: instantiate and configure one object/control/view
- `build...`: assemble multiple UI parts into one region/container
- `of...`: validating static factory
- `with...`: static factory-style configured variant
- `compute...`: deterministic calculation from input/state
- `to...`/`from...`: deterministic value/coordinate conversion
- `as...`: conversion or alternative view accessor
- `is...`: boolean state/validity/mode query
- `has...`: presence/availability query
- `get...`/`set...`: standard mutable property accessors
- `...Property`: JavaFX property accessor naming
- `initialize...`: lifecycle initialization
- `reset...`: reset state to initial/clean condition
- `shutdown...`: cleanup and resource release
- `request...`: user-intent action trigger/flag
- Compact names: Use concise noun-style names (`locale()`, `area()`, `opposite()`) on utility/value APIs.
- Keep generated Java record accessor names (`x()`, `y()`) unless a custom method adds distinct behavior.

## Java Records

- Use records for small immutable value carriers; move derived/convenience behavior to small methods.
- Use canonical/compact constructors to enforce invariants and to defensively copy mutable inputs (collections, maps,
  arrays) before storing.
- Do not use records for mutable JavaFX state, properties, or classes with identity/lifecycle semantics.

## Enums

- Use enums only for fixed, closed domain sets.
- Place enum behavior/metadata on the enum type itself.
- Use exhaustive `switch` expressions for enum branching; do not add a `default` branch when all constants are known.
- Use explicit stable codes/keys for external representations; never persist or exchange `ordinal()`.
- Document declaration-order semantics in type Javadoc when order matters.

## Nullability (JSpecify)

All packages use `@NullMarked` in `package-info.java` to establish non-null-by-default.

- Treat unannotated types as non-null under `@NullMarked`.
- Use `@org.jspecify.annotations.Nullable` only for intentional nullable contracts.
- Do not add `@Nullable` defensively or "just in case".
- Outside package-specific exceptions, do not add routine `Objects.requireNonNull(...)` guards at the start of methods
  solely for non-null-by-default parameters.
- Use `Objects.requireNonNull(...)` only for established boundary checks, state assertions after nullable fields are
  checked, or where a more specific instruction explicitly allows it.

Valid `@Nullable` locations include fields, method parameters, return types, and type arguments such as
`ObjectProperty<@Nullable GC>`.

## Javadoc

- Document public types at API/module boundaries and public types with non-obvious responsibilities.
- Document public methods at API boundaries and non-obvious behavior, covering intent, inputs, outputs, and significant
  side effects.
- Describe behavior; avoid implementation detail unless it is contract-relevant.
- Do not add Javadoc to pure `@Override` methods when inherited docs already fully define the contract; add override
  Javadoc when the override adds constraints, side effects, or surprising semantics.
- Do not add routine `NullPointerException` notes for non-null-by-default contracts.

## Logging with AppLogger

Use `de.mkalb.etpetssim.core.AppLogger` for all application logging.

- Use `Supplier<String>` overloads (`debug(() -> ...)`, `info(() -> ...)`) when message construction is non-trivial to
  avoid unnecessary string building when the level is disabled.
- Use `*f` formatting overloads (`debugf`, `infof`, `warnf`, `errorf`) for parameterized messages instead of manual
  `String.format(...)` at the call site.
- Prefix log messages with a short, stable component tag when helpful (e.g., `"SimulationTimer: ..."`) to aid log
  filtering.
- Do not log inside tight rendering or simulation step loops at `info` or higher.
- Log exceptions via `AppLogger.error(message, throwable)`; do not concatenate stack traces into the message manually.

## Suppressions Policy

- Keep each `@SuppressWarnings` scope as narrow as possible (prefer method/field over class).
- Do not add explanatory comments to `@SuppressWarnings`.
- Do not add or remove `@SuppressWarnings` automatically; add them only after human review and trade-off evaluation.
- Prefer improving inspections/rules over adding suppressions when practical.

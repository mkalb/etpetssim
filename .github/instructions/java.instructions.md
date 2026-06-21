---
applyTo: "**/*.java"
description: "Java-specific coding rules for etpetssim. Use when writing Java code, defining records, using enums, working with JSpecify nullability, or using AppLogger."
---

# Java Coding Instructions

Java-specific rules for all `.java` files. These complement repository-wide instructions.

## Code Style

- Do not add comments that only restate obvious code.
- Avoid formatting-only changes, import sorting, or import cleanup unless required by the task.
- Keep existing wildcard imports unless a concrete change requires otherwise.
- Prefer modern Java 25 APIs and language features when they improve clarity; avoid novelty.
- Use locale-stable normalization for technical text (e.g., `toLowerCase(Locale.ROOT)`).

## Naming Conventions

Use these method naming patterns when they match the behavior:

- `create...`: instantiate/configure one object/control/view
- `build...`: assemble multiple UI parts into one region/container
- `of...`/`with...`: validating/configured static factory
- `compute...`: deterministic calculation from input/state
- `to...`/`from...`/`as...`: conversion or alternative view accessor
- `is...`/`has...`: boolean state, validity, mode, presence, or availability query
- `get...`/`set...`/`...Property`: JavaFX or mutable property accessors
- `initialize...`/`reset...`/`shutdown...`: lifecycle setup, reset, and cleanup
- `request...`: user-intent action trigger/flag
- Use concise noun-style names (`locale()`, `area()`, `opposite()`) on utility/value APIs.
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
- Outside package-specific exceptions, do not add routine `Objects.requireNonNull(...)` guards solely for
  non-null-by-default parameters.
- Use `Objects.requireNonNull(...)` only for established boundary checks, state assertions after nullable checks, or
  where a more specific instruction allows it.
- Valid `@Nullable` locations include fields, parameters, return types, and type arguments
  (`ObjectProperty<@Nullable GC>`).

## Javadoc

- Document public types at API/module boundaries and public types with non-obvious responsibilities.
- Document public methods at API boundaries and non-obvious behavior; cover intent, inputs, outputs, and significant
  side
  effects.
- Describe behavior; avoid implementation detail unless it is contract-relevant.
- Do not add Javadoc to pure `@Override` methods unless the override adds constraints, side effects, or surprising
  semantics.
- Do not add routine `NullPointerException` notes for non-null-by-default contracts.

## Logging with AppLogger

Use `de.mkalb.etpetssim.core.AppLogger` for all application logging.

- Use `Supplier<String>` overloads (`debug(() -> ...)`, `info(() -> ...)`) when message construction is non-trivial to
  avoid unnecessary string building.
- Use `*f` formatting overloads (`debugf`, `infof`, `warnf`, `errorf`) for parameterized messages instead of manual
  `String.format(...)`.
- Prefix log messages with a short, stable component tag when helpful (e.g., `"SimulationTimer: ..."`).
- Do not log inside tight rendering or simulation step loops at `info` or higher.
- Log exceptions via `AppLogger.error(message, throwable)`; do not concatenate stack traces into the message manually.

## Suppressions Policy

- Keep each `@SuppressWarnings` scope as narrow as possible (prefer method/field over class).
- Do not add explanatory comments to `@SuppressWarnings`.
- Do not add or remove `@SuppressWarnings` automatically; require human review and trade-off evaluation.
- Prefer improving inspections/rules over adding suppressions when practical.

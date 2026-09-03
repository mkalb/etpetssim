---
applyTo: "**/*.java"
description: "Java rules for etpetssim: code style, naming, records, enums, JSpecify nullability, and AppLogger usage."
---

# Java Coding Instructions

Rules for all `.java` files; repository-wide instructions still apply.

## Code Style

- Do not add comments that only restate obvious code.
- Avoid formatting-only changes, import sorting, or import cleanup unless required by the task.
- Keep existing wildcard imports unless a concrete change requires otherwise.
- Prefer modern Java 26 APIs and language features when they improve clarity; avoid novelty.
- Use locale-stable normalization for technical text (e.g., `toLowerCase(Locale.ROOT)`).

## Naming Conventions

Use these method naming patterns when they match the method's primary behavior; test method names follow
`junit.instructions.md`.

- `create...`: instantiate and configure one coherent object, control, view, or value, including composed containers
- `build...`: assemble a top-level simulation view region; reserved for the `build*Region()` methods
- `of...`: create an instance through a validating or configured static factory
- `from...`: create, parse, or derive a result from the source named in the method
- `to...`: convert a value to a target type or representation
- `format...`: produce display text from values, usually localized
- `as...`: expose existing data through an alternative type, view, property, or binding
- `compute...`: calculate or derive a deterministic result from inputs or current state
- `resolve...`: select which concrete result applies from selection state, configuration, context, or a fallback
- `is...`/`has...`/`can...`/`should...`/`contains...`/`are...`/`includes...`: query boolean state, validity, mode,
  presence, availability, capability, or policy
- `get...`: retrieve or obtain state, data, or resources, including parameterized lookups and JavaBean-style accessors
- `set...`: assign or replace mutable state, property values, model or UI content, or callbacks
- `update...`: refresh existing state, statistics, or UI content to match current data
- `apply...`: enact a rule, transformation, or action on input or mutable state
- `perform...`: carry out one simulation, agent, or strategy step inside a step runner or step logic
- `execute...`: run or orchestrate an operation, action, or sequence
- `draw...`: render visual output
- `initialize...`/`reset...`/`shutdown...`: perform lifecycle setup, state reset, or cleanup
- `clear...`: remove existing content and leave the target empty
- `request...`: signal a user-intent action or set a corresponding flag
- `increment...`/`decrement...`: change a mutable counter by exactly `1` or `-1`, respectively
- `adjust...`: adapt a value, property, or layout using a signed delta, factor, constraint, or contextual target
- `...Property`: JavaFX property accessor; this suffix takes precedence over any prefix rule
- `...And...`/`...Or...`: name a deliberately coordinated operation whose additional effect must stay visible to callers
- `toDisplayString()`: short human-readable representation of a value or entity; keep it separate from `toString()`,
  which stays a technical diagnostic representation
- Use concise noun-style names (`locale()`, `area()`, `opposite()`) for immutable value access and derived values.
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
- Enums offered as a UI selection follow the existing `labelResourceKey()`/`resourceKey()` pair for their
  localization keys.
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

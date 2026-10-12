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
- Prefer modern Java 27 APIs and language features when they improve clarity; avoid novelty.
- Use locale-stable normalization for technical text (e.g., `toLowerCase(Locale.ROOT)`).

## Naming Conventions

Use these method naming patterns when they match the method's primary behavior; test method names follow
`junit.instructions.md`.

- `create...`: build and configure a new object, control, view, value, or resource (e.g., a file), including composed
  containers; a static factory on the created type uses `create...` when it presets state that is not given by its
  parameters (`createWithMinStepDuration`)
- `build...`: assemble a top-level simulation view region; reserved for the `build*Region()` methods
- `of...`: build and validate an instance from component values in a static factory on the created type or its
  companion utility class
- `from...`: create or derive a result from the non-text source named in the method or given as parameter (e.g.,
  `EtpetsCell.from(model, coordinate)`); `from<Code>(String)` looks up a constant by its external code and returns
  `Optional<T>`
- `to...`: convert a value to a target type or representation, typically as a new independent value or copy
- `toDisplayString()`: short human-readable representation of the receiver (a value or entity); keep it separate from
  `toString()`, which stays a technical diagnostic representation
- `parse...`: convert text with a format or grammar to a value, as in `Integer.parseInt`; throw on invalid input or
  return a supplied default
- `format...`: produce display or log text from values, usually localized
- `as...`/`...As<Type>`: expose existing data through an alternative type, view, property, or binding; use `to...` for
  snapshots of mutable data and serialized forms
- `compute...`: calculate a result that is fully determined by a formula or geometry from the inputs, when the
  calculation is the primary intent and no more specific prefix (e.g., `clamp...`, `adjust...`, `to...`) or
  noun-style query applies
- `resolve...`: choose the applicable result among alternatives by selection state, configuration, precedence, or
  fallback rules
- `find...`: search candidates, or look up a value that has a `require...` counterpart; return `Optional<T>` when
  absence is expected
- `require...`: return a required value or throw if it is missing; may have a `find...` counterpart
- `get...`: retrieve a value, resource, or property by a supplied key or path (return `Optional<T>` when it may be
  missing), or expose it through a JavaBean-style accessor
- `check...`: verify an argument or state and throw on violation, as in `Objects.checkIndex`
- Boolean queries: `is...` for state, type, or validity; `has...` for an owned component or property, including a valid
  or reached part (`hasValidRanges`, `hasReachedAgingEffectsAge`); `contains...` for membership of an element or key in
  an owned collection; `includes...` for a mode or configuration that covers a category; `can...` for capability;
  `should...` for a decision or policy; `are...` for a plural subject (`areDirectRelatives(a, b)`).
- Action methods may return a boolean success flag; boolean prefixes are reserved for queries.
- `set...`: assign supplied or fixed values (e.g., a default or placeholder) to mutable state, properties, model or UI
  content, or a single callback slot
- `update...`: recompute or refresh state, statistics, or UI content from current data or a supplied source object
  (e.g., a model, config, or coordinate)
- `record...`: append a value or event to a history, sample, or tracking state
- `register...`: attach a listener, handler, cleanup action, or provider to an owner or registry
- `bind...`/`unbind...`: create or release JavaFX bindings
- `clear...`: remove existing content and leave the target empty
- `fill...`: set every cell of a grid or paint a whole area
- `place...`: put entities at selected coordinates of a grid
- `increment...`/`decrement...`: change a mutable counter by exactly `1` or `-1`, respectively
- `adjust...`: change mutable state by a signed delta, or return an adapted value (scaled by a factor, fitted to the
  valid range and step of its owner, or with one component replaced)
- `clamp...`: restrict a value to a named range or supplied bounds
- `apply...`: enact a rule, transformation, or action on input or mutable state
- `perform...`: carry out one simulation or agent step in an implementation of a step contract (step runner or step
  logic)
- `execute...`: run or orchestrate simulation steps or another operation sequence
- `draw...`: render visual output
- `initialize...`/`reset...`/`shutdown...`: perform lifecycle setup, state reset, or cleanup
- `request...`: signal a user-intent action or set a corresponding flag
- `handle...`: react to a UI, listener, or lifecycle event; invoked from a listener or callback, not as a
  general-purpose API
- `...Property`: accessor returning a JavaFX property or a repository property wrapper (`Input*Property`,
  `SeedProperty`, `*RuleProperty`); this suffix takes precedence over any prefix rule, except for `as...Property()`
  adapters, which follow `as...`
- `...And...`: name a deliberately coordinated operation with two effects that must stay visible to callers
- `...Or...`: name an alternative path when the primary result is missing, as in `getOrCreate...`; `...OrFallback`
  returns a defined fallback value
- Boolean predicates and combinators (`and`, `or`, `andThen`) are not affected by `...And...` and `...Or...`.
- Use concise noun-style names (`locale()`, `area()`, `opposite()`) for stored properties, intrinsic derived values, and
  named catalog values. They are especially appropriate for records, enums, immutable value objects, data-focused types,
  and components exposing owned state. Noun-style names may take qualifying parameters for pure queries whose name
  describes the result (e.g., `turnForState(int)`, `boundingBoxAt(Point2D)`).
- Keep JavaBean-style `getX()` for getters paired with a JavaFX `xProperty()`, for ViewModel accessors (see
  `simulations.instructions.md`), and for existing JavaBean-style field accessors (e.g., `EditToolBarView.getNode()`,
  `DrawCallThrottler.getAverageDurationMillis()`).
- Keep generated Java record accessor names (`x()`, `y()`) unless a custom method adds distinct behavior.
- Write acronyms in method and variable names in camel case (`Id`, `Url`, `Uri`, `Css`); the uppercase `FX` prefix
  applies only to class names.
- Private and package-private helper methods may use other clear verbs (e.g., `log...`, `configure...`,
  `normalize...`, `pick...`) when no pattern above fits.

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
- Enums offered as a config selection follow the existing `labelResourceKey()`/`resourceKey()` pair for their
  localization keys; edit toolbar choices and records expose the key of their own display text as `labelKey()`.
- Document declaration-order semantics in type Javadoc when order matters.

## Nullability (JSpecify)

The module declaration `module-info.java` uses `@NullMarked` to establish non-null-by-default for all packages. Do not
add `package-info.java` files. IntelliJ does not apply the module-level `@NullMarked` to test sources, which are
patched into the module only by Gradle; test code is therefore unspecified, while calls into main code are still
checked. Do not use `@Nullable` in test sources. When a method declared in test sources overrides a method with a
non-null reference return type (from main code or the JDK, e.g. `toString()`), annotate it with
`@org.jspecify.annotations.NonNull` on its own line before `@Override`.

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

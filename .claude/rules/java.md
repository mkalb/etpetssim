---
paths:
  - "**/*.java"
---

# Java Coding Instructions

Rules for all `.java` files; repository-wide instructions still apply.

## Code Style

- Do not add comments that only restate obvious code.
- Format code as `.editorconfig` and the IntelliJ defaults specify (e.g., 4 spaces, a continuation indent of 8, at
  most 120 characters per line, and chained method calls without alignment). End each file with exactly one newline,
  and put one blank line before the closing brace of a non-empty type body; the formatter does not add or remove it.
  Put `extends` and `implements` on the line of the type declaration when the line fits within 120 characters;
  otherwise break before them with continuation indent. A record whose components are wrapped puts `implements` on
  its own line after the closing parenthesis (e.g., the `*Config` records).
- Use single-type imports, also for static members; do not use on-demand imports (`.*`). Import a nested type with
  a regular import, not with `import static`.
- Group imports as IntelliJ does, with one blank line between the groups: all other packages, `javax.*`, `java.*`,
  then static imports. Sort each group by name with uppercase before lowercase letters (`engine.GridCoordinate`
  before `engine.model.GridModel`). Add new imports at their sorted position and remove imports that a change
  leaves unused; leave the other imports unchanged.
- Prefer modern Java 27 APIs and language features when they improve clarity; avoid novelty.
- `var` is allowed for local variables when the type is evident from the right-hand side (e.g., a constructor,
  factory, or cast); write the type otherwise. Do not change existing declarations between `var` and an explicit
  type, and do not report them in reviews.
- Keep stream operations free of side effects, collect with `toList()` instead of `Collectors.toList()`, and use a
  loop when it is clearer than a stream. Extract lambdas with several statements into named methods.
- Use locale-stable normalization for technical text (e.g., `toLowerCase(Locale.ROOT)`).
- Reference localization keys through constants instead of string literals at the call site: app-wide keys in
  `core.AppLocalizationKeys`, keys shared by the simulations in `simulations.core.view.SimulationLocalizationKeys`,
  simulation-specific keys in the class that uses them; `i18n.md` describes the key schema and the constant names.
  The key methods of enums (`labelResourceKey()`, `resourceKey()`) may return string literals.
- Name domain numbers as constants (e.g., in `<Simulation>Constraints` or `<Simulation>Balance`); literals in
  formulas, geometry, and layout code may stay inline.
- Do not initialize fields explicitly to their default value (`0`, `false`, `null`); an explicit initializer is for a
  different value, such as the sentinel `Long.MAX_VALUE` of a minimum.
- Order the members of classes, enums, and records as the IntelliJ default arrangement does: static fields
  (`static final` first, each group from public to private), static initializers, instance fields (`final` first,
  each group from public to private), instance initializers, constructors, static methods, instance methods, then
  nested types. Within the static and within the instance methods, order public, protected, package-private, then
  private methods; overrides (e.g., lifecycle methods) count as public, `toString()` directly precedes the private
  instance methods, and overloads as well as a getter and its setter stay together. Test classes order their methods
  as `junit.md` describes.
- Order interface members as constants, static methods (including private static helpers), abstract methods, then
  default methods.

## Design Principles

- Prefer composition (fields, strategies, records) over inheritance. Use inheritance only for a shared skeleton or
  shared state of a closed type family, such as the `Abstract*` and `Base*` classes in `simulations.core` and the
  entity base classes (e.g., `CreatureBase`).
- Fail fast: validate arguments and invariants where a value enters a constructor or factory, and throw there instead
  of storing or correcting an invalid value (see "Exceptions"; null checks follow "Nullability"); `*Config` records
  are checked with `isValid()` instead (`simulations.md`).
- Prefer immutability: make fields `final` where possible, model values as records, and expose collections as
  unmodifiable copies or views. Use mutable state only for types that own changing state, such as agents, grid
  models, and ViewModels.

## Naming Conventions

Use these method naming patterns when they match the method's primary behavior; test method names follow
`junit.md`.

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
  `toString()`, which stays a technical diagnostic representation. It is locale-independent: format numbers with
  `Locale.ROOT`; localized UI text uses `AppLocalization` or `NumberFormat` instead. Show enum values with `name()`
  unless the enum provides its own display text (e.g., `CompassDirection.arrow()` or an entity's
  `toDisplayString()`)
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
  named catalog values, especially of records, enums, value types, and components exposing owned state. They may take
  qualifying parameters for pure queries whose name describes the result (e.g., `turnForState(int)`,
  `boundingBoxAt(Point2D)`).
- Keep JavaBean-style `getX()` for getters paired with a JavaFX `xProperty()`, for ViewModel accessors (see
  `simulations.md`), and for existing JavaBean-style field accessors (e.g., `EditToolBarView.getNode()`,
  `DrawCallThrottler.getAverageDurationMillis()`).
- Keep generated Java record accessor names (`x()`, `y()`) unless a custom method adds distinct behavior.
- Write acronyms in method and variable names in camel case (`Id`, `Url`, `Uri`, `Css`); the uppercase `FX` prefix
  applies only to class names.
- Private and package-private helper methods may use other clear verbs (e.g., `log...`, `configure...`,
  `normalize...`, `pick...`) when no pattern above fits.

## Java Records

- Use records for small immutable value carriers and holders; move derived/convenience behavior to small methods.
- Use canonical/compact constructors to enforce invariants and to defensively copy mutable inputs (collections, maps,
  arrays) before storing. A short-lived parameter object that only bundles data for one call (e.g., `MoveContext`)
  may keep them without copies.
- Records are shallowly immutable: they may group references to mutable objects (JavaFX properties or nodes, grid
  models, entities, `Random`) as a wrapper, holder, snapshot, or parameter object (e.g., `MoveContext`). Use a class
  instead when the type itself owns changing state or controls a lifecycle. Such a record says so in the last paragraph
  of its type Javadoc: "The record wraps/groups/holds ... without copying ...", optionally followed by the
  consequence after a semicolon; a private record does so only when it explains something. Do not use such a record as
  a key in a hash-based collection or compare it with `equals(Object)`; compare its components instead, unless they
  are immutable in that use (e.g., `GridCell` with an enum entity).
- Prefer plain adjectives or noun phrases for boolean record components (`batchModeRunning`, `finalStep`); boolean
  query names (`isFinished`, `hasTarget`) are allowed. Their generated accessors are exempt from the boolean query
  prefixes in "Naming Conventions". Use one style within a record, and use the name of an implemented interface method
  where one exists (e.g., `MoveDecision.isFoodTarget()`).
- Declare `toDisplayString()` on a record whose values are shown to users or in logs (e.g., coordinates, sizes,
  neighborhood results, cells, descriptors, rules); results, statistics, configs, user action contexts, and private
  records do not need one.

## Enums

- Use enums only for fixed, closed domain sets.
- Place enum behavior/metadata on the enum type itself.
- Use exhaustive `switch` expressions for enum branching; do not add a `default` branch when all constants are known.
- Use explicit stable codes/keys for external representations; never persist or exchange `ordinal()`.
- Enums offered as a config selection follow the existing `labelResourceKey()`/`resourceKey()` pair for their
  localization keys; edit toolbar choices and records expose the key of their own display text as `labelKey()`.
  The enum defines its keys itself: per-constant keys as constructor arguments, the type label key as the string
  literal returned by the static `labelResourceKey()`.
- Document declaration-order semantics in type Javadoc when order matters.

## Utility Classes

Rules for classes with only static members (constants, static methods, or both).

- Declare a utility class `final` with exactly one empty `private` constructor without Javadoc; do not throw an
  exception from it.
- Sort the constants of a constant holder without domain grouping (e.g., localization keys or style class names)
  alphabetically by name, and check the order in its test with `ConstantTestSupport.assertConstantNamesSorted(..)`.
- Name a utility class with static factories for an interface after the plural of the interface (`GridPatterns` for
  `GridPattern`, `GridInitializers` for `GridInitializer`), as in `Collections`; name classes that create JavaFX
  objects `FX*Factory` (see `ui.md`); name other utility classes after their role (e.g., `GridGeometry`,
  `CellNeighborhoods`, `<Simulation>Constraints`).

## Test Access

Rules for all classes in main sources. Tests lie in the package of the tested class (`junit.md`), so they access its
package-private members directly.

- Keep a method `private` and test it through the API of its class by default. Test it directly only if it contains
  non-trivial logic (e.g., calculations, rules, parsing, or validation) whose cases are hard to reach through the
  class API with reasonable setup; then make it package-private instead of using reflection. Several methods of one
  class may become package-private; do not extract logic into new classes or methods only for tests.
- In a non-final class, including an abstract class, declare a method that is package-private only for tests also
  `final`.
- Check private state through the API or an observable property. If that is not possible, add a package-private
  accessor named by "Naming Conventions" (e.g., a noun-style name); do not open fields for tests.
- Package-private constructors or overloads that let tests replace a dependency are allowed (e.g., the constructor of
  `DefaultMainViewModel` that takes an `ExecutorService`).
- Reserve `*ForTesting` methods for the test infrastructure of `core` (`core.md`), which changes the state of a class
  for tests (e.g., `AppLogger.initializeForTesting()`); other members for tests do not use the suffix.
- Do not mark members opened for tests: no annotation such as `@VisibleForTesting`, and no Javadoc or comment that
  says so.

## Nullability (JSpecify)

The module declaration `module-info.java` uses `@NullMarked` to establish non-null-by-default for all packages. Do not
add `package-info.java` files. IntelliJ does not apply the module-level `@NullMarked` to test sources, which are
patched into the module only by Gradle; test code is therefore unspecified, while calls into main code are still
checked. Do not use `@Nullable` in test sources. When a method declared in test sources overrides a method with a
non-null reference return type (from main code or the JDK, e.g. `toString()`), annotate it with `@NonNull`
(imported from `org.jspecify.annotations`) on its own line before `@Override`. When it overrides a method of main code,
also annotate each parameter that is non-null there with `@NonNull` directly before its type (`@NonNull TimeUnit unit`).

Only IntelliJ checks these contracts; the Gradle build and the tests do not. Check your own code against the rules
below.

- Use `@Nullable` (imported from `org.jspecify.annotations`) only for intentional nullable contracts, not
  defensively.
- Use `Optional` only as a return type; use `@Nullable` or an overload for parameters and fields. Return empty
  collections instead of `null`.
- Do not check non-null-by-default parameters with `Objects.requireNonNull(...)`; use it only to assert that a
  `@Nullable` value (e.g., a field or a result) is set at this point.
- Place `@Nullable` directly before the annotated type on the same line, because it is a type-use annotation
  (`@Nullable String emojiKey()`, `ObjectProperty<@Nullable GC>`).
- Handle `null` before dereferencing a `@Nullable` field, parameter, or result.
- Do not pass `null` or a `@Nullable` value to a non-null parameter, assign it to a non-null field, or return it from
  a method with a non-null return type.
- Keep overrides compatible: an override may make a parameter `@Nullable`, but not its return type if the overridden
  return type is non-null.
- Handle `null` from JDK methods documented to return it (e.g., `Map.get` for a missing key); `@NullMarked` does not
  cover JDK types.
- When adding or removing `@Nullable` on an existing contract, update all callers, overrides, and implementations.
- Name added or removed `@Nullable` contracts in your final summary, so that reviewers can check them in IntelliJ.

## Javadoc

Rules for main sources; `core.md` adds stricter rules for `core`, and test sources follow `junit.md`.

### Coverage

- Any type may have Javadoc; public and protected types, including nested ones, must have it.
- Constructors, methods, fields, constants, and enum constants of any visibility may have Javadoc only when it
  explains something that the name, signature, and types do not show, such as valid ranges and units, the meaning of
  empty, `Optional`, or `@Nullable` results, contract-relevant exceptions, side effects, threading requirements,
  order or priority, lifecycle, domain rules, or when abstract or protected methods meant for subclasses are called
  and what implementations must do.
- Do not add Javadoc whose summary and tags only restate the name and signature (e.g., "Private constructor to
  prevent instantiation." or "Returns the value." on `getValue()`); remove such Javadoc, but keep existing Javadoc
  when in doubt.
- Do not add Javadoc to pure `@Override` methods unless the override adds constraints, side effects, or surprising
  semantics.

### Tags

- When a type, constructor, or method has Javadoc, document every parameter, record component, and type parameter
  with `@param`, every non-void result with `@return` (not `{@return ...}`), and every checked exception of the
  `throws` clause with `@throws`. `app:javadoc` does not report missing tags; check them in reviews.
- A compact constructor that rejects arguments has Javadoc that documents its invariants with `@throws` and has no
  `@param` tags; state the invariants there, not in the record Javadoc. A compact constructor that only copies
  needs no Javadoc.
- Document contract-relevant unchecked exceptions with `@throws`; never document a `NullPointerException` for
  non-null-by-default parameters.
- Use `@see` rarely: only for a closely related element that the text does not already name, such as a counterpart
  or an alternative to the documented member; not for the types of parameters, components, fields, or results.

### Style

- Start a method summary with a verb in the third person (e.g., "Returns ...", "Creates ..."); a constructor's
  summary starts with "Creates a ..." (e.g., "Creates a runner that ..."). Types, fields, constants, and enum
  constants may start with a noun phrase that says what they are or provide (e.g., "Factory for ...", "Defaults,
  limits, and allowed values of ..."), but not with "This class ..." or "Utility class ...".
- Write the literals `true`, `false`, and `null` as `{@code true}`, `{@code false}`, and `{@code null}`.
- Describe behavior; avoid implementation detail unless it is contract-relevant.
- Document only behavior verified in the code, its callers, or its tests; do not invent thread safety, nullability,
  exceptions, or limits, and leave out what you cannot verify.
- Use `{@link}` only when the target helps the reader; otherwise write code elements with `{@code}`. Reference
  imported types by simple name and other types by fully qualified name; do not add imports only for Javadoc. Do
  not link to a package that the documented package must not depend on (e.g., from `shared` to `model`); use
  `{@code SimpleName}` instead.
- Separate paragraphs with an empty `*` line and start each paragraph after the first with `<p>` directly before its
  first word (`<p>Text`); do not write `</p>`, and do not put `<p>` before block elements such as `<ul>`, `<pre>`, or
  `{@snippet}`.
- Write lists with `<ul>` and `<li>`, not with `- `.
- Align block tags as the IntelliJ formatter does: align the descriptions of the `@param` tags in one column and
  those of the `@throws` tags in one column, and indent their continuation lines to that column; start the
  continuation lines of `@return` directly after `* `, without extra indentation.
- `{@snippet}` is welcome for code examples; do not use Markdown documentation comments (`///`).

## Exceptions

- Throw specific JDK exceptions (e.g., `IllegalArgumentException`, `IllegalStateException`), not `RuntimeException`
  or `Exception`.
- Write exception messages without a final period in one of three forms: `<name> must <condition>, but was: <value>`
  for rejected values (`in [min, max]` for inclusive ranges, `name=value` pairs for relations between values),
  `Unable to <action>: <reason or name=value pairs>` for failed operations, and `No <thing> found for <name>: <value>`
  for failed lookups. Use the exact code name of the checked value, its `toString()`, and string concatenation.
- Catch specific exception types. Catch broadly (`RuntimeException`, `Error`) only at top-level boundaries, such as
  application startup, background tasks, and UI action handlers, and log the exception there (e.g.,
  `DefaultMainViewModel`).
- Do not leave a catch block empty, and do not log an exception and rethrow it; handle and log it in one place.
- Use try-with-resources for `AutoCloseable` resources.

## Logging with AppLogger

Use `de.mkalb.etpetssim.core.AppLogger` for all application logging.

- Use `Supplier<String>` overloads (`debug(() -> ...)`, `info(() -> ...)`) when message construction is non-trivial.
- Use `*f` formatting overloads (`debugf`, `infof`, `warnf`, `errorf`) for parameterized messages instead of manual
  `String.format(...)`.
- Prefix every log message with the class name or a fixed component name and a colon (e.g., `"AppArgs: ..."`, or
  `LOG_COMPONENT` as `"%s: ..."` with `"MainView"` in the shared main views), and write it without a final period.
  Put values after a colon as `name: value` or `name=value` pairs. Start a message about ignored input with
  `Ignoring ...` and one about a failed operation with `Failed to ...`; a failed lookup may use `... not found: ...`.
- Do not log inside tight rendering or simulation step loops at `info` or higher.
- Log exceptions via `AppLogger.error(throwable, message)`; do not concatenate stack traces into the message manually.
- Commented-out `AppLogger` calls may be kept as diagnostic traces (e.g., in step logic); do not remove them as dead
  code.

## Suppressions Policy

- Keep each `@SuppressWarnings` scope as narrow as possible (prefer method/field over class).
- Do not add explanatory comments to `@SuppressWarnings`.
- Do not add or remove `@SuppressWarnings` without the maintainer's review; prefer improving inspections or rules
  over adding suppressions.

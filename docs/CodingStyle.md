## Java Method Naming Conventions

These conventions reflect the method naming patterns currently used in the Java codebase.

- **build**: Use for methods that assemble multiple UI parts into a composed region or container.
  Example: `buildMainRegion()`, `buildConfigRegion()`

- **create**: Use for methods that instantiate and configure a single object, control, or view.
  Example: `createLabel()`, `createVBox()`, `createSimulationRegion()`

- **compute**: Use for deterministic calculations that derive a value from inputs or current state.
  Example: `computeCellDimension()`, `computeCellFontSize()`

- **draw**: Use for rendering logic that paints onto a canvas or graphics context.
  Example: `drawCell()`, `drawCenteredTextInCell()`

- **is**: Use for boolean queries that describe state, validity, or mode checks.
  Example: `isIllegal()`, `isWithinBounds()`, `isModeTimed()`

- **has**: Use for boolean queries that express presence, availability, or possession.
  Example: `hasKey()`, `hasEqualEdgeBehaviors()`

- **get / set**: Use standard Java-style getters and setters for mutable values and model state.
  Example: `getValue()`, `setValue()`, `setDirection()`

- **Unprefixed accessors in utility-style APIs**: In selected static utility or value-centric APIs, concise noun-style accessors are acceptable.
  Example: `locale()`, `bundle()`, `keys()`, `supportedLocales()`

- **Unprefixed domain operations in immutable types**: In records, enums, and immutable value objects, concise domain terms are acceptable for pure derived values or navigation.
  Example: `area()`, `perimeter()`, `aspectRatio()`, `opposite()`, `nextClockwise()`

- **Property suffix**: Use the `Property` suffix for JavaFX property accessors that return a property object.
  Example: `actionButtonRequestedProperty()`, `stepDurationProperty()`

- **as**: Use for view or conversion accessors that expose another representation.
  Example: `asStringBinding()`, `asObjectProperty()`

- **to / from**: Use for deterministic coordinate or value conversions.
  Example: `toCanvasPosition()`, `fromCanvasPosition()`

- **Collection and stream views**: Use clear plural nouns for materialized collections and `...Stream()` / `stream...()` for stream-producing methods.
  Example: `coordinatesList()`, `coordinatesStream()`, `cells()`, `streamClockwise()`

- **request**: Use for methods that trigger an action flag or explicit user-intent event.
  Example: `requestActionButton()`, `requestCancelButton()`

- **of**: Prefer `of(...)` for static factory methods that validate and create configured instances.
  Example: `InputDoubleProperty.of(...)`, `InputEnumProperty.of(...)`

- **with**: Use for static factory-style methods that create a configured variant of an object.
  Example: `withMinStepDuration(...)`

- **initialize / reset / shutdown**: Use lifecycle verbs for setup and teardown operations.
  Example: `initialize(...)`, `resetForTesting()`, `shutdown()`

- **Functional combinators**: For predicate/function composition APIs, short combinator names are acceptable.
  Example: `and(...)`, `or(...)`, `negate()`, `compose(...)`, `identity()`

- **Record accessors**: Java records keep their generated accessor names without prefixes.
  Example: `x()`, `y()`

- **Test methods**: Name test methods with a `test` prefix followed by the behavior or scenario being verified.
  Example: `testEnumValues()`, `testNullArgumentsThrowsException()`

### General notes

- Use lowerCamelCase for method and parameter names.
- Prefer descriptive verbs and domain terms over abbreviations.
- When `null` is part of the contract, document it explicitly and annotate it consistently with `@Nullable`.

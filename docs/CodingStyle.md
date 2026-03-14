## Java Method Naming Conventions

These conventions reflect the method naming patterns already established in the Java codebase.

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

- **Property suffix**: Use the `Property` suffix for JavaFX property accessors that return a property object instead of the raw value.
  Example: `actionButtonRequestedProperty()`, `stepDurationProperty()`

- **request**: Use for methods that trigger an action flag or explicit user-intent event.
  Example: `requestActionButton()`, `requestCancelButton()`

- **with**: Use for static factory-style methods that create a configured variant of an object.
  Example: `withMinStepDuration(...)`

- **Record accessors**: Java records keep their generated accessor names without prefixes.
  Example: `x()`, `y()`

- **Test methods**: Name test methods with a `test` prefix followed by the behavior or scenario being verified.
  Example: `testEnumValues()`, `testNullArgumentsThrowsException()`

### General notes

- Use lower camel case for method and parameter names.
- Prefer descriptive verbs and domain terms over abbreviations.
- When `null` is part of the contract, document it explicitly and annotate it consistently with `@Nullable`.

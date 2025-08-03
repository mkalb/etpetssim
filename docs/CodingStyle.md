## Method Naming Conventions

- **build**: Use for methods that assemble or compose UI components or complex objects, often returning a ready-to-use
  instance.
    - Example: `buildConfigRegion()`, `buildControlButton()`

- **create**: Use for methods that instantiate new objects or components, typically returning a new instance each time.
    - Example: `createVBox()`, `createHorizontalGradient()`

- **compute**: Use for pure functions that derive or transform a value based on input, without causing side effects or
  modifying state. These methods should be deterministic and only depend on their parameters.
    - Example: `computeCellDimension()`, `computeTextDimension()`

Follow these conventions to ensure clarity and consistency in method naming.
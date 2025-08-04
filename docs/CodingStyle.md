## Method Naming Conventions

- **build**: Use for methods that assemble or compose multiple UI components into a larger structure (e\.g\. panels,
  scenes, layouts)\. These methods typically return a fully composed component or container, often combining several
  subcomponents\.
    - Example: `buildConfigRegion()`

- **create**: Use for methods that instantiate and configure a single UI element or object, typically returning a new
  instance each time \(e\.g\. a new `Button`, `VBox`, etc\.\)\.
    - Example: `createVBox()`, `createHorizontalGradient()`

- **compute**: Use for pure functions that derive or transform a value based on input, without causing side effects or
  modifying state\. These methods should be deterministic and only depend on their parameters\.
    - Example: `computeCellDimension()`, `computeTextDimension()`

- **draw**: Use for methods that render or paint visual elements onto a canvas, scene, or graphical context\. These
  methods typically perform drawing operations and may have side effects on the UI\.
    - Example: `drawCanvas()`, `drawCell()`

Follow these conventions to ensure clarity and consistency in method naming\.
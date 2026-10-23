# JavaFX-Independent Application Variants

**Status:** idea (not planned; after the 1.0 release, possibly never)

**Related issues:** none yet; related: #44 (configuration file from the command line), #54 (rendering performance)

## Idea

The application is bound to JavaFX today. Two variants without the JavaFX desktop UI could extend its use:

1. a command-line application (CLI) that runs simulations without a window,
2. a web application that runs the simulations in a browser.

Both are large projects that need research and design before any issue is created. The web application is clearly
larger and more uncertain than the CLI.

## Shared Foundation: A JavaFX-Free Core

Both variants need the engine and the simulation models without JavaFX. Current state (2026-10-23, files importing
`javafx.*`):

| Package | Files with JavaFX | Main use |
|---|---|---|
| `core` | 1 of 6 | `javafx.scene.image` |
| `engine` | 5 of 50 | `javafx.scene.paint` (colors) |
| `ui` | 12 of 14 | JavaFX UI infrastructure |
| `simulations` | 92 of 276 | views and view models; in `model` packages only the entity descriptors (colors) |

The application is a single Gradle and JPMS module that requires `javafx.controls`.

Rough steps:

- Replace `javafx.scene.paint.Color` in `engine` and the entity descriptors with a JavaFX-free color type; convert to
  JavaFX colors only in the UI.
- Split the build into a core module (engine, models, configuration, statistics) without JavaFX and the JavaFX
  application module.

## Variant 1: Command-Line Application

Possible purposes:

- batch and parameter studies without a window, reproducible with a fixed random seed,
- benchmarks of the engine and the simulations (see #54),
- runs on servers or in CI, e.g. as tests of complete simulations.

Possible outputs:

- statistics per step as CSV or JSON,
- a summary at the end (steps, run time, extrema),
- an ASCII rendering for small grids,
- image export (e.g. PNG via `java.desktop` instead of JavaFX),
- configuration loaded from a file (see #44).

Rough split into later issues:

1. JavaFX-free color type in `engine` and the entity descriptors.
2. Gradle and JPMS split into core and JavaFX application.
3. CLI runner with arguments (simulation, configuration, number of steps, seed).
4. Output formats.
5. Distribution of the CLI variant.

## Variant 2: Web Application

Only a rough idea so far. Possible approaches to evaluate:

- run the existing JavaFX UI in the browser (e.g. JPro or WebFX),
- run the JavaFX-free core in the browser (e.g. compiled to JavaScript or WebAssembly with TeaVM, or a JVM in the
  browser such as CheerpJ) with a new web UI,
- run the core on a server and render in the browser (e.g. HTML canvas, updates via WebSocket).

## Open Questions

- Is there a concrete use for the CLI variant that justifies the build split?
- Which outputs does the CLI variant need first?
- Which web approach supports Java 27 and fits the project's preference for few build dependencies?
- Should the web application reuse the JavaFX UI or get its own UI?

# Extraterrestrial Pets Simulation

## Overview

Welcome to the **Extraterrestrial Pets Simulation** project (_etpetssim_)!
This open-source project aims to create various simple 2D simulations (_Toy models_, _Agent-Based Models_, _Cellular
automata_) with a top-down view.
The simulation engine organizes the grid into structured cells, each with coordinates and entities, enabling flexible
modeling and efficient computation.
Each cell is a regular polygon —triangle, square, or hexagon— with equal sides and angles, ensuring consistent geometry
and neighbor relationships throughout the grid.
The grid supports various edge behaviors —blocking, wrapping, or absorbing— and offers flexible modes for
calculating neighbors, either by shared edges or by both edges and vertices.

**Status:** This project is currently under development.

**Maintainer note:** This is a single-developer project maintained by me (Mathias Kalb).

## Simulations

| Simulation                                                                     | Type               | Description                                                                                          |
|--------------------------------------------------------------------------------|--------------------|------------------------------------------------------------------------------------------------------|
| ET Pets                                                                        | Agent-Based Model  | Agents search for plants and insects, avoid obstacles, create trails, and reproduce with inheritance |
| [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)                                 | Agent-Based Model  | Agents (fish and sharks) interact in a predator–prey system                                          |
| [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) | Cellular Automaton | Cells evolve based on simple local birth and survival rules                                          |
| [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s_ant)                 | Cellular Automaton | A moving ant updates cell states using simple rules                                                  |
| [Forest-fire model](https://en.wikipedia.org/wiki/Forest-fire_model)           | Cellular Automaton | Cells model tree growth and fire spread through local rules                                          |
| [Sugarscape](https://en.wikipedia.org/wiki/Sugarscape)                         | Agent-Based Model  | Agents collect and consume sugar resources                                                           |
| [Snake](https://en.wikipedia.org/wiki/Snake_(video_game_genre))                | Agent-Based Model  | Snakes move, grow, and avoid collisions while consuming food                                         |
| Rebounding Entities                                                            | Agent-Based Model  | Entities move directionally, bounce off boundaries, and destroy obstacles and other agents           |
| Simulation Lab                                                                 | Development Tool   | Testing environment for grid rendering and cell shapes                                               |

For a detailed inventory of simulation entity types, see
the [Simulation Entity Catalog](docs/simulations/Simulation_Entity_Catalog.md).

### Simulation Gallery

#### ET Pets

(Screenshots will be added once the simulation is fully implemented.)

#### Wa-Tor

![Wa-Tor](assets/screenshots/screenshot_wator_01.png)

#### Conway's Game of Life

![Conway's Game of Life](assets/screenshots/screenshot_conway_01.png)

#### Langton's Ant

![Langton's Ant](assets/screenshots/screenshot_langton_01.png)

#### Forest-fire model

![Forest-fire model](assets/screenshots/screenshot_forest_01.png)

#### Sugarscape

![Sugarscape](assets/screenshots/screenshot_sugar_01.png)

#### Snake

![Snake](assets/screenshots/screenshot_snake_01.png)

#### Rebounding Entities

![Rebounding Entities](assets/screenshots/screenshot_rebounding_01.png)

#### Simulation Lab

![Simulation Lab — Hexagon](assets/screenshots/screenshot_lab_01.png)
![Simulation Lab — Triangle](assets/screenshots/screenshot_lab_02.png)
![Simulation Lab — Square](assets/screenshots/screenshot_lab_03.png)

## Goals

- Explore and apply modern Java features in practice.
- Get to know the JavaFX library and gain some initial experience with it.
- Enjoy creativity in developing new simulations and adapting well-known models.

## Development Approach

Artificial intelligence (AI) tools are used during development to improve productivity and support code quality. In
particular, Microsoft Copilot and GitHub Copilot are used for code generation, documentation support, refactoring and
optimization tasks, and as a practical aid while learning and applying JavaFX and MVVM concepts. These tools also help
accelerate exploration of implementation variants and architecture options during day-to-day development.

At the same time, all generated content is reviewed and adapted in the context of the project goals, codebase
consistency, and long-term maintainability. AI support is therefore treated as development assistance, while final
technical decisions remain project-driven and under maintainer control.

Project-specific AI instruction files for coding assistants are available
in [.github/instructions](.github/instructions).

## Feedback and Issues

Bug reports and improvement suggestions are very welcome.
Please open a [GitHub Issue](https://github.com/mkalb/etpetssim/issues) if you find a problem or have an idea to improve
the project.

If possible, include:

- clear steps to reproduce (for bugs),
- expected vs. actual behavior,
- screenshots or logs.

## Run the App

Use the Gradle Wrapper from the repository root. No global Gradle installation is required.
Prerequisite: Java 25.

### Windows (PowerShell)

```powershell
.\gradlew.bat :app:run
.\gradlew.bat test
.\gradlew.bat :app:distZip
```

### macOS / Linux

```bash
./gradlew :app:run
./gradlew test
./gradlew :app:distZip
```

## Technologies Used

- **Java**: The primary programming language used throughout the project.
- **JavaFX**: Used to create the graphical user interface.
- **Gradle**: Build system used for the project, including a Gradle wrapper.
- **IntelliJ IDEA Community Edition**: The development environment ("IDE") of choice, provided by JetBrains.

This project uses the latest stable versions of all technologies whenever possible.

| Technology     | Version            | URL                                                       |
|----------------|--------------------|-----------------------------------------------------------|
| Java (OpenJDK) | Eclipse Temurin 25 | [adoptium.net](https://adoptium.net/)                     |
| JavaFX         | 25.0.2             | [openjfx.io](https://openjfx.io/)                         |
| Gradle         | 9.4.1              | [gradle.org](https://gradle.org/)                         |
| IntelliJ IDEA  | 2025.3             | [www.jetbrains.com/idea](https://www.jetbrains.com/idea/) |

## License

This project is licensed under the [MIT License](LICENSE).

This project uses several third-party libraries and tools, each with its own license.
For the project's third-party list, see the [THIRD-PARTY-LICENSES](THIRD-PARTY-LICENSES) file.

Both license files are also available in the application's About dialog.

## Author

- Name: Mathias Kalb
- GitHub: [mkalb](https://github.com/mkalb)
- Project: [Extraterrestrial Pets Simulation](https://github.com/mkalb/etpetssim)

Copyright (c) 2025-2026 Mathias Kalb

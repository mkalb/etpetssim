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

### Implemented Simulations

| Name (Wikipedia)                                                               | Sync / Async | Grid Model     | Default Entity    | Entities / Agents / Cells                                                    |
|--------------------------------------------------------------------------------|--------------|----------------|-------------------|------------------------------------------------------------------------------|
| [Wa-Tor](https://en.wikipedia.org/wiki/Wa-Tor)                                 | asynchronous | Array          | 🟦 Water          | Shark 🦈 (many), Fish 🐟 (many)                                              |
| [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) | synchronous  | Sparse         | 🟨 Dead           | Alive 🟥 (many)                                                              |
| [Langton's Ant](https://en.wikipedia.org/wiki/Langton%27s_ant)                 | asynchronous | Array & Sparse | ⬜ Unvisited       | Ant 🐜 (1 agent), Visited Ground (many, 16 colors)                           |
| [Forest-fire model](https://en.wikipedia.org/wiki/Forest-fire_model)           | synchronous  | Array          | 🟫 Empty          | Tree 🌲 (many), Burning 🔥 (many)                                            |
| [Sugarscape](https://en.wikipedia.org/wiki/Sugarscape)                         | asynchronous | Sparse         | None (🟫 Terrain) | 🟨 Sugar (many), 🟦 Agent (many)                                             |
| [Snake](https://en.wikipedia.org/wiki/Snake_(video_game_genre))                | asynchronous | Sparse         | ⬛ Ground          | 🔵 Snake Head (many), 🟩 Snake Segment (many), 🟡 Food (many), ⬜ Wall (many) |
| Rebounding Entities                                                            | asynchronous | Sparse         | ⬛ Ground          | 🟨 Moving Entity (many), ⬜ Wall (many)                                       |
| Simulation Lab                                                                 | n/a          | Sparse         | Normal            | Highlighted                                                                  |

### Planned Simulations

- Extraterrestrial Pets Simulation (ET Pets)

### Screenshots

#### Wa-Tor
![Wa-Tor - Screenshot 01](assets/screenshots/screenshot_wator_01.png)

#### Conway's Game of Life
![Conway's Game of Life - Screenshot 01](assets/screenshots/screenshot_conway_01.png)

#### Langton's Ant
![Langton's Ant - Screenshot 01](assets/screenshots/screenshot_langton_01.png)

#### Forest-fire model
![Forest-fire model - Screenshot 01](assets/screenshots/screenshot_forest_01.png)

#### Sugarscape
![Sugarscape - Screenshot 01](assets/screenshots/screenshot_sugar_01.png)

#### Snake
![Snake - Screenshot 01](assets/screenshots/screenshot_snake_01.png)

#### Rebounding Entities
![Rebounding Entities - Screenshot 01](assets/screenshots/screenshot_rebounding_01.png)

#### Simulation Lab
![Simulation Lab - Screenshot 01](assets/screenshots/screenshot_lab_01.png)
![Simulation Lab - Screenshot 02](assets/screenshots/screenshot_lab_02.png)
![Simulation Lab - Screenshot 03](assets/screenshots/screenshot_lab_03.png)

## Goals

- Explore and apply modern Java features in practice.
- Get to know the JavaFX library and gain some initial experience with it.
- Enjoy creativity in developing new simulations and adapting well-known models.

## Development Approach

Artificial intelligence (AI) tools were used during development to enhance productivity and code quality. Specifically,
Microsoft Copilot and GitHub Copilot assisted with code generation, documentation, optimization tasks, learning JavaFX
and MVVM, and making design and architecture decisions.

## Feedback and Issues

Bug reports and improvement suggestions are very welcome.
Please open a [GitHub Issue](https://github.com/mkalb/etpetssim/issues) if you find a problem or have an idea to improve the project.

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

| Technology     | Version            | URL                                                            |
|----------------|--------------------|----------------------------------------------------------------|
| Java (OpenJDK) | Eclipse Temurin 25 | [adoptium.net](https://adoptium.net/)                          |
| JavaFX         | 25.0.2             | [openjfx.io](https://openjfx.io/)                              |
| Gradle         | 9.4.1              | [gradle.org](https://gradle.org/)                              |
| IntelliJ IDEA  | 2025.3             | [www.jetbrains.com/idea](https://www.jetbrains.com/idea/)      |

## License

This project is licensed under the [MIT License](LICENSE).

This project uses several third-party libraries, each with its own license.
For a complete list, see the [THIRD-PARTY-LICENSES](THIRD-PARTY-LICENSES) file.

Both license files are also available in the application's About dialog.

## Author

- Name: Mathias Kalb
- GitHub: [mkalb](https://github.com/mkalb)
- Project: [Extraterrestrial Pets Simulation](https://github.com/mkalb/etpetssim)

Copyright (c) 2025-2026 Mathias Kalb
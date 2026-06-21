# etpetssim Copilot Instructions

etpetssim is a Java 25 / JavaFX 25 MVVM application for 2D grid-based simulations
(agent-based models, cellular automata, and toy models). The engine models grids of triangle, square, or hexagon cells
with configurable edge behavior and neighbor modes.

Use these as repository-wide defaults. More specific `.github/instructions/*.instructions.md` files take precedence for
matching paths.

## Priority and Scope

- Keep changes minimal, focused, and convention-preserving; avoid unrelated refactors and formatting-only edits.
- Preserve public APIs unless explicitly asked to change them.
- Do not invent file paths, package names, class names, symbols, or APIs.
- Prefer root-cause fixes over surface patches.
- Follow existing naming patterns and nearest peer-file conventions when adding new code.
- Ask before guessing when requirements are ambiguous.
- Do not commit, create branches, or run destructive git commands unless explicitly asked.

## Platform Baseline

- Use Java 25 language features only.
- Use JavaFX 25 for UI work.
- Use the Gradle Wrapper from the repository root. Prefer `gradlew.bat app:compileJava` for compile checks and
  `gradlew.bat app:test` for tests.
- Use `gradlew.bat app:run` only when running the JavaFX application is necessary.
- Run relevant checks when practical; if verification is skipped or blocked, say so briefly.

## Encoding and File Conventions

- Use UTF-8 for `.java`, `.md`, and `.properties` files.
- Prefer ASCII unless existing content or domain needs justify non-ASCII.
- Keep `.properties` files and localization keys sorted alphabetically.
- Write repository Markdown in standard GitHub Markdown.

## Core Engineering Rules

- Do not duplicate business logic; extract shared logic into focused methods or classes (DRY).
- Prefer the simplest complete solution; keep methods small and single-purpose (KISS).
- Do not introduce abstractions without clear ongoing value.
- Use meaningful domain names and keep classes cohesive.
- Remove dead code and outdated comments; commented-out `AppLogger` calls may be kept as diagnostic traces.

## Localization and Text

- Write comments, Javadoc, and repository Markdown in English (en_US).
- Keep user-facing text in `i18n.messages` resource bundles.
- Use constants for localization keys instead of string literals.

## Reviews

- For code reviews, lead with bugs, regressions, risks, and missing tests before summaries.

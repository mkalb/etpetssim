---
applyTo: "**"
description: "Project-wide GitHub Copilot rules for etpetssim. Applies to all files unless a more specific instruction exists."
---

# etpetssim Global Instructions

This repository implements 2D grid-based simulations using MVVM architecture.

Use this file as the default behavior baseline for all tasks in this repository.
If a more specific instructions file exists for a path, that file takes precedence.
Treat these rules as preferred defaults: follow them by default, and only deviate when a clear, task-specific reason
exists.

## Priority And Scope

- Keep changes minimal and focused. Do not reformat unrelated code.
- Do not invent file paths, package names, class names, symbols, or APIs.
- If requirements are ambiguous, ask before guessing.
- Follow existing naming patterns and nearest peer-file conventions when adding new code.

## Platform Baseline

- Use Java 25 language features only.
- Use JavaFX 25 for UI work.
- Use the Gradle Wrapper from repository root for build and test commands.

## Encoding and File Conventions

- Use UTF-8 for `.java`, `.md`, and `.properties` files.
- Keep Java properties files sorted alphabetically by key.
- Write repository Markdown in standard GitHub Markdown.

## Core Engineering Rules

- Do not duplicate business logic; extract shared logic into focused methods or classes (DRY).
- Prefer the simplest solution that fully satisfies requirements; keep methods small and single-purpose (KISS).
- Do not introduce abstractions without clear ongoing value.
- Use meaningful domain names and keep classes cohesive.
- Remove dead code and outdated comments; commented-out `AppLogger` calls may be kept as diagnostic traces.

## Localization And Text

- Write comments, Javadoc, and repository Markdown in English (en_US).
- Keep user-facing text in `i18n.messages` resource bundles.
- Use constants for localization keys instead of string literals.
- Keep `.properties` keys sorted alphabetically.

## Nullability and Tests

- Follow JSpecify defaults.
- Use `@Nullable` only for intentional nullable contracts.
- Use JUnit 5 for tests. Name test methods with `test...`.

## API And Naming Discipline

- Preserve existing public APIs unless explicitly asked to change them.
- Follow existing naming patterns and nearest peer-file conventions.
- Prefer simple, cohesive designs and avoid unnecessary abstractions.

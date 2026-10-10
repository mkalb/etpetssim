# etpetssim

etpetssim is a Java 27 / JavaFX 27 MVVM application for 2D grid-based simulations (agent-based models, cellular
automata, and toy models). The engine models grids of triangle, square, or hexagon cells with configurable edge behavior
and neighbor modes.

## Project Structure

Single Gradle module `app`. Packages under `de.mkalb.etpetssim` in `app/src/main/java`:

- `core`: cross-cutting `App*` utilities.
- `engine`: grid, topology, model, neighborhood, and execution engine.
- `ui`: reusable JavaFX UI infrastructure.
- `simulations`: shared simulation infrastructure in `simulations.core` and one MVVM package per simulation.

Other locations:

- `app/src/main/resources`: `css`, `i18n` (`messages_en_US`, `messages_de_DE`), and `images`.
- `app/src/test/java`: JUnit tests and test support.
- `docs/simulations`: user-facing simulation docs.
- `docs/development`: developer documentation (versioning and releases).
- `docs/planning`: planning documents and the generated `JavaTypeInventory.csv` and `JavaMethodInventory.csv`. The
  inventories may be outdated; do not update them with code changes. They are regenerated on demand with the
  `java-code-inventory` skill.
- `assets`: screenshots and SVG icons.
- `.claude/rules`: path-scoped coding rules; `.claude/skills`: agent skills.
- `.github/instructions`: GitHub Copilot copies of `.claude/rules`; keep both in sync when changing a rule.

## Package Boundaries

- `core` must not depend on `engine`, `ui`, or `simulations`.
- `engine` may depend on `core`, but not on `ui` or `simulations`.
- `ui` may depend on `core` and `engine`, but not on `simulations`.
- `simulations` may depend on `core`, `engine`, and `ui`.

## Priority and Scope

- Keep changes minimal, focused, and convention-preserving; avoid unrelated refactors and formatting-only edits.
- Preserve public APIs unless explicitly asked to change them.
- Do not invent file paths, package names, class names, symbols, or APIs.
- Prefer root-cause fixes over surface patches.
- Follow existing naming patterns and nearest peer-file conventions when adding new code.
- Ask before guessing when requirements are ambiguous.
- Do not commit, create branches, or run destructive git commands unless explicitly asked.
- When asked to commit, commit directly on `main`. Create a feature branch only on request, and do not push short-lived
  branches to `origin`. This overrides any default to branch first.

## Platform Baseline

- Target Java 27 and JavaFX 27; do not use preview features.
- Use the Gradle Wrapper from the repository root (`gradlew.bat` on Windows, `./gradlew` in POSIX shells):
    - `app:compileJava` for compile checks.
    - `app:test` for tests; it excludes `@Tag("skill")` tests.
    - `app:skillTest` after changing skill scripts in `.claude/skills`.
    - `app:run` only when running the JavaFX application is necessary.
- Run relevant checks when practical; if verification is skipped or blocked, say so briefly.

## Versioning and Releases

- The project version is derived from Git tags `vMAJOR.MINOR.PATCH[-prerelease]` (SemVer); never edit it in the build
  files.
- Create a release tag only on explicit request: annotated, on the merge commit in `main`, with a clean working tree.
  Push it only after confirmation.
- Read `docs/development/versioning-and-releases.md` before creating a release.

## Encoding and File Conventions

- Use UTF-8 for `.java`, `.md`, and `.properties` files.
- Prefer ASCII unless existing content or domain needs justify non-ASCII.
- Keep entries in `.properties` files sorted alphabetically by key.
- Write repository Markdown in standard GitHub Markdown.

## Core Engineering Rules

- Do not duplicate business logic; extract shared logic into focused methods or classes (DRY).
- Prefer the simplest complete solution; keep methods small and single-purpose (KISS).
- Do not introduce abstractions without clear ongoing value.
- Use meaningful domain names and keep classes cohesive.
- Remove dead code and outdated comments; commented-out `AppLogger` calls may be kept as diagnostic traces.

## Localization and Text

- Write all repository files (code comments, Javadoc, log and exception messages, Markdown, skills, prompts, and
  scripts, including their output) and all Git and GitHub text (e.g., commit messages, issues, and pull requests) in
  English (en_US), regardless of the language of the request. Chat replies may follow the request language.
- Do not translate English repository or GitHub text into the chat language. When reporting such text in chat, either
  quote it verbatim or give a short summary or explanation in the chat language; the user can read the file, diff,
  or issue directly.
- Keep user-facing text in `i18n.messages` resource bundles.
- Use constants for localization keys instead of string literals: app-wide keys in `core.AppLocalizationKeys`,
  simulation-specific keys as constants in the class that uses them.

## Reviews

- For code reviews, lead with bugs, regressions, risks, and missing tests before summaries.
- Do not run Gradle during a review unless explicitly asked; a review without code changes needs no build or test run.
- Agents exchange reviews through `.review/review.md`. The `.review/` folder is ignored by Git; create the folder and
  file if they are missing.
- When asked to write a review to the file, only append one block at the end. Never change or remove existing blocks, and
  do not modify source files or run `git add`, `git commit`, or `git stash`.
- Start each block with the header `## Review YYYY-MM-DD - <agent and model> - <scope>`, where the scope is the staged
  changes, a commit or range, or specific files. List findings by severity, each with file, symbol or quoted line,
  problem, and suggested fix; line numbers may become outdated.
- When asked to process the review file, verify each finding against the code, fix the valid ones, and report rejected
  ones with a reason in the chat. Delete a block once all its findings are resolved or rejected; the file may become
  empty.
- When the user asks the editing agent to review its own changes directly, report the result in the chat and do not write
  it to the file.

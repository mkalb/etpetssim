# Java Formatter

**Status:** idea (not planned; reconsider only when the conditions below apply)

**Related issues:** none yet; related: #9 (decision against mechanical formatting), #14 (CI build), #46 (static
analysis), #49 (deny rules and hooks for safety, code quality, and tests)

## Idea

A deterministic Java formatter could format or check agent-generated and external code in the Gradle build, in CI, or
in a Claude Code hook, instead of relying on the format rules in `.claude/rules/java.md` and `.editorconfig`.

## Current Decision

#9 decided against mechanical formatting: no Spotless, no IntelliJ command-line formatter, and no manual reformat of
agent changes in IntelliJ. An analysis on 2026-11-06 found that agents already write code in the IntelliJ format; the
only deviation (Javadoc tag alignment) is fixed by a rule in `java.md`. This decision still applies.

## When to Reconsider

- External contributions make a format check necessary (condition from #9).
- Agents deviate from the IntelliJ format again in a way that a rule cannot fix.
- The CI build (#14) or quality hooks (#49) exist and could run a format check.

## Candidates

Sources: the article "Comparing Java Formatters: Why Open Java Format Is My New Favorite" by Guillaume Laforge
(<https://foojay.io/today/comparing-java-formatters-why-open-java-format-is-my-new-favorite/>, 2026-11) and the
README of <https://github.com/spring-io/spring-javaformat>; facts as stated there, not verified.

| Formatter                           | Style and configurability                                                       | Integration                                                      | Notes                                                                                  |
|-------------------------------------|---------------------------------------------------------------------------------|------------------------------------------------------------------|----------------------------------------------------------------------------------------|
| Open Java Format (fork of Palantir) | 4 spaces, 120 columns; few options (style, Javadoc formatting)                  | own Gradle plugin (with `formatDiff`), Spotless, IntelliJ plugin | preferred by the article; tested up to JDK 25; needs `--add-exports` (javac internals) |
| palantir-java-format                | 4 spaces, 120 columns; few options                                              | Spotless step                                                    | known bugs that Open Java Format fixes (per the article)                               |
| google-java-format                  | 2 spaces, 100 columns (AOSP: 4 spaces); few options                             | Spotless step                                                    | style far from the current code base                                                   |
| spring-javaformat                   | Spring style, not configurable; tabs by default (spaces by config); 120 columns | own Gradle plugin (`format`, `checkFormat`), IntelliJ plugin     | Eclipse-based; does not reorder imports; Java 21+ support not documented in the README |

## Evaluation Criteria

- Support for Java 27 syntax and a short delay after new Java releases (the risk that rejected Error Prone in #10).
- Difference to the current IntelliJ format: size of a one-time reformat of the code base, Javadoc (including the tag
  alignment from #9), import layout, and lambda and record layout.
- Consistency with IntelliJ: the IDE must produce the same format (IDE plugin or matching code style settings).
- Build dependencies: number of Gradle plugins and their compatibility with the configuration cache.
- Use: format task, check task in the build or CI (#14), or a quality hook for agents (#49); formatting only changed
  lines (e.g. `formatDiff`) versus whole files.

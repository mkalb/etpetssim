# Development Setup

IntelliJ IDEA is the recommended IDE and the maintainer's development environment. VS Code and Claude Code are used as
well. This guide describes the prerequisites that apply to every environment and the setup of each tool.

## Common Prerequisites

These settings do not depend on an IDE:

- Java for the Gradle Wrapper: the Gradle Wrapper starts with any Java 17 to 27 from `JAVA_HOME` or the `PATH`. Only
  if neither provides one, set the environment variable `JAVA_HOME` to such a Java installation (e.g.,
  `JAVA_HOME=<path to jdk-27>`), also for the terminal of the IDE (IntelliJ IDEA: Settings > Tools > Terminal). The
  build provisions its Java 27 toolchain itself.
- Encoding: UTF-8 for all project files, including `.properties` files (`.editorconfig`: `charset = utf-8`).
- Line endings: the working copy uses LF for all text files except Windows scripts (`*.bat`, CRLF). `.gitattributes`
  overrides the local Git setting `core.autocrlf`, and `.editorconfig` overrides the line separator of the editor.
  Keep the line separator of existing files. `git ls-files --eol` shows the line endings of the working copy in the
  column `w/` (`w/lf`, `w/crlf`, or `w/mixed`).

### Linux and WSL

Linux x64 is supported for development (see "Platform Support" in the README). On Windows 11, WSL 2 provides such an
environment:

- Clone the repository inside the Linux file system (e.g., `~/dev/etpetssim`), not under `/mnt/c`, where file access
  is slow.
- WSLg displays the JavaFX window on the Windows desktop; no X server is needed.

## IntelliJ IDEA

Some IDE settings are committed to the repository, so that every working copy uses the same code style, inspections,
spelling dictionary, and run configurations. All other IDE settings stay local.

### Shared Files

| File or directory                              | Content                                                        |
|------------------------------------------------|----------------------------------------------------------------|
| `.editorconfig`                                | Line endings, indentation, line length, Java and `.properties` |
| `.idea/inspectionProfiles/Project_Default.xml` | Inspection profile "Project Default"                           |
| `.idea/dictionaries/project.xml`               | Project dictionary of the spell checker                        |
| `.run/`                                        | Shared run configurations                                      |

`.gitignore` ignores the `.idea` directory except for `dictionaries` and `inspectionProfiles`. The `.run` directory
lies outside `.idea` and is not ignored.

### Run Configurations

| Name                    | Action                                                                           |
|-------------------------|----------------------------------------------------------------------------------|
| `gradle_run_en`         | `app:run` with English UI and INFO log output on the console                     |
| `gradle_run_de`         | `app:run` with German UI and INFO log output on the console                      |
| `gradle_run_de_lab`     | Like `gradle_run_de`, but starts the [Simulation Lab](simulation-lab.md)         |
| `gradle_test`           | `app:test`                                                                       |
| `I18nConsistencyCheck`  | Runs the report of the `i18n-consistency` skill script in a terminal             |
| `JavaCodeInventory`     | Regenerates the CSV files in `docs/generated` in a terminal                      |

The two script configurations call `java` from the `PATH` and run in the default shell of the system.

### Manual Setup

These settings are not committed and must be set once per working copy, in addition to the
[common prerequisites](#common-prerequisites):

- Project SDK: any Java 27 JDK (the maintainer uses Eclipse Temurin 27); language level 27.
- Gradle: open the project as a Gradle project and keep building and running with Gradle (the default). The Gradle
  build uses a Java 27 toolchain.
- Inspections: select the profile "Project Default" (stored in the project) in Settings > Editor > Inspections.
- Code style (Settings > Editor > Code Style), with the IDE scheme "Default"; `.editorconfig` overrides the scheme:
    - General: line separator "Unix and macOS (\n)", hard wrap at 120 columns, "Wrap on typing" off, "Detect and use
      existing file indents for editing" off, "Enable EditorConfig support" on.
    - Formatter: do not format `*.md`, because the IntelliJ formatter joined wrapped lines that start with special
      characters into very long lines; formatter markers in code comments off.
- File encodings (Settings > Editor > File Encodings): UTF-8 for project files and for `.properties` files.

### Deliberate Choices

- `.editorconfig` sets the import-on-demand thresholds to 999, so that IntelliJ does not replace single imports with
  `*` imports.
- The inspection `IllegalDependencyOnInternalPackage` is disabled for the scope "Tests". Gradle compiles and runs the
  tests patched into the application module (`--patch-module`); IntelliJ treats test sources as outside the module
  and would otherwise report false module dependency errors.

### Changing Shared Settings

1. Change the setting in IntelliJ (or the `.run` configuration with "Store as project file").
2. Check the diff: commit only the shared files listed above and no unrelated IDE changes.
3. Add project-specific words to the project dictionary, not to the personal one.

## VS Code

`.vscode/tasks.json` is committed and provides the commands of the [run configurations](#run-configurations) in `.run`
as tasks. `.gitignore` ignores the rest of the `.vscode` directory, so the workspace settings stay local. The
recommended settings for `.vscode/settings.json` apply the encoding, line endings, final newline, and trailing
whitespace of `.editorconfig`:

```json
{
  "files.encoding": "utf8",
  "files.eol": "\n",
  "[bat]": {
    "files.eol": "\r\n"
  },
  "files.insertFinalNewline": true,
  "files.trimTrailingWhitespace": true
}
```

## Claude Code

The repository provides the configuration for Claude Code:

- `AGENTS.md`: project instructions for all agents.
- `.claude/rules`: path-scoped coding rules.
- `.claude/skills`: agent skills; their scripts need Java 27 as `java` on the `PATH`.
- `.claude/settings.json`: shared permissions.
- `.claude/settings.local.json`: personal settings; ignored by `.gitignore`.

With the Bash sandbox of Claude Code enabled (`/sandbox`), Gradle cannot use `~/.gradle` or download Gradle,
dependencies, and the Java toolchain, and `gh` cannot read its login. `git push` cannot reach GitHub, and with `gh` as
the Git credential helper, it cannot read the login either. Exclude these commands from the sandbox in the personal
settings (`~/.claude/settings.json` or `.claude/settings.local.json`):

```json
{
  "sandbox": {
    "excludedCommands": ["./gradlew *", "gh *", "git push *"]
  }
}
```

Excluded commands run outside the sandbox and are subject to the permission rules of `.claude/settings.json`. A call
is excluded only if it matches a pattern as a whole: with a redirection (e.g., `gh issue list > issues.txt`) or an
environment variable before the command, it stays in the sandbox and fails.

### Optional Plugin gradle-skills

The official plugin [gradle-skills](https://github.com/gradle/gradle-skills) (Apache-2.0) is optional and installed at
user scope, not enabled in `.claude/settings.json`:

```text
/plugin marketplace add gradle/gradle-skills
/plugin install gradle-skills@gradle-skills
```

It provides two skills:

- `gradle-skills:gradle-wrapper-upgrade`: upgrades the Gradle Wrapper (see "Gradle" in
  [Dependency Updates](dependency-updates.md#gradle)). It needs a POSIX shell; on Windows, Claude Code runs it in Git
  Bash (Bash tool).
- `gradle-skills:gradle-best-practices`: audits the build against the official Gradle best practices.

# IntelliJ IDEA Setup

IntelliJ IDEA is the development environment of the project. Some IDE settings are committed to the repository, so
that every working copy uses the same code style, inspections, spelling dictionary, and run configurations. All other
IDE settings stay local.

## Shared Files

| File or directory                              | Content                                                      |
|------------------------------------------------|--------------------------------------------------------------|
| `.editorconfig`                                | Code style: indentation, line length, Java and `.properties` |
| `.idea/inspectionProfiles/Project_Default.xml` | Inspection profile "Project Default"                         |
| `.idea/dictionaries/project.xml`               | Project dictionary of the spell checker                      |
| `.run/`                                        | Shared run configurations                                    |

`.gitignore` ignores the `.idea` directory except for `dictionaries` and `inspectionProfiles`. The `.run` directory
lies outside `.idea` and is not ignored.

## Run Configurations

| Name                    | Action                                                                           |
|-------------------------|----------------------------------------------------------------------------------|
| `gradle_run_en`         | `app:run` with English UI and INFO log output on the console                     |
| `gradle_run_de`         | `app:run` with German UI and INFO log output on the console                      |
| `gradle_run_de_lab`     | Like `gradle_run_de`, but starts the [Simulation Lab](simulation-lab.md)         |
| `gradle_test`           | `app:test`                                                                       |
| `I18nConsistencyCheck`  | Runs the report of the `i18n-consistency` skill script in a PowerShell terminal  |
| `JavaCodeInventory`     | Regenerates the CSV files in `docs/generated` in a PowerShell terminal           |

The two script configurations call `java` from the `PATH` and run in `pwsh.exe`.

## Manual Setup

These settings are not committed and must be set once per working copy:

- Project SDK: a Java 27 JDK (Eclipse Temurin 27); language level 27.
- Gradle: open the project as a Gradle project and keep building and running with Gradle (the default). The Gradle
  build uses a Java 27 toolchain.
- Inspections: select the profile "Project Default" (stored in the project) in Settings > Editor > Inspections.
- Code style (Settings > Editor > Code Style), with the IDE scheme "Default"; `.editorconfig` overrides the scheme:
    - General: line separator "System-Dependent", hard wrap at 120 columns, "Wrap on typing" off, "Detect and use
      existing file indents for editing" off, "Enable EditorConfig support" on.
    - Formatter: do not format `*.md`, because the IntelliJ formatter joined wrapped lines that start with special
      characters into very long lines; formatter markers in code comments off.
- Terminal (Settings > Tools > Terminal): set the environment variable `JAVA_HOME` to the Java 27 JDK (e.g.,
  `JAVA_HOME=<path to jdk-27>`), so that the Gradle Wrapper in the IntelliJ terminal uses it.
- File encodings: UTF-8 for project files and for `.properties` files.
- Line endings: Git converts the working copy to CRLF (`core.autocrlf=true`); keep the line separator of existing
  files.

## Deliberate Choices

- `.editorconfig` sets the import-on-demand thresholds to 999, so that IntelliJ does not replace single imports with
  `*` imports.
- The inspection `IllegalDependencyOnInternalPackage` is disabled for the scope "Tests". Gradle compiles and runs the
  tests patched into the application module (`--patch-module`); IntelliJ treats test sources as outside the module
  and would otherwise report false module dependency errors.

## Changing Shared Settings

1. Change the setting in IntelliJ (or the `.run` configuration with "Store as project file").
2. Check the diff: commit only the shared files listed above and no unrelated IDE changes.
3. Add project-specific words to the project dictionary, not to the personal one.

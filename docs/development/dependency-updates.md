# Dependency Updates

How to update Gradle, Java, JavaFX, and the libraries and plugins of the build, and where their versions appear. The
project uses the latest stable versions whenever possible. Dependency updates are part of the maintainer workflow (see
"Maintainer Workflow" in `AGENTS.md`). The commands use PowerShell on Windows; on Linux, use `./gradlew` instead of
`.\gradlew.bat`.

## General Rules

- Use only stable releases: no release candidates, milestones, early-access builds, or snapshots.
- A new major version of Gradle needs a confirmation by the maintainer.
- Update one component per commit (Java and JavaFX together); the commit message names the new version.
- After the update, search for the old version with `git grep -n "<old version>"` and update every remaining
  reference. Ignore unrelated matches and `docs/generated`.
- Keep the technology table in `README.md` and the versions in `THIRD-PARTY-LICENSES` in sync with the build.
- Check with the new version: `app:test`, `app:skillTest`, `app:build`, and `app:javadoc`.

## Gradle

1. Determine the target version at https://services.gradle.org/versions/current (or https://gradle.org/releases/).
2. Get the SHA-256 checksum of the `-bin` distribution from
   `https://services.gradle.org/distributions/gradle-<version>-bin.zip.sha256`.
3. Run the `wrapper` task twice from the repository root:
   `.\gradlew.bat wrapper --gradle-version <version> --gradle-distribution-sha256-sum <sha256>`.
   The first run updates `distributionUrl`, but regenerates the scripts and the JAR from the templates of the old
   Gradle version; the second run regenerates them with the new version.
4. Verify that the SHA-256 checksum of `gradle/wrapper/gradle-wrapper.jar` matches
   `https://services.gradle.org/distributions/gradle-<version>-wrapper.jar.sha256`.
5. Update the version in `README.md` (technology table) and in `THIRD-PARTY-LICENSES` (version and license URL
   `https://github.com/gradle/gradle/blob/v<version>/LICENSE`).
6. For a minor or major release (not for a patch release), check the release notes for changes to the Java versions
   that can run Gradle. The supported range ("Java 17 to 27") appears in `README.md` (Prerequisites) and
   `docs/development/development-setup.md` ("Common Prerequisites").

The `wrapper` task updates `gradle/wrapper/gradle-wrapper.properties` and `gradle/wrapper/gradle-wrapper.jar`, and
`gradlew` and `gradlew.bat` if their templates changed.

If the optional plugin `gradle-skills` is installed (see
[Development Setup](development-setup.md#optional-plugin-gradle-skills)), steps 1 to 3 may be done with the skill
`gradle-skills:gradle-wrapper-upgrade` (e.g., "Upgrade the Gradle wrapper to `<version>`"). Steps 4 to 6 and the
checks of "General Rules" remain required.

## Java and JavaFX

Java and JavaFX share the major version and are updated together. Gradle must support the new Java version as a
toolchain (see the [compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html)); update
Gradle first if necessary.

Java version references:

- `app/build.gradle.kts`: toolchain `JavaLanguageVersion.of(<version>)`.
- `AGENTS.md`: introduction and "Platform Baseline".
- `.claude/rules/java.md`: modern Java APIs.
- `.claude/skills/i18n-consistency/SKILL.md` and `.claude/skills/java-code-inventory/SKILL.md`: requirements of the
  single-file source programs. A new JDK may deprecate APIs these programs use; `app:skillTest` covers them.
- `README.md`: badge, Prerequisites (toolchain and upper bound of the range for running Gradle), the runtime of the
  distribution ZIP, and the technology table.
- `docs/development/development-setup.md`: "Common Prerequisites" (Java for the Gradle Wrapper) and "Manual Setup" of
  IntelliJ IDEA (Project SDK, Gradle).

JavaFX version references:

- `gradle/libs.versions.toml`: `javafx`.
- `AGENTS.md`: introduction and "Platform Baseline".
- `README.md`: badge and technology table.
- `THIRD-PARTY-LICENSES`: version and the `<version>-ga` links to the source code and to `jpeg_fx.md`.

The distribution ZIP ships the JavaFX JARs together with the license texts in `licenses`. Compare these texts and the
notes on JavaFX in `THIRD-PARTY-LICENSES` (bundled native libraries) with the legal files of the new JavaFX release.

## Libraries and Plugins

- JSpecify and JUnit: version in `gradle/libs.versions.toml`, version in `THIRD-PARTY-LICENSES`.
- Foojay toolchain resolver plugin: version in `settings.gradle.kts`; not listed in `THIRD-PARTY-LICENSES`.

## Dependabot

The repository has Dependabot alerts, malware alerts, and Dependabot security updates enabled. For a dependency
with a known vulnerability, Dependabot opens an alert on the tab "Security" and a pull request with the label
`dependencies`. Do not merge such a pull request as is: apply the update by the rules above (including `README.md` and
`THIRD-PARTY-LICENSES`) and close the pull request.

GitHub does not detect the Gradle dependencies from the build files. The repository setting "Automatic dependency
submission" (Settings, Advanced Security) is enabled: on every push to `main`, GitHub runs a Gradle build in its own
workflow run and submits the resolved dependencies, including transitive ones, to the dependency graph. Alerts and
security updates rely on these submitted dependencies.

`.github/dependabot.yml` enables monthly version updates with the label `dependencies`:

- `github-actions`: a pull request updates the commit SHA and the version comment of an action in
  `.github/workflows`. Check the diff and the release notes of the action, start a CI run on the branch of the pull
  request (`gh workflow run ci.yml --ref <branch>`), and merge the pull request when both jobs pass.
- `gradle`: a pull request reports a new version of Gradle, a plugin, or a library. Do not merge it as is: apply the
  update by the rules above and close the pull request, as for security updates.

Secret scanning with push protection and private vulnerability reporting are enabled as well; `SECURITY.md` describes
how to report a vulnerability.

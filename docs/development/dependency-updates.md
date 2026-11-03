# Dependency Updates

How to update Gradle, Java, JavaFX, and the libraries and plugins of the build, and where their versions appear. The
project uses the latest stable versions whenever possible.

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
6. Check the release notes for changes to the Java versions that can run Gradle. The supported range ("Java 17 to 27")
   appears in `README.md` (Prerequisites) and `docs/development/intellij-setup.md` (Terminal).

The `wrapper` task updates `gradle/wrapper/gradle-wrapper.properties` and `gradle/wrapper/gradle-wrapper.jar`, and
`gradlew` and `gradlew.bat` if their templates changed. It writes the properties file with LF line endings; Git warns
about it and converts it to CRLF on the next checkout.

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
- `docs/development/intellij-setup.md`: "Manual Setup" (Project SDK, Gradle, Terminal).

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

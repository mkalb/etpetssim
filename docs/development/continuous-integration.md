# Continuous Integration

The GitHub Actions workflow `.github/workflows/ci.yml` builds and tests the committed state of a branch on Windows x64
and Linux x64. It detects build and test failures independently of a local working tree, which may differ from the
committed state.

## Trigger

The workflow runs only when started manually (`workflow_dispatch`); there are no automatic triggers on push or pull
request. It can be started for any branch, usually `main`. GitHub lists the workflow only while the file is on the
default branch.

## Jobs

The job `build` runs as a matrix on `windows-latest` and `ubuntu-latest`. A failure on one platform does not cancel the
other (`fail-fast: false`), and each job is stopped after 30 minutes.

Each job runs these steps:

1. Check out the full history with tags (`fetch-depth: 0`); the version is derived with `git describe` (see
   [Versioning and Releases](versioning-and-releases.md)). The token is not stored in the Git config
   (`persist-credentials: false`), because the build runs no authenticated Git commands.
2. Set up Eclipse Temurin JDK 27 with `actions/setup-java`. It matches the Gradle toolchain (Java 27, `ADOPTIUM`) and
   also runs the Gradle Wrapper.
3. Set up Gradle with `gradle/actions/setup-gradle`: it validates the checksum of the Gradle Wrapper JAR and caches the
   Gradle User Home.
4. Run the Gradle Wrapper once with `-Porg.gradle.java.installations.auto-download=false`, so that Gradle uses the JDK
   from step 2 and fails instead of downloading a JDK:

   | Job              | Gradle tasks                               |
   |------------------|--------------------------------------------|
   | `windows-latest` | `app:test`, `app:skillTest`, `app:distZip` |
   | `ubuntu-latest`  | `app:test`, `app:skillTest`, `app:javadoc` |

   Windows x64 is the supported distribution platform, so only the Windows job builds the distribution ZIP. Javadoc
   is platform-independent and runs once, on Linux. The JavaFX tests run on the headless Glass platform
   (`-Dglass.platform=headless`, `-Dprism.order=sw`), without a virtual display.

   The Windows job runs Gradle with `--no-daemon`. A daemon that keeps running after the build holds lock files in
   the Gradle User Home, which `setup-gradle` cannot read on Windows when it saves the cache. On Linux, the daemon
   does not block saving the cache.
5. Windows only: print the name of the ZIP in `app/build/distributions` and fail if its version contains `-dirty`,
   which means that the build modified tracked files.
6. Only if the job fails: upload the test reports as an artifact (see "Test Reports").

The workflow token has only the permission `contents: read`. The distribution ZIP is not uploaded; release ZIPs are
built from the tagged commit.

## Starting a Run and Reading the Result

Start a run in the GitHub web UI (tab "Actions", workflow "CI", "Run workflow", select the branch) or with the GitHub
CLI:

```shell
gh workflow run ci.yml --ref main
```

Read the result in the web UI or with the GitHub CLI:

```shell
gh run list --workflow ci.yml
gh run view <run-id>
gh run view <run-id> --log-failed
```

The summary of each job also lists the Gradle builds and their outcome.

## Test Reports

If a job fails, it uploads `app/build/reports/tests` (the HTML reports of `test` and `skillTest`) as the artifact
`test-reports-<os>` (e.g., `test-reports-ubuntu-latest`), kept for 7 days. Download it from the run page in the web UI
or with `gh run download <run-id> --name test-reports-<os>`.

## Caches

`setup-gradle` uses the open-source cache provider `basic` (`cache-provider: basic`), which stores the directories
`caches` and `wrapper` of the Gradle User Home with `actions/cache`. The default provider `enhanced` is not used,
because it is a proprietary component with separate terms of use.

- The cache key is a hash of the Gradle build files (e.g., `*.gradle.kts`, `gradle/libs.versions.toml`, and
  `gradle-wrapper.properties`) per operating system. After a change to these files, the next run starts with an empty
  cache and downloads all dependencies again. An existing cache entry is not overwritten, so it keeps the state of the
  first run that saved it.
- Runs on the default branch save the cache; runs on other branches only read it.
- The Gradle User Home contains the local build cache (`org.gradle.caching=true` in `gradle.properties`). A task whose
  inputs match a result in the restored cache is not run, but takes the result from the cache (`FROM-CACHE`); this
  applies to `test`, `skillTest`, and `javadoc` as well and is accepted, because the result is the same.
- The configuration cache is stored in the project directory and is not restored between runs.

The settings of `gradle.properties` are not changed for the workflow.

## Updating the Actions

Every action is pinned to the full-length commit SHA of a release, with the version as a comment, e.g.
`uses: actions/checkout@<sha> # v7.0.1`. A tag can be moved to other code; a commit SHA cannot. Dependabot opens
monthly pull requests that update the SHAs (see "Dependabot" in [Dependency Updates](dependency-updates.md#dependabot)).
To update a SHA manually:

1. Determine the latest release of the action, e.g., `gh release view --repo actions/checkout --json tagName`, and read
   its release notes, especially for a new major version. `gradle/actions/setup-gradle` is released in the repository
   `gradle/actions`.
2. Read the commit SHA of the release tag, e.g., `gh api repos/actions/checkout/commits/v7.0.1 --jq .sha`.
3. Replace the SHA and the version comment in `.github/workflows/ci.yml`.
4. Start a run and check that both jobs pass.

The repository settings (Settings, Actions, General, "Actions permissions") restrict the actions that workflows may
use: only actions owned by `mkalb` or created by GitHub, plus the explicitly allowed `gradle/actions/setup-gradle@*`,
and every action must be pinned to a full-length commit SHA. A run that uses any other action fails with a startup
failure before a job starts; allow a new action in these settings before using it. A new release of
`gradle/actions/setup-gradle` needs no change there, because the entry allows every version.

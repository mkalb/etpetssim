# Versioning and Releases

The Git tag is the single source of truth for the project version. Never edit the version in the build files.

## Tags

- Format: `vMAJOR.MINOR.PATCH` with an optional pre-release suffix, following [SemVer](https://semver.org/)
  (e.g. `v0.1.0`, `v1.0.0-rc.1`).
- Tags are annotated and created on the merge commit in `main`.
- `v1.0.0` is reserved for the first official 1.0 release.

## Versions

The build derives the version with `git describe` from the latest reachable tag matching `v[0-9]*`:

| State                       | Version                    | Example                |
|-----------------------------|----------------------------|------------------------|
| Tagged commit               | Tag without `v`            | `0.1.0`, `1.0.0-rc.1`  |
| Commits after a tag         | Next patch + `-SNAPSHOT`   | `0.1.1-SNAPSHOT`       |
| Commits after a pre-release | Core version + `-SNAPSHOT` | `1.0.0-SNAPSHOT`       |
| Uncommitted tracked changes | Above + `-dirty`           | `0.1.1-SNAPSHOT-dirty` |

Only a version without `-SNAPSHOT` and `-dirty` is a release version.

The build fails with an error if Git is unavailable, no matching tag is reachable (shallow clones may lack tags: fetch
with `git fetch --tags`), or the latest matching tag is not valid SemVer. Only tasks that need the version
(e.g. `jar`, `distZip`) require Git.

## Creating a Release

1. Merge all changes into `main` and check out `main` with a clean working tree.
2. Run `gradlew.bat app:test` and make sure it passes.
3. Create the annotated tag on the current `main` commit: `git tag -a v0.1.0 -m "Release 0.1.0"`.
4. Verify the version: `gradlew.bat app:distZip` must produce `ExtraterrestrialPetsSimulation-0.1.0.zip`.
5. Publish the tag: `git push origin v0.1.0`.

Never move or delete a pushed tag. If a release is faulty, fix it and create a new tag with a higher version.

## Rules for Claude

- Create or push a tag only on explicit request.
- Run the checks above first and ask for confirmation before `git push`.

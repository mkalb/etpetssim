---
name: i18n-consistency
description: 'Checks the production bundles app/src/main/resources/i18n/messages_en_US.properties and messages_de_DE.properties for key parity, alphabetical ordering, = column alignment, placeholder (%) count parity, and unwanted \uXXXX Unicode escapes; optional auto-fix for sorting, = column alignment, and escape-to-UTF-8 conversion. Use when someone wants to check or clean up localization/i18n.'
argument-hint: "[report|fix]"
---

# i18n Consistency

## How to run (do this first)

Run exactly one command from the repository root. Do not open, read, or compare
the `.properties` files yourself first — the helper performs the full analysis.

```powershell
java .github/skills/i18n-consistency/I18nConsistencyCheck.java report
```

`report` is the default, read-only mode. Requirements: Java 26+ on the `PATH`.
No compilation, Gradle, or `JAVA_HOME` setup is needed.

## Interpreting the result

The helper prints findings grouped by rule and ends with an `Overall:` verdict.
Determine the result **only** from the printed `Overall:` line. Do **not** read,
evaluate, rely on, or report the process exit code — it is unreliable in some
terminals and must be ignored.

| Verdict | Meaning                                                                                           |
|---------|---------------------------------------------------------------------------------------------------|
| `PASS`  | Rule satisfied.                                                                                   |
| `WARN`  | Convention deviation: alphabetical ordering, `=` column alignment, or `\uXXXX` Unicode escapes.   |
| `FAIL`  | Hard consistency violation: key parity or placeholder (`%`) count parity between the two bundles. |

A `WARN` or `FAIL` verdict is an **expected finding, not a skill execution
error**. Read it off the printed output and report it.

Keys ending in `.url` are exempt from the key-parity and placeholder-count
checks: their values are locale-specific links that may use different `%`
URL-encoding and may exist in only one bundle.

## After running

- Report the findings exactly as printed.
- If the verdict is `PASS`, stop here. Do not ask about or run `fix`.
- If the output contains only non-fixable `FAIL` findings (`key parity` or
  `placeholder count`) and no fixable `WARN` findings, stop here. Do **not** ask
  about or run `fix`; explain that these findings must be resolved manually.
- If the output contains fixable `WARN` findings (`alphabetical ordering`,
  `= alignment`, or `Unicode escapes`), **ask the user one explicit question**
  before doing anything else: whether the auto-`fix` should be run for those
  fixable findings. Offer the two choices "Run fix" and "Do not run fix". Wait
  for the answer.
    - Only run `fix` if the user clearly confirms (for example by choosing
      "Run fix"). A bare "fix" reply counts as confirmation **only** in answer to
      this question.
    - If the user declines or does not confirm, do nothing further.
- **Never edit, sort, re-align, or otherwise change the `.properties` files
  yourself.** The only allowed way to change them is by invoking the helper in
  `fix` mode (see below). If you cannot run the helper, report that and stop —
  do not hand-edit the bundles.
- `fix` only sorts entries, re-aligns the `=` column, and converts `\uXXXX`
  escapes to UTF-8 characters. It never adds keys, removes keys, or changes
  placeholders or translations, so it **cannot** resolve key-parity or
  placeholder-count `FAIL` findings.

## Fix mode (only after explicit user confirmation)

Run this **only** after the user has confirmed it in answer to the question
above. Run exactly this command; do not change the files in any other way.

```powershell
java .github/skills/i18n-consistency/I18nConsistencyCheck.java fix
```

Fix mode writes only the two production bundles, then re-runs the report so any
remaining `FAIL` findings stay visible. As with `report`, judge the result from
the printed `Overall:` line only and ignore the exit code.

## Scope

Only these two production bundles are checked:

- `app/src/main/resources/i18n/messages_en_US.properties`
- `app/src/main/resources/i18n/messages_de_DE.properties`

Java constants, unused keys, string literals, and test bundles are out of scope.

The deterministic work is done by the Java 26 single-file source program
[I18nConsistencyCheck.java](./I18nConsistencyCheck.java).
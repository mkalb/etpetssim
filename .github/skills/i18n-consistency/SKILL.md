---
name: i18n-consistency
description: 'Checks the production bundles app/src/main/resources/i18n/messages_en_US.properties and messages_de_DE.properties for UTF-8 encoding without a byte order mark, invisible/non-printable characters anywhere in the file, consistent LF or CRLF line endings with a single trailing newline, key parity, alphabetical ordering, = column alignment, placeholder (%) count parity, and unwanted \uXXXX Unicode escapes; optional auto-fix for stripping a BOM/invisible characters, normalizing line endings, sorting, = column alignment, and escape-to-UTF-8 conversion. Use when someone wants to check or clean up localization/i18n.'
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

| Verdict | Meaning                                                                                                                                             |
|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PASS`  | Rule satisfied.                                                                                                                                     |
| `WARN`  | Convention deviation: alphabetical ordering, `=` column alignment, or `\uXXXX` Unicode escapes.                                                    |
| `FAIL`  | Hard consistency violation: UTF-8 BOM, an invisible character anywhere in the file, mixed or missing line endings, a missing or extra trailing newline, key parity, or placeholder (`%`) count parity between the two bundles. Of these, UTF-8 BOM, invisible characters, line ending consistency, and trailing newline are fixable via `fix`; UTF-8 encoding, key parity, and placeholder count are not. |

A `WARN` or `FAIL` verdict is an **expected finding, not a skill execution
error**. Read it off the printed output and report it.

The helper checks encoding first: each bundle must be valid UTF-8 without a
byte order mark (BOM), and it scans every character in the file for invisible
or non-printable characters (control, format, and non-regular-space Unicode
categories; the plain space U+0020 is exempt). A leading BOM is reported only
by the dedicated `UTF-8 BOM` rule, not again as an invisible character.

After those checks, key parity, ordering, alignment, placeholder counts, and
Unicode escapes are analyzed on a cleaned in-memory view with the BOM and
invisible characters removed. This prevents duplicate or misleading follow-on
findings without modifying either bundle in `report` mode. `fix` uses the same
cleaned view when formatting the files.

It also checks that each bundle uses one consistent line ending throughout
(either all LF or all CRLF, never a mix) and ends with exactly one trailing
line break (no missing trailing newline, no extra trailing blank lines).

Alphabetical ordering is case-insensitive as the primary sort key, with a
case-sensitive tiebreak for keys that differ only by letter case.

Keys ending in `.url` are exempt from the key-parity and placeholder-count
checks: their values are locale-specific links that may use different `%`
URL-encoding and may exist in only one bundle.

## After running

- Report the findings exactly as printed.
- If the verdict is `PASS`, stop here. Do not ask about or run `fix`.
- If the output contains only non-fixable `FAIL` findings (`UTF-8 encoding`,
  `key parity`, or `placeholder count`) and no fixable `WARN` or fixable
  `FAIL` findings, stop here. Do **not** ask about or run `fix`; explain that
  these findings must be resolved manually.
- If the output contains fixable `WARN` findings (`alphabetical ordering`,
  `= alignment`, or `Unicode escapes`) or fixable `FAIL` findings (`UTF-8 BOM`,
  `invisible characters`, `line ending consistency`, or `trailing newline`),
  **ask the user one explicit question** before doing anything else: whether
  the auto-`fix` should be run for those fixable findings. Offer the two
  choices "Run fix" and "Do not run fix". Wait for the answer.
    - Only run `fix` if the user clearly confirms (for example by choosing
      "Run fix"). A bare "fix" reply counts as confirmation **only** in answer to
      this question.
    - If the user declines or does not confirm, do nothing further.
- **Never edit, sort, re-align, or otherwise change the `.properties` files
  yourself.** The only allowed way to change them is by invoking the helper in
  `fix` mode (see below). If you cannot run the helper, report that and stop —
  do not hand-edit the bundles.
- `fix` strips a UTF-8 BOM, removes invisible/non-printable characters (except
  regular spaces), normalizes line endings to one consistent style with
  exactly one trailing newline (CRLF is used when the file contains any CRLF,
  otherwise LF), sorts entries, re-aligns the `=` column, and
  converts `\uXXXX` escapes in values to UTF-8 characters. It may remove
  comments and blank lines while rewriting the entries. It does not convert
  `\uXXXX` escapes in keys. It never adds keys, removes keys, or changes
  placeholders or translations, so it **cannot** resolve UTF-8 encoding,
  key-parity, or placeholder-count `FAIL` findings.

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

---
name: i18n-consistency
description: 'Checks the production bundles app/src/main/resources/i18n/messages_en_US.properties and messages_de_DE.properties for UTF-8 encoding without a byte order mark, invisible/non-printable characters, consistent LF or CRLF line endings with a single trailing newline, valid Java properties syntax, non-empty content, unique decoded keys, key parity, alphabetical ordering, = column alignment, decoded trailing whitespace, placeholder (%) count parity, and unwanted \uXXXX Unicode escapes in values or keys; optional auto-fix for stripping a BOM/literal invisible characters, normalizing line endings, semantic canonicalization, sorting, = column alignment, and safe escape conversion. Use when someone wants to check or clean up localization/i18n.'
argument-hint: "[report|fix]"
---

# i18n Consistency

## How to run (do this first)

Run exactly one command from the repository root. Do not open, read, or compare
the `.properties` files yourself first — the helper performs the full analysis.

```powershell
java .github/skills/i18n-consistency/I18nConsistencyCheck.java report
```

`report` is the default, read-only mode. Requirements: Java 26 on the `PATH`.
No compilation, Gradle, or `JAVA_HOME` setup is needed.

## Interpreting the result

The helper prints findings grouped by rule and ends with an `Overall:` verdict.
Determine the result **only** from the printed `Overall:` line. Do **not** read,
evaluate, rely on, or report the observed process exit code as the verdict —
terminal integrations may misreport it.

| Verdict | Meaning                                                                                                                                                                                                                                                                                                                                                                         |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `PASS`  | Rule satisfied.                                                                                                                                                                                                                                                                                                                                                                 |
| `WARN`  | Convention deviation: alphabetical ordering, `=` column alignment, decoded trailing whitespace in a value, or `\uXXXX` Unicode escapes in values or keys. Trailing whitespace is not changed by `fix`; safe value and key escapes are eligible for conversion.                                                                                                                  |
| `FAIL`  | Hard consistency violation: UTF-8 BOM or encoding, invisible characters, inconsistent/unsupported/missing line endings, an invalid trailing newline, malformed properties syntax, duplicate decoded keys, an empty bundle, key parity, or placeholder (`%`) count parity. Syntax, duplicates, empty bundles, UTF-8 encoding, key parity, and placeholder count are not fixable. |

The helper uses this stable exit-code contract: `0` for `PASS`, `1` for `WARN`,
`2` for a successfully produced report with `Overall: FAIL`, `3` for usage
errors, and `4` for I/O or unexpected execution errors. These codes describe
the helper contract, but the printed `Overall:` line remains authoritative for
the agent workflow because terminal wrappers can surface a different code.

A `WARN` or `FAIL` verdict is an **expected finding, not a skill execution
error**. Read it off the printed output and report it.

The helper checks encoding first: each bundle must be valid UTF-8 without a
byte order mark (BOM), and it scans every character in the file for invisible
or non-printable characters (control, format, and non-regular-space Unicode
categories; the plain space U+0020 is exempt). A leading BOM is reported only
by the dedicated `UTF-8 BOM` rule, not again as an invisible character.

The parser accepts Java properties syntax, including `=`, `:`, and whitespace
separators, escaped key separators, comments, blank lines, and continued logical
entries. It retains source positions while using `Properties.load` for decoded
key/value semantics. Malformed Unicode escapes or other malformed logical
entries are `properties syntax` failures. Duplicate decoded keys name both
source lines and are failures; a bundle with no semantic entries is also a
failure. All report analyses use the decoded source semantics without changing
either bundle.

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
- If the output contains only non-fixable findings — `FAIL` findings for
  `UTF-8 encoding`, `properties syntax`, `duplicate keys`, `empty bundle`,
  `key parity`, or `placeholder count`, and/or `WARN` findings for `trailing
  whitespace` — stop here. Do **not** ask about or run `fix`; explain that these
  findings must be resolved manually.
- If the output contains fixable `WARN` findings (`alphabetical ordering`,
  `= alignment`, or safe `Unicode escapes` in values or keys) or fixable
  `FAIL` findings (`UTF-8 BOM`, `invisible characters`, `line ending
  consistency`, or `trailing newline`), **ask the user one explicit question**
  before doing anything else: whether the auto-`fix` should be run for those
  fixable findings. Offer the two choices "Run fix" and "Do not run fix". Wait
  for the answer. The `trailing whitespace` warning is non-fixable. Unicode
  escapes that must remain escaped for safe properties syntax can remain after
  `fix`.
    - Only run `fix` if the user clearly confirms (for example by choosing
      "Run fix"). A bare "fix" reply counts as confirmation **only** in answer to
      this question.
    - If the user declines or does not confirm, do nothing further.
- **Never edit, sort, re-align, or otherwise change the `.properties` files
  yourself.** The only allowed way to change them is by invoking the helper in
  `fix` mode (see below). If you cannot run the helper, report that and stop —
  do not hand-edit the bundles.
- `fix` strips a UTF-8 BOM, removes invisible/non-printable characters (except
  regular spaces) that are literally present in decoded keys or values,
  normalizes line endings to one consistent style with exactly one trailing
  newline (CRLF is used when the file contains any CRLF, otherwise LF), sorts
  entries, re-aligns the `=` column, and converts only unescaped, valid Unicode
  scalar escapes in values or keys that are safe to materialize as UTF-8.
  Unsafe control, invisible, malformed-surrogate, and escaped-backslash
  sequences are retained safely.
- `fix` semantically canonicalizes accepted Java properties syntax to one
  `key = value` entry per physical line. It may remove comments and blank lines
  and rewrite separators or escapes, but validates that the rendered bundle has
  the same semantic entry count and sanitized decoded key/value pairs. Removing
  literal invisible characters is the only intentional semantic cleanup, and
  the per-bundle fix result reports the exact removal count. If sanitization
  would create a duplicate key or rendered semantics cannot be preserved, the
  operation fails before commit.
- `fix` validates and renders both bundles before writing either one. It writes
  temporary files beside the destinations, then replaces both destinations. If
  replacement fails after one file changed, it attempts to restore every
  replaced file from the original bytes and reports incomplete restoration.
  Two filesystem replacements cannot be fully atomic, so this is a best-effort
  two-file commit. Parse, validation, rendering, and encoding failures leave
  both originals byte-for-byte unchanged.
- The helper blocks `fix` for malformed syntax, duplicate keys, and empty
  bundles, but it does not block writes for key-parity or placeholder-count
  failures, trailing-whitespace warnings, or Unicode-escape warnings. If
  invoked directly, it may therefore rewrite canonical source even when only
  non-fixable findings exist. The agent workflow above must not invoke `fix`
  when only non-fixable findings are present. Any remaining findings stay
  visible in the post-fix report.

## Fix mode (only after explicit user confirmation)

Run this **only** after the user has confirmed it in answer to the question
above. Run exactly this command; do not change the files in any other way.

```powershell
java .github/skills/i18n-consistency/I18nConsistencyCheck.java fix
```

Fix mode writes only the two production bundles, then re-runs the report so any
remaining `WARN` or `FAIL` findings stay visible. As with `report`, judge the
result from the printed `Overall:` line rather than the observed exit code.

## Scope

Only these two production bundles are checked:

- `app/src/main/resources/i18n/messages_en_US.properties`
- `app/src/main/resources/i18n/messages_de_DE.properties`

Java constants, unused keys, string literals, and test bundles are out of scope.

The deterministic work is done by the Java 26 single-file source program
[I18nConsistencyCheck.java](./I18nConsistencyCheck.java).

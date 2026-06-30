# Copilot Customization Plan

> Planning and evaluation document for extending the project's GitHub Copilot
> customization. It collects ideas for Copilot-assisted workflows and decides,
> per idea, whether it is best implemented as a **prompt**, a **skill**, or an
> **agent**. This document is evaluation only; no skill or agent files are
> created as part of it.

## 1. Purpose and Scope

The project already uses Copilot **instructions** effectively and has one
**prompt** that has not yet been exercised. The goal now is to gain practical
experience with project-specific **skills** and **agents**.

This document:

- Explains the differences between instructions, prompts, skills, and agents.
- Collects seven concrete ideas for Copilot support.
- Classifies and evaluates each idea against shared criteria.
- Recommends a low-risk pilot and a phased adoption order.

Out of scope: implementing any prompt, skill, or agent files. Those happen in
later sessions once the classifications below are accepted.

## 2. Current State

- `.github/copilot-instructions.md` — repository-wide defaults.
- `.github/instructions/*.instructions.md` — path-scoped rules for `core`,
  `java`, `junit`, `simulations`, and `ui`. These are mature and satisfactory.
- `.github/prompts/create-simulation.prompt.md` — a detailed prompt for adding a
  new simulation. Written but not yet used or tested.

No skills (`.github/skills/`) or custom agents (`.github/agents/`) exist yet.

## 3. Primer: Instructions vs Prompts vs Skills vs Agents

These four mechanisms are complementary, not competing. The table summarizes the
authoritative behavior from the official GitHub Copilot and VS Code
documentation.

| Mechanism         | Format / Location                                                   | Portability                           | Best for                                                                           |
|-------------------|---------------------------------------------------------------------|---------------------------------------|------------------------------------------------------------------------------------|
| **Instructions**  | `.github/instructions/*.instructions.md`, `copilot-instructions.md` | VS Code, GitHub.com, CLI              | Always-on coding standards and conventions                                         |
| **Prompt files**  | `.github/prompts/*.prompt.md`                                       | VS Code-centric                       | One-off, parameterized tasks; no tool restrictions                                 |
| **Agent Skills**  | `.github/skills/<skill-name>/SKILL.md`                              | VS Code + CLI + cloud (open standard) | Portable, reusable capability, optionally with scripts/resources; loaded on-demand |
| **Custom agents** | `.github/agents/<agent-name>.agent.md`                              | VS Code + CLI + cloud                 | Persistent persona with tool restrictions, model choice, handoffs                  |

### Decision rule of thumb

The official guidance is straightforward:

- Use a **prompt** for a one-off task that does not need tool restrictions.
- Use a **skill** for a portable, reusable capability — especially when it can
  carry scripts, templates, or other resources, and should load only when
  relevant.
- Use a **custom agent** when you need a persistent persona with specific tool
  restrictions, a preferred model, or handoffs between roles.

### Implications for this project

- The owner develops mainly in IntelliJ but wants to invest in the **CLI**
  because it is faster. JetBrains support for skills and agents lags behind
  VS Code.
- **Skills** are the safest investment: they are an open standard and run in the
  CLI, in VS Code, and in cloud agents. Most ideas below are therefore proposed
  as skills.
- **Custom agents** are the right tool when a workflow needs a constrained
  persona (read-only planning) or elevated permissions (git write). They also
  run in the CLI.
- The existing prompt remains a valid pattern for the highly parameterized
  "create a new simulation" workflow.

## 4. Master Comparison Table

Effort uses relative T-shirt sizing (S/M/L); no calendar-time estimates are
given, per repository convention.

| # | Idea                             | Recommended | Value    | Complexity | Effort | Interactivity          | Key tool needs             |
|---|----------------------------------|-------------|----------|------------|--------|------------------------|----------------------------|
| 1 | Code-review of changed files     | Agent       | High     | High       | L      | Interactive (approval) | git read/write, file edits |
| 2 | Per-simulation documentation     | Skill       | Med-High | Med        | M      | Mostly autonomous      | read package, write doc    |
| 3 | Refactoring planning             | Agent       | High     | Med        | M-L    | Some user questions    | read-only + write one doc  |
| 4 | Feature brainstorming            | Agent       | Med-High | Med        | M      | Highly interactive     | read-only + write one doc  |
| 5 | i18n consistency check (pilot)   | Skill       | Med      | Low-Med    | S-M    | Autonomous             | read `.properties`, report |
| 6 | Instruction-compliance lint      | Skill       | Med      | Med        | M      | Autonomous             | read source, report        |
| 7 | README / simulation-catalog sync | Skill       | Med      | Med        | M      | Mostly autonomous      | read source, write docs    |

## 5. Grouped Evaluations

Each idea is evaluated on: recommended classification and rationale, value,
complexity, effort (S/M/L), interactivity, tool/permission needs, dependencies,
and risks/open questions.

### 5.1 Prompts

No new prompt is proposed at this time. The existing
`create-simulation.prompt.md` already covers the one strongly parameterized,
one-off workflow. Recommended next step for prompts: actually exercise that
prompt end-to-end once, capture friction, and decide whether parts of it should
migrate into a skill.

### 5.2 Skills

#### Idea 5 — i18n consistency check (recommended pilot)

- **Classification:** Skill. A deterministic, repeatable check that benefits from
  a helper script and clear pass/fail output; ideal for learning the skill
  format with minimal risk.
- **What it does:** Verify the production `messages_*.properties` bundles
  against each other. Version 1 checks key parity, alphabetical ordering, and
  format-placeholder compatibility across locales, and reports Unicode escape
  sequences that violate the UTF-8 convention. It is report-first; an optional
  auto-fix mode may sort and align existing entries and convert Unicode escapes
  to UTF-8 characters.
- **Value:** Med — guards an existing, explicitly stated convention.
- **Complexity:** Low-Med — mostly file parsing and set comparison.
- **Effort:** S-M.
- **Interactivity:** Autonomous; produces a report with `PASS`, `WARN`, and
  `FAIL` findings.
- **Tool/permission needs:** Read `.properties` and source files; no writes
  required for the default report mode. The optional auto-sort mode would add
  writes limited to the production bundle files.
- **Dependencies:** The localization rules in `copilot-instructions.md`.
- **Risks/open questions:** Keep the pilot narrow. Do not include Java constant
  usage, unused-key detection, or string-literal detection in Version 1; those
  require broader Java source analysis and belong in later versions.

##### Version 1 scope

Version 1 compares only the production bundles:

- `app/src/main/resources/i18n/messages_en_US.properties`
- `app/src/main/resources/i18n/messages_de_DE.properties`

It deliberately excludes test bundles and `AppLocalizationKeys` from the first
scope. This keeps the pilot deterministic and focused on the localization files
themselves.

##### Version 1 report rules

| Rule                                         | Severity | Evaluation                                                                                                                        |
|----------------------------------------------|----------|-----------------------------------------------------------------------------------------------------------------------------------|
| Key parity across production locale bundles  | `FAIL`   | High-value and deterministic. Missing or extra keys directly create locale drift.                                                 |
| Alphabetical key ordering inside each bundle | `WARN`   | Repository convention and safe to detect. Warn in report mode because auto-fix can address it.                                    |
| Placeholder compatibility across locales     | `FAIL`   | Prevents runtime formatting defects. Compare Java-style format placeholders such as `%1$s`, `%2$d`, `%1$,.0f`, and escaped `%%`.  |
| Unicode escape sequences in property values  | `WARN`   | The project uses UTF-8 for `.properties` files, so localized characters should be stored directly instead of as `\uXXXX` escapes. |

##### Optional auto-fix rules

Auto-fix must be opt-in and narrower than report mode.

| Rule                                               | Allowed in auto-fix? | Evaluation                                                                       |
|----------------------------------------------------|----------------------|----------------------------------------------------------------------------------|
| Sort existing entries alphabetically               | Yes                  | Safe, deterministic, and convention-preserving.                                  |
| Preserve or normalize key/value column alignment   | Yes                  | Safe when only whitespace before `=` is adjusted.                                |
| Add missing keys with copied or placeholder values | No                   | Risky because it creates untranslated or misleading user-facing text.            |
| Modify placeholders                                | No                   | Risky because placeholder semantics depend on the localized sentence.            |
| Convert Unicode escapes to UTF-8 characters        | Yes                  | Safe for localized `.properties` values because UTF-8 is the project convention. |

##### Later extension candidates

These are useful but intentionally deferred until the pilot has proven the skill
format and workflow:

- Compare production bundles with `AppLocalizationKeys` and report missing or
  surplus constants.
- Detect unused localization keys by scanning Java references.
- Detect direct localization-key string literals and recommend using
  `AppLocalizationKeys`.
- Extend parity checks to test resource bundles after the production-bundle
  workflow is stable.

##### Implementation handoff (Version 1)

This subsection gives the implementing agent everything needed to create the
skill. Scope is limited to the **Version 1 report rules** and **Optional
auto-fix rules** above. The **Later extension candidates** are explicitly out of
scope and must not be implemented now.

**Skill layout**

- Create the skill under `.github/skills/i18n-consistency/`.
- All skill content must be written in **English (en_US)**: the `SKILL.md`
  frontmatter and body, the helper script (including code comments), and any
  report or console output the skill produces. This holds even when the
  implementing request or surrounding conversation is in another language.
- `SKILL.md` describes purpose, when to use, invocation, and how to interpret the
  report; it delegates the deterministic work to the helper script.
- Place the helper script next to `SKILL.md` in the same skill directory.

**Helper script technology**

- Implement the helper as a single-file **Java 26** source program run in
  source-file mode (`java <script>.java <args>`); no compilation step, no Gradle,
  no external dependencies, JDK standard library only.
- Read and write files as UTF-8. Do not emit a BOM.
- Use exit codes to signal severity: `0` = PASS (no findings), `1` = WARN only,
  `2` = at least one FAIL. The auto-fix mode returns `0` when it successfully
  applied fixes and left no FAIL-level findings.

**Bundle ground truth (verified against the current repository)**

- Files in scope:
    - `app/src/main/resources/i18n/messages_en_US.properties` (reference locale)
    - `app/src/main/resources/i18n/messages_de_DE.properties`
- Encoding: UTF-8, no BOM. Localized non-ASCII characters are stored directly
  (no `\uXXXX` escapes currently present).
- No comment lines (`#`/`!`) and no blank lines exist; one `key = value` entry
  per line.
- Separator/alignment format: each key is left-padded with spaces so the `=`
  aligns to a common column, followed by `= ` then the value
  (`key<spaces>= value`). The current alignment column is one space past the
  longest key; auto-fix must recompute this column from the longest key after
  sorting rather than hardcoding it.
- en_US is currently alphabetically sorted; de_DE follows the same order.

**Rule algorithms (Version 1)**

- Key parity (`FAIL`): parse keys from both bundles; report keys present in one
  bundle but missing in the other, in both directions.
- Alphabetical ordering (`WARN`): for each bundle, report keys that are out of
  ascending order. Use locale-stable comparison (`Locale.ROOT`).
- Placeholder compatibility (`FAIL`): for each shared key, extract the
  Java-format placeholders (e.g. `%1$s`, `%2$d`, `%1$,.0f`) and the literal `%%`
  escape; report keys whose placeholder multiset differs between locales.
- Unicode escapes (`WARN`): report any value containing a `\uXXXX` escape, per
  bundle and key, because UTF-8 storage is the convention.

**Auto-fix behavior (opt-in only)**

- Default invocation is report-only and performs no writes.
- Auto-fix mode applies only the rules marked `Yes` in the auto-fix table: sort
  entries alphabetically, recompute and normalize `=` column alignment, and
  convert `\uXXXX` escapes to UTF-8 characters.
- Auto-fix must never add or remove keys and must never alter placeholders or
  translated text. After writing, it re-runs the report so any remaining
  `FAIL`-level findings (e.g. key parity) stay visible.

**Report output**

- Produce a human-readable summary grouped by rule, each finding tagged `PASS`,
  `WARN`, or `FAIL`, naming the bundle, key, and the locale(s) involved.
- End with an overall verdict line reflecting the highest severity present.

**Acceptance criteria**

- Running report mode against the current repository must:
    - Report a `FAIL` for key parity because `simulation.forest.url` and
      `simulation.sugar.url` exist only in `messages_en_US.properties`.
    - Report no ordering `WARN` (both bundles are currently sorted).
    - Report no Unicode-escape `WARN` (no escapes currently present).
    - Report placeholder findings only where locales genuinely differ.
- Auto-fix mode on already-sorted, escape-free bundles must produce no content
  changes (idempotent) and must not touch the unresolved key-parity gap.

#### Idea 6 — Instruction-compliance lint

- **Classification:** Skill. A reusable, on-demand capability that can carry a
  checklist resource derived from the instruction files.
- **What it does:** Check a given file or package against repository rules
  (Java 26 features only, JSpecify nullability, AppLogger usage, DRY/KISS,
  naming, UTF-8/ASCII) and report violations.
- **Value:** Med — complements the always-on instructions with an explicit audit
  pass.
- **Complexity:** Med — many rules are heuristic, not mechanically checkable.
- **Effort:** M.
- **Interactivity:** Autonomous; produces a rated report.
- **Tool/permission needs:** Read source; report only (no writes).
- **Dependencies:** All `.github/instructions/*.instructions.md` files.
- **Risks/open questions:** Overlaps with always-on instructions; keep the skill
  focused on an explicit, requestable audit rather than duplicating rules.
  Decide which rules are script-checkable vs. model-judged.

#### Idea 2 — Per-simulation documentation

- **Classification:** Skill. A repeatable, parameterized task (input: which
  simulation) that can carry a documentation template as a resource and loads
  only when relevant.
- **What it does:** Generate or update documentation for one simulation from the
  Java code of that simulation's package; create the doc if missing, update it if
  present.
- **Value:** Med-High — keeps `docs/simulations/*` aligned with the code.
- **Complexity:** Med — requires reading and summarizing a whole package
  accurately.
- **Effort:** M.
- **Interactivity:** Mostly autonomous; may confirm the target simulation.
- **Tool/permission needs:** Read the simulation package; write one Markdown doc.
- **Dependencies:** `simulations.instructions.md`; the existing
  `docs/simulations/Simulation_Entity_Catalog.md` style.
- **Risks/open questions:** Define the canonical doc structure and where output
  lives (per-simulation file vs. a section in the catalog).

#### Idea 7 — README / simulation-catalog sync

- **Classification:** Skill. A reusable maintenance task with file writes,
  triggered when simulations change.
- **What it does:** Keep `README.md` and `docs/simulations/*` in sync with the
  set of registered simulations (names, categories, descriptions, links).
- **Value:** Med — reduces drift between code and user-facing catalogs.
- **Complexity:** Med — must discover the registered simulations reliably.
- **Effort:** M.
- **Interactivity:** Mostly autonomous; confirms before overwriting curated text.
- **Tool/permission needs:** Read source/registration; write README and catalog
  docs.
- **Dependencies:** Simulation registration conventions; partially overlaps with
  Idea 2 (could share a discovery helper).
- **Risks/open questions:** Avoid clobbering hand-written prose; define which
  sections are machine-managed.

### 5.3 Agents

#### Idea 3 — Refactoring planning

- **Classification:** Custom agent. A read-only planning persona that should be
  prevented from editing code and only writes a plan document. Tool restriction
  is the deciding factor.
- **What it does:** Analyze the code in depth and produce a planning document
  with the big picture, motivation, and precise, agent-executable step-by-step
  instructions for the refactoring.
- **Value:** High — turns large refactors into reviewable, executable plans.
- **Complexity:** Med — quality depends on analysis depth and step precision.
- **Effort:** M-L.
- **Interactivity:** Some prompts to scope the refactor.
- **Tool/permission needs:** Read-only across the codebase; write a single plan
  document. No source edits.
- **Dependencies:** Existing planning-doc conventions in `docs/planning/`.
- **Risks/open questions:** Overlaps with the CLI's built-in plan mode; the
  custom agent's value is enforcing read-only + the project's plan format and
  step granularity.

#### Idea 4 — Feature brainstorming

- **Classification:** Custom agent. A highly interactive planning/brainstorming
  persona; tool restriction (read-only + write one doc) and a distinct persona
  justify an agent over a prompt.
- **What it does:** Ask the owner for feature ideas, evaluate them, contribute
  the agent's own suggestions, analyze the code, and produce a document with
  agent-executable implementation steps.
- **Value:** Med-High — structured, collaborative ideation with a usable output.
- **Complexity:** Med.
- **Effort:** M.
- **Interactivity:** Highly interactive (collaborative dialogue).
- **Tool/permission needs:** Read-only across the codebase; write one ideas/plan
  document.
- **Dependencies:** `docs/planning/ET_Pets_Roadmap.md` as an input source of
  existing ideas.
- **Risks/open questions:** Close cousin of Idea 3; consider whether they should
  be two agents or one planning agent with a brainstorming and a refactoring
  mode (handoffs).

#### Idea 1 — Code-review of changed files

- **Classification:** Custom agent. Needs a reviewer persona plus the broadest
  permissions (git read/write and source edits), so it is the clearest agent and
  the highest-risk one.
- **What it does:** Inspect `git status`/`git diff`, review the changes and the
  affected files as a whole against the instructions, check whether test classes
  must be adjusted or extended, immediately apply small corrections (e.g.,
  Javadoc and unit-test tweaks), deliver larger findings as a rated list, and —
  only after explicit user approval — run `git add` and `git commit`.
- **Value:** High — a tailored, instructions-aware review-and-commit loop.
- **Complexity:** High — combines review judgment, test-impact analysis,
  safe auto-fixes, and gated git operations.
- **Effort:** L.
- **Interactivity:** Interactive; commit is gated on user approval.
- **Tool/permission needs:** git read and write (add/commit), file edits.
- **Dependencies:** All instruction files; `junit.instructions.md` for the
  test-impact checks; the commit-trailer convention.
- **Risks/open questions:** Highest-risk idea because of git-write and auto-edit
  permissions. The CLI already provides a built-in `/review`; the custom agent's
  value is layering project rules, the test-impact step, scoped auto-fixes, and
  the approval-gated commit. Implement last, after experience with safer skills
  and read-only agents.

## 6. Recommended Pilot and Adoption Order

**Pilot: Idea 5 — i18n consistency check, as a Skill.** It is small, high in
clarity, low in risk (report-only), needs no git access, and is the cleanest way
to learn the `SKILL.md` format and the CLI skill workflow.

Phased adoption, increasing in risk and ambition:

- **Phase 1 — Skills (low risk):** 5 (pilot) -> 6 -> 2 -> 7. Build confidence
  with portable, mostly read-only capabilities.
- **Phase 2 — Read-only planning agents:** 3 -> 4. Learn the `.agent.md` format
  and tool restrictions without code-mutation risk.
- **Phase 3 — Highest risk last:** 1, the code-review agent with git-write and
  auto-fix permissions, once the earlier phases have established trust and
  patterns.

## 7. Open Questions and Next Steps

- Exercise `create-simulation.prompt.md` once to validate the existing prompt and
  decide whether any part should become a skill.
- For Idea 5, implement the accepted Version 1 scope: production-bundle key
  parity, alphabetical ordering, placeholder compatibility, Unicode-escape
  reporting, and opt-in sort/alignment plus Unicode-escape auto-fix. The helper
  is a single-file Java 26 source program (`java <script>.java`); see the
  "Implementation handoff (Version 1)" subsection for the full specification.
- For Ideas 2 and 7, define the canonical per-simulation doc structure and which
  README/catalog sections are machine-managed.
- For Ideas 3 and 4, decide whether they are two agents or one planning agent
  with handoffs between a brainstorming and a refactoring mode.
- Confirm whether skills should live in `.github/skills/` (committed, shared) for
  team and CLI use.

## 8. References

- [Custom agents in VS Code](https://code.visualstudio.com/docs/agent-customization/custom-agents)
- [Use Agent Skills in VS Code](https://code.visualstudio.com/docs/agent-customization/agent-skills)
- [Using GitHub Copilot CLI](https://docs.github.com/en/copilot/how-tos/use-copilot-agents/use-copilot-cli)

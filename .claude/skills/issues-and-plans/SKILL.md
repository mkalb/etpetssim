---
name: issues-and-plans
description: 'Create, edit, refine, link, and review GitHub issues of this repository and planning documents in docs/planning: labels, issue body structure, needs-refinement workflow, sub-issues and dependencies, gh commands, the periodic review of all open issues, and plan files. Use for any work on issues or plans.'
---

# Issues and plans

Rules and procedures for GitHub issues and the planning documents in `docs/planning`. The sections "Issues and Plans"
and "External Contributions" in `AGENTS.md` always apply and are not repeated here.

## Labels

Give each issue exactly one kind label and any number of area and process labels:

| Label              | Group   | Meaning                                                                     |
|--------------------|---------|-----------------------------------------------------------------------------|
| `bug`              | kind    | Something does not work as intended.                                        |
| `enhancement`      | kind    | New functionality, including executable agent tools (skills, hooks).        |
| `refactoring`      | kind    | Code cleanup or restructuring without new functionality.                    |
| `documentation`    | kind    | Documentation, including agent instructions (`AGENTS.md`, rules).           |
| `area-build`       | area    | Gradle build, CI, packaging, and code checks.                               |
| `area-styling`     | area    | JavaFX CSS, layout, and visual design.                                      |
| `area-agents`      | area    | Agent instructions, rules, skills, and hooks.                               |
| `needs-refinement` | process | Not ready for implementation; needs refinement with the maintainer.         |
| `release-1.0`      | process | Required before the first 1.0 release.                                      |

- The GitHub default labels `question`, `duplicate`, `invalid`, `wontfix`, `good first issue`, and `help wanted` keep
  their default meaning. `question` is for questions from outside that expect an answer, not an implementation; if
  one turns into work, retitle it and add `needs-refinement`, or create a new issue.
- Do not use milestones or priority labels. Whether a sub-issue of a `release-1.0` parent also gets `release-1.0` is
  decided per case.

## Issue body

- Make each issue complete enough for another agent to implement it alone and successfully; otherwise, label it
  `needs-refinement`.
- Use these sections in this order and omit those that do not apply:
    - `## Problem`, or `## Background` for an implemented feature whose issue only collects ideas,
    - `## Goal`,
    - `## Scope` or `## Tasks`,
    - `## Acceptance criteria` (required unless the issue is labeled `needs-refinement`),
    - `## Dependencies` (reasons and non-issue prerequisites, on the blocked issue only),
    - `## Related` (loose links, on the issue being created or edited only).
- Open questions that may be decided during implementation go to `## Open questions (decide during implementation)`.
- The issue body is the source of truth (for the details of an issue with a working plan, see "Planning documents"):
  merge additions and decisions from comments into the body. Comments do not have to be deleted. Only when an issue
  is reworked completely with the maintainer (usually during refinement), propose deleting the maintainer's comments
  that are completely merged, and delete them only after confirmation. Never delete external comments.
- Do not repeat labels in the body (e.g., no "Status: Needs refinement" line).
- When the scope of an issue changes, update its title. When the title or labels of an issue you work on do not, or no
  longer, match its content, propose a change to the maintainer.

## Refinement

An issue labeled `needs-refinement` is not implemented. Analyze it, answer the agent questions, and present the results
and the open decisions to the maintainer in the chat.

Structure its open questions as follows:

```markdown
## Open questions

### Analysis (agent)

- [ ] ...

### Decisions (user)

- [ ] ...
```

When all questions are resolved, replace `## Open questions` with a `## Decisions` section that lists each answer and
decision, and remove the `needs-refinement` label.

## Links between issues

Native links ("blocked by" dependencies and sub-issues) are the source of truth for dependencies and hierarchy; the
body only explains them. GitHub shows them in the sidebar ("blocked by", "blocking", parent, sub-issues) and mentions in
the timeline, so do not add back-references by hand.

- **Dependency:** record every prerequisite that is an issue as "blocked by" on GitHub. `## Dependencies` only states
  the reason for a dependency and prerequisites that are not issues; write it on the blocked issue only. The blocking
  issue gets no back-reference, because GitHub shows "blocking" automatically.
- **Sub-issue:** for follow-up work planned by its parent, or for related issues bundled under a parent issue. Track
  it as a GitHub sub-issue instead of a hand-maintained list in the parent body; the parent body lists only planned
  follow-ups that are not created yet.
- **Related:** GitHub has no native "related" link. Loose links go to `## Related`, written only on the issue being
  created or edited; the other issue gets no back-reference, because the GitHub timeline shows the mention.
- **Parent issue:** issue types (e.g., "Epic") are only available for organization repositories, not for this user
  repository; a parent issue with sub-issues takes their place. When about three or more open issues share a common
  goal and have no parent, propose a parent issue that bundles them as sub-issues. It needs no extra label; its kind
  label follows its content, and it has its own acceptance criteria (usually: all sub-issues are done). Examples:
  #23, #24.

## Commands

Run `gh` from the repository root; `{owner}/{repo}` is filled in by `gh api`. Quote API paths in single quotes,
because PowerShell otherwise parses the braces; the commands below work in PowerShell and Bash.

- Read an issue with labels and comments:
  `gh issue view <number> --json number,title,state,labels,body,comments`. Skip comments with `isMinimized`.
- Edit a body: save the current body to a file in the scratchpad (`gh issue view <number> --json body --jq .body`),
  change only the affected parts with a script, preserve unrelated content, check the diff, then run
  `gh issue edit <number> --body-file <file>`.
- Add sub-issues: `gh issue edit <parent> --add-sub-issue <numbers>`. List them:
  `gh api 'repos/{owner}/{repo}/issues/<number>/sub_issues' --jq '[.[].number]'`.
- Add a "blocked by" dependency; it needs the numeric REST `id` of the blocking issue, not its number or GraphQL node
  ID. Read the `id` first, then insert it:

  ```text
  gh api 'repos/{owner}/{repo}/issues/<blocking>' --jq .id
  gh api -X POST 'repos/{owner}/{repo}/issues/<blocked>/dependencies/blocked_by' -F issue_id=<id>
  ```

  List them: `gh api 'repos/{owner}/{repo}/issues/<number>/dependencies/blocked_by' --jq '[.[].number]'`.
- Delete a merged maintainer comment: `gh api -X DELETE 'repos/{owner}/{repo}/issues/comments/<comment-id>'`; get the
  ID from `gh api 'repos/{owner}/{repo}/issues/<number>/comments' --jq '.[] | {id, user: .user.login}'`.

## Review of all open issues

On request, review all open issues (`gh issue list --state open --limit 1000`) and report the findings per issue in the
chat before changing anything:

- labels: exactly one kind label; area and process labels match the content,
- titles: match the current scope,
- body structure: sections as in "Issue body"; acceptance criteria unless `needs-refinement`; no status lines; no
  decisions left only in comments,
- readiness: an issue without `needs-refinement` is complete enough to implement,
- sub-issues and dependencies: used as in "Links between issues"; every issue number in `## Dependencies` has a
  matching native "blocked by" link; text links that the native links replace are removed,
- parent issues: groups of about three or more open issues with a common goal and no parent that could be bundled
  under a parent issue,
- overlaps: issues with overlapping scope are merged, split, or linked.

## Planning documents

- Track concrete, bounded work as a GitHub issue. Write a plan in `docs/planning` for work that needs research or
  design before issues can be created, or as a working document for a large issue.
- A plan has one of two purposes:
    - **Preliminary plan:** research, design, or collected ideas before issues exist; it leads to one or more issues.
    - **Working plan:** a working document for one existing issue whose work is too large for the issue body, e.g.,
      an inventory, a workflow, per-item findings, and decisions collected over several sessions. Agents edit the
      local file faster and more reliably than an issue body, and the maintainer reviews changes as Git diffs.
- A working plan is the source of truth for the details of its issue while the work is in progress; the issue links
  the plan.
- A plan may contain exceptions to `AGENTS.md` or the rules only if the maintainer wrote or approved them.
- Name a plan in lowercase kebab-case with an area prefix and its topic, not its status (e.g.,
  `simulation-etpets-extensions.md`, `engine-grid-cell-aggregation.md`).
- Start each plan with a `**Status:**` line (`idea`, `planned`, `in progress`, or `done`) and a `**Related issues:**`
  line.
- Keep vague ideas for the distant future, which may never be implemented, as plans with status `idea` and a short
  note on priority (e.g. `idea (not planned; after the 1.0 release, possibly never)`), not as issues.
- A preliminary plan is completed when all its open work is tracked in issues; a working plan when the work of
  its issue is done. Before setting a completed plan to `done`, document its important information, results,
  and decisions where they last, e.g., in the issue, Javadoc, `docs/`, or `.claude/rules`. Commit the plan in this
  final state before deleting it in a separate commit, so that Git history keeps its decisions and rationale, also
  for issues and other plans that refer to it.

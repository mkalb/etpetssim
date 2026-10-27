---
name: review-exchange
description: 'Exchange code reviews between agents through .review/review.md: append a review block to the file, or process the file by assessing all findings, asking the maintainer, and then fixing or rejecting each finding. Use when asked to write a review to the review file or to process, work through, or apply the review file.'
---

# Review exchange

Agents exchange reviews through `.review/review.md`. The `.review/` folder is ignored by Git; create the folder and
file if they are missing. The section "Reviews" in `AGENTS.md` always applies.

## Severity

Use exactly one of these severities for each finding:

- **High:** a bug, regression, data loss, or security risk; or a broken build, test, or documented contract.
- **Medium:** a likely defect in an edge case, a missing test for changed behavior, a rule violation, or misleading
  documentation.
- **Low:** naming, style, simplification, or other improvements without a functional effect.

## Write a review to the file

- Only append one block at the end. Never change or remove existing blocks.
- Do not modify source files or run `git add`, `git commit`, or `git stash`.
- Start the block with the header `## Review YYYY-MM-DD - <agent and model> - <scope>`, where the scope is the staged
  changes, a commit or range, or specific files.
- List findings by severity, highest first, each with its severity, file, symbol or quoted line, problem, and
  suggested fix; line numbers may become outdated.

## Process the review file

1. Verify every finding of all blocks against the code before changing anything, and assess it as valid, partly
   valid, or rejected, with a short reason.
2. Report the assessment in the chat, together with questions about unclear findings and the decisions the maintainer
   has to make (e.g., between several fixes, or for findings that change public APIs or behavior).
3. Fix the findings only after the maintainer has answered; fix those confirmed as valid, as decided.
4. Report the outcome in the chat: fixed findings and rejected findings with their reasons.
5. Delete a block once all its findings are resolved or rejected; the file may become empty.

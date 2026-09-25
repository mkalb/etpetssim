---
name: java-method-inventory
description: 'Create a deterministic CSV inventory of Java method and constructor declarations, including implicit constructors and record accessors.'
user-invocable: true
disable-model-invocation: true
---

# Java Method Inventory

## How to Run

Run this command from the repository root in a terminal:

```text
java .github/skills/java-method-inventory/JavaMethodInventory.java
```

Requirements: Java 26 JDK on the `PATH`. The generator has no arguments.

Run the command exactly once, without prior exploration or searches. The generator is deterministic and tested; its
exit code and output are the complete result:

- Exit code `0` prints `Generated docs/planning/JavaMethodInventory.csv with <n> declarations.` (path separators
  depend on the operating system). Report this line and stop. Do not open, inspect, count, or re-validate the CSV.
- Any other exit code prints `FAIL ...` diagnostics; follow [Failure Handling](#failure-handling).

## Scope

The generator parses these source roots:

- `app/src/main/java`
- `app/src/test/java`

It writes the current snapshot to `docs/planning/JavaMethodInventory.csv`.
Explicit declarations in named top-level and member types are included. Additionally, these implicit members are
included:

- `DEFAULT_CONSTRUCTOR`: classes and enums without an explicit constructor, at the type declaration line.
- `CANONICAL_CONSTRUCTOR`: records without an explicit canonical or compact constructor, at the type declaration line.
- `RECORD_ACCESSOR`: record component accessors that are not declared explicitly, at the component's line.

Lambdas, initializers, local or anonymous types, and other compiler-generated members such as enum `values`/`valueOf`
or implicit `Object` methods (`equals`, `hashCode`, `toString`) are excluded; those appear only when explicitly
declared.

## Inventory Schema

The CSV is UTF-8 without BOM, uses the runtime-native line separator, follows RFC 4180 escaping, and has one row per
member. Rows are sorted by source set, repository-relative source path, and declaration line.

| Column                | Meaning                                                                                                   |
|-----------------------|-----------------------------------------------------------------------------------------------------------|
| `source_set`          | `main` or `test`                                                                                          |
| `source_path`         | Repository-relative Java source path                                                                      |
| `package_name`        | Declared package                                                                                          |
| `declaring_type`      | Enclosing type name, including named member types                                                         |
| `declaring_type_kind` | Java type kind, such as `CLASS`, `INTERFACE`, `ENUM`, or `RECORD`                                         |
| `member_name`         | Method or accessor name, or `<init>` for constructors                                                     |
| `member_kind`         | `METHOD`, `CONSTRUCTOR`, `COMPACT_CONSTRUCTOR`, or an implicit kind listed in Scope                       |
| `parameter_types`     | Ordered parameter types in normalized source spelling; empty for compact constructors                     |
| `visibility`          | Effective `public`, `protected`, `private`, or `package-private` access                                   |
| `modifiers`           | Explicit non-visibility modifiers; empty for implicit members                                             |
| `return_type`         | Method return type or record component type; empty for constructors                                       |
| `throws_types`        | Declared thrown types                                                                                     |
| `annotations`         | Declaration annotations, including arguments; record accessors repeat the component annotations           |
| `type_parameters`     | Declared method type parameters                                                                           |
| `line_number`         | Source line where the declaration starts, including leading annotations                                   |

## Failure Handling

The generator prints every Java parser error and exits without replacing the inventory when any parsed file has a syntax
error. Report the diagnostics and stop; do not infer rules from an incomplete inventory.

Exit codes: `2` parser errors, `3` usage error, `4` I/O or unexpected error. In every case, report the `FAIL` lines
verbatim and stop; do not retry or debug unless asked.

The deterministic implementation is [JavaMethodInventory.java](./JavaMethodInventory.java).

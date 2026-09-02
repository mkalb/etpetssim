---
name: java-method-inventory
description: 'Create a deterministic CSV inventory of explicit Java method and constructor declarations.'
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

## Scope

The generator parses these source roots:

- `app/src/main/java`
- `app/src/test/java`

It writes the current snapshot to `docs/planning/JavaMethodInventory.csv`.
Only explicit declarations in named top-level and member types are included. Lambdas, initializers, local or anonymous
types, and compiler-generated members such as record accessors are excluded.

## Inventory Schema

The CSV is UTF-8 without BOM, uses the runtime-native line separator, follows RFC 4180 escaping, and has one row per
declared member. Rows are sorted by source set, repository-relative source path, and declaration line.

| Column                | Meaning                                                           |
|-----------------------|-------------------------------------------------------------------|
| `source_set`          | `main` or `test`                                                  |
| `source_path`         | Repository-relative Java source path                              |
| `package_name`        | Declared package                                                  |
| `declaring_type`      | Enclosing type name, including named member types                 |
| `declaring_type_kind` | Java type kind, such as `CLASS`, `INTERFACE`, `ENUM`, or `RECORD` |
| `member_name`         | Declared method name, or `<init>` for constructors                |
| `member_kind`         | `METHOD`, `CONSTRUCTOR`, or `COMPACT_CONSTRUCTOR`                 |
| `parameter_types`     | Ordered parameter types in normalized source spelling             |
| `visibility`          | `public`, `protected`, `private`, or `package-private`            |
| `modifiers`           | Explicit non-visibility modifiers                                 |
| `return_type`         | Method return type; empty for constructors                        |
| `throws_types`        | Declared thrown types                                             |
| `annotations`         | Declaration annotations, including arguments                      |
| `type_parameters`     | Declared method type parameters                                   |
| `line_number`         | Source line of the declaration                                    |

## Failure Handling

The generator prints every Java parser error and exits without replacing the inventory when any parsed file has a syntax
error. Report the diagnostics and stop; do not infer rules from an incomplete inventory.

The deterministic implementation is [JavaMethodInventory.java](./JavaMethodInventory.java).

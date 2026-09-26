---
name: java-code-inventory
description: 'Create deterministic CSV inventories of Java type and method/constructor declarations, including implicit constructors, record accessors, and marker interfaces.'
user-invocable: true
disable-model-invocation: true
---

# Java Code Inventory

## How to Run

Run this command from the repository root in a terminal:

```text
java .github/skills/java-code-inventory/JavaCodeInventory.java
```

Requirements: Java 26 JDK on the `PATH`. The generator has no arguments.

Run the command exactly once, without prior exploration or searches. The generator is deterministic and tested; its
exit code and output are the complete result:

- Exit code `0` prints two lines (path separators depend on the operating system):
  - `Generated docs/planning/JavaMethodInventory.csv with <n> declarations.`
  - `Generated docs/planning/JavaTypeInventory.csv with <n> declarations.`

  Report both lines and stop. Do not open, inspect, count, or re-validate the CSVs.
- Any other exit code prints `FAIL ...` diagnostics; follow [Failure Handling](#failure-handling).

## Scope

The generator parses these source roots:

- `app/src/main/java`
- `app/src/test/java`

It writes the current snapshot to `docs/planning/JavaMethodInventory.csv` and `docs/planning/JavaTypeInventory.csv`.
Explicit declarations in named top-level and member types are included. Lambdas, initializers, local or anonymous
types, and other compiler-generated members such as enum `values`/`valueOf` or implicit `Object` methods (`equals`,
`hashCode`, `toString`) are excluded; those appear only when explicitly declared.

### Method Inventory

Additionally, these implicit members are included:

- `DEFAULT_CONSTRUCTOR`: classes and enums without an explicit constructor, at the type declaration line.
- `CANONICAL_CONSTRUCTOR`: records without an explicit canonical or compact constructor, at the type declaration line.
- `RECORD_ACCESSOR`: record component accessors that are not declared explicitly, at the component's line.

Because of these implicit constructors, classes, enums, and records without any explicit method or constructor still
produce one row. Interfaces and annotation types have no implicit-constructor rule, so an empty interface (for
example a marker interface with no members) produces no row in this CSV; it is covered by the Type Inventory instead.

### Type Inventory

Every named top-level and member type declaration (`CLASS`, `INTERFACE`, `ENUM`, `RECORD`, `ANNOTATION_TYPE`) produces
exactly one row, regardless of whether it declares any members. This makes marker interfaces and other member-less
types visible, which the Method Inventory alone does not guarantee.

## Inventory Schema

Both CSVs are UTF-8 without BOM, use the runtime-native line separator, follow RFC 4180 escaping, and have one row per
declaration. Rows are sorted by source set, repository-relative source path, and declaration line. The first three
columns share the same name and meaning in both CSVs. Method rows can be joined to their declaring type on
`(source_set, source_path, declaring_type)` = `(source_set, source_path, type_name)`.

### JavaMethodInventory.csv

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

### JavaTypeInventory.csv

| Column                | Meaning                                                                                                   |
|-----------------------|-----------------------------------------------------------------------------------------------------------|
| `source_set`          | `main` or `test`                                                                                          |
| `source_path`         | Repository-relative Java source path                                                                      |
| `package_name`        | Declared package                                                                                          |
| `type_name`           | Type name, including named member types (dotted nesting path)                                             |
| `type_kind`           | Java type kind: `CLASS`, `INTERFACE`, `ENUM`, `RECORD`, or `ANNOTATION_TYPE`                              |
| `extends_types`       | Explicit superclass for classes, or extended super-interfaces for interfaces; empty if none written       |
| `implements_types`    | Explicit implemented interfaces for classes/enums/records; always empty for interfaces                    |
| `permits_types`       | Explicit `permits` clause for sealed types; empty if not sealed or the permits clause is implicit         |
| `visibility`          | Effective `public`, `protected`, `private`, or `package-private` access                                   |
| `modifiers`           | Explicit non-visibility modifiers, such as `abstract`, `final`, `sealed`, `non-sealed`, or `static`       |
| `annotations`         | Type declaration annotations, including arguments                                                         |
| `type_parameters`     | Declared type parameters of the type itself (not its methods)                                             |
| `line_number`         | Source line where the type declaration starts, including leading annotations                              |

## Failure Handling

The generator prints every Java parser error and exits without replacing either inventory when any parsed file has a
syntax error. Report the diagnostics and stop; do not infer rules from an incomplete inventory.

Exit codes: `2` parser errors, `3` usage error, `4` I/O or unexpected error. In every case, report the `FAIL` lines
verbatim and stop; do not retry or debug unless asked.

The deterministic implementation is [JavaCodeInventory.java](./JavaCodeInventory.java).

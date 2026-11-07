# IntelliJ Warning Suppressions

**Status:** planned

**Related issues:** #68, #23, #46

Temporary working document for #68: an inventory of all `@SuppressWarnings` annotations in the main and test sources,
grouped by source set and inspection type, with a proposal and a decision for each type. The maintainer deletes it
after all types are done and the important decisions are documented where they last (`.claude/rules`,
`docs/development/intellij-setup.md`, or the inspection profile).

## Workflow

### Goals

- Every suppression is reviewed, and for each inspection type (and, where needed, each occurrence) one action is
  decided: adjust the inspection, remove the suppression, change the code, or keep the suppression.
- Suppressions that stay are as narrow as possible (`java.md`, "Suppressions Policy"); the exceptions are the
  class-level scopes that a rule explicitly allows.
- Similar cases are decided the same way in main and test sources; record such decisions under "Cross-type
  decisions".

### Rules for this work

- Do not add or remove a suppression without the maintainer's decision (`java.md`, "Suppressions Policy").
- The IntelliJ settings belong to the maintainer: the agent proposes changes to the inspection profile
  (`.idea/inspectionProfiles/Project_Default.xml`) and to the project dictionary (`.idea/dictionaries/project.xml`),
  and the maintainer applies them in IntelliJ. The agent does not edit these files.
- The agent cannot run IntelliJ inspections. After each change, the maintainer checks in IntelliJ that no new warning
  appears and that "Redundant suppression" reports nothing.
- One commit per source set and inspection type. The agent implements one planned commit at a time; the maintainer
  approves each commit (workflow of #33).
- Code changes follow `.claude/rules`; public API changes need the maintainer's approval before they are implemented.
- Rule changes (`java.md`, `junit.md`) are part of the commit that makes them necessary and need the maintainer's
  approval.
- Verify every fact against the sources; the inventory below may be outdated.

### Order

- This plan is done before `code-simulation-classes.md` starts (maintainer, 2026-10-30), because several suppressions
  are in the classes that plan aligns.
- Test sources first, then main sources. Within each source set, profile changes first (they remove the most
  annotations without code changes), then the types with code changes or per-occurrence decisions.

### Steps per inspection type

1. Set the type to `in progress` in the summary table. Read the occurrences in the sources and the relevant rules.
2. Present the analysis and the open questions in the chat; mark API and rule changes explicitly.
3. Record the decision under the type.
4. Implement it: the maintainer applies profile or dictionary changes in IntelliJ, the agent changes code, annotations,
   and rules. Run `app:compileJava` and `app:compileTestJava`, and `app:test` after code changes.
5. The maintainer checks the inspections in IntelliJ and approves the commit.
6. Set the type to `done` in the summary table and update the inventory.

### Documentation format

- One `###` subsection per inspection type with the bullets "Count:", "Proposal:", and "Decision:" (with the date),
  followed by the inventory. Inspection types with a single occurrence are collected in one subsection per source set,
  with one bullet per type.
- Inventory: one bullet per file (relative to `de.mkalb.etpetssim`) with the annotated members; methods end with
  `()`, constructors use the class name.
- Status values in the summary table: `open`, `in progress`, `done`.

## Baseline

As of 2026-10-30 (`f7266596`), after the maintainer added the suppressions that IntelliJ still reported; IntelliJ's
"Redundant suppression" inspection reported no obsolete suppressions. Counts are names in `@SuppressWarnings`; an
annotation with two names counts twice. There are no `//noinspection` comments.

| Sources | Suppressions | Files | Inspection types |
|---------|-------------:|------:|-----------------:|
| main    |          126 |    78 |               21 |
| test    |          117 |    78 |               16 |

Updated on 2026-11-06 (after `791c5f71`), after the maintainer ran the inspections again on main and test and added
a suppression for every remaining warning except proofreading, grammar, spelling, and style; IntelliJ then reported
no warnings. The inventory below reflects this state.

| Sources | Suppressions | Files | Inspection types |
|---------|-------------:|------:|-----------------:|
| main    |          144 |    86 |               27 |
| test    |          131 |    81 |               17 |

Not counted: the `@SuppressWarnings` text in the test data of `skills/JavaCodeInventoryTest.java`.

The test sources were done on 2026-11-06: 52 suppressions in 22 files and 9 inspection types remain, all decided as
"keep".

## Inspection profile

- The shared profile "Project Default" lists only the inspections whose settings differ from the IntelliJ defaults;
  all others run with their defaults.
- The profile already limits one inspection by scope: `IllegalDependencyOnInternalPackage` is disabled for the scope
  "Tests". The same mechanism can adjust inspections for test sources only. The scope "Tests" covers all classes in
  the test source root, including the helper classes that are not tests (e.g., `EtpetsScoreTuningRunner`).
- The project dictionary (`.idea/dictionaries/project.xml`) contains the owner login, the simulation and project
  names, their CLI codes, and the `AppLogger` method names.

## Summary

| Sources | Inspection type                     | Count | Proposal                         | Status      |
|---------|-------------------------------------|------:|----------------------------------|-------------|
| test    | `MagicNumber`                       |    51 | disable for scope "Tests"        | done        |
| test    | `SameParameterValue`                |    17 | disable for scope "Tests"        | done        |
| test    | `TestMethodWithoutAssertion`        |    20 | keep                             | done        |
| test    | `HardcodedLineSeparator`            |     8 | disable for scope "Tests"        | done        |
| test    | `TextBlockMigration`                |     3 | change code (text blocks)        | done        |
| test    | `UseOfProcessBuilder`               |     2 | keep                             | done        |
| test    | `DataFlowIssue`                     |     8 | keep 5; change code in 3         | done        |
| test    | `unused`                            |     5 | keep                             | done        |
| test    | `ResultOfMethodCallIgnored`         |     4 | keep                             | done        |
| test    | `NumericCastThatLosesPrecision`     |     3 | profile option                   | done        |
| test    | `ConstantValue`                     |     2 | narrow scope                     | done        |
| test    | `InsertLiteralUnderscores`          |     2 | change code                      | done        |
| test    | `SpellCheckingInspection`           |     2 | dictionary                       | done        |
| test    | other types (4)                     |     4 | per occurrence                   | done        |
| main    | `MarkerInterface`                   |     9 | disable                          | done        |
| main    | `InterfaceMayBeAnnotatedFunctional` |     5 | disable                          | done        |
| main    | `SpellCheckingInspection`           |     8 | dictionary                       | done        |
| main    | `unchecked`                         |    11 | change code in `ArrayGridModel`  | done        |
| main    | `ConstantValue`                     |     9 | change code (list cells, equals) | done        |
| main    | `DataFlowIssue`                     |    11 | change code (list cells)         | done        |
| main    | `SameReturnValue`                   |    13 | disable                          | done        |
| main    | `MagicNumber`                       |    21 | disable                          | done        |
| main    | `SameParameterValue`                |    14 | keep 12; change code in 2        | done        |
| main    | `NumericCastThatLosesPrecision`     |    12 | keep 8; change code in 4         | done        |
| main    | `unused`                            |     6 | keep 4; remove 2 parameters      | done        |
| main    | `CommentedOutCode`                  |     4 | keep                             | done        |
| main    | `ObjectEquality`                    |     3 | change code; keep 1 (narrowed)   | done        |
| main    | types with two occurrences (4)      |     8 | keep 5; change code in 1         | done        |
| main    | other types (10)                    |    10 | per occurrence                   | open        |

## Cross-type decisions

- Work on this plan before `code-simulation-classes.md` (maintainer, 2026-10-30).
- `MagicNumber` is disabled for the scope "Tests" in the inspection profile instead of being suppressed per test class
  (maintainer, 2026-10-30).
- The suppressions that the maintainer added on 2026-11-06 are committed as a baseline before the types are worked
  on (maintainer, 2026-11-06).

## 1. Test sources

### `MagicNumber`

- Count: 51; 50 at class level as `junit.md` prescribed, 1 at method level (`AppResourcesTest.testGetImage()`).
- Proposal: disable the inspection for the scope "Tests", remove all 51 suppressions, and remove the `MagicNumber`
  bullet from `junit.md` (rule change).
- Decision: disable for the scope "Tests" (maintainer, 2026-10-30); removing the suppressions and the rule change are
  part of the same commit.

The inventory is omitted: all occurrences are removed by the same commit.

### `SameParameterValue`

- Count: 17; 16 at type level (9 test classes and 7 nested fixture types), 1 at method level.
- Proposal: disable for the scope "Tests". Test helpers and fixture records are often called with the same values, and
  the warning rarely points to a problem there. Otherwise, narrow the class-level scopes to the affected methods.
- Decision: disable for the scope "Tests" and remove all 17 suppressions (maintainer, 2026-11-06).

The inventory is omitted: all occurrences are removed by the same commit.

### `TestMethodWithoutAssertion`

- Count: 20. The tests delegate their assertions to helpers: `ConstantTestSupport.assert*` and, in
  `FXGridCanvasPainterTest`, test-local `assert*` methods inside the lambda of `FXTestSupport.runAndWait`.
- Proposal: register the helper methods as assertion methods in the options of the inspection (class and method name
  pattern) and remove all 20 suppressions. Check in IntelliJ that the option also covers the assertions inside the
  lambda; otherwise keep those 8 suppressions. Alternative for the 12 constant tests: call the helpers through static
  imports, as the enum tests do, which IntelliJ does not report.
- Decision: keep all 20 suppressions (maintainer, 2026-11-06).

- `core/AppLocalizationKeysTest.java`: `testConstantNamesAreAlphabeticallySorted()`,
  `testConstantsAreUniqueAndNonBlank()`, `testConstantsExistInAllBundles()`
- `simulations/conway/view/ConwayStyleClassesTest.java`: `testConstantNamesAreAlphabeticallySorted()`,
  `testConstantsAreUniqueAndNonBlank()`, `testConstantsUseCssTokenFormat()`
- `simulations/core/view/SimulationLocalizationKeysTest.java`: `testConstantNamesAreAlphabeticallySorted()`,
  `testConstantsAreUniqueAndNonBlank()`, `testConstantsExistInAllBundles()`
- `ui/FXGridCanvasPainterTest.java`: `testDrawCell()`, `testDrawCellBoundingBox()`, `testDrawCellInnerCircle()`,
  `testDrawCircle()`, `testDrawPolygon()`, `testDrawRectangle()`, `testDrawScaledCell()`, `testFillCanvasBackground()`
- `ui/FXStyleClassesTest.java`: `testConstantNamesAreAlphabeticallySorted()`, `testConstantsAreUniqueAndNonBlank()`,
  `testConstantsUseCssTokenFormat()`

### `HardcodedLineSeparator`

- Count: 8, all at class level, in tests that build or compare text (skill scripts, files, command-line arguments,
  and dialog texts) and in a helper class that builds text output.
- Proposal: disable for the scope "Tests"; tests compare text with fixed line separators on purpose.
- Decision: disable for the scope "Tests" and remove all 8 suppressions (maintainer, 2026-11-06).

The inventory is omitted: all occurrences are removed by the same commit.

### `TextBlockMigration`

- Count: 3, expected strings with `\n` in the tests of `formatUnexpectedErrorContent()`.
- Proposal: change code: write the expected strings as text blocks and remove the suppressions.
- Decision: change code (maintainer, 2026-11-06).

The inventory is omitted: all occurrences are removed by the same commit.

### `UseOfProcessBuilder`

- Count: 2, in the helpers that start skill scripts.
- Proposal: disable for the scope "Tests"; starting processes is the purpose of these tests.
- Decision: keep the inspection and both method-level suppressions (maintainer, 2026-11-06).

- `skills/I18nConsistencyCheckTest.java`: `runHelper()`
- `skills/JavaCodeInventoryTest.java`: `runHelper()`

### `DataFlowIssue`

- Count: 8, all at method level; only one is an intentional null-passing test as `junit.md` prescribed. Four modify
  an unmodifiable collection on purpose (`assertThrows(UnsupportedOperationException.class, ...)`), three cast an
  entity in `assertAll` after `assertInstanceOf`.
- Proposal: keep the null-passing test and the four immutability tests, and extend the `junit.md` rule to the
  immutability tests (rule change). Change code in the three cast tests: use the result of `assertInstanceOf`
  instead of the casts, and remove the suppressions.
- Decision: as proposed (maintainer, 2026-11-06). The code change also removes the casts that the class-level
  `OverlyStrongTypeCast` suppression of `EtpetsUserActionTest` covered; that suppression is removed in the same
  commit.

Remaining suppressions:

- `core/AppArgsTest.java`: `testKeys()`
- `core/AppManifestTest.java`: `testParseMainAttributes()`
- `simulations/core/model/StatisticsHistoryTest.java`: `testToListReturnsImmutableSnapshot()`
- `simulations/snake/model/strategy/NamedMoveStrategyTest.java`: `testDecideMove()`
- `simulations/snake/model/strategy/SnakeMoveStrategiesTest.java`: `testStrategies()`

### `unused`

- Count: 5, fixture classes whose constants are only read by reflection.
- Proposal: keep.
- Decision: keep (maintainer, 2026-11-06).

- `testsupport/ConstantTestSupportTest.java`: class `BlankConstants`, class `DuplicateConstants`, class
  `NoStringConstants`, class `SortedConstants`, class `UnsortedConstants`

### `ResultOfMethodCallIgnored`

- Count: 4, in tests of interrupt handling. Each ignores the result of `Thread.interrupted()`, which clears the
  interrupt status: three in a `finally` block, one in a fixture lambda.
- Proposal: check each occurrence: assert the result where it is meaningful (change code), otherwise keep.
- Decision: keep all 4; clearing the interrupt status with `Thread.interrupted()` is the usual idiom, and no result
  is meaningful to assert (maintainer, 2026-11-06).

- `ExtraterrestrialPetsSimulationTest.java`: `testStopInterruptionEscalatesEveryPendingTermination()`,
  `testStopInterruptsTerminationWaitAndRestoresInterruptStatus()`
- `simulations/core/viewmodel/DefaultMainViewModelTest.java`:
  `testShutdownSimulationSuppressesStaleInitializationCompletion()`
- `testsupport/FXTestSupportTest.java`: `testSupplyAndWaitRestoresInterruptedStatus()`

### `NumericCastThatLosesPrecision` (tests)

- Count: 3, casts such as `(byte) 0xFF` that build invalid bytes on purpose.
- Proposal: enable the option "Ignore overflowing byte casts" of the inspection (`ignoreOverflowingByteCasts` of
  `CastThatLosesPrecision`, set to `false` in the profile); it applies to main and test sources, and main sources have
  no such casts. Remove the 3 suppressions.
- Decision: as proposed; the maintainer also enabled "Ignore integer char casts" (`ignoreIntegerCharCasts`)
  (maintainer, 2026-11-06).

The inventory is omitted: all occurrences are removed by the same commit.

### `ConstantValue`

- Count: 2; the annotation on `EtpetsBalanceTest` is at class level.
- Proposal: keep; narrow the class-level scope of `EtpetsBalanceTest` to the methods that check constants.
- Decision: keep `WorkCheckpointsTest`; narrow `EtpetsBalanceTest` to the methods that IntelliJ reports after the
  class-level suppression is removed (maintainer, 2026-11-06). Only one method is reported.

Remaining suppressions:

- `engine/support/WorkCheckpointsTest.java`: `testCancellationCheckInterval()`
- `simulations/etpets/model/EtpetsBalanceTest.java`: `testInitialCurrentEnergyReductionKeepsMinimumEnergy()`

### `InsertLiteralUnderscores`

- Count: 2; a weak warning, on the assertion delta `0.00001`.
- Proposal: change code (insert the underscores) and remove the suppressions, unless the literals are expected values
  that are easier to read without underscores.
- Decision: change code: write the deltas as `1.0e-5`, like the `DELTA` constants of other tests, and remove the
  suppressions (maintainer, 2026-11-06).

The inventory is omitted: all occurrences are removed by the same commit.

### `SpellCheckingInspection`

- Count: 2, both at class level.
- Proposal: add real domain words to the project dictionary and remove the suppressions; keep a narrowed suppression
  only for artificial test strings.
- Decision: as proposed (maintainer, 2026-11-06). The maintainer added the CLI codes of the simulations and
  `etpetssim` to the project dictionary and moved the suppressions to the methods with artificial test strings. The
  spelling inspection was not part of the baseline, so this also adds method-level suppressions in two classes that
  had none.

Remaining suppressions (12):

- `core/AppArgsTest.java`: `testFormatHelpArguments()`
- `simulations/core/SimulationTypeTest.java`: `testCliArgumentsReturnsExpectedValues()`
- `simulations/langton/shared/LangtonMovementRulesTest.java`: `testIsValidForCellShape()`, `testParse()`,
  `testToDisplayString()`
- `skills/I18nConsistencyCheckTest.java`: `testDuplicateDecodedKeysInBothLocalesAreReportedAndBlockFix()`,
  `testFixCanonicalizesUnicodeEscapesInKeys()`, `testFixCanonicalizesValidNonCanonicalSyntaxAndIsIdempotent()`,
  `testFixDoesNotCreateEscapesWhenRemovingLiteralInvisibleCharacters()`,
  `testMalformedUnicodeEscapesAreReportedWithSourceLinesAndBlockFix()`,
  `testReportParsesPropertiesGrammarWithDecodedKeysValuesAndSourceLines()`,
  `testReportWarnsAboutValidUnicodeEscapesInKeys()`

### Other test inspection types

One occurrence each; decide per occurrence.

- `EqualsWithItself`: `simulations/core/SimulationTerminationHandleTest.java`, `testCompletedReturnsSameInstance()`.
  Proposal: keep. Decision: keep; comparing the singleton with itself is the purpose of the test (maintainer,
  2026-11-06).
- `FieldNamingConvention`: `testsupport/FXTestSupport.java`, variable `started`. Proposal: check the name against the
  naming rules; rename or keep. Decision: the profile required `[A-Z][A-Z_\d]*` for non-final static fields
  ("StaticVariableNamingConvention"); change it to camelCase `[a-z][A-Za-z\d]*`, remove the suppression, and remove
  the explicit default initializer `= false` (`java.md`). The same profile change resolves `AppLocalization.state` in
  main sources, in a separate commit (maintainer, 2026-11-06).
- `OverlyStrongTypeCast`: `simulations/etpets/model/EtpetsUserActionTest.java`, class level. Proposal: narrow the scope
  to the affected method or change the cast. Decision: removed together with the `DataFlowIssue` code change, which
  removes the casts (2026-11-06).
- `UnusedReturnValue`: `simulations/core/viewmodel/SimulationUserActionDescriptorTest.java`, `createDescriptor()`.
  Proposal: check whether the method can return `void`; otherwise keep. Decision: keep; a `create...` helper returns
  the created object (maintainer, 2026-11-06).

## 2. Main sources

### `MarkerInterface`

- Count: 9, all sealed or base marker interfaces on purpose (mainly the `*UserActionContext` family).
- Proposal: disable the inspection in the profile, where it is enabled explicitly; remove all 9 suppressions.
- Decision: as proposed (maintainer, 2026-11-06). The maintainer removed the entry from the profile; the
  inspection is disabled by default.

The inventory is omitted: all occurrences are removed by the same commit.

### `InterfaceMayBeAnnotatedFunctional`

- Count: 5, interfaces with one abstract method that are not meant as lambda targets.
- Proposal: disable the inspection; remove all 5 suppressions.
- Decision: as proposed (maintainer, 2026-11-06). The maintainer removed the entry from the profile; the
  inspection is disabled by default.

The inventory is omitted: all occurrences are removed by the same commit.

### `SpellCheckingInspection`

- Count: 8, all at class level (constants, style classes, rule presets, and patterns); 7 remain.
- Proposal: add real domain words to the project dictionary and remove the suppressions; for artificial tokens (rule
  strings, pattern data), keep a suppression narrowed to the affected fields.
- Decision: as proposed; the maintainer removes the class-level suppressions in IntelliJ, adds domain words to the
  dictionary, and narrows the rest to fields or methods (maintainer, 2026-11-06).
    - `AppLocalizationKeys`: the dictionary words added for the tests cover its keys; suppression removed.
    - `EtpetsAgentLogic.collectActionCandidates()`: a later run of the spelling inspection reported `eggCoord`; the
      variable is renamed to `eggCoordinate` instead of suppressed.
    - `ConwayStyleClasses`, `FXStyleClasses`: the JavaFX layout class names (e.g., `GRIDPANE`, `HBOX`) and
      `TRANSITIONRULES` are added to the dictionary; suppressions removed.
    - `SimulationType`: `forestfiremodel` and `LANGTONS` are added to the dictionary; the suppression is narrowed to
      the enum constants `CONWAYS_LIFE` and `LANGTONS_ANT`, whose CLI codes are artificial tokens.
    - `ConwayPatterns`, `ConwayRulePresetSquare`, `LangtonRulePresetHexagon`, `LangtonRulePresetSquare`: keep the
      class-level suppressions; most of their values are artificial tokens (pattern data and rule strings).

Remaining suppressions (6):

- `simulations/conway/model/ConwayPatterns.java`: class `ConwayPatterns`
- `simulations/conway/shared/ConwayRulePresetSquare.java`: enum `ConwayRulePresetSquare`
- `simulations/core/SimulationType.java`: enum constants `CONWAYS_LIFE`, `LANGTONS_ANT`
- `simulations/langton/shared/LangtonRulePresetHexagon.java`: enum `LangtonRulePresetHexagon`
- `simulations/langton/shared/LangtonRulePresetSquare.java`: enum `LangtonRulePresetSquare`

### `unchecked`

- Count: 11; 8 of them in `ArrayGridModel`, which casts the entries of its `Object` array to `ENT`.
- Proposal: change code in `ArrayGridModel`: a private method reads and casts an entry, with one method-level
  suppression (8 to 1). Keep the 3 suppressions for generic arrays in `AbstractConfigView` and the casts of the
  toggle user data in `FXComponentFactory`.
- Decision: as proposed (maintainer, 2026-11-06). `ArrayGridModel` reads all entries through the private method
  `entityAt(int, int)`.

Remaining suppressions (4):

- `engine/model/ArrayGridModel.java`: `entityAt()`
- `simulations/core/view/AbstractConfigView.java`: `createLayoutPane()`, `toLabeledControlArray()`
- `ui/FXComponentFactory.java`: `createLabeledChoiceRadioButtons()`

### `ConstantValue`

- Count: 9. Seven are the `updateItem()` override of a `ListCell` (`empty || (item == null)`, together with
  `DataFlowIssue`): six views repeat the cell of `FXComponentFactory.createDisplayNameListCell`.
- Proposal: change code: a shared cell factory in `ui` replaces the six copies, so one suppression pair remains (DRY).
  `CreatureBase.equals()`: declare the parameter `@Nullable Object` or use an `instanceof` pattern as in
  `RadiusRingCell`, and remove the suppression. `FXComponentFactory.createLabeledChoiceComboBox()`: check.
- Decision (maintainer, 2026-11-06):
    - The new public method `FXComponentFactory.createTextListCell(Function<T, String>)` replaces the private
      `createDisplayNameListCell(..)` and the anonymous cells of the six views (API change in `ui`, approved); the
      private `create...Cell()` methods of the views delegate to it. One `ConstantValue`/`DataFlowIssue` pair remains.
    - `CreatureBase.equals(@Nullable Object)`; the `getClass()` check stays, because an `instanceof` pattern would
      make a fish and a shark with the same id equal.
    - `createLabeledChoiceComboBox()`: the selection listener is a `ChangeListener<@Nullable T>`, because the
      selection can be cleared; the listener of the `InputChoiceProperty` drops its null check, because the value is
      never `null`.

Remaining suppressions (1):

- `ui/FXComponentFactory.java`: `updateItem()` in `createTextListCell()`

### `DataFlowIssue`

- Count: 11; seven are the `updateItem()` overrides (see `ConstantValue`), three are `initializeSimulation()` and
  `drawSimulation()` overrides of `ForestMainView` and `WatorMainView`.
- Proposal: decided together with `ConstantValue`; check `DefaultMainViewModel.applyUserAction()` and the
  `ForestMainView` and `WatorMainView` overrides separately.
- Decision: the `updateItem()` overrides are decided together with `ConstantValue`; 6 of 7 are removed (maintainer,
  2026-11-06). Change code in the other 4 (maintainer, 2026-11-06):
    - `DefaultMainViewModel.applyUserAction()`: the error log passed the possibly `null` manager to the varargs of
      `AppLogger.errorf(..)`; it logs `managerActive=true|false` instead. `@Nullable` is not used on local variables
      (`java.md`, "Nullability").
    - `ForestMainView`, `WatorMainView`: the lambdas read the `@Nullable` fields `cellEmojiFont` and `cellDrawer`
      after a null check, which IntelliJ does not carry into the lambda; copy the field into a local variable before
      the check.

Remaining suppressions (1):

- `ui/FXComponentFactory.java`: `updateItem()` in `createTextListCell()`

### `SameReturnValue`

- Count: 13; 11 are the static `labelResourceKey()` methods of enums, which return a literal by design (`AGENTS.md`,
  "Localization and Text").
- Proposal: keep. Check `GridStructure.minCoordinateInclusive()` and `SimulationTerminationHandle.completed()` (a
  singleton factory); keep them if the method is part of a consistent API.
- Decision: disable the inspection in the profile and remove all 13 suppressions (maintainer, 2026-11-06). All
  occurrences are intended: the key methods return literals as `java.md` prescribes, `minCoordinateInclusive()` is
  the counterpart of `maxCoordinateExclusive()` in the engine API, and `completed()` is a singleton factory; every new
  choice enum would need another suppression.

The inventory is omitted: all occurrences are removed by the same commit.

### `MagicNumber`

- Count: 21, mostly in UI geometry and simulation math; `CellNeighborhoods` is suppressed at class level.
- Proposal: per occurrence: extract named constants where a value has a domain meaning, otherwise keep. Narrow the
  class-level scope of `CellNeighborhoods`.
- Decision: disable the inspection in the profile and remove all 21 suppressions (maintainer, 2026-11-07). All
  occurrences are literals in formulas, geometry, and layout code (halving with `2.0d`, font and stroke factors,
  percent scaling, rounding thresholds), which `java.md` allows inline; domain numbers are named in `*Constraints`
  and `*Balance`. The maintainer removed the entry from the profile, including its scope setting for "Tests"; the
  inspection is disabled by default.

The inventory is omitted: all occurrences are removed by the same commit.

### `SameParameterValue`

- Count: 14; 11 are enum constructors.
- Proposal: per occurrence: remove the parameter where it is not needed (change code), otherwise keep.
- Decision (maintainer, 2026-11-07):
    - Keep the 11 enum constructors. The entity enums pass a complete `GridEntityDescriptorSpec` per constant, whose
      `visible` is `true` everywhere (and every value is the same in enums with one constant); `SimulationType` keeps
      `implemented`, which `StartMainView` evaluates, although every type is implemented.
    - Change code in the two private helpers with one call each: `ConwayConfigView.createTransitionRulesControl(..)`
      uses `CONWAY_CONFIG_RULES` and `CONWAY_CONFIG_RULES_TOOLTIP` directly instead of the parameters `labelKey` and
      `tooltipKey`; `LabMainView.drawBaseCanvasCoordinate(..)` uses `SHAPE_LINE_WIDTH` instead of the parameter
      `strokeLineWidth`. Both suppressions are removed.
    - Keep `AbstractConfigView.formatDoubleRangeTooltip(String, InputDoubleIntRangeProperty)`, the protected
      counterpart of the overload for `InputDoubleProperty`; its visibility is recorded as a finding
      in `code-simulation-classes.md` (category 3).

Remaining suppressions (12):

- `simulations/conway/model/entity/ConwayEntity.java`: `ConwayEntity()`
- `simulations/core/SimulationType.java`: `SimulationType()`
- `simulations/core/view/AbstractConfigView.java`: `formatDoubleRangeTooltip()`
- `simulations/etpets/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/forest/model/entity/ForestEntity.java`: `ForestEntity()`
- `simulations/lab/model/entity/LabEntity.java`: `LabEntity()`
- `simulations/langton/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/rebounding/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/snake/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/sugar/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/wator/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/wator/model/entity/TerrainConstant.java`: `TerrainConstant()`

### `NumericCastThatLosesPrecision`

- Count: 12, casts from `double` or `long` to `int` after rounding or clamping.
- Proposal: keep; check each occurrence for a clearer alternative (e.g., `Math.toIntExact`, `Math.clamp`).
- Decision (maintainer, 2026-11-07):
    - Change code in the 4 casts from `long` to `int`, whose values are always in the `int` range, and remove their
      suppressions: `GridInitializers.fillRandomPercent(..)` and `placeRandomPercent(..)` and
      `SnakeSimulationManager.foodInitializer(..)` use `Math.toIntExact(..)`; `PetGenome.fromParents(..)` passes the
      rounded `long` to `Math.clamp(long, int, int)`, which returns an `int`. The results and random sequences stay
      the same.
    - Keep the 8 casts from `double` to `int` or `long` that truncate on purpose (pixels to cells, milliseconds,
      points, sugar amounts, and metric values). The suppression of `SugarSimulationManager.computeSugarAmount()`
      drops its braces (`{"..."}`).
    - The exception messages of `StatisticsExtremum` are recorded as a deferred finding in
      `code-simulation-classes.md`.

Remaining suppressions (8):

- `simulations/core/model/StatisticsExtremum.java`: `intValue()`
- `simulations/core/view/AbstractObservationView.java`: `updateGenericMetricSection()`
- `simulations/core/viewmodel/DefaultMainViewModel.java`: `configureSimulationTimeout()`
- `simulations/snake/model/SnakeStepLogic.java`: `moveSnake()`
- `simulations/sugar/model/SugarSimulationManager.java`: `computeClampedIndex()`, `computeSugarAmount()`
- `ui/FXGridCanvasPainter.java`: `isOnCanvas()`
- `ui/GridGeometry.java`: `estimateGridCoordinate()`

### `unused` (main)

- Count: 6; five are on overridable methods whose parameters some implementations ignore, one is the private helper
  `formatActionCandidates()`, which only a commented-out diagnostic log call uses.
- Proposal: per occurrence: keep the suppressions on the overridable methods (parameters that implementations or
  overrides may ignore); for `formatActionCandidates()`, keep it together with the commented-out `AppLogger` call
  (`java.md`, "Logging with AppLogger") or remove both.
- Decision (maintainer, 2026-11-07):
    - Keep 4: `SynchronousStepLogic.performSynchronousStep(..)` (engine API; Conway and Forest ignore `stepIndex`),
      `SimulationUserAction.apply(..)` (every parameter is used by some user action),
      `AbstractMainView.createEditToolBarOptionPanel(..)` (only the default implementation ignores
      `selectedToolId`), and `EtpetsAgentLogic.formatActionCandidates(..)` together with the commented-out
      `AppLogger` call (`java.md`, "Logging with AppLogger").
    - API change in `simulations.core.view` (approved): remove the parameters that no implementation uses, and their
      suppressions: `model` of `AbstractDefaultMainView.initializeSimulation(..)` (8 main views) and `mousePoint` of
      `AbstractMainView.handleMouseClickedCoordinate(..)` (`AbstractDefaultMainView`, `LabMainView`).

Remaining suppressions (4):

- `engine/executor/SynchronousStepLogic.java`: `performSynchronousStep()`
- `simulations/core/model/SimulationUserAction.java`: `apply()`
- `simulations/core/view/AbstractMainView.java`: `createEditToolBarOptionPanel()`
- `simulations/etpets/model/EtpetsAgentLogic.java`: `formatActionCandidates()`

### `CommentedOutCode`

- Count: 4, commented-out diagnostic code in the Etpets model ("Only for testing / debugging" blocks and
  `AppLogger` calls).
- Proposal: keep; `java.md` ("Logging with AppLogger") allows commented-out `AppLogger` calls as diagnostic traces.
  Check whether the other commented-out blocks are still needed; otherwise remove them (change code).
- Decision: keep all 4 (maintainer, 2026-11-07). `EtpetsAgentLogic.selectAndApplyAction()` keeps the commented-out
  `AppLogger` call that `java.md` allows. The three blocks "Only for testing / debugging" in
  `EtpetsSimulationManager` stay as a temporary exception without a rule change: the optimization of ET Pets still
  needs them and removes them afterwards, together with their suppressions.

- `simulations/etpets/model/EtpetsAgentLogic.java`: `selectAndApplyAction()`
- `simulations/etpets/model/EtpetsSimulationManager.java`: `initializePets()`, `initializeResources()`,
  `initializeTerrain()`

### `ObjectEquality`

- Count: 3.
- Proposal: `GridCoordinate` compares with its own constant on purpose; keep, or use `equals` if equally fast. Check
  `EtpetsAgentLogic.apply()` separately.
- Decision (maintainer, 2026-11-07):
    - `GridCoordinate.isIllegal()`: the identity check with `ILLEGAL` is removed; the checks of `x` and `y` already
      cover it. Suppression removed.
    - `GridCoordinate.toDisplayString()`: compares with `equals(ILLEGAL)`, as fits a record; the Javadoc says
      "equals the constant". A separately created coordinate equal to `ILLEGAL` now also returns `(illegal)`;
      `GridCoordinateTest.testToDisplayString()` expected the numbers before and is adapted. Suppression removed.
    - `EtpetsAgentLogic.apply(..)`: the identity check of the snapshot entity moves to the private method
      `checkSnapshotEntity(..)`, which carries the suppression. Its exception message follows `java.md`
      ("Exceptions"): `Unable to process agent: snapshot entity mismatch, coordinate=..., expected=..., actual=...`.

Remaining suppressions (1):

- `simulations/etpets/model/EtpetsAgentLogic.java`: `checkSnapshotEntity()`

### Main inspection types with two occurrences

- `ExtractMethodRecommender`: check the recommendations; change code or keep. Decision (maintainer, 2026-11-07):
  `FXComponentFactory.createLabeledIntSlider(..)` builds its slider through the new private method
  `createIntSlider(..)`, like its siblings through `createRoundingSlider(..)`; suppression removed.
  `FXPaintFactory.computeBrightnessVariantsMap(..)` keeps its suppression: checking the arguments and then computing
  in one method is the usual fail-fast pattern.
- `FieldNamingConvention`: `CompassDirection` has one-letter constants (`N`, `E`, `S`, `W`), shorter than the
  minimum length 2 of the constant naming convention in the profile: set the minimum length to 1 or keep. Decision:
  set the minimum length of the enum constant naming convention to 1 and remove the suppression (maintainer,
  2026-11-06). The maintainer set the minimum length to 1 for all four field naming conventions (constants,
  constants with a mutable type, enum constants, and static variables). Done.
  `AppLocalization.state`: done; the camelCase convention for non-final static fields (see the test decision)
  removes the suppression (maintainer, 2026-11-06).
- `OptionalUsedAsFieldOrParameterType`: check whether the callers can pass the value or `null` instead; otherwise
  keep. Decision: keep for now (maintainer, 2026-11-07); the finding "Optional parameters of
  AbstractObservationView" in `code-simulation-classes.md` (category 3) removes the parameters and the suppressions.
- `OverloadedMethodsWithSameNumberOfParameters`: change code: give the two private `clampToMapRange` methods
  distinct names. Decision: keep (maintainer, 2026-11-07); the callers pass clearly an `int` or a `double`, and the
  `int` overload states that it only clamps, without the `NaN` check and rounding of the `double` overload.

Remaining suppressions (5):

- `simulations/core/view/AbstractObservationView.java`: `updateGenericMetricSection()`, `updateStatusSectionLabel()`
- `simulations/etpets/view/EtpetsMainView.java`: `clampToMapRange()` (2x)
- `ui/FXPaintFactory.java`: `computeBrightnessVariantsMap()`

### Other main inspection types

One occurrence each; decide per occurrence.

- `BooleanMethodIsAlwaysInverted`: `simulations/etpets/model/entity/Pet.java`, `hasReachedAgingEffectsAge()`.
  Proposal: change code (invert the method and its name) or keep.
- `CallToSystemExit`: `AppLauncher.java`, `parseArgumentsAndHandleInformation()`. Proposal: keep.
- `ClassReferencesSubclass`: `engine/model/entity/GridEntity.java`, `isConstant()`. Proposal: keep.
- `CollectionDeclaredAsConcreteClass`: `core/AppManifest.java`, `parseMainAttributes()`. Proposal: check the local
  `HashMap` declaration; change code or keep.
- `HardcodedLineSeparator`: `ExtraterrestrialPetsSimulation.java`, `formatUnexpectedErrorContent()` (text of the
  error alert). Proposal: check whether the alert needs `\n`; change code or keep. Decided as a single case,
  independent of the profile decision for the tests (maintainer, 2026-11-06).
- `OptionalIsPresent`: `simulations/core/viewmodel/DefaultMainViewModel.java`,
  `applyUserAction(SimulationUserActionDescriptor)`. Proposal: check the functional rewrite; change code or keep.
- `ParameterHidesMemberVariable`: `simulations/core/view/AbstractDefaultMainView.java`, `drawSimulation()`. Proposal:
  rename the parameter or keep.
- `StringBufferWithoutInitialCapacity`: `ui/AboutDialog.java`, `formatFilePaths()`. Proposal: check whether an
  initial capacity is useful for at most two lines; change code or keep.
- `SwitchExpressionCanBePushedDown`: `simulations/langton/model/LangtonStepRunner.java`,
  `computeNewAntDirection()`. Proposal: check the rewrite; change code or keep.
- `UnusedReturnValue`: `core/AppSettings.java`, `writeLocale(CountryLocale)`. Proposal: check whether a caller
  should use the result or the method can return `void` (public API of `core`); otherwise keep.

## Deferred findings

None yet.

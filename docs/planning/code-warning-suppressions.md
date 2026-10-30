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

Not counted: the `@SuppressWarnings` text in the test data of `skills/JavaCodeInventoryTest.java`.

## Inspection profile

- The shared profile "Project Default" lists only the inspections whose settings differ from the IntelliJ defaults;
  all others run with their defaults.
- The profile already limits one inspection by scope: `IllegalDependencyOnInternalPackage` is disabled for the scope
  "Tests". The same mechanism can adjust inspections for test sources only. The scope "Tests" covers all classes in
  the test source root, including the helper classes that are not tests (e.g., `EtpetsScoreTuningRunner`).
- The project dictionary contains only `mkalb`.

## Summary

| Sources | Inspection type                     | Count | Proposal                         | Status |
|---------|-------------------------------------|------:|----------------------------------|--------|
| test    | `MagicNumber`                       |    50 | disable for scope "Tests"        | open   |
| test    | `SameParameterValue`                |    17 | disable for scope "Tests"        | open   |
| test    | `TestMethodWithoutAssertion`        |    17 | configure assertion methods      | open   |
| test    | `HardcodedLineSeparator`            |     4 | disable for scope "Tests"        | open   |
| test    | `UseOfProcessBuilder`               |     2 | disable for scope "Tests"        | open   |
| test    | `DataFlowIssue`                     |     7 | keep; check each occurrence      | open   |
| test    | `unused`                            |     5 | keep                             | open   |
| test    | `ResultOfMethodCallIgnored`         |     4 | check each occurrence            | open   |
| test    | `ConstantValue`                     |     2 | narrow scope                     | open   |
| test    | `InsertLiteralUnderscores`          |     2 | change code                      | open   |
| test    | `SpellCheckingInspection`           |     2 | dictionary                       | open   |
| test    | other types (5)                     |     5 | per occurrence                   | open   |
| main    | `MarkerInterface`                   |     9 | disable                          | open   |
| main    | `InterfaceMayBeAnnotatedFunctional` |     5 | disable                          | open   |
| main    | `SpellCheckingInspection`           |     9 | dictionary                       | open   |
| main    | `unchecked`                         |    11 | change code in `ArrayGridModel`  | open   |
| main    | `ConstantValue`                     |     9 | change code (list cells, equals) | open   |
| main    | `DataFlowIssue`                     |     8 | change code (list cells)         | open   |
| main    | `SameReturnValue`                   |    13 | keep                             | open   |
| main    | `MagicNumber`                       |    21 | per occurrence                   | open   |
| main    | `SameParameterValue`                |    13 | per occurrence                   | open   |
| main    | `NumericCastThatLosesPrecision`     |    12 | keep; check each occurrence      | open   |
| main    | types with two occurrences (5)      |    10 | per occurrence                   | open   |
| main    | other types (6)                     |     6 | per occurrence                   | open   |

## Cross-type decisions

- Work on this plan before `code-simulation-classes.md` (maintainer, 2026-10-30).
- `MagicNumber` is disabled for the scope "Tests" in the inspection profile instead of being suppressed per test class
  (maintainer, 2026-10-30).

## 1. Test sources

### `MagicNumber`

- Count: 50, all at class level as `junit.md` prescribes.
- Proposal: disable the inspection for the scope "Tests", remove all 50 suppressions, and remove the `MagicNumber`
  bullet from `junit.md` (rule change).
- Decision: disable for the scope "Tests" (maintainer, 2026-10-30); removing the suppressions and the rule change are
  part of the same commit.

The inventory is omitted: every occurrence is a class-level annotation and is removed by the same commit.

### `SameParameterValue`

- Count: 17; 16 at type level (9 test classes and 7 nested fixture types), 1 at method level.
- Proposal: disable for the scope "Tests". Test helpers and fixture records are often called with the same values, and
  the warning rarely points to a problem there. Otherwise, narrow the class-level scopes to the affected methods.
- Decision: open.

- `engine/model/GridCellViewTest.java`: record `TestCellView`
- `engine/model/entity/GridEntityTest.java`: record `TestConstantEntity`, record `TestEntity`
- `engine/support/GridInitializersTest.java`: class `GridInitializersTest`
- `simulations/conway/model/ConwayUserActionTest.java`: class `ConwayUserActionTest`
- `simulations/core/model/SimulationConfigTest.java`: record `TestSimulationConfig`
- `simulations/core/viewmodel/DefaultObservationViewModelTest.java`: record `TestStatistics`
- `simulations/etpets/model/EtpetsGridModelTest.java`: class `EtpetsGridModelTest`
- `simulations/etpets/model/entity/PetGenomeTest.java`: class `StubRandom`
- `simulations/langton/model/LangtonGridModelTest.java`: class `LangtonGridModelTest`
- `simulations/langton/viewmodel/LangtonEditToolBarViewModelTest.java`: class `LangtonEditToolBarViewModelTest`
- `simulations/snake/model/strategy/NamedMoveStrategyTest.java`: record `TestMoveDecision`
- `simulations/sugar/model/SugarGridModelTest.java`: class `SugarGridModelTest`
- `testsupport/FXTestSupport.java`: `awaitLatch()`
- `ui/GridGeometryTest.java`: class `GridGeometryTest`
- `ui/InputDoubleIntRangePropertyTest.java`: class `InputDoubleIntRangePropertyTest`
- `ui/InputDoublePropertyTest.java`: class `InputDoublePropertyTest`

### `TestMethodWithoutAssertion`

- Count: 17. The tests delegate their assertions to helpers: `ConstantTestSupport.assert*` and, in
  `FXGridCanvasPainterTest`, test-local `assert*` methods inside the lambda of `FXTestSupport.runAndWait`.
- Proposal: register the helper methods as assertion methods in the options of the inspection (class and method name
  pattern) and remove all 17 suppressions. Check in IntelliJ that the option also covers the assertions inside the
  lambda; otherwise keep those 8 suppressions.
- Decision: open.

- `core/AppLocalizationKeysTest.java`: `testConstantNamesAreAlphabeticallySorted()`,
  `testConstantsAreUniqueAndNonBlank()`, `testConstantsExistInAllBundles()`
- `simulations/conway/view/ConwayStyleClassesTest.java`: `testConstantNamesAreAlphabeticallySorted()`,
  `testConstantsAreUniqueAndNonBlank()`, `testConstantsUseCssTokenFormat()`
- `ui/FXGridCanvasPainterTest.java`: `testDrawCell()`, `testDrawCellBoundingBox()`, `testDrawCellInnerCircle()`,
  `testDrawCircle()`, `testDrawPolygon()`, `testDrawRectangle()`, `testDrawScaledCell()`, `testFillCanvasBackground()`
- `ui/FXStyleClassesTest.java`: `testConstantNamesAreAlphabeticallySorted()`, `testConstantsAreUniqueAndNonBlank()`,
  `testConstantsUseCssTokenFormat()`

### `HardcodedLineSeparator`

- Count: 4, all at class level, in the tests of skill scripts and in helper classes that build text output.
- Proposal: disable for the scope "Tests"; tests compare text with fixed line separators on purpose.
- Decision: open.

- `simulations/core/model/ExtremaGoldenValueAnalyzer.java`: class `ExtremaGoldenValueAnalyzer`
- `skills/I18nConsistencyCheckTest.java`: class `I18nConsistencyCheckTest`
- `skills/JavaCodeInventoryTest.java`: class `JavaCodeInventoryTest`
- `ui/AboutDialogTest.java`: class `AboutDialogTest`

### `UseOfProcessBuilder`

- Count: 2, in the helpers that start skill scripts.
- Proposal: disable for the scope "Tests"; starting processes is the purpose of these tests.
- Decision: open.

- `skills/I18nConsistencyCheckTest.java`: `runHelper()`
- `skills/JavaCodeInventoryTest.java`: `runHelper()`

### `DataFlowIssue`

- Count: 7, all at method level as `junit.md` prescribes for intentional null-passing tests.
- Proposal: keep the rule; check for each occurrence (especially the four added in `f7266596`) that it covers an
  intentional null-passing test and not a nullable result that the test could handle.
- Decision: open.

- `core/AppArgsTest.java`: `testKeys()`
- `simulations/core/model/StatisticsHistoryTest.java`: `testToListReturnsImmutableSnapshot()`
- `simulations/etpets/model/EtpetsUserActionTest.java`: `testApplySetResourceInsectUsesDeterministicFullDefaults()`,
  `testApplySetResourcePlantUsesDeterministicFullDefaults()`
- `simulations/snake/model/strategy/NamedMoveStrategyTest.java`: `testDecideMove()`
- `simulations/snake/model/strategy/SnakeMoveStrategiesTest.java`: `testStrategies()`
- `simulations/sugar/model/SugarUserActionTest.java`: `testApplyAddSugarAddsResourceWithSelectedLevel()`

### `unused`

- Count: 5, fixture classes whose constants are only read by reflection.
- Proposal: keep.
- Decision: open.

- `testsupport/ConstantTestSupportTest.java`: class `BlankConstants`, class `DuplicateConstants`, class
  `NoStringConstants`, class `SortedConstants`, class `UnsortedConstants`

### `ResultOfMethodCallIgnored`

- Count: 4, in tests of interrupt handling.
- Proposal: check each occurrence: assert the result where it is meaningful (change code), otherwise keep.
- Decision: open.

- `ExtraterrestrialPetsSimulationTest.java`: `testStopInterruptionEscalatesEveryPendingTermination()`,
  `testStopInterruptsTerminationWaitAndRestoresInterruptStatus()`
- `simulations/core/viewmodel/DefaultMainViewModelTest.java`:
  `testShutdownSimulationSuppressesStaleInitializationCompletion()`
- `testsupport/FXTestSupportTest.java`: `testSupplyAndWaitRestoresInterruptedStatus()`

### `ConstantValue`

- Count: 2; the annotation on `EtpetsBalanceTest` is at class level.
- Proposal: keep; narrow the class-level scope of `EtpetsBalanceTest` to the methods that check constants.
- Decision: open.

- `engine/support/WorkCheckpointsTest.java`: `testCancellationCheckInterval()`
- `simulations/etpets/model/EtpetsBalanceTest.java`: class `EtpetsBalanceTest`

### `InsertLiteralUnderscores`

- Count: 2; a weak warning.
- Proposal: change code (insert the underscores) and remove the suppressions, unless the literals are expected values
  that are easier to read without underscores.
- Decision: open.

- `engine/GridOffsetTest.java`: `testEuclideanLength()`
- `engine/GridSizeTest.java`: `testAspectRatio()`

### `SpellCheckingInspection`

- Count: 2, both at class level.
- Proposal: add real domain words to the project dictionary and remove the suppressions; keep a narrowed suppression
  only for artificial test strings.
- Decision: open.

- `simulations/core/SimulationTypeTest.java`: class `SimulationTypeTest`
- `skills/I18nConsistencyCheckTest.java`: class `I18nConsistencyCheckTest`

### Other test inspection types

One occurrence each; decide per occurrence.

- `EqualsWithItself`: `simulations/core/SimulationTerminationHandleTest.java`, `testCompletedReturnsSameInstance()`.
  Proposal: keep.
- `FieldNamingConvention`: `testsupport/FXTestSupport.java`, variable `started`. Proposal: check the name against the
  naming rules; rename or keep.
- `NumericCastThatLosesPrecision`: `skills/I18nConsistencyCheckTest.java`,
  `testFixRejectsMalformedUtf8InSecondBundleWithoutChangingEitherBundle()`. Proposal: keep (the cast builds invalid
  bytes on purpose; to check).
- `OverlyStrongTypeCast`: `simulations/etpets/model/EtpetsUserActionTest.java`, class level. Proposal: narrow the scope
  to the affected method or change the cast.
- `UnusedReturnValue`: `simulations/core/viewmodel/SimulationUserActionDescriptorTest.java`, `createDescriptor()`.
  Proposal: check whether the method can return `void`; otherwise keep.

## 2. Main sources

### `MarkerInterface`

- Count: 9, all sealed or base marker interfaces on purpose (mainly the `*UserActionContext` family).
- Proposal: disable the inspection in the profile, where it is enabled explicitly; remove all 9 suppressions.
- Decision: open.

- `engine/model/entity/ConstantGridEntity.java`: interface `ConstantGridEntity`
- `simulations/conway/shared/ConwayUserActionContext.java`: interface `ConwayUserActionContext`
- `simulations/core/shared/SimulationUserActionContext.java`: interface `SimulationUserActionContext`
- `simulations/etpets/shared/EtpetsUserActionContext.java`: interface `EtpetsUserActionContext`
- `simulations/langton/model/entity/AntEntity.java`: interface `AntEntity`
- `simulations/langton/shared/LangtonUserActionContext.java`: interface `LangtonUserActionContext`
- `simulations/rebounding/shared/ReboundingUserActionContext.java`: interface `ReboundingUserActionContext`
- `simulations/snake/shared/SnakeUserActionContext.java`: interface `SnakeUserActionContext`
- `simulations/sugar/shared/SugarUserActionContext.java`: interface `SugarUserActionContext`

### `InterfaceMayBeAnnotatedFunctional`

- Count: 5, interfaces with one abstract method that are not meant as lambda targets.
- Proposal: disable the inspection; remove all 5 suppressions.
- Decision: open.

- `engine/model/entity/ConstantGridEntity.java`: interface `ConstantGridEntity`
- `engine/model/entity/ConstantGridEntityDescriptorProvider.java`: interface `ConstantGridEntityDescriptorProvider`
- `engine/model/entity/GridEntity.java`: interface `GridEntity`
- `engine/model/entity/SpecBackedGridEntityDescriptorProvider.java`: interface `SpecBackedGridEntityDescriptorProvider`
- `simulations/core/view/SimulationConfigView.java`: interface `SimulationConfigView`

### `SpellCheckingInspection`

- Count: 9, all at class level (constants, style classes, rule presets, and patterns).
- Proposal: add real domain words to the project dictionary and remove the suppressions; for artificial tokens (rule
  strings, pattern data), keep a suppression narrowed to the affected fields.
- Decision: open.

- `core/AppLocalizationKeys.java`: class `AppLocalizationKeys`
- `simulations/conway/model/ConwayPatterns.java`: class `ConwayPatterns`
- `simulations/conway/shared/ConwayRulePresetSquare.java`: enum `ConwayRulePresetSquare`
- `simulations/conway/view/ConwayStyleClasses.java`: class `ConwayStyleClasses`
- `simulations/core/SimulationType.java`: enum `SimulationType`
- `simulations/langton/shared/LangtonRulePresetHexagon.java`: enum `LangtonRulePresetHexagon`
- `simulations/langton/shared/LangtonRulePresetSquare.java`: enum `LangtonRulePresetSquare`
- `simulations/wator/view/WatorConfigView.java`: class `WatorConfigView`
- `ui/FXStyleClasses.java`: class `FXStyleClasses`

### `unchecked`

- Count: 11; 8 of them in `ArrayGridModel`, which casts the entries of its `Object` array to `ENT`.
- Proposal: change code in `ArrayGridModel`: a private method reads and casts an entry, with one method-level
  suppression (8 to 1). Keep the 3 suppressions for generic arrays in `AbstractConfigView` and `FXComponentFactory`.
- Decision: open.

- `engine/model/ArrayGridModel.java`: `getEntity()`, variable `entity` (7x)
- `simulations/core/view/AbstractConfigView.java`: `createLayoutPane()`, `toLabeledControlArray()`
- `ui/FXComponentFactory.java`: `createLabeledChoiceRadioButtons()`

### `ConstantValue`

- Count: 9. Seven are the `updateItem()` override of a `ListCell` (`empty || (item == null)`, together with
  `DataFlowIssue`): six views repeat the cell of `FXComponentFactory.createDisplayNameListCell`.
- Proposal: change code: a shared cell factory in `ui` replaces the six copies, so one suppression pair remains (DRY).
  `CreatureBase.equals()`: declare the parameter `@Nullable Object` or use an `instanceof` pattern as in
  `RadiusRingCell`, and remove the suppression. `FXComponentFactory.createLabeledChoiceComboBox()`: check.
- Decision: open.

- `simulations/conway/view/ConwayMainView.java`: `updateItem()`
- `simulations/etpets/view/EtpetsMainView.java`: `updateItem()` (2x)
- `simulations/langton/view/LangtonMainView.java`: `updateItem()`
- `simulations/rebounding/view/ReboundingMainView.java`: `updateItem()`
- `simulations/sugar/view/SugarMainView.java`: `updateItem()`
- `simulations/wator/model/entity/CreatureBase.java`: `equals()`
- `ui/FXComponentFactory.java`: `createLabeledChoiceComboBox()`, `updateItem()`

### `DataFlowIssue`

- Count: 8; seven are the `updateItem()` overrides (see `ConstantValue`).
- Proposal: decided together with `ConstantValue`; check `DefaultMainViewModel.applyUserAction()` separately.
- Decision: open.

- `simulations/conway/view/ConwayMainView.java`: `updateItem()`
- `simulations/core/viewmodel/DefaultMainViewModel.java`: `applyUserAction()`
- `simulations/etpets/view/EtpetsMainView.java`: `updateItem()` (2x)
- `simulations/langton/view/LangtonMainView.java`: `updateItem()`
- `simulations/rebounding/view/ReboundingMainView.java`: `updateItem()`
- `simulations/sugar/view/SugarMainView.java`: `updateItem()`
- `ui/FXComponentFactory.java`: `updateItem()`

### `SameReturnValue`

- Count: 13; 11 are the static `labelResourceKey()` methods of enums, which return a literal by design (`AGENTS.md`,
  "Localization and Text").
- Proposal: keep. Check `GridStructure.minCoordinateInclusive()` and `SimulationTerminationHandle.completed()` (a
  singleton factory); keep them if the method is part of a consistent API.
- Decision: open.

- `engine/CellShape.java`: `labelResourceKey()`
- `engine/EdgeBehavior.java`: `labelResourceKey()`
- `engine/GridEdgeBehavior.java`: `labelResourceKey()`
- `engine/GridStructure.java`: `minCoordinateInclusive()`
- `engine/neighborhood/CellConnectionType.java`: `labelResourceKey()`
- `engine/neighborhood/CompassDirection.java`: `labelResourceKey()`
- `engine/neighborhood/NeighborhoodMode.java`: `labelResourceKey()`
- `simulations/core/SimulationTerminationHandle.java`: `completed()`
- `simulations/core/SimulationType.java`: `labelResourceKey()`
- `simulations/core/shared/CellDisplayMode.java`: `labelResourceKey()`
- `simulations/core/shared/SimulationMode.java`: `labelResourceKey()`
- `simulations/lab/shared/LabColorMode.java`: `labelResourceKey()`
- `simulations/snake/shared/SnakeDeathMode.java`: `labelResourceKey()`

### `MagicNumber`

- Count: 21, mostly in UI geometry and simulation math; `CellNeighborhoods` is suppressed at class level.
- Proposal: per occurrence: extract named constants where a value has a domain meaning, otherwise keep. Narrow the
  class-level scope of `CellNeighborhoods`.
- Decision: open.

- `ExtraterrestrialPetsSimulation.java`: `resolveScreenContainingStage()`
- `engine/neighborhood/CellNeighborhoods.java`: class `CellNeighborhoods`
- `engine/support/GridInitializers.java`: `fillRandomPercent()`
- `simulations/core/view/AbstractDefaultMainView.java`: `computeStrokeLineWidth()`
- `simulations/core/view/AbstractMainView.java`: `computeCellEmojiFontSize()`, `computeCellFontSize()`,
  `computeVisibleCanvasCenter()`
- `simulations/core/view/StatisticsHistoryChartView.java`: `computeNiceCeiling()`
- `simulations/etpets/model/EtpetsScoreMath.java`: `computeRawReproduceScore()`
- `simulations/etpets/model/entity/PetGenome.java`: `fromParents()`
- `simulations/etpets/model/entity/PetTraits.java`: `genomeQualityScore()`
- `simulations/langton/view/LangtonMainView.java`: `initializeSimulation()`
- `simulations/sugar/model/SugarSimulationManager.java`: `computeClampedIndex()`
- `ui/FXComponentFactory.java`: `computeMinorTickCountForPercentSlider()`, `createLabeledDoubleSlider()`,
  `createLabeledPercentSlider()`
- `ui/FXGridCanvasPainter.java`: `computeCenteredTextOffset()`, `drawCircle()`, `drawRectangle()`
- `ui/FXPaintFactory.java`: `computeBrightnessVariantsMap()`, `createRadialGradient()`

### `SameParameterValue`

- Count: 13; 10 are constructors of enums and entity classes.
- Proposal: per occurrence: remove the parameter where it is not needed (change code), otherwise keep.
- Decision: open.

- `simulations/conway/model/entity/ConwayEntity.java`: `ConwayEntity()`
- `simulations/core/SimulationType.java`: `SimulationType()`
- `simulations/core/view/AbstractConfigView.java`: `formatDoubleRangeTooltip()`
- `simulations/etpets/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/forest/model/entity/ForestEntity.java`: `ForestEntity()`
- `simulations/lab/model/entity/LabEntity.java`: `LabEntity()`
- `simulations/lab/view/LabMainView.java`: `drawBaseCanvasCoordinate()`
- `simulations/langton/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/rebounding/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/snake/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/sugar/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/wator/model/entity/EntityDescriptors.java`: `EntityDescriptors()`
- `simulations/wator/model/entity/TerrainConstant.java`: `TerrainConstant()`

### `NumericCastThatLosesPrecision`

- Count: 12, casts from `double` or `long` to `int` after rounding or clamping.
- Proposal: keep; check each occurrence for a clearer alternative (e.g., `Math.toIntExact`, `Math.clamp`).
- Decision: open.

- `engine/support/GridInitializers.java`: `fillRandomPercent()`, `placeRandomPercent()`
- `simulations/core/model/StatisticsExtremum.java`: `intValue()`
- `simulations/core/view/AbstractObservationView.java`: `updateGenericMetricSection()`
- `simulations/core/viewmodel/DefaultMainViewModel.java`: `configureSimulationTimeout()`
- `simulations/etpets/model/entity/PetGenome.java`: `fromParents()`
- `simulations/snake/model/SnakeSimulationManager.java`: `foodInitializer()`
- `simulations/snake/model/SnakeStepLogic.java`: `moveSnake()`
- `simulations/sugar/model/SugarSimulationManager.java`: `computeClampedIndex()`, `computeSugarAmount()`
- `ui/FXGridCanvasPainter.java`: `isOnCanvas()`
- `ui/GridGeometry.java`: `estimateGridCoordinate()`

### Main inspection types with two occurrences

- `FieldNamingConvention`: `CompassDirection` has one-letter constants (`N`, `E`, `S`, `W`), shorter than the
  minimum length 2 of the constant naming convention in the profile: set the minimum length to 1 or keep.
  `AppLocalization.state`: check the name against the naming rules; rename or keep.
- `ObjectEquality`: `GridCoordinate` compares with its own constant on purpose; keep, or use `equals` if equally fast.
- `OptionalUsedAsFieldOrParameterType`: check whether the callers can pass the value or `null` instead; otherwise
  keep.
- `OverloadedMethodsWithSameNumberOfParameters`: change code: give the two private `clampToMapRange` methods
  distinct names.
- `unused`: keep (parameters that implementations or overrides may ignore).
- Decision: open.

- `core/AppLocalization.java`: variable `state`
- `engine/neighborhood/CompassDirection.java`: enum `CompassDirection`
- `engine/GridCoordinate.java`: `isIllegal()`, `toDisplayString()`
- `simulations/core/view/AbstractObservationView.java`: `updateGenericMetricSection()`, `updateStatusSectionLabel()`
- `simulations/etpets/view/EtpetsMainView.java`: `clampToMapRange()` (2x)
- `simulations/core/model/SimulationUserAction.java`: `apply()`
- `simulations/core/view/AbstractMainView.java`: `createEditToolBarOptionPanel()`

### Other main inspection types

One occurrence each; decide per occurrence.

- `BooleanMethodIsAlwaysInverted`: `simulations/etpets/model/entity/Pet.java`, `hasReachedAgingEffectsAge()`.
  Proposal: change code (invert the method and its name) or keep.
- `CallToSystemExit`: `AppLauncher.java`, `parseArgumentsAndHandleHelp()`. Proposal: keep.
- `ClassReferencesSubclass`: `engine/model/entity/GridEntity.java`, `isConstant()`. Proposal: keep.
- `ExtractMethodRecommender`: `ui/FXComponentFactory.java`, `createLabeledIntSlider()`. Proposal: check the
  recommendation; change code or keep.
- `ParameterHidesMemberVariable`: `simulations/core/view/AbstractDefaultMainView.java`, `drawSimulation()`. Proposal:
  rename the parameter or keep.
- `SwitchExpressionCanBePushedDown`: `simulations/langton/model/LangtonStepRunner.java`,
  `computeNewAntDirection()`. Proposal: check the rewrite; change code or keep.

## Deferred findings

None yet.

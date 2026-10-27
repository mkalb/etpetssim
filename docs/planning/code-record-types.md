# Record Types Overview

**Status:** done

**Related issues:** #23

Temporary working document: an inventory of all record types in the `main` source set, grouped into categories, and
the workflow for aligning them. The maintainer deletes it after all categories are done and pushed.

## Workflow

### Goals

- Every record in `app/src/main/java` complies with `.claude/rules`, in particular its Javadoc and its unit tests.
  Where a rule is missing, unclear, or contradicts a sound existing convention, propose to adjust or extend the rule.
- Records are as similar as sensible and possible in style, layout, format, Javadoc, naming (type, components, and
  methods), member order, validation, annotations, suppressions, and test coverage: strictly within a category, and
  also across category boundaries.
- Similar or identical cases are decided together and the same way, regardless of their category; record such
  decisions under "Cross-category decisions".

### Rules for this refactoring

- Exception to `AGENTS.md`: public API changes (renames, signatures, component order, type kind, type hierarchy) are
  allowed in this refactoring, but each one needs the maintainer's approval before it is implemented.
- Unit tests are always part of the work: check and improve the existing tests of the records, and add missing ones as
  `junit.md` requires. The record test rules in `junit.md` ("Common Contract Patterns") are already set for this plan;
  each category lists the resulting test work under "Known findings".
- Findings that need larger changes or mainly affect other Java types (classes, interfaces, enums) may be moved to
  "Deferred findings" at the end of this plan instead of being implemented; they are handled one by one after the
  final commit.
- Do not update the generated CSV files; this plan and the CSV files may be outdated, so verify every fact against the
  sources.
- Do not commit, review, or delete this plan; the maintainer requests the review and the commit explicitly after all
  categories are done (after formatting, inspecting, and testing in IntelliJ).
- The working tree contains the uncommitted changes of earlier categories. Do not revert them; if `app:test` or
  `app:javadoc` fails because of them, fix the cause. Category 1 is already committed (`6cefb34a`) on the
  maintainer's request.
- Topics of the sibling plans `code-private-constructor-classes.md` and `code-other-classes.md` are out of scope; record
  overlapping findings under "Deferred findings" instead of implementing them.
- All categories of `code-interface-types.md` are done and committed; its decisions apply here, because 27 records
  implement interfaces of that plan, except where this plan supersedes them (see category 6).
  Complete this plan before `code-private-constructor-classes.md`, which builds on both.

### Record-specific guidelines

- Component names and component order are part of the API (canonical constructor and all callers); each change needs
  the maintainer's approval like any other API change. Decide the component order of the nine configs together for
  all of them.
- Validation is in scope: add missing and align inconsistent invariant checks in compact constructors, as `java.md`
  requires. The configs keep validating through `SimulationConfig.isValid()`, as `simulations.md` ("Configuration and
  Input") now states.
- Tests follow the record rules in `junit.md` (see "Cross-category decisions"): a record with a constructor body or an
  overridden `toString()`, `equals(Object)`, or `hashCode()` has a `testRecord`; a record with only own methods tests
  these methods and has no `testRecord`; a pure data carrier has no test of its own; a private nested record is tested
  through its enclosing type. Existing tests that contradict these rules are renamed, merged, or removed as listed
  under "Known findings".
- Private nested records (category 9) are aligned in style, naming, and compact constructors; they get Javadoc only
  where it explains something.
- Records that reference mutable objects: `java.md` ("Java Records") now allows records as shallowly immutable
  wrappers, holders, snapshots, or parameter objects, and requires a class when the type itself owns changing state or
  controls a lifecycle. Check the borderline cases listed under "Known findings" (categories 3, 8, and 9); a change of
  the type kind is an API change and goes to "Deferred findings" if it is larger.
- Boolean components: `java.md` ("Java Records") prefers plain adjectives or noun phrases (`visible`) and allows
  boolean query names (`isFinished`), with one style per record and interface method names taking precedence. Renames
  only for consistency are not required.
- `toDisplayString()`: `java.md` ("Java Records") now requires it for records whose values are shown to users or in
  logs, and not for results, statistics, configs, user action contexts, and private records. Existing declarations
  stay; check in each category that they have a caller and a consistent format, and do not add new ones only for
  completeness.

### Steps per category

Work on exactly one category at a time. The agent may choose the order; suggested order (engine values first, then
the types that build on them, private helpers last): 1, 2, 3, 5, 4, 6, 7, 8, 9.

1. Set the category to `in progress` in the summary table. Read the category section including its "Known findings",
   the relevant rules (`java.md`, `junit.md`, and the path-scoped rules for the affected packages), the CSV entries,
   every record source file of the category, and its existing tests. Also read "Cross-category decisions" and
   "Deferred findings" of this plan and the decisions recorded in `code-interface-types.md`.
2. Analyze: verify the known findings, then look for further rule violations, inconsistencies within the category and
   with already processed categories, test gaps, and possible rule changes.
3. Present the results and recommendations in the chat and ask the open decision questions; mark every recommended
   API change explicitly. Do not change code before the maintainer has decided.
4. Implement the decisions, including required changes to callers, tests, and rules.
5. Run `app:test` and `app:javadoc`; both must pass before the category counts as done.
6. Update this plan: set the category to `done` in the summary table, replace "Known findings" with "Decisions", add
   cross-category decisions and deferred findings, and correct outdated inventory facts, including the cross-cutting
   facts in the summary (visibility, compact constructors, implemented interfaces, generics, `@Nullable` components,
   static factory names, Javadoc, suppressions, and tests).

### Documentation format

- Known findings: a `### Known findings` subsection at the end of each open category section, one bullet per finding,
  each starting with a short topic prefix (e.g., "Tests:", "Javadoc:", "Validation:", "Mutable references:", or "From
  other plans:"). They are verified and decided in step 2 and 3.
- Category decisions: a `### Decisions` subsection that replaces "Known findings" when the category is done, one bullet
  per decision; every known finding is covered by a decision. Prefix a bullet with "API change:" or "Rule change:"
  (naming the rule file) when it applies.
- Cross-category decisions: one bullet per decision with the affected categories in parentheses, e.g.,
  "(1, 2) ...".
- Deferred findings: one `###` subsection per finding with a short title, followed by the bullets "Origin:" (category),
  "Types:" (affected types), "Problem:", and "Proposal:".

Status values in the summary table: `open`, `in progress`, `done`.

## Summary

Sources: `docs/generated/JavaTypeInventory.csv`, `docs/generated/JavaMethodInventory.csv`, checked against the sources
(72 record types). Each record appears in exactly one category. Names are fully qualified; the source file is
`app/src/main/java/` plus the package path. Nested records are marked with "nested" and named with their enclosing
type. "Compact" marks a record with a compact constructor.

| # | Category                                                  | Count | Status |
|---|-----------------------------------------------------------|------:|--------|
| 1 | Grid geometry values                                      |     6 | done   |
| 2 | Neighborhood results                                      |     6 | done   |
| 3 | Grid cells, composite grid models, and entity descriptors |     9 | done   |
| 4 | Simulation configs                                        |     9 | done   |
| 5 | Execution results, statistics, and events                 |     7 | done   |
| 6 | User action contexts and descriptors                      |     9 | done   |
| 7 | Simulation rules, traits, and strategy data               |     7 | done   |
| 8 | UI input properties and view holders                      |     8 | done   |
| 9 | Private nested helper records                             |    11 | done   |
|   | **Total**                                                 |    72 |        |

Cross-cutting facts:

- Added after this plan: the private nested records `SnakeMoveStrategies.MoveWeights` and
  `AppLocalization.LocalizationState` (`code-private-constructor-classes.md`, category 7 and the deferred finding
  "State models of AppLogger and AppLocalization"); they follow the decisions of category 9 and are not counted here.
- 22 records are nested, 50 are top-level. 59 are `public`, 11 `private` (all nested, category 9), and 2
  package-private (`CellNeighborhoods.CellNeighborConnection`, `NamedMoveStrategy`).
- 29 records have a compact constructor; none of the nine configs has one (they validate with `isValid()`). Only
  `StatisticsMetric` (2) and `SimulationUserActionDescriptor` (1) declare additional constructors.
- 27 records implement an interface: `GridCellView` (4), `CompositeGridModel` (3), `SimulationConfig` (9), a user action
  context interface (7), `Comparable` (`GridCoordinate`), `SnakeMoveStrategy` (`NamedMoveStrategy`), `MoveDecision`
  (`SnakeMoveStrategies.ScoredMove`), and `SimulationTerminationHandle`
  (`DefaultMainViewModel.ExecutorSimulationTerminationHandle`).
- 8 records are generic: `GridCell<ENT>`, `RadiusRingCell<C>`, `StatisticsMetric<STA>`,
  `DefaultMainViewModel.SimulationStartRequest<CON>`, `SimulationUserActionDescriptor<CTX>`,
  `FXComponentFactory.LabeledControl<R>`, `InputChoiceProperty<T>`, and `InputEnumProperty<E>`.
- `@Nullable` components exist in `GridEntityDescriptor`, `GridEntityDescriptorSpec`,
  `AbstractObservationView.MetricLabels`, and `EtpetsAgentLogic.ActionCandidate`.
- 15 records declare `toDisplayString()`. Only `RadiusRingCell` overrides `equals(Object)`/`hashCode()`; no record
  overrides `toString()`.
- Static factory names vary: `of`, `from`, `parse`, `empty`, `none`, `square`, `between`, `fromParents`, `ofList`,
  and `ofEnum`.
- 19 records group references to mutable objects: JavaFX properties (the five `Input*Property` records,
  `ConfigValidationRule`), JavaFX nodes (`LabeledControl`, `MetricLabels`, `GroupChart`), objects with a lifecycle
  (`SimulationInstance`, `ExecutorSimulationTerminationHandle`), and live simulation state (the three composite grid
  models, `GridCell` and the three composite cells, `MoveContext`). None of the referenced mutable types overrides
  `equals(Object)` or `hashCode()`, so the record equality compares these components by reference.
- Boolean components: `StepExecutionResult` (`isFinished`, `isInterrupted`) and `ScoredMove` (`isFoodTarget`, from
  `MoveDecision`) use query names; `SimulationStepEvent`, `GridEntityDescriptor`, `GridEntityDescriptorSpec`
  (`visible`), and `SimulationStartRequest` use plain names. No record mixes both styles.
- No record type carries an annotation. `@SuppressWarnings` exists only on methods: `GridCoordinate.isIllegal()` and
  `toDisplayString()` (`ObjectEquality`), `GridStructure.minCoordinateInclusive()` (`SameReturnValue`),
  `PetGenome.fromParents(..)` and `PetTraits.genomeQualityScore()` (`MagicNumber`), and `StatisticsExtremum.intValue()`
  (`NumericCastThatLosesPrecision`).
- `stepCount` is an `int` in all records that have it (category 5).
- Javadoc: 8 of the 11 private records have none (`ActionCandidate`, `MetricLabels`, and
  `ExecutorSimulationTerminationHandle` have it). All other records follow the paragraph rules of `java.md`, have all
  component and type parameter `@param` tags, and document a rejecting compact constructor with `@throws` only (a
  compact constructor that only copies has no Javadoc).
- Tests by the `junit.md` record rules (61 non-private records): 26 records have a constructor body or an override and
  need a `testRecord` (all have one); 22 records have only own methods (none has a `testRecord`); 13 records are pure
  data carriers (none has a test of its own).

## 1. Grid geometry values

Immutable value types for coordinates, sizes, and cell geometry, mostly in `engine`.

| Record                                         | Compact | Components | Other members |
|------------------------------------------------|:-------:|------------|---------------|
| `de.mkalb.etpetssim.engine.GridCoordinate`     |         | `x`, `y` | implements `Comparable`; `isIllegal()`, `isWithinBounds(..)` (2), `isWithinOriginBounds(..)`, three `is*OutOfOriginBounds(..)`, `isEven/OddColumn/Row()`, `isTriangleCellPointingDown()`, `hasHexagonCellYOffset()`, `clampToBounds(..)` (2), `clampToOriginBounds(..)`, `incremented()`, `decremented()`, `offset(..)` (2), `offsetTo(..)`, `compareTo(..)`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.GridOffset`         |         | `dx`, `dy` | static `between(..)`; `add(..)`, `subtract(..)`, `negate()`, `scale(int)`, `isZero()`, `manhattanLength()`, `euclideanLength()`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.GridSize`           | yes     | `width`, `height` | static `isInvalidSize(int)`, `square(int)`; `perimeter()`, `area()`, `isSquare()`, `isLandscape()`, `isPortrait()`, `aspectRatio()`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.GridTopology`       |         | `cellShape`, `gridEdgeBehavior` | static `requiredWidthMultiple(..)`, `requiredHeightMultiple(..)`; instance variants of both, `edgeBehaviorX()`, `edgeBehaviorY()`, `cellVertexCount()`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.GridStructure`      | yes     | `topology`, `size` | static `isValid(..)`; `cellShape()`, `cellCount()`, `edgeBehaviorX()`, `edgeBehaviorY()`, `min/maxCoordinate*()`, `isCoordinateValid(..)`, `coordinatesStream()`, `coordinatesList()`, `toDisplayString()` |
| `de.mkalb.etpetssim.ui.CellDimension`          | yes     | `edgeLength`, `width`, `height`, `halfEdgeLength`, `halfWidth`, `halfHeight`, `innerRadius`, `outerRadius`, `columnWidth`, `rowHeight` | `boundingBoxDimension()`, `boundingBoxAt(..)` (2), `toDisplayString()` |

Notes:

- `GridTopology` and `GridStructure` both expose `edgeBehaviorX()` and `edgeBehaviorY()`, and `GridStructure` also
  delegates `cellShape()`; the same pair exists in the enum `GridEdgeBehavior`.
- Validation helpers are named differently: `GridSize.isInvalidSize(int)` (negative) vs. `GridStructure.isValid(..)`.
- `GridCoordinate` and `GridTopology` have no compact constructor; `GridSize` and `GridStructure` validate in one.
- `CellDimension` is in `ui`, but has the same value character as the engine types.

### Decisions

- API change: the ten unused preset constants of `GridSize` (`EXTRA_SMALL_SQUARE` ... `EXTRA_LARGE_RECTANGLE`) are
  removed; tests use `GridSize.square(GridSize.MIN_SIZE)` instead of `EXTRA_SMALL_SQUARE`.
- Tests: `testRecord` removed from `GridCoordinateTest`, `GridOffsetTest`, and `GridTopologyTest`. The `testRecord` of
  `GridSize`, `GridStructure`, and `CellDimension` no longer checks the generated `toString()`;
  `GridStructureTest.testRecord` checks `topology()` and `size()`.
- Tests: `GridTopologyTest` has one `testToDisplayString` (two tests merged); `GridCoordinateTest.testCompareTo` moved
  to its declaration position; `GridStructureTest.testConstructorRejectsNullArguments` removed (it only checked the
  implicit NPE under `@NullMarked`); `CellDimensionTest.testToDisplayStringUsesUsLocaleFormatting` renamed to
  `testToDisplayString`.
- Validation: `CellDimension` rejects non-finite values (`NaN` and infinity passed all comparisons before); added
  `testConstructorRejectsNonFiniteValues`.
- `GridStructure` shares the multiple checks between its compact constructor and `isValid(..)` through private static
  helpers; the error messages stay separate for width and height. `cellCount()` and `coordinatesList()` use the
  `size` field like the other methods.
- `GridCoordinate`: the three `is*OutOfOriginBounds(..)` methods use private helpers for the per-axis check, and
  `hasHexagonCellYOffset()` delegates to `isOddColumn()`.
- Javadoc: all six records follow the `<p>` rules of `java.md` (type and member Javadoc); boolean results use
  `{@code true}`/`{@code false}`; the per-shape `*_MAX_REQUIRED_*` constants of `GridTopology` are documented as the
  maximum required multiple for `WRAP`; the redundant line comment on `GridSize.MAX_SIZE` is removed. The compact
  constructor Javadoc of `GridSize` has only `@throws` (see "Cross-category decisions").
- `toDisplayString()`: all six keep it. `GridCoordinate` has many callers, `GridStructure` is used in
  `FXGridCanvasPainter.toString()`, `GridTopology` and `GridSize` through `GridStructure`, `CellDimension` now in
  `FXGridCanvasPainter.toString()` instead of the generated `toString()`. `GridOffset.toDisplayString()` stays without
  a caller as part of the engine toolkit (like the unused `GridPattern` defaults in `code-interface-types.md`). All
  `String.format` calls in the six records use `Locale.ROOT` (`CellDimension` before: `Locale.US`).
- Tests (aligned after category 5): `GridSizeTest.testConstructorAcceptsMinAndMaxSize` and
  `GridStructureTest.testConstructorAcceptsValidMultiplesForAllCellShapes` moved into their `testRecord`;
  `GridStructureTest` and `CellDimensionTest` no longer check exception messages (the two message helpers of
  `CellDimensionTest` are replaced by plain `assertThrows(...)`).
- Unchanged: `GridSize.isInvalidSize(int)` keeps its negative form (like `isInvalidValue(..)` of the `Input*Property`
  records); the `edgeBehaviorX()`/`edgeBehaviorY()` delegations stay; `CellDimension` stays in `ui`; the display
  strings keep `×`; plain `int` constants get no tests of their own.

## 2. Neighborhood results

Results of neighborhood and edge behavior calculations.

| Record                                                                   | Nested | Compact | Components | Other members |
|--------------------------------------------------------------------------|:------:|:-------:|------------|---------------|
| `de.mkalb.etpetssim.engine.neighborhood.CellNeighbor`                    |        |         | `startCoordinate`, `direction`, `connection`, `neighborCoordinate` | `isEdgeConnection()`, `isVertexConnection()`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior`    |        |         | `startCoordinate`, `direction`, `connection`, `originalNeighborCoordinate`, `mappedNeighborCoordinate`, `edgeBehaviorAction` | static `of(CellNeighbor, EdgeBehaviorResult)`; `isEdgeConnection()`, `isVertexConnection()`, `toDisplayString()` |
| `de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods.CellNeighborConnection` | nested |  | `offset`, `direction`, `connection` | package-private |
| `de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorResult`              |        |         | `original`, `mapped`, `action` | `toDisplayString()` |
| `de.mkalb.etpetssim.engine.neighborhood.RadiusRingCell`                  |        | yes     | `ring`, `coordinate`, `cell` (type `C`), `reachedFromPreviousRing` | `equals(Object)`, `hashCode()` |
| `de.mkalb.etpetssim.simulations.lab.viewmodel.LabNeighborhoodHighlights` |        | yes     | `ringCellsByRadius`, `validNeighborCoordinates`, `validNeighborsWithEdgeBehavior`, `neighborEdgeBehaviorResults` | - |

Notes:

- `CellNeighbor` and `CellNeighborWithEdgeBehavior` share three components and the methods `isEdgeConnection()` and
  `isVertexConnection()`.
- All three connection records name the connection type component `connection`.
- `CellNeighborWithEdgeBehavior` uses `original*`/`mapped*` prefixes, `EdgeBehaviorResult` uses `original`/`mapped`
  and `action` (vs. `edgeBehaviorAction`).
- `RadiusRingCell` is the only record that overrides `equals(Object)` and `hashCode()`.
- The `toDisplayString()` methods and `isEdgeConnection()`/`isVertexConnection()` have no caller in `main`.

### Decisions

- API change: `CellNeighborhoods.CellNeighborConnection.connectionType` is renamed to `connection`, like in
  `CellNeighbor` and `CellNeighborWithEdgeBehavior`.
- Validation: the compact constructor of `RadiusRingCell` rejects a negative `ring` (`IllegalArgumentException`); its
  Javadoc documents this with `@throws`; added `testConstructorRejectsNegativeRing`.
- `toDisplayString()`: `EdgeBehaviorResult` shows the action in lowercase (`[wrapped] mapped: (15, 0)`), like the
  enum names in `CellNeighbor` and `CellNeighborWithEdgeBehavior`. All three use `String.format(Locale.ROOT, ...)`.
  They stay without a caller as part of the engine toolkit (like `GridOffset.toDisplayString()` in category 1), as do
  `isEdgeConnection()`/`isVertexConnection()`; their duplication in the two `CellNeighbor*` records stays.
- Tests: `testRecord` removed from `CellNeighborTest`, `CellNeighborWithEdgeBehaviorTest`, and
  `EdgeBehaviorResultTest`; `CellNeighborWithEdgeBehaviorTest.testOf` uses a wrapped neighbor and `assertAll`.
- Tests: the three `testToDisplayString` tests compare exact strings instead of `contains` checks.
- Tests: `RadiusRingCellTest.testConstructorCreatesSortedUnmodifiableCopy` became `testRecord` (now also checks
  `ring`, `coordinate`, and `cell`); `testEqualsAndHashCodeUseCoordinateOnly` is split into `testEquals` and
  `testHashCode`, because a test name has one subject.
- Tests: added `LabNeighborhoodHighlightsTest.testRecord` (defensive copies of all four components including the
  nested maps and lists, unmodifiable views, preserved order).
- Tests: `CellNeighborConnection` is a pure data carrier and stays covered by `CellNeighborhoodsTest`.
- Javadoc: the five engine records follow the `<p>` rules of `java.md` (type and member Javadoc; `<br>` removed from
  `EdgeBehaviorResult`); the format of `CellNeighborWithEdgeBehavior.toDisplayString()` is in `{@code}`.
- Unchanged: the component names `original`/`mapped`/`action` of `EdgeBehaviorResult` (a prefix would be redundant
  there); `LabNeighborhoodHighlights` keeps its Javadoc and its compact constructor without Javadoc (no exceptions);
  `LabMainViewModel` keeps `List.copyOf(..)`, because `neighborEdgeBehaviorResults(..)` returns a `Collection`.

## 3. Grid cells, composite grid models, and entity descriptors

Cell views and multi-layer grid models of the engine and the composite simulations (Etpets, Langton, Sugar), plus the
entity descriptor pair.

| Record                                                          | Compact | Implements | Components | Other members |
|-----------------------------------------------------------------|:-------:|------------|------------|---------------|
| `de.mkalb.etpetssim.engine.model.GridCell`                      |         | `GridCellView<ENT>` | `coordinate`, `entity` | - |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsCell`        |         | `GridCellView<EtpetsEntity>` | `coordinate`, `terrainEntity`, `resourceEntity`, `agentEntity` | static `from(..)`; `entity()`, `toDisplayString()`, `isWalkable()` |
| `de.mkalb.etpetssim.simulations.langton.model.LangtonCell`      |         | `GridCellView<LangtonEntity>` | `coordinate`, `terrainEntity`, `antEntity` | static `from(..)`; `entity()`, `toDisplayString()` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarCell`          |         | `GridCellView<SugarEntity>` | `coordinate`, `resourceEntity`, `agentEntity` | static `from(..)`; `entity()`, `toDisplayString()` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsGridModel`   | yes     | `CompositeGridModel<EtpetsEntity>` | `structure`, `terrainModel`, `resourceModel`, `agentModel` | `getEntities(..)`, `layerCount()` |
| `de.mkalb.etpetssim.simulations.langton.model.LangtonGridModel` | yes     | `CompositeGridModel<LangtonEntity>` | `structure`, `terrainModel`, `antModel` | `getEntities(..)`, `layerCount()` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarGridModel`     | yes     | `CompositeGridModel<SugarEntity>` | `structure`, `resourceModel`, `agentModel` | `getEntities(..)`, `layerCount()` |
| `de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec` |       | - | `descriptorId`, `visible`, `shortNameKey`, `longNameKey`, `descriptionKey`, `emojiKey`, `color`, `borderColor` | - |
| `de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor`   |         | - | `descriptorId`, `visible`, `shortName`, `longName`, `description`, `emoji`, `color`, `borderColor` | `emojiAsOptional()`, `colorAsOptional()`, `colorOrFallback()`, `borderColorAsOptional()`, `borderColorOrFallback()`, `toDisplayString()` |

Notes:

- The three composite cells follow the same pattern (static `from(model, coordinate)`, one component per layer,
  overridden `entity()` and `toDisplayString()`), as do the three composite grid models.
- Layer components are named after the layer entity type: `terrain*`, `resource*`, `agent*`, and `ant*` (Langton).
- `GridCell` does not declare `toDisplayString()` and inherits the default from `GridCellView`; the composite cells
  override it.
- `GridEntityDescriptorSpec` (resource keys) and `GridEntityDescriptor` (resolved texts) have the same eight-component
  shape; `emoji*`, `color`, and `borderColor` are `@Nullable` in both.

### Decisions

- API change: Langton uses "terrain" instead of "ground" everywhere, like the established `TerrainConstant` and the
  Etpets layer names: `LangtonGridModel.groundModel` became `terrainModel`, `LangtonCell.terrainConstant` became
  `terrainEntity`; in `LangtonStepRunner` `switchGround(..)` became `switchTerrain(..)` and
  `resolveGroundAndCountVisit(..)` became `resolveTerrainAndCountVisit(..)`; local names in `LangtonUserAction` and
  `LangtonMainView` follow. The descriptor IDs (`terrainunvisited`, `terrain0` ... `terrain15`), the resource keys
  (`langton.entity.terrain.*`), their texts ("Terrain"/"Gelände"), `docs/simulations/langton.md`, and
  `Simulation_Entity_Catalog.md` were changed accordingly. `TerrainConstant` keeps its name.
- `entity()`: the priority check uses the entity contract instead of `instanceof`: `SugarCell` uses
  `agentEntity.isNotEmpty()` like `EtpetsCell`, `LangtonCell` uses `antEntity.isAgent()` (Langton entities have no
  `isEmpty()`).
- `toDisplayString()`: the three composite cells and the `GridCellView` default use `String.format(Locale.ROOT, ...)`;
  their Javadoc gives the format and an example in one `<p>` paragraph like `GridCellView`.
  `GridEntityDescriptor.toDisplayString()` stays without a caller in `main`, because `java.md` names descriptors as
  records with a display string.
- Javadoc: all nine records follow the `<p>` rules of `java.md` (type and member Javadoc, no `</p>` and `<br>`);
  "ET-Pets" became "ET Pets"; `GridEntityDescriptor` links to `GridEntityDescriptorSpec` with `@see`.
- Javadoc: the composite grid models document `getEntities(..)` with the layer order (as `CompositeGridModel`
  requires), their compact constructor with `@throws IllegalArgumentException` only, and the layer descriptions in
  the same wording.
- Mutable references: the composite grid models only group the writable layers of a running simulation (no own
  state, no lifecycle) and stay records; the cells and `GridCell` hold entity references without copies. Both is now
  stated in the type Javadoc.
- Tests: added `EtpetsGridModelTest`, `LangtonGridModelTest`, and `SugarGridModelTest` (`testRecord`,
  `testConstructorRejectsDifferentStructure` for each layer, `testGetEntities`, `testLayerCount`) and
  `EtpetsCellTest`, `LangtonCellTest`, and `SugarCellTest` (`testFrom`, `testEntity` for every priority case,
  `testToDisplayString` with the exact string, `EtpetsCell.testIsWalkable`; no `testRecord`). `GridCell` is a pure
  data carrier and has no test; the `GridCellView` defaults stay in `GridCellViewTest`.
- Tests: `GridEntityDescriptorSpecTest` removed (pure data carrier). `GridEntityDescriptorTest`:
  `testToDisplayStringUsesShortName` became `testToDisplayString`; `testColorOrFallback` and
  `testBorderColorOrFallback` also check the configured color; the tests use `assertAll` and follow the declaration
  order.
- Unchanged: the compact constructors of the grid models keep one combined check and message; the descriptors get no
  validation; `layerCount()` stays a constant.

## 4. Simulation configs

One `*Config` record per simulation, each implementing `SimulationConfig` and overriding `isValid()`. No config has a
compact constructor. The first six components are always `cellShape`, `gridEdgeBehavior`, `gridWidth`, `gridHeight`,
`cellEdgeLength`, `cellDisplayMode`.

| Record                                                        | Components | Position of `seed` / `neighborhoodMode` | Simulation-specific components | Private validation helpers |
|---------------------------------------------------------------|-----------:|:---------------------------------------:|--------------------------------|----------------------------|
| `de.mkalb.etpetssim.simulations.conway.model.ConwayConfig`    | 10 | 7 / 9  | `alivePercent`, `transitionRules` | `hasValidRanges()`, `hasValidTransitionRules()` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig`    | 13 | 7 / 13 | `rockPercent`, `waterPercent`, `plantPercent`, `insectPercent`, `petCount` | `hasValidRanges()`, `hasValidCombinedRanges()` |
| `de.mkalb.etpetssim.simulations.forest.model.ForestConfig`    | 11 | 7 / 9  | `treeDensity`, `treeGrowthProbability`, `lightningIgnitionProbability` | `hasValidRanges()` |
| `de.mkalb.etpetssim.simulations.lab.model.LabConfig`          |  9 | 8 / 9  | `colorMode` (position 7) | - |
| `de.mkalb.etpetssim.simulations.langton.model.LangtonConfig`  |  9 | 7 / 8  | `movementRules` | `hasValidMovementRules()` |
| `de.mkalb.etpetssim.simulations.rebounding.model.ReboundingConfig` | 10 | 7 / 10 | `verticalWalls`, `movingEntityPercent` | `hasValidRanges()` |
| `de.mkalb.etpetssim.simulations.snake.model.SnakeConfig`      | 16 | 7 / 12 | `verticalWalls`, `foodCells`, `snakes`, `initialPendingGrowth`, `deathMode`, `growthPerFood`, `basePointsPerFood`, `segmentLengthMultiplier` | `hasValidRanges()` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarConfig`      | 18 | 7 / 14 | `agentPercent`, `sugarPeaks`, `sugarRadiusLimit`, `minSugarAmount`, `maxSugarAmount`, `agentInitialEnergy`, `sugarRegenerationRate`, `agentMetabolismRate`, `agentVisionRange`, `agentMaxAge` | `hasValidRanges()`, `hasValidCombinedRanges()` |
| `de.mkalb.etpetssim.simulations.wator.model.WatorConfig`      | 20 | 7 / 10 | `fishPercent`, `sharkPercent`, `fishMaxAge`, `fishMinReproductionAge`, `fishMinReproductionInterval`, `sharkMaxAge`, `sharkBirthEnergy`, `sharkEnergyLossPerStep`, `sharkEnergyGainPerFish`, `sharkMinReproductionAge`, `sharkMinReproductionEnergy`, `sharkMinReproductionInterval` | `hasValidRanges()`, `hasValidCombinedRanges()` |

Notes:

- The components follow the groups Structure, Layout, Initialization (starting with `seed`), and Rules (starting with
  `neighborhoodMode`); `LabConfig.colorMode` belongs to Layout, so its `seed` is at position 8.
- Percent and density naming varies: `*Percent`, `treeDensity`, and `movingEntityPercent`; all store a fraction.
- `verticalWalls` exists in both Rebounding and Snake.

### Decisions

- API change: `LangtonConfig.langtonMovementRules` became `movementRules` (like `ConwayConfig.transitionRules`);
  `LangtonStepRunner` and the tests follow.
- Validation: all nine `isValid()` call `isBaseValid()` and `hasAllowedCoreSelections(..)` first and use the same
  order of checks. The combined checks are named `hasValidCombinedRanges()` in Etpets, Sugar, and Wa-Tor (before:
  `hasValidCombinedPercents()`, `hasValidCombinedPopulationShare()`); `SugarConfig.hasExpectedInitializationRules()`
  is inlined as `hasExpectedSelection(minSugarAmount, ..)` like the `neighborhoodMode` checks. Forest, Lab,
  Rebounding, and Wa-Tor offer all neighborhood modes and need no `neighborhoodMode` check.
- Rule change: `simulations.md` ("Configuration and Input") defines the component groups with their line comments
  and the order of checks in `isValid()`; the component order of the nine configs already follows it and is
  unchanged.
- Javadoc: every config names its simulation (by its title), states the four groups, and refers to `isValid()`; all
  share components (`*Percent`, `treeDensity`) are documented as a fraction of all cells; the `isValid()` Javadoc
  uses one wording ("the X-specific selections, ranges, ..."); "ET-Pets" became "ET Pets".
- Tests: the nine `*ConfigTest` classes create variants through a private `isValidWith(Consumer<ConfigValues>)`
  helper and a mutable `ConfigValues` fixture at the bottom of the class (one field per component, initialized with
  the defaults, and `toConfig()`).
- Tests: every config test has `testIsValidAcceptsDefaultConstraints`,
  `testIsValidAcceptsSupportedAlternativeSelections` (where alternatives exist), `testIsValidAcceptsBoundaryValues`
  (every range at its minimum and maximum), one
  `testIsValidRejectsUnsupported<Selection>` per restricted selection list, `testIsValidRejectsUnexpected<Value>` per
  fixed value, and one `testIsValidRejects<Component>OutsideRange` per range that checks both sides (`MIN - 1` or
  `Math.nextDown`, `MAX + 1` or `Math.nextUp`).
- Tests: added the missing cases `EtpetsConfigTest` (`waterPercent`, `plantPercent`, `insectPercent`),
  `SnakeConfigTest.testIsValidRejectsUnsupportedGridEdgeBehavior`, and
  `WatorConfigTest.testIsValidRejectsPopulationShareSumAboveOne`. Etpets uses `Unsupported` instead of `Unexpected`
  for its selection lists; Lab and Wa-Tor use `testIsValidAcceptsSupportedAlternativeSelections` (two Wa-Tor tests
  merged); `EtpetsConfigTest.testIsValidAcceptsTotalPercentBelowLimit` is removed (same as the defaults test).
- Unchanged: `treeDensity` (the domain term of the forest-fire model and its UI label) and the count components
  `petCount`, `snakes`, `foodCells`, `verticalWalls`, `sugarPeaks` (they match their UI labels);
  `SugarConfig.hasValidCombinedRanges()` stays although `minSugarAmount` is fixed to the minimum of `maxSugarAmount`.

## 5. Execution results, statistics, and events

Results of step execution and statistics data, in `engine.executor`, `simulations.core.model`, and
`simulations.core.shared`.

| Record                                                              | Compact | Components | Other members |
|---------------------------------------------------------------------|:-------:|------------|---------------|
| `de.mkalb.etpetssim.engine.executor.StepExecutionResult`            | yes | `stepCount`, `executedSteps`, `isFinished`, `isInterrupted` | - |
| `de.mkalb.etpetssim.engine.executor.StepTimingStatistics`           | yes | `currentNanos`, `minNanos`, `maxNanos`, `sumNanos`, `avgNanos` | static `empty()` |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsMetric`        | yes | `key`, `labelKey`, `extractor`, `extremaMode`, `chartGroup`, `chartWindowSize` | two additional constructors with defaults |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsSample`        | yes | `stepCount`, `stepTimingStatistics`, `values` | - |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsExtrema`       | yes | `minimumValues`, `maximumValues` | static `empty()` |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsExtremum`      | yes | `value`, `stepCount` | `intValue()` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationStepEvent`    | yes | `batchModeRunning`, `stepCount`, `finalStep` | - |

Notes:

- Boolean component naming is mixed: `isFinished`/`isInterrupted` (accessors `isFinished()`, `isInterrupted()`) vs.
  `batchModeRunning`/`finalStep`; the private `ScoredMove` (category 9) also uses `isFoodTarget`.
- `stepCount` appears in four of these records, always as `int`.
- All seven have a compact constructor.

### Decisions

- API change: `StatisticsExtremum.stepCount` and the parameter of `StatisticsExtremaTracker.recordSample(..)` changed
  from `long` to `int`, like `stepCount` in the other records and in `StatisticsSample`, its source.
- Javadoc: the compact constructors that reject arguments (all except `StatisticsExtrema`) document their invariants
  with `@throws IllegalArgumentException`; the constraint sentences in the type Javadoc of `StepExecutionResult` and
  `StepTimingStatistics` moved there. Both records follow the `<p>` rules of `java.md`. `StatisticsSample` and
  `StatisticsExtremum` describe `stepCount` as a step count, not a step index.
- Tests: the positive constructor cases are one `testRecord` per record (stored values, the zero step count boundary,
  defensive copies, order, unmodifiable views, the `NaN` gap value): `StepTimingStatisticsTest`,
  `StatisticsExtremaTest`, `StatisticsSampleTest`, and `StatisticsExtremumTest` merged their tests;
  `testEmptyReturnsAllZeros`, `testEmptyReturnsInstanceWithoutValues`, and `testIntValueUsesNarrowingConversion` became
  `testEmpty` and `testIntValue`.
- Tests: `StatisticsExtremaTest.testConstructorRejectsNullMaps` removed (implicit NPE under `@NullMarked`, as in
  category 1).
- Tests: the statistics tests use plain `assertThrows(...)` without the `assertNotNull(...)` wrapper and without
  message checks, like the rest of the test sources; `StatisticsExtremaTrackerTest` and
  `DefaultObservationViewModelTest` use `int` step counts.
- Tests: added `SimulationStepEventTest` (`testRecord`, `testConstructorRejectsNegativeStepCount`).
- Unchanged: the boolean components `isFinished`/`isInterrupted` (they match `SimulationExecutor.isFinished()`) and
  `batchModeRunning`/`finalStep`; `StatisticsMetric` and its tests; no record gets `toDisplayString()`.

## 6. User action contexts and descriptors

Parameterized user action contexts (nested in the sealed context interfaces) and the records that describe the
actions offered in the UI.

| Record                                                                             | Nested | Compact | Components | Other members |
|------------------------------------------------------------------------------------|:------:|:-------:|------------|---------------|
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext.PlacePattern`      | nested |  | `patternChoice` | - |
| `de.mkalb.etpetssim.simulations.etpets.shared.EtpetsUserActionContext.SetTerrain`        | nested |  | `terrainChoice` | - |
| `de.mkalb.etpetssim.simulations.etpets.shared.EtpetsUserActionContext.SetResource`       | nested |  | `resourceChoice` | - |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext.AddAnt`          | nested |  | `direction` | - |
| `de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext.AddRebounder` | nested | | `direction` | - |
| `de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext.AddSnake`            | nested |  | `strategy` | - |
| `de.mkalb.etpetssim.simulations.sugar.shared.SugarUserActionContext.AddSugar`            | nested |  | `level` | - |
| `de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor`           |        | yes | `toolId`, `scope`, `labelKey`, `tooltipKey`, `contextResolver` | additional constructor `(String, CTX, SimulationUserActionScope, String, String)`; `resolveContext()` |
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayPatternChoice`                       |        | yes | `choiceId`, `labelKey`, `patternSupplier`, `availabilityRule` | `pattern()`, `isAvailableFor(..)` |

Notes:

- The seven context records are single-component wrappers; the component type is an enum
  (`EtpetsTerrainChoice`, `EtpetsResourceChoice`, `CompassDirection`, `SugarAddSugarLevel`), a strategy, or the record
  `ConwayPatternChoice`.
- Names follow verb patterns `Add*`, `Set*`, and `PlacePattern`.
- `ConwayPatternChoice` is a record because it carries a pattern supplier. Like the toolbar choice enums
  (`EtpetsTerrainChoice`, `EtpetsResourceChoice`, `SugarAddSugarLevel`) it exposes `labelKey()`, but it keeps
  `choiceId()`, which the enums no longer have; `ConwayEditToolBarViewModel` uses it.
- ID naming varies: `toolId` here, `choiceId` in `ConwayPatternChoice`.

### Decisions

- Tests: the seven context records are pure data carriers; the six `*UserActionContextTest` classes (only their
  record tests) are deleted. This supersedes the decision in `code-interface-types.md` (category 7) to keep them.
- Tests: added `SimulationUserActionDescriptorTest` (`testRecord`, `testConstructorWithContext` for the additional
  constructor, `testConstructorRejects...` for a blank `toolId`, the reserved `SELECT_TOOL_ID`, a blank `labelKey`,
  and a blank `tooltipKey`, and `testResolveContext` with the empty and the present case). `SELECT_TOOL_ID` gets no
  test of its own (plain constant, as in category 1).
- Tests: `ConwayPatternChoiceTest.testConstructorAcceptsValidArguments` became `testRecord`;
  `testConstructorRejectsNullChoiceId` and `testConstructorRejectsNullLabelKey` are removed (implicit NPE under
  `@NullMarked`, as in categories 1 and 5); the rejection tests no longer check exception messages;
  `testPatternReturnsNormalizedPattern` became `testPattern` and `testIsAvailableForDelegatesToPredicate` became
  `testIsAvailableFor`.
- Javadoc: `ConwayPatternChoice` has type Javadoc with `@param` for all four components, compact constructor Javadoc
  with `@throws` only, and Javadoc for `pattern()` (a new normalized pattern per call).
- Javadoc: the compact constructor of `SimulationUserActionDescriptor` documents its invariants with `@throws`; the
  additional constructor, which delegates to it, has the same `@throws`.
- Unchanged: the component names of the seven context records (named after their type), `choiceId` of
  `ConwayPatternChoice` (`<thing>Id` like `toolId`), the parameter order of the additional
  `SimulationUserActionDescriptor` constructor (`toolId`, `context`, ...), and the `choiceId`/`labelKey` difference
  check; no record of this category gets `toDisplayString()`.

## 7. Simulation rules, traits, and strategy data

Simulation-specific domain values: rules, pet genetics, and the data of Snake move strategies.

| Record                                                                | Compact | Components | Other members |
|-----------------------------------------------------------------------|:-------:|------------|---------------|
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayTransitionRules`  | yes | `surviveCounts`, `birthCounts` | static `of(..)`, `parse(String)`; `shouldSurvive(int)`, `shouldBeBorn(int)`, `toDisplayString()` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules`  | yes | `turns` | static `parse(String)`; `turnForState(int)`, `ruleCount()`, `isValidForCellShape(..)`, `toDisplayString()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.PetGenome`        |     | `traits` | static `fromParents(..)`; private static `mutate(..)` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.PetTraits`        | yes | `maxEnergy`, `movementCostModifier`, `reproductionMinEnergy`, `reproductionCooldown` | `genomeQualityScore()`; private static `normalize(..)` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.Pet.PetLastAction` |    | `type`, `score` | nested |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.MoveContext`     |     | `snakeHead`, `headCoordinate`, `model`, `groundNeighbors`, `foodNeighbors`, `structure`, `neighborDirectionRing`, `config`, `random` | - |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.NamedMoveStrategy` |   | `displayName`, `strategy` | package-private; implements `SnakeMoveStrategy`; `decideMove(..)` |

Notes:

- Both rule records have a static `parse(String)` and `toDisplayString()`; only Conway also has `of(..)`.
- `PetGenome` wraps a single `PetTraits` component without own validation.
- `Pet.PetLastAction` and the private `EtpetsAgentLogic.ActionCandidate` (category 9) both carry `type` and `score`.

### Decisions

- Validation: `ConwayTransitionRules` copies both sets in ascending natural order; before, a set with its own
  comparator (e.g., reverse order) kept it, and `toDisplayString()` returned `32/3` instead of `23/3`. The range check
  and the copy use private static helpers (`checkNeighborCounts(..)`, `copyInAscendingOrder(..)`).
- Validation: `PetTraits` has a compact constructor that rejects traits outside their `PET_TRAITS_*_RANGE_MIN`/`MAX`
  bounds (since `code-private-constructor-classes.md`, category 2: `PET_TRAITS_*_MIN`/`MAX`) in `EtpetsBalance`
  (including `NaN`), which `genomeQualityScore()` assumes; all callers already stay within them.
- `NamedMoveStrategy` no longer overrides `toString()`; `SnakeHead.toDisplayString()` uses `strategy.displayName()`
  instead of the implicit `toString()`, so the display string is unchanged and `toString()` stays technical.
- Javadoc: `ConwayTransitionRules` follows the `<p>` rules (no `<br>`, no em dash), states the range 0 to 9 instead
  of "non-negative", and its compact constructor has `@throws` only. `LangtonMovementRules` documents its compact
  constructor (`@throws`) and the accepted characters and the exception of `parse(String)`. `PetGenome.fromParents(..)`
  documents mutation, rounding, and clamping; `PetTraits` documents the ranges and the range of
  `genomeQualityScore()`. `Pet.PetLastAction` and `NamedMoveStrategy` have type Javadoc.
- Mutable references: `MoveContext` stays without a compact constructor (it only bundles the data of one move
  decision; no defensive copies). Its type Javadoc no longer says "Immutable" and states that the references are not
  copied. `java.md` now allows this for short-lived parameter objects (see "Cross-category decisions").
- Tests: `ConwayTransitionRulesTest` has a `testRecord` (boundary values, empty sets, natural order of a reverse-ordered
  set, defensive copy, unmodifiable sets), one test per method (`testOf`, `testShouldSurvive`, `testShouldBeBorn`,
  `testToDisplayString`) and the specific `testParse...` cases; the two round-trip tests became
  `testToDisplayStringIsParsable`; the tests of the generated `equals(Object)`/`hashCode()` are removed.
- Tests: added `LangtonMovementRulesTest` (`testRecord`, `testConstructorRejectsTooFewTurns`/`TooManyTurns`,
  `testParse`, `testParseAcceptsAliases`, `testParseRejectsUnknownCharacter`/`TooFewTurns`, `testTurnForState`,
  `testRuleCount`, `testIsValidForCellShape`, `testToDisplayString`).
- Tests: added `PetTraitsTest` (`testRecord` with both range ends, one `testConstructorRejects<Trait>OutsideRange` per
  trait, `testGenomeQualityScore`) and `PetGenomeTest` (`testFromParents`, `testFromParentsAppliesMutation`,
  `testFromParentsClampsToTraitRanges`, with a `StubRandom` fixture).
- Tests: added `NamedMoveStrategyTest.testDecideMove` (delegation); no `testRecord`, because the record no longer
  overrides `toString()`. `Pet.PetLastAction` and `MoveContext` are pure data carriers and have no test.
- Unchanged: the static factory names `of`/`parse`, the `MIN_*`/`MAX_*` constants, and the Snake term "ground" in
  `MoveContext.groundNeighbors` (it matches `SnakeEntity.isGround()`). The semantics of `genomeQualityScore()` went
  to "Deferred findings".

## 8. UI input properties and view holders

Records in `ui` and the view layer that wrap JavaFX properties or group view objects.

| Record                                                                          | Nested | Compact | Components | Other members |
|---------------------------------------------------------------------------------|:------:|:-------:|------------|---------------|
| `de.mkalb.etpetssim.ui.InputChoiceProperty`                                     |        | yes | `property`, `validValues`, `displayNameProvider` | static `ofList(..)`, `ofEnum(..)`, package-private `isInvalidValue(..)`; `asStringBinding(..)`, `getValue()`, `setValue(..)`, `isValue(..)`, `hasMultipleValidValues()` |
| `de.mkalb.etpetssim.ui.InputEnumProperty`                                       |        | yes | `property`, `validValues`, `displayNameProvider` | static `of(..)` (2), package-private `isInvalidValue(..)`; same instance methods as `InputChoiceProperty` |
| `de.mkalb.etpetssim.ui.InputIntegerProperty`                                    |        | yes | `property`, `min`, `max`, `step` | static `of(..)`, package-private `isInvalidValue(..)`; `asObjectProperty()`, `asStringBinding(..)`, `getValue()`, `setValue(..)`, `adjustValue(..)` |
| `de.mkalb.etpetssim.ui.InputDoubleProperty`                                     |        | yes | `property`, `min`, `max` | same members as `InputIntegerProperty` with `double` |
| `de.mkalb.etpetssim.ui.InputDoubleIntRangeProperty`                             |        | yes | `property`, `min`, `max` (`int`) | same members as `InputDoubleProperty` |
| `de.mkalb.etpetssim.ui.FXComponentFactory.LabeledControl`                       | nested |     | `label`, `controlRegion` | - |
| `de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel.CommonConfigSettings` | nested | yes | 18 components: `*Initial`, `*Values`, `*Min`, `*Max`, `*Step` for the common config fields | - |
| `de.mkalb.etpetssim.simulations.core.SimulationInstance`                        |        |     | `simulationType`, `simulationMainView`, `region` | package-private static `of(..)` |

Notes:

- `InputChoiceProperty` and `InputEnumProperty` have identical components and instance methods; their factories are
  named differently (`ofList`/`ofEnum` vs. overloaded `of`).
- `InputDoubleProperty` and `InputDoubleIntRangeProperty` differ only in the type of `min`/`max`.
- `CommonConfigSettings` stores `cellEdgeLength*` as `int` while `SimulationConfig.cellEdgeLength()` is `double`, and
  `seedInitial` as `String` while `seed()` is `long`.

### Decisions

- API change: `SimulationInstance.toDisplayString()` is removed (no caller; the logs use `simulationType()`).
- Mutable references: `SimulationInstance` stays a record. It only passes the view on; `ExtraterrestrialPetsSimulation`
  controls the lifecycle through `simulationMainView().shutdownSimulation()`, as its type Javadoc now states. The
  `Input*Property` records and `LabeledControl` state in their type Javadoc that they only wrap or group the JavaFX
  objects without copies.
- Javadoc: the five `Input*Property` records, `LabeledControl`, and `SimulationInstance` follow the `<p>` rules of
  `java.md`. The compact constructors of the `Input*Property` records have `@throws` with the concrete conditions
  instead of `@param` tags and "if any argument is invalid". `CommonConfigSettings` keeps its compact constructor
  without Javadoc (it only copies).
- Tests: the five `Input*PropertyTest` classes follow one scheme in declaration order: `testRecord` (stored
  components, accepted boundaries, defensive copy and unmodifiable list for the choice records), one
  `testConstructorRejects...` per rejected case without message checks, `testOf` (`testOfList`/`testOfEnum`,
  `testOfWithEnumClass`), and one test per method (`testIsInvalidValue`, `testAsObjectProperty`, `testAsStringBinding`,
  `testSetValue`, `testAdjustValue`, `testIsValue`, `testHasMultipleValidValues`) plus `testSetValueAcceptsInvalidValue`
  for the logging factory properties. The `testOfRejects...` duplicates of the constructor rejections are removed.
  `NaN` is now covered as an invalid value of both double records.
- Tests: added `AbstractConfigViewModelTest.testCommonConfigSettingsRecord` (stored values, defensive copies,
  unmodifiable lists).
- Tests: `SimulationInstanceTest` keeps only `testOf`; `testRecord`, `testToDisplayString`, and the three tests of the
  generated `equals(Object)` are removed. `LabeledControl` stays covered by `FXComponentFactoryTest`.
- Unchanged: the types of `CommonConfigSettings` (`int` cell edge lengths for `InputDoubleIntRangeProperty`, `String`
  seed for `SeedProperty`), the factory names (`ofList`/`ofEnum` vs. `of`), and `ui.md` ("Input Property Records"),
  because `junit.md` covers the record tests. The duplication of `InputChoiceProperty` and `InputEnumProperty` went to
  "Deferred findings".

## 9. Private nested helper records

Private tuples inside a single class, used as local result or state holders.

| Record                                                                                   | Compact | Components |
|------------------------------------------------------------------------------------------|:-------:|------------|
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsAgentLogic.ActionCandidate`           | yes | `type`, `score`, `moveTarget`, `interactionTarget`, `eggTarget` (`@Nullable`) |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsAgentLogic.ReproductionOption`        | yes | `partnerCell`, `eggCoordinate` |
| `de.mkalb.etpetssim.simulations.etpets.model.EtpetsAgentLogic.ActionEffect`              | yes | `eggCountDelta`; private static `none()` |
| `de.mkalb.etpetssim.simulations.sugar.model.SugarAgentLogic.AgentMoveCandidate`          |     | `target`, `moveTo`, `targetSugarAmount`, `distanceToTarget` |
| `de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategies.ScoredMove`     |     | `score`, `targetCoordinate`, `direction`, `isFoodTarget`; implements `MoveDecision` |
| `de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel.SimulationStartRequest` |  | `config`, `startPaused`, `timedMode`, `continuousBatchMode`, `stepDurationMillis`, `stepCount`, `terminationChecked` |
| `de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel.ExecutorSimulationTerminationHandle` | | `executor`; implements `SimulationTerminationHandle` |
| `de.mkalb.etpetssim.simulations.core.viewmodel.AbstractConfigViewModel.ConfigValidationRule` | | `violatedCondition`, `messageProvider` |
| `de.mkalb.etpetssim.simulations.core.view.AbstractObservationView.MetricLabels`          |     | `currentLabel`, `minLabel`, `minTooltip`, `maxLabel`, `maxTooltip` (all but `currentLabel` `@Nullable`) |
| `de.mkalb.etpetssim.simulations.core.view.StatisticsHistoryChartView.GroupChart`         |     | `chart`, `xAxis`, `yAxis` |
| `de.mkalb.etpetssim.ui.AboutDialog.ManifestLine`                                         |     | `attributeName`, `localizationKey` |

Notes:

- The Etpets helpers have compact constructors, the comparable Sugar and Snake helpers do not.
- `ScoredMove` and `ExecutorSimulationTerminationHandle` are the only private records that implement a public
  interface.
- Candidate records exist in Etpets (`ActionCandidate`), Sugar (`AgentMoveCandidate`), and Snake (`ScoredMove`) with
  different naming.

### Decisions

- Tests: the eleven private records are tested through their enclosing types; no tests of their own.
- Javadoc: only `ActionCandidate` (meaning of `moveTarget`, `interactionTarget`, and the `@Nullable` `eggTarget`;
  compact constructor with `@throws`), `MetricLabels` (the `@Nullable` labels and tooltips of metrics without
  minimum or maximum), and `ExecutorSimulationTerminationHandle` (the ViewModel shuts the executor down; the handle
  only waits or forces termination) have Javadoc; the other eight have none.
- Mutable references: `ExecutorSimulationTerminationHandle` stays a record; it only delegates `awaitTermination(..)`
  and `shutdownNow()`, while `DefaultMainViewModel` creates the executor and calls `shutdown()`. `MetricLabels`,
  `GroupChart`, and `ConfigValidationRule` are allowed by `java.md` and get no Javadoc.
- Style: `EtpetsAgentLogic` uses the accessor `option.partnerCell()` instead of the field `option.partnerCell`.
- Style: empty record bodies are written as `{` and `}` on two lines; `MetricLabels`, `GroupChart`, and, from earlier
  categories, `CellNeighborConnection`, `LabeledControl`, `MoveContext`, and `GridCell` changed from `{}` (see
  "Cross-category decisions").
- Unchanged: no compact constructors for `AgentMoveCandidate` and `ScoredMove` (their values are created locally);
  the `EtpetsAgentLogic: ` prefix of the messages in the three Etpets records (like the other messages of their
  class); the boolean component names of `SimulationStartRequest` and `ScoredMove`; the different candidate names in
  Etpets, Sugar, and Snake.

## Cross-category decisions

- (all) Rule change: `junit.md` ("Test Method Style" and "Common Contract Patterns"): a record with a constructor body
  (compact or explicit canonical) or an override of `toString()`, `equals(Object)`, or `hashCode()` has a
  `testRecord` (the positive constructor case with the stored values, normalization, and defensive copies;
  `toString()` only when overridden); rejections are tested in `testConstructorRejects...`, and its own methods as
  usual. A record with only own methods tests these methods and has no `testRecord`; this covers the configs without
  an exception. A pure data carrier has no test of its own, and a private nested record is tested through its
  enclosing type. Existing positive
  constructor tests under other names become the `testRecord`; `testRecord` tests of records without a constructor
  body, record tests of pure data carriers, and tests of generated `equals(Object)`/`hashCode()` are removed.
- (all) Rule change: `java.md` ("Java Records"): a record declares `toDisplayString()` when its values are shown to
  users or in logs; results, statistics, configs, user action contexts, and private records need none, and none is
  added only for completeness.
- (3, 7, 8, 9) Rule change: `java.md` ("Java Records"): records are shallowly immutable and may group references to
  mutable objects as a wrapper, holder, snapshot, or parameter object; a type that owns changing state or controls a
  lifecycle is a class. This replaces "Do not use records for mutable JavaFX state, properties, or classes with
  identity/lifecycle semantics" and resolves the contradiction with `ui.md` ("Input Property Records").
- (3, 5, 9) Rule change: `java.md` ("Java Records"): boolean components prefer plain adjectives or noun phrases
  (`visible`); boolean query names (`isFinished`) are allowed, with one style per record and interface method names
  taking precedence. The generated accessors are exempt from the boolean query prefixes of "Naming Conventions".
- (4) Rule change: `simulations.md` ("Configuration and Input"): `*Config` records have no validating constructor and
  are checked with `isValid()` (which calls `isBaseValid()` first) before they reach the Model layer.
- (1, all) Rule change: `java.md` ("Naming Conventions", `toDisplayString()`): `toDisplayString()` is
  locale-independent and formats numbers with `Locale.ROOT`; localized UI text uses `AppLocalization` or
  `NumberFormat`. Apply it to every record with `toDisplayString()` or `String.format` in later categories.
- (1, all) Rule change: `java.md` ("Javadoc", "Tags"): the Javadoc of a compact constructor documents its invariants
  with `@throws` and has no `@param` tags (the components are documented in the record Javadoc). Remove the `@param`
  tags of the other compact constructors in their categories (e.g., the `Input*Property` records,
  `ConwayTransitionRules`).
- (1, 2, all) Rule change: `junit.md` ("Common Contract Patterns") and `java.md` ("Naming Conventions",
  `toDisplayString()`): `testToDisplayString` compares the exact display string with `assertEquals`, not with
  `contains` checks; `toDisplayString()` shows enum values in lowercase (`name().toLowerCase(Locale.ROOT)`) unless the
  enum provides its own display text (e.g., `CompassDirection.arrow()` or an entity's `toDisplayString()`).
- (3, 7, 8, 9) Rule change: `java.md` ("Java Records"): a record that groups references to mutable objects says so in
  the last paragraph of its type Javadoc in one sentence of the form "The record wraps/groups/holds ... without
  copying ...", optionally followed by the consequence after a semicolon (e.g., "The record wraps the given property
  without copying it; bindings and listeners on the property stay effective." or "The record holds the entity
  references read at creation without copying the entities; later changes to a mutable entity (e.g., `Pet`) are
  visible through the record."). A private record does so only when the sentence explains something (category 9:
  only `ExecutorSimulationTerminationHandle`).
- (7) Rule change: `java.md` ("Java Records"): a short-lived parameter object that only bundles data for one call
  (`MoveContext`) may keep collections without defensive copies; this resolves the former deferred finding "Defensive
  copies in parameter objects".
- (1, 2, 3, 5, all) Rule change: `java.md` ("Javadoc", "Tags"): every compact constructor that rejects arguments has
  Javadoc with `@throws`; the invariants are stated there, not in the type Javadoc. A compact constructor that only
  copies needs no Javadoc.
- (5, all) Rule change: `junit.md` ("Test Method Style" and "Common Contract Patterns"): accepted boundary values of
  the constructor (e.g., a zero step count) belong in `testRecord`; rejections use plain `assertThrows(...)` without
  an `assertNotNull(...)` wrapper or message checks.
- (1, 5, 6) Rule change: `junit.md` ("Test Method Style"): no tests for the implicit `NullPointerException` of a
  `null` argument for a non-null parameter under `@NullMarked` (e.g., the removed `testConstructorRejectsNull...`).
- (4, all) Rule change: `junit.md` ("Common Contract Patterns"): a record whose tests need many variants of its
  components (the configs) builds them through a private mutable fixture with default values and a `Consumer`-based
  helper, not through helpers with one parameter per component.
- (3, all) Rule change: `simulations.md` ("Entity Design", "Naming"): layer and component names use one term per
  concept in a simulation and follow the established type names (`terrain` for `TerrainConstant` layers, also in
  Langton); resource keys and descriptor IDs follow the same term.
- (1, 2, 3, 7, 8, 9) Style: an empty record body is written as `{` and `}` on two lines, not as `{}`; the maintainer
  decided against a rule for it.

## Deferred findings

The findings from `code-interface-types.md` were moved to the "Known findings" of categories 3 and 7.

All categories are done and committed. The deferred findings were analyzed on 2026-10-23 (bullets "Analysis:" and
"Recommendation:") and decided on 2026-10-25 (bullets "Decision:"): the first and the third are implemented and
committed (`86ca6224`, `59e9e58e`), the second moved to `code-private-constructor-classes.md`, the fourth is issue
#85, and the fifth is part of issue #70. Nothing remains open; the rule decisions are already in `.claude/rules`, so
no facts need to move to `docs/development`, and the plan can be deleted.

### Locale of toDisplayString() in entity classes

- Origin: category 1
- Types: `Fish`, `Shark` (Wa-Tor), `Sugar`, `Agent` (Sugar), `SnakeHead`
- Problem: their `toDisplayString()` formats numbers with the default locale (`String.format` without locale or
  `String.formatted(..)`), while `java.md` now requires `Locale.ROOT`; the Etpets entities already use it.
- Proposal: use `String.format(Locale.ROOT, ...)` in these methods; check their tests.
- Analysis: confirmed for all five types. The output does not change in practice, because `%d` without the `,` flag
  adds no grouping separators in `de_DE` or `en_US` either; the change only aligns the code with `java.md`. `Ant` and
  `Rebounder` also format without a locale, but only insert arrows (`direction.arrow()`), no numbers.
  `SnakeHead.toString()` uses `.formatted(..)` with `%d`, but `toString()` is not covered by the rule.
- Recommendation: implement now (small, no risk). Leave `Ant`, `Rebounder`, and `SnakeHead.toString()` unchanged.
- Decision (maintainer, 2026-10-25): implemented for all twelve entity classes that override `toDisplayString()`
  (also `Ant`, `Rebounder`, `Pet`, `PetEgg`, `Plant`, `Insect`, `Trail`): one style
  `String.format(Locale.ROOT, "[TYPE ...]",` with one argument per line, and one format
  `[TYPE #id *step KEY=value ...]`. Format changes: `Sugar` shows `A=current/max` like `Plant` and `Insect`; `Pet`
  shows its last action in lowercase (`A=eat@12`) and a dead pet with a trailing `†` (like `SnakeHead`) instead of
  `S=ALIVE`/`S=DEAD`. Each class has a new `<Entity>Test.testToDisplayString`. The enum entities keep the default
  `[NAME]` from `GridEntity`. In `toString()`, `Sugar` and `Agent` close with `'}'` like the others, `Fish` and
  `Shark` no longer show the fixed `descriptorId`, and `SnakeHead` shows `strategy.displayName()` instead of the
  generated record string with the lambda; the summaries (`reproductionCount`, snake segments), the field order, and
  the duplicated methods of `Fish`/`Shark` and `Plant`/`Insect` stay.

### Names of the combined range constants

- Origin: category 4
- Types: `EtpetsConstraints`, `WatorConstraints` (plan `code-private-constructor-classes.md`)
- Problem: the upper limit of the combined shares is named `TOTAL_PERCENT_MAX` (Etpets) and
  `POPULATION_SHARE_SUM_MAX_INCLUSIVE` (Wa-Tor), while the configs now use one helper name,
  `hasValidCombinedRanges()`.
- Proposal: align both constant names when the constraints classes are processed.
- Analysis: confirmed. Both constants are inclusive upper limits (`<=` in `EtpetsConfig.hasValidCombinedRanges()` and
  `WatorConfig.hasValidCombinedRanges()`) and are also used in `EtpetsConfigViewModel` and `WatorConfigViewModel`.
  The suffix convention of the constraints constants (`*_MAX` or `*_MAX_INCLUSIVE`) belongs to the constraints
  classes as a whole and should not be decided for these two constants alone.
- Recommendation: move this finding to the "Deferred findings" of `code-private-constructor-classes.md` and decide it
  with the other constraints constants there; a candidate name is `COMBINED_PERCENT_MAX` (like
  `hasValidCombinedRanges()` and `EtpetsConstraints.PERCENT_MAX`).
- Decision (maintainer, 2026-10-25): moved to the notes of category 2 of `code-private-constructor-classes.md`; no
  change in this plan.

### assertThrows wrapper in StatisticsHistoryTest

- Origin: category 5
- Types: `StatisticsHistoryTest`, `StatisticsExtremaTrackerTest`, `SimulationManagerTest`, `GridGeometryTest`,
  `FXGridCanvasPainterTest`
- Problem: `StatisticsHistoryTest` still wraps `assertThrows(...)` in `assertNotNull(...)`, and the other four check
  exception messages (e.g., `getMessage().contains("dup")`), while `junit.md` ("Test Method Style") now requires plain
  `assertThrows(...)` unless the message is part of the contract. `FXTestSupportTest` and
  `SimulationInitializationCanceledExceptionTest` check messages that are part of the contract and stay.
- Proposal: use plain `assertThrows(...)` in the five test classes.
- Analysis: confirmed: `StatisticsHistoryTest` has three `assertNotNull(assertThrows(...))` wrappers (two in the
  constructor test, one for the unmodifiable snapshot); `StatisticsExtremaTrackerTest` (1), `GridGeometryTest` (2), and
  `FXGridCanvasPainterTest` (1) check message fragments; `SimulationManagerTest` checks the message "Simulation
  initialization was canceled." in eight `testConstructorHonorsCancellationFor...` tests. That message is already
  covered by `SimulationInitializationCanceledExceptionTest`, so the exception type is enough there.
- Recommendation: implement now (test-only change). `GridGeometryTest` and `FXGridCanvasPainterTest` belong to the
  topics of `code-other-classes.md`, but the change is too small to wait for that plan.
- Decision (maintainer, 2026-10-25): implemented in all five test classes; they use plain `assertThrows(...)`. The
  eight cancellation tests of `SimulationManagerTest` check only the exception type, and
  `StatisticsExtremaTrackerTest` no longer checks the duplicate key in the message. `FXTestSupportTest` and
  `SimulationInitializationCanceledExceptionTest` keep their message checks.

### Genome quality score of disadvantageous traits

- Origin: category 7
- Types: `PetTraits.genomeQualityScore()`, `EtpetsAgentLogic` (`computeReproduceScore(..)`), `EtpetsScoreMath`
- Problem: the score averages the normalized values of all four traits, so a higher `movementCostModifier`,
  `reproductionMinEnergy`, and `reproductionCooldown` raise it although they are disadvantages; the score feeds the
  reproduction score.
- Proposal: decide the intended semantics with the maintainer (game balance), e.g., count the disadvantageous traits
  as `1 - normalized`, and adapt the Javadoc, `PetTraitsTest`, and `docs/simulations` if needed.
- Analysis: confirmed: a pet with all traits at their minimum scores `0.0`, although a low `movementCostModifier`
  (range 0.5 to 1.5) and a short `reproductionCooldown` (100 to 300) are advantages. The only caller is
  `EtpetsAgentLogic`, which passes the scores of both parents to `EtpetsScoreMath.computeRawReproduceScore(..)`. This
  is a game balance question, not a record or style topic: a change alters the simulation behavior and needs
  observation in the running simulation. Open: whether a high `reproductionMinEnergy` (30 to 80) is a disadvantage or
  a cautious strategy, and whether `maxEnergy` should weigh more.
- Recommendation: track it outside this plan, as a GitHub issue (`enhancement` or `bug`, with `needs-refinement`)
  or, if it has low priority, as a plan with status `idea`.
- Decision (maintainer, 2026-10-25): added to issue #70 (ET Pets review, sub-issue of #24) as a known finding with
  one analysis and one decision question. No change in this plan.

### Duplicated choice and enum input properties

- Origin: category 8
- Types: `InputChoiceProperty`, `InputEnumProperty`, their callers (`FXComponentFactory`, `DefaultControlViewModel`,
  `ConwayRuleProperty`, `LangtonRuleProperty`, and the config ViewModels of Forest, Lab, Rebounding, and Wa-Tor),
  `ui.md`, and `simulations.md`
- Problem: both records have the same components, validation, and instance methods; `InputChoiceProperty.ofEnum(..)`
  already covers the enum case of `InputEnumProperty.of(.., Class, ..)`. Only `isValue(..)` differs (`Objects.equals`
  vs. `==`).
- Proposal: replace `InputEnumProperty` with `InputChoiceProperty` (`ofEnum` and `ofList`) and update the callers,
  the tests, and the rules that name `InputEnumProperty`; an API change that needs the maintainer's approval.
- Analysis: confirmed. For enums, `==` and `Objects.equals(..)` behave the same, so the merge changes no behavior.
  The caller list above is outdated: `InputEnumProperty` is only used in `FXComponentFactory`,
  `DefaultControlViewModel`, `LabConfigViewModel`, and `ConwayRuleProperty`; Forest, Rebounding, Wa-Tor, Langton, and
  Snake already use `InputChoiceProperty`. Also affected: `InputEnumPropertyTest` (removed), `FXComponentFactoryTest`,
  `simulations.md` ("Configuration and Input" names `InputEnumProperty`), and
  `ui.md` if needed.
- Recommendation: track it as a GitHub issue (`refactoring`, no `needs-refinement`), because it is concrete, bounded
  work that another agent can implement alone; show the issue text to the maintainer before creating it.
- Decision (maintainer, 2026-10-25): tracked as issue #85 (`refactoring`); it also renames the two `FXComponentFactory`
  methods to `createLabeledChoice*` and makes them generic. No change in this plan.

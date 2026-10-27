# Enum Types Overview

**Status:** done

**Related issues:** #23, #64, #65

Temporary working document: an inventory of all enum types in the `main` source set, grouped into categories. It is the
basis for later alignments and optimizations of the enum types. All ten categories have been reviewed; the changes from
the reviews are committed (`fde09c8f`). The notes per category record the decisions, and "Deferred findings" at the end
lists the follow-up work: two findings are done, the `LabColorMode` localization is tracked in #64, and a shared
interface for `resourceKey()` is tracked in #65 (it depends on the key placement decision in #64). All open findings
are now tracked in issues, so this document can be deleted.

Sources: `docs/generated/JavaTypeInventory.csv`, `docs/generated/JavaMethodInventory.csv`, checked against the sources
(62 enum types). Each enum appears in exactly one category. Names are fully qualified; the source file is
`app/src/main/java/` plus the package path. Inner enums are marked with "inner" and named with their enclosing type.

## Summary

| #  | Category                                                     | Count |
|----|--------------------------------------------------------------|------:|
| 1  | Pure value enums                                             |     9 |
| 2  | Localized enums with `resourceKey()`                         |     9 |
| 3  | Toolbar choices with `labelKey()`                            |     3 |
| 4  | Rich domain enums with several resource keys                 |     2 |
| 5  | Rule presets with `ruleString()` and `displayName()`         |     6 |
| 6  | `EntityDescriptors` providers                                |     6 |
| 7  | Self-describing constant entities                            |     4 |
| 8  | Constant entities with `descriptorId()`                      |     9 |
| 9  | User action contexts                                         |     7 |
| 10 | Technical and state enums without localization               |     7 |
|    | **Total**                                                    |    62 |

Cross-cutting facts:

- Inner enums (11): `AppArgs.Key`, `AppLocalization.CountryLocale`, `AppLogger.LogLevel`,
  `AppStorage.OperatingSystem`, `SimulationTerminationHandle.CompletedSimulationTerminationHandle`,
  `LangtonMovementRules.AntTurn`, and the five `*UserActionContext.FixedAction` enums. All other enums are top-level.
- All enums are `public` (the inner `ConwayUserActionContext.FixedAction` implicitly, as a member of an interface).
- No enum overrides `toString()` (the six rule presets did until the review of category 5). `toDisplayString()`
  exists in `LangtonMovementRules` (a class), not in an enum.
- Three naming schemes exist for a per-constant resource key: `resourceKey()` (category 2), `labelKey()`
  (category 3), and `*Key()`/`*ResourceKey()` (category 4).

## 1. Pure value enums

Constants only: no fields, no constructor, no methods, no interfaces.

| Enum                                                                         | Inner | Constants |
|------------------------------------------------------------------------------|:-----:|-----------|
| `de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction`                  |       | `VALID`, `BLOCKED`, `WRAPPED`, `ABSORBED` |
| `de.mkalb.etpetssim.ui.CellShapeSide`                                        |       | `TOP`, `BOTTOM`, `LEFT`, `RIGHT` |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsChartGroup`             |       | `NONE`, `PRIMARY`, `SECONDARY` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationStartMode`             |       | `START_IMMEDIATELY`, `START_PAUSED` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationTerminationCheckMode`  |       | `CHECKED`, `UNCHECKED` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope`       |       | `CELL_SELECTED`, `GLOBAL` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.PetActionType`           |       | `EAT`, `REPRODUCE`, `MOVE`, `WAIT` |
| `de.mkalb.etpetssim.simulations.lab.shared.LabColorMode`                     |       | `COLOR`, `GRAYSCALE` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonMovementRules.AntTurn` | inner | `LEFT`, `RIGHT`, `LEFT2`, `RIGHT2`, `NONE`, `U_TURN` |

## 2. Localized enums with `resourceKey()`

Uniform pattern: `private final String resourceKey` set by the constructor and `public String resourceKey()`. All
except `SimulationNotificationType` also have `public static String labelResourceKey()` (annotated
`@SuppressWarnings("SameReturnValue")`) returning a constant from `AppLocalizationKeys` (`ENUM_LABEL_*`).

| Enum                                                                   | `labelResourceKey()` | Additional members |
|------------------------------------------------------------------------|:--------------------:|--------------------|
| `de.mkalb.etpetssim.engine.CellShape`                                  | yes | `vertexCount()` |
| `de.mkalb.etpetssim.engine.EdgeBehavior`                               | yes | - |
| `de.mkalb.etpetssim.engine.GridEdgeBehavior`                           | yes | `edgeBehaviorX()`, `edgeBehaviorY()`, `hasEqualEdgeBehaviors()` |
| `de.mkalb.etpetssim.engine.neighborhood.CellConnectionType`            | yes | - |
| `de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode`              | yes | `includesVertexNeighbors()` |
| `de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode`           | yes | `hasBorder()` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationMode`            | yes | - |
| `de.mkalb.etpetssim.simulations.snake.shared.SnakeDeathMode`           | yes | - |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationNotificationType` | no | - |

Notes:

- `EdgeBehavior` and `CellConnectionType` are not shown in the UI, so their keys are used only by tests. They keep
  `labelResourceKey()` and `resourceKey()` so that all localized `engine` enums follow the same pattern.
- The tests check that each key exists in all main resource bundles
  (`LocalizationTestSupport.assertKeysInAllBundles(...)`), not the literal key values.

## 3. Toolbar choices with `labelKey()`

Edit toolbar options of a simulation; the label key is passed to the constructor and exposed as `labelKey()`, as
`.claude/rules/java.md` requires for edit toolbar choices.

| Enum                                                                | Members |
|---------------------------------------------------------------------|---------|
| `de.mkalb.etpetssim.simulations.etpets.shared.EtpetsResourceChoice` | `labelKey()` |
| `de.mkalb.etpetssim.simulations.etpets.shared.EtpetsTerrainChoice`  | `labelKey()` |
| `de.mkalb.etpetssim.simulations.sugar.shared.SugarAddSugarLevel`    | `labelKey()`, `computeSugarAmount(int)` |

Notes:

- The former ID methods `choiceId()` and `levelId()` were removed in the review of category 3; the edit toolbar
  ViewModels compare the enum constants directly. `ConwayPatternChoice` (a record, not an enum) keeps its
  `choiceId()`.

## 4. Rich domain enums with several resource keys

Many fields and methods, several resource keys per constant, and a static `labelResourceKey()`.

| Enum                                                      | Key methods | Other members |
|-----------------------------------------------------------|-------------|---------------|
| `de.mkalb.etpetssim.engine.neighborhood.CompassDirection` | static `labelResourceKey()`, `nameResourceKey()`, `abbreviationResourceKey()` | `level()`, `arrow()`, `opposite()`, `nextClockwise()`, `nextCounterClockwise()`, `listClockwise()`, `listCounterClockwise()`, `streamClockwise()`, `streamCounterClockwise()`, static `distance(..)`, static `distanceOnRing(..)` |
| `de.mkalb.etpetssim.simulations.core.SimulationType`      | static `labelResourceKey()`, `titleKey()`, `subtitleKey()`, `urlKey()` | static `fromCliArgument(..)`, `isImplemented()`, `isShownOnStartScreen()`, `cssPath()`, `cliArguments()`, `title()`, `subtitle()`, `url()`, `uri()`, `cssUrl()` |

Notes:

- Key methods use the suffixes `ResourceKey()` and `Key()` side by side.
- `CompassDirection.labelResourceKey()` and `abbreviationResourceKey()` are not used in `main`; they are kept, like
  the keys of the `engine` enums in category 2.
- `SimulationType`: the declaration order defines the order on the start screen. URL keys may be missing in any
  resource bundle and are not tested; subtitle keys are treated as optional as well, because `subtitle()` returns an
  `Optional`. The `implemented` flag is kept although all constants are currently implemented.

## 5. Rule presets with `ruleString()` and `displayName()`

All implement a rule preset interface that exposes the rule string as `ruleString()`.

| Enum                                                                      | Implements          | Members |
|---------------------------------------------------------------------------|---------------------|---------|
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetHexagon`    | `ConwayRulePreset`  | `ruleString()`, `displayName()`, `recommendedDensityPercent()` |
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetSquare`     | `ConwayRulePreset`  | `ruleString()`, `displayName()`, `recommendedDensityPercent()` |
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayRulePresetTriangle`   | `ConwayRulePreset`  | `ruleString()`, `displayName()`, `recommendedDensityPercent()` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetHexagon`  | `LangtonRulePreset` | `ruleString()`, `displayName()` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetSquare`   | `LangtonRulePreset` | `ruleString()`, `displayName()` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonRulePresetTriangle` | `LangtonRulePreset` | `ruleString()`, `displayName()` |

Notes:

- Conway presets store a separate, non-localized `displayName`; Langton presets return the rule string from both
  `ruleString()` and `displayName()`.
- Each preset enum has an `EMPTY` constant with an empty rule string.
- Until the review of category 5, the rule string was exposed through `toString()`; `ConwayRulePresetTest` and
  `LangtonRulePresetTest` now check the interface contracts, including the documented declaration order.

## 6. `EntityDescriptors` providers

Pure descriptor holders: each constant builds a `GridEntityDescriptorSpec` and returns it from `descriptorSpec()`. The
entities themselves are other types (see category 8).

| Enum                                                                       | Implements |
|----------------------------------------------------------------------------|------------|
| `de.mkalb.etpetssim.simulations.etpets.model.entity.EntityDescriptors`     | `SpecBackedGridEntityDescriptorProvider` |
| `de.mkalb.etpetssim.simulations.langton.model.entity.EntityDescriptors`    | `SpecBackedGridEntityDescriptorProvider` |
| `de.mkalb.etpetssim.simulations.rebounding.model.entity.EntityDescriptors` | `SpecBackedGridEntityDescriptorProvider` |
| `de.mkalb.etpetssim.simulations.snake.model.entity.EntityDescriptors`      | `SpecBackedGridEntityDescriptorProvider` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.EntityDescriptors`      | `SpecBackedGridEntityDescriptorProvider` |
| `de.mkalb.etpetssim.simulations.wator.model.entity.EntityDescriptors`      | `SpecBackedGridEntityDescriptorProvider` |

Notes:

- All six share an identical constructor `(String, boolean, String, String, String, String, Color, Color)` and an
  identical `descriptorSpec()` body. This duplication is kept deliberately; enums cannot share a base class.
- Each `EntityDescriptorsTest` checks unique descriptor IDs and the existence of all descriptor keys
  (`LocalizationTestSupport.assertDescriptorKeysInAllBundles(...)`).

## 7. Self-describing constant entities

The enum is the grid entity and its own descriptor provider: it builds a `GridEntityDescriptorSpec` and returns it
from `descriptorSpec()`.

| Enum                                                                  | Implements | Other members |
|-----------------------------------------------------------------------|------------|---------------|
| `de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity`     | `ConstantGridEntityDescriptorProvider` | `isDead()`, `isAlive()` |
| `de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity`     | `ConstantGridEntityDescriptorProvider` | `isEmpty()`, `isTree()`, `isBurning()` |
| `de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity`           | `ConstantGridEntityDescriptorProvider` | `isNormal()`, `isHighlighted()` |
| `de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant` | `LangtonEntity`, `ConstantGridEntityDescriptorProvider` | static `requireByRuleIndex(int)`, `isUnvisited()`, `ruleIndex()`, `isAgent()` |

Notes:

- `ConwayEntity`, `ForestEntity`, and `LabEntity` use the same constructor signature as the `EntityDescriptors`
  enums (category 6).
- The Langton `TerrainConstant` builds its spec from a rule index and a color; the other `TerrainConstant` enums
  belong to category 8.
- Langton registers `EntityDescriptors` and `TerrainConstant` together through
  `LangtonEntity.allEntityDescriptorProviders()`; `LangtonEntityTest` checks that their descriptor IDs are unique.

## 8. Constant entities with `descriptorId()`

Implement `ConstantGridEntity` plus a simulation-specific entity interface and return a descriptor ID; the descriptor
itself lives in the simulation's `EntityDescriptors` (category 6).

| Enum                                                                     | Implements | Members |
|--------------------------------------------------------------------------|------------|---------|
| `de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent`             | `AgentEntity`, `ConstantGridEntity` | `descriptorId()`, `isAgent()`, `isEmpty()` |
| `de.mkalb.etpetssim.simulations.langton.model.entity.NoAgent`            | `AntEntity`, `ConstantGridEntity` | `descriptorId()`, `isAgent()` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.NoAgent`              | `AgentEntity`, `ConstantGridEntity` | `descriptorId()`, `isAgent()`, `isEmpty()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource`          | `ResourceEntity`, `ConstantGridEntity` | `descriptorId()`, `isResource()`, `isEmpty()` |
| `de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource`           | `ResourceEntity`, `ConstantGridEntity` | `descriptorId()`, `isResource()`, `isEmpty()` |
| `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant`     | `TerrainEntity`, `ConstantGridEntity` | `descriptorId()`, `isWalkable()` |
| `de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant` | `ReboundingEntity`, `ConstantGridEntity` | `descriptorId()`, `isGround()`, `isWall()`, `isRebounder()` |
| `de.mkalb.etpetssim.simulations.snake.model.entity.TerrainConstant`      | `SnakeEntity`, `ConstantGridEntity` | `descriptorId()`, `isAgent()` |
| `de.mkalb.etpetssim.simulations.wator.model.entity.TerrainConstant`      | `WatorEntity`, `ConstantGridEntity` | `descriptorId()`, `isAgent()`, `isFish()`, `isShark()`, `isWater()` |

Notes:

- `NoAgent` exists three times and `NoResource` twice with the same names in different simulation packages, as
  recommended by `.claude/rules/simulations.md`.
- `NoAgent` and `NoResource` use descriptor IDs without a registered descriptor; empty cells are not drawn.
- The `TerrainConstantTest` classes of ET Pets, Rebounding, Snake, and Wa-Tor check that the descriptor IDs are
  registered in the simulation's `EntityDescriptors`.

## 9. User action contexts

Constants only, each implementing a user action context interface. Used as type-safe values of a sealed or marker
interface.

| Enum                                                                                       | Inner | Implements | Constants |
|--------------------------------------------------------------------------------------------|:-----:|------------|-----------|
| `de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext`                           |       | `SimulationUserActionContext` | `NO_CONTEXT` |
| `de.mkalb.etpetssim.simulations.wator.shared.WatorUserActionContext`                       |       | `SimulationUserActionContext` | `ADD_FISH`, `ADD_SHARK`, `REMOVE_CREATURE` |
| `de.mkalb.etpetssim.simulations.conway.shared.ConwayUserActionContext.FixedAction`         | inner | `ConwayUserActionContext` | `CLEAR_GRID`, `TOGGLE_CELL` |
| `de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext.FixedAction`       | inner | `LangtonUserActionContext` | `REMOVE_ANT` |
| `de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext.FixedAction` | inner | `ReboundingUserActionContext` | `ADD_WALL`, `FILL_WALLS`, `REMOVE_WALL`, `REMOVE_REBOUNDER` |
| `de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext.FixedAction`           | inner | `SnakeUserActionContext` | `ADD_WALL`, `REMOVE_WALL`, `ADD_FOOD`, `REMOVE_FOOD`, `REMOVE_SNAKE` |
| `de.mkalb.etpetssim.simulations.sugar.shared.SugarUserActionContext.FixedAction`           | inner | `SugarUserActionContext` | `REMOVE_SUGAR` |

Notes:

- `WatorUserActionContext` is the only simulation-specific user action context that is a plain enum instead of a
  sealed interface with a `FixedAction` enum, as `.claude/rules/simulations.md` describes for actions without
  parameters.
- The declaration order of these enums has no meaning (the views define the toolbar order), so it is not tested.

## 10. Technical and state enums without localization

No resource keys; contain fields, parsing, state logic, or an interface implementation.

| Enum                                                                                                   | Inner | Members |
|--------------------------------------------------------------------------------------------------------|:-----:|---------|
| `de.mkalb.etpetssim.core.AppArgs.Key`                                                                  | inner | static `fromString(String)`, static `printHelp(Appendable)`, `key()`, `description()`, `isFlag()` |
| `de.mkalb.etpetssim.core.AppLocalization.CountryLocale`                                                | inner | `countryLocale()`, `displayName()`, `languageCode()`, `countryCode()`, `localeCode()` |
| `de.mkalb.etpetssim.core.AppLogger.LogLevel`                                                           | inner | static `fromString(String)`, package-private `toJavaLogLevel()` |
| `de.mkalb.etpetssim.core.AppStorage.OperatingSystem`                                                   | inner | static `detect()` |
| `de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle.CompletedSimulationTerminationHandle` | inner | `awaitTermination(long, TimeUnit)`, `shutdownNow()` (implements `SimulationTerminationHandle`) |
| `de.mkalb.etpetssim.simulations.core.model.StatisticsExtremaMode`                                      |       | `includesMinimum()`, `includesMaximum()` |
| `de.mkalb.etpetssim.simulations.core.shared.SimulationState`                                           |       | `isStartable()`, `isRunning()`, `isPaused()` |

Notes:

- `AppArgs.Key.fromString(String)` and `AppLogger.LogLevel.fromString(String)` both parse a name into an `Optional`;
  `SimulationType.fromCliArgument(..)` (category 4) is similar.
- `CountryLocale.displayName()` returns a hard-coded native language name, not a resource key.
- `CompletedSimulationTerminationHandle` is a singleton implementation of an interface.
- The declaration order of `AppArgs.Key` (help output) and `CountryLocale` (matching precedence) is documented and
  tested; the order of the other enums in this category has no meaning.
- `OperatingSystem.detect()` reads the global system property `os.name` and is deliberately not tested.

## Deferred findings

Findings from the category reviews that are not implemented in the review itself.

- Done: `de.mkalb.etpetssim.simulations.langton.model.LangtonStepRunner`: the `default -> null` branch of the
  `AntTurn` switch for `TRIANGLE` was replaced by `case LEFT2, RIGHT2, NONE -> null`, so the switch is exhaustive and
  a new `AntTurn` constant causes a compile error. Behavior is unchanged: `LangtonConfig` already rejects these turns
  on triangle cells through `LangtonMovementRules.isValidForCellShape(...)`, and a `null` result still ends in the
  existing `IllegalStateException`. The inner `CompassDirection` switches keep `default -> null`, because only some
  directions are valid per cell shape. The method was not restructured (it carries
  `@SuppressWarnings("SwitchExpressionCanBePushedDown")`), and no test was added.
- Tracked in #64 (decided not to change it in the review session):
  `de.mkalb.etpetssim.simulations.lab.shared.LabColorMode` is offered as a config selection but has no localization.
    - Findings: `LabConfigViewModel` creates the property with
      `InputEnumProperty.of(COLOR_MODE_DEFAULT, COLOR_MODE_VALUES, Enum::toString)`, so the radio buttons show the raw
      names `COLOR` and `GRAYSCALE` in all languages. The field label comes from the view-local key
      `lab.config.color` ("Color:" / "Farbe:", constant `LAB_CONFIG_COLOR` in `LabConfigView`); the tooltip key
      `lab.config.color.tooltip` stays. Per `.claude/rules/java.md`, config selection enums follow the
      `labelResourceKey()`/`resourceKey()` pair (category 2).
    - Nearest peer: `SnakeDeathMode` (per-constant keys `snakedeathmode.*`, `labelResourceKey()` returning
      `AppLocalizationKeys.ENUM_LABEL_SNAKEDEATHMODE`, `SnakeDeathModeTest` with `testLabelResourceKey` and
      `testResourceKey`).
    - Proposed change: give `LabColorMode` per-constant keys (`labcolormode.color`, `labcolormode.grayscale`),
      `resourceKey()`, and a static `labelResourceKey()` (`labcolormode.label`, replacing `lab.config.color`); use
      the localized display name provider in `LabConfigViewModel` and `LabColorMode.labelResourceKey()` in
      `LabConfigView`; add the keys to all bundles; add `LabColorModeTest` like `SnakeDeathModeTest`.
    - Key placement conflict: `AGENTS.md` puts simulation-specific keys as constants in the class that uses them, and
      `.claude/rules/simulations.md` ("Resources and Internationalization") allows simulation-local keys only as
      private constants in the consuming View. The label key of `LabColorMode` is consumed by the enum itself, which
      neither rule covers. `ENUM_LABEL_SNAKEDEATHMODE` in `core.AppLocalizationKeys` is a simulation-specific key and
      does not belong there. More generally, each of the ten `ENUM_LABEL_*` constants is used only by its own enum
      (plus `ENUM_LABEL_CELLSHAPE` as a sample key in `LocalizationTestSupportTest`), and those of `engine` and
      `simulations` enums are not app-wide keys either.
    - Required first: decide where enum-owned label keys live (e.g., a private constant in the enum), then adjust
      `.claude/rules/simulations.md`, possibly `AGENTS.md` and the category 2
      description above, move `ENUM_LABEL_SNAKEDEATHMODE` (and possibly the other `ENUM_LABEL_*` constants) out of
      `AppLocalizationKeys`, and only then localize `LabColorMode`.
- Tracked in #65 (decided to implement it later, together with the `LabColorMode` finding): category 2 enums share no
  common interface for `resourceKey()`.
    - Findings: the display name provider
      `e -> AppLocalization.getOptionalText(e.resourceKey()).orElse(e.toString())` is repeated nine times in seven
      ViewModels: `AbstractConfigViewModel` (`CellShape`, `GridEdgeBehavior`, `CellDisplayMode`),
      `DefaultControlViewModel` (`SimulationMode`), and the config ViewModels of Forest, Lab, Rebounding, Wa-Tor
      (`NeighborhoodMode` each), and Snake (`SnakeDeathMode`). It is passed as `Function<E, String>` to
      `InputChoiceProperty.ofList/ofEnum` and `InputEnumProperty.of` (`ui`). The duplicated part is a policy (a
      missing or blank text falls back to `toString()`), and each new config selection copies it again.
      `SimulationNotificationType` is used with `AppLocalization.getText(...)` without fallback
      (`AbstractMainView`).
    - Proposed change: a small interface in `core` (working name `AppLocalizedConstant`; the enums live in `engine`
      and `simulations`, so `ui` is not possible) with `String resourceKey()` and
      `default String toDisplayString()` returning `AppLocalization.getOptionalText(resourceKey()).orElse(toString())`
      (name per the `toDisplayString()` rule in `.claude/rules/java.md`). The category 2 enums implement it, and the
      ViewModels use method references such as `CellShape::toDisplayString`. `core` contains only `App*` classes so
      far; `.claude/rules/core.md` requires Javadoc and a test (with a test enum) for the new type.
    - Dependency: decide together with the key placement question of the `LabColorMode` finding, since the
      enum-owned label keys may affect the same contract.
- Done: the record tests in the `*UserActionContextTest` classes of Conway, Langton, Rebounding, Snake, Sugar, and
  ET Pets (seven tests such as `testAddSnakeStoresStrategy`) were renamed to `test<Record>Record` (e.g.,
  `testAddSnakeRecord`) per `.claude/rules/junit.md` and now check the accessor and `toString()` with `assertAll`.
  Enum components are checked as literals (e.g., `direction=W`); the components of `AddSnake` (`NamedMoveStrategy`)
  and `PlacePattern` (a record with lambdas) are checked through their own `toString()`. Superseded by
  `code-record-types.md`, category 6: these records are pure data carriers, so the tests are removed.

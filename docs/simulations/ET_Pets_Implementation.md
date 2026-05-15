# ET Pets - Implementation Guide

> **Purpose:** Document the current ET Pets V1 codebase structure, entity model, and key design patterns.
> For simulation rules, entities, constraints, and balancing parameters, see `ET_Pets_Specification.md`.

## 1. Document Scope and Authority

This guide documents the **actual V1 implementation**, not a planning template.

- `ET_Pets_Specification.md` is authoritative for **behavior and runtime correctness**.
- This guide is authoritative for **code structure, class mapping, and design patterns**.
- Normative simulation rules belong only to `ET_Pets_Specification.md`; this guide references them.
- If code and spec conflict, the specification is the source of truth; identify discrepancies for correction.
- Naming conventions follow existing simulations (`wator`, `sugar`, `snake`) for consistency.

### V1 Delivery Status

- Production code is complete and integrated.
- No test classes exist for ET Pets (intentional; testing is a future phase).
- Code structure is aligned with this guide.

## 2. Architecture Baseline (Aligned with Existing Simulations)

ET Pets must follow the same high-level shape as existing simulations (`wator`, `sugar`, `snake`):

- factory entry point (`EtpetsFactory`)
- model package (`config`, `grid model`, `simulation manager`, `step logic`, `statistics`, `termination`)
- entity package (descriptors and runtime entities)
- viewmodel package (UI-input to config mapping)
- view package (main/config/observation views)
- `package-info.java` in each package

The simulation is asynchronous and uses a **3-layer composite model**:

1. terrain layer
2. resource layer
3. agent layer

## 3. Actual Package Structure (V1)

The implemented package tree under `app/src/main/java/de/mkalb/etpetssim/simulations/etpets`:

```text
simulations/etpets/
  EtpetsFactory.java
  package-info.java
  model/
    EtpetsBalance.java                   <- balance constants and parameters
    EtpetsCell.java                      <- record snapshot of all 3 layers at one coordinate
    EtpetsConfig.java
    EtpetsConfigBuilder.java
    EtpetsDeterminism.java
    EtpetsGridModel.java
    EtpetsIdSequence.java
    EtpetsResourceLogic.java
    EtpetsScoreMath.java                 <- agent decision scoring math
    EtpetsSimulationManager.java
    EtpetsStatistics.java
    EtpetsStepRunner.java
    EtpetsTerrainLogic.java
    EtpetsTerminationCondition.java
    package-info.java
    entity/
      AgentEntity.java                   <- sealed interface for pet/egg
      EtpetsEntity.java                  <- root sealed interface
      EntityDescriptors.java             <- enum providing all descriptor specs
      NoAgent.java                       <- single-value enum: NONE
      NoResource.java                    <- single-value enum: NONE
      Pet.java                           <- final class
      PetEgg.java                        <- final class
      PetGenome.java
      PetTraits.java
      Plant.java                         <- final class
      ResourceBase.java                  <- abstract base for mutable resources
      ResourceEntity.java                <- sealed interface for plant/insect
      TerrainConstant.java               <- enum: GROUND, ROCK, WATER
      TerrainEntity.java                 <- sealed interface for terrain types
      Trail.java                         <- final class for mutable trail
      Insect.java                        <- final class
      package-info.java
  viewmodel/
    EtpetsConfigViewModel.java
    package-info.java
  view/
    EtpetsConfigView.java
    EtpetsMainView.java
    EtpetsObservationView.java
    package-info.java
```

## 4. `package-info.java` Requirement

Each new package must include `package-info.java` exactly in the same style as existing simulations:

```java
@org.jspecify.annotations.NullMarked
package de.mkalb.etpetssim.simulations.etpets.<subpkg>;
```

Packages that require this file:

- `...simulations.etpets`
- `...simulations.etpets.model`
- `...simulations.etpets.model.entity`
- `...simulations.etpets.viewmodel`
- `...simulations.etpets.view`

## 5. Core Type Mapping (Specification -> Code)

| Specification Concept                    | Code Artifact                                                                    |
|------------------------------------------|----------------------------------------------------------------------------------|
| simulation type `etpets`                 | `SimulationType.ET_PETS` (implemented, aliases registered)                       |
| fixed shape/edge constraints             | `EtpetsConfig` hardcoded to `HEXAGON`, `EDGES_ONLY`, `BLOCK_XY`                  |
| layered model (terrain/resources/agents) | `EtpetsGridModel` as composite with 3 `WritableGridModel` sub-models             |
| all balance parameters and constants     | `EtpetsBalance` - centralized final constants for all simulation values          |
| terrain default ground                   | `TerrainConstant.GROUND` placed as default in terrain layer initialization       |
| obstacles rock/water                     | `TerrainConstant.ROCK` and `TerrainConstant.WATER`                               |
| trail mutable intensity                  | `Trail` entity with bounded `int intensity` field                                |
| resources plant/insect                   | `Plant` and `Insect` extending `ResourceBase` with regeneration and consumption  |
| pet lifecycle                            | `Pet`, `PetEgg`, `PetGenome`, `PetTraits`                                        |
| async step sequence                      | `EtpetsStepRunner` invoked per step; order is strict: agent → resource → terrain |
| deterministic seeded behavior            | `EtpetsDeterminism` utility; seeded `Random` from config; tie-break ordering     |
| aggregated metrics                       | `EtpetsStatistics`; `EtpetsSimulationManager.updateStatistics()` after each step |
| scoring model for decisions              | `EtpetsScoreMath` - centralized scoring math for move/eat/reproduce/wait         |
| cell snapshot helper                     | `EtpetsCell` record - convenient 3-layer snapshot for a coordinate               |

## 6. Integration Points in Existing Application

### 6.1 `SimulationType`

In `app/src/main/java/de/mkalb/etpetssim/SimulationType.java`:

- Keep `ET_PETS` identifier and CLI alias `etpets`.
- Set `implemented` to `true` only when minimum runnable ET Pets is complete.
- Keep localization keys:
    - `simulation.etpets.title`
    - `simulation.etpets.subtitle`
    - `simulation.etpets.url`: the key remains in `SimulationType.ET_PETS` for structural consistency with all other
      simulation types, but no value is set in `messages_en_US.properties` (ET Pets has no official Wikipedia page).
      `AppLocalization.getOptionalText()` therefore returns `Optional.empty()`, which is the intended behavior.

### 6.2 `SimulationFactory`

In `app/src/main/java/de/mkalb/etpetssim/simulations/core/SimulationFactory.java`:

- add import `de.mkalb.etpetssim.simulations.etpets.EtpetsFactory`
- add switch branch:

```java
case ET_PETS -> EtpetsFactory.createMainView();
```

### 6.3 i18n (`messages_en_US.properties`)

Add ET Pets sections following existing naming pattern:

- config labels/tooltips (`etpets.config.*`)
- entity short/long/description (`etpets.entity.*`)
- observation labels (`etpets.observation.*`)
- optional emoji keys only if actually used in rendering mode
- **do NOT add `simulation.etpets.url`**: ET Pets is not an officially documented simulation; the key must remain absent
  so `AppLocalization.getOptionalText()` returns `Optional.empty()`.

## 7. Entity Model Design (Actual V1)

### 7.1 Root entity interface and descriptor IDs

`EtpetsEntity` is the sealed root interface extending `GridEntity`, with static descriptor ID constants for all entity
types:

- terrain: `DESCRIPTOR_ID_GROUND`, `DESCRIPTOR_ID_ROCK`, `DESCRIPTOR_ID_WATER`, `DESCRIPTOR_ID_TRAIL`
- resources: `DESCRIPTOR_ID_NO_RESOURCE`, `DESCRIPTOR_ID_PLANT`, `DESCRIPTOR_ID_INSECT`
- agents: `DESCRIPTOR_ID_NO_AGENT`, `DESCRIPTOR_ID_PET`, `DESCRIPTOR_ID_PET_EGG`

All descriptor specs are centralized in the `EntityDescriptors` enum (which implements
`SpecBackedGridEntityDescriptorProvider`), providing:

- localization keys
- colors and optional border colors for rendering
- visibility state

### 7.2 Sealed interface hierarchy

```
EtpetsEntity (sealed)
├── TerrainEntity (sealed)
│   ├── TerrainConstant (enum: GROUND, ROCK, WATER)
│   └── Trail (final class)
├── ResourceEntity (sealed)
│   ├── NoResource (single-value enum: NONE)
│   ├── Plant (final class extends ResourceBase)
│   └── Insect (final class extends ResourceBase)
└── AgentEntity (sealed)
    ├── NoAgent (single-value enum: NONE)
    ├── Pet (final class)
    └── PetEgg (final class)
```

### 7.3 Terrain entities

- `TerrainConstant` is a **single enum** implementing `TerrainEntity`, `ConstantGridEntity`, with values:
  `GROUND`, `ROCK`, `WATER`.
- `Trail` is a **final class** (not record, not enum) implementing `TerrainEntity`.

`Trail` state and behavior:

- `int intensity` - mutable internal field
- Clamp range `[TRAIL_INTENSITY_RANGE_MIN, TRAIL_INTENSITY_RANGE_MAX]` from `EtpetsBalance`
- `void increase(int amount)` - adds `amount` to `intensity` in-place, clamped
- `void decay(int amount)` - subtracts `amount` from `intensity` in-place, floor at `0`
- `boolean isDepleted()` - returns `true` when `intensity <= 0`

### 7.4 Resource entities

- `NoResource` is a **single-value enum** (`NONE`) implementing `ResourceEntity`, `ConstantGridEntity` - used as layer
  default.
- `ResourceBase` is the **abstract mutable base class** for resource implementations.
- `Plant` and `Insect` are **final classes** extending `ResourceBase`.

Per-instance fields (both plant and insect, inherited from `ResourceBase`):

- `int currentAmount`
- `int maxAmount`
- `double regenerationPerStep`

Per-type constants (static finals in each class):

- `CONSUMPTION_PER_ACT` - integer from `EtpetsBalance`
- `ENERGY_GAIN_PER_ACT` - integer from `EtpetsBalance`

### 7.5 Agent entities

- `NoAgent` is a **single-value enum** (`NONE`) implementing `AgentEntity`, `ConstantGridEntity` - used as layer
  default.
- `Pet` is a **final class** representing a movable mobile agent.
- `PetEgg` is a **final class** representing an immobile lifecycle entity.

`Pet` fields:

- `long petId`
- `Long parentAId`, `Long parentBId` (nullable)
- `int stepIndexOfBirth`
- `int currentEnergy` (mutable)
- `int reproductionCooldownRemaining` (mutable)
- `GridCoordinate previousCoordinate`, `GridCoordinate previousPreviousCoordinate` (nullable, mutable)
- `boolean dead` (mutable)
- `PetTraits traits` (immutable)

`PetEgg` fields:

- `long eggId`
- `long parentAId`, `long parentBId`
- `PetGenome petGenome`
- `int stepIndexOfLaying`
- `int incubationRemaining` (mutable)

`PetTraits` schema (V1 fixed):

- `int maxEnergy`
- `double movementCostModifier`
- `int reproductionMinEnergy`
- `int reproductionCooldown`

All trait bounds are defined in `EtpetsBalance` and must match `ET_Pets_Specification.md`.

### 7.6 Numeric types

- Identifier fields (`petId`, `eggId`, `parentAId`, `parentBId`) are `long` / `Long`.
- Counters, step indices, energy, and intensity values are `int`.
- Regeneration rates and modifiers that require fractional precision are `double`.

### 7.7 String representation (`toString` / `toDisplayString`)

Implementation follows existing simulation patterns:

- Entities analogous to existing simulations (`wator`, `sugar`, `snake`) implement `toString()` and `toDisplayString()`
  where those simulations do.
- Output style is compact and diagnostic, following established patterns rather than ET Pets-specific formats.

### 7.8 Helper methods and patterns

`Pet` and `PetEgg` follow method structure from `WatorCreature`, `SnakeHead`, `SugarAgent`:

- getters for immutable and mutable state
- helper methods for lifecycle checks (`canReproduceWith`, etc.)
- mutation methods for internal state updates

`Plant` and `Insect` follow `ResourceBase` / `SugarResourceSugar` patterns:

- regeneration update logic
- consumption preconditions
- immutable per-type constants alongside mutable per-instance state

## 8. Grid Model and Layer Ownership (Actual V1)

`EtpetsGridModel` is a record with three sub-models:

- `WritableGridModel<TerrainEntity> terrainModel`
- `WritableGridModel<ResourceEntity> resourceModel`
- `WritableGridModel<AgentEntity> agentModel`

### Layer defaults:

- terrain default: `TerrainConstant.GROUND`
- resource default: `NoResource.NONE`
- agent default: `NoAgent.NONE`

### Layer rules (enforced):

- all sub-models share the same `GridStructure`
- mutable terrain state (trail intensity) is stored in `Trail` entity only
- per-coordinate collocation: a coordinate MUST NOT contain both a resource and an agent in V1
- all V1 layer-occupancy constraints from `ET_Pets_Specification.md` are enforced at runtime

### Helper: EtpetsCell

The `EtpetsCell` record provides a convenient 3-layer snapshot:

```java
public record EtpetsCell(
        GridCoordinate coordinate,
        TerrainEntity terrainEntity,
        ResourceEntity resourceEntity,
        AgentEntity agentEntity) { ...
}
```

Created via `EtpetsCell.of(coordinate, model)`, it includes helper methods:

- `isWalkable()` returns `true` if terrain is walkable, resource is empty, and agent is empty

## 9. Step Execution Order (Mandatory)

`EtpetsStepRunner` executes the asynchronous per-step pipeline and applies updates immediately in strict order:

1. **Agent logic** (`EtpetsAgentLogic.apply`) - pet lifecycle, age-related mortality, age-aware wait scoring,
   decision-making, reproduction, movement
2. **Resource regeneration** (`EtpetsResourceLogic.apply`) - per-step regeneration for plant and insect
3. **Terrain trail decay** (`EtpetsTerrainLogic.apply`) - decay trail intensity

Statistics are updated by `EtpetsSimulationManager.updateStatistics()` **after** the step runner completes, following
the `AbstractTimedSimulationManager` pattern.

All rule details - action priority, occupancy constraints, consumption constraints, hatch/death timing, and operation
order - are authoritative in `ET_Pets_Specification.md`.

## 10. Balance Constants (`EtpetsBalance`)

All numerical parameters, thresholds, and ranges are centralized in the static final class `EtpetsBalance`.

Key constants include:

- **Trail**: intensity bounds, increase-per-entry, decay-per-step
- **Plant**: max amount range, regeneration base and variance, consumption and energy gain
- **Insect**: max amount range, regeneration base and variance, consumption and energy gain
- **Pet traits**: energy range, movement cost modifier range, reproduction thresholds, cooldown range
- **Pet lifecycle**: step energy loss, fertility age minimum, birth energy factor, ageing-effect age threshold
- **Ageing behavior**: age-related mortality chance (base, slope, cap), age-aware WAIT chance (base, slope, cap), WAIT
  score growth and cap
- **Pet decision scoring**: all weights, exponents, penalties, and thresholds for move/eat/reproduce scoring
- **Genome and mutation**: mutation chance per trait, mutation delta

Changes to balance constants MUST be synchronized with updates to `ET_Pets_Specification.md` to maintain specification
alignment.

## 11. Decision Scoring (`EtpetsScoreMath`)

The utility class `EtpetsScoreMath` encapsulates all agent decision-making math, supporting the unified action-scoring
model described in the specification.

Scoring methods compute raw scores for:

- **Move score** - resource bonuses, trail bonuses, exploration bonuses, oscillation penalties, low-mobility penalties,
  crowding penalties
- **Eat score** - hunger weight, panic threshold, resource gain weight, overfill penalty, age decay
- **Reproduce score** - genome quality average, quality floor, clamped range
- **Wait score** - age-aware probabilistic activation and age-based growth with cap

Scoring uses survival pressure and other derived quantities computed from pet energy state and trait values.

The specification defines all scoring weights and exponents (in the **V1 Parameter Baseline** section);
`EtpetsScoreMath` implements these formulas without redefining them.

## 12. Determinism Strategy (`EtpetsDeterminism`)

The utility class `EtpetsDeterminism` centralizes all tie-break logic to ensure deterministic, reproducible behavior
under fixed seed.

Key rules:

- All stochastic operations use one seeded `Random` from `EtpetsConfig.seed`
- Tie-breaking order (when multiple candidates score equally):
    1. Primary rule-specific scoring
    2. Energy level
    3. Pet ID
    4. Coordinate (x ascending, then y ascending)
    5. Only as last resort: seeded random

Additional practices:

- Initialization order is always deterministic (shuffled collections use seeded random)
- Selection collections are sorted by deterministic criteria before optional random fallback
- No uncontrolled external random state leaks into simulation

## 13. Configuration and Validation (`EtpetsConfig`)

`EtpetsConfig` (record implementing `SimulationConfig`) includes:

- **Grid**: width, height, cell edge length
- **Seed**: random seed for reproducibility
- **Terrain**: rock percentage, water percentage
- **Resources**: plant percentage, insect percentage
- **Pets**: initial pet count (range 0–20)
- **Pet traits and energy**: energy range, movement cost range, reproduction thresholds, cooldown range, birth energy
  factor
- **Regeneration and consumption**: plant and insect per-step rates, consumption and energy gain
- **Trail and movement**: trail intensity bounds, increase and decay, trail preference threshold for decision-making
- **Mutation**: mutation chance and delta per trait
- **Incubation**: egg incubation duration

### Fixed constraints (not configurable in V1):

- Shape: `HEXAGON`
- Neighborhood: `EDGES_ONLY`
- Edge behavior: `BLOCK_XY`

### Validation (fail-fast):

Fail-fast validation in `EtpetsConfig` and initialization code enforces:

- Terrain percentages: `rockPercent + waterPercent <= 50`
- Resource percentages: `plantPercent + insectPercent <= 100`
- Pet count in range `[0, 20]`
- All trait bounds respected (ranges from `EtpetsBalance`)
- Deterministic percent-to-count derivation using floor integer math

`EtpetsConfigBuilder` provides fluent configuration with sensible defaults.

## 14. ViewModel and UI

## 14. ViewModel and UI

### 14.1 Config ViewModel

`EtpetsConfigViewModel` extends `AbstractConfigViewModel<EtpetsConfig>`.

Configuration options exposed in UI:

- **Grid structure**: fixed shape `HEXAGON`, fixed edge behavior `BLOCK_XY`
- **Size**: width and height (range 20–200)
- **Terrain**: rock percentage, water percentage
- **Resources**: plant percentage, insect percentage
- **Pets**: initial count (range 0–20)
- **Pet traits**: energy range, movement cost modifier range, reproduction energy threshold, reproduction cooldown
- **Regeneration**: plant and insect per-step base rate and variance
- **Trail**: intensity bounds, preference threshold
- **Mutation**: chance and delta per trait
- **Incubation**: egg incubation duration
- **Seed**: for reproducible runs

### 14.2 Config View

`EtpetsConfigView` follows patterns from existing simulations (`SugarConfigView`, `SnakeConfigView`):

- Grouped sections: structure, initialization, resources, pet lifecycle, mutation, trail
- All labels and tooltips via i18n keys (e.g., `etpets.config.*`)
- No hardcoded text

### 14.3 Main View

`EtpetsMainView` extends `AbstractDefaultMainView` and renders layers in order:

1. Terrain layer (ground, rock, water, trail with intensity-based brightness)
2. Resource layer (plant, insect)
3. Agent layer (egg, pet)

Trail rendering maps intensity to brightness variants of the trail base color.

### 14.4 Observation View

`EtpetsObservationView` displays minimum statistics:

- Step count
- Active pet count
- Egg count
- Cumulative dead pet count
- Optional: selected-cell detail panel with entity info and runtime values

## 15. Statistics and Logging (`EtpetsStatistics`)

`EtpetsStatistics` extends the timed statistics pattern used in other simulations.

Must track at minimum:

- Total cells
- Step count
- Active pet count
- Egg count
- Cumulative dead pets

Updated after each step completes (in `EtpetsSimulationManager.updateStatistics()`), not during step execution.

Updated after each step completes (in `EtpetsSimulationManager.updateStatistics()`), not during step execution.

## 16. Integration Points

### 16.1 `SimulationType`

`SimulationType.ET_PETS` is registered with:

- CLI alias: `etpets`
- Localization keys:
    - `simulation.etpets.title`
    - `simulation.etpets.subtitle`
    - `simulation.etpets.url` (no value in V1; ET Pets has no official external documentation)

The enum constant marks `ET_PETS` as implemented for discovery and UI registration.

### 16.2 `SimulationFactory`

Switch case in `SimulationFactory.createMainView()`:

```java
case ET_PETS ->EtpetsFactory.

createMainView();
```

### 16.3 Internationalization (i18n)

`messages_en_US.properties` must include ET Pets sections:

- `etpets.config.*` - configuration labels and tooltips
- `etpets.entity.*` - entity short/long/description names
- `etpets.observation.*` - observation view labels
- Optional emoji keys only if rendering actually uses them

## 17. Definition of Done (V1 Implementation)

V1 implementation is complete when:

✓ ET Pets launches from start screen with CLI alias `etpets`
✓ Simulation runs asynchronously and reproducibly under fixed seed
✓ All V1 constraints from specification are implemented exactly
✓ Three-layer ownership rules are strictly enforced
✓ Deterministic tie-break policy is operationalized via `EtpetsDeterminism`
✓ Statistics contract is satisfied
✓ All balance values are centralized in `EtpetsBalance` with specification alignment
✓ Scoring model is implemented via `EtpetsScoreMath`
✓ Package structure matches this guide
✓ All ET Pets packages include `package-info.java`
✓ **No test classes were added (intentional; testing is a future phase)**

## 18. Key Design Decisions

- **Centralized balance parameters**: All numerical values live in `EtpetsBalance`, making them easy to adjust and
  verify against the specification.
- **Scoring math isolation**: `EtpetsScoreMath` encapsulates decision-making formulas, separating concerns from agent
  logic.
- **Cell snapshots**: `EtpetsCell` record provides convenient way to work with 3-layer state within logical unit.
- **Enum-based constants**: Terrain and "No" entities use enum pattern for type safety and immutability.
- **Descriptor centralization**: `EntityDescriptors` enum standardizes descriptor specs, colors, and i18n keys in one
  place.

## 19. Maintenance and Future Changes

When modifying ET Pets code:

1. **Change balance parameters?** Update `EtpetsBalance` AND `ET_Pets_Specification.md` in the same change.
2. **Change scoring math?** Update `EtpetsScoreMath` and reference the specification section for the formula basis.
3. **Add a new entity type?** Add to `EtpetsEntity`, create sealed subtypes if needed, and update `EntityDescriptors`.
4. **Change step order?** Update both `EtpetsStepRunner` AND `ET_Pets_Specification.md` section 6.
5. **Change entity state?** Verify no assumptions in `EtpetsCell`, scoring math, or statistics are broken.

## 20. Out-of-Scope for V1

- Unit, integration, and UI testing (planned follow-up phase)
- V2 features (multiple species, advanced genetics, ecosystem balancing)
- Performance micro-optimizations
- Custom replay or time-travel mechanics

---

*This guide documents the finalized V1 implementation. Update it whenever the codebase significantly changes.*

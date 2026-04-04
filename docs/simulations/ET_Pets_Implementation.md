# ET Pets - Implementation Guide

> **Purpose:** This document defines the *how* of the ET Pets simulation -
> package structure, class mapping, implementation order, and references to existing patterns.
> For simulation rules, entities, constraints, and balancing parameters, see `ET_Pets_Specification.md`.

## 1. Document Scope and Working Mode

This guide is the implementation companion to `ET_Pets_Specification.md`.

- `ET_Pets_Specification.md` is authoritative for behavior and correctness.
- This guide is authoritative for code structure and delivery sequence.
- If both documents conflict, update this guide so that it matches the specification.

### Important implementation policy for the first ET Pets delivery

**No test classes are created in this phase.**

- Intentionally do **not** add files under `app/src/test/...` for ET Pets yet.
- Focus on production code structure, deterministic behavior, and integration wiring.
- Test coverage is planned as a dedicated follow-up phase after V1 stabilization.

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

## 3. Target Package Structure

Create the following package tree under `app/src/main/java/de/mkalb/etpetssim/simulations/etpets`:

```text
simulations/etpets/
  EtpetsFactory.java
  package-info.java
  model/
    EtpetsConfig.java
    EtpetsGridModel.java
    EtpetsSimulationManager.java
    EtpetsStepRunner.java
    EtpetsTerminationCondition.java
    EtpetsStatistics.java
    EtpetsAgentLogic.java
    EtpetsResourceLogic.java
    EtpetsTerrainLogic.java
    EtpetsDeterminism.java
    EtpetsIdSequence.java
    package-info.java
    entity/
      EtpetsEntity.java
      EtpetsEntityDescribable.java
      EtpetsTerrainEntity.java
      EtpetsTerrainConstant.java         <- enum: GROUND, ROCK, WATER
      EtpetsTerrainTrail.java            <- mutable class
      EtpetsResourceEntity.java
      EtpetsResourceNone.java            <- single-value enum: NONE
      EtpetsResourcePlant.java
      EtpetsResourceInsect.java
      EtpetsAgentEntity.java
      EtpetsAgentNone.java               <- single-value enum: NONE
      EtpetsPet.java
      EtpetsPetEgg.java
      EtpetsPetGenome.java
      EtpetsPetTraits.java
      package-info.java
  viewmodel/
    EtpetsConfigViewModel.java
    package-info.java
  view/
    EtpetsMainView.java
    EtpetsConfigView.java
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

| Specification Concept                    | Code Artifact                                                                        |
|------------------------------------------|--------------------------------------------------------------------------------------|
| simulation type `etpets`                 | `SimulationType.ET_PETS` aliases, i18n keys, `SimulationFactory` switch case         |
| fixed shape/edge constraints             | `EtpetsConfig` validation (`HEXAGON`, `EDGES_ONLY`, `BLOCK_XY`)                      |
| layered model (terrain/resources/agents) | `EtpetsGridModel` as `CompositeGridModel<EtpetsEntity>` with 3 writable sub-models   |
| terrain default ground                   | terrain `SparseGridModel` with default `EtpetsTerrainConstant.GROUND`                |
| obstacles rock/water                     | constant terrain entities (`ConstantGridEntity`)                                     |
| trail mutable intensity                  | `EtpetsTerrainTrail` mutable entity with bounded `double intensity`                  |
| resources plant/insect                   | mutable resource entities with `currentAmount`, `maxAmount`, `regenRate`, act values |
| pet lifecycle                            | `EtpetsPet`, `EtpetsPetEgg`, `EtpetsPetGenome`, `EtpetsPetTraits`                    |
| async step sequence                      | `EtpetsStepRunner` invoked by `DefaultSimulationExecutor`                            |
| deterministic seeded behavior            | `Random` seeded from config + deterministic tie-break ordering                       |
| aggregated metrics                       | `EtpetsStatistics`                                                                   |

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

## 7. Entity Model Design

### 7.1 Root entity interface

Create `EtpetsEntity` as sealed interface extending `GridEntity`, with descriptor IDs for all renderable classes.

Fixed descriptor IDs (must match exactly across `EtpetsEntity`, `EtpetsEntityDescribable`, and all rendering/lookup
code):

- terrain: `terrain_ground`, `terrain_rock`, `terrain_water`, `terrain_trail`
- resources: `resource_none`, `resource_plant`, `resource_insect`
- agents: `agent_none`, `agent_pet`, `agent_pet_egg`

Constant entity pattern:

- All `ConstantGridEntity` implementations MUST use the **enum pattern**, following `SnakeConstantEntity` as a
  reference.
- Terrain constants (Ground, Rock, Water) are grouped in one enum `EtpetsTerrainConstant`.
- Each of the layer-default "None" entities (`EtpetsResourceNone`, `EtpetsAgentNone`) is a separate single-value enum.
- Mutable entities (`EtpetsTerrainTrail`, `EtpetsResourcePlant`, `EtpetsResourceInsect`, `EtpetsPet`, `EtpetsPetEgg`)
  are regular classes.

Sealed interface hierarchy:

- `EtpetsEntity` permits `EtpetsTerrainEntity`, `EtpetsResourceEntity`, `EtpetsAgentEntity`
- `EtpetsTerrainEntity` permits `EtpetsTerrainConstant`, `EtpetsTerrainTrail`
- `EtpetsResourceEntity` permits `EtpetsResourceNone`, `EtpetsResourcePlant`, `EtpetsResourceInsect`
- `EtpetsAgentEntity` permits `EtpetsAgentNone`, `EtpetsPet`, `EtpetsPetEgg`

### 7.2 Terrain entities

- `EtpetsTerrainConstant` is a **single enum** implementing `EtpetsTerrainEntity`, `ConstantGridEntity`, with values:
  `GROUND`, `ROCK`, `WATER`.
- `EtpetsTerrainTrail` implements `EtpetsTerrainEntity` as a **mutable class** (not a record, not an enum).

`EtpetsTerrainTrail` state and behavior:

- `double intensity` - mutable internal field
- Clamp range `[trailMin, trailMax]` enforced at mutation time
- `void increase(double amount, double max)` - adds `amount` to `intensity` in-place, clamped to `max`
- `void decay(double amount)` - subtracts `amount` from `intensity` in-place, floor at `0`
- `boolean isDepleted()` - returns `true` when `intensity <= 0`

### 7.3 Resource entities

- `EtpetsResourceNone` is a **single-value enum** (`NONE`) implementing `EtpetsResourceEntity`, `ConstantGridEntity` -
  used as the layer default.
- `EtpetsResourcePlant` and `EtpetsResourceInsect` are **mutable classes**.

Per-instance fields (both plant and insect):

- `double currentAmount`
- `double maxAmount`
- `double regenerationPerStep`

Per-type constants (baked into each class, not per-instance fields):

- `int consumptionPerAct` - integer, matches spec values (`Plant: 2`, `Insect: 4`)
- `int energyGainPerAct` - integer, matches spec values (`Plant: 3`, `Insect: 8`)

### 7.4 Agent entities

- `EtpetsAgentNone` is a **single-value enum** (`NONE`) implementing `EtpetsAgentEntity`, `ConstantGridEntity` - used as
  the layer default.
- `EtpetsPet` is a **mutable class** representing a movable agent.
- `EtpetsPetEgg` is a **mutable class** representing an immobile lifecycle entity.

`EtpetsPet` minimum fields (V1):

- `long petId`
- `Long parentAId`, `Long parentBId`
- `int stepIndexOfBirth`
- `int currentEnergy`
- `int visionRange`
- `double movementCostModifier`
- `int reproductionCooldownRemaining`
- `EtpetsPetTraits traits`
- `boolean dead`
- `int stepIndexOfDeath` (`-1` when alive)

`EtpetsPetEgg` minimum fields (V1):

- `long eggId`
- `long parentAId`, `long parentBId`
- `EtpetsPetGenome petGenome`
- `int stepIndexOfLaying`
- `int incubationRemaining`

## 8. Grid Model and Layer Ownership

Implement `EtpetsGridModel` as a record similar to `SugarGridModel`, but with 3 sub-models:

- `WritableGridModel<EtpetsTerrainEntity> terrainModel`
- `WritableGridModel<EtpetsResourceEntity> resourceModel`
- `WritableGridModel<EtpetsAgentEntity> agentModel`

`getEntities(GridCoordinate coordinate)` must return all three layer entities in order:
`List.of(terrainModel.getEntity(coordinate), resourceModel.getEntity(coordinate), agentModel.getEntity(coordinate))`

Rules:

- all submodels must share the same `GridStructure`
- terrain default is `EtpetsTerrainConstant.GROUND`
- resource default is `EtpetsResourceNone.NONE`
- agent default is `EtpetsAgentNone.NONE`
- mutable terrain state (trail intensity) remains in terrain entity only
- in V1, a coordinate MUST NOT hold a resource entity (Layer 2) and an agent entity (`EtpetsPet` or `EtpetsPetEgg`) in
  Layer 3 at the same time

## 9. Step Execution Order (Mandatory)

`EtpetsStepRunner` must execute one step in this strict order:

1. Collect the current list of agent coordinates (Pets and Eggs) from the agent layer.
2. For each `EtpetsPet` in the collected list, in order:
    1. If the pet was marked dead in the **previous** step (`dead == true`), remove it from the agent layer and skip
       further processing.
    2. Apply the fixed priority chain from the specification: Death-check -> Eat-if-adjacent ->
       Move-to-resource-if-hungry -> Reproduce-if-possible -> Move-to-enable-reproduction -> Explore/Trail.
3. For each `EtpetsPetEgg` in the collected list: decrement `incubationRemaining`; if it reaches `0`, remove the egg and
   place a new `EtpetsPet` at the same coordinate.
4. Regenerate all resource cells (`currentAmount += regenerationPerStep`, capped at `maxAmount`).
5. Decay all trail cells (`intensity -= trailDecayPerStep`); replace with `EtpetsTerrainConstant.GROUND` if
   `isDepleted()`.

Additional mandatory V1 constraints during step execution:

- A pet may consume only from an adjacent resource cell and MUST NOT move onto the resource coordinate when eating.
- Target coordinates for pet movement and egg placement MUST be free of Layer 2 resources and Layer 3 agents.
- Dead-pet removal follows the spec window rule: remove pets that were already marked dead when they are reached in the
  next iteration.

Statistics are updated by `EtpetsSimulationManager.updateStatistics()` after the step runner completes - not inside the
runner itself - following the existing `AbstractTimedSimulationManager` pattern.

No separate collision-resolution phase is introduced in V1.

## 10. Determinism Strategy

Create `EtpetsDeterminism` utility methods to keep all tie-breaks centralized.

Required deterministic order before random fallback:

- primary rule-specific scoring
- energy
- id
- coordinate (`x asc`, `y asc`)
- only then seeded random

Additional rules:

- all stochastic operations use one seeded `Random` from `EtpetsConfig.seed`
- initialization order is deterministic
- selection collections should be sorted before optional random fallback

Additional deterministic tie-break requirements (V1):

- Resource selection tie-break: higher `energyGainPerAct` -> higher `currentAmount` -> stable coordinate order (`x`,
  then `y`) -> seeded random fallback.
- Resource target selection at equal distance (`Move-to-resource-if-hungry`): same tie-break order as above.
- Reproduction partner tie-break at equal quality score: higher `currentEnergy` -> lower `petId` -> stable coordinate
  order (`x`, then `y`) -> seeded random fallback.
- Egg placement tie-break: stable coordinate order (`x`, then `y`) -> seeded random fallback.
- Explore/Trail tie-break: highest trail intensity first; if still tied, stable coordinate order (`x`, then `y`) ->
  seeded random fallback.

## 11. Configuration and Validation

`EtpetsConfig` (record implementing `SimulationConfig`) includes:

- common base config values from core
- terrain percentages (`rockPercent`, `waterPercent`)
- initial pet count (`petCount`, integer, range `0..20`)
- resource counts and per-type rates
- pet energy/reproduction/trail/mutation parameters
- incubation settings
- `NeighborhoodMode` - hardcoded constant `EDGES_ONLY`; not a user-configurable field

Validation rules (fail fast):

- `cellShape == HEXAGON`
- `gridEdgeBehavior == BLOCK_XY`
- `neighborhoodMode == EDGES_ONLY`
- `rockPercent + waterPercent <= 50`
- obstacle counts use deterministic floor-based formulas:
    - `rockCount = floor(totalTerrainCells * rockPercent / 100)`
    - `waterCount = floor(totalTerrainCells * waterPercent / 100)`
- all min/max ranges valid
- all rate/threshold values non-negative and internally consistent

Additional terrain perception rule (V1):

- In line-of-sight checks, adjacent `Rock` blocks visibility behind it in scan direction, while `Water` does not block
  visibility.

## 12. ViewModel and UI Plan

### 12.1 Config ViewModel

`EtpetsConfigViewModel` extends `AbstractConfigViewModel<EtpetsConfig>`.

Common config settings:

- fixed shape list containing only `HEXAGON`
- fixed edge behavior list containing only `BLOCK_XY`
- cell display mode initially `SHAPE` (adjust later if required)

Expose ET Pets-specific properties for:

- `petCount` - `InputIntegerProperty`, initial `5`, min `0`, max `20`, step `1`
- terrain percentages
- resource parameters
- energy thresholds
- reproduction thresholds/cooldown
- trail settings
- mutation settings
- incubation duration

### 12.2 Config View

`EtpetsConfigView` follows patterns from `SugarConfigView`/`SnakeConfigView`:

- grouped sections: structure, initialization, rules
- localized labels/tooltips only via i18n keys
- no hidden hardcoded text

### 12.3 Main View

`EtpetsMainView` extends `AbstractDefaultMainView` and renders by layer priority:

1. terrain background (ground/rock/water/trail)
2. resources (plant/insect)
3. agents (egg/pet)

Trail rendering:

- map intensity to brightness variants of trail base color
- intensity must be clamped before color lookup

### 12.4 Observation View

`EtpetsObservationView` minimum labels:

- step
- active pets
- eggs
- cumulative dead pets
- optional selected-cell detail (entity + key runtime values)

## 13. Statistics and Logging

`EtpetsStatistics` extends timed statistics style used in other simulations.

Must include at least:

- total cells
- step count
- active pet count
- egg count
- cumulative dead pets

Logging guideline:

- keep structured and sparse at info level
- use debug-level details for per-agent decisions where available
- avoid noisy per-cell logs by default

## 14. Incremental Delivery Plan (Small PRs)

### PR-01: Skeleton and wiring prep

- create all ET Pets packages + `package-info.java`
- add placeholder classes/interfaces with compile-safe stubs
- no behavior yet

### PR-02: Type registration and descriptors

- implement `EtpetsEntity` hierarchy
- implement `EtpetsEntityDescribable`
- register descriptor registry in `EtpetsFactory`

### PR-03: Config + ViewModel + ConfigView baseline

- implement `EtpetsConfig`
- implement `EtpetsConfigViewModel`
- implement basic `EtpetsConfigView`

### PR-04: Grid model + simulation manager scaffold

- implement `EtpetsGridModel` with 3 layers
- implement `EtpetsSimulationManager` constructor, initialization hooks
- integrate seeded random

### PR-05: Terrain initialization

- deterministic obstacle placement
- fail-fast validation for percentages
- default ground and initial trail handling

### PR-06: Resource initialization/regeneration

- place `Plant` and `Insect` on valid terrain only
- implement per-cell regen variance
- implement consumption preconditions and update functions

### PR-07: Pet and egg lifecycle core

- spawn initial pets
- egg incubation and hatching
- death marking/removal with one-step dead visualization

### PR-08: Pet decision logic

- implement fixed priority chain
- deterministic tie-breakers
- random fallback only at final tie

### PR-09: Trail dynamics

- reinforce trail on movement
- global decay at end of step
- revert to ground at zero intensity

### PR-10: Statistics and observation UI

- wire `EtpetsStatistics`
- implement `EtpetsObservationView`
- selected-cell details in observation panel

### PR-11: Main rendering polish

- terrain/resource/agent drawing pass
- trail intensity color scaling
- dead-pet visualization window

### PR-12: App integration switch-on

- wire `SimulationFactory` case
- complete i18n keys
- mark `SimulationType.ET_PETS` as implemented

## 15. Definition of Done (V1 Implementation)

Implementation is considered done when all points are true:

- ET Pets launches from start screen and CLI alias `etpets`
- simulation runs asynchronously and reproducibly with identical seed
- all fixed V1 constraints from specification are implemented
- three-layer ownership rules are respected
- deterministic tie-break policy is implemented
- statistics show required aggregated counters
- package structure and naming are consistent with project conventions
- all ET Pets packages include `package-info.java`
- **no ET Pets test classes were added in this phase (intentional)**

## 16. Explicit Out-of-Scope for This Guide Revision

- writing ET Pets unit/integration/UI tests
- V2 features (extra species, additional resources, advanced genetics, obstacle clustering)
- performance micro-optimizations before functional baseline is complete

## 17. Open Questions (Deferred)

The following questions are not yet resolved and must be answered before the affected PR can be implemented.

### Q1 - `EtpetsPetTraits`: V1 trait list undefined

`EtpetsPetTraits.java` and `EtpetsPetGenome.java` are listed in the package structure but their concrete fields are not
yet defined.

What needs to be decided:

- Which concrete traits are inheritable in V1? (e.g., `metabolismRate`, `visionRange`, `movementCostModifier`, ...)
- What are the per-trait min/max bounds used for normalization in `genomeQualityScore`?
- Which fields of `EtpetsPet` are derived from traits at birth vs. independently mutable at runtime?

**Blocking for:** PR-07 (pet spawn from genome), PR-08 (pet decision logic, reproduction scoring).

**Resolution:** deferred. Until resolved, `EtpetsPetTraits` and `EtpetsPetGenome` are created as placeholder stubs (
e.g., empty records or minimal interfaces) that compile without errors.

---

*This implementation guide is a living plan. Update it after each delivered PR so it remains an accurate map of the
current ET Pets codebase.*
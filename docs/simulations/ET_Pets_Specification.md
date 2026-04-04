# ET Pets - Simulation Specification

> **Purpose:** This document defines the *what* of the ET Pets simulation -
> rules, entities, constraints, layer model, lifecycle, and balancing parameters.
> It is the authoritative reference for correctness and completeness of V1.
> For the implementation roadmap, class structure, and package mapping, see `ET_Pets_Implementation.md`.

## Scope

ET Pets is a custom 2D asynchronous agent-based simulation set on a fictional alien planet.
Alien beings keep a small group of exotic pets inside a terrarium. The simulation models
pet lifecycle, movement, resource seeking, reproduction, and death over time.

The model is intentionally different from simpler rule systems (for example Conway's
Game of Life or Langton's Ant): ET Pets uses fewer active agents, but each agent has
richer state and decision logic.

V1 scope focuses on a stable, extensible baseline that can be implemented and tested
incrementally:

- exactly one pet species,
- exactly three stacked grid layers (terrain, resources, agents),
- asynchronous step execution,
- egg-based reproduction with delayed hatching,
- trail mechanics on terrain,
- reproducible, seed-driven simulation behavior,
- aggregated statistics and logging for behavior analysis.

Out of scope for V1:

- multiple pet species,
- sex/gender modeling,
- complex genetics beyond trait averaging plus mutation,
- advanced ecosystem balancing beyond the listed default parameters.

## Global Constraints

- Runtime model MUST be asynchronous.
- The simulation type identifier MUST be `etpets`.
- Cell shape MUST be `CellShape.HEXAGON` only.
- Neighborhood mode MUST be `NeighborhoodMode.EDGES_ONLY`.
- Edge behavior MUST be `GridEdgeBehavior.BLOCK_XY` only.
- ET Pets MUST use a composite, layered grid model with exactly three logical layers:
  terrain, resources, and agents.
- Architecture SHOULD remain conceptually close to Sugarscape (stacked layered model),
  but ET Pets MUST implement significantly richer per-agent behavior.
- Layer responsibilities MUST stay separated:
    - terrain stores terrain-layer entities and their mutable terrain state (trail),
    - resources store consumable/regenerating food state,
    - agents store lifecycle entities (`Pet`, `PetEgg`).
- Layer data ownership MUST be strict: mutable terrain state MUST remain in terrain-layer state and MUST NOT replace
  resource or agent layer data.
- In V1, a grid coordinate MUST NOT simultaneously hold a resource entity (Layer 2) and a `Pet` or `PetEgg` entity (
  Layer 3).
- The simulation MUST be extensible so additional pet types, traits, and behavior modules can be added later without
  breaking existing behavior.
- All stochastic decisions MUST be controlled by a configured random seed.
- All three `GridModel` layers MUST be initially populated during initialization using configuration parameters and
  seeded randomness.
- Initial placement of terrain, resources, and agents MUST be deterministic under equal seed and equal configuration.
- Under equal initial state and equal configured seed, the simulation MUST produce identical results.
- In V1, pet decision logic MUST use deterministic rule ordering first; seeded randomness is only a fallback when
  deterministic tie-break rules cannot resolve a choice.
- The simulation MUST support configurable balancing parameters (for example thresholds, cooldowns, mutation, trail
  decay) without code-structure changes.
- The simulation MUST be designed for performance with moderate-to-growing agent counts while preserving per-agent
  behavioral complexity.
- The simulation MUST provide structured logging and a statistics object with aggregated values per step, including at
  least:
    - number of active pets,
    - number of pet eggs,
    - cumulative number of dead pets up to and including the current step.

## Layer Model (Fixed: 3 Layers)

ET Pets MUST use exactly the following three stacked `GridModel` layers in this order:

1. Terrain Layer (base environment)
2. Resource Layer (food sources)
3. Agent Layer (pet lifecycle entities)

All layers MUST share the same structure, size and topology (cell shape, edge behavior)
but MUST maintain strict separation of entities and mutable state according to the rules defined below.

Fixed rules:

- Edge behavior MUST be `GridEdgeBehavior.BLOCK_XY` only.
- Cell shape MUST be `CellShape.HEXAGON` only.
- Neighborhood mode MUST be `NeighborhoodMode.EDGES_ONLY` only.
- Size MUST be configurable and between `20x20` and `200x200` cells; all layers MUST have the same size and MUST satisfy topology validity constraints.

### Layer 1: Terrain

Purpose:

- Represents the base terrarium environment, movement constraints, and trail formation.

Fixed rules:

- Terrain entities on Layer 1 MUST be divided into three categories:
    - `Ground` for the single default walkable terrain entity,
    - `Trail` for worn walkable terrain with mutable state,
    - blocking obstacles for non-walkable terrain.
- Only `Ground` and `Trail` cells are walkable for pets.
- Detailed rules for these categories are defined in the subsections below.

State and properties:

- In V1, the terrain layer MUST provide a default walkable state represented by `Ground`.
- Terrain state MUST be represented directly in the terrain layer.

Deferred details:

- Future placement improvements are documented as V2 notes in the subsections below.

#### Ground

Purpose:

- Represents the default walkable base terrain of the terrarium.

Entities and semantics:

- V1 MUST define exactly one constant `Ground` terrain.
- `Ground` MUST be the default entity of the terrain `GridModel`.
- `Ground` MUST be the default walkable terrain state for empty, non-blocking cells.
- `Ground` MUST be the fallback terrain state when a `Trail` decays back to intensity `0`.
- `Ground` MUST remain traversable for pets.
- `Ground` MUST have no mutable per-cell properties in V1.

#### Trail

Purpose:

- Represents worn walkable terrain caused by repeated pet movement.

Entities and data ownership:

- A dedicated mutable terrain `GridEntity` named `Trail` MUST exist.
- `Trail` MUST store its mutable intensity counter directly in entity state.

Lifecycle rules:

- If a pet moves onto walkable ground, that terrain cell MUST become `Trail` (with an initial intensity).
- If a pet moves onto an existing `Trail`, the trail intensity counter MUST increase up to the maximum.
- In V1, trail intensity MUST increase by the same fixed amount (`trailIncreasePerEntry`) for all pets regardless of
  individual pet properties.
- Trail intensity MUST be capped at a configured maximum value.
- Trail intensity MUST decay by a configured amount each simulation step.
- If trail intensity reaches `0`, the terrain cell MUST revert to walkable `Ground`.
- Trail decay MUST be executed as the final operation of each simulation step, after pet and resource simulation.

Behavior and effects:

- Trail intensity MUST affect both cell color rendering and pet movement selection.
- A `Trail` cell MUST remain walkable.
- Terrain-dependent logic in other layers (for example resource compatibility) MUST remain valid when a cell is in
  `Trail` state.

#### Blocking Obstacles

Purpose:

- Represents terrain cells that block pet movement and define the unusable parts of the terrarium interior.

Entities and semantics:

- V1 MUST define exactly two constant blocking terrain types: `Rock` and `Water`.
- Blocking obstacles MUST be non-walkable for pets.
- Because pets cannot enter blocking obstacles, entering them can never create or reinforce a `Trail`.
- Blocking obstacles MUST remain terrain-layer entities and MUST NOT be modeled as agent-layer entities.
- Blocking obstacles MUST have no mutable properties and MUST NOT participate in per-step simulation updates.
- The V1 obstacle types MUST differ visually, at minimum by color.
- No additional blocking obstacle types are planned for V1.
- In V1, users MUST configure percentage shares for `Rock` cells and `Water` cells before initialization.
- The exact number of `Rock` and `Water` cells MUST be derived from these percentages and the terrain cell count using a
  deterministic calculation rule.
- In V1, the deterministic calculation rule MUST use floor-based counts:
    - `rockCount = floor(totalTerrainCells * rockPercent / 100)`
    - `waterCount = floor(totalTerrainCells * waterPercent / 100)`
- In V1, if `rockPercent + waterPercent > 50`, initialization MUST fail fast with a validation error.
- In V1, `Rock` and `Water` cells MUST be placed at initialization time by seeded randomness on cells that are currently
  `Ground` in the terrain `GridModel`.
- `Rock` MUST be incompatible with resource placement.
- `Water` MAY later be compatible with dedicated resource types, but exact resource compatibility and interaction rules
  are deferred to Layer 2.
- In V1 visibility checks, a `Rock` directly adjacent to a pet in a scan direction MUST block line of sight to cells
  behind it in the same direction.
- In V1 visibility checks, `Water` MUST NOT block line of sight.
- Pets MAY later react to adjacent blocking obstacles; any such reaction logic is deferred to the pet behavior
  specification.

V2 note:

- Improved obstacle placement rules (for example cluster generation, minimum spacing, or border-distance constraints)
  are deferred to V2.

### V1 Terrain Colors (Draft)

- `groundColor = RGB(38, 42, 46)`
- `rockColor = RGB(96, 102, 110)`
- `waterColor = RGB(34, 78, 128)`
- `trailColorDarkest = RGB(52, 66, 48)`

Notes:

- The values above are a first visual draft for V1 and MAY be adjusted after implementation and playtesting.
- Only the darkest `Trail` color is fixed here; higher trail intensity uses the existing brightness adjustment logic.

### Layer 2: Resources

Purpose:

- Represents consumable food sources used by pets.

Entities:

- V1 MUST define exactly two resource types: `Plant` and `Insect`.
- Both resource types are mutable entities that track current availability/amount per cell.

Fixed rules:

- In V1, resource cells MUST be placed at initialization time using configuration parameters and seeded randomness.
- In V1, resource distribution MUST be configured per type via percentages:
    - `plantPercent` in range `0..100`
    - `insectPercent` in range `0..100`
- In V1, percentages MUST satisfy `plantPercent + insectPercent <= 100`.
- In V1, per-type resource-cell counts MUST be derived deterministically from total terrain cells using floor-based counts:
    - `plantCellCount = floor(totalTerrainCells * plantPercent / 100)`
    - `insectCellCount = floor(totalTerrainCells * insectPercent / 100)`
- If derived counts cannot be placed on valid `Ground` cells under all V1 occupancy constraints, initialization MUST fail fast.
- Resource cells MUST be placed only on terrain cells that currently contain `Ground` entities.
- In V1, the total count of resource cells MUST remain constant throughout the simulation.
- In V1, individual resource cells MUST NOT be created or destroyed during simulation steps.
- Pets MUST actively move toward resource cells when they seek food.
- In V1, a pet MUST consume a resource from an adjacent resource cell and MUST NOT occupy the same coordinate as that
  resource.
- A pet MUST only eat from a resource cell if `currentAmount >= consumptionPerAct` of that resource type (cell must not
  be depleted).
- Consuming a resource from a cell MUST reduce `currentAmount` by the resource type's configured `consumptionPerAct` and
  MUST increase the pet's `currentEnergy` by the resource type's configured `energyGainPerAct`, capped at `maxEnergy`.
- Each resource type MUST define both a `consumptionPerAct` value (amount deducted from the cell) and an
  `energyGainPerAct` value (energy credited to the pet); these are independent parameters.
- If multiple adjacent resource cells satisfy the minimum amount condition, the pet MUST select using these
  deterministic rules in order:
    1. Prefer the resource type with the higher `energyGainPerAct` (e.g., `Insect` before `Plant`).
    2. If equal `energyGainPerAct`, prefer the cell with higher `currentAmount`.
    3. If still tied, prefer the cell with stable coordinate order (x ascending, then y ascending).
- Resources have mutable per-cell values (current amount, max amount) that are tracked in the resource layer.
- Resources MUST regenerate over time.
- Resource regeneration MUST occur during each simulation step BEFORE trail decay as a fixed simulation step operation.
- Regeneration rates are per-resource and are configured at initialization with a base value (depending on resource
  type) plus a per-cell variance.
- In V1, the per-cell regeneration rate MUST be sampled once at initialization (`base + variance`) and remain constant
  for that cell during runtime.
- Resource amount regeneration MUST be terrain-dependent.
- Resource cells MUST NOT be placed or maintained on incompatible terrain entities (for example not on rock).

State and properties:

- Resource cells track availability/amount according to regeneration logic.
- Each resource cell MUST maintain a `currentAmount` and a `maxAmount` value.
- `currentAmount` MUST increase per step (regeneration) and MUST decrease when pets consume.
- `currentAmount` MUST be capped at `maxAmount`.

Deferred details:

- Exact final balancing of regeneration/consumption values per resource type is deferred to V1 balancing iterations.
- In V2, additional resource types and `Water`-terrain compatibility are deferred.

#### V1 Resource Types (Draft)

**Plant:**

- Visual color (draft): `RGB(76, 140, 52)` (natural green)
- Semantics: vegetation/plant-based food source, quick to regenerate, moderate energy per unit
- Usage: abundant but less energy-dense
- `energyGainPerAct`: lower than Insect (see default parameters)

**Insect:**

- Visual color (draft): `RGB(168, 106, 36)` (warm brown/orange)
- Semantics: animal-based food source (insects, small fauna), slower to regenerate, high energy per unit
- Usage: scarcer but more nutritious
- `energyGainPerAct`: higher than Plant; pets prefer Insect cells when both are adjacent

Notes:

- The two resource types above are draft proposals for V1 differentiation.
- Both are placed on `Ground` only at initialization; regeneration rules are applied per step and are terrain-dependent.
- Regeneration rates (base + variance per cell) will be fine-tuned after implementation.
- Colors and energy values MAY be adjusted after playtesting.

### Layer 3: Agents (Pets)

Purpose:

- Simulates pet lifecycle entities (active pets and eggs).

Entities:

- `Pet` (movable agent)
- `PetEgg` (immobile lifecycle entity)

Fixed rules:

- Exactly one pet agent species/type is planned.
- Individual pets MUST have multiple properties that influence movement,
  interaction, and behavior.
- Compared to existing simulations (Wa-Tor, Sugarscape), ET Pets SHOULD have fewer active agents,
  but each agent SHOULD have higher behavioral complexity.
- `PetEgg` MUST be non-movable.
- `PetEgg` MUST have dedicated parameters and lifecycle rules.
- `PetEgg` MUST store an immutable `PetGenome` as offspring blueprint, decided at
  egg creation time.
- The offspring `Pet` object MUST NOT exist before hatch.
- `PetEgg` MUST NOT embed a mutable `Pet` reference and MUST NOT duplicate the
  complete `Pet` field set.
- Parent linkage MUST be ID-based only (`parentAId`, `parentBId`) on both `Pet`
  and `PetEgg`.
- In V1, a `PetEgg` MUST NOT be destroyed or fail before hatching; no egg failure conditions exist in V1.
- A `PetEgg` MUST always hatch after exactly `incubationDuration` steps.
- In V1, pet sensing MUST use a fixed vision range of `2` and scan in all six `EDGE` directions of the hex grid.
- In V1, movement/action priorities MUST be evaluated in this fixed order:
    1. `Death-check`
    2. `Eat-if-adjacent`
    3. `Move-to-resource-if-hungry`
    4. `Reproduce-if-possible`
    5. `Move-to-enable-reproduction`
    6. `Explore/Trail`
- In V1, `Explore/Trail` MUST prefer directly adjacent `Trail` cells over directly adjacent `Ground` cells.
- If multiple adjacent `Trail` cells are available, the pet MUST prefer the one with the highest trail intensity.
- If no adjacent `Trail` cell is available, the pet MUST evaluate visible walkable cells within vision range `2` and
  prefer distant `Trail` cells over `Ground` cells.
- In V1, pet movement targets MUST be walkable `Ground`/`Trail` cells that are free of `Resource`, `Pet`, and `PetEgg`.
- Because pet steps are processed asynchronously and applied immediately, V1 uses no separate collision-resolution phase
  for movement conflicts.

State and properties:

Pet property set (V1 fixed, can be revised later):

- `petId` (unique sequence-based ID)
- `parentAId` (nullable for initial population)
- `parentBId` (nullable for initial population)
- `stepIndexOfBirth`
- `currentEnergy`
- `visionRange` (V1 fixed to `2`, can be revised later)
- `movementCostModifier`
- `reproductionCooldown`
- `traits`
- `isDead`
- `stepIndexOfDeath`

Pet age SHOULD be treated as a derived value based on `stepIndexOfBirth` and the
current simulation step.

Initial values (V1 fixed):

- In V1, `currentEnergy` MUST be the single central runtime value for survival, feeding, and reproduction.
- In V1, newly created `Pet` entities MUST initialize `currentEnergy` to the maximum configured energy value.
- In V1, `visionRange` MUST be fixed to `2` for all pets.
- Future versions MAY introduce randomized or varied starting values.

Trait schema (V1 fixed):

- In V1, `PetTraits` / `PetGenome` MUST include exactly these four inheritable traits:
  - `maxEnergy` (int, range `60..140`)
  - `movementCostModifier` (double, range `0.5..1.5`)
  - `reproductionMinEnergy` (int, range `50..90`)
  - `reproductionCooldownMax` (int, range `120..320`)
- In V1, `visionRange` is fixed to `2` and is NOT part of inheritable trait scoring.
- In V1, `genomeQualityScore` MUST be computed from the arithmetic mean of the four normalized trait values above.

PetEgg property set (V1 fixed, can be revised later):

- `eggId` (unique sequence-based ID)
- `parentAId`
- `parentBId`
- `petGenome` (immutable inherited blueprint)
- `stepIndexOfLaying`
- `incubationRemaining` (decremented each step; egg hatches when reaches `0`)

Deferred details:

- The `eggHealth`/`eggViability` field is NOT needed in V1 as eggs cannot fail or be destroyed.

#### V1 Agent Colors (Draft)

**Pet:**

- Visual color (draft): `RGB(180, 100, 160)` (warm magenta/rose)
- Semantics: active, living agent with rich behavior
- Visibility: highly visible, distinct from terrain and resources

**PetEgg:**

- Visual color (draft): `RGB(220, 200, 140)` (cream/beige, egg-like)
- Semantics: lifecycle entity awaiting hatch
- Visibility: clearly distinguishable from parent pet and other entities

Notes:

- The two agent colors above are draft proposals for V1 visual differentiation.
- Colors MAY be adjusted after implementation and playtesting.

## Life Cycle and Reproduction

Pets have age, can die, and can reproduce.

Death handling (V1 fixed):

- On death, a `Pet` MUST be marked as dead and store the death time
  (`stepIndexOfDeath`).
- In V1, a pet MUST die when `currentEnergy` reaches `0`.
- In V1, a dead `Pet` MUST remain on Layer 3 for exactly one simulation step as a visual window and then MUST be
  removed.
- During each simulation step, all `Pet` entities including dead ones are iterated in sequence.
- When a dead `Pet` is reached during iteration, it MUST be removed from Layer 3 at that point.
- Death events SHOULD still be tracked in statistics/logging.

Reproduction rules (fixed so far):

- Reproduction MUST require exactly two parent pets.
- The model MUST NOT simulate biological sex/gender categories.
- Traits/properties MUST be inheritable by offspring.
- Population design SHOULD favor long-lived pets and rare reproduction events.
- Parents MUST occupy two different adjacent cells; they MUST NOT share one cell.
- Reproduction MUST require at least one free adjacent cell where an egg can be placed.
- The egg placement cell MUST be adjacent via `EDGE` to BOTH parent pets simultaneously.
- The egg placement cell MUST contain `Ground` in the terrain layer and MUST be free of any resource, `Pet`, and
  `PetEgg` in Layers 2 and 3.
- With `CellShape.HEXAGON` and `NeighborhoodMode.EDGES_ONLY`, exactly two cells can be shared edge-neighbors of two
  adjacent cells; both are candidates for egg placement.
- Parent ID order MUST be normalized: if both parents exist, `parentAId < parentBId`.
- Direct relatives MUST NOT be valid reproduction partners in V1.
- In V1, reproduction partner validation MUST reject any pair where the two `petId` values and the four parent IDs (
  `parentAId`, `parentBId` of both pets) contain the same non-null ID more than once.
- This rule explicitly excludes parent-child pairs and sibling pairs from reproduction.
- On reproduction, parent IDs MUST be written into the egg as normalized
  (`parentAId < parentBId`).
- At egg creation, inheritance and mutation MUST produce the immutable `PetGenome`.
- If multiple reproduction partners are eligible, partner selection MUST use deterministic filtering and scoring:
    - A partner candidate MUST be directly adjacent via `EDGE`, satisfy all reproduction eligibility rules, and share at
      least one valid egg placement cell with the active pet.
    - For each candidate, a deterministic `genomeQualityScore` MUST be computed from inheritable traits in `PetGenome`.
    - In V1, each inheritable trait value MUST be normalized to the range `[0, 1]` using its trait-specific minimum and
      maximum bounds.
    - In V1, `genomeQualityScore` MUST be the arithmetic mean of all normalized inheritable trait values.
    - The candidate with the highest `genomeQualityScore` MUST be selected.
- If multiple candidates have the same `genomeQualityScore`, deterministic tie-breakers MUST be applied in this order:
    1. higher `currentEnergy`
    2. lower `petId`
    3. stable coordinate order (x, then y)
- Seeded randomness MUST be used only if all deterministic tie-breakers are still equal.
- If multiple egg placement cells are valid, deterministic tie-break rules MUST be applied first (stable coordinate
  order x, then y); seeded random fallback is allowed only if deterministic rules still tie.
- Both parents MUST have their `reproductionCooldown` reset to its maximum configured value after successful
  reproduction.
- Offspring MUST hatch from a `PetEgg` entity after a delay (incubation period).
- A `Pet` instance MUST be created only at hatch time from `PetGenome`.
- At hatch time, the `PetEgg` MUST be removed and a new `Pet` MUST be created on the
  same coordinate in Layer 3.
- The new `Pet` MUST be instantiated from `PetGenome` plus hatch-time context
  (for example `stepIndexOfBirth`).
- The hatched `Pet` MUST receive a new unique `petId` and MUST retain the same
  normalized parent IDs from the egg.

Reproduction eligibility (V1 fixed, can be revised later):

- Both parents MUST satisfy a minimum age threshold.
- Both parents MUST satisfy a minimum current energy threshold.
- Parents MUST be direct neighbors (same-cell occupancy is forbidden by the grid model).
- Both parents MUST have reproduction cooldown available.

Inheritance model (V1 fixed):

- Inheritance MUST use trait averaging plus small mutation.
- Offspring traits MUST be computed from the average of both parent trait values.
- A small mutation factor SHOULD be applied per inheritable trait.

This differs from Wa-Tor by targeting lower population density and lower birth frequency.

## V1 Default Parameters (Initial, To Be Fine-Tuned)

The following defaults are accepted as a first balancing baseline and are expected
to be adjusted after playtesting.

### Egg Lifecycle

- `incubationDuration = 10` (steps; fixed constant in V1, to be adjusted during testing)
- In V1, eggs cannot fail; all eggs hatch after exactly `incubationDuration` steps.

### Resource Amounts and Rates (Draft, to be adjusted during V1 testing)

**Plant:**

- `plantMaxAmountRange = [3, 6]` (random per cell at initialization)
- `plantBaseRegenerationRate = 0.2` (currentAmount gained per step)
- `plantConsumptionPerAct = 2` (currentAmount lost per pet feeding act)
- `plantEnergyGainPerAct = 3` (currentEnergy gained by pet per feeding act)

**Insect:**

- `insectMaxAmountRange = [6, 12]` (random per cell at initialization)
- `insectBaseRegenerationRate = 0.05` (currentAmount gained per step)
- `insectConsumptionPerAct = 4` (currentAmount lost per pet feeding act)
- `insectEnergyGainPerAct = 8` (currentEnergy gained by pet per feeding act)

**Shared:**

- `regenerationRateVariance = 0.02` (+/-0.02 added to base regeneration rate per cell at initialization)
- `initialCurrentAmount = maxAmount` (all resource cells start fully stocked)

Notes:

- The resource amount/rate values above are non-binding draft baselines for V1 balancing.

### Pet Energy (Draft, to be adjusted during V1 testing)

- `maxEnergy = 100`
- `energyLossPerStep = 1`
- `reproductionMinEnergy = 70`
- `eatIfAdjacentEnergyThreshold = 80` (pet eats from an adjacent resource cell whenever
  `currentEnergy < eatIfAdjacentEnergyThreshold`; opportunistic eating even when not critically hungry)
- `resourceSeekingEnergyThreshold = 60` (pet actively moves toward the nearest visible resource only when
  `currentEnergy < resourceSeekingEnergyThreshold`; triggers `Move-to-resource-if-hungry`)
- In V1, consuming resources restores `currentEnergy` by the resource type's `energyGainPerAct` value, capped at
  `maxEnergy`. The pet's hunger (i.e., `maxEnergy - currentEnergy`) is NOT compared to the resource amount - the pet
  always eats a full act if the resource cell has enough supply.
- Both thresholds are first-draft values; they MUST be adjusted during V1 testing.

### Pet Population Initialization (V1)

- `petCount = 5` (initial number of pets at simulation start)
- Valid range: `0..20` (integer only)
- Initialization MUST place exactly `petCount` pets on valid Layer-3 start coordinates.
- Start coordinates MUST satisfy all V1 occupancy constraints (no overlap with `Resource`, `Pet`, or `PetEgg`).
- Under equal seed and equal configuration, initial pet placement MUST be deterministic.

### Trail Intensity

- `trailIncreasePerEntry = 1.0`
- `trailDecayPerStep = 0.02`
- `trailMin = 0`
- `trailMax = 100`

### Reproduction Thresholds

- `minAge = 120`
- `minEnergy = 70`
- `reproductionCooldown = 200`
- `parentDistance = adjacent cells only` (same cell is not allowed)
- `requiredFreeAdjacentEggCell = true`

### Mutation

- `mutationChancePerTrait = 8%`
- `mutationDelta = +/-5%` of the trait value
- Trait-specific min/max bounds MUST be enforced.

### Death Visualization

- `deadVisualSteps = 1` (fixed for V1)
- `deathTimeField = stepIndexOfDeath`
- No dedicated `DeadPet` entity is created; visualization uses `Pet` status only.

## Open V1 Decisions

There are currently no unresolved V1 decision blocks.

The following decision blocks are resolved:

- Trait schema for `PetGenome` / `PetTraits` is fixed for V1:
  - `maxEnergy` in `60..140`
  - `movementCostModifier` in `0.5..1.5`
  - `reproductionMinEnergy` in `50..90`
  - `reproductionCooldownMax` in `120..320`
  - `visionRange` remains fixed (`2`) and is excluded from trait scoring
  - `genomeQualityScore` uses arithmetic mean over normalized inheritable trait values

- For `Move-to-resource-if-hungry`: when multiple visible resource cells are at the same minimum hex distance, the pet
  MUST target the cell with higher `energyGainPerAct` (Insect before Plant); if still tied, prefer higher
  `currentAmount`; if still tied, stable coordinate order (x ascending, then y ascending).
- For `Explore/Trail` movement tie-breaks: stable coordinate order (x ascending, then y ascending) is the final
  deterministic rule before seeded-random fallback.

## Future Ideas (Post-V1)

The following topics are deferred and NOT required for V1. They are collected here for future planning.

### Agent and Species

- **Multiple pet species:** V1 supports exactly one species. Future versions may introduce additional species with
  distinct behavior profiles, visuals, and interaction rules.
- **Sex/gender modeling:** Explicitly out of scope for V1. Could be introduced as an optional trait dimension in a later
  version.
- **Complex genetics:** V1 uses trait averaging plus mutation only. A more sophisticated inheritance model (for example
  dominance, recessive traits, gene pools) is deferred.
- **Separate health model (`currentHealth`):** V1 uses `currentEnergy` as the single central runtime value. A future
  version may reintroduce `currentHealth` as a separate injury/disease/survival system.
- **Pet social behavior:** V1 pets ignore proximity to other pets. Future versions may add avoidance, attraction,
  grouping, or territorial behaviors.
- **Varied pet initial values:** V1 starts all pets at maximum configured values; randomized or varied starting states
  are a V2+ option.
- **Trail intensity per pet type:** V1 applies a fixed global `trailIncreasePerEntry`; per-species or property-based
  reinforcement is a future option.
- **Reproduction history field (`stepIndexOfReproductions`):** V1 does not require this field. A future version may add
  an ordered reproduction-history list for analytics/debugging.

### Reproduction and Lifecycle

- **Egg failure conditions:** Eggs cannot fail in V1. A future version may introduce egg mortality based on
  environmental or parent-proximity conditions.

### Terrain

- **Advanced pet vision and perception system:** V1 defines a basic LOS rule (`Rock` blocks, `Water` does not). Advanced
  perception (for example peripheral weighting, memory, smell proxies, probabilistic sensing) is deferred.
- **Pet reaction to obstacles:** Logic for pets reacting to adjacent `Rock` or `Water` cells is deferred to a future
  version.
- **Obstacle clustering:** Improved obstacle placement (cluster generation, minimum spacing, border distance) is
  deferred to V2.
- **Trail formula variants:** V1 uses linear reinforcement and linear decay. Nonlinear or exponential variants may be
  explored in V2 or later.

### Resources

- **Additional resource types:** V1 defines exactly `Plant` and `Insect`. Further resource types may be added in future
  versions.
- **Water-terrain resources:** Resource types compatible with `Water` cells (for example drinking water, fish) are
  deferred to V2.

### Balancing and Regulation

- **Advanced ecosystem balancing:** V1 uses fixed default parameters. Dynamic balancing, adaptive parameters, or
  scenario-based presets are deferred.
- **Population hard cap:** V1 has no hard cap on `Pet` or `PetEgg` count. Natural regulation occurs through resource
  scarcity and geometric egg-placement constraints. Population control mechanisms (soft or hard cap) may be added in V2
  if needed.
- **Mutation policy refinements:** V1 uses global mutation parameters. Trait-specific mutation distributions, per-trait
  mutation caps, and asymmetric mutation ranges are deferred.

---

*This document is a living specification and will be extended step by step.*
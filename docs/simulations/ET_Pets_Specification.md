# ET Pets - Simulation Specification (V1)

> Purpose: Define the behavior contract for the ET Pets simulation.
> This document is optimized for implementation and maintenance by GitHub Copilot.

## 1) How To Use This Spec (Copilot-First)

### Requirement keywords

- `MUST`: required for V1 correctness.
- `SHOULD`: strongly recommended for V1 quality.
- `MAY`: optional or future-facing.

### Detail levels

- **WHAT**: user-visible behavior and simulation rules.
- **HOW**: algorithm and scoring details needed for reproducibility.
- **WHERE**: implementation anchors (class/symbol names) for fast navigation.

### Source-of-truth policy

1. Runtime behavior in released V1 code is the temporary source of truth.
2. Any intentional mismatch between code and spec MUST be documented in the "Known Deviations" table.
3. Changes to V1-critical constants MUST update this spec in the same change set.

### Copilot execution policy

When editing code for ET Pets, Copilot SHOULD:

1. Preserve deterministic behavior under fixed seed.
2. Preserve fixed topology constraints (hex, edges-only, block edges).
3. Avoid changing V1-critical constants unless explicitly requested.
4. Add or update tests for behavior changes.

## 2) Scope

ET Pets is a 2D asynchronous agent-based simulation in Java/JavaFX.
The world is an alien terrarium with pets, food resources, and terrain trails.

V1 goals:

- one pet species,
- exactly three grid layers,
- asynchronous updates,
- egg-based reproduction,
- trail creation and decay,
- deterministic seeded behavior,
- per-step aggregate statistics.

Out of scope for V1:

- multiple species,
- sex/gender model,
- advanced genetics beyond average + mutation,
- adaptive ecosystem auto-balancing.

## 3) Global Constraints (MUST)

- Simulation identifier MUST be `etpets`.
- Cell shape MUST be `CellShape.HEXAGON`.
- Neighborhood mode MUST be `NeighborhoodMode.EDGES_ONLY`.
- Grid edge behavior MUST be `GridEdgeBehavior.BLOCK_XY`.
- Grid size MUST be configurable in range `20x20` to `200x200`.
- All random decisions MUST use configured seed.
- Under identical config + seed + initial state, results MUST be reproducible.
- Layer ownership MUST remain strict:
    - terrain state in terrain layer only,
    - resource state in resource layer only,
    - lifecycle entities in agent layer only.
- A coordinate MUST NOT contain both a resource and an agent (`Pet` or `PetEgg`) in V1.

## 4) Layer Model (Fixed: 3 Layers)

Order is fixed:

1. Terrain layer
2. Resource layer
3. Agent layer

### 4.1 Terrain Layer

Purpose: walkability, obstacles, and trail state.

Entities:

- `Ground` (default, walkable)
- `Trail` (walkable, mutable intensity)
- `Rock` (blocking)
- `Water` (blocking)

Rules:

- Pets MAY move on `Ground` and `Trail` only.
- `Rock` and `Water` are non-walkable.
- Entering `Ground` creates a `Trail`.
- Entering `Trail` increases intensity (capped).
- Trail decays each step by fixed amount.
- Trail with intensity below minimum reverts to `Ground`.
- Trail decays only when no active pet occupies that cell.
- In V1 LOS checks, `Rock` blocks line-of-sight and `Water` does not.

### 4.2 Resource Layer

Purpose: consumable food with regeneration.

Entities:

- `Plant`
- `Insect`

Rules:

- Resources are placed during initialization only.
- Resource cell count is constant during runtime.
- Each resource cell tracks `currentAmount` and `maxAmount`.
- Consume requires `currentAmount >= consumptionPerAct`.
- Consume reduces `currentAmount` and increases pet energy.
- Regeneration runs every step and is capped at `maxAmount`.
- Regeneration rate is sampled once per cell at init and then fixed.

### 4.3 Agent Layer

Purpose: lifecycle entities.

Entities:

- `Pet` (mobile)
- `PetEgg` (immobile)

Rules:

- `PetEgg` stores immutable `PetGenome` only.
- Offspring `Pet` is created at hatch time, not before.
- Parent linkage is ID-based (`parentAId`, `parentBId`).
- Eggs do not fail in V1.

## 5) Entity Schemas

### 5.1 Pet

Required state:

- `petId: long`
- `parentAId: Long?`
- `parentBId: Long?`
- `stepIndexOfBirth: int`
- `traits: PetTraits`
- `currentEnergy: int`
- `reproductionCooldownRemaining: int`
- `previousCoordinate: GridCoordinate?`
- `previousPreviousCoordinate: GridCoordinate?`
- `dead: boolean`

Derived:

- `age = stepIndex - stepIndexOfBirth`

### 5.2 PetEgg

Required state:

- `eggId: long`
- `parentAId: long`
- `parentBId: long`
- `petGenome: PetGenome`
- `stepIndexOfLaying: int`
- `incubationRemaining: int`

### 5.3 PetTraits / PetGenome

V1 inheritable traits:

- `maxEnergy: int`
- `movementCostModifier: double`
- `reproductionMinEnergy: int`
- `reproductionCooldown: int`

`genomeQualityScore` uses arithmetic mean of normalized trait values.

## 6) Simulation Step Pipeline (Order is fixed)

Per step:

1. Agent logic (`EtpetsAgentLogic.apply`)
2. Resource regeneration (`EtpetsResourceLogic.apply`)
3. Terrain trail decay (`EtpetsTerrainLogic.apply`)

This order MUST remain stable for deterministic behavior.

## 7) Lifecycle and Reproduction Rules

### 7.1 Death

- Pets lose passive energy each step.
- A pet dies when energy drops below minimum threshold.
- Starting at `PET_AGEING_EFFECTS_AGE_MIN`, pets MAY also die from age-related mortality.
- Age-related mortality chance increases with age and is capped by a maximum probability.
- Dead pet remains visible for exactly one step, then is removed.
- Death events update cumulative dead count statistics.

### 7.2 Reproduction Preconditions

Both parents MUST:

- be alive,
- satisfy minimum age,
- satisfy minimum energy (trait-based),
- have cooldown available,
- be edge-adjacent,
- not be direct relatives.

Direct relative rejection rule (V1):

- If any non-null ID repeats across both pets' own IDs and parent IDs, pair is invalid.

### 7.3 Egg Placement

- Egg cell MUST be edge-adjacent to both parents.
- Egg cell MUST be `Ground` in terrain layer.
- Egg cell MUST be empty in resource and agent layers.
- If multiple cells are valid, deterministic order is preferred.

### 7.4 Inheritance and Mutation

- Child traits = average of both parent traits.
- Mutation applies independently per trait with configured chance and delta.
- Values are clamped to trait bounds.

### 7.5 Hatching

- Egg incubation decrements every step.
- Egg hatches after fixed incubation duration.
- Hatch creates a new `Pet` with new `petId`, inherited parent IDs, and genome traits.

## 8) Agent Decision Model (V1)

V1 uses unified action scoring (not simple fixed-priority branching).

Candidate actions:

- `MOVE`
- `EAT`
- `REPRODUCE`
- `WAIT`

Process:

1. Build local neighborhood snapshot (radius 2, edges-only).
2. Compute look-ahead bonuses from ring-2 to ring-1.
3. Score ring-1 movement targets.
4. Score ring-0 interactions (eat/reproduce/wait).
5. Pick top score; break ties with seeded randomness.

## 9) Scoring Model (HOW)

### 9.1 MOVE score

Move score combines:

- base score,
- resource look-ahead bonus (hunger-sensitive),
- partner look-ahead bonus,
- trail bonus (curve-based),
- exploration bonus on fresh ground,
- oscillation penalties,
- low-mobility penalty,
- crowding penalty.

Special case:

- rare exploration spike on fresh ground may multiply score.

### 9.2 EAT score

Eat score combines:

- relative hunger term,
- panic term under low absolute energy,
- resource energy-gain term,
- age bonus,
- overfill waste penalty.

### 9.3 REPRODUCE score

Reproduce score combines:

- weighted average of both parents' `genomeQualityScore`,
- weighted minimum quality floor,
- clamped final score range.

### 9.4 WAIT score

Wait score behavior:

- Before `PET_AGEING_EFFECTS_AGE_MIN`, `WAIT` score is always `0`.
- At and after `PET_AGEING_EFFECTS_AGE_MIN`, a non-zero `WAIT` score is sampled probabilistically.
- The chance of obtaining a non-zero `WAIT` score increases with age and is capped.
- If triggered, the `WAIT` score starts at `1`, increases with age, and is capped by `PET_AGEING_WAIT_SCORE_MAX`.

## 10) Initialization Rules

### 10.1 Percent-based placement

Counts use floor behavior from integer math:

- `rockCount = floor(totalCells * rockPercent / 100)`
- `waterCount = floor(totalCells * waterPercent / 100)`
- `plantCount = floor(totalCells * plantPercent / 100)`
- `insectCount = floor(totalCells * insectPercent / 100)`

Validation:

- `rockPercent + waterPercent <= 50`
- `plantPercent + insectPercent <= 100`
- `petCount in [0, 20]`

### 10.2 Placement order

1. Terrain obstacles (`Rock`, then `Water`) on shuffled coordinates.
2. Resources on traversable empty cells.
3. Pets on traversable empty cells.

All placement MUST be seeded and deterministic.

## 11) V1 Critical Constants (Reproducibility)

These values are critical for stable V1 behavior.

| Constant                               |  Value |
|----------------------------------------|-------:|
| `PET_FERTILITY_AGE_RANGE_MIN`          |  `400` |
| `PET_EGG_INCUBATION_REMAINING_DEFAULT` |   `20` |
| `PET_STEP_ENERGY_LOSS`                 |    `1` |
| `TRAIL_INTENSITY_INCREASE_PER_ENTRY`   |   `40` |
| `TRAIL_INTENSITY_DECAY_PER_STEP`       |    `1` |
| `PET_GENOME_MUTATION_CHANCE_PER_TRAIT` | `0.08` |
| `PET_GENOME_MUTATION_DELTA`            | `0.05` |

## 12) V1 Parameter Baseline (Actual Implementation)

### 12.1 Egg

- `incubationDuration = 20` `[VALIDATED]`

### 12.2 Plant

- `maxAmountRange = [12, 30]`
- `baseRegeneration = 0.30`
- `regenerationVariance = +/-0.08` `[VALIDATED]`
- `consumptionPerAct = 1`
- `energyGainPerAct = 4`

### 12.3 Insect

- `maxAmountRange = [4, 10]`
- `baseRegeneration = 0.04`
- `regenerationVariance = +/-0.02` `[VALIDATED]`
- `consumptionPerAct = 3`
- `energyGainPerAct = 20`

### 12.4 Pet traits and energy

- `maxEnergy range = [75, 150]`
- `movementCostModifier range = [0.5, 1.5]`
- `reproductionMinEnergy range = [30, 80]`
- `reproductionCooldown range = [100, 300]`
- birth energy factor: `0.2 * maxEnergy`
- passive step loss: `1`

### 12.5 Trail

- `intensityDefault = 20`
- `increasePerEntry = 40`
- `decayPerStep = 1`
- `intensityMin = 1`
- `intensityMax = 10000`
- `trailBonusThreshold = 120` `[VALIDATED]`

### 12.6 Movement exploration spike

- chance: `0.005`
- multiplier: `3.0`
- status: `[DRAFT]` (subject to future balancing)

### 12.7 Ageing behavior

- `ageingEffectsAgeMin = 4000`
- mortality chance base: `0.0002`
- mortality chance increase per ageing step: `0.00001`
- mortality chance max: `0.08`
- wait chance base: `0.002`
- wait chance increase per ageing step: `0.00002`
- wait chance max: `0.25`
- wait score increase step span: `10`
- wait score max: `50`

## 13) JavaFX / UI Notes (V1)

UI-exposed config parameters:

- `gridWidth: 20..200`
- `gridHeight: 20..200`
- `cellEdgeLength: 5..50`
- `seed`
- `rockPercent: 0..100`
- `waterPercent: 0..100`
- `plantPercent: 0..100`
- `insectPercent: 0..100`
- `petCount: 0..20`

Current UI defaults (from view model):

- `rockPercent = 1`
- `waterPercent = 2`
- `plantPercent = 5`
- `insectPercent = 1`
- `petCount = 10`

Rendering intent:

- terrain rendered first,
- resources rendered second,
- agents rendered third,
- dead pets shown darker for one-step death window,
- selected cell gets white outline.

## 14) Statistics Contract

Per-step stats MUST track at minimum:

- active pet count,
- egg count,
- cumulative dead pet count.

Step timing stats SHOULD also be tracked and exposed.

## 15) Known Deviations From Early Draft Spec

These are already reflected in runtime behavior and this document:

- Egg incubation is `20` (not `10`).
- Insect energy gain is `20` (not early low draft values).
- Fertility age minimum is `400` (not early `120`).
- Decision logic is unified scoring, not pure fixed-priority branching.
- Trail values are integer and larger-scale than early draft values.

## 16) Open V1 Decisions

No unresolved V1 decision blocks.

## 17) Copilot Acceptance Checklist (for code changes)

A change is V1-safe only if all checks pass:

- [ ] Determinism preserved under fixed seed.
- [ ] Layer separation unchanged.
- [ ] Topology constraints unchanged (hex, edges-only, block edges).
- [ ] Step pipeline order unchanged.
- [ ] V1-critical constants unchanged (or intentionally updated with spec update).
- [ ] Statistics contract still satisfied.
- [ ] Reproduction relative-check behavior preserved.
- [ ] Trail create/reinforce/decay lifecycle preserved.

## 18) V2 Backlog (Prioritized)

### P1 - low complexity

- Add reproduction history tracking.
- Add terrain compatibility per resource type.
- Add per-trait mutation rates.

### P2 - medium complexity

- Multiple species.
- Obstacle clustering strategies.
- Population soft/hard cap options.
- Optional sex/gender model.

### P3 - high complexity

- Separate `currentHealth` model.
- Social behavior (avoidance/grouping/territory).
- Advanced sensing (memory/scent/probabilistic vision).
- Dynamic ecosystem balancing.
- Mendelian-like genetics model.

### P4 - product features

- Replay/time-travel.
- Event system (disasters, seasonal shifts).
- Scenario presets and config profiles.
- Lineage visualization and analytics dashboard.

---

This is a living specification for V1 behavior fidelity and V2 planning.

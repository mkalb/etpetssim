# ET Pets - Roadmap

**Status:** in progress (working plan for #70 until the 1.0 release; roadmap for the time after it)

**Related issues:** #70 (ET Pets review for 1.0), #102 (regenerate the simulation docs), #124 (configuration presets),
#123 (polygon shapes for entities), #122 (connected-area display mode), #58 (statistics history and charts)

## Goal

ET Pets stands out from the more mathematical simulations as a pet simulation: pets have visible individuality,
inherited traits influence their behavior, and the user can follow single pets over a long time.

All planned improvements of ET Pets are collected in this plan, not in #70. They are split by time:

- "Before the 1.0 release": fixes of errors and weaknesses, and a few small features. The effort must stay small.
- "After the 1.0 release": the larger topics, ordered by their dependencies. Turn a topic into an issue when it is
  planned.
- "Maybe in the future": vague ideas that may never be implemented.

## Current state

- Rules: `EtpetsConfig` has only `neighborhoodMode` as a rule, and it is fixed. All behavior values (e.g., fertility
  age 400, aging effects from age 4000, score weights) are constants in `EtpetsBalance`.
- Grid: the cell shape is fixed to hexagons with edge neighbors; the edge behavior (`BLOCK_XY`) and the cell display
  mode (`SHAPE`) are fixed as well (`EtpetsConstraints`). The egg placement relies on the hexagons: an egg needs a
  common neighbor of both parents, which exists for hexagons (two cells) but not for triangles or squares with edge
  neighbors.
- Perception: a pet only sees two rings around its cell (`CellNeighborhoods.cellsByRadiusRings(..)` in
  `EtpetsAgentLogic`); ring 2 only adds a look-ahead bonus to the cells of ring 1. There is no path planning and no
  line of sight.
- Terrain: rock and water are both impassable; resources (plants and insects) exist only on ground. A cell with a
  resource is not walkable; pets eat from a neighboring cell.
- Actions: a pet performs the best-scored action. The reproduce score is at least 30 (about 58 to 76 with the initial
  traits), the move score at most 40, and the eat score of a pet with enough energy to reproduce is usually lower.
  An eligible pair with a free egg cell therefore reproduces almost always.
- Genetics: one mutation rate applies to all traits (chance 8% per trait, change of 5% of the value). The findings
  F1 to F5 below describe the weaknesses.
- Statistics: three metrics (active pets, eggs, cumulative deaths); `EtpetsStatistics` already assigns the metric of
  the active pets to the primary chart group (window of 1,000 steps), while #58 (open, `needs-refinement`) covers
  further statistics history and charts. The observation view shows no details of the selected pet (TODO in
  `EtpetsObservationView`).
- Display: pets are circles whose brightness follows their energy; the minimum cell edge length is 5, the maximum grid
  size is 200 x 200.
- Editing: terrain and resources can be set; pets and eggs cannot be placed or removed.

## Before the 1.0 release

Working part of #70. The findings come from the analysis of 2026-11-10; the decisions are by the maintainer
(2026-11-10). Each fix changes the behavior of the simulation and the random sequence of a seed; observe the result
in the running simulation.

### Fixes

#### F1 Genome quality score rewards disadvantageous traits

- Finding: `PetTraits.genomeQualityScore()` averages the normalized values (0 to 1) of all four traits. A higher
  `movementCostModifier` (0.5 to 1.5) and a longer `reproductionCooldown` (100 to 300) are disadvantages, but they
  raise the score; a pet with all traits at their minimum scores `0.0`. The only caller is
  `EtpetsAgentLogic.computeReproduceScore(..)`. Because an eligible pair reproduces almost always (see "Current
  state"), the score effectively only decides between several possible partners.
- Decision: count `movementCostModifier` and `reproductionCooldown` as `1 - normalized`, remove
  `reproductionMinEnergy` from the score (a threshold, not a quality), and keep `maxEnergy` with the same weight as
  the other terms.
- Affected: `PetTraits` (Javadoc), `PetTraitsTest`.
- Origin: deferred finding of the record plan of #23 (`code-record-types.md`, category 7; deleted, see the Git
  history).

#### F2 `movementCostModifier` has almost no effect on the movement cost

- Finding: `EtpetsAgentLogic.applyMoveAction(..)` computes the cost as
  `Math.max(1, Math.round(movementCostModifier))`. Within 0.5 to 1.5, the result is always 1, except 2 for exactly
  1.5. The trait only influences the exploration bonus of the move score.
- Decision: round the cost at random, so that its expected value equals the modifier: the integer part plus 1 with
  the probability of the fractional part (e.g., 1.3: cost 1, or 2 with a chance of 30%; 0.5: cost 0 or 1 with a
  chance of 50% each). The minimum cost of 1 is dropped; otherwise, values below 1 have no effect.
- Affected: `EtpetsAgentLogic.applyMoveAction(..)`, which takes the `Random` of the step as a new parameter, and
  `EtpetsAgentLogicTest`. A fractional part of 0 (e.g., the default modifier 1.0) needs no random draw; keep this
  early return and explain it in a comment (`.claude/rules/simulations.md`), because it keeps the random sequence of
  a seed stable.

#### F3 Traits have hardly any diversity

- Finding: the initial pets differ only in `maxEnergy`; the other three traits start with their default values for
  all pets (`EtpetsSimulationManager.initializePets(..)`). `PetGenome.fromParents(..)` averages the traits of both
  parents, which halves the variance in each generation, and the mutation is rare and small. The population
  converges to its mean, and individuality hardly arises.
- Decision: initial pets get random values for all four traits over their full ranges. A child takes each trait
  from one of its parents at random instead of the average. The mutation stays as today (chance 8%, change of 5%)
  for now; adjust it after observing the running simulation.
- Decision: there must be no infertile pets. With random values, `reproductionMinEnergy` (30 to 80) could exceed
  `maxEnergy` (75 to 150). The range of `reproductionMinEnergy` is therefore lowered to 30 to 70
  (`PET_TRAITS_REPRODUCTION_MIN_ENERGY_MAX`), so that it is always below the smallest `maxEnergy`.
- Affected: `EtpetsSimulationManager.initializePets(..)`, `PetGenome.fromParents(..)` (Javadoc), `EtpetsBalance`,
  `PetGenomeTest`, and a relation test for the two ranges in `EtpetsBalanceTest`.

#### F4 Reproduction costs no energy

- Finding: `reproductionMinEnergy` is only a threshold; the parents only get a cooldown. The child starts with 30% of
  its `maxEnergy` (`PET_CURRENT_ENERGY_BIRTH_FACTOR`), which comes from nothing. A low `reproductionMinEnergy` is
  therefore always an advantage.
- Decision: the parents pay the birth energy of the child, each one half (11 to 23 with the current ranges, which
  is below the minimum of `reproductionMinEnergy`, so that no parent dies from it). The energy is deducted when the
  egg is laid, together with the cooldown. `reproductionMinEnergy` then becomes a real trade-off between early
  reproduction and the risk of starving afterwards.
- Expected effect: the population grows more slowly, because the parents have to eat again after reproducing.
- Affected: `EtpetsAgentLogic.applyReproduceAction(..)` and `hatchEgg(..)` (the birth energy is computed in one
  place), `EtpetsAgentLogicTest`.

#### F5 Egg placement is restrictive and deterministic

- Finding: `EtpetsAgentLogic.findEggPlacementCoordinate(..)` accepts only a common neighbor of both parents that is
  plain ground without a trail, a resource, or an agent. Pets leave trails around them, so that both candidate cells
  are often rejected. Among several candidates, the first one by coordinate order is chosen, not a random one. The
  method has two TODOs ("Optimize method" and "replace compareCoordinates with random or better solution").
- Decision: the egg cell stays a common neighbor of both parents; ET Pets has only hexagon cells. Every walkable
  common neighbor is accepted (`EtpetsCell.isWalkable()`, which includes trails and matches the check in
  `applyReproduceAction(..)`). Among two candidates, the egg cell is chosen at random (no random draw for a single
  candidate). The method is simplified so that both TODOs are resolved.
- Decision: the trail behavior under an egg stays as the current code defines it: laying an egg does
  not change the terrain. A trail does not decay while an agent is on its cell (`EtpetsTerrainLogic`), so that the
  trail under an egg keeps its intensity during the incubation (20 steps). Hatching creates a trail on ground
  (intensity 50) or reinforces the existing trail (by 20), like every other pet action on a cell. Check the visual
  result in the running simulation.
- Affected: `EtpetsAgentLogic.findEggPlacementCoordinate(..)`, `EtpetsAgentLogicTest`.

#### F8 Logging in the step loop

- Finding: `EtpetsAgentLogic` logs hatching and death at `info` inside the step loop, and it contains one
  commented-out log call in `selectAndApplyAction(..)`. The helper methods `formatActionCandidates(..)` and
  `formatActionCandidate(..)` serve only that call.
- Decision: the logging stays during the development. Directly before the 1.0 release, the `info` log calls in the
  step loop and the commented-out log call are removed (exception to the rule in `.claude/rules/java.md` that
  commented-out `AppLogger` calls may be kept; approved by the maintainer). The commented-out blocks "Only for
  testing / debugging" in `EtpetsSimulationManager` are removed as well.
- Consequences of the removal: the `log...` methods, the two `format...` methods, the constants `LOG_COMPONENT` and
  `DISPLAY_STRING_CAPACITY_HINT`, and the imports `AppLogger` and `Locale` become unused and are removed.
  The `@SuppressWarnings` that only cover the removed code (`"CommentedOutCode"` on `selectAndApplyAction(..)` and on
  the three methods `initializeTerrain(..)`, `initializeResources(..)`, and `initializePets(..)`, and `"unused"` on
  `formatActionCandidates(..)`) are removed too; the suppressions policy requires the review of the maintainer.

### New features

Decision: all four features are implemented before the 1.0 release. The effort must stay small; the larger parts of
their topics follow after the release.

- N1 Details of the selected pet in the observation view: age, energy, traits, parents, and last action (resolves
  the TODO in `EtpetsObservationView`; first step of topic 6). Small effort.
- N2 Pet color by individual or trait, e.g., a slight hue variation, in addition to the brightness by energy (first
  step of topic 5). Small effort.
- N3 Three configurable rules, moved from `EtpetsBalance` into `EtpetsConfig` (first step of topic 1): the mutation
  chance per trait (today 8%), the fertility age (today 400), and the age from which the aging effects start (today
  4000). No further values before the 1.0 release. Small to medium effort (config, constraints, config view, texts
  in both languages, tests). The values are not yet reachable from the logic: `Pet` reads the two ages directly from
  `EtpetsBalance` (`hasReachedAgingEffectsAge(..)`, `agingStepsAtStepIndex(..)`, and
  `isReproductionEligibleByState(..)`), `EtpetsAgentLogic` reads the mutation chance, and `EtpetsStepRunner` has no
  config yet. The config must be passed through `EtpetsSimulationManager`, `EtpetsStepRunner` (constructor order:
  grid model, config, further inputs; see `.claude/rules/simulations.md`), and `EtpetsAgentLogic` to these methods.
  The `Balance` constants become `Constraints` defaults and limits.
- N4 An edit tool that removes a pet or an egg (part of topic 7). The agent is taken out of the simulation: it
  disappears at once, without the darkened step of a dead pet, and it is not counted as a death; the count of
  active pets or eggs is adjusted. A dead pet stays on the grid for one step and is already excluded from the active
  pets (`EtpetsAgentLogic`); removing it must not decrease the count a second time. Removing the last agent ends the
  simulation like an extinction (`EtpetsTerminationCondition` checks the counts of active pets and eggs). Medium
  effort.

Open points:

- N2: which value determines the color (the ID, one trait such as `maxEnergy`, or a mix of the traits)? Left open
  by the maintainer (2026-11-10).
- F10 Type of an empty cell (found in #23, 2026-11-12): the observation view shows `-` as the type of an empty cell,
  because `NoResource` and `NoAgent` have the descriptor IDs `no_resource` and `no_agent` without an entity
  descriptor. Sugarscape has the same IDs (#75); decide whether it is fixed before the 1.0 release, and fix it the
  same way in both simulations.

### Verification

- The effect of the fixes and the development of the traits over many generations are observed in the running
  simulation. Decision: no manual headless runner is added.
- The points of the checklist of #24 that need the running application (colors, performance with large grids,
  start, pause, and restart, log output) are tracked in #70.

### Not changed

- F6 Initial pet count of 0 or 1: with 0 pets the simulation is finished at once, and one pet cannot reproduce.
  Decision: intended, the minimum stays 0.
- F9 Docs: `docs/simulations/etpets.md` does not mention the non-walkable resource cells, the fertility age, the
  exclusion of direct relatives, and the end of the simulation. Decision: no change here; the docs are created anew
  in #102.

## After the 1.0 release

### 1. Rules and population dynamics

- Make the rules configurable like in the other simulations: move selected values from `EtpetsBalance` into
  `EtpetsConfig`. N3 is the first step.
- Lifespan: pets with personality may live longer. A longer life means fewer generations, so reproduction must be
  slowed down accordingly.
- Presets that set up a complete experience, built on #124: e.g., "Evolution" (fast-changing populations, frequent
  inheritance, small cells) and "Pets" (few long-living pets, rare inheritance, large display).

Open points:

- Which further `EtpetsBalance` values become configurable, and which stay fixed?
- How are lifespan and reproduction balanced in both presets?

### 2. Individuality and inheritance

- Inherited traits have a real influence on behavior first; making them visible comes second. F1 to F4 are the
  first steps.
- Per-trait mutation rates; reproduction history and lineage tracking.
- Optional names for pets.
- Make inheritance visible in the observation view and the statistics (e.g., trait averages over time, building on
  #58).

Open points:

- Which traits exist beyond the four of today, and how does each one change the behavior measurably?
- Are names generated, inherited, or both?

### 3. Perception and movement

- Revise sight range and path finding: pets see further and plan a path to a target instead of choosing only among
  neighboring cells.
- Line of sight: rock blocks the sight, pets can see across water.
- Optional memory of places with resources.

Open points:

- How far do pets see, and how is line of sight computed for triangle, square, and hexagon cells?
- Which path finding fits the step budget for large grids (e.g., breadth-first search within the sight range)?
- Is memory worth it? It overlaps with the trails and makes balancing harder.

### 4. Terrain and resources

- Make the difference between plants and insects clearer and visible (e.g., in regeneration, energy, or movement);
  test the actual effect on the population first.
- Distinguish rock and water beyond the line of sight (see topic 3).
- Water as a source: fish (or water plants) as a third resource that pets hunt from the shore, water as a drinking
  source, or both.
- Terrain compatibility per resource type.
- Better terrain generation: clustered obstacles and organic patterns instead of uniform random placement.

Open points:

- Fish, drinking, or both? Hunger and thirst together may become too complicated.
- Resources exist only on ground today; resources on or next to water also affect the edit action "Set Resource".

### 5. Display

- Draw pets more expressively than a circle, also if drawing gets somewhat slower; build on the polygon shapes of #123.
- The appearance of a pet follows its personality or traits; the brightness by energy may be dropped. N2 is the
  first step.
- Draw rock and water as connected areas (#122).
- Rework the pet color adjustment in `EtpetsMainView`.

Open points:

- How is the energy state shown when the brightness is dropped (e.g., a border, a small bar, or only in the
  observation view)?
- Raise the minimum cell edge length (today 5, e.g., to 10)? It must fit the maximum grid size (200 x 200) so that the
  grid stays visible; at edge length 10, a square grid of 200 x 200 is 2000 x 2000 pixels.

### 6. Following a pet

- Select a pet and follow it over a long time, with its own statistics such as age, number of children, and consumed
  resources.
- More details for the selected cell in the observation view, beyond N1.
- F7 More metrics: births, resources, average energy, and trait averages; candidates for #58.
- Lineage visualization and analytics; may build on the statistics history (#58).

### 7. Editing

- An edit tool that places an egg from which a new pet hatches. It needs a deliberate design because pets carry IDs,
  traits, energy, age, cooldown, and parent metadata.
- The edit tool that removes pets and eggs is N4.

Open points:

- Which traits does a pet from a placed egg get: random, or a personality chosen before placing?

## Maybe in the future

Not planned; possibly never.

- Multiple species.
- Optional sex or gender model.
- Separate health model in addition to energy.
- Advanced sensing beyond sight: scent and probabilistic vision.
- Mendelian-like genetics model.
- Dynamic ecosystem balancing.
- Event system with disasters; may build on per-step events (#58, idea 6).

## Discarded

- Day and night, and seasons: too complicated for too little added value.
- Social behavior (avoidance, grouping, and territory).
- Population soft or hard cap: rejected; food and free egg cells limit the population.
- Per-trait mutation rates for resource regeneration: the intent is no longer known.

## Covered elsewhere

- Replay and time travel: statistics history (#58) and edit history (#59).
- Saving and loading configurations: configuration files (#43, #44); presets: #124.
- Simulation docs: #102.

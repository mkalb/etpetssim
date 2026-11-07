# ET Pets - Roadmap

**Status:** planned (mostly after the 1.0 release; the scope for 1.0 is decided in #70)

**Related issues:** #70 (ET Pets review for 1.0), #124 (configuration presets), #123 (polygon shapes for entities),
#122 (connected-area display mode), #58 (statistics history and charts)

## Goal

ET Pets stands out from the more mathematical simulations as a pet simulation: pets have visible individuality,
inherited traits influence their behavior, and the user can follow single pets over a long time. The topics below are
ordered by their dependencies. Turn a topic into an issue when it is planned.

## Current state

- Rules: `EtpetsConfig` has only `neighborhoodMode` as a rule, and it is fixed. All behavior values (e.g., fertility
  age 400, aging effects from age 4000, score weights) are constants in `EtpetsBalance`.
- Perception: a pet only sees two rings around its cell (`CellNeighborhoods.cellsByRadiusRings(..)` in
  `EtpetsAgentLogic`); ring 2 only adds a look-ahead bonus to the cells of ring 1. There is no path planning and no
  line of sight.
- Terrain: rock and water are both impassable; resources (plants and insects) exist only on ground.
- Display: pets are circles whose brightness follows their energy; the minimum cell edge length is 5, the maximum grid
  size is 200 x 200.
- Genetics: one mutation rate applies to all traits; `PetTraits.genomeQualityScore()` rewards disadvantageous traits
  (known finding in #70).

## 1. Rules and population dynamics

- Make the rules configurable like in the other simulations: move selected values from `EtpetsBalance` into
  `EtpetsConfig`.
- Lifespan: pets with personality may live longer. A longer life means fewer generations, so reproduction must be
  slowed down accordingly.
- Presets that set up a complete experience, built on #124: e.g., "Evolution" (fast-changing populations, frequent
  inheritance, small cells) and "Pets" (few long-living pets, rare inheritance, large display).
- Population soft or hard cap.

Open points:

- Which `EtpetsBalance` values become configurable, and which stay fixed?
- How are lifespan and reproduction balanced in both presets?

## 2. Individuality and inheritance

- Inherited traits have a real influence on behavior first; making them visible comes second.
- Fix the genome quality score (#70) before building on it.
- Per-trait mutation rates; reproduction history and lineage tracking.
- Optional names for pets.
- Make inheritance visible in the observation view and the statistics (e.g., trait averages over time, building on
  #58).

Open points:

- Which traits exist, and how does each one change the behavior measurably?
- Are names generated, inherited, or both?

## 3. Perception and movement

- Revise sight range and path finding: pets see further and plan a path to a target instead of choosing only among
  neighboring cells.
- Line of sight: rock blocks the sight, pets can see across water.
- Optional memory of places with resources.

Open points:

- How far do pets see, and how is line of sight computed for triangle, square, and hexagon cells?
- Which path finding fits the step budget for large grids (e.g., breadth-first search within the sight range)?
- Is memory worth it? It overlaps with the trails and makes balancing harder.

## 4. Terrain and resources

- Make the difference between plants and insects clearer and visible (e.g., in regeneration, energy, or movement);
  test the actual effect on the population first.
- Distinguish rock and water beyond the line of sight (see topic 3).
- Water as a source: fish (or water plants) as a third resource that pets hunt from the shore, water as a drinking
  source, or both.
- Terrain compatibility per resource type.
- Per-trait mutation rates for resource regeneration.
- Better terrain generation: clustered obstacles and organic patterns instead of uniform random placement.

Open points:

- Fish, drinking, or both? Hunger and thirst together may become too complicated.
- Resources exist only on ground today; resources on or next to water also affect the edit action "Set Resource".

## 5. Display

- Draw pets more expressively than a circle, also if drawing gets somewhat slower; build on the polygon shapes of #123.
- The appearance of a pet follows its personality or traits; the brightness by energy may be dropped.
- Draw rock and water as connected areas (#122).
- Rework the pet color adjustment in `EtpetsMainView`.

Open points:

- How is the energy state shown when the brightness is dropped (e.g., a border, a small bar, or only in the
  observation view)?
- Raise the minimum cell edge length (today 5, e.g., to 10)? It must fit the maximum grid size (200 x 200) so that the
  grid stays visible; at edge length 10, a square grid of 200 x 200 is 2000 x 2000 pixels.

## 6. Following a pet

- Select a pet and follow it over a long time, with its own statistics such as age, number of children, and consumed
  resources.
- More details for the selected cell in the observation view (see the TODO in `EtpetsObservationView`).
- Lineage visualization and analytics; may build on the statistics history (#58).

## 7. Editing

- An edit tool that places an egg from which a new pet hatches.
- An edit tool that removes pets and eggs. Both need a deliberate design because pets carry IDs, traits, energy, age,
  cooldown, and parent metadata.

Open points:

- Which traits does a pet from a placed egg get: random, or a personality chosen before placing?

## Further ideas (not planned)

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

## Covered elsewhere

- Replay and time travel: statistics history (#58) and edit history (#59).
- Saving and loading configurations: configuration files (#43, #44); presets: #124.

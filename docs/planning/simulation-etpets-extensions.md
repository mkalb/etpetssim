# ET Pets - Roadmap

**Status:** idea

**Related issues:** #24 (scope of ET Pets for 1.0)

Ideas for ET Pets beyond the current version. Turn an idea into an issue when it is planned.

## Terrain

- Better terrain generation: clustered obstacles, biomes, and organic patterns instead of uniform random placement.
- Terrain compatibility per resource type (today, resources exist only on ground).
- Line-of-sight blocking by rock.

## Resources

- Water-based resources such as fish or water plants (today, water is impassable terrain only).
- Per-trait mutation rates for resource regeneration.

## Pets

- Multiple species.
- Optional sex or gender model.
- Separate health model in addition to energy.
- Social behavior: avoidance, grouping, and territory.
- Advanced sensing: memory, scent, and probabilistic vision.
- Population soft or hard cap.

## Genetics

- Per-trait mutation rates (today, one rate applies to all traits).
- Mendelian-like genetics model.
- Reproduction history and lineage tracking.
- Dynamic ecosystem balancing.

## UI

- More details for the selected cell in the observation view (see the TODO in `EtpetsObservationView`).
- Rework the pet color adjustment in `EtpetsMainView`.
- User actions to add and remove pets and pet eggs. They need a deliberate design because pets carry IDs, traits,
  energy, age, cooldown, and parent metadata.
- Lineage visualization and analytics; may build on the statistics history (#58).

## Events

- Event system with disasters and seasonal shifts; may build on per-step events (#58, idea 6).

## Covered elsewhere

- Replay and time travel: statistics history (#58) and edit history (#59).
- Scenario presets and configuration profiles: configuration files (#43, #44).

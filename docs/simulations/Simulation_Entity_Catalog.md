# Simulation Entity Inventory

This document lists the Java types in each simulation's `.../model/entity` package.
It serves as current project documentation and as a naming guide for future simulations.
Update it whenever entity-package types are added, removed, renamed, or changed structurally.

## Scope

- Included: Java types in simulation-specific `.../model/entity` packages
- Excluded: `STARTSCREEN` because it does not have a simulation `.../model/entity` package

## References

- Simulation types: `app/src/main/java/de/mkalb/etpetssim/SimulationType.java`
- Implemented simulations: `README.md`, section `Implemented Simulations`

## Table of Contents

- [Scope](#scope)
- [References](#references)
- [Naming Rules](#naming-rules)
- [Maintenance Checklist](#maintenance-checklist)
- [Ordering Conventions](#ordering-conventions)
- [Template for New Simulations](#template-for-new-simulations)
- [ET_PETS](#et_pets)
- [WATOR](#wator)
- [CONWAYS_LIFE](#conways_life)
- [LANGTONS_ANT](#langtons_ant)
- [FOREST_FIRE](#forest_fire)
- [SUGARSCAPE](#sugarscape)
- [SNAKE](#snake)
- [REBOUNDING_ENTITIES](#rebounding_entities)
- [SIMULATION_LAB](#simulation_lab)

## Naming Rules

Apply these rules in order:

1. Use one simulation-specific root entity contract per simulation.
2. For simple simulations with a single entity enum, use that enum itself as the primary entity type.
3. Use concise, role-focused names for additional entity types inside `.../model/entity`.

Names in `.../model/entity` packages are local to each simulation. They do not need to be globally unique across the
project.

- Use one simulation-specific root entity contract per simulation. A common name is `<SimulationName>Entity`
  (for example `EtpetsEntity`, `WatorEntity`, `LangtonEntity`, `SugarEntity`, `SnakeEntity`, `ReboundingEntity`).
- Keep the root entity contract easy to find. Use it as the main entry point for entity-related logic.
- For simple simulations with a single entity enum, prefer a direct enum name such as `ConwayEntity`, `ForestEntity`,
  or `LabEntity`.
- Prefer role-focused names inside `.../model/entity` packages instead of repeating the simulation prefix
  (for example `Fish`, `Shark`, `Ant`, `Pet`, `Rebounder`).
- Keep domain terms when they are already clear and specific (`Fish`, `Shark`, `Ant`, `Pet`, `Rebounder`,
  `SnakeHead`).
- Use the standard placeholder names `NoAgent` and `NoResource` when placeholder entities are needed.
- Use the suffix `Base` for abstract base classes (for example `ResourceBase`, `CreatureBase`).
- Use the standard name `TerrainConstant` for constant terrain enums.
- For descriptor enums that implement `SpecBackedGridEntityDescriptorProvider`, use the standard name
  `EntityDescriptors`.
  Exception: simple simulations whose single enum already serves as the entity type and implements
  `ConstantGridEntityDescriptorProvider` do not need a separate descriptor enum.
- For value object records, use concise domain-specific names that keep the needed context
  (for example `PetGenome`, `PetTraits`).
- For factories, prefer domain-specific names over overly generic names such as `EntityFactory`
  (for example `CreatureFactory`).
- For each new simulation, document all entity-package types with file name, FQCN, role, type kind,
  `extends`/`implements` relations, and grid interface information.

## Maintenance Checklist

Use this checklist when you add or update a simulation:

- Add, remove, or rename rows to match the current contents of the simulation's `.../model/entity` package.
- Update `Java File`, `FQCN`, `Kind`, `Extends`, `Implements`, and `Grid Interface` together.
- Keep `Entity Role` terminology consistent with the existing tables.
- Keep root entity contracts simulation-specific and easy to identify.
- Apply the naming rules above to placeholders, abstract bases, descriptors, records, and factories.
- Prefer documenting the current implementation over planned refactorings.

## Ordering Conventions

- Keep simulation sections in the same order as `README.md` under `Implemented Simulations`.
- Within each simulation table, group related types together instead of sorting strictly alphabetically.
- Use this row order when it fits:
    1. root entity contract
    2. role-specific contracts
    3. placeholders
    4. descriptor enums
    5. abstract base classes and factories
    6. concrete classes, enums, and records
- If a different order reads better for a specific simulation, keep it stable and consistent within that table.

## Template for New Simulations

Use this template when you add a new simulation section:

```markdown
## NEW_SIMULATION

| Simulation Type | Java File            | FQCN                                                                | Entity Role          | Kind      | Extends      | Implements | Grid Interface |
|-----------------|----------------------|---------------------------------------------------------------------|----------------------|-----------|--------------|------------|----------------|
| NEW_SIMULATION  | `ExampleEntity.java` | `de.mkalb.etpetssim.simulations.example.model.entity.ExampleEntity` | base entity contract | interface | `GridEntity` | -          | `GridEntity`   |

### NEW_SIMULATION - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key             | Emoji | Fill Color                 | Border Color              | Default In Layer(s) |
|---------------|-------------------|---------------------------|-------|----------------------------|---------------------------|---------------------|
| `example`     | Example Cell      | `example.entity.long`     | -     | `#FFFFFF` / `Color.WHITE`  | -                         | `grid`              |
| `example.alt` | Example Alt Cell  | `example.entity.alt.long` | `🧪`  | `#FF0000` / `Color.RED`    | `#000000` / `Color.BLACK` | -                   |
```

Notes:

- Use one row per descriptor that contributes rendering metadata.
- Resolve `Long Name (en_US)` from `messages_en_US.properties` via `Long Name Key`.
- Document colors as RGB hex in the format `#RRGGBB`; when a JavaFX `Color.*` constant is used in code, append it as
  `#RRGGBB` / `Color.NAME`; use `-` when not defined.
- Use `Default In Layer(s)` to document model defaults such as `grid`, `ground`, `agent`, `resource`, or `terrain`; use
  `-` when a descriptor is not a layer default.

## ET_PETS

| Simulation Type | Java File                | FQCN                                                                   | Entity Role          | Kind           | Extends        | Implements                               | Grid Interface       |
|-----------------|--------------------------|------------------------------------------------------------------------|----------------------|----------------|----------------|------------------------------------------|----------------------|
| ET_PETS         | `EtpetsEntity.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity`      | base entity contract | interface      | `GridEntity`   | -                                        | `GridEntity`         |
| ET_PETS         | `AgentEntity.java`       | `de.mkalb.etpetssim.simulations.etpets.model.entity.AgentEntity`       | agent contract       | interface      | `EtpetsEntity` | -                                        | `GridEntity`         |
| ET_PETS         | `ResourceEntity.java`    | `de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceEntity`    | resource contract    | interface      | `EtpetsEntity` | -                                        | `GridEntity`         |
| ET_PETS         | `TerrainEntity.java`     | `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity`     | terrain contract     | interface      | `EtpetsEntity` | -                                        | `GridEntity`         |
| ET_PETS         | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent`           | agent placeholder    | enum           | -              | `AgentEntity`, `ConstantGridEntity`      | `ConstantGridEntity` |
| ET_PETS         | `NoResource.java`        | `de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource`        | resource placeholder | enum           | -              | `ResourceEntity`, `ConstantGridEntity`   | `ConstantGridEntity` |
| ET_PETS         | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EntityDescriptors` | descriptor           | enum           | -              | `SpecBackedGridEntityDescriptorProvider` | `none`               |
| ET_PETS         | `ResourceBase.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceBase`      | abstract resource    | abstract class | -              | `ResourceEntity`                         | `GridEntity`         |
| ET_PETS         | `Pet.java`               | `de.mkalb.etpetssim.simulations.etpets.model.entity.Pet`               | agent                | class          | -              | `AgentEntity`                            | `GridEntity`         |
| ET_PETS         | `PetEgg.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetEgg`            | agent                | class          | -              | `AgentEntity`                            | `GridEntity`         |
| ET_PETS         | `PetGenome.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetGenome`         | value object         | record         | -              | -                                        | `none`               |
| ET_PETS         | `PetTraits.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetTraits`         | value object         | record         | -              | -                                        | `none`               |
| ET_PETS         | `Insect.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.Insect`            | resource             | class          | `ResourceBase` | -                                        | `GridEntity`         |
| ET_PETS         | `Plant.java`             | `de.mkalb.etpetssim.simulations.etpets.model.entity.Plant`             | resource             | class          | `ResourceBase` | -                                        | `GridEntity`         |
| ET_PETS         | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant`   | terrain constant     | enum           | -              | `TerrainEntity`, `ConstantGridEntity`    | `ConstantGridEntity` |
| ET_PETS         | `Trail.java`             | `de.mkalb.etpetssim.simulations.etpets.model.entity.Trail`             | terrain              | class          | -              | `TerrainEntity`                          | `GridEntity`         |

### ET_PETS - Entity Display Catalog

Note: ET_PETS is still under active development; this table is intentionally left empty and will be filled once
descriptor colors and emoji keys are finalized.

| Descriptor ID | Long Name (en_US) | Long Name Key | Emoji | Fill Color | Border Color | Default In Layer(s) |
|---------------|-------------------|---------------|-------|------------|--------------|---------------------|

## WATOR

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind           | Extends        | Implements                                | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|----------------|----------------|-------------------------------------------|----------------------|
| WATOR           | `WatorEntity.java`       | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity`       | base entity contract | interface      | `GridEntity`   | -                                         | `GridEntity`         |
| WATOR           | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.EntityDescriptors` | descriptor           | enum           | -              | `SpecBackedGridEntityDescriptorProvider`  | `none`               |
| WATOR           | `CreatureBase.java`      | `de.mkalb.etpetssim.simulations.wator.model.entity.CreatureBase`      | abstract creature    | abstract class | -              | `WatorEntity`, `Comparable<CreatureBase>` | `GridEntity`         |
| WATOR           | `CreatureFactory.java`   | `de.mkalb.etpetssim.simulations.wator.model.entity.CreatureFactory`   | factory              | class          | -              | -                                         | `none`               |
| WATOR           | `Fish.java`              | `de.mkalb.etpetssim.simulations.wator.model.entity.Fish`              | agent                | class          | `CreatureBase` | -                                         | `GridEntity`         |
| WATOR           | `Shark.java`             | `de.mkalb.etpetssim.simulations.wator.model.entity.Shark`             | agent                | class          | `CreatureBase` | -                                         | `GridEntity`         |
| WATOR           | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.wator.model.entity.TerrainConstant`   | terrain constant     | enum           | -              | `WatorEntity`, `ConstantGridEntity`       | `ConstantGridEntity` |

### WATOR - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key             | Emoji | Fill Color          | Border Color | Default In Layer(s) |
|---------------|-------------------|---------------------------|-------|---------------------|--------------|---------------------|
| `water`       | Water Cell        | `wator.entity.water.long` | -     | `#141964`           | -            | `grid`              |
| `fish`        | Fish Cell         | `wator.entity.fish.long`  | `🐟`  | `#00A064` [^wfish]  | `#141964`    | -                   |
| `shark`       | Shark Cell        | `wator.entity.shark.long` | `🦈`  | `#737878` [^wshark] | `#141964`    | -                   |

[^wfish]: Base color; rendered darker with increasing age — 10 brightness steps, factor range [0.0, −0.5].
[^wshark]: Base color; rendered brighter with increasing energy — 6 brightness steps, factor range [0.0, +0.7].

## CONWAYS_LIFE

| Simulation Type | Java File           | FQCN                                                              | Entity Role | Kind | Extends | Implements                             | Grid Interface       |
|-----------------|---------------------|-------------------------------------------------------------------|-------------|------|---------|----------------------------------------|----------------------|
| CONWAYS_LIFE    | `ConwayEntity.java` | `de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

### CONWAYS_LIFE - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key              | Emoji | Fill Color                      | Border Color                  | Default In Layer(s) |
|---------------|-------------------|----------------------------|-------|---------------------------------|-------------------------------|---------------------|
| `dead`        | Dead Cell         | `conway.entity.dead.long`  | -     | `#FFFFE0` / `Color.LIGHTYELLOW` | -                             | `grid`              |
| `alive`       | Living Cell       | `conway.entity.alive.long` | -     | `#8B0000` / `Color.DARKRED`     | `#CD5C5C` / `Color.INDIANRED` | -                   |

## LANGTONS_ANT

| Simulation Type | Java File                | FQCN                                                                    | Entity Role          | Kind      | Extends         | Implements                                              | Grid Interface       |
|-----------------|--------------------------|-------------------------------------------------------------------------|----------------------|-----------|-----------------|---------------------------------------------------------|----------------------|
| LANGTONS_ANT    | `LangtonEntity.java`     | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity`     | base entity contract | interface | `GridEntity`    | -                                                       | `GridEntity`         |
| LANGTONS_ANT    | `AntEntity.java`         | `de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity`         | agent contract       | interface | `LangtonEntity` | -                                                       | `GridEntity`         |
| LANGTONS_ANT    | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.langton.model.entity.NoAgent`           | agent placeholder    | enum      | -               | `AntEntity`, `ConstantGridEntity`                       | `ConstantGridEntity` |
| LANGTONS_ANT    | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.EntityDescriptors` | descriptor           | enum      | -               | `SpecBackedGridEntityDescriptorProvider`                | `none`               |
| LANGTONS_ANT    | `Ant.java`               | `de.mkalb.etpetssim.simulations.langton.model.entity.Ant`               | agent                | class     | -               | `AntEntity`                                             | `GridEntity`         |
| LANGTONS_ANT    | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant`   | terrain constant     | enum      | -               | `LangtonEntity`, `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

### LANGTONS_ANT - Entity Display Catalog

| Descriptor ID     | Long Name (en_US)     | Long Name Key                          | Emoji        | Fill Color                    | Border Color              | Default In Layer(s) |
|-------------------|-----------------------|----------------------------------------|--------------|-------------------------------|---------------------------|---------------------|
| `ant`             | Langton's Ant         | `langton.entity.ant.long`              | - [^lantdir] | `#FF0000` / `Color.RED`       | `#000000` / `Color.BLACK` | -                   |
| `groundunvisited` | Unvisited Ground Cell | `langton.entity.ground.unvisited.long` | -            | `#FFFFFF` / `Color.WHITE`     | -                         | `ground`            |
| `ground0`         | Ground Cell 0         | `langton.entity.ground.0.long`         | -            | `#D3D3D3` / `Color.LIGHTGRAY` | -                         | -                   |
| `ground1`         | Ground Cell 1         | `langton.entity.ground.1.long`         | -            | `#000000` / `Color.BLACK`     | -                         | -                   |
| `ground2`         | Ground Cell 2         | `langton.entity.ground.2.long`         | -            | `#FFA500` / `Color.ORANGE`    | -                         | -                   |
| `ground3`         | Ground Cell 3         | `langton.entity.ground.3.long`         | -            | `#FFFF00` / `Color.YELLOW`    | -                         | -                   |
| `ground4`         | Ground Cell 4         | `langton.entity.ground.4.long`         | -            | `#008000` / `Color.GREEN`     | -                         | -                   |
| `ground5`         | Ground Cell 5         | `langton.entity.ground.5.long`         | -            | `#006400` / `Color.DARKGREEN` | -                         | -                   |
| `ground6`         | Ground Cell 6         | `langton.entity.ground.6.long`         | -            | `#0000FF` / `Color.BLUE`      | -                         | -                   |
| `ground7`         | Ground Cell 7         | `langton.entity.ground.7.long`         | -            | `#00008B` / `Color.DARKBLUE`  | -                         | -                   |
| `ground8`         | Ground Cell 8         | `langton.entity.ground.8.long`         | -            | `#800080` / `Color.PURPLE`    | -                         | -                   |
| `ground9`         | Ground Cell 9         | `langton.entity.ground.9.long`         | -            | `#EE82EE` / `Color.VIOLET`    | -                         | -                   |
| `ground10`        | Ground Cell 10        | `langton.entity.ground.10.long`        | -            | `#FFC0CB` / `Color.PINK`      | -                         | -                   |
| `ground11`        | Ground Cell 11        | `langton.entity.ground.11.long`        | -            | `#A52A2A` / `Color.BROWN`     | -                         | -                   |
| `ground12`        | Ground Cell 12        | `langton.entity.ground.12.long`        | -            | `#00FFFF` / `Color.CYAN`      | -                         | -                   |
| `ground13`        | Ground Cell 13        | `langton.entity.ground.13.long`        | -            | `#FF00FF` / `Color.MAGENTA`   | -                         | -                   |
| `ground14`        | Ground Cell 14        | `langton.entity.ground.14.long`        | -            | `#FF1493` / `Color.DEEPPINK`  | -                         | -                   |
| `ground15`        | Ground Cell 15        | `langton.entity.ground.15.long`        | -            | `#FFD700` / `Color.GOLD`      | -                         | -                   |

[^lantdir]: No emoji key defined. The view renders `ant.direction().arrow()` (`CompassDirection`) as centered text in
the
border color — single Unicode arrows for cardinal/intercardinal directions (↑ ↗ → ↘ ↓ ↙ ← ↖), two-character combinations
for secondary intercardinal directions (e.g., ↑↗, →↗). Rendered only when the emoji font is loaded and
`cellDimension.innerRadius() > 5.0`.

## FOREST_FIRE

| Simulation Type | Java File           | FQCN                                                              | Entity Role | Kind | Extends | Implements                             | Grid Interface       |
|-----------------|---------------------|-------------------------------------------------------------------|-------------|------|---------|----------------------------------------|----------------------|
| FOREST_FIRE     | `ForestEntity.java` | `de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

### FOREST_FIRE - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key                | Emoji | Fill Color                      | Border Color | Default In Layer(s) |
|---------------|-------------------|------------------------------|-------|---------------------------------|--------------|---------------------|
| `empty`       | Empty Cell        | `forest.entity.empty.long`   | -     | `#2D1E0F`                       | -            | `grid`              |
| `tree`        | Tree Cell         | `forest.entity.tree.long`    | -     | `#228B22` / `Color.FORESTGREEN` | -            | -                   |
| `burning`     | Burning Tree      | `forest.entity.burning.long` | -     | `#FF4500` / `Color.ORANGERED`   | -            | -                   |

## SUGARSCAPE

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind      | Extends       | Implements                               | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|-----------|---------------|------------------------------------------|----------------------|
| SUGARSCAPE      | `SugarEntity.java`       | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity`       | base entity contract | interface | `GridEntity`  | -                                        | `GridEntity`         |
| SUGARSCAPE      | `AgentEntity.java`       | `de.mkalb.etpetssim.simulations.sugar.model.entity.AgentEntity`       | agent contract       | interface | `SugarEntity` | -                                        | `GridEntity`         |
| SUGARSCAPE      | `ResourceEntity.java`    | `de.mkalb.etpetssim.simulations.sugar.model.entity.ResourceEntity`    | resource contract    | interface | `SugarEntity` | -                                        | `GridEntity`         |
| SUGARSCAPE      | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.sugar.model.entity.NoAgent`           | agent placeholder    | enum      | -             | `AgentEntity`, `ConstantGridEntity`      | `ConstantGridEntity` |
| SUGARSCAPE      | `NoResource.java`        | `de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource`        | resource placeholder | enum      | -             | `ResourceEntity`, `ConstantGridEntity`   | `ConstantGridEntity` |
| SUGARSCAPE      | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.EntityDescriptors` | descriptor           | enum      | -             | `SpecBackedGridEntityDescriptorProvider` | `none`               |
| SUGARSCAPE      | `Agent.java`             | `de.mkalb.etpetssim.simulations.sugar.model.entity.Agent`             | agent                | class     | -             | `AgentEntity`                            | `GridEntity`         |
| SUGARSCAPE      | `Sugar.java`             | `de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar`             | resource             | class     | -             | `ResourceEntity`                         | `GridEntity`         |

Note: SUGARSCAPE has no terrain entity contract or terrain entities. The `TERRAIN` descriptor in EntityDescriptors is a
logical descriptor only, not a Java type.

### SUGARSCAPE - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key               | Emoji            | Fill Color              | Border Color | Default In Layer(s) |
|---------------|-------------------|-----------------------------|------------------|-------------------------|--------------|---------------------|
| `terrain`     | Terrain Cell      | `sugar.entity.terrain.long` | -                | `#453525` [^sugterrain] | -            | -                   |
| `sugar`       | Sugar Resource    | `sugar.entity.sugar.long`   | `🍬` [^sugemoji] | `#D7BE13` [^sugsugar]   | -            | -                   |
| `agent`       | Agent Cell        | `sugar.entity.agent.long`   | `👤` [^sugemoji] | `#1B5ABA` [^sugagent]   | -            | -                   |

[^sugterrain]: Logical descriptor only; no Java entity with this ID exists. Its color is used as canvas background
fill (see also the note above this table).
[^sugemoji]: Emoji key is defined in `EntityDescriptors` but `SugarMainView` does not implement emoji rendering; emojis
are not displayed.
[^sugsugar]: Base color; rendered brighter with increasing current sugar amount — config-dependent number of steps,
factor range [0.0, +0.3].
[^sugagent]: Base color; rendered brighter with increasing energy — 7 brightness steps, factor range [0.0, +0.6]. Newly
spawned agents additionally receive a white border for one step.

## SNAKE

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind      | Extends      | Implements                               | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|-----------|--------------|------------------------------------------|----------------------|
| SNAKE           | `SnakeEntity.java`       | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity`       | base entity contract | interface | `GridEntity` | -                                        | `GridEntity`         |
| SNAKE           | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.EntityDescriptors` | descriptor           | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | `none`               |
| SNAKE           | `SnakeHead.java`         | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead`         | agent                | class     | -            | `SnakeEntity`                            | `GridEntity`         |
| SNAKE           | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.snake.model.entity.TerrainConstant`   | terrain constant     | enum      | -            | `SnakeEntity`, `ConstantGridEntity`      | `ConstantGridEntity` |

### SNAKE - Entity Display Catalog

| Descriptor ID   | Long Name (en_US) | Long Name Key                    | Emoji | Fill Color           | Border Color         | Default In Layer(s) |
|-----------------|-------------------|----------------------------------|-------|----------------------|----------------------|---------------------|
| `ground`        | Ground Cell       | `snake.entity.ground.long`       | -     | `#0B1220`            | -                    | `grid`              |
| `wall`          | Wall Cell         | `snake.entity.wall.long`         | -     | `#4A4A4A`            | `#7A7A7A`            | -                   |
| `growth_food`   | Growth Food       | `snake.entity.growthfood.long`   | -     | `#FFCC00`            | `#CC9900`            | -                   |
| `snake_segment` | Snake Segment     | `snake.entity.snakesegment.long` | -     | `#4CAF50` [^snkseg]  | `#2E7D32` [^snkseg]  | -                   |
| `snake_head`    | Snake Head        | `snake.entity.snakehead.long`    | -     | `#00BCD4` [^snkhead] | `#008BA3` [^snkhead] | -                   |

[^snkseg]: alive color defined in `EntityDescriptors`. alive+selected computed in `SnakeMainView` via
`aliveColor.interpolate(Color.WHITE, 0.3)` (fill `#82C784`, border `#6DA470`). dead colors computed in
`SnakeMainView` via `Color.hsb(22.0, 0.45, aliveColor.getBrightness() * factor)` with factor `0.75` for fill and
`0.55` for border (fill `#835E48`, border `#453126`).
[^snkhead]: alive color defined in `EntityDescriptors`. alive+selected computed in `SnakeMainView` via
`aliveColor.interpolate(Color.WHITE, 0.3)` (fill `#4CD0E1`, border `#4CAEBF`). dead colors computed in
`SnakeMainView` via `Color.hsb(4.0, 0.45, aliveColor.getBrightness() * factor)` with factor `0.75` for fill and
`0.55` for border (fill `#9F5C57`, border `#5A3431`).

## REBOUNDING_ENTITIES

| Simulation Type     | Java File                | FQCN                                                                       | Entity Role          | Kind      | Extends      | Implements                               | Grid Interface       |
|---------------------|--------------------------|----------------------------------------------------------------------------|----------------------|-----------|--------------|------------------------------------------|----------------------|
| REBOUNDING_ENTITIES | `ReboundingEntity.java`  | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity`  | base entity contract | interface | `GridEntity` | -                                        | `GridEntity`         |
| REBOUNDING_ENTITIES | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.EntityDescriptors` | descriptor           | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | `none`               |
| REBOUNDING_ENTITIES | `Rebounder.java`         | `de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder`         | agent                | class     | -            | `ReboundingEntity`                       | `GridEntity`         |
| REBOUNDING_ENTITIES | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant`   | terrain constant     | enum      | -            | `ReboundingEntity`, `ConstantGridEntity` | `ConstantGridEntity` |

### REBOUNDING_ENTITIES - Entity Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key                      | Emoji | Fill Color | Border Color | Default In Layer(s) |
|---------------|-------------------|------------------------------------|-------|------------|--------------|---------------------|
| `ground`      | Ground Cell       | `rebounding.entity.ground.long`    | -     | `#0B1220`  | -            | `grid`              |
| `wall`        | Wall Cell         | `rebounding.entity.wall.long`      | -     | `#4A4A4A`  | `#7A7A7A`    | -                   |
| `rebounder`   | Moving Entity     | `rebounding.entity.rebounder.long` | -     | `#FFCC00`  | `#CC9900`    | -                   |

## SIMULATION_LAB

| Simulation Type | Java File        | FQCN                                                        | Entity Role | Kind | Extends | Implements                             | Grid Interface       |
|-----------------|------------------|-------------------------------------------------------------|-------------|------|---------|----------------------------------------|----------------------|
| SIMULATION_LAB  | `LabEntity.java` | `de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

### SIMULATION_LAB - Entity Display Catalog

Note: `SIMULATION_LAB` is a development sandbox. No colors are defined in `LabEntity`; rendering colors are determined
entirely by `LabMainView` independent of entity type.

| Descriptor ID | Long Name (en_US) | Long Name Key                 | Emoji | Fill Color   | Border Color | Default In Layer(s) |
|---------------|-------------------|-------------------------------|-------|--------------|--------------|---------------------|
| `normal`      | Normal Cell       | `lab.entity.normal.long`      | -     | - [^labview] | -            | `grid`              |
| `highlighted` | Highlighted Cell  | `lab.entity.highlighted.long` | -     | - [^labhigh] | -            | -                   |

[^labview]: No color defined in `LabEntity`. The base canvas renders all cells with coordinate-based colors independent
of entity type (`x % 2`, `y % 2`) — color mode: `#87CEFA` / `Color.LIGHTSKYBLUE`, `#B0C4DE` / `Color.LIGHTSTEELBLUE`,
`#98FB98` / `Color.PALEGREEN`, `#66CDAA` / `Color.MEDIUMAQUAMARINE`; grayscale mode: `#FFFFFF` / `Color.WHITE`,
`#D3D3D3` / `Color.LIGHTGRAY`, `#A9A9A9` / `Color.DARKGRAY`, `#808080` / `Color.GRAY`.
[^labhigh]: Same coordinate-based base color as `normal`. Additionally overlaid with translucent red
(`Color.RED` at α 0.5) via `drawModel()`.


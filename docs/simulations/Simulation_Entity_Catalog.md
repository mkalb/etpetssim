# Simulation Entity Catalog

This document lists all Java types in each simulation's `.../model/entity` package and their visual rendering
properties.

`STARTSCREEN` is excluded because it has no entity package.

Simulation types are defined in `app/src/main/java/de/mkalb/etpetssim/SimulationType.java`.

## ET_PETS - ET Pets

**Package:** `de.mkalb.etpetssim.simulations.etpets.model.entity`
**Cell shapes:** Hexagon

| Java File                | Kind           | Extends        | Implements                               | Entity Role          |
|--------------------------|----------------|----------------|------------------------------------------|----------------------|
| `EtpetsEntity.java`      | interface      | `GridEntity`   | -                                        | base entity contract |
| `AgentEntity.java`       | interface      | `EtpetsEntity` | -                                        | agent contract       |
| `ResourceEntity.java`    | interface      | `EtpetsEntity` | -                                        | resource contract    |
| `TerrainEntity.java`     | interface      | `EtpetsEntity` | -                                        | terrain contract     |
| `NoAgent.java`           | enum           | -              | `AgentEntity`, `ConstantGridEntity`      | agent placeholder    |
| `NoResource.java`        | enum           | -              | `ResourceEntity`, `ConstantGridEntity`   | resource placeholder |
| `EntityDescriptors.java` | enum           | -              | `SpecBackedGridEntityDescriptorProvider` | descriptor           |
| `ResourceBase.java`      | abstract class | -              | `ResourceEntity`                         | abstract resource    |
| `Pet.java`               | class          | -              | `AgentEntity`                            | agent                |
| `PetEgg.java`            | class          | -              | `AgentEntity`                            | agent                |
| `PetGenome.java`         | record         | -              | -                                        | value object         |
| `PetTraits.java`         | record         | -              | -                                        | value object         |
| `Insect.java`            | class          | `ResourceBase` | -                                        | resource             |
| `Plant.java`             | class          | `ResourceBase` | -                                        | resource             |
| `TerrainConstant.java`   | enum           | -              | `TerrainEntity`, `ConstantGridEntity`    | terrain constant     |
| `Trail.java`             | class          | -              | `TerrainEntity`                          | terrain              |

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key                        | Emoji | Fill Color            | Border Color | Default In Layer(s) |
|---------------|-------------------|--------------------------------------|-------|-----------------------|--------------|---------------------|
| `ground`      | Ground Terrain    | `etpets.entity.terrain.ground.long`  | -     | `#2E2E2E`             | -            | `terrain`           |
| `rock`        | Rock Terrain      | `etpets.entity.terrain.rock.long`    | -     | `#737373`             | -            | -                   |
| `water`       | Water Terrain     | `etpets.entity.terrain.water.long`   | -     | `#1F4FA0`             | -            | -                   |
| `trail`       | Trail Terrain     | `etpets.entity.terrain.trail.long`   | -     | `#503812` [^ettrail]  | -            | -                   |
| `plant`       | Plant Resource    | `etpets.entity.resource.plant.long`  | -     | `#3A8020` [^etplant]  | `#3A8020`    | -                   |
| `insect`      | Insect Resource   | `etpets.entity.resource.insect.long` | -     | `#B8A000` [^etinsect] | `#B8A000`    | -                   |
| `pet`         | ET Pet            | `etpets.entity.agent.pet.long`       | -     | `#7820CC` [^etpet]    | -            | -                   |
| `pet_egg`     | ET Pet Egg        | `etpets.entity.agent.petegg.long`    | -     | `#D8B6CC` [^etegg]    | `#4B3A66`    | -                   |

[^ettrail]: Brighter with increasing trail intensity — 5 levels, factor [0.0, +0.50]. Full terrain hexagon replacing
ground.
[^etplant]: Brighter with increasing resource amount — 6 levels, factor [0.0, +0.55]. Border: same color at 50% alpha.
Inner circle on terrain layer.
[^etinsect]: Brighter with increasing resource amount — 7 levels, factor [0.0, +0.40]. Border: same color at 50% alpha.
Inner circle on terrain layer.
[^etpet]: Brighter with increasing energy — 5 levels, factor [0.0, +0.65]. Dead: 20% brightness, no border, 1 step.
Inner circle on terrain layer.
[^etegg]: Brighter with increasing incubation remaining — 5 groups over 20 steps (range [1, 20]), factor [0.0, +0.30].
Inner circle with dark purple border (`#4B3A66`, stroke 1.0) on terrain layer.

## WATOR - Wa-Tor

**Package:** `de.mkalb.etpetssim.simulations.wator.model.entity`
**Cell shapes:** Square (default), Triangle, Hexagon

| Java File                | Kind           | Extends        | Implements                                | Entity Role          |
|--------------------------|----------------|----------------|-------------------------------------------|----------------------|
| `WatorEntity.java`       | interface      | `GridEntity`   | -                                         | base entity contract |
| `EntityDescriptors.java` | enum           | -              | `SpecBackedGridEntityDescriptorProvider`  | descriptor           |
| `CreatureBase.java`      | abstract class | -              | `WatorEntity`, `Comparable<CreatureBase>` | abstract creature    |
| `CreatureFactory.java`   | class          | -              | -                                         | factory              |
| `Fish.java`              | class          | `CreatureBase` | -                                         | agent                |
| `Shark.java`             | class          | `CreatureBase` | -                                         | agent                |
| `TerrainConstant.java`   | enum           | -              | `WatorEntity`, `ConstantGridEntity`       | terrain constant     |

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key             | Emoji | Fill Color          | Border Color | Default In Layer(s) |
|---------------|-------------------|---------------------------|-------|---------------------|--------------|---------------------|
| `water`       | Water Cell        | `wator.entity.water.long` | -     | `#141964`           | -            | `grid`              |
| `fish`        | Fish Cell         | `wator.entity.fish.long`  | `🐟`  | `#00A064` [^wfish]  | `#141964`    | -                   |
| `shark`       | Shark Cell        | `wator.entity.shark.long` | `🦈`  | `#737878` [^wshark] | `#141964`    | -                   |

[^wfish]: Darker with increasing age — 10 steps, factor [0.0, −0.5].
[^wshark]: Brighter with increasing energy — 6 steps, factor [0.0, +0.7].

## CONWAYS_LIFE - Conway's Game of Life

**Package:** `de.mkalb.etpetssim.simulations.conway.model.entity`
**Cell shapes:** Square (default), Triangle, Hexagon

| Java File           | Kind | Extends | Implements                             | Entity Role |
|---------------------|------|---------|----------------------------------------|-------------|
| `ConwayEntity.java` | enum | -       | `ConstantGridEntityDescriptorProvider` | cell state  |

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key              | Emoji | Fill Color                      | Border Color                  | Default In Layer(s) |
|---------------|-------------------|----------------------------|-------|---------------------------------|-------------------------------|---------------------|
| `dead`        | Dead Cell         | `conway.entity.dead.long`  | -     | `#FFFFE0` / `Color.LIGHTYELLOW` | -                             | `grid`              |
| `alive`       | Living Cell       | `conway.entity.alive.long` | -     | `#8B0000` / `Color.DARKRED`     | `#CD5C5C` / `Color.INDIANRED` | -                   |

## LANGTONS_ANT - Langton's Ant

**Package:** `de.mkalb.etpetssim.simulations.langton.model.entity`
**Cell shapes:** Square (default), Triangle, Hexagon

| Java File                | Kind      | Extends         | Implements                                              | Entity Role          |
|--------------------------|-----------|-----------------|---------------------------------------------------------|----------------------|
| `LangtonEntity.java`     | interface | `GridEntity`    | -                                                       | base entity contract |
| `AntEntity.java`         | interface | `LangtonEntity` | -                                                       | agent contract       |
| `NoAgent.java`           | enum      | -               | `AntEntity`, `ConstantGridEntity`                       | agent placeholder    |
| `EntityDescriptors.java` | enum      | -               | `SpecBackedGridEntityDescriptorProvider`                | descriptor           |
| `Ant.java`               | class     | -               | `AntEntity`                                             | agent                |
| `TerrainConstant.java`   | enum      | -               | `LangtonEntity`, `ConstantGridEntityDescriptorProvider` | terrain constant     |

### Display Catalog

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

[^lantdir]: No emoji key. View renders `ant.direction().arrow()` as centered text in border color — Unicode arrows
(↑ ↗ → ↘ ↓ ↙ ← ↖), two-char combos for secondary intercardinals (e.g., ↑↗). Requires emoji font and
`cellDimension.innerRadius() > 5.0`.

## FOREST_FIRE - Forest-fire model

**Package:** `de.mkalb.etpetssim.simulations.forest.model.entity`
**Cell shapes:** Hexagon (default), Triangle, Square

| Java File           | Kind | Extends | Implements                             | Entity Role |
|---------------------|------|---------|----------------------------------------|-------------|
| `ForestEntity.java` | enum | -       | `ConstantGridEntityDescriptorProvider` | cell state  |

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key                | Emoji | Fill Color                    | Border Color               | Default In Layer(s) |
|---------------|-------------------|------------------------------|-------|-------------------------------|----------------------------|---------------------|
| `empty`       | Empty Cell        | `forest.entity.empty.long`   | -     | `#1E140A`                     | -                          | `grid`              |
| `tree`        | Tree Cell         | `forest.entity.tree.long`    | `🌲`  | `#2E8B57` / `Color.SEAGREEN`  | -                          | -                   |
| `burning`     | Burning Tree      | `forest.entity.burning.long` | `🔥`  | `#FF4500` / `Color.ORANGERED` | `#FFA500` / `Color.ORANGE` | -                   |

## SUGARSCAPE - Sugarscape

**Package:** `de.mkalb.etpetssim.simulations.sugar.model.entity`
**Cell shapes:** Square (default), Hexagon

| Java File                | Kind      | Extends       | Implements                               | Entity Role          |
|--------------------------|-----------|---------------|------------------------------------------|----------------------|
| `SugarEntity.java`       | interface | `GridEntity`  | -                                        | base entity contract |
| `AgentEntity.java`       | interface | `SugarEntity` | -                                        | agent contract       |
| `ResourceEntity.java`    | interface | `SugarEntity` | -                                        | resource contract    |
| `NoAgent.java`           | enum      | -             | `AgentEntity`, `ConstantGridEntity`      | agent placeholder    |
| `NoResource.java`        | enum      | -             | `ResourceEntity`, `ConstantGridEntity`   | resource placeholder |
| `EntityDescriptors.java` | enum      | -             | `SpecBackedGridEntityDescriptorProvider` | descriptor           |
| `Agent.java`             | class     | -             | `AgentEntity`                            | agent                |
| `Sugar.java`             | class     | -             | `ResourceEntity`                         | resource             |

Note: SUGARSCAPE has no terrain entity contract or terrain entities. The `TERRAIN` descriptor in EntityDescriptors is a
logical descriptor only, not a Java type.

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key               | Emoji | Fill Color              | Border Color | Default In Layer(s) |
|---------------|-------------------|-----------------------------|-------|-------------------------|--------------|---------------------|
| `terrain`     | Terrain Cell      | `sugar.entity.terrain.long` | -     | `#453525` [^sugterrain] | -            | -                   |
| `sugar`       | Sugar Resource    | `sugar.entity.sugar.long`   | -     | `#D7BE13` [^sugsugar]   | -            | -                   |
| `agent`       | Agent Cell        | `sugar.entity.agent.long`   | -     | `#1B5ABA` [^sugagent]   | -            | -                   |

[^sugterrain]: Logical descriptor only; no Java entity. Color used as canvas background fill.
[^sugsugar]: Brighter with increasing sugar amount — config-dependent steps, factor [0.0, +0.3].
[^sugagent]: Brighter with increasing energy — 7 steps, factor [0.0, +0.6]. Newly spawned: white border for 1 step.

## SNAKE - Snake

**Package:** `de.mkalb.etpetssim.simulations.snake.model.entity`
**Cell shapes:** Hexagon (default), Square

| Java File                | Kind      | Extends      | Implements                               | Entity Role          |
|--------------------------|-----------|--------------|------------------------------------------|----------------------|
| `SnakeEntity.java`       | interface | `GridEntity` | -                                        | base entity contract |
| `EntityDescriptors.java` | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | descriptor           |
| `SnakeHead.java`         | class     | -            | `SnakeEntity`                            | agent                |
| `TerrainConstant.java`   | enum      | -            | `SnakeEntity`, `ConstantGridEntity`      | terrain constant     |

### Display Catalog

| Descriptor ID   | Long Name (en_US) | Long Name Key                    | Emoji | Fill Color           | Border Color         | Default In Layer(s) |
|-----------------|-------------------|----------------------------------|-------|----------------------|----------------------|---------------------|
| `ground`        | Ground Cell       | `snake.entity.ground.long`       | -     | `#0B1220`            | -                    | `grid`              |
| `wall`          | Wall Cell         | `snake.entity.wall.long`         | -     | `#4A4A4A`            | `#7A7A7A`            | -                   |
| `growth_food`   | Growth Food       | `snake.entity.growthfood.long`   | -     | `#FFCC00`            | `#CC9900`            | -                   |
| `snake_segment` | Snake Segment     | `snake.entity.snakesegment.long` | -     | `#4CAF50` [^snkseg]  | `#2E7D32` [^snkseg]  | -                   |
| `snake_head`    | Snake Head        | `snake.entity.snakehead.long`    | -     | `#00BCD4` [^snkhead] | `#008BA3` [^snkhead] | -                   |

[^snkseg]: Alive color from `EntityDescriptors`. Selected: `aliveColor.interpolate(Color.WHITE, 0.3)` (fill `#82C784`,
border `#6DA470`). Dead: `Color.hsb(22.0, 0.45, brightness * factor)` — fill `#835E48` (0.75), border `#453126` (0.55).
[^snkhead]: Alive color from `EntityDescriptors`. Selected: `aliveColor.interpolate(Color.WHITE, 0.3)` (fill `#4CD0E1`,
border `#4CAEBF`). Dead: `Color.hsb(4.0, 0.45, brightness * factor)` — fill `#9F5C57` (0.75), border `#5A3431` (0.55).

## REBOUNDING_ENTITIES - Rebounding Entities

**Package:** `de.mkalb.etpetssim.simulations.rebounding.model.entity`
**Cell shapes:** Hexagon (default), Square

| Java File                | Kind      | Extends      | Implements                               | Entity Role          |
|--------------------------|-----------|--------------|------------------------------------------|----------------------|
| `ReboundingEntity.java`  | interface | `GridEntity` | -                                        | base entity contract |
| `EntityDescriptors.java` | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | descriptor           |
| `Rebounder.java`         | class     | -            | `ReboundingEntity`                       | agent                |
| `TerrainConstant.java`   | enum      | -            | `ReboundingEntity`, `ConstantGridEntity` | terrain constant     |

### Display Catalog

| Descriptor ID | Long Name (en_US) | Long Name Key                      | Emoji | Fill Color | Border Color | Default In Layer(s) |
|---------------|-------------------|------------------------------------|-------|------------|--------------|---------------------|
| `ground`      | Ground Cell       | `rebounding.entity.ground.long`    | -     | `#0B1220`  | -            | `grid`              |
| `wall`        | Wall Cell         | `rebounding.entity.wall.long`      | -     | `#4A4A4A`  | `#7A7A7A`    | -                   |
| `rebounder`   | Moving Entity     | `rebounding.entity.rebounder.long` | -     | `#FFCC00`  | `#CC9900`    | -                   |

## SIMULATION_LAB - Simulation Lab

**Package:** `de.mkalb.etpetssim.simulations.lab.model.entity`
**Cell shapes:** Hexagon (default), Triangle, Square

| Java File        | Kind | Extends | Implements                             | Entity Role |
|------------------|------|---------|----------------------------------------|-------------|
| `LabEntity.java` | enum | -       | `ConstantGridEntityDescriptorProvider` | cell state  |

### Display Catalog

Note: `SIMULATION_LAB` is a development sandbox. No colors are defined in `LabEntity`; rendering colors are determined
entirely by `LabMainView` independent of entity type.

| Descriptor ID | Long Name (en_US) | Long Name Key                 | Emoji | Fill Color   | Border Color | Default In Layer(s) |
|---------------|-------------------|-------------------------------|-------|--------------|--------------|---------------------|
| `normal`      | Normal Cell       | `lab.entity.normal.long`      | -     | - [^labview] | -            | `grid`              |
| `highlighted` | Highlighted Cell  | `lab.entity.highlighted.long` | -     | - [^labhigh] | -            | -                   |

[^labview]: No color in `LabEntity`. Canvas uses coordinate-based colors (`x % 2`, `y % 2`) — color mode:
`Color.LIGHTSKYBLUE`, `Color.LIGHTSTEELBLUE`, `Color.PALEGREEN`, `Color.MEDIUMAQUAMARINE`; grayscale: `Color.WHITE`,
`Color.LIGHTGRAY`, `Color.DARKGRAY`, `Color.GRAY`.
[^labhigh]: Same as `normal`, overlaid with translucent red (`Color.RED` at α 0.5).

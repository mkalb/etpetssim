# Simulation Entity Inventory (Refactoring Baseline)

This file inventories all Java types from each simulation's `.../model/entity` package.

- Source for simulation types: `app/src/main/java/de/mkalb/etpetssim/SimulationType.java`
- Implemented simulations according to project documentation: `README.md` (section "Implemented Simulations")
- `STARTSCREEN` is intentionally excluded because there is no simulation `.../model/entity` package for it.

## Naming Rules Used for `Final Name`

Rule precedence (top to bottom):

1. Keep simulation root entity contracts unchanged.
2. Keep single enum entity types unchanged for simple simulations.
3. Apply generic renaming rules (prefix removal, placeholder unification, `Base`, etc.).

Scope note: `Final Name` values are simulation-local proposal names for refactoring and are not required to be globally
unique.

Execution scope for follow-up rename chats:

- In scope: Java type names from simulation `.../model/entity` packages listed in this file.
- Out of scope for now: i18n keys/texts, UI labels, descriptor IDs, method names, and non-entity package symbols.

- Keep simulation root entity types unchanged (`EtpetsEntity`, `WatorEntity`, `LangtonEntity`, `SugarEntity`,
  `SnakeEntity`, `ReboundingEntity`) to stay easy to find.
- Keep single entity enums unchanged for simple simulations (`ConwayEntity`, `ForestEntity`, `LabEntity`).
- Remove simulation prefixes from concrete/entity-role types in `.../model/entity` (for example `WatorFish` -> `Fish`).
- Keep domain terms when they are the domain name (`Fish`, `Shark`, `Ant`, `Pet`, `Rebounder`, `SnakeHead`).
- Use unified placeholder names: `NoAgent` and `NoResource`.
- Use `Base` for abstract classes (for example `EtpetsResourceGeneric` -> `ResourceBase`, `WatorCreature` ->
  `CreatureBase`).
- Use unified constant terrain name `TerrainConstant` for constant terrain enums.
- For descriptor enums implementing `GridEntityDescribable`, use unified role name `EntityDescriptors`
  (for example `WatorEntityDescribable` -> `EntityDescriptors`).
  Exception: single-entity enums for simple simulations (`ConwayEntity`, `ForestEntity`, `LabEntity`) stay unchanged.
- For value object records, remove simulation prefixes but keep domain context
  (for example `EtpetsPetGenome` -> `PetGenome`, `EtpetsPetTraits` -> `PetTraits`).
- For factories, prefer domain-focused names over generic `EntityFactory`
  (for example `WatorEntityFactory` -> `CreatureFactory`).

Decision note: In `REBOUNDING_ENTITIES`, `ReboundingMovingEntity` is mapped to `Rebounder` as the target type name.
Terminology changes outside `Final Name` (for example UI/properties keys and descriptor constants) are intentionally
deferred.

## ET_PETS

| Simulation Type | Java File Name (Type)          | Current FQCN                                                                 | Entity Role          | Java Type Kind | Extends (`extends`)     | Implements (`implements`)                    | Grid Interface Info  | Final Name          |
|-----------------|--------------------------------|------------------------------------------------------------------------------|----------------------|----------------|-------------------------|----------------------------------------------|----------------------|---------------------|
| ET_PETS         | `EtpetsAgentEntity.java`       | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsAgentEntity`       | agent contract       | interface      | `EtpetsEntity`          | -                                            | `GridEntity`         | `AgentEntity`       |
| ET_PETS         | `EtpetsAgentNone.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsAgentNone`         | agent placeholder    | enum           | -                       | `EtpetsAgentEntity`, `ConstantGridEntity`    | `ConstantGridEntity` | `NoAgent`           |
| ET_PETS         | `EtpetsEntity.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity`            | base entity contract | interface      | `GridEntity`            | -                                            | `GridEntity`         | `EtpetsEntity`      |
| ET_PETS         | `EtpetsEntityDescribable.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntityDescribable` | descriptor           | enum           | -                       | `GridEntityDescribable`                      | none                 | `EntityDescriptors` |
| ET_PETS         | `EtpetsPet.java`               | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPet`               | agent                | class          | -                       | `EtpetsAgentEntity`                          | `GridEntity`         | `Pet`               |
| ET_PETS         | `EtpetsPetEgg.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetEgg`            | agent                | class          | -                       | `EtpetsAgentEntity`                          | `GridEntity`         | `PetEgg`            |
| ET_PETS         | `EtpetsPetGenome.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetGenome`         | value object         | record         | -                       | -                                            | none                 | `PetGenome`         |
| ET_PETS         | `EtpetsPetTraits.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetTraits`         | value object         | record         | -                       | -                                            | none                 | `PetTraits`         |
| ET_PETS         | `EtpetsResourceEntity.java`    | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceEntity`    | resource contract    | interface      | `EtpetsEntity`          | -                                            | `GridEntity`         | `ResourceEntity`    |
| ET_PETS         | `EtpetsResourceGeneric.java`   | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceGeneric`   | abstract resource    | abstract class | -                       | `EtpetsResourceEntity`                       | `GridEntity`         | `ResourceBase`      |
| ET_PETS         | `EtpetsResourceInsect.java`    | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceInsect`    | resource             | class          | `EtpetsResourceGeneric` | -                                            | `GridEntity`         | `Insect`            |
| ET_PETS         | `EtpetsResourceNone.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceNone`      | resource placeholder | enum           | -                       | `EtpetsResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` | `NoResource`        |
| ET_PETS         | `EtpetsResourcePlant.java`     | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourcePlant`     | resource             | class          | `EtpetsResourceGeneric` | -                                            | `GridEntity`         | `Plant`             |
| ET_PETS         | `EtpetsTerrainConstant.java`   | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainConstant`   | terrain constant     | enum           | -                       | `EtpetsTerrainEntity`, `ConstantGridEntity`  | `ConstantGridEntity` | `TerrainConstant`   |
| ET_PETS         | `EtpetsTerrainEntity.java`     | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainEntity`     | terrain contract     | interface      | `EtpetsEntity`          | -                                            | `GridEntity`         | `TerrainEntity`     |
| ET_PETS         | `EtpetsTerrainTrail.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainTrail`      | terrain              | class          | -                       | `EtpetsTerrainEntity`                        | `GridEntity`         | `Trail`             |

## WATOR

| Simulation Type | Java File Name (Type)         | Current FQCN                                                               | Entity Role          | Java Type Kind | Extends (`extends`) | Implements (`implements`)                  | Grid Interface Info  | Final Name          |
|-----------------|-------------------------------|----------------------------------------------------------------------------|----------------------|----------------|---------------------|--------------------------------------------|----------------------|---------------------|
| WATOR           | `WatorConstantEntity.java`    | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorConstantEntity`    | terrain constant     | enum           | -                   | `WatorEntity`, `ConstantGridEntity`        | `ConstantGridEntity` | `TerrainConstant`   |
| WATOR           | `WatorCreature.java`          | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorCreature`          | abstract creature    | abstract class | -                   | `WatorEntity`, `Comparable<WatorCreature>` | `GridEntity`         | `CreatureBase`      |
| WATOR           | `WatorEntity.java`            | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity`            | base entity contract | interface      | `GridEntity`        | -                                          | `GridEntity`         | `WatorEntity`       |
| WATOR           | `WatorEntityDescribable.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntityDescribable` | descriptor           | enum           | -                   | `GridEntityDescribable`                    | none                 | `EntityDescriptors` |
| WATOR           | `WatorEntityFactory.java`     | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntityFactory`     | factory              | class          | -                   | -                                          | none                 | `CreatureFactory`   |
| WATOR           | `WatorFish.java`              | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorFish`              | agent                | class          | `WatorCreature`     | -                                          | `GridEntity`         | `Fish`              |
| WATOR           | `WatorShark.java`             | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorShark`             | agent                | class          | `WatorCreature`     | -                                          | `GridEntity`         | `Shark`             |

## CONWAYS_LIFE

| Simulation Type | Java File Name (Type) | Current FQCN                                                      | Entity Role | Java Type Kind | Extends (`extends`) | Implements (`implements`)                     | Grid Interface Info  | Final Name     |
|-----------------|-----------------------|-------------------------------------------------------------------|-------------|----------------|---------------------|-----------------------------------------------|----------------------|----------------|
| CONWAYS_LIFE    | `ConwayEntity.java`   | `de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity` | cell state  | enum           | -                   | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` | `ConwayEntity` |

## LANGTONS_ANT

| Simulation Type | Java File Name (Type)              | Current FQCN                                                                      | Entity Role          | Java Type Kind | Extends (`extends`) | Implements (`implements`)                                      | Grid Interface Info  | Final Name          |
|-----------------|------------------------------------|-----------------------------------------------------------------------------------|----------------------|----------------|---------------------|----------------------------------------------------------------|----------------------|---------------------|
| LANGTONS_ANT    | `LangtonAnt.java`                  | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAnt`                  | agent                | class          | -                   | `LangtonAntEntity`                                             | `GridEntity`         | `Ant`               |
| LANGTONS_ANT    | `LangtonAntEntity.java`            | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntEntity`            | agent contract       | interface      | `LangtonEntity`     | -                                                              | `GridEntity`         | `AntEntity`         |
| LANGTONS_ANT    | `LangtonAntEntityDescribable.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntEntityDescribable` | descriptor           | enum           | -                   | `GridEntityDescribable`                                        | none                 | `EntityDescriptors` |
| LANGTONS_ANT    | `LangtonAntNone.java`              | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntNone`              | agent placeholder    | enum           | -                   | `LangtonAntEntity`, `ConstantGridEntity`                       | `ConstantGridEntity` | `NoAgent`           |
| LANGTONS_ANT    | `LangtonEntity.java`               | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity`               | base entity contract | interface      | `GridEntity`        | -                                                              | `GridEntity`         | `LangtonEntity`     |
| LANGTONS_ANT    | `LangtonGroundEntity.java`         | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonGroundEntity`         | terrain state        | enum           | -                   | `LangtonEntity`, `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` | `TerrainConstant`   |

## FOREST_FIRE

| Simulation Type | Java File Name (Type) | Current FQCN                                                      | Entity Role | Java Type Kind | Extends (`extends`) | Implements (`implements`)                     | Grid Interface Info  | Final Name     |
|-----------------|-----------------------|-------------------------------------------------------------------|-------------|----------------|---------------------|-----------------------------------------------|----------------------|----------------|
| FOREST_FIRE     | `ForestEntity.java`   | `de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity` | cell state  | enum           | -                   | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` | `ForestEntity` |

## SUGARSCAPE

| Simulation Type | Java File Name (Type)         | Current FQCN                                                               | Entity Role          | Java Type Kind | Extends (`extends`) | Implements (`implements`)                   | Grid Interface Info  | Final Name          |
|-----------------|-------------------------------|----------------------------------------------------------------------------|----------------------|----------------|---------------------|---------------------------------------------|----------------------|---------------------|
| SUGARSCAPE      | `SugarAgent.java`             | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgent`             | agent                | class          | -                   | `SugarAgentEntity`                          | `GridEntity`         | `Agent`             |
| SUGARSCAPE      | `SugarAgentEntity.java`       | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgentEntity`       | agent contract       | interface      | `SugarEntity`       | -                                           | `GridEntity`         | `AgentEntity`       |
| SUGARSCAPE      | `SugarAgentNone.java`         | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgentNone`         | agent placeholder    | enum           | -                   | `SugarAgentEntity`, `ConstantGridEntity`    | `ConstantGridEntity` | `NoAgent`           |
| SUGARSCAPE      | `SugarEntity.java`            | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity`            | base entity contract | interface      | `GridEntity`        | -                                           | `GridEntity`         | `SugarEntity`       |
| SUGARSCAPE      | `SugarEntityDescribable.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntityDescribable` | descriptor           | enum           | -                   | `GridEntityDescribable`                     | none                 | `EntityDescriptors` |
| SUGARSCAPE      | `SugarResourceEntity.java`    | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceEntity`    | resource contract    | interface      | `SugarEntity`       | -                                           | `GridEntity`         | `ResourceEntity`    |
| SUGARSCAPE      | `SugarResourceNone.java`      | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceNone`      | resource placeholder | enum           | -                   | `SugarResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` | `NoResource`        |
| SUGARSCAPE      | `SugarResourceSugar.java`     | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceSugar`     | resource             | class          | -                   | `SugarResourceEntity`                       | `GridEntity`         | `Sugar`             |

## SNAKE

| Simulation Type | Java File Name (Type)         | Current FQCN                                                               | Entity Role          | Java Type Kind | Extends (`extends`) | Implements (`implements`)           | Grid Interface Info  | Final Name          |
|-----------------|-------------------------------|----------------------------------------------------------------------------|----------------------|----------------|---------------------|-------------------------------------|----------------------|---------------------|
| SNAKE           | `SnakeConstantEntity.java`    | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity`    | terrain constant     | enum           | -                   | `SnakeEntity`, `ConstantGridEntity` | `ConstantGridEntity` | `TerrainConstant`   |
| SNAKE           | `SnakeEntity.java`            | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity`            | base entity contract | interface      | `GridEntity`        | -                                   | `GridEntity`         | `SnakeEntity`       |
| SNAKE           | `SnakeEntityDescribable.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntityDescribable` | descriptor           | enum           | -                   | `GridEntityDescribable`             | none                 | `EntityDescriptors` |
| SNAKE           | `SnakeHead.java`              | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead`              | agent                | class          | -                   | `SnakeEntity`                       | `GridEntity`         | `SnakeHead`         |

## REBOUNDING_ENTITIES

| Simulation Type     | Java File Name (Type)              | Current FQCN                                                                         | Entity Role          | Java Type Kind | Extends (`extends`) | Implements (`implements`)                | Grid Interface Info  | Final Name          |
|---------------------|------------------------------------|--------------------------------------------------------------------------------------|----------------------|----------------|---------------------|------------------------------------------|----------------------|---------------------|
| REBOUNDING_ENTITIES | `ReboundingConstantEntity.java`    | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingConstantEntity`    | terrain constant     | enum           | -                   | `ReboundingEntity`, `ConstantGridEntity` | `ConstantGridEntity` | `TerrainConstant`   |
| REBOUNDING_ENTITIES | `ReboundingEntity.java`            | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity`            | base entity contract | interface      | `GridEntity`        | -                                        | `GridEntity`         | `ReboundingEntity`  |
| REBOUNDING_ENTITIES | `ReboundingEntityDescribable.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntityDescribable` | descriptor           | enum           | -                   | `GridEntityDescribable`                  | none                 | `EntityDescriptors` |
| REBOUNDING_ENTITIES | `ReboundingMovingEntity.java`      | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingMovingEntity`      | agent                | class          | -                   | `ReboundingEntity`                       | `GridEntity`         | `Rebounder`         |

## SIMULATION_LAB

| Simulation Type | Java File Name (Type) | Current FQCN                                                | Entity Role | Java Type Kind | Extends (`extends`) | Implements (`implements`)                     | Grid Interface Info  | Final Name  |
|-----------------|-----------------------|-------------------------------------------------------------|-------------|----------------|---------------------|-----------------------------------------------|----------------------|-------------|
| SIMULATION_LAB  | `LabEntity.java`      | `de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity` | cell state  | enum           | -                   | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` | `LabEntity` |


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
- For descriptor enums that implement `SpecBackedGridEntityDescriptorProvider`, use the standard name `EntityDescriptors`.
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

Use this minimal template when you add a new simulation section:

```markdown
## NEW_SIMULATION

| Simulation Type | Java File            | FQCN                                                                | Entity Role          | Kind      | Extends      | Implements | Grid Interface |
|-----------------|----------------------|---------------------------------------------------------------------|----------------------|-----------|--------------|------------|----------------|
| NEW_SIMULATION  | `ExampleEntity.java` | `de.mkalb.etpetssim.simulations.example.model.entity.ExampleEntity` | base entity contract | interface | `GridEntity` | -          | `GridEntity`   |
```

## ET_PETS

| Simulation Type | Java File                | FQCN                                                                   | Entity Role          | Kind           | Extends        | Implements                             | Grid Interface       |
|-----------------|--------------------------|------------------------------------------------------------------------|----------------------|----------------|----------------|----------------------------------------|----------------------|
| ET_PETS         | `EtpetsEntity.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity`      | base entity contract | interface      | `GridEntity`   | -                                      | `GridEntity`         |
| ET_PETS         | `AgentEntity.java`       | `de.mkalb.etpetssim.simulations.etpets.model.entity.AgentEntity`       | agent contract       | interface      | `EtpetsEntity` | -                                      | `GridEntity`         |
| ET_PETS         | `ResourceEntity.java`    | `de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceEntity`    | resource contract    | interface      | `EtpetsEntity` | -                                      | `GridEntity`         |
| ET_PETS         | `TerrainEntity.java`     | `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity`     | terrain contract     | interface      | `EtpetsEntity` | -                                      | `GridEntity`         |
| ET_PETS         | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent`           | agent placeholder    | enum           | -              | `AgentEntity`, `ConstantGridEntity`    | `ConstantGridEntity` |
| ET_PETS         | `NoResource.java`        | `de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource`        | resource placeholder | enum           | -              | `ResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| ET_PETS         | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EntityDescriptors` | descriptor           | enum           | -              | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| ET_PETS         | `ResourceBase.java`      | `de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceBase`      | abstract resource    | abstract class | -              | `ResourceEntity`                       | `GridEntity`         |
| ET_PETS         | `Pet.java`               | `de.mkalb.etpetssim.simulations.etpets.model.entity.Pet`               | agent                | class          | -              | `AgentEntity`                          | `GridEntity`         |
| ET_PETS         | `PetEgg.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetEgg`            | agent                | class          | -              | `AgentEntity`                          | `GridEntity`         |
| ET_PETS         | `PetGenome.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetGenome`         | value object         | record         | -              | -                                      | `none`               |
| ET_PETS         | `PetTraits.java`         | `de.mkalb.etpetssim.simulations.etpets.model.entity.PetTraits`         | value object         | record         | -              | -                                      | `none`               |
| ET_PETS         | `Insect.java`            | `de.mkalb.etpetssim.simulations.etpets.model.entity.Insect`            | resource             | class          | `ResourceBase` | -                                      | `GridEntity`         |
| ET_PETS         | `Plant.java`             | `de.mkalb.etpetssim.simulations.etpets.model.entity.Plant`             | resource             | class          | `ResourceBase` | -                                      | `GridEntity`         |
| ET_PETS         | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant`   | terrain constant     | enum           | -              | `TerrainEntity`, `ConstantGridEntity`  | `ConstantGridEntity` |
| ET_PETS         | `Trail.java`             | `de.mkalb.etpetssim.simulations.etpets.model.entity.Trail`             | terrain              | class          | -              | `TerrainEntity`                        | `GridEntity`         |

## WATOR

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind           | Extends        | Implements                                | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|----------------|----------------|-------------------------------------------|----------------------|
| WATOR           | `WatorEntity.java`       | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity`       | base entity contract | interface      | `GridEntity`   | -                                         | `GridEntity`         |
| WATOR           | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.EntityDescriptors` | descriptor           | enum           | -              | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| WATOR           | `CreatureBase.java`      | `de.mkalb.etpetssim.simulations.wator.model.entity.CreatureBase`      | abstract creature    | abstract class | -              | `WatorEntity`, `Comparable<CreatureBase>` | `GridEntity`         |
| WATOR           | `CreatureFactory.java`   | `de.mkalb.etpetssim.simulations.wator.model.entity.CreatureFactory`   | factory              | class          | -              | -                                         | `none`               |
| WATOR           | `Fish.java`              | `de.mkalb.etpetssim.simulations.wator.model.entity.Fish`              | agent                | class          | `CreatureBase` | -                                         | `GridEntity`         |
| WATOR           | `Shark.java`             | `de.mkalb.etpetssim.simulations.wator.model.entity.Shark`             | agent                | class          | `CreatureBase` | -                                         | `GridEntity`         |
| WATOR           | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.wator.model.entity.TerrainConstant`   | terrain constant     | enum           | -              | `WatorEntity`, `ConstantGridEntity`       | `ConstantGridEntity` |

## CONWAYS_LIFE

| Simulation Type | Java File           | FQCN                                                              | Entity Role | Kind | Extends | Implements                      | Grid Interface       |
|-----------------|---------------------|-------------------------------------------------------------------|-------------|------|---------|---------------------------------|----------------------|
| CONWAYS_LIFE    | `ConwayEntity.java` | `de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

## LANGTONS_ANT

| Simulation Type | Java File                | FQCN                                                                    | Entity Role          | Kind      | Extends         | Implements                                       | Grid Interface       |
|-----------------|--------------------------|-------------------------------------------------------------------------|----------------------|-----------|-----------------|--------------------------------------------------|----------------------|
| LANGTONS_ANT    | `LangtonEntity.java`     | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity`     | base entity contract | interface | `GridEntity`    | -                                                | `GridEntity`         |
| LANGTONS_ANT    | `AntEntity.java`         | `de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity`         | agent contract       | interface | `LangtonEntity` | -                                                | `GridEntity`         |
| LANGTONS_ANT    | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.langton.model.entity.NoAgent`           | agent placeholder    | enum      | -               | `AntEntity`, `ConstantGridEntity`                | `ConstantGridEntity` |
| LANGTONS_ANT    | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.EntityDescriptors` | descriptor           | enum      | -               | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| LANGTONS_ANT    | `Ant.java`               | `de.mkalb.etpetssim.simulations.langton.model.entity.Ant`               | agent                | class     | -               | `AntEntity`                                      | `GridEntity`         |
| LANGTONS_ANT    | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant`   | terrain constant     | enum      | -               | `LangtonEntity`, `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

## FOREST_FIRE

| Simulation Type | Java File           | FQCN                                                              | Entity Role | Kind | Extends | Implements                      | Grid Interface       |
|-----------------|---------------------|-------------------------------------------------------------------|-------------|------|---------|---------------------------------|----------------------|
| FOREST_FIRE     | `ForestEntity.java` | `de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |

## SUGARSCAPE

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind      | Extends       | Implements                             | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|-----------|---------------|----------------------------------------|----------------------|
| SUGARSCAPE      | `SugarEntity.java`       | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity`       | base entity contract | interface | `GridEntity`  | -                                      | `GridEntity`         |
| SUGARSCAPE      | `AgentEntity.java`       | `de.mkalb.etpetssim.simulations.sugar.model.entity.AgentEntity`       | agent contract       | interface | `SugarEntity` | -                                      | `GridEntity`         |
| SUGARSCAPE      | `ResourceEntity.java`    | `de.mkalb.etpetssim.simulations.sugar.model.entity.ResourceEntity`    | resource contract    | interface | `SugarEntity` | -                                      | `GridEntity`         |
| SUGARSCAPE      | `NoAgent.java`           | `de.mkalb.etpetssim.simulations.sugar.model.entity.NoAgent`           | agent placeholder    | enum      | -             | `AgentEntity`, `ConstantGridEntity`    | `ConstantGridEntity` |
| SUGARSCAPE      | `NoResource.java`        | `de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource`        | resource placeholder | enum      | -             | `ResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| SUGARSCAPE      | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.EntityDescriptors` | descriptor           | enum      | -             | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| SUGARSCAPE      | `Agent.java`             | `de.mkalb.etpetssim.simulations.sugar.model.entity.Agent`             | agent                | class     | -             | `AgentEntity`                          | `GridEntity`         |
| SUGARSCAPE      | `Sugar.java`             | `de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar`             | resource             | class     | -             | `ResourceEntity`                       | `GridEntity`         |

## SNAKE

| Simulation Type | Java File                | FQCN                                                                  | Entity Role          | Kind      | Extends      | Implements                          | Grid Interface       |
|-----------------|--------------------------|-----------------------------------------------------------------------|----------------------|-----------|--------------|-------------------------------------|----------------------|
| SNAKE           | `SnakeEntity.java`       | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity`       | base entity contract | interface | `GridEntity` | -                                   | `GridEntity`         |
| SNAKE           | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.EntityDescriptors` | descriptor           | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| SNAKE           | `SnakeHead.java`         | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead`         | agent                | class     | -            | `SnakeEntity`                       | `GridEntity`         |
| SNAKE           | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.snake.model.entity.TerrainConstant`   | terrain constant     | enum      | -            | `SnakeEntity`, `ConstantGridEntity` | `ConstantGridEntity` |

## REBOUNDING_ENTITIES

| Simulation Type     | Java File                | FQCN                                                                       | Entity Role          | Kind      | Extends      | Implements                               | Grid Interface       |
|---------------------|--------------------------|----------------------------------------------------------------------------|----------------------|-----------|--------------|------------------------------------------|----------------------|
| REBOUNDING_ENTITIES | `ReboundingEntity.java`  | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity`  | base entity contract | interface | `GridEntity` | -                                        | `GridEntity`         |
| REBOUNDING_ENTITIES | `EntityDescriptors.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.EntityDescriptors` | descriptor           | enum      | -            | `SpecBackedGridEntityDescriptorProvider` | `none`             |
| REBOUNDING_ENTITIES | `Rebounder.java`         | `de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder`         | agent                | class     | -            | `ReboundingEntity`                       | `GridEntity`         |
| REBOUNDING_ENTITIES | `TerrainConstant.java`   | `de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant`   | terrain constant     | enum      | -            | `ReboundingEntity`, `ConstantGridEntity` | `ConstantGridEntity` |

## SIMULATION_LAB

| Simulation Type | Java File        | FQCN                                                        | Entity Role | Kind | Extends | Implements                      | Grid Interface       |
|-----------------|------------------|-------------------------------------------------------------|-------------|------|---------|---------------------------------|----------------------|
| SIMULATION_LAB  | `LabEntity.java` | `de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity` | cell state  | enum | -       | `ConstantGridEntityDescriptorProvider` | `ConstantGridEntity` |


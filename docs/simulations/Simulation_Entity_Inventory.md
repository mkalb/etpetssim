# Simulation Entity Inventory (Refactoring Baseline)

This file lists all Java types from `.../model/entity` for each implemented simulation.

- Source for simulation types: `app/src/main/java/de/mkalb/etpetssim/SimulationType.java`
- Implemented simulations according to project documentation: `README.md` (section "Implemented Simulations")
- `STARTSCREEN` is intentionally excluded because there is no simulation `.../model/entity` package for it.

## ET_PETS

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| ET_PETS | `EtpetsAgentEntity.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsAgentEntity` | agent contract | interface | `EtpetsEntity` | - | `GridEntity` |
| ET_PETS | `EtpetsAgentNone.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsAgentNone` | agent placeholder | enum | - | `EtpetsAgentEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| ET_PETS | `EtpetsEntity.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| ET_PETS | `EtpetsEntityDescribable.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| ET_PETS | `EtpetsPet.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPet` | agent | class | - | `EtpetsAgentEntity` | `GridEntity` |
| ET_PETS | `EtpetsPetEgg.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetEgg` | agent | class | - | `EtpetsAgentEntity` | `GridEntity` |
| ET_PETS | `EtpetsPetGenome.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetGenome` | value object | record | - | - | none |
| ET_PETS | `EtpetsPetTraits.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsPetTraits` | value object | record | - | - | none |
| ET_PETS | `EtpetsResourceEntity.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceEntity` | resource contract | interface | `EtpetsEntity` | - | `GridEntity` |
| ET_PETS | `EtpetsResourceGeneric.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceGeneric` | abstract resource | abstract class | - | `EtpetsResourceEntity` | `GridEntity` |
| ET_PETS | `EtpetsResourceInsect.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceInsect` | resource | class | `EtpetsResourceGeneric` | - | `GridEntity` |
| ET_PETS | `EtpetsResourceNone.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceNone` | resource placeholder | enum | - | `EtpetsResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| ET_PETS | `EtpetsResourcePlant.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourcePlant` | resource | class | `EtpetsResourceGeneric` | - | `GridEntity` |
| ET_PETS | `EtpetsTerrainConstant.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainConstant` | terrain constant | enum | - | `EtpetsTerrainEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| ET_PETS | `EtpetsTerrainEntity.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainEntity` | terrain contract | interface | `EtpetsEntity` | - | `GridEntity` |
| ET_PETS | `EtpetsTerrainTrail.java` | `de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainTrail` | terrain | class | - | `EtpetsTerrainEntity` | `GridEntity` |

## WATOR

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| WATOR | `WatorConstantEntity.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorConstantEntity` | terrain constant | enum | - | `WatorEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| WATOR | `WatorCreature.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorCreature` | abstract creature | abstract class | - | `WatorEntity`, `Comparable<WatorCreature>` | `GridEntity` |
| WATOR | `WatorEntity.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| WATOR | `WatorEntityDescribable.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| WATOR | `WatorEntityFactory.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntityFactory` | factory | class | - | - | none |
| WATOR | `WatorFish.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorFish` | agent | class | `WatorCreature` | - | `GridEntity` |
| WATOR | `WatorShark.java` | `de.mkalb.etpetssim.simulations.wator.model.entity.WatorShark` | agent | class | `WatorCreature` | - | `GridEntity` |

## CONWAYS_LIFE

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| CONWAYS_LIFE | `ConwayEntity.java` | `de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity` | cell state | enum | - | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` |

## LANGTONS_ANT

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| LANGTONS_ANT | `LangtonAnt.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAnt` | agent | class | - | `LangtonAntEntity` | `GridEntity` |
| LANGTONS_ANT | `LangtonAntEntity.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntEntity` | agent contract | interface | `LangtonEntity` | - | `GridEntity` |
| LANGTONS_ANT | `LangtonAntEntityDescribable.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| LANGTONS_ANT | `LangtonAntNone.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonAntNone` | agent placeholder | enum | - | `LangtonAntEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| LANGTONS_ANT | `LangtonEntity.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| LANGTONS_ANT | `LangtonGroundEntity.java` | `de.mkalb.etpetssim.simulations.langton.model.entity.LangtonGroundEntity` | terrain state | enum | - | `LangtonEntity`, `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` |

## FOREST_FIRE

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| FOREST_FIRE | `ForestEntity.java` | `de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity` | cell state | enum | - | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` |

## SUGARSCAPE

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| SUGARSCAPE | `SugarAgent.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgent` | agent | class | - | `SugarAgentEntity` | `GridEntity` |
| SUGARSCAPE | `SugarAgentEntity.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgentEntity` | agent contract | interface | `SugarEntity` | - | `GridEntity` |
| SUGARSCAPE | `SugarAgentNone.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgentNone` | agent placeholder | enum | - | `SugarAgentEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| SUGARSCAPE | `SugarEntity.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| SUGARSCAPE | `SugarEntityDescribable.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| SUGARSCAPE | `SugarResourceEntity.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceEntity` | resource contract | interface | `SugarEntity` | - | `GridEntity` |
| SUGARSCAPE | `SugarResourceNone.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceNone` | resource placeholder | enum | - | `SugarResourceEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| SUGARSCAPE | `SugarResourceSugar.java` | `de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceSugar` | resource | class | - | `SugarResourceEntity` | `GridEntity` |

## SNAKE

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| SNAKE | `SnakeConstantEntity.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeConstantEntity` | terrain constant | enum | - | `SnakeEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| SNAKE | `SnakeEntity.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| SNAKE | `SnakeEntityDescribable.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| SNAKE | `SnakeHead.java` | `de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead` | agent | class | - | `SnakeEntity` | `GridEntity` |

## REBOUNDING_ENTITIES

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| REBOUNDING_ENTITIES | `ReboundingConstantEntity.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingConstantEntity` | terrain constant | enum | - | `ReboundingEntity`, `ConstantGridEntity` | `ConstantGridEntity` |
| REBOUNDING_ENTITIES | `ReboundingEntity.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity` | base entity contract | interface | `GridEntity` | - | `GridEntity` |
| REBOUNDING_ENTITIES | `ReboundingEntityDescribable.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntityDescribable` | descriptor | enum | - | `GridEntityDescribable` | none |
| REBOUNDING_ENTITIES | `ReboundingMovingEntity.java` | `de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingMovingEntity` | agent | class | - | `ReboundingEntity` | `GridEntity` |

## SIMULATION_LAB

| Simulation Type | Java File Name (Class) | Current FQCN | Entity Role | Java Type Kind | Superclass (`extends`) | Implemented Interfaces (`implements`) | Grid Interface Info |
|---|---|---|---|---|---|---|---|
| SIMULATION_LAB | `LabEntity.java` | `de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity` | cell state | enum | - | `ConstantGridEntity`, `GridEntityDescribable` | `ConstantGridEntity` |


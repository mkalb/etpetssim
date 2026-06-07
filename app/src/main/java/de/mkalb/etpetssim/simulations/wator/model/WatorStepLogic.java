package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.executor.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;

import java.util.*;

public final class WatorStepLogic implements AgentStepLogic<WatorEntity, WatorStatistics> {

    private final WatorConfig config;
    private final Random random;
    private final CreatureFactory entityFactory;

    public WatorStepLogic(WatorConfig config, Random random, CreatureFactory entityFactory) {
        this.config = config;
        this.random = random;
        this.entityFactory = entityFactory;
    }

    @Override
    public void performAgentStep(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
                                 WatorStatistics statistics) {
        WatorEntity entity = agentCell.entity();

        List<GridCell<WatorEntity>> fishCells = new ArrayList<>();
        List<GridCell<WatorEntity>> waterCells = new ArrayList<>();

        for (var result : CellNeighborhoods.neighborEdgeResults(agentCell.coordinate(), config.neighborhoodMode(), model.structure())) {
            if ((result.action() == EdgeBehaviorAction.VALID) || (result.action() == EdgeBehaviorAction.WRAPPED)) {
                WatorEntity neighborEntity = model.getEntity(result.mapped());
                if (neighborEntity.isFish()) {
                    fishCells.add(new GridCell<>(result.mapped(), neighborEntity));
                } else if (neighborEntity.isWater()) {
                    waterCells.add(new GridCell<>(result.mapped(), neighborEntity));
                }
            }
        }

        if (entity instanceof Fish fish) {
            fishLogic(agentCell, model, stepIndex, statistics, fish, waterCells);
        } else if (entity instanceof Shark shark) {
            sharkLogic(agentCell, model, stepIndex, statistics, shark, fishCells, waterCells);
        }
    }

    private void fishLogic(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
                           WatorStatistics statistics,
                           Fish fish, List<GridCell<WatorEntity>> waterCells) {
        GridCoordinate fishOriginalCoordinate = agentCell.coordinate();
        GridCoordinate fishNewCoordinate = fishOriginalCoordinate;

        // Check if fish is still at its original coordinate. It could have been eaten by a shark.
        if (!model.getEntity(fishOriginalCoordinate).equals(fish)) {
            return;
        }

        // Move fish, if possible
        if (!waterCells.isEmpty()) {
            GridCell<WatorEntity> waterCell = chooseRandomCell(waterCells);
            model.swapInputCellEntities(agentCell, waterCell);
            fishNewCoordinate = waterCell.coordinate();

            // Reproduce, if conditions are met
            if (fish.ageAtStepIndex(stepIndex) >= config.fishMinReproductionAge()) {
                if (fish.timeOfLastReproduction().isEmpty() ||
                        ((stepIndex - fish.timeOfLastReproduction().getAsInt()) >= config.fishMinReproductionInterval())) {
                    Fish childFish = entityFactory.createFish(stepIndex);
                    statistics.incrementFishCells();
                    fish.reproduce(childFish);
                    model.setEntity(fishOriginalCoordinate, childFish);
                    // AppLogger.info("WatorStepLogic - Fish at coordinate: " + fishNewCoordinate + " reproduced with child at: " + fishOriginalCoordinate);
                }
            }
        }

        // Remove fish if it is too old
        if (fish.ageAtStepIndex(stepIndex) >= config.fishMaxAge()) {
            model.setEntityToDefault(fishNewCoordinate);
            statistics.decrementFishCells();
            // AppLogger.info("WatorStepLogic - Fish at coordinate: " + fishNewCoordinate + " is too old and removed.");
        }
    }

    private void sharkLogic(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
                            WatorStatistics statistics,
                            Shark shark, List<GridCell<WatorEntity>> fishCells, List<GridCell<WatorEntity>> waterCells) {
        GridCoordinate sharkOriginalCoordinate = agentCell.coordinate();
        GridCoordinate sharkNewCoordinate = sharkOriginalCoordinate;

        // Reduce energy
        shark.reduceEnergy(config.sharkEnergyLossPerStep());

        if (!fishCells.isEmpty()) {
            GridCell<WatorEntity> fishCell = chooseRandomCell(fishCells);
            model.setEntity(fishCell.coordinate(), shark);
            model.setEntityToDefault(sharkOriginalCoordinate);
            sharkNewCoordinate = fishCell.coordinate();
            shark.gainEnergy(config.sharkEnergyGainPerFish());
            statistics.decrementFishCells();
            // AppLogger.info("WatorStepLogic - Shark at coordinate: " + sharkOriginalCoordinate + " ate fish at: " + sharkNewCoordinate);
        } else if (!waterCells.isEmpty()) {
            GridCell<WatorEntity> waterCell = chooseRandomCell(waterCells);
            model.swapInputCellEntities(agentCell, waterCell);
            sharkNewCoordinate = waterCell.coordinate();
            //    // AppLogger.info("WatorStepLogic - Moving shark from coordinate: " + coordinate + " to: " + sharkCoordinate);
        }
        // Reproduce, if conditions are met
        if (!sharkOriginalCoordinate.equals(sharkNewCoordinate)) {
            if (shark.ageAtStepIndex(stepIndex) >= config.sharkMinReproductionAge()) {
                if (shark.currentEnergy() >= config.sharkMinReproductionEnergy()) {
                    if (shark.timeOfLastReproduction().isEmpty() ||
                            ((stepIndex - shark.timeOfLastReproduction().getAsInt()) >= config.sharkMinReproductionInterval())) {
                        Shark childShark = entityFactory.createShark(stepIndex, config.sharkBirthEnergy());
                        statistics.incrementSharkCells();
                        shark.reproduce(childShark);
                        model.setEntity(sharkOriginalCoordinate, childShark);
                        // AppLogger.info("WatorStepLogic - Reproducing shark at coordinate: " + sharkNewCoordinate + " with child at: " + sharkOriginalCoordinate);
                    }
                }
            }
        }
        // Remove shark if it is too old or has no energy left
        if ((shark.ageAtStepIndex(stepIndex) >= config.sharkMaxAge()) || (shark.currentEnergy() <= 0)) {
            model.setEntityToDefault(sharkNewCoordinate);
            statistics.decrementSharkCells();
            // AppLogger.info("WatorStepLogic - Removing shark at coordinate: " + sharkNewCoordinate);
        }
    }

    private GridCell<WatorEntity> chooseRandomCell(List<GridCell<WatorEntity>> cells) {
        return cells.get(random.nextInt(cells.size()));
    }

}

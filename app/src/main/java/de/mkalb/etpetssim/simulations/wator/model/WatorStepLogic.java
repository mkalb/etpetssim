package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.executor.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.simulations.wator.model.entity.CreatureFactory;
import de.mkalb.etpetssim.simulations.wator.model.entity.Fish;
import de.mkalb.etpetssim.simulations.wator.model.entity.Shark;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class WatorStepLogic implements AgentStepLogic<WatorEntity, WatorStatistics> {

    private final WatorConfig config;
    private final Random random;
    private final CreatureFactory creatureFactory;

    public WatorStepLogic(WatorConfig config, Random random, CreatureFactory creatureFactory) {
        this.config = config;
        this.random = random;
        this.creatureFactory = creatureFactory;
    }

    @Override
    public void performAgentStep(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
                                 WatorStatistics statistics) {
        WatorEntity entity = agentCell.entity();

        List<GridCell<WatorEntity>> fishCells = new ArrayList<>();
        List<GridCell<WatorEntity>> waterCells = new ArrayList<>();

        for (var result : CellNeighborhoods.neighborEdgeBehaviorResults(agentCell.coordinate(), config.neighborhoodMode(), model.structure())) {
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
            performFishStep(agentCell, model, stepIndex, statistics, fish, waterCells);
        } else if (entity instanceof Shark shark) {
            performSharkStep(agentCell, model, stepIndex, statistics, shark, fishCells, waterCells);
        }
    }

    private void performFishStep(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
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
            GridCell<WatorEntity> waterCell = pickRandomCell(waterCells);
            model.setSwappedEntities(agentCell, waterCell);
            fishNewCoordinate = waterCell.coordinate();

            // Reproduce, if conditions are met
            if (fish.ageAtStepIndex(stepIndex) >= config.fishMinReproductionAge()) {
                if (fish.stepIndexOfLastReproduction().isEmpty() ||
                        ((stepIndex - fish.stepIndexOfLastReproduction().getAsInt()) >= config.fishMinReproductionInterval())) {
                    Fish childFish = creatureFactory.createFish(stepIndex);
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

    private void performSharkStep(GridCell<WatorEntity> agentCell, WritableGridModel<WatorEntity> model, int stepIndex,
                                  WatorStatistics statistics,
                                  Shark shark, List<GridCell<WatorEntity>> fishCells, List<GridCell<WatorEntity>> waterCells) {
        GridCoordinate sharkOriginalCoordinate = agentCell.coordinate();
        GridCoordinate sharkNewCoordinate = sharkOriginalCoordinate;

        // Reduce energy
        shark.adjustEnergy(-config.sharkEnergyLossPerStep());

        if (!fishCells.isEmpty()) {
            GridCell<WatorEntity> fishCell = pickRandomCell(fishCells);
            model.setEntity(fishCell.coordinate(), shark);
            model.setEntityToDefault(sharkOriginalCoordinate);
            sharkNewCoordinate = fishCell.coordinate();
            shark.adjustEnergy(config.sharkEnergyGainPerFish());
            statistics.decrementFishCells();
            // AppLogger.info("WatorStepLogic - Shark at coordinate: " + sharkOriginalCoordinate + " ate fish at: " + sharkNewCoordinate);
        } else if (!waterCells.isEmpty()) {
            GridCell<WatorEntity> waterCell = pickRandomCell(waterCells);
            model.setSwappedEntities(agentCell, waterCell);
            sharkNewCoordinate = waterCell.coordinate();
            //    // AppLogger.info("WatorStepLogic - Moving shark from coordinate: " + coordinate + " to: " + sharkCoordinate);
        }
        // Reproduce, if conditions are met
        if (!sharkOriginalCoordinate.equals(sharkNewCoordinate)) {
            if (shark.ageAtStepIndex(stepIndex) >= config.sharkMinReproductionAge()) {
                if (shark.currentEnergy() >= config.sharkMinReproductionEnergy()) {
                    if (shark.stepIndexOfLastReproduction().isEmpty() ||
                            ((stepIndex - shark.stepIndexOfLastReproduction().getAsInt()) >= config.sharkMinReproductionInterval())) {
                        Shark childShark = creatureFactory.createShark(stepIndex, config.sharkBirthEnergy());
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

    private GridCell<WatorEntity> pickRandomCell(List<GridCell<WatorEntity>> cells) {
        return cells.get(random.nextInt(cells.size()));
    }

}

package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.AgentStepLogic;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;

import java.util.*;

public final class WatorAgentLogicFactory {

    private final WatorConfig config;
    private final Random random;
    private final WatorEntityFactory entityFactory;

    public WatorAgentLogicFactory(WatorConfig config, Random random, WatorEntityFactory entityFactory) {
        this.config = config;
        this.random = random;
        this.entityFactory = entityFactory;
    }

    public AgentStepLogic<WatorEntity, WatorStatistics> createAgentLogic(WatorLogicType type) {
        return switch (type) {
            case SIMPLE -> this::simpleLogic;
            case ADVANCED -> this::advancedLogic;
        };
    }

    private void simpleLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, int stepIndex,
                             WatorStatistics statistics) {
        GridCoordinate coordinate = agentCell.coordinate();
        WatorEntity entity = agentCell.entity();
        GridStructure structure = model.structure();

        List<GridCell<WatorEntity>> fishCells = new ArrayList<>();
        List<GridCell<WatorEntity>> sharkCells = new ArrayList<>();
        List<GridCell<WatorEntity>> waterCells = new ArrayList<>();

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighbors = CellNeighborhoods.cellNeighborsWithEdgeBehavior(coordinate, config.neighborhoodMode(), structure);
        for (GridCoordinate neighborCoordinate : neighbors.keySet()) {
            if (structure.isCoordinateValid(neighborCoordinate)) {
                WatorEntity neighborEntity = model.getEntity(neighborCoordinate);
                if (neighborEntity.isFish()) {
                    fishCells.add(new GridCell<>(neighborCoordinate, neighborEntity));
                } else if (neighborEntity.isShark()) {
                    sharkCells.add(new GridCell<>(neighborCoordinate, neighborEntity));
                } else {
                    waterCells.add(new GridCell<>(neighborCoordinate, neighborEntity));
                }
            }
        }

        // TODO handle REFLECT and ABSORB

        if (entity instanceof WatorFish fish) {
            fishSimpleLogic(agentCell, model, stepIndex, statistics, fish, waterCells);
        } else if (entity instanceof WatorShark shark) {
            sharkSimpleLogic(agentCell, model, stepIndex, statistics, shark, fishCells, waterCells);
        }
    }

    private void fishSimpleLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, int stepIndex,
                                 WatorStatistics statistics,
                                 WatorFish fish, List<GridCell<WatorEntity>> waterCells) {
        GridCoordinate fishOriginalCoordinate = agentCell.coordinate();
        GridCoordinate fishNewCoordinate = fishOriginalCoordinate;

        // Check if fish is still at its original coordinate. It could have been eaten by a shark.
        if (!model.getEntity(fishOriginalCoordinate).equals(fish)) {
            return;
        }

        // Move fish, if possible
        if (!waterCells.isEmpty()) {
            GridCell<WatorEntity> waterCell = chooseRandomCoordinate(waterCells);
            model.swapEntities(agentCell, waterCell);
            fishNewCoordinate = waterCell.coordinate();

            // Reproduce, if conditions are met
            if (fish.ageAtStepIndex(stepIndex) >= 5) {
                if (fish.timeOfLastReproduction().isEmpty() ||
                        ((stepIndex - fish.timeOfLastReproduction().getAsInt()) >= 3)) {
                    WatorFish childFish = entityFactory.createFish(stepIndex);
                    statistics.incrementFishCells();
                    fish.reproduce(childFish);
                    model.setEntity(fishOriginalCoordinate, childFish);
                    // AppLogger.info("WatorAgentLogicFactory - Fish at coordinate: " + fishNewCoordinate + " reproduced with child at: " + fishOriginalCoordinate);
                }
            }
        }

        // Remove fish if it is too old
        if (fish.ageAtStepIndex(stepIndex) >= config.fishMaxAge()) {
            model.setEntityToDefault(fishNewCoordinate);
            statistics.decrementFishCells();
            // AppLogger.info("WatorAgentLogicFactory - Fish at coordinate: " + fishNewCoordinate + " is too old and removed.");
        }
    }

    private void sharkSimpleLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, int stepIndex,
                                  WatorStatistics statistics,
                                  WatorShark shark, List<GridCell<WatorEntity>> fishCells, List<GridCell<WatorEntity>> waterCells) {
        GridCoordinate sharkOriginalCoordinate = agentCell.coordinate();
        GridCoordinate sharkNewCoordinate = sharkOriginalCoordinate;

        // Reduce energy
        shark.reduceEnergy(1);

        if (!fishCells.isEmpty()) {
            GridCell<WatorEntity> fishCell = chooseRandomCoordinate(fishCells);
            model.setEntity(fishCell.coordinate(), shark);
            model.setEntityToDefault(sharkOriginalCoordinate);
            sharkNewCoordinate = fishCell.coordinate();
            shark.gainEnergy(2);
            statistics.decrementFishCells();
            // AppLogger.info("WatorAgentLogicFactory - Shark at coordinate: " + sharkOriginalCoordinate + " ate fish at: " + sharkNewCoordinate);
        } else if (!waterCells.isEmpty()) {
            GridCell<WatorEntity> waterCell = chooseRandomCoordinate(waterCells);
            model.swapEntities(agentCell, waterCell);
            sharkNewCoordinate = waterCell.coordinate();
            //    // AppLogger.info("WatorAgentLogicFactory - Moving shark from coordinate: " + coordinate + " to: " + sharkCoordinate);
        }
        // Reproduce, if conditions are met
        if (!sharkOriginalCoordinate.equals(sharkNewCoordinate)) {
            if (shark.ageAtStepIndex(stepIndex) >= 15) {
                if (shark.currentEnergy() >= 5) {
                    if (shark.timeOfLastReproduction().isEmpty() ||
                            ((stepIndex - shark.timeOfLastReproduction().getAsInt()) >= 3)) {
                        WatorShark childShark = entityFactory.createShark(stepIndex, config.sharkBirthEnergy());
                        statistics.incrementSharkCells();
                        shark.reproduce(childShark);
                        model.setEntity(sharkOriginalCoordinate, childShark);
                        // AppLogger.info("WatorAgentLogicFactory - Reproducing shark at coordinate: " + sharkNewCoordinate + " with child at: " + sharkOriginalCoordinate);
                    }
                }
            }
        }
        // Remove shark if it is too old or has no energy left
        if ((shark.ageAtStepIndex(stepIndex) >= config.sharkMaxAge()) || (shark.currentEnergy() <= 0)) {
            model.setEntityToDefault(sharkNewCoordinate);
            statistics.decrementSharkCells();
            // AppLogger.info("WatorAgentLogicFactory - Removing shark at coordinate: " + sharkNewCoordinate);
        }
    }

    private GridCell<WatorEntity> chooseRandomCoordinate(List<GridCell<WatorEntity>> cells) {
        return cells.get(random.nextInt(cells.size()));
    }

    private void advancedLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, int stepIndex,
                               WatorStatistics statistics) {
        // TODO Implement advanced agent logic here
    }

    public enum WatorLogicType {
        SIMPLE, ADVANCED
    }

}

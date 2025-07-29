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

    private void simpleLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, long currentStep, WatorStatistics statistics) {
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

        if (entity.isFish()) {
            // Move fish, if possible
            if (!waterCells.isEmpty()) {
                GridCell<WatorEntity> newCell = chooseRandomCoordinate(waterCells);
                model.swapEntities(agentCell, newCell);

                // Reproduce, if conditions are met

            }
        }

        // TODO Implement simple agent logic here
    }

    private GridCell<WatorEntity> chooseRandomCoordinate(List<GridCell<WatorEntity>> cells) {
        return cells.get(random.nextInt(cells.size()));
    }

    private void advancedLogic(GridCell<WatorEntity> agentCell, GridModel<WatorEntity> model, long currentStep, WatorStatistics statistics) {
        // TODO Implement advanced agent logic here
    }

    public enum WatorLogicType {
        SIMPLE, ADVANCED
    }

}

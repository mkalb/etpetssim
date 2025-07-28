package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborWithEdgeBehavior;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;

import java.util.*;
import java.util.function.*;

public final class WatorAgentLogicFactory {

    private final WatorConfig config;
    private final WatorEntityFactory entityFactory;

    public WatorAgentLogicFactory(WatorConfig config, WatorEntityFactory entityFactory) {
        this.config = config;
        this.entityFactory = entityFactory;
    }

    public Consumer<WatorAgentContext> createAgentLogic(WatorLogicType type) {
        return switch (type) {
            case SIMPLE -> this::simpleLogic;
            case ADVANCED -> this::advancedLogic;
        };
    }

    private void simpleLogic(WatorAgentContext context) {
        GridCoordinate coordinate = context.cell().coordinate();
        WatorEntity entity = context.cell().entity();
        GridModel<WatorEntity> model = context.model();
        GridStructure structure = model.structure();
        WatorStatistics statistics = context.statistics();

        List<GridCell<WatorEntity>> fishCells = new ArrayList<>();
        List<GridCell<WatorEntity>> sharkCells = new ArrayList<>();
        List<GridCell<WatorEntity>> waterCells = new ArrayList<>();

        Map<GridCoordinate, List<CellNeighborWithEdgeBehavior>> neighbors = CellNeighborhoods.cellNeighborsWithEdgeBehavior(coordinate, NeighborhoodMode.EDGES_ONLY, structure);
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

        // TODO Implement simple agent logic here
    }

    private void advancedLogic(WatorAgentContext context) {
        // TODO Implement advanced agent logic here
    }

    public enum WatorLogicType {
        SIMPLE, ADVANCED
    }

}

package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;

import java.util.*;

public final class LabSimulationManager {

    public static final LabConfig CONFIG_TRIANGLE = new LabConfig(
            CellShape.TRIANGLE,
            90.0d,
            24,
            24);
    public static final LabConfig CONFIG_SQUARE = new LabConfig(
            CellShape.SQUARE,
            70.0d,
            24,
            16);
    public static final LabConfig CONFIG_HEXAGON = new LabConfig(
            CellShape.HEXAGON,
            40.0d,
            24,
            16);

    private final LabConfig config;

    private final GridStructure structure;
    private final GridModel<LabEntity> model;

    public LabSimulationManager(LabConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(config.shape(), GridEdgeBehavior.BLOCK_X_BLOCK_Y),
                new GridSize(config.gridWidth(), config.gridHeight())
        );

        model = new SparseGridModel<>(structure, LabEntity.NORMAL);
        GridInitializers.placeRandomCounted(3, () -> LabEntity.HIGHLIGHTED, new Random())
                        .initialize(model);

        // Place a symmetric small cross pattern of highlighted entities
        GridEntityUtils.placePatternAt(new GridCoordinate(5, 5), model,
                GridPatterns.of(LabEntity.HIGHLIGHTED, List.of(
                        new GridOffset(0, 1),
                        new GridOffset(1, 0),
                        new GridOffset(2, 1),
                        new GridOffset(1, 2)
                )));
    }

    public ReadableGridModel<LabEntity> currentModel() {
        return model;
    }

    public GridStructure structure() {
        return structure;
    }

    public LabConfig config() {
        return config;
    }

}

package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.*;

import java.util.*;

public final class LabSimulationManager {

    private final LabConfig config;

    private final GridStructure structure;
    private final GridModel<LabEntity> model;

    public LabSimulationManager(LabConfig config) {
        this.config = config;

        structure = new GridStructure(
                new GridTopology(config.cellShape(), config.gridEdgeBehavior()),
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

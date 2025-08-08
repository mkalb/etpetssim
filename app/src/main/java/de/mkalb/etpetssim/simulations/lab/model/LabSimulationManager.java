package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.SimulationManager;

import java.util.*;

public final class LabSimulationManager
        implements SimulationManager<LabEntity, LabConfig, LabStatistics> {

    private final LabConfig config;

    private final GridStructure structure;
    private final LabStatistics statistics;
    private final GridModel<LabEntity> model;

    public LabSimulationManager(LabConfig config) {
        this.config = config;

        structure = config.createGridStructure();
        statistics = new LabStatistics(structure.cellCount());

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

    @Override
    public LabConfig config() {
        return config;
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public LabStatistics statistics() {
        return statistics;
    }

    @Override
    public void executeStep() {
        // Do nothing
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int stepCount() {
        return 0;
    }

    @Override
    public ReadableGridModel<LabEntity> currentModel() {
        return model;
    }

}

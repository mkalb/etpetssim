package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridOffset;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.StepExecutionResult;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.support.GridEntityUtils;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.engine.support.GridPatterns;
import de.mkalb.etpetssim.simulations.core.model.SimulationManager;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;

import java.util.*;

public final class LabSimulationManager
        implements SimulationManager<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

    private static final int INITIAL_HIGHLIGHTED_COUNT = 3;

    private final LabConfig config;

    private final GridStructure structure;
    private final LabStatistics statistics;
    private final WritableGridModel<LabEntity> model;

    public LabSimulationManager(LabConfig config) {
        this.config = config;

        structure = config.createGridStructure();
        statistics = new LabStatistics(structure);

        model = new SparseGridModel<>(structure, LabEntity.NORMAL);
        GridInitializers.placeRandomCount(INITIAL_HIGHLIGHTED_COUNT,
                                () -> LabEntity.HIGHLIGHTED,
                                e -> e == LabEntity.NORMAL,
                                new Random(config.seed()))
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
    public StepExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep) {
        // Do nothing
        return new StepExecutionResult(stepCount(), 0, true, false);
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean isExecutorFinished() {
        return true;
    }

    @Override
    public int stepCount() {
        return 0;
    }

    @Override
    public WritableGridModel<LabEntity> currentModel() {
        return model;
    }

}

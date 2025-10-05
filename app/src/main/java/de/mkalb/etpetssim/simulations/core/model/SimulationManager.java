package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationExecutor;

public interface SimulationManager<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics> {

    CON config();

    GridStructure structure();

    STA statistics();

    void executeStep();

    SimulationExecutor.ExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep);

    boolean isFinished();

    boolean isExecutorFinished();

    int stepCount();

    GM currentModel();

}
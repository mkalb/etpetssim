package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SimulationExecutor;

public interface SimulationManager<ENT extends GridEntity, CON extends SimulationConfig, STA extends SimulationStatistics> {

    CON config();

    GridStructure structure();

    STA statistics();

    void executeStep();

    SimulationExecutor.ExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep);

    boolean isFinished();

    int stepCount();

    ReadableGridModel<ENT> currentModel();

}
package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;

public interface SimulationManager<ENT extends GridEntity, CON extends SimulationConfig, STA extends SimulationStatistics> {

    CON config();

    GridStructure structure();

    STA statistics();

    void executeStep();

    void executeSteps(int count, Runnable onStep);

    boolean isRunning();

    int stepCount();

    ReadableGridModel<ENT> currentModel();

}
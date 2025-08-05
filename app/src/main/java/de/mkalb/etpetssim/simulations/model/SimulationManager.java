package de.mkalb.etpetssim.simulations.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;

public interface SimulationManager<ENT extends GridEntity, CON, STA> {

    CON config();

    GridStructure structure();

    STA statistics();

    void executeStep();

    boolean isRunning();

    int stepCount();

    ReadableGridModel<ENT> currentModel();

}
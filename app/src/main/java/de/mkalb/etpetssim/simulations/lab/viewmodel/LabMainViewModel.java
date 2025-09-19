package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.simulations.lab.model.*;
import de.mkalb.etpetssim.simulations.model.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractMainViewModel;
import javafx.beans.property.ObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabMainViewModel
        extends AbstractMainViewModel<LabEntity, WritableGridModel<LabEntity>, LabConfig, LabStatistics> {

    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};
    private Runnable drawRequestedListener = () -> {};
    private Runnable drawModelRequestedListener = () -> {};
    private Runnable drawTestRequestedListener = () -> {};

    public LabMainViewModel(ObjectProperty<SimulationState> simulationState,
                            LabConfigViewModel configViewModel,
                            LabControlViewModel controlViewModel,
                            LabObservationViewModel observationViewModel) {
        super(simulationState, configViewModel, observationViewModel);

        configViewModel.configChangedRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleConfigChanged();
                configViewModel.configChangedRequestedProperty().set(false); // reset
            }
        });
        controlViewModel.drawRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleDrawRequested();
                controlViewModel.drawRequestedProperty().set(false); // reset
            }
        });
        controlViewModel.drawModelRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleDrawModelRequested();
                controlViewModel.drawModelRequestedProperty().set(false); // reset
            }
        });
        controlViewModel.drawTestRequestedProperty().addListener((_, _, newVal) -> {
            if (newVal) {
                handleDrawTestRequested();
                controlViewModel.drawTestRequestedProperty().set(false); // reset
            }
        });
        lastClickedCoordinateProperty().addListener((_, _, newVal) -> observationViewModel.lastClickedCoordinateProperty().set(newVal));
    }

    @Override
    public void shutdownSimulation() {
        // Do nothing
    }

    @Override
    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    @Override
    public double getCellEdgeLength() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config().cellEdgeLength();
    }

    @Override
    public LabConfig getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config();
    }

    @Override
    public WritableGridModel<LabEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

    @Override
    public boolean hasSimulationManager() {
        return simulationManager != null;
    }

    public void setConfigChangedListener(Runnable listener) {
        configChangedListener = listener;
    }

    public void setDrawRequestedListener(Runnable listener) {
        drawRequestedListener = listener;
    }

    public void setDrawModelRequestedListener(Runnable listener) {
        drawModelRequestedListener = listener;
    }

    public void setDrawTestRequestedListener(Runnable listener) {
        drawTestRequestedListener = listener;
    }

    public void handleConfigChanged() {
        setSimulationState(SimulationState.INITIAL);
        reset();

        configChangedListener.run();
    }

    public void handleDrawRequested() {
        // Reset notification type.
        setNotificationType(SimulationNotificationType.NONE);

        setSimulationState(SimulationState.RUNNING_TIMED);

        reset();

        LabConfig config = configViewModel.getConfig();
        if (!config.isValid()) {
            setSimulationState(SimulationState.ERROR);
            AppLogger.warn("Cannot start simulation, because configuration is invalid.");
            setNotificationType(SimulationNotificationType.INVALID_CONFIG);
            return;
        }

        simulationManager = new LabSimulationManager(config);

        // Log information
        AppLogger.info("Structure:       " + simulationManager.structure().toDisplayString());
        AppLogger.info("Cell count:      " + simulationManager.structure().cellCount());

        drawRequestedListener.run();

        setSimulationState(SimulationState.PAUSED);
    }

    public void handleDrawModelRequested() {
        drawModelRequestedListener.run();
    }

    public void handleDrawTestRequested() {
        drawTestRequestedListener.run();
    }

    private void reset() {
        // Reset the simulation manager if it exists
        simulationManager = null;
        // Clear last clicked coordinate
        resetClickedCoordinateProperties();
    }

}

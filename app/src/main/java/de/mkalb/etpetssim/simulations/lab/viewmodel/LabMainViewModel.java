package de.mkalb.etpetssim.simulations.lab.viewmodel;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.lab.model.*;
import de.mkalb.etpetssim.simulations.model.SimulationNotificationType;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.viewmodel.AbstractMainViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabMainViewModel
        extends AbstractMainViewModel<LabEntity, LabConfig, LabStatistics> {

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
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
        lastClickedCoordinate.addListener((_, _, newVal) -> observationViewModel.lastClickedCoordinateProperty().set(newVal));
    }

    @Override
    public void shutdownSimulation() {
        // Do nothing
    }

    public ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinateProperty() {
        return lastClickedCoordinate;
    }

    public @Nullable GridCoordinate getLastClickedCoordinate() {
        return lastClickedCoordinate.get();
    }

    public void setLastClickedCoordinate(@Nullable GridCoordinate value) {
        lastClickedCoordinate.set(value);
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
    public ReadableGridModel<LabEntity> getCurrentModel() {
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
        AppLogger.info("Structure:       " + simulationManager.currentModel().structure().toDisplayString());
        AppLogger.info("Cell count:      " + simulationManager.currentModel().structure().cellCount());
        AppLogger.info("NonDefaultCells: " + simulationManager.currentModel().nonDefaultCells().count());

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
        setLastClickedCoordinate(null);
    }

}

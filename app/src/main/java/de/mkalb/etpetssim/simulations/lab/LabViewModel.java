package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.simulations.SimulationState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabViewModel {

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
    private final ObjectProperty<SimulationState> simulationState;
    private final LabConfigViewModel configViewModel;
    private final LabControlViewModel controlViewModel;
    private final LabObservationViewModel observationViewModel;
    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};
    private Runnable drawRequestedListener = () -> {};
    private Runnable drawModelRequestedListener = () -> {};
    private Runnable drawTestRequestedListener = () -> {};

    public LabViewModel(SimpleObjectProperty<SimulationState> simulationState,
                        LabConfigViewModel configViewModel,
                        LabControlViewModel controlViewModel,
                        LabObservationViewModel observationViewModel) {
        this.simulationState = simulationState;
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;
        this.observationViewModel = observationViewModel;

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
        lastClickedCoordinate.addListener((_, _, newVal) -> {
            observationViewModel.lastClickedCoordinateProperty().set(newVal);
        });
    }

    public ReadOnlyObjectProperty<SimulationState> simulationStateProperty() {
        return simulationState;
    }

    public SimulationState getSimulationState() {
        return simulationState.get();
    }

    private void setSimulationState(SimulationState state) {
        simulationState.set(state);
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

    public GridStructure getStructure() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.structure();
    }

    public LabConfig getCurrentConfig() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.config();
    }

    public ReadableGridModel<LabEntity> getCurrentModel() {
        Objects.requireNonNull(simulationManager, "Simulation manager is not initialized.");
        return simulationManager.currentModel();
    }

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
        setSimulationState(SimulationState.READY);
        // Reset the simulation manager if it exists
        simulationManager = null;

        configChangedListener.run();
    }

    public void handleDrawRequested() {
        setSimulationState(SimulationState.RUNNING);

        // Reset the simulation manager if it exists
        simulationManager = null;

        LabConfig config = configViewModel.getConfig();
        if (!config.isValid()) {
            AppLogger.warn("Invalid configuration: " + config);
            return;
        }

        simulationManager = new LabSimulationManager(config);

        // Log information
        AppLogger.info("Structure:       " + simulationManager.currentModel().structure().toDisplayString());
        AppLogger.info("Cell count:      " + simulationManager.currentModel().structure().cellCount());
        AppLogger.info("NonDefaultCells: " + simulationManager.currentModel().nonDefaultCells().count());

        drawRequestedListener.run();
    }

    public void handleDrawModelRequested() {
        drawModelRequestedListener.run();
    }

    public void handleDrawTestRequested() {
        drawTestRequestedListener.run();
    }

}

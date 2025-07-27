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
    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};
    private Runnable drawListener = () -> {};
    private Runnable drawModelListener = () -> {};
    private Runnable drawTestListener = () -> {};

    public LabViewModel(SimpleObjectProperty<SimulationState> simulationState, LabConfigViewModel configViewModel, LabControlViewModel controlViewModel) {
        this.simulationState = simulationState;
        this.configViewModel = configViewModel;
        this.controlViewModel = controlViewModel;

        configViewModel.setOnConfigChangedListener(this::onConfigChanged);
        controlViewModel.setOnDrawButtonListener(this::onDrawEvent);
        controlViewModel.setOnDrawModelButtonListener(this::onDrawModelEvent);
        controlViewModel.setOnDrawTestButtonListener(this::onDrawTestEvent);
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

    public void setDrawListener(Runnable listener) {
        drawListener = listener;
    }

    public void setDrawModelListener(Runnable listener) {
        drawModelListener = listener;
    }

    public void setDrawTestListener(Runnable listener) {
        drawTestListener = listener;
    }

    public void onConfigChanged() {
        setSimulationState(SimulationState.READY);
        // Reset the simulation manager if it exists
        simulationManager = null;

        configChangedListener.run();
    }

    public void onDrawEvent() {
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

        drawListener.run();
    }

    public void onDrawModelEvent() {
        drawModelListener.run();
    }

    public void onDrawTestEvent() {
        drawTestListener.run();
    }

}

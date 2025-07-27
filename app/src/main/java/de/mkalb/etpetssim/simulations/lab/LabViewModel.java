package de.mkalb.etpetssim.simulations.lab;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class LabViewModel {

    private final ObjectProperty<@Nullable GridCoordinate> lastClickedCoordinate = new SimpleObjectProperty<>(null);
    private final LabConfigViewModel configViewModel;
    private @Nullable LabSimulationManager simulationManager;

    private Runnable configChangedListener = () -> {};

    public LabViewModel(LabConfigViewModel configViewModel) {
        this.configViewModel = configViewModel;

        configViewModel.setOnConfigChangedListener(this::onConfigChanged);
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

    public void onConfigChanged() {
        configChangedListener.run();
    }

    public void onDrawButtonClicked() {
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
    }

}

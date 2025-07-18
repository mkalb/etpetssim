package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conwayslife.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conwayslife.view.*;
import de.mkalb.etpetssim.simulations.conwayslife.viewmodel.*;
import de.mkalb.etpetssim.simulations.simulationlab.*;
import de.mkalb.etpetssim.simulations.startscreen.StartScreenView;
import de.mkalb.etpetssim.simulations.wator.WaTorController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

import java.util.function.*;

/**
 * Factory class for creating instances of simulations.
 * It provides a method to create a simulation instance based on the type of simulation requested.
 */
public final class SimulationFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SimulationFactory() {
    }

    /**
     * Creates a new instance of a simulation based on the provided type, stage and stage updater.
     *
     * @param type the type of simulation to create
     * @param stage the stage where the simulation will be displayed
     * @param stageUpdater a consumer that updates the stage with the new simulation type
     * @return a new SimulationInstance for the specified type
     */
    public static SimulationInstance createInstance(SimulationType type,
                                                    Stage stage,
                                                    BiConsumer<Stage, SimulationType> stageUpdater) {
        return switch (type) {
            case STARTSCREEN -> SimulationInstance.of(type, createStartScreenView(stage, stageUpdater));
            case SIMULATION_LAB -> SimulationInstance.of(type, createLabView());
            case WATOR -> SimulationInstance.of(type, new WaTorController());
            case CONWAYS_LIFE -> SimulationInstance.of(type, createConwayView());
            // add other simulation types here later after implementing them
            default -> {
                AppLogger.error("Simulation type not implemented: " + type.name());
                // Switch to the start screen as a fallback
                yield SimulationInstance.of(SimulationType.STARTSCREEN, createStartScreenView(stage, stageUpdater));
            }
        };
    }

    private static StartScreenView createStartScreenView(Stage stage,
                                                         BiConsumer<Stage, SimulationType> stageUpdater) {
        return new StartScreenView(stage, stageUpdater);
    }

    private static LabView createLabView() {
        // Common
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(LabEntity.values());

        // ViewModel
        var viewModel = new LabViewModel(LabSimulationManager.CONFIG_HEXAGON);

        // View
        var view = new LabView(viewModel, entityDescriptorRegistry);

        // Return the main view
        return view;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static ConwayView createConwayView() {
        // Common
        var simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ConwayEntity.values());

        // ViewModel
        var configViewModel = new ConwayConfigViewModel(simulationState);
        var controlViewModel = new ConwayControlViewModel(simulationState);
        var observationViewModel = new ConwayObservationViewModel(simulationState);
        var viewModel = new ConwayViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new ConwayConfigView(configViewModel);
        var controlView = new ConwayControlView(controlViewModel);
        var observationView = new ConwayObservationView(observationViewModel);
        var view = new ConwayView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

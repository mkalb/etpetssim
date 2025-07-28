package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.view.*;
import de.mkalb.etpetssim.simulations.conway.viewmodel.*;
import de.mkalb.etpetssim.simulations.lab.model.LabEntity;
import de.mkalb.etpetssim.simulations.lab.view.*;
import de.mkalb.etpetssim.simulations.lab.viewmodel.*;
import de.mkalb.etpetssim.simulations.start.StartView;
import de.mkalb.etpetssim.simulations.wator.model.WatorEntityDescribable;
import de.mkalb.etpetssim.simulations.wator.view.*;
import de.mkalb.etpetssim.simulations.wator.viewmodel.*;
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
            case STARTSCREEN -> SimulationInstance.of(type, createStartView(stage, stageUpdater));
            case SIMULATION_LAB -> SimulationInstance.of(type, createLabView());
            case WATOR -> SimulationInstance.of(type, createWatorView());
            case CONWAYS_LIFE -> SimulationInstance.of(type, createConwayView());
            // add other simulation types here later after implementing them
            default -> {
                AppLogger.error("Simulation type not implemented: " + type.name());
                // Switch to the start screen as a fallback
                yield SimulationInstance.of(SimulationType.STARTSCREEN, createStartView(stage, stageUpdater));
            }
        };
    }

    private static StartView createStartView(Stage stage,
                                             BiConsumer<Stage, SimulationType> stageUpdater) {
        return new StartView(stage, stageUpdater);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static LabView createLabView() {
        // Common
        var simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(LabEntity.values());

        // ViewModel
        var configViewModel = new LabConfigViewModel(simulationState);
        var controlViewModel = new LabControlViewModel(simulationState);
        var observationViewModel = new LabObservationViewModel(simulationState);
        var viewModel = new LabViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new LabConfigView(configViewModel);
        var controlView = new LabControlView(controlViewModel);
        var observationView = new LabObservationView(observationViewModel);
        var view = new LabView(viewModel, configView, controlView, observationView, entityDescriptorRegistry);

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

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static WatorView createWatorView() {
        // Common
        var simulationState = new SimpleObjectProperty<>(SimulationState.READY);
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(WatorEntityDescribable.values());

        // ViewModel
        var configViewModel = new WatorConfigViewModel(simulationState);
        var controlViewModel = new WatorControlViewModel(simulationState);
        var observationViewModel = new WatorObservationViewModel(simulationState);
        var viewModel = new WatorViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new WatorConfigView(configViewModel);
        var controlView = new WatorControlView(controlViewModel);
        var observationView = new WatorObservationView(observationViewModel);
        var view = new WatorView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.view.ConwayConfigView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayObservationView;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabEntity;
import de.mkalb.etpetssim.simulations.lab.view.*;
import de.mkalb.etpetssim.simulations.lab.viewmodel.*;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.start.StartMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorEntityDescribable;
import de.mkalb.etpetssim.simulations.wator.model.WatorSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.view.WatorConfigView;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import de.mkalb.etpetssim.simulations.wator.view.WatorObservationView;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    private static StartMainView createStartView(Stage stage,
                                                 BiConsumer<Stage, SimulationType> stageUpdater) {
        return new StartMainView(stage, stageUpdater);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static LabMainView createLabView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(LabEntity.values());

        // ViewModel
        var configViewModel = new LabConfigViewModel(readOnlySimulationState);
        var controlViewModel = new LabControlViewModel(readOnlySimulationState);
        var observationViewModel = new LabObservationViewModel(readOnlySimulationState);
        var viewModel = new LabMainViewModel(simulationState, configViewModel, controlViewModel, observationViewModel);

        // View
        var configView = new LabConfigView(configViewModel);
        var controlView = new LabControlView(controlViewModel);
        var observationView = new LabObservationView(observationViewModel);
        var view = new LabMainView(viewModel, configView, controlView, observationView, entityDescriptorRegistry);

        // Return the main view
        return view;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static ConwayMainView createConwayView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ConwayEntity.values());

        // ViewModel
        var configViewModel = new ConwayConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<ConwayStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, ConwaySimulationManager::new);
        // View
        var configView = new ConwayConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ConwayObservationView(observationViewModel);
        var view = new ConwayMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static WatorMainView createWatorView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(WatorEntityDescribable.values());

        // ViewModel
        var configViewModel = new WatorConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<WatorStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, WatorSimulationManager::new);

        // View
        var configView = new WatorConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new WatorObservationView(observationViewModel);
        var view = new WatorMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

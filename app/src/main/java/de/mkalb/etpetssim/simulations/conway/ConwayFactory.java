package de.mkalb.etpetssim.simulations.conway;

import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.view.ConwayConfigView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayObservationView;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.model.SimulationState;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultObservationViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ConwayFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ConwayFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ConwayEntity.values());

        // ViewModel
        var configViewModel = new ConwayConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<ConwayEntity, ConwayStatistics>(readOnlySimulationState);
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

}

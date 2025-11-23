package de.mkalb.etpetssim.simulations.sugar;

import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarSimulationManager;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntityDescribable;
import de.mkalb.etpetssim.simulations.sugar.view.SugarConfigView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarMainView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarObservationView;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class SugarFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private SugarFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState;
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(SugarEntityDescribable.values());

        // ViewModel
        var configViewModel = new SugarConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel = new DefaultObservationViewModel<SugarEntity, SugarStatistics>(readOnlySimulationState);
        var viewModel = new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel,
                observationViewModel, SugarSimulationManager::new);
        // View
        var configView = new SugarConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new SugarObservationView(observationViewModel);
        var view = new SugarMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

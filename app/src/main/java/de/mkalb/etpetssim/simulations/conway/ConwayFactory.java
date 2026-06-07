package de.mkalb.etpetssim.simulations.conway;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwaySimulationManager;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.view.ConwayConfigView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.conway.view.ConwayObservationView;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayConfigViewModel;
import de.mkalb.etpetssim.simulations.core.model.NoUserAction;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public final class ConwayFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private ConwayFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(ConwayEntity.values());

        // ViewModel
        var configViewModel = new ConwayConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        ConwaySimulationManager::new, ReadableGridModel::getGridCell, new NoUserAction<>());

        // View
        var configView = new ConwayConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new ConwayObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new ConwayMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

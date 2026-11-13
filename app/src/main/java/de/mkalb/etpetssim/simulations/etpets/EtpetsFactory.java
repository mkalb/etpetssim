package de.mkalb.etpetssim.simulations.etpets;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.ControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsCell;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsSimulationManager;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsUserAction;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsConfigView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsMainView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsObservationView;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsConfigViewModel;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsEditToolBarViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * Factory for the ET Pets main view.
 */
public final class EtpetsFactory {

    private EtpetsFactory() {
    }

    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.READY);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.of(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new EtpetsConfigViewModel(readOnlySimulationState);
        var editToolBarViewModel = new EtpetsEditToolBarViewModel();
        var controlViewModel = new ControlViewModel(readOnlySimulationState);
        var observationViewModel = new ObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics>();
        var viewModel =
                new MainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        EtpetsSimulationManager::new, EtpetsCell::from, new EtpetsUserAction());

        // View
        var configView = new EtpetsConfigView(configViewModel);
        var controlView = new ControlView(controlViewModel);
        var observationView = new EtpetsObservationView(observationViewModel, entityDescriptorRegistry);
        return new EtpetsMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView,
                observationView);
    }

}

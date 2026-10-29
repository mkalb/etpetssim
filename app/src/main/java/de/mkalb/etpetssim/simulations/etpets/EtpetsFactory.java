package de.mkalb.etpetssim.simulations.etpets;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
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
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics>(readOnlySimulationState);
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        EtpetsSimulationManager::new, EtpetsCell::from, new EtpetsUserAction());

        // View
        var configView = new EtpetsConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new EtpetsObservationView(observationViewModel, entityDescriptorRegistry);
        return new EtpetsMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}

package de.mkalb.etpetssim.simulations.etpets;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.simulations.etpets.view.*;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.*;
import javafx.beans.property.*;

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

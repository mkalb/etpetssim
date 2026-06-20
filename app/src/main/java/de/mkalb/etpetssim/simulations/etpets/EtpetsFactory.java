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

public final class EtpetsFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private EtpetsFactory() {
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static SimulationMainView createMainView() {
        // Common
        ReadOnlyObjectWrapper<SimulationState> simulationState = new ReadOnlyObjectWrapper<>(SimulationState.INITIAL);
        ReadOnlyObjectProperty<SimulationState> readOnlySimulationState = simulationState.getReadOnlyProperty();
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EntityDescriptors.values());

        // ViewModel
        var configViewModel = new EtpetsConfigViewModel(readOnlySimulationState);
        var controlViewModel = new DefaultControlViewModel(readOnlySimulationState);
        var observationViewModel =
                new DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics>(readOnlySimulationState);
        var editToolBarViewModel = new EtpetsEditToolBarViewModel();
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        EtpetsSimulationManager::new, EtpetsCell::of, new EtpetsUserAction());

        // View
        var configView = new EtpetsConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new EtpetsObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new EtpetsMainView(viewModel, editToolBarViewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}

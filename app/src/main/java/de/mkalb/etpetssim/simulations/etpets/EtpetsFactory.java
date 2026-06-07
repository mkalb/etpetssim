package de.mkalb.etpetssim.simulations.etpets;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.NoUserAction;
import de.mkalb.etpetssim.simulations.core.shared.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsCell;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsSimulationManager;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EntityDescriptors;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsConfigView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsMainView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsObservationView;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsConfigViewModel;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

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
        var viewModel =
                new DefaultMainViewModel<>(simulationState, configViewModel, controlViewModel, observationViewModel,
                        EtpetsSimulationManager::new, EtpetsCell::of, new NoUserAction<>());

        // View
        var configView = new EtpetsConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new EtpetsObservationView(observationViewModel, entityDescriptorRegistry);
        var view = new EtpetsMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);

        // Return the main view
        return view;
    }

}


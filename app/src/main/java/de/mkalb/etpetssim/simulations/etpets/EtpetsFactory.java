package de.mkalb.etpetssim.simulations.etpets;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.SimulationState;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.view.SimulationMainView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultControlViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsSimulationManager;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsConfigView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsMainView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsObservationView;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsConfigViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class EtpetsFactory {

    private EtpetsFactory() {
    }

    public static SimulationMainView createMainView() {
        ObjectProperty<SimulationState> simulationState = new SimpleObjectProperty<>(SimulationState.INITIAL);
        var entityDescriptorRegistry = GridEntityDescriptorRegistry.ofArray(EtpetsEntityDescribable.values());

        var configViewModel = new EtpetsConfigViewModel(simulationState);
        var controlViewModel = DefaultControlViewModel.withMinStepDuration(simulationState);
        var observationViewModel = new DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics>(simulationState);
        var viewModel = new DefaultMainViewModel<>(
                simulationState,
                configViewModel,
                controlViewModel,
                observationViewModel,
                EtpetsSimulationManager::new,
                (model, selectedCoordinate) -> {
                    EtpetsAgentEntity agentEntity = model.agentModel().getEntity(selectedCoordinate);
                    if (!agentEntity.isNone()) {
                        return new GridCell<>(selectedCoordinate, agentEntity);
                    }

                    EtpetsResourceEntity resourceEntity = model.resourceModel().getEntity(selectedCoordinate);
                    if (!resourceEntity.isNone()) {
                        return new GridCell<>(selectedCoordinate, resourceEntity);
                    }

                    return new GridCell<>(selectedCoordinate, model.terrainModel().getEntity(selectedCoordinate));
                });

        var configView = new EtpetsConfigView(configViewModel);
        var controlView = new DefaultControlView(controlViewModel);
        var observationView = new EtpetsObservationView(observationViewModel);

        return new EtpetsMainView(viewModel, entityDescriptorRegistry, configView, controlView, observationView);
    }

}


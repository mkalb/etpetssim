package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import javafx.scene.layout.Region;

import java.util.*;

public final class LabObservationView
        extends AbstractObservationView<
        LabEntity,
        LabStatistics,
        DefaultObservationViewModel<LabEntity, LabStatistics>> {

    public LabObservationView(DefaultObservationViewModel<LabEntity, LabStatistics> viewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<LabStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
    }

    public void initializeForDraw() {
        initializeObservationLabels();
    }

}

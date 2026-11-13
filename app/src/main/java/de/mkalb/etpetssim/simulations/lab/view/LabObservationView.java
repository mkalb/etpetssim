package de.mkalb.etpetssim.simulations.lab.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.lab.model.LabStatistics;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import javafx.scene.layout.Region;

/**
 * Observation view of the Simulation Lab.
 */
public final class LabObservationView
        extends AbstractObservationView<
        LabEntity,
        GridCell<LabEntity>,
        LabStatistics,
        DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics>> {

    public LabObservationView(DefaultObservationViewModel<LabEntity, GridCell<LabEntity>, LabStatistics> viewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(LabStatistics.metrics());
        Region selectedCellSection = createSelectedCellSection();

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                createChartSection(),
                selectedCellSection
        );
    }

}

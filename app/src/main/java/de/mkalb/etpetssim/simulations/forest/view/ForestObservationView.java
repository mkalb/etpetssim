package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import javafx.scene.layout.Region;

import java.util.*;

public final class ForestObservationView
        extends AbstractObservationView<
        ForestEntity,
        GridCell<ForestEntity>,
        ForestStatistics,
        DefaultObservationViewModel<ForestEntity, GridCell<ForestEntity>, ForestStatistics>> {

    public ForestObservationView(DefaultObservationViewModel<ForestEntity, GridCell<ForestEntity>, ForestStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(ForestStatistics.metrics());
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                buildChartSection(),
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ForestStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
        updateGenericMetricSection(statistics, viewModel.getStatisticsExtrema());
    }

}

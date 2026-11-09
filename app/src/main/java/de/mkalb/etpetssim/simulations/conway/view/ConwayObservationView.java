package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.ConwayStatistics;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import javafx.scene.layout.Region;

public final class ConwayObservationView
        extends AbstractObservationView<
        ConwayEntity,
        GridCell<ConwayEntity>,
        ConwayStatistics,
        DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>, ConwayStatistics>> {

    public ConwayObservationView(DefaultObservationViewModel<ConwayEntity, GridCell<ConwayEntity>,
                                         ConwayStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(ConwayStatistics.metrics());
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

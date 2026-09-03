package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsObservationView
        extends AbstractObservationView<
        EtpetsEntity,
        EtpetsCell,
        EtpetsStatistics,
        DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics>> {

    public EtpetsObservationView(DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable EtpetsCell gridCell) {
        super.onSelectedCellChanged(gridCell);
        // TODO EtpetsObservationView: Add and update more labels for selected cell
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(EtpetsStatistics.metrics());
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                createChartSection(),
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<EtpetsStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
        updateGenericMetricSection(statistics, viewModel.getStatisticsExtrema());
    }

}

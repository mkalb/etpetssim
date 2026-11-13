package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsCell;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

/**
 * Observation view of ET Pets.
 */
public final class EtpetsObservationView extends AbstractObservationView<EtpetsEntity, EtpetsCell, EtpetsStatistics> {

    public EtpetsObservationView(ObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void handleSelectedGridCellChanged(@Nullable EtpetsCell gridCell) {
        super.handleSelectedGridCellChanged(gridCell);
        // TODO EtpetsObservationView: Add and update more labels for selected cell
    }

    @Override
    public Region buildObservationRegion() {
        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(EtpetsStatistics.metrics());
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

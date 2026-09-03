package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WatorObservationView
        extends AbstractObservationView<
        WatorEntity,
        GridCell<WatorEntity>,
        WatorStatistics,
        DefaultObservationViewModel<WatorEntity, GridCell<WatorEntity>, WatorStatistics>> {

    private static final String WATOR_OBSERVATION_AGE = "wator.observation.age";

    private final Label ageLabel = new Label();

    public WatorObservationView(DefaultObservationViewModel<WatorEntity, GridCell<WatorEntity>, WatorStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<WatorEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(ageLabel);

        if (gridCell != null) {
            Optional<WatorStatistics> statistics = viewModel.getStatistics();
            if (statistics.isPresent() && (gridCell.entity() instanceof CreatureBase creature)) {
                setFormattedIntegerValue(ageLabel, creature.ageAtStepCount(statistics.get().getStepCount()));
            }
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(WatorStatistics.metrics());
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        WATOR_OBSERVATION_AGE
                },
                new Label[]{
                        ageLabel
                }
        );
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
        Optional<WatorStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
        updateGenericMetricSection(statistics, viewModel.getStatisticsExtrema());
    }

}

package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.rebounding.model.ReboundingStatistics;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ReboundingObservationView
        extends AbstractObservationView<
        ReboundingEntity,
        GridCell<ReboundingEntity>,
        ReboundingStatistics,
        DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics>> {

    private static final String REBOUNDING_OBSERVATION_DIRECTION = "rebounding.observation.direction";

    private final Label directionLabel = new Label();

    public ReboundingObservationView(DefaultObservationViewModel<ReboundingEntity, GridCell<ReboundingEntity>, ReboundingStatistics> viewModel,
                                     GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<ReboundingEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(directionLabel);

        if (gridCell != null) {
            if (gridCell.entity() instanceof Rebounder entity) {
                directionLabel.setText(entity.getDirection().arrow());
            }
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(ReboundingStatistics.metrics());
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        REBOUNDING_OBSERVATION_DIRECTION
                },
                new Label[]{
                        directionLabel
                }
        );
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ReboundingStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
        updateGenericMetricSection(statistics, viewModel.getStatisticsExtrema());
    }

}

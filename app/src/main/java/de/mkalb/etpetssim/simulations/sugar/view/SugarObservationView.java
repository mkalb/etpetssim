package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SugarObservationView
        extends AbstractObservationView<
        SugarEntity,
        SugarCell,
        SugarStatistics,
        DefaultObservationViewModel<SugarEntity, SugarCell, SugarStatistics>> {

    private static final String SUGAR_OBSERVATION_CURRENT_ENERGY = "sugar.observation.currentenergy";
    private static final String SUGAR_OBSERVATION_CURRENT_AMOUNT = "sugar.observation.currentamount";

    private final Label currentEnergyLabel = new Label();
    private final Label currentAmountLabel = new Label();

    public SugarObservationView(DefaultObservationViewModel<SugarEntity, SugarCell, SugarStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable SugarCell gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(
                currentEnergyLabel,
                currentAmountLabel);

        if (gridCell != null) {
            if (gridCell.agentEntity() instanceof Agent agent) {
                setFormattedIntegerValue(currentEnergyLabel, agent.currentEnergy());
            }
            if (gridCell.resourceEntity() instanceof Sugar resource) {
                setFormattedIntegerValue(currentAmountLabel, resource.currentAmount());
            }
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(SugarStatistics.metrics());
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        SUGAR_OBSERVATION_CURRENT_ENERGY,
                        SUGAR_OBSERVATION_CURRENT_AMOUNT
                },
                new Label[]{
                        currentEnergyLabel,
                        currentAmountLabel
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
        Optional<SugarStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);
        updateGenericMetricSection(statistics, viewModel.getStatisticsExtrema());
    }

}

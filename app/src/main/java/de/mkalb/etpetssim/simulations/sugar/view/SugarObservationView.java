package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarCell;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Agent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

/**
 * Observation view of Sugarscape.
 */
public final class SugarObservationView extends AbstractObservationView<SugarEntity, SugarCell, SugarStatistics> {

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
    protected void handleSelectedGridCellChanged(@Nullable SugarCell gridCell) {
        super.handleSelectedGridCellChanged(gridCell);
        setUnknownValues(
                currentEnergyLabel,
                currentAmountLabel);

        if (gridCell != null) {
            if (gridCell.agentEntity() instanceof Agent agent) {
                setFormattedIntegerValue(currentEnergyLabel, agent.currentEnergy());
            }
            if (gridCell.resourceEntity() instanceof Sugar sugar) {
                setFormattedIntegerValue(currentAmountLabel, sugar.currentAmount());
            }
        }
    }

    @Override
    public Region buildObservationRegion() {
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

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                createChartSection(),
                selectedCellSection
        );
    }

}

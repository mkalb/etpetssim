package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarAgent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarResourceSugar;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class SugarObservationView
        extends
        AbstractObservationView<SugarStatistics, DefaultObservationViewModel<SugarEntity, SugarStatistics>> {

    static final String SUGAR_OBSERVATION_TOTAL_CELLS = "sugar.observation.cells.total";
    static final String SUGAR_OBSERVATION_RESOURCE_CELLS = "sugar.observation.cells.resource";
    static final String SUGAR_OBSERVATION_AGENT_CELLS = "sugar.observation.cells.agent";
    static final String SUGAR_OBSERVATION_COORDINATE = "sugar.observation.coordinate";
    static final String SUGAR_OBSERVATION_CURRENT_ENERGY = "sugar.observation.currentenergy";
    static final String SUGAR_OBSERVATION_CURRENT_AMOUNT = "sugar.observation.currentamount";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label resourceCellsLabel = new Label();
    private final Label agentCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label currentEnergyLabel = new Label();
    private final Label currentAmountLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public SugarObservationView(DefaultObservationViewModel<SugarEntity, SugarStatistics> viewModel) {
        super(viewModel);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<SugarEntity> gridCell) {
        if ((gridCell != null)
                && (intFormat != null)
                && !gridCell.entity().isNone()) {
            coordinateLabel.setText(gridCell.coordinate().toDisplayString());
            if (gridCell.entity().isAgent() && (gridCell.entity() instanceof SugarAgent agent)) {
                currentEnergyLabel.setText(intFormat.format(agent.currentEnergy()));
            } else {
                currentEnergyLabel.setText("");
            }
            if (gridCell.entity().isResource()
                    && (gridCell.entity() instanceof SugarResourceSugar resource)) {
                currentAmountLabel.setText(intFormat.format(resource.currentAmount()));
            } else {
                currentAmountLabel.setText("");
            }
        } else {
            coordinateLabel.setText("");
            currentEnergyLabel.setText("");
            currentAmountLabel.setText("");
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                SUGAR_OBSERVATION_TOTAL_CELLS,
                SUGAR_OBSERVATION_RESOURCE_CELLS,
                SUGAR_OBSERVATION_AGENT_CELLS,
                SUGAR_OBSERVATION_COORDINATE,
                SUGAR_OBSERVATION_CURRENT_ENERGY,
                SUGAR_OBSERVATION_CURRENT_AMOUNT
        };
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                resourceCellsLabel,
                agentCellsLabel,
                coordinateLabel,
                currentEnergyLabel,
                currentAmountLabel
        };

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<SugarStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            resourceCellsLabel.setText(intFormat.format(statistics.get().getResourceCells()));
            agentCellsLabel.setText(intFormat.format(statistics.get().getAgentCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            resourceCellsLabel.setText(valueUnknown);
            agentCellsLabel.setText(valueUnknown);
        }
    }

}
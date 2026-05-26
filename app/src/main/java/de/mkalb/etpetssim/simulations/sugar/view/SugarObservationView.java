package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Agent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SugarObservationView
        extends
        AbstractObservationView<SugarStatistics, DefaultObservationViewModel<SugarEntity, SugarStatistics>> {

    private static final String SUGAR_OBSERVATION_TOTAL_CELLS = "sugar.observation.cells.total";
    private static final String SUGAR_OBSERVATION_RESOURCE_CELLS = "sugar.observation.cells.resource";
    private static final String SUGAR_OBSERVATION_AGENT_CELLS = "sugar.observation.cells.agent";
    private static final String SUGAR_OBSERVATION_CURRENT_ENERGY = "sugar.observation.currentenergy";
    private static final String SUGAR_OBSERVATION_CURRENT_AMOUNT = "sugar.observation.currentamount";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label resourceCellsLabel = new Label();
    private final Label agentCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label currentEnergyLabel = new Label();
    private final Label currentAmountLabel = new Label();

    public SugarObservationView(DefaultObservationViewModel<SugarEntity, SugarStatistics> viewModel) {
        super(viewModel);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<SugarEntity> gridCell) {
        if ((gridCell != null)
                && gridCell.entity().isNotEmpty()) {
            coordinateLabel.setText(gridCell.coordinate().toDisplayString());
            if (gridCell.entity().isAgent() && (gridCell.entity() instanceof Agent agent)) {
                setFormattedIntegerValue(currentEnergyLabel, agent.currentEnergy());
            } else {
                clearValues(currentEnergyLabel);
            }
            if (gridCell.entity().isResource()
                    && (gridCell.entity() instanceof Sugar resource)) {
                setFormattedIntegerValue(currentAmountLabel, resource.currentAmount());
            } else {
                clearValues(currentAmountLabel);
            }
        } else {
            clearValues(coordinateLabel, currentEnergyLabel, currentAmountLabel);
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
                AppLocalizationKeys.OBSERVATION_COORDINATE,
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

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<SugarStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            SugarStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(totalCellsLabel, current.getTotalCells());
            setFormattedIntegerValue(resourceCellsLabel, current.getResourceCells());
            setFormattedIntegerValue(agentCellsLabel, current.getAgentCells());
        } else {
            setUnknownValues(stepCountLabel, totalCellsLabel, resourceCellsLabel, agentCellsLabel);
        }
    }

}
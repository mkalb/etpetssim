package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.sugar.model.SugarStatistics;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Agent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SugarObservationView
        extends
        AbstractObservationView<SugarStatistics, DefaultObservationViewModel<SugarEntity, SugarStatistics>> {

    private static final String SUGAR_OBSERVATION_AGENT_CELLS = "sugar.observation.cells.agent";
    private static final String SUGAR_OBSERVATION_CURRENT_AMOUNT = "sugar.observation.currentamount";
    private static final String SUGAR_OBSERVATION_CURRENT_ENERGY = "sugar.observation.currentenergy";
    private static final String SUGAR_OBSERVATION_RESOURCE_CELLS = "sugar.observation.cells.resource";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label resourceCellsLabel = new Label();
    private final Label agentCellsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private final Label currentEnergyLabel = new Label();
    private final Label currentAmountLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public SugarObservationView(DefaultObservationViewModel<SugarEntity, SugarStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<SugarEntity> gridCell) {
        boolean hasValidCell = (gridCell != null) && gridCell.entity().isNotEmpty();
        updateSelectedCellSectionVisibility(selectedCellSection, hasValidCell);

        if ((gridCell != null) && gridCell.entity().isNotEmpty()) {
            updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, gridCell);
            if (gridCell.entity().isAgent() && (gridCell.entity() instanceof Agent agent)) {
                setFormattedIntegerValue(currentEnergyLabel, agent.currentEnergy());
            } else {
                clearValues(currentEnergyLabel);
            }
            if (gridCell.entity().isResource() && (gridCell.entity() instanceof Sugar resource)) {
                setFormattedIntegerValue(currentAmountLabel, resource.currentAmount());
            } else {
                clearValues(currentAmountLabel);
            }
        } else {
            updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, null);
            clearValues(currentEnergyLabel, currentAmountLabel);
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATUS,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_STEP
                },
                new Label[]{
                        stepCountLabel
                }
        );
        Region gridSection = createGridSection(totalCellsLabel);
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        SUGAR_OBSERVATION_RESOURCE_CELLS,
                        SUGAR_OBSERVATION_AGENT_CELLS
                },
                new Label[]{
                        resourceCellsLabel,
                        agentCellsLabel
                }
        );
        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE,
                        SUGAR_OBSERVATION_CURRENT_ENERGY,
                        SUGAR_OBSERVATION_CURRENT_AMOUNT
                },
                new Label[]{
                        coordinateLabel,
                        cellTypeLabel,
                        currentEnergyLabel,
                        currentAmountLabel
                }
        );

        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<SugarStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            SugarStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(resourceCellsLabel, current.getResourceCells());
            setFormattedIntegerValue(agentCellsLabel, current.getAgentCells());
        } else {
            setUnknownValues(stepCountLabel, resourceCellsLabel, agentCellsLabel);
        }
        updateGridSectionLabel(totalCellsLabel);
    }

}
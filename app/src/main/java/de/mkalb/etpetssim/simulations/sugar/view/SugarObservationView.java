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
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SugarObservationView
        extends AbstractObservationView<
        SugarEntity,
        SugarStatistics,
        DefaultObservationViewModel<SugarEntity, SugarStatistics>> {

    private static final String SUGAR_OBSERVATION_RESOURCE_CELLS = "sugar.observation.cells.resource";
    private static final String SUGAR_OBSERVATION_AGENT_CELLS = "sugar.observation.cells.agent";
    private static final String SUGAR_OBSERVATION_CURRENT_ENERGY = "sugar.observation.currentenergy";
    private static final String SUGAR_OBSERVATION_CURRENT_AMOUNT = "sugar.observation.currentamount";

    private final Label resourceCellsLabel = new Label();
    private final Label agentCellsLabel = new Label();
    private final Label currentEnergyLabel = new Label();
    private final Label currentAmountLabel = new Label();

    public SugarObservationView(DefaultObservationViewModel<SugarEntity, SugarStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<SugarEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(
                currentEnergyLabel,
                currentAmountLabel);

        if (gridCell != null) {
            if (gridCell.entity() instanceof Agent agent) {
                setFormattedIntegerValue(currentEnergyLabel, agent.currentEnergy());
            } else if (gridCell.entity() instanceof Sugar resource) {
                setFormattedIntegerValue(currentAmountLabel, resource.currentAmount());
            }
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
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
                currentSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<SugarStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(resourceCellsLabel, current.getResourceCells());
            setFormattedIntegerValue(agentCellsLabel, current.getAgentCells());
        } else {
            setUnknownValues(
                    resourceCellsLabel,
                    agentCellsLabel);
        }
    }

}
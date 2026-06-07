package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class ForestObservationView
        extends AbstractObservationView<
        ForestEntity,
        GridCell<ForestEntity>,
        ForestStatistics,
        DefaultObservationViewModel<ForestEntity, GridCell<ForestEntity>, ForestStatistics>> {

    private static final String FOREST_OBSERVATION_TREE_CELLS = "forest.observation.cells.tree";
    private static final String FOREST_OBSERVATION_BURNING_CELLS = "forest.observation.cells.burning";
    private static final String FOREST_OBSERVATION_MAX_TREE_CELLS = "forest.observation.cells.maxtree";
    private static final String FOREST_OBSERVATION_MAX_BURNING_CELLS = "forest.observation.cells.maxburning";

    private final Label treeCellsLabel = new Label();
    private final Label burningCellsLabel = new Label();
    private final Label maxTreeCellsLabel = new Label();
    private final Label maxBurningCellsLabel = new Label();

    public ForestObservationView(DefaultObservationViewModel<ForestEntity, GridCell<ForestEntity>, ForestStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        FOREST_OBSERVATION_TREE_CELLS,
                        FOREST_OBSERVATION_BURNING_CELLS
                },
                new Label[]{
                        treeCellsLabel,
                        burningCellsLabel
                }
        );
        Region statisticsSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATISTICS,
                new String[]{
                        FOREST_OBSERVATION_MAX_TREE_CELLS,
                        FOREST_OBSERVATION_MAX_BURNING_CELLS
                },
                new Label[]{
                        maxTreeCellsLabel,
                        maxBurningCellsLabel
                }
        );
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                statisticsSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ForestStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(treeCellsLabel, current.getTreeCells());
            setFormattedIntegerValue(burningCellsLabel, current.getBurningCells());
            setFormattedIntegerValue(maxTreeCellsLabel, current.getMaxTreeCells());
            setFormattedIntegerValue(maxBurningCellsLabel, current.getMaxBurningCells());
        } else {
            setUnknownValues(
                    treeCellsLabel,
                    burningCellsLabel,
                    maxTreeCellsLabel,
                    maxBurningCellsLabel);
        }
    }

}
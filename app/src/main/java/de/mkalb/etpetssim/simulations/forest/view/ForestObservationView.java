package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

import java.util.*;

public final class ForestObservationView
        extends AbstractObservationView<ForestStatistics, DefaultObservationViewModel<ForestEntity, ForestStatistics>> {

    private static final String FOREST_OBSERVATION_TOTAL_CELLS = "forest.observation.cells.total";
    private static final String FOREST_OBSERVATION_TREE_CELLS = "forest.observation.cells.tree";
    private static final String FOREST_OBSERVATION_BURNING_CELLS = "forest.observation.cells.burning";
    private static final String FOREST_OBSERVATION_MAX_TREE_CELLS = "forest.observation.cells.maxtree";
    private static final String FOREST_OBSERVATION_MAX_BURNING_CELLS = "forest.observation.cells.maxburning";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label treeCellsLabel = new Label();
    private final Label burningCellsLabel = new Label();
    private final Label maxTreeCellsLabel = new Label();
    private final Label maxBurningCellsLabel = new Label();

    public ForestObservationView(DefaultObservationViewModel<ForestEntity, ForestStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);
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
        Region gridSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_GRID,
                new String[]{
                        FOREST_OBSERVATION_TOTAL_CELLS
                },
                new Label[]{
                        totalCellsLabel
                }
        );
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

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                statisticsSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ForestStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            ForestStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(totalCellsLabel, current.getTotalCells());
            setFormattedIntegerValue(treeCellsLabel, current.getTreeCells());
            setFormattedIntegerValue(burningCellsLabel, current.getBurningCells());
            setFormattedIntegerValue(maxTreeCellsLabel, current.getMaxTreeCells());
            setFormattedIntegerValue(maxBurningCellsLabel, current.getMaxBurningCells());
        } else {
            setUnknownValues(stepCountLabel, totalCellsLabel, treeCellsLabel, burningCellsLabel, maxTreeCellsLabel, maxBurningCellsLabel);
        }
    }

}
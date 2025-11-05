package de.mkalb.etpetssim.simulations.forest.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.forest.model.ForestEntity;
import de.mkalb.etpetssim.simulations.forest.model.ForestStatistics;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class ForestObservationView
        extends AbstractObservationView<ForestStatistics, DefaultObservationViewModel<ForestEntity, ForestStatistics>> {

    static final String FOREST_OBSERVATION_TOTAL_CELLS = "forest.observation.cells.total";
    static final String FOREST_OBSERVATION_TREE_CELLS = "forest.observation.cells.tree";
    static final String FOREST_OBSERVATION_BURNING_CELLS = "forest.observation.cells.burning";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_OBSERVATION_MAX_TREE_CELLS = "forest.observation.cells.maxtree";
    @SuppressWarnings("SpellCheckingInspection")
    static final String FOREST_OBSERVATION_MAX_BURNING_CELLS = "forest.observation.cells.maxburning";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label treeCellsLabel = new Label();
    private final Label burningCellsLabel = new Label();
    private final Label maxTreeCellsLabel = new Label();
    private final Label maxBurningCellsLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public ForestObservationView(DefaultObservationViewModel<ForestEntity, ForestStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                FOREST_OBSERVATION_TOTAL_CELLS,
                FOREST_OBSERVATION_TREE_CELLS,
                FOREST_OBSERVATION_BURNING_CELLS,
                FOREST_OBSERVATION_MAX_TREE_CELLS,
                FOREST_OBSERVATION_MAX_BURNING_CELLS};
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                treeCellsLabel,
                burningCellsLabel,
                maxTreeCellsLabel,
                maxBurningCellsLabel};

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<ForestStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
            treeCellsLabel.setText(intFormat.format(statistics.get().getTreeCells()));
            burningCellsLabel.setText(intFormat.format(statistics.get().getBurningCells()));
            maxTreeCellsLabel.setText(intFormat.format(statistics.get().getMaxTreeCells()));
            maxBurningCellsLabel.setText(intFormat.format(statistics.get().getMaxBurningCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            treeCellsLabel.setText(valueUnknown);
            burningCellsLabel.setText(valueUnknown);
            maxTreeCellsLabel.setText(valueUnknown);
            maxBurningCellsLabel.setText(valueUnknown);
        }
    }

}
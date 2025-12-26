package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class SnakeObservationView
        extends AbstractObservationView<SnakeStatistics, DefaultObservationViewModel<SnakeEntity, SnakeStatistics>> {

    static final String SNAKE_OBSERVATION_TOTAL_CELLS = "snake.observation.cells.total";
    static final String SNAKE_OBSERVATION_COORDINATE = "snake.observation.coordinate";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label coordinateLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public SnakeObservationView(DefaultObservationViewModel<SnakeEntity, SnakeStatistics> viewModel) {
        super(viewModel);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<SnakeEntity> gridCell) {
        Optional<SnakeStatistics> statistics = viewModel.getStatistics();
        if (statistics.isPresent()
                && (gridCell != null)
                && (intFormat != null)) {
            coordinateLabel.setText(gridCell.coordinate().toDisplayString());
        } else {
            coordinateLabel.setText("");
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                SNAKE_OBSERVATION_TOTAL_CELLS,
                SNAKE_OBSERVATION_COORDINATE
        };
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                coordinateLabel
        };

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<SnakeStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            stepCountLabel.setText(intFormat.format(statistics.get().getStepCount()));
            totalCellsLabel.setText(intFormat.format(statistics.get().getTotalCells()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
        }
    }

}
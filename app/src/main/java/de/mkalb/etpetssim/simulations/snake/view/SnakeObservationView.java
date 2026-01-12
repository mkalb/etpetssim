package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class SnakeObservationView
        extends AbstractObservationView<SnakeStatistics, DefaultObservationViewModel<SnakeEntity, SnakeStatistics>> {

    static final String SNAKE_OBSERVATION_TOTAL_CELLS = "snake.observation.cells.total";
    static final String SNAKE_OBSERVATION_SNAKE_HEAD_CELLS = "snake.observation.cells.snakehead";
    static final String SNAKE_OBSERVATION_FOOD_CELLS = "snake.observation.cells.food";
    static final String SNAKE_OBSERVATION_DEATHS = "snake.observation.deaths";
    static final String SNAKE_OBSERVATION_COORDINATE = "snake.observation.coordinate";
    static final String SNAKE_OBSERVATION_SNAKE_ID = "snake.observation.snake.id";
    static final String SNAKE_OBSERVATION_SNAKE_STRATEGY = "snake.observation.snake.strategy";
    static final String SNAKE_OBSERVATION_SNAKE_DEATHS = "snake.observation.snake.deaths";
    static final String SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT = "snake.observation.snake.segmentcount";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label snakeHeadCells = new Label();
    private final Label foodCellsLabel = new Label();
    private final Label deathsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label snakeIdLabel = new Label();
    private final Label snakeStrategyLabel = new Label();
    private final Label snakeDeathsLabel = new Label();
    private final Label snakeSegmentCountLabel = new Label();

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
            if (gridCell.entity() instanceof SnakeHead snakeHead) {
                snakeIdLabel.setText("#" + snakeHead.id());
                snakeStrategyLabel.setText(snakeHead.strategy().name());
                snakeDeathsLabel.setText(intFormat.format(snakeHead.deaths()));
                snakeSegmentCountLabel.setText(intFormat.format(snakeHead.segmentCount()));
            } else {
                snakeIdLabel.setText("");
                snakeStrategyLabel.setText("");
                snakeDeathsLabel.setText("");
                snakeSegmentCountLabel.setText("");
            }
        } else {
            coordinateLabel.setText("");
            snakeIdLabel.setText("");
            snakeStrategyLabel.setText("");
            snakeDeathsLabel.setText("");
            snakeSegmentCountLabel.setText("");
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                SNAKE_OBSERVATION_TOTAL_CELLS,
                SNAKE_OBSERVATION_SNAKE_HEAD_CELLS,
                SNAKE_OBSERVATION_FOOD_CELLS,
                SNAKE_OBSERVATION_DEATHS,
                SNAKE_OBSERVATION_COORDINATE,
                SNAKE_OBSERVATION_SNAKE_ID,
                SNAKE_OBSERVATION_SNAKE_STRATEGY,
                SNAKE_OBSERVATION_SNAKE_DEATHS,
                SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT
        };
        Label[] valueLabels = {
                stepCountLabel,
                totalCellsLabel,
                snakeHeadCells,
                foodCellsLabel,
                deathsLabel,
                coordinateLabel,
                snakeIdLabel,
                snakeStrategyLabel,
                snakeDeathsLabel,
                snakeSegmentCountLabel
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
            snakeHeadCells.setText(intFormat.format(statistics.get().getSnakeHeadCells()));
            foodCellsLabel.setText(intFormat.format(statistics.get().getFoodCells()));
            deathsLabel.setText(intFormat.format(statistics.get().getDeaths()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            totalCellsLabel.setText(valueUnknown);
            snakeHeadCells.setText(valueUnknown);
            foodCellsLabel.setText(valueUnknown);
            deathsLabel.setText(valueUnknown);
        }
    }

}
package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeObservationView
        extends AbstractObservationView<
        SnakeEntity,
        SnakeStatistics,
        DefaultObservationViewModel<SnakeEntity, SnakeStatistics>> {

    private static final String SNAKE_OBSERVATION_SNAKE_HEAD_CELLS = "snake.observation.cells.snakehead";
    private static final String SNAKE_OBSERVATION_FOOD_CELLS = "snake.observation.cells.food";
    private static final String SNAKE_OBSERVATION_DEATHS = "snake.observation.deaths";
    private static final String SNAKE_OBSERVATION_SNAKE_ID = "snake.observation.snake.id";
    private static final String SNAKE_OBSERVATION_SNAKE_STRATEGY = "snake.observation.snake.strategy";
    private static final String SNAKE_OBSERVATION_SNAKE_DEATHS = "snake.observation.snake.deaths";
    private static final String SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT = "snake.observation.snake.segmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT = "snake.observation.snake.maxsegmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_POINTS = "snake.observation.snake.points";

    private final Label snakeHeadCellsLabel = new Label();
    private final Label foodCellsLabel = new Label();
    private final Label deathsLabel = new Label();
    private final Label snakeIdLabel = new Label();
    private final Label snakeStrategyLabel = new Label();
    private final Label snakeDeathsLabel = new Label();
    private final Label snakeSegmentCountLabel = new Label();
    private final Label snakeMaxSegmentCountLabel = new Label();
    private final Label snakePointsLabel = new Label();
    // TODO SnakeObservationView: Add age for selected SnakeHead

    public SnakeObservationView(DefaultObservationViewModel<SnakeEntity, SnakeStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<SnakeEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        setUnknownValues(
                snakeIdLabel,
                snakeStrategyLabel,
                snakeDeathsLabel,
                snakeSegmentCountLabel,
                snakeMaxSegmentCountLabel,
                snakePointsLabel);

        if (gridCell != null) {
            if (gridCell.entity() instanceof SnakeHead snakeHead) {
                snakeIdLabel.setText("#" + snakeHead.id());
                snakeStrategyLabel.setText(snakeHead.strategy().name());
                setFormattedIntegerValue(snakeDeathsLabel, snakeHead.deaths());
                setFormattedIntegerValue(snakeSegmentCountLabel, snakeHead.segmentCount());
                setFormattedIntegerValue(snakeMaxSegmentCountLabel, snakeHead.maxSegmentCount());
                setFormattedIntegerValue(snakePointsLabel, snakeHead.points());
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
                        SNAKE_OBSERVATION_SNAKE_HEAD_CELLS,
                        SNAKE_OBSERVATION_FOOD_CELLS,
                        SNAKE_OBSERVATION_DEATHS
                },
                new Label[]{
                        snakeHeadCellsLabel,
                        foodCellsLabel,
                        deathsLabel
                }
        );
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        SNAKE_OBSERVATION_SNAKE_ID,
                        SNAKE_OBSERVATION_SNAKE_STRATEGY,
                        SNAKE_OBSERVATION_SNAKE_DEATHS,
                        SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_POINTS
                },
                new Label[]{
                        snakeIdLabel,
                        snakeStrategyLabel,
                        snakeDeathsLabel,
                        snakeSegmentCountLabel,
                        snakeMaxSegmentCountLabel,
                        snakePointsLabel
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
        Optional<SnakeStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(snakeHeadCellsLabel, current.getSnakeHeadCells());
            setFormattedIntegerValue(foodCellsLabel, current.getFoodCells());
            setFormattedIntegerValue(deathsLabel, current.getDeaths());
        } else {
            setUnknownValues(
                    snakeHeadCellsLabel,
                    foodCellsLabel,
                    deathsLabel);
        }
    }

}
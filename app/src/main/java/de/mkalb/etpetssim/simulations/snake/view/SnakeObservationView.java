package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.ObservationViewModel;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * Observation view of Snake.
 */
public final class SnakeObservationView
        extends AbstractObservationView<SnakeEntity, GridCell<SnakeEntity>, SnakeStatistics> {

    private static final String SNAKE_OBSERVATION_SNAKE_ID = "snake.observation.snake.id";
    private static final String SNAKE_OBSERVATION_SNAKE_AGE = "snake.observation.snake.age";
    private static final String SNAKE_OBSERVATION_SNAKE_DEATHS = "snake.observation.snake.deaths";
    private static final String SNAKE_OBSERVATION_SNAKE_STRATEGY = "snake.observation.snake.strategy";
    private static final String SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT = "snake.observation.snake.segmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT = "snake.observation.snake.maxsegmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_POINTS = "snake.observation.snake.points";

    private final Label snakeIdLabel = new Label();
    private final Label snakeAgeLabel = new Label();
    private final Label snakeDeathsLabel = new Label();
    private final Label snakeStrategyLabel = new Label();
    private final Label snakeSegmentCountLabel = new Label();
    private final Label snakeMaxSegmentCountLabel = new Label();
    private final Label snakePointsLabel = new Label();

    public SnakeObservationView(ObservationViewModel<SnakeEntity, GridCell<SnakeEntity>,
                                        SnakeStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void handleSelectedGridCellChanged(@Nullable GridCell<SnakeEntity> gridCell) {
        super.handleSelectedGridCellChanged(gridCell);
        setUnknownValues(
                snakeIdLabel,
                snakeAgeLabel,
                snakeDeathsLabel,
                snakeStrategyLabel,
                snakeSegmentCountLabel,
                snakeMaxSegmentCountLabel,
                snakePointsLabel);

        Optional<SnakeStatistics> statistics = viewModel.getStatistics();
        if ((gridCell != null) && statistics.isPresent() && (gridCell.entity() instanceof SnakeHead snakeHead)) {
            snakeIdLabel.setText("#" + snakeHead.id());
            setFormattedIntegerValue(snakeAgeLabel, snakeHead.ageAtStepCount(statistics.get().stepCount()));
            setFormattedIntegerValue(snakeDeathsLabel, snakeHead.deaths());
            snakeStrategyLabel.setText(snakeHead.strategy().displayName());
            setFormattedIntegerValue(snakeSegmentCountLabel, snakeHead.segmentCount());
            setFormattedIntegerValue(snakeMaxSegmentCountLabel, snakeHead.maxSegmentCount());
            setFormattedIntegerValue(snakePointsLabel, snakeHead.points());
        }
    }

    @Override
    public Region buildObservationRegion() {
        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region metricsSection = createGenericMetricSection(SnakeStatistics.metrics());
        Region selectedCellSection = createExtendedSelectedCellSection(
                new String[]{
                        SNAKE_OBSERVATION_SNAKE_ID,
                        SNAKE_OBSERVATION_SNAKE_AGE,
                        SNAKE_OBSERVATION_SNAKE_DEATHS,
                        SNAKE_OBSERVATION_SNAKE_STRATEGY,
                        SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_POINTS
                },
                new Label[]{
                        snakeIdLabel,
                        snakeAgeLabel,
                        snakeDeathsLabel,
                        snakeStrategyLabel,
                        snakeSegmentCountLabel,
                        snakeMaxSegmentCountLabel,
                        snakePointsLabel
                }
        );

        return createObservationScrollPane(
                statusSection,
                gridSection,
                metricsSection,
                createChartSection(),
                selectedCellSection
        );
    }

}

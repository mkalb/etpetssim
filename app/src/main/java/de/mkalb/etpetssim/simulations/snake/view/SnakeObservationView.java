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
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeObservationView
        extends AbstractObservationView<SnakeStatistics, DefaultObservationViewModel<SnakeEntity, SnakeStatistics>> {

    private static final String SNAKE_OBSERVATION_DEATHS = "snake.observation.deaths";
    private static final String SNAKE_OBSERVATION_FOOD_CELLS = "snake.observation.cells.food";
    private static final String SNAKE_OBSERVATION_SNAKE_DEATHS = "snake.observation.snake.deaths";
    private static final String SNAKE_OBSERVATION_SNAKE_ID = "snake.observation.snake.id";
    private static final String SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT = "snake.observation.snake.maxsegmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_POINTS = "snake.observation.snake.points";
    private static final String SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT = "snake.observation.snake.segmentcount";
    private static final String SNAKE_OBSERVATION_SNAKE_STRATEGY = "snake.observation.snake.strategy";
    private static final String SNAKE_OBSERVATION_SNAKE_HEAD_CELLS = "snake.observation.cells.snakehead";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label snakeHeadCells = new Label();
    private final Label foodCellsLabel = new Label();
    private final Label deathsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private final Label snakeIdLabel = new Label();
    private final Label snakeStrategyLabel = new Label();
    private final Label snakeDeathsLabel = new Label();
    private final Label snakeSegmentCountLabel = new Label();
    private final Label snakeMaxSegmentCountLabel = new Label();
    private final Label snakePointsLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public SnakeObservationView(DefaultObservationViewModel<SnakeEntity, SnakeStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<SnakeEntity> gridCell) {
        updateSelectedCellSectionVisibility(gridCell != null);

        if (gridCell != null) {
            coordinateLabel.setText(gridCell.coordinate().toDisplayString());
            cellTypeLabel.setText(localizedShortCellTypeName(gridCell.entity()));
            if (gridCell.entity() instanceof SnakeHead snakeHead) {
                snakeIdLabel.setText("#" + snakeHead.id());
                snakeStrategyLabel.setText(snakeHead.strategy().name());
                setFormattedIntegerValue(snakeDeathsLabel, snakeHead.deaths());
                setFormattedIntegerValue(snakeSegmentCountLabel, snakeHead.segmentCount());
                setFormattedIntegerValue(snakeMaxSegmentCountLabel, snakeHead.maxSegmentCount());
                setFormattedIntegerValue(snakePointsLabel, snakeHead.points());
            } else {
                clearValues(snakeIdLabel, snakeStrategyLabel, snakeDeathsLabel,
                        snakeSegmentCountLabel, snakeMaxSegmentCountLabel, snakePointsLabel);
            }
        } else {
            clearValues(coordinateLabel, cellTypeLabel, snakeIdLabel, snakeStrategyLabel, snakeDeathsLabel,
                    snakeSegmentCountLabel, snakeMaxSegmentCountLabel, snakePointsLabel);
        }
    }

    private void updateSelectedCellSectionVisibility(boolean visible) {
        if (selectedCellSection != null) {
            selectedCellSection.setManaged(visible);
            selectedCellSection.setVisible(visible);
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
                        SNAKE_OBSERVATION_SNAKE_HEAD_CELLS,
                        SNAKE_OBSERVATION_FOOD_CELLS,
                        SNAKE_OBSERVATION_DEATHS
                },
                new Label[]{
                        snakeHeadCells,
                        foodCellsLabel,
                        deathsLabel
                }
        );
        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE,
                        SNAKE_OBSERVATION_SNAKE_ID,
                        SNAKE_OBSERVATION_SNAKE_STRATEGY,
                        SNAKE_OBSERVATION_SNAKE_DEATHS,
                        SNAKE_OBSERVATION_SNAKE_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_MAX_SEGMENT_COUNT,
                        SNAKE_OBSERVATION_SNAKE_POINTS
                },
                new Label[]{
                        coordinateLabel,
                        cellTypeLabel,
                        snakeIdLabel,
                        snakeStrategyLabel,
                        snakeDeathsLabel,
                        snakeSegmentCountLabel,
                        snakeMaxSegmentCountLabel,
                        snakePointsLabel
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
        Optional<SnakeStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            SnakeStatistics current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(snakeHeadCells, current.getSnakeHeadCells());
            setFormattedIntegerValue(foodCellsLabel, current.getFoodCells());
            setFormattedIntegerValue(deathsLabel, current.getDeaths());
        } else {
            setUnknownValues(stepCountLabel, snakeHeadCells, foodCellsLabel, deathsLabel);
        }
        updateGridSectionLabel(totalCellsLabel);
    }

}
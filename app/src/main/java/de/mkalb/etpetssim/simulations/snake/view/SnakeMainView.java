package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.snake.model.*;
import de.mkalb.etpetssim.simulations.snake.model.entity.*;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeMainView
        extends AbstractDefaultMainView<
        SnakeEntity,
        GridCell<SnakeEntity>,
        WritableGridModel<SnakeEntity>,
        SnakeConfig,
        SnakeStatistics,
        SnakeSimulationManager,
        SnakeUserActionContext,
        SnakeConfigView,
        SnakeObservationView> {

    private static final String SNAKE_TOOL_ID_ADD_WALL = "snake.addWall";
    private static final String SNAKE_TOOL_ID_REMOVE_WALL = "snake.removeWall";
    private static final String SNAKE_TOOL_ID_ADD_FOOD = "snake.addFood";
    private static final String SNAKE_TOOL_ID_REMOVE_FOOD = "snake.removeFood";
    private static final String SNAKE_TOOLBAR_ADD_WALL = "snake.toolbar.addwall";
    private static final String SNAKE_TOOLBAR_ADD_WALL_TOOLTIP = "snake.toolbar.addwall.tooltip";
    private static final String SNAKE_TOOLBAR_REMOVE_WALL = "snake.toolbar.removewall";
    private static final String SNAKE_TOOLBAR_REMOVE_WALL_TOOLTIP = "snake.toolbar.removewall.tooltip";
    private static final String SNAKE_TOOLBAR_ADD_FOOD = "snake.toolbar.addfood";
    private static final String SNAKE_TOOLBAR_ADD_FOOD_TOOLTIP = "snake.toolbar.addfood.tooltip";
    private static final String SNAKE_TOOLBAR_REMOVE_FOOD = "snake.toolbar.removefood";
    private static final String SNAKE_TOOLBAR_REMOVE_FOOD_TOOLTIP = "snake.toolbar.removefood.tooltip";

    // Entity dead colors are derived from the alive colors using two warm dead hues.
    private static final double DEAD_HEAD_HUE = 4.0d;
    private static final double DEAD_SEGMENT_HUE = 22.0d;
    private static final double DEAD_SATURATION = 0.45d;
    private static final double DEAD_FILL_BRIGHTNESS_FACTOR = 0.75d;
    private static final double DEAD_BORDER_BRIGHTNESS_FACTOR = 0.55d;

    // Entity stroke widths
    private static final double WALL_STROKE_LINE_WIDTH = 0.5d;
    private static final double FOOD_STROKE_LINE_WIDTH = 2.0d;
    private static final double SNAKE_SEGMENT_STROKE_LINE_WIDTH = 1.0d;
    private static final double SNAKE_HEAD_STROKE_LINE_WIDTH = 2.0d;
    private static final double ALIVE_SELECTED_WHITE_BLEND_FACTOR = 0.3d;

    private static final Color SELECTED_STROKE_COLOR = Color.PINK;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    public SnakeMainView(DefaultMainViewModel<SnakeEntity, GridCell<SnakeEntity>, WritableGridModel<SnakeEntity>, SnakeConfig, SnakeStatistics, SnakeSimulationManager, SnakeUserActionContext> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         SnakeConfigView configView,
                         DefaultControlView controlView,
                         SnakeObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    private static Color toSelectedColor(Color aliveColor) {
        return aliveColor.interpolate(Color.WHITE, ALIVE_SELECTED_WHITE_BLEND_FACTOR);
    }

    private static Color toDeadFillColor(Color aliveColor, double deadHue) {
        return toDeadColor(aliveColor, deadHue, DEAD_FILL_BRIGHTNESS_FACTOR);
    }

    private static Color toDeadBorderColor(Color aliveColor, double deadHue) {
        return toDeadColor(aliveColor, deadHue, DEAD_BORDER_BRIGHTNESS_FACTOR);
    }

    private static Color toDeadColor(Color aliveColor, double deadHue, double brightnessFactor) {
        return Color.hsb(deadHue, DEAD_SATURATION, aliveColor.getBrightness() * brightnessFactor);
    }

    @Override
    protected void initSimulation(SnakeConfig config, CellDimension cellDimension, WritableGridModel<SnakeEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<SnakeEntity> oldGridCell,
                                          @Nullable GridCell<SnakeEntity> newGridCell) {
        if (oldGridCell != null) {
            painter.clearCanvasBackground();
        }
        if (newGridCell != null) {
            painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                    SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                    StrokeType.OUTSIDE);
            if (newGridCell.entity() instanceof SnakeHead head) {
                for (GridCoordinate coordinate : head.currentSegments()) {
                    painter.drawCell(coordinate, null,
                            SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH);
                }
            }
        }
    }

    @Override
    protected void drawSimulation(WritableGridModel<SnakeEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        var wallDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_WALL);
        var growthFoodDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD);
        var snakeSegmentDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_SNAKE_SEGMENT);
        var snakeHeadDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD);

        // Alive colors are defined in EntityDescriptors.
        Color segmentAliveColor = Objects.requireNonNull(snakeSegmentDescriptor.color());
        Color segmentAliveBorderColor = Objects.requireNonNull(snakeSegmentDescriptor.borderColor());
        Color headAliveColor = Objects.requireNonNull(snakeHeadDescriptor.color());
        Color headAliveBorderColor = Objects.requireNonNull(snakeHeadDescriptor.borderColor());

        // Alive+selected colors are computed as a white-blend of the alive colors.
        Color segmentAliveSelectedColor = toSelectedColor(segmentAliveColor);
        Color segmentAliveSelectedBorderColor = toSelectedColor(segmentAliveBorderColor);
        Color headAliveSelectedColor = toSelectedColor(headAliveColor);
        Color headAliveSelectedBorderColor = toSelectedColor(headAliveBorderColor);

        Color segmentDeadColor = toDeadFillColor(segmentAliveColor, DEAD_SEGMENT_HUE);
        Color segmentDeadBorderColor = toDeadBorderColor(segmentAliveBorderColor, DEAD_SEGMENT_HUE);
        Color headDeadColor = toDeadFillColor(headAliveColor, DEAD_HEAD_HUE);
        Color headDeadBorderColor = toDeadBorderColor(headAliveBorderColor, DEAD_HEAD_HUE);

        currentModel.filteredCoordinates(SnakeEntity::isWall)
                    .forEach(coordinate ->
                            dynamicPainter.drawCell(coordinate, wallDescriptor.color(), wallDescriptor.borderColor(), WALL_STROKE_LINE_WIDTH));
        currentModel.filteredCoordinates(SnakeEntity::isFood)
                    .forEach(coordinate ->
                            dynamicPainter.drawCellInnerCircle(coordinate, growthFoodDescriptor.color(), growthFoodDescriptor.borderColor(), FOOD_STROKE_LINE_WIDTH, StrokeType.INSIDE));
        currentModel.filteredCells(e -> e instanceof SnakeHead)
                    .forEach(cell -> {
                        if (cell.entity() instanceof SnakeHead head) {
                            boolean isDead = head.isDead();
                            // Check if last selected entity is not null and is the same instance as the current head
                            boolean isSelected = isSelected(head);
                            Color snakeHeadColor;
                            Color snakeHeadColorBorder;
                            Color snakeSegmentColor;
                            Color snakeSegmentColorBorder;
                            if (isDead) {
                                snakeHeadColor = headDeadColor;
                                snakeHeadColorBorder = headDeadBorderColor;
                                snakeSegmentColor = segmentDeadColor;
                                snakeSegmentColorBorder = segmentDeadBorderColor;
                            } else if (isSelected) {
                                snakeHeadColor = headAliveSelectedColor;
                                snakeHeadColorBorder = headAliveSelectedBorderColor;
                                snakeSegmentColor = segmentAliveSelectedColor;
                                snakeSegmentColorBorder = segmentAliveSelectedBorderColor;
                            } else {
                                snakeHeadColor = headAliveColor;
                                snakeHeadColorBorder = headAliveBorderColor;
                                snakeSegmentColor = segmentAliveColor;
                                snakeSegmentColorBorder = segmentAliveBorderColor;
                            }
                            for (GridCoordinate coordinate : head.currentSegments()) {
                                dynamicPainter.drawCell(coordinate, snakeSegmentColor, snakeSegmentColorBorder, SNAKE_SEGMENT_STROKE_LINE_WIDTH);
                            }
                            dynamicPainter.drawCellInnerCircle(cell.coordinate(), snakeHeadColor, snakeHeadColorBorder, SNAKE_HEAD_STROKE_LINE_WIDTH,
                                    StrokeType.OUTSIDE);
                        }
                    });
    }

    @Override
    protected List<SimulationUserActionDescriptor<SnakeUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_ADD_WALL,
                        SnakeUserActionContext.ADD_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_ADD_WALL,
                        SNAKE_TOOLBAR_ADD_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_REMOVE_WALL,
                        SnakeUserActionContext.REMOVE_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_REMOVE_WALL,
                        SNAKE_TOOLBAR_REMOVE_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_ADD_FOOD,
                        SnakeUserActionContext.ADD_FOOD,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_ADD_FOOD,
                        SNAKE_TOOLBAR_ADD_FOOD_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_REMOVE_FOOD,
                        SnakeUserActionContext.REMOVE_FOOD,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_REMOVE_FOOD,
                        SNAKE_TOOLBAR_REMOVE_FOOD_TOOLTIP));
    }

    private boolean isSelected(SnakeHead head) {
        return Objects.equals(head, viewModel.lastSelectedEntityProperty().getValue());
    }

}

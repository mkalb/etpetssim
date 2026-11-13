package de.mkalb.etpetssim.simulations.snake.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.snake.model.SnakeConfig;
import de.mkalb.etpetssim.simulations.snake.model.SnakeSimulationManager;
import de.mkalb.etpetssim.simulations.snake.model.SnakeStatistics;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeEntity;
import de.mkalb.etpetssim.simulations.snake.model.entity.SnakeHead;
import de.mkalb.etpetssim.simulations.snake.shared.SnakeUserActionContext;
import de.mkalb.etpetssim.simulations.snake.viewmodel.SnakeEditToolBarViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Main view of Snake.
 */
public final class SnakeMainView
        extends AbstractMainView<
        SnakeEntity,
        GridCell<SnakeEntity>,
        WritableGridModel<SnakeEntity>,
        SnakeConfig,
        SnakeStatistics,
        SnakeSimulationManager,
        SnakeUserActionContext,
        SnakeObservationView> {

    private static final String SNAKE_TOOLBAR_ADD_FOOD = "snake.toolbar.addfood";
    private static final String SNAKE_TOOLBAR_ADD_FOOD_TOOLTIP = "snake.toolbar.addfood.tooltip";
    private static final String SNAKE_TOOLBAR_ADD_SNAKE = "snake.toolbar.addsnake";
    private static final String SNAKE_TOOLBAR_ADD_SNAKE_STRATEGY = "snake.toolbar.addsnake.strategy";
    private static final String SNAKE_TOOLBAR_ADD_SNAKE_STRATEGY_TOOLTIP = "snake.toolbar.addsnake.strategy.tooltip";
    private static final String SNAKE_TOOLBAR_ADD_SNAKE_TOOLTIP = "snake.toolbar.addsnake.tooltip";
    private static final String SNAKE_TOOLBAR_ADD_WALL = "snake.toolbar.addwall";
    private static final String SNAKE_TOOLBAR_ADD_WALL_TOOLTIP = "snake.toolbar.addwall.tooltip";
    private static final String SNAKE_TOOLBAR_REMOVE_FOOD = "snake.toolbar.removefood";
    private static final String SNAKE_TOOLBAR_REMOVE_FOOD_TOOLTIP = "snake.toolbar.removefood.tooltip";
    private static final String SNAKE_TOOLBAR_REMOVE_SNAKE = "snake.toolbar.removesnake";
    private static final String SNAKE_TOOLBAR_REMOVE_SNAKE_TOOLTIP = "snake.toolbar.removesnake.tooltip";
    private static final String SNAKE_TOOLBAR_REMOVE_WALL = "snake.toolbar.removewall";
    private static final String SNAKE_TOOLBAR_REMOVE_WALL_TOOLTIP = "snake.toolbar.removewall.tooltip";
    private static final String SNAKE_TOOL_ID_ADD_FOOD = "snake.addFood";
    private static final String SNAKE_TOOL_ID_ADD_SNAKE = "snake.addSnake";
    private static final String SNAKE_TOOL_ID_ADD_WALL = "snake.addWall";
    private static final String SNAKE_TOOL_ID_REMOVE_FOOD = "snake.removeFood";
    private static final String SNAKE_TOOL_ID_REMOVE_SNAKE = "snake.removeSnake";
    private static final String SNAKE_TOOL_ID_REMOVE_WALL = "snake.removeWall";

    // Entity dead colors are derived from the alive colors using two warm dead hues.
    private static final double DEAD_HEAD_HUE = 4.0d;
    private static final double DEAD_SEGMENT_HUE = 22.0d;
    private static final double DEAD_SATURATION = 0.45d;
    private static final double DEAD_FILL_BRIGHTNESS_FACTOR = 0.75d;
    private static final double DEAD_BORDER_BRIGHTNESS_FACTOR = 0.55d;

    // Entity selected colors are derived from the alive colors as a white blend.
    private static final double ALIVE_SELECTED_WHITE_BLEND_FACTOR = 0.3d;

    // Entity stroke widths
    private static final double WALL_STROKE_LINE_WIDTH = 0.5d;
    private static final double FOOD_STROKE_LINE_WIDTH = 2.0d;
    private static final double SNAKE_SEGMENT_STROKE_LINE_WIDTH = 1.0d;
    private static final double SNAKE_HEAD_STROKE_LINE_WIDTH = 2.0d;

    private static final Color SELECTED_STROKE_COLOR = Color.PINK;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;

    private final SnakeEditToolBarViewModel editToolBarViewModel;
    private final SnakeColors aliveColors;
    private final SnakeColors selectedColors;
    private final SnakeColors deadColors;

    public SnakeMainView(DefaultMainViewModel<SnakeEntity, GridCell<SnakeEntity>, WritableGridModel<SnakeEntity>,
                                 SnakeConfig, SnakeStatistics, SnakeSimulationManager,
                                 SnakeUserActionContext> viewModel,
                         SnakeEditToolBarViewModel editToolBarViewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         SnakeConfigView configView,
                         DefaultControlView controlView,
                         SnakeObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;

        // Alive colors are defined in EntityDescriptors.
        var headDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD);
        var segmentDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_SNAKE_SEGMENT);
        aliveColors = new SnakeColors(
                Objects.requireNonNull(headDescriptor.color()),
                Objects.requireNonNull(headDescriptor.borderColor()),
                Objects.requireNonNull(segmentDescriptor.color()),
                Objects.requireNonNull(segmentDescriptor.borderColor()));
        selectedColors = toSelectedColors(aliveColors);
        deadColors = toDeadColors(aliveColors);
    }

    private static SnakeColors toSelectedColors(SnakeColors aliveColors) {
        return new SnakeColors(
                toSelectedColor(aliveColors.head()),
                toSelectedColor(aliveColors.headBorder()),
                toSelectedColor(aliveColors.segment()),
                toSelectedColor(aliveColors.segmentBorder()));
    }

    private static Color toSelectedColor(Color aliveColor) {
        return aliveColor.interpolate(Color.WHITE, ALIVE_SELECTED_WHITE_BLEND_FACTOR);
    }

    private static SnakeColors toDeadColors(SnakeColors aliveColors) {
        return new SnakeColors(
                toDeadColor(aliveColors.head(), DEAD_HEAD_HUE, DEAD_FILL_BRIGHTNESS_FACTOR),
                toDeadColor(aliveColors.headBorder(), DEAD_HEAD_HUE, DEAD_BORDER_BRIGHTNESS_FACTOR),
                toDeadColor(aliveColors.segment(), DEAD_SEGMENT_HUE, DEAD_FILL_BRIGHTNESS_FACTOR),
                toDeadColor(aliveColors.segmentBorder(), DEAD_SEGMENT_HUE, DEAD_BORDER_BRIGHTNESS_FACTOR));
    }

    private static Color toDeadColor(Color aliveColor, double deadHue, double brightnessFactor) {
        return Color.hsb(deadHue, DEAD_SATURATION, aliveColor.getBrightness() * brightnessFactor);
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, SnakeConfig config, CellDimension cellDimension) {
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        painters.base().fillCanvasBackground(backgroundColor);
    }

    @Override
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
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
    protected void drawSimulation(CanvasPainters painters,
                                  WritableGridModel<SnakeEntity> currentGridModel,
                                  int stepCount,
                                  int lastDrawnStepCount) {
        painters.dynamic().clearCanvasBackground();

        var wallDescriptor = entityDescriptorRegistry.requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_WALL);
        var growthFoodDescriptor = entityDescriptorRegistry
                .requireByDescriptorId(SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD);

        currentGridModel.filteredCoordinates(SnakeEntity::isWall)
                .forEach(coordinate ->
                        painters.dynamic().drawCell(coordinate, wallDescriptor.color(), wallDescriptor.borderColor(),
                                WALL_STROKE_LINE_WIDTH));
        currentGridModel.filteredCoordinates(SnakeEntity::isFood)
                .forEach(coordinate ->
                        painters.dynamic().drawCellInnerCircle(coordinate, growthFoodDescriptor.color(),
                                growthFoodDescriptor.borderColor(), FOOD_STROKE_LINE_WIDTH, StrokeType.INSIDE));
        for (var cell : currentGridModel.filteredCells(SnakeEntity::isAgent)) {
            if (cell.entity() instanceof SnakeHead head) {
                drawSnake(painters.dynamic(), cell.coordinate(), head);
            }
        }
    }

    @Override
    protected List<SimulationUserActionDescriptor<SnakeUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_ADD_WALL,
                        SnakeUserActionContext.FixedAction.ADD_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_ADD_WALL,
                        SNAKE_TOOLBAR_ADD_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_REMOVE_WALL,
                        SnakeUserActionContext.FixedAction.REMOVE_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_REMOVE_WALL,
                        SNAKE_TOOLBAR_REMOVE_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_ADD_FOOD,
                        SnakeUserActionContext.FixedAction.ADD_FOOD,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_ADD_FOOD,
                        SNAKE_TOOLBAR_ADD_FOOD_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_REMOVE_FOOD,
                        SnakeUserActionContext.FixedAction.REMOVE_FOOD,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_REMOVE_FOOD,
                        SNAKE_TOOLBAR_REMOVE_FOOD_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_REMOVE_SNAKE,
                        SnakeUserActionContext.FixedAction.REMOVE_SNAKE,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_REMOVE_SNAKE,
                        SNAKE_TOOLBAR_REMOVE_SNAKE_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SNAKE_TOOL_ID_ADD_SNAKE,
                        SimulationUserActionScope.CELL_SELECTED,
                        SNAKE_TOOLBAR_ADD_SNAKE,
                        SNAKE_TOOLBAR_ADD_SNAKE_TOOLTIP,
                        () -> Optional.of(editToolBarViewModel.getSelectedStrategy())
                                .map(SnakeUserActionContext.AddSnake::new)
                                .map(Function.identity())));
    }

    @Override
    protected Node createEditToolBarOptionPanel(ObjectProperty<String> selectedToolId) {
        var strategyControl = FXComponentFactory.createLabeledChoiceComboBox(
                editToolBarViewModel.selectedStrategyProperty(),
                AppLocalization.getText(SNAKE_TOOLBAR_ADD_SNAKE_STRATEGY),
                AppLocalization.getText(SNAKE_TOOLBAR_ADD_SNAKE_STRATEGY_TOOLTIP),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX,
                this::registerEditToolBarCleanup);
        Label strategyLabel = strategyControl.label();
        strategyLabel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        var strategyComboBox = strategyControl.controlRegion();

        var disableWhenNoAddSnakeToolSelected = Bindings.not(selectedToolId.isEqualTo(SNAKE_TOOL_ID_ADD_SNAKE));
        strategyLabel.disableProperty().bind(disableWhenNoAddSnakeToolSelected);
        strategyComboBox.disableProperty().bind(disableWhenNoAddSnakeToolSelected);
        registerEditToolBarCleanup(() -> {
            strategyLabel.disableProperty().unbind();
            strategyComboBox.disableProperty().unbind();
        });

        HBox optionPanel = new HBox(strategyLabel, strategyComboBox);
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private void drawSnake(FXGridCanvasPainter painter, GridCoordinate headCoordinate, SnakeHead head) {
        SnakeColors colors = resolveSnakeColors(head);
        for (GridCoordinate coordinate : head.currentSegments()) {
            painter.drawCell(coordinate, colors.segment(), colors.segmentBorder(), SNAKE_SEGMENT_STROKE_LINE_WIDTH);
        }
        painter.drawCellInnerCircle(headCoordinate, colors.head(), colors.headBorder(), SNAKE_HEAD_STROKE_LINE_WIDTH,
                StrokeType.OUTSIDE);
    }

    private SnakeColors resolveSnakeColors(SnakeHead head) {
        if (head.isDead()) {
            return deadColors;
        }
        return isSelected(head) ? selectedColors : aliveColors;
    }

    private boolean isSelected(SnakeHead head) {
        return Objects.equals(head, viewModel.lastSelectedEntityProperty().getValue());
    }

    private record SnakeColors(
            Color head,
            Color headBorder,
            Color segment,
            Color segmentBorder) {
    }

}

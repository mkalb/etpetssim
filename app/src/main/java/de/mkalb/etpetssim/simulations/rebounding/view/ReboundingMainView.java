package de.mkalb.etpetssim.simulations.rebounding.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.rebounding.model.*;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import de.mkalb.etpetssim.simulations.rebounding.viewmodel.ReboundingEditToolBarViewModel;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class ReboundingMainView
        extends AbstractDefaultMainView<
        ReboundingEntity,
        GridCell<ReboundingEntity>,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingSimulationManager,
        ReboundingUserActionContext,
        ReboundingConfigView,
        ReboundingObservationView> {

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;
    private static final String REBOUNDING_TOOL_ID_ADD_REBOUNDER = "rebounding.addRebounder";
    private static final String REBOUNDING_TOOL_ID_ADD_WALL = "rebounding.addWall";
    private static final String REBOUNDING_TOOL_ID_REMOVE_REBOUNDER = "rebounding.removeRebounder";
    private static final String REBOUNDING_TOOL_ID_REMOVE_WALL = "rebounding.removeWall";
    private static final String REBOUNDING_TOOLBAR_ADD_REBOUNDER = "rebounding.toolbar.addrebounder";
    private static final String REBOUNDING_TOOLBAR_ADD_REBOUNDER_DIRECTION = "rebounding.toolbar.addrebounder.direction";
    private static final String REBOUNDING_TOOLBAR_ADD_REBOUNDER_DIRECTION_TOOLTIP = "rebounding.toolbar.addrebounder.direction.tooltip";
    private static final String REBOUNDING_TOOLBAR_ADD_REBOUNDER_TOOLTIP = "rebounding.toolbar.addrebounder.tooltip";
    private static final String REBOUNDING_TOOLBAR_ADD_WALL = "rebounding.toolbar.addwall";
    private static final String REBOUNDING_TOOLBAR_ADD_WALL_TOOLTIP = "rebounding.toolbar.addwall.tooltip";
    private static final String REBOUNDING_TOOLBAR_REMOVE_REBOUNDER = "rebounding.toolbar.removerebounder";
    private static final String REBOUNDING_TOOLBAR_REMOVE_REBOUNDER_TOOLTIP = "rebounding.toolbar.removerebounder.tooltip";
    private static final String REBOUNDING_TOOLBAR_REMOVE_WALL = "rebounding.toolbar.removewall";
    private static final String REBOUNDING_TOOLBAR_REMOVE_WALL_TOOLTIP = "rebounding.toolbar.removewall.tooltip";

    private final ReboundingEditToolBarViewModel editToolBarViewModel;
    private @Nullable CellDrawer<ReboundingEntity> cellDrawer;

    public ReboundingMainView(DefaultMainViewModel<ReboundingEntity, GridCell<ReboundingEntity>, WritableGridModel<ReboundingEntity>, ReboundingConfig, ReboundingStatistics, ReboundingSimulationManager, ReboundingUserActionContext> viewModel,
                              ReboundingEditToolBarViewModel editToolBarViewModel,
                              GridEntityDescriptorRegistry entityDescriptorRegistry,
                              ReboundingConfigView configView,
                              DefaultControlView controlView,
                              ReboundingObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
    }

    @Override
    protected void initSimulation(ReboundingConfig config, CellDimension cellDimension, WritableGridModel<ReboundingEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ReboundingEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        editToolBarViewModel.updateAvailableDirections(config);
        basePainter.fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.colorOrFallback(),
                            null,
                            NO_STROKE_LINE_WIDTH);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.colorOrFallback(),
                            backgroundColor,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE, CellDisplayMode.CIRCLE_BORDERED, CellDisplayMode.EMOJI ->
                    throw new IllegalArgumentException("CellDisplayMode not supported: " + config.cellDisplayMode());
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ReboundingEntity> oldGridCell,
                                          @Nullable GridCell<ReboundingEntity> newGridCell) {
        if (oldGridCell != null) {
            painter.clearCanvasBackground();
        }
        if (newGridCell != null) {
            painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                    SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                    StrokeType.OUTSIDE);
        }
    }

    @Override
    protected void drawSimulation(WritableGridModel<ReboundingEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        currentModel.nonDefaultCells()
                    .forEach(cell -> cellDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                            dynamicPainter, cell, stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<ReboundingUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        REBOUNDING_TOOL_ID_ADD_WALL,
                        ReboundingUserActionContext.FixedAction.ADD_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        REBOUNDING_TOOLBAR_ADD_WALL,
                        REBOUNDING_TOOLBAR_ADD_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        REBOUNDING_TOOL_ID_REMOVE_WALL,
                        ReboundingUserActionContext.FixedAction.REMOVE_WALL,
                        SimulationUserActionScope.CELL_SELECTED,
                        REBOUNDING_TOOLBAR_REMOVE_WALL,
                        REBOUNDING_TOOLBAR_REMOVE_WALL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        REBOUNDING_TOOL_ID_REMOVE_REBOUNDER,
                        ReboundingUserActionContext.FixedAction.REMOVE_REBOUNDER,
                        SimulationUserActionScope.CELL_SELECTED,
                        REBOUNDING_TOOLBAR_REMOVE_REBOUNDER,
                        REBOUNDING_TOOLBAR_REMOVE_REBOUNDER_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        REBOUNDING_TOOL_ID_ADD_REBOUNDER,
                        SimulationUserActionScope.CELL_SELECTED,
                        REBOUNDING_TOOLBAR_ADD_REBOUNDER,
                        REBOUNDING_TOOLBAR_ADD_REBOUNDER_TOOLTIP,
                        () -> editToolBarViewModel.resolveSelectedAddRebounderContext()
                                                  .map(Function.identity())));
    }

    @Override
    protected Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        ObservableList<CompassDirection> availableDirections = editToolBarViewModel.availableDirections();
        ComboBox<CompassDirection> directionComboBox = new ComboBox<>();
        directionComboBox.setItems(availableDirections);
        directionComboBox.setMaxWidth(Double.MAX_VALUE);
        directionComboBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX);
        directionComboBox.setCellFactory(_ -> createDirectionCell());
        directionComboBox.setButtonCell(createDirectionCell());
        CompassDirection selectedDirection = editToolBarViewModel.getSelectedDirection();
        if (selectedDirection != null) {
            directionComboBox.setValue(selectedDirection);
        }

        Label directionLabel = FXComponentFactory.createLabel(
                AppLocalization.getText(REBOUNDING_TOOLBAR_ADD_REBOUNDER_DIRECTION),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        directionLabel.setLabelFor(directionComboBox);
        directionLabel.setTooltip(new Tooltip(AppLocalization.getText(REBOUNDING_TOOLBAR_ADD_REBOUNDER_DIRECTION_TOOLTIP)));
        directionComboBox.setTooltip(new Tooltip(AppLocalization.getText(REBOUNDING_TOOLBAR_ADD_REBOUNDER_DIRECTION_TOOLTIP)));

        ChangeListener<@Nullable CompassDirection> comboSelectionListener = (_, oldValue, newValue) -> {
            if (oldValue != newValue) {
                editToolBarViewModel.setSelectedDirection(newValue);
            }
        };
        ChangeListener<@Nullable CompassDirection> viewModelSelectionListener = (_, _, newValue) -> {
            if (directionComboBox.getValue() != newValue) {
                if (newValue == null) {
                    directionComboBox.getSelectionModel().clearSelection();
                } else {
                    directionComboBox.setValue(newValue);
                }
            }
        };
        var selectedDirectionProperty = editToolBarViewModel.selectedDirectionProperty();
        directionComboBox.valueProperty().addListener(comboSelectionListener);
        selectedDirectionProperty.addListener(viewModelSelectionListener);

        if (selectedToolId != null) {
            var enabledBinding = Bindings.createBooleanBinding(
                    () -> REBOUNDING_TOOL_ID_ADD_REBOUNDER.equals(selectedToolId.get()) && !availableDirections.isEmpty(),
                    selectedToolId,
                    availableDirections);
            directionLabel.disableProperty().bind(enabledBinding.not());
            directionComboBox.disableProperty().bind(enabledBinding.not());
            registerActionToolBarCleanup(() -> {
                directionLabel.disableProperty().unbind();
                directionComboBox.disableProperty().unbind();
                enabledBinding.dispose();
                directionComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedDirectionProperty.removeListener(viewModelSelectionListener);
            });
        } else {
            registerActionToolBarCleanup(() -> {
                directionComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedDirectionProperty.removeListener(viewModelSelectionListener);
            });
        }

        HBox optionPanel = new HBox(directionLabel, directionComboBox);
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private ListCell<CompassDirection> createDirectionCell() {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable CompassDirection item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || (item == null)) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(AppLocalization.getText(item.nameResourceKey()));
                }
            }
        };
    }

}

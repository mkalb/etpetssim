package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.langton.model.*;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonEditToolBarViewModel;
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

public final class LangtonMainView
        extends AbstractDefaultMainView<
        LangtonEntity,
        LangtonCell,
        LangtonGridModel,
        LangtonConfig,
        LangtonStatistics,
        LangtonSimulationManager,
        LangtonUserActionContext,
        LangtonConfigView,
        LangtonObservationView> {

    private static final String LANGTON_TOOL_ID_ADD_ANT = "langton.addAnt";
    private static final String LANGTON_TOOLBAR_ADD_ANT_DIRECTION = "langton.toolbar.addant.direction";
    private static final String LANGTON_TOOLBAR_ADD_ANT_DIRECTION_TOOLTIP = "langton.toolbar.addant.direction.tooltip";
    private static final String LANGTON_TOOLBAR_ADD_ANT = "langton.toolbar.addant";
    private static final String LANGTON_TOOLBAR_ADD_ANT_TOOLTIP = "langton.toolbar.addant.tooltip";
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;

    private static final Color SELECTED_STROKE_COLOR = Color.RED;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final LangtonEditToolBarViewModel editToolBarViewModel;
    private @Nullable CellDrawer<TerrainConstant> cellGroundDrawer;
    private @Nullable CellDrawer<AntEntity> cellAntDrawer;
    private @Nullable LangtonConfig currentConfig;

    public LangtonMainView(DefaultMainViewModel<LangtonEntity, LangtonCell, LangtonGridModel, LangtonConfig, LangtonStatistics, LangtonSimulationManager, LangtonUserActionContext> viewModel,
                           LangtonEditToolBarViewModel editToolBarViewModel,
                           GridEntityDescriptorRegistry entityDescriptorRegistry,
                           LangtonConfigView configView,
                           DefaultControlView controlView,
                           LangtonObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
    }

    @SuppressWarnings("MagicNumber")
    @Override
    protected void initSimulation(LangtonConfig config, CellDimension cellDimension, LangtonGridModel model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        currentConfig = config;
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(TerrainConstant.UNVISITED.descriptorId())
                .colorOrFallback();
        editToolBarViewModel.updateAvailableDirections(config);
        basePainter.fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellGroundDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            null,
                            NO_STROKE_LINE_WIDTH);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, _) ->
                    painter.drawCell(
                            cell.coordinate(),
                            descriptor.color(),
                            backgroundColor,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE, CellDisplayMode.CIRCLE_BORDERED, CellDisplayMode.EMOJI ->
                    throw new IllegalArgumentException("CellDisplayMode not supported: " + config.cellDisplayMode());
        };
        cellAntDrawer = (descriptor, painter, cell, _) -> {
            if ((cellEmojiFont != null)
                    && (cellDimension.innerRadius() > 5.0d)
                    && (descriptor.borderColor() != null)
                    && (cell.entity() instanceof Ant ant)) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), descriptor.borderColor(), 1.0d, StrokeType.INSIDE);
                painter.drawCenteredTextInCell(cell.coordinate(), ant.direction().arrow(), descriptor.borderColor(), cellEmojiFont);
            } else if (cellDimension.innerRadius() > 2.0d) {
                painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH, StrokeType.INSIDE);
            } else {
                painter.drawCell(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH);
            }
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable LangtonCell oldGridCell,
                                          @Nullable LangtonCell newGridCell) {
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
    protected void drawSimulation(LangtonGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if ((cellGroundDrawer == null) || (cellAntDrawer == null)) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        var groundModel = currentModel.groundModel();
        var antModel = currentModel.antModel();

        if ((lastDrawnStepCount + 1) < stepCount) {
            // draw ground
            groundModel.nonDefaultCells()
                       .forEach(groundCell -> cellGroundDrawer.draw(
                               entityDescriptorRegistry.requireByDescriptorId(groundCell.descriptorId()),
                               basePainter, groundCell, stepCount));
            // draw ant
            antModel.nonDefaultCells()
                    .forEach(antCell -> cellAntDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                            dynamicPainter, antCell, stepCount));
        } else {
            antModel.nonDefaultCells()
                    .forEach(antCell -> {
                        GridCell<TerrainConstant> groundCell = groundModel.getGridCell(antCell.coordinate());
                        // draw ground
                        cellGroundDrawer.draw(
                                entityDescriptorRegistry.requireByDescriptorId(groundCell.descriptorId()),
                                basePainter, groundCell, stepCount);
                        // draw ant
                        cellAntDrawer.draw(
                                entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                                dynamicPainter, antCell, stepCount);
                    });
        }
    }

    @Override
    protected List<SimulationUserActionDescriptor<LangtonUserActionContext>> createUserActionDescriptors() {
        return List.of(new SimulationUserActionDescriptor<>(
                LANGTON_TOOL_ID_ADD_ANT,
                SimulationUserActionScope.CELL_SELECTED,
                LANGTON_TOOLBAR_ADD_ANT,
                LANGTON_TOOLBAR_ADD_ANT_TOOLTIP,
                () -> resolveSelectedAddAntContext().map(Function.identity())));
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
                AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_DIRECTION),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        directionLabel.setLabelFor(directionComboBox);
        directionLabel.setTooltip(new Tooltip(AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_DIRECTION_TOOLTIP)));
        directionComboBox.setTooltip(new Tooltip(AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_DIRECTION_TOOLTIP)));

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
                    () -> LANGTON_TOOL_ID_ADD_ANT.equals(selectedToolId.get()),
                    selectedToolId,
                    availableDirections);
            directionLabel.disableProperty().bind(enabledBinding.not());
            directionComboBox.disableProperty().bind(enabledBinding.not());
            directionLabel.visibleProperty().bind(Bindings.isNotEmpty(availableDirections));
            directionLabel.managedProperty().bind(directionLabel.visibleProperty());
            directionComboBox.visibleProperty().bind(Bindings.isNotEmpty(availableDirections));
            directionComboBox.managedProperty().bind(directionComboBox.visibleProperty());
            registerActionToolBarCleanup(() -> {
                directionLabel.disableProperty().unbind();
                directionComboBox.disableProperty().unbind();
                directionLabel.visibleProperty().unbind();
                directionLabel.managedProperty().unbind();
                directionComboBox.visibleProperty().unbind();
                directionComboBox.managedProperty().unbind();
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

    private Optional<LangtonUserActionContext.AddAnt> resolveSelectedAddAntContext() {
        LangtonConfig config = currentConfig;
        if (config == null) {
            return Optional.empty();
        }
        return editToolBarViewModel.resolveSelectedAddAntContext(config, selectedCoordinate());
    }

    private @Nullable GridCoordinate selectedCoordinate() {
        LangtonCell selectedCell = viewModel.selectedGridCellProperty().get();
        return (selectedCell == null) ? null : selectedCell.coordinate();
    }

}

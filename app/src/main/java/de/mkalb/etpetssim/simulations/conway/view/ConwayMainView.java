package de.mkalb.etpetssim.simulations.conway.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.conway.model.*;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import de.mkalb.etpetssim.simulations.conway.viewmodel.ConwayEditToolBarViewModel;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.ui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.*;

public final class ConwayMainView
        extends AbstractDefaultMainView<
        ConwayEntity,
        GridCell<ConwayEntity>,
        WritableGridModel<ConwayEntity>,
        ConwayConfig,
        ConwayStatistics,
        ConwaySimulationManager,
        ConwayUserActionContext,
        ConwayConfigView,
        ConwayObservationView> {

    private static final Color SELECTED_STROKE_COLOR = Color.BLACK;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;
    private static final String CONWAY_TOOL_ID_PLACE_PATTERN = "conway.placePattern";
    private static final String CONWAY_TOOL_ID_TOGGLE_CELL = "conway.toggleCell";
    private static final String CONWAY_TOOLBAR_PATTERN = "conway.toolbar.pattern";
    private static final String CONWAY_TOOLBAR_PATTERN_TOOLTIP = "conway.toolbar.pattern.tooltip";
    private static final String CONWAY_TOOLBAR_PLACE_PATTERN = "conway.toolbar.placepattern";
    private static final String CONWAY_TOOLBAR_PLACE_PATTERN_TOOLTIP = "conway.toolbar.placepattern.tooltip";
    private static final String CONWAY_TOOLBAR_TOGGLE_CELL = "conway.toolbar.togglecell";
    private static final String CONWAY_TOOLBAR_TOGGLE_CELL_TOOLTIP = "conway.toolbar.togglecell.tooltip";

    private final ConwayEditToolBarViewModel editToolBarViewModel;
    private @Nullable CoordinateDrawer coordinateDrawer;

    public ConwayMainView(DefaultMainViewModel<ConwayEntity, GridCell<ConwayEntity>, WritableGridModel<ConwayEntity>, ConwayConfig, ConwayStatistics, ConwaySimulationManager, ConwayUserActionContext> viewModel,
                          ConwayEditToolBarViewModel editToolBarViewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          ConwayConfigView configView,
                          DefaultControlView controlView,
                          ConwayObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
    }

    @Override
    protected void initSimulation(ConwayConfig config, CellDimension cellDimension, WritableGridModel<ConwayEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        editToolBarViewModel.updateAvailablePatternChoices(config);
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(ConwayEntity.DEAD.descriptorId())
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        var descriptor = entityDescriptorRegistry.requireByDescriptorId(ConwayEntity.ALIVE.descriptorId());
        var aliveColor = descriptor.colorOrFallback();
        var aliveBorderColor = descriptor.borderColorOrFallback();

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        coordinateDrawer = switch (config.cellDisplayMode()) {
            case SHAPE -> (painter, coordinate, _) ->
                    painter.drawCell(
                            coordinate,
                            aliveColor,
                            null,
                            NO_STROKE_LINE_WIDTH);
            case SHAPE_BORDERED -> (painter, coordinate, _) ->
                    painter.drawCell(
                            coordinate,
                            aliveColor,
                            aliveBorderColor,
                            strokeLineWidth);
            case CIRCLE -> (painter, coordinate, _) ->
                    painter.drawCellInnerCircle(
                            coordinate,
                            aliveColor,
                            null,
                            NO_STROKE_LINE_WIDTH,
                            StrokeType.INSIDE);
            case CIRCLE_BORDERED -> (painter, coordinate, _) ->
                    painter.drawCellInnerCircle(
                            coordinate,
                            aliveColor,
                            aliveBorderColor,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case EMOJI -> throw new IllegalArgumentException("CellDisplayMode not supported!");
        };
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<ConwayEntity> oldGridCell,
                                          @Nullable GridCell<ConwayEntity> newGridCell) {
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
    protected void drawSimulation(WritableGridModel<ConwayEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (coordinateDrawer == null) {
            AppLogger.warn("CoordinateDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        currentModel.nonDefaultCoordinates()
                    .forEach(coordinate -> coordinateDrawer.draw(
                            dynamicPainter, coordinate, stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<ConwayUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        CONWAY_TOOL_ID_TOGGLE_CELL,
                        ConwayUserActionContext.FixedAction.TOGGLE_CELL,
                        SimulationUserActionScope.CELL_SELECTED,
                        CONWAY_TOOLBAR_TOGGLE_CELL,
                        CONWAY_TOOLBAR_TOGGLE_CELL_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        CONWAY_TOOL_ID_PLACE_PATTERN,
                        SimulationUserActionScope.CELL_SELECTED,
                        CONWAY_TOOLBAR_PLACE_PATTERN,
                        CONWAY_TOOLBAR_PLACE_PATTERN_TOOLTIP,
                        () -> editToolBarViewModel.resolveSelectedPatternContext()
                                                  .map(Function.identity())));
    }

    @Override
    protected Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        ObservableList<ConwayPatternChoice> availablePatternChoices = editToolBarViewModel.availablePatternChoices();
        ComboBox<ConwayPatternChoice> patternComboBox = new ComboBox<>();
        patternComboBox.setItems(availablePatternChoices);
        patternComboBox.setMaxWidth(Double.MAX_VALUE);
        patternComboBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX);
        patternComboBox.setCellFactory(_ -> createPatternChoiceCell());
        patternComboBox.setButtonCell(createPatternChoiceCell());
        ConwayPatternChoice selectedPatternChoice = editToolBarViewModel.getSelectedPatternChoice();
        if (selectedPatternChoice != null) {
            patternComboBox.setValue(selectedPatternChoice);
        }

        Label patternLabel = FXComponentFactory.createLabel(
                AppLocalization.getText(CONWAY_TOOLBAR_PATTERN),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        patternLabel.setLabelFor(patternComboBox);
        patternLabel.setTooltip(new Tooltip(AppLocalization.getText(CONWAY_TOOLBAR_PATTERN_TOOLTIP)));
        patternComboBox.setTooltip(new Tooltip(AppLocalization.getText(CONWAY_TOOLBAR_PATTERN_TOOLTIP)));

        ChangeListener<@Nullable ConwayPatternChoice> comboSelectionListener = (_, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                editToolBarViewModel.setSelectedPatternChoice(newValue);
            }
        };
        ChangeListener<@Nullable ConwayPatternChoice> viewModelSelectionListener = (_, _, newValue) -> {
            if (!Objects.equals(patternComboBox.getValue(), newValue)) {
                if (newValue == null) {
                    patternComboBox.getSelectionModel().clearSelection();
                } else {
                    patternComboBox.setValue(newValue);
                }
            }
        };
        var selectedPatternChoiceProperty = editToolBarViewModel.selectedPatternChoiceProperty();
        patternComboBox.valueProperty().addListener(comboSelectionListener);
        selectedPatternChoiceProperty.addListener(viewModelSelectionListener);

        if (selectedToolId != null) {
            var enabledBinding = Bindings.createBooleanBinding(
                    () -> CONWAY_TOOL_ID_PLACE_PATTERN.equals(selectedToolId.get()) && !availablePatternChoices.isEmpty(),
                    selectedToolId,
                    availablePatternChoices);
            patternLabel.disableProperty().bind(enabledBinding.not());
            patternComboBox.disableProperty().bind(enabledBinding.not());
            registerActionToolBarCleanup(() -> {
                patternLabel.disableProperty().unbind();
                patternComboBox.disableProperty().unbind();
                enabledBinding.dispose();
                patternComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedPatternChoiceProperty.removeListener(viewModelSelectionListener);
            });
        } else {
            registerActionToolBarCleanup(() -> {
                patternComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedPatternChoiceProperty.removeListener(viewModelSelectionListener);
            });
        }

        HBox optionPanel = new HBox(patternLabel, patternComboBox);
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setAlignment(Pos.CENTER_LEFT);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);

        return optionPanel;
    }

    private ListCell<ConwayPatternChoice> createPatternChoiceCell() {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable ConwayPatternChoice item, boolean empty) {
                // Follow the JavaFX Cell#updateItem(...) contract: call super first and clear the cell for empty or null items.
                super.updateItem(item, empty);
                if (empty || (item == null)) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(AppLocalization.getText(item.labelKey()));
                }
            }
        };
    }

}


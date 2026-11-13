package de.mkalb.etpetssim.simulations.langton.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.langton.model.LangtonCell;
import de.mkalb.etpetssim.simulations.langton.model.LangtonConfig;
import de.mkalb.etpetssim.simulations.langton.model.LangtonGridModel;
import de.mkalb.etpetssim.simulations.langton.model.LangtonSimulationManager;
import de.mkalb.etpetssim.simulations.langton.model.LangtonStatistics;
import de.mkalb.etpetssim.simulations.langton.model.entity.Ant;
import de.mkalb.etpetssim.simulations.langton.model.entity.AntEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.LangtonEntity;
import de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.langton.shared.LangtonUserActionContext;
import de.mkalb.etpetssim.simulations.langton.viewmodel.LangtonEditToolBarViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Main view of Langton's Ant.
 */
public final class LangtonMainView
        extends AbstractMainView<
        LangtonEntity,
        LangtonCell,
        LangtonGridModel,
        LangtonConfig,
        LangtonStatistics,
        LangtonSimulationManager,
        LangtonUserActionContext,
        LangtonObservationView> {

    private static final String LANGTON_TOOL_ID_ADD_ANT = "langton.addAnt";
    private static final String LANGTON_TOOL_ID_REMOVE_ANT = "langton.removeAnt";
    private static final String LANGTON_TOOLBAR_ADD_ANT_DIRECTION = "langton.toolbar.addant.direction";
    private static final String LANGTON_TOOLBAR_ADD_ANT_DIRECTION_TOOLTIP = "langton.toolbar.addant.direction.tooltip";
    private static final String LANGTON_TOOLBAR_ADD_ANT = "langton.toolbar.addant";
    private static final String LANGTON_TOOLBAR_ADD_ANT_TOOLTIP = "langton.toolbar.addant.tooltip";
    private static final String LANGTON_TOOLBAR_REMOVE_ANT = "langton.toolbar.removeant";
    private static final String LANGTON_TOOLBAR_REMOVE_ANT_TOOLTIP = "langton.toolbar.removeant.tooltip";
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;

    private static final Color SELECTED_STROKE_COLOR = Color.RED;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final LangtonEditToolBarViewModel editToolBarViewModel;
    private @Nullable CellDrawer<TerrainConstant> cellTerrainDrawer;
    private @Nullable CellDrawer<AntEntity> cellAntDrawer;
    private @Nullable LangtonConfig currentConfig;

    public LangtonMainView(MainViewModel<LangtonEntity, LangtonCell, LangtonGridModel, LangtonConfig,
                                   LangtonStatistics, LangtonSimulationManager, LangtonUserActionContext> viewModel,
                           LangtonEditToolBarViewModel editToolBarViewModel,
                           GridEntityDescriptorRegistry entityDescriptorRegistry,
                           LangtonConfigView configView,
                           ControlView controlView,
                           LangtonObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, LangtonConfig config, CellDimension cellDimension) {
        currentConfig = config;
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(TerrainConstant.UNVISITED.descriptorId())
                .colorOrFallback();
        editToolBarViewModel.updateAvailableDirections(config);
        painters.base().fillCanvasBackground(backgroundColor);

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellTerrainDrawer = switch (config.cellDisplayMode()) {
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
                    throw new IllegalArgumentException("cellDisplayMode must be SHAPE or SHAPE_BORDERED, but was: "
                            + config.cellDisplayMode());
        };
        cellAntDrawer = (descriptor, painter, cell, _) -> drawAnt(descriptor, painter, cell, cellDimension);
    }

    @Override
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
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
    protected void drawSimulation(CanvasPainters painters,
                                  LangtonGridModel currentGridModel,
                                  int stepCount,
                                  int lastDrawnStepCount) {
        if ((cellTerrainDrawer == null) || (cellAntDrawer == null)) {
            AppLogger.warn("LangtonMainView: Skipping canvas draw: cell drawers are not initialized");
            return;
        }

        painters.dynamic().clearCanvasBackground();

        var terrainModel = currentGridModel.terrainModel();
        var antModel = currentGridModel.antModel();

        if ((lastDrawnStepCount + 1) < stepCount) {
            terrainModel.nonDefaultCells()
                    .forEach(terrainCell -> cellTerrainDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(terrainCell.descriptorId()),
                            painters.base(), terrainCell, stepCount));
            antModel.nonDefaultCells()
                    .forEach(antCell -> cellAntDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                            painters.dynamic(), antCell, stepCount));
        } else {
            for (GridCell<AntEntity> antCell : antModel.nonDefaultCells()) {
                GridCell<TerrainConstant> terrainCell = terrainModel.getGridCell(antCell.coordinate());
                cellTerrainDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(terrainCell.descriptorId()),
                        painters.base(), terrainCell, stepCount);
                cellAntDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(antCell.descriptorId()),
                        painters.dynamic(), antCell, stepCount);
            }
        }
    }

    @Override
    protected List<SimulationUserActionDescriptor<LangtonUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        LANGTON_TOOL_ID_REMOVE_ANT,
                        LangtonUserActionContext.FixedAction.REMOVE_ANT,
                        SimulationUserActionScope.CELL_SELECTED,
                        LANGTON_TOOLBAR_REMOVE_ANT,
                        LANGTON_TOOLBAR_REMOVE_ANT_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        LANGTON_TOOL_ID_ADD_ANT,
                        SimulationUserActionScope.CELL_SELECTED,
                        LANGTON_TOOLBAR_ADD_ANT,
                        LANGTON_TOOLBAR_ADD_ANT_TOOLTIP,
                        () -> resolveSelectedAddAntContext().map(Function.identity())));
    }

    @Override
    protected Node createEditToolBarOptionPanel(ObjectProperty<String> selectedToolId) {
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

        Label directionLabel = FXComponentFactory.createLabeledControl(
                directionComboBox,
                AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_DIRECTION),
                AppLocalization.getText(LANGTON_TOOLBAR_ADD_ANT_DIRECTION_TOOLTIP),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL).label();

        ChangeListener<@Nullable CompassDirection> comboSelectionListener =
                (_, _, newValue) -> editToolBarViewModel.setSelectedDirection(newValue);
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
        registerEditToolBarCleanup(() -> {
            directionComboBox.valueProperty().removeListener(comboSelectionListener);
            selectedDirectionProperty.removeListener(viewModelSelectionListener);
        });

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
        registerEditToolBarCleanup(() -> {
            directionLabel.disableProperty().unbind();
            directionComboBox.disableProperty().unbind();
            directionLabel.visibleProperty().unbind();
            directionLabel.managedProperty().unbind();
            directionComboBox.visibleProperty().unbind();
            directionComboBox.managedProperty().unbind();
            enabledBinding.dispose();
        });

        HBox optionPanel = new HBox(directionLabel, directionComboBox);
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private void drawAnt(GridEntityDescriptor descriptor,
                         FXGridCanvasPainter painter,
                         GridCell<AntEntity> cell,
                         CellDimension cellDimension) {
        if ((cellEmojiFont != null)
                && (cellDimension.innerRadius() > 5.0d)
                && (descriptor.borderColor() != null)
                && (cell.entity() instanceof Ant ant)) {
            painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), descriptor.borderColor(), 1.0d,
                    StrokeType.INSIDE);
            painter.drawCenteredTextInCell(cell.coordinate(), ant.direction().arrow(), descriptor.borderColor(),
                    cellEmojiFont);
        } else if (cellDimension.innerRadius() > 2.0d) {
            painter.drawCellInnerCircle(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH,
                    StrokeType.INSIDE);
        } else {
            painter.drawCell(cell.coordinate(), descriptor.color(), null, NO_STROKE_LINE_WIDTH);
        }
    }

    private ListCell<CompassDirection> createDirectionCell() {
        return FXComponentFactory.createTextListCell(direction -> AppLocalization.getText(direction.nameResourceKey()));
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

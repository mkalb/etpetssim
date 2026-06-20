package de.mkalb.etpetssim.simulations.sugar.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.entity.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.sugar.model.*;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import de.mkalb.etpetssim.simulations.sugar.shared.*;
import de.mkalb.etpetssim.simulations.sugar.viewmodel.SugarEditToolBarViewModel;
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

public final class SugarMainView
        extends AbstractDefaultMainView<
        SugarEntity,
        SugarCell,
        SugarGridModel,
        SugarConfig,
        SugarStatistics,
        SugarSimulationManager,
        SugarUserActionContext,
        SugarConfigView,
        SugarObservationView> {

    private static final Color FALLBACK_COLOR_SUGAR = Color.WHITE;
    private static final Color FALLBACK_COLOR_AGENT = Color.BLUE;
    private static final double SUGAR_MAX_FACTOR_DELTA = 0.3d;
    private static final double AGENT_MAX_FACTOR_DELTA = 0.6d;
    private static final int AGENT_GROUP_COUNT = 7;
    private static final int MAX_COLOR_AGENT_ENERGY_FACTOR = 2;
    private static final Color SELECTED_STROKE_COLOR = Color.rgb(255, 120, 120);
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;
    private static final String SUGAR_TOOLBAR_ADD_SUGAR = "sugar.toolbar.addsugar";
    private static final String SUGAR_TOOLBAR_ADD_SUGAR_LEVEL = "sugar.toolbar.addsugar.level";
    private static final String SUGAR_TOOLBAR_ADD_SUGAR_LEVEL_TOOLTIP = "sugar.toolbar.addsugar.level.tooltip";
    private static final String SUGAR_TOOLBAR_ADD_SUGAR_TOOLTIP = "sugar.toolbar.addsugar.tooltip";
    private static final String SUGAR_TOOLBAR_REMOVE_SUGAR = "sugar.toolbar.removesugar";
    private static final String SUGAR_TOOLBAR_REMOVE_SUGAR_TOOLTIP = "sugar.toolbar.removesugar.tooltip";
    private static final String SUGAR_TOOL_ID_ADD_SUGAR = "sugar.addSugar";
    private static final String SUGAR_TOOL_ID_REMOVE_SUGAR = "sugar.removeSugar";

    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private final SugarEditToolBarViewModel editToolBarViewModel;
    private @Nullable CellDrawer<ResourceEntity> cellResourceDrawer;
    private @Nullable CellDrawer<AgentEntity> cellAgentDrawer;
    private int maxColorAgentEnergy = 1;

    public SugarMainView(DefaultMainViewModel<SugarEntity, SugarCell, SugarGridModel, SugarConfig, SugarStatistics, SugarSimulationManager, SugarUserActionContext> viewModel,
                         SugarEditToolBarViewModel editToolBarViewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         SugarConfigView configView,
                         DefaultControlView controlView,
                         SugarObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
        entityColors = HashMap.newHashMap(2);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_SUGAR, null);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_AGENT, null);
    }

    private int computeMaxColorAgentEnergy(SugarConfig config) {
        return config.agentInitialEnergy() * MAX_COLOR_AGENT_ENERGY_FACTOR;
    }

    @Override
    protected void initSimulation(SugarConfig config, CellDimension cellDimension, SugarGridModel model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(SugarEntity.DESCRIPTOR_ID_TERRAIN)
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        maxColorAgentEnergy = computeMaxColorAgentEnergy(config);
        entityColors.put(SugarEntity.DESCRIPTOR_ID_SUGAR,
                computeBrightnessVariantsMap(entityDescriptorRegistry.requireByDescriptorId(SugarEntity.DESCRIPTOR_ID_SUGAR),
                        1, config.maxSugarAmount(), config.maxSugarAmount(), SUGAR_MAX_FACTOR_DELTA));
        entityColors.put(SugarEntity.DESCRIPTOR_ID_AGENT,
                computeBrightnessVariantsMap(entityDescriptorRegistry.requireByDescriptorId(SugarEntity.DESCRIPTOR_ID_AGENT),
                        1, maxColorAgentEnergy, AGENT_GROUP_COUNT, AGENT_MAX_FACTOR_DELTA));

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellResourceDrawer = (descriptor, painter, cell, _) ->
                painter.drawCell(
                        cell.coordinate(),
                        resolveResourceFillColor(descriptor, cell.entity()),
                        null,
                        NO_STROKE_LINE_WIDTH);

        cellAgentDrawer = (descriptor, painter, cell, stepCount) -> {
            if ((stepCount > 0)
                    && (cell.entity() instanceof Agent agent)
                    && (agent.stepIndexOfSpawn() == (stepCount - 1))) {
                // draw newly spawned agents with a white border (not for stepCount == 0, as all agents are new then)
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        Color.WHITE,
                        strokeLineWidth,
                        StrokeType.CENTERED);
            } else {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        null,
                        NO_STROKE_LINE_WIDTH,
                        StrokeType.CENTERED);
            }
        };
    }

    private Color resolveResourceFillColor(GridEntityDescriptor entityDescriptor,
                                           ResourceEntity entity) {
        Color baseColor = entityDescriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Sugar sugar -> sugar.currentAmount();
                case NoResource _ -> -1;
            };

            return colorMap.getOrDefault(value, baseColor);
        }
        return (entityDescriptor.color() != null) ? entityDescriptor.color() : FALLBACK_COLOR_SUGAR;
    }

    private Color resolveAgentFillColor(GridEntityDescriptor entityDescriptor,
                                        AgentEntity entity) {
        Color baseColor = entityDescriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Agent agent -> Math.min(maxColorAgentEnergy, agent.currentEnergy());
                case NoAgent _ -> -1;
            };

            return colorMap.getOrDefault(value, baseColor);
        }
        return (entityDescriptor.color() != null) ? entityDescriptor.color() : FALLBACK_COLOR_AGENT;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable SugarCell oldGridCell,
                                          @Nullable SugarCell newGridCell) {
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
    protected void drawSimulation(SugarGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if ((cellResourceDrawer == null) || (cellAgentDrawer == null)) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        var resourceModel = currentModel.resourceModel();
        var agentModel = currentModel.agentModel();

        resourceModel.nonDefaultCells()
                     .forEach(resourceCell -> cellResourceDrawer.draw(
                             entityDescriptorRegistry.requireByDescriptorId(resourceCell.descriptorId()),
                             dynamicPainter, resourceCell, stepCount));

        agentModel.nonDefaultCells()
                  .forEach(agentCell -> cellAgentDrawer.draw(
                          entityDescriptorRegistry.requireByDescriptorId(agentCell.descriptorId()),
                          dynamicPainter, agentCell, stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<SugarUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        SUGAR_TOOL_ID_REMOVE_SUGAR,
                        SugarUserActionContext.FixedAction.REMOVE_SUGAR,
                        SimulationUserActionScope.CELL_SELECTED,
                        SUGAR_TOOLBAR_REMOVE_SUGAR,
                        SUGAR_TOOLBAR_REMOVE_SUGAR_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        SUGAR_TOOL_ID_ADD_SUGAR,
                        SimulationUserActionScope.CELL_SELECTED,
                        SUGAR_TOOLBAR_ADD_SUGAR,
                        SUGAR_TOOLBAR_ADD_SUGAR_TOOLTIP,
                        () -> editToolBarViewModel.resolveSelectedAddSugarContext()
                                                  .map(Function.identity()))
        );
    }

    @Override
    protected Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        ObservableList<SugarAddSugarLevel> availableAddSugarLevels = editToolBarViewModel.availableAddSugarLevels();
        ComboBox<SugarAddSugarLevel> levelComboBox = new ComboBox<>();
        levelComboBox.setItems(availableAddSugarLevels);
        levelComboBox.setMaxWidth(Double.MAX_VALUE);
        levelComboBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX);
        levelComboBox.setCellFactory(_ -> createAddSugarLevelCell());
        levelComboBox.setButtonCell(createAddSugarLevelCell());

        SugarAddSugarLevel selectedLevel = editToolBarViewModel.getSelectedAddSugarLevel();
        if (selectedLevel != null) {
            levelComboBox.setValue(selectedLevel);
        }

        Label levelLabel = FXComponentFactory.createLabel(
                AppLocalization.getText(SUGAR_TOOLBAR_ADD_SUGAR_LEVEL),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        levelLabel.setLabelFor(levelComboBox);
        levelLabel.setTooltip(new Tooltip(AppLocalization.getText(SUGAR_TOOLBAR_ADD_SUGAR_LEVEL_TOOLTIP)));
        levelComboBox.setTooltip(new Tooltip(AppLocalization.getText(SUGAR_TOOLBAR_ADD_SUGAR_LEVEL_TOOLTIP)));

        ChangeListener<@Nullable SugarAddSugarLevel> comboSelectionListener = (_, oldValue, newValue) -> {
            if (oldValue != newValue) {
                editToolBarViewModel.setSelectedAddSugarLevel(newValue);
            }
        };
        ChangeListener<@Nullable SugarAddSugarLevel> viewModelSelectionListener = (_, _, newValue) -> {
            if (levelComboBox.getValue() != newValue) {
                if (newValue == null) {
                    levelComboBox.getSelectionModel().clearSelection();
                } else {
                    levelComboBox.setValue(newValue);
                }
            }
        };
        var selectedLevelProperty = editToolBarViewModel.selectedAddSugarLevelProperty();
        levelComboBox.valueProperty().addListener(comboSelectionListener);
        selectedLevelProperty.addListener(viewModelSelectionListener);

        if (selectedToolId != null) {
            var enabledBinding = Bindings.createBooleanBinding(
                    () -> SUGAR_TOOL_ID_ADD_SUGAR.equals(selectedToolId.get()) && !availableAddSugarLevels.isEmpty(),
                    selectedToolId,
                    availableAddSugarLevels);
            levelLabel.disableProperty().bind(enabledBinding.not());
            levelComboBox.disableProperty().bind(enabledBinding.not());
            registerActionToolBarCleanup(() -> {
                levelLabel.disableProperty().unbind();
                levelComboBox.disableProperty().unbind();
                enabledBinding.dispose();
                levelComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedLevelProperty.removeListener(viewModelSelectionListener);
            });
        } else {
            registerActionToolBarCleanup(() -> {
                levelComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedLevelProperty.removeListener(viewModelSelectionListener);
            });
        }

        HBox optionPanel = new HBox(levelLabel, levelComboBox);
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private ListCell<SugarAddSugarLevel> createAddSugarLevelCell() {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable SugarAddSugarLevel item, boolean empty) {
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

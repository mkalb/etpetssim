package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.engine.model.entity.*;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.simulations.etpets.shared.*;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsEditToolBarViewModel;
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

public final class EtpetsMainView extends AbstractDefaultMainView<
        EtpetsEntity,
        EtpetsCell,
        EtpetsGridModel,
        EtpetsConfig,
        EtpetsStatistics,
        EtpetsSimulationManager,
        EtpetsUserActionContext,
        EtpetsConfigView,
        EtpetsObservationView> {

    private static final int TRAIL_GROUP_COUNT = 5;
    private static final double TRAIL_MAX_FACTOR_DELTA = 0.50d;
    private static final int PLANT_GROUP_COUNT = 6;
    private static final double PLANT_MAX_FACTOR_DELTA = 0.55d;
    private static final int INSECT_GROUP_COUNT = 7;
    private static final double INSECT_MAX_FACTOR_DELTA = 0.40d;
    private static final int PET_GROUP_COUNT = 5;
    private static final double PET_MAX_FACTOR_DELTA = 0.65d;
    private static final int PET_EGG_GROUP_COUNT = 5;
    private static final double PET_EGG_MAX_FACTOR_DELTA = 0.30d;

    private static final double DEAD_PET_BRIGHTNESS_FACTOR = 0.20d;
    private static final double EDIT_OPTION_BOX_SPACING = 6.0d;
    private static final String ETPETS_TOOL_ID_SET_RESOURCE = "etpets.setResource";
    private static final String ETPETS_TOOL_ID_SET_TERRAIN = "etpets.setTerrain";
    private static final String ETPETS_TOOLBAR_SET_RESOURCE = "etpets.toolbar.setresource";
    private static final String ETPETS_TOOLBAR_SET_RESOURCE_OPTION = "etpets.toolbar.setresource.option";
    private static final String ETPETS_TOOLBAR_SET_RESOURCE_OPTION_TOOLTIP = "etpets.toolbar.setresource.option.tooltip";
    private static final String ETPETS_TOOLBAR_SET_RESOURCE_TOOLTIP = "etpets.toolbar.setresource.tooltip";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN = "etpets.toolbar.setterrain";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_OPTION = "etpets.toolbar.setterrain.option";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_OPTION_TOOLTIP = "etpets.toolbar.setterrain.option.tooltip";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_TOOLTIP = "etpets.toolbar.setterrain.tooltip";

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double PET_EGG_STROKE_LINE_WIDTH = 1.0d;

    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private final EtpetsEditToolBarViewModel editToolBarViewModel;
    private @Nullable CellDrawer<TerrainEntity> cellTerrainDrawer;
    private @Nullable CellDrawer<ResourceEntity> cellResourceDrawer;
    private @Nullable CellDrawer<AgentEntity> cellAgentDrawer;

    public EtpetsMainView(DefaultMainViewModel<EtpetsEntity, EtpetsCell, EtpetsGridModel, EtpetsConfig, EtpetsStatistics, EtpetsSimulationManager, EtpetsUserActionContext> viewModel,
                          EtpetsEditToolBarViewModel editToolBarViewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          EtpetsConfigView configView,
                          DefaultControlView controlView,
                          EtpetsObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        this.editToolBarViewModel = editToolBarViewModel;
        entityColors = HashMap.newHashMap(5);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_TRAIL, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PLANT, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_INSECT, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET_EGG, null);
    }

    @Override
    protected void initSimulation(EtpetsConfig config, CellDimension cellDimension, EtpetsGridModel model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_TRAIL,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                        minByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                        maxByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                        TRAIL_GROUP_COUNT,
                        TRAIL_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PLANT,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                        minByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                        maxByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                        PLANT_GROUP_COUNT,
                        PLANT_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_INSECT,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                        minByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                        maxByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                        INSECT_GROUP_COUNT,
                        INSECT_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                        minByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                        maxByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                        PET_GROUP_COUNT,
                        PET_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET_EGG,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                        minByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                        maxByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                        PET_EGG_GROUP_COUNT,
                        PET_EGG_MAX_FACTOR_DELTA));

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellTerrainDrawer = (descriptor, painter, cell, _) ->
                painter.drawCell(
                        cell.coordinate(),
                        resolveTerrainFillColor(descriptor, cell.entity()),
                        null,
                        NO_STROKE_LINE_WIDTH);

        cellResourceDrawer = (descriptor, painter, cell, _) ->
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveResourceFillColor(descriptor, cell.entity()),
                        descriptor.borderColorOrFallback(),
                        strokeLineWidth,
                        StrokeType.CENTERED);

        cellAgentDrawer = (descriptor, painter, cell, _) -> {
            if ((cell.entity() instanceof Pet pet) && pet.isDead()) {
                Color baseColor = descriptor.colorOrFallback();
                Color fillColor = FXPaintFactory.adjustBrightness(baseColor, DEAD_PET_BRIGHTNESS_FACTOR);
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        fillColor,
                        null,
                        NO_STROKE_LINE_WIDTH,
                        StrokeType.INSIDE);
            } else if (cell.entity() instanceof PetEgg) {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        descriptor.borderColorOrFallback(),
                        PET_EGG_STROKE_LINE_WIDTH,
                        StrokeType.INSIDE);
            } else {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        resolveAgentFillColor(descriptor, cell.entity()),
                        null,
                        NO_STROKE_LINE_WIDTH,
                        StrokeType.INSIDE);
            }
        };
    }

    private int minByDescriptorId(String descriptorId) {
        return switch (descriptorId) {
            case EtpetsEntity.DESCRIPTOR_ID_TRAIL -> EtpetsBalance.TRAIL_INTENSITY_RANGE_MIN;
            case EtpetsEntity.DESCRIPTOR_ID_PLANT -> EtpetsBalance.PLANT_CURRENT_AMOUNT_RANGE_MIN;
            case EtpetsEntity.DESCRIPTOR_ID_INSECT -> EtpetsBalance.INSECT_CURRENT_AMOUNT_RANGE_MIN;
            case EtpetsEntity.DESCRIPTOR_ID_PET -> EtpetsBalance.PET_CURRENT_ENERGY_RANGE_MIN;
            case EtpetsEntity.DESCRIPTOR_ID_PET_EGG -> EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MIN;
            default -> throw new IllegalArgumentException("No min defined for descriptorId: " + descriptorId);
        };
    }

    private int maxByDescriptorId(String descriptorId) {
        return switch (descriptorId) {
            case EtpetsEntity.DESCRIPTOR_ID_TRAIL -> EtpetsBalance.TRAIL_INTENSITY_RANGE_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PLANT -> EtpetsBalance.PLANT_CURRENT_AMOUNT_RANGE_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_INSECT -> EtpetsBalance.INSECT_CURRENT_AMOUNT_RANGE_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PET -> EtpetsBalance.PET_CURRENT_ENERGY_RANGE_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PET_EGG -> EtpetsBalance.PET_EGG_INCUBATION_REMAINING_RANGE_MAX;
            default -> throw new IllegalArgumentException("No max defined for descriptorId: " + descriptorId);
        };
    }

    private int normalizeDoubleValueForMapRange(double value,
                                                int min,
                                                int max) {
        if (Double.isNaN(value)) {
            return min;
        }
        if (Double.isInfinite(value)) {
            return max;
        }

        return Math.clamp(Math.round(value), min, max);
    }

    private int normalizeIntValueForMapRange(int value,
                                             int min,
                                             int max) {
        return Math.clamp(value, min, max);
    }

    private Color resolveTerrainFillColor(GridEntityDescriptor descriptor,
                                          TerrainEntity entity) {
        String descriptorId = descriptor.descriptorId();
        Color baseColor = descriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(descriptorId);
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Trail trail -> normalizeIntValueForMapRange(
                        trail.intensity(),
                        minByDescriptorId(descriptorId),
                        maxByDescriptorId(descriptorId));
                case TerrainConstant ignoredTerrain -> -1;
            };
            return colorMap.getOrDefault(value, baseColor);
        }
        return baseColor;
    }

    private Color resolveResourceFillColor(GridEntityDescriptor descriptor,
                                           ResourceEntity entity) {
        String descriptorId = descriptor.descriptorId();
        Color baseColor = descriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(descriptorId);
        if (colorMap != null) {
            Integer value = switch (entity) {
                case ResourceBase resource -> normalizeDoubleValueForMapRange(
                        resource.currentAmount(),
                        minByDescriptorId(descriptorId),
                        maxByDescriptorId(descriptorId));
                case NoResource ignoredResource -> -1;
            };
            return colorMap.getOrDefault(value, baseColor);
        }
        return baseColor;
    }

    private Color resolveAgentFillColor(GridEntityDescriptor descriptor,
                                        AgentEntity entity) {
        String descriptorId = descriptor.descriptorId();
        Color baseColor = descriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(descriptorId);
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Pet pet -> normalizeIntValueForMapRange(
                        pet.currentEnergy(),
                        minByDescriptorId(descriptorId),
                        maxByDescriptorId(descriptorId));
                case PetEgg egg -> normalizeIntValueForMapRange(
                        egg.incubationRemaining(),
                        minByDescriptorId(descriptorId),
                        maxByDescriptorId(descriptorId));
                case NoAgent ignoredAgent -> -1;
            };
            return colorMap.getOrDefault(value, baseColor);
        }
        return baseColor;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable EtpetsCell oldGridCell,
                                          @Nullable EtpetsCell newGridCell) {
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
    protected void drawSimulation(EtpetsGridModel currentModel, int stepCount, int lastDrawnStepCount) {
        if ((basePainter == null) || (dynamicPainter == null) || (overlayPainter == null)) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if ((cellTerrainDrawer == null) || (cellResourceDrawer == null) || (cellAgentDrawer == null)) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        dynamicPainter.clearCanvasBackground();

        var terrainModel = currentModel.terrainModel();
        var resourceModel = currentModel.resourceModel();
        var agentModel = currentModel.agentModel();

        var rockDescriptor = entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_ROCK);
        terrainModel.filteredCoordinates(e -> EtpetsEntity.DESCRIPTOR_ID_ROCK.equals(e.descriptorId()))
                    .forEach(coordinate ->
                            dynamicPainter.drawCell(coordinate, rockDescriptor.color(), rockDescriptor.borderColor(), NO_STROKE_LINE_WIDTH));

        var waterDescriptor = entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_WATER);
        terrainModel.filteredCoordinates(e -> EtpetsEntity.DESCRIPTOR_ID_WATER.equals(e.descriptorId()))
                    .forEach(coordinate ->
                            dynamicPainter.drawCell(coordinate, waterDescriptor.color(), waterDescriptor.borderColor(), NO_STROKE_LINE_WIDTH));

        terrainModel.filteredCells(e -> e instanceof Trail)
                    .forEach(cell -> cellTerrainDrawer.draw(
                            entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                            dynamicPainter,
                            cell,
                            stepCount));

        resourceModel.nonDefaultCells()
                     .forEach(cell -> cellResourceDrawer.draw(
                             entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                             dynamicPainter,
                             cell,
                             stepCount));

        agentModel.nonDefaultCells()
                  .forEach(cell -> cellAgentDrawer.draw(
                          entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                          dynamicPainter,
                          cell,
                          stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<EtpetsUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        ETPETS_TOOL_ID_SET_TERRAIN,
                        SimulationUserActionScope.CELL_SELECTED,
                        ETPETS_TOOLBAR_SET_TERRAIN,
                        ETPETS_TOOLBAR_SET_TERRAIN_TOOLTIP,
                        () -> editToolBarViewModel.resolveSelectedTerrainContext()
                                                  .map(Function.identity())),
                new SimulationUserActionDescriptor<>(
                        ETPETS_TOOL_ID_SET_RESOURCE,
                        SimulationUserActionScope.CELL_SELECTED,
                        ETPETS_TOOLBAR_SET_RESOURCE,
                        ETPETS_TOOLBAR_SET_RESOURCE_TOOLTIP,
                        () -> editToolBarViewModel.resolveSelectedResourceContext()
                                                  .map(Function.identity()))
        );
    }

    @Override
    protected Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        HBox optionPanel = new HBox(
                createTerrainOptionBox(selectedToolId),
                createResourceOptionBox(selectedToolId));
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private HBox createTerrainOptionBox(@Nullable ObjectProperty<String> selectedToolId) {
        ObservableList<EtpetsTerrainChoice> availableTerrainChoices = editToolBarViewModel.availableTerrainChoices();
        ComboBox<EtpetsTerrainChoice> terrainComboBox = new ComboBox<>();
        terrainComboBox.setItems(availableTerrainChoices);
        terrainComboBox.setMaxWidth(Double.MAX_VALUE);
        terrainComboBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX);
        terrainComboBox.setCellFactory(_ -> createTerrainChoiceCell());
        terrainComboBox.setButtonCell(createTerrainChoiceCell());
        EtpetsTerrainChoice selectedTerrainChoice = editToolBarViewModel.getSelectedTerrainChoice();
        if (selectedTerrainChoice != null) {
            terrainComboBox.setValue(selectedTerrainChoice);
        }

        Label terrainLabel = FXComponentFactory.createLabel(
                AppLocalization.getText(ETPETS_TOOLBAR_SET_TERRAIN_OPTION),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        terrainLabel.setLabelFor(terrainComboBox);
        terrainLabel.setTooltip(new Tooltip(AppLocalization.getText(ETPETS_TOOLBAR_SET_TERRAIN_OPTION_TOOLTIP)));
        terrainComboBox.setTooltip(new Tooltip(AppLocalization.getText(ETPETS_TOOLBAR_SET_TERRAIN_OPTION_TOOLTIP)));

        ChangeListener<@Nullable EtpetsTerrainChoice> comboSelectionListener = (_, oldValue, newValue) -> {
            if (oldValue != newValue) {
                editToolBarViewModel.setSelectedTerrainChoice(newValue);
            }
        };
        ChangeListener<@Nullable EtpetsTerrainChoice> viewModelSelectionListener = (_, _, newValue) -> {
            if (terrainComboBox.getValue() != newValue) {
                if (newValue == null) {
                    terrainComboBox.getSelectionModel().clearSelection();
                } else {
                    terrainComboBox.setValue(newValue);
                }
            }
        };
        var selectedTerrainChoiceProperty = editToolBarViewModel.selectedTerrainChoiceProperty();
        terrainComboBox.valueProperty().addListener(comboSelectionListener);
        selectedTerrainChoiceProperty.addListener(viewModelSelectionListener);

        if (selectedToolId != null) {
            var enabledBinding = Bindings.createBooleanBinding(
                    () -> ETPETS_TOOL_ID_SET_TERRAIN.equals(selectedToolId.get()) && !availableTerrainChoices.isEmpty(),
                    selectedToolId,
                    availableTerrainChoices);
            terrainLabel.disableProperty().bind(enabledBinding.not());
            terrainComboBox.disableProperty().bind(enabledBinding.not());
            registerActionToolBarCleanup(() -> {
                terrainLabel.disableProperty().unbind();
                terrainComboBox.disableProperty().unbind();
                enabledBinding.dispose();
                terrainComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedTerrainChoiceProperty.removeListener(viewModelSelectionListener);
            });
        } else {
            registerActionToolBarCleanup(() -> {
                terrainComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedTerrainChoiceProperty.removeListener(viewModelSelectionListener);
            });
        }

        HBox terrainOptionBox = new HBox(terrainLabel, terrainComboBox);
        terrainOptionBox.setAlignment(Pos.CENTER_LEFT);
        terrainOptionBox.setSpacing(EDIT_OPTION_BOX_SPACING);
        return terrainOptionBox;
    }

    private HBox createResourceOptionBox(@Nullable ObjectProperty<String> selectedToolId) {
        ObservableList<EtpetsResourceChoice> availableResourceChoices = editToolBarViewModel.availableResourceChoices();
        ComboBox<EtpetsResourceChoice> resourceComboBox = new ComboBox<>();
        resourceComboBox.setItems(availableResourceChoices);
        resourceComboBox.setMaxWidth(Double.MAX_VALUE);
        resourceComboBox.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX);
        resourceComboBox.setCellFactory(_ -> createResourceChoiceCell());
        resourceComboBox.setButtonCell(createResourceChoiceCell());
        EtpetsResourceChoice selectedResourceChoice = editToolBarViewModel.getSelectedResourceChoice();
        if (selectedResourceChoice != null) {
            resourceComboBox.setValue(selectedResourceChoice);
        }

        Label resourceLabel = FXComponentFactory.createLabel(
                AppLocalization.getText(ETPETS_TOOLBAR_SET_RESOURCE_OPTION),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        resourceLabel.setLabelFor(resourceComboBox);
        resourceLabel.setTooltip(new Tooltip(AppLocalization.getText(ETPETS_TOOLBAR_SET_RESOURCE_OPTION_TOOLTIP)));
        resourceComboBox.setTooltip(new Tooltip(AppLocalization.getText(ETPETS_TOOLBAR_SET_RESOURCE_OPTION_TOOLTIP)));

        ChangeListener<@Nullable EtpetsResourceChoice> comboSelectionListener = (_, oldValue, newValue) -> {
            if (oldValue != newValue) {
                editToolBarViewModel.setSelectedResourceChoice(newValue);
            }
        };
        ChangeListener<@Nullable EtpetsResourceChoice> viewModelSelectionListener = (_, _, newValue) -> {
            if (resourceComboBox.getValue() != newValue) {
                if (newValue == null) {
                    resourceComboBox.getSelectionModel().clearSelection();
                } else {
                    resourceComboBox.setValue(newValue);
                }
            }
        };
        var selectedResourceChoiceProperty = editToolBarViewModel.selectedResourceChoiceProperty();
        resourceComboBox.valueProperty().addListener(comboSelectionListener);
        selectedResourceChoiceProperty.addListener(viewModelSelectionListener);

        if (selectedToolId != null) {
            var enabledBinding = Bindings.createBooleanBinding(
                    () -> ETPETS_TOOL_ID_SET_RESOURCE.equals(selectedToolId.get()) && !availableResourceChoices.isEmpty(),
                    selectedToolId,
                    availableResourceChoices);
            resourceLabel.disableProperty().bind(enabledBinding.not());
            resourceComboBox.disableProperty().bind(enabledBinding.not());
            registerActionToolBarCleanup(() -> {
                resourceLabel.disableProperty().unbind();
                resourceComboBox.disableProperty().unbind();
                enabledBinding.dispose();
                resourceComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedResourceChoiceProperty.removeListener(viewModelSelectionListener);
            });
        } else {
            registerActionToolBarCleanup(() -> {
                resourceComboBox.valueProperty().removeListener(comboSelectionListener);
                selectedResourceChoiceProperty.removeListener(viewModelSelectionListener);
            });
        }

        HBox resourceOptionBox = new HBox(resourceLabel, resourceComboBox);
        resourceOptionBox.setAlignment(Pos.CENTER_LEFT);
        resourceOptionBox.setSpacing(EDIT_OPTION_BOX_SPACING);
        return resourceOptionBox;
    }

    private ListCell<EtpetsTerrainChoice> createTerrainChoiceCell() {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable EtpetsTerrainChoice item, boolean empty) {
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

    private ListCell<EtpetsResourceChoice> createResourceChoiceCell() {
        return new ListCell<>() {
            @SuppressWarnings({"ConstantValue", "DataFlowIssue"})
            @Override
            protected void updateItem(@Nullable EtpetsResourceChoice item, boolean empty) {
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

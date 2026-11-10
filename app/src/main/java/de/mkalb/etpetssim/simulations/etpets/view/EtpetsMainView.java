package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsCell;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsConfig;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsGridModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsSimulationManager;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.AgentEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Insect;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.etpets.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Pet;
import de.mkalb.etpetssim.simulations.etpets.model.entity.PetEgg;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Plant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Trail;
import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsUserActionContext;
import de.mkalb.etpetssim.simulations.etpets.viewmodel.EtpetsEditToolBarViewModel;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.InputChoiceProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Main view of ET Pets.
 */
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
    private static final String ETPETS_TOOLBAR_SET_RESOURCE_OPTION_TOOLTIP =
            "etpets.toolbar.setresource.option.tooltip";
    private static final String ETPETS_TOOLBAR_SET_RESOURCE_TOOLTIP = "etpets.toolbar.setresource.tooltip";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN = "etpets.toolbar.setterrain";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_OPTION = "etpets.toolbar.setterrain.option";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_OPTION_TOOLTIP = "etpets.toolbar.setterrain.option.tooltip";
    private static final String ETPETS_TOOLBAR_SET_TERRAIN_TOOLTIP = "etpets.toolbar.setterrain.tooltip";

    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;
    private static final double PET_EGG_STROKE_LINE_WIDTH = 1.0d;

    private final EtpetsEditToolBarViewModel editToolBarViewModel;
    private @Nullable Map<Integer, Color> trailColors;
    private @Nullable Map<Integer, Color> plantColors;
    private @Nullable Map<Integer, Color> insectColors;
    private @Nullable Map<Integer, Color> petColors;
    private @Nullable Map<Integer, Color> petEggColors;
    private @Nullable CellDrawer<TerrainEntity> cellTerrainDrawer;
    private @Nullable CellDrawer<ResourceEntity> cellResourceDrawer;
    private @Nullable CellDrawer<AgentEntity> cellAgentDrawer;

    public EtpetsMainView(DefaultMainViewModel<EtpetsEntity, EtpetsCell, EtpetsGridModel, EtpetsConfig,
                                  EtpetsStatistics, EtpetsSimulationManager, EtpetsUserActionContext> viewModel,
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
    }

    private static int clampToMapRange(double value,
                                       int min,
                                       int max) {
        if (Double.isNaN(value)) {
            return min;
        }
        return Math.clamp(Math.round(value), min, max);
    }

    private static Color resolveVariantColor(@Nullable Map<Integer, Color> variantColors,
                                             int value,
                                             Color baseColor) {
        return (variantColors == null) ? baseColor : variantColors.getOrDefault(value, baseColor);
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, EtpetsConfig config, CellDimension cellDimension) {
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        painters.base().fillCanvasBackground(backgroundColor);

        trailColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                EtpetsBalance.TRAIL_INTENSITY_MIN,
                EtpetsBalance.TRAIL_INTENSITY_MAX,
                TRAIL_GROUP_COUNT,
                TRAIL_MAX_FACTOR_DELTA);
        plantColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                EtpetsBalance.PLANT_CURRENT_AMOUNT_MIN,
                EtpetsBalance.PLANT_CURRENT_AMOUNT_MAX,
                PLANT_GROUP_COUNT,
                PLANT_MAX_FACTOR_DELTA);
        insectColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                EtpetsBalance.INSECT_CURRENT_AMOUNT_MIN,
                EtpetsBalance.INSECT_CURRENT_AMOUNT_MAX,
                INSECT_GROUP_COUNT,
                INSECT_MAX_FACTOR_DELTA);
        petColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                EtpetsBalance.PET_CURRENT_ENERGY_MIN,
                EtpetsBalance.PET_CURRENT_ENERGY_MAX,
                PET_GROUP_COUNT,
                PET_MAX_FACTOR_DELTA);
        petEggColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN,
                EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MAX,
                PET_EGG_GROUP_COUNT,
                PET_EGG_MAX_FACTOR_DELTA);

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

    @Override
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
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
    protected void drawSimulation(CanvasPainters painters,
                                  EtpetsGridModel currentGridModel,
                                  int stepCount,
                                  int lastDrawnStepCount) {
        if ((cellTerrainDrawer == null) || (cellResourceDrawer == null) || (cellAgentDrawer == null)) {
            AppLogger.warn("EtpetsMainView: Skipping canvas draw: cell drawers are not initialized");
            return;
        }

        painters.dynamic().clearCanvasBackground();

        var terrainModel = currentGridModel.terrainModel();
        var resourceModel = currentGridModel.resourceModel();
        var agentModel = currentGridModel.agentModel();

        var rockDescriptor = entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_ROCK);
        terrainModel.filteredCoordinates(entity -> entity == TerrainConstant.ROCK)
                .forEach(coordinate ->
                        painters.dynamic().drawCell(coordinate, rockDescriptor.color(), rockDescriptor.borderColor(),
                                NO_STROKE_LINE_WIDTH));

        var waterDescriptor = entityDescriptorRegistry.requireByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_WATER);
        terrainModel.filteredCoordinates(entity -> entity == TerrainConstant.WATER)
                .forEach(coordinate ->
                        painters.dynamic().drawCell(coordinate, waterDescriptor.color(), waterDescriptor.borderColor(),
                                NO_STROKE_LINE_WIDTH));

        terrainModel.filteredCells(entity -> entity instanceof Trail)
                .forEach(cell -> cellTerrainDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                        painters.dynamic(),
                        cell,
                        stepCount));

        resourceModel.nonDefaultCells()
                .forEach(cell -> cellResourceDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                        painters.dynamic(),
                        cell,
                        stepCount));

        agentModel.nonDefaultCells()
                .forEach(cell -> cellAgentDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                        painters.dynamic(),
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
                        () -> Optional.of(editToolBarViewModel.getSelectedTerrainChoice())
                                .map(EtpetsUserActionContext.SetTerrain::new)
                                .map(Function.identity())),
                new SimulationUserActionDescriptor<>(
                        ETPETS_TOOL_ID_SET_RESOURCE,
                        SimulationUserActionScope.CELL_SELECTED,
                        ETPETS_TOOLBAR_SET_RESOURCE,
                        ETPETS_TOOLBAR_SET_RESOURCE_TOOLTIP,
                        () -> Optional.of(editToolBarViewModel.getSelectedResourceChoice())
                                .map(EtpetsUserActionContext.SetResource::new)
                                .map(Function.identity()))
        );
    }

    @Override
    protected Node createEditToolBarOptionPanel(@Nullable ObjectProperty<String> selectedToolId) {
        HBox optionPanel = new HBox(
                createOptionBox(editToolBarViewModel.selectedTerrainChoiceProperty(),
                        ETPETS_TOOLBAR_SET_TERRAIN_OPTION,
                        ETPETS_TOOLBAR_SET_TERRAIN_OPTION_TOOLTIP,
                        ETPETS_TOOL_ID_SET_TERRAIN,
                        selectedToolId),
                createOptionBox(editToolBarViewModel.selectedResourceChoiceProperty(),
                        ETPETS_TOOLBAR_SET_RESOURCE_OPTION,
                        ETPETS_TOOLBAR_SET_RESOURCE_OPTION_TOOLTIP,
                        ETPETS_TOOL_ID_SET_RESOURCE,
                        selectedToolId));
        optionPanel.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_PANEL);
        optionPanel.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionPanel;
    }

    private Color resolveTerrainFillColor(GridEntityDescriptor descriptor,
                                          TerrainEntity entity) {
        Color baseColor = descriptor.colorOrFallback();
        return switch (entity) {
            case Trail trail -> resolveVariantColor(trailColors,
                    clampToMapRange(trail.intensity(),
                            EtpetsBalance.TRAIL_INTENSITY_MIN,
                            EtpetsBalance.TRAIL_INTENSITY_MAX),
                    baseColor);
            case TerrainConstant _ -> baseColor;
        };
    }

    private Color resolveResourceFillColor(GridEntityDescriptor descriptor,
                                           ResourceEntity entity) {
        Color baseColor = descriptor.colorOrFallback();
        return switch (entity) {
            case Plant plant -> resolveVariantColor(plantColors,
                    clampToMapRange(plant.currentAmount(),
                            EtpetsBalance.PLANT_CURRENT_AMOUNT_MIN,
                            EtpetsBalance.PLANT_CURRENT_AMOUNT_MAX),
                    baseColor);
            case Insect insect -> resolveVariantColor(insectColors,
                    clampToMapRange(insect.currentAmount(),
                            EtpetsBalance.INSECT_CURRENT_AMOUNT_MIN,
                            EtpetsBalance.INSECT_CURRENT_AMOUNT_MAX),
                    baseColor);
            case NoResource _ -> baseColor;
        };
    }

    private Color resolveAgentFillColor(GridEntityDescriptor descriptor,
                                        AgentEntity entity) {
        Color baseColor = descriptor.colorOrFallback();
        return switch (entity) {
            case Pet pet -> resolveVariantColor(petColors,
                    clampToMapRange(pet.currentEnergy(),
                            EtpetsBalance.PET_CURRENT_ENERGY_MIN,
                            EtpetsBalance.PET_CURRENT_ENERGY_MAX),
                    baseColor);
            case PetEgg egg -> resolveVariantColor(petEggColors,
                    clampToMapRange(egg.incubationRemaining(),
                            EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MIN,
                            EtpetsBalance.PET_EGG_INCUBATION_REMAINING_MAX),
                    baseColor);
            case NoAgent _ -> baseColor;
        };
    }

    private <T> HBox createOptionBox(InputChoiceProperty<T> choiceProperty,
                                     String labelKey,
                                     String tooltipKey,
                                     String toolId,
                                     @Nullable ObjectProperty<String> selectedToolId) {
        var control = FXComponentFactory.createLabeledChoiceComboBox(
                choiceProperty,
                AppLocalization.getText(labelKey),
                AppLocalization.getText(tooltipKey),
                FXStyleClasses.SIMULATION_EDIT_TOOLBAR_COMBOBOX,
                this::registerEditToolBarCleanup);
        Label label = control.label();
        label.getStyleClass().add(FXStyleClasses.SIMULATION_EDIT_TOOLBAR_OPTION_LABEL);
        var comboBox = control.controlRegion();

        if (selectedToolId == null) {
            label.setDisable(true);
            comboBox.setDisable(true);
        } else {
            var disableWhenToolNotSelected = Bindings.not(selectedToolId.isEqualTo(toolId));
            label.disableProperty().bind(disableWhenToolNotSelected);
            comboBox.disableProperty().bind(disableWhenToolNotSelected);
            registerEditToolBarCleanup(() -> {
                label.disableProperty().unbind();
                comboBox.disableProperty().unbind();
            });
        }

        HBox optionBox = new HBox(label, comboBox);
        optionBox.setAlignment(Pos.CENTER_LEFT);
        optionBox.setSpacing(EDIT_OPTION_BOX_SPACING);
        return optionBox;
    }

}

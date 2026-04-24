package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsMainView extends AbstractDefaultMainView<
        EtpetsEntity,
        EtpetsGridModel,
        EtpetsConfig,
        EtpetsStatistics,
        EtpetsConfigView,
        EtpetsObservationView> {

    private static final int TRAIL_LEVEL_COUNT = 10;
    private static final double TRAIL_MAX_FACTOR_DELTA = 0.65d;
    private static final int RESOURCE_LEVEL_COUNT = 10;
    private static final double RESOURCE_MAX_FACTOR_DELTA = 0.50d;
    private static final int PET_ENERGY_LEVEL_COUNT = 10;
    private static final double PET_ENERGY_MAX_FACTOR_DELTA = 0.65d;
    private static final int EGG_INCUBATION_GROUP_COUNT = 5;
    private static final double EGG_MAX_FACTOR_DELTA = 0.55d;
    private static final double DEAD_PET_BRIGHTNESS_FACTOR = 0.30d;
    private static final double DEAD_PET_ALPHA = 0.70d;
    private static final Color SELECTED_STROKE_COLOR = Color.WHITE;
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final Color backgroundColor;
    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private @Nullable CellDrawer<TerrainEntity> cellTerrainDrawer;
    private @Nullable CellDrawer<ResourceEntity> cellResourceDrawer;
    private @Nullable CellDrawer<AgentEntity> cellAgentDrawer;

    public EtpetsMainView(DefaultMainViewModel<EtpetsEntity, EtpetsGridModel, EtpetsConfig, EtpetsStatistics> viewModel,
                          GridEntityDescriptorRegistry entityDescriptorRegistry,
                          EtpetsConfigView configView,
                          DefaultControlView controlView,
                          EtpetsObservationView observationView) {
        super(viewModel, configView, controlView, observationView, entityDescriptorRegistry);
        backgroundColor = entityDescriptorRegistry
                .getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_GROUND)
                .colorOrFallback();
        entityColors = HashMap.newHashMap(5);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_TRAIL, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PLANT, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_INSECT, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET, null);
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET_EGG, null);
    }

    @Override
    protected void initSimulation(EtpetsConfig config, CellDimension cellDimension) {
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_TRAIL,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                        1,
                        maxAmountByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_TRAIL),
                        TRAIL_LEVEL_COUNT,
                        TRAIL_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PLANT,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                        0,
                        maxAmountByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PLANT),
                        RESOURCE_LEVEL_COUNT,
                        RESOURCE_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_INSECT,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                        0,
                        maxAmountByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_INSECT),
                        RESOURCE_LEVEL_COUNT,
                        RESOURCE_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                        1,
                        maxAmountByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET),
                        PET_ENERGY_LEVEL_COUNT,
                        PET_ENERGY_MAX_FACTOR_DELTA));
        entityColors.put(EtpetsEntity.DESCRIPTOR_ID_PET_EGG,
                computeBrightnessVariantsMap(
                        entityDescriptorRegistry.getRequiredByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                        1,
                        maxAmountByDescriptorId(EtpetsEntity.DESCRIPTOR_ID_PET_EGG),
                        EGG_INCUBATION_GROUP_COUNT,
                        EGG_MAX_FACTOR_DELTA));

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
                        null,
                        NO_STROKE_LINE_WIDTH,
                        StrokeType.INSIDE);

        cellAgentDrawer = (descriptor, painter, cell, _) -> {
            Color fillColor = resolveAgentFillColor(descriptor, cell.entity());
            if ((cell.entity() instanceof Pet pet) && pet.isDead()) {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        fillColor,
                        backgroundColor,
                        strokeLineWidth,
                        StrokeType.CENTERED);
            } else {
                painter.drawCellInnerCircle(
                        cell.coordinate(),
                        fillColor,
                        null,
                        NO_STROKE_LINE_WIDTH,
                        StrokeType.INSIDE);
            }
        };
    }

    private Color resolveTerrainFillColor(GridEntityDescriptor descriptor,
                                          TerrainEntity entity) {
        String descriptorId = descriptor.descriptorId();
        Color baseColor = descriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(descriptorId);
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Trail trail -> normalizeValueForMapRange(
                        trail.intensity(),
                        1,
                        maxAmountByDescriptorId(descriptorId));
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
                case ResourceBase resource -> normalizeValueForMapRange(
                        resource.currentAmount(),
                        0,
                        maxAmountByDescriptorId(descriptorId));
                case NoResource ignoredResource -> -1;
            };
            return colorMap.getOrDefault(value, baseColor);
        }
        return baseColor;
    }

    private int maxAmountByDescriptorId(String descriptorId) {
        return switch (descriptorId) {
            case EtpetsEntity.DESCRIPTOR_ID_TRAIL -> (int) EtpetsAgentLogic.TRAIL_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PLANT -> EtpetsSimulationManager.PLANT_MAX_AMOUNT_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_INSECT -> EtpetsSimulationManager.INSECT_MAX_AMOUNT_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PET -> PetTraits.MAX_ENERGY_MAX;
            case EtpetsEntity.DESCRIPTOR_ID_PET_EGG -> EtpetsAgentLogic.INCUBATION_DURATION;
            default -> throw new IllegalArgumentException("No max amount defined for descriptorId: " + descriptorId);
        };
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private int normalizeValueForMapRange(double value,
                                          int min,
                                          int max) {
        if ((max < min) || Double.isNaN(value)) {
            return min;
        }
        int roundedValue = (int) Math.round(value);
        return Math.max(min, Math.min(max, roundedValue));
    }

    private Color resolveAgentFillColor(GridEntityDescriptor descriptor,
                                        AgentEntity entity) {
        String descriptorId = descriptor.descriptorId();
        Color baseColor = descriptor.colorOrFallback();
        if ((entity instanceof Pet pet) && pet.isDead()) {
            return FXPaintFactory.adjustColorAlpha(
                    FXPaintFactory.adjustBrightness(baseColor, DEAD_PET_BRIGHTNESS_FACTOR),
                    DEAD_PET_ALPHA);
        }

        Map<Integer, Color> colorMap = entityColors.get(descriptorId);
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Pet pet -> normalizeValueForMapRange(
                        pet.currentEnergy(),
                        1,
                        maxAmountByDescriptorId(descriptorId));
                case PetEgg egg -> normalizeValueForMapRange(
                        (EtpetsAgentLogic.INCUBATION_DURATION - egg.incubationRemaining()) + 1,
                        1,
                        maxAmountByDescriptorId(descriptorId));
                case NoAgent ignoredAgent -> -1;
            };
            return colorMap.getOrDefault(value, baseColor);
        }
        return baseColor;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<EtpetsEntity> oldGridCell,
                                          @Nullable GridCell<EtpetsEntity> newGridCell) {
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
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellTerrainDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }
        if (cellResourceDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }
        if (cellAgentDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundColor);

        currentModel.terrainModel().nonDefaultCells()
                    .forEachOrdered(cell -> cellTerrainDrawer.draw(
                            entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId()),
                            basePainter,
                            cell,
                            stepCount));

        currentModel.resourceModel().nonDefaultCells()
                    .forEachOrdered(cell -> cellResourceDrawer.draw(
                            entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId()),
                            basePainter,
                            cell,
                            stepCount));

        currentModel.agentModel().nonDefaultCells()
                    .forEachOrdered(cell -> cellAgentDrawer.draw(
                            entityDescriptorRegistry.getRequiredByDescriptorId(cell.descriptorId()),
                            basePainter,
                            cell,
                            stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}


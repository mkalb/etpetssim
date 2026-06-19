package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.*;
import de.mkalb.etpetssim.simulations.core.shared.*;
import de.mkalb.etpetssim.simulations.core.view.*;
import de.mkalb.etpetssim.simulations.core.viewmodel.*;
import de.mkalb.etpetssim.simulations.wator.model.*;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;
import de.mkalb.etpetssim.simulations.wator.shared.WatorUserActionContext;
import de.mkalb.etpetssim.ui.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WatorMainView
        extends AbstractDefaultMainView<
        WatorEntity,
        GridCell<WatorEntity>,
        WritableGridModel<WatorEntity>,
        WatorConfig,
        WatorStatistics,
        WatorSimulationManager,
        WatorUserActionContext,
        WatorConfigView,
        WatorObservationView> {

    private static final String WATOR_TOOLBAR_ADD_FISH = "wator.toolbar.addfish";
    private static final String WATOR_TOOLBAR_ADD_FISH_TOOLTIP = "wator.toolbar.addfish.tooltip";
    private static final String WATOR_TOOLBAR_ADD_SHARK = "wator.toolbar.addshark";
    private static final String WATOR_TOOLBAR_ADD_SHARK_TOOLTIP = "wator.toolbar.addshark.tooltip";

    private static final Color FALLBACK_COLOR_AGENT = Color.WHITE;
    private static final double FISH_MAX_FACTOR_DELTA = -0.5d;
    private static final double SHARK_MAX_FACTOR_DELTA = 0.7d;
    private static final int FISH_GROUP_COUNT = 10;
    private static final int SHARK_GROUP_COUNT = 6;
    private static final int MAX_COLOR_SHARK_ENERGY_FACTOR = 3;
    private static final Color SELECTED_STROKE_COLOR = Color.rgb(255, 255, 120);
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private @Nullable CellDrawer<WatorEntity> cellDrawer;

    private int maxColorSharkEnergy = 1;

    public WatorMainView(DefaultMainViewModel<WatorEntity, GridCell<WatorEntity>, WritableGridModel<WatorEntity>, WatorConfig, WatorStatistics, WatorSimulationManager, WatorUserActionContext> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         WatorConfigView configView,
                         DefaultControlView controlView,
                         WatorObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        entityColors = HashMap.newHashMap(2);
        entityColors.put(EntityDescriptors.FISH.descriptorId(), null);
        entityColors.put(EntityDescriptors.SHARK.descriptorId(), null);
    }

    private int computeMaxColorSharkEnergy(WatorConfig config) {
        return Math.max(config.sharkBirthEnergy(), config.sharkMinReproductionEnergy()) * MAX_COLOR_SHARK_ENERGY_FACTOR;
    }

    @Override
    protected void initSimulation(WatorConfig config, CellDimension cellDimension, WritableGridModel<WatorEntity> model) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(WatorEntity.DESCRIPTOR_ID_WATER)
                .colorOrFallback();
        basePainter.fillCanvasBackground(backgroundColor);

        maxColorSharkEnergy = computeMaxColorSharkEnergy(config);
        entityColors.put(EntityDescriptors.FISH.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.requireByDescriptorId(EntityDescriptors.FISH.descriptorId()),
                        0, config.fishMaxAge() - 1, FISH_GROUP_COUNT, FISH_MAX_FACTOR_DELTA));
        entityColors.put(EntityDescriptors.SHARK.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.requireByDescriptorId(EntityDescriptors.SHARK.descriptorId()),
                        1, maxColorSharkEnergy, SHARK_GROUP_COUNT, SHARK_MAX_FACTOR_DELTA));

        double strokeLineWidth = computeStrokeLineWidth(cellDimension);

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            null,
                            NO_STROKE_LINE_WIDTH);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            backgroundColor,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            null,
                            NO_STROKE_LINE_WIDTH,
                            StrokeType.INSIDE);
            case CellDisplayMode.CIRCLE_BORDERED -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            backgroundColor,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.EMOJI -> {
                if (cellEmojiFont == null) {
                    yield (descriptor, painter, cell, stepCount) ->
                            painter.drawCellInnerCircle(
                                    cell.coordinate(),
                                    resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                                    null,
                                    NO_STROKE_LINE_WIDTH,
                                    StrokeType.INSIDE);
                }
                yield (descriptor, painter, cell, stepCount) ->
                        painter.drawCenteredTextInCell(
                                cell.coordinate(),
                                descriptor.emojiAsOptional().orElse("#"),
                                resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                                cellEmojiFont);
            }
        };
    }

    private Color resolveEntityFillColor(GridEntityDescriptor entityDescriptor,
                                         WatorEntity entity,
                                         int stepCount) {
        Color baseColor = entityDescriptor.colorOrFallback();
        Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
        if (colorMap != null) {
            Integer value = switch (entity) {
                case Fish fish -> fish.ageAtStepCount(stepCount);
                case Shark shark -> Math.min(maxColorSharkEnergy, shark.currentEnergy());
                case TerrainConstant ignored -> -1; // Not expected when colorMap is non-null
            };

            return colorMap.getOrDefault(value, baseColor);
        }
        return (entityDescriptor.color() != null) ? entityDescriptor.color() : FALLBACK_COLOR_AGENT;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<WatorEntity> oldGridCell,
                                          @Nullable GridCell<WatorEntity> newGridCell) {
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
    protected void drawSimulation(WritableGridModel<WatorEntity> currentModel, int stepCount, int lastDrawnStepCount) {
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
    protected List<SimulationUserActionDescriptor<WatorUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        WatorUserActionContext.ADD_FISH,
                        SimulationUserActionScope.CELL_SELECTED,
                        WATOR_TOOLBAR_ADD_FISH,
                        WATOR_TOOLBAR_ADD_FISH_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        WatorUserActionContext.ADD_SHARK,
                        SimulationUserActionScope.CELL_SELECTED,
                        WATOR_TOOLBAR_ADD_SHARK,
                        WATOR_TOOLBAR_ADD_SHARK_TOOLTIP));
    }

}

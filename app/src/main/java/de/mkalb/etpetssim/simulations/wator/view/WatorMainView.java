package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.shared.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionScope;
import de.mkalb.etpetssim.simulations.core.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.core.view.CanvasPainters;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.ControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.MainViewModel;
import de.mkalb.etpetssim.simulations.core.viewmodel.SimulationUserActionDescriptor;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.simulations.wator.model.WatorSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.entity.Fish;
import de.mkalb.etpetssim.simulations.wator.model.entity.Shark;
import de.mkalb.etpetssim.simulations.wator.model.entity.TerrainConstant;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import de.mkalb.etpetssim.simulations.wator.shared.WatorUserActionContext;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Main view of Wa-Tor.
 */
public final class WatorMainView
        extends AbstractMainView<
        WatorEntity,
        GridCell<WatorEntity>,
        WritableGridModel<WatorEntity>,
        WatorConfig,
        WatorStatistics,
        WatorSimulationManager,
        WatorUserActionContext,
        WatorObservationView> {

    private static final String WATOR_TOOL_ID_ADD_FISH = "wator.addFish";
    private static final String WATOR_TOOL_ID_ADD_SHARK = "wator.addShark";
    private static final String WATOR_TOOL_ID_REMOVE_CREATURE = "wator.removeCreature";
    private static final String WATOR_TOOLBAR_ADD_FISH = "wator.toolbar.addfish";
    private static final String WATOR_TOOLBAR_ADD_FISH_TOOLTIP = "wator.toolbar.addfish.tooltip";
    private static final String WATOR_TOOLBAR_ADD_SHARK = "wator.toolbar.addshark";
    private static final String WATOR_TOOLBAR_ADD_SHARK_TOOLTIP = "wator.toolbar.addshark.tooltip";
    private static final String WATOR_TOOLBAR_REMOVE_CREATURE = "wator.toolbar.removecreature";
    private static final String WATOR_TOOLBAR_REMOVE_CREATURE_TOOLTIP = "wator.toolbar.removecreature.tooltip";

    private static final double FISH_MAX_FACTOR_DELTA = -0.5d;
    private static final double SHARK_MAX_FACTOR_DELTA = 0.7d;
    private static final int FISH_GROUP_COUNT = 10;
    private static final int SHARK_GROUP_COUNT = 6;
    private static final int MAX_COLOR_SHARK_ENERGY_FACTOR = 3;
    private static final Color SELECTED_STROKE_COLOR = Color.rgb(255, 255, 120);
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private @Nullable Map<Integer, Color> fishColors;
    private @Nullable Map<Integer, Color> sharkColors;
    private @Nullable CellDrawer<WatorEntity> cellDrawer;
    private int maxColorSharkEnergy = 1;

    public WatorMainView(MainViewModel<WatorEntity, GridCell<WatorEntity>, WritableGridModel<WatorEntity>,
                                 WatorConfig, WatorStatistics, WatorSimulationManager,
                                 WatorUserActionContext> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         WatorConfigView configView,
                         ControlView controlView,
                         WatorObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
    }

    private static Color resolveVariantColor(@Nullable Map<Integer, Color> variantColors,
                                             int value,
                                             Color baseColor) {
        return (variantColors == null) ? baseColor : variantColors.getOrDefault(value, baseColor);
    }

    @Override
    protected void initializeSimulation(CanvasPainters painters, WatorConfig config, CellDimension cellDimension) {
        Color backgroundColor = entityDescriptorRegistry
                .requireByDescriptorId(WatorEntity.DESCRIPTOR_ID_WATER)
                .colorOrFallback();
        painters.base().fillCanvasBackground(backgroundColor);

        maxColorSharkEnergy = computeMaxColorSharkEnergy(config);
        fishColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(WatorEntity.DESCRIPTOR_ID_FISH),
                0, config.fishMaxAge() - 1, FISH_GROUP_COUNT, FISH_MAX_FACTOR_DELTA);
        sharkColors = computeBrightnessVariantsMap(
                entityDescriptorRegistry.requireByDescriptorId(WatorEntity.DESCRIPTOR_ID_SHARK),
                1, maxColorSharkEnergy, SHARK_GROUP_COUNT, SHARK_MAX_FACTOR_DELTA);

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
                Font emojiFont = cellEmojiFont;
                if (emojiFont == null) {
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
                                emojiFont);
            }
        };
    }

    @Override
    protected void handleSelectedGridCellChanged(FXGridCanvasPainter painter,
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
    protected void drawSimulation(CanvasPainters painters,
                                  WritableGridModel<WatorEntity> currentGridModel,
                                  int stepCount,
                                  int lastDrawnStepCount) {
        if (cellDrawer == null) {
            AppLogger.warn("WatorMainView: Skipping canvas draw: cellDrawer is not initialized");
            return;
        }

        painters.dynamic().clearCanvasBackground();

        currentGridModel.nonDefaultCells()
                .forEach(cell -> cellDrawer.draw(
                        entityDescriptorRegistry.requireByDescriptorId(cell.descriptorId()),
                        painters.dynamic(), cell, stepCount));
    }

    @Override
    protected List<SimulationUserActionDescriptor<WatorUserActionContext>> createUserActionDescriptors() {
        return List.of(
                new SimulationUserActionDescriptor<>(
                        WATOR_TOOL_ID_ADD_FISH,
                        WatorUserActionContext.ADD_FISH,
                        SimulationUserActionScope.CELL_SELECTED,
                        WATOR_TOOLBAR_ADD_FISH,
                        WATOR_TOOLBAR_ADD_FISH_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        WATOR_TOOL_ID_ADD_SHARK,
                        WatorUserActionContext.ADD_SHARK,
                        SimulationUserActionScope.CELL_SELECTED,
                        WATOR_TOOLBAR_ADD_SHARK,
                        WATOR_TOOLBAR_ADD_SHARK_TOOLTIP),
                new SimulationUserActionDescriptor<>(
                        WATOR_TOOL_ID_REMOVE_CREATURE,
                        WatorUserActionContext.REMOVE_CREATURE,
                        SimulationUserActionScope.CELL_SELECTED,
                        WATOR_TOOLBAR_REMOVE_CREATURE,
                        WATOR_TOOLBAR_REMOVE_CREATURE_TOOLTIP));
    }

    private int computeMaxColorSharkEnergy(WatorConfig config) {
        return Math.max(config.sharkBirthEnergy(), config.sharkMinReproductionEnergy()) * MAX_COLOR_SHARK_ENERGY_FACTOR;
    }

    private Color resolveEntityFillColor(GridEntityDescriptor entityDescriptor,
                                         WatorEntity entity,
                                         int stepCount) {
        Color baseColor = entityDescriptor.colorOrFallback();
        return switch (entity) {
            case Fish fish -> resolveVariantColor(fishColors, fish.ageAtStepCount(stepCount), baseColor);
            case Shark shark -> resolveVariantColor(sharkColors,
                    Math.min(maxColorSharkEnergy, shark.currentEnergy()), baseColor);
            case TerrainConstant _ -> baseColor;
        };
    }

}

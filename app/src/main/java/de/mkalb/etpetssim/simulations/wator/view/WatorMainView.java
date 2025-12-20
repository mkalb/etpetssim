package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptor;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.model.CellDisplayMode;
import de.mkalb.etpetssim.simulations.core.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.core.view.CellDrawer;
import de.mkalb.etpetssim.simulations.core.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorConfig;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;
import de.mkalb.etpetssim.ui.CellDimension;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WatorMainView
        extends AbstractDefaultMainView<
        WatorEntity,
        WritableGridModel<WatorEntity>,
        WatorConfig,
        WatorStatistics,
        WatorConfigView,
        WatorObservationView> {

    private static final Color FALLBACK_COLOR_AGENT = Color.WHITE;
    private static final Color FALLBACK_COLOR_BACKGROUND = Color.BLACK;
    private static final double FISH_MAX_FACTOR_DELTA = -0.5d;
    private static final double SHARK_MAX_FACTOR_DELTA = 0.7d;
    private static final int FISH_GROUP_COUNT = 10;
    private static final int SHARK_GROUP_COUNT = 6;
    private static final int MAX_COLOR_SHARK_ENERGY_FACTOR = 3;
    private static final Color SELECTED_STROKE_COLOR = Color.rgb(255, 255, 120);
    private static final double SELECTED_STROKE_LINE_WIDTH = 1.5d;

    private final Paint backgroundPaint;
    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private @Nullable CellDrawer<WatorEntity> cellDrawer;

    private int maxColorSharkEnergy = 1;

    public WatorMainView(DefaultMainViewModel<WatorEntity, WritableGridModel<WatorEntity>, WatorConfig,
                                 WatorStatistics> viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         WatorConfigView configView,
                         DefaultControlView controlView,
                         WatorObservationView observationView) {
        super(viewModel,
                configView,
                controlView,
                observationView,
                entityDescriptorRegistry);
        backgroundPaint = entityDescriptorRegistry
                .getRequiredByDescriptorId(WatorEntity.DESCRIPTOR_ID_WATER)
                .colorAsOptional().orElse(FALLBACK_COLOR_BACKGROUND);
        entityColors = HashMap.newHashMap(2);
        entityColors.put(WatorEntityDescribable.FISH.descriptorId(), null);
        entityColors.put(WatorEntityDescribable.SHARK.descriptorId(), null);
    }

    private int computeMaxColorSharkEnergy(WatorConfig config) {
        return Math.max(config.sharkBirthEnergy(), config.sharkMinReproductionEnergy()) * MAX_COLOR_SHARK_ENERGY_FACTOR;
    }

    @SuppressWarnings({"MagicNumber", "unused"})
    private double computeStrokeLineWidth(CellShape cellShape, CellDimension cellDimension) {
        if (cellDimension.innerRadius() < 2.0d) {
            return 0.0d;
        }
        return Math.log(cellDimension.innerRadius());
    }

    @Override
    protected void initSimulation(WatorConfig config, CellDimension cellDimension) {
        maxColorSharkEnergy = computeMaxColorSharkEnergy(config);
        entityColors.put(WatorEntityDescribable.FISH.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(WatorEntityDescribable.FISH.descriptorId()),
                        0, config.fishMaxAge() - 1, FISH_GROUP_COUNT, FISH_MAX_FACTOR_DELTA));
        entityColors.put(WatorEntityDescribable.SHARK.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(WatorEntityDescribable.SHARK.descriptorId()),
                        1, maxColorSharkEnergy, SHARK_GROUP_COUNT, SHARK_MAX_FACTOR_DELTA));

        double strokeLineWidth = config.cellDisplayMode().hasBorder() ?
                computeStrokeLineWidth(config.cellShape(), cellDimension) : 0.0d;

        cellDrawer = switch (config.cellDisplayMode()) {
            case CellDisplayMode.SHAPE -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            null,
                            strokeLineWidth);
            case CellDisplayMode.SHAPE_BORDERED -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCell(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            backgroundPaint,
                            strokeLineWidth);
            case CellDisplayMode.CIRCLE -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            null,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.CIRCLE_BORDERED -> (descriptor, painter, cell, stepCount) ->
                    painter.drawCellInnerCircle(
                            cell.coordinate(),
                            resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                            backgroundPaint,
                            strokeLineWidth,
                            StrokeType.INSIDE);
            case CellDisplayMode.EMOJI -> {
                if (cellEmojiFont == null) {
                    yield (descriptor, painter, cell, stepCount) ->
                            painter.drawCellInnerCircle(
                                    cell.coordinate(),
                                    resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                                    null,
                                    strokeLineWidth,
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

    private Paint resolveEntityFillColor(GridEntityDescriptor entityDescriptor,
                                         WatorEntity entity,
                                         int stepCount) {
        Paint paint = entityDescriptor.color();
        if (paint instanceof Color baseColor) {
            Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
            if (colorMap != null) {
                Integer value = switch (entity) {
                    case WatorFish fish -> fish.ageAtStepCount(stepCount);
                    case WatorShark shark -> Math.min(maxColorSharkEnergy, shark.currentEnergy());
                    default -> -1; // Illegal value
                };

                return colorMap.getOrDefault(value, baseColor);
            }
        } else if (paint == null) {
            paint = FALLBACK_COLOR_AGENT;
        }
        return paint;
    }

    @Override
    protected void handleGridCellSelected(FXGridCanvasPainter painter,
                                          @Nullable GridCell<WatorEntity> oldGridCell,
                                          @Nullable GridCell<WatorEntity> newGridCell) {
        if ((oldGridCell != null) && oldGridCell.entity().isAgent()) {
            painter.clearCanvasBackground();
        }
        if ((newGridCell != null) && newGridCell.entity().isAgent()) {
            painter.drawCellOuterCircle(newGridCell.coordinate(), null,
                    SELECTED_STROKE_COLOR, SELECTED_STROKE_LINE_WIDTH,
                    StrokeType.OUTSIDE);
        }
    }

    @Override
    protected void drawSimulation(WritableGridModel<WatorEntity> currentModel, int stepCount, int lastDrawnStepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        if (cellDrawer == null) {
            AppLogger.warn("CellDrawer is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.nonDefaultCells()
                    .forEachOrdered(cell ->
                            cellDrawer.draw(entityDescriptorRegistry.getRequiredByDescriptorId(cell.entity().descriptorId()),
                                    basePainter, cell, stepCount));
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}

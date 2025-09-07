package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.view.AbstractDefaultMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.viewmodel.DefaultMainViewModel;
import de.mkalb.etpetssim.simulations.wator.model.*;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import de.mkalb.etpetssim.ui.StrokeAdjustment;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WatorMainView
        extends AbstractDefaultMainView<
        WatorEntity,
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

    private int maxColorSharkEnergy = 1;

    public WatorMainView(DefaultMainViewModel<WatorEntity, WatorConfig, WatorStatistics> viewModel,
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

    @Override
    protected void initSimulation(WatorConfig config) {
        maxColorSharkEnergy = computeMaxColorSharkEnergy(config);
        entityColors.put(WatorEntityDescribable.FISH.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(WatorEntityDescribable.FISH.descriptorId()),
                        0, config.fishMaxAge() - 1, FISH_GROUP_COUNT, FISH_MAX_FACTOR_DELTA));
        entityColors.put(WatorEntityDescribable.SHARK.descriptorId(),
                computeBrightnessVariantsMap(entityDescriptorRegistry.getRequiredByDescriptorId(WatorEntityDescribable.SHARK.descriptorId()),
                        1, maxColorSharkEnergy, SHARK_GROUP_COUNT, SHARK_MAX_FACTOR_DELTA));
    }

    private @Nullable Map<Integer, Color> computeBrightnessVariantsMap(GridEntityDescriptor descriptor,
                                                                       int min,
                                                                       int max,
                                                                       int groupCount,
                                                                       double maxFactorDelta) {
        if (!(descriptor.color() instanceof Color baseColor)) {
            return null;
        }
        return FXPaintFactory.getBrightnessVariantsMap(baseColor, min, max, groupCount, maxFactorDelta);
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
                    StrokeAdjustment.OUTSIDE);
        }
    }

    @Override
    protected void drawSimulation(ReadableGridModel<WatorEntity> currentModel, int stepCount) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        basePainter.fillCanvasBackground(backgroundPaint);

        currentModel.nonDefaultCells()
                    .forEachOrdered(cell ->
                            GridEntityUtils.consumeDescriptorAt(
                                    cell.coordinate(),
                                    currentModel,
                                    entityDescriptorRegistry,
                                    descriptor ->
                                            basePainter.drawCell(
                                                    cell.coordinate(),
                                                    resolveEntityFillColor(descriptor, cell.entity(), stepCount),
                                                    null,
                                                    0.0d))
                    );
    }

    @Override
    protected List<Node> createModificationToolbarNodes() {
        return List.of();
    }

}


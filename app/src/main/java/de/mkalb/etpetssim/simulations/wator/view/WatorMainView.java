package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.view.DefaultControlView;
import de.mkalb.etpetssim.simulations.wator.model.*;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorMainViewModel;
import de.mkalb.etpetssim.ui.FXPaintFactory;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class WatorMainView
        extends AbstractMainView<WatorMainViewModel, WatorConfigView, DefaultControlView, WatorObservationView> {

    private final Map<String, @Nullable Map<Integer, Color>> entityColors;

    public WatorMainView(WatorMainViewModel viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         WatorConfigView configView,
                         DefaultControlView controlView,
                         WatorObservationView observationView) {
        super(viewModel,
                configView, controlView, observationView,
                entityDescriptorRegistry);

        entityColors = HashMap.newHashMap(2);
        entityColors.put(WatorEntityDescribable.FISH.descriptorId(), null);
        entityColors.put(WatorEntityDescribable.SHARK.descriptorId(), null);
    }

    @Override
    protected void registerViewModelListeners() {
        viewModel.setSimulationInitializedListener(this::initializeSimulationCanvas);
        viewModel.setSimulationStepListener(this::updateSimulationStep);
    }

    private void initializeSimulationCanvas() {
        double cellEdgeLength = viewModel.getCellEdgeLength();
        ReadableGridModel<WatorEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
        GridStructure structure = viewModel.getStructure();
        long currentStep = viewModel.getCurrentStep();

        AppLogger.info("Initialize canvas and painter with structure " + structure.toDisplayString() +
                " and cell edge length " + cellEdgeLength);

        createPainterAndUpdateCanvas(structure, cellEdgeLength);

        WatorConfig config = viewModel.getCurrentConfig();

        initializeEntityColorVariants(WatorEntityDescribable.FISH, 0, config.fishMaxAge() - 1, 3, false, 0.05d);
        initializeEntityColorVariants(WatorEntityDescribable.SHARK, 1, 30, 2, true, 0.05d);

        updateCanvasBorderPane(structure);

        drawCanvas(currentModel, currentStep);
        observationView.updateObservationLabels();
    }

    private void initializeEntityColorVariants(WatorEntityDescribable entityDescribable, int min, int max, int step, boolean brighten, double factorStep) {
        String descriptorId = entityDescribable.descriptorId();
        GridEntityDescriptor descriptor = entityDescriptorRegistry.getRequiredByDescriptorId(descriptorId);
        Paint paint = descriptor.color();
        if (paint instanceof Color baseColor) {
            Map<Integer, Color> colorMap = FXPaintFactory.getBrightnessVariantsMap(baseColor, min, max, step, brighten, factorStep);
            entityColors.put(descriptorId, colorMap);
        } else {
            AppLogger.warn("Descriptor " + descriptorId + " does not provide a Color for brightness variants.");
            entityColors.put(descriptorId, null);
        }
    }

    private void updateSimulationStep() {
        ReadableGridModel<WatorEntity> currentModel = Objects.requireNonNull(viewModel.getCurrentModel());
        long currentStep = viewModel.getCurrentStep();
        AppLogger.info("Drawing canvas for step " + currentStep);

        drawCanvas(currentModel, currentStep);
        observationView.updateObservationLabels();
    }

    private @Nullable Paint resolveEntityFillColor(GridEntityDescriptor entityDescriptor, WatorEntity entity, long step) {
        Paint paint = entityDescriptor.color();
        if (paint instanceof Color baseColor) {
            Map<Integer, Color> colorMap = entityColors.get(entityDescriptor.descriptorId());
            if (colorMap != null) {
                Integer value = switch (entity) {
                    case WatorFish fish -> fish.age(step);
                    case WatorShark shark -> Math.min(30, shark.currentEnergy());
                    default -> 0;
                };
                return colorMap.getOrDefault(value, baseColor);
            }
        }
        return paint;
    }

    private void fillBackground() {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }
        Paint background = entityDescriptorRegistry
                .getRequiredByDescriptorId(WatorEntity.DESCRIPTOR_ID_WATER)
                .colorAsOptional().orElse(Color.BLACK);
        basePainter.fillCanvasBackground(background);
    }

    private void drawCanvas(ReadableGridModel<WatorEntity> currentModel, long currentStep) {
        if (basePainter == null) {
            AppLogger.warn("Painter is not initialized, cannot draw canvas.");
            return;
        }

        fillBackground();

        currentModel.nonDefaultCells().forEachOrdered(cell ->
                GridEntityUtils.consumeDescriptorAt(
                        cell.coordinate(),
                        currentModel,
                        entityDescriptorRegistry,
                        descriptor -> basePainter.drawCell(
                                cell.coordinate(),
                                resolveEntityFillColor(descriptor, cell.entity(), currentStep),
                                null,
                                0.0d))
        );
    }

}


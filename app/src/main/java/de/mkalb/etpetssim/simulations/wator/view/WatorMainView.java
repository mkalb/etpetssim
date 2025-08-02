package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.SimulationView;
import de.mkalb.etpetssim.simulations.view.AbstractMainView;
import de.mkalb.etpetssim.simulations.wator.model.*;
import de.mkalb.etpetssim.simulations.wator.viewmodel.WatorMainViewModel;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXPaintBuilder;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

@SuppressWarnings("MagicNumber")
public final class WatorMainView extends AbstractMainView<WatorMainViewModel> implements SimulationView {

    private static final double INITIAL_CANVAS_SIZE = 100.0d;

    private final WatorConfigView configView;
    private final WatorControlView controlView;
    private final WatorObservationView observationView;
    private final GridEntityDescriptorRegistry entityDescriptorRegistry;
    private final Canvas baseCanvas;
    private final Canvas overlayCanvas;
    private final BorderPane canvasBorderPane;
    private final Map<String, @Nullable Map<Integer, Color>> entityColors;
    private @Nullable FXGridCanvasPainter basePainter;
    private @Nullable FXGridCanvasPainter overlayPainter;

    public WatorMainView(WatorMainViewModel viewModel,
                         GridEntityDescriptorRegistry entityDescriptorRegistry,
                         WatorConfigView configView,
                         WatorControlView controlView,
                         WatorObservationView observationView) {
        super(viewModel);
        this.configView = configView;
        this.observationView = observationView;
        this.controlView = controlView;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        overlayCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        canvasBorderPane = new BorderPane();

        entityColors = HashMap.newHashMap(2);
        entityColors.put(WatorEntityDescribable.FISH.descriptorId(), null);
        entityColors.put(WatorEntityDescribable.SHARK.descriptorId(), null);
    }

    @Override
    public Region buildViewRegion() {
        Region configRegion = configView.buildRegion();
        Region simulationRegion = createSimulationRegion();
        Region controlRegion = controlView.buildRegion();
        Region observationRegion = observationView.buildRegion();

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(configRegion);
        borderPane.setCenter(simulationRegion);
        borderPane.setBottom(controlRegion);
        borderPane.setRight(observationRegion);
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        registerViewModelListeners();

        return borderPane;
    }

    private void registerViewModelListeners() {
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

        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(6_000.0d, basePainter.gridDimension2D().getWidth()));
        baseCanvas.setHeight(Math.min(4_000.0d, basePainter.gridDimension2D().getHeight()));

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));

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
            Map<Integer, Color> colorMap = FXPaintBuilder.getBrightnessVariantsMap(baseColor, min, max, step, brighten, factorStep);
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

    private Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);

        ScrollPane scrollPane = new ScrollPane(canvasBorderPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return scrollPane;
    }

    private void updateCanvasBorderPane(GridStructure structure) {
        canvasBorderPane.setLeft((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_LEFT_BORDERPANE));
        canvasBorderPane.setRight((structure.edgeBehaviorX() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_RIGHT_BORDERPANE));
        canvasBorderPane.setTop((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_TOP_BORDERPANE));
        canvasBorderPane.setBottom((structure.edgeBehaviorY() == EdgeBehavior.WRAP) ? null :
                createBorderRegion(FXStyleClasses.SIMULATION_BOTTOM_BORDERPANE));
    }

    private Region createBorderRegion(String styleClass) {
        Region border = new Region();
        border.getStyleClass().add(styleClass);
        return border;
    }

}


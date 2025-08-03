package de.mkalb.etpetssim.simulations.view;

import de.mkalb.etpetssim.engine.EdgeBehavior;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.viewmodel.BaseMainViewModel;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jspecify.annotations.Nullable;

public abstract class AbstractMainView<T extends BaseMainViewModel> implements SimulationMainView {

    private static final double INITIAL_CANVAS_SIZE = 100.0d;

    protected final T viewModel;
    protected final GridEntityDescriptorRegistry entityDescriptorRegistry;
    protected final Canvas baseCanvas;
    protected final Canvas overlayCanvas;
    private final BorderPane canvasBorderPane;

    protected @Nullable FXGridCanvasPainter basePainter;
    protected @Nullable FXGridCanvasPainter overlayPainter;

    protected AbstractMainView(T viewModel,
                               GridEntityDescriptorRegistry entityDescriptorRegistry) {
        this.viewModel = viewModel;
        this.entityDescriptorRegistry = entityDescriptorRegistry;

        baseCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        overlayCanvas = new Canvas(INITIAL_CANVAS_SIZE, INITIAL_CANVAS_SIZE);
        baseCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);
        overlayCanvas.getStyleClass().add(FXStyleClasses.SIMULATION_CANVAS);

        canvasBorderPane = new BorderPane();
        canvasBorderPane.getStyleClass().add(FXStyleClasses.SIMULATION_CENTER_BORDERPANE);
    }

    @Override
    public final Region buildRegion() {
        BorderPane borderPane = buildMainBorderPane();
        borderPane.setCenter(createSimulationRegion());
        borderPane.getStyleClass().add(FXStyleClasses.VIEW_BORDERPANE);

        registerViewModelListeners();

        return borderPane;
    }

    public abstract BorderPane buildMainBorderPane();

    protected abstract void registerViewModelListeners();

    protected final Region createSimulationRegion() {
        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);
        stackPane.getStyleClass().add(FXStyleClasses.SIMULATION_STACKPANE);

        canvasBorderPane.setCenter(stackPane);

        ScrollPane scrollPane = new ScrollPane(canvasBorderPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add(FXStyleClasses.SIMULATION_SCROLLPANE);

        return scrollPane;
    }

    protected final void createPainterAndUpdateCanvas(GridStructure structure, double cellEdgeLength) {
        basePainter = new FXGridCanvasPainter(baseCanvas, structure, cellEdgeLength);
        baseCanvas.setWidth(Math.min(6_000.0d, basePainter.gridDimension2D().getWidth()));
        baseCanvas.setHeight(Math.min(4_000.0d, basePainter.gridDimension2D().getHeight()));

        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellEdgeLength);
        overlayCanvas.setWidth(Math.min(6_000.0d, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(4_000.0d, overlayPainter.gridDimension2D().getHeight()));
    }

    protected final void updateCanvasBorderPane(GridStructure structure) {
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
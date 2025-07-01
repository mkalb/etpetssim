package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.GridGeometry;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Builder;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("MagicNumber")
public class SimulationLabViewBuilder implements Builder<Region> {

    private static final Color MOUSE_CLICK_COLOR = Color.CRIMSON;
    private static final Color MOUSE_HOVER_COLOR = Color.DARKSLATEBLUE;
    private static final double MOUSE_CLICK_LINE_WIDTH = 4.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;
    private final GridStructure structure;
    private final FXGridCanvasPainter painter;
    private final FXGridCanvasPainter overlayPainter;
    private final Font font;
    private @Nullable GridCoordinate lastClickedCoordinate = null;

    public SimulationLabViewBuilder(GridStructure structure, double cellSideLength) {
        this.structure = structure;

        Canvas canvas = new Canvas(cellSideLength, cellSideLength);
        painter = new FXGridCanvasPainter(canvas, structure, cellSideLength);
        double border = 30.0d; // only for testing grid dimension
        canvas.setWidth(Math.min(5_000.0d, painter.gridDimension2D().getWidth() + border));
        canvas.setHeight(Math.min(3_000.0d, painter.gridDimension2D().getHeight() + border));

        Canvas overlayCanvas = new Canvas(cellSideLength, cellSideLength);
        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellSideLength);
        overlayCanvas.setWidth(Math.min(5_000.0d, overlayPainter.gridDimension2D().getWidth()));
        overlayCanvas.setHeight(Math.min(3_000.0d, overlayPainter.gridDimension2D().getHeight()));
        font = Font.font("SansSerif", 14.0d);
    }

    @Override
    public Region build() {
        Canvas baseCanvas = painter.canvas();
        Canvas overlayCanvas = overlayPainter.canvas();

        StackPane stackPane = new StackPane(baseCanvas, overlayCanvas);
        StackPane.setAlignment(baseCanvas, Pos.TOP_LEFT);
        StackPane.setAlignment(overlayCanvas, Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(stackPane);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("simulation-scroll-pane");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(scrollPane);

        registerEvents();

        drawCanvas();

        return borderPane;
    }

    private void registerEvents() {
        Canvas overlayCanvas = overlayPainter.canvas();
        overlayCanvas.setOnMouseClicked(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), painter.cellDimension(), structure);
            overlayPainter.clearCanvasBackground();
            if (overlayPainter.isOutsideGrid(coordinate)) {
                lastClickedCoordinate = null;
            } else {
                if (!coordinate.equals(lastClickedCoordinate)) {
                    lastClickedCoordinate = coordinate;
                    overlayPainter.drawOuterCircle(coordinate, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH);
                } else {
                    lastClickedCoordinate = null;
                }
                overlayPainter.drawInnerCircle(coordinate, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
            }
        });

        overlayCanvas.setOnMouseMoved(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), painter.cellDimension(), structure);
            overlayPainter.clearCanvasBackground();
            if (lastClickedCoordinate != null) {
                overlayPainter.drawOuterCircle(lastClickedCoordinate, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH);
            }
            if (!coordinate.isIllegal() && !overlayPainter.isOutsideGrid(coordinate)) {
                overlayPainter.drawInnerCircle(coordinate, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
            }
        });
    }

    private void drawCanvas() {
        painter.fillCanvasBackground(Color.BLACK);
        painter.fillGridBackground(Color.DIMGRAY);

        structure.allCoordinates().forEachOrdered(coordinate -> {
            painter.fillCell(coordinate, calculateColumnSimilarityColor(coordinate));
            painter.drawCenteredTextInCell(coordinate, coordinate.asString(), Color.BLACK, font);
        });

        painter.drawBoundingBox(new GridCoordinate(10, 1), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(10, 2), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(10, 3), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(10, 4), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(15, 1), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(15, 2), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(15, 3), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawBoundingBox(new GridCoordinate(15, 4), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);

        painter.drawInnerCircle(new GridCoordinate(2, 2), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawInnerCircle(new GridCoordinate(2, 3), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawInnerCircle(new GridCoordinate(3, 2), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);
        painter.drawInnerCircle(new GridCoordinate(3, 3), Color.BLACK, MOUSE_HOVER_LINE_WIDTH);

        painter.drawOuterCircle(new GridCoordinate(4, 4), Color.WHITE, MOUSE_HOVER_LINE_WIDTH);
        painter.drawOuterCircle(new GridCoordinate(4, 5), Color.WHITE, MOUSE_HOVER_LINE_WIDTH);
        painter.drawOuterCircle(new GridCoordinate(5, 4), Color.WHITE, MOUSE_HOVER_LINE_WIDTH);
        painter.drawOuterCircle(new GridCoordinate(5, 5), Color.WHITE, MOUSE_HOVER_LINE_WIDTH);

        painter.fillAndStrokeSquareInset(new GridCoordinate(2, 8), Color.WHITE, Color.BLACK, 10.0d);
    }

    private Color calculateColumnSimilarityColor(GridCoordinate coordinate) {
        int columnGroup = coordinate.x() % 2;
        int rowGroup = coordinate.y() % 2;

        return switch ((columnGroup << 1) | rowGroup) {
            case 0 -> Color.LIGHTSKYBLUE;       // Column 0, Row 0
            case 1 -> Color.LIGHTSTEELBLUE;     // Column 0, Row 1 (cooler blue)
            case 2 -> Color.PALEGREEN;          // Column 1, Row 0
            case 3 -> Color.MEDIUMAQUAMARINE;   // Column 1, Row 1 (stronger green)
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

}

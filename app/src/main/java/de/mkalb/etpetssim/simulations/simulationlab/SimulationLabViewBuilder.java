package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import de.mkalb.etpetssim.ui.GridGeometry;
import de.mkalb.etpetssim.ui.StrokeAdjustment;
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

    private static final Color MOUSE_CLICK_COLOR = Color.ROSYBROWN;
    private static final Color MOUSE_HOVER_COLOR = Color.DARKSLATEBLUE;
    private static final Color TEXT_COLOR = Color.DARKSLATEGRAY;
    private static final Color CANVAS_COLOR = Color.BLACK;
    private static final Color GRID_BACKGROUND_COLOR = Color.DIMGRAY;
    private static final Color TRANSLUCENT_WHITE = new Color(1.0, 1.0, 1.0, 0.2); // for lightening effect
    private static final Color TRANSLUCENT_BLACK = new Color(0.0, 0.0, 0.0, 0.2); // for darkening effect

    private static final double MOUSE_CLICK_LINE_WIDTH = 8.0d;
    private static final double MOUSE_HOVER_LINE_WIDTH = 2.0d;

    private final GridStructure structure;
    private final FXGridCanvasPainter painter;
    private final FXGridCanvasPainter overlayPainter;
    private final Font font;
    private @Nullable GridCoordinate lastClickedCoordinate = null;
    private @Nullable GridCoordinate lastHoverCoordinate = null;

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

        drawCanvas(false);
        // drawCanvas(true);
        drawTest();

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
                    overlayPainter.drawCellOuterCircle(coordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);
                } else {
                    lastClickedCoordinate = null;
                }
            }
        });

        overlayCanvas.setOnMouseMoved(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), painter.cellDimension(), structure);
            if (!coordinate.equals(lastHoverCoordinate)) {
                overlayPainter.clearCanvasBackground();
                if (!coordinate.isIllegal() && !overlayPainter.isOutsideGrid(coordinate)) {
                    lastHoverCoordinate = coordinate;
                    overlayPainter.drawCellInnerCircle(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.INSIDE);
                    overlayPainter.drawCellBoundingBox(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH, StrokeAdjustment.OUTSIDE);

                    overlayPainter.drawHexagonMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                    overlayPainter.drawTriangleMatchingCellWidth(coordinate, null, MOUSE_HOVER_COLOR, MOUSE_HOVER_LINE_WIDTH);
                }
                if (lastClickedCoordinate != null) {
                    overlayPainter.drawCellOuterCircle(lastClickedCoordinate, TRANSLUCENT_WHITE, MOUSE_CLICK_COLOR, MOUSE_CLICK_LINE_WIDTH, StrokeAdjustment.OUTSIDE);
                }
            }
        });
    }

    private void drawCanvas(boolean drawCellAsInnerCircle) {
        // Background
        painter.fillCanvasBackground(CANVAS_COLOR);
        painter.fillGridBackground(GRID_BACKGROUND_COLOR);

        // Cells at all coordinates
        structure.allCoordinates().forEachOrdered(coordinate -> {
            if (drawCellAsInnerCircle) {
                painter.drawCellInnerCircle(coordinate, calculateColumnSimilarityColor(coordinate), null, 0.0d, StrokeAdjustment.CENTERED);
            } else {
                painter.fillCell(coordinate, calculateColumnSimilarityColor(coordinate));
            }

            painter.drawCenteredTextInCell(coordinate, coordinate.asString(), TEXT_COLOR, font);
        });
    }

    private void drawTest() {
        Color t1 = new Color(1.0, 0.0, 1.0, 0.5);
        Color t2 = new Color(1.0, 1.0, 0.0, 0.5);

        painter.drawCellBoundingBox(new GridCoordinate(2, 4), t1, t2, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(2, 6), t2, t1, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(2, 8), t1, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(2, 10), t2, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        painter.drawCellBoundingBox(new GridCoordinate(4, 4), null, t2, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(4, 6), null, t1, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(4, 8), null, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        painter.drawCellBoundingBox(new GridCoordinate(4, 10), null, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        painter.drawCellInnerCircle(new GridCoordinate(6, 4), t1, t2, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(6, 6), t2, t1, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(6, 8), t1, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(6, 10), t2, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        painter.drawCellInnerCircle(new GridCoordinate(8, 4), null, t2, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(8, 6), null, t1, 8.0d, StrokeAdjustment.INSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(8, 8), null, t2, 8.0d, StrokeAdjustment.OUTSIDE);
        painter.drawCellInnerCircle(new GridCoordinate(8, 10), null, t1, 8.0d, StrokeAdjustment.OUTSIDE);

        for (int x = 50; x < 100; x++) {
            painter.drawPixelDirect(x * 4, 100, Color.MAGENTA);
            painter.drawPixelRect(x * 4, 120, Color.RED);
        }

        painter.drawTriangle(new GridCoordinate(11, 4),
                GridGeometry.convertSideLengthToMatchWidth(painter.cellDimension().sideLength(), painter.gridStructure().cellShape(), CellShape.TRIANGLE),
                Color.WHITE, Color.BLACK, 4.0d);
        painter.drawHexagon(new GridCoordinate(9, 3),
                GridGeometry.convertSideLengthToMatchWidth(painter.cellDimension().sideLength(), painter.gridStructure().cellShape(), CellShape.HEXAGON),
                Color.WHITE, Color.BLACK, 4.0d);
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

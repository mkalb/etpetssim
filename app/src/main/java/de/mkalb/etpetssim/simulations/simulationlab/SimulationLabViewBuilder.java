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
import javafx.util.Builder;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("MagicNumber")
public class SimulationLabViewBuilder implements Builder<Region> {

    private final GridStructure structure;
    private final FXGridCanvasPainter painter;
    private final FXGridCanvasPainter overlayPainter;
    private @Nullable GridCoordinate lastClickedCoordinate = null;

    public SimulationLabViewBuilder(GridStructure structure, double cellSideLength) {
        this.structure = structure;

        Canvas canvas = new Canvas(cellSideLength, cellSideLength);
        painter = new FXGridCanvasPainter(canvas, structure, cellSideLength);
        double border = 30.0d; // only for testing grid dimension
        canvas.setWidth(painter.gridDimension2D().getWidth() + border);
        canvas.setHeight(painter.gridDimension2D().getHeight() + border);

        Canvas overlayCanvas = new Canvas(cellSideLength, cellSideLength);
        overlayPainter = new FXGridCanvasPainter(overlayCanvas, structure, cellSideLength);
        overlayCanvas.setWidth(overlayPainter.gridDimension2D().getWidth());
        overlayCanvas.setHeight(overlayPainter.gridDimension2D().getHeight());
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

        drawCanvas();

        Color transparentRed = new Color(1, 0, 0, 0.5);
        overlayCanvas.setOnMouseClicked(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), painter.cellDimension(), structure.cellShape());
            if (overlayPainter.isOutsideGrid(coordinate)) {
                System.out.println("Clicked on coordinate OUTSIDE!!!: " + coordinate);
            } else {
                System.out.println("Clicked on coordinate: " + coordinate);

                if (!coordinate.equals(lastClickedCoordinate)) {
                    lastClickedCoordinate = coordinate;
                    overlayPainter.clearCanvasBackground();
                    overlayPainter.drawOuterCircle(coordinate, transparentRed, 4.0d);
                } else {
                    lastClickedCoordinate = null;
                    overlayPainter.clearCanvasBackground();
                }
                overlayPainter.drawInnerCircle(coordinate, Color.GRAY, 2.0d);
            }
        });

        overlayCanvas.setOnMouseMoved(event -> {
            GridCoordinate coordinate = GridGeometry.fromCanvasPosition(new Point2D(event.getX(), event.getY()), painter.cellDimension(), structure.cellShape());
            if (overlayPainter.isOutsideGrid(coordinate)) {
                System.out.println("Moved over coordinate OUTSIDE!!!: " + coordinate);
            } else {
                System.out.println("Moved over coordinate: " + coordinate);
                overlayPainter.clearCanvasBackground();
                overlayPainter.drawInnerCircle(coordinate, Color.GRAY, 2.0d);

                if (lastClickedCoordinate != null) {
                    overlayPainter.drawOuterCircle(lastClickedCoordinate, transparentRed, 4.0d);
                }
            }
        });
        return borderPane;
    }

    private void drawCanvas() {
        painter.fillCanvasBackground(Color.PINK);
        painter.fillGridBackground(Color.ORANGE);

        structure.allCoordinates().forEachOrdered(coordinate -> painter.fillCell(coordinate, calculateTestColor(coordinate)));

        painter.drawBoundingBox(new GridCoordinate(10, 1), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(10, 2), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(10, 3), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(10, 4), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(15, 1), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(15, 2), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(15, 3), Color.BLACK, 2.0d);
        painter.drawBoundingBox(new GridCoordinate(15, 4), Color.BLACK, 2.0d);

        painter.drawInnerCircle(new GridCoordinate(2, 2), Color.BLACK, 2.0d);
        painter.drawInnerCircle(new GridCoordinate(2, 3), Color.BLACK, 2.0d);
        painter.drawInnerCircle(new GridCoordinate(3, 2), Color.BLACK, 2.0d);
        painter.drawInnerCircle(new GridCoordinate(3, 3), Color.BLACK, 2.0d);

        painter.drawOuterCircle(new GridCoordinate(4, 4), Color.WHITE, 2.0d);
        painter.drawOuterCircle(new GridCoordinate(4, 5), Color.WHITE, 2.0d);
        painter.drawOuterCircle(new GridCoordinate(5, 4), Color.WHITE, 2.0d);
        painter.drawOuterCircle(new GridCoordinate(5, 5), Color.WHITE, 2.0d);

        painter.fillAndStrokeSquareInset(new GridCoordinate(2, 8), Color.WHITE, Color.BLACK, 10.0d);
    }

    private Color calculateTestColor(GridCoordinate coordinate) {
        return switch (((coordinate.x() % 2) << 1) | (coordinate.y() % 2)) {
            case 0 -> Color.DARKBLUE;
            case 1 -> Color.DARKGREEN;
            case 2 -> Color.LIGHTBLUE;
            case 3 -> Color.LIGHTGREEN;
            default -> throw new IllegalStateException("Unexpected value");
        };
    }

}

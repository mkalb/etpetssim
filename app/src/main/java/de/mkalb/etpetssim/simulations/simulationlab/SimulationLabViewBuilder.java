package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Builder;

import java.util.function.*;

@SuppressWarnings("MagicNumber")
public class SimulationLabViewBuilder implements Builder<Region> {

    private final GridStructure structure;
    private final FXGridCanvasPainter painter;

    public SimulationLabViewBuilder(GridStructure structure, double cellSideLength) {
        this.structure = structure;

        Canvas canvas = new Canvas(cellSideLength, cellSideLength);
        painter = new FXGridCanvasPainter(canvas, structure, cellSideLength);
        double border = 10.0d; // only for testing
        canvas.setWidth(painter.gridDimension2D().getWidth() + border);
        canvas.setHeight(painter.gridDimension2D().getHeight() + border);
    }

    @Override
    public Region build() {
        ScrollPane scrollPane = new ScrollPane(painter.canvas());
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.getStyleClass().add("simulation-scroll-pane");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(scrollPane);

        drawCanvas();

        return borderPane;
    }

    private void drawCanvas() {
        painter.fillCanvasBackground(Color.PINK);
        painter.fillGridBackground(Color.ORANGE);

        Consumer<GridCoordinate> consumerSpecialDrawMethods = coordinate -> {
            switch (structure.cellShape()) {
                case SQUARE -> drawSquareCell(coordinate);
                case TRIANGLE -> drawTriangleCell(coordinate);
                case HEXAGON -> drawHexagonCell(coordinate);
            }
        };
        Consumer<GridCoordinate> consumerDrawCell = this::drawCell;
        structure.allCoordinates().forEachOrdered(consumerDrawCell);

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

    private void drawCell(GridCoordinate coordinate) {
        painter.fillCell(coordinate, calculateTestColor(coordinate));
    }

    private void drawSquareCell(GridCoordinate coordinate) {
        if ((coordinate.x() % 3) == 1) {
            painter.fillAndStrokeSquareInset(coordinate, calculateTestColor(coordinate), Color.DARKMAGENTA, 15.0d);
        } else {
            painter.fillSquare(coordinate, calculateTestColor(coordinate));
        }
    }

    private void drawTriangleCell(GridCoordinate coordinate) {
        if ((coordinate.x() % 3) == 1) {
            painter.fillAndStrokeTriangle(coordinate, calculateTestColor(coordinate), Color.DARKMAGENTA, 7.0d);
        } else {
            painter.fillTriangle(coordinate, calculateTestColor(coordinate));
        }
    }

    private void drawHexagonCell(GridCoordinate coordinate) {
        if ((coordinate.x() % 3) == 1) {
            painter.fillAndStrokeHexagon(coordinate, calculateTestColor(coordinate), Color.DARKMAGENTA, 7.0d);
        } else {
            painter.fillHexagon(coordinate, calculateTestColor(coordinate));
        }
    }

}

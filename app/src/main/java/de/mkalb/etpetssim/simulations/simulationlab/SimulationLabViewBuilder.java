package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.ui.FXGridCanvasPainter;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Builder;

public class SimulationLabViewBuilder implements Builder<Region> {

    private final GridStructure structure;
    private final FXGridCanvasPainter painter;

    public SimulationLabViewBuilder(GridStructure structure, double cellSideLength) {
        this.structure = structure;
        Canvas canvas = new Canvas(
                (structure.size().width() * cellSideLength) + (2 * cellSideLength),
                (structure.size().height() * cellSideLength) + (2 * cellSideLength));
        painter = new FXGridCanvasPainter(canvas, structure, cellSideLength);
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

        if (structure.cellShape() == CellShape.SQUARE) {
            structure.allCoordinates().forEachOrdered(this::drawSquareCell);
        } else if (structure.cellShape() == CellShape.TRIANGLE) {
            structure.allCoordinates().forEachOrdered(this::drawTriangleCell);
        } else {
            structure.allCoordinates().forEachOrdered(this::drawHexagonCell);
        }
    }

    private void drawSquareCell(GridCoordinate coordinate) {
        if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillSquare(coordinate, Color.DARKBLUE);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillSquare(coordinate, Color.LIGHTBLUE);
        } else if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillSquare(coordinate, Color.DARKGREEN);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillSquare(coordinate, Color.LIGHTGREEN);
        }
    }

    private void drawTriangleCell(GridCoordinate coordinate) {
        if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillTriangle(coordinate, Color.DARKBLUE);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillTriangle(coordinate, Color.LIGHTBLUE);
        } else if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillTriangle(coordinate, Color.DARKGREEN);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillTriangle(coordinate, Color.LIGHTGREEN);
        }
    }

    private void drawHexagonCell(GridCoordinate coordinate) {
        if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillHexagon(coordinate, Color.DARKBLUE);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillHexagon(coordinate, Color.LIGHTBLUE);
        } else if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillHexagon(coordinate, Color.DARKGREEN);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillHexagon(coordinate, Color.LIGHTGREEN);
        }
    }

}

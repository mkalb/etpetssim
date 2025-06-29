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

public class SimulationLabViewBuilder implements Builder<Region> {

    private final GridStructure structure;
    private final FXGridCanvasPainter painter;

    public SimulationLabViewBuilder(GridStructure structure, double cellSideLength) {
        this.structure = structure;

        Canvas canvas = new Canvas(cellSideLength, cellSideLength);
        painter = new FXGridCanvasPainter(canvas, structure, cellSideLength);
        double border = 10.0d; // only for testing
        canvas.setWidth(painter.gridDimension().getWidth() + border);
        canvas.setHeight(painter.gridDimension().getHeight() + border);
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

        structure.allCoordinates().forEachOrdered(coordinate -> {
            switch (structure.cellShape()) {
                case SQUARE -> drawSquareCell(coordinate);
                case TRIANGLE -> drawTriangleCell(coordinate);
                case HEXAGON -> drawHexagonCell(coordinate);
            }
        });
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

    private void drawSquareCell(GridCoordinate coordinate) {
        painter.fillSquare(coordinate, calculateTestColor(coordinate));
    }

    private void drawTriangleCell(GridCoordinate coordinate) {
        painter.fillTriangle(coordinate, calculateTestColor(coordinate));
    }

    private void drawHexagonCell(GridCoordinate coordinate) {
        painter.fillHexagon(coordinate, calculateTestColor(coordinate));
    }

}

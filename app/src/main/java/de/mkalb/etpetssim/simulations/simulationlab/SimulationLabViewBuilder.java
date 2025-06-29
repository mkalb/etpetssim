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

@SuppressWarnings("MagicNumber")
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

        structure.allCoordinates().forEachOrdered(this::drawCell);
    }

    private void drawCell(GridCoordinate coordinate) {
        if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillSquare(coordinate, Color.LIGHTGREY);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) == 0)) {
            painter.fillAndStrokeSquare(coordinate, Color.LIGHTBLUE, Color.DARKBLUE, 0.0d);
        } else if (((coordinate.x() % 2) == 0) && ((coordinate.y() % 2) != 0)) {
            painter.fillAndStrokeSquare(coordinate, Color.LIGHTCYAN, Color.DARKCYAN, 31.5d);
        } else if (((coordinate.x() % 2) != 0) && ((coordinate.y() % 2) != 0)) {
            //     painter.fillSquare(coordinate, Color.LIGHTGREEN);
        }
    }

}

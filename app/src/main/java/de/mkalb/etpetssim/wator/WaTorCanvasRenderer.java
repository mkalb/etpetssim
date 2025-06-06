package de.mkalb.etpetssim.wator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public final class WaTorCanvasRenderer {

    private final WaTorModel waTorModel;
    private final Canvas simulationCanvas;

    public WaTorCanvasRenderer(WaTorModel waTorModel) {
        this.waTorModel = waTorModel;
        simulationCanvas = new Canvas(calculateCanvasWidth(), calculateCanvasHeight());
    }

    Canvas simulationCanvas() {
        return simulationCanvas;
    }

    private int calculateCanvasWidth() {
        return waTorModel.xSize() * waTorModel.cellLength();
    }

    private int calculateCanvasHeight() {
        return waTorModel.ySize() * waTorModel.cellLength();
    }

    void prepareStart() {
        simulationCanvas.setWidth(calculateCanvasWidth());
        simulationCanvas.setHeight(calculateCanvasHeight());
    }

    void draw() {
        GraphicsContext simulationGraphicsContext = simulationCanvas.getGraphicsContext2D();
        double width = simulationCanvas.getWidth();
        double height = simulationCanvas.getHeight();
        simulationGraphicsContext.clearRect(0, 0, width, height);
        simulationGraphicsContext.setFill(Color.BLUE);
        simulationGraphicsContext.fillRect(0, 0, width, height);
        simulationGraphicsContext.setFill(Color.BLACK);
        simulationGraphicsContext.fillText(String.valueOf(waTorModel.timeCounter()), 100, 100);
    }

}

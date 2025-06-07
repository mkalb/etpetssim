package de.mkalb.etpetssim.wator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.*;

public final class WaTorCanvasRenderer {

    private final WaTorModel waTorModel;
    private final Canvas simulationCanvas;
    private final Function<WaTorCoordinate, Optional<WaTorSeaCreature>> creatureFunction;

    public WaTorCanvasRenderer(WaTorModel waTorModel, Function<WaTorCoordinate, Optional<WaTorSeaCreature>> creatureFunction) {
        this.waTorModel = waTorModel;
        this.creatureFunction = creatureFunction;
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

    Color adjustColor(Color color, WaTorShark shark) {
        double energyFactor = Math.min(shark.currentEnergy() / 10.0, 1.0);
        return color.interpolate(Color.YELLOW, energyFactor);
    }

    Color adjustColor(Color color, WaTorFish fish) {
        double ageFactor = Math.min((waTorModel.timeCounter() - fish.timeOfBirth()) / 100.0, 1.0);
        return color.interpolate(Color.ORANGE, ageFactor);
    }

    void draw() {
        GraphicsContext simulationGraphicsContext = simulationCanvas.getGraphicsContext2D();
        double width = simulationCanvas.getWidth();
        double height = simulationCanvas.getHeight();
        simulationGraphicsContext.setFill(Color.BLUE);
        simulationGraphicsContext.fillRect(0, 0, width, height);
        for (int x = 0; x < waTorModel.xSize(); x++) {
            for (int y = 0; y < waTorModel.ySize(); y++) {
                Optional<WaTorSeaCreature> creature = creatureFunction.apply(new WaTorCoordinate(x, y));
                if (creature.isPresent()) {
                    simulationGraphicsContext.setFill(
                            switch (creature.orElseThrow()) {
                                case WaTorFish fish -> adjustColor(Color.GREEN, fish);
                                case WaTorShark shark -> adjustColor(Color.RED, shark);
                            });
                    simulationGraphicsContext.fillRect(x * waTorModel.cellLength(), y * waTorModel.cellLength(), waTorModel.cellLength(), waTorModel.cellLength());
                }
            }
        }
    }

}

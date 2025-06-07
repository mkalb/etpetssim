package de.mkalb.etpetssim.wator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.*;

public final class WaTorCanvasRenderer {

    private final WaTorConfigModel waTorConfigModel;
    private final Canvas simulationCanvas;
    private final Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction;

    public WaTorCanvasRenderer(WaTorConfigModel waTorConfigModel, Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction) {
        this.waTorConfigModel = waTorConfigModel;
        this.creatureFunction = creatureFunction;
        simulationCanvas = new Canvas(calculateCanvasWidth(), calculateCanvasHeight());
    }

    Canvas simulationCanvas() {
        return simulationCanvas;
    }

    private int calculateCanvasWidth() {
        return waTorConfigModel.xSize() * waTorConfigModel.cellLength();
    }

    private int calculateCanvasHeight() {
        return waTorConfigModel.ySize() * waTorConfigModel.cellLength();
    }

    void prepareStart() {
        simulationCanvas.setWidth(calculateCanvasWidth());
        simulationCanvas.setHeight(calculateCanvasHeight());
    }

    Color adjustColor(Color color, WaTorShark shark) {
        double energyFactor = Math.min(shark.currentEnergy() / 10.0, 1.0);
        double ageFactor = Math.min((shark.age(waTorConfigModel.timeCounter())) / 100.0, 1.0);
        return color.interpolate(Color.RED, energyFactor).interpolate(Color.BLACK, ageFactor);
    }

    Color adjustColor(Color color, WaTorFish fish) {
        double ageFactor = Math.min((fish.age(waTorConfigModel.timeCounter())) / 100.0, 1.0);
        return color.interpolate(Color.BLACK, ageFactor);
    }

    void draw() {
        GraphicsContext simulationGraphicsContext = simulationCanvas.getGraphicsContext2D();
        double width = simulationCanvas.getWidth();
        double height = simulationCanvas.getHeight();
        simulationGraphicsContext.setFill(Color.DARKBLUE.darker());
        simulationGraphicsContext.fillRect(0, 0, width, height);
        for (int x = 0; x < waTorConfigModel.xSize(); x++) {
            for (int y = 0; y < waTorConfigModel.ySize(); y++) {
                Optional<WaTorCreature> creature = creatureFunction.apply(new WaTorCoordinate(x, y));
                if (creature.isPresent()) {
                    simulationGraphicsContext.setFill(
                            switch (creature.orElseThrow()) {
                                case WaTorFish fish -> adjustColor(Color.GREEN, fish);
                                case WaTorShark shark -> adjustColor(Color.ORANGE, shark);
                            });
                    simulationGraphicsContext.fillRect(x * waTorConfigModel.cellLength(), y * waTorConfigModel.cellLength(), waTorConfigModel.cellLength(), waTorConfigModel.cellLength());
                }
            }
        }
    }

}

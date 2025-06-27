package de.mkalb.etpetssim.simulations.wator;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.function.*;

public final class WaTorCanvasRenderer {

    private static final int MAX_ALLOWED_DRAW_TIME = 200;
    private static final int MAX_ALLOWED_AVG_DRAW_TIME = 150;
    private static final double MAX_PROPORTION_OF_DRAW_TIME_TO_SPEED = 0.5d;
    private static final int FORCE_EVAL_DRAW_TIME = 100;
    private static final int FREQ_MIN = 2;
    private static final int FREQ_MAX = 30;
    private static final Color COLOR_WATER = Color.DARKBLUE.darker();
    private final WaTorConfigModel waTorConfigModel;
    private final WaTorSimulationModel waTorSimulationModel;
    private final Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction;
    private final Canvas simulationCanvas;
    private int drawEveryNFrames;
    private long totalDrawTime;
    private int drawCount;
    private int frameCounter;
    private boolean forceEvaluation;

    public WaTorCanvasRenderer(WaTorConfigModel waTorConfigModel,
                               WaTorSimulationModel waTorSimulationModel,
                               Function<WaTorCoordinate, Optional<WaTorCreature>> creatureFunction) {
        this.waTorConfigModel = waTorConfigModel;
        this.waTorSimulationModel = waTorSimulationModel;
        this.creatureFunction = creatureFunction;
        simulationCanvas = new Canvas(calculateCanvasWidth(), calculateCanvasHeight());
        drawEveryNFrames = 1;
        totalDrawTime = 0;
        drawCount = 0;
        frameCounter = 0;
        forceEvaluation = true;
    }

    private int calculateCanvasWidth() {
        return waTorConfigModel.xSize() * waTorConfigModel.cellLength();
    }

    private int calculateCanvasHeight() {
        return waTorConfigModel.ySize() * waTorConfigModel.cellLength();
    }

    Canvas simulationCanvas() {
        return simulationCanvas;
    }

    void prepareInitialStart() {
        simulationCanvas.setWidth(calculateCanvasWidth());
        simulationCanvas.setHeight(calculateCanvasHeight());
        prepareTimelineStart();
    }

    void prepareTimelineStart() {
        drawEveryNFrames = 1;
        totalDrawTime = 0;
        drawCount = 0;
        frameCounter = 0;
        forceEvaluation = true;
        drawSimulationCanvas();
    }

    private Color adjustColor(Color color, WaTorShark shark) {
        double energyFactor = Math.min(shark.currentEnergy() / 10.0, 1.0);
        double ageFactor = Math.min((shark.age(waTorSimulationModel.timeCounter())) / 100.0, 1.0);
        return color.interpolate(Color.RED, energyFactor).interpolate(Color.BLACK, ageFactor);
    }

    private Color adjustColor(Color color, WaTorFish fish) {
        double ageFactor = Math.min((fish.age(waTorSimulationModel.timeCounter())) / 100.0, 1.0);
        return color.interpolate(Color.BLACK, ageFactor);
    }

    private void drawSimulationCanvas() {
        GraphicsContext simulationGraphicsContext = simulationCanvas.getGraphicsContext2D();

        // Draw water background
        simulationGraphicsContext.setFill(COLOR_WATER);
        simulationGraphicsContext.fillRect(0, 0, simulationCanvas.getWidth(), simulationCanvas.getHeight());

        // Draw creatures
        // TODO use WaTorSimulationModel to get the creatures.
        // TODO Optimize color adjustment
        long creatureCount = 0;
        for (int x = 0; x < waTorConfigModel.xSize(); x++) {
            for (int y = 0; y < waTorConfigModel.ySize(); y++) {
                Optional<WaTorCreature> creature = creatureFunction.apply(new WaTorCoordinate(x, y));
                if (creature.isPresent()) {
                    creatureCount++;
                    simulationGraphicsContext.setFill(
                            switch (creature.orElseThrow()) {
                                case WaTorFish fish -> adjustColor(Color.GREEN, fish);
                                case WaTorShark shark -> adjustColor(Color.ORANGE, shark);
                            });
                    simulationGraphicsContext.fillRect(x * waTorConfigModel.cellLength(), y * waTorConfigModel.cellLength(), waTorConfigModel.cellLength(), waTorConfigModel.cellLength());
                }
            }
        }

        // Draw debug information
        simulationGraphicsContext.setFill(Color.WHITE);
        simulationGraphicsContext.fillText("Time: " + waTorSimulationModel.timeCounter(), 10, 20);
        simulationGraphicsContext.fillText("Speed: " + waTorConfigModel.speed(), 10, 40);
        simulationGraphicsContext.fillText("Creatures: " + creatureCount, 10, 60);
        simulationGraphicsContext.fillText("Draw frames" + drawEveryNFrames, 10, 80);
    }

    DrawingStatus draw(boolean forceDraw) {
        frameCounter++;

        // Skip drawing if not forced and not the right frame
        if (!forceDraw && ((frameCounter % drawEveryNFrames) != 0)) {
            // System.out.println("Skipping frame!      frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime);
            return DrawingStatus.SKIPPED;
        }

        // Draw the simulation canvas and measure the time taken
        long startMillis = System.currentTimeMillis();
        try {
            drawCount++;
            drawSimulationCanvas();
        } catch (Exception e) {
            System.err.println("Error during drawing: " + e.getMessage());
            e.printStackTrace();
            return DrawingStatus.ERROR;
        }
        long drawTime = System.currentTimeMillis() - startMillis;
        totalDrawTime += drawTime;
        // System.out.println("Draw!                frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", drawTime=" + drawTime);

        // Check if draw time exceeds the allowed limit
        if ((drawTime > MAX_ALLOWED_DRAW_TIME)
                || (drawTime > (waTorConfigModel.speed() * MAX_PROPORTION_OF_DRAW_TIME_TO_SPEED))) {
            System.err.println("Error: Draw time too high! frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", drawTime=" + drawTime);
            return DrawingStatus.ERROR;
        } else if (drawTime > FORCE_EVAL_DRAW_TIME) {
            System.out.println("Force evaluation!    frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", drawTime=" + drawTime);
            forceEvaluation = true; // Force evaluation on next draw
        }

        // Evaluate drawing performance and adjust drawEveryNFrames
        int evalFreq = Math.max(FREQ_MIN, FREQ_MAX / drawEveryNFrames);
        if (forceEvaluation || ((drawCount % evalFreq) == 0)) {
            long avgDrawTime = totalDrawTime / drawCount;

            // Check if average draw time exceeds the allowed limit
            if ((drawCount > 1) && (avgDrawTime > MAX_ALLOWED_AVG_DRAW_TIME)) {
                System.err.println("Error: Average draw time too high: " + avgDrawTime + " ms. Stopping drawing. Current draw time: " + drawTime + " ms, total draw time: " + totalDrawTime + " ms, draw count: " + drawCount);
                return DrawingStatus.ERROR;
            }

            // TODO Optimize calculation of maxDrawEveryNFrames
            int maxDrawEveryNFrames = Math.min(500, Math.max(FREQ_MIN, 5000 / waTorConfigModel.speed()));

            // TODO Optimize treshold values and adjustment logic
            if ((drawEveryNFrames < maxDrawEveryNFrames) && (avgDrawTime > Math.min(25, waTorConfigModel.speed() / 3))) {
                System.out.println("++ Increase!         evalFreq=" + evalFreq + ", frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", avgDrawTimeMs=" + avgDrawTime);
                drawEveryNFrames = Math.min(maxDrawEveryNFrames, drawEveryNFrames + 1 + ((maxDrawEveryNFrames - drawEveryNFrames) / 2));
                System.out.println("New drawEveryNFrames: " + drawEveryNFrames + " (max: " + maxDrawEveryNFrames + ")");
            } else if ((drawEveryNFrames > 1) && (avgDrawTime < Math.min(20, waTorConfigModel.speed() / 4))) {
                System.out.println("-- Decrease!         evalFreq=" + evalFreq + ", frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", avgDrawTimeMs=" + avgDrawTime);
                drawEveryNFrames = Math.max(1, (drawEveryNFrames / 2));
                System.out.println("New drawEveryNFrames: " + drawEveryNFrames + " (max: " + maxDrawEveryNFrames + ")");
            } else {
                System.out.println("== Keep!             evalFreq=" + evalFreq + ", frameCounter=" + frameCounter + ", drawEveryNFrames=" + drawEveryNFrames + ", drawCount=" + drawCount + ", totalDrawTime=" + totalDrawTime + ", avgDrawTimeMs=" + avgDrawTime);
            }

            // Reset statistics
            totalDrawTime = 0;
            drawCount = 0;
        }
        forceEvaluation = false;

        return DrawingStatus.DRAWN;
    }

    public enum DrawingStatus {
        DRAWN, SKIPPED, ERROR
    }

}

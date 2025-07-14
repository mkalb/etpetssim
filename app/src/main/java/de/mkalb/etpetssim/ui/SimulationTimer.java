package de.mkalb.etpetssim.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A timer for running periodic simulation steps using JavaFX Timeline.
 * <p>
 * This class allows starting, stopping, and checking the status of a simulation loop,
 * where a given {@link Runnable} is executed at a fixed interval.
 * </p>
 *
 * Example usage:
 * <pre>
 *     SimulationTimer timer = new SimulationTimer(() -> doStep());
 *     timer.start(Duration.millis(500));
 * </pre>
 */
public final class SimulationTimer {

    /** The action to execute on each timer tick. */
    private final Runnable simulationStep;

    /** The JavaFX Timeline managing the periodic execution. */
    private @Nullable Timeline timeline;

    /**
     * Constructs a new SimulationTimer.
     *
     * @param simulationStep the action to execute periodically
     */
    public SimulationTimer(Runnable simulationStep) {
        this.simulationStep = simulationStep;
    }

    /**
     * Starts the timer with the given interval. Stops any previous timer.
     * <p>
     * This method can be called repeatedly with a new interval to update the timer
     * without creating a new instance.
     * </p>
     *
     * @param interval the interval between executions
     */
    public void start(Duration interval) {
        stop();
        timeline = new Timeline(new KeyFrame(interval, _ -> simulationStep.run()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * Stops the timer if running.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    /**
     * Returns true if the timer is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return timeline != null;
    }

}

package de.mkalb.etpetssim.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.jspecify.annotations.Nullable;

/**
 * A timer for running periodic simulation steps using a JavaFX {@link Timeline}.
 *
 * <p>Allows starting, stopping, and checking the status of a simulation loop,
 * where a given {@link Runnable} is executed at a fixed interval on the JavaFX Application Thread.
 *
 * <p>Example usage:
 * <pre>
 *     SimulationTimer timer = new SimulationTimer(() -> doStep());
 *     timer.start(Duration.millis(500));
 * </pre>
 */
public final class SimulationTimer {

    private final Runnable simulationStep;

    private @Nullable Timeline timeline;

    public SimulationTimer(Runnable simulationStep) {
        this.simulationStep = simulationStep;
    }

    /**
     * Starts the timer with the given interval. Stops any previous timer.
     *
     * <p>Can be called repeatedly with a new interval to update the timer
     * without creating a new instance.
     *
     * @param interval the interval between executions; must be finite and greater than 0 ms
     * @throws IllegalArgumentException if {@code interval} is unknown, indefinite, or not greater than 0 ms
     */
    public void start(Duration interval) {
        if (interval.isUnknown() || interval.isIndefinite() || (interval.toMillis() <= 0.0d)) {
            throw new IllegalArgumentException("interval must be a finite duration greater than 0 ms");
        }
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
     * Returns whether the timer is currently running.
     *
     * @return {@code true} if running, {@code false} otherwise
     */
    public boolean isRunning() {
        return (timeline != null) && (timeline.getStatus() == Animation.Status.RUNNING);
    }

}

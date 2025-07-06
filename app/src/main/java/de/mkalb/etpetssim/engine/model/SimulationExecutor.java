package de.mkalb.etpetssim.engine.model;

import java.util.*;
import java.util.function.*;

/**
 * Executes simulation steps for a given model until a specified termination condition is met.
 * <p>
 * This executor manages the simulation lifecycle, allowing stepwise execution,
 * batch execution, or running the simulation to completion. It also provides
 * access to the current model state and step count, and supports resetting the simulation.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 */
public final class SimulationExecutor<T extends GridEntity> {

    private final SimulationStep<T> step;
    private final Supplier<GridModel<T>> modelSupplier;
    private final SimulationTerminationCondition<T> terminationCondition;

    private long currentStep;

    /**
     * Constructs a new SimulationExecutor.
     *
     * @param step the simulation step logic to perform
     * @param modelSupplier supplies the current simulation model
     * @param terminationCondition the condition that determines when the simulation should stop
     * @throws NullPointerException if any argument is null
     */
    public SimulationExecutor(
            SimulationStep<T> step,
            Supplier<GridModel<T>> modelSupplier,
            SimulationTerminationCondition<T> terminationCondition) {
        Objects.requireNonNull(step);
        Objects.requireNonNull(modelSupplier);
        Objects.requireNonNull(terminationCondition);
        this.step = step;
        this.modelSupplier = modelSupplier;
        this.terminationCondition = terminationCondition;
        currentStep = 0;
    }

    /**
     * Returns the current simulation model.
     *
     * @return the current {@link GridModel}
     */
    public GridModel<T> currentModel() {
        return modelSupplier.get();
    }

    /**
     * Returns the current simulation step count.
     *
     * @return the current step (starting from 0)
     */
    public long currentStep() {
        return currentStep;
    }

    /**
     * Checks whether the simulation has finished.
     *
     * @return {@code true} if the simulation is finished, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isFinished() {
        return (currentStep == Long.MAX_VALUE) || terminationCondition.isFinished(currentModel(), currentStep);
    }

    /**
     * Executes a single simulation step and increments the step counter.
     */
    public void executeStep() {
        step.performStep();
        currentStep++;
    }

    /**
     * Executes up to the specified number of simulation steps, or until the simulation is finished.
     *
     * @param count the maximum number of steps to execute
     */
    public void executeSteps(int count) {
        for (int i = 0; (i < count) && !isFinished(); i++) {
            executeStep();
        }
    }

    /**
     * Executes simulation steps until the termination condition is met.
     */
    public void executeAllSteps() {
        while (!isFinished()) {
            executeStep();
        }
    }

    /**
     * Resets the simulation step counter to zero.
     */
    public void reset() {
        currentStep = 0;
    }

}
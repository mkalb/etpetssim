package de.mkalb.etpetssim.engine.model;

import java.util.function.*;

/**
 * Default implementation of {@link SimulationExecutor} that executes simulation steps
 * for a given model until a specified termination condition is met.
 * <p>
 * This executor manages the simulation lifecycle, supporting stepwise execution,
 * batch execution, and running the simulation to completion. It provides access
 * to the current model state and step count, and allows resetting the simulation.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 * @param <C> the type of the context object (e.g., statistics or additional state)
 */
public final class DefaultSimulationExecutor<T extends GridEntity, C> implements SimulationExecutor<T> {

    private final SimulationStep<T> step;
    private final Supplier<GridModel<T>> modelSupplier;
    private final SimulationTerminationCondition<T, C> terminationCondition;
    private final C context;

    private long currentStep;

    /**
     * Creates a new {@code DefaultSimulationExecutor}.
     *
     * @param step the logic to perform a single simulation step
     * @param modelSupplier supplies the current simulation model
     * @param terminationCondition the condition that determines when the simulation should stop
     * @param context the context object with additional information for the termination condition
     */
    public DefaultSimulationExecutor(
            SimulationStep<T> step,
            Supplier<GridModel<T>> modelSupplier,
            SimulationTerminationCondition<T, C> terminationCondition,
            C context) {
        this.step = step;
        this.modelSupplier = modelSupplier;
        this.terminationCondition = terminationCondition;
        this.context = context;
        currentStep = 0;
    }

    @Override
    public GridModel<T> currentModel() {
        return modelSupplier.get();
    }

    @Override
    public long currentStep() {
        return currentStep;
    }

    @Override
    public boolean isRunning() {
        return (currentStep != Long.MAX_VALUE) && !terminationCondition.isFinished(currentModel(), currentStep, context);
    }

    @Override
    public void executeStep() {
        step.performStep();
        currentStep++;
    }

    @Override
    public void executeSteps(int count) {
        for (int i = 0; (i < count) && isRunning(); i++) {
            executeStep();
        }
    }

    @Override
    public void executeAllSteps() {
        while (isRunning()) {
            executeStep();
        }
    }

    @Override
    public void reset() {
        currentStep = 0;
    }

}

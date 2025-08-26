package de.mkalb.etpetssim.engine.model;

import java.util.function.*;

/**
 * Default implementation of {@link SimulationExecutor} that executes simulation steps
 * for a given model until a specified termination condition is met.
 * <p>
 * This executor manages the simulation lifecycle, supporting stepwise execution
 * and batch execution. It provides access to the current model state and step count.
 *
 * @param <T> the type of {@link GridEntity} in the simulation
 * @param <C> the type of the context object used to share or accumulate state during the simulation
 */
public final class DefaultSimulationExecutor<T extends GridEntity, C> implements SimulationExecutor<T> {

    private final SimulationStepRunner<C> stepRunner;
    private final Supplier<GridModel<T>> modelSupplier;
    private final SimulationTerminationCondition<T, C> terminationCondition;
    private final C context;

    /**
     * The index of the current simulation step during execution.
     * Represents the number of completed steps (i.e., the next step to execute).
     */
    private int stepCount;

    /**
     * Creates a new {@code DefaultSimulationExecutor}.
     *
     * @param stepRunner the logic to perform a single simulation step, using the provided context
     * @param modelSupplier supplies the current simulation model
     * @param terminationCondition the condition that determines when the simulation should stop, evaluated with the context
     * @param context the context object used to share or accumulate state during the simulation
     */
    public DefaultSimulationExecutor(
            SimulationStepRunner<C> stepRunner,
            Supplier<GridModel<T>> modelSupplier,
            SimulationTerminationCondition<T, C> terminationCondition,
            C context) {
        this.stepRunner = stepRunner;
        this.modelSupplier = modelSupplier;
        this.terminationCondition = terminationCondition;
        this.context = context;
        stepCount = 0;
    }

    @Override
    public GridModel<T> currentModel() {
        return modelSupplier.get();
    }

    @Override
    public int stepCount() {
        return stepCount;
    }

    @Override
    public boolean isFinished() {
        return (stepCount == Integer.MAX_VALUE) || terminationCondition.isFinished(currentModel(), stepCount, context);
    }

    @Override
    public void executeStep() {
        // Use stepCount as stepIndex for the current step
        stepRunner.performStep(stepCount, context);
        stepCount++;
    }

}

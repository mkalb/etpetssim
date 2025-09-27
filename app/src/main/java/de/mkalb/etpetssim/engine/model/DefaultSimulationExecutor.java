package de.mkalb.etpetssim.engine.model;

import java.util.function.*;

/**
 * Default implementation of {@link SimulationExecutor} that executes simulation steps
 * for a given model until a specified termination condition is met.
 * <p>
 * This executor manages the simulation lifecycle, supporting stepwise execution
 * and batch execution. It provides access to the current model state and step count.
 *
 * @param <ENT> the type of {@link GridEntity} in the simulation
 * @param <GM> the type of {@link GridModel} in the simulation
 * @param <C> the type of the context object used to share or accumulate state or statistics during the simulation
 */
public final class DefaultSimulationExecutor<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        C>
        implements SimulationExecutor<ENT, GM> {

    private final SimulationStepRunner<C> stepRunner;
    private final Supplier<GM> modelSupplier;
    private final SimulationTerminationCondition<ENT, ? super GM, C> terminationCondition;
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
            Supplier<GM> modelSupplier,
            SimulationTerminationCondition<ENT, ? super GM, C> terminationCondition,
            C context) {
        this.stepRunner = stepRunner;
        this.modelSupplier = modelSupplier;
        this.terminationCondition = terminationCondition;
        this.context = context;
        stepCount = 0;
    }

    @Override
    public GM currentModel() {
        return modelSupplier.get();
    }

    @Override
    public int stepCount() {
        return stepCount;
    }

    @Override
    public boolean isFinished() {
        return terminationCondition.isFinished(currentModel(), stepCount, context);
    }

    @Override
    public void executeStep() {
        // Use stepCount as the step index for the current step.
        stepRunner.performStep(stepCount, context);
        stepCount++;
    }

}

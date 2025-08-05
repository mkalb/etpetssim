package de.mkalb.etpetssim.engine.model;

/**
 * Functional interface defining the logic for a synchronous simulation step on a grid model.
 * <p>
 * Implementations specify how the simulation state should be updated in a synchronous manner:
 * reading from the current model (as {@link ReadableGridModel}) and writing results to the next model.
 * The method receives the current model, the next model, the simulation step index, and a context object
 * for sharing or accumulating state across steps.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @param <C> the type of the context object provided to each simulation step
 * @see de.mkalb.etpetssim.engine.model.SynchronousStepRunner
 */
@FunctionalInterface
public interface SynchronousStepLogic<T extends GridEntity, C> {

    /**
     * Performs the logic for a single synchronous simulation step.
     * <p>
     * Reads from the current model and writes results to the next model.
     *
     * @param currentModel the current (read-only) grid model representing the simulation state
     * @param nextModel    the grid model to write the updated state to
     * @param stepIndex    the index of the current simulation step
     * @param context      the context object used to share or accumulate state during the simulation
     */
    void performSynchronousStep(ReadableGridModel<T> currentModel, GridModel<T> nextModel, int stepIndex, C context);

}

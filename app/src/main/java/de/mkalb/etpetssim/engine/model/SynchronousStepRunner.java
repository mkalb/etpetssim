package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Executes synchronous simulation steps on a {@link WritableGridModel}.
 * <p>
 * This runner applies a synchronous update strategy to the simulation model:
 * it reads from the current model (as a {@link ReadableGridModel}) and writes results
 * to the next model. After each step, the models are swapped, and the next model is cleared
 * for the following step.
 *
 * @param <T> the type of {@link de.mkalb.etpetssim.engine.model.entity.GridEntity} contained in the grid model
 * @param <C> the type of the context object provided to each simulation step
 * @see SynchronousStepLogic
 */
public final class SynchronousStepRunner<T extends GridEntity, C> implements SimulationStepRunner<C> {

    private final SynchronousStepLogic<T, C> stepLogic;
    private WritableGridModel<T> currentModel;
    private WritableGridModel<T> nextModel;

    /**
     * Constructs a new {@code SynchronousStepRunner} with the given initial model and update strategy.
     * The update strategy must not modify the {@code ReadableGridModel} parameter.
     *
     * @param initialModel the initial grid model
     * @param stepLogic    the logic to apply for each synchronous simulation step
     */
    public SynchronousStepRunner(WritableGridModel<T> initialModel,
                                 SynchronousStepLogic<T, C> stepLogic) {
        currentModel = initialModel;
        nextModel = currentModel.copyWithDefaultEntity();
        this.stepLogic = stepLogic;
    }

    /**
     * Performs a single synchronous simulation step.
     * <p>
     * The update strategy reads from the current model and writes to the next model.
     * After the update, the models are swapped and the next model is cleared.
     *
     * @param stepIndex the index of the current simulation step
     * @param context   the context object used to share or accumulate state during the simulation
     */
    @Override
    public void performStep(int stepIndex, C context) {
        stepLogic.performSynchronousStep(currentModel, nextModel, stepIndex, context);
        WritableGridModel<T> tempModel = currentModel;
        currentModel = nextModel;
        nextModel = tempModel;
        nextModel.clear();
    }

    /**
     * Returns the current grid model representing the latest simulation state.
     *
     * @return the current {@link WritableGridModel}
     */
    public WritableGridModel<T> currentModel() {
        return currentModel;
    }

    /**
     * Returns the next grid model used for the upcoming simulation step.
     *
     * @return the next {@link WritableGridModel}
     */
    public WritableGridModel<T> nextModel() {
        return nextModel;
    }

}

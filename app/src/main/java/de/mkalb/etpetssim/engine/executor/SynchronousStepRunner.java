package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Executes synchronous simulation steps on a {@link de.mkalb.etpetssim.engine.model.WritableGridModel}.
 * <p>
 * This runner applies synchronous step logic to the simulation model:
 * it reads from the current model (as a {@link de.mkalb.etpetssim.engine.model.ReadableGridModel}) and writes results
 * to the next model. After each step, the models are swapped, and the next model is cleared
 * for the following step.
 *
 * @param <ENT> the type of {@link de.mkalb.etpetssim.engine.model.entity.GridEntity} contained in the grid model
 * @param <C>   the type of the context object provided to each simulation step
 * @see SynchronousStepLogic
 */
public final class SynchronousStepRunner<ENT extends GridEntity, C> implements SimulationStepRunner<C> {

    private final SynchronousStepLogic<ENT, C> stepLogic;
    private WritableGridModel<ENT> currentGridModel;
    private WritableGridModel<ENT> nextGridModel;

    /**
     * Constructs a new {@code SynchronousStepRunner} with the given initial model and step logic.
     * The step logic must not modify the {@code ReadableGridModel} parameter.
     *
     * @param initialModel the initial grid model
     * @param stepLogic    the logic to apply for each synchronous simulation step
     */
    public SynchronousStepRunner(WritableGridModel<ENT> initialModel,
                                 SynchronousStepLogic<ENT, C> stepLogic) {
        currentGridModel = initialModel;
        nextGridModel = currentGridModel.copyWithDefaultEntity();
        this.stepLogic = stepLogic;
    }

    /**
     * Performs a single synchronous simulation step.
     * <p>
     * The step logic reads from the current model and writes to the next model.
     * After the update, the models are swapped and the next model is cleared.
     *
     * @param stepIndex the index of the current simulation step
     * @param context   the context object used to share or accumulate state during the simulation
     */
    @Override
    public void performStep(int stepIndex, C context) {
        stepLogic.performSynchronousStep(currentGridModel, nextGridModel, stepIndex, context);
        WritableGridModel<ENT> tempModel = currentGridModel;
        currentGridModel = nextGridModel;
        nextGridModel = tempModel;
        nextGridModel.clear();
    }

    /**
     * Returns the current grid model representing the latest simulation state.
     *
     * @return the current {@link WritableGridModel}
     */
    public WritableGridModel<ENT> currentGridModel() {
        return currentGridModel;
    }

    /**
     * Returns the next grid model used for the upcoming simulation step.
     *
     * @return the next {@link WritableGridModel}
     */
    public WritableGridModel<ENT> nextGridModel() {
        return nextGridModel;
    }

}

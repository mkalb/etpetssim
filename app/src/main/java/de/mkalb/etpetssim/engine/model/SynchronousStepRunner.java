package de.mkalb.etpetssim.engine.model;

import java.util.*;
import java.util.function.*;

/**
 * Executes synchronous simulation steps on a {@link GridModel}.
 * <p>
 * The update strategy must only read from the current model (as {@link ReadableGridModel})
 * and write results to the next model.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 */
public final class SynchronousStepRunner<T extends GridEntity> implements SimulationStep<T> {

    private final BiConsumer<ReadableGridModel<T>, GridModel<T>> updateStrategy;
    private GridModel<T> currentModel;
    private GridModel<T> nextModel;

    /**
     * Constructs a new {@code SynchronousStepRunner} with the given initial model and update strategy.
     * The update strategy must not modify the {@code ReadableGridModel} parameter.
     *
     * @param initialModel   the initial grid model (must not be {@code null})
     * @param updateStrategy the update strategy, which reads from the current model and writes to the next model (must not be {@code null})
     * @throws NullPointerException if {@code initialModel} or {@code updateStrategy} is {@code null}
     */
    public SynchronousStepRunner(GridModel<T> initialModel,
                                 BiConsumer<ReadableGridModel<T>, GridModel<T>> updateStrategy) {
        Objects.requireNonNull(initialModel);
        Objects.requireNonNull(updateStrategy);
        currentModel = initialModel;
        nextModel = currentModel.copyWithDefaultEntity();
        this.updateStrategy = updateStrategy;
    }

    /**
     * Performs a single synchronous simulation step.
     * <p>
     * The update strategy reads from the current model and writes to the next model.
     */
    @Override
    public void performStep() {
        updateStrategy.accept(currentModel, nextModel);
        GridModel<T> tempModel = currentModel;
        currentModel = nextModel;
        nextModel = tempModel;
        nextModel.clear();
    }

    /**
     * Returns the current grid model representing the latest simulation state.
     *
     * @return the current {@link GridModel}
     */
    public GridModel<T> currentModel() {
        return currentModel;
    }

    /**
     * Returns the next grid model used for the upcoming simulation step.
     *
     * @return the next {@link GridModel}
     */
    public GridModel<T> nextModel() {
        return nextModel;
    }

}

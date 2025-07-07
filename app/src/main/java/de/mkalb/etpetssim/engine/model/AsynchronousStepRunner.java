package de.mkalb.etpetssim.engine.model;

import java.util.*;
import java.util.function.*;

/**
 * Executes asynchronous simulation steps on a {@link GridModel}.
 * <p>
 * This runner applies agent-specific logic to entities in the grid model
 * based on a predicate that identifies agents.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 */
public final class AsynchronousStepRunner<T extends GridEntity> implements SimulationStep<T> {

    private final Predicate<T> isAgent;
    private final BiConsumer<GridCell<T>, GridModel<T>> agentLogic;
    private final GridModel<T> model;

    /**
     * Constructs a new {@code AsynchronousStepRunner} with the given model, agent predicate, and agent logic.
     *
     * @param model      the grid model to operate on (must not be {@code null})
     * @param isAgent    a predicate to identify agents in the grid (must not be {@code null})
     * @param agentLogic the logic to apply to each agent (must not be {@code null})
     * @throws NullPointerException if any parameter is {@code null}
     */
    public AsynchronousStepRunner(GridModel<T> model, Predicate<T> isAgent, BiConsumer<GridCell<T>, GridModel<T>> agentLogic) {
        Objects.requireNonNull(model, "Model must not be null.");
        Objects.requireNonNull(isAgent, "Agent predicate must not be null.");
        Objects.requireNonNull(agentLogic, "Agent logic must not be null.");
        this.model = model;
        this.isAgent = isAgent;
        this.agentLogic = agentLogic;
    }

    /**
     * Performs a single asynchronous simulation step.
     * <p>
     * Applies the agent logic to all entities identified as agents in the grid model.
     */
    @Override
    public void performStep() {
        List<GridCell<T>> snapshot = model.cellsAsStream()
                                          .filter(cell -> isAgent.test(cell.entity()))
                                          .toList();
        for (GridCell<T> cell : snapshot) {
            agentLogic.accept(cell, model);
        }
    }

    /**
     * Returns the grid model used by this runner.
     *
     * @return the current {@link GridModel}
     */
    public GridModel<T> model() {
        return model;
    }

}

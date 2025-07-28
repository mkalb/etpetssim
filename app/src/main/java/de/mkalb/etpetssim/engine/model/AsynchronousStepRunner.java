package de.mkalb.etpetssim.engine.model;

import java.util.*;
import java.util.function.*;

/**
 * Executes asynchronous simulation steps on a {@link GridModel}.
 * <p>
 * This runner applies agent-specific logic to entities in the grid model
 * based on a predicate that identifies agents. The agent logic operates on
 * a context object, constructed for each agent using a {@link SimulationAgentContextBuilder}.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @param <C> the type of context object provided to agent logic
 */
public final class AsynchronousStepRunner<T extends GridEntity, C> implements SimulationStep<T> {

    private final Predicate<T> isAgent;
    private final SimulationAgentContextBuilder<T, C> contextBuilder;
    private final Consumer<C> agentLogic;
    private final GridModel<T> model;

    /**
     * Constructs a new {@code AsynchronousStepRunner} with the given model, agent predicate,
     * context builder, and agent logic.
     *
     * @param model         the grid model to operate on
     * @param isAgent       a predicate to identify agents in the grid
     * @param contextBuilder a builder to create the context for each agent
     * @param agentLogic    the logic to apply to each agent context
     */
    public AsynchronousStepRunner(GridModel<T> model,
                                  Predicate<T> isAgent,
                                  SimulationAgentContextBuilder<T, C> contextBuilder,
                                  Consumer<C> agentLogic) {
        this.model = model;
        this.isAgent = isAgent;
        this.contextBuilder = contextBuilder;
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
            C context = contextBuilder.build(cell, model);
            agentLogic.accept(context);
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

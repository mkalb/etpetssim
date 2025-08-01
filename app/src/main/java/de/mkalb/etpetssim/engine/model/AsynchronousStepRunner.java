package de.mkalb.etpetssim.engine.model;

import java.util.*;
import java.util.function.*;

/**
 * Executes asynchronous simulation steps on a {@link GridModel}.
 * <p>
 * This runner applies agent-specific logic to all entities in the grid model
 * that satisfy the given agent predicate. The agent logic is applied to each
 * agent cell, using the provided context object for state sharing or accumulation.
 * The order in which agents are processed is determined by the provided
 * {@code agentOrderingStrategy}.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @param <C> the type of the context object provided to each simulation step
 */
public final class AsynchronousStepRunner<T extends GridEntity, C> implements SimulationStep<C> {

    private final Predicate<T> isAgent;
    private final GridModel<T> model;
    private final Comparator<GridCell<T>> agentOrderingStrategy;
    private final AgentStepLogic<T, C> agentStepLogic;

    /**
     * Constructs a new {@code AsynchronousStepRunner} with the given model, agent predicate,
     * agent ordering strategy, and agent logic.
     *
     * @param model                  the grid model to operate on
     * @param isAgent                a predicate to identify agents in the grid
     * @param agentOrderingStrategy  the comparator defining the order in which agent cells are processed
     * @param agentStepLogic         the logic to apply to each agent cell
     */
    public AsynchronousStepRunner(GridModel<T> model,
                                  Predicate<T> isAgent,
                                  Comparator<GridCell<T>> agentOrderingStrategy,
                                  AgentStepLogic<T, C> agentStepLogic) {
        this.model = model;
        this.isAgent = isAgent;
        this.agentOrderingStrategy = agentOrderingStrategy;
        this.agentStepLogic = agentStepLogic;
    }

    /**
     * Performs a single asynchronous simulation step.
     * <p>
     * Applies the agent logic to all entities identified as agents in the grid model.
     * The context object is passed to each agent logic invocation.
     *
     * @param currentStep the current simulation step number
     * @param context     the context object used to share or accumulate state during the simulation
     */
    @Override
    public void performStep(long currentStep, C context) {
        List<GridCell<T>> snapshot = model.cellsAsStream()
                                          .filter(cell -> isAgent.test(cell.entity()))
                                          .sorted(agentOrderingStrategy)
                                          .toList();
        for (GridCell<T> cell : snapshot) {
            agentStepLogic.performAgentStep(cell, model, currentStep, context);
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

package de.mkalb.etpetssim.engine.model;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;

/**
 * Executes asynchronous simulation steps on a {@link WritableGridModel}.
 * <p>
 * This runner applies agent-specific logic to all entities in the grid model
 * that satisfy the given {@code agentPredicate}. The agent logic is applied to each
 * agent cell, using the provided context object for state sharing or accumulation.
 * The order in which agents are processed is determined by the provided
 * {@code agentOrderingStrategy}.
 *
 * @param <T> the type of {@link de.mkalb.etpetssim.engine.model.entity.GridEntity} contained in the grid model
 * @param <C> the type of the context object provided to each simulation step
 */
@SuppressWarnings("ClassCanBeRecord")
public final class AsynchronousStepRunner<T extends GridEntity, C> implements SimulationStepRunner<C> {

    private final WritableGridModel<T> model;
    private final Predicate<T> agentPredicate;
    private final Comparator<GridCell<T>> agentOrderingStrategy;
    private final AgentStepLogic<T, C> agentStepLogic;

    /**
     * Constructs a new {@code AsynchronousStepRunner} with the given grid model, agent predicate,
     * agent ordering strategy, and agent step logic.
     *
     * @param model                 the grid model to operate on
     * @param agentPredicate        predicate to identify agent entities in the grid
     * @param agentOrderingStrategy comparator defining the order in which agent cells are processed
     * @param agentStepLogic        logic to apply to each agent cell
     */
    public AsynchronousStepRunner(WritableGridModel<T> model,
                                  Predicate<T> agentPredicate,
                                  Comparator<GridCell<T>> agentOrderingStrategy,
                                  AgentStepLogic<T, C> agentStepLogic) {
        this.model = model;
        this.agentPredicate = agentPredicate;
        this.agentOrderingStrategy = agentOrderingStrategy;
        this.agentStepLogic = agentStepLogic;
    }

    /**
     * Performs a single asynchronous simulation step.
     * <p>
     * Applies the agent logic to all entities identified as agents in the grid model.
     * The context object is passed to each agent logic invocation.
     *
     * @param stepIndex the index of the current simulation step
     * @param context   the context object used to share or accumulate state during the simulation
     */
    @Override
    public void performStep(int stepIndex, C context) {
        List<GridCell<T>> orderedAgentCells = model.filteredAndSortedCells(agentPredicate, agentOrderingStrategy);
        for (GridCell<T> agentCell : orderedAgentCells) {
            agentStepLogic.performAgentStep(agentCell, model, stepIndex, context);
        }
    }

    /**
     * Returns the grid model used by this runner.
     *
     * @return the current {@link WritableGridModel}
     */
    public WritableGridModel<T> model() {
        return model;
    }

}

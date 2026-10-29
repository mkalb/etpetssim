package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.*;
import java.util.function.*;

/**
 * Executes asynchronous simulation steps on a {@link WritableGridModel}.
 * <p>
 * Applies the agent step logic to each cell whose entity satisfies the {@code agentPredicate}, in the order of the
 * {@code agentOrderingStrategy}. The context object is passed to each invocation.
 * <p>
 * The agent cells are selected and sorted once at the start of each step. Because the step logic may change the
 * grid model, a cell can be outdated when its turn comes (e.g., its agent was removed by an earlier agent); the step
 * logic checks the current entity at the cell's coordinate when it depends on it.
 *
 * @param <ENT> the type of {@link GridEntity} contained in the grid model
 * @param <C>   the type of the context object provided to each simulation step
 */
public final class AsynchronousStepRunner<ENT extends GridEntity, C> implements SimulationStepRunner<C> {

    private final WritableGridModel<ENT> model;
    private final Predicate<ENT> agentPredicate;
    private final Comparator<GridCell<ENT>> agentOrderingStrategy;
    private final AgentStepLogic<ENT, C> agentStepLogic;

    public AsynchronousStepRunner(WritableGridModel<ENT> model,
                                  Predicate<ENT> agentPredicate,
                                  Comparator<GridCell<ENT>> agentOrderingStrategy,
                                  AgentStepLogic<ENT, C> agentStepLogic) {
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
        List<GridCell<ENT>> orderedAgentCells = model.filteredCellsSortedBy(agentPredicate, agentOrderingStrategy);
        for (GridCell<ENT> agentCell : orderedAgentCells) {
            agentStepLogic.performAgentStep(agentCell, model, stepIndex, context);
        }
    }

    /**
     * Returns the grid model used by this runner.
     *
     * @return the current {@link WritableGridModel}
     */
    public WritableGridModel<ENT> model() {
        return model;
    }

}

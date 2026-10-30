package de.mkalb.etpetssim.engine.executor;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Executes asynchronous simulation steps on a {@link WritableGridModel}.
 *
 * <p>Applies the agent step logic to each cell whose entity satisfies the {@code agentPredicate}, in the order of the
 * {@code agentOrderingStrategy}. The context object is passed to each invocation.
 *
 * <p>The agent cells are selected and sorted once at the start of each step. Because the step logic may change the
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

    @Override
    public void performStep(int stepIndex, C context) {
        List<GridCell<ENT>> orderedAgentCells = model.filteredCellsSortedBy(agentPredicate, agentOrderingStrategy);
        for (GridCell<ENT> agentCell : orderedAgentCells) {
            agentStepLogic.performAgentStep(agentCell, model, stepIndex, context);
        }
    }

    public WritableGridModel<ENT> model() {
        return model;
    }

}

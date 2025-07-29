package de.mkalb.etpetssim.engine.model;

/**
 * Defines the logic to be executed for a single agent during an asynchronous simulation step.
 * <p>
 * Implementations of this interface specify how an agent, represented by a {@link GridCell},
 * should update its state or interact with the simulation environment at each step.
 * The method receives the agent's cell, the complete grid model, the current simulation step,
 * and a context object for sharing or accumulating state across steps or agents.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @param <C> the type of the context object provided to each agent step
 * @see de.mkalb.etpetssim.engine.model.AsynchronousStepRunner
 */
@FunctionalInterface
public interface AgentStepLogic<T extends GridEntity, C> {

    /**
     * Performs the logic for a single agent during a simulation step.
     *
     * @param agentCell   the grid cell containing the agent to update
     * @param model       the grid model representing the simulation environment
     * @param currentStep the current simulation step number
     * @param context     the context object used to share or accumulate state during the simulation
     */
    void performAgentStep(GridCell<T> agentCell, GridModel<T> model, long currentStep, C context);

}

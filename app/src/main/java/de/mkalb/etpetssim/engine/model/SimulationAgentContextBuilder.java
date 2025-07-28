package de.mkalb.etpetssim.engine.model;

/**
 * Functional interface for building a simulation agent context from a grid cell and its model.
 * <p>
 * Implementations of this interface are responsible for constructing a context object
 * that encapsulates all information required by agent logic for a single simulation step.
 *
 * @param <T> the type of {@link GridEntity} contained in the grid model
 * @param <C> the type of context object to be created for agent logic
 */
@FunctionalInterface
public interface SimulationAgentContextBuilder<T extends GridEntity, C> {

    /**
     * Builds a context object for agent logic based on the given grid cell and model.
     *
     * @param cell  the grid cell representing the agent
     * @param model the grid model containing the cell
     * @return a context object containing all necessary information for agent logic
     */
    C build(GridCell<T> cell, GridModel<T> model);

}

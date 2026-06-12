package de.mkalb.etpetssim.simulations.snake.shared;

import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;

/**
 * Identifies which modification the user wants to apply to the selected cell.
 */
public enum SnakeUserActionContext implements SimulationUserActionContext {
    /**
     * Add a wall to the selected cell.
     */
    ADD_WALL,

    /**
     * Remove a wall from the selected cell.
     */
    REMOVE_WALL,

    /**
     * Add food to the selected cell.
     */
    ADD_FOOD,

    /**
     * Remove food from the selected cell.
     */
    REMOVE_FOOD
}

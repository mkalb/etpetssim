package de.mkalb.etpetssim.simulations.core;

/**
 * Categories that group the simulation types on the start screen.
 *
 * <p>Simulations are grouped by their predominant mechanics: agent- and movement-based models focus on moving agents
 * or entities; cellular automata update cell states by local rules. The categories are for navigation, not a strict
 * scientific classification.
 *
 * <p>The declaration order defines the order of the groups on the start screen.
 */
public enum SimulationCategory {

    /**
     * Simulations whose agents or entities move across the grid and interact with it and with each other.
     */
    AGENT_AND_MOVEMENT_BASED_MODEL("simulationcategory.agentandmovementbasedmodel"),

    /**
     * Simulations whose cells change their states by local rules.
     */
    CELLULAR_AUTOMATON("simulationcategory.cellularautomaton");

    private final String resourceKey;

    SimulationCategory(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "simulationcategory.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}

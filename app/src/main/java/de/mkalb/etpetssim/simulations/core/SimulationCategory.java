package de.mkalb.etpetssim.simulations.core;

/**
 * Categories that group the simulation types on the start screen.
 *
 * <p>The declaration order defines the order of the groups on the start screen.
 */
public enum SimulationCategory {

    /**
     * Simulations of autonomous agents that move and interact on the grid.
     */
    AGENT_BASED_MODEL("simulationcategory.agentbasedmodel"),

    /**
     * Simulations whose cells change their states by local rules.
     */
    CELLULAR_AUTOMATON("simulationcategory.cellularautomaton"),

    /**
     * Simple custom models without a scientific reference model.
     */
    TOY_MODEL("simulationcategory.toymodel");

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

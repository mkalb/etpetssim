package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the available simulation modes for running a simulation.
 * <p>
 * Each mode represents a distinct way of executing the simulation, either interactively (live)
 * or in a batch process. The {@code resourceKey} field allows for resource lookup, such as
 * internationalized labels.
 */
public enum SimulationMode {

    /**
     * Live mode: The simulation runs interactively and updates in real time.
     */
    LIVE("simulationmode.live"),

    /**
     * Batch mode: The simulation runs as a batch process, typically without real-time interaction.
     */
    BATCH("simulationmode.batch");

    private final String resourceKey;

    /**
     * Constructs a simulation mode with the specified resource key.
     *
     * @param resourceKey the resource key for this simulation mode
     */
    SimulationMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key for the label (title) of the enum SimulationMode.
     *
     * @return the resource key for the label of the enum SimulationMode
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "simulationmode.label";
    }

    /**
     * Returns the resource key associated with this simulation mode.
     * <p>
     * The resource key can be used for resource lookup purposes.
     *
     * @return the resource key for this simulation mode
     */
    public String resourceKey() {
        return resourceKey;
    }

}

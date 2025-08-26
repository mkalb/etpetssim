package de.mkalb.etpetssim.simulations.model;

/**
 * Defines the available restart modes for batch simulation execution.
 * <p>
 * Each mode determines whether the simulation should automatically start the next batch run
 * after a successful batch execution, or only execute a single batch run.
 * <p>
 * The restart mode is selected in the control panel and affects the behavior after each batch run.
 *
 * @see de.mkalb.etpetssim.simulations.viewmodel.DefaultControlViewModel
 */
public enum SimulationRestartMode {

    /**
     * Automatically restart the next batch run after a successful batch execution.
     */
    RESTART,

    /**
     * Do not restart; only execute a single batch run.
     */
    NO_RESTART

}

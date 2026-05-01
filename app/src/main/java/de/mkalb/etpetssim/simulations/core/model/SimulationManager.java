package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.SimulationExecutor;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;

/**
 * Defines the core lifecycle and execution contract of a simulation manager.
 *
 * @param <ENT> entity type used by the simulation model
 * @param <GM> model type used for simulation execution
 * @param <CON> immutable configuration type
 * @param <STA> statistics snapshot type
 */
public interface SimulationManager<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends SimulationStatistics> {

    /**
     * Returns the active immutable simulation configuration.
     *
     * @return simulation configuration
     */
    CON config();

    /**
     * Returns the grid structure currently used by the simulation.
     *
     * @return grid structure
     */
    GridStructure structure();

    /**
     * Returns the latest statistics snapshot.
     *
     * @return simulation statistics
     */
    STA statistics();

    /**
     * Executes a single simulation step.
     */
    void executeStep();

    /**
     * Executes multiple simulation steps with optional early termination.
     *
     * @param count maximum number of steps to execute
     * @param checkTermination whether logical termination should stop execution early
     * @param onStep callback invoked after each executed step
     * @return aggregated execution result
     */
    SimulationExecutor.ExecutionResult executeSteps(int count, boolean checkTermination, Runnable onStep);

    /**
     * Indicates whether the simulation reached its logical termination condition.
     *
     * @return {@code true} if logically finished
     */
    boolean isFinished();

    /**
     * Indicates whether the executor reached its technical step limit.
     *
     * @return {@code true} if technically finished
     */
    boolean isExecutorFinished();

    /**
     * Returns the number of completed simulation steps.
     *
     * @return completed step count
     */
    int stepCount();

    /**
     * Returns the current model snapshot.
     *
     * @return current simulation model
     */
    GM currentModel();

}
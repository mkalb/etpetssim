package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import org.jspecify.annotations.Nullable;

/** Defines a user-triggered action that modifies the current simulation state while paused.
 *
 *  <p>Implementations receive the current model, configuration, statistics, and optionally a selected grid cell,
 *  and may mutate the model or statistics in place.
 *
 *  @param <ENT> entity type stored in grid cells
 *  @param <GM>  grid model type that the action may mutate
 *  @param <CON> immutable simulation config type
 *  @param <STA> timed statistics type that the action may update
 */
@FunctionalInterface
public interface SimulationUserAction<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics> {

    /**
     * Applies this action to the current simulation state.
     *
     * @param model        the current grid model, may be mutated by the action
     * @param statistics   the current simulation statistics, may be updated by the action
     * @param config       the active simulation configuration
     * @param selectedCell the currently selected grid cell, or {@code null} if no cell is selected
     */
    @SuppressWarnings("unused")
    void apply(GM model, STA statistics, CON config, @Nullable GridCell<ENT> selectedCell);

}

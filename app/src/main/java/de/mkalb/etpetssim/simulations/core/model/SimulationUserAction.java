package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.jspecify.annotations.Nullable;

/**
 * Defines a user-triggered action that may mutate the current simulation state.
 *
 * <p>Implementations receive the current model, statistics, configuration, a simulation-specific action context,
 * and optionally a selected grid cell. They may mutate the model or statistics in place.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GM>  grid model type that the action may mutate
 * @param <CON> immutable simulation config type
 * @param <STA> timed statistics type that the action may update
 * @param <CTX> simulation-specific action context type
 */
@FunctionalInterface
public interface SimulationUserAction<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics,
        CTX extends SimulationUserActionContext> {

    /**
     * Applies this action to the current simulation state.
     *
     * @param model        the current grid model, may be mutated by the action
     * @param statistics   the current simulation statistics, may be updated by the action
     * @param config       the active simulation configuration
     * @param context      the simulation-specific action context
     * @param selectedCell the currently selected grid cell, or {@code null} if no cell is selected
     */
    @SuppressWarnings("unused")
    void apply(GM model, STA statistics, CON config, CTX context, @Nullable GridCellView<ENT> selectedCell);

}

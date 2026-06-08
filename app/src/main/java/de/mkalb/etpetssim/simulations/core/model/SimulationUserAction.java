package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.shared.SimulationUserActionContext;
import org.jspecify.annotations.Nullable;

/**
 * Defines a user-triggered action that may mutate the current simulation state.
 *
 * <p>Implementations receive the active simulation manager, a simulation-specific action context,
 * and optionally a selected grid cell. Model, statistics, configuration, and step count are all
 * accessible through the manager.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GM>  grid model type that the action may mutate
 * @param <CON> immutable simulation config type
 * @param <STA> timed statistics type that the action may update
 * @param <SM>  concrete simulation manager type
 * @param <CTX> simulation-specific action context type
 */
@FunctionalInterface
public interface SimulationUserAction<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics,
        SM extends SimulationManager<ENT, GM, CON, STA>,
        CTX extends SimulationUserActionContext> {

    /**
     * Applies this action to the current simulation state.
     *
     * @param manager      the active simulation manager; provides model, statistics, config, and step count
     * @param context      the simulation-specific action context
     * @param selectedCell the currently selected grid cell, or {@code null} if no cell is selected
     */
    @SuppressWarnings("unused")
    void apply(SM manager, CTX context, @Nullable GridCellView<ENT> selectedCell);

}

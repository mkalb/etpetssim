package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.GridCellView;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import org.jspecify.annotations.Nullable;

/**
 * A {@link SimulationUserAction} implementation that intentionally performs no mutation.
 *
 * <p>This can be used as a default action when a simulation exposes no user-triggered behavior, or when an action
 * slot must be populated with a no-op implementation.
 *
 * @param <ENT> entity type stored in grid cells
 * @param <GM>  grid model type
 * @param <CON> immutable simulation config type
 * @param <STA> timed statistics type
 */
public final class NoUserAction<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics>
        implements SimulationUserAction<ENT, GM, CON, STA, NoUserActionContext> {

    @Override
    public void apply(
            GM model,
            STA statistics,
            CON config,
            NoUserActionContext context,
            @Nullable GridCellView<ENT> selectedCell) {
        // intentional no-op
    }

}
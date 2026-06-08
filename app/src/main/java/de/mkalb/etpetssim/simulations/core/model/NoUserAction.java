package de.mkalb.etpetssim.simulations.core.model;

import de.mkalb.etpetssim.engine.model.*;
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
 * @param <SM>  concrete simulation manager type
 */
public final class NoUserAction<
        ENT extends GridEntity,
        GM extends GridModel<ENT>,
        CON extends SimulationConfig,
        STA extends TimedSimulationStatistics,
        SM extends AbstractTimedSimulationManager<ENT, GM, CON, STA>>
        implements SimulationUserAction<ENT, GM, CON, STA, SM, NoUserActionContext> {

    @Override
    public void apply(SM manager, NoUserActionContext context, @Nullable GridCellView<ENT> selectedCell) {
        // intentional no-op
    }

}

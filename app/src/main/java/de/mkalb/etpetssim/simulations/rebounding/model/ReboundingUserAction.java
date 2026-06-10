package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.core.shared.NoUserActionContext;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import org.jspecify.annotations.Nullable;

public final class ReboundingUserAction
        implements SimulationUserAction<
        ReboundingEntity,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingSimulationManager,
        NoUserActionContext> {

    public ReboundingUserAction() {
    }

    @Override
    public void apply(ReboundingSimulationManager manager,
                      NoUserActionContext context,
                      @Nullable GridCellView<ReboundingEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var coordinate = selectedCell.coordinate();
        ReboundingEntity entity = manager.currentModel().getEntity(coordinate);
        if (!entity.isGround()) {
            return;
        }

        WritableGridModel<ReboundingEntity> model = manager.currentModel();
        ReboundingStatistics statistics = manager.statistics();

        model.setEntity(coordinate, TerrainConstant.WALL);
        statistics.increaseWallCells();
    }

}

package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.core.model.SimulationUserAction;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;
import de.mkalb.etpetssim.simulations.rebounding.shared.ReboundingUserActionContext;
import org.jspecify.annotations.Nullable;

public final class ReboundingUserAction
        implements SimulationUserAction<
        ReboundingEntity,
        WritableGridModel<ReboundingEntity>,
        ReboundingConfig,
        ReboundingStatistics,
        ReboundingSimulationManager,
        ReboundingUserActionContext> {

    public ReboundingUserAction() {
    }

    @Override
    public void apply(ReboundingSimulationManager manager,
                      ReboundingUserActionContext context,
                      @Nullable GridCellView<ReboundingEntity> selectedCell) {
        if (selectedCell == null) {
            return;
        }

        var model = manager.currentModel();
        var statistics = manager.statistics();
        var coordinate = selectedCell.coordinate();
        var entity = model.getEntity(coordinate);

        switch (context) {
            case ReboundingUserActionContext.FixedAction fixedAction -> {
                switch (fixedAction) {
                    case ADD_WALL -> {
                        if (entity.isGround()) {
                            model.setEntity(coordinate, TerrainConstant.WALL);
                            statistics.increaseWallCells();
                        }
                    }
                    case REMOVE_WALL -> {
                        if (entity.isWall()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decreaseWallCells();
                        }
                    }
                    case REMOVE_REBOUNDER -> {
                        if (entity.isRebounder()) {
                            model.setEntityToDefault(coordinate);
                            statistics.decreaseMovingEntityCells();
                        }
                    }
                }
            }
            case ReboundingUserActionContext.AddRebounder addRebounder -> {
                if (entity.isGround()) {
                    model.setEntity(coordinate, new Rebounder(addRebounder.direction()));
                    statistics.increaseMovingEntityCells();
                }
            }
        }
    }

}

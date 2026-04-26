package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Trail;

public final class EtpetsTerrainLogic {

    private EtpetsTerrainLogic() {
    }

    public static void apply(EtpetsGridModel gridModel) {
        // List is needed, because we will modify the terrainModel while iterating over the cells
        var nonDefaultCells = gridModel.terrainModel()
                                       .nonDefaultCells()
                                       .toList();
        for (GridCell<TerrainEntity> cell : nonDefaultCells) {
            if (cell.entity() instanceof Trail trail) {
                if (gridModel.agentModel().getEntity(cell.coordinate()).isEmpty()) {
                    trail.decrementIntensity(EtpetsBalance.TRAIL_INTENSITY_DECAY_PER_STEP);
                    if (trail.intensity() < EtpetsBalance.TRAIL_INTENSITY_RANGE_MIN) {
                        // Replace Trail with Ground
                        gridModel.terrainModel().setEntityToDefault(cell.coordinate());
                    }
                }
            }
        }
    }

}


package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.etpets.model.entity.TerrainEntity;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Trail;

public final class EtpetsTerrainLogic {

    private static final double TRAIL_DECAY_PER_STEP = 0.02d;

    private EtpetsTerrainLogic() {
    }

    public static void apply(EtpetsGridModel gridModel) {
        var nonDefaultCells = gridModel.terrainModel()
                                       .nonDefaultCells()
                                       .toList();
        for (GridCell<TerrainEntity> cell : nonDefaultCells) {
            if (cell.entity() instanceof Trail trail) {
                trail.decay(TRAIL_DECAY_PER_STEP);
                if (trail.isDepleted()) {
                    // Replace Trail with Ground
                    gridModel.terrainModel().setEntityToDefault(cell.coordinate());
                }
            }
        }
    }

}


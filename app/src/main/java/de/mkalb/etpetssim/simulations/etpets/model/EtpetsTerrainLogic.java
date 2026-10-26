package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.etpets.model.entity.*;

/**
 * Terrain phase of an ET Pets step: the decay of the trails.
 */
public final class EtpetsTerrainLogic {

    private EtpetsTerrainLogic() {
    }

    /**
     * Applies the terrain phase of one step: every trail without an agent on its cell loses intensity, and a trail
     * whose intensity falls below {@link EtpetsBalance#TRAIL_INTENSITY_MIN} turns back into ground.
     *
     * @param gridModel the grid model whose trails decay
     */
    public static void apply(EtpetsGridModel gridModel) {
        var nonDefaultCells = gridModel.terrainModel()
                                       .nonDefaultCells();
        for (GridCell<TerrainEntity> cell : nonDefaultCells) {
            if ((cell.entity() instanceof Trail trail)
                    && gridModel.agentModel().getEntity(cell.coordinate()).isEmpty()) {
                trail.adjustIntensity(-EtpetsBalance.TRAIL_INTENSITY_DECAY_PER_STEP);
                if (trail.intensity() < EtpetsBalance.TRAIL_INTENSITY_MIN) {
                    gridModel.terrainModel().setEntityToDefault(cell.coordinate());
                }
            }
        }
    }

}

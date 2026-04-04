package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainConstant;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsTerrainTrail;

public final class EtpetsTerrainLogic {

    private static final double TRAIL_DECAY_PER_STEP = 0.02d;

    private EtpetsTerrainLogic() {
    }

    public static void apply(EtpetsGridModel gridModel, EtpetsConfig config, int stepIndex, EtpetsStatistics statistics) {
        gridModel.terrainModel().nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof EtpetsTerrainTrail trail) {
                trail.decay(TRAIL_DECAY_PER_STEP);
                if (trail.isDepleted()) {
                    gridModel.terrainModel().setEntity(cell.coordinate(), EtpetsTerrainConstant.GROUND);
                }
            }
        });
    }

}


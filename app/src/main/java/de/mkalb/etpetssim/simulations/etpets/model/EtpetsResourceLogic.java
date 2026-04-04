package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceInsect;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourcePlant;

public final class EtpetsResourceLogic {

    private EtpetsResourceLogic() {
    }

    public static void apply(EtpetsGridModel gridModel, EtpetsConfig config, int stepIndex, EtpetsStatistics statistics) {
        gridModel.resourceModel().nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof EtpetsResourcePlant plant) {
                plant.regenerate();
            } else if (cell.entity() instanceof EtpetsResourceInsect insect) {
                insect.regenerate();
            }
        });
    }

}


package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;

public final class SugarResourceLogic {

    private SugarResourceLogic() {
    }

    public static void apply(SugarConfig config, SugarGridModel gridModel) {
        // Sugar regeneration
        gridModel.resourceModel().nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof Sugar sugar) {
                sugar.gainEnergy(config.sugarRegenerationRate());
            }
        });
    }

}

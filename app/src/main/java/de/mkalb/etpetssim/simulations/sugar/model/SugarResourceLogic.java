package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;

/**
 * Resource phase of a Sugarscape step: the regeneration of the sugar.
 */
public final class SugarResourceLogic {

    private SugarResourceLogic() {
    }

    /**
     * Applies the resource phase of one step: the sugar of every cell grows by the configured regeneration rate.
     *
     * @param gridModel the grid model whose sugar regenerates
     * @param config    the configuration with the sugar regeneration rate
     */
    public static void apply(SugarGridModel gridModel, SugarConfig config) {
        gridModel.resourceModel().nonDefaultCells().forEach(cell -> {
            if (cell.entity() instanceof Sugar sugar) {
                sugar.adjustAmount(config.sugarRegenerationRate());
            }
        });
    }

}

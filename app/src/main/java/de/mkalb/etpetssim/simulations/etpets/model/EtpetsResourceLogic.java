package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceBase;

/**
 * Resource phase of an ET Pets step: the regeneration of the resources.
 */
public final class EtpetsResourceLogic {

    private EtpetsResourceLogic() {
    }

    /**
     * Applies the resource phase of one step: every resource on the grid regenerates once.
     *
     * @param gridModel the grid model whose resources regenerate
     */
    public static void apply(EtpetsGridModel gridModel) {
        gridModel.resourceModel()
                .nonDefaultCells()
                .forEach(cell -> {
                    if (cell.entity() instanceof ResourceBase resource) {
                        resource.regenerate();
                    }
                });
    }

}

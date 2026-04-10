package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.simulations.etpets.model.entity.ResourceBase;

public final class EtpetsResourceLogic {

    private EtpetsResourceLogic() {
    }

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


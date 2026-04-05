package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsResourceGeneric;

public final class EtpetsResourceLogic {

    private EtpetsResourceLogic() {
    }

    public static void apply(EtpetsGridModel gridModel) {
        gridModel.resourceModel()
                 .nonDefaultCells()
                 .forEach(cell -> {
                     if (cell.entity() instanceof EtpetsResourceGeneric resource) {
                         resource.regenerate();
                     }
                 });
    }

}


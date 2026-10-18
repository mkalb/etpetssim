package de.mkalb.etpetssim.simulations.etpets.shared;

/**
 * Discrete resource options used by ET Pets edit actions.
 */
public enum EtpetsResourceChoice {

    /**
     * Removes the resource from the cell.
     */
    NONE("etpets.toolbar.setresource.option.none"),

    PLANT("etpets.toolbar.setresource.option.plant"),

    INSECT("etpets.toolbar.setresource.option.insect");

    private final String labelKey;

    EtpetsResourceChoice(String labelKey) {
        this.labelKey = labelKey;
    }

    public String labelKey() {
        return labelKey;
    }

}

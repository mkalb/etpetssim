package de.mkalb.etpetssim.simulations.etpets.shared;

/**
 * Discrete terrain options used by ET Pets edit actions.
 */
public enum EtpetsTerrainChoice {

    GROUND("etpets.toolbar.setterrain.option.ground"),

    ROCK("etpets.toolbar.setterrain.option.rock"),

    WATER("etpets.toolbar.setterrain.option.water");

    private final String labelKey;

    EtpetsTerrainChoice(String labelKey) {
        this.labelKey = labelKey;
    }

    public String labelKey() {
        return labelKey;
    }

}

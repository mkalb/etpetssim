package de.mkalb.etpetssim.simulations.etpets.shared;

/**
 * Discrete terrain options used by ET Pets edit actions.
 */
public enum EtpetsTerrainChoice {
    GROUND("etpets-terrain-ground", "etpets.toolbar.setterrain.option.ground"),
    ROCK("etpets-terrain-rock", "etpets.toolbar.setterrain.option.rock"),
    WATER("etpets-terrain-water", "etpets.toolbar.setterrain.option.water");

    private final String choiceId;
    private final String labelKey;

    EtpetsTerrainChoice(String choiceId,
                        String labelKey) {
        this.choiceId = choiceId;
        this.labelKey = labelKey;
    }

    public String choiceId() {
        return choiceId;
    }

    public String labelKey() {
        return labelKey;
    }
}

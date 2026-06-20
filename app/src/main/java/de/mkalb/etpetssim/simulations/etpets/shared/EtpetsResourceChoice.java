package de.mkalb.etpetssim.simulations.etpets.shared;

/**
 * Discrete resource options used by ET Pets edit actions.
 */
public enum EtpetsResourceChoice {
    NONE("etpets-resource-none", "etpets.toolbar.setresource.option.none"),
    PLANT("etpets-resource-plant", "etpets.toolbar.setresource.option.plant"),
    INSECT("etpets-resource-insect", "etpets.toolbar.setresource.option.insect");

    private final String choiceId;
    private final String labelKey;

    EtpetsResourceChoice(String choiceId,
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

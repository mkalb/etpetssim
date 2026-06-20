package de.mkalb.etpetssim.simulations.sugar.shared;

/**
 * Discrete amount levels used by the Sugar edit action.
 */
public enum SugarAddSugarLevel {
    LOW("sugar-low", "sugar.toolbar.addsugar.level.low", 0.25d),
    MEDIUM("sugar-medium", "sugar.toolbar.addsugar.level.medium", 0.50d),
    HIGH("sugar-high", "sugar.toolbar.addsugar.level.high", 1.00d);

    private final String levelId;
    private final String labelKey;
    private final double factorOfMaxSugarAmount;

    SugarAddSugarLevel(String levelId,
                       String labelKey,
                       double factorOfMaxSugarAmount) {
        this.levelId = levelId;
        this.labelKey = labelKey;
        this.factorOfMaxSugarAmount = factorOfMaxSugarAmount;
    }

    public String levelId() {
        return levelId;
    }

    public String labelKey() {
        return labelKey;
    }

    public int resolveSugarAmount(int maxSugarAmount) {
        return Math.max(1, Math.toIntExact(Math.round(maxSugarAmount * factorOfMaxSugarAmount)));
    }
}

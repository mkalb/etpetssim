package de.mkalb.etpetssim.simulations.sugar.shared;

/**
 * Discrete amount levels used by the Sugar edit action.
 *
 * <p>Each level adds a fixed fraction of the configured maximum sugar amount.
 */
public enum SugarAddSugarLevel {
    LOW("sugar.toolbar.addsugar.level.low", 0.25d),
    MEDIUM("sugar.toolbar.addsugar.level.medium", 0.50d),
    HIGH("sugar.toolbar.addsugar.level.high", 1.00d);

    private final String labelKey;
    private final double factorOfMaxSugarAmount;

    SugarAddSugarLevel(String labelKey,
                       double factorOfMaxSugarAmount) {
        this.labelKey = labelKey;
        this.factorOfMaxSugarAmount = factorOfMaxSugarAmount;
    }

    public String labelKey() {
        return labelKey;
    }

    /**
     * Computes the sugar amount of this level for the given maximum sugar amount.
     *
     * <p>The fraction of the maximum is rounded to the nearest integer, with ties rounded up.
     *
     * @param maxSugarAmount the configured maximum sugar amount of a cell
     * @return the rounded sugar amount, at least {@code 1}
     */
    public int computeSugarAmount(int maxSugarAmount) {
        return Math.max(1, Math.toIntExact(Math.round(maxSugarAmount * factorOfMaxSugarAmount)));
    }

}

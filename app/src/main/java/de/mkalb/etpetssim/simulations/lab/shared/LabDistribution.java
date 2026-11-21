package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Distributions of the highlighted cells at the start of the Simulation Lab.
 *
 * <p>Each distribution uses one grid initializer of the engine; the random distributions take their amount
 * from the configured percentage of highlighted cells, the pattern distribution places the configured pattern.
 */
public enum LabDistribution {

    /**
     * The configured share of the cells is highlighted exactly, at random cells.
     */
    RANDOM_PERCENT("labdistribution.randompercent"),

    /**
     * Each cell is highlighted independently with the configured percentage as probability.
     */
    RANDOM_PROBABILITY("labdistribution.randomprobability"),

    /**
     * The configured share of the cells is highlighted exactly, at the first cells of a shuffled list of all normal
     * cells.
     */
    RANDOM_CELLS("labdistribution.randomcells"),

    /**
     * The outermost rows and columns of the grid are highlighted.
     */
    BORDER("labdistribution.border"),

    /**
     * The configured pattern is highlighted at the center of the grid.
     */
    PATTERN("labdistribution.pattern");

    private final String resourceKey;

    LabDistribution(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "labdistribution.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

    /**
     * Returns whether this distribution takes its amount from the configured percentage of highlighted cells.
     *
     * @return {@code true} for the random distributions, otherwise {@code false}
     */
    public boolean usesHighlightedPercent() {
        return switch (this) {
            case RANDOM_PERCENT, RANDOM_PROBABILITY, RANDOM_CELLS -> true;
            case BORDER, PATTERN -> false;
        };
    }

    /**
     * Returns whether this distribution places the configured pattern.
     *
     * @return {@code true} for {@link #PATTERN}, otherwise {@code false}
     */
    public boolean usesPattern() {
        return this == PATTERN;
    }

}

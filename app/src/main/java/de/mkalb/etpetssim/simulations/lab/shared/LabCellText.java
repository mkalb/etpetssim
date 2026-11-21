package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Visibility of the texts of the base layer of the Simulation Lab: the coordinates of the checkerboard and the variant
 * numbers of the painter test.
 */
public enum LabCellText {

    /**
     * The base layer draws its texts, if the cells are large enough for the cell font.
     */
    SHOWN("labcelltext.shown"),

    /**
     * The base layer draws no texts; the text variants of the painter test and the texts of the dynamic layer and
     * the overlay stay.
     */
    HIDDEN("labcelltext.hidden");

    private final String resourceKey;

    LabCellText(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "labcelltext.label";
    }

    /**
     * Returns the resource key of the display text for this constant.
     *
     * @return the resource key of this constant
     */
    public String resourceKey() {
        return resourceKey;
    }

}

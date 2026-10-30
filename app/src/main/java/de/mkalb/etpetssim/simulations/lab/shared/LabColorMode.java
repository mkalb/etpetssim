package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Color rendering modes for the simulation lab canvas.
 */
public enum LabColorMode {

    /**
     * The canvas is drawn in color.
     */
    COLOR("labcolormode.color"),

    /**
     * The canvas is drawn in grayscale.
     */
    GRAYSCALE("labcolormode.grayscale");

    private final String resourceKey;

    LabColorMode(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "labcolormode.label";
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

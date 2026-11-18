package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Contents of the base layer of the Simulation Lab canvas, drawn once at the start.
 */
public enum LabBaseLayer {

    /**
     * A checkerboard of four colors by column and row parity, with the coordinate in each cell.
     */
    CHECKERBOARD("labbaselayer.checkerboard"),

    /**
     * A numbered list of drawing variants of the canvas painter; cell <i>i</i> in row-major order shows variant
     * <i>i</i> modulo the number of variants.
     */
    PAINTER_TEST("labbaselayer.paintertest");

    private final String resourceKey;

    LabBaseLayer(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "labbaselayer.label";
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

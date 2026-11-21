package de.mkalb.etpetssim.simulations.lab.shared;

/**
 * Implementation of the grid model of the Simulation Lab, so that the grid models can be compared for every
 * distribution and grid size.
 */
public enum LabGridModelType {

    /**
     * {@code SparseGridModel}: a hash map that stores only the cells whose entity differs from the default entity.
     */
    SPARSE("labgridmodeltype.sparse"),

    /**
     * {@code ArrayGridModel}: an array with one entity reference per cell.
     */
    ARRAY("labgridmodeltype.array");

    private final String resourceKey;

    LabGridModelType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * Returns the resource key of the label for this enum type as a whole.
     *
     * @return the resource key of the type label
     */
    public static String labelResourceKey() {
        return "labgridmodeltype.label";
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

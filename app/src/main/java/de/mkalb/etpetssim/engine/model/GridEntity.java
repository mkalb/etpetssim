package de.mkalb.etpetssim.engine.model;

/**
 * Represents a grid entity in the simulation engine.
 *
 * @see GridModel
 * @see GridCell
 */
public interface GridEntity {

    /**
     * Checks if the given grid entity is a constant entity.
     * <p>
     * This static method evaluates whether the provided entity is an instance
     * of the ConstantGridEntity class.
     *
     * @param entity the grid entity to check
     * @return true if the entity is a constant entity, false otherwise
     */
    @SuppressWarnings("ClassReferencesSubclass")
    static boolean isConstant(GridEntity entity) {
        return entity instanceof ConstantGridEntity;
    }

}

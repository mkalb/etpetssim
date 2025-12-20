package de.mkalb.etpetssim.engine.model.entity;

/**
 * Represents a grid entity in the simulation engine.
 *
 * @see de.mkalb.etpetssim.engine.model.GridModel
 * @see de.mkalb.etpetssim.engine.model.GridCell
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
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

    /**
     * Returns the unique descriptor ID of this entity.
     * <p>
     * This ID is used to look up the corresponding {@link GridEntityDescriptor}
     * in the {@link GridEntityDescriptorRegistry}.
     * </p>
     *
     * @return the descriptor ID
     * @see GridEntityDescriptorRegistry
     */
    String descriptorId();

    /**
     * Returns a short, human-readable string representation of this grid entity.
     * <p>
     * Implementations should override this method to provide a concise and meaningful
     * display string for the entity.
     * <p>
     * Format: {@code [short readable content]}
     * <br>
     * Example: {@code [WALL]}
     *
     * @return a concise display string for this grid entity
     */
    default String toDisplayString() {
        return String.format("[%s]", this);
    }

}

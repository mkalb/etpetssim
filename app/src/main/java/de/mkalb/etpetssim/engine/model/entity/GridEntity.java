package de.mkalb.etpetssim.engine.model.entity;

/**
 * Represents a grid entity in the simulation engine.
 *
 * <p>Every entity exposes a {@linkplain #descriptorId() descriptor ID} that resolves its
 * {@link GridEntityDescriptor}. Entities whose state does not change implement {@link ConstantGridEntity}.
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface GridEntity {

    /**
     * Checks if the given grid entity is a constant entity.
     *
     * @param entity the grid entity to check
     * @return {@code true} if the entity is a constant entity, {@code false} otherwise
     */
    @SuppressWarnings("ClassReferencesSubclass")
    static boolean isConstant(GridEntity entity) {
        return entity instanceof ConstantGridEntity;
    }

    /**
     * Returns the unique descriptor ID of this entity.
     *
     * <p>The descriptor ID is used to resolve the corresponding
     * {@link GridEntityDescriptor} in the {@link GridEntityDescriptorRegistry}.
     *
     * @return the descriptor ID
     */
    String descriptorId();

    /**
     * Returns a short, human-readable string representation of this grid entity.
     *
     * <p>Implementations may override this method to provide a concise display string.
     * The default implementation formats the result of {@code toString()}.
     *
     * <p>Format: {@code [short readable content]}, for example {@code [WALL]}.
     *
     * @return a concise display string for this grid entity
     */
    default String toDisplayString() {
        return "[" + this + "]";
    }

}

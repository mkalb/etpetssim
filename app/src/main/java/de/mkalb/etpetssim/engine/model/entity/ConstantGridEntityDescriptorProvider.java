package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Convenience interface for constant grid entities that provide descriptor metadata.
 * <p>
 * Implementations provide one {@link GridEntityDescriptorSpec} and reuse the default
 * descriptor accessors from this interface.
 * </p>
 *
 * @see ConstantGridEntity
 * @see GridEntityDescriptorProvider
 * @see GridEntityDescriptorSpec
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ConstantGridEntityDescriptorProvider extends ConstantGridEntity, GridEntityDescriptorProvider {

    /**
     * Returns the immutable descriptor specification for this entity.
     *
     * @return the {@link GridEntityDescriptorSpec} containing raw descriptor keys and rendering hints
     */
    GridEntityDescriptorSpec descriptorSpec();

    @Override
    default String descriptorId() {
        return descriptorSpec().descriptorId();
    }

    @Override
    default boolean visible() {
        return descriptorSpec().visible();
    }

    @Override
    default String shortNameKey() {
        return descriptorSpec().shortNameKey();
    }

    @Override
    default String longNameKey() {
        return descriptorSpec().longNameKey();
    }

    @Override
    default String descriptionKey() {
        return descriptorSpec().descriptionKey();
    }

    @Override
    default @Nullable String emojiKey() {
        return descriptorSpec().emojiKey();
    }

    @Override
    default @Nullable Paint color() {
        return descriptorSpec().color();
    }

    @Override
    default @Nullable Color borderColor() {
        return descriptorSpec().borderColor();
    }

}

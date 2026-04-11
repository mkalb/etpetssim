package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Convenience interface for descriptor-provider entities backed by a descriptor spec.
 * <p>
 * Implementations provide one {@link GridEntityDescriptorSpec} and reuse the default
 * descriptor accessors from this interface.
 * </p>
 *
 * @see GridEntityDescriptorProvider
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SpecBackedGridEntityDescriptorProvider extends GridEntityDescriptorProvider {

    /**
     * Returns the immutable descriptor specification for this entity.
     *
     * @return descriptor metadata
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
    default String shortKey() {
        return descriptorSpec().shortKey();
    }

    @Override
    default String longKey() {
        return descriptorSpec().longKey();
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

    @Override
    default int renderPriority() {
        return descriptorSpec().renderPriority();
    }

}


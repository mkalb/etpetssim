package de.mkalb.etpetssim.engine.model.entity;

/**
 * Convenience interface for constant grid entities that expose descriptor metadata
 * through a {@link GridEntityDescriptorSpec}.
 *
 * @see ConstantGridEntity
 * @see GridEntityDescriptorProvider
 * @see GridEntityDescriptorSpec
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface ConstantGridEntityDescriptorProvider
        extends ConstantGridEntity, SpecBackedGridEntityDescriptorProvider {

    @Override
    default String descriptorId() {
        return SpecBackedGridEntityDescriptorProvider.super.descriptorId();
    }

}

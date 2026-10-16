package de.mkalb.etpetssim.engine.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ConstantGridEntityDescriptorProviderTest {

    private static ConstantGridEntityDescriptorProvider createProvider() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "constant-id",
                false,
                "short.key",
                "long.key",
                "description.key",
                null,
                null,
                null
        );
        return new TestConstantGridEntityDescriptorProvider(spec);
    }

    @Test
    void testDescriptorId() {
        assertEquals("constant-id", createProvider().descriptorId());
    }

    @Test
    void testIsConstant() {
        assertTrue(GridEntity.isConstant(createProvider()));
    }

    private record TestConstantGridEntityDescriptorProvider(GridEntityDescriptorSpec descriptorSpec)
            implements ConstantGridEntityDescriptorProvider {
    }

}


package de.mkalb.etpetssim.simulations.sugar.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

final class ResourceEntityTest {

    @Test
    void testIsAgent() {
        assertFalse(NoResource.NO_RESOURCE.isAgent());
    }

}

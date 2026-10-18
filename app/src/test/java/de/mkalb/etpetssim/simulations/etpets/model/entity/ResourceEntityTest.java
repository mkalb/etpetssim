package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

final class ResourceEntityTest {

    @Test
    void testIsTerrain() {
        assertFalse(NoResource.NO_RESOURCE.isTerrain());
    }

    @Test
    void testIsAgent() {
        assertFalse(NoResource.NO_RESOURCE.isAgent());
    }

}

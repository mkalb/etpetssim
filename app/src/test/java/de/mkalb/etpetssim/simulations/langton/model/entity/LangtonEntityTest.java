package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LangtonEntityTest {

    @Test
    void testAllEntityDescriptorProviders() {
        List<GridEntityDescriptorProvider> providers = LangtonEntity.allEntityDescriptorProviders();
        List<String> descriptorIds = providers.stream().map(GridEntityDescriptorProvider::descriptorId).toList();
        assertAll(
                () -> assertEquals(EntityDescriptors.values().length + TerrainConstant.values().length, providers.size()),
                () -> assertEquals(descriptorIds.size(), Set.copyOf(descriptorIds).size()),
                () -> assertThrows(UnsupportedOperationException.class, () -> providers.add(TerrainConstant.UNVISITED))
        );
    }

}

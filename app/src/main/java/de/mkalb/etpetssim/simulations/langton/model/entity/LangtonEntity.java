package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Common contract for all entities in Langton's Ant.
 */
public sealed interface LangtonEntity extends GridEntity
        permits AntEntity, TerrainConstant {

    String DESCRIPTOR_ID_ANT = "ant";
    String DESCRIPTOR_ID_NO_AGENT = "no_agent";

    /**
     * Returns all descriptor providers used by Langton's Ant.
     *
     * @return immutable list of descriptor providers
     */
    static List<GridEntityDescriptorProvider> allEntityDescriptorProviders() {
        return Stream.<GridEntityDescriptorProvider>concat(Arrays.stream(EntityDescriptors.values()),
                        Arrays.stream(TerrainConstant.values()))
                .toList();
    }

    boolean isAgent();

}

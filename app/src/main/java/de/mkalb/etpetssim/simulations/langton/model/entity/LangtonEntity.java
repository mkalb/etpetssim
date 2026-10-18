package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;

import java.util.*;
import java.util.stream.*;

/**
 * Common contract for all entities in the Langton simulation.
 */
public sealed interface LangtonEntity extends GridEntity
        permits AntEntity, TerrainConstant {

    String DESCRIPTOR_ID_ANT = "ant";
    String DESCRIPTOR_ID_NO_AGENT = "no_agent";

    /**
     * Returns all descriptor providers used by the Langton simulation.
     *
     * @return immutable list of descriptor providers
     */
    static List<GridEntityDescriptorProvider> allEntityDescriptorProviders() {
        return Stream.concat(Arrays.stream(EntityDescriptors.values()), Arrays.stream(TerrainConstant.values()))
                     .collect(Collectors.toUnmodifiableList());
    }

    boolean isAgent();

}

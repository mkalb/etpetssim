package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider;

import java.util.*;
import java.util.stream.*;

/**
 * Common contract for all entities in the Langton simulation.
 */
public sealed interface LangtonEntity extends GridEntity
        permits AntEntity, TerrainConstant {

    /**
     * Returns all descriptor providers used by the Langton simulation.
     *
     * @return immutable list of descriptor providers
     */
    static List<GridEntityDescriptorProvider> allEntityDescriptorProviders() {
        return Stream.concat(Arrays.stream(EntityDescriptors.values()), Arrays.stream(TerrainConstant.values()))
                     .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Indicates whether this entity is an agent.
     *
     * @return {@code true} when this entity represents an agent
     */
    boolean isAgent();

}

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
        List<GridEntityDescriptorProvider> allEntityDescriptorProviders = new ArrayList<>();
        allEntityDescriptorProviders.addAll(Arrays.asList(EntityDescriptors.values()));
        allEntityDescriptorProviders.addAll(Arrays.asList(TerrainConstant.values()));
        return Collections.unmodifiableList(allEntityDescriptorProviders);
    }

    /**
     * Indicates whether this entity is an agent.
     *
     * @return {@code true} when this entity represents an agent
     */
    boolean isAgent();

}

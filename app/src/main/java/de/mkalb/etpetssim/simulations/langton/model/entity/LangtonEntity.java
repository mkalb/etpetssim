package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorProvider;

import java.util.*;

public sealed interface LangtonEntity extends GridEntity
        permits AntEntity, TerrainConstant {

    static List<GridEntityDescriptorProvider> allEntityDescriptorProviders() {
        List<GridEntityDescriptorProvider> allEntityDescriptorProviders = new ArrayList<>();
        allEntityDescriptorProviders.addAll(Arrays.asList(EntityDescriptors.values()));
        allEntityDescriptorProviders.addAll(Arrays.asList(TerrainConstant.values()));
        return allEntityDescriptorProviders;
    }

    boolean isAgent();

}

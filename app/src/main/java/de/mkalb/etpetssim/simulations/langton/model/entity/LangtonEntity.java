package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;

import java.util.*;

public sealed interface LangtonEntity extends GridEntity
        permits AntEntity, TerrainConstant {

    static List<GridEntityDescribable> allEntityDescribable() {
        List<GridEntityDescribable> allEntityDescribable = new ArrayList<>();
        allEntityDescribable.addAll(Arrays.asList(EntityDescriptors.values()));
        allEntityDescribable.addAll(Arrays.asList(TerrainConstant.values()));
        return allEntityDescribable;
    }

    boolean isAgent();

}

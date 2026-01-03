package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;

import java.util.*;

public sealed interface LangtonEntity extends GridEntity
        permits LangtonAntEntity, LangtonGroundEntity {

    static List<GridEntityDescribable> allEntityDescribable() {
        List<GridEntityDescribable> allEntityDescribable = new ArrayList<>();
        allEntityDescribable.addAll(Arrays.asList(LangtonAntEntityDescribable.values()));
        allEntityDescribable.addAll(Arrays.asList(LangtonGroundEntity.values()));
        return allEntityDescribable;
    }

    boolean isAgent();

}

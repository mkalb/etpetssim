package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescribable;

import java.util.*;

public sealed interface LangtonEntity extends GridEntity
        permits LangtonAntEntity, LangtonGroundEntity {

    static List<GridEntityDescribable> allEntityDescribable() {
        List<GridEntityDescribable> describables = new ArrayList<>();
        describables.addAll(Arrays.asList(LangtonAntEntityDescribable.values()));
        describables.addAll(Arrays.asList(LangtonGroundEntity.values()));
        return describables;
    }

    boolean isAgent();

}

package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.GridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescribable;

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

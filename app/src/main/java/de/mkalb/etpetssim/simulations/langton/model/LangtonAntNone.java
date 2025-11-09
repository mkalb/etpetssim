package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;

public enum LangtonAntNone implements LangtonAntEntity, ConstantGridEntity {

    NONE;

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public String descriptorId() {
        return LangtonAntEntity.DESCRIPTOR_ID_ANT_NONE;
    }

}

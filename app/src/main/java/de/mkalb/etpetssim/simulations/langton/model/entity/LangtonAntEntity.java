package de.mkalb.etpetssim.simulations.langton.model.entity;

public sealed interface LangtonAntEntity extends LangtonEntity
        permits LangtonAnt, LangtonAntNone {

    String DESCRIPTOR_ID_ANT_NONE = "none";
    String DESCRIPTOR_ID_ANT = "ant";

}

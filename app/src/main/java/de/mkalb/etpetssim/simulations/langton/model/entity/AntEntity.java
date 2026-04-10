package de.mkalb.etpetssim.simulations.langton.model.entity;

public sealed interface AntEntity extends LangtonEntity
        permits Ant, NoAgent {

    String DESCRIPTOR_ID_ANT_NONE = "none";
    String DESCRIPTOR_ID_ANT = "ant";

}

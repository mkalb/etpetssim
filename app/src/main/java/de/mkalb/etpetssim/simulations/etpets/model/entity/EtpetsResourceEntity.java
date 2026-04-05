package de.mkalb.etpetssim.simulations.etpets.model.entity;

public sealed interface EtpetsResourceEntity extends EtpetsEntity
        permits EtpetsResourceNone, EtpetsResourceGeneric {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isResource() {
        return true;
    }

    @Override
    default boolean isAgent() {
        return false;
    }

}


package de.mkalb.etpetssim.simulations.etpets.model.entity;

public sealed interface EtpetsAgentEntity extends EtpetsEntity
        permits EtpetsAgentNone, EtpetsPet, EtpetsPetEgg {

    @Override
    default boolean isTerrain() {
        return false;
    }

    @Override
    default boolean isResource() {
        return false;
    }

}


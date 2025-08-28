package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.GridEntity;

public sealed interface WatorEntity extends GridEntity
        permits WatorCreature, WatorConstantEntity {

    String DESCRIPTOR_ID_WATER = "water";
    String DESCRIPTOR_ID_FISH = "fish";
    String DESCRIPTOR_ID_SHARK = "shark";

    boolean isAgent();

    boolean isFish();

    boolean isShark();

    boolean isWater();

}

package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

public sealed interface ReboundingEntity extends GridEntity
        permits TerrainConstant, Rebounder {

    String DESCRIPTOR_ID_GROUND = "ground";
    String DESCRIPTOR_ID_WALL = "wall";
    String DESCRIPTOR_ID_REBOUNDER = "rebounder";

    boolean isGround();

    boolean isWall();

    boolean isRebounder();

}

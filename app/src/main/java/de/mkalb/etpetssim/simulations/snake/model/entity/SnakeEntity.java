package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.GridEntity;

public sealed interface SnakeEntity extends GridEntity
        permits SnakeConstantEntity, SnakeHead {

    String DESCRIPTOR_ID_GROUND = "ground";
    String DESCRIPTOR_ID_WALL = "wall";
    String DESCRIPTOR_ID_GROWTH_FOOD = "growth_food";
    String DESCRIPTOR_ID_SNAKE_SEGMENT = "snake_segment";
    String DESCRIPTOR_ID_SNAKE_HEAD = "snake_head";

    boolean isAgent();

    default boolean isGround() {
        return descriptorId().equals(DESCRIPTOR_ID_GROUND);
    }

}

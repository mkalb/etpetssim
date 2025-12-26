package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntity;

public enum SnakeConstantEntity implements SnakeEntity, ConstantGridEntity {

    GROUND(SnakeEntity.DESCRIPTOR_ID_GROUND),
    WALL(SnakeEntity.DESCRIPTOR_ID_WALL),
    GROWTH_FOOD(SnakeEntity.DESCRIPTOR_ID_GROWTH_FOOD),
    SNAKE_SEGMENT(SnakeEntity.DESCRIPTOR_ID_SNAKE_SEGMENT);

    private final String descriptorId;

    SnakeConstantEntity(String descriptorId) {
        this.descriptorId = descriptorId;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return descriptorId;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

}

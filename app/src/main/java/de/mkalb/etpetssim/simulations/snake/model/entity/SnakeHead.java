package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;

import java.util.*;

public final class SnakeHead implements SnakeEntity {

    private final int id;
    private final Deque<GridCoordinate> snakeSegments;

    private int pendingGrowth;
    private int deaths;
    private int stepIndexOfSpawn;

    public SnakeHead(int id,
                     int initialPendingGrowth,
                     int stepIndexOfSpawn) {
        if (initialPendingGrowth < 0) {
            throw new IllegalArgumentException("initialPendingGrowth must be non-negative");
        }
        this.id = id;
        snakeSegments = new ArrayDeque<>();
        pendingGrowth = initialPendingGrowth;
        deaths = 0;
        this.stepIndexOfSpawn = stepIndexOfSpawn;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    public int id() {
        return id;
    }

    public int pendingGrowth() {
        return pendingGrowth;
    }

    public int deaths() {
        return deaths;
    }

    public int stepIndexOfSpawn() {
        return stepIndexOfSpawn;
    }

    public int ageAtStepIndex(int stepIndex) {
        return stepIndex - stepIndexOfSpawn;
    }

    public int ageAtStepCount(int stepCount) {
        return ageAtStepIndex(stepCount - 1);
    }

    public void move(GridCoordinate lastHeaderCoordinate, int additionalGrowth) {
        snakeSegments.addFirst(lastHeaderCoordinate);
        pendingGrowth += additionalGrowth;
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            snakeSegments.removeLast();
            pendingGrowth = 0; // Ensure non-negative
        }
    }

    public void die(int stepIndexOfRespawn) {
        snakeSegments.clear();
        pendingGrowth = 0;
        deaths++;
        stepIndexOfSpawn = stepIndexOfRespawn;
    }

    @Override
    public String toString() {
        return "SnakeHead{" +
                "id=" + id +
                ", pendingGrowth=" + pendingGrowth +
                ", deaths=" + deaths +
                ", stepIndexOfSpawn=" + stepIndexOfSpawn +
                ", snakeSegments=" + snakeSegments +
                '}';
    }

}

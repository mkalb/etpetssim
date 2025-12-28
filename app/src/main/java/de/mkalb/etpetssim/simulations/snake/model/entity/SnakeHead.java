package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeHead implements SnakeEntity {

    private final int id;
    private final Deque<GridCoordinate> snakeSegments;

    private int pendingGrowth;
    private int deaths;
    private int stepIndexOfSpawn;
    private boolean dead;
    private @Nullable CompassDirection direction;

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
        dead = false;
        direction = null;
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

    public boolean isDead() {
        return dead;
    }

    public Optional<CompassDirection> direction() {
        return Optional.ofNullable(direction);
    }

    public List<GridCoordinate> currentSegments() {
        return List.copyOf(snakeSegments);
    }

    public Optional<GridCoordinate> move(GridCoordinate lastHeadCoordinate, CompassDirection moveDirection, int additionalGrowth) {
        snakeSegments.addFirst(lastHeadCoordinate);
        direction = moveDirection;
        pendingGrowth += additionalGrowth;
        if (pendingGrowth > 0) {
            pendingGrowth--;
            return Optional.empty();
        } else {
            pendingGrowth = 0; // Ensure non-negative
            return Optional.of(snakeSegments.removeLast());
        }
    }

    public void die() {
        dead = true;
        deaths++;
    }

    public void respawn(int initialPendingGrowth, int stepIndexOfRespawn) {
        dead = false;
        snakeSegments.clear();
        pendingGrowth = initialPendingGrowth;
        stepIndexOfSpawn = stepIndexOfRespawn;
        direction = null;
    }

    @Override
    public String toString() {
        return "SnakeHead{" +
                "id=" + id +
                ", pendingGrowth=" + pendingGrowth +
                ", deaths=" + deaths +
                ", stepIndexOfSpawn=" + stepIndexOfSpawn +
                ", dead=" + dead +
                ", direction=" + direction +
                ", snakeSegments=" + snakeSegments +
                '}';
    }

}

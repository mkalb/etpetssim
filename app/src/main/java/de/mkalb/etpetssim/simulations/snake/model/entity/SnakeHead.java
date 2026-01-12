package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class SnakeHead implements SnakeEntity {

    private final int id;
    private final SnakeMoveStrategy strategy;
    private final Deque<GridCoordinate> snakeSegments;

    private int pendingGrowth;
    private int deaths;
    private int stepIndexOfSpawn;
    private boolean dead;
    private int points;
    private int maxSegmentCount;
    private @Nullable CompassDirection direction;

    public SnakeHead(int id,
                     SnakeMoveStrategy strategy,
                     int initialPendingGrowth,
                     int stepIndexOfSpawn) {
        if (initialPendingGrowth < 0) {
            throw new IllegalArgumentException("initialPendingGrowth must be non-negative");
        }
        this.id = id;
        this.strategy = strategy;
        snakeSegments = new ArrayDeque<>();
        pendingGrowth = initialPendingGrowth;
        deaths = 0;
        this.stepIndexOfSpawn = stepIndexOfSpawn;
        dead = false;
        points = 0;
        maxSegmentCount = 0;
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

    public SnakeMoveStrategy strategy() {
        return strategy;
    }

    public int pendingGrowth() {
        return pendingGrowth;
    }

    public int deaths() {
        return deaths;
    }

    public int points() {
        return points;
    }

    public int maxSegmentCount() {
        return maxSegmentCount;
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

    public int segmentCount() {
        return snakeSegments.size();
    }

    public Optional<GridCoordinate> move(GridCoordinate lastHeadCoordinate, CompassDirection moveDirection, int additionalGrowth, int addedPoints) {
        snakeSegments.addFirst(lastHeadCoordinate);
        direction = moveDirection;
        pendingGrowth += additionalGrowth;
        points += addedPoints;
        if (pendingGrowth > 0) {
            pendingGrowth--;
            maxSegmentCount = Math.max(maxSegmentCount, segmentCount());
            return Optional.empty();
        } else {
            pendingGrowth = 0; // Ensure non-negative
            maxSegmentCount = Math.max(maxSegmentCount, segmentCount() - 1);
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
    public String toDisplayString() {
        return String.format("[SNAKE_HEAD #%d %s %s %s %s]",
                id,
                (direction == null) ? "+" : direction.arrow(),
                dead ? ("†" + deaths) : ("*" + stepIndexOfSpawn),
                (pendingGrowth > 0) ? (snakeSegments.size() + "+" + pendingGrowth) : String.valueOf(snakeSegments.size()),
                strategy.toString());
    }

    @Override
    public String toString() {
        int size = snakeSegments.size();
        String segmentsStr = switch (size) {
            case 0 -> "#0 ()";
            case 1 -> "#1 " + snakeSegments.peekFirst().toDisplayString();
            case 2 -> "#2 %s %s".formatted(
                    snakeSegments.peekFirst().toDisplayString(),
                    snakeSegments.peekLast().toDisplayString());
            default -> "#%d %s...%s".formatted(size,
                    snakeSegments.peekFirst().toDisplayString(),
                    snakeSegments.peekLast().toDisplayString());
        };

        return "SnakeHead{" +
                "id=" + id +
                ", strategy=" + strategy.toString() +
                ", stepIndexOfSpawn=" + stepIndexOfSpawn +
                ", direction=" + direction +
                ", dead=" + dead +
                ", deaths=" + deaths +
                ", pendingGrowth=" + pendingGrowth +
                ", segments=" + segmentsStr +
                '}';
    }

}

package de.mkalb.etpetssim.simulations.snake.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.snake.model.strategy.SnakeMoveStrategy;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Snake head of Snake, the agent that moves by its strategy and drags its segments behind it.
 */
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
        checkInitialPendingGrowth(initialPendingGrowth);
        this.id = id;
        this.strategy = strategy;
        snakeSegments = new ArrayDeque<>();
        pendingGrowth = initialPendingGrowth;
        this.stepIndexOfSpawn = stepIndexOfSpawn;
    }

    private static void checkInitialPendingGrowth(int initialPendingGrowth) {
        if (initialPendingGrowth < 0) {
            throw new IllegalArgumentException("initialPendingGrowth must be >= 0, but was: " + initialPendingGrowth);
        }
    }

    @Override
    public String descriptorId() {
        return SnakeEntity.DESCRIPTOR_ID_SNAKE_HEAD;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public boolean isGround() {
        return false;
    }

    @Override
    public boolean isWall() {
        return false;
    }

    @Override
    public boolean isFood() {
        return false;
    }

    public int id() {
        return id;
    }

    public SnakeMoveStrategy strategy() {
        return strategy;
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

    public Optional<GridCoordinate> move(GridCoordinate lastHeadCoordinate,
                                         CompassDirection moveDirection,
                                         int additionalGrowth,
                                         int addedPoints) {
        snakeSegments.addFirst(lastHeadCoordinate);
        direction = moveDirection;
        pendingGrowth += additionalGrowth;
        points += addedPoints;
        if (pendingGrowth > 0) {
            pendingGrowth--;
            maxSegmentCount = Math.max(maxSegmentCount, segmentCount());
            return Optional.empty();
        } else {
            maxSegmentCount = Math.max(maxSegmentCount, segmentCount() - 1);
            return Optional.of(snakeSegments.removeLast());
        }
    }

    public void die() {
        dead = true;
        deaths++;
    }

    public void respawn(int initialPendingGrowth, int stepIndexOfRespawn) {
        checkInitialPendingGrowth(initialPendingGrowth);
        dead = false;
        snakeSegments.clear();
        pendingGrowth = initialPendingGrowth;
        stepIndexOfSpawn = stepIndexOfRespawn;
        direction = null;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[SNAKE #%d %s %s L=%s P=%d %s]",
                id,
                (direction == null) ? "+" : direction.arrow(),
                dead ? ("†" + deaths) : ("*" + stepIndexOfSpawn),
                (pendingGrowth > 0) ? (snakeSegments.size() + "+" + pendingGrowth)
                        : String.valueOf(snakeSegments.size()),
                points,
                strategy.displayName());
    }

    @Override
    public String toString() {
        int size = snakeSegments.size();
        String segmentsStr = switch (size) {
            case 0 -> "#0 ()";
            case 1 -> "#1 " + snakeSegments.getFirst().toDisplayString();
            case 2 -> String.format(Locale.ROOT, "#2 %s %s",
                    snakeSegments.getFirst().toDisplayString(),
                    snakeSegments.getLast().toDisplayString());
            default -> String.format(Locale.ROOT, "#%d %s...%s", size,
                    snakeSegments.getFirst().toDisplayString(),
                    snakeSegments.getLast().toDisplayString());
        };

        return "SnakeHead{" +
                "id=" + id +
                ", strategy=" + strategy.displayName() +
                ", stepIndexOfSpawn=" + stepIndexOfSpawn +
                ", direction=" + direction +
                ", dead=" + dead +
                ", deaths=" + deaths +
                ", points=" + points +
                ", pendingGrowth=" + pendingGrowth +
                ", maxSegmentCount=" + maxSegmentCount +
                ", segments=" + segmentsStr +
                '}';
    }

}

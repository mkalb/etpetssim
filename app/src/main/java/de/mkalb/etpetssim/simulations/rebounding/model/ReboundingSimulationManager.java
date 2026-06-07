package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.executor.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.*;
import de.mkalb.etpetssim.engine.support.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.*;

import java.util.*;

public final class ReboundingSimulationManager
        extends AbstractTimedSimulationManager<ReboundingEntity, WritableGridModel<ReboundingEntity>, ReboundingConfig,
        ReboundingStatistics> {

    private final GridStructure structure;
    private final ReboundingStatistics statistics;
    private final TimedSimulationExecutor<ReboundingEntity, WritableGridModel<ReboundingEntity>> executor;

    public ReboundingSimulationManager(ReboundingConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new ReboundingStatistics(structure);
        var random = new Random(config.seed());
        var model = new SparseGridModel<ReboundingEntity>(structure, TerrainConstant.GROUND);

        var agentStepLogic = new ReboundingStepLogic(structure, config);
        var runner = new AsynchronousStepRunner<>(model, ReboundingEntity::isRebounder, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new ReboundingTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        initializeGrid(config, model, random);

        initializeStatistics(model);
    }

    private void initializeGrid(ReboundingConfig config, WritableGridModel<ReboundingEntity> model, Random random) {
        createWallInitializer(config).initialize(model);
        createMovingEntityInitializer(config, random).initialize(model);
    }

    private int computeMovingEntityCount(ReboundingConfig config) {
        return Math.toIntExact(Math.round(structure.cellCount() * config.movingEntityPercent()));
    }

    private GridInitializer<ReboundingEntity> createWallInitializer(ReboundingConfig config) {
        int width = structure.size().width();
        int height = structure.size().height();
        int wallCount = config.verticalWalls();
        int movingEntityCount = computeMovingEntityCount(config);
        int wallEntityCount = wallCount * height;

        if ((wallCount > 0)
                && (width >= wallCount)
                && ((movingEntityCount + wallEntityCount) <= structure.size().area())) {
            Set<Integer> wallXPositions = computeWallXPositions(width, wallCount);
            List<GridCell<ReboundingEntity>> cells = new ArrayList<>();
            for (int x : wallXPositions) {
                for (int y = 0; y < height; y++) {
                    cells.add(new GridCell<>(new GridCoordinate(x, y), TerrainConstant.WALL));
                }
            }
            return GridInitializers.fromList(cells);
        }
        return GridInitializers.identity();
    }

    private Set<Integer> computeWallXPositions(int width, int wallCount) {
        // Integer division (floor), ensures that walls have equal spacing and fit within the grid
        int distanceX = width / wallCount;
        // Center walls within the grid. Leave additional space on the left and right.
        int adjustment = (-1 - ((distanceX - 1) / 2)) + ((width % wallCount) / 2);
        Set<Integer> positions = new TreeSet<>();
        for (int i = 1; i <= wallCount; i++) {
            positions.add((i * distanceX) + adjustment);
        }
        return positions;
    }

    private GridInitializer<ReboundingEntity> createMovingEntityInitializer(ReboundingConfig config, Random random) {
        int movingEntityCount = computeMovingEntityCount(config);
        if (movingEntityCount == 0) {
            return GridInitializers.identity();
        }
        List<CompassDirection> directionRing = resolveDirectionRing(config);
        List<ReboundingEntity> movingEntities = new ArrayList<>(movingEntityCount);
        for (int i = 0; i < movingEntityCount; i++) {
            movingEntities.add(new Rebounder(directionRing.get(random.nextInt(directionRing.size()))));
        }
        return GridInitializers.placeAllAtRandomPositions(
                movingEntities,
                ReboundingEntity::isGround,
                random);
    }

    private List<CompassDirection> resolveDirectionRing(ReboundingConfig config) {
        if (structure.cellShape() == CellShape.SQUARE) {
            return (config.neighborhoodMode() == NeighborhoodMode.EDGES_AND_VERTICES)
                    ? CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING
                    : CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING;
        } else if (structure.cellShape() == CellShape.HEXAGON) {
            return CellNeighborhoods.HEXAGON_DIRECTION_RING;
        }
        throw new IllegalStateException("Unsupported cell shape: " + structure.cellShape());
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public ReboundingStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void initializeStatistics(WritableGridModel<ReboundingEntity> model) {
        int wallCellsInitial = Math.toIntExact(model
                .countEntities(ReboundingEntity::isWall));
        int movingEntityCellsInitial = Math.toIntExact(model
                .countEntities(ReboundingEntity::isRebounder));
        statistics.updateInitialCells(wallCellsInitial, movingEntityCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<ReboundingEntity, WritableGridModel<ReboundingEntity>> executor() {
        return executor;
    }

}

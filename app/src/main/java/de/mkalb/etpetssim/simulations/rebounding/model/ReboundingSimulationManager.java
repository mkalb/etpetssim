package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant;

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
        statistics = new ReboundingStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SparseGridModel<ReboundingEntity>(structure, TerrainConstant.GROUND);

        // Executor with runner and terminationCondition
        var agentStepLogic = new ReboundingStepLogic(structure, config);
        var runner = new AsynchronousStepRunner<>(model, ReboundingEntity::isRebounder, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new ReboundingTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(ReboundingConfig config, WritableGridModel<ReboundingEntity> model, Random random) {
        wallInitializer(config).initialize(model);
        movingEntityInitializer(config, random).initialize(model);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private GridInitializer<ReboundingEntity> wallInitializer(ReboundingConfig config) {
        int width = structure.size().width();
        int height = structure.size().height();
        int wallCount = config.verticalWalls();
        int movingEntityCount = (int) (structure.cellCount() * config.movingEntityPercent());
        int wallEntityCount = wallCount * height;

        if ((wallCount > 0)
                && (width >= wallCount)
                && ((movingEntityCount + wallEntityCount) <= structure.size().area())) {
            // Integer division (floor), ensures that walls have equal spacing and fit within the grid
            int distanceX = width / wallCount;
            // Center walls within the grid. Leave additional space on the left and right.
            int adjustment = (-1 - ((distanceX - 1) / 2)) + ((width % wallCount) / 2);

            // Calculate wall x-positions
            Set<Integer> wallXPositions = new TreeSet<>();
            for (int i = 1; i <= wallCount; i++) {
                int x = (i * distanceX) + adjustment;
                wallXPositions.add(x);
            }

            // Create wall cells
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

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private GridInitializer<ReboundingEntity> movingEntityInitializer(ReboundingConfig config, Random random) {
        if (config.movingEntityPercent() > 0.0d) {
            List<CompassDirection> directionRing;
            if (structure.cellShape() == CellShape.SQUARE) {
                directionRing = (config.neighborhoodMode() == NeighborhoodMode.EDGES_AND_VERTICES)
                        ? CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING
                        : CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING;
            } else if (structure.cellShape() == CellShape.HEXAGON) {
                directionRing = CellNeighborhoods.HEXAGON_DIRECTION_RING;
            } else {
                throw new IllegalStateException("Unsupported cell shape: " + structure.cellShape());
            }
            int movingEntityCount = (int) (structure.cellCount() * config.movingEntityPercent());
            List<ReboundingEntity> movingEntities = new ArrayList<>(movingEntityCount);
            for (int i = 0; i < movingEntityCount; i++) {
                movingEntities.add(new Rebounder(directionRing.get(random.nextInt(directionRing.size()))));
            }
            return GridInitializers.placeAllAtRandomPositions(
                    movingEntities,
                    ReboundingEntity::isGround,
                    random);
        }
        return GridInitializers.identity();
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

    private void updateInitialStatistics(WritableGridModel<ReboundingEntity> model) {
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

package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.AsynchronousStepRunner;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.model.WritableGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.support.AgentOrderingStrategies;
import de.mkalb.etpetssim.engine.support.GridInitializer;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.Rebounder;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.TerrainConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static de.mkalb.etpetssim.engine.support.WorkCheckpoints.isCancellationCheckpoint;

/**
 * Simulation manager of Rebounding.
 */
public final class ReboundingSimulationManager
        extends AbstractTimedSimulationManager<ReboundingEntity, WritableGridModel<ReboundingEntity>, ReboundingConfig,
        ReboundingStatistics> {

    private final GridStructure structure;
    private final ReboundingStatistics statistics;
    private final TimedSimulationExecutor<ReboundingEntity, WritableGridModel<ReboundingEntity>> executor;

    public ReboundingSimulationManager(ReboundingConfig config) {
        this(config, SimulationInitializationCancellation.none());
    }

    public ReboundingSimulationManager(ReboundingConfig config, SimulationInitializationCancellation cancellation) {
        super(config, ReboundingStatistics.metrics());
        cancellation.checkCanceled();

        structure = config.createGridStructure();
        statistics = new ReboundingStatistics(structure);
        var random = new Random(config.seed());
        var model = new SparseGridModel<ReboundingEntity>(structure, TerrainConstant.GROUND);

        var agentStepLogic = new ReboundingStepLogic(structure, config);
        var runner = new AsynchronousStepRunner<>(model, ReboundingEntity::isRebounder,
                AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new ReboundingTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model,
                terminationCondition, statistics));

        initializeGrid(model, random, cancellation);
        cancellation.checkCanceled();
        initializeStatistics(model);
        recordInitialStatisticsSample();
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
        statistics.setStepData(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @Override
    protected TimedSimulationExecutor<ReboundingEntity, WritableGridModel<ReboundingEntity>> executor() {
        return executor;
    }

    private void initializeGrid(WritableGridModel<ReboundingEntity> model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        createWallInitializer(cancellation).initialize(model);
        createRebounderInitializer(random, cancellation).initialize(model);
    }

    private int computeRebounderCount() {
        return Math.toIntExact(Math.round(structure.cellCount() * config().rebounderPercent()));
    }

    private GridInitializer<ReboundingEntity> createWallInitializer(SimulationInitializationCancellation cancellation) {
        int width = structure.size().width();
        int height = structure.size().height();
        int wallCount = config().verticalWalls();
        int rebounderCount = computeRebounderCount();
        int wallEntityCount = wallCount * height;

        if ((wallCount > 0)
                && (width >= wallCount)
                && ((rebounderCount + wallEntityCount) <= structure.size().area())) {
            Set<Integer> wallXPositions = computeWallXPositions(width, wallCount);
            List<GridCell<ReboundingEntity>> cells = new ArrayList<>();
            int index = 0;
            for (int x : wallXPositions) {
                for (int y = 0; y < height; y++) {
                    if (isCancellationCheckpoint(index)) {
                        cancellation.checkCanceled();
                    }
                    index++;
                    cells.add(new GridCell<>(new GridCoordinate(x, y), TerrainConstant.WALL));
                }
            }
            return GridInitializers.fromList(cells, cancellation::checkCanceled);
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

    private GridInitializer<ReboundingEntity> createRebounderInitializer(
            Random random,
            SimulationInitializationCancellation cancellation) {
        int rebounderCount = computeRebounderCount();
        if (rebounderCount == 0) {
            return GridInitializers.identity();
        }
        List<CompassDirection> directionRing = CellNeighborhoods.directionRing(config().neighborhoodMode(),
                structure.cellShape());
        List<ReboundingEntity> rebounders = new ArrayList<>(rebounderCount);
        for (int i = 0; i < rebounderCount; i++) {
            if (isCancellationCheckpoint(i)) {
                cancellation.checkCanceled();
            }
            rebounders.add(new Rebounder(directionRing.get(random.nextInt(directionRing.size()))));
        }
        return GridInitializers.placeAllAtRandomPositions(
                rebounders,
                ReboundingEntity::isGround,
                random,
                cancellation::checkCanceled);
    }

    private void initializeStatistics(WritableGridModel<ReboundingEntity> model) {
        int wallCellsInitial = Math.toIntExact(model.countEntities(ReboundingEntity::isWall));
        int rebounderCellsInitial = Math.toIntExact(model.countEntities(ReboundingEntity::isRebounder));
        statistics.initializeStartupCellCounts(wallCellsInitial, rebounderCellsInitial);
    }

}

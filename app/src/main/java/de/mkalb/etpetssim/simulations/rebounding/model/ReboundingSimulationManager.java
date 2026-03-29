package de.mkalb.etpetssim.simulations.rebounding.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingConstantEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingEntity;
import de.mkalb.etpetssim.simulations.rebounding.model.entity.ReboundingMovingEntity;

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
        var model = new SparseGridModel<ReboundingEntity>(structure, ReboundingConstantEntity.GROUND);

        // Executor with runner and terminationCondition
        var agentStepLogic = new ReboundingStepLogic(structure, config);
        var runner = new AsynchronousStepRunner<>(model, ReboundingEntity::isMovingEntity, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new ReboundingTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(ReboundingConfig config, WritableGridModel<ReboundingEntity> model, Random random) {
        List<CompassDirection> directionRing;
        if (model.structure().cellShape() == CellShape.SQUARE) {
            directionRing = (config.neighborhoodMode() == NeighborhoodMode.EDGES_AND_VERTICES)
                    ? CellNeighborhoods.SQUARE_EDGES_AND_VERTICES_DIRECTION_RING
                    : CellNeighborhoods.SQUARE_EDGES_DIRECTION_RING;
        } else if (model.structure().cellShape() == CellShape.HEXAGON) {
            directionRing = CellNeighborhoods.HEXAGON_DIRECTION_RING;
        } else {
            throw new IllegalStateException("Unsupported cell shape: " + model.structure().cellShape());
        }
        GridInitializer<ReboundingEntity> movingEntityGridInitializer =
                GridInitializers.fillRandomPercent(
                        () -> new ReboundingMovingEntity(directionRing.get(random.nextInt(directionRing.size()))),
                        config.movingEntityPercent(),
                        ReboundingConstantEntity.GROUND,
                        random);
        movingEntityGridInitializer.initialize(model);
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

    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    private void updateInitialStatistics(WritableGridModel<ReboundingEntity> model) {
        int wallCellsInitial = (int) model.countEntities(ReboundingEntity::isWall);
        int movingEntityCellsInitial = (int) model.countEntities(ReboundingEntity::isMovingEntity);
        statistics.updateInitialCells(wallCellsInitial, movingEntityCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<ReboundingEntity, WritableGridModel<ReboundingEntity>> executor() {
        return executor;
    }

}

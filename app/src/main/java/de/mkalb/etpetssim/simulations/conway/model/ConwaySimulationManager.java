package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

import java.util.*;

public final class ConwaySimulationManager
        extends AbstractTimedSimulationManager<ConwayEntity, ConwayConfig, ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayStatistics statistics;
    private final TimedSimulationExecutor<ConwayEntity> executor;

    public ConwaySimulationManager(ConwayConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new ConwayStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);

        // Executor with runner and terminationCondition
        var runner = new SynchronousStepRunner<>(model, new ConwayUpdateStrategy(structure, config));
        var terminationCondition = new ConwayTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::currentModel, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    @SuppressWarnings("MagicNumber")
    private void initializeGrid(ConwayConfig config, GridModel<ConwayEntity> model, Random random) {
        double alivePercent = config.alivePercent();
        double deadPercent = 1.0d - config.alivePercent();

        GridInitializer<ConwayEntity> gridInitializer;
        if ((alivePercent > 0) && (alivePercent <= 0.75d)) {
            gridInitializer = GridInitializers.placeRandomPercent(
                    () -> ConwayEntity.ALIVE,
                    ConwayEntity::isDead,
                    alivePercent, random);
        } else if (deadPercent < 1.0d) {
            gridInitializer = GridInitializers.constant(ConwayEntity.ALIVE)
                                              .andThen(GridInitializers.placeRandomPercent(
                                                      () -> ConwayEntity.DEAD,
                                                      ConwayEntity::isAlive,
                                                      deadPercent, random));
        } else {
            gridInitializer = GridInitializers.identity();
        }
        gridInitializer.initialize(model);
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public ConwayStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private void updateInitialStatistics(GridModel<ConwayEntity> model) {
        int aliveEntities = (int) model.countEntities(ConwayEntity::isAlive);
        statistics.updateCells(aliveEntities, aliveEntities);
    }

    @Override
    protected TimedSimulationExecutor<ConwayEntity> executor() {
        return executor;
    }

}

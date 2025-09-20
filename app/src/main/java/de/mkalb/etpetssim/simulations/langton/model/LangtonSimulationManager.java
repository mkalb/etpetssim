package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

import java.util.*;

public final class LangtonSimulationManager
        extends AbstractTimedSimulationManager<LangtonEntity, CompositeGridModel<LangtonEntity>, LangtonConfig,
        LangtonStatistics> {

    public static final int MODEL_LAYER_GROUND = 0;
    public static final int MODEL_LAYER_ANT = 1;

    private final GridStructure structure;
    private final LangtonStatistics statistics;
    private final TimedSimulationExecutor<LangtonEntity, CompositeGridModel<LangtonEntity>> executor;

    public LangtonSimulationManager(LangtonConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new LangtonStatistics(structure.cellCount());
        WritableGridModel<LangtonGroundEntity> groundModel = new SparseGridModel<>(structure, LangtonGroundEntity.UNVISITED);
        WritableGridModel<LangtonAntEntity> antModel = new SparseGridModel<>(structure, LangtonAntNone.NONE);
        CompositeGridModel<LangtonEntity> compositeGridModel = new CompositeGridModel<>(List.of(groundModel, antModel));

        // Executor with runner and terminationCondition
        var runner = new LangtonStepRunner(structure, config, groundModel, antModel, compositeGridModel);
        var terminationCondition = new LangtonTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::compositeGridModel, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, groundModel, antModel);

        updateInitialStatistics(config, groundModel, antModel);
    }

    private void initializeGrid(LangtonConfig config, WritableGridModel<LangtonGroundEntity> groundModel, WritableGridModel<LangtonAntEntity> antModel) {
        LangtonAnt ant = new LangtonAnt(CompassDirection.N);
        GridCoordinate coordinate = new GridCoordinate(structure.size().width() / 2, structure.size().height() / 2);
        antModel.setEntity(coordinate, ant);
        groundModel.setEntity(coordinate, LangtonGroundEntity.COLOR_1);
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public LangtonStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    private void updateInitialStatistics(LangtonConfig config, WritableGridModel<LangtonGroundEntity> groundModel, WritableGridModel<LangtonAntEntity> antModel) {
        statistics.updateCells((int) antModel.countEntities(LangtonEntity::isAgent), true);
    }

    @Override
    protected TimedSimulationExecutor<LangtonEntity, CompositeGridModel<LangtonEntity>> executor() {
        return executor;
    }

}

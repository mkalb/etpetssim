package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.langton.model.entity.*;

public final class LangtonSimulationManager
        extends AbstractTimedSimulationManager<LangtonEntity, LangtonGridModel, LangtonConfig,
        LangtonStatistics> {

    private final GridStructure structure;
    private final LangtonStatistics statistics;
    private final TimedSimulationExecutor<LangtonEntity, LangtonGridModel> executor;

    public LangtonSimulationManager(LangtonConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new LangtonStatistics(structure.cellCount());
        var model = new LangtonGridModel(structure,
                new ArrayGridModel<>(structure, TerrainConstant.UNVISITED),
                new SparseGridModel<>(structure, NoAgent.NONE));

        // Executor with runner and terminationCondition
        var runner = new LangtonStepRunner(config, model);
        var terminationCondition = new LangtonTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model);

        updateInitialStatistics(model);
    }

    private void initializeGrid(LangtonConfig config, LangtonGridModel model) {
        Ant ant = new Ant(CompassDirection.N);
        // Start at the middle of teh grid but round down to an even number. This avoids problems with TRIANGLE.
        GridCoordinate coordinate = new GridCoordinate(halveToEven(structure.size().width()), halveToEven(structure.size().height()));
        model.antModel().setEntity(coordinate, ant);
        model.groundModel().setEntity(coordinate, TerrainConstant.COLOR_1);
    }

    int halveToEven(int n) {
        return (n / 2) & ~1;
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

    private void updateInitialStatistics(LangtonGridModel model) {
        int newAnts = Math.toIntExact(model.antModel()
                                           .countEntities(LangtonEntity::isAgent));
        statistics.updateCells(newAnts, newAnts);
    }

    @Override
    protected TimedSimulationExecutor<LangtonEntity, LangtonGridModel> executor() {
        return executor;
    }

}

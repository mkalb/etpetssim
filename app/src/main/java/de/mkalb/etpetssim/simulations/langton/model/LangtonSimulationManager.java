package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.model.AbstractTimedSimulationManager;

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
                new ArrayGridModel<>(structure, LangtonGroundEntity.UNVISITED),
                new SparseGridModel<>(structure, LangtonAntNone.NONE));

        // Executor with runner and terminationCondition
        var runner = new LangtonStepRunner(config, model);
        var terminationCondition = new LangtonTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model);

        updateInitialStatistics(config, model);
    }

    private void initializeGrid(LangtonConfig config, LangtonGridModel model) {
        LangtonAnt ant = new LangtonAnt(CompassDirection.N);
        // Start at the middle of teh grid but round down to an even number. This avoids problems with TRIANGLE.
        GridCoordinate coordinate = new GridCoordinate(halveToEven(structure.size().width()), halveToEven(structure.size().height()));
        model.antModel().setEntity(coordinate, ant);
        model.groundModel().setEntity(coordinate, LangtonGroundEntity.COLOR_1);
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

    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    private void updateInitialStatistics(LangtonConfig config, LangtonGridModel model) {
        int newAnts = (int) model.antModel().countEntities(LangtonEntity::isAgent);
        statistics.updateCells(newAnts, newAnts);
    }

    @Override
    protected TimedSimulationExecutor<LangtonEntity, LangtonGridModel> executor() {
        return executor;
    }

}

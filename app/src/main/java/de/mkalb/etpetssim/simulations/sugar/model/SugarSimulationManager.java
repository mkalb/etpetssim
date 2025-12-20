package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;

import java.util.*;

public final class SugarSimulationManager
        extends AbstractTimedSimulationManager<SugarEntity, SugarGridModel, SugarConfig,
        SugarStatistics> {

    private final GridStructure structure;
    private final SugarStatistics statistics;
    private final TimedSimulationExecutor<SugarEntity, SugarGridModel> executor;

    public SugarSimulationManager(SugarConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new SugarStatistics(structure.cellCount());
        var random = new Random(config.seed());
        var model = new SugarGridModel(structure,
                new SparseGridModel<>(structure, SugarResourceNone.NONE),
                new SparseGridModel<>(structure, SugarAgentNone.NONE));

        // Executor with runner and terminationCondition
        var runner = new SugarStepRunner(config, random, model);
        var terminationCondition = new SugarTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        updateStatistics();

        initializeGrid(config, model, random);

        updateInitialStatistics(model);
    }

    private void initializeGrid(SugarConfig config, SugarGridModel model, Random random) {
        initializeGridSugar(config, model);
        initializeGridAgent(config, model, random);
        //  initializeGridTest(config, model);
    }

    /**
     * Maps a fraction (0.0 .. 1.0) to an integer index in the range [0, size - 1].
     * Rounds with floor when fraction < 0.5, otherwise ceil, and clamps the result.
     *
     * @param size  the dimension size (number of cells)
     * @param percent fraction between 0.0 and 1.0
     * @return clamped index in [0, size - 1]
     */
    @SuppressWarnings({"MagicNumber", "NumericCastThatLosesPrecision"})
    private int percentToClampedIndex(int size, double percent) {
        int count = (int) ((percent < 0.5d) ? Math.floor(size * percent) : Math.ceil(size * percent));
        return Math.max(0, Math.min(count, size - 1));
    }

    @SuppressWarnings("MagicNumber")
    private List<GridCoordinate> computeSugarPeakCoordinates() {
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCoordinate> peakCoordinates = new ArrayList<>();

        if (config().sugarPeaks() > 0) {
            if ((config().sugarPeaks() % 2) == 1) {
                peakCoordinates.add(new GridCoordinate(percentToClampedIndex(width, 0.5d), percentToClampedIndex(height, 0.5d)));
            }
            if (config().sugarPeaks() >= 2) {
                peakCoordinates.add(new GridCoordinate(percentToClampedIndex(width, 0.75d), percentToClampedIndex(height, 0.25d)));
                peakCoordinates.add(new GridCoordinate(percentToClampedIndex(width, 0.25d), percentToClampedIndex(height, 0.75d)));
            }
            if (config().sugarPeaks() >= 4) {
                peakCoordinates.add(new GridCoordinate(percentToClampedIndex(width, 0.25d), percentToClampedIndex(height, 0.25d)));
                peakCoordinates.add(new GridCoordinate(percentToClampedIndex(width, 0.75d), percentToClampedIndex(height, 0.75d)));
            }
        }

        return peakCoordinates;
    }

    private void initializeGridSugar(SugarConfig config, SugarGridModel model) {
        List<GridCoordinate> peakCoordinates = computeSugarPeakCoordinates();

        Map<GridCoordinate, Integer> sugarMap = computeSugarRadiusMap(config, peakCoordinates);
        sugarMap.forEach(((coordinate, amount) -> model.resourceModel().setEntity(coordinate, new SugarResourceSugar(amount, amount))));
    }

    private Map<GridCoordinate, Integer> computeSugarRadiusMap(SugarConfig config, List<GridCoordinate> peakCoordinates) {
        Map<GridCoordinate, Integer> sugarMap = new HashMap<>();
        int minSugarAmount = config().minSugarAmount();
        int radiusLimit = config.sugarRadiusLimit();
        int maxSugarAmount = Math.max(minSugarAmount, config.maxSugarAmount());
        int sugarRange = maxSugarAmount - minSugarAmount;

        Set<GridCoordinate> visited = new HashSet<>();
        Queue<GridCoordinate> queue = new ArrayDeque<>();

        // Initialize peaks first
        for (GridCoordinate peak : peakCoordinates) {
            visited.add(peak);
            queue.add(peak);
            sugarMap.put(peak, maxSugarAmount);
        }

        int radiusLevel = 1;

        while (!queue.isEmpty() && (radiusLevel <= radiusLimit)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                GridCoordinate current = queue.remove();
                int sugarAmount = computeSugarAmount(minSugarAmount, sugarRange, radiusLimit, radiusLevel);
                for (var result : CellNeighborhoods.neighborEdgeResults(current, config.neighborhoodMode(), structure)) {
                    if ((result.action() == EdgeBehaviorAction.VALID) || (result.action() == EdgeBehaviorAction.WRAPPED)) {
                        GridCoordinate mapped = result.mapped();
                        if (!visited.contains(mapped)) {
                            visited.add(mapped);
                            queue.add(mapped);
                            sugarMap.put(mapped, sugarAmount);
                        }
                    }
                }
            }
            radiusLevel++;
        }

        return sugarMap;
    }

    private int computeSugarAmount(int minSugar, int sugarRange, int radiusLimit, int radiusLevel) {
        // linear interpolation: near peaks -> max, am Rand -> min
        if (radiusLimit <= 0) {
            return minSugar;
        }
        return minSugar + ((sugarRange * (radiusLimit - radiusLevel)) / radiusLimit);
    }

    private void initializeGridAgent(SugarConfig config, SugarGridModel model, Random random) {
        int stepIndexOfSpawn = -1;
        GridInitializer<SugarAgentEntity> agentGridInitializer =
                GridInitializers.fillRandomPercent(
                        () -> new SugarAgent(config.agentInitialEnergy(), stepIndexOfSpawn),
                        config.agentPercent(),
                        SugarAgentNone.NONE,
                        random);
        agentGridInitializer.initialize(model.agentModel());
    }

    @SuppressWarnings("MagicNumber")
    private void initializeGridTest(SugarConfig config, SugarGridModel model) {
        int stepIndexOfSpawn = -1;
        for (int i = 1; i < 30; i++) {
            model.agentModel().setEntity(new GridCoordinate(10 + (i * 2), 10), new SugarAgent(i, stepIndexOfSpawn));
        }

        for (int i = 1; i <= config.maxSugarAmount(); i++) {
            model.resourceModel().setEntity(new GridCoordinate(10 + (i * 2), 20), new SugarResourceSugar(config.maxSugarAmount(), i));

            model.resourceModel().setEntity(new GridCoordinate(10 + (i * 2), 30),
                    new SugarResourceSugar(config.maxSugarAmount(), 1));
            model.agentModel().setEntity(new GridCoordinate(10 + (i * 2), 30), new SugarAgent(i, stepIndexOfSpawn));
        }
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public SugarStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @SuppressWarnings({"NumericCastThatLosesPrecision"})
    private void updateInitialStatistics(SugarGridModel model) {
        int resourceCellsInitial = (int) model.resourceModel().countEntities(e -> !e.isNone());
        int agentCellsInitial = (int) model.agentModel().countEntities(e -> !e.isNone());
        statistics.updateInitialCells(resourceCellsInitial, agentCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<SugarEntity, SugarGridModel> executor() {
        return executor;
    }

}

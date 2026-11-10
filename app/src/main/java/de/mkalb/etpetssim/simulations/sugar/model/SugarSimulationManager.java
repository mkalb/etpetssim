package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.DefaultSimulationExecutor;
import de.mkalb.etpetssim.engine.executor.TimedSimulationExecutor;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.support.GridInitializer;
import de.mkalb.etpetssim.engine.support.GridInitializers;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.core.model.SimulationInitializationCancellation;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Agent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.AgentEntity;
import de.mkalb.etpetssim.simulations.sugar.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import de.mkalb.etpetssim.simulations.sugar.model.entity.SugarEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import static de.mkalb.etpetssim.engine.support.WorkCheckpoints.isCancellationCheckpoint;

/**
 * Simulation manager of Sugarscape.
 */
public final class SugarSimulationManager
        extends AbstractTimedSimulationManager<SugarEntity, SugarGridModel, SugarConfig, SugarStatistics> {

    private final GridStructure structure;
    private final SugarStatistics statistics;
    private final TimedSimulationExecutor<SugarEntity, SugarGridModel> executor;

    public SugarSimulationManager(SugarConfig config) {
        this(config, SimulationInitializationCancellation.none());
    }

    public SugarSimulationManager(SugarConfig config, SimulationInitializationCancellation cancellation) {
        super(config, SugarStatistics.metrics());
        cancellation.checkCanceled();

        structure = config.createGridStructure();
        statistics = new SugarStatistics(structure);
        var random = new Random(config.seed());
        var model = new SugarGridModel(structure,
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));

        var runner = new SugarStepRunner(model, config, random);
        var terminationCondition = new SugarTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model,
                terminationCondition, statistics));

        initializeGrid(model, random, cancellation);
        cancellation.checkCanceled();
        initializeStatistics(model);
        recordInitialStatisticsSample();
    }

    /**
     * Maps a fraction ({@code 0.0} to {@code 1.0}) to an integer index in the range {@code [0, size - 1]}.
     * Rounds down when the fraction is below {@code 0.5}, otherwise up, and clamps the result.
     *
     * @param size     the dimension size (number of cells)
     * @param fraction the fraction between {@code 0.0} and {@code 1.0}
     * @return the clamped index in {@code [0, size - 1]}
     */
    @SuppressWarnings("NumericCastThatLosesPrecision")
    private static int computeClampedIndex(int size, double fraction) {
        int count = (int) ((fraction < 0.5d) ? Math.floor(size * fraction) : Math.ceil(size * fraction));
        return Math.clamp(count, 0, size - 1);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    private static int computeSugarAmount(int minSugarAmount,
                                          int sugarRange,
                                          int radiusLimit,
                                          int radiusLevel,
                                          Random random) {
        // linear interpolation: near peaks -> max, at edge -> min
        if (radiusLimit <= 0) {
            return minSugarAmount;
        }
        return (int) Math.floor(minSugarAmount
                + (((double) sugarRange * (radiusLimit - radiusLevel)) / radiusLimit)
                + random.nextDouble(SugarBalance.SUGAR_NOISE_MIN, SugarBalance.SUGAR_NOISE_MAX));
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
        statistics.setStepData(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    @Override
    protected TimedSimulationExecutor<SugarEntity, SugarGridModel> executor() {
        return executor;
    }

    private void initializeGrid(SugarGridModel model,
                                Random random,
                                SimulationInitializationCancellation cancellation) {
        initializeGridSugar(model, random, cancellation);
        initializeGridAgent(model, random, cancellation);
    }

    private List<GridCoordinate> computeSugarPeakCoordinates() {
        int width = structure.size().width();
        int height = structure.size().height();
        List<GridCoordinate> peakCoordinates = new ArrayList<>();

        if (config().sugarPeaks() > 0) {
            if ((config().sugarPeaks() % 2) == 1) {
                peakCoordinates.add(new GridCoordinate(
                        computeClampedIndex(width, SugarBalance.PEAK_POSITION_CENTER),
                        computeClampedIndex(height, SugarBalance.PEAK_POSITION_CENTER)));
            }
            if (config().sugarPeaks() >= 2) {
                peakCoordinates.add(new GridCoordinate(
                        computeClampedIndex(width, SugarBalance.PEAK_POSITION_THREE_QUARTERS),
                        computeClampedIndex(height, SugarBalance.PEAK_POSITION_QUARTER)));
                peakCoordinates.add(new GridCoordinate(
                        computeClampedIndex(width, SugarBalance.PEAK_POSITION_QUARTER),
                        computeClampedIndex(height, SugarBalance.PEAK_POSITION_THREE_QUARTERS)));
            }
            if (config().sugarPeaks() >= 4) {
                peakCoordinates.add(new GridCoordinate(
                        computeClampedIndex(width, SugarBalance.PEAK_POSITION_QUARTER),
                        computeClampedIndex(height, SugarBalance.PEAK_POSITION_QUARTER)));
                peakCoordinates.add(new GridCoordinate(
                        computeClampedIndex(width, SugarBalance.PEAK_POSITION_THREE_QUARTERS),
                        computeClampedIndex(height, SugarBalance.PEAK_POSITION_THREE_QUARTERS)));
            }
        }

        return peakCoordinates;
    }

    private void initializeGridSugar(SugarGridModel model,
                                     Random random,
                                     SimulationInitializationCancellation cancellation) {
        List<GridCoordinate> peakCoordinates = computeSugarPeakCoordinates();

        Map<GridCoordinate, Integer> sugarMap = computeSugarRadiusMap(peakCoordinates, random, cancellation);
        int index = 0;
        for (var entry : sugarMap.entrySet()) {
            if (isCancellationCheckpoint(index)) {
                cancellation.checkCanceled();
            }
            index++;
            model.resourceModel().setEntity(entry.getKey(), new Sugar(entry.getValue(), entry.getValue()));
        }
    }

    private Map<GridCoordinate, Integer> computeSugarRadiusMap(List<GridCoordinate> peakCoordinates,
                                                               Random random,
                                                               SimulationInitializationCancellation cancellation) {
        Map<GridCoordinate, Integer> sugarMap = new HashMap<>();
        int minSugarAmount = config().minSugarAmount();
        int radiusLimit = config().sugarRadiusLimit();
        int maxSugarAmount = config().maxSugarAmount();
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
        int index = 0;

        while (!queue.isEmpty() && (radiusLevel <= radiusLimit)) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                if (isCancellationCheckpoint(index)) {
                    cancellation.checkCanceled();
                }
                index++;
                GridCoordinate current = queue.remove();
                for (var result : CellNeighborhoods.neighborEdgeBehaviorResults(current, config().neighborhoodMode(),
                        structure)) {
                    if (result.action().isWithinGrid()) {
                        GridCoordinate mapped = result.mapped();
                        if (!visited.contains(mapped)) {
                            visited.add(mapped);
                            queue.add(mapped);
                            int sugarAmount = computeSugarAmount(minSugarAmount, sugarRange, radiusLimit, radiusLevel,
                                    random);
                            if ((sugarAmount >= minSugarAmount) && (sugarAmount <= maxSugarAmount)) {
                                sugarMap.put(mapped, sugarAmount);
                            }
                        }
                    }
                }
            }
            radiusLevel++;
        }

        return sugarMap;
    }

    private void initializeGridAgent(SugarGridModel model,
                                     Random random,
                                     SimulationInitializationCancellation cancellation) {
        int stepIndexOfSpawn = -1;
        int agentInitialEnergy = config().agentInitialEnergy();
        GridInitializer<AgentEntity> agentGridInitializer =
                GridInitializers.fillRandomPercent(
                        config().agentPercent(),
                        () -> new Agent(agentInitialEnergy, stepIndexOfSpawn),
                        NoAgent.NO_AGENT,
                        random,
                        cancellation::checkCanceled);
        agentGridInitializer.initialize(model.agentModel());
    }

    private void initializeStatistics(SugarGridModel model) {
        int resourceCellsInitial = Math.toIntExact(model.resourceModel().countEntities(SugarEntity::isNotEmpty));
        int agentCellsInitial = Math.toIntExact(model.agentModel().countEntities(SugarEntity::isNotEmpty));
        statistics.initializeStartupCellCounts(resourceCellsInitial, agentCellsInitial);
    }

}

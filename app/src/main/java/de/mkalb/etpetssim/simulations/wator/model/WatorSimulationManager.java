package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.executor.*;
import de.mkalb.etpetssim.engine.model.*;
import de.mkalb.etpetssim.engine.support.*;
import de.mkalb.etpetssim.simulations.core.model.AbstractTimedSimulationManager;
import de.mkalb.etpetssim.simulations.wator.model.entity.*;

import java.util.*;

public final class WatorSimulationManager
        extends AbstractTimedSimulationManager<WatorEntity, WritableGridModel<WatorEntity>, WatorConfig,
        WatorStatistics> {

    private final GridStructure structure;
    private final WatorStatistics statistics;
    private final TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor;
    private final CreatureFactory creatureFactory;

    public WatorSimulationManager(WatorConfig config) {
        super(config);

        structure = config.createGridStructure();
        statistics = new WatorStatistics(structure);
        var random = new Random(config.seed());
        var model = new ArrayGridModel<WatorEntity>(structure, TerrainConstant.WATER);

        creatureFactory = new CreatureFactory();
        var agentStepLogic = new WatorStepLogic(config, random, creatureFactory);
        var runner = new AsynchronousStepRunner<>(model, WatorEntity::isAgent, AgentOrderingStrategies.byPosition(), agentStepLogic);
        var terminationCondition = new WatorTerminationCondition();
        executor = new TimedSimulationExecutor<>(new DefaultSimulationExecutor<>(runner, runner::model, terminationCondition, statistics));

        initializeGrid(model, random);

        initializeStatistics(model);
    }

    private void initializeGrid(WritableGridModel<WatorEntity> model, Random random) {
        var fishCount = Math.clamp(
                Math.toIntExact(Math.round(config().fishPercent() * structure.cellCount())),
                0, structure.cellCount());
        var fish = new ArrayList<WatorEntity>(fishCount);
        for (int i = 0; i < fishCount; i++) {
            fish.add(createInitialFish(random));
        }

        var sharkCount = Math.clamp(
                Math.toIntExact(Math.round(config().sharkPercent() * structure.cellCount())),
                0, structure.cellCount() - fishCount);
        var sharks = new ArrayList<WatorEntity>(sharkCount);
        for (int i = 0; i < sharkCount; i++) {
            sharks.add(createInitialShark(random));
        }

        GridInitializers.placeAllAtRandomPositions(fish, WatorEntity::isWater, random).initialize(model);
        GridInitializers.placeAllAtRandomPositions(sharks, WatorEntity::isWater, random).initialize(model);
    }

    public Fish createFish(int stepIndexOfBirth) {
        return creatureFactory.createFish(stepIndexOfBirth);
    }

    private Fish createInitialFish(Random random) {
        int stepIndexOfBirth = -1 - random.nextInt(config().fishMaxAge()); // negative age for birth time
        return createFish(stepIndexOfBirth);
    }

    public Shark createShark(int stepIndexOfBirth) {
        return creatureFactory.createShark(stepIndexOfBirth, config().sharkBirthEnergy());
    }

    private Shark createInitialShark(Random random) {
        int stepIndexOfBirth = -1 - random.nextInt(config().sharkMaxAge()); // negative age for birth time
        return createShark(stepIndexOfBirth);
    }

    @Override
    public GridStructure structure() {
        return structure;
    }

    @Override
    public WatorStatistics statistics() {
        return statistics;
    }

    @Override
    protected void updateStatistics() {
        statistics.updateMinMaxCells();
        statistics.update(
                executor.stepCount(),
                executor.stepTimingStatistics());
    }

    private void initializeStatistics(ReadableGridModel<WatorEntity> model) {
        int fishCellsInitial = Math.toIntExact(model.countEntities(WatorEntity::isFish));
        int sharkCellsInitial = Math.toIntExact(model.countEntities(WatorEntity::isShark));
        statistics.initializeStartupCellCounts(fishCellsInitial, sharkCellsInitial);
    }

    @Override
    protected TimedSimulationExecutor<WatorEntity, WritableGridModel<WatorEntity>> executor() {
        return executor;
    }

}

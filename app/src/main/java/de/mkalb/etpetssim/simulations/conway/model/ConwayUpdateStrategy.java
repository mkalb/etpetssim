package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;

public final class ConwayUpdateStrategy implements SynchronousStepLogic<ConwayEntity, ConwayStatistics> {

    private final GridStructure structure;
    private final ConwayConfig config;

    public ConwayUpdateStrategy(GridStructure structure, ConwayConfig config) {
        this.structure = structure;
        this.config = config;
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    @Override
    public void performSynchronousStep(ReadableGridModel<ConwayEntity> currentModel,
                                       GridModel<ConwayEntity> nextModel,
                                       int stepIndex,
                                       ConwayStatistics statistics) {
        ConwayTransitionRules transitionRules = config.transitionRules();

        // TODO check if updating ConwayStatistics would be helpful here

        structure.coordinatesStream().forEach(coordinate -> {
            boolean isAlive = currentModel.getEntity(coordinate).isAlive();
            // TODO validate, if a loop is faster than stream
            int aliveNeighbors = (int) CellNeighborhoods.neighborEdgeResults(coordinate, config.neighborhoodMode(), structure)
                                                        .filter(result ->
                                                                (result.action() == EdgeBehaviorAction.VALID)
                                                                        || (result.action() == EdgeBehaviorAction.WRAPPED))
                                                        .filter(result -> structure.isCoordinateValid(result.mapped()))
                                                        .filter(n -> currentModel.getEntity(n.mapped()).isAlive())
                                                        .count();

            if (isAlive ? transitionRules.shouldSurvive(aliveNeighbors)
                    : transitionRules.shouldBeBorn(aliveNeighbors)) {
                nextModel.setEntity(coordinate, ConwayEntity.ALIVE);
            }
        });
    }

}
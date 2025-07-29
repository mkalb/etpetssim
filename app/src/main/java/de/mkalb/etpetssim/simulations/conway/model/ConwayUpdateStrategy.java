package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;
import de.mkalb.etpetssim.engine.model.SynchronousStepLogic;
import de.mkalb.etpetssim.engine.neighborhood.CellNeighborhoods;
import de.mkalb.etpetssim.engine.neighborhood.EdgeBehaviorAction;
import de.mkalb.etpetssim.engine.neighborhood.NeighborhoodMode;

public final class ConwayUpdateStrategy implements SynchronousStepLogic<ConwayEntity, ConwayStatistics> {

    private final GridStructure structure;

    public ConwayUpdateStrategy(GridStructure structure) {
        this.structure = structure;
    }

    @Override
    public void performSynchronousStep(ReadableGridModel<ConwayEntity> currentModel,
                                       GridModel<ConwayEntity> nextModel,
                                       long currentStep,
                                       ConwayStatistics statistics) {
        // TODO Use ConwayStatistics
        structure.coordinatesStream().forEach(coordinate -> {
            boolean isAlive = currentModel.getEntity(coordinate).isAlive();
            long aliveNeighbors = CellNeighborhoods.neighborEdgeResults(coordinate, NeighborhoodMode.EDGES_AND_VERTICES, structure)
                                                   .filter(result ->
                                                           (result.action() == EdgeBehaviorAction.VALID)
                                                                   || (result.action() == EdgeBehaviorAction.WRAPPED))
                                                   .filter(result -> structure.isCoordinateValid(result.mapped()))
                                                   .filter(n -> currentModel.getEntity(n.mapped()).isAlive())
                                                   .count();

            // Only set ALIVE cells; DEAD is already the default
            if (structure.cellShape() == CellShape.HEXAGON) {
                if (isAlive ? ((aliveNeighbors == 2) || (aliveNeighbors == 3)) :
                        ((aliveNeighbors == 3) || (aliveNeighbors == 4))) {
                    nextModel.setEntity(coordinate, ConwayEntity.ALIVE); // 2/3 udn 3/4
                }
            } else if (structure.cellShape() == CellShape.TRIANGLE) {
                if (isAlive ? ((aliveNeighbors == 4) || (aliveNeighbors == 5)) :
                        ((aliveNeighbors == 4) || (aliveNeighbors == 5) || (aliveNeighbors == 6))) {
                    nextModel.setEntity(coordinate, ConwayEntity.ALIVE); // 4/5 und 4/5/6
                }
            } else {
                if (isAlive ? ((aliveNeighbors == 2) || (aliveNeighbors == 3)) : (aliveNeighbors == 3)) {
                    nextModel.setEntity(coordinate, ConwayEntity.ALIVE);
                }
            }
        });
    }

}
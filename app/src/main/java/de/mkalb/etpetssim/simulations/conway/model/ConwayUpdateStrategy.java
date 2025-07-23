package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;

import java.util.function.*;

public final class ConwayUpdateStrategy implements BiConsumer<ReadableGridModel<ConwayEntity>,
        GridModel<ConwayEntity>> {

    private final GridStructure structure;

    public ConwayUpdateStrategy(GridStructure structure) {
        this.structure = structure;
    }

    @Override
    public void accept(ReadableGridModel<ConwayEntity> currentModel, GridModel<ConwayEntity> nextModel) {
        structure.coordinatesStream().forEach(coordinate -> {
            boolean isAlive = currentModel.getEntity(coordinate).isAlive();
            long aliveNeighbors = GridArrangement.validCellNeighborsStream(
                                                         coordinate, NeighborhoodMode.EDGES_AND_VERTICES, structure)
                                                 .filter(n -> currentModel.getEntity(n.neighborCoordinate()).isAlive())
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
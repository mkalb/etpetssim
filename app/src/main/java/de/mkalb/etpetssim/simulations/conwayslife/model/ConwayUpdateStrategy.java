package de.mkalb.etpetssim.simulations.conwayslife.model;

import de.mkalb.etpetssim.engine.GridArrangement;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.NeighborhoodMode;
import de.mkalb.etpetssim.engine.model.GridModel;
import de.mkalb.etpetssim.engine.model.ReadableGridModel;

import java.util.function.*;

public class ConwayUpdateStrategy implements BiConsumer<ReadableGridModel<ConwayEntity>, GridModel<ConwayEntity>> {

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
            if (isAlive ? ((aliveNeighbors == 2) || (aliveNeighbors == 3)) : (aliveNeighbors == 3)) {
                nextModel.setEntity(coordinate, ConwayEntity.ALIVE);
            }
        });
    }

}
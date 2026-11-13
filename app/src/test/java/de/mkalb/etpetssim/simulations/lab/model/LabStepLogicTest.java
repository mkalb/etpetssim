package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class LabStepLogicTest {

    @Test
    void testPerformSynchronousStep() {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.WRAP_XY),
                new GridSize(8, 8));
        var currentGridModel = new SparseGridModel<>(structure, LabEntity.NORMAL);
        var nextGridModel = new SparseGridModel<>(structure, LabEntity.NORMAL);
        var statistics = new LabStatistics(structure);
        currentGridModel.setEntity(new GridCoordinate(0, 0), LabEntity.HIGHLIGHTED);
        currentGridModel.setEntity(new GridCoordinate(2, 3), LabEntity.HIGHLIGHTED);

        new LabStepLogic().performSynchronousStep(currentGridModel, nextGridModel, 0, statistics);

        assertAll(
                () -> assertEquals(Set.copyOf(currentGridModel.nonDefaultCells()),
                        Set.copyOf(nextGridModel.nonDefaultCells())),
                () -> assertEquals(2, statistics.highlightedCells()),
                () -> assertEquals(statistics.totalCells() - 2, statistics.normalCells())
        );
    }

}

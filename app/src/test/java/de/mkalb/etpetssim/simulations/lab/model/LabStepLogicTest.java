package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class LabStepLogicTest {

    @Test
    void testPerformSynchronousStep() {
        GridStructure structure = new LabConfig(
                LabConstraints.CELL_SHAPE_DEFAULT,
                LabConstraints.GRID_EDGE_BEHAVIOR_DEFAULT,
                LabConstraints.GRID_WIDTH_DEFAULT,
                LabConstraints.GRID_HEIGHT_DEFAULT,
                LabConstraints.CELL_EDGE_LENGTH_DEFAULT,
                LabConstraints.CELL_DISPLAY_MODE_DEFAULT,
                LabConstraints.COLOR_MODE_DEFAULT,
                1L,
                LabConstraints.HIGHLIGHTED_PERCENT_DEFAULT,
                LabConstraints.NEIGHBORHOOD_MODE_DEFAULT
        ).createGridStructure();
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

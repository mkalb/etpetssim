package de.mkalb.etpetssim.simulations.lab.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.lab.model.entity.LabEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class LabTerminationConditionTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void testIsFinished(int stepCount) {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
        var model = new SparseGridModel<>(structure, LabEntity.NORMAL);

        assertTrue(new LabTerminationCondition().isFinished(model, stepCount, new LabStatistics(structure)));
    }

}

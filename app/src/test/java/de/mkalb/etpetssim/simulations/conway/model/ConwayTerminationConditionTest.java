package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ConwayTerminationConditionTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "0, 3, true",
            "3, 0, true",
            "3, 1, false"
    })
    void testIsFinished(int aliveCells, int changedCells, boolean expected) {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
        var model = new SparseGridModel<>(structure, ConwayEntity.DEAD);
        var statistics = new ConwayStatistics(structure);
        statistics.setCellCounts(aliveCells, changedCells);

        assertEquals(expected, new ConwayTerminationCondition().isFinished(model, 1, statistics));
    }

}

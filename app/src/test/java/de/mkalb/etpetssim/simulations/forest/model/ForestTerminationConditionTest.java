package de.mkalb.etpetssim.simulations.forest.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.simulations.forest.model.entity.ForestEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ForestTerminationConditionTest {

    @ParameterizedTest
    @CsvSource({
            "0, 0, true",
            "1, 0, false",
            "0, 1, false",
            "1, 1, false"
    })
    void testIsFinished(int treeCells, int burningCells, boolean expected) {
        GridStructure structure = new GridStructure(new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                new GridSize(GridSize.MIN_SIZE, GridSize.MIN_SIZE));
        var model = new ArrayGridModel<>(structure, ForestEntity.EMPTY);
        var statistics = new ForestStatistics(structure);
        statistics.setCellCounts(treeCells, burningCells);

        assertEquals(expected, new ForestTerminationCondition().isFinished(model, 1, statistics));
    }

}

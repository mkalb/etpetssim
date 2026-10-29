package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.ArrayGridModel;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;
import de.mkalb.etpetssim.simulations.langton.model.entity.Ant;
import de.mkalb.etpetssim.simulations.langton.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.langton.model.entity.TerrainConstant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class LangtonCellTest {

    private static final GridCoordinate COORDINATE = new GridCoordinate(1, 2);

    private static LangtonGridModel createModel() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                GridSize.square(GridSize.MIN_SIZE));
        return new LangtonGridModel(structure,
                new ArrayGridModel<>(structure, TerrainConstant.UNVISITED),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));
    }

    @Test
    void testFrom() {
        LangtonGridModel model = createModel();
        Ant ant = new Ant(CompassDirection.N);
        model.terrainModel().setEntity(COORDINATE, TerrainConstant.COLOR_1);
        model.antModel().setEntity(COORDINATE, ant);

        LangtonCell cell = LangtonCell.from(model, COORDINATE);

        assertAll(
                () -> assertEquals(COORDINATE, cell.coordinate()),
                () -> assertSame(TerrainConstant.COLOR_1, cell.terrainEntity()),
                () -> assertSame(ant, cell.antEntity())
        );
    }

    @Test
    void testEntity() {
        Ant ant = new Ant(CompassDirection.N);

        assertAll(
                () -> assertSame(ant, new LangtonCell(COORDINATE, TerrainConstant.COLOR_1, ant).entity()),
                () -> assertSame(TerrainConstant.COLOR_1,
                        new LangtonCell(COORDINATE, TerrainConstant.COLOR_1, NoAgent.NO_AGENT).entity())
        );
    }

    @Test
    void testToDisplayString() {
        LangtonCell cell = new LangtonCell(COORDINATE, TerrainConstant.COLOR_1, new Ant(CompassDirection.N));

        assertEquals("(1, 2) [COLOR_1] [ANT " + CompassDirection.N.arrow() + "]", cell.toDisplayString());
    }

}

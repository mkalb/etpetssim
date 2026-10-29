package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.GridEdgeBehavior;
import de.mkalb.etpetssim.engine.GridSize;
import de.mkalb.etpetssim.engine.GridStructure;
import de.mkalb.etpetssim.engine.GridTopology;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Agent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.NoAgent;
import de.mkalb.etpetssim.simulations.sugar.model.entity.NoResource;
import de.mkalb.etpetssim.simulations.sugar.model.entity.Sugar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class SugarCellTest {

    private static final GridCoordinate COORDINATE = new GridCoordinate(1, 2);

    private static SugarGridModel createModel() {
        GridStructure structure = new GridStructure(
                new GridTopology(CellShape.SQUARE, GridEdgeBehavior.BLOCK_XY),
                GridSize.square(GridSize.MIN_SIZE));
        return new SugarGridModel(structure,
                new SparseGridModel<>(structure, NoResource.NO_RESOURCE),
                new SparseGridModel<>(structure, NoAgent.NO_AGENT));
    }

    @Test
    void testFrom() {
        SugarGridModel model = createModel();
        Sugar sugar = new Sugar(4, 3);
        Agent agent = new Agent(10, 2);
        model.resourceModel().setEntity(COORDINATE, sugar);
        model.agentModel().setEntity(COORDINATE, agent);

        SugarCell cell = SugarCell.from(model, COORDINATE);

        assertAll(
                () -> assertEquals(COORDINATE, cell.coordinate()),
                () -> assertSame(sugar, cell.resourceEntity()),
                () -> assertSame(agent, cell.agentEntity())
        );
    }

    @Test
    void testEntity() {
        Sugar sugar = new Sugar(4, 3);
        Agent agent = new Agent(10, 2);

        assertAll(
                () -> assertSame(agent, new SugarCell(COORDINATE, sugar, agent).entity()),
                () -> assertSame(sugar, new SugarCell(COORDINATE, sugar, NoAgent.NO_AGENT).entity()),
                () -> assertSame(NoResource.NO_RESOURCE,
                        new SugarCell(COORDINATE, NoResource.NO_RESOURCE, NoAgent.NO_AGENT).entity())
        );
    }

    @Test
    void testToDisplayString() {
        SugarCell cell = new SugarCell(COORDINATE, new Sugar(4, 3), new Agent(10, 2));

        assertEquals("(1, 2) [SUGAR A=3/4] [AGENT *2 E=10]", cell.toDisplayString());
    }

}

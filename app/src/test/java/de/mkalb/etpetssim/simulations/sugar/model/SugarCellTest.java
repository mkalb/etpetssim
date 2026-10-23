package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.*;
import de.mkalb.etpetssim.engine.model.SparseGridModel;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        assertEquals("(1, 2) [SUGAR 3/4] [AGENT *2 E=10]", cell.toDisplayString());
    }

}

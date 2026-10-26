package de.mkalb.etpetssim.simulations.sugar.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.simulations.sugar.model.entity.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static de.mkalb.etpetssim.simulations.sugar.model.SugarLogicTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

final class SugarAgentLogicTest {

    private static final GridCoordinate AGENT_COORDINATE = new GridCoordinate(3, 3);
    private static final int STEP_INDEX = 5;

    private static SugarStatistics apply(SugarGridModel model, SugarConfig config) {
        SugarStatistics statistics = new SugarStatistics(model.structure());
        statistics.adjustAgentCells(model.agentModel().nonDefaultCells().size());
        SugarAgentLogic.apply(model, config, new Random(1L), STEP_INDEX, statistics);
        return statistics;
    }

    @Test
    void testApplyMovesAgentOneCellTowardMostSugar() {
        SugarConfig config = createConfig(2, SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT);
        SugarGridModel model = createGridModel(config);
        Agent agent = new Agent(INITIAL_ENERGY, 0);
        model.agentModel().setEntity(AGENT_COORDINATE, agent);
        model.resourceModel().setEntity(new GridCoordinate(4, 3), new Sugar(10, 3));
        model.resourceModel().setEntity(new GridCoordinate(3, 1), new Sugar(10, 8));

        apply(model, config);

        assertAll(
                () -> assertSame(agent, model.agentModel().getEntity(new GridCoordinate(3, 2))),
                () -> assertTrue(model.agentModel().isDefaultEntity(AGENT_COORDINATE)),
                () -> assertEquals(INITIAL_ENERGY - METABOLISM_RATE, agent.currentEnergy())
        );
    }

    @Test
    void testApplyHarvestsSugarOfTargetCell() {
        SugarConfig config = createConfig(1, SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT);
        SugarGridModel model = createGridModel(config);
        Agent agent = new Agent(INITIAL_ENERGY, 0);
        model.agentModel().setEntity(AGENT_COORDINATE, agent);
        GridCoordinate sugarCoordinate = new GridCoordinate(4, 3);
        Sugar sugar = new Sugar(10, 5);
        model.resourceModel().setEntity(sugarCoordinate, sugar);

        apply(model, config);

        assertAll(
                () -> assertSame(agent, model.agentModel().getEntity(sugarCoordinate)),
                () -> assertEquals((INITIAL_ENERGY + 5) - METABOLISM_RATE, agent.currentEnergy()),
                () -> assertEquals(0, sugar.currentAmount())
        );
    }

    @Test
    void testApplyReplacesAgentWithoutEnergy() {
        SugarConfig config = createConfig(1, SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT);
        SugarGridModel model = createGridModel(config);
        Agent agent = new Agent(METABOLISM_RATE, 0);
        model.agentModel().setEntity(AGENT_COORDINATE, agent);

        SugarStatistics statistics = apply(model, config);

        List<GridCell<AgentEntity>> agentCells = model.agentModel().nonDefaultCells();
        assertAll(
                () -> assertEquals(1, agentCells.size()),
                () -> assertNotSame(agent, agentCells.getFirst().entity()),
                () -> assertEquals(INITIAL_ENERGY, ((Agent) agentCells.getFirst().entity()).currentEnergy()),
                () -> assertEquals(1, statistics.agentCells())
        );
    }

    @Test
    void testApplyReplacesAgentAtMaxAge() {
        SugarConfig config = createConfig(1, SugarConstraints.SUGAR_REGENERATION_RATE_DEFAULT);
        SugarGridModel model = createGridModel(config);
        Agent agent = new Agent(INITIAL_ENERGY * 10, STEP_INDEX - MAX_AGE);
        model.agentModel().setEntity(AGENT_COORDINATE, agent);

        SugarStatistics statistics = apply(model, config);

        List<GridCell<AgentEntity>> agentCells = model.agentModel().nonDefaultCells();
        assertAll(
                () -> assertEquals(1, agentCells.size()),
                () -> assertNotSame(agent, agentCells.getFirst().entity()),
                () -> assertEquals(1, statistics.agentCells())
        );
    }

}

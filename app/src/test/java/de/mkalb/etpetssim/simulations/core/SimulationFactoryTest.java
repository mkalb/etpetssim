package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.etpets.view.EtpetsMainView;
import de.mkalb.etpetssim.simulations.forest.view.ForestMainView;
import de.mkalb.etpetssim.simulations.lab.view.LabMainView;
import de.mkalb.etpetssim.simulations.langton.view.LangtonMainView;
import de.mkalb.etpetssim.simulations.rebounding.view.ReboundingMainView;
import de.mkalb.etpetssim.simulations.snake.view.SnakeMainView;
import de.mkalb.etpetssim.simulations.sugar.view.SugarMainView;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import de.mkalb.etpetssim.testsupport.FXTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@Execution(ExecutionMode.SAME_THREAD)
final class SimulationFactoryTest {

    private static final Map<SimulationType, Class<?>> EXPECTED_MAIN_VIEW_TYPES = Map.ofEntries(
            Map.entry(SimulationType.ET_PETS, EtpetsMainView.class),
            Map.entry(SimulationType.WATOR, WatorMainView.class),
            Map.entry(SimulationType.CONWAYS_LIFE, ConwayMainView.class),
            Map.entry(SimulationType.LANGTONS_ANT, LangtonMainView.class),
            Map.entry(SimulationType.FOREST_FIRE, ForestMainView.class),
            Map.entry(SimulationType.SUGARSCAPE, SugarMainView.class),
            Map.entry(SimulationType.SNAKE, SnakeMainView.class),
            Map.entry(SimulationType.REBOUNDING_ENTITIES, ReboundingMainView.class),
            Map.entry(SimulationType.SIMULATION_LAB, LabMainView.class));

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
        FXTestSupport.ensureStarted();
    }

    private static SimulationScreen createScreen(SimulationType type) {
        return FXTestSupport.supplyAndWaitNonNull(() -> SimulationFactory.createScreen(type));
    }

    @Test
    void testCreateScreen() {
        assertEquals(EnumSet.allOf(SimulationType.class), EXPECTED_MAIN_VIEW_TYPES.keySet());

        for (SimulationType type : SimulationType.values()) {
            SimulationScreen screen = createScreen(type);
            assertAll(
                    type.name(),
                    () -> assertEquals(type, screen.simulationType()),
                    () -> assertInstanceOf(EXPECTED_MAIN_VIEW_TYPES.get(type), screen.simulationMainView())
            );
        }
    }

}

package de.mkalb.etpetssim;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
final class SimulationTypeTest {

    @Test
    void testEnumValues() {
        assertNotNull(SimulationType.valueOf("STARTSCREEN"));
        assertNotNull(SimulationType.valueOf("ET_PETS"));
        assertNotNull(SimulationType.valueOf("WATOR"));
        assertNotNull(SimulationType.valueOf("CONWAYS_LIFE"));
        assertNotNull(SimulationType.valueOf("LANGTONS_ANT"));
        assertNotNull(SimulationType.valueOf("FOREST_FIRE"));
        assertNotNull(SimulationType.valueOf("SUGARSCAPE"));
        assertNotNull(SimulationType.valueOf("SNAKE"));
        assertNotNull(SimulationType.valueOf("REBOUNDING_ENTITIES"));
        assertNotNull(SimulationType.valueOf("SIMULATION_LAB"));
    }

    @Test
    void testEnumCount() {
        assertEquals(10, SimulationType.values().length, "There should be exactly 10 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new SimulationType[]{
                        SimulationType.STARTSCREEN,
                        SimulationType.ET_PETS,
                        SimulationType.WATOR,
                        SimulationType.CONWAYS_LIFE,
                        SimulationType.LANGTONS_ANT,
                        SimulationType.FOREST_FIRE,
                        SimulationType.SUGARSCAPE,
                        SimulationType.SNAKE,
                        SimulationType.REBOUNDING_ENTITIES,
                        SimulationType.SIMULATION_LAB
                },
                SimulationType.values()
        );
    }

    @Test
    void testValueOfInvalidThrows() {
        assertThrows(IllegalArgumentException.class, () -> SimulationType.valueOf("INVALID"));
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("simulationtype.label", SimulationType.labelResourceKey());
    }

    @Test
    void testIsImplemented() {
        assertTrue(SimulationType.STARTSCREEN.isImplemented());
        assertTrue(SimulationType.ET_PETS.isImplemented());
        assertTrue(SimulationType.WATOR.isImplemented());
        assertTrue(SimulationType.CONWAYS_LIFE.isImplemented());
        assertTrue(SimulationType.LANGTONS_ANT.isImplemented());
        assertTrue(SimulationType.FOREST_FIRE.isImplemented());
        assertTrue(SimulationType.SUGARSCAPE.isImplemented());
        assertTrue(SimulationType.SNAKE.isImplemented());
        assertTrue(SimulationType.REBOUNDING_ENTITIES.isImplemented());
        assertTrue(SimulationType.SIMULATION_LAB.isImplemented());
    }

    @Test
    void testIsShownOnStartScreen() {
        assertFalse(SimulationType.STARTSCREEN.isShownOnStartScreen());
        assertTrue(SimulationType.ET_PETS.isShownOnStartScreen());
        assertTrue(SimulationType.WATOR.isShownOnStartScreen());
        assertTrue(SimulationType.CONWAYS_LIFE.isShownOnStartScreen());
        assertTrue(SimulationType.LANGTONS_ANT.isShownOnStartScreen());
        assertTrue(SimulationType.FOREST_FIRE.isShownOnStartScreen());
        assertTrue(SimulationType.SUGARSCAPE.isShownOnStartScreen());
        assertTrue(SimulationType.SNAKE.isShownOnStartScreen());
        assertTrue(SimulationType.REBOUNDING_ENTITIES.isShownOnStartScreen());
        assertTrue(SimulationType.SIMULATION_LAB.isShownOnStartScreen());
    }

    @Test
    void testTitleKey() {
        assertEquals("simulation.start.title", SimulationType.STARTSCREEN.titleKey());
        assertEquals("simulation.etpets.title", SimulationType.ET_PETS.titleKey());
        assertEquals("simulation.wator.title", SimulationType.WATOR.titleKey());
        assertEquals("simulation.conway.title", SimulationType.CONWAYS_LIFE.titleKey());
        assertEquals("simulation.langton.title", SimulationType.LANGTONS_ANT.titleKey());
        assertEquals("simulation.forest.title", SimulationType.FOREST_FIRE.titleKey());
        assertEquals("simulation.sugar.title", SimulationType.SUGARSCAPE.titleKey());
        assertEquals("simulation.snake.title", SimulationType.SNAKE.titleKey());
        assertEquals("simulation.rebounding.title", SimulationType.REBOUNDING_ENTITIES.titleKey());
        assertEquals("simulation.lab.title", SimulationType.SIMULATION_LAB.titleKey());
    }

    @Test
    void testSubtitleKey() {
        assertEquals("simulation.start.subtitle", SimulationType.STARTSCREEN.subtitleKey());
        assertEquals("simulation.etpets.subtitle", SimulationType.ET_PETS.subtitleKey());
        assertEquals("simulation.wator.subtitle", SimulationType.WATOR.subtitleKey());
        assertEquals("simulation.conway.subtitle", SimulationType.CONWAYS_LIFE.subtitleKey());
        assertEquals("simulation.langton.subtitle", SimulationType.LANGTONS_ANT.subtitleKey());
        assertEquals("simulation.forest.subtitle", SimulationType.FOREST_FIRE.subtitleKey());
        assertEquals("simulation.sugar.subtitle", SimulationType.SUGARSCAPE.subtitleKey());
        assertEquals("simulation.snake.subtitle", SimulationType.SNAKE.subtitleKey());
        assertEquals("simulation.rebounding.subtitle", SimulationType.REBOUNDING_ENTITIES.subtitleKey());
        assertEquals("simulation.lab.subtitle", SimulationType.SIMULATION_LAB.subtitleKey());
    }

    @Test
    void testUrlKey() {
        assertEquals("simulation.start.url", SimulationType.STARTSCREEN.urlKey());
        assertEquals("simulation.etpets.url", SimulationType.ET_PETS.urlKey());
        assertEquals("simulation.wator.url", SimulationType.WATOR.urlKey());
        assertEquals("simulation.conway.url", SimulationType.CONWAYS_LIFE.urlKey());
        assertEquals("simulation.langton.url", SimulationType.LANGTONS_ANT.urlKey());
        assertEquals("simulation.forest.url", SimulationType.FOREST_FIRE.urlKey());
        assertEquals("simulation.sugar.url", SimulationType.SUGARSCAPE.urlKey());
        assertEquals("simulation.snake.url", SimulationType.SNAKE.urlKey());
        assertEquals("simulation.rebounding.url", SimulationType.REBOUNDING_ENTITIES.urlKey());
        assertEquals("simulation.lab.url", SimulationType.SIMULATION_LAB.urlKey());
    }

    @Test
    void testCssPath() {
        assertEquals("", SimulationType.STARTSCREEN.cssPath());
        assertEquals("etpets.css", SimulationType.ET_PETS.cssPath());
        assertEquals("", SimulationType.WATOR.cssPath());
        assertEquals("conway.css", SimulationType.CONWAYS_LIFE.cssPath());
        assertEquals("", SimulationType.LANGTONS_ANT.cssPath());
        assertEquals("", SimulationType.FOREST_FIRE.cssPath());
        assertEquals("", SimulationType.SUGARSCAPE.cssPath());
        assertEquals("", SimulationType.SNAKE.cssPath());
        assertEquals("", SimulationType.REBOUNDING_ENTITIES.cssPath());
        assertEquals("lab.css", SimulationType.SIMULATION_LAB.cssPath());
    }

    @Test
    void testCliArguments() {
        assertEquals(List.of("startscreen", "start"), SimulationType.STARTSCREEN.cliArguments());
        assertEquals(List.of("etpets"), SimulationType.ET_PETS.cliArguments());
        assertEquals(List.of("wator", "wa-tor"), SimulationType.WATOR.cliArguments());
        assertEquals(List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol"), SimulationType.CONWAYS_LIFE.cliArguments());
        assertEquals(List.of("langton", "langtonsant", "langtons-ant", "ant"), SimulationType.LANGTONS_ANT.cliArguments());
        assertEquals(List.of("forestfire", "forest-fire", "forest", "fire", "forestfiremodel"), SimulationType.FOREST_FIRE.cliArguments());
        assertEquals(List.of("sugarscape", "sugar"), SimulationType.SUGARSCAPE.cliArguments());
        assertEquals(List.of("snake", "snakes"), SimulationType.SNAKE.cliArguments());
        assertEquals(List.of("reboundingentities", "rebounding-entities", "rebounding-entity", "rebounding", "rebound", "rebounders"), SimulationType.REBOUNDING_ENTITIES.cliArguments());
        assertEquals(List.of("simulationlab", "lab"), SimulationType.SIMULATION_LAB.cliArguments());
    }

    @Test
    void testFromCliArgument() {
        assertEquals(Optional.of(SimulationType.STARTSCREEN), SimulationType.fromCliArgument("start", false));
        assertEquals(Optional.of(SimulationType.STARTSCREEN), SimulationType.fromCliArgument("START", false));
        assertEquals(Optional.of(SimulationType.ET_PETS), SimulationType.fromCliArgument("etpets", true));
        assertEquals(Optional.of(SimulationType.WATOR), SimulationType.fromCliArgument("wator", true));
        assertEquals(Optional.of(SimulationType.WATOR), SimulationType.fromCliArgument("WA-TOR", true));
        assertEquals(Optional.of(SimulationType.CONWAYS_LIFE), SimulationType.fromCliArgument("conway", true));
        assertEquals(Optional.of(SimulationType.SIMULATION_LAB), SimulationType.fromCliArgument("lab", false));

        assertEquals(Optional.empty(), SimulationType.fromCliArgument("notfound", false));
    }

}

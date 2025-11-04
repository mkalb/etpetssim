package de.mkalb.etpetssim;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
final class SimulationTypeTest {

    @Test
    void testEnumValues() {
        assertNotNull(SimulationType.valueOf("STARTSCREEN"));
        assertNotNull(SimulationType.valueOf("SIMULATION_LAB"));
        assertNotNull(SimulationType.valueOf("ET_PETS_SIM"));
        assertNotNull(SimulationType.valueOf("WATOR"));
        assertNotNull(SimulationType.valueOf("CONWAYS_LIFE"));
        assertNotNull(SimulationType.valueOf("SUGARSCAPE"));
        assertNotNull(SimulationType.valueOf("SNAKE"));
        assertNotNull(SimulationType.valueOf("FOREST_FIRE"));
        assertNotNull(SimulationType.valueOf("LANGTONS_ANT"));
    }

    @Test
    void testEnumCount() {
        assertEquals(9, SimulationType.values().length, "There should be exactly 9 values");
    }

    @Test
    void testEnumOrdinal() {
        assertEquals(0, SimulationType.STARTSCREEN.ordinal());
        assertEquals(1, SimulationType.WATOR.ordinal());
        assertEquals(2, SimulationType.CONWAYS_LIFE.ordinal());
        assertEquals(3, SimulationType.LANGTONS_ANT.ordinal());
        assertEquals(4, SimulationType.ET_PETS_SIM.ordinal());
        assertEquals(5, SimulationType.SUGARSCAPE.ordinal());
        assertEquals(6, SimulationType.SNAKE.ordinal());
        assertEquals(7, SimulationType.FOREST_FIRE.ordinal());
        assertEquals(8, SimulationType.SIMULATION_LAB.ordinal());
    }

    @Test
    void testStaticLabelResourceKey() {
        assertEquals("simulationtype.label", SimulationType.labelResourceKey());
    }

    @Test
    void testIsImplemented() {
        assertTrue(SimulationType.STARTSCREEN.isImplemented());
        assertTrue(SimulationType.SIMULATION_LAB.isImplemented());
        // Only test mandatory simulations.
    }

    @Test
    void testIsShownOnStartScreen() {
        assertFalse(SimulationType.STARTSCREEN.isShownOnStartScreen());
        assertTrue(SimulationType.SIMULATION_LAB.isShownOnStartScreen());
        // Only test mandatory simulations.
    }

    @Test
    void testTitleKey() {
        assertEquals("simulation.start.title", SimulationType.STARTSCREEN.titleKey());
        assertEquals("simulation.lab.title", SimulationType.SIMULATION_LAB.titleKey());
        assertEquals("simulation.etpetssim.title", SimulationType.ET_PETS_SIM.titleKey());
        assertEquals("simulation.wator.title", SimulationType.WATOR.titleKey());
        assertEquals("simulation.conway.title", SimulationType.CONWAYS_LIFE.titleKey());
        assertEquals("simulation.sugar.title", SimulationType.SUGARSCAPE.titleKey());
        assertEquals("simulation.snake.title", SimulationType.SNAKE.titleKey());
        assertEquals("simulation.forest.title", SimulationType.FOREST_FIRE.titleKey());
        assertEquals("simulation.langton.title", SimulationType.LANGTONS_ANT.titleKey());
    }

    @Test
    void testSubtitleKey() {
        assertEquals("simulation.start.subtitle", SimulationType.STARTSCREEN.subtitleKey());
        assertEquals("simulation.lab.subtitle", SimulationType.SIMULATION_LAB.subtitleKey());
        assertEquals("simulation.etpetssim.subtitle", SimulationType.ET_PETS_SIM.subtitleKey());
        assertEquals("simulation.wator.subtitle", SimulationType.WATOR.subtitleKey());
        assertEquals("simulation.conway.subtitle", SimulationType.CONWAYS_LIFE.subtitleKey());
        assertEquals("simulation.sugar.subtitle", SimulationType.SUGARSCAPE.subtitleKey());
        assertEquals("simulation.snake.subtitle", SimulationType.SNAKE.subtitleKey());
        assertEquals("simulation.forest.subtitle", SimulationType.FOREST_FIRE.subtitleKey());
        assertEquals("simulation.langton.subtitle", SimulationType.LANGTONS_ANT.subtitleKey());
    }

    @Test
    void testUrlKey() {
        assertEquals("simulation.start.url", SimulationType.STARTSCREEN.urlKey());
        assertEquals("simulation.lab.url", SimulationType.SIMULATION_LAB.urlKey());
        assertEquals("simulation.etpetssim.url", SimulationType.ET_PETS_SIM.urlKey());
        assertEquals("simulation.wator.url", SimulationType.WATOR.urlKey());
        assertEquals("simulation.conway.url", SimulationType.CONWAYS_LIFE.urlKey());
        assertEquals("simulation.sugar.url", SimulationType.SUGARSCAPE.urlKey());
        assertEquals("simulation.snake.url", SimulationType.SNAKE.urlKey());
        assertEquals("simulation.forest.url", SimulationType.FOREST_FIRE.urlKey());
        assertEquals("simulation.langton.url", SimulationType.LANGTONS_ANT.urlKey());
    }

    @Test
    void testEmojiKey() {
        assertEquals("simulation.start.emoji", SimulationType.STARTSCREEN.emojiKey());
        assertEquals("simulation.lab.emoji", SimulationType.SIMULATION_LAB.emojiKey());
        assertEquals("simulation.etpetssim.emoji", SimulationType.ET_PETS_SIM.emojiKey());
        assertEquals("simulation.wator.emoji", SimulationType.WATOR.emojiKey());
        assertEquals("simulation.conway.emoji", SimulationType.CONWAYS_LIFE.emojiKey());
        assertEquals("simulation.sugar.emoji", SimulationType.SUGARSCAPE.emojiKey());
        assertEquals("simulation.snake.emoji", SimulationType.SNAKE.emojiKey());
        assertEquals("simulation.forest.emoji", SimulationType.FOREST_FIRE.emojiKey());
        assertEquals("simulation.langton.emoji", SimulationType.LANGTONS_ANT.emojiKey());
    }

    @Test
    void testCssPath() {
        assertEquals("", SimulationType.STARTSCREEN.cssPath());
        assertEquals("lab.css", SimulationType.SIMULATION_LAB.cssPath());
        assertEquals("", SimulationType.ET_PETS_SIM.cssPath());
        assertEquals("wator.css", SimulationType.WATOR.cssPath());
        assertEquals("conway.css", SimulationType.CONWAYS_LIFE.cssPath());
        assertEquals("", SimulationType.SUGARSCAPE.cssPath());
        assertEquals("", SimulationType.SNAKE.cssPath());
        assertEquals("", SimulationType.FOREST_FIRE.cssPath());
        assertEquals("langton.css", SimulationType.LANGTONS_ANT.cssPath());
    }

    @Test
    void testCliArguments() {
        assertEquals(List.of("startscreen", "start"), SimulationType.STARTSCREEN.cliArguments());
        assertEquals(List.of("simulationlab", "lab"), SimulationType.SIMULATION_LAB.cliArguments());
        assertEquals(List.of("etpetssim", "etpets"), SimulationType.ET_PETS_SIM.cliArguments());
        assertEquals(List.of("wator", "wa-tor"), SimulationType.WATOR.cliArguments());
        assertEquals(List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol"), SimulationType.CONWAYS_LIFE.cliArguments());
        assertEquals(List.of("sugarscape", "sugar"), SimulationType.SUGARSCAPE.cliArguments());
        assertEquals(List.of("snake", "snakes"), SimulationType.SNAKE.cliArguments());
        assertEquals(List.of("forestfire", "forest-fire", "fire", "forestfiremodel"), SimulationType.FOREST_FIRE.cliArguments());
        assertEquals(List.of("langton", "langtonsant", "langtons-ant", "ant"), SimulationType.LANGTONS_ANT.cliArguments());
    }

    @Test
    void testFromCliArgument() {
        assertEquals(Optional.of(SimulationType.STARTSCREEN), SimulationType.fromCliArgument("start", false));
        assertEquals(Optional.of(SimulationType.SIMULATION_LAB), SimulationType.fromCliArgument("lab", false));
        assertEquals(Optional.empty(), SimulationType.fromCliArgument("notfound", false));
        assertEquals(Optional.of(SimulationType.WATOR), SimulationType.fromCliArgument("wator", true));
        assertEquals(Optional.empty(), SimulationType.fromCliArgument("etpets", true)); // Not implemented.
    }

}

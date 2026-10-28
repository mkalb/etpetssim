package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.core.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static de.mkalb.etpetssim.testsupport.LocalizationTestSupport.assertKeysInAllBundles;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("SpellCheckingInspection")
final class SimulationTypeTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new SimulationType[]{
                        SimulationType.STARTSCREEN,
                        SimulationType.ET_PETS,
                        SimulationType.WATOR,
                        SimulationType.SUGARSCAPE,
                        SimulationType.CONWAYS_LIFE,
                        SimulationType.LANGTONS_ANT,
                        SimulationType.FOREST_FIRE,
                        SimulationType.SNAKE,
                        SimulationType.REBOUNDING_ENTITIES,
                        SimulationType.SIMULATION_LAB
                },
                SimulationType.values()
        );
    }

    @Test
    void testLabelResourceKey() {
        assertKeysInAllBundles(List.of(SimulationType.labelResourceKey()));
    }

    @Test
    void testTitleKey() {
        assertKeysInAllBundles(Arrays.stream(SimulationType.values()).map(SimulationType::titleKey).toList());
    }

    @Test
    void testIsImplemented() {
        assertTrue(SimulationType.STARTSCREEN.isImplemented());
        assertTrue(SimulationType.ET_PETS.isImplemented());
        assertTrue(SimulationType.WATOR.isImplemented());
        assertTrue(SimulationType.SUGARSCAPE.isImplemented());
        assertTrue(SimulationType.CONWAYS_LIFE.isImplemented());
        assertTrue(SimulationType.LANGTONS_ANT.isImplemented());
        assertTrue(SimulationType.FOREST_FIRE.isImplemented());
        assertTrue(SimulationType.SNAKE.isImplemented());
        assertTrue(SimulationType.REBOUNDING_ENTITIES.isImplemented());
        assertTrue(SimulationType.SIMULATION_LAB.isImplemented());
    }

    @Test
    void testIsShownOnStartScreen() {
        assertFalse(SimulationType.STARTSCREEN.isShownOnStartScreen());
        assertTrue(SimulationType.ET_PETS.isShownOnStartScreen());
        assertTrue(SimulationType.WATOR.isShownOnStartScreen());
        assertTrue(SimulationType.SUGARSCAPE.isShownOnStartScreen());
        assertTrue(SimulationType.CONWAYS_LIFE.isShownOnStartScreen());
        assertTrue(SimulationType.LANGTONS_ANT.isShownOnStartScreen());
        assertTrue(SimulationType.FOREST_FIRE.isShownOnStartScreen());
        assertTrue(SimulationType.SNAKE.isShownOnStartScreen());
        assertTrue(SimulationType.REBOUNDING_ENTITIES.isShownOnStartScreen());
        assertFalse(SimulationType.SIMULATION_LAB.isShownOnStartScreen());
    }

    @Test
    void testCliArgumentsReturnsExpectedValues() {
        assertEquals(List.of("startscreen", "start"), SimulationType.STARTSCREEN.cliArguments());
        assertEquals(List.of("etpets"), SimulationType.ET_PETS.cliArguments());
        assertEquals(List.of("wator", "wa-tor"), SimulationType.WATOR.cliArguments());
        assertEquals(List.of("sugarscape", "sugar"), SimulationType.SUGARSCAPE.cliArguments());
        assertEquals(List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol"), SimulationType.CONWAYS_LIFE.cliArguments());
        assertEquals(List.of("langton", "langtonsant", "langtons-ant", "ant"), SimulationType.LANGTONS_ANT.cliArguments());
        assertEquals(List.of("forestfire", "forest-fire", "forest", "fire", "forestfiremodel"), SimulationType.FOREST_FIRE.cliArguments());
        assertEquals(List.of("snake", "snakes"), SimulationType.SNAKE.cliArguments());
        assertEquals(List.of("reboundingentities", "rebounding-entities", "rebounding-entity", "rebounding", "rebound", "rebounders"), SimulationType.REBOUNDING_ENTITIES.cliArguments());
        assertEquals(List.of("simulationlab", "lab"), SimulationType.SIMULATION_LAB.cliArguments());
    }

    @Test
    void testCliArgumentsReturnsUnmodifiableList() {
        assertThrows(UnsupportedOperationException.class, () -> SimulationType.STARTSCREEN.cliArguments().add("other"));
    }

    @Test
    void testCliArgumentsAreUniqueAndLowercase() {
        List<String> allArguments = Arrays.stream(SimulationType.values())
                                          .flatMap(simulationType -> simulationType.cliArguments().stream())
                                          .toList();
        assertAll(
                () -> assertEquals(allArguments.size(), Set.copyOf(allArguments).size()),
                () -> assertTrue(allArguments.stream().allMatch(argument -> argument.equals(argument.toLowerCase(Locale.ROOT))))
        );
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

    @Test
    void testTitle() {
        for (SimulationType simulationType : SimulationType.values()) {
            assertFalse(simulationType.title().isBlank(), simulationType.name());
        }
    }

    @Test
    void testUri() {
        for (SimulationType simulationType : SimulationType.values()) {
            assertEquals(simulationType.url().isPresent(), simulationType.uri().isPresent(), simulationType.name());
        }
    }

    @Test
    void testCssUrl() {
        for (SimulationType simulationType : SimulationType.values()) {
            assertEquals(!simulationType.cssPath().isBlank(), simulationType.cssUrl().isPresent(), simulationType.name());
        }
    }

}

package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class AppArgsTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArgumentsThrowsException() {
        assertThrows(NullPointerException.class, () -> new AppArgs(null));
    }

    @Test
    void testEmptyArguments() {
        String[] args = {};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(AppArgs.Key.HELP));
        assertFalse(cli.hasKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.HELP).isEmpty());
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testUnknownFlagsIsIgnored() {
        String[] args = {"--unknownFlag"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testUnknownNonFlagIsIgnored() {
        String[] args = {"--unknownKey=unknownValue"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testFlagMissingValue() {
        String[] args = {"--help"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testFlagEmptyValue() {
        String[] args = {"--help="};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(AppArgs.Key.HELP));
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testFlagValidTrueValue() {
        String[] args = {"--help=true"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testFlagValidFalseValue() {
        String[] args = {"--help=false"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.HELP));
        assertEquals("false", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testFlagInvalidValue() {
        String[] args = {"--help=maybe"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testNonFlagMissingValue() {
        String[] args = {"--simulation"};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagEmptyValue() {
        String[] args = {"--simulation="};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagValidStringValue() {
        String[] args = {"--simulation=testSim"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.SIMULATION));
        assertEquals(Optional.of("testSim"), cli.getValue(AppArgs.Key.SIMULATION));
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagValidIntValue() {
        String[] args = {"--simulation=42"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.SIMULATION));
        assertEquals(Optional.of("42"), cli.getValue(AppArgs.Key.SIMULATION));
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(42, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testTwoArgumentsWithSameKey() {
        String[] args = {"--simulation=first", "--simulation=second"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.SIMULATION));
        assertFalse(cli.hasKey(AppArgs.Key.HELP));
        assertEquals(Optional.of("second"), cli.getValue(AppArgs.Key.SIMULATION));
    }

    @Test
    void testTwoValidArgumentsWithDifferentKeys() {
        String[] args = {"--simulation=first", "--help"};
        AppArgs cli = new AppArgs(args);

        assertEquals(2, cli.keys().size());
        assertTrue(cli.hasKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.hasKey(AppArgs.Key.HELP));
        assertEquals(Optional.of("first"), cli.getValue(AppArgs.Key.SIMULATION));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
    }

    @Test
    void testIsFlagActiveTrue() {
        String[] args = {"--help"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.isFlagActive(AppArgs.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(AppArgs.Key.SIMULATION));
    }

    @Test
    void testIsFlagActiveFalse() {
        String[] args = {"--help=false"};
        AppArgs cli = new AppArgs(args);

        assertFalse(cli.isFlagActive(AppArgs.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(AppArgs.Key.SIMULATION));
    }

    @Test
    void testKeysPrintHelp() {
        Appendable appendable = new StringBuilder(512);
        AppArgs.Key.printHelp(appendable);
        String result = appendable.toString();

        assertTrue(result.startsWith("List of available command-line arguments:"));
        assertTrue(result.contains("--help"));
        assertTrue(result.contains("--simulation"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testKeysReturnsUnmodifiableSet() {
        String[] args = {"--help"};
        AppArgs cli = new AppArgs(args);

        Set<AppArgs.Key> keys = cli.keys();
        assertThrows(UnsupportedOperationException.class, () -> keys.add(AppArgs.Key.SIMULATION));
    }

    @Test
    void testEnumKeyValues() {
        for (AppArgs.Key key : AppArgs.Key.values()) {
            assertNotNull(key.key(), "Key name should not be null: " + key);
            assertFalse(key.key().isBlank(), "Key name should not be blank: " + key);
        }
    }

    @Test
    void testEnumKeyDescriptions() {
        for (AppArgs.Key key : AppArgs.Key.values()) {
            assertNotNull(key.description(), "Key description should not be null: " + key);
            assertFalse(key.description().isBlank(), "Key description should not be blank: " + key);
        }
    }

    @Test
    void testEnumFromString() {
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("help").orElseThrow());
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("HELP").orElseThrow());
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("Help").orElseThrow());

        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("simulation").orElseThrow());
        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("SIMULATION").orElseThrow());
        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("Simulation").orElseThrow());

        assertFalse(AppArgs.Key.fromString("unknown").isPresent());
        assertFalse(AppArgs.Key.fromString("help ").isPresent());
        assertFalse(AppArgs.Key.fromString(" help").isPresent());
        assertFalse(AppArgs.Key.fromString(" ").isPresent());
        assertFalse(AppArgs.Key.fromString("").isPresent());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testParseBooleanValue() {
        assertTrue(AppArgs.parseBooleanValue("true", false));
        assertTrue(AppArgs.parseBooleanValue("TRUE", false));
        assertTrue(AppArgs.parseBooleanValue("1", false));
        assertTrue(AppArgs.parseBooleanValue("yes", false));
        assertTrue(AppArgs.parseBooleanValue("on", false));

        assertFalse(AppArgs.parseBooleanValue("false", true));
        assertFalse(AppArgs.parseBooleanValue("FALSE", true));
        assertFalse(AppArgs.parseBooleanValue("0", true));
        assertFalse(AppArgs.parseBooleanValue("no", true));
        assertFalse(AppArgs.parseBooleanValue("off", true));

        assertTrue(AppArgs.parseBooleanValue("maybe", true));
        assertTrue(AppArgs.parseBooleanValue("unknown", true));
        assertTrue(AppArgs.parseBooleanValue("", true));
        assertFalse(AppArgs.parseBooleanValue("maybe", false));
        assertFalse(AppArgs.parseBooleanValue("unknown", false));
        assertFalse(AppArgs.parseBooleanValue("", false));

        assertThrows(NullPointerException.class, () -> AppArgs.parseBooleanValue(null, false));
    }

}

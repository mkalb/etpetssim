package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class CommandLineArgumentsTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArgumentsThrowsException() {
        assertThrows(NullPointerException.class, () -> new CommandLineArguments(null));
    }

    @Test
    void testEmptyArguments() {
        String[] args = {};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(CommandLineArguments.Key.HELP));
        assertFalse(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.getValue(CommandLineArguments.Key.HELP).isEmpty());
        assertTrue(cli.getValue(CommandLineArguments.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testUnknownFlagsIsIgnored() {
        String[] args = {"--unknownFlag"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testUnknownNonFlagIsIgnored() {
        String[] args = {"--unknownKey=unknownValue"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testFlagMissingValue() {
        String[] args = {"--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals("true", cli.getValue(CommandLineArguments.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testFlagEmptyValue() {
        String[] args = {"--help="};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(CommandLineArguments.Key.HELP));
        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testFlagValidTrueValue() {
        String[] args = {"--help=true"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals("true", cli.getValue(CommandLineArguments.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testFlagValidFalseValue() {
        String[] args = {"--help=false"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals("false", cli.getValue(CommandLineArguments.Key.HELP).orElseThrow());
        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testFlagInvalidValue() {
        String[] args = {"--help=maybe"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals("true", cli.getValue(CommandLineArguments.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testNonFlagMissingValue() {
        String[] args = {"--simulation"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.getValue(CommandLineArguments.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(CommandLineArguments.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(CommandLineArguments.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagEmptyValue() {
        String[] args = {"--simulation="};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.getValue(CommandLineArguments.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(CommandLineArguments.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(CommandLineArguments.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagValidStringValue() {
        String[] args = {"--simulation=testSim"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertEquals(Optional.of("testSim"), cli.getValue(CommandLineArguments.Key.SIMULATION));
        assertFalse(cli.getBoolean(CommandLineArguments.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(CommandLineArguments.Key.SIMULATION, 1));
    }

    @Test
    void testNonFlagValidIntValue() {
        String[] args = {"--simulation=42"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertEquals(Optional.of("42"), cli.getValue(CommandLineArguments.Key.SIMULATION));
        assertFalse(cli.getBoolean(CommandLineArguments.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.SIMULATION, true));
        assertEquals(42, cli.getInt(CommandLineArguments.Key.SIMULATION, 1));
    }

    @Test
    void testTwoArgumentsWithSameKey() {
        String[] args = {"--simulation=first", "--simulation=second"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertFalse(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals(Optional.of("second"), cli.getValue(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testTwoValidArgumentsWithDifferentKeys() {
        String[] args = {"--simulation=first", "--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertEquals(2, cli.keys().size());
        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals(Optional.of("first"), cli.getValue(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
    }

    @Test
    void testIsFlagActiveTrue() {
        String[] args = {"--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.isFlagActive(CommandLineArguments.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testIsFlagActiveFalse() {
        String[] args = {"--help=false"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertFalse(cli.isFlagActive(CommandLineArguments.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testKeysPrintHelp() {
        Appendable appendable = new StringBuilder(512);
        CommandLineArguments.Key.printHelp(appendable);
        String result = appendable.toString();

        assertTrue(result.startsWith("List of available command-line arguments:"));
        assertTrue(result.contains("--help"));
        assertTrue(result.contains("--simulation"));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testKeysReturnsUnmodifiableSet() {
        String[] args = {"--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        Set<CommandLineArguments.Key> keys = cli.keys();
        assertThrows(UnsupportedOperationException.class, () -> keys.add(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testEnumKeyValues() {
        for (CommandLineArguments.Key key : CommandLineArguments.Key.values()) {
            assertNotNull(key.key(), "Key name should not be null: " + key);
            assertFalse(key.key().isBlank(), "Key name should not be blank: " + key);
        }
    }

    @Test
    void testEnumKeyDescriptions() {
        for (CommandLineArguments.Key key : CommandLineArguments.Key.values()) {
            assertNotNull(key.description(), "Key description should not be null: " + key);
            assertFalse(key.description().isBlank(), "Key description should not be blank: " + key);
        }
    }

    @Test
    void testEnumFromString() {
        assertEquals(CommandLineArguments.Key.HELP, CommandLineArguments.Key.fromString("help").orElseThrow());
        assertEquals(CommandLineArguments.Key.HELP, CommandLineArguments.Key.fromString("HELP").orElseThrow());
        assertEquals(CommandLineArguments.Key.HELP, CommandLineArguments.Key.fromString("Help").orElseThrow());

        assertEquals(CommandLineArguments.Key.SIMULATION, CommandLineArguments.Key.fromString("simulation").orElseThrow());
        assertEquals(CommandLineArguments.Key.SIMULATION, CommandLineArguments.Key.fromString("SIMULATION").orElseThrow());
        assertEquals(CommandLineArguments.Key.SIMULATION, CommandLineArguments.Key.fromString("Simulation").orElseThrow());

        assertFalse(CommandLineArguments.Key.fromString("unknown").isPresent());
        assertFalse(CommandLineArguments.Key.fromString("help ").isPresent());
        assertFalse(CommandLineArguments.Key.fromString(" help").isPresent());
        assertFalse(CommandLineArguments.Key.fromString(" ").isPresent());
        assertFalse(CommandLineArguments.Key.fromString("").isPresent());
    }

}

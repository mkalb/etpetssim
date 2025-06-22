package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("MagicNumber")
class CommandLineArgumentsTest {

    @Test
    void testValidKeyWithValue() {
        String[] args = {"--simulation=testSim"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertEquals(Optional.of("testSim"), cli.getValue(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testFlagWithoutValueDefaultsToTrue() {
        String[] args = {"--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
    }

    @Test
    void testMissingKeyReturnsDefaultBoolean() {
        String[] args = {};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, false));
    }

    @Test
    void testInvalidBooleanReturnsDefault() {
        String[] args = {"--help=maybe"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertFalse(cli.getBoolean(CommandLineArguments.Key.HELP, false));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, true));
    }

    @Test
    void testValidIntegerParsing() {
        String[] args = {"--simulation=42"};
        CommandLineArguments cli = new CommandLineArguments(args);

        int value = cli.getInt(CommandLineArguments.Key.SIMULATION, 0);
        assertEquals(42, value);
    }

    @Test
    void testInvalidIntegerReturnsDefault() {
        String[] args = {"--simulation=abc"};
        CommandLineArguments cli = new CommandLineArguments(args);

        int value = cli.getInt(CommandLineArguments.Key.SIMULATION, 99);
        assertEquals(99, value);
    }

    @Test
    void testUnknownKeyIsIgnored() {
        String[] args = {"--unknown=value"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testEmptyArgumentsArray() {
        String[] args = {};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testTwoArgumentsWithSameKey() {
        String[] args = {"--simulation=first", "--simulation=second"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertEquals(Optional.of("second"), cli.getValue(CommandLineArguments.Key.SIMULATION));
    }

    @Test
    void testTwoValidArgumentsWithDifferentKeys() {
        String[] args = {"--simulation=first", "--help"};
        CommandLineArguments cli = new CommandLineArguments(args);

        assertTrue(cli.hasKey(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.hasKey(CommandLineArguments.Key.HELP));
        assertEquals(Optional.of("first"), cli.getValue(CommandLineArguments.Key.SIMULATION));
        assertTrue(cli.getBoolean(CommandLineArguments.Key.HELP, false));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullArgumentsThrowsException() {
        assertThrows(NullPointerException.class, () -> new CommandLineArguments(null));
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
        assertTrue(CommandLineArguments.Key.fromString("help").isPresent());
        assertEquals(CommandLineArguments.Key.HELP, CommandLineArguments.Key.fromString("help").get());

        assertTrue(CommandLineArguments.Key.fromString("simulation").isPresent());
        assertEquals(CommandLineArguments.Key.SIMULATION, CommandLineArguments.Key.fromString("simulation").get());

        assertFalse(CommandLineArguments.Key.fromString("unknown").isPresent());
    }

    @Test
    void testEnumFromStringDifferentCase() {
        assertTrue(CommandLineArguments.Key.fromString("HELP").isPresent());
        assertEquals(CommandLineArguments.Key.HELP, CommandLineArguments.Key.fromString("HELP").get());

        assertTrue(CommandLineArguments.Key.fromString("SiMuLaTiOn").isPresent());
        assertEquals(CommandLineArguments.Key.SIMULATION, CommandLineArguments.Key.fromString("SiMuLaTiOn").get());

        assertFalse(CommandLineArguments.Key.fromString("UnknownKey").isPresent());
    }

}

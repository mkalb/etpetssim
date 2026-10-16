package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("MagicNumber")
final class AppArgsTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testConstructorRejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> new AppArgs(null));
    }

    @Test
    void testConstructorAcceptsEmptyArguments() {
        String[] args = {};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.containsKey(AppArgs.Key.HELP));
        assertFalse(cli.containsKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.HELP).isEmpty());
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorIgnoresUnknownFlag() {
        String[] args = {"--unknownFlag"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testConstructorIgnoresUnknownKeyValue() {
        String[] args = {"--unknownKey=unknownValue"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.keys().isEmpty());
    }

    @Test
    void testConstructorTreatsFlagWithoutValueAsTrue() {
        String[] args = {"--help"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorIgnoresFlagWithEmptyValue() {
        String[] args = {"--help="};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.containsKey(AppArgs.Key.HELP));
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorParsesFlagTrueValue() {
        String[] args = {"--help=true"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorParsesFlagFalseValue() {
        String[] args = {"--help=false"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.HELP));
        assertEquals("false", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, false));
        assertFalse(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorTreatsInvalidFlagValueAsTrue() {
        String[] args = {"--help=maybe"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.HELP));
        assertEquals("true", cli.getValue(AppArgs.Key.HELP).orElseThrow());
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, true));
    }

    @Test
    void testConstructorIgnoresNonFlagWithoutValue() {
        String[] args = {"--simulation"};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.containsKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testConstructorIgnoresNonFlagWithEmptyValue() {
        String[] args = {"--simulation="};
        AppArgs cli = new AppArgs(args);

        assertEquals(0, cli.keys().size());
        assertFalse(cli.containsKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty());
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testConstructorParsesNonFlagStringValue() {
        String[] args = {"--simulation=testSim"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.SIMULATION));
        assertEquals(Optional.of("testSim"), cli.getValue(AppArgs.Key.SIMULATION));
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testConstructorParsesNonFlagIntValue() {
        String[] args = {"--simulation=42"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.SIMULATION));
        assertEquals(Optional.of("42"), cli.getValue(AppArgs.Key.SIMULATION));
        assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false));
        assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true));
        assertEquals(42, cli.getInt(AppArgs.Key.SIMULATION, 1));
    }

    @Test
    void testConstructorKeepsLastValueForDuplicateKey() {
        String[] args = {"--simulation=first", "--simulation=second"};
        AppArgs cli = new AppArgs(args);

        assertEquals(1, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.SIMULATION));
        assertFalse(cli.containsKey(AppArgs.Key.HELP));
        assertEquals(Optional.of("second"), cli.getValue(AppArgs.Key.SIMULATION));
    }

    @Test
    void testConstructorParsesDifferentKeys() {
        String[] args = {"--simulation=first", "--help"};
        AppArgs cli = new AppArgs(args);

        assertEquals(2, cli.keys().size());
        assertTrue(cli.containsKey(AppArgs.Key.SIMULATION));
        assertTrue(cli.containsKey(AppArgs.Key.HELP));
        assertEquals(Optional.of("first"), cli.getValue(AppArgs.Key.SIMULATION));
        assertTrue(cli.getBoolean(AppArgs.Key.HELP, false));
    }

    @Test
    void testIsFlagActiveReturnsTrue() {
        String[] args = {"--help"};
        AppArgs cli = new AppArgs(args);

        assertTrue(cli.isFlagActive(AppArgs.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(AppArgs.Key.SIMULATION));
    }

    @Test
    void testIsFlagActiveReturnsFalse() {
        String[] args = {"--help=false"};
        AppArgs cli = new AppArgs(args);

        assertFalse(cli.isFlagActive(AppArgs.Key.HELP));
        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(AppArgs.Key.SIMULATION));
    }

    @Test
    void testPrintHelp() {
        Appendable appendable = new StringBuilder(512);
        AppArgs.Key.printHelp(appendable);
        String result = appendable.toString();

        assertTrue(result.startsWith("List of available command-line arguments:"));
        assertTrue(result.contains("--help"));
        assertTrue(result.contains("--locale"));
        assertTrue(result.contains("--log-console"));
        assertTrue(result.contains("--log-file"));
        assertTrue(result.contains("--log-level"));
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
    void testEnumValues() {
        assertNotNull(AppArgs.Key.valueOf("HELP"));
        assertNotNull(AppArgs.Key.valueOf("LOCALE"));
        assertNotNull(AppArgs.Key.valueOf("LOG_CONSOLE"));
        assertNotNull(AppArgs.Key.valueOf("LOG_FILE"));
        assertNotNull(AppArgs.Key.valueOf("LOG_LEVEL"));
        assertNotNull(AppArgs.Key.valueOf("SIMULATION"));
    }

    @Test
    void testEnumCount() {
        assertEquals(6, AppArgs.Key.values().length, "There should be exactly 6 values");
    }

    @Test
    void testDeclarationOrder() {
        assertArrayEquals(
                new AppArgs.Key[]{
                        AppArgs.Key.HELP,
                        AppArgs.Key.LOCALE,
                        AppArgs.Key.LOG_CONSOLE,
                        AppArgs.Key.LOG_FILE,
                        AppArgs.Key.LOG_LEVEL,
                        AppArgs.Key.SIMULATION
                },
                AppArgs.Key.values()
        );
    }

    @Test
    void testKey() {
        for (AppArgs.Key key : AppArgs.Key.values()) {
            assertNotNull(key.key(), "Key name should not be null: " + key);
            assertFalse(key.key().isBlank(), "Key name should not be blank: " + key);
        }

        assertEquals("help", AppArgs.Key.HELP.key());
        assertEquals("locale", AppArgs.Key.LOCALE.key());
        assertEquals("log-console", AppArgs.Key.LOG_CONSOLE.key());
        assertEquals("log-file", AppArgs.Key.LOG_FILE.key());
        assertEquals("log-level", AppArgs.Key.LOG_LEVEL.key());
        assertEquals("simulation", AppArgs.Key.SIMULATION.key());
    }

    @Test
    void testDescription() {
        for (AppArgs.Key key : AppArgs.Key.values()) {
            assertNotNull(key.description(), "Key description should not be null: " + key);
            assertFalse(key.description().isBlank(), "Key description should not be blank: " + key);
        }
    }

    @Test
    void testFromString() {
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("help").orElseThrow());
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("HELP").orElseThrow());
        assertEquals(AppArgs.Key.HELP, AppArgs.Key.fromString("Help").orElseThrow());

        assertEquals(AppArgs.Key.LOCALE, AppArgs.Key.fromString("locale").orElseThrow());
        assertEquals(AppArgs.Key.LOCALE, AppArgs.Key.fromString("LOCALE").orElseThrow());
        assertEquals(AppArgs.Key.LOCALE, AppArgs.Key.fromString("Locale").orElseThrow());

        assertEquals(AppArgs.Key.LOG_CONSOLE, AppArgs.Key.fromString("log-console").orElseThrow());
        assertEquals(AppArgs.Key.LOG_CONSOLE, AppArgs.Key.fromString("LOG-CONSOLE").orElseThrow());
        assertEquals(AppArgs.Key.LOG_CONSOLE, AppArgs.Key.fromString("Log-Console").orElseThrow());

        assertEquals(AppArgs.Key.LOG_FILE, AppArgs.Key.fromString("log-file").orElseThrow());
        assertEquals(AppArgs.Key.LOG_FILE, AppArgs.Key.fromString("LOG-FILE").orElseThrow());
        assertEquals(AppArgs.Key.LOG_FILE, AppArgs.Key.fromString("Log-File").orElseThrow());

        assertEquals(AppArgs.Key.LOG_LEVEL, AppArgs.Key.fromString("log-level").orElseThrow());
        assertEquals(AppArgs.Key.LOG_LEVEL, AppArgs.Key.fromString("LOG-LEVEL").orElseThrow());
        assertEquals(AppArgs.Key.LOG_LEVEL, AppArgs.Key.fromString("Log-Level").orElseThrow());

        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("simulation").orElseThrow());
        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("SIMULATION").orElseThrow());
        assertEquals(AppArgs.Key.SIMULATION, AppArgs.Key.fromString("Simulation").orElseThrow());

        assertFalse(AppArgs.Key.fromString("unknown").isPresent());
        assertFalse(AppArgs.Key.fromString("help ").isPresent());
        assertFalse(AppArgs.Key.fromString(" help").isPresent());
        assertFalse(AppArgs.Key.fromString(" ").isPresent());
        assertFalse(AppArgs.Key.fromString("").isPresent());
    }

    @Test
    void testValueOfRejectsInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> AppArgs.Key.valueOf("INVALID"));
    }

    @Test
    void testValueOfRejectsNull() {
        assertThrows(NullPointerException.class, () -> AppArgs.Key.valueOf(null));
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

    @Test
    void testToArgumentString() {
        assertEquals("--help --simulation=testSim", new AppArgs(new String[]{"--help", "--simulation=testSim"}).toArgumentString());
        assertEquals("--simulation=testSim", new AppArgs(new String[]{"--help=", "--simulation=testSim"}).toArgumentString());
        assertEquals("--help --simulation=testSim", new AppArgs(new String[]{"--help=true", "--simulation=testSim"}).toArgumentString());
        assertEquals("--help=false --simulation=testSim", new AppArgs(new String[]{"--help=false", "--simulation=testSim"}).toArgumentString());

        assertEquals("", new AppArgs(new String[]{"--simulation"}).toArgumentString());
        assertEquals("", new AppArgs(new String[]{"--simulation="}).toArgumentString());
        assertEquals("--simulation=1", new AppArgs(new String[]{"--simulation=1"}).toArgumentString());
        assertEquals("--simulation=false", new AppArgs(new String[]{"--simulation=false"}).toArgumentString());
    }

    @Test
    void testToString() {
        assertEquals("AppArgs{arguments={HELP=true, SIMULATION=testSim}}", new AppArgs(new String[]{"--help", "--simulation=testSim"}).toString());
        assertEquals("AppArgs{arguments={SIMULATION=testSim}}", new AppArgs(new String[]{"--help=", "--simulation=testSim"}).toString());
        assertEquals("AppArgs{arguments={HELP=true, SIMULATION=testSim}}", new AppArgs(new String[]{"--help=true", "--simulation=testSim"}).toString());
        assertEquals("AppArgs{arguments={HELP=false, SIMULATION=testSim}}", new AppArgs(new String[]{"--help=false", "--simulation=testSim"}).toString());

        assertEquals("AppArgs{arguments={}}", new AppArgs(new String[]{"--simulation"}).toString());
        assertEquals("AppArgs{arguments={}}", new AppArgs(new String[]{"--simulation="}).toString());
        assertEquals("AppArgs{arguments={SIMULATION=1}}", new AppArgs(new String[]{"--simulation=1"}).toString());
        assertEquals("AppArgs{arguments={SIMULATION=false}}", new AppArgs(new String[]{"--simulation=false"}).toString());
    }

}

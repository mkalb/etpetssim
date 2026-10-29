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

    private static AppArgs parse(String... args) {
        return new AppArgs(args);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testConstructorRejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> new AppArgs(null));
    }

    @Test
    void testConstructorAcceptsEmptyArguments() {
        AppArgs cli = parse();

        assertAll(
                () -> assertTrue(cli.keys().isEmpty()),
                () -> assertTrue(cli.getValue(AppArgs.Key.HELP).isEmpty()),
                () -> assertTrue(cli.getValue(AppArgs.Key.SIMULATION).isEmpty())
        );
    }

    @Test
    void testConstructorIgnoresUnknownFlag() {
        assertTrue(parse("--unknownFlag").keys().isEmpty());
    }

    @Test
    void testConstructorIgnoresUnknownKeyValue() {
        assertTrue(parse("--unknownKey=unknownValue").keys().isEmpty());
    }

    @Test
    void testConstructorTreatsFlagWithoutValueAsTrue() {
        AppArgs cli = parse("--help");

        assertAll(
                () -> assertEquals(Set.of(AppArgs.Key.HELP), cli.keys()),
                () -> assertEquals(Optional.of("true"), cli.getValue(AppArgs.Key.HELP))
        );
    }

    @Test
    void testConstructorIgnoresFlagWithEmptyValue() {
        assertTrue(parse("--help=").keys().isEmpty());
    }

    @Test
    void testConstructorParsesFlagTrueValue() {
        assertEquals(Optional.of("true"), parse("--help=true").getValue(AppArgs.Key.HELP));
    }

    @Test
    void testConstructorParsesFlagFalseValue() {
        assertEquals(Optional.of("false"), parse("--help=false").getValue(AppArgs.Key.HELP));
    }

    @Test
    void testConstructorTreatsInvalidFlagValueAsTrue() {
        assertEquals(Optional.of("true"), parse("--help=maybe").getValue(AppArgs.Key.HELP));
    }

    @Test
    void testConstructorParsesFlagValueWithSpaces() {
        assertEquals(Optional.of("true"), parse("--help=no thanks").getValue(AppArgs.Key.HELP));
    }

    @Test
    void testConstructorIgnoresNonFlagWithoutValue() {
        assertTrue(parse("--simulation").keys().isEmpty());
    }

    @Test
    void testConstructorIgnoresNonFlagWithEmptyValue() {
        assertTrue(parse("--simulation=").keys().isEmpty());
    }

    @Test
    void testConstructorParsesNonFlagStringValue() {
        AppArgs cli = parse("--simulation=testSim");

        assertAll(
                () -> assertEquals(Set.of(AppArgs.Key.SIMULATION), cli.keys()),
                () -> assertEquals(Optional.of("testSim"), cli.getValue(AppArgs.Key.SIMULATION))
        );
    }

    @Test
    void testConstructorParsesNonFlagValueWithSpaces() {
        assertEquals(Optional.of("C:\\My Simulations\\conway.properties"),
                parse("--simulation=C:\\My Simulations\\conway.properties").getValue(AppArgs.Key.SIMULATION));
    }

    @Test
    void testConstructorStripsValue() {
        AppArgs cli = parse("--simulation= \tvalue with spaces \t", "--log-console= false ");

        assertAll(
                () -> assertEquals(Optional.of("value with spaces"), cli.getValue(AppArgs.Key.SIMULATION)),
                () -> assertFalse(cli.isFlagActive(AppArgs.Key.LOG_CONSOLE))
        );
    }

    @Test
    void testConstructorIgnoresBlankValue() {
        assertTrue(parse("--simulation=   ", "--simulation=\t", "--help= ").keys().isEmpty());
    }

    @Test
    void testConstructorIgnoresWhitespaceInKey() {
        assertTrue(parse("--simu lation=testSim", "-- simulation=testSim", "--simulation =testSim").keys().isEmpty());
    }

    @Test
    void testConstructorIgnoresKeyCase() {
        assertEquals(Optional.of("testSim"), parse("--SIMULATION=testSim").getValue(AppArgs.Key.SIMULATION));
    }

    @Test
    void testConstructorKeepsLastValueForDuplicateKey() {
        AppArgs cli = parse("--simulation=first", "--simulation=second");

        assertAll(
                () -> assertEquals(Set.of(AppArgs.Key.SIMULATION), cli.keys()),
                () -> assertEquals(Optional.of("second"), cli.getValue(AppArgs.Key.SIMULATION))
        );
    }

    @Test
    void testConstructorParsesDifferentKeys() {
        AppArgs cli = parse("--simulation=first", "--help");

        assertAll(
                () -> assertEquals(Set.of(AppArgs.Key.SIMULATION, AppArgs.Key.HELP), cli.keys()),
                () -> assertEquals(Optional.of("first"), cli.getValue(AppArgs.Key.SIMULATION)),
                () -> assertTrue(cli.isFlagActive(AppArgs.Key.HELP))
        );
    }

    @Test
    void testGetValue() {
        AppArgs cli = parse("--simulation=testSim");

        assertAll(
                () -> assertEquals(Optional.of("testSim"), cli.getValue(AppArgs.Key.SIMULATION)),
                () -> assertTrue(cli.getValue(AppArgs.Key.LOCALE).isEmpty())
        );
    }

    @Test
    void testGetBoolean() {
        AppArgs cli = parse("--help=false", "--log-console");

        assertAll(
                () -> assertFalse(cli.getBoolean(AppArgs.Key.HELP, true)),
                () -> assertTrue(cli.getBoolean(AppArgs.Key.LOG_CONSOLE, false)),
                () -> assertTrue(cli.getBoolean(AppArgs.Key.LOG_FILE, true)),
                () -> assertFalse(cli.getBoolean(AppArgs.Key.LOG_FILE, false))
        );
    }

    @Test
    void testGetBooleanReturnsDefaultForUnparseableValue() {
        AppArgs cli = parse("--simulation=testSim");

        assertAll(
                () -> assertTrue(cli.getBoolean(AppArgs.Key.SIMULATION, true)),
                () -> assertFalse(cli.getBoolean(AppArgs.Key.SIMULATION, false))
        );
    }

    @Test
    void testGetInt() {
        AppArgs cli = parse("--simulation=42");

        assertAll(
                () -> assertEquals(42, cli.getInt(AppArgs.Key.SIMULATION, 1)),
                () -> assertEquals(1, cli.getInt(AppArgs.Key.LOCALE, 1))
        );
    }

    @Test
    void testGetIntReturnsDefaultForInvalidValue() {
        AppArgs cli = parse("--simulation=testSim", "--log-level=2147483648");

        assertAll(
                () -> assertEquals(1, cli.getInt(AppArgs.Key.SIMULATION, 1)),
                () -> assertEquals(1, cli.getInt(AppArgs.Key.LOG_LEVEL, 1))
        );
    }

    @Test
    void testIsFlagActive() {
        AppArgs cli = parse("--help", "--log-console=false");

        assertAll(
                () -> assertTrue(cli.isFlagActive(AppArgs.Key.HELP)),
                () -> assertFalse(cli.isFlagActive(AppArgs.Key.LOG_CONSOLE)),
                () -> assertFalse(cli.isFlagActive(AppArgs.Key.LOG_FILE))
        );
    }

    @Test
    void testIsFlagActiveThrowsForNonFlagKey() {
        AppArgs cli = parse("--simulation=testSim");

        assertThrows(IllegalArgumentException.class, () -> cli.isFlagActive(AppArgs.Key.SIMULATION));
    }

    @Test
    void testContainsKey() {
        AppArgs cli = parse("--help=false");

        assertAll(
                () -> assertTrue(cli.containsKey(AppArgs.Key.HELP)),
                () -> assertFalse(cli.containsKey(AppArgs.Key.SIMULATION))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testKeys() {
        Set<AppArgs.Key> keys = parse("--help").keys();

        assertAll(
                () -> assertEquals(Set.of(AppArgs.Key.HELP), keys),
                () -> assertThrows(UnsupportedOperationException.class, () -> keys.add(AppArgs.Key.SIMULATION))
        );
    }

    @Test
    void testToArgumentString() {
        assertAll(
                () -> assertEquals("--help --simulation=testSim", parse("--help", "--simulation=testSim").toArgumentString()),
                () -> assertEquals("--simulation=testSim", parse("--help=", "--simulation=testSim").toArgumentString()),
                () -> assertEquals("--help --simulation=testSim", parse("--help=true", "--simulation=testSim").toArgumentString()),
                () -> assertEquals("--help=false --simulation=testSim", parse("--help=false", "--simulation=testSim").toArgumentString()),
                () -> assertEquals("", parse("--simulation").toArgumentString()),
                () -> assertEquals("", parse("--simulation=").toArgumentString()),
                () -> assertEquals("--simulation=1", parse("--simulation=1").toArgumentString()),
                () -> assertEquals("--simulation=false", parse("--simulation=false").toArgumentString()),
                () -> assertEquals("--help --simulation=\"value with spaces\"",
                        parse("--help", "--simulation=value with spaces").toArgumentString()),
                () -> assertEquals("--simulation=\"tab\tvalue\"", parse("--simulation=tab\tvalue").toArgumentString())
        );
    }

    @Test
    void testToString() {
        assertAll(
                () -> assertEquals("AppArgs{arguments={HELP=true, SIMULATION=testSim}}",
                        parse("--help", "--simulation=testSim").toString()),
                () -> assertEquals("AppArgs{arguments={HELP=false, SIMULATION=testSim}}",
                        parse("--help=false", "--simulation=testSim").toString()),
                () -> assertEquals("AppArgs{arguments={}}", parse("--simulation").toString())
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testParseBooleanValue() {
        assertAll(
                () -> assertTrue(AppArgs.parseBooleanValue("true", false)),
                () -> assertTrue(AppArgs.parseBooleanValue("TRUE", false)),
                () -> assertTrue(AppArgs.parseBooleanValue("1", false)),
                () -> assertTrue(AppArgs.parseBooleanValue("yes", false)),
                () -> assertTrue(AppArgs.parseBooleanValue("on", false)),
                () -> assertFalse(AppArgs.parseBooleanValue("false", true)),
                () -> assertFalse(AppArgs.parseBooleanValue("FALSE", true)),
                () -> assertFalse(AppArgs.parseBooleanValue("0", true)),
                () -> assertFalse(AppArgs.parseBooleanValue("no", true)),
                () -> assertFalse(AppArgs.parseBooleanValue("off", true)),
                () -> assertTrue(AppArgs.parseBooleanValue("maybe", true)),
                () -> assertTrue(AppArgs.parseBooleanValue("", true)),
                () -> assertFalse(AppArgs.parseBooleanValue("maybe", false)),
                () -> assertFalse(AppArgs.parseBooleanValue("", false)),
                () -> assertThrows(NullPointerException.class, () -> AppArgs.parseBooleanValue(null, false))
        );
    }

    @Test
    void testKeyDeclarationOrder() {
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
    void testFromKeyName() {
        assertAll(
                () -> assertEquals(Optional.of(AppArgs.Key.HELP), AppArgs.Key.fromKeyName("help")),
                () -> assertEquals(Optional.of(AppArgs.Key.HELP), AppArgs.Key.fromKeyName("HELP")),
                () -> assertEquals(Optional.of(AppArgs.Key.LOCALE), AppArgs.Key.fromKeyName("Locale")),
                () -> assertEquals(Optional.of(AppArgs.Key.LOG_CONSOLE), AppArgs.Key.fromKeyName("Log-Console")),
                () -> assertEquals(Optional.of(AppArgs.Key.LOG_FILE), AppArgs.Key.fromKeyName("log-file")),
                () -> assertEquals(Optional.of(AppArgs.Key.LOG_LEVEL), AppArgs.Key.fromKeyName("LOG-LEVEL")),
                () -> assertEquals(Optional.of(AppArgs.Key.SIMULATION), AppArgs.Key.fromKeyName("simulation"))
        );
    }

    @Test
    void testFromKeyNameReturnsEmptyForUnknownName() {
        assertAll(
                () -> assertTrue(AppArgs.Key.fromKeyName("unknown").isEmpty()),
                () -> assertTrue(AppArgs.Key.fromKeyName("help ").isEmpty()),
                () -> assertTrue(AppArgs.Key.fromKeyName(" help").isEmpty()),
                () -> assertTrue(AppArgs.Key.fromKeyName("--help").isEmpty()),
                () -> assertTrue(AppArgs.Key.fromKeyName(" ").isEmpty()),
                () -> assertTrue(AppArgs.Key.fromKeyName("").isEmpty())
        );
    }

    @Test
    void testPrintHelp() {
        StringBuilder appendable = new StringBuilder(512);

        AppArgs.Key.printHelp(appendable);
        String result = appendable.toString();

        assertAll(
                () -> assertTrue(result.startsWith("List of available command-line arguments:")),
                () -> assertTrue(result.contains("--help: Shows this help message (flag)")),
                () -> assertTrue(result.contains("--locale: ")),
                () -> assertTrue(result.contains("--log-console: ")),
                () -> assertTrue(result.contains("--log-file: ")),
                () -> assertTrue(result.contains("--log-level: ")),
                () -> assertTrue(result.contains("--simulation: "))
        );
    }

    @Test
    void testKey() {
        assertAll(
                () -> assertEquals("help", AppArgs.Key.HELP.key()),
                () -> assertEquals("locale", AppArgs.Key.LOCALE.key()),
                () -> assertEquals("log-console", AppArgs.Key.LOG_CONSOLE.key()),
                () -> assertEquals("log-file", AppArgs.Key.LOG_FILE.key()),
                () -> assertEquals("log-level", AppArgs.Key.LOG_LEVEL.key()),
                () -> assertEquals("simulation", AppArgs.Key.SIMULATION.key())
        );
    }

    @Test
    void testDescription() {
        for (AppArgs.Key key : AppArgs.Key.values()) {
            assertFalse(key.description().isBlank(), "Key description should not be blank: " + key);
        }
    }

    @Test
    void testIsFlag() {
        assertAll(
                () -> assertTrue(AppArgs.Key.HELP.isFlag()),
                () -> assertFalse(AppArgs.Key.LOCALE.isFlag()),
                () -> assertTrue(AppArgs.Key.LOG_CONSOLE.isFlag()),
                () -> assertTrue(AppArgs.Key.LOG_FILE.isFlag()),
                () -> assertFalse(AppArgs.Key.LOG_LEVEL.isFlag()),
                () -> assertFalse(AppArgs.Key.SIMULATION.isFlag())
        );
    }

}

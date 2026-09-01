package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class AppLoggerTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static String formatRecord(LogRecord record) {
        try {
            Class<?> formatterClass = Class.forName("de.mkalb.etpetssim.core.AppLogger$AppLogFormatter");
            Constructor<?> constructor = formatterClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Formatter formatter = (Formatter) constructor.newInstance();
            return formatter.format(record);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to format log record", e);
        }
    }

    @BeforeEach
    void setUpBeforeEach() {
        AppLogger.resetForTesting();
    }

    @Test
    void testInitialize() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.numberOfHandlersForTesting());
    }

    @Test
    void testInitializeWithoutHandlers() {
        int numHandlers = AppLogger.numberOfHandlersForTesting();
        AppLogger.initialize(AppLogger.LogLevel.INFO, false, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(numHandlers, AppLogger.numberOfHandlersForTesting());
    }

    @Test
    void testInitializeForTesting() {
        AppLogger.initializeForTesting();
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.numberOfHandlersForTesting());
    }

    @Test
    void testResetForTesting() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.numberOfHandlersForTesting());
        AppLogger.resetForTesting();
        assertFalse(AppLogger.isInitialized());
        assertEquals(0, AppLogger.numberOfHandlersForTesting());
    }

    @Test
    void testDebugLogging() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        assertDoesNotThrow(() -> AppLogger.debug("Debug message"));
    }

    @Test
    void testDebugfLogging() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        assertDoesNotThrow(() -> AppLogger.debugf("Debug %s %d", "message", 1));
    }

    @Test
    void testInfoLogging() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertDoesNotThrow(() -> AppLogger.info("Info message"));
    }

    @Test
    void testInfofLogging() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertDoesNotThrow(() -> AppLogger.infof("Info %s %d", "message", 2));
    }

    @Test
    void testWarnLogging() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        assertDoesNotThrow(() -> AppLogger.warn("Warn message"));
    }

    @Test
    void testWarnfLogging() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        assertDoesNotThrow(() -> AppLogger.warnf("Warn %s %d", "message", 3));
    }

    @Test
    void testErrorLogging() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertDoesNotThrow(() -> AppLogger.error("Error message"));
    }

    @Test
    void testErrorfLogging() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertDoesNotThrow(() -> AppLogger.errorf("Error %s %d", "message", 4));
    }

    @Test
    void testErrorfWithThrowableLogging() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        Throwable t = new RuntimeException("Test exception");
        assertDoesNotThrow(() -> AppLogger.errorf(t, "Error %s %d", "message", 5));
    }

    @Test
    void testDebugSupplierLogging() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        Supplier<String> supplier = () -> "Debug from supplier";
        assertDoesNotThrow(() -> AppLogger.debug(supplier));
    }

    @Test
    void testInfoSupplierLogging() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        Supplier<String> supplier = () -> "Info from supplier";
        assertDoesNotThrow(() -> AppLogger.info(supplier));
    }

    @Test
    void testWarnSupplierLogging() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        Supplier<String> supplier = () -> "Warn from supplier";
        assertDoesNotThrow(() -> AppLogger.warn(supplier));
    }

    @Test
    void testErrorSupplierLogging() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        Supplier<String> supplier = () -> "Error from supplier";
        assertDoesNotThrow(() -> AppLogger.error(supplier));
    }

    @Test
    void testErrorWithThrowable() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        Throwable t = new RuntimeException("Test exception");
        assertDoesNotThrow(() -> AppLogger.error(t, "Error with throwable"));
    }

    @Test
    void testFormatterIncludesCurrentThreadNameAfterLogLevel() {
        LogRecord record = new LogRecord(Level.INFO, "Info message");

        String formatted = formatRecord(record);

        assertTrue(formatted.contains("[INFO   ] [" + Thread.currentThread().getName() + "] Info message"));
    }

    @Test
    void testFormatterAbbreviatesJavaFxApplicationThreadName() throws InterruptedException {
        AtomicReference<@Nullable String> formattedReference = new AtomicReference<>();
        Thread thread = Thread.ofPlatform().name("JavaFX Application Thread").start(
                () -> formattedReference.set(formatRecord(new LogRecord(Level.INFO, "Info message")))
        );

        thread.join();

        String s = formattedReference.get();
        assertNotNull(s, "Formatted string should not be null");
        assertTrue(s.contains("[INFO   ] [FX] Info message"));
    }

    @Test
    void testInitializeTwiceThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertThrows(IllegalStateException.class, () ->
                AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullSupplierThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppLogger.debug((Supplier<String>) null)),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.info((Supplier<String>) null)),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.warn((Supplier<String>) null)),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.error((Supplier<String>) null))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullFormatThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppLogger.debugf(null, "x")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.infof(null, "x")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.warnf(null, "x")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf((String) null, "x")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf(new RuntimeException("x"), null, "x"))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testNullThrowableThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> AppLogger.error(null, "msg")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf((Throwable) null, "msg %s", "x"))
        );
    }

}

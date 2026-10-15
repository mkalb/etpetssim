package de.mkalb.etpetssim.core;

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
    void testInitializeAddsConsoleHandler() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.handlerCountForTesting());
    }

    @Test
    void testInitializeAddsNoHandlerWithoutConsole() {
        int numHandlers = AppLogger.handlerCountForTesting();
        AppLogger.initialize(AppLogger.LogLevel.INFO, false, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(numHandlers, AppLogger.handlerCountForTesting());
    }

    @Test
    void testInitializeForTesting() {
        AppLogger.initializeForTesting();
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.handlerCountForTesting());
    }

    @Test
    void testResetForTesting() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertTrue(AppLogger.isInitialized());
        assertEquals(1, AppLogger.handlerCountForTesting());
        AppLogger.resetForTesting();
        assertFalse(AppLogger.isInitialized());
        assertEquals(0, AppLogger.handlerCountForTesting());
    }

    @Test
    void testDebug() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        assertDoesNotThrow(() -> AppLogger.debug("Debug message"));
    }

    @Test
    void testDebugf() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        assertDoesNotThrow(() -> AppLogger.debugf("Debug %s %d", "message", 1));
    }

    @Test
    void testInfo() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertDoesNotThrow(() -> AppLogger.info("Info message"));
    }

    @Test
    void testInfof() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertDoesNotThrow(() -> AppLogger.infof("Info %s %d", "message", 2));
    }

    @Test
    void testWarn() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        assertDoesNotThrow(() -> AppLogger.warn("Warn message"));
    }

    @Test
    void testWarnf() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        assertDoesNotThrow(() -> AppLogger.warnf("Warn %s %d", "message", 3));
    }

    @Test
    void testError() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertDoesNotThrow(() -> AppLogger.error("Error message"));
    }

    @Test
    void testErrorf() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertDoesNotThrow(() -> AppLogger.errorf("Error %s %d", "message", 4));
    }

    @Test
    void testErrorfWithThrowable() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        Throwable t = new RuntimeException("Test exception");
        assertDoesNotThrow(() -> AppLogger.errorf(t, "Error %s %d", "message", 5));
    }

    @Test
    void testDebugWithSupplier() {
        AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null);
        Supplier<String> supplier = () -> "Debug from supplier";
        assertDoesNotThrow(() -> AppLogger.debug(supplier));
    }

    @Test
    void testInfoWithSupplier() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        Supplier<String> supplier = () -> "Info from supplier";
        assertDoesNotThrow(() -> AppLogger.info(supplier));
    }

    @Test
    void testWarnWithSupplier() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        Supplier<String> supplier = () -> "Warn from supplier";
        assertDoesNotThrow(() -> AppLogger.warn(supplier));
    }

    @Test
    void testErrorWithSupplier() {
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
    void testFormatIncludesCurrentThreadNameAfterLogLevel() {
        LogRecord record = new LogRecord(Level.INFO, "Info message");

        String formatted = formatRecord(record);

        assertTrue(formatted.contains("[INFO   ] [" + Thread.currentThread().getName() + "] Info message"));
    }

    @Test
    void testFormatAbbreviatesJavaFxApplicationThreadName() throws InterruptedException {
        AtomicReference<String> formattedReference = new AtomicReference<>();
        Thread thread = Thread.ofPlatform().name("JavaFX Application Thread").start(
                () -> formattedReference.set(formatRecord(new LogRecord(Level.INFO, "Info message")))
        );

        thread.join();

        String formattedRecord = formattedReference.get();
        assertNotNull(formattedRecord, "Formatted string should not be null");
        assertTrue(formattedRecord.contains("[INFO   ] [FX] Info message"));
    }

    @Test
    void testInitializeThrowsWhenInitializedTwice() {
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

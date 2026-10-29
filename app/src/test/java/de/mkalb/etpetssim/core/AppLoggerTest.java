package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AppLoggerTest {

    private static final String MESSAGE = "Test message";
    private static final Throwable THROWABLE = new RuntimeException("Test exception");

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static RecordingHandler initializeWithRecordingHandler(AppLogger.LogLevel logLevel) {
        AppLogger.initialize(logLevel, true, null);
        RecordingHandler handler = new RecordingHandler();
        Logger.getLogger("").addHandler(handler);
        handler.records.clear();
        return handler;
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

    private static void assertSingleRecord(RecordingHandler handler, Level level, String message,
                                           Throwable thrown) {
        assertEquals(1, handler.records.size());
        LogRecord record = handler.records.getFirst();
        assertAll(
                () -> assertEquals(level, record.getLevel()),
                () -> assertEquals(message, record.getMessage()),
                () -> assertSame(thrown, record.getThrown())
        );
    }

    @BeforeEach
    void setUpBeforeEach() {
        AppLogger.resetForTesting();
    }

    @Test
    void testInitialize() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);

        assertAll(
                () -> assertTrue(AppLogger.isInitialized()),
                () -> assertEquals(1, AppLogger.handlerCountForTesting()),
                () -> assertEquals(Level.WARNING, Logger.getLogger("").getLevel())
        );
    }

    @Test
    void testInitializeWritesLogFile(@TempDir Path tempDir) throws IOException {
        Path logPath = tempDir.resolve(AppLogger.LOG_FILE_NAME);

        AppLogger.initialize(AppLogger.LogLevel.INFO, false, logPath);
        AppLogger.info(MESSAGE);
        AppLogger.resetForTesting();

        assertTrue(Files.readString(logPath).contains(MESSAGE));
    }

    @Test
    void testInitializeDisablesLoggingWithoutHandlers(@TempDir Path tempDir) {
        Path logPath = tempDir.resolve("missing").resolve(AppLogger.LOG_FILE_NAME);

        AppLogger.initialize(AppLogger.LogLevel.INFO, false, logPath);

        assertAll(
                () -> assertTrue(AppLogger.isInitialized()),
                () -> assertEquals(0, AppLogger.handlerCountForTesting()),
                () -> assertEquals(Level.OFF, Logger.getLogger("").getLevel()),
                () -> assertFalse(Files.exists(logPath))
        );
    }

    @Test
    void testInitializeThrowsWhenInitializedTwice() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);

        assertThrows(IllegalStateException.class, () -> AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testInitializeRejectsNullLogLevel() {
        assertThrows(NullPointerException.class, () -> AppLogger.initialize(null, true, null));
    }

    @Test
    void testIsInitialized() {
        assertFalse(AppLogger.isInitialized());

        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);

        assertTrue(AppLogger.isInitialized());
    }

    @Test
    void testInitializeForTesting() {
        AppLogger.initializeForTesting();

        assertAll(
                () -> assertTrue(AppLogger.isInitialized()),
                () -> assertEquals(1, AppLogger.handlerCountForTesting()),
                () -> assertEquals(Level.FINE, Logger.getLogger("").getLevel())
        );
    }

    @Test
    void testShutdown() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);

        AppLogger.shutdown();

        assertAll(
                () -> assertFalse(AppLogger.isInitialized()),
                () -> assertEquals(0, AppLogger.handlerCountForTesting())
        );
    }

    @Test
    void testDebug() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.DEBUG);

        AppLogger.debug(MESSAGE);

        assertSingleRecord(handler, Level.FINE, MESSAGE, null);
    }

    @Test
    void testDebugIgnoresMessageBelowLogLevel() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.INFO);

        AppLogger.debug(MESSAGE);

        assertTrue(handler.records.isEmpty());
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testDebugWithSupplier() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.DEBUG);

        AppLogger.debug(() -> MESSAGE);

        assertAll(
                () -> assertSingleRecord(handler, Level.FINE, MESSAGE, null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.debug((Supplier<String>) null))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testDebugf() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.DEBUG);

        AppLogger.debugf("Test %s %d", "message", 1);

        assertAll(
                () -> assertSingleRecord(handler, Level.FINE, "Test message 1", null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.debugf(null, "x"))
        );
    }

    @Test
    void testInfo() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.INFO);

        AppLogger.info(MESSAGE);

        assertSingleRecord(handler, Level.INFO, MESSAGE, null);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testInfoWithSupplier() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.INFO);

        AppLogger.info(() -> MESSAGE);

        assertAll(
                () -> assertSingleRecord(handler, Level.INFO, MESSAGE, null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.info((Supplier<String>) null))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testInfof() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.INFO);

        AppLogger.infof("Test %s %d", "message", 2);

        assertAll(
                () -> assertSingleRecord(handler, Level.INFO, "Test message 2", null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.infof(null, "x"))
        );
    }

    @Test
    void testWarn() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.WARN);

        AppLogger.warn(MESSAGE);

        assertSingleRecord(handler, Level.WARNING, MESSAGE, null);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testWarnWithSupplier() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.WARN);

        AppLogger.warn(() -> MESSAGE);

        assertAll(
                () -> assertSingleRecord(handler, Level.WARNING, MESSAGE, null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.warn((Supplier<String>) null))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testWarnf() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.WARN);

        AppLogger.warnf("Test %s %d", "message", 3);

        assertAll(
                () -> assertSingleRecord(handler, Level.WARNING, "Test message 3", null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.warnf(null, "x"))
        );
    }

    @Test
    void testError() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.ERROR);

        AppLogger.error(MESSAGE);

        assertSingleRecord(handler, Level.SEVERE, MESSAGE, null);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testErrorWithSupplier() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.ERROR);

        AppLogger.error(() -> MESSAGE);

        assertAll(
                () -> assertSingleRecord(handler, Level.SEVERE, MESSAGE, null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.error((Supplier<String>) null))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testErrorWithThrowable() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.ERROR);

        AppLogger.error(THROWABLE, MESSAGE);

        assertAll(
                () -> assertSingleRecord(handler, Level.SEVERE, MESSAGE, THROWABLE),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.error(null, MESSAGE))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testErrorf() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.ERROR);

        AppLogger.errorf("Test %s %d", "message", 4);

        assertAll(
                () -> assertSingleRecord(handler, Level.SEVERE, "Test message 4", null),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf((String) null, "x"))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testErrorfWithThrowable() {
        RecordingHandler handler = initializeWithRecordingHandler(AppLogger.LogLevel.ERROR);

        AppLogger.errorf(THROWABLE, "Test %s %d", "message", 5);

        assertAll(
                () -> assertSingleRecord(handler, Level.SEVERE, "Test message 5", THROWABLE),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf(THROWABLE, null, "x")),
                () -> assertThrows(NullPointerException.class, () -> AppLogger.errorf((Throwable) null, "msg %s", "x"))
        );
    }

    @Test
    void testResetForTesting() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);

        AppLogger.resetForTesting();

        assertAll(
                () -> assertFalse(AppLogger.isInitialized()),
                () -> assertEquals(0, AppLogger.handlerCountForTesting()),
                () -> assertEquals(AppLogger.DEFAULT_LOG_LEVEL.toJavaLogLevel(), Logger.getLogger("").getLevel())
        );
    }

    @Test
    void testLogLevelFromString() {
        assertAll(
                () -> assertEquals(Optional.of(AppLogger.LogLevel.DEBUG), AppLogger.LogLevel.fromString("debug")),
                () -> assertEquals(Optional.of(AppLogger.LogLevel.INFO), AppLogger.LogLevel.fromString("INFO")),
                () -> assertEquals(Optional.of(AppLogger.LogLevel.WARN), AppLogger.LogLevel.fromString("Warn")),
                () -> assertEquals(Optional.of(AppLogger.LogLevel.ERROR), AppLogger.LogLevel.fromString("error")),
                () -> assertEquals(Optional.empty(), AppLogger.LogLevel.fromString("verbose"))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testLogLevelFromStringRejectsNull() {
        assertThrows(NullPointerException.class, () -> AppLogger.LogLevel.fromString(null));
    }

    @Test
    void testLogLevelToJavaLogLevel() {
        assertAll(
                () -> assertEquals(Level.FINE, AppLogger.LogLevel.DEBUG.toJavaLogLevel()),
                () -> assertEquals(Level.INFO, AppLogger.LogLevel.INFO.toJavaLogLevel()),
                () -> assertEquals(Level.WARNING, AppLogger.LogLevel.WARN.toJavaLogLevel()),
                () -> assertEquals(Level.SEVERE, AppLogger.LogLevel.ERROR.toJavaLogLevel())
        );
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

        assertTrue(formattedReference.get().contains("[INFO   ] [FX] Info message"));
    }

    private static final class RecordingHandler extends Handler {

        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

    }

}

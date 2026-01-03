package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
class AppLoggerTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
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
    void testInfoLogging() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertDoesNotThrow(() -> AppLogger.info("Info message"));
    }

    @Test
    void testWarnLogging() {
        AppLogger.initialize(AppLogger.LogLevel.WARN, true, null);
        assertDoesNotThrow(() -> AppLogger.warn("Warn message"));
    }

    @Test
    void testErrorLogging() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertDoesNotThrow(() -> AppLogger.error("Error message"));
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
        assertDoesNotThrow(() -> AppLogger.error("Error with throwable", t));
    }

    @Test
    void testInitializeTwiceThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
        assertThrows(IllegalStateException.class, () ->
                AppLogger.initialize(AppLogger.LogLevel.DEBUG, true, null));
    }

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

    @Test
    void testNullThrowableThrowsException() {
        AppLogger.initialize(AppLogger.LogLevel.ERROR, true, null);
        assertThrows(NullPointerException.class, () -> AppLogger.error("msg", null));
    }

}

package de.mkalb.etpetssim.testsupport;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class FXTestSupportTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FXTestSupport.ensureStarted();
    }

    private static void awaitRelease(CountDownLatch releaseFxThread) {
        try {
            releaseFxThread.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testEnsureStartedIsIdempotent() {
        assertDoesNotThrow(FXTestSupport::ensureStarted);
    }

    @Test
    void testSupplyAndWaitReturnsSuppliedValue() {
        assertEquals("result", FXTestSupport.supplyAndWait(() -> "result", 1));
    }

    @Test
    void testSupplyAndWaitAllowsNullResult() {
        assertNull(FXTestSupport.supplyAndWait(() -> null, 1));
    }

    @Test
    void testSupplyAndWaitPropagatesRuntimeException() {
        IllegalStateException expected = new IllegalStateException("Test exception");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> FXTestSupport.supplyAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testSupplyAndWaitPropagatesAssertionError() {
        AssertionError expected = new AssertionError("Test assertion error");

        AssertionError actual = assertThrows(
                AssertionError.class,
                () -> FXTestSupport.supplyAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testSupplyAndWaitTimesOutWhenFxThreadIsBlocked() throws InterruptedException {
        CountDownLatch fxThreadBlocked = new CountDownLatch(1);
        CountDownLatch releaseFxThread = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            fxThreadBlocked.countDown();
            awaitRelease(releaseFxThread);
        });
        assertTrue(fxThreadBlocked.await(FXTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> FXTestSupport.supplyAndWait(() -> "result", 0));

            assertEquals("Timed out waiting for JavaFX action completion", exception.getMessage());
        } finally {
            releaseFxThread.countDown();
        }
        FXTestSupport.runAndWait(() -> {});
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testSupplyAndWaitRestoresInterruptedStatus() {
        Thread testThread = Thread.currentThread();
        javafx.application.Platform.runLater(testThread::interrupt);

        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> FXTestSupport.supplyAndWait(() -> "result", 1));

            assertEquals("Interrupted while waiting on JavaFX thread", exception.getMessage());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
        FXTestSupport.runAndWait(() -> {});
    }

    @Test
    void testSupplyAndWaitUsesDefaultTimeout() {
        assertEquals("result", FXTestSupport.supplyAndWait(() -> "result"));
    }

    @Test
    void testSupplyAndWaitReturnsSuppliedValueOnFxThread() {
        FXTestSupport.runAndWait(() -> assertEquals("result", FXTestSupport.supplyAndWait(() -> "result", 1)));
    }

    @Test
    void testSupplyAndWaitAllowsNullResultOnFxThread() {
        FXTestSupport.runAndWait(() -> assertNull(FXTestSupport.supplyAndWait(() -> null, 1)));
    }

    @Test
    void testSupplyAndWaitNonNullReturnsSuppliedValue() {
        assertEquals("result", FXTestSupport.supplyAndWaitNonNull(() -> "result"));
    }

    @Test
    void testSupplyAndWaitNonNullRejectsNullResult() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> FXTestSupport.supplyAndWaitNonNull(() -> null));

        assertEquals("JavaFX supplier returned null", exception.getMessage());
    }

    @Test
    void testSupplyAndWaitNonNullReturnsSuppliedValueOnFxThread() {
        FXTestSupport.runAndWait(() ->
                assertEquals("result", FXTestSupport.supplyAndWaitNonNull(() -> "result")));
    }

    @Test
    void testSupplyAndWaitNonNullRejectsNullResultOnFxThread() {
        FXTestSupport.runAndWait(() -> {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> FXTestSupport.supplyAndWaitNonNull(() -> null));

            assertEquals("JavaFX supplier returned null", exception.getMessage());
        });
    }

    @Test
    void testRunAndWaitRunsActionOnFxThread() {
        AtomicBoolean runsOnFxThread = new AtomicBoolean();

        FXTestSupport.runAndWait(() -> runsOnFxThread.set(javafx.application.Platform.isFxApplicationThread()), 1);

        assertTrue(runsOnFxThread.get());
    }

    @Test
    void testRunAndWaitRunsActionWhenAlreadyOnFxThread() {
        AtomicBoolean nestedActionRunsOnFxThread = new AtomicBoolean();

        FXTestSupport.runAndWait(() ->
                FXTestSupport.runAndWait(() ->
                        nestedActionRunsOnFxThread.set(javafx.application.Platform.isFxApplicationThread())));

        assertTrue(nestedActionRunsOnFxThread.get());
    }

    @Test
    void testRunAndWaitPropagatesRuntimeException() {
        IllegalStateException expected = new IllegalStateException("Test exception");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> FXTestSupport.runAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testRunAndWaitPropagatesAssertionError() {
        AssertionError expected = new AssertionError("Test assertion error");

        AssertionError actual = assertThrows(
                AssertionError.class,
                () -> FXTestSupport.runAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testRunAndWaitUsesDefaultTimeout() {
        AtomicBoolean runsOnFxThread = new AtomicBoolean();

        FXTestSupport.runAndWait(() -> runsOnFxThread.set(javafx.application.Platform.isFxApplicationThread()));

        assertTrue(runsOnFxThread.get());
    }

}

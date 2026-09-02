package de.mkalb;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class FxTestSupportTest {

    @BeforeAll
    static void setUpBeforeAll() {
        FxTestSupport.ensureStarted();
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
        assertDoesNotThrow(FxTestSupport::ensureStarted);
    }

    @Test
    void testSupplyAndWaitReturnsSuppliedValue() {
        assertEquals("result", FxTestSupport.supplyAndWait(() -> "result", 1));
    }

    @Test
    void testSupplyAndWaitAllowsNullResult() {
        assertNull(FxTestSupport.supplyAndWait(() -> null, 1));
    }

    @Test
    void testSupplyAndWaitPropagatesRuntimeException() {
        IllegalStateException expected = new IllegalStateException("Test exception");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> FxTestSupport.supplyAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testSupplyAndWaitPropagatesAssertionError() {
        AssertionError expected = new AssertionError("Test assertion error");

        AssertionError actual = assertThrows(
                AssertionError.class,
                () -> FxTestSupport.supplyAndWait(() -> {
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
        assertTrue(fxThreadBlocked.await(FxTestSupport.DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS));

        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> FxTestSupport.supplyAndWait(() -> "result", 0));

            assertEquals("Timed out waiting for JavaFX action completion", exception.getMessage());
        } finally {
            releaseFxThread.countDown();
        }
        FxTestSupport.runAndWait(() -> {});
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void testSupplyAndWaitRestoresInterruptedStatus() {
        Thread testThread = Thread.currentThread();
        javafx.application.Platform.runLater(testThread::interrupt);

        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> FxTestSupport.supplyAndWait(() -> "result", 1));

            assertEquals("Interrupted while waiting on JavaFX thread", exception.getMessage());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
        FxTestSupport.runAndWait(() -> {});
    }

    @Test
    void testSupplyAndWaitUsesDefaultTimeout() {
        assertEquals("result", FxTestSupport.supplyAndWait(() -> "result"));
    }

    @Test
    void testSupplyAndWaitReturnsSuppliedValueOnFxThread() {
        FxTestSupport.runAndWait(() -> assertEquals("result", FxTestSupport.supplyAndWait(() -> "result", 1)));
    }

    @Test
    void testSupplyAndWaitAllowsNullResultOnFxThread() {
        FxTestSupport.runAndWait(() -> assertNull(FxTestSupport.supplyAndWait(() -> null, 1)));
    }

    @Test
    void testSupplyAndWaitNonNullReturnsSuppliedValue() {
        assertEquals("result", FxTestSupport.supplyAndWaitNonNull(() -> "result"));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testSupplyAndWaitNonNullRejectsNullResult() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> FxTestSupport.supplyAndWaitNonNull(() -> null));

        assertEquals("JavaFX supplier returned null", exception.getMessage());
    }

    @Test
    void testSupplyAndWaitNonNullReturnsSuppliedValueOnFxThread() {
        FxTestSupport.runAndWait(() ->
                assertEquals("result", FxTestSupport.supplyAndWaitNonNull(() -> "result")));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void testSupplyAndWaitNonNullRejectsNullResultOnFxThread() {
        FxTestSupport.runAndWait(() -> {
            NullPointerException exception = assertThrows(
                    NullPointerException.class,
                    () -> FxTestSupport.supplyAndWaitNonNull(() -> null));

            assertEquals("JavaFX supplier returned null", exception.getMessage());
        });
    }

    @Test
    void testRunAndWaitRunsActionOnFxThread() {
        AtomicBoolean runsOnFxThread = new AtomicBoolean();

        FxTestSupport.runAndWait(() -> runsOnFxThread.set(javafx.application.Platform.isFxApplicationThread()), 1);

        assertTrue(runsOnFxThread.get());
    }

    @Test
    void testRunAndWaitRunsActionWhenAlreadyOnFxThread() {
        AtomicBoolean nestedActionRunsOnFxThread = new AtomicBoolean();

        FxTestSupport.runAndWait(() ->
                FxTestSupport.runAndWait(() ->
                        nestedActionRunsOnFxThread.set(javafx.application.Platform.isFxApplicationThread())));

        assertTrue(nestedActionRunsOnFxThread.get());
    }

    @Test
    void testRunAndWaitPropagatesRuntimeException() {
        IllegalStateException expected = new IllegalStateException("Test exception");

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> FxTestSupport.runAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testRunAndWaitPropagatesAssertionError() {
        AssertionError expected = new AssertionError("Test assertion error");

        AssertionError actual = assertThrows(
                AssertionError.class,
                () -> FxTestSupport.runAndWait(() -> {
                    throw expected;
                }, 1));

        assertSame(expected, actual);
    }

    @Test
    void testRunAndWaitUsesDefaultTimeout() {
        AtomicBoolean runsOnFxThread = new AtomicBoolean();

        FxTestSupport.runAndWait(() -> runsOnFxThread.set(javafx.application.Platform.isFxApplicationThread()));

        assertTrue(runsOnFxThread.get());
    }

}

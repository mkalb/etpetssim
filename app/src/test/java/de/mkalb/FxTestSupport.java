package de.mkalb;

import javafx.application.Platform;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * Utility class for JavaFX testing support.
 * <p>
 * This class provides methods to ensure that the JavaFX platform is started
 * and to run tasks on the JavaFX Application Thread, waiting for their completion.
 * <p>
 * It is designed to be used in test environments where JavaFX components need to be
 * manipulated or tested.
 */
public final class FxTestSupport {

    /**
     * Default timeout in seconds for JavaFX operations.
     */
    public static final long DEFAULT_TIMEOUT_SECONDS = 10;

    @SuppressWarnings("FieldNamingConvention")
    private static volatile boolean started = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private FxTestSupport() {
    }

    /**
     * Ensures that the JavaFX platform is started.
     * If the platform is not already started, this method will initialize it
     * and wait for the initialization to complete.
     *
     * @see #DEFAULT_TIMEOUT_SECONDS
     */
    public static synchronized void ensureStarted() {
        if (!started) {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            try {
                if (!latch.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting for JavaFX initialization");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("FX start interrupted", e);
            }
            started = true;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void awaitLatch(CountDownLatch latch, long timeoutSeconds, String timeoutMessage) {
        try {
            if (!latch.await(timeoutSeconds, TimeUnit.SECONDS)) {
                throw new IllegalStateException(timeoutMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting on JavaFX thread", e);
        }
    }

    /**
     * Re-throws a throwable that was captured on the JavaFX Application Thread.
     * <p>
     * {@link RuntimeException} and {@link Error} (including {@link AssertionError}) are re-thrown
     * unchanged so that JUnit assertions made on the JavaFX thread fail the test as expected.
     * Any other throwable is wrapped in an {@link IllegalStateException}.
     *
     * @param thrown the captured throwable, or {@code null} if none occurred
     */
    private static void rethrowIfPresent(@Nullable Throwable thrown) {
        switch (thrown) {
            case null -> {
                // No throwable captured: nothing to re-throw.
            }
            case RuntimeException runtimeException -> throw runtimeException;
            case Error error -> throw error;
            default -> throw new IllegalStateException("Exception on JavaFX application thread", thrown);
        }
    }

    /**
     * Runs the given Supplier on the JavaFX Application Thread, waits for its completion, and returns its result.
     * <p>
     * If the supplier throws, the throwable is captured on the JavaFX thread and re-thrown on the calling thread.
     * This ensures that assertions and exceptions on the JavaFX thread are not silently swallowed.
     *
     * @param supplier       the Supplier to execute
     * @param timeoutSeconds the maximum time to wait in seconds
     * @param <T>            the type of the supplied result
     * @return the result produced by the supplier
     */
    public static <T extends @Nullable Object> T supplyAndWait(Supplier<T> supplier, long timeoutSeconds) {
        if (Platform.isFxApplicationThread()) {
            return supplier.get();
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> resultRef = new AtomicReference<>();
        AtomicReference<@Nullable Throwable> throwableRef = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                resultRef.set(supplier.get());
            } catch (Throwable t) { // Capture AssertionError and any other throwable from the FX thread.
                throwableRef.set(t);
            } finally {
                latch.countDown();
            }
        });
        awaitLatch(latch, timeoutSeconds, "Timed out waiting for JavaFX action completion");
        rethrowIfPresent(throwableRef.get());
        return resultRef.get();
    }

    /**
     * Runs the given Supplier on the JavaFX Application Thread, waits for its completion, and returns its result.
     * Uses a default timeout.
     *
     * @param supplier the Supplier to execute
     * @param <T>      the type of the supplied result
     * @return the result produced by the supplier
     * @see #supplyAndWait(Supplier, long)
     * @see #DEFAULT_TIMEOUT_SECONDS
     */
    public static <T extends @Nullable Object> T supplyAndWait(Supplier<T> supplier) {
        return supplyAndWait(supplier, DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Runs the given Runnable on the JavaFX Application Thread and waits for its completion.
     * <p>
     * If the runnable throws, the throwable is captured on the JavaFX thread and re-thrown on the calling thread.
     * This ensures that assertions and exceptions on the JavaFX thread are not silently swallowed.
     *
     * @param r              the Runnable to execute
     * @param timeoutSeconds the maximum time to wait in seconds
     */
    public static void runAndWait(Runnable r, long timeoutSeconds) {
        supplyAndWait(() -> {
            r.run();
            return null;
        }, timeoutSeconds);
    }

    /**
     * Runs the given Runnable on the JavaFX Application Thread and waits for its completion.
     * Uses a default timeout.
     *
     * @param r the Runnable to execute
     * @see #runAndWait(Runnable, long)
     * @see #DEFAULT_TIMEOUT_SECONDS
     */
    public static void runAndWait(Runnable r) {
        runAndWait(r, DEFAULT_TIMEOUT_SECONDS);
    }

}

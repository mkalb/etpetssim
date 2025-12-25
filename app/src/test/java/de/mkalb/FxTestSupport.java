package de.mkalb;

import javafx.application.Platform;

import java.util.concurrent.*;

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
     * Runs the given Runnable on the JavaFX Application Thread and waits for its completion.
     * @param r the Runnable to execute
     * @param timeoutSeconds the maximum time to wait in seconds
     */
    public static void runAndWait(Runnable r, long timeoutSeconds) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                try {
                    r.run();
                } finally {
                    latch.countDown();
                }
            });
            awaitLatch(latch, timeoutSeconds, "Timed out waiting for JavaFX action completion");
        }
    }

    /**
     * Runs the given Runnable on the JavaFX Application Thread and waits for its completion.
     * Uses a default timeout.
     * @param r the Runnable to execute
     * @see #runAndWait(Runnable, long)
     * @see #DEFAULT_TIMEOUT_SECONDS
     */
    public static void runAndWait(Runnable r) {
        runAndWait(r, DEFAULT_TIMEOUT_SECONDS);
    }

}


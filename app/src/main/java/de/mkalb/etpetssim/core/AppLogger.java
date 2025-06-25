package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

/**
 * The AppLogger class is a singleton logger utility for the application.
 * It allows logging messages at different levels (debug, info, warn, error) and can log to both console and a file.
 */
public final class AppLogger {

    /**
     * Enum representing the log levels supported by the AppLogger.
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    /**
     * Singleton instance of the AppLogger.
     */
    private static final AppLogger APP_LOGGER = new AppLogger();

    /**
     * The root logger for the application.
     */
    private final Logger logger;
    private boolean initialized;

    private AppLogger() {
        initialized = false;
        // Root logger for the application
        logger = Logger.getLogger("");
        // Use INFO level for the logger by default
        logger.setLevel(Level.INFO);
    }

    /**
     * Initializes the AppLogger with the specified log level, console output, and log file path.
     * This method must be called exactly once before any logging methods are used.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null or empty, no file logging will be done
     * @throws IllegalStateException if the logger is already initialized.
     */
    private synchronized void initializeLogger(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        if (initialized) {
            throw new IllegalStateException("AppLogger is already initialized.");
        }
        Objects.requireNonNull(logLevel, "Log level must not be null");

        APP_LOGGER.logger.setUseParentHandlers(false);

        // Map log levels to java.util.logging.Level
        Level level = switch (logLevel) {
            case DEBUG -> Level.FINE;
            case INFO -> Level.INFO;
            case WARN -> Level.WARNING;
            case ERROR -> Level.SEVERE;
        };

        // Set the log level for the logger
        APP_LOGGER.logger.setLevel(level);

        List<StreamHandler> logHandlers = new ArrayList<>();

        // ConsoleHandler for console output
        if (useConsole) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(level);
            logHandlers.add(consoleHandler);
            APP_LOGGER.logger.info("ConsoleHandler initialized with level: " + level);
        }

        // FileHandler for file output
        if ((logPath != null) && !logPath.isBlank()) {
            try {
                Path path = Paths.get(logPath);
                Path parent = path.getParent();
                if ((parent != null) && Files.exists(parent) && Files.isDirectory(parent) && Files.isWritable(parent)) {
                    Files.deleteIfExists(path);
                    FileHandler fileHandler = new FileHandler(logPath, false);
                    fileHandler.setFormatter(new SimpleFormatter());
                    fileHandler.setLevel(level);
                    logHandlers.add(fileHandler);
                    APP_LOGGER.logger.info("FileHandler initialized with path: " + logPath + " and level: " + level);
                } else {
                    APP_LOGGER.logger.severe("Parent directory for log file does not exist or is not writable: " + path);
                }
            } catch (IOException e) {
                APP_LOGGER.logger.log(Level.SEVERE, "Failed to create log file handler! " + logPath, e);
            }
        }

        if (logHandlers.isEmpty()) {
            APP_LOGGER.logger.warning("No logging handlers were created.");
        } else {
            // Remove all existing handlers from the logger before adding new ones
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                APP_LOGGER.logger.removeHandler(handler);
            }

            // Add all new handlers to the logger
            logHandlers.forEach(APP_LOGGER.logger::addHandler);
        }
        initialized = true;
    }

    /**
     * Initializes the AppLogger singleton instance with the specified log level, console output, and log file path.
     * This method must be called exactly once before any logging methods are used.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null or empty, no file logging will be done
     * @throws IllegalStateException if the logger is already initialized.
     */
    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        APP_LOGGER.initializeLogger(logLevel, useConsole, logPath);
        info("AppLogger initialized with level: " + logLevel + ", console: " + useConsole + ", logPath: " + logPath);
    }

    /**
     * Checks if the AppLogger singleton instance has been initialized.
     * @return true if the logger is initialized, false otherwise
     */
    public static synchronized boolean isInitialized() {
        return APP_LOGGER.initialized;
    }

    /**
     * Initializes the AppLogger for testing purposes.
     * This method sets the log level to DEBUG and enables console output.
     * It initializes the logger only if it has not been initialized yet.
     */
    public static synchronized void initializeForTesting() {
        if (!isInitialized()) {
            APP_LOGGER.initializeLogger(LogLevel.DEBUG, true, null);
            debug("AppLogger initialized for testing with DEBUG level and console output.");
        }
    }

    /**
     * Resets the AppLogger singleton instance for testing purposes.
     * This method sets the logger to its default state, allowing for re-initialization in tests.
     * It resets the logger only if it has been initialized.
     */
    static synchronized void resetForTesting() {
        if (isInitialized()) {
            debug("AppLogger reset for testing.");
            APP_LOGGER.initialized = false;
            APP_LOGGER.logger.setLevel(Level.INFO);
            APP_LOGGER.logger.setUseParentHandlers(true);
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                APP_LOGGER.logger.removeHandler(handler);
            }
        }
    }

    /**
     * Returns the number of handlers currently attached to the AppLogger for testing purposes.
     * @return the number of handlers attached to the logger
     */
    static synchronized int numberOfHandlersForTesting() {
        return APP_LOGGER.logger.getHandlers().length;
    }

    /**
     * Logs a debug message.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param message the message to log, can be null
     */
    public static void debug(@Nullable String message) {
        APP_LOGGER.logger.fine(message);
    }

    /**
     * Logs a debug message using a Supplier.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param messageSupplier a Supplier that provides the message to log
     */
    public static void debug(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.fine(messageSupplier);
    }

    /**
     * Logs an informational message.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param message the message to log, can be null
     */
    public static void info(@Nullable String message) {
        APP_LOGGER.logger.info(message);
    }

    /**
     * Logs an informational message using a Supplier.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param messageSupplier a Supplier that provides the message to log
     */
    public static void info(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.info(messageSupplier);
    }

    /**
     * Logs a warn message.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param message the message to log, can be null
     */
    public static void warn(@Nullable String message) {
        APP_LOGGER.logger.warning(message);
    }

    /**
     * Logs a warn message using a Supplier.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param messageSupplier a Supplier that provides the message to log
     */
    public static void warn(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.warning(messageSupplier);
    }

    /**
     * Logs an error message.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param message the message to log, can be null
     */
    public static void error(@Nullable String message) {
        APP_LOGGER.logger.severe(message);
    }

    /**
     * Logs an error message using a Supplier.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param messageSupplier a Supplier that provides the message to log
     */
    public static void error(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.severe(messageSupplier);
    }

    /**
     * Logs an error message with an associated Throwable.
     * If the logger has not been initialized, the default root logger configuration is used.
     *
     * @param message   the message to log, can be null
     * @param throwable the Throwable to log
     */
    public static void error(@Nullable String message, Throwable throwable) {
        Objects.requireNonNull(throwable, "Throwable must not be null");
        APP_LOGGER.logger.log(Level.SEVERE, message, throwable);
    }

}

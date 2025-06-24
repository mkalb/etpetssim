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
 * It allows logging messages at different levels (info, warning, error) and can log to both console and a file.
 */
public final class AppLogger {

    /**
     * Enum representing the log levels supported by the AppLogger.
     */
    public enum LogLevel {
        INFO, WARNING, ERROR
    }

    private static @Nullable AppLogger instance;

    private final Logger logger;

    /**
     * Private constructor for AppLogger.
     * Initializes the logger with the specified log level, console output option, and log file path.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null or empty, no file logging will be done
     */
    private AppLogger(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        Objects.requireNonNull(logLevel, "Log level must not be null");

        // Root logger for the application
        logger = Logger.getLogger("");

        // Map log levels to java.util.logging.Level
        Level level = switch (logLevel) {
            case INFO -> Level.INFO;
            case WARNING -> Level.WARNING;
            case ERROR -> Level.SEVERE;
        };

        // Set the log level for the logger
        logger.setLevel(level);

        List<StreamHandler> logHandlers = new ArrayList<>();

        // ConsoleHandler for console output
        if (useConsole) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(level);
            logHandlers.add(consoleHandler);
            logger.info("ConsoleHandler initialized with level: " + level);
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
                    logger.info("FileHandler initialized with path: " + logPath + " and level: " + level);
                } else {
                    logger.severe("Parent directory for log file does not exist or is not writable: " + path);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create log file handler! " + logPath, e);
            }
        }

        if (logHandlers.isEmpty()) {
            logger.warning("No logging handlers were created.");
        } else {
            // Remove all existing handlers from the logger before adding new ones
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            // Add all new handlers to the logger
            logHandlers.forEach(logger::addHandler);
        }
    }

    /**
     * Initializes the AppLogger singleton instance.
     * This method must be called before any logging methods are used.
     * It must be called exactly once.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null or empty, no file logging will be done
     * @throws IllegalStateException if the logger is already initialized.
     */
    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        if (instance != null) {
            throw new IllegalStateException("AppLogger instance is already initialized.");
        }
        Objects.requireNonNull(logLevel, "Log level must not be null");
        instance = new AppLogger(logLevel, useConsole, logPath);
    }

    /**
     * Logs an informational message.
     *
     * @param message the message to log, can be null
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void info(@Nullable String message) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        instance.logger.info(message);
    }

    /**
     * Logs an informational message using a Supplier.
     *
     * @param messageSupplier a Supplier that provides the message to log
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void info(Supplier<@Nullable String> messageSupplier) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.info(messageSupplier);
    }

    /**
     * Logs a warning message.
     *
     * @param message the message to log, can be null
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void warning(@Nullable String message) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        instance.logger.warning(message);
    }

    /**
     * Logs a warning message using a Supplier.
     *
     * @param messageSupplier a Supplier that provides the message to log
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void warning(Supplier<@Nullable String> messageSupplier) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.warning(messageSupplier);
    }

    /**
     * Logs an error message.
     *
     * @param message the message to log, can be null
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void error(@Nullable String message) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        instance.logger.severe(message);
    }

    /**
     * Logs an error message using a Supplier.
     *
     * @param messageSupplier a Supplier that provides the message to log
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void error(Supplier<@Nullable String> messageSupplier) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.severe(messageSupplier);
    }

    /**
     * Logs an error message with an associated Throwable.
     *
     * @param message   the message to log, can be null
     * @param throwable the Throwable to log, can be null
     * @throws IllegalStateException if the logger is not initialized
     */
    public static void error(@Nullable String message, @Nullable Throwable throwable) {
        if (instance == null) {
            throw new IllegalStateException("AppLogger instance is not initialized.");
        }
        instance.logger.log(Level.SEVERE, message, throwable);
    }

}

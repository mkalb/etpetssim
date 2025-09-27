package de.mkalb.etpetssim.core;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * The AppLogger class is a singleton logger utility for the application.
 * It allows logging messages at different levels (debug, info, warn, error) and can log to both console and a file.
 */
public final class AppLogger {

    /**
     * The name of the log file used by the AppLogger.
     */
    public static final String LOG_FILE_NAME = "ExtraterrestrialPetsSimulation.log";
    /**
     * The default log level for the AppLogger.
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;
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
        // Root logger for the application.
        logger = Logger.getLogger("");
        // Set the default log level.
        logger.setLevel(DEFAULT_LOG_LEVEL.toJavaLogLevel());
    }

    /**
     * Initializes the AppLogger singleton instance with the specified log level, console output, and log file path.
     * This method must be called exactly once before any logging methods are used.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null, no file logging will be done
     * @throws IllegalStateException if the logger is already initialized.
     */
    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        APP_LOGGER.initializeLogger(logLevel, useConsole, logPath);
        info("AppLogger: Logger initialized with level: " + logLevel +
                ", console output: " + useConsole +
                ", log file: " + (logPath != null ? logPath.toString() : "none"));
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
            debug("AppLogger: Logger initialized for testing with level: DEBUG and console output.");
        }
    }

    /**
     * Resets the AppLogger singleton instance for testing purposes.
     * This method sets the logger to its default state, allowing for re-initialization in tests.
     * It resets the logger only if it has been initialized.
     */
    static synchronized void resetForTesting() {
        if (isInitialized()) {
            debug("AppLogger: Reset logger for testing.");
            APP_LOGGER.initialized = false;
            APP_LOGGER.logger.setLevel(DEFAULT_LOG_LEVEL.toJavaLogLevel());
            APP_LOGGER.logger.setUseParentHandlers(true);
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                APP_LOGGER.logger.removeHandler(handler);
            }
        }
    }

    /**
     * Shuts down the AppLogger singleton instance.
     * This method closes all handlers and removes them from the logger.
     */
    public static synchronized void shutdown() {
        debug("AppLogger: Shutting down logger.");
        APP_LOGGER.initialized = false;
        for (Handler handler : APP_LOGGER.logger.getHandlers()) {
            handler.close();
            APP_LOGGER.logger.removeHandler(handler);
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

    /**
     * Initializes the AppLogger with the specified log level, console output, and log file path.
     * This method must be called exactly once before any logging methods are used.
     *
     * @param logLevel   the log level to set for the logger
     * @param useConsole if true, logs will also be printed to the console
     * @param logPath    the path to the log file; if null, no file logging will be done
     * @throws IllegalStateException if the logger is already initialized.
     */
    private synchronized void initializeLogger(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        if (initialized) {
            throw new IllegalStateException("AppLogger is already initialized.");
        }
        Objects.requireNonNull(logLevel, "Log level must not be null");

        APP_LOGGER.logger.setUseParentHandlers(false);

        Level level = logLevel.toJavaLogLevel();

        // Set the log level for the logger.
        APP_LOGGER.logger.setLevel(level);

        List<StreamHandler> logHandlers = new ArrayList<>();

        // ConsoleHandler for console output.
        if (useConsole) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new AppLogFormatter());
            try {
                consoleHandler.setEncoding(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                APP_LOGGER.logger.log(Level.SEVERE, "AppLogger: Failed to set encoding for console handler", e);
            }
            consoleHandler.setLevel(level);
            logHandlers.add(consoleHandler);
        }

        // FileHandler for file output.
        if (logPath != null) {
            try {
                Path parent = logPath.getParent();
                if ((parent != null) && Files.exists(parent) && Files.isDirectory(parent) && Files.isWritable(parent)) {
                    FileHandler fileHandler = new FileHandler(logPath.toString(), false);
                    fileHandler.setFormatter(new AppLogFormatter());
                    fileHandler.setEncoding(StandardCharsets.UTF_8.name());
                    fileHandler.setLevel(level);
                    logHandlers.add(fileHandler);
                } else {
                    APP_LOGGER.logger.severe("AppLogger: Parent directory for log file does not exist or is not writable: " + logPath);
                }
            } catch (IOException e) {
                APP_LOGGER.logger.log(Level.SEVERE, "AppLogger: Failed to create log file handler: " + logPath, e);
            }
        }

        if (logHandlers.isEmpty()) {
            APP_LOGGER.logger.warning("AppLogger: No logging handlers were created.");

            // Remove all existing handlers.
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                APP_LOGGER.logger.removeHandler(handler);
            }

            // Disable all logging.
            APP_LOGGER.logger.setLevel(Level.OFF);
        } else {
            // Remove all existing handlers from the logger before adding new ones.
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                APP_LOGGER.logger.removeHandler(handler);
            }

            // Add all new handlers to the logger.
            logHandlers.forEach(APP_LOGGER.logger::addHandler);
            if (logLevel == LogLevel.DEBUG) {
                logHandlers.forEach(handler -> debug("AppLogger: Added handler: " + handler.getClass().getSimpleName()));
            }

        }
        initialized = true;
    }

    /**
     * Enum representing the log levels supported by the AppLogger.
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR;

        /**
         * Converts a string representation of a log level to the corresponding LogLevel enum.
         * @param level the string representation of the log level, case-insensitive
         * @return an Optional containing the LogLevel if the string is valid, or an empty Optional if not
         */
        public static Optional<LogLevel> fromString(String level) {
            try {
                return Optional.of(LogLevel.valueOf(level.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        /**
         * Converts this LogLevel to the corresponding java.util.logging.Level.
         * @return the java.util.logging.Level corresponding to this LogLevel
         */
        Level toJavaLogLevel() {
            return switch (this) {
                case DEBUG -> Level.FINE;
                case INFO -> Level.INFO;
                case WARN -> Level.WARNING;
                case ERROR -> Level.SEVERE;
            };
        }

    }

    /**
     * Formatter for log records in the AppLogger.
     * This formatter formats log records with a timestamp, log level, message, and exception stack trace (if any).
     */
    private static class AppLogFormatter extends Formatter {

        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        private static final int BUILDER_CAPACITY = 128; // Initial capacity for the StringBuilder used in formatting.
        private static final int LEVEL_PADDING = 7; // Padding for log level alignment.

        @SuppressWarnings("StringConcatenationMissingWhitespace")
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(BUILDER_CAPACITY);

            // Time.
            String time = TIME_FORMATTER.format(LocalTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()));
            sb.append("[").append(time).append("] ");

            // Log level.
            sb.append("[").append(String.format("%-" + LEVEL_PADDING + "s", record.getLevel().getName())).append("] ");

            // Message.
            sb.append(formatMessage(record)).append(System.lineSeparator());

            // Exception (if any).
            if (record.getThrown() != null) {
                var throwable = record.getThrown();
                sb.append(throwable.toString()).append(System.lineSeparator());
                for (StackTraceElement element : throwable.getStackTrace()) {
                    sb.append("\tat ").append(element).append(System.lineSeparator());
                }
            }

            return sb.toString();
        }

    }

}

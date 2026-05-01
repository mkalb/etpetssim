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
 * Application-wide logging facade built on {@link java.util.logging}.
 * <p>
 * Configure once via {@link #initialize(LogLevel, boolean, Path)} and then use the
 * static logging methods throughout the application.
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
     * Initializes logging.
     *
     * @param logLevel the minimum enabled log level
     * @param useConsole whether to attach a console handler
     * @param logPath optional file path for file logging; {@code null} disables file logging
     * @throws NullPointerException if {@code logLevel} is {@code null}
     * @throws IllegalStateException if logging is already initialized
     */
    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        APP_LOGGER.initializeLogger(logLevel, useConsole, logPath);
        info("AppLogger: Logger initialized with level: " + logLevel +
                ", console output: " + useConsole +
                ", log file: " + (logPath != null ? logPath.toString() : "none"));
    }

    /**
     * Returns whether logging has been initialized.
     *
     * @return {@code true} if initialized
     */
    public static synchronized boolean isInitialized() {
        return APP_LOGGER.initialized;
    }

    /**
     * Initializes logging for tests if not already initialized.
     */
    public static synchronized void initializeForTesting() {
        if (!isInitialized()) {
            APP_LOGGER.initializeLogger(LogLevel.DEBUG, true, null);
            debug("AppLogger: Logger initialized for testing with level: DEBUG and console output.");
        }
    }

    /**
     * Resets logger state for tests.
     */
    static synchronized void resetForTesting() {
        if (isInitialized()) {
            debug("AppLogger: Reset logger for testing.");
            APP_LOGGER.initialized = false;
            APP_LOGGER.logger.setLevel(DEFAULT_LOG_LEVEL.toJavaLogLevel());
            APP_LOGGER.logger.setUseParentHandlers(true);
            for (Handler handler : APP_LOGGER.logger.getHandlers()) {
                handler.close();
                APP_LOGGER.logger.removeHandler(handler);
            }
        }
    }

    /**
     * Shuts down logging and releases all handlers.
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
     * Returns the number of attached handlers (test utility).
     *
     * @return number of active handlers
     */
    static synchronized int numberOfHandlersForTesting() {
        return APP_LOGGER.logger.getHandlers().length;
    }

    /**
     * Logs a debug message.
     *
     * @param message message text, may be {@code null}
     */
    public static void debug(@Nullable String message) {
        APP_LOGGER.logger.fine(message);
    }

    /**
     * Logs a lazily-evaluated debug message.
     *
     * @param messageSupplier supplier producing the message text
     * @throws NullPointerException if {@code messageSupplier} is {@code null}
     */
    public static void debug(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.fine(messageSupplier);
    }

    /**
     * Logs a formatted debug message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args format arguments
     * @throws NullPointerException if {@code format} is {@code null}
     */
    public static void debugf(String format, Object... args) {
        Objects.requireNonNull(format, "Format string must not be null");
        APP_LOGGER.logger.log(Level.FINE, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Logs an informational message.
     *
     * @param message message text, may be {@code null}
     */
    public static void info(@Nullable String message) {
        APP_LOGGER.logger.info(message);
    }

    /**
     * Logs a lazily-evaluated informational message.
     *
     * @param messageSupplier supplier producing the message text
     * @throws NullPointerException if {@code messageSupplier} is {@code null}
     */
    public static void info(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.info(messageSupplier);
    }

    /**
     * Logs a formatted informational message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args format arguments
     * @throws NullPointerException if {@code format} is {@code null}
     */
    public static void infof(String format, Object... args) {
        Objects.requireNonNull(format, "Format string must not be null");
        APP_LOGGER.logger.log(Level.INFO, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Logs a warning message.
     *
     * @param message message text, may be {@code null}
     */
    public static void warn(@Nullable String message) {
        APP_LOGGER.logger.warning(message);
    }

    /**
     * Logs a lazily-evaluated warning message.
     *
     * @param messageSupplier supplier producing the message text
     * @throws NullPointerException if {@code messageSupplier} is {@code null}
     */
    public static void warn(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.warning(messageSupplier);
    }

    /**
     * Logs a formatted warning message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args format arguments
     * @throws NullPointerException if {@code format} is {@code null}
     */
    public static void warnf(String format, Object... args) {
        Objects.requireNonNull(format, "Format string must not be null");
        APP_LOGGER.logger.log(Level.WARNING, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Logs an error message.
     *
     * @param message message text, may be {@code null}
     */
    public static void error(@Nullable String message) {
        APP_LOGGER.logger.severe(message);
    }

    /**
     * Logs a lazily-evaluated error message.
     *
     * @param messageSupplier supplier producing the message text
     * @throws NullPointerException if {@code messageSupplier} is {@code null}
     */
    public static void error(Supplier<String> messageSupplier) {
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        APP_LOGGER.logger.severe(messageSupplier);
    }

    /**
     * Logs a formatted error message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args format arguments
     * @throws NullPointerException if {@code format} is {@code null}
     */
    public static void errorf(String format, Object... args) {
        Objects.requireNonNull(format, "Format string must not be null");
        APP_LOGGER.logger.log(Level.SEVERE, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Logs an error message with an exception.
     *
     * @param message message text, may be {@code null}
     * @param throwable exception to attach
     * @throws NullPointerException if {@code throwable} is {@code null}
     */
    public static void error(@Nullable String message, Throwable throwable) {
        Objects.requireNonNull(throwable, "Throwable must not be null");
        APP_LOGGER.logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * Internal initialization implementation.
     *
     * @param logLevel the minimum enabled log level
     * @param useConsole whether to attach a console handler
     * @param logPath optional file path for file logging
     * @throws NullPointerException if {@code logLevel} is {@code null}
     * @throws IllegalStateException if logging is already initialized
     */
    private synchronized void initializeLogger(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        if (initialized) {
            throw new IllegalStateException("AppLogger is already initialized.");
        }
        Objects.requireNonNull(logLevel, "Log level must not be null");

        logger.setUseParentHandlers(false);

        Level level = logLevel.toJavaLogLevel();

        // Set the log level for the logger.
        logger.setLevel(level);

        List<Handler> logHandlers = new ArrayList<>();

        // ConsoleHandler for console output.
        if (useConsole) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new AppLogFormatter());
            try {
                consoleHandler.setEncoding(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                logger.log(Level.SEVERE, "AppLogger: Failed to set encoding for console handler", e);
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
                    logger.severe("AppLogger: Parent directory for log file does not exist or is not writable: " + logPath);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "AppLogger: Failed to create log file handler: " + logPath, e);
            }
        }

        if (logHandlers.isEmpty()) {
            logger.warning("AppLogger: No logging handlers were created.");

            // Remove all existing handlers.
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            // Disable all logging.
            logger.setLevel(Level.OFF);
        } else {
            // Remove all existing handlers from the logger before adding new ones.
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            // Add all new handlers to the logger.
            logHandlers.forEach(logger::addHandler);
            if (logLevel == LogLevel.DEBUG) {
                logHandlers.forEach(handler -> debug("AppLogger: Added handler: " + handler.getClass().getSimpleName()));
            }

        }
        initialized = true;
    }

    /**
     * Log levels supported by {@link AppLogger}.
     */
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR;

        /**
         * Parses a log level name.
         *
         * @param level level name (case-insensitive)
         * @return an {@link Optional} with the parsed level, or empty if invalid
         * @throws NullPointerException if {@code level} is {@code null}
         */
        public static Optional<LogLevel> fromString(String level) {
            Objects.requireNonNull(level, "Level must not be null");
            try {
                return Optional.of(LogLevel.valueOf(level.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        /**
         * Converts this level to {@link java.util.logging.Level}.
         *
         * @return mapped JUL level
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
        private static final int INITIAL_BUILDER_CAPACITY = 128; // Initial capacity for the StringBuilder used in formatting.
        private static final int LOG_LEVEL_PADDING_WIDTH = 7; // Padding for log level alignment.

        @SuppressWarnings("StringConcatenationMissingWhitespace")
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(INITIAL_BUILDER_CAPACITY);

            // Time.
            String time = TIME_FORMATTER.format(LocalTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()));
            sb.append("[").append(time).append("] ");

            // Log level.
            sb.append("[").append(String.format("%-" + LOG_LEVEL_PADDING_WIDTH + "s", record.getLevel().getName())).append("] ");

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

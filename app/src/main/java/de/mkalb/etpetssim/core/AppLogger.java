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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Application-wide logging facade built on {@link java.util.logging}.
 *
 * <p>Configure once via {@link #initialize(LogLevel, boolean, Path)} and then use the static logging methods
 * throughout the application.
 */
public final class AppLogger {

    /**
     * File name of the application log file in the log directory.
     */
    public static final String LOG_FILE_NAME = "ExtraterrestrialPetsSimulation.log";
    /**
     * Log level used before initialization and when no log level is specified.
     */
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;

    private static final AppLogger APP_LOGGER = new AppLogger();

    private final Logger logger;
    private boolean initialized;

    private AppLogger() {
        // Root logger for the application.
        logger = Logger.getLogger("");
        logger.setLevel(DEFAULT_LOG_LEVEL.toJavaLogLevel());
    }

    /**
     * Initializes logging.
     *
     * @param logLevel   the minimum enabled log level
     * @param useConsole whether to attach a console handler
     * @param logPath    optional file path for file logging; {@code null} disables file logging
     * @throws IllegalStateException if logging is already initialized
     */
    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        APP_LOGGER.initializeLogger(logLevel, useConsole, logPath);
        infof("AppLogger: Logger initialized with level: %s, console output: %b, log file: %s",
                logLevel, useConsole, (logPath != null) ? logPath : "none");
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
     *
     * <p>Tests that run production code which logs call this method once before their tests; it logs at
     * {@link LogLevel#DEBUG} to the console.
     */
    public static synchronized void initializeForTesting() {
        if (!isInitialized()) {
            APP_LOGGER.initializeLogger(LogLevel.DEBUG, true, null);
            debug("AppLogger: Logger initialized for testing with level: DEBUG and console output.");
        }
    }

    /**
     * Shuts down logging and closes and removes all handlers.
     */
    public static synchronized void shutdown() {
        debug("AppLogger: Shutting down logger.");
        APP_LOGGER.initialized = false;
        APP_LOGGER.removeHandlers(true);
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
     */
    public static void debug(Supplier<String> messageSupplier) {
        APP_LOGGER.logger.fine(messageSupplier);
    }

    /**
     * Logs a formatted debug message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args   format arguments
     */
    public static void debugf(String format, Object... args) {
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
     */
    public static void info(Supplier<String> messageSupplier) {
        APP_LOGGER.logger.info(messageSupplier);
    }

    /**
     * Logs a formatted informational message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args   format arguments
     */
    public static void infof(String format, Object... args) {
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
     */
    public static void warn(Supplier<String> messageSupplier) {
        APP_LOGGER.logger.warning(messageSupplier);
    }

    /**
     * Logs a formatted warning message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args   format arguments
     */
    public static void warnf(String format, Object... args) {
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
     */
    public static void error(Supplier<String> messageSupplier) {
        APP_LOGGER.logger.severe(messageSupplier);
    }

    /**
     * Logs an error message with an exception.
     *
     * @param throwable exception to attach
     * @param message   message text, may be {@code null}
     */
    public static void error(Throwable throwable, @Nullable String message) {
        APP_LOGGER.logger.log(Level.SEVERE, message, throwable);
    }

    /**
     * Logs a formatted error message.
     *
     * @param format format string compatible with {@link String#format(String, Object...)}
     * @param args   format arguments
     */
    public static void errorf(String format, Object... args) {
        APP_LOGGER.logger.log(Level.SEVERE, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Logs a formatted error message with an exception.
     *
     * @param throwable exception to attach
     * @param format    format string compatible with {@link String#format(String, Object...)}
     * @param args      format arguments
     */
    public static void errorf(Throwable throwable, String format, Object... args) {
        APP_LOGGER.logger.log(Level.SEVERE, throwable, () -> String.format(Locale.ROOT, format, args));
    }

    /**
     * Resets the logger state for tests, so that a test can initialize logging again with other settings.
     */
    static synchronized void resetForTesting() {
        if (isInitialized()) {
            debug("AppLogger: Reset logger for testing.");
            APP_LOGGER.initialized = false;
            APP_LOGGER.logger.setLevel(DEFAULT_LOG_LEVEL.toJavaLogLevel());
            APP_LOGGER.logger.setUseParentHandlers(true);
            APP_LOGGER.removeHandlers(true);
        }
    }

    /**
     * Returns the number of attached handlers, so that tests can check the effect of initialization.
     *
     * @return number of active handlers
     */
    static synchronized int handlerCountForTesting() {
        return APP_LOGGER.logger.getHandlers().length;
    }

    private synchronized void initializeLogger(LogLevel logLevel, boolean useConsole, @Nullable Path logPath) {
        if (initialized) {
            throw new IllegalStateException("Unable to initialize AppLogger: already initialized");
        }

        logger.setUseParentHandlers(false);
        Level level = logLevel.toJavaLogLevel();
        logger.setLevel(level);

        List<Handler> logHandlers = new ArrayList<>();
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
                    logger.severe("AppLogger: Parent directory for log file does not exist or is not writable: "
                            + logPath);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "AppLogger: Failed to create log file handler: " + logPath, e);
            }
        }

        if (logHandlers.isEmpty()) {
            // Warn through the existing handlers before they are removed.
            logger.warning("AppLogger: No logging handlers were created.");
            removeHandlers(false);
            logger.setLevel(Level.OFF);
        } else {
            removeHandlers(false);
            logHandlers.forEach(logger::addHandler);
            if (logLevel == LogLevel.DEBUG) {
                logHandlers.forEach(handler ->
                        debug("AppLogger: Added handler: " + handler.getClass().getSimpleName()));
            }
        }
        initialized = true;
    }

    private void removeHandlers(boolean closeHandlers) {
        for (Handler handler : logger.getHandlers()) {
            if (closeHandlers) {
                handler.close();
            }
            logger.removeHandler(handler);
        }
    }

    /**
     * Log levels supported by {@link AppLogger}.
     */
    public enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR;

        /**
         * Resolves a log level name to an enum constant.
         *
         * @param levelName the level name to resolve (case-insensitive)
         * @return an {@link Optional} containing the matching level, or empty if unknown
         */
        public static Optional<LogLevel> fromLevelName(String levelName) {
            try {
                return Optional.of(LogLevel.valueOf(levelName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        /**
         * Converts this level to {@link Level}.
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
     * Formatter for the log records of {@link AppLogger}: time, log level, thread name, message, and the stack trace of
     * an attached exception.
     */
    static final class AppLogFormatter extends Formatter {

        private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
        private static final String HEADER_FORMAT = "[%s] [%-7s] [%s] ";
        private static final int INITIAL_BUILDER_CAPACITY = 128;

        private static String displayThreadName() {
            String threadName = Thread.currentThread().getName();
            return "JavaFX Application Thread".equals(threadName) ? "FX" : threadName;
        }

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder(INITIAL_BUILDER_CAPACITY);
            String time = TIME_FORMATTER.format(
                    LocalTime.ofInstant(Instant.ofEpochMilli(record.getMillis()), ZoneId.systemDefault()));
            sb.append(String.format(Locale.ROOT, HEADER_FORMAT, time, record.getLevel().getName(),
                    displayThreadName()));
            sb.append(formatMessage(record)).append(System.lineSeparator());

            Throwable throwable = record.getThrown();
            if (throwable != null) {
                sb.append(throwable).append(System.lineSeparator());
                for (StackTraceElement element : throwable.getStackTrace()) {
                    sb.append("\tat ").append(element).append(System.lineSeparator());
                }
            }

            return sb.toString();
        }

    }

}

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
 * The LoggingManager class is responsible for managing logging in the application.
 * It allows for initialization with a specific log level, console output, and log file path.
 * It provides static methods to log messages at different levels (info, warning, error).
 */
public final class LoggingManager {

    public enum LogLevel {
        INFO, WARNING, ERROR
    }

    private static @Nullable LoggingManager instance;

    private final Logger logger;

    private LoggingManager(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        Objects.requireNonNull(logLevel, "Log level must not be null");
        logger = Logger.getLogger("");

        Level level = switch (logLevel) {
            case INFO -> Level.INFO;
            case WARNING -> Level.WARNING;
            case ERROR -> Level.SEVERE;
        };

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        if (useConsole) {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(level);
            logger.addHandler(consoleHandler);
        }

        if ((logPath != null) && !logPath.isBlank()) {
            try {
                Path path = Paths.get(logPath);
                Path parent = path.getParent();
                if ((parent != null) && Files.exists(parent) && Files.isDirectory(parent) && Files.isWritable(parent)) {
                    Files.deleteIfExists(path);
                    FileHandler fileHandler = new FileHandler(logPath, false);
                    fileHandler.setFormatter(new SimpleFormatter());
                    fileHandler.setLevel(level);
                    logger.addHandler(fileHandler);
                } else {
                    logger.warning(() -> "Parent directory for log file does not exist or is not writable: " + path);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create log file handler! " + logPath, e);
            }
        }
    }

    public static synchronized void initialize(LogLevel logLevel, boolean useConsole, @Nullable String logPath) {
        if (instance != null) {
            throw new IllegalStateException("LoggingManager instance is already initialized.");
        }
        instance = new LoggingManager(logLevel, useConsole, logPath);
    }

    public static void info(@Nullable String message) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        instance.logger.info(message);
    }

    public static void info(Supplier<@Nullable String> messageSupplier) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.info(messageSupplier);
    }

    public static void warning(@Nullable String message) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        instance.logger.warning(message);
    }

    public static void warning(Supplier<@Nullable String> messageSupplier) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.warning(messageSupplier);
    }

    public static void error(@Nullable String message) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        instance.logger.severe(message);
    }

    public static void error(Supplier<@Nullable String> messageSupplier) {
        Objects.requireNonNull(instance, "LoggingManager instance is not initialized.");
        Objects.requireNonNull(messageSupplier, "Message supplier must not be null");
        instance.logger.severe(messageSupplier);
    }

}

package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Application launcher and bootstrap entry point.
 * <p>
 * This class parses command-line arguments, initializes logging and localization,
 * and launches {@link ExtraterrestrialPetsSimulation}. It is intentionally separate
 * from the JavaFX {@link Application} subclass.
 */
public final class AppLauncher {

    /**
     * Private constructor to prevent instantiation.
     */
    private AppLauncher() {
    }

    /**
     * Parses command-line arguments and handles the help flag.
     * <p>
     * If {@link AppArgs.Key#HELP} is active, usage information is written to
     * {@link System#out} and the process exits with status code {@code 0}.
     *
     * @param args command-line arguments; may be empty
     * @return parsed {@link AppArgs} instance
     * @see AppArgs.Key#printHelp(Appendable)
     */
    @SuppressWarnings("CallToSystemExit")
    private static AppArgs parseArgumentsAndHandleHelp(String[] args) {
        AppArgs arguments = new AppArgs(args);
        if (arguments.isFlagActive(AppArgs.Key.HELP)) {
            AppArgs.Key.printHelp(System.out);
            // Exit the JavaFX application after printing help
            AppLogger.info("AppLauncher: Exiting after printing help.");
            System.exit(0);
        }
        return arguments;
    }

    /**
     * Initializes application logging from parsed arguments.
     *
     * @param arguments parsed command-line arguments
     */
    private static void initAppLogger(AppArgs arguments) {
        var logLevel = arguments.getValue(AppArgs.Key.LOG_LEVEL)
                                .flatMap(AppLogger.LogLevel::fromString)
                                .orElse(AppLogger.DEFAULT_LOG_LEVEL);
        boolean useConsole = arguments.getBoolean(AppArgs.Key.LOG_CONSOLE, false);
        try {
            boolean useFile = arguments.getBoolean(AppArgs.Key.LOG_FILE, false);
            Path logPath = useFile ? AppStorage.getLogFile(AppLogger.LOG_FILE_NAME, AppStorage.OperatingSystem.detect()) : null;

            // Initialize the AppLogger with the specified log level, console usage, and log file path
            AppLogger.initialize(logLevel, useConsole, logPath);
        } catch (IOException e) {
            // Initialize the AppLogger with the specified log level, console usage and null log file path
            AppLogger.initialize(logLevel, useConsole, null);
            AppLogger.error("AppLauncher: Failed to initialize log file.", e);
        }
    }

    /**
     * Initializes localization from parsed arguments and updates the JVM default locale.
     *
     * @param arguments parsed command-line arguments
     */
    private static void initAppLocalization(AppArgs arguments) {
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
        Locale.setDefault(AppLocalization.locale());
    }

    /**
     * Application entry point.
     * <p>
     * Arguments are parsed first, then logging and localization are initialized,
     * and finally the JavaFX application is launched.
     *
     * @param args command-line arguments passed to the application
     */
    static void main(String[] args) {
        var arguments = parseArgumentsAndHandleHelp(args);
        initAppLogger(arguments);
        initAppLocalization(arguments);

        AppLogger.info("AppLauncher: Launching application with arguments: " + arguments.argumentsAsString());
        Application.launch(ExtraterrestrialPetsSimulation.class, args);
    }

}

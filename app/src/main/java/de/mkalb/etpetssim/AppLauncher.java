package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * The launcher class for the Extraterrestrial Pets Simulation application.
 * <p>
 * Responsible for parsing command-line arguments, initializing logging and localization,
 * and starting the JavaFX application. This class does not extend {@link javafx.application.Application}
 * and serves as the main entry point for the application.
 * </p>
 * <p>
 * The JavaFX application is started via {@link ExtraterrestrialPetsSimulation}.
 * </p>
 * <p>
 * Use the {@code --help} flag to display usage information and exit.
 * </p>
 */
public final class AppLauncher {

    /**
     * Private constructor to prevent instantiation.
     */
    private AppLauncher() {
    }

    /**
     * Parses command-line arguments and terminates the application if the help flag is present.
     *
     * <p>When {@link AppArgs.Key#HELP} is active, this method prints usage information
     * to {@link System#out} and calls {@link System#exit(int)} with code 0.</p>
     *
     * @param args the command-line arguments to parse; must not be {@code null}. Can be empty.
     * @return the parsed {@link AppArgs} instance
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
     * Initializes the application logger based on command-line arguments.
     * @param arguments the parsed command-line arguments
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
     * Initializes the application localization based on command-line arguments.
     * Set the default locale for the application.
     *
     * @param arguments the parsed command-line arguments
     */
    private static void initAppLocalization(AppArgs arguments) {
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null));
        Locale.setDefault(AppLocalization.locale());
    }

    /**
     * The main entry point for the Extraterrestrial Pets Simulation application.
     * <p>
     * It is not the JavaFX Application class itself.
     * The command-line arguments are parsed with AppArgs.
     * Use "--help" to display the help message and exit the application.
     *
     * @param args the command-line arguments passed to the application
     */
    static void main(String[] args) {
        var arguments = parseArgumentsAndHandleHelp(args);
        initAppLogger(arguments);
        initAppLocalization(arguments);

        AppLogger.info("AppLauncher: Launching application with arguments: " + arguments.argumentsAsString());
        Application.launch(ExtraterrestrialPetsSimulation.class, args);
    }

}

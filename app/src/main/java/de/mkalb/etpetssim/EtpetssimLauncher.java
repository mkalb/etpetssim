package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppArgs;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppManifest;
import de.mkalb.etpetssim.core.AppSettings;
import de.mkalb.etpetssim.core.AppStorage;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

/**
 * Application launcher and bootstrap entry point.
 *
 * <p>Parses command-line arguments, initializes logging and localization, and launches
 * {@link EtpetssimApplication}. It is intentionally separate from the JavaFX {@link Application} subclass.
 */
public final class EtpetssimLauncher {

    private EtpetssimLauncher() {
    }

    /**
     * Application entry point.
     *
     * <p>Arguments are parsed first, then logging and localization are initialized, and finally the JavaFX
     * application is launched.
     *
     * @param args command-line arguments passed to the application
     */
    static void main(String[] args) {
        var arguments = parseArgumentsAndHandleInformation(args);
        initializeAppLogger(arguments);
        initializeAppLocalization(arguments);

        AppLogger.infof("EtpetssimLauncher: Launching %s with arguments: %s", AppManifest.formatVersionLine(),
                arguments.toArgumentString());
        Application.launch(EtpetssimApplication.class, args);
    }

    /**
     * Parses command-line arguments and handles the information flags.
     *
     * <p>If {@link AppArgs.Key#HELP} is active, usage information is written to {@link System#out}; otherwise, if
     * {@link AppArgs.Key#VERSION} is active, the version line of {@link AppManifest#formatVersionLine()} is written.
     * In both cases the process exits with status code {@code 0}.
     *
     * @param args command-line arguments; may be empty
     * @return parsed {@link AppArgs} instance
     */
    @SuppressWarnings("CallToSystemExit")
    private static AppArgs parseArgumentsAndHandleInformation(String[] args) {
        AppArgs arguments = new AppArgs(args);
        if (arguments.isFlagActive(AppArgs.Key.HELP)) {
            AppArgs.Key.printHelp(System.out);
            System.exit(0);
        }
        if (arguments.isFlagActive(AppArgs.Key.VERSION)) {
            System.out.println(AppManifest.formatVersionLine());
            System.exit(0);
        }
        return arguments;
    }

    /**
     * Initializes application logging from parsed arguments.
     *
     * <p>An unknown log level is ignored with a warning, and the default log level is used. If the log file is
     * requested but its directory cannot be created, logging continues without a file and the error is logged.
     *
     * @param arguments parsed command-line arguments
     */
    private static void initializeAppLogger(AppArgs arguments) {
        Optional<String> logLevelArgument = arguments.getValue(AppArgs.Key.LOG_LEVEL);
        Optional<AppLogger.LogLevel> requestedLogLevel = logLevelArgument.flatMap(AppLogger.LogLevel::fromLevelName);
        var logLevel = requestedLogLevel.orElse(AppLogger.DEFAULT_LOG_LEVEL);
        boolean useConsole = arguments.getBoolean(AppArgs.Key.LOG_CONSOLE, false);
        boolean useFile = arguments.getBoolean(AppArgs.Key.LOG_FILE, false);
        try {
            Path logPath = useFile
                    ? AppStorage.getLogFile(AppLogger.LOG_FILE_NAME, AppStorage.OperatingSystem.detect())
                    : null;
            AppLogger.initialize(logLevel, useConsole, logPath);
        } catch (IOException e) {
            AppLogger.initialize(logLevel, useConsole, null);
            AppLogger.error(e, "EtpetssimLauncher: Failed to initialize log file");
        }
        if (logLevelArgument.isPresent() && requestedLogLevel.isEmpty()) {
            AppLogger.warnf("EtpetssimLauncher: Ignoring unknown log level argument: '%s'", logLevelArgument.get());
        }
    }

    /**
     * Initializes localization from parsed arguments and updates the JVM default locale.
     *
     * <p>A valid {@code --locale} argument takes precedence over the locale saved in the application settings, which
     * takes precedence over the system locale. The argument does not change the saved setting.
     *
     * @param arguments parsed command-line arguments
     */
    private static void initializeAppLocalization(AppArgs arguments) {
        Optional<Locale> settingsLocale = AppSettings.readLocale()
                .map(AppLocalization.CountryLocale::countryLocale);
        Locale defaultLocale = settingsLocale.orElseGet(Locale::getDefault);
        AppLogger.debugf("EtpetssimLauncher: Resolved fallback locale: %s, source: %s", defaultLocale,
                settingsLocale.isPresent() ? "settings" : "system");
        AppLocalization.initialize(arguments.getValue(AppArgs.Key.LOCALE).orElse(null), defaultLocale);
        Locale.setDefault(AppLocalization.locale());
    }

}

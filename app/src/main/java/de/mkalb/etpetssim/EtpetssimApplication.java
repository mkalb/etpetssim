package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppArgs;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalization.CountryLocale;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;
import de.mkalb.etpetssim.core.AppSettings;
import de.mkalb.etpetssim.simulations.core.SimulationFactory;
import de.mkalb.etpetssim.simulations.core.SimulationInstance;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.ui.AboutDialog;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import de.mkalb.etpetssim.ui.UnexpectedErrorDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Main JavaFX {@link Application} implementation.
 *
 * <p>Creates the primary stage content, switches between the start screen and the simulations,
 * and coordinates startup and shutdown behavior. Command-line parsing, logging,
 * and localization are initialized by {@link EtpetssimLauncher} before launch.
 */
public final class EtpetssimApplication extends Application {

    /**
     * Resource paths of the application icons, which are set on the primary stage and the dialogs.
     *
     * <p>The sizes cover the small and large window icons for display scaling from 100% to 200% (16x16 to 64x64
     * pixels), plus 128x128 pixels.
     */
    private static final String[] APP_ICON_PATHS = {
            "etpetssim16.png",
            "etpetssim20.png",
            "etpetssim24.png",
            "etpetssim32.png",
            "etpetssim40.png",
            "etpetssim48.png",
            "etpetssim64.png",
            "etpetssim128.png"
    };
    private static final double FALLBACK_WINDOW_DECORATION_WIDTH = 16.0d;
    private static final double FALLBACK_WINDOW_DECORATION_HEIGHT = 39.0d;
    private static final double DEFAULT_WINDOW_MARGIN = 8.0d;
    private static final long TERMINATION_TIMEOUT_SECONDS = 5L;

    private final List<SimulationTerminationHandle> pendingTerminationHandles = new ArrayList<>();
    private List<Image> appIcons = List.of();
    private @Nullable SimulationInstance currentSimulationInstance;
    private boolean terminationAwaited;

    /**
     * Resolves the initial simulation type from parsed arguments, restricted to implemented simulations.
     *
     * <p>An unknown simulation argument is ignored with a warning.
     *
     * @param arguments parsed application arguments
     * @return an {@link Optional} containing the matching simulation type, or empty if none matches
     */
    private static Optional<SimulationType> resolveSimulationType(AppArgs arguments) {
        Optional<String> simulationArgument = arguments.getValue(AppArgs.Key.SIMULATION);
        Optional<SimulationType> simulationType = simulationArgument.flatMap(SimulationType::fromCliArgument);
        if (simulationArgument.isPresent() && simulationType.isEmpty()) {
            AppLogger.warnf("EtpetssimApplication: Ignoring unsupported simulation argument: '%s'",
                    simulationArgument.get());
        }
        return simulationType;
    }

    /**
     * Resolves the screen containing the stage center point, with the primary screen as a fallback.
     *
     * @param stage stage to evaluate
     * @return matching screen, or the primary screen if none matches
     */
    private static Screen resolveScreenContainingStage(Stage stage) {
        double cx = stage.getX() + (stage.getWidth() / 2.0d);
        double cy = stage.getY() + (stage.getHeight() / 2.0d);

        for (Screen s : Screen.getScreens()) {
            if (s.getBounds().contains(cx, cy)) {
                return s;
            }
        }
        return Screen.getPrimary();
    }

    /**
     * Starts the JavaFX application.
     *
     * <p>Loads the application icons, installs uncaught-exception handling that logs the exception and shows it in an
     * {@link UnexpectedErrorDialog}, resolves the requested simulation type, registers the shutdown of the current
     * simulation on a close request, and shows the primary stage with that simulation, or with the start screen if no
     * valid simulation argument is given.
     *
     * @param primaryStage primary stage created by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        updateStageIcons(primaryStage);
        UnexpectedErrorDialog unexpectedErrorDialog = new UnexpectedErrorDialog(appIcons);
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.errorf(throwable, "EtpetssimApplication: Uncaught exception in thread: %s", thread.getName());
            Platform.runLater(() -> unexpectedErrorDialog.handleUncaughtException(throwable));
        });

        AppArgs arguments = new AppArgs(getParameters().getRaw().toArray(String[]::new));
        Optional<SimulationType> type = resolveSimulationType(arguments);

        primaryStage.setOnCloseRequest(_ -> shutdownCurrentSimulation());
        if (type.isPresent()) {
            AppLogger.infof("EtpetssimApplication: Starting with simulation type: %s", type.get().name());
            switchToSimulation(primaryStage, type.get());
        } else {
            AppLogger.info("EtpetssimApplication: Starting with start screen");
            switchToStartScreen(primaryStage);
        }
        primaryStage.show();
        AppLogger.info("EtpetssimApplication: Started and showed primary stage");
    }

    /**
     * Stops the application.
     *
     * <p>Shuts down the current simulation, waits at most {@value #TERMINATION_TIMEOUT_SECONDS} seconds in total for
     * all pending simulation terminations, and shuts down logging.
     */
    @Override
    public void stop() {
        AppLogger.info("EtpetssimApplication: Shutting down");
        shutdownCurrentSimulation();
        awaitPendingTerminations();
        AppLogger.shutdown();
    }

    /**
     * Sets the active simulation instance without shutting down the previous one;
     * {@link #switchToSimulation(Stage, SimulationType)} shuts it down first.
     *
     * @param simulationInstance simulation instance shown on the primary stage
     */
    void setCurrentSimulationInstance(SimulationInstance simulationInstance) {
        currentSimulationInstance = simulationInstance;
    }

    /**
     * Shuts down the currently active simulation instance, if one exists.
     *
     * <p>The instance reference is cleared before invoking simulation shutdown so
     * repeated lifecycle paths, such as close request followed by {@link #stop()},
     * cannot shut down the same instance twice.
     */
    void shutdownCurrentSimulation() {
        SimulationInstance simulationInstance = currentSimulationInstance;
        if (simulationInstance == null) {
            return;
        }

        currentSimulationInstance = null;
        try {
            AppLogger.infof("EtpetssimApplication: Shutting down simulation: %s",
                    simulationInstance.simulationType());
            simulationInstance.region().setDisable(true);
            pendingTerminationHandles.add(simulationInstance.simulationMainView().shutdownSimulation());
        } catch (RuntimeException e) {
            AppLogger.errorf(e, "EtpetssimApplication: Failed to shut down simulation: %s",
                    simulationInstance.simulationType());
        }
    }

    /**
     * Loads the application icons, keeps them for the dialogs, and applies them to a stage.
     *
     * @param stage stage to update
     */
    private void updateStageIcons(Stage stage) {
        appIcons = AppResources.getImages(APP_ICON_PATHS);
        if (appIcons.isEmpty()) {
            AppLogger.error("EtpetssimApplication: Failed to load application icons");
        } else {
            stage.getIcons().addAll(appIcons);
        }
    }

    /**
     * Switches the primary stage to the start screen.
     *
     * <p>The current simulation is shut down and the stage content is replaced by the start screen; no simulation is
     * active afterward.
     *
     * @param stage primary application stage
     */
    private void switchToStartScreen(Stage stage) {
        AppLogger.info("EtpetssimApplication: Switching to start screen");
        shutdownCurrentSimulation();

        String title = AppLocalization.getText(AppLocalizationKeys.START_TITLE);
        Node headerNode = createHeaderNode(stage, title,
                AppLocalization.getOptionalText(AppLocalizationKeys.START_SUBTITLE).orElse(null), null, false);
        Region mainRegion = StartScreen.createRegion(
                simulationType -> switchToSimulation(stage, simulationType),
                countryLocale -> handleLanguageSelection(stage, countryLocale));
        setStageContent(stage, headerNode, mainRegion, null, title);
    }

    /**
     * Switches the primary stage to a simulation.
     *
     * <p>The current simulation is shut down, a new simulation instance is created and becomes the active one, and the
     * stage content is replaced by the header and the region of the simulation.
     *
     * @param stage          primary application stage
     * @param simulationType simulation type to display
     */
    private void switchToSimulation(Stage stage, SimulationType simulationType) {
        AppLogger.infof("EtpetssimApplication: Switching to simulation: %s", simulationType);
        shutdownCurrentSimulation();

        var simulationInstance = SimulationFactory.createInstance(simulationType);
        setCurrentSimulationInstance(simulationInstance);

        String title = simulationType.title();
        Node headerNode = createHeaderNode(stage, title, simulationType.subtitle().orElse(null),
                simulationType.uri().orElse(null), true);
        setStageContent(stage, headerNode, simulationInstance.region(), simulationType.cssUrl().orElse(null), title);
    }

    /**
     * Activates the selected locale, saves it as application setting, and rebuilds the start screen in the new
     * language.
     *
     * @param stage         primary application stage
     * @param countryLocale the selected locale, which differs from the active one
     */
    private void handleLanguageSelection(Stage stage, CountryLocale countryLocale) {
        AppLocalization.setLocale(countryLocale);
        Locale.setDefault(AppLocalization.locale());
        AppSettings.writeLocale(countryLocale);
        switchToStartScreen(stage);
    }

    /**
     * Replaces the scene root of the stage by the header and the main region, and updates the stylesheets, the window
     * title, and the stage layout.
     *
     * @param stage      primary application stage
     * @param headerNode header shown above the main region
     * @param mainRegion main region, which grows vertically
     * @param cssUrl     URL of the stylesheet added after the common one, or {@code null} for none
     * @param title      title inserted into the window title pattern
     */
    private void setStageContent(Stage stage, Node headerNode, Region mainRegion, @Nullable String cssUrl,
                                 String title) {
        VBox root = new VBox(headerNode, mainRegion);
        root.getStyleClass().add(FXStyleClasses.APP_VBOX);
        VBox.setVgrow(mainRegion, Priority.ALWAYS);
        root.setDisable(true); // Disable first until fully initialized later

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        // Add the common stylesheet first and then the specific one
        List<String> styles = new ArrayList<>();
        AppResources.getCssUrl("scene.css").ifPresent(styles::add);
        if (cssUrl != null) {
            styles.add(cssUrl);
        }
        scene.getStylesheets().setAll(styles);

        stage.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE, title));

        // Adjust stage size and position to fit the screen. Run later to ensure layout is calculated.
        Platform.runLater(() -> adjustStageLayoutToScreen(stage, root));
    }

    /**
     * Waits for the pending simulation terminations, at most once per application.
     *
     * <p>All terminations share one timeout budget of {@value #TERMINATION_TIMEOUT_SECONDS} seconds; a termination
     * that does not complete in time is forced with {@link SimulationTerminationHandle#shutdownNow()}. If the waiting
     * thread is interrupted, all pending terminations are forced and the interrupted status is restored.
     */
    private void awaitPendingTerminations() {
        if (terminationAwaited) {
            return;
        }
        terminationAwaited = true;

        long deadlineNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(TERMINATION_TIMEOUT_SECONDS);
        boolean interrupted = false;
        for (SimulationTerminationHandle terminationHandle : pendingTerminationHandles) {
            long remainingNanos = deadlineNanos - System.nanoTime();
            try {
                if ((remainingNanos <= 0L)
                        || !terminationHandle.awaitTermination(remainingNanos, TimeUnit.NANOSECONDS)) {
                    AppLogger.warn("EtpetssimApplication: Simulation did not terminate within shutdown timeout");
                    terminationHandle.shutdownNow();
                }
            } catch (InterruptedException e) {
                AppLogger.warn("EtpetssimApplication: Interrupted while waiting for simulation terminations");
                interrupted = true;
                pendingTerminationHandles.forEach(SimulationTerminationHandle::shutdownNow);
                break;
            }
        }
        pendingTerminationHandles.clear();
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Adjusts stage size and position to fit the current screen's visual bounds.
     *
     * <p>Iconified stages are left unchanged, full-screen stages adopt the visual bounds,
     * and maximized or normal stages are resized and repositioned with decoration and
     * margin handling.
     *
     * <p>Intended to run from {@link Platform#runLater(Runnable)} after
     * JavaFX has completed the initial layout pass.
     *
     * @param stage stage to adjust
     * @param root  scene root associated with the stage
     */
    private void adjustStageLayoutToScreen(Stage stage, Region root) {
        if (stage.isIconified()) {
            root.setDisable(false); // Enable now. No adjustments needed.

            AppLogger.info("EtpetssimApplication: Stage is minimized, layout not adjusted");
            return;
        }

        Screen screen = resolveScreenContainingStage(stage);
        Rectangle2D visualBounds = screen.getVisualBounds();
        AppLogger.infof("EtpetssimApplication: Using visual bounds of screen: %s", visualBounds);

        if (stage.isFullScreen()) {
            root.setMaxWidth(visualBounds.getWidth());
            root.setMaxHeight(visualBounds.getHeight());

            // Stabilize layout calculations
            root.applyCss();
            root.layout();

            root.setDisable(false); // Enable now after all adjustments are done

            AppLogger.info("EtpetssimApplication: Stage is full screen, root size limited to visual bounds");
            return;
        }

        // Stabilize layout calculations
        root.applyCss();
        root.layout();

        if (stage.isMaximized()) {
            // First un-maximize the stage, so we can resize/relocate it
            stage.setMaximized(false);

            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());

            // Stabilize layout calculations another time after changing size/position
            root.applyCss();
            root.layout();

            // Re-maximize the stage one step later to ensure proper maximized state
            Platform.runLater(() -> {
                // Important: Set only maximized, do NOT set size/position again here!
                stage.setMaximized(true);
                root.setDisable(false); // Enable now after all adjustments are done

                AppLogger.info("EtpetssimApplication: Stage maximized to visual bounds");
            });
        } else {
            double prefWidth = Math.max(0, root.prefWidth(-1));
            double prefHeight = Math.max(0, root.prefHeight(-1));

            double decoWidth = Math.max(0, stage.getWidth() - stage.getScene().getWidth());
            double decoHeight = Math.max(0, stage.getHeight() - stage.getScene().getHeight());
            if ((decoWidth == 0) || (decoHeight == 0)) {
                decoWidth = FALLBACK_WINDOW_DECORATION_WIDTH;
                decoHeight = FALLBACK_WINDOW_DECORATION_HEIGHT;
            }

            // Limit root max size to visual bounds minus decoration and margin
            root.setMaxWidth(visualBounds.getWidth() - decoWidth - DEFAULT_WINDOW_MARGIN);
            root.setMaxHeight(visualBounds.getHeight() - decoHeight - DEFAULT_WINDOW_MARGIN);

            // Stabilize layout calculations another time after changing max sizes
            root.applyCss();
            root.layout();

            stage.setMaxWidth(visualBounds.getWidth());
            stage.setMaxHeight(visualBounds.getHeight());

            stage.setWidth(Math.min(prefWidth + decoWidth + DEFAULT_WINDOW_MARGIN, visualBounds.getWidth()));
            stage.setHeight(Math.min(prefHeight + decoHeight + DEFAULT_WINDOW_MARGIN, visualBounds.getHeight()));

            stage.setX(Math.clamp(stage.getX(), visualBounds.getMinX(), visualBounds.getMaxX() - stage.getWidth()));
            stage.setY(Math.clamp(stage.getY(), visualBounds.getMinY(), visualBounds.getMaxY() - stage.getHeight()));

            root.setDisable(false); // Enable now after all adjustments are done

            AppLogger.infof(
                    "EtpetssimApplication: Stage adjusted to size: %.1fx%.1f at position: (%.1f, %.1f)",
                    stage.getWidth(),
                    stage.getHeight(),
                    stage.getX(),
                    stage.getY());
        }
    }

    /**
     * Creates the header shown above the main region of the start screen or a simulation.
     *
     * @param stage                primary stage
     * @param title                title of the header
     * @param subtitle             subtitle below the title, or {@code null} for none
     * @param uri                  URI of an external link, or {@code null} for none
     * @param startScreenLinkShown whether the header shows a link that switches to the start screen
     * @return header node containing titles and navigation links
     */
    private Node createHeaderNode(Stage stage, String title, @Nullable String subtitle, @Nullable URI uri,
                                  boolean startScreenLinkShown) {
        VBox titleBox = new VBox(FXComponentFactory.createLabel(title, FXStyleClasses.HEADER_TITLE_LABEL));
        titleBox.getStyleClass().add(FXStyleClasses.HEADER_TITLE_VBOX);

        if (subtitle != null) {
            titleBox.getChildren().add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.HEADER_SUBTITLE_LABEL));
        }

        VBox linkBox = new VBox();
        linkBox.setAlignment(Pos.TOP_RIGHT);
        linkBox.getStyleClass().add(FXStyleClasses.HEADER_LINK_VBOX);

        if (startScreenLinkShown) {
            Hyperlink startScreenLink = new Hyperlink(
                    AppLocalization.getText(AppLocalizationKeys.HEADER_STARTSCREEN_LINK));
            startScreenLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            startScreenLink.setOnAction(_ -> switchToStartScreen(stage));
            linkBox.getChildren().add(startScreenLink);
        }

        Hyperlink aboutLink = new Hyperlink(AppLocalization.getText(AppLocalizationKeys.HEADER_ABOUT_LINK));
        aboutLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
        aboutLink.setOnAction(_ -> new AboutDialog(appIcons, getHostServices()).show());
        linkBox.getChildren().add(aboutLink);

        if (uri != null) {
            Hyperlink urlLink = new Hyperlink(uri.toString());
            urlLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            urlLink.setOnAction(_ -> getHostServices().showDocument(uri.toString()));
            linkBox.getChildren().add(urlLink);
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerBox = new HBox(titleBox, spacer, linkBox);
        headerBox.getStyleClass().add(FXStyleClasses.HEADER_HBOX);

        return headerBox;
    }

}

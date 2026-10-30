package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppArgs;
import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;
import de.mkalb.etpetssim.simulations.core.SimulationFactory;
import de.mkalb.etpetssim.simulations.core.SimulationInstance;
import de.mkalb.etpetssim.simulations.core.SimulationTerminationHandle;
import de.mkalb.etpetssim.simulations.core.SimulationType;
import de.mkalb.etpetssim.ui.AboutDialog;
import de.mkalb.etpetssim.ui.FXComponentFactory;
import de.mkalb.etpetssim.ui.FXDialogStyler;
import de.mkalb.etpetssim.ui.FXStyleClasses;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Main JavaFX {@link Application} implementation.
 *
 * <p>Creates the primary stage content, switches between simulations,
 * and coordinates startup and shutdown behavior. Command-line parsing, logging,
 * and localization are initialized by {@link AppLauncher} before launch.
 */
public final class ExtraterrestrialPetsSimulation extends Application {

    /**
     * The application icon image paths used in the JavaFX application.
     * They are loaded from the resources and set on the application stage.
     * It contains the small and large window icon sizes for display scaling from 100% to 200%
     * (16x16 to 64x64 pixels) and 128x128 pixels.
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
     * @param arguments parsed application arguments
     * @return an {@link Optional} containing the matching simulation type, or empty if none matches
     */
    private static Optional<SimulationType> resolveSimulationType(AppArgs arguments) {
        return arguments.getValue(AppArgs.Key.SIMULATION)
                .flatMap(arg -> SimulationType.fromCliArgument(arg, true));
    }

    /**
     * Resolves the screen containing the stage center point, with the primary screen as a fallback.
     *
     * @param stage stage to evaluate
     * @return matching screen, or the primary screen if none matches
     */
    @SuppressWarnings("MagicNumber")
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
     * <p>Installs uncaught-exception handling, resolves the requested
     * simulation type, and shows the primary stage.
     *
     * @param primaryStage primary stage created by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.errorf(throwable, "Application: Uncaught exception in thread %s", thread.getName());
            Platform.runLater(() -> showUnexpectedErrorAlert(throwable));
        });

        AppArgs arguments = new AppArgs(getParameters().getRaw().toArray(new String[0]));
        SimulationType type = resolveSimulationType(arguments).orElse(SimulationType.STARTSCREEN);
        AppLogger.infof("Application: Starting with simulation type: %s", type.name());

        updateStageIcons(primaryStage);
        switchToSimulation(primaryStage, type);
        primaryStage.show();
        AppLogger.info("Application: Application started successfully. Primary stage is now visible.");
    }

    /**
     * Stops the application.
     *
     * <p>Shuts down the current simulation, waits at most {@value #TERMINATION_TIMEOUT_SECONDS} seconds in total for
     * all pending simulation terminations, and shuts down logging.
     */
    @Override
    public void stop() {
        AppLogger.info("Application: Shutting down.");
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
            AppLogger.infof("Application: Shutting down simulation instance: %s", simulationInstance.simulationType());
            simulationInstance.region().setDisable(true);
            pendingTerminationHandles.add(simulationInstance.simulationMainView().shutdownSimulation());
        } catch (Exception e) {
            AppLogger.errorf(e, "Application: Error during simulation shutdown: %s",
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
            AppLogger.error("Application: Failed to load application icons. Icons will not be set.");
        } else {
            stage.getIcons().addAll(appIcons);
        }
    }

    /**
     * Shows an alert for an uncaught exception and waits until it is closed.
     *
     * @param throwable uncaught exception whose message is shown
     */
    private void showUnexpectedErrorAlert(Throwable throwable) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        FXDialogStyler.applyAppStyle(alert, appIcons);
        alert.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE,
                AppLocalization.getText(AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_TITLE)));
        alert.setHeaderText(AppLocalization.getText(AppLocalizationKeys.ALERT_UNEXPECTED_ERROR_HEADER));
        alert.setGraphic(null);
        alert.setContentText(throwable.getMessage());
        alert.showAndWait();
    }

    /**
     * Switches the primary stage to a simulation.
     *
     * <p>The current simulation is shut down, a new simulation instance is created, the scene content is replaced, and
     * the stage title, stylesheets, shutdown handling, and layout are updated. The {@link SimulationFactory} receives
     * this method as a callback, so that a simulation (e.g., the start screen) can switch to another one.
     *
     * @param stage          primary application stage
     * @param simulationType simulation type to display
     */
    private void switchToSimulation(Stage stage, SimulationType simulationType) {
        AppLogger.infof("Application: Updating stage scene to simulation: %s", simulationType);
        shutdownCurrentSimulation();

        var simulationInstance = SimulationFactory.createInstance(simulationType, stage, this::switchToSimulation);
        var simulationHeaderNode = createSimulationHeaderNode(stage, simulationInstance);
        var simulationMainRegion = simulationInstance.region();

        setCurrentSimulationInstance(simulationInstance);

        VBox root = new VBox(simulationHeaderNode, simulationMainRegion);
        root.getStyleClass().add(FXStyleClasses.APP_VBOX);
        VBox.setVgrow(simulationMainRegion, Priority.ALWAYS);
        root.setDisable(true); // Disable first until fully initialized later

        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        // Add the common stylesheet first and then the stylesheet of the simulation type
        List<String> styles = new ArrayList<>();
        AppResources.getCssUrl("scene.css").ifPresent(styles::add);
        simulationType.cssUrl().ifPresent(styles::add);
        scene.getStylesheets().setAll(styles);

        stage.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE, simulationType.title()));
        stage.setOnCloseRequest(_ -> shutdownCurrentSimulation());

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
                    AppLogger.warn(
                            "Application: Simulation executor did not terminate before application shutdown timeout.");
                    terminationHandle.shutdownNow();
                }
            } catch (InterruptedException e) {
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

            AppLogger.info("Application: Stage is minimized. No adjustments made.");
            return;
        }

        Screen screen = resolveScreenContainingStage(stage);
        Rectangle2D visualBounds = screen.getVisualBounds();
        AppLogger.infof("Application: Found screen and using visual bounds: %s", visualBounds);

        if (stage.isFullScreen()) {
            root.setMaxWidth(visualBounds.getWidth());
            root.setMaxHeight(visualBounds.getHeight());

            // Stabilize layout calculations
            root.applyCss();
            root.layout();

            root.setDisable(false); // Enable now after all adjustments are done

            AppLogger.info("Application: Stage is fullscreen.");
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

                AppLogger.info("Application: Stage maximized");
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
                    "Application: Stage adjusted to size: %.1fx%.1f at position: (%.1f, %.1f)",
                    stage.getWidth(),
                    stage.getHeight(),
                    stage.getX(),
                    stage.getY());
        }
    }

    /**
     * Creates the simulation header shown above the main simulation region.
     *
     * @param stage              primary stage
     * @param simulationInstance active simulation instance
     * @return header node containing titles and navigation links
     */
    private Node createSimulationHeaderNode(Stage stage, SimulationInstance simulationInstance) {
        SimulationType simulationType = simulationInstance.simulationType();

        VBox titleBox = new VBox(
                FXComponentFactory.createLabel(simulationType.title(), FXStyleClasses.HEADER_TITLE_LABEL));
        titleBox.getStyleClass().add(FXStyleClasses.HEADER_TITLE_VBOX);

        simulationType.subtitle()
                .ifPresent(subtitle -> titleBox.getChildren()
                        .add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.HEADER_SUBTITLE_LABEL)));

        VBox linkBox = new VBox();
        linkBox.setAlignment(Pos.TOP_RIGHT);
        linkBox.getStyleClass().add(FXStyleClasses.HEADER_LINK_VBOX);

        if (simulationType != SimulationType.STARTSCREEN) {
            Hyperlink startScreenLink = new Hyperlink(
                    AppLocalization.getText(AppLocalizationKeys.HEADER_STARTSCREEN_LINK));
            startScreenLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            startScreenLink.setOnAction(_ -> {
                AppLogger.infof("Application: Switching to start screen from simulation: %s", simulationType);
                switchToSimulation(stage, SimulationType.STARTSCREEN);
            });
            linkBox.getChildren().add(startScreenLink);
        }

        Hyperlink aboutLink = new Hyperlink(AppLocalization.getText(AppLocalizationKeys.HEADER_ABOUT_LINK));
        aboutLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
        aboutLink.setOnAction(_ -> new AboutDialog(appIcons, getHostServices()).show());
        linkBox.getChildren().add(aboutLink);

        simulationType.uri().ifPresent(uri -> {
            Hyperlink urlLink = new Hyperlink(uri.toString());
            urlLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            urlLink.setOnAction(_ -> getHostServices().showDocument(uri.toString()));
            linkBox.getChildren().add(urlLink);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox simulationHeaderBox = new HBox(titleBox, spacer, linkBox);
        simulationHeaderBox.getStyleClass().add(FXStyleClasses.HEADER_HBOX);

        return simulationHeaderBox;
    }

}

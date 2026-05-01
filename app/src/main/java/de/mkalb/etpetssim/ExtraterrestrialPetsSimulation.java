package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.*;
import de.mkalb.etpetssim.simulations.core.SimulationFactory;
import de.mkalb.etpetssim.simulations.core.SimulationInstance;
import de.mkalb.etpetssim.ui.FXComponentFactory;
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
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.*;

/**
 * Main JavaFX {@link Application} implementation.
 * <p>
 * This class creates the primary stage content, switches between simulations,
 * and coordinates startup and shutdown behavior. Command-line parsing, logging,
 * and localization are initialized by {@link AppLauncher} before launch.
 */
public final class ExtraterrestrialPetsSimulation extends Application {

    /**
     * The application icon image paths used in the JavaFX application.
     * They are loaded from the resources and set on the application stage.
     * It contains standard sizes: 16x16, 32x32, 64x64, and 128x128 pixels.
     */
    private static final String[] APP_ICON_PATHS = {
            "etpetssim16.png",
            "etpetssim32.png",
            "etpetssim64.png",
            "etpetssim128.png"
    };
    private static final double FALLBACK_WINDOW_DECORATION_WIDTH = 16.0d;
    private static final double FALLBACK_WINDOW_DECORATION_HEIGHT = 39.0d;
    private static final double DEFAULT_WINDOW_MARGIN = 8.0d;

    /**
     * Determines the initial simulation type from parsed arguments.
     *
     * @param arguments parsed application arguments
     * @param onlyImplemented whether to restrict matching to implemented simulations
     * @return an {@link Optional} containing the matching simulation type, or empty if none matches
     */
    @SuppressWarnings("SameParameterValue")
    private Optional<SimulationType> determineSimulationType(AppArgs arguments, boolean onlyImplemented) {
        return arguments.getValue(AppArgs.Key.SIMULATION)
                        .flatMap(arg -> SimulationType.fromCliArgument(arg, onlyImplemented));
    }

    /**
     * Loads and applies application icons to a stage.
     *
     * @param stage stage to update
     */
    private void updateStageIcons(Stage stage) {
        List<Image> icons = AppResources.getImages(APP_ICON_PATHS);
        if (icons.isEmpty()) {
            AppLogger.error("Application: Failed to load application icons. Icons will not be set.");
        } else {
            stage.getIcons().addAll(icons);
        }
    }

    /**
     * Switches the primary stage to a simulation.
     * <p>
     * A new simulation instance is created, the scene content is replaced, and the
     * stage title, stylesheets, shutdown handling, and layout are updated.
     *
     * @param stage primary application stage
     * @param simulationType simulation type to display
     * @see de.mkalb.etpetssim.simulations.core.SimulationFactory
     * @see de.mkalb.etpetssim.simulations.start.StartMainView
     */
    private void switchToSimulation(Stage stage, SimulationType simulationType) {
        Objects.requireNonNull(stage, "Stage must not be null");
        Objects.requireNonNull(simulationType, "SimulationType must not be null");

        AppLogger.info("Application: Updating stage scene to simulation: " + simulationType);

        // Create new simulation instance with header and main region
        var simulationInstance = SimulationFactory.createInstance(simulationType, stage, this::switchToSimulation);
        var simulationHeaderNode = buildSimulationHeaderNode(stage, simulationInstance);
        var simulationMainRegion = simulationInstance.region();

        // Create new root layout with header and main region
        VBox root = new VBox(simulationHeaderNode, simulationMainRegion);
        root.getStyleClass().add(FXStyleClasses.APP_VBOX);
        VBox.setVgrow(simulationMainRegion, Priority.ALWAYS);
        root.setDisable(true); // Disable first until fully initialized later

        // Create or update scene with new root
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        // Update scene styles -  Add common stylesheets first and then the specific simulation type stylesheet
        List<String> styles = new ArrayList<>();
        AppResources.getCssUrl("scene.css").ifPresent(styles::add);
        simulationType.cssUrl().ifPresent(styles::add);
        scene.getStylesheets().setAll(styles);

        // Update stage
        stage.setTitle(AppLocalization.getFormattedText(AppLocalizationKeys.WINDOW_TITLE, simulationType.title()));
        stage.setOnCloseRequest(_ -> {
            try {
                AppLogger.info("Application: Shutting down simulation instance: " + simulationInstance.simulationType());
                simulationMainRegion.setDisable(true);
                simulationInstance.simulationMainView().shutdownSimulation();
            } catch (Exception e) {
                AppLogger.error("Application: Error during simulation shutdown: " + simulationInstance.simulationType(), e);
            }
        });

        // Adjust stage size and position to fit the screen. Run later to ensure layout is calculated.
        Platform.runLater(() -> adjustStageLayoutToScreen(stage, root));
    }

    /**
     * Finds the screen containing the stage center point.
     *
     * @param stage stage to evaluate
     * @return matching screen, or the primary screen if none matches
     */
    @SuppressWarnings("MagicNumber")
    private Screen findScreenContainingStage(Stage stage) {
        // Calculate center coordinates of the stage
        double cx = stage.getX() + (stage.getWidth() / 2.0d);
        double cy = stage.getY() + (stage.getHeight() / 2.0d);

        for (Screen s : Screen.getScreens()) {
            if (s.getBounds().contains(cx, cy)) {
                return s;
            }
        }
        // Fallback to primary screen
        return Screen.getPrimary();
    }

    /**
     * Adjusts stage size and position to fit the current screen's visual bounds.
     * <p>
     * Iconified stages are left unchanged, full-screen stages adopt the visual bounds,
     * and maximized or normal stages are resized and repositioned with decoration and
     * margin handling.
     * <p>
     * This method is intended to run from {@link Platform#runLater(Runnable)} after
     * JavaFX has completed the initial layout pass.
     *
     * @param stage stage to adjust
     * @param root scene root associated with the stage
     */
    private void adjustStageLayoutToScreen(Stage stage, Region root) {
        Objects.requireNonNull(stage, "Stage must not be null");
        Objects.requireNonNull(root, "Root region must not be null");

        // Skip if minimized
        if (stage.isIconified()) {
            root.setDisable(false); // Enable now. No adjustments needed.

            AppLogger.info("Application: Stage is minimized. No adjustments made.");
            return;
        }

        // Visual screen bounds
        Screen screen = findScreenContainingStage(stage);
        Rectangle2D visualBounds = screen.getVisualBounds();
        AppLogger.info("Application: Found screen and using visual bounds: " + visualBounds);

        // Handle Fullscreen separately
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

            // Adjust stage size and position to visual bounds
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

            double margin = DEFAULT_WINDOW_MARGIN;

            // Limit root max size to visual bounds minus decoration and margin
            root.setMaxWidth(visualBounds.getWidth() - decoWidth - margin);
            root.setMaxHeight(visualBounds.getHeight() - decoHeight - margin);

            // Stabilize layout calculations another time after changing max sizes
            root.applyCss();
            root.layout();

            // Limit stage max size to visual bounds
            stage.setMaxWidth(visualBounds.getWidth());
            stage.setMaxHeight(visualBounds.getHeight());

            // Set new stage size within visual bounds
            stage.setWidth(Math.min(prefWidth + decoWidth + margin, visualBounds.getWidth()));
            stage.setHeight(Math.min(prefHeight + decoHeight + margin, visualBounds.getHeight()));

            // Set new stage position within visual bounds
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
     * Builds the simulation header shown above the main simulation region.
     *
     * @param stage primary stage
     * @param instance active simulation instance
     * @return header node containing titles and navigation links
     */
    private Node buildSimulationHeaderNode(Stage stage, SimulationInstance instance) {
        SimulationType simulationType = instance.simulationType();

        VBox titleBox = new VBox(FXComponentFactory.createLabel(simulationType.title(), FXStyleClasses.HEADER_TITLE_LABEL));
        titleBox.getStyleClass().add(FXStyleClasses.HEADER_TITLE_VBOX);

        simulationType.subtitle()
                      .ifPresent(subtitle ->
                              titleBox.getChildren().add(FXComponentFactory.createLabel(subtitle, FXStyleClasses.HEADER_SUBTITLE_LABEL)));

        VBox linkBox = new VBox();
        linkBox.setAlignment(Pos.TOP_RIGHT);
        linkBox.getStyleClass().add(FXStyleClasses.HEADER_LINK_VBOX);

        if (simulationType != SimulationType.STARTSCREEN) {
            Hyperlink startScreenLink = new Hyperlink(AppLocalization.getText(AppLocalizationKeys.HEADER_STARTSCREEN_LINK));
            startScreenLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            startScreenLink.setOnAction(_ -> {
                AppLogger.info("Application: Switching to start screen from simulation: " + instance.simulationType());
                instance.region().setDisable(true);
                instance.simulationMainView().shutdownSimulation();
                switchToSimulation(stage, SimulationType.STARTSCREEN);
            });
            linkBox.getChildren().add(startScreenLink);
        }

        Hyperlink aboutLink = new Hyperlink(AppLocalization.getText(AppLocalizationKeys.HEADER_ABOUT_LINK));
        aboutLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
        aboutLink.setOnAction(_ -> new AboutDialog(AppResources.getImages(APP_ICON_PATHS)).showAboutDialog());
        linkBox.getChildren().add(aboutLink);

        simulationType.urlAsURI().ifPresent(url -> {
            Hyperlink urlLink = new Hyperlink(url.toString());
            urlLink.getStyleClass().add(FXStyleClasses.HEADER_URL_HYPERLINK);
            urlLink.setOnAction(_ -> getHostServices().showDocument(url.toString()));
            linkBox.getChildren().add(urlLink);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        HBox simulationHeaderBox = new HBox(titleBox, spacer, linkBox);
        simulationHeaderBox.getStyleClass().add(FXStyleClasses.HEADER_HBOX);

        return simulationHeaderBox;
    }

    /**
     * Starts the JavaFX application.
     * <p>
     * This method installs uncaught-exception handling, resolves the requested
     * simulation type, and shows the primary stage.
     *
     * @param stage primary stage created by JavaFX
     */
    @Override
    public void start(Stage stage) {
        // Initialize exception handling for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.error("Uncaught exception in thread " + thread.getName(), throwable);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(AppLocalization.getText(AppLocalizationKeys.ALERT_TITLE_UNEXPECTED_ERROR));
                alert.setHeaderText(AppLocalization.getText(AppLocalizationKeys.ALERT_HEADER_UNEXPECTED_ERROR));
                alert.setContentText(throwable.getMessage());
                alert.showAndWait();
            });
        });

        // Determine the simulation type from command-line arguments
        AppArgs arguments = new AppArgs(getParameters().getRaw().toArray(new String[0]));
        SimulationType type = determineSimulationType(arguments, true)
                .orElse(SimulationType.STARTSCREEN); // Default to STARTSCREEN if no valid simulation type is found
        AppLogger.info("Application: Starting with simulation type: " + type.name());

        // Initialize and show the primary stage with the appropriate scene
        updateStageIcons(stage);
        switchToSimulation(stage, type);
        stage.show();
        AppLogger.info("Application: Application started successfully. Primary stage is now visible.");
    }

    /**
     * Stops the application and shuts down logging.
     */
    @Override
    public void stop() {
        AppLogger.info("Application: Shutting down.");
        AppLogger.shutdown();
    }

}

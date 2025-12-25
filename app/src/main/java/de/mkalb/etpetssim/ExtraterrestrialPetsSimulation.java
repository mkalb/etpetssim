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
 * The main JavaFX {@link Application} class for the Extraterrestrial Pets Simulation.
 * <p>
 * Responsible for setting up the user interface, handling simulation selection,
 * and managing the application lifecycle events such as startup and shutdown.
 * Command-line argument parsing, logging, and localization are initialized in {@link AppLauncher}.
 * </p>
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
     * Determines the simulation type based on the command-line arguments.
     *
     * @param arguments the parsed application arguments
     * @param onlyImplemented if true, only implemented simulations are considered
     * @return an Optional containing the matching SimulationType, or empty if none found
     */
    @SuppressWarnings("SameParameterValue")
    private Optional<SimulationType> determineSimulationType(AppArgs arguments, boolean onlyImplemented) {
        return arguments.getValue(AppArgs.Key.SIMULATION)
                        .flatMap(arg -> SimulationType.fromCliArgument(arg, onlyImplemented));
    }

    /**
     * Updates the JavaFX stage with the application icons.
     *
     * @param stage the JavaFX stage to update with icons
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
     * Updates the JavaFX stage with a new scene based on the specified simulation type.
     * It is passed as a method reference to the SimulationFactory and the StartMainView.
     *
     * @param stage the JavaFX stage to update
     * @param simulationType the type of simulation to display in the new scene
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
     * Finds the screen that contains the center point of the given stage.
     *
     * @param stage the stage to find the screen for
     * @return the screen containing the center of the stage, or the primary screen if none found
     * @see Screen#getScreens()
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
     * Adjusts the stage size and position to fit within the visual bounds of the screen.
     * <p>
     * Handles three distinct stage states:
     * <ul>
     *   <li><b>Iconified:</b> No adjustments are made</li>
     *   <li><b>Fullscreen:</b> Sets root max dimensions to screen bounds</li>
     *   <li><b>Maximized/Normal:</b> Resizes and repositions the stage within visual bounds,
     *       accounting for window decorations and margins</li>
     * </ul>
     * </p>
     * <p>
     * <b>Important:</b> Must be invoked via {@link Platform#runLater(Runnable)} to ensure
     * layout calculations are complete before adjustments.
     * </p>
     *
     * @param stage the JavaFX stage to adjust; must not be {@code null}
     * @param root the root region of the stage's scene; must not be {@code null}
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

            // noinspection StringConcatenationMissingWhitespace
            AppLogger.info("Application: Stage adjusted to size: " + stage.getWidth() + "x" + stage.getHeight() +
                    " at position: (" + stage.getX() + ", " + stage.getY() + ")");
        }
    }

    /**
     * Builds the header node for the simulation, which includes the title, subtitle, and links.
     *
     * @param stage the JavaFX stage
     * @param instance the simulation instance
     * @return a Node representing the header of the simulation
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

    @Override
    public void start(Stage primaryStage) {
        // Initialize exception handling for uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.error("Uncaught exception in thread " + thread.getName(), throwable);
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unexpected Error");
                alert.setHeaderText("An unexpected error occurred.");
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
        updateStageIcons(primaryStage);
        switchToSimulation(primaryStage, type);
        primaryStage.show();
        AppLogger.info("Application: Application started successfully. Primary stage is now visible.");
    }

    @Override
    public void stop() {
        AppLogger.info("Application: Shutting down.");
        AppLogger.shutdown();
    }

}

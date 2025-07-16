package de.mkalb.etpetssim.ui;

/**
 * A utility class that defines constants for CSS style class names used in the application.
 *
 * <p>This class provides a centralized location for managing style class names, ensuring
 * consistency and reducing the risk of typos. The constants are grouped by their usage
 * context, such as configuration, control, observation, and simulation.</p>
 *
 * <p>Note: This class is not meant to be instantiated.</p>
 */
@SuppressWarnings("SpellCheckingInspection")
public final class FXStyleClasses {

    public static final String CONFIG_HBOX = "config-hbox";
    public static final String CONFIG_SLIDER = "config-slider";
    public static final String CONFIG_SPINNER = "config-spinner";
    public static final String CONFIG_TITLEDPANE = "config-titledpane";
    public static final String CONFIG_VBOX = "config-vbox";
    public static final String CONTROL_BUTTON = "control-button";
    public static final String CONTROL_HBOX = "control-hbox";
    public static final String OBSERVATION_GRID = "observation-grid";
    public static final String OBSERVATION_NAME_LABEL = "observation-name-label";
    public static final String OBSERVATION_VALUE_LABEL = "observation-value-label";
    public static final String SIMULATION_BORDERPANE = "simulation-borderpane";
    public static final String SIMULATION_CANVAS = "simulation-canvas";
    public static final String SIMULATION_SCROLLPANE = "simulation-scrollpane";
    public static final String SIMULATION_STACKPANE = "simulation-stackpane";

    /**
     * Private constructor to prevent instantiation.
     */
    private FXStyleClasses() {
    }

}

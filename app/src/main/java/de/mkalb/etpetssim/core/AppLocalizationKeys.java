package de.mkalb.etpetssim.core;

/**
 * Contains string constants for localization resource keys used in the application.
 * <p>
 * This class provides a central place for referencing keys in resource bundles,
 * ensuring consistency and reducing the risk of typos throughout the codebase.
 * <p>
 * The constants are grouped by their usage context (e.g., configuration, control, observation).
 * <p>
 * This class is not intended to be instantiated.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class AppLocalizationKeys {

    public static final String CONFIG_CELL_EDGE_LENGTH = "config.celledgelength";
    public static final String CONFIG_CELL_EDGE_LENGTH_TOOLTIP = "config.celledgelength.tooltip";
    public static final String CONFIG_GRID_HEIGHT = "config.grid.height";
    public static final String CONFIG_GRID_HEIGHT_TOOLTIP = "config.grid.height.tooltip";
    public static final String CONFIG_GRID_WIDTH = "config.grid.width";
    public static final String CONFIG_GRID_WIDTH_TOOLTIP = "config.grid.width.tooltip";
    public static final String CONFIG_TITLE_INITIALIZATION = "config.title.initialization";
    public static final String CONFIG_TITLE_RULES = "config.title.rules";
    public static final String CONFIG_TITLE_STRUCTURE = "config.title.structure";
    public static final String CONTROL_CANCEL = "control.cancel";
    public static final String CONTROL_PAUSE = "control.pause";
    public static final String CONTROL_RESUME = "control.resume";
    public static final String CONTROL_START = "control.start";
    public static final String CONTROL_STEP_COUNT = "control.stepcount";
    public static final String CONTROL_STEP_COUNT_TOOLTIP = "control.stepcount.tooltip";
    public static final String CONTROL_STEP_DURATION = "control.stepduration";
    public static final String CONTROL_STEP_DURATION_TOOLTIP = "control.stepduration.tooltip";
    public static final String CONTROL_STEP_NUMBER = "control.step.number";
    public static final String CONTROL_STEP_TITLE = "control.step.title";
    public static final String NOTIFICATION_CANVAS_SIZE_LIMIT = "notification.canvas.sizelimit";
    public static final String NOTIFICATION_SIMULATION_TIMEOUT = "notification.simulation.timeout";
    public static final String OBSERVATION_STEP = "observation.step";
    public static final String OBSERVATION_VALUE_UNKNOWN = "observation.valueunknown";

    /**
     * Private constructor to prevent instantiation.
     */
    private AppLocalizationKeys() {
    }

}

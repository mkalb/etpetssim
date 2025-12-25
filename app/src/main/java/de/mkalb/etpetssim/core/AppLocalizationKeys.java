package de.mkalb.etpetssim.core;

/**
 * Contains string constants for localization resource keys used in the application.
 * <p>
 * This class provides a central place for referencing keys in resource bundles,
 * ensuring consistency and reducing the risk of typos throughout the codebase.
 * <p>
 * The constants are generally sorted alphabetically and start with a prefix indicating their
 * usage context, followed by a descriptive name. However, this sorting is not enforced programmatically.
 * <p>
 * The keys correspond to entries in the localization resource files located at {@code i18n/messages_*.properties}.
 * <p>
 * This class is not intended to be instantiated.
 */
@SuppressWarnings("SpellCheckingInspection")
public final class AppLocalizationKeys {

    public static final String ABOUT_TAB_LICENSE = "about.tab.license";
    public static final String ABOUT_TAB_README = "about.tab.readme";
    public static final String ABOUT_TAB_THIRD_PARTY_LICENSES = "about.tab.thirdpartylicenses";
    public static final String ABOUT_TAB_VERSION = "about.tab.version";
    public static final String ABOUT_TITLE = "about.title";
    public static final String CONFIG_CELL_EDGE_LENGTH = "config.celledgelength";
    public static final String CONFIG_CELL_EDGE_LENGTH_TOOLTIP = "config.celledgelength.tooltip";
    public static final String CONFIG_CELL_SHAPE_TOOLTIP = "config.cellshape.tooltip";
    public static final String CONFIG_GRID_EDGE_BEHAVIOR_TOOLTIP = "config.gridedgebehavior.tooltip";
    public static final String CONFIG_GRID_HEIGHT = "config.grid.height";
    public static final String CONFIG_GRID_HEIGHT_TOOLTIP = "config.grid.height.tooltip";
    public static final String CONFIG_GRID_WIDTH = "config.grid.width";
    public static final String CONFIG_GRID_WIDTH_TOOLTIP = "config.grid.width.tooltip";
    public static final String CONFIG_NEIGHBORHOOD_MODE_TOOLTIP = "config.neighborhoodmode.tooltip";
    public static final String CONFIG_SEED = "config.seed";
    public static final String CONFIG_SEED_CLEAR_TOOLTIP = "config.seed.clear.tooltip";
    public static final String CONFIG_SEED_PROMPT = "config.seed.prompt";
    public static final String CONFIG_SEED_TOOLTIP = "config.seed.tooltip";
    public static final String CONFIG_TITLE_INITIALIZATION = "config.title.initialization";
    public static final String CONFIG_TITLE_LAYOUT = "config.title.layout";
    public static final String CONFIG_TITLE_RULES = "config.title.rules";
    public static final String CONFIG_TITLE_STRUCTURE = "config.title.structure";
    public static final String CONTROL_CANCEL = "control.cancel";
    public static final String CONTROL_PAUSE = "control.pause";
    public static final String CONTROL_RESUME = "control.resume";
    public static final String CONTROL_SIMULATION_MODE_TOOLTIP = "control.simulationmode.tooltip";
    public static final String CONTROL_START = "control.start";
    public static final String CONTROL_START_PAUSED = "control.startpaused";
    public static final String CONTROL_START_PAUSED_TOOLTIP = "control.startpaused.tooltip";
    public static final String CONTROL_STEP_COUNT = "control.stepcount";
    public static final String CONTROL_STEP_COUNT_TOOLTIP = "control.stepcount.tooltip";
    public static final String CONTROL_STEP_DURATION = "control.stepduration";
    public static final String CONTROL_STEP_DURATION_TOOLTIP = "control.stepduration.tooltip";
    public static final String CONTROL_STEP_NUMBER = "control.step.number";
    public static final String CONTROL_STEP_TITLE = "control.step.title";
    public static final String CONTROL_TERMINATION_CHECK = "control.terminationcheck";
    public static final String CONTROL_TERMINATION_CHECK_TOOLTIP = "control.terminationcheck.tooltip";
    public static final String HEADER_ABOUT_LINK = "header.about.link";
    public static final String HEADER_STARTSCREEN_LINK = "header.startscreen.link";
    public static final String OBSERVATION_STEP = "observation.step";
    public static final String OBSERVATION_VALUE_UNKNOWN = "observation.valueunknown";
    public static final String WINDOW_TITLE = "window.title";

    /**
     * Private constructor to prevent instantiation.
     */
    private AppLocalizationKeys() {
    }

}

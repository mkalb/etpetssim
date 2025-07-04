package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;

import java.net.URI;
import java.util.*;

/**
 * Enum representing different types of simulations available in the Extraterrestrial Pets Simulation application.
 * Each simulation type has properties such as whether it is implemented, whether it should be shown on the start screen,
 * and various keys for localization and CLI arguments.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum SimulationType {
    /**
     * Represents the start screen of the application.
     * It is the default entry point for users and the fallback if no other simulation is specified.
     * It is not a simulation in the traditional sense but serves as a menu to select other simulations.
     */
    STARTSCREEN(
            false,
            false,
            "simulation.startscreen.title",
            "simulation.startscreen.subtitle",
            "simulation.startscreen.url",
            "simulation.startscreen.emoji",
            "",
            List.of("startscreen", "start")
    ),
    /**
     * The simulation lab is not a specific simulation but a collection of various tests during development.
     */
    SIMULATION_LAB(
            true,
            true,
            "simulation.simulationlab.title",
            "simulation.simulationlab.subtitle",
            "simulation.simulationlab.url",
            "simulation.simulationlab.emoji",
            "simulationlab.css",
            List.of("simulationlab", "lab")
    ),
    /**
     * Extraterrestrial Pets Simulation (ET pets) will be the simulation I developed and designed myself.
     */
    ET_PETS_SIM(
            false,
            true,
            "simulation.etpetssim.title",
            "simulation.etpetssim.subtitle",
            "simulation.etpetssim.url",
            "simulation.etpetssim.emoji",
            "",
            List.of("etpetssim", "etpets")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Wa-Tor">Wa-Tor</a>
     */
    WATOR(
            true,
            true,
            "simulation.wator.title",
            "simulation.wator.subtitle",
            "simulation.wator.url",
            "simulation.wator.emoji",
            "wator.css",
            List.of("wator", "wa-tor")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's Game of Life</a>
     */
    CONWAYS_LIFE(
            false,
            true,
            "simulation.conwayslife.title",
            "simulation.conwayslife.subtitle",
            "simulation.conwayslife.url",
            "simulation.conwayslife.emoji",
            "",
            List.of("conwayslife", "conways-life", "conway", "life", "cgol")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Sugarscape">Sugarscape</a>
     */
    SUGARSCAPE(
            false,
            true,
            "simulation.sugarscape.title",
            "simulation.sugarscape.subtitle",
            "simulation.sugarscape.url",
            "simulation.sugarscape.emoji",
            "",
            List.of("sugarscape", "sugar")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Snake_(video_game_genre)">Snake (video game genre)</a>
     */
    SNAKE(
            false,
            true,
            "simulation.snake.title",
            "simulation.snake.subtitle",
            "simulation.snake.url",
            "simulation.snake.emoji",
            "",
            List.of("snake", "snakes")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Forest-fire_model">Forest-fire model</a>
     */
    FOREST_FIRE(
            false,
            true,
            "simulation.forestfire.title",
            "simulation.forestfire.subtitle",
            "simulation.forestfire.url",
            "simulation.forestfire.emoji",
            "",
            List.of("forestfire", "forest-fire", "fire", "forestfiremodel")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Langton%27s_ant">Langton's ant</a>
     */
    LANGTONS_ANT(
            false,
            true,
            "simulation.langtonsant.title",
            "simulation.langtonsant.subtitle",
            "simulation.langtonsant.url",
            "simulation.langtonsant.emoji",
            "",
            List.of("langtonsant", "langtons-ant", "ant")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Paterson%27s_worms">Paterson's worms</a>
     */
    PATERSONS_WORMS(
            false,
            true,
            "simulation.patersonsworms.title",
            "simulation.patersonsworms.subtitle",
            "simulation.patersonsworms.url",
            "simulation.patersonsworms.emoji",
            "",
            List.of("patersonsworms", "patersons-worms", "worms")
    );

    private final boolean implemented;
    private final boolean showOnStartScreen;
    private final String titleKey;
    private final String subtitleKey;
    private final String urlKey;
    private final String emojiKey;
    private final String cssPath;
    private final List<String> cliArguments;

    SimulationType(boolean implemented, boolean showOnStartScreen,
                   String titleKey, String subtitleKey, String urlKey, String emojiKey,
                   String cssPath,
                   List<String> cliArguments) {
        Objects.requireNonNull(titleKey);
        Objects.requireNonNull(subtitleKey);
        Objects.requireNonNull(urlKey);
        Objects.requireNonNull(emojiKey);
        Objects.requireNonNull(cssPath);
        Objects.requireNonNull(cliArguments);
        this.implemented = implemented;
        this.showOnStartScreen = showOnStartScreen;
        this.titleKey = titleKey;
        this.subtitleKey = subtitleKey;
        this.urlKey = urlKey;
        this.emojiKey = emojiKey;
        this.cssPath = cssPath;
        this.cliArguments = cliArguments;
    }

    /**
     * Returns an Optional containing the SimulationType that matches the provided CLI argument.
     * @param arg the CLI argument to match against the simulation types
     * @param onlyImplemented if true, only considers implemented simulations
     * @return an Optional containing the matching SimulationType, or empty if none found
     */
    public static Optional<SimulationType> fromCliArgument(String arg, boolean onlyImplemented) {
        Objects.requireNonNull(arg, "Argument must not be null");
        return Arrays.stream(values())
                     .filter(sim -> !onlyImplemented || sim.implemented)
                     .filter(sim -> sim.cliArguments.stream().anyMatch(a -> a.equalsIgnoreCase(arg)))
                     .findFirst();
    }

    public boolean isImplemented() {
        return implemented;
    }

    public boolean isShownOnStartScreen() {
        return showOnStartScreen;
    }

    public String titleKey() {
        return titleKey;
    }

    public String subtitleKey() {
        return subtitleKey;
    }

    public String urlKey() {
        return urlKey;
    }

    public String emojiKey() {
        return emojiKey;
    }

    /**
     * Returns the CSS resource relative path for this simulation type.
     *
     * @return the CSS resource relative path as a String, which may be blank if not set
     */
    public String cssPath() {
        return cssPath;
    }

    public List<String> cliArguments() {
        return cliArguments;
    }

    /**
     * Returns the localized title for this simulation type.
     * @return the localized title of the simulation
     */
    public String title() {
        return AppLocalization.getText(titleKey);
    }

    /**
     * Returns the localized subtitle for this simulation type as an Optional.
     * @return an Optional containing the localized subtitle of the simulation, or empty if not available
     */
    public Optional<String> subtitle() {
        return AppLocalization.getOptionalText(subtitleKey);
    }

    /**
     * Returns the localized URL for this simulation type as an Optional.
     * @return an Optional containing the URL of the simulation, or empty if not available
     */
    public Optional<String> url() {
        return AppLocalization.getOptionalText(urlKey);
    }

    /**
     * Returns the localized emoji for this simulation type as an Optional.
     * @return an Optional containing the emoji of the simulation, or empty if not available
     */
    public Optional<String> emoji() {
        return AppLocalization.getOptionalText(emojiKey);
    }

    /**
     * Converts the URL of this simulation type to a URI and returns it as an Optional.
     * @return an Optional containing the URI of the simulation URL, or empty if the URL is invalid or missing
     */
    public Optional<URI> urlAsURI() {
        return url().flatMap(text -> {
            try {
                return Optional.of(URI.create(text));
            } catch (IllegalArgumentException e) {
                AppLogger.warn("SimulationType: Invalid URI format for simulation: " + name());
                return Optional.empty();
            }
        });
    }

    /**
     * Returns the CSS resource path for this simulation type as an Optional.
     * @return an Optional containing the CSS resource path, or empty if the path is blank
     */
    public Optional<String> cssResource() {
        if (cssPath.isBlank()) {
            return Optional.empty();
        }
        return AppResources.getCss(cssPath);
    }

    /**
     * Returns the resource key for the label (title) of the enum SimulationType.
     *
     * @return the resource key for the label of the enum SimulationType
     */
    public static String labelResourceKey() {
        return "simulationtype.label";
    }

}
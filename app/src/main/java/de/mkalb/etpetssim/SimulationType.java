package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;

import java.net.URI;
import java.util.*;

/**
 * Represents the types of simulations available in the Extraterrestrial Pets Simulation application.
 * <p>
 * Each simulation type declares whether it is implemented, whether it should be shown on the
 * start screen, and provides localization keys, a CSS path, and CLI argument aliases.
 * </p>
 */
@SuppressWarnings("SpellCheckingInspection")
public enum SimulationType {
    /**
     * Represents the start screen of the application.
     * It is the default entry point for users and the fallback if no other simulation is specified.
     * It is not a simulation in the traditional sense but serves as a menu to select other simulations.
     */
    STARTSCREEN(
            true,
            false,
            "simulation.start.title",
            "simulation.start.subtitle",
            "simulation.start.url",
            "simulation.start.emoji",
            "",
            List.of("startscreen", "start")
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
            "",
            List.of("wator", "wa-tor")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's Game of Life</a>
     */
    CONWAYS_LIFE(
            true,
            true,
            "simulation.conway.title",
            "simulation.conway.subtitle",
            "simulation.conway.url",
            "simulation.conway.emoji",
            "conway.css",
            List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Langton%27s_ant">Langton's ant</a>
     */
    LANGTONS_ANT(
            true,
            true,
            "simulation.langton.title",
            "simulation.langton.subtitle",
            "simulation.langton.url",
            "simulation.langton.emoji",
            "",
            List.of("langton", "langtonsant", "langtons-ant", "ant")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Forest-fire_model">Forest-fire model</a>
     */
    FOREST_FIRE(
            true,
            true,
            "simulation.forest.title",
            "simulation.forest.subtitle",
            "simulation.forest.url",
            "simulation.forest.emoji",
            "",
            List.of("forestfire", "forest-fire", "forest", "fire", "forestfiremodel")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Sugarscape">Sugarscape</a>
     */
    SUGARSCAPE(
            true,
            true,
            "simulation.sugar.title",
            "simulation.sugar.subtitle",
            "simulation.sugar.url",
            "simulation.sugar.emoji",
            "",
            List.of("sugarscape", "sugar")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Snake_(video_game_genre)">Snake (video game genre)</a>
     */
    SNAKE(
            true,
            true,
            "simulation.snake.title",
            "simulation.snake.subtitle",
            "simulation.snake.url",
            "simulation.snake.emoji",
            "",
            List.of("snake", "snakes")
    ),
    /**
     * Extraterrestrial Pets Simulation (ET pets): a planned custom simulation by the author.
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
     * The simulation lab is not a specific simulation but a collection of various tests during development.
     */
    SIMULATION_LAB(
            true,
            true,
            "simulation.lab.title",
            "simulation.lab.subtitle",
            "simulation.lab.url",
            "simulation.lab.emoji",
            "lab.css",
            List.of("simulationlab", "lab")
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
     * Finds a simulation type by a CLI argument, case-insensitively.
     *
     * @param arg the CLI argument alias to match
     * @param onlyImplemented if {@code true}, only consider simulations marked as implemented
     * @return an Optional containing the matching type, or empty if none matches
     */
    public static Optional<SimulationType> fromCliArgument(String arg, boolean onlyImplemented) {
        return Arrays.stream(values())
                     .filter(sim -> !onlyImplemented || sim.implemented)
                     .filter(sim -> sim.cliArguments.stream().anyMatch(a -> a.equalsIgnoreCase(arg)))
                     .findFirst();
    }

    /**
     * Returns the resource key for the localized label (title) of the enum SimulationType.
     *
     * @return the resource key identifier
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "simulationtype.label";
    }

    /**
     * Indicates whether this simulation type is implemented and usable.
     *
     * @return {@code true} if implemented; {@code false} otherwise
     */
    public boolean isImplemented() {
        return implemented;
    }

    /**
     * Indicates whether this simulation type should be shown on the start screen.
     *
     * @return {@code true} if it should be displayed on the start screen; {@code false} otherwise
     */
    public boolean isShownOnStartScreen() {
        return showOnStartScreen;
    }

    /**
     * Returns the localization key for the title of this simulation type.
     *
     * @return the title resource key
     */
    public String titleKey() {
        return titleKey;
    }

    /**
     * Returns the localization key for the optional subtitle of this simulation type.
     *
     * @return the subtitle resource key
     */
    public String subtitleKey() {
        return subtitleKey;
    }

    /**
     * Returns the localization key for the optional URL of this simulation type.
     *
     * @return the URL resource key
     */
    public String urlKey() {
        return urlKey;
    }

    /**
     * Returns the localization key for the optional emoji of this simulation type.
     *
     * @return the emoji resource key
     */
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

    /**
     * Returns all CLI argument aliases that can be used to select this simulation from the command line.
     *
     * @return an immutable list of CLI argument aliases
     */
    public List<String> cliArguments() {
        return cliArguments;
    }

    /**
     * Returns the localized title for this simulation type.
     *
     * @return the localized title text
     */
    public String title() {
        return AppLocalization.getText(titleKey);
    }

    /**
     * Returns the localized subtitle for this simulation type.
     *
     * @return an Optional containing the subtitle text, if present
     */
    public Optional<String> subtitle() {
        return AppLocalization.getOptionalText(subtitleKey);
    }

    /**
     * Returns the localized URL for this simulation type.
     *
     * @return an Optional containing the URL text, if present
     */
    public Optional<String> url() {
        return AppLocalization.getOptionalText(urlKey);
    }

    /**
     * Returns the localized emoji for this simulation type.
     *
     * @return an Optional containing the emoji, if present
     */
    public Optional<String> emoji() {
        return AppLocalization.getOptionalText(emojiKey);
    }

    /**
     * Converts the localized URL of this simulation type to a {@link URI}.
     *
     * @return an Optional containing the URI, or empty if the URL is missing or invalid
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
     * Returns the full URL to the CSS resource for this simulation type.
     *
     * @return an Optional containing the CSS URL, or empty if no CSS is set
     * @see AppResources#getCssUrl(String)
     * @see java.net.URL#toExternalForm()
     */
    public Optional<String> cssUrl() {
        if (cssPath.isBlank()) {
            return Optional.empty();
        }
        return AppResources.getCssUrl(cssPath);
    }

}
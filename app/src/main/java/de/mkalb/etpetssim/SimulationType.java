package de.mkalb.etpetssim;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;

import java.net.URI;
import java.util.*;

/**
 * Available simulation types.
 * <p>
 * Each constant describes availability, start-screen visibility, localization keys,
 * an optional CSS resource, and accepted command-line aliases.
 */
@SuppressWarnings("SpellCheckingInspection")
public enum SimulationType {
    /**
     * Application start screen used as the default entry view.
     */
    STARTSCREEN(
            true,
            false,
            "simulation.start.title",
            "simulation.start.subtitle",
            "simulation.start.url",
            "",
            List.of("startscreen", "start")
    ),
    /**
     * Custom extraterrestrial pets simulation.
     */
    ET_PETS(
            true,
            true,
            "simulation.etpets.title",
            "simulation.etpets.subtitle",
            "simulation.etpets.url",
            "etpets.css",
            List.of("etpets")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Wa-Tor">Wa-Tor</a> predator-prey simulation.
     */
    WATOR(
            true,
            true,
            "simulation.wator.title",
            "simulation.wator.subtitle",
            "simulation.wator.url",
            "",
            List.of("wator", "wa-tor")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's Game of Life</a>.
     */
    CONWAYS_LIFE(
            true,
            true,
            "simulation.conway.title",
            "simulation.conway.subtitle",
            "simulation.conway.url",
            "conway.css",
            List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Langton%27s_ant">Langton's ant</a>.
     */
    LANGTONS_ANT(
            true,
            true,
            "simulation.langton.title",
            "simulation.langton.subtitle",
            "simulation.langton.url",
            "",
            List.of("langton", "langtonsant", "langtons-ant", "ant")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Forest-fire_model">Forest-fire model</a>.
     */
    FOREST_FIRE(
            true,
            true,
            "simulation.forest.title",
            "simulation.forest.subtitle",
            "simulation.forest.url",
            "",
            List.of("forestfire", "forest-fire", "forest", "fire", "forestfiremodel")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Sugarscape">Sugarscape</a>.
     */
    SUGARSCAPE(
            true,
            true,
            "simulation.sugar.title",
            "simulation.sugar.subtitle",
            "simulation.sugar.url",
            "",
            List.of("sugarscape", "sugar")
    ),
    /**
     * <a href="https://en.wikipedia.org/wiki/Snake_(video_game_genre)">Snake</a> simulation.
     */
    SNAKE(
            true,
            true,
            "simulation.snake.title",
            "simulation.snake.subtitle",
            "simulation.snake.url",
            "",
            List.of("snake", "snakes")
    ),
    /**
     * Custom rebounding-entities simulation.
     */
    REBOUNDING_ENTITIES(
            true,
            true,
            "simulation.rebounding.title",
            "simulation.rebounding.subtitle",
            "simulation.rebounding.url",
            "",
            List.of("reboundingentities", "rebounding-entities", "rebounding-entity", "rebounding", "rebound", "rebounders")
    ),
    /**
     * Development-oriented simulation laboratory.
     */
    SIMULATION_LAB(
            true,
            true,
            "simulation.lab.title",
            "simulation.lab.subtitle",
            "simulation.lab.url",
            "lab.css",
            List.of("simulationlab", "lab")
    );

    private final boolean implemented;
    private final boolean showOnStartScreen;
    private final String titleKey;
    private final String subtitleKey;
    private final String urlKey;
    private final String cssPath;
    private final List<String> cliArguments;

    SimulationType(boolean implemented, boolean showOnStartScreen,
                   String titleKey, String subtitleKey, String urlKey,
                   String cssPath,
                   List<String> cliArguments) {
        this.implemented = implemented;
        this.showOnStartScreen = showOnStartScreen;
        this.titleKey = titleKey;
        this.subtitleKey = subtitleKey;
        this.urlKey = urlKey;
        this.cssPath = cssPath;
        this.cliArguments = cliArguments;
    }

    /**
     * Resolves a simulation type from a command-line alias.
     *
     * @param arg alias to match, case-insensitively
     * @param onlyImplemented whether to restrict matching to implemented simulations
     * @return an {@link Optional} containing the matching type, or empty if unmatched
     */
    public static Optional<SimulationType> fromCliArgument(String arg, boolean onlyImplemented) {
        return Arrays.stream(values())
                     .filter(sim -> !onlyImplemented || sim.implemented)
                     .filter(sim -> sim.cliArguments.stream().anyMatch(a -> a.equalsIgnoreCase(arg)))
                     .findFirst();
    }

    /**
     * Returns the localization key for the simulation-type field label.
     *
     * @return localization key identifier
     */
    @SuppressWarnings("SameReturnValue")
    public static String labelResourceKey() {
        return "simulationtype.label";
    }

    /**
     * Returns whether this simulation is implemented.
     *
     * @return {@code true} if implemented, otherwise {@code false}
     */
    public boolean isImplemented() {
        return implemented;
    }

    /**
     * Returns whether this simulation should appear on the start screen.
     *
     * @return {@code true} if shown on the start screen, otherwise {@code false}
     */
    public boolean isShownOnStartScreen() {
        return showOnStartScreen;
    }

    /**
     * Returns the localization key for the title.
     *
     * @return title resource key
     */
    public String titleKey() {
        return titleKey;
    }

    /**
     * Returns the localization key for the subtitle.
     *
     * @return subtitle resource key
     */
    public String subtitleKey() {
        return subtitleKey;
    }

    /**
     * Returns the localization key for the external URL.
     *
     * @return URL resource key
     */
    public String urlKey() {
        return urlKey;
    }

    /**
     * Returns the simulation-specific CSS resource path.
     *
     * @return CSS path relative to the CSS resource folder, or blank if none is configured
     */
    public String cssPath() {
        return cssPath;
    }

    /**
     * Returns all accepted command-line aliases.
     *
     * @return immutable list of aliases
     */
    public List<String> cliArguments() {
        return cliArguments;
    }

    /**
     * Returns the localized title.
     *
     * @return localized title text
     */
    public String title() {
        return AppLocalization.getText(titleKey);
    }

    /**
     * Returns the localized subtitle, if available.
     *
     * @return an {@link Optional} containing non-blank subtitle text
     */
    public Optional<String> subtitle() {
        return AppLocalization.getOptionalText(subtitleKey);
    }

    /**
     * Returns the localized external URL, if available.
     *
     * @return an {@link Optional} containing non-blank URL text
     */
    public Optional<String> url() {
        return AppLocalization.getOptionalText(urlKey);
    }

    /**
     * Converts the localized URL to a {@link URI}.
     *
     * @return an {@link Optional} containing the parsed URI, or empty if missing or invalid
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
     * Resolves the simulation-specific CSS resource to an external URL string.
     *
     * @return an {@link Optional} containing the CSS URL, or empty if no CSS is configured or found
     * @see AppResources#getCssUrl(String)
     */
    public Optional<String> cssUrl() {
        if (cssPath.isBlank()) {
            return Optional.empty();
        }
        return AppResources.getCssUrl(cssPath);
    }

}
package de.mkalb.etpetssim.simulations.core;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import de.mkalb.etpetssim.core.AppResources;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Available simulation types.
 *
 * <p>Each constant describes its category, localization keys, an optional CSS resource, and accepted command-line
 * aliases. A type appears on the start screen if and only if it has a category.
 *
 * <p>The declaration order defines the order of the simulations within a category on the start screen.
 */
public enum SimulationType {

    /**
     * Custom extraterrestrial pets simulation.
     */
    ET_PETS(
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
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
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
            "simulation.wator.title",
            "simulation.wator.subtitle",
            "simulation.wator.url",
            "",
            List.of("wator", "wa-tor")
    ),

    /**
     * <a href="https://en.wikipedia.org/wiki/Sugarscape">Sugarscape</a>.
     */
    SUGARSCAPE(
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
            "simulation.sugar.title",
            "simulation.sugar.subtitle",
            "simulation.sugar.url",
            "",
            List.of("sugarscape", "sugar")
    ),

    /**
     * <a href="https://en.wikipedia.org/wiki/Langton%27s_ant">Langton's ant</a>.
     */
    @SuppressWarnings("SpellCheckingInspection")
    LANGTONS_ANT(
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
            "simulation.langton.title",
            "simulation.langton.subtitle",
            "simulation.langton.url",
            "",
            List.of("langton", "langtonsant", "langtons-ant", "ant")
    ),

    /**
     * <a href="https://en.wikipedia.org/wiki/Snake_(video_game_genre)">Snake</a> simulation.
     */
    SNAKE(
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
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
            SimulationCategory.AGENT_AND_MOVEMENT_BASED_MODEL,
            "simulation.rebounding.title",
            "simulation.rebounding.subtitle",
            "simulation.rebounding.url",
            "",
            List.of("reboundingentities", "rebounding-entities", "rebounding-entity", "rebounding", "rebound",
                    "rebounders")
    ),

    /**
     * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's Game of Life</a>.
     */
    @SuppressWarnings("SpellCheckingInspection")
    CONWAYS_LIFE(
            SimulationCategory.CELLULAR_AUTOMATON,
            "simulation.conway.title",
            "simulation.conway.subtitle",
            "simulation.conway.url",
            "conway.css",
            List.of("conwayslife", "conways-life", "conway", "conways", "life", "cgol")
    ),

    /**
     * <a href="https://en.wikipedia.org/wiki/Forest-fire_model">Forest-fire model</a>.
     */
    FOREST_FIRE(
            SimulationCategory.CELLULAR_AUTOMATON,
            "simulation.forest.title",
            "simulation.forest.subtitle",
            "simulation.forest.url",
            "",
            List.of("forestfire", "forest-fire", "forest", "fire", "forestfiremodel")
    ),

    /**
     * Development-oriented simulation laboratory.
     *
     * <p>Hidden from the start screen, because it has no category; it is started only with a command-line alias.
     */
    SIMULATION_LAB(
            null,
            "simulation.lab.title",
            "simulation.lab.subtitle",
            "simulation.lab.url",
            "lab.css",
            List.of("simulationlab", "lab")
    );

    private final @Nullable SimulationCategory category;
    private final String titleKey;
    private final String subtitleKey;
    private final String urlKey;
    private final String cssPath;
    private final List<String> cliArguments;

    SimulationType(@Nullable SimulationCategory category,
                   String titleKey,
                   String subtitleKey,
                   String urlKey,
                   String cssPath,
                   List<String> cliArguments) {
        this.category = category;
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
     * @return an {@link Optional} containing the matching type, or empty if unmatched
     */
    public static Optional<SimulationType> fromCliArgument(String arg) {
        return Arrays.stream(values())
                .filter(sim -> sim.cliArguments.stream().anyMatch(a -> a.equalsIgnoreCase(arg)))
                .findFirst();
    }

    /**
     * Returns the category of the simulation, which groups it on the start screen.
     *
     * @return an {@link Optional} containing the category, or empty if the type is hidden from the start screen
     */
    public Optional<SimulationCategory> category() {
        return Optional.ofNullable(category);
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
     * <p>The key exists in all resource bundles. An empty value means that the simulation has no link in this
     * locale; {@link #url()} is then empty.
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
    public Optional<URI> uri() {
        return url().flatMap(text -> {
            try {
                return Optional.of(URI.create(text));
            } catch (IllegalArgumentException e) {
                AppLogger.warn("SimulationType: Ignoring invalid URI for simulation: " + name());
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

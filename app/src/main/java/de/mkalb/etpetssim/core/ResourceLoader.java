package de.mkalb.etpetssim.core;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Utility class for loading various types of resources such as property bundles,
 * CSS files, and images from the classpath.
 * <p>
 * Resources are expected to be located under the standard Maven/Gradle directory:
 * {@code src/main/resources/}, organized by type (e.g., {@code /css/}, {@code /images/}, {@code /i18n/}).
 */
public final class ResourceLoader {

    private static final String DEFAULT_BUNDLE_PATH = "i18n.messages";

    /**
     * Private constructor to prevent instantiation.
     */
    private ResourceLoader() {
    }

    /**
     * Loads the default resource bundle for the given locale.
     *
     * @param locale the locale to use for loading the bundle
     * @return an Optional containing the ResourceBundle if found
     */
    public static Optional<ResourceBundle> getBundle(Locale locale) {
        Objects.requireNonNull(locale, "locale must not be null");
        return getBundle(DEFAULT_BUNDLE_PATH, locale);
    }

    /**
     * Loads a resource bundle from a specific base name and locale.
     *
     * @param baseName the base name of the resource bundle
     * @param locale   the locale to use for loading the bundle
     * @return an Optional containing the ResourceBundle if found
     */
    public static Optional<ResourceBundle> getBundle(String baseName, Locale locale) {
        Objects.requireNonNull(baseName, "baseName must not be null");
        Objects.requireNonNull(locale, "locale must not be null");
        try {
            return Optional.of(ResourceBundle.getBundle(baseName, locale));
        } catch (MissingResourceException e) {
            // TODO: Replace with LoggingManager
            System.err.println("Failed to load resource bundle: " + baseName + " for locale " + locale + " - " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Loads a CSS file as a URL string.
     *
     * @param relativePath the relative path to the CSS file under /css/
     * @return an Optional containing the external form of the URL
     */
    public static Optional<String> getCss(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        URL url = ResourceLoader.class.getResource("/css/" + relativePath);
        if (url == null) {
            // TODO: Replace with LoggingManager
            System.err.println("CSS resource not found: /css/" + relativePath);
            return Optional.empty();
        }
        return Optional.of(url.toExternalForm());
    }

    /**
     * Loads an image from the /images/ directory.
     *
     * @param relativePath the relative path to the image file
     * @return an Optional containing the loaded Image
     */
    public static Optional<Image> getImage(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        InputStream stream = ResourceLoader.class.getResourceAsStream("/images/" + relativePath);
        if (stream == null) {
            // TODO: Replace with LoggingManager
            System.err.println("Image resource not found: /images/" + relativePath);
            return Optional.empty();
        }
        return Optional.of(new Image(stream));
    }

    public static List<Image> getImages(String... relativePaths) {
        Objects.requireNonNull(relativePaths, "relativePaths must not be null");
        List<Image> images = new ArrayList<>();
        for (String relativePath : relativePaths) {
            Optional<Image> image = getImage(relativePath);
            image.ifPresent(images::add);
        }
        return images;
    }

    /**
     * Loads any resource as an InputStream.
     *
     * @param relativePath the relative path to the resource
     * @return an Optional containing the InputStream
     */
    public static Optional<InputStream> getResourceAsStream(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        InputStream stream = ResourceLoader.class.getResourceAsStream("/" + relativePath);
        if (stream == null) {
            // TODO: Replace with LoggingManager
            System.err.println("Resource stream not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(stream);
    }

    /**
     * Returns a URL to any resource.
     *
     * @param relativePath the relative path to the resource
     * @return an Optional containing the URL
     */
    public static Optional<URL> getResource(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        URL url = ResourceLoader.class.getResource("/" + relativePath);
        if (url == null) {
            // TODO: Replace with LoggingManager
            System.err.println("Resource URL not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(url);
    }

    /**
     * Checks whether a resource exists.
     *
     * @param relativePath the relative path to the resource
     * @return {@code true} if the resource exists, {@code false} otherwise
     */
    public static boolean resourceExists(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        return getResource(relativePath).isPresent();
    }

}




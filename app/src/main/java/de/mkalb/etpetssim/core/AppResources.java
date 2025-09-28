package de.mkalb.etpetssim.core;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Utility class for loading various types of resources such as property bundles,
 * CSS files, and images from the classpath.
 * <p>
 * Resources are expected to be located under the standard Maven/Gradle directory:
 * {@code src/main/resources/}, organized by type (e.g., {@code /css/}, {@code /images/}, {@code /i18n/}).
 */
public final class AppResources {

    /**
     * The base name of the resource bundle used for internationalization.
     */
    public static final String BUNDLE_BASE_NAME = "i18n.messages";
    /**
     * The folder path for CSS resources.
     */
    public static final String FOLDER_CSS = "css/";
    /**
     * The folder path for image resources.
     */
    public static final String FOLDER_IMAGES = "images/";

    /**
     * Private constructor to prevent instantiation.
     */
    private AppResources() {
    }

    /**
     * Loads the default resource bundle for the given locale.
     *
     * @param locale the locale to use for loading the bundle
     * @return an Optional containing the ResourceBundle if found
     */
    public static Optional<ResourceBundle> getBundle(Locale locale) {
        Objects.requireNonNull(locale, "locale must not be null");
        return getBundle(BUNDLE_BASE_NAME, locale);
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
            AppLogger.error("Resource bundle not found: " + baseName + " for locale " + locale, e);
            return Optional.empty();
        }
    }

    /**
     * Loads a CSS file as a URL string.
     *
     * @param relativePath the relative path to the CSS file under /css/
     * @return an Optional containing the external form of the URL
     * @see #FOLDER_CSS
     */
    public static Optional<String> getCss(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        String name = "/" + FOLDER_CSS + relativePath;
        URL url = AppResources.class.getResource(name);
        if (url == null) {
            AppLogger.error("CSS resource not found: " + name);
            return Optional.empty();
        }
        return Optional.of(url.toExternalForm());
    }

    /**
     * Loads an image from the /images/ directory.
     *
     * @param relativePath the relative path to the image file
     * @return an Optional containing the loaded Image
     * @see #FOLDER_IMAGES
     */
    public static Optional<Image> getImage(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        String name = "/" + FOLDER_IMAGES + relativePath;
        InputStream stream = AppResources.class.getResourceAsStream(name);
        if (stream == null) {
            AppLogger.error("Image resource not found: " + name);
            return Optional.empty();
        }
        return Optional.of(new Image(stream));
    }

    /**
     * Loads multiple images from the /images/ directory.
     *
     * @param relativePaths the relative paths to the image files
     * @return a List of loaded Images
     * @see #FOLDER_IMAGES
     */
    public static List<Image> getImages(String... relativePaths) {
        Objects.requireNonNull(relativePaths, "relativePaths must not be null");
        List<Image> images = new ArrayList<>(relativePaths.length);
        for (String relativePath : relativePaths) {
            getImage(relativePath).ifPresent(images::add);
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
        InputStream stream = AppResources.class.getResourceAsStream("/" + relativePath);
        if (stream == null) {
            AppLogger.error("Resource not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(stream);
    }

    /**
     * Loads a resource from the classpath as a String using the specified charset.
     *
     * @param relativePath the relative path to the resource
     * @param charset the charset to use for decoding the resource bytes
     * @return an Optional containing the resource content as a String, or Optional.empty() if not found or an error occurs
     */
    public static Optional<String> getResourceAsString(String relativePath, Charset charset) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        Objects.requireNonNull(charset, "charset must not be null");
        try (InputStream stream = AppResources.class.getResourceAsStream("/" + relativePath)) {
            if (stream == null) {
                AppLogger.error("Resource not found: /" + relativePath);
                return Optional.empty();
            }
            return Optional.of(new String(stream.readAllBytes(), charset));
        } catch (Exception e) {
            AppLogger.error("Failed to read resource as string: /" + relativePath, e);
            return Optional.empty();
        }
    }

    /**
     * Loads any resource as a URL.
     *
     * @param relativePath the relative path to the resource
     * @return an Optional containing the URL
     */
    public static Optional<URL> getResourceAsURL(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        URL url = AppResources.class.getResource("/" + relativePath);
        if (url == null) {
            AppLogger.error("Resource not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(url);
    }

}




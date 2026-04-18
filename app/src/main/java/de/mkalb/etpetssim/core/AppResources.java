package de.mkalb.etpetssim.core;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Utility methods for loading classpath resources.
 * <p>
 * Supported resource types include bundles, CSS files, images, streams, strings,
 * and URLs.
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
     * Loads the default localization bundle for a locale.
     *
     * @param locale bundle locale
     * @return an {@link Optional} containing the bundle, or empty if unavailable
     * @throws NullPointerException if {@code locale} is {@code null}
     */
    public static Optional<ResourceBundle> getBundle(Locale locale) {
        Objects.requireNonNull(locale, "locale must not be null");
        return getBundle(BUNDLE_BASE_NAME, locale);
    }

    /**
     * Loads a resource bundle for a base name and locale.
     *
     * @param baseName bundle base name
     * @param locale bundle locale
     * @return an {@link Optional} containing the bundle, or empty if unavailable
     * @throws NullPointerException if {@code baseName} or {@code locale} is {@code null}
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
     * Resolves a CSS resource to its external URL string.
     *
     * @param relativePath path below {@code /css/} without a leading slash
     * @return an {@link Optional} containing the external URL string, or empty if missing
     * @throws NullPointerException if {@code relativePath} is {@code null}
     * @see #FOLDER_CSS
     */
    public static Optional<String> getCssUrl(String relativePath) {
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
     * Loads an image from {@code /images/}.
     *
     * @param relativePath path below {@code /images/} without a leading slash
     * @return an {@link Optional} containing the decoded image, or empty on lookup/decode errors
     * @throws NullPointerException if {@code relativePath} is {@code null}
     * @see #FOLDER_IMAGES
     */
    public static Optional<Image> getImage(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        String name = "/" + FOLDER_IMAGES + relativePath;
        URL url = AppResources.class.getResource(name);
        if (url == null) {
            AppLogger.error("Image resource not found: " + name);
            return Optional.empty();
        }
        // backgroundLoading=false => load/decoding completes in constructor
        Image image = new Image(url.toExternalForm(), 0, 0,
                true, true,
                false);
        if (image.isError()) {
            AppLogger.error("Failed to decode image: " + name, image.getException());
            return Optional.empty();
        }
        return Optional.of(image);
    }

    /**
     * Loads multiple images from {@code /images/}.
     *
     * @param relativePaths image paths without leading slashes
     * @return list of successfully loaded images; missing/invalid entries are skipped
     * @throws NullPointerException if {@code relativePaths} is {@code null}
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
     * Opens a classpath resource as stream.
     *
     * @param relativePath resource path without leading slash
     * @return an {@link Optional} containing the stream, or empty if missing
     * @throws NullPointerException if {@code relativePath} is {@code null}
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
     * Loads a classpath resource as text.
     *
     * @param relativePath resource path without leading slash
     * @param charset charset used to decode bytes
     * @return an {@link Optional} containing resource text, or empty on lookup/read errors
     * @throws NullPointerException if {@code relativePath} or {@code charset} is {@code null}
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
     * Resolves a classpath resource to a URL.
     *
     * @param relativePath resource path without leading slash
     * @return an {@link Optional} containing the URL, or empty if missing
     * @throws NullPointerException if {@code relativePath} is {@code null}
     */
    public static Optional<URL> getResourceAsUrl(String relativePath) {
        Objects.requireNonNull(relativePath, "relativePath must not be null");
        URL url = AppResources.class.getResource("/" + relativePath);
        if (url == null) {
            AppLogger.error("Resource not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(url);
    }

}




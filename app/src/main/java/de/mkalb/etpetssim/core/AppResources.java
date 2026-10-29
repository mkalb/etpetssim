package de.mkalb.etpetssim.core;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Access to the resources of the application module: resource bundles, CSS files, images, streams, text, and URLs.
 *
 * <p>Missing or unreadable resources are logged and returned as an empty {@link Optional} or skipped.
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
        Objects.requireNonNull(locale, "Locale must not be null");
        return getBundle(BUNDLE_BASE_NAME, locale);
    }

    /**
     * Loads a resource bundle for a base name and locale.
     *
     * @param baseName bundle base name
     * @param locale   bundle locale
     * @return an {@link Optional} containing the bundle, or empty if unavailable
     * @throws NullPointerException if {@code baseName} or {@code locale} is {@code null}
     */
    public static Optional<ResourceBundle> getBundle(String baseName, Locale locale) {
        Objects.requireNonNull(baseName, "Base name must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");
        try {
            return Optional.of(ResourceBundle.getBundle(baseName, locale));
        } catch (MissingResourceException e) {
            AppLogger.error(e, "AppResources: Resource bundle not found: " + baseName + " for locale " + locale);
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
        Objects.requireNonNull(relativePath, "Relative path must not be null");
        return getResourceAsUrl(FOLDER_CSS + relativePath).map(URL::toExternalForm);
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
        Objects.requireNonNull(relativePath, "Relative path must not be null");
        Optional<URL> url = getResourceAsUrl(FOLDER_IMAGES + relativePath);
        if (url.isEmpty()) {
            return Optional.empty();
        }
        // backgroundLoading=false => load/decoding completes in constructor
        Image image = new Image(url.get().toExternalForm(), 0, 0,
                true, true,
                false);
        if (image.isError()) {
            AppLogger.error(image.getException(), "AppResources: Failed to decode image: /" + FOLDER_IMAGES + relativePath);
            return Optional.empty();
        }
        return Optional.of(image);
    }

    /**
     * Loads multiple images from {@code /images/}.
     *
     * @param relativePaths image paths without leading slashes
     * @return list of successfully loaded images; missing or undecodable resources are skipped
     * @throws NullPointerException if {@code relativePaths} is {@code null} or contains {@code null} elements
     * @see #FOLDER_IMAGES
     */
    public static List<Image> getImages(String... relativePaths) {
        Objects.requireNonNull(relativePaths, "Relative paths must not be null");
        List<Image> images = new ArrayList<>(relativePaths.length);
        for (String relativePath : relativePaths) {
            getImage(relativePath).ifPresent(images::add);
        }
        return images;
    }

    /**
     * Opens a resource of the application module as stream.
     *
     * <p>The caller owns the returned stream and must close it.
     *
     * @param relativePath resource path without leading slash
     * @return an {@link Optional} containing the stream, or empty if missing
     * @throws NullPointerException if {@code relativePath} is {@code null}
     */
    public static Optional<InputStream> getResourceAsStream(String relativePath) {
        Objects.requireNonNull(relativePath, "Relative path must not be null");
        InputStream stream = AppResources.class.getResourceAsStream("/" + relativePath);
        if (stream == null) {
            AppLogger.error("AppResources: Resource not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(stream);
    }

    /**
     * Loads a resource of the application module as text.
     *
     * @param relativePath resource path without leading slash
     * @param charset      charset used to decode bytes
     * @return an {@link Optional} containing resource text, or empty on lookup/read errors
     * @throws NullPointerException if {@code relativePath} or {@code charset} is {@code null}
     */
    public static Optional<String> getResourceAsString(String relativePath, Charset charset) {
        Objects.requireNonNull(relativePath, "Relative path must not be null");
        Objects.requireNonNull(charset, "Charset must not be null");
        try (InputStream stream = AppResources.class.getResourceAsStream("/" + relativePath)) {
            if (stream == null) {
                AppLogger.error("AppResources: Resource not found: /" + relativePath);
                return Optional.empty();
            }
            return Optional.of(new String(stream.readAllBytes(), charset));
        } catch (IOException e) {
            AppLogger.error(e, "AppResources: Failed to read resource as string: /" + relativePath);
            return Optional.empty();
        }
    }

    /**
     * Resolves a resource of the application module to a URL.
     *
     * @param relativePath resource path without leading slash
     * @return an {@link Optional} containing the URL, or empty if missing
     * @throws NullPointerException if {@code relativePath} is {@code null}
     */
    public static Optional<URL> getResourceAsUrl(String relativePath) {
        Objects.requireNonNull(relativePath, "Relative path must not be null");
        URL url = AppResources.class.getResource("/" + relativePath);
        if (url == null) {
            AppLogger.error("AppResources: Resource not found: /" + relativePath);
            return Optional.empty();
        }
        return Optional.of(url);
    }

}

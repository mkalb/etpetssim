package de.mkalb.etpetssim.core;

import de.mkalb.FxTestSupport;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AppResourcesTest {

    private static final Locale TEST_LOCALE = Locale.GERMANY;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        FxTestSupport.ensureStarted();
    }

    /**
     * Tests the retrieval of the default resource bundle for a specific locale.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The resource bundle for the specified locale must exist in the classpath.</li>
     *     <li>The bundle should contain a key "greeting" with a corresponding value.</li>
     * </ul>
     *
     * <p>If the resource bundle is not present or does not contain the expected key, the test will fail.</p>
     */
    @Test
    void testGetBundleDefaultWithLocale() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Default resource bundle should be present for locale");
        assertTrue(bundleOpt.get().containsKey("greeting"), "Bundle should contain key 'greeting'");

        assertThrows(NullPointerException.class, () -> AppResources.getBundle(null));
    }

    /**
     * Tests the retrieval of a resource bundle by its base name and locale.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The resource bundle with the specified base name must exist in the classpath.</li>
     *     <li>The bundle should contain a key "greeting" with a corresponding value.</li>
     * </ul>
     *
     * <p>If the resource bundle is not present or does not contain the expected key, the test will fail.</p>
     */
    @Test
    void testGetBundleWithBaseNameAndLocale() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Resource bundle should be present for base name and locale");
        assertTrue(bundleOpt.get().containsKey("greeting"), "Bundle should contain key 'greeting'");

        assertThrows(NullPointerException.class, () -> AppResources.getBundle(null, TEST_LOCALE));
        assertThrows(NullPointerException.class, () -> AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, null));
    }

    @Test
    void testMissingBundle() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle("invalid.bundle", Locale.ENGLISH);
        assertFalse(bundleOpt.isPresent(), "Missing bundle should not be present");
    }

    /**
     * Tests the retrieval of a CSS file by its filename.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The CSS file "etpetssim.css" must exist in the /css/ directory.</li>
     * </ul>
     *
     * <p>If this CSS file is not present, the test will fail.</p>
     */
    @Test
    void testGetCssUrl() {
        Optional<String> cssUrlOpt = AppResources.getCssUrl("etpetssim.css");
        assertTrue(cssUrlOpt.isPresent(), "CSS URL should be present");
        assertTrue(cssUrlOpt.get().endsWith("etpetssim.css"), "CSS URL should end with 'etpetssim.css'");

        assertThrows(NullPointerException.class, () -> AppResources.getCssUrl(null));
    }

    @Test
    void testMissingCssUrl() {
        Optional<String> cssUrlOpt = AppResources.getCssUrl("missing.css");
        assertFalse(cssUrlOpt.isPresent(), "Missing CSS should not be present");
    }

    /**
     * Tests the retrieval of an image by its filename.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The image "etpetssim16.png" must exist in the /images/ directory.</li>
     * </ul>
     *
     * <p>If this image is not present, the test will fail.</p>
     */
    @Test
    void testGetImage() {
        Optional<Image> imageOpt = AppResources.getImage("etpetssim16.png");
        assertTrue(imageOpt.isPresent(), "Image should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getImage(null));
    }

    /**
     * Tests the retrieval of multiple images, including duplicates and missing images.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>Images "etpetssim16.png" and "etpetssim32.png" must exist in the /images/ directory.</li>
     *     <li>"unknown.png" is a placeholder for a missing image.</li>
     * </ul>
     *
     * <p>If these images are not present, the test will fail.</p>
     */
    @Test
    void testGetImages() {
        List<Image> images = AppResources.getImages("etpetssim16.png", "etpetssim16.png", "unknown.png",
                "etpetssim32.png");
        assertEquals(3, images.size(), "Number of images should be 3");
        assertTrue(images.stream().allMatch(Objects::nonNull), "All images should be non-null");

        assertThrows(NullPointerException.class, () -> AppResources.getImages((String[]) null));
    }

    @Test
    void testMissingImage() {
        Optional<Image> imageOpt = AppResources.getImage("unknown.png");
        assertFalse(imageOpt.isPresent(), "Missing image should not be present");
    }

    @Test
    void testIllegalImage() {
        Optional<Image> imageOpt = AppResources.getImage("illegal_image.png");
        assertFalse(imageOpt.isPresent(), "Illegal image should not be present");
    }

    /**
     * Tests the retrieval of a resource as an InputStream.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The resource "css/etpetssim.css" must exist in the classpath.</li>
     * </ul>
     *
     * <p>If this resource is not present, the test will fail.</p>
     */
    @Test
    void testGetResourceAsStream() {
        Optional<InputStream> streamOpt = AppResources.getResourceAsStream("css/etpetssim.css");
        assertTrue(streamOpt.isPresent(), "InputStream should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsStream(null));
    }

    @Test
    void testMissingResourceAsStream() {
        Optional<InputStream> streamOpt = AppResources.getResourceAsStream("missing/file.txt");
        assertFalse(streamOpt.isPresent(), "Missing resource stream should not be present");
    }

    @Test
    void testGetResourceAsString() {
        Optional<String> resOpt = AppResources.getResourceAsString("LICENSE", StandardCharsets.UTF_8);
        assertTrue(resOpt.isPresent(), "Resource string should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsString(null, StandardCharsets.UTF_8));
        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsString("", null));
    }

    @Test
    void testMissingResourceString() {
        Optional<String> resOpt = AppResources.getResourceAsString("missing/file.txt", StandardCharsets.UTF_8);
        assertFalse(resOpt.isPresent(), "Missing string should not be present");
    }

    /**
     * Tests the retrieval of a resource as a URL.
     *
     * <p><b>Test data prerequisites:</b></p>
     * <ul>
     *     <li>The resource "css/etpetssim.css" must exist in the classpath.</li>
     * </ul>
     *
     * <p>If this resource is not present, the test will fail.</p>
     */
    @Test
    void testGetResourceAsUrl() {
        Optional<URL> urlOpt = AppResources.getResourceAsUrl("css/etpetssim.css");
        assertTrue(urlOpt.isPresent(), "URL should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsUrl(null));
    }

    @Test
    void testMissingResourceAsUrl() {
        Optional<URL> urlOpt = AppResources.getResourceAsUrl("missing/file.txt");
        assertFalse(urlOpt.isPresent(), "Missing resource URL should not be present");
    }

}


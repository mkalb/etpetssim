package de.mkalb.etpetssim.core;

import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AppResourcesTest {

    private static final Locale TEST_LOCALE = Locale.GERMANY;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
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
    @SuppressWarnings("DataFlowIssue")
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
    @SuppressWarnings("DataFlowIssue")
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
    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetCss() {
        Optional<String> cssUrlOpt = AppResources.getCss("etpetssim.css");
        assertTrue(cssUrlOpt.isPresent(), "CSS URL should be present");
        assertTrue(cssUrlOpt.get().endsWith("etpetssim.css"), "CSS URL should end with 'etpetssim.css'");

        assertThrows(NullPointerException.class, () -> AppResources.getCss(null));
    }

    @Test
    void testMissingCss() {
        Optional<String> cssUrlOpt = AppResources.getCss("missing.css");
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
    @SuppressWarnings("DataFlowIssue")
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
    @SuppressWarnings("DataFlowIssue")
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
    @SuppressWarnings("DataFlowIssue")
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
    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetResourceAsURL() {
        Optional<URL> urlOpt = AppResources.getResourceAsURL("css/etpetssim.css");
        assertTrue(urlOpt.isPresent(), "URL should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsURL(null));
    }

    @Test
    void testMissingResourceAsURL() {
        Optional<URL> urlOpt = AppResources.getResourceAsURL("missing/file.txt");
        assertFalse(urlOpt.isPresent(), "Missing resource URL should not be present");
    }

}


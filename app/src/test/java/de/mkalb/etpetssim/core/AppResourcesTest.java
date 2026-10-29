package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class AppResourcesTest {

    private static final Locale TEST_LOCALE = Locale.GERMANY;
    private static final String MAIN_BUNDLE_KEY = AppLocalizationKeys.ABOUT_RESOURCE_NOT_FOUND;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        FXTestSupport.ensureStarted();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetBundle() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Default resource bundle should be present for locale");
        assertTrue(bundleOpt.get().containsKey(MAIN_BUNDLE_KEY), "Bundle should contain key " + MAIN_BUNDLE_KEY);

        assertThrows(NullPointerException.class, () -> AppResources.getBundle(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetBundleWithBaseNameReturnsBundle() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Resource bundle should be present for base name and locale");
        assertTrue(bundleOpt.get().containsKey(MAIN_BUNDLE_KEY), "Bundle should contain key " + MAIN_BUNDLE_KEY);

        assertThrows(NullPointerException.class, () -> AppResources.getBundle(null, TEST_LOCALE));
        assertThrows(NullPointerException.class, () -> AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, null));
    }

    @Test
    void testGetBundleWithBaseNameReturnsEmptyForMissingBundle() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle("invalid.bundle", Locale.ENGLISH);
        assertFalse(bundleOpt.isPresent(), "Missing bundle should not be present");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetCssUrlReturnsUrl() {
        Optional<String> cssUrlOpt = AppResources.getCssUrl("test_style.css");
        assertTrue(cssUrlOpt.isPresent(), "CSS URL should be present");
        assertTrue(cssUrlOpt.get().endsWith("test_style.css"), "CSS URL should end with 'test_style.css'");

        assertThrows(NullPointerException.class, () -> AppResources.getCssUrl(null));
    }

    @Test
    void testGetCssUrlReturnsEmptyForMissingFile() {
        Optional<String> cssUrlOpt = AppResources.getCssUrl("missing.css");
        assertFalse(cssUrlOpt.isPresent(), "Missing CSS should not be present");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetImageReturnsImage() {
        Optional<Image> imageOpt = AppResources.getImage("etpetssim16.png");
        assertTrue(imageOpt.isPresent(), "Image should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getImage(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetImages() {
        List<Image> images = AppResources.getImages("etpetssim16.png", "etpetssim16.png", "unknown.png",
                "etpetssim32.png");
        assertEquals(3, images.size(), "Number of images should be 3");

        assertThrows(NullPointerException.class, () -> AppResources.getImages((String[]) null));
    }

    @Test
    void testGetImageReturnsEmptyForMissingImage() {
        Optional<Image> imageOpt = AppResources.getImage("unknown.png");
        assertFalse(imageOpt.isPresent(), "Missing image should not be present");
    }

    @Test
    void testGetImageReturnsEmptyForIllegalImage() {
        Optional<Image> imageOpt = AppResources.getImage("illegal_image.png");
        assertFalse(imageOpt.isPresent(), "Illegal image should not be present");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetResourceAsStreamReturnsStream() throws Exception {
        try (InputStream stream = AppResources.getResourceAsStream("css/test_style.css").orElseThrow()) {
            assertTrue(stream.read() >= 0, "InputStream should provide the resource content");
        }

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsStream(null));
    }

    @Test
    void testGetResourceAsStreamReturnsEmptyForMissingResource() {
        Optional<InputStream> streamOpt = AppResources.getResourceAsStream("missing/file.txt");
        assertFalse(streamOpt.isPresent(), "Missing resource stream should not be present");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetResourceAsStringReturnsString() {
        Optional<String> resOpt = AppResources.getResourceAsString("LICENSE", StandardCharsets.UTF_8);
        assertTrue(resOpt.isPresent(), "Resource string should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsString(null, StandardCharsets.UTF_8));
        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsString("", null));
    }

    @Test
    void testGetResourceAsStringReturnsEmptyForMissingResource() {
        Optional<String> resOpt = AppResources.getResourceAsString("missing/file.txt", StandardCharsets.UTF_8);
        assertFalse(resOpt.isPresent(), "Missing string should not be present");
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetResourceAsUrlReturnsUrl() {
        Optional<URL> urlOpt = AppResources.getResourceAsUrl("css/test_style.css");
        assertTrue(urlOpt.isPresent(), "URL should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getResourceAsUrl(null));
    }

    @Test
    void testGetResourceAsUrlReturnsEmptyForMissingResource() {
        Optional<URL> urlOpt = AppResources.getResourceAsUrl("missing/file.txt");
        assertFalse(urlOpt.isPresent(), "Missing resource URL should not be present");
    }

}

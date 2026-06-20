package de.mkalb.etpetssim.core;

import de.mkalb.FxTestSupport;
import javafx.scene.image.Image;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
final class AppResourcesTest {

    private static final Locale TEST_LOCALE = Locale.GERMANY;

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        FxTestSupport.ensureStarted();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetBundleDefaultWithLocale() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Default resource bundle should be present for locale");
        assertTrue(bundleOpt.get().containsKey("greeting"), "Bundle should contain key 'greeting'");

        assertThrows(NullPointerException.class, () -> AppResources.getBundle(null));
    }

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

    @SuppressWarnings("DataFlowIssue")
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

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetImage() {
        Optional<Image> imageOpt = AppResources.getImage("etpetssim16.png");
        assertTrue(imageOpt.isPresent(), "Image should be present");

        assertThrows(NullPointerException.class, () -> AppResources.getImage(null));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testGetImages() {
        List<@Nullable Image> images = AppResources.getImages("etpetssim16.png", "etpetssim16.png", "unknown.png",
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

    @SuppressWarnings("DataFlowIssue")
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

    @SuppressWarnings("DataFlowIssue")
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

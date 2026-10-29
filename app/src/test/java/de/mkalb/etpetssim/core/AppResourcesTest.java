package de.mkalb.etpetssim.core;

import de.mkalb.etpetssim.testsupport.FXTestSupport;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void testGetBundle() {
        ResourceBundle bundle = AppResources.getBundle(TEST_LOCALE).orElseThrow();

        assertAll(
                () -> assertEquals(TEST_LOCALE, bundle.getLocale()),
                () -> assertTrue(bundle.containsKey(MAIN_BUNDLE_KEY))
        );
    }

    @Test
    void testGetBundleWithBaseName() {
        ResourceBundle bundle = AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, TEST_LOCALE).orElseThrow();

        assertAll(
                () -> assertEquals(TEST_LOCALE, bundle.getLocale()),
                () -> assertTrue(bundle.containsKey(MAIN_BUNDLE_KEY))
        );
    }

    @Test
    void testGetBundleWithBaseNameReturnsEmptyForMissingBundle() {
        assertTrue(AppResources.getBundle("invalid.bundle", Locale.ENGLISH).isEmpty());
    }

    @Test
    void testGetCssUrl() {
        Optional<String> cssUrl = AppResources.getCssUrl("test_style.css");

        assertTrue(cssUrl.orElseThrow().endsWith("css/test_style.css"));
    }

    @Test
    void testGetCssUrlReturnsEmptyForMissingFile() {
        assertTrue(AppResources.getCssUrl("missing.css").isEmpty());
    }

    @SuppressWarnings("MagicNumber")
    @Test
    void testGetImage() {
        Optional<Image> image = AppResources.getImage("etpetssim16.png");

        assertEquals(16.0, image.orElseThrow().getWidth(), 0.0);
    }

    @Test
    void testGetImageReturnsEmptyForMissingImage() {
        assertTrue(AppResources.getImage("unknown.png").isEmpty());
    }

    @Test
    void testGetImageReturnsEmptyForIllegalImage() {
        assertTrue(AppResources.getImage("illegal_image.png").isEmpty());
    }

    @Test
    void testGetImages() {
        List<Image> images = AppResources.getImages("etpetssim16.png", "etpetssim16.png", "unknown.png",
                "etpetssim32.png");

        assertEquals(3, images.size());
    }

    @Test
    void testGetResourceAsStream() throws IOException {
        try (InputStream stream = AppResources.getResourceAsStream("css/test_style.css").orElseThrow()) {
            assertTrue(stream.read() >= 0);
        }
    }

    @Test
    void testGetResourceAsStreamReturnsEmptyForMissingResource() {
        assertTrue(AppResources.getResourceAsStream("missing/file.txt").isEmpty());
    }

    @Test
    void testGetResourceAsString() {
        Optional<String> text = AppResources.getResourceAsString("LICENSE", StandardCharsets.UTF_8);

        assertTrue(text.orElseThrow().contains("License"));
    }

    @Test
    void testGetResourceAsStringReturnsEmptyForMissingResource() {
        assertTrue(AppResources.getResourceAsString("missing/file.txt", StandardCharsets.UTF_8).isEmpty());
    }

    @Test
    void testGetResourceAsUrl() {
        Optional<URL> url = AppResources.getResourceAsUrl("css/test_style.css");

        assertTrue(url.orElseThrow().toExternalForm().endsWith("css/test_style.css"));
    }

    @Test
    void testGetResourceAsUrlReturnsEmptyForMissingResource() {
        assertTrue(AppResources.getResourceAsUrl("missing/file.txt").isEmpty());
    }

}

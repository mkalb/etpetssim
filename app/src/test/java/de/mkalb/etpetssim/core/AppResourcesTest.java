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
    static void setUp() {
        AppLogger.initialize(AppLogger.LogLevel.INFO, true, null);
    }

    @Test
    void testGetBundleDefaultWithLocale() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Default resource bundle should be present for locale");
        assertTrue(bundleOpt.get().containsKey("greeting"), "Bundle should contain key 'greeting'");
    }

    @Test
    void testGetBundleWithBaseNameAndLocale() {
        Optional<ResourceBundle> bundleOpt = AppResources.getBundle(AppResources.BUNDLE_BASE_NAME, TEST_LOCALE);
        assertTrue(bundleOpt.isPresent(), "Resource bundle should be present for base name and locale");
        assertTrue(bundleOpt.get().containsKey("greeting"), "Bundle should contain key 'greeting'");
    }

    @Test
    void testGetCss() {
        Optional<String> cssUrlOpt = AppResources.getCss("etpetssim.css");
        assertTrue(cssUrlOpt.isPresent(), "CSS URL should be present");
        assertTrue(cssUrlOpt.get().endsWith("etpetssim.css"), "CSS URL should end with 'etpetssim.css'");
    }

    @Test
    void testGetImage() {
        Optional<Image> imageOpt = AppResources.getImage("etpetssim16.png");
        assertTrue(imageOpt.isPresent(), "Image should be present");
    }

    @Test
    void testGetImages() {
        List<Image> images = AppResources.getImages("etpetssim16.png", "etpetssim16.png", "unknown.png");
        assertEquals(2, images.size(), "Number of images should be 2");
        assertTrue(images.stream().allMatch(Objects::nonNull), "All images should be non-null");
    }

    @Test
    void testGetResourceAsStream() {
        Optional<InputStream> streamOpt = AppResources.getResourceAsStream("css/etpetssim.css");
        assertTrue(streamOpt.isPresent(), "InputStream should be present");
    }

    @Test
    void testGetResourceAsURL() {
        Optional<URL> urlOpt = AppResources.getResourceAsURL("css/etpetssim.css");
        assertTrue(urlOpt.isPresent(), "URL should be present");
    }

}


package de.mkalb.etpetssim.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Manifest;

/**
 * Access to the build metadata in the manifest {@code META-INF/MANIFEST.MF} of the application JAR.
 *
 * <p>The manifest exists only when the application runs from its JAR. Without a readable manifest (e.g., when running
 * from the class directories), all attributes are missing; callers show them as unknown instead of failing.
 */
public final class AppManifest {

    /**
     * Name of the manifest attribute with the commit date of the build in ISO 8601 format.
     */
    public static final String BUILD_COMMIT_DATE = "Build-Commit-Date";
    /**
     * Name of the manifest attribute with the Java version of the build.
     */
    public static final String BUILD_JDK_SPEC = "Build-Jdk-Spec";
    /**
     * Name of the manifest attribute with the short Git commit hash of the build.
     */
    public static final String BUILD_REVISION = "Build-Revision";
    /**
     * Name of the manifest attribute with the application title.
     */
    public static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    /**
     * Name of the manifest attribute with the project URL.
     */
    public static final String IMPLEMENTATION_URL = "Implementation-URL";
    /**
     * Name of the manifest attribute with the vendor.
     */
    public static final String IMPLEMENTATION_VENDOR = "Implementation-Vendor";
    /**
     * Name of the manifest attribute with the application version.
     */
    public static final String IMPLEMENTATION_VERSION = "Implementation-Version";

    private static final String MANIFEST_PATH = "/META-INF/MANIFEST.MF";
    private static final String UNKNOWN_VALUE = "unknown";

    private AppManifest() {
    }

    /**
     * Reads the main attributes of the application manifest.
     *
     * <p>Attributes with a blank value are left out. A missing manifest is logged at debug level, a read error as
     * error; both return an empty map.
     *
     * @return an unmodifiable map of attribute names to values, or an empty map if the manifest is unavailable
     */
    public static Map<String, String> readMainAttributes() {
        InputStream stream = AppManifest.class.getResourceAsStream(MANIFEST_PATH);
        if (stream == null) {
            AppLogger.debug("AppManifest: Manifest not found: " + MANIFEST_PATH);
            return Map.of();
        }
        try (stream) {
            return parseMainAttributes(stream);
        } catch (IOException e) {
            AppLogger.error(e, "AppManifest: Failed to read manifest: " + MANIFEST_PATH);
            return Map.of();
        }
    }

    /**
     * Reads the application manifest and formats a locale-independent version line.
     *
     * <p>The line has the form {@code <title> <version> (<revision>, <commit date>)}, e.g.
     * {@code Extraterrestrial Pets Simulation 1.0.0 (a1b2c3d, 2026-11-05T10:15:30+01:00)}; a missing attribute is
     * written as {@code unknown}.
     *
     * @return the version line
     */
    public static String formatVersionLine() {
        return formatVersionLine(readMainAttributes());
    }

    /**
     * Formats the version line of {@link #formatVersionLine()} from manifest attributes.
     *
     * @param attributes manifest attributes, as returned by {@link #readMainAttributes()}
     * @return the version line
     */
    static String formatVersionLine(Map<String, String> attributes) {
        return String.format(Locale.ROOT, "%s %s (%s, %s)",
                attributes.getOrDefault(IMPLEMENTATION_TITLE, UNKNOWN_VALUE),
                attributes.getOrDefault(IMPLEMENTATION_VERSION, UNKNOWN_VALUE),
                attributes.getOrDefault(BUILD_REVISION, UNKNOWN_VALUE),
                attributes.getOrDefault(BUILD_COMMIT_DATE, UNKNOWN_VALUE));
    }

    /**
     * Parses the main attributes of a manifest.
     *
     * @param stream manifest content; not closed by this method
     * @return an unmodifiable map of attribute names to values, without attributes with a blank value
     * @throws IOException if the manifest cannot be read
     */
    static Map<String, String> parseMainAttributes(InputStream stream) throws IOException {
        Map<Object, Object> attributes = new Manifest(stream).getMainAttributes();
        Map<String, String> result = HashMap.newHashMap(attributes.size());
        for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
            String value = entry.getValue().toString();
            if (!value.isBlank()) {
                result.put(entry.getKey().toString(), value);
            }
        }
        return Map.copyOf(result);
    }

}

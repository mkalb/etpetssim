package de.mkalb.etpetssim.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("HardcodedLineSeparator")
final class AppManifestTest {

    private static final Map<String, String> COMPLETE_ATTRIBUTES = Map.of(
            AppManifest.IMPLEMENTATION_TITLE, "Extraterrestrial Pets Simulation",
            AppManifest.IMPLEMENTATION_VERSION, "1.0.0",
            AppManifest.BUILD_REVISION, "a1b2c3d",
            AppManifest.BUILD_COMMIT_DATE, "2026-11-05T10:15:30+01:00",
            AppManifest.BUILD_JDK_SPEC, "27");

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
    }

    private static InputStream manifestStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testReadMainAttributes() {
        Map<String, String> attributes = AppManifest.readMainAttributes();

        assertAll(
                () -> assertTrue(attributes.values().stream().noneMatch(String::isBlank)),
                () -> assertThrows(UnsupportedOperationException.class, () -> attributes.put("Key", "value"))
        );
    }

    @Test
    void testFormatVersionLine() {
        assertEquals(AppManifest.formatVersionLine(AppManifest.readMainAttributes()), AppManifest.formatVersionLine());
    }

    @Test
    void testFormatVersionLineWithAttributes() {
        assertEquals("Extraterrestrial Pets Simulation 1.0.0 (a1b2c3d, 2026-11-05T10:15:30+01:00)",
                AppManifest.formatVersionLine(COMPLETE_ATTRIBUTES));
    }

    @Test
    void testFormatVersionLineWithAttributesWritesUnknownForMissingAttributes() {
        assertAll(
                () -> assertEquals("unknown unknown (unknown, unknown)", AppManifest.formatVersionLine(Map.of())),
                () -> assertEquals("unknown 1.0.0 (unknown, unknown)",
                        AppManifest.formatVersionLine(Map.of(AppManifest.IMPLEMENTATION_VERSION, "1.0.0")))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testParseMainAttributes() throws IOException {
        Map<String, String> attributes = AppManifest.parseMainAttributes(manifestStream("""
                Manifest-Version: 1.0
                Implementation-Title: Extraterrestrial Pets Simulation
                Implementation-Version: 1.0.0
                Build-Revision:\s
                Implementation-URL: https://github.com/mkalb/
                 etpetssim

                Name: de/mkalb/etpetssim/
                Implementation-Vendor: Section Vendor
                """));

        assertAll(
                () -> assertEquals(Map.of(
                        "Manifest-Version", "1.0",
                        AppManifest.IMPLEMENTATION_TITLE, "Extraterrestrial Pets Simulation",
                        AppManifest.IMPLEMENTATION_VERSION, "1.0.0",
                        AppManifest.IMPLEMENTATION_URL, "https://github.com/mkalb/etpetssim"), attributes),
                () -> assertThrows(UnsupportedOperationException.class, () -> attributes.put("Key", "value"))
        );
    }

    @Test
    void testParseMainAttributesReturnsEmptyForEmptyManifest() throws IOException {
        assertTrue(AppManifest.parseMainAttributes(manifestStream("")).isEmpty());
    }

    @Test
    void testParseMainAttributesThrowsForInvalidManifest() {
        assertThrows(IOException.class, () -> AppManifest.parseMainAttributes(manifestStream("invalid line\n")));
    }

}

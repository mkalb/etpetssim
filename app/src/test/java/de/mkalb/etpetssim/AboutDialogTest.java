package de.mkalb.etpetssim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("HardcodedLineSeparator")
final class AboutDialogTest {

    @Test
    void testFormatReadmeTextStartsAtFirstSecondLevelHeading() {
        String readme = """
                <p align="center">
                  <img src="assets/icon/etpetssim.svg" alt="icon">
                </p>
                
                <h1 align="center">Title</h1>
                
                ## Overview
                
                Text
                
                ## Simulations
                """;

        assertEquals("""
                ## Overview
                
                Text
                
                ## Simulations
                """, AboutDialog.formatReadmeText(readme));
    }

    @Test
    void testFormatReadmeTextSupportsWindowsLineSeparators() {
        String readme = "<h1>Title</h1>\r\n\r\n## Overview\r\nText\r\n";

        assertEquals("## Overview\r\nText\r\n", AboutDialog.formatReadmeText(readme));
    }

    @Test
    void testFormatReadmeTextIgnoresOtherHeadingsAndInlineMarkers() {
        String readme = """
                # Title
                ### Details
                Text with ## inside
                """;

        assertEquals(readme, AboutDialog.formatReadmeText(readme));
    }

    @Test
    void testFormatReadmeTextReturnsTextWithoutSecondLevelHeadingUnchanged() {
        assertAll(
                () -> assertEquals("", AboutDialog.formatReadmeText("")),
                () -> assertEquals("Resource not found", AboutDialog.formatReadmeText("Resource not found")));
    }

}

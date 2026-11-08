package de.mkalb.etpetssim.ui;

import de.mkalb.etpetssim.core.AppLocalization;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
final class UnexpectedErrorDialogTest {

    @BeforeAll
    static void setUpBeforeAll() {
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
    }

    @Test
    void testFormatContent() {
        String content = UnexpectedErrorDialog.formatContent(new IllegalStateException("Boom"), 0);

        assertEquals("Boom\n\nDetails are in the application log if logging is enabled (--log-file).", content);
    }

    @Test
    void testFormatContentReturnsClassNameForNullMessage() {
        String content = UnexpectedErrorDialog.formatContent(new IllegalStateException(), 0);

        assertEquals("""
                IllegalStateException

                Details are in the application log if logging is enabled (--log-file).""", content);
    }

    @Test
    void testFormatContentReturnsClassNameForBlankMessage() {
        String content = UnexpectedErrorDialog.formatContent(new IllegalArgumentException("  "), 0);

        assertEquals("""
                IllegalArgumentException

                Details are in the application log if logging is enabled (--log-file).""", content);
    }

    @Test
    void testFormatContentReturnsCounterLineForPositiveCount() {
        String content = UnexpectedErrorDialog.formatContent(new IllegalStateException("Boom"), 3);

        assertEquals("""
                Boom

                Details are in the application log if logging is enabled (--log-file).
                Further errors since this alert: 3""", content);
    }

}

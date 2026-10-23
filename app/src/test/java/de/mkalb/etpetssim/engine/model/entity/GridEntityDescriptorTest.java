package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityDescriptorTest {

    private static GridEntityDescriptor createDescriptorWithValues() {
        return new GridEntityDescriptor(
                "id-1",
                true,
                "Wall",
                "Wall Cell",
                "An obstacle",
                "🙂",
                Color.GREEN,
                Color.BLUE
        );
    }

    private static GridEntityDescriptor createDescriptorWithoutValues() {
        return new GridEntityDescriptor(
                "id-2",
                false,
                "Short",
                "Long",
                "Description",
                null,
                null,
                null
        );
    }

    @Test
    void testEmojiAsOptional() {
        assertAll(
                () -> assertEquals("🙂", createDescriptorWithValues().emojiAsOptional().orElseThrow()),
                () -> assertTrue(createDescriptorWithoutValues().emojiAsOptional().isEmpty())
        );
    }

    @Test
    void testColorAsOptional() {
        assertAll(
                () -> assertEquals(Color.GREEN, createDescriptorWithValues().colorAsOptional().orElseThrow()),
                () -> assertTrue(createDescriptorWithoutValues().colorAsOptional().isEmpty())
        );
    }

    @Test
    void testColorOrFallback() {
        assertAll(
                () -> assertEquals(Color.GREEN, createDescriptorWithValues().colorOrFallback()),
                () -> assertEquals(Color.BLACK, createDescriptorWithoutValues().colorOrFallback())
        );
    }

    @Test
    void testBorderColorAsOptional() {
        assertAll(
                () -> assertEquals(Color.BLUE, createDescriptorWithValues().borderColorAsOptional().orElseThrow()),
                () -> assertTrue(createDescriptorWithoutValues().borderColorAsOptional().isEmpty())
        );
    }

    @Test
    void testBorderColorOrFallback() {
        assertAll(
                () -> assertEquals(Color.BLUE, createDescriptorWithValues().borderColorOrFallback()),
                () -> assertEquals(Color.BLACK, createDescriptorWithoutValues().borderColorOrFallback())
        );
    }

    @Test
    void testToDisplayString() {
        assertEquals("[Wall]", createDescriptorWithValues().toDisplayString());
    }

}

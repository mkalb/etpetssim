package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityDescriptorTest {

    private static GridEntityDescriptor createDescriptorWithValues() {
        return new GridEntityDescriptor(
                "id-1",
                true,
                "Short",
                "Long",
                "Description",
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
        assertEquals("🙂", createDescriptorWithValues().emojiAsOptional().orElseThrow());
        assertTrue(createDescriptorWithoutValues().emojiAsOptional().isEmpty());
    }

    @Test
    void testColorAsOptional() {
        assertEquals(Color.GREEN, createDescriptorWithValues().colorAsOptional().orElseThrow());
        assertTrue(createDescriptorWithoutValues().colorAsOptional().isEmpty());
    }

    @Test
    void testBorderColorAsOptional() {
        assertEquals(Color.BLUE, createDescriptorWithValues().borderColorAsOptional().orElseThrow());
        assertTrue(createDescriptorWithoutValues().borderColorAsOptional().isEmpty());
    }

    @Test
    void testColorOrFallback() {
        assertEquals(Color.BLACK, createDescriptorWithoutValues().colorOrFallback());
    }

    @Test
    void testBorderColorOrFallback() {
        assertEquals(Color.BLACK, createDescriptorWithoutValues().borderColorOrFallback());
    }

    @Test
    void testToDisplayStringUsesShortName() {
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                "id-1",
                true,
                "Wall",
                "Wall Cell",
                "An obstacle",
                null,
                null,
                null
        );

        assertEquals("[Wall]", descriptor.toDisplayString());
    }

}


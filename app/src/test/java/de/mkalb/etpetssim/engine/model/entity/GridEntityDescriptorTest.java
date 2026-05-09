package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityDescriptorTest {

    @Test
    void testOptionalViewsReflectNullableFields() {
        GridEntityDescriptor withValues = new GridEntityDescriptor(
                "id-1",
                true,
                "Short",
                "Long",
                "Description",
                "🙂",
                Color.GREEN,
                Color.BLUE
        );
        GridEntityDescriptor withoutValues = new GridEntityDescriptor(
                "id-2",
                false,
                "Short",
                "Long",
                "Description",
                null,
                null,
                null
        );

        assertEquals("🙂", withValues.emojiAsOptional().orElseThrow());
        assertEquals(Color.GREEN, withValues.colorAsOptional().orElseThrow());
        assertEquals(Color.BLUE, withValues.borderColorAsOptional().orElseThrow());

        assertTrue(withoutValues.emojiAsOptional().isEmpty());
        assertTrue(withoutValues.colorAsOptional().isEmpty());
        assertTrue(withoutValues.borderColorAsOptional().isEmpty());
    }

    @Test
    void testFallbackColorsUseDefaultsWhenUnset() {
        GridEntityDescriptor descriptor = new GridEntityDescriptor(
                "id-1",
                true,
                "Short",
                "Long",
                "Description",
                null,
                null,
                null
        );

        assertEquals(Color.BLACK, descriptor.colorOrFallback());
        assertEquals(Color.BLACK, descriptor.borderColorOrFallback());
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


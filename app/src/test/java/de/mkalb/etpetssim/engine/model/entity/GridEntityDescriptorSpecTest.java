package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityDescriptorSpecTest {

    @Test
    void testRecordStoresAllValues() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "wall",
                true,
                "entity.wall.short",
                "entity.wall.long",
                "entity.wall.description",
                "entity.wall.emoji",
                Color.GRAY,
                Color.BLACK
        );

        assertAll(
                () -> assertEquals("wall", spec.descriptorId()),
                () -> assertTrue(spec.visible()),
                () -> assertEquals("entity.wall.short", spec.shortNameKey()),
                () -> assertEquals("entity.wall.long", spec.longNameKey()),
                () -> assertEquals("entity.wall.description", spec.descriptionKey()),
                () -> assertEquals("entity.wall.emoji", spec.emojiKey()),
                () -> assertEquals(Color.GRAY, spec.color()),
                () -> assertEquals(Color.BLACK, spec.borderColor())
        );
    }

    @Test
    void testRecordAllowsNullableOptionalFields() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "food",
                false,
                "entity.food.short",
                "entity.food.long",
                "entity.food.description",
                null,
                null,
                null
        );

        assertAll(
                () -> assertNull(spec.emojiKey()),
                () -> assertNull(spec.color()),
                () -> assertNull(spec.borderColor())
        );
    }

}


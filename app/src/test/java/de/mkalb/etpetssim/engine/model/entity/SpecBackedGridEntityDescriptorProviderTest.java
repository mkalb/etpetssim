package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

final class SpecBackedGridEntityDescriptorProviderTest {

    @Test
    void testDefaultAccessorsDelegateToDescriptorSpec() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "entity-id",
                "short.key",
                "long.key",
                "description.key",
                "emoji.key",
                Color.RED,
                Color.ORANGE
        );
        SpecBackedGridEntityDescriptorProvider provider = new TestSpecBackedProvider(spec);

        assertAll(
                () -> assertEquals("entity-id", provider.descriptorId()),
                () -> assertEquals("short.key", provider.shortNameKey()),
                () -> assertEquals("long.key", provider.longNameKey()),
                () -> assertEquals("description.key", provider.descriptionKey()),
                () -> assertEquals("emoji.key", provider.emojiKey()),
                () -> assertEquals(Color.RED, provider.color()),
                () -> assertEquals(Color.ORANGE, provider.borderColor())
        );
    }

    @Test
    void testDefaultAccessorsReturnNullForUnsetOptionalValues() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "entity-id",
                "short.key",
                "long.key",
                "description.key",
                null,
                null,
                null
        );
        SpecBackedGridEntityDescriptorProvider provider = new TestSpecBackedProvider(spec);

        assertAll(
                () -> assertNull(provider.emojiKey()),
                () -> assertNull(provider.color()),
                () -> assertNull(provider.borderColor())
        );
    }

    private record TestSpecBackedProvider(GridEntityDescriptorSpec descriptorSpec)
            implements SpecBackedGridEntityDescriptorProvider {
    }

}

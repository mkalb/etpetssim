package de.mkalb.etpetssim.engine.model.entity;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class SpecBackedGridEntityDescriptorProviderTest {

    @Test
    void testDefaultAccessorsDelegateToDescriptorSpec() {
        GridEntityDescriptorSpec spec = new GridEntityDescriptorSpec(
                "entity-id",
                true,
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
                () -> assertTrue(provider.visible()),
                () -> assertEquals("short.key", provider.shortNameKey()),
                () -> assertEquals("long.key", provider.longNameKey()),
                () -> assertEquals("description.key", provider.descriptionKey()),
                () -> assertEquals("emoji.key", provider.emojiKey()),
                () -> assertEquals(Color.RED, provider.color()),
                () -> assertEquals(Color.ORANGE, provider.borderColor())
        );
    }

    private record TestSpecBackedProvider(GridEntityDescriptorSpec descriptorSpec)
            implements SpecBackedGridEntityDescriptorProvider {
    }

}


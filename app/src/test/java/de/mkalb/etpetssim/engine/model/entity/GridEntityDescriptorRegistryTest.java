package de.mkalb.etpetssim.engine.model.entity;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import javafx.scene.paint.Color;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

final class GridEntityDescriptorRegistryTest {

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US");
        }
    }

    private static void assertRegisteredDescriptor(GridEntityDescriptorRegistry registry, GridEntityDescriptorProvider provider) {
        GridEntityDescriptor descriptor = registry.requireByDescriptorId(provider.descriptorId());
        String expectedEmoji = java.util.Optional.ofNullable(provider.emojiKey())
                                                 .map(AppLocalization::getText)
                                                 .orElse(null);

        assertAll(
                () -> assertEquals(provider.descriptorId(), descriptor.descriptorId()),
                () -> assertEquals(provider.visible(), descriptor.visible()),
                () -> assertEquals(AppLocalization.getText(provider.shortNameKey()), descriptor.shortName()),
                () -> assertEquals(AppLocalization.getText(provider.longNameKey()), descriptor.longName()),
                () -> assertEquals(AppLocalization.getText(provider.descriptionKey()), descriptor.description()),
                () -> assertEquals(expectedEmoji, descriptor.emoji()),
                () -> assertEquals(provider.color(), descriptor.color()),
                () -> assertEquals(provider.borderColor(), descriptor.borderColor())
        );
    }

    @Test
    void testFactoryMethodsRegisterProviders() {
        GridEntityDescriptorProvider providerA = new TestDescriptorProvider(
                "entity.a",
                true,
                "conway.entity.alive.short",
                "conway.entity.alive.long",
                "conway.entity.alive.description",
                null,
                Color.GREEN,
                null
        );
        GridEntityDescriptorProvider providerB = new TestDescriptorProvider(
                "entity.b",
                false,
                "conway.entity.dead.short",
                "conway.entity.dead.long",
                "conway.entity.dead.description",
                "forest.entity.tree.emoji",
                Color.DARKGRAY,
                Color.GRAY
        );

        GridEntityDescriptorRegistry fromVarargs = GridEntityDescriptorRegistry.of(providerA, providerB);
        GridEntityDescriptorRegistry fromArray = GridEntityDescriptorRegistry.ofArray(new GridEntityDescriptorProvider[]{providerA, providerB});
        GridEntityDescriptorRegistry fromCollection = GridEntityDescriptorRegistry.ofCollection(List.of(providerA, providerB));

        assertRegisteredDescriptor(fromVarargs, providerA);
        assertRegisteredDescriptor(fromVarargs, providerB);
        assertRegisteredDescriptor(fromArray, providerA);
        assertRegisteredDescriptor(fromArray, providerB);
        assertRegisteredDescriptor(fromCollection, providerA);
        assertRegisteredDescriptor(fromCollection, providerB);
    }

    @Test
    void testRegisterReplacesDescriptorForSameId() {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(1);

        registry.register(
                "entity-id",
                true,
                "conway.entity.alive.short",
                "conway.entity.alive.long",
                "conway.entity.alive.description",
                null,
                Color.GREEN,
                null
        );
        GridEntityDescriptor replaced = registry.register(
                "entity-id",
                false,
                "conway.entity.dead.short",
                "conway.entity.dead.long",
                "conway.entity.dead.description",
                null,
                Color.BLACK,
                Color.WHITE
        );

        GridEntityDescriptor actual = registry.requireByDescriptorId("entity-id");
        assertEquals(replaced, actual);
        assertEquals(AppLocalization.getText("conway.entity.dead.short"), actual.shortName());
        assertFalse(actual.visible());
        assertEquals(Color.WHITE, actual.borderColor());
    }

    @Test
    void testGetRequiredThrowsForUnknownDescriptorId() {
        GridEntityDescriptorRegistry registry = new GridEntityDescriptorRegistry(1);

        assertThrows(NoSuchElementException.class, () -> registry.requireByDescriptorId("missing"));
        assertTrue(registry.findByDescriptorId("missing").isEmpty());
    }

    private record TestDescriptorProvider(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Color color,
            @Nullable Color borderColor
    ) implements GridEntityDescriptorProvider {
    }

}

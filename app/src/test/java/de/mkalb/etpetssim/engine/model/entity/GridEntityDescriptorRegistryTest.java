package de.mkalb.etpetssim.engine.model.entity;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLogger;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
final class GridEntityDescriptorRegistryTest {

    private static final GridEntityDescriptorProvider PROVIDER_A = new TestDescriptorProvider(
            "entity.a",
            true,
            "conway.entity.alive.short",
            "conway.entity.alive.long",
            "conway.entity.alive.description",
            null,
            Color.GREEN,
            null
    );
    private static final GridEntityDescriptorProvider PROVIDER_B = new TestDescriptorProvider(
            "entity.b",
            false,
            "conway.entity.dead.short",
            "conway.entity.dead.long",
            "conway.entity.dead.description",
            "forest.entity.tree.emoji",
            Color.DARKGRAY,
            Color.GRAY
    );
    private static final GridEntityDescriptorProvider PROVIDER_B_REPLACEMENT = new TestDescriptorProvider(
            "entity.b",
            true,
            "conway.entity.alive.short",
            "conway.entity.alive.long",
            "conway.entity.alive.description",
            null,
            Color.BLACK,
            Color.WHITE
    );

    @BeforeAll
    static void setUpBeforeAll() {
        AppLogger.initializeForTesting();
        if (!AppLocalization.isInitialized()) {
            AppLocalization.initialize("en_US", Locale.US);
        }
    }

    private static void assertRegisteredDescriptor(GridEntityDescriptorRegistry registry, GridEntityDescriptorProvider provider) {
        GridEntityDescriptor descriptor = registry.requireByDescriptorId(provider.descriptorId());
        String expectedEmoji = Optional.ofNullable(provider.emojiKey())
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
    void testOf() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.of(PROVIDER_A, PROVIDER_B);

        assertRegisteredDescriptor(registry, PROVIDER_A);
        assertRegisteredDescriptor(registry, PROVIDER_B);
    }

    @Test
    void testOfKeepsLastProviderForSameDescriptorId() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.of(PROVIDER_B, PROVIDER_B_REPLACEMENT);

        assertRegisteredDescriptor(registry, PROVIDER_B_REPLACEMENT);
    }

    @Test
    void testOfCollection() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.ofCollection(List.of(PROVIDER_A, PROVIDER_B));

        assertRegisteredDescriptor(registry, PROVIDER_A);
        assertRegisteredDescriptor(registry, PROVIDER_B);
    }

    @Test
    void testFindByDescriptorId() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.of(PROVIDER_A);

        assertEquals(Optional.of(registry.requireByDescriptorId(PROVIDER_A.descriptorId())),
                registry.findByDescriptorId(PROVIDER_A.descriptorId()));
    }

    @Test
    void testFindByDescriptorIdReturnsEmptyForUnknownId() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.of(PROVIDER_A);

        assertTrue(registry.findByDescriptorId("missing").isEmpty());
    }

    @Test
    void testRequireByDescriptorIdThrowsForUnknownId() {
        GridEntityDescriptorRegistry registry = GridEntityDescriptorRegistry.of();

        assertThrows(NoSuchElementException.class, () -> registry.requireByDescriptorId("missing"));
    }

    private record TestDescriptorProvider(
            String descriptorId,
            boolean visible,
            String shortNameKey,
            String longNameKey,
            String descriptionKey,
            String emojiKey,
            Color color,
            Color borderColor
    ) implements GridEntityDescriptorProvider {
    }

}

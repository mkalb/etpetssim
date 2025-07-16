package de.mkalb.etpetssim.simulations.simulationlab;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

/**
 * Defines the available entities for the Simulation Lab grid.
 * <p>
 * Each enum constant represents a specific type of entity that can be placed on the grid,
 * providing all necessary descriptor information for registration and rendering.
 * </p>
 *
 * <ul>
 *   <li><b>descriptorId</b>: Unique identifier for the entity descriptor.</li>
 *   <li><b>visible</b>: Whether the entity should be visible in the UI.</li>
 *   <li><b>shortKey</b>: Resource key for the short display name.</li>
 *   <li><b>longKey</b>: Resource key for the long display name.</li>
 *   <li><b>descriptionKey</b>: Resource key for the entity description.</li>
 *   <li><b>emojiKey</b>: Optional resource key for an emoji representation.</li>
 *   <li><b>color</b>: Optional fill color for rendering the entity.</li>
 *   <li><b>borderColor</b>: Optional border color for rendering the entity.</li>
 *   <li><b>renderPriority</b>: Priority for rendering order (higher values are rendered above lower ones).</li>
 * </ul>
 *
 * @see de.mkalb.etpetssim.engine.model.GridEntityDescriptorRegistry
 * @see de.mkalb.etpetssim.engine.model.ConstantGridEntity
 */
public enum LabEntity implements ConstantGridEntity, GridEntityDescribable {
    NORMAL(
            "normal",
            true,
            "simulationlab.entity.normal.short",
            "simulationlab.entity.normal.long",
            "simulationlab.entity.normal.description",
            null,
            null,
            null,
            0
    ),
    HIGHLIGHTED(
            "highlighted",
            true,
            "simulationlab.entity.highlighted.short",
            "simulationlab.entity.highlighted.long",
            "simulationlab.entity.highlighted.description",
            null,
            null,
            null,
            1
    );

    private final String descriptorId;
    private final boolean visible;
    private final String shortKey;
    private final String longKey;
    private final String descriptionKey;
    private final @Nullable String emojiKey;
    private final @Nullable Paint color;
    private final @Nullable Color borderColor;
    private final int renderPriority;

    LabEntity(
            String descriptorId,
            boolean visible,
            String shortKey,
            String longKey,
            String descriptionKey,
            @Nullable String emojiKey,
            @Nullable Paint color,
            @Nullable Color borderColor,
            int renderPriority
    ) {
        this.descriptorId = descriptorId;
        this.visible = visible;
        this.shortKey = shortKey;
        this.longKey = longKey;
        this.descriptionKey = descriptionKey;
        this.emojiKey = emojiKey;
        this.color = color;
        this.borderColor = borderColor;
        this.renderPriority = renderPriority;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return descriptorId;
    }

    /**
     * Indicates whether this entity should be visible in the UI.
     *
     * @return {@code true} if the entity is visible, {@code false} otherwise
     */
    @Override
    public boolean visible() {
        return visible;
    }

    /**
     * Returns the resource key for the short display name of this entity.
     *
     * @return the short name resource key
     */
    @Override
    public String shortKey() {
        return shortKey;
    }

    /**
     * Returns the resource key for the long display name of this entity.
     *
     * @return the long name resource key
     */
    @Override
    public String longKey() {
        return longKey;
    }

    /**
     * Returns the resource key for the description of this entity.
     *
     * @return the description resource key
     */
    @Override
    public String descriptionKey() {
        return descriptionKey;
    }

    /**
     * Returns the optional resource key for an emoji representation of this entity.
     *
     * @return the emoji resource key, or {@code null} if not set
     */
    @Override
    public @Nullable String emojiKey() {
        return emojiKey;
    }

    /**
     * Returns the optional fill color for rendering this entity.
     *
     * @return the fill color, or {@code null} if not set
     */
    @Override
    public @Nullable Paint color() {
        return color;
    }

    /**
     * Returns the optional border color for rendering this entity.
     *
     * @return the border color, or {@code null} if not set
     */
    @Override
    public @Nullable Color borderColor() {
        return borderColor;
    }

    /**
     * Returns the render priority for this entity.
     * Higher values are rendered above lower ones.
     *
     * @return the render priority
     */
    @Override
    public int renderPriority() {
        return renderPriority;
    }

}

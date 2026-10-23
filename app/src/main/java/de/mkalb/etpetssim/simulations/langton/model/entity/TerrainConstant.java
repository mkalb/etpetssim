package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.*;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Terrain cell states for the Langton's Ant simulation.
 *
 * <p>Each constant represents one terrain cell state identified by a {@code ruleIndex}.
 * The special value {@link #UNVISITED} (rule index {@code -1}) marks cells that have
 * not yet been visited by any ant. Constants {@link #COLOR_0} through {@link #COLOR_15}
 * represent the 16 color states used by Langton's Ant rule sets.
 *
 * <p>Every constant carries a {@link GridEntityDescriptorSpec}
 * for descriptor-registry registration and rendering.
 *
 * @see LangtonEntity
 * @see ConstantGridEntityDescriptorProvider
 * @see GridEntityDescriptorRegistry
 */
public enum TerrainConstant implements LangtonEntity, ConstantGridEntityDescriptorProvider {

    UNVISITED(-1, Color.WHITE),
    COLOR_0(0, Color.LIGHTGRAY),
    COLOR_1(1, Color.BLACK),
    COLOR_2(2, Color.ORANGE),
    COLOR_3(3, Color.YELLOW),
    COLOR_4(4, Color.GREEN),
    COLOR_5(5, Color.DARKGREEN),
    COLOR_6(6, Color.BLUE),
    COLOR_7(7, Color.DARKBLUE),
    COLOR_8(8, Color.PURPLE),
    COLOR_9(9, Color.VIOLET),
    COLOR_10(10, Color.PINK),
    COLOR_11(11, Color.BROWN),
    COLOR_12(12, Color.CYAN),
    COLOR_13(13, Color.MAGENTA),
    COLOR_14(14, Color.DEEPPINK),
    COLOR_15(15, Color.GOLD);

    private static final Map<Integer, TerrainConstant> BY_RULE_INDEX = HashMap.newHashMap(TerrainConstant.values().length);

    static {
        for (TerrainConstant constant : values()) {
            BY_RULE_INDEX.put(constant.ruleIndex(), constant);
        }
    }

    private final int ruleIndex;
    private final GridEntityDescriptorSpec spec;

    TerrainConstant(int ruleIndex,
                    Color color) {
        this.ruleIndex = ruleIndex;
        String id = (ruleIndex < 0) ? "unvisited" : String.valueOf(ruleIndex);
        spec = new GridEntityDescriptorSpec(
                "terrain" + id,
                true,
                "langton.entity.terrain." + id + ".short",
                "langton.entity.terrain." + id + ".long",
                "langton.entity.terrain." + id + ".description",
                null,
                color,
                null);
    }

    /**
     * Returns the {@link TerrainConstant} associated with the given rule index.
     *
     * @param ruleIndex the rule index to look up
     * @return the matching {@link TerrainConstant}
     * @throws IllegalArgumentException if no constant exists for the given rule index
     */
    public static TerrainConstant requireByRuleIndex(int ruleIndex) {
        TerrainConstant result = BY_RULE_INDEX.get(ruleIndex);
        if (result == null) {
            throw new IllegalArgumentException("No TerrainConstant for rule index: " + ruleIndex);
        }
        return result;
    }

    public boolean isUnvisited() {
        return this == UNVISITED;
    }

    /**
     * Returns the rule index of this terrain constant.
     *
     * <p>The value {@code -1} is used for {@link #UNVISITED}; values {@code 0}–{@code 15}
     * correspond to the color states used in Langton's Ant rule sets.
     *
     * @return the rule index
     */
    public int ruleIndex() {
        return ruleIndex;
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
        return spec;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

}

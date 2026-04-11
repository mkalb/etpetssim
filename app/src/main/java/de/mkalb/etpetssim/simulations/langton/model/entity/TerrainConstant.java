package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.model.entity.ConstantGridEntityDescriptorProvider;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorSpec;
import javafx.scene.paint.Color;

import java.util.*;

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
        for (TerrainConstant ground : values()) {
            BY_RULE_INDEX.put(ground.ruleIndex(), ground);
        }
    }

    private final int ruleIndex;
    private final GridEntityDescriptorSpec spec;

    TerrainConstant(int ruleIndex, Color color) {
        this.ruleIndex = ruleIndex;
        String id = (ruleIndex < 0) ? "unvisited" : String.valueOf(ruleIndex);
        spec = new GridEntityDescriptorSpec(
                "ground" + id,
                true,
                "langton.entity.ground." + id + ".short",
                "langton.entity.ground." + id + ".long",
                "langton.entity.ground." + id + ".description",
                null,
                color,
                null,
                2
        );
    }

    public static TerrainConstant byRuleIndex(int ruleIndex) {
        return BY_RULE_INDEX.get(ruleIndex);
    }

    public boolean isUnvisited() {
        return this == UNVISITED;
    }

    public int ruleIndex() {
        return ruleIndex;
    }

    @Override
    public GridEntityDescriptorSpec descriptorSpec() {
        return spec;
    }

    /**
     * Checks if this entity represents an agent.
     *
     * @return {@code true} if this entity is an agent, {@code false} otherwise
     */
    @Override
    public boolean isAgent() {
        return false;
    }

}

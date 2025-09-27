package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.ConstantGridEntity;
import de.mkalb.etpetssim.engine.model.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

import java.util.*;

public enum LangtonGroundEntity implements LangtonEntity, ConstantGridEntity, GridEntityDescribable {

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

    private static final Map<Integer, LangtonGroundEntity> BY_RULE_INDEX = HashMap.newHashMap(LangtonGroundEntity.values().length);

    static {
        for (LangtonGroundEntity ground : values()) {
            BY_RULE_INDEX.put(ground.ruleIndex(), ground);
        }
    }

    private final int ruleIndex;
    private final String descriptorId;
    private final boolean visible;
    private final String shortKey;
    private final String longKey;
    private final String descriptionKey;
    private final @Nullable String emojiKey;
    private final Paint color;
    private final @Nullable Color borderColor;
    private final int renderPriority;

    LangtonGroundEntity(int ruleIndex, Color color) {
        this.ruleIndex = ruleIndex;
        String id = (ruleIndex < 0) ? "unvisited" : String.valueOf(ruleIndex);
        descriptorId = "ground" + id;
        visible = true;
        shortKey = "langton.entity.ground." + id + ".short";
        longKey = "langton.entity.ground." + id + ".long";
        descriptionKey = "langton.entity.ground." + id + ".description";
        emojiKey = null;
        this.color = color;
        borderColor = null;
        renderPriority = 2;
    }

    public static LangtonGroundEntity byRuleIndex(int ruleIndex) {
        return BY_RULE_INDEX.get(ruleIndex);
    }

    public boolean isUnvisited() {
        return this == UNVISITED;
    }

    public int ruleIndex() {
        return ruleIndex;
    }

    @Override
    public String descriptorId() {
        return descriptorId;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public String shortKey() {
        return shortKey;
    }

    @Override
    public String longKey() {
        return longKey;
    }

    @Override
    public String descriptionKey() {
        return descriptionKey;
    }

    @Override
    public @Nullable String emojiKey() {
        return emojiKey;
    }

    @Override
    public Paint color() {
        return color;
    }

    @Override
    public @Nullable Color borderColor() {
        return borderColor;
    }

    @Override
    public int renderPriority() {
        return renderPriority;
    }

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public String toDisplayString() {
        return "TODO"; // TODO implement
    }

    public String toString() {
        return "TODO"; // TODO implement
    }

}

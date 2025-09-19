package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.model.GridEntityDescribable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jspecify.annotations.Nullable;

public enum LangtonAntNone implements LangtonAntEntity, GridEntityDescribable {

    NONE;

    @Override
    public boolean isAgent() {
        return false;
    }

    @Override
    public String descriptorId() {
        return LangtonAntEntity.DESCRIPTOR_ID_ANT_NONE;
    }

    @Override
    public boolean visible() {
        return false;
    }

    @Override
    public String shortKey() {
        return "";
    }

    @Override
    public String longKey() {
        return "";
    }

    @Override
    public String descriptionKey() {
        return "";
    }

    @Override
    public @Nullable String emojiKey() {
        return null;
    }

    @Override
    public @Nullable Paint color() {
        return null;
    }

    @Override
    public @Nullable Color borderColor() {
        return null;
    }

    @Override
    public int renderPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toDisplayString() {
        return "TODO"; // TODO implement
    }

    public String toString() {
        return "TODO"; // TODO implement
    }

}

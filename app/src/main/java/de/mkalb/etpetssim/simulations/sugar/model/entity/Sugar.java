package de.mkalb.etpetssim.simulations.sugar.model.entity;

import java.util.*;

public final class Sugar implements ResourceEntity {

    private final int maxAmount;
    private int currentAmount;

    public Sugar(int maxAmount, int currentAmount) {
        this.maxAmount = maxAmount;
        this.currentAmount = currentAmount;
    }

    @Override
    public String descriptorId() {
        return SugarEntity.DESCRIPTOR_ID_SUGAR;
    }

    public int maxAmount() {
        return maxAmount;
    }

    public int currentAmount() {
        return currentAmount;
    }

    public void adjustAmount(int delta) {
        currentAmount = Math.clamp(currentAmount + delta, 0, maxAmount);
    }

    @Override
    public boolean isResource() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[SUGAR A=%d/%d]",
                currentAmount,
                maxAmount);
    }

    @Override
    public String toString() {
        return "Sugar{" +
                "maxAmount=" + maxAmount +
                ", currentAmount=" + currentAmount +
                '}';
    }

}

package de.mkalb.etpetssim.simulations.wator.model.entity;

import java.util.*;

public final class Fish extends CreatureBase {

    public Fish(long sequenceId, int stepIndexOfBirth) {
        super(WatorEntity.DESCRIPTOR_ID_FISH, sequenceId, stepIndexOfBirth);
    }

    @Override
    public boolean isFish() {
        return true;
    }

    @Override
    public boolean isShark() {
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[FISH #%d *%d]",
                sequenceId(),
                stepIndexOfBirth());
    }

    @Override
    public String toString() {
        return "Fish{" +
                "sequenceId=" + sequenceId() +
                ", stepIndexOfBirth=" + stepIndexOfBirth() +
                ", reproductionCount=" + reproductionCount() +
                ", stepIndexOfLastReproduction=" + stepIndexOfLastReproduction() +
                '}';
    }

}

package de.mkalb.etpetssim.simulations.wator.model.entity;

import java.util.*;

public abstract sealed class WatorCreature implements WatorEntity, Comparable<WatorCreature>
        permits WatorFish, WatorShark {

    private final String descriptorId;
    private final long sequenceId;
    private final int stepIndexOfBirth;
    private final List<Integer> timeOfReproduction;

    protected WatorCreature(
            String descriptorId,
            long sequenceId,
            int stepIndexOfBirth
    ) {
        this.descriptorId = descriptorId;
        this.sequenceId = sequenceId;
        this.stepIndexOfBirth = stepIndexOfBirth;

        timeOfReproduction = new ArrayList<>();
    }

    @Override
    public final String descriptorId() {
        return descriptorId;
    }

    @Override
    public final boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        WatorCreature that = (WatorCreature) obj;
        return sequenceId == that.sequenceId;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(sequenceId);
    }

    @Override
    public String toString() {
        return "WatorCreature{" +
                "sequenceId=" + sequenceId +
                ", descriptorId='" + descriptorId + '\'' +
                ", stepIndexOfBirth=" + stepIndexOfBirth +
                '}';
    }

    @Override
    public final int compareTo(WatorCreature o) {
        return Long.compare(sequenceId(), o.sequenceId());
    }

    public final long sequenceId() {
        return sequenceId;
    }

    public final int stepIndexOfBirth() {
        return stepIndexOfBirth;
    }

    public final int ageAtStepIndex(int stepIndex) {
        return stepIndex - stepIndexOfBirth;
    }

    public final int ageAtStepCount(int stepCount) {
        return ageAtStepIndex(stepCount - 1);
    }

    public final int numberOfReproductions() {
        return timeOfReproduction.size();
    }

    public final OptionalInt timeOfLastReproduction() {
        return timeOfReproduction.isEmpty() ? OptionalInt.empty() : OptionalInt.of(timeOfReproduction.getLast());
    }

    public final void reproduce(WatorCreature child) {
        timeOfReproduction.add(child.stepIndexOfBirth);
    }

    @Override
    public final boolean isAgent() {
        return true;
    }

    @Override
    public final boolean isWater() {
        return false;
    }

}

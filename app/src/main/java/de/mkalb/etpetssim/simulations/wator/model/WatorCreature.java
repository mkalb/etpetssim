package de.mkalb.etpetssim.simulations.wator.model;

import java.util.*;

public abstract sealed class WatorCreature implements WatorEntity, Comparable<WatorCreature>
        permits WatorFish, WatorShark {

    private final String descriptorId;
    private final long sequenceId;
    private final long timeOfBirth;
    private final List<Long> timeOfReproduction;

    protected WatorCreature(
            String descriptorId,
            long sequenceId,
            long timeOfBirth
    ) {
        this.descriptorId = descriptorId;
        this.sequenceId = sequenceId;
        this.timeOfBirth = timeOfBirth;

        timeOfReproduction = new ArrayList<>();
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
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
                ", timeOfBirth=" + timeOfBirth +
                '}';
    }

    @Override
    public final int compareTo(WatorCreature o) {
        return Long.compare(sequenceId(), o.sequenceId());
    }

    public final long sequenceId() {
        return sequenceId;
    }

    public final long timeOfBirth() {
        return timeOfBirth;
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    public final int age(long timeCounter) {
        return (int) (timeCounter - timeOfBirth());
    }

    public final int numberOfReproductions() {
        return timeOfReproduction.size();
    }

    public final OptionalLong timeOfLastReproduction() {
        return timeOfReproduction.isEmpty() ? OptionalLong.empty() : OptionalLong.of(timeOfReproduction.getLast());
    }

    public final void reproduce(WatorCreature child) {
        timeOfReproduction.add(child.timeOfBirth());
    }

    @Override
    public final boolean isAgent() {
        return true;
    }

}

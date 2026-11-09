package de.mkalb.etpetssim.simulations.wator.model.entity;

import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;

/**
 * Base class of the Wa-Tor creatures; two creatures are equal if they have the same class and sequence ID.
 */
public abstract sealed class CreatureBase implements WatorEntity
        permits Fish, Shark {

    private final String descriptorId;
    private final long sequenceId;
    private final int stepIndexOfBirth;
    private int reproductionCount;
    private int stepIndexOfLastReproduction;

    protected CreatureBase(String descriptorId, long sequenceId, int stepIndexOfBirth) {
        this.descriptorId = descriptorId;
        this.sequenceId = sequenceId;
        this.stepIndexOfBirth = stepIndexOfBirth;
    }

    @Override
    public final String descriptorId() {
        return descriptorId;
    }

    @Override
    public final boolean equals(@Nullable Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        CreatureBase that = (CreatureBase) obj;
        return sequenceId == that.sequenceId;
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(sequenceId);
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

    public final int reproductionCount() {
        return reproductionCount;
    }

    /**
     * Returns the step index of the last reproduction.
     *
     * @return the step of birth of the last child, or an empty result if the creature has not reproduced yet
     */
    public final OptionalInt stepIndexOfLastReproduction() {
        return (reproductionCount == 0) ? OptionalInt.empty() : OptionalInt.of(stepIndexOfLastReproduction);
    }

    /**
     * Records a reproduction at the step of birth of the given child.
     *
     * @param child the newly created child
     */
    public final void reproduce(CreatureBase child) {
        reproductionCount++;
        stepIndexOfLastReproduction = child.stepIndexOfBirth;
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

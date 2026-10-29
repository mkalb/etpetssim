package de.mkalb.etpetssim.simulations.wator.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

public abstract sealed class CreatureBase implements WatorEntity, Comparable<CreatureBase>
        permits Fish, Shark {

    private final String descriptorId;
    private final long sequenceId;
    private final int stepIndexOfBirth;
    private final List<Integer> reproductionStepIndices;

    protected CreatureBase(
            String descriptorId,
            long sequenceId,
            int stepIndexOfBirth
    ) {
        this.descriptorId = descriptorId;
        this.sequenceId = sequenceId;
        this.stepIndexOfBirth = stepIndexOfBirth;

        reproductionStepIndices = new ArrayList<>();
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
        CreatureBase that = (CreatureBase) obj;
        return sequenceId == that.sequenceId;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(sequenceId);
    }

    @Override
    public final int compareTo(CreatureBase other) {
        return Long.compare(sequenceId(), other.sequenceId());
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
        return reproductionStepIndices.size();
    }

    public final OptionalInt stepIndexOfLastReproduction() {
        return reproductionStepIndices.isEmpty() ? OptionalInt.empty() : OptionalInt.of(reproductionStepIndices.getLast());
    }

    public final void reproduce(CreatureBase child) {
        reproductionStepIndices.add(child.stepIndexOfBirth);
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

package de.mkalb.etpetssim.simulations.etpets.model.entity;

import org.jspecify.annotations.Nullable;

public final class Pet implements AgentEntity {

    public static final int VISION_RANGE = 2;

    private final long petId;
    private final @Nullable Long parentAId;
    private final @Nullable Long parentBId;
    private final int stepIndexOfBirth;
    private final PetTraits traits;
    private int currentEnergy;
    private int reproductionCooldownRemaining;
    private boolean dead;
    private int stepIndexOfDeath;

    public Pet(long petId,
               @Nullable Long parentAId,
               @Nullable Long parentBId,
               int stepIndexOfBirth,
               int currentEnergy,
               int reproductionCooldownRemaining,
               PetTraits traits) {
        this.petId = petId;
        this.parentAId = parentAId;
        this.parentBId = parentBId;
        this.stepIndexOfBirth = stepIndexOfBirth;
        this.currentEnergy = currentEnergy;
        this.reproductionCooldownRemaining = reproductionCooldownRemaining;
        this.traits = traits;
        stepIndexOfDeath = -1;
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_AGENT_PET;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    public long petId() {
        return petId;
    }

    public @Nullable Long parentAId() {
        return parentAId;
    }

    public @Nullable Long parentBId() {
        return parentBId;
    }

    public int stepIndexOfBirth() {
        return stepIndexOfBirth;
    }

    public PetTraits traits() {
        return traits;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public int ageAtStepIndex(int stepIndex) {
        return stepIndex - stepIndexOfBirth;
    }

    public int ageAtStepCount(int stepCount) {
        return ageAtStepIndex(stepCount - 1);
    }

    public void changeEnergy(int delta) {
        currentEnergy += delta;
        if (currentEnergy > traits.maxEnergy()) {
            currentEnergy = traits.maxEnergy();
        }
    }

    public int reproductionCooldownRemaining() {
        return reproductionCooldownRemaining;
    }

    public void decrementReproductionCooldown() {
        if (reproductionCooldownRemaining > 0) {
            reproductionCooldownRemaining--;
        }
    }

    public void setReproductionCooldownRemaining(int reproductionCooldownRemaining) {
        this.reproductionCooldownRemaining = reproductionCooldownRemaining;
    }

    public boolean isDead() {
        return dead;
    }

    public int stepIndexOfDeath() {
        return stepIndexOfDeath;
    }

    @SuppressWarnings("ParameterHidesMemberVariable")
    public void markDead(int stepIndexOfDeath) {
        dead = true;
        this.stepIndexOfDeath = stepIndexOfDeath;
    }

    @Override
    public String toDisplayString() {
        return String.format("[PET#%d E=%d %s]", petId, currentEnergy, dead ? "DEAD" : "alive");
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", currentEnergy=" + currentEnergy +
                ", reproductionCooldownRemaining=" + reproductionCooldownRemaining +
                ", dead=" + dead +
                '}';
    }

}

package de.mkalb.etpetssim.simulations.etpets.model.entity;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsBalance;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class Pet implements AgentEntity {

    private final long petId;
    private final @Nullable Long parentAId;
    private final @Nullable Long parentBId;
    private final int stepIndexOfBirth;
    private final PetTraits traits;
    private final Deque<GridCoordinate> movementHistory;
    private int currentEnergy;
    private int reproductionCooldownRemaining;
    private @Nullable PetLastAction lastAction;
    private boolean dead;

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
        movementHistory = new LinkedList<>();
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_PET;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public boolean isEmpty() {
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

    public boolean canEat() {
        return currentEnergy < traits.maxEnergy();
    }

    public int ageAtStepIndex(int stepIndex) {
        return stepIndex - stepIndexOfBirth;
    }

    public int ageAtStepCount(int stepCount) {
        return ageAtStepIndex(stepCount - 1);
    }

    public boolean hasReachedAgeingEffectsAge(int stepIndex) {
        return ageAtStepIndex(stepIndex) >= EtpetsBalance.PET_AGEING_EFFECTS_AGE_MIN;
    }

    public int ageingStepsAtStepIndex(int stepIndex) {
        return Math.max(0, ageAtStepIndex(stepIndex) - EtpetsBalance.PET_AGEING_EFFECTS_AGE_MIN);
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

    public boolean hasCoordinateInMovementHistory(GridCoordinate coordinate) {
        return movementHistory.contains(coordinate);
    }

    public @Nullable PetLastAction lastAction() {
        return lastAction;
    }

    public void recordLastAction(PetActionType type, int score) {
        lastAction = new PetLastAction(type, score);
    }

    public void recordMoveFrom(GridCoordinate fromCoordinate) {
        movementHistory.addFirst(fromCoordinate);
        if (movementHistory.size() > EtpetsBalance.PET_MOVE_HISTORY_LENGTH) {
            movementHistory.removeLast();
        }
    }

    public void tickReproductionCooldown() {
        reproductionCooldownRemaining = Math.max(
                EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN,
                reproductionCooldownRemaining - 1
        );
    }

    public boolean isReproductionEligibleByState(int stepIndex) {
        return !dead
                && (currentEnergy >= traits.reproductionMinEnergy())
                && (reproductionCooldownRemaining <= EtpetsBalance.PET_REPRODUCTION_COOLDOWN_REMAINING_RANGE_MIN)
                && (ageAtStepIndex(stepIndex) >= EtpetsBalance.PET_FERTILITY_AGE_RANGE_MIN);
    }

    public void resetReproductionCooldown() {
        reproductionCooldownRemaining = traits.reproductionCooldown();
    }

    public boolean isDead() {
        return dead;
    }

    public void die() {
        dead = true;
    }

    @Override
    public String toDisplayString() {
        String lastActionDisplay = (lastAction != null)
                ? (lastAction.type() + "@" + lastAction.score())
                : "-";
        return String.format(Locale.ROOT, "[PET #%d *%d E=%d C=%d A=%s S=%s]",
                petId,
                stepIndexOfBirth,
                currentEnergy,
                reproductionCooldownRemaining,
                lastActionDisplay,
                dead ? "DEAD" : "ALIVE");
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", parentAId=" + parentAId +
                ", parentBId=" + parentBId +
                ", stepIndexOfBirth=" + stepIndexOfBirth +
                ", traits=" + traits +
                ", currentEnergy=" + currentEnergy +
                ", reproductionCooldownRemaining=" + reproductionCooldownRemaining +
                ", movementHistory=" + movementHistory +
                ", lastAction=" + lastAction +
                ", dead=" + dead +
                '}';
    }

    public record PetLastAction(PetActionType type, int score) {
    }

}

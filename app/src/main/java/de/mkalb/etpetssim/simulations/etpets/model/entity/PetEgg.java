package de.mkalb.etpetssim.simulations.etpets.model.entity;

import java.util.*;

public final class PetEgg implements AgentEntity {

    private final int id;
    private final int parentAId;
    private final int parentBId;
    private final PetGenome petGenome;
    private final int stepIndexOfLaying;
    private int incubationRemaining;

    public PetEgg(int id,
                  int parentAId,
                  int parentBId,
                  PetGenome petGenome,
                  int stepIndexOfLaying,
                  int incubationRemaining) {
        this.id = id;
        this.parentAId = parentAId;
        this.parentBId = parentBId;
        this.petGenome = petGenome;
        this.stepIndexOfLaying = stepIndexOfLaying;
        this.incubationRemaining = incubationRemaining;
    }

    @Override
    public String descriptorId() {
        return EtpetsEntity.DESCRIPTOR_ID_PET_EGG;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int id() {
        return id;
    }

    public int parentAId() {
        return parentAId;
    }

    public int parentBId() {
        return parentBId;
    }

    public PetGenome petGenome() {
        return petGenome;
    }

    public int stepIndexOfLaying() {
        return stepIndexOfLaying;
    }

    public int incubationRemaining() {
        return incubationRemaining;
    }

    public void decrementIncubationRemaining() {
        incubationRemaining--;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT,
                "[PET_EGG #%d *%d I=%d]",
                id,
                stepIndexOfLaying,
                incubationRemaining);
    }

    @Override
    public String toString() {
        return "PetEgg{" +
                "id=" + id +
                ", parentAId=" + parentAId +
                ", parentBId=" + parentBId +
                ", petGenome=" + petGenome +
                ", stepIndexOfLaying=" + stepIndexOfLaying +
                ", incubationRemaining=" + incubationRemaining +
                '}';
    }

}

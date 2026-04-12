package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class PetEgg implements AgentEntity {

    private final long eggId;
    private final long parentAId;
    private final long parentBId;
    private final PetGenome petGenome;
    private final int stepIndexOfLaying;
    private int incubationRemaining;

    public PetEgg(long eggId,
                  long parentAId,
                  long parentBId,
                  PetGenome petGenome,
                  int stepIndexOfLaying,
                  int incubationRemaining) {
        this.eggId = eggId;
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

    public long eggId() {
        return eggId;
    }

    public long parentAId() {
        return parentAId;
    }

    public long parentBId() {
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

    public void decreaseIncubation() {
        incubationRemaining--;
    }

    @Override
    public String toDisplayString() {
        return String.format("[EGG #%d *%d I=%d]", eggId, stepIndexOfLaying, incubationRemaining);
    }

    @Override
    public String toString() {
        return "PetEgg{" +
                "eggId=" + eggId +
                ", parentAId=" + parentAId +
                ", parentBId=" + parentBId +
                ", petGenome=" + petGenome +
                ", stepIndexOfLaying=" + stepIndexOfLaying +
                ", incubationRemaining=" + incubationRemaining +
                '}';
    }

}

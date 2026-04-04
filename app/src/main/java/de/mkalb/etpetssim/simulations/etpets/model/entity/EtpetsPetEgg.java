package de.mkalb.etpetssim.simulations.etpets.model.entity;

public final class EtpetsPetEgg implements EtpetsAgentEntity {

    private final long eggId;
    private final long parentAId;
    private final long parentBId;
    private final EtpetsPetGenome petGenome;
    private final int stepIndexOfLaying;
    private int incubationRemaining;

    public EtpetsPetEgg(long eggId,
                        long parentAId,
                        long parentBId,
                        EtpetsPetGenome petGenome,
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
        return EtpetsEntity.DESCRIPTOR_ID_AGENT_PET_EGG;
    }

    @Override
    public boolean isNone() {
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

    public EtpetsPetGenome petGenome() {
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

}


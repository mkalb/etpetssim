package de.mkalb.etpetssim.simulations.sugar.model.entity;

public final class SugarAgent implements SugarAgentEntity {

    private final int stepIndexOfSpawn;
    private int currentEnergy;

    public SugarAgent(int currentEnergy, int stepIndexOfSpawn) {
        this.currentEnergy = currentEnergy;
        this.stepIndexOfSpawn = stepIndexOfSpawn;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return SugarEntity.DESCRIPTOR_ID_AGENT;
    }

    public int currentEnergy() {
        return currentEnergy;
    }

    public void reduceEnergy(int loss) {
        currentEnergy = currentEnergy - loss;
    }

    public void gainEnergy(int gain) {
        currentEnergy = currentEnergy + gain;
    }

    public int stepIndexOfSpawn() {
        return stepIndexOfSpawn;
    }

    public int ageAtStepIndex(int stepIndex) {
        return stepIndex - stepIndexOfSpawn;
    }

    public int ageAtStepCount(int stepCount) {
        return ageAtStepIndex(stepCount - 1);
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public String toString() {
        return "SugarAgent{" +
                "currentEnergy=" + currentEnergy +
                ", stepIndexOfSpawn=" + stepIndexOfSpawn +
                "}";
    }

}

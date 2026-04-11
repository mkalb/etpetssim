package de.mkalb.etpetssim.simulations.sugar.model.entity;

public final class Agent implements AgentEntity {

    private final int stepIndexOfSpawn;
    private int currentEnergy;

    public Agent(int currentEnergy, int stepIndexOfSpawn) {
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
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format("[AGENT *%d E=%d]", stepIndexOfSpawn, currentEnergy);
    }

    @Override
    public String toString() {
        return "Agent{" +
                "stepIndexOfSpawn=" + stepIndexOfSpawn +
                ", currentEnergy=" + currentEnergy +
                "}";
    }

}

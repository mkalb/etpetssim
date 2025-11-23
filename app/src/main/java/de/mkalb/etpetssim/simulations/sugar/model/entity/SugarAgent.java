package de.mkalb.etpetssim.simulations.sugar.model.entity;

public final class SugarAgent implements SugarAgentEntity {

    private int currentEnergy;

    public SugarAgent(int currentEnergy) {
        this.currentEnergy = currentEnergy;
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
                "}";
    }

}

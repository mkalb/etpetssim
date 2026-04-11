package de.mkalb.etpetssim.simulations.sugar.model.entity;

public final class Sugar implements ResourceEntity {

    private final int maxAmount;
    private int currentAmount;

    public Sugar(int maxAmount, int currentAmount) {
        this.maxAmount = maxAmount;
        this.currentAmount = currentAmount;
    }

    /**
     * Returns the unique descriptor ID for this entity.
     *
     * @return the descriptor ID string
     */
    @Override
    public String descriptorId() {
        return SugarEntity.DESCRIPTOR_ID_SUGAR;
    }

    public int maxAmount() {
        return maxAmount;
    }

    public int currentAmount() {
        return currentAmount;
    }

    public void reduceEnergy(int loss) {
        currentAmount = Math.max(currentAmount - loss, 0);
    }

    public void gainEnergy(int gain) {
        currentAmount = Math.min(currentAmount + gain, maxAmount);
    }

    @Override
    public boolean isResource() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public String toDisplayString() {
        return String.format("[SUGAR %d/%d]", currentAmount, maxAmount);
    }

    @Override
    public String toString() {
        return "Sugar{" +
                "maxAmount=" + maxAmount +
                ", currentAmount=" + currentAmount +
                "}";
    }

}

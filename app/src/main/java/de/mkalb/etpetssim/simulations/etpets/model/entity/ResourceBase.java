package de.mkalb.etpetssim.simulations.etpets.model.entity;

public abstract sealed class ResourceBase implements ResourceEntity
        permits Plant, Insect {

    private final double maxAmount;
    private final double regenerationPerStep;
    private double currentAmount;

    protected ResourceBase(double currentAmount, double maxAmount, double regenerationPerStep) {
        this.currentAmount = currentAmount;
        this.maxAmount = maxAmount;
        this.regenerationPerStep = regenerationPerStep;
    }

    @Override
    public final boolean isResource() {
        return true;
    }

    @Override
    public final boolean isEmpty() {
        return false;
    }

    public final double currentAmount() {
        return currentAmount;
    }

    public final double maxAmount() {
        return maxAmount;
    }

    public final double regenerationPerStep() {
        return regenerationPerStep;
    }

    public final boolean canConsume() {
        return currentAmount >= consumptionPerAct();
    }

    public final void consume() {
        currentAmount = Math.max(0.0d, currentAmount - consumptionPerAct());
    }

    public final void regenerate() {
        currentAmount = Math.min(maxAmount, currentAmount + regenerationPerStep);
    }

    protected abstract int consumptionPerAct();

    public abstract int energyGainPerAct();

}

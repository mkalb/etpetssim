package de.mkalb.etpetssim.simulations.etpets.model.entity;

/**
 * Base class of the ET Pets resources, whose amount pets consume and that regenerates up to its maximum amount.
 */
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
        currentAmount = Math.max(minAmount(), currentAmount - consumptionPerAct());
    }

    public final void regenerate() {
        currentAmount = Math.min(maxAmount, currentAmount + regenerationPerStep);
    }

    public abstract int energyGainPerAct();

    protected abstract double minAmount();

    protected abstract int consumptionPerAct();

}

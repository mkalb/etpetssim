package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

public final class Rebounder implements ReboundingEntity {

    private CompassDirection direction;

    public Rebounder(CompassDirection direction) {
        this.direction = direction;
    }

    @Override
    public String descriptorId() {
        return ReboundingEntity.DESCRIPTOR_ID_REBOUNDER;
    }

    @Override
    public boolean isGround() {
        return false;
    }

    @Override
    public boolean isWall() {
        return false;
    }

    @Override
    public boolean isRebounder() {
        return true;
    }

    public CompassDirection getDirection() {
        return direction;
    }

    public void setDirection(CompassDirection direction) {
        this.direction = direction;
    }

    @Override
    public String toDisplayString() {
        return String.format("[MOVING_ENTITY %s]", direction);
    }

    @Override
    public String toString() {
        return "Rebounder{" +
                "direction=" + direction +
                '}';
    }

}

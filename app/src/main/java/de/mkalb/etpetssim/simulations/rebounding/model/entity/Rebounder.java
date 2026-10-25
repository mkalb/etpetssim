package de.mkalb.etpetssim.simulations.rebounding.model.entity;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

import java.util.*;

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

    public CompassDirection direction() {
        return direction;
    }

    public void changeDirection(CompassDirection newDirection) {
        direction = newDirection;
    }

    @Override
    public String toDisplayString() {
        return String.format(Locale.ROOT, "[REBOUNDER %s]",
                direction.arrow());
    }

    @Override
    public String toString() {
        return "Rebounder{" +
                "direction=" + direction +
                '}';
    }

}

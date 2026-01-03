package de.mkalb.etpetssim.simulations.langton.model.entity;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

public final class LangtonAnt implements LangtonAntEntity {

    private CompassDirection direction;

    public LangtonAnt(CompassDirection direction) {
        this.direction = direction;
    }

    @Override
    public boolean isAgent() {
        return true;
    }

    @Override
    public String descriptorId() {
        return DESCRIPTOR_ID_ANT;
    }

    public CompassDirection direction() {
        return direction;
    }

    public void changeDirection(CompassDirection newDirection) {
        direction = newDirection;
    }

    @Override
    public String toDisplayString() {
        return String.format("[ANT %s]", direction.arrow());
    }

    @Override
    public String toString() {
        return "LangtonAnt{" +
                "direction=" + direction +
                '}';
    }

}

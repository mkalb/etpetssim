package de.mkalb.etpetssim.simulations.langton.model;

import de.mkalb.etpetssim.engine.neighborhood.CompassDirection;

@SuppressWarnings("ClassCanBeRecord")
public final class LangtonAnt implements LangtonAntEntity {

    private final CompassDirection direction;

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

    @Override
    public String toDisplayString() {
        return "TODO"; // TODO implement
    }

    public String toString() {
        return "TODO"; // TODO implement
    }

    public CompassDirection direction() {
        return direction;
    }

}

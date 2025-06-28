package de.mkalb.etpetssim.engine;

public interface GridModel<T> {

    GridStructure structure();

    T defaultValue();

}

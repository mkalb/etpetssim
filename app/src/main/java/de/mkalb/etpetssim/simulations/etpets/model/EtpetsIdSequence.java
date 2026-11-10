package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sequence of the ascending IDs of the ET Pets pets and eggs, starting at the initial value.
 */
public final class EtpetsIdSequence {

    private final AtomicInteger nextValue;

    public EtpetsIdSequence(int initialValue) {
        nextValue = new AtomicInteger(initialValue);
    }

    public int next() {
        return nextValue.getAndIncrement();
    }

}

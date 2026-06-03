package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.concurrent.atomic.*;

public final class EtpetsIdSequence {

    private final AtomicInteger nextValue;

    public EtpetsIdSequence(int initialValue) {
        nextValue = new AtomicInteger(initialValue);
    }

    public int next() {
        return nextValue.getAndIncrement();
    }

}


package de.mkalb.etpetssim.simulations.etpets.model;

import java.util.concurrent.atomic.*;

public final class EtpetsIdSequence {

    private final AtomicLong nextValue;

    public EtpetsIdSequence(long initialValue) {
        nextValue = new AtomicLong(initialValue);
    }

    public long next() {
        return nextValue.getAndIncrement();
    }

}


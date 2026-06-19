package de.mkalb.etpetssim.simulations.conway.model;

import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

import java.util.function.*;

public record ConwayPatternChoice(
        String choiceId,
        String labelKey,
        Supplier<GridPattern<ConwayEntity>> patternSupplier,
        Predicate<ConwayConfig> availabilityRule) {

    public ConwayPatternChoice {
        if (choiceId.isBlank()) {
            throw new IllegalArgumentException("choiceId must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
    }

    public GridPattern<ConwayEntity> pattern() {
        return patternSupplier.get().normalized();
    }

    public boolean availableFor(ConwayConfig config) {
        return availabilityRule.test(config);
    }

}


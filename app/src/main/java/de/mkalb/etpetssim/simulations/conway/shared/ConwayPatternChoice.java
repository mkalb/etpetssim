package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

import java.util.function.*;

public record ConwayPatternChoice(
        String choiceId,
        String labelKey,
        Supplier<GridPattern<ConwayEntity>> patternSupplier,
        BiPredicate<CellShape, ConwayTransitionRules> availabilityRule) {

    public ConwayPatternChoice {
        if (choiceId.isBlank()) {
            throw new IllegalArgumentException("choiceId must not be blank");
        }
        if (labelKey.isBlank()) {
            throw new IllegalArgumentException("labelKey must not be blank");
        }
        if (choiceId.equals(labelKey)) {
            throw new IllegalArgumentException("choiceId must differ from labelKey");
        }
    }

    public GridPattern<ConwayEntity> pattern() {
        return patternSupplier.get().normalized();
    }

    public boolean isAvailableFor(CellShape cellShape, ConwayTransitionRules transitionRules) {
        return availabilityRule.test(cellShape, transitionRules);
    }

}


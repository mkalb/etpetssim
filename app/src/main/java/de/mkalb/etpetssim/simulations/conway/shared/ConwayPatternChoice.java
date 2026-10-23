package de.mkalb.etpetssim.simulations.conway.shared;

import de.mkalb.etpetssim.engine.CellShape;
import de.mkalb.etpetssim.engine.support.GridPattern;
import de.mkalb.etpetssim.simulations.conway.model.entity.ConwayEntity;

import java.util.function.*;

/**
 * Pattern that the Conway edit toolbar offers for placement on the grid.
 *
 * <p>The pattern is created on demand by the supplier; the availability rule decides for which cell shape and
 * transition rules the pattern is offered.
 *
 * @param choiceId         stable identifier of the choice, distinct from {@code labelKey}
 * @param labelKey         localization key for the display text of the pattern
 * @param patternSupplier  supplier of the pattern
 * @param availabilityRule rule that decides whether the pattern is offered for a cell shape and transition rules
 */
public record ConwayPatternChoice(
        String choiceId,
        String labelKey,
        Supplier<GridPattern<ConwayEntity>> patternSupplier,
        BiPredicate<CellShape, ConwayTransitionRules> availabilityRule) {

    /**
     * Creates a pattern choice.
     *
     * @throws IllegalArgumentException if {@code choiceId} or {@code labelKey} is blank, or if both are equal
     */
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

    /**
     * Returns a new pattern from the supplier, normalized to the origin.
     *
     * @return normalized pattern; each call asks the supplier again
     */
    public GridPattern<ConwayEntity> pattern() {
        return patternSupplier.get().normalized();
    }

    public boolean isAvailableFor(CellShape cellShape, ConwayTransitionRules transitionRules) {
        return availabilityRule.test(cellShape, transitionRules);
    }

}


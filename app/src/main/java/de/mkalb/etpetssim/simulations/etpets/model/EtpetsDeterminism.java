package de.mkalb.etpetssim.simulations.etpets.model;

import de.mkalb.etpetssim.engine.GridCoordinate;
import de.mkalb.etpetssim.simulations.etpets.model.entity.Pet;

public final class EtpetsDeterminism {

    private EtpetsDeterminism() {
    }

    public static int compareCoordinates(GridCoordinate left, GridCoordinate right) {
        int xCompare = Integer.compare(left.x(), right.x());
        if (xCompare != 0) {
            return xCompare;
        }
        return Integer.compare(left.y(), right.y());
    }

    /**
     * Compares two pet/coordinate pairs for reproduction partner selection priority.
     * <p>
     * Order: highest genomeQualityScore (desc), highest currentEnergy (desc),
     * lowest petId (asc), then coordinate order (asc).
     */
    public static int comparePetsForReproduction(Pet a, GridCoordinate aCoord,
                                                 Pet b, GridCoordinate bCoord) {
        int qCmp = Double.compare(b.traits().genomeQualityScore(), a.traits().genomeQualityScore());
        if (qCmp != 0) {
            return qCmp;
        }
        int eCmp = Integer.compare(b.currentEnergy(), a.currentEnergy());
        if (eCmp != 0) {
            return eCmp;
        }
        int idCmp = Long.compare(a.petId(), b.petId());
        if (idCmp != 0) {
            return idCmp;
        }
        return compareCoordinates(aCoord, bCoord);
    }

}

import org.jspecify.annotations.NullMarked;

/**
 * Extraterrestrial Pets Simulation (etpetssim), a JavaFX application for 2D grid-based simulations
 * such as agent- and movement-based models and cellular automata.
 */
@NullMarked
open module de.mkalb.etpetssim {
    requires java.logging;
    requires javafx.controls;
    requires static org.jspecify;
}

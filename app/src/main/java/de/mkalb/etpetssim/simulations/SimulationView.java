package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.simulations.conway.view.ConwayView;
import de.mkalb.etpetssim.simulations.lab.view.LabView;
import de.mkalb.etpetssim.simulations.start.StartView;
import de.mkalb.etpetssim.simulations.wator.WaTorController;
import javafx.scene.layout.Region;

/**
 * The SimulationView interface represents a contract for all simulation views
 * in the application. It is a sealed interface, meaning only the specified
 * permitted classes can implement it.
 *
 * Permitted implementations:
 * - {@link ConwayView}
 * - {@link LabView}
 * - {@link de.mkalb.etpetssim.simulations.start.StartView}
 * - {@link WaTorController}
 */
public sealed interface SimulationView
        permits ConwayView, LabView, StartView, WaTorController {

    /**
     * Builds the main view region for the simulation.
     *
     * This method is responsible for constructing and returning the primary
     * UI component (a {@link Region}) for the simulation. Each implementation
     * of this interface should provide its own specific view region.
     *
     * @return the main view region for the simulation
     */
    Region buildViewRegion();

}

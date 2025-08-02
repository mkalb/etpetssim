package de.mkalb.etpetssim.simulations;

import de.mkalb.etpetssim.simulations.conway.view.ConwayMainView;
import de.mkalb.etpetssim.simulations.lab.view.LabMainView;
import de.mkalb.etpetssim.simulations.start.StartView;
import de.mkalb.etpetssim.simulations.wator.WaTorController;
import de.mkalb.etpetssim.simulations.wator.view.WatorMainView;
import javafx.scene.layout.Region;

/**
 * The SimulationView interface represents a contract for all simulation views
 * in the application. It is a sealed interface, meaning only the specified
 * permitted classes can implement it.
 *
 * Permitted implementations:
 * - {@link de.mkalb.etpetssim.simulations.conway.view.ConwayMainView}
 * - {@link de.mkalb.etpetssim.simulations.lab.view.LabMainView}
 * - {@link de.mkalb.etpetssim.simulations.start.StartView}
 * - {@link WaTorController}
 */
public sealed interface SimulationView
        permits ConwayMainView, LabMainView, StartView, WatorMainView, WaTorController {

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

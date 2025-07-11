@org.jspecify.annotations.NullMarked
module de.mkalb.etpetssim {
    requires org.jspecify;
    requires javafx.controls;
    requires java.logging;
    exports de.mkalb.etpetssim;
    exports de.mkalb.etpetssim.core;
    exports de.mkalb.etpetssim.engine;
    exports de.mkalb.etpetssim.engine.model;
    exports de.mkalb.etpetssim.simulations;
    exports de.mkalb.etpetssim.simulations.simulationlab;
    exports de.mkalb.etpetssim.simulations.startscreen;
    exports de.mkalb.etpetssim.simulations.wator;
}
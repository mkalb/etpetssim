@org.jspecify.annotations.NullMarked
module de.mkalb.etpetssim {
    requires org.jspecify;
    requires javafx.controls;
    requires java.logging;
    exports de.mkalb.etpetssim.core;
    exports de.mkalb.etpetssim.engine.model;
    exports de.mkalb.etpetssim.engine;
    exports de.mkalb.etpetssim.simulations.conway.model;
    exports de.mkalb.etpetssim.simulations.conway.view;
    exports de.mkalb.etpetssim.simulations.conway.viewmodel;
    exports de.mkalb.etpetssim.simulations.lab;
    exports de.mkalb.etpetssim.simulations.start;
    exports de.mkalb.etpetssim.simulations.wator;
    exports de.mkalb.etpetssim.simulations;
    exports de.mkalb.etpetssim.ui;
    exports de.mkalb.etpetssim;
}
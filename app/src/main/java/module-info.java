@org.jspecify.annotations.NullMarked
module de.mkalb.etpetssim {
    requires org.jspecify;
    requires javafx.controls;
    requires java.logging;
    exports de.mkalb.etpetssim.core;
    exports de.mkalb.etpetssim.engine.model;
    exports de.mkalb.etpetssim.engine.neighborhood;
    exports de.mkalb.etpetssim.engine;
    exports de.mkalb.etpetssim.simulations.conway.model;
    exports de.mkalb.etpetssim.simulations.conway.view;
    exports de.mkalb.etpetssim.simulations.conway.viewmodel;
    exports de.mkalb.etpetssim.simulations.lab.model;
    exports de.mkalb.etpetssim.simulations.lab.view;
    exports de.mkalb.etpetssim.simulations.lab.viewmodel;
    exports de.mkalb.etpetssim.simulations.langton.model;
    exports de.mkalb.etpetssim.simulations.langton.view;
    exports de.mkalb.etpetssim.simulations.langton.viewmodel;
    exports de.mkalb.etpetssim.simulations.model;
    exports de.mkalb.etpetssim.simulations.start;
    exports de.mkalb.etpetssim.simulations.view;
    exports de.mkalb.etpetssim.simulations.viewmodel;
    exports de.mkalb.etpetssim.simulations.wator.model;
    exports de.mkalb.etpetssim.simulations.wator.view;
    exports de.mkalb.etpetssim.simulations.wator.viewmodel;
    exports de.mkalb.etpetssim.simulations;
    exports de.mkalb.etpetssim.ui;
    exports de.mkalb.etpetssim;
}
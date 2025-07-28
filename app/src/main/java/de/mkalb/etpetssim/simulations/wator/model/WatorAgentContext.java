package de.mkalb.etpetssim.simulations.wator.model;

import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.GridModel;

public record WatorAgentContext(
        GridCell<WatorEntity> cell,
        GridModel<WatorEntity> model,
        WatorStatistics statistics
) {}

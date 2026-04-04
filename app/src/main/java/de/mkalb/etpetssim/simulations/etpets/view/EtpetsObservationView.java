package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalization;
import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

public final class EtpetsObservationView
        extends AbstractObservationView<EtpetsStatistics, DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics>> {

    private static final String ETPETS_OBSERVATION_ACTIVE_PETS = "etpets.observation.cells.pets";
    private static final String ETPETS_OBSERVATION_EGGS = "etpets.observation.cells.eggs";
    private static final String ETPETS_OBSERVATION_DEAD_PETS = "etpets.observation.pets.dead";

    private final Label stepCountLabel = new Label();
    private final Label activePetsLabel = new Label();
    private final Label eggsLabel = new Label();
    private final Label deadPetsLabel = new Label();

    private @Nullable NumberFormat intFormat;

    public EtpetsObservationView(DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics> viewModel) {
        super(viewModel);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        String[] nameKeys = {
                AppLocalizationKeys.OBSERVATION_STEP,
                ETPETS_OBSERVATION_ACTIVE_PETS,
                ETPETS_OBSERVATION_EGGS,
                ETPETS_OBSERVATION_DEAD_PETS
        };
        Label[] valueLabels = {
                stepCountLabel,
                activePetsLabel,
                eggsLabel,
                deadPetsLabel
        };

        intFormat = NumberFormat.getIntegerInstance(AppLocalization.locale());

        return createObservationScrollPane(createObservationGrid(nameKeys, valueLabels));
    }

    @Override
    protected void updateObservationLabels() {
        Optional<EtpetsStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent() && (intFormat != null)) {
            var current = statistics.get();
            stepCountLabel.setText(intFormat.format(current.getStepCount()));
            activePetsLabel.setText(intFormat.format(current.getActivePetCount()));
            eggsLabel.setText(intFormat.format(current.getEggCount()));
            deadPetsLabel.setText(intFormat.format(current.getCumulativeDeadPetCount()));
        } else {
            String valueUnknown = AppLocalization.getText(AppLocalizationKeys.OBSERVATION_VALUE_UNKNOWN);
            stepCountLabel.setText(valueUnknown);
            activePetsLabel.setText(valueUnknown);
            eggsLabel.setText(valueUnknown);
            deadPetsLabel.setText(valueUnknown);
        }
    }

}


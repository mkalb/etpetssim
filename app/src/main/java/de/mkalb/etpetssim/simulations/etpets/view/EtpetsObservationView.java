package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.EtpetsStatistics;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsObservationView
        extends AbstractObservationView<
        EtpetsEntity,
        EtpetsStatistics,
        DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics>> {

    private static final String ETPETS_OBSERVATION_ACTIVE_PETS = "etpets.observation.cells.pets";
    private static final String ETPETS_OBSERVATION_EGGS = "etpets.observation.cells.eggs";
    private static final String ETPETS_OBSERVATION_DEAD_PETS = "etpets.observation.pets.dead";

    private final Label activePetsLabel = new Label();
    private final Label eggsLabel = new Label();
    private final Label deadPetsLabel = new Label();

    public EtpetsObservationView(DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable GridCell<EtpetsEntity> gridCell) {
        super.onSelectedCellChanged(gridCell);
        // TODO EtpetsObservationView: Add and update more labels for selected cell
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        ETPETS_OBSERVATION_ACTIVE_PETS,
                        ETPETS_OBSERVATION_EGGS,
                        ETPETS_OBSERVATION_DEAD_PETS
                },
                new Label[]{
                        activePetsLabel,
                        eggsLabel,
                        deadPetsLabel
                }
        );
        Region selectedCellSection = createSelectedCellSection();
        onSelectedCellChanged(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<EtpetsStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(activePetsLabel, current.getActivePetCount());
            setFormattedIntegerValue(eggsLabel, current.getEggCount());
            setFormattedIntegerValue(deadPetsLabel, current.getCumulativeDeadPetCount());
        } else {
            setUnknownValues(
                    activePetsLabel,
                    eggsLabel,
                    deadPetsLabel);
        }
    }

}

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
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsObservationView
        extends AbstractObservationView<EtpetsStatistics, DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics>> {

    private static final String ETPETS_OBSERVATION_ACTIVE_PETS = "etpets.observation.cells.pets";
    private static final String ETPETS_OBSERVATION_DEAD_PETS = "etpets.observation.pets.dead";
    private static final String ETPETS_OBSERVATION_EGGS = "etpets.observation.cells.eggs";

    private final Label stepCountLabel = new Label();
    private final Label totalCellsLabel = new Label();
    private final Label activePetsLabel = new Label();
    private final Label eggsLabel = new Label();
    private final Label deadPetsLabel = new Label();
    private final Label coordinateLabel = new Label();
    private final Label cellTypeLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public EtpetsObservationView(DefaultObservationViewModel<EtpetsEntity, EtpetsStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<EtpetsEntity> gridCell) {
        updateSelectedCellSectionVisibility(selectedCellSection, gridCell != null);

        updateSelectedCellBasicLabels(coordinateLabel, cellTypeLabel, gridCell);
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATUS,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_STEP
                },
                new Label[]{
                        stepCountLabel
                }
        );
        Region gridSection = createGridSection(totalCellsLabel);
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
        selectedCellSection = createSelectedCellSection(coordinateLabel, cellTypeLabel);
        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                selectedCellSection
        );
    }

    @Override
    protected void initializeObservationLabels() {
        updateGridSectionLabel(totalCellsLabel);
        updateObservationLabels();
    }

    @Override
    protected void updateObservationLabels() {
        Optional<EtpetsStatistics> statistics = viewModel.getStatistics();

        if (statistics.isPresent()) {
            var current = statistics.get();
            setFormattedIntegerValue(stepCountLabel, current.getStepCount());
            setFormattedIntegerValue(activePetsLabel, current.getActivePetCount());
            setFormattedIntegerValue(eggsLabel, current.getEggCount());
            setFormattedIntegerValue(deadPetsLabel, current.getCumulativeDeadPetCount());
        } else {
            setUnknownValues(stepCountLabel, activePetsLabel, eggsLabel, deadPetsLabel);
        }
    }

}


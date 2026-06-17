package de.mkalb.etpetssim.simulations.etpets.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.etpets.model.*;
import de.mkalb.etpetssim.simulations.etpets.model.entity.EtpetsEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsObservationView
        extends AbstractObservationView<
        EtpetsEntity,
        EtpetsCell,
        EtpetsStatistics,
        DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics>> {

    private static final String ETPETS_OBSERVATION_ACTIVE_PET_CELLS = "etpets.observation.cells.activepets";
    private static final String ETPETS_OBSERVATION_EGG_CELLS = "etpets.observation.cells.eggs";
    private static final String ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT = "etpets.observation.cumulativepetdeathcount";

    private final Label activePetCellsLabel = new Label();
    private final Label eggCellsLabel = new Label();
    private final Label cumulativePetDeathCountLabel = new Label();

    public EtpetsObservationView(DefaultObservationViewModel<EtpetsEntity, EtpetsCell, EtpetsStatistics> viewModel,
                                 GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        registerSelectedCellListener(viewModel.selectedGridCellProperty());
    }

    @Override
    protected void onSelectedCellChanged(@Nullable EtpetsCell gridCell) {
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
                        ETPETS_OBSERVATION_ACTIVE_PET_CELLS,
                        ETPETS_OBSERVATION_EGG_CELLS,
                        ETPETS_OBSERVATION_CUMULATIVE_PET_DEATH_COUNT
                },
                new Label[]{
                        activePetCellsLabel,
                        eggCellsLabel,
                        cumulativePetDeathCountLabel
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
            setFormattedIntegerValue(activePetCellsLabel, current.getActivePetCells());
            setFormattedIntegerValue(eggCellsLabel, current.getEggCells());
            setFormattedIntegerValue(cumulativePetDeathCountLabel, current.getCumulativePetDeathCount());
        } else {
            setUnknownValues(
                    activePetCellsLabel,
                    eggCellsLabel,
                    cumulativePetDeathCountLabel);
        }
    }

}

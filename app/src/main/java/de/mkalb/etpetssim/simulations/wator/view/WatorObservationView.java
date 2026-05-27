package de.mkalb.etpetssim.simulations.wator.view;

import de.mkalb.etpetssim.core.AppLocalizationKeys;
import de.mkalb.etpetssim.engine.model.GridCell;
import de.mkalb.etpetssim.engine.model.entity.GridEntityDescriptorRegistry;
import de.mkalb.etpetssim.simulations.core.view.AbstractObservationView;
import de.mkalb.etpetssim.simulations.core.viewmodel.DefaultObservationViewModel;
import de.mkalb.etpetssim.simulations.wator.model.WatorStatistics;
import de.mkalb.etpetssim.simulations.wator.model.entity.CreatureBase;
import de.mkalb.etpetssim.simulations.wator.model.entity.WatorEntity;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class WatorObservationView
        extends AbstractObservationView<WatorStatistics, DefaultObservationViewModel<WatorEntity, WatorStatistics>> {

    private static final String WATOR_OBSERVATION_AGE = "wator.observation.age";
    private static final String WATOR_OBSERVATION_FISH_CELLS = "wator.observation.cells.fish";
    private static final String WATOR_OBSERVATION_MAX_FISH_CELLS = "wator.observation.cells.maxfish";
    private static final String WATOR_OBSERVATION_MAX_SHARK_CELLS = "wator.observation.cells.maxshark";
    private static final String WATOR_OBSERVATION_MIN_FISH_CELLS = "wator.observation.cells.minfish";
    private static final String WATOR_OBSERVATION_MIN_SHARK_CELLS = "wator.observation.cells.minshark";
    private static final String WATOR_OBSERVATION_SHARK_CELLS = "wator.observation.cells.shark";
    private final Label maxFishCellsLabel = new Label();
    private final Label maxSharkCellsLabel = new Label();
    private final Label minFishCellsLabel = new Label();
    private final Label minSharkCellsLabel = new Label();
    private final Label fishCellsLabel = new Label();
    private final Label sharkCellsLabel = new Label();
    private final Label ageLabel = new Label();
    private @Nullable VBox selectedCellSection;

    public WatorObservationView(DefaultObservationViewModel<WatorEntity, WatorStatistics> viewModel,
                                GridEntityDescriptorRegistry entityDescriptorRegistry) {
        super(viewModel, entityDescriptorRegistry);

        viewModel.selectedGridCellProperty().addListener((_, _, newCell) ->
                updateSelectedGridCell(newCell));
    }

    private void updateSelectedGridCell(@Nullable GridCell<WatorEntity> gridCell) {
        Optional<WatorStatistics> statistics = viewModel.getStatistics();
        boolean hasValidCell = gridCell != null;

        updateSelectedCellSectionVisibility(selectedCellSection, hasValidCell);

        if (gridCell != null) {
            updateSelectedCellBasicLabels(gridCell);
            if (statistics.isPresent() && (gridCell.entity() instanceof CreatureBase creature)) {
                setFormattedIntegerValue(ageLabel, creature.ageAtStepCount(statistics.get().getStepCount()));
            } else {
                clearValues(ageLabel);
            }
        } else {
            updateSelectedCellBasicLabels(null);
            clearValues(ageLabel);
        }
    }

    @Override
    public Region buildObservationRegion() {
        updateObservationLabels();

        Region statusSection = createStatusSection();
        Region gridSection = createGridSection();
        Region currentSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_CURRENT,
                new String[]{
                        WATOR_OBSERVATION_FISH_CELLS,
                        WATOR_OBSERVATION_SHARK_CELLS
                },
                new Label[]{
                        fishCellsLabel,
                        sharkCellsLabel
                }
        );
        Region statisticsSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_STATISTICS,
                new String[]{
                        WATOR_OBSERVATION_MIN_FISH_CELLS,
                        WATOR_OBSERVATION_MAX_FISH_CELLS,
                        WATOR_OBSERVATION_MIN_SHARK_CELLS,
                        WATOR_OBSERVATION_MAX_SHARK_CELLS
                },
                new Label[]{
                        minFishCellsLabel,
                        maxFishCellsLabel,
                        minSharkCellsLabel,
                        maxSharkCellsLabel
                }
        );
        selectedCellSection = createObservationSection(
                AppLocalizationKeys.OBSERVATION_SECTION_SELECTED_CELL,
                new String[]{
                        AppLocalizationKeys.OBSERVATION_COORDINATE,
                        AppLocalizationKeys.OBSERVATION_CELL_TYPE,
                        WATOR_OBSERVATION_AGE
                },
                new Label[]{
                        selectedCellCoordinateLabel(),
                        selectedCellTypeLabel(),
                        ageLabel
                }
        );

        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());

        return createObservationScrollPane(
                statusSection,
                gridSection,
                currentSection,
                statisticsSection,
                selectedCellSection
        );
    }

    @Override
    protected void updateObservationLabels() {
        Optional<WatorStatistics> statistics = viewModel.getStatistics();
        updateStatusSectionLabel(statistics);

        if (statistics.isPresent()) {
            WatorStatistics current = statistics.get();
            setFormattedIntegerValue(maxFishCellsLabel, current.getMaxFishCells());
            setFormattedIntegerValue(maxSharkCellsLabel, current.getMaxSharkCells());
            setFormattedIntegerValue(minFishCellsLabel, current.getMinFishCells());
            setFormattedIntegerValue(minSharkCellsLabel, current.getMinSharkCells());
            setFormattedIntegerValue(fishCellsLabel, current.getFishCells());
            setFormattedIntegerValue(sharkCellsLabel, current.getSharkCells());
        } else {
            setUnknownValues(maxFishCellsLabel, maxSharkCellsLabel,
                    minFishCellsLabel, minSharkCellsLabel, fishCellsLabel, sharkCellsLabel);
        }

        updateSelectedGridCell(viewModel.selectedGridCellProperty().get());
    }

}
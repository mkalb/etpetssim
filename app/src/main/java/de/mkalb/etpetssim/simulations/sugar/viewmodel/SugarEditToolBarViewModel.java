package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.simulations.sugar.shared.SugarAddSugarLevel;
import de.mkalb.etpetssim.simulations.sugar.shared.SugarUserActionContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public final class SugarEditToolBarViewModel {

    private final ObservableList<SugarAddSugarLevel> availableAddSugarLevels =
            FXCollections.observableArrayList(SugarAddSugarLevel.values());
    private final ObjectProperty<@Nullable SugarAddSugarLevel> selectedAddSugarLevel = new SimpleObjectProperty<>();

    public SugarEditToolBarViewModel() {
        selectedAddSugarLevel.set(availableAddSugarLevels.isEmpty() ? null : availableAddSugarLevels.getFirst());
    }

    public ObservableList<SugarAddSugarLevel> availableAddSugarLevels() {
        return availableAddSugarLevels;
    }

    public ObjectProperty<@Nullable SugarAddSugarLevel> selectedAddSugarLevelProperty() {
        return selectedAddSugarLevel;
    }

    public @Nullable SugarAddSugarLevel getSelectedAddSugarLevel() {
        return selectedAddSugarLevel.get();
    }

    public void setSelectedAddSugarLevel(@Nullable SugarAddSugarLevel level) {
        if ((level == null) || availableAddSugarLevels.contains(level)) {
            selectedAddSugarLevel.set(level);
        }
    }

    public Optional<SugarUserActionContext.AddSugar> resolveSelectedAddSugarContext() {
        SugarAddSugarLevel level = selectedAddSugarLevel.get();
        if ((level == null) || !availableAddSugarLevels.contains(level)) {
            return Optional.empty();
        }
        return Optional.of(new SugarUserActionContext.AddSugar(level));
    }

}

package de.mkalb.etpetssim.simulations.sugar.viewmodel;

import de.mkalb.etpetssim.simulations.sugar.shared.*;
import javafx.beans.property.*;
import javafx.collections.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

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
        if ((level == null) || containsLevelId(level.levelId())) {
            selectedAddSugarLevel.set(level);
        }
    }

    public Optional<SugarUserActionContext.AddSugar> resolveSelectedAddSugarContext() {
        SugarAddSugarLevel level = selectedAddSugarLevel.get();
        if ((level == null) || !containsLevelId(level.levelId())) {
            return Optional.empty();
        }
        return Optional.of(new SugarUserActionContext.AddSugar(level));
    }

    private boolean containsLevelId(String levelId) {
        return availableAddSugarLevels.stream()
                                      .map(SugarAddSugarLevel::levelId)
                                      .anyMatch(levelId::equals);
    }

}

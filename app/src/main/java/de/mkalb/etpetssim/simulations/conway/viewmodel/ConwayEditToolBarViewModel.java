package de.mkalb.etpetssim.simulations.conway.viewmodel;

import de.mkalb.etpetssim.simulations.conway.model.*;
import javafx.beans.property.*;
import javafx.collections.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class ConwayEditToolBarViewModel {

    private final ObservableList<ConwayPatternChoice> availablePatternChoices = FXCollections.observableArrayList();
    private final ObjectProperty<@Nullable ConwayPatternChoice> selectedPatternChoice = new SimpleObjectProperty<>();

    public ObservableList<ConwayPatternChoice> availablePatternChoices() {
        return availablePatternChoices;
    }

    public ObjectProperty<@Nullable ConwayPatternChoice> selectedPatternChoiceProperty() {
        return selectedPatternChoice;
    }

    public @Nullable ConwayPatternChoice getSelectedPatternChoice() {
        return selectedPatternChoice.get();
    }

    public void setSelectedPatternChoice(@Nullable ConwayPatternChoice patternChoice) {
        if ((patternChoice == null) || containsChoiceId(patternChoice.choiceId())) {
            selectedPatternChoice.set(patternChoice);
        }
    }

    public void updateAvailablePatternChoices(ConwayConfig config) {
        List<ConwayPatternChoice> availableChoices = ConwayPatterns.availableChoices(config);
        availablePatternChoices.setAll(availableChoices);
        selectedPatternChoice.set(availableChoices.isEmpty() ? null : availableChoices.getFirst());
    }

    public Optional<ConwayUserActionContext.PlacePattern> resolveSelectedPatternContext() {
        ConwayPatternChoice patternChoice = selectedPatternChoice.get();
        if ((patternChoice == null) || !containsChoiceId(patternChoice.choiceId())) {
            return Optional.empty();
        }
        return Optional.of(new ConwayUserActionContext.PlacePattern(patternChoice));
    }

    private boolean containsChoiceId(String choiceId) {
        return availablePatternChoices.stream()
                                      .map(ConwayPatternChoice::choiceId)
                                      .anyMatch(choiceId::equals);
    }

}


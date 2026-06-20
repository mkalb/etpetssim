package de.mkalb.etpetssim.simulations.etpets.viewmodel;

import de.mkalb.etpetssim.simulations.etpets.shared.*;
import javafx.beans.property.*;
import javafx.collections.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

public final class EtpetsEditToolBarViewModel {

    private final ObservableList<EtpetsTerrainChoice> availableTerrainChoices =
            FXCollections.observableArrayList(EtpetsTerrainChoice.values());
    private final ObservableList<EtpetsResourceChoice> availableResourceChoices =
            FXCollections.observableArrayList(EtpetsResourceChoice.values());
    private final ObjectProperty<@Nullable EtpetsTerrainChoice> selectedTerrainChoice = new SimpleObjectProperty<>();
    private final ObjectProperty<@Nullable EtpetsResourceChoice> selectedResourceChoice = new SimpleObjectProperty<>();

    public EtpetsEditToolBarViewModel() {
        selectedTerrainChoice.set(availableTerrainChoices.isEmpty() ? null : availableTerrainChoices.getFirst());
        selectedResourceChoice.set(availableResourceChoices.isEmpty() ? null : availableResourceChoices.getFirst());
    }

    public ObservableList<EtpetsTerrainChoice> availableTerrainChoices() {
        return availableTerrainChoices;
    }

    public ObservableList<EtpetsResourceChoice> availableResourceChoices() {
        return availableResourceChoices;
    }

    public ObjectProperty<@Nullable EtpetsTerrainChoice> selectedTerrainChoiceProperty() {
        return selectedTerrainChoice;
    }

    public ObjectProperty<@Nullable EtpetsResourceChoice> selectedResourceChoiceProperty() {
        return selectedResourceChoice;
    }

    public @Nullable EtpetsTerrainChoice getSelectedTerrainChoice() {
        return selectedTerrainChoice.get();
    }

    public void setSelectedTerrainChoice(@Nullable EtpetsTerrainChoice terrainChoice) {
        if ((terrainChoice == null) || containsTerrainChoiceId(terrainChoice.choiceId())) {
            selectedTerrainChoice.set(terrainChoice);
        }
    }

    public @Nullable EtpetsResourceChoice getSelectedResourceChoice() {
        return selectedResourceChoice.get();
    }

    public void setSelectedResourceChoice(@Nullable EtpetsResourceChoice resourceChoice) {
        if ((resourceChoice == null) || containsResourceChoiceId(resourceChoice.choiceId())) {
            selectedResourceChoice.set(resourceChoice);
        }
    }

    public Optional<EtpetsUserActionContext.SetTerrain> resolveSelectedTerrainContext() {
        EtpetsTerrainChoice terrainChoice = selectedTerrainChoice.get();
        if ((terrainChoice == null) || !containsTerrainChoiceId(terrainChoice.choiceId())) {
            return Optional.empty();
        }
        return Optional.of(new EtpetsUserActionContext.SetTerrain(terrainChoice));
    }

    public Optional<EtpetsUserActionContext.SetResource> resolveSelectedResourceContext() {
        EtpetsResourceChoice resourceChoice = selectedResourceChoice.get();
        if ((resourceChoice == null) || !containsResourceChoiceId(resourceChoice.choiceId())) {
            return Optional.empty();
        }
        return Optional.of(new EtpetsUserActionContext.SetResource(resourceChoice));
    }

    private boolean containsTerrainChoiceId(String choiceId) {
        return availableTerrainChoices.stream()
                                      .map(EtpetsTerrainChoice::choiceId)
                                      .anyMatch(choiceId::equals);
    }

    private boolean containsResourceChoiceId(String choiceId) {
        return availableResourceChoices.stream()
                                       .map(EtpetsResourceChoice::choiceId)
                                       .anyMatch(choiceId::equals);
    }

}

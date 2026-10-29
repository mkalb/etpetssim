package de.mkalb.etpetssim.simulations.etpets.viewmodel;

import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsResourceChoice;
import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsTerrainChoice;
import de.mkalb.etpetssim.simulations.etpets.shared.EtpetsUserActionContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

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
        if ((terrainChoice == null) || availableTerrainChoices.contains(terrainChoice)) {
            selectedTerrainChoice.set(terrainChoice);
        }
    }

    public @Nullable EtpetsResourceChoice getSelectedResourceChoice() {
        return selectedResourceChoice.get();
    }

    public void setSelectedResourceChoice(@Nullable EtpetsResourceChoice resourceChoice) {
        if ((resourceChoice == null) || availableResourceChoices.contains(resourceChoice)) {
            selectedResourceChoice.set(resourceChoice);
        }
    }

    public Optional<EtpetsUserActionContext.SetTerrain> resolveSelectedTerrainContext() {
        EtpetsTerrainChoice terrainChoice = selectedTerrainChoice.get();
        if ((terrainChoice == null) || !availableTerrainChoices.contains(terrainChoice)) {
            return Optional.empty();
        }
        return Optional.of(new EtpetsUserActionContext.SetTerrain(terrainChoice));
    }

    public Optional<EtpetsUserActionContext.SetResource> resolveSelectedResourceContext() {
        EtpetsResourceChoice resourceChoice = selectedResourceChoice.get();
        if ((resourceChoice == null) || !availableResourceChoices.contains(resourceChoice)) {
            return Optional.empty();
        }
        return Optional.of(new EtpetsUserActionContext.SetResource(resourceChoice));
    }

}

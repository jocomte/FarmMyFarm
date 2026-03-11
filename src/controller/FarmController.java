package controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.*;

public class FarmController {
    @FXML private GridPane farmGrid;
    @FXML private VBox animalBox;
    private GameState state;

    public void init(GameState state) {
        this.state = state;
        Timeline loop = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            for (Crop c : state.plots) if (c != null) c.update();
            for (Animal a : state.myAnimals) a.update();
            refreshUI();
        }));
        loop.setCycleCount(Animation.INDEFINITE);
        loop.play();
    }

    private void refreshUI() {
        farmGrid.getChildren().clear();
        for (int i = 0; i < 64; i++) { // La grille comporte 64 parcelles au total [cite: 3]
            StackPane cell = new StackPane();
            cell.setPrefSize(55, 55);
            int idx = i;

            if (!state.unlocked[i]) { // Gestion des zones verrouillées [cite: 8]
                cell.setStyle("-fx-background-color: #333; -fx-border-color: #000;");

                // AJOUT DU CADENAS ET DU PRIX
                Label lockLabel = new Label("🔒\n100$");
                lockLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
                lockLabel.setTextAlignment(TextAlignment.CENTER);
                cell.getChildren().add(lockLabel);

                cell.setOnMouseClicked(e -> {
                    if(state.walletProperty().get() >= 100) { // Coût de déblocage
                        state.walletProperty().set(state.walletProperty().get() - 100);
                        state.unlocked[idx] = true;
                        refreshUI(); // Rafraîchissement immédiat après achat
                    }
                });
            } else { // Gestion des zones débloquées (terre) [cite: 8]
                cell.setStyle("-fx-background-color: #8B4513; -fx-border-color: #5d4037;");

                if (state.plots[i] != null) {
                    // Affichage de l'icône selon le stade de croissance [cite: 1]
                    String icon = state.plots[i].getGrowthStage() == 2 ? "🌾" : "🌱";
                    cell.getChildren().add(new Label(icon));
                }
                cell.setOnMouseClicked(e -> handlePlot(idx));
            }
            farmGrid.add(cell, i % 8, i / 8);
        }
        updateAnimals();
    }

    private void handlePlot(int i) {
        if (state.plots[i] == null) {
            String s = state.selectedSeedProperty().get();
            if (state.seedStock.get(s) > 0) { // Plantation si les graines sont en stock
                state.plots[i] = new Crop(s, 10);
                state.seedStock.put(s, state.seedStock.get(s) - 1);
            }
        } else if (state.plots[i].getGrowthStage() == 2) { // Récolte vers les réserves
            state.foodStock.put(state.plots[i].getName(), state.foodStock.get(state.plots[i].getName()) + 1);
            state.plots[i] = null;
        }
    }

    private void updateAnimals() {
        animalBox.getChildren().clear();
        for (Animal a : state.myAnimals) {
            ProgressBar pb = new ProgressBar();
            pb.progressProperty().bind(a.progressProperty());

            Button btn = new Button(a.isReady() ? "Récolter" : "Nourrir");
            btn.setOnAction(e -> {
                if (a.isReady()) { // Production de ressources (Lait, Oeuf, Bacon) [cite: 10]
                    state.productStock.put(a.getResourceProduced(), state.productStock.getOrDefault(a.getResourceProduced(), 0) + 1);
                    a.progressProperty().set(0);
                } else if (state.foodStock.get(a.getFoodNeeded()) > 0) { // Nourrissage [cite: 8]
                    state.foodStock.put(a.getFoodNeeded(), state.foodStock.get(a.getFoodNeeded()) - 1);
                    a.feed();
                }
            });

            // Affichage de l'animal avec son icône et sa barre de progression [cite: 2]
            animalBox.getChildren().addAll(new Label(a.getIcon() + " " + a.getResourceProduced()), pb, btn);
        }
    }
}